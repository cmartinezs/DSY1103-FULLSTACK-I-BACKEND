# Lección 05 - Tutorial paso a paso: agregando POST a tu API

Sigue esta guía en orden. Vas a extender el proyecto de tickets que construiste en la lección anterior, agregando la capacidad de crear nuevos tickets a través de una petición `POST`.

---

## Paso 1: entender qué cambios necesitamos

Antes de tocar el código, piensa en lo que falta. Tu API actualmente tiene esto:

```
GET /tickets → devuelve la lista completa
```

Y lo que necesita tener:

```
GET  /tickets → devuelve la lista completa         (ya existe)
POST /tickets → recibe un ticket nuevo y lo guarda (lo que vamos a construir)
```

Para que el `POST` funcione, necesitas modificar **cuatro capas**:

1. **`Ticket` (Model):** agregar un constructor vacío y tres nuevos campos de fecha
2. **`TicketRepository`:** agregar `existsByTitle()` para validar duplicados y el método `save()` con ID incremental
3. **`TicketService`:** agregar `create()` con toda la lógica de negocio (validación, estado, fechas)
4. **`TicketController`:** agregar el endpoint `@PostMapping` con `@RequestBody` y `ResponseEntity`

La separación de capas hace que los cambios estén bien localizados: cada capa se modifica por su propia razón, no por razones de otra capa.

---

## Paso 2: preparar el Modelo (`Ticket.java`)

Abre la clase `Ticket` en el paquete `model`. Necesita dos cambios: un constructor vacío para que Jackson pueda deserializar el JSON entrante, y tres nuevos campos para representar el ciclo de vida del ticket en el tiempo.

```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDate estimatedResolutionDate;
    private LocalDateTime effectiveResolutionDate;
}
```

> **¿Por qué `@NoArgsConstructor`?**
> Spring usa Jackson para convertir el JSON del cliente en un objeto Java. El proceso es: Jackson crea una instancia vacía (`new Ticket()`) y luego llama a los setters campo por campo. Sin `@NoArgsConstructor`, ese primer paso falla y la petición devuelve un error `400 Bad Request` confuso sobre deserialización.

> **¿Por qué conservamos `@AllArgsConstructor`?**
> Porque el `TicketRepository` lo sigue usando para construir los tickets semilla con todos sus campos. Ambas anotaciones coexisten sin problema: Java permite múltiples constructores con diferentes firmas.

> **¿Por qué `LocalDate` para la estimada y `LocalDateTime` para las otras?**
> La fecha de creación y de resolución efectiva necesitan precisión de hora y minuto: importa saber a qué hora exacta ocurrió cada evento. La fecha estimada, en cambio, es una fecha de vencimiento: no importa la hora, solo el día. `LocalDate` comunica esa intención con más precisión que un `LocalDateTime` donde la hora sería siempre `00:00`.

> **¿El cliente manda estos campos en el POST?**
> No. El cliente solo manda `title` y `description`. Los campos `status`, `createdAt`, `estimatedResolutionDate` y `effectiveResolutionDate` los asigna exclusivamente el servidor. Si el cliente los incluye en el JSON, el servidor los ignora y los sobreescribe. Esa es la lógica de negocio que vive en el `Service`.

---

## Paso 3: agregar `existsByTitle()` y `save()` al Repository (`TicketRepository.java`)

El `Repository` cumple dos responsabilidades nuevas: verificar si un título ya existe, y persistir un ticket nuevo con un ID generado automáticamente.

Reemplaza el contenido de `TicketRepository` con lo siguiente:

```java
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketRepository {

    private List<Ticket> tickets;
    private Long currentId = 3L;

    public TicketRepository() {
        tickets = new ArrayList<>();
        tickets.add(new Ticket(
            1L, "Ticket 1", "Descripción del ticket 1", "NEW",
            LocalDateTime.of(2026, 3, 15, 9, 0),
            LocalDate.of(2026, 3, 22),
            null
        ));
        tickets.add(new Ticket(
            2L, "Ticket 2", "Descripción del ticket 2", "RESOLVED",
            LocalDateTime.of(2026, 3, 10, 14, 30),
            LocalDate.of(2026, 3, 17),
            LocalDateTime.of(2026, 3, 16, 11, 0)
        ));
    }

    public List<Ticket> getAll() {
        return tickets;
    }

    public boolean existsByTitle(String title) {
        return tickets.stream()
            .anyMatch(t -> t.getTitle().equalsIgnoreCase(title));
    }

    public Ticket save(Ticket ticket) {
        ticket.setId(currentId++);
        tickets.add(ticket);
        return ticket;
    }
}
```

> **¿Por qué `currentId` empieza en `3L`?**
> Los tickets semilla ya ocupan los IDs `1` y `2`. Empezar en `3` garantiza que no haya colisión de IDs.

> **¿Por qué `existsByTitle()` usa `equalsIgnoreCase()`?**
> Para que `"login falla"`, `"Login Falla"` y `"LOGIN FALLA"` sean considerados el mismo título. Un usuario que comete un error de capitalización no debería poder crear un ticket duplicado. La comparación sin distinción de mayúsculas es más robusta y más amigable.

**Código equivalente sin expresiones lambda:**

```java
public boolean existsByTitle(String title) {
    for (Ticket ticket : tickets) {
        if (ticket.getTitle().equalsIgnoreCase(title)) {
            return true;
        }
    }
    return false;
}
```

El `for` recorre cada ticket y retorna `true` en cuanto encuentra un título coincidente. Si termina el recorrido sin encontrar ninguno, retorna `false`. El stream con `anyMatch` hace exactamente lo mismo con menos líneas.

> **¿Por qué esta validación vive en el `Repository` y no en el `Service`?**
> La consulta de si algo existe en el almacenamiento es responsabilidad del `Repository`: es quien sabe dónde y cómo están guardados los datos. Pero la *decisión* de qué hacer si existe un duplicado (lanzar una excepción, ignorar, etc.) es responsabilidad del `Service`. El `Repository` solo responde la pregunta; el `Service` toma la acción.

> **Los datos semilla ahora tienen fechas realistas:** el Ticket 1 está abierto (`effectiveResolutionDate: null`), el Ticket 2 ya fue resuelto antes de su fecha estimada. Esto permite probar el `GET` con datos que reflejan ambos estados posibles de un ticket.

---

## Paso 4: agregar `create()` al Service (`TicketService.java`)

El `Service` es donde vive toda la lógica de negocio de la creación. Esta es la capa más importante de este paso: aquí se concentra todo lo que el servidor decide de forma autónoma, sin depender de lo que el cliente mande.

Abre `TicketService` y agrega el método `create()`:

```java
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public List<Ticket> getTickets() {
        return this.repository.getAll();
    }

    public Ticket create(Ticket ticket) {
        if (repository.existsByTitle(ticket.getTitle())) {
            throw new IllegalArgumentException(
                "Ya existe un ticket con el título: \"" + ticket.getTitle() + "\""
            );
        }

        ticket.setStatus("NEW");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));
        ticket.setEffectiveResolutionDate(null);

        return this.repository.save(ticket);
    }
}
```

> **¿Por qué el `Service` lanza una excepción en lugar de devolver `null` o `false`?**
> Porque una excepción comunica explícitamente que ocurrió algo inesperado e impide que el flujo continúe. Si devolviéramos `null`, el controlador tendría que verificar si el resultado es nulo y tomar la decisión, lo que mezcla lógica de negocio con lógica de presentación HTTP. La excepción fuerza al controlador a manejar el error de forma explícita.

> **¿Por qué el `Service` asigna el `status` en lugar de recibirlo del cliente?**
> Porque "un ticket recién creado siempre empieza como `NEW`" es una **regla de negocio**. Si el cliente pudiera mandar `"status": "RESOLVED"` y el servidor lo aceptara, cualquier usuario podría resolver un ticket sin haberlo trabajado. El servidor tiene la autoridad sobre su propio estado interno.

> **¿Por qué el `Service` calcula la fecha estimada (y no el cliente)?**
> Por el mismo principio: la regla "la resolución estimada es 5 días después de la creación" es lógica de negocio. Si el cliente calculara esa fecha, cada cliente podría mandar una fecha diferente. Centralizar el cálculo en el `Service` garantiza que la regla se aplique de forma consistente sin importar desde dónde se cree el ticket.

> **¿Por qué `effectiveResolutionDate` se asigna como `null`?**
> Porque en el momento de la creación el ticket aún no está resuelto. Esta fecha se asignará en el futuro, cuando se implemente el endpoint de actualización de estado (`PUT /tickets/{id}`). Por ahora, dejarla como `null` es el estado correcto para un ticket nuevo.

---

## Paso 5: agregar el endpoint `POST` al Controller (`TicketController.java`)

El controlador recibe la petición, llama al `Service` y devuelve la respuesta apropiada. Incluye manejo de la excepción de duplicado porque, mientras no tengamos `@ControllerAdvice`, esta es la única forma de interceptarla antes de que Spring devuelva un `500 Internal Server Error`.

Reemplaza el contenido de `TicketController` con lo siguiente:

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return this.service.getTickets();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Ticket ticket) {
        try {
            service.create(ticket);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
```

> **¿Por qué `ResponseEntity<Object>` y no `ResponseEntity<Ticket>`?**
> Porque el método puede retornar dos tipos distintos: un `Ticket` (cuando todo sale bien) o un `String` con el mensaje de error (cuando hay duplicado). Java no permite que un método genérico retorne dos tipos diferentes, así que usamos `Object` como tipo común. Esta es una limitación temporal: cuando implementemos `@ControllerAdvice` en lecciones futuras, el controlador volverá a tener `ResponseEntity<Ticket>` y el manejo de errores vivirá en una clase dedicada.

> **¿Por qué `409 Conflict` para el duplicado?**
> El estándar HTTP define `409 Conflict` para situaciones donde la petición no puede completarse por un conflicto con el estado actual del recurso. Crear un ticket con un título que ya existe es exactamente eso: la petición entra en conflicto con un dato que ya existe. Es más preciso que `400 Bad Request` (que indica que el formato del request está mal) o `422 Unprocessable Entity` (que indica que la entidad no puede procesarse).

> **¿Por qué el `try/catch` está en el `Controller` y no en el `Service`?**
> Porque la decisión de qué código HTTP devolver es responsabilidad del controlador. El `Service` solo sabe que algo salió mal (por eso lanza la excepción). El `Controller` es quien sabe cómo traducir ese error a un código HTTP. Cada capa hace lo que le corresponde.

---

## Paso 6: verificar que todo funciona

Levanta la aplicación y abre Postman, Insomnia o Thunder Client.

### Prueba 1: crear un ticket nuevo

Haz una petición `POST` a:

```
POST http://localhost:8080/tickets
Content-Type: application/json
```

Con el siguiente body. **Nota:** solo mandas `title` y `description`. El servidor se encarga de todo lo demás.

```json
{
  "title": "Login falla con usuario especial",
  "description": "El sistema no permite el acceso con caracteres especiales en el nombre de usuario"
}
```

Resultado esperado (`201 Created`):

```
Ticket Creado
```

Observa que:
- La respuesta es un texto plano confirmando la creación, no el objeto completo
- Internamente, el servidor asignó `id`, `status = "NEW"`, `createdAt` y `estimatedResolutionDate` (5 días después)
- Puedes verificar el ticket creado con `GET /tickets`

### Prueba 2: intentar crear un ticket con el mismo título

Vuelve a mandar el mismo POST con el mismo título:

```json
{
  "title": "Login falla con usuario especial",
  "description": "Otro intento con el mismo título"
}
```

Resultado esperado (`409 Conflict`):

```
Ya existe un ticket con el título: "Login falla con usuario especial"
```

El servidor rechaza la creación porque ya existe un ticket con ese título. El mensaje viene directamente de la excepción lanzada en el `Service`.

### Prueba 3: verificar que el GET refleja el estado correcto

```
GET http://localhost:8080/tickets
```

Deberías ver los 3 tickets: los 2 semilla más el que acabas de crear. Los semilla tienen `status = "NEW"` y el nuevo también.

---

## Paso 7: reflexiona antes de cerrar

Antes de pasar a la actividad, respóndete estas preguntas:

1. El cliente mandó un JSON sin el campo `status`. ¿Qué valor tiene `status` en el objeto `Ticket` cuando llega al `Service`? ¿Qué pasa si el cliente sí lo manda con `"status": "RESOLVED"`?
2. Si mañana la regla de negocio cambia y la fecha estimada pasa de 5 días a 10 días hábiles, ¿qué archivo modificarías? ¿Tendrías que tocar el `Controller` o el `Repository`?
3. ¿Por qué el `try/catch` está en el `Controller` y no en el `Service`? ¿Qué pasaría si lo pusieras en el `Service`?

---

## Extensión opcional

Si terminaste todo lo anterior y quieres ir un paso más, implementa el endpoint de resolución de un ticket:

```
PUT /tickets/by-id/{id}/resolve
```

- Busca el ticket por `id` en el `Repository`
- Si no existe, devuelve `404 Not Found`
- Si ya está `"RESOLVED"`, devuelve `409 Conflict` con un mensaje claro
- Si existe y está `"NEW"`, cambia el `status` a `"RESOLVED"` y asigna `effectiveResolutionDate = LocalDateTime.now()`
- Devuelve `200 OK` con el ticket actualizado

Este es el momento en que `effectiveResolutionDate` deja de ser `null`. Toda la lógica de ese cambio de estado vive en el `Service`.
