# Lección 04 - Tutorial paso a paso: construyendo tu primera API con capas

Sigue esta guía en orden. Cada sección te explica qué vas a hacer y **por qué lo hacemos así**, para que no solo copies código sino que entiendas la lógica detrás.

---

## Paso 1: el problema del "controlador que hace todo"

Antes de escribir código, piensa en esto:

Imagina que tienes un restaurante donde el mismo mesero toma el pedido, lo cocina, lo sirve y también lleva la contabilidad. Cuando el restaurante es pequeño quizás funciona, pero cuando creces ese modelo colapsa: si el mesero se enferma, todo falla; si quieres cambiar el menú, tienes que reentrenar a toda la persona.

En programación ocurre exactamente lo mismo. Si tu `Controller` valida datos, contiene la lógica de negocio, accede directamente a la colección de datos y formatea la respuesta, estás ante el mismo problema:

- Es difícil de probar: no puedes testear la lógica sin levantar el servidor HTTP completo
- Es frágil: cambiar una regla de negocio puede romper el manejo HTTP
- No escala: cuando el proyecto crece, ese archivo se vuelve imposible de mantener

La solución es **separar responsabilidades**: cada parte del sistema hace una sola cosa y la hace bien. Eso es exactamente lo que vas a construir hoy.

---

## Paso 2: crear la estructura de paquetes (CSR)

El patrón que vamos a usar se llama **CSR** (Controller - Service - Repository). Antes de escribir ninguna clase, crea los siguientes paquetes dentro de `cl.duoc.fullstack.tickets`:

```
controller/   → recibe y responde peticiones HTTP
service/      → contiene la lógica de negocio
repository/   → accede y almacena los datos
model/        → define la forma de los datos (las "entidades")
```

> **¿Por qué paquetes separados?** En Java, los paquetes son más que carpetas: comunican intención. Cuando alguien abre tu proyecto y ve estos cuatro paquetes, inmediatamente sabe que sigues una arquitectura por capas. Es un lenguaje común entre desarrolladores.

El flujo de una petición siempre sigue este camino:

```
HTTP → Controller → Service → Repository → Service → Controller → HTTP
```

Nunca al revés, nunca saltando capas. El `Controller` nunca llama directamente al `Repository`. Si algún día ves eso en tu código, es una señal de que algo está mal ubicado.

---

## Paso 3: el Modelo (`Ticket.java`)

El modelo es la clase que representa los datos de tu dominio. En este caso, un `Ticket` tiene cuatro atributos: un identificador, un título, una descripción y un estado.

Crea la clase `Ticket` en el paquete `model`:

```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
}
```

> **¿Qué es Lombok y por qué lo usamos?**
> Lombok es una librería que genera código repetitivo automáticamente durante la compilación. Las tres anotaciones hacen lo siguiente:
> - `@Getter`: genera un método `getId()`, `getTitle()`, etc. por cada campo
> - `@Setter`: genera un método `setId()`, `setTitle()`, etc. por cada campo
> - `@AllArgsConstructor`: genera un constructor con todos los campos como parámetros
>
> Sin Lombok tendrías que escribir todo eso a mano. Con Lombok, tu clase queda limpia y legible.

> **¿Por qué los campos están en inglés?**
> Es una convención del sector. Los identificadores de código (clases, métodos, variables) se escriben en inglés para que el proyecto sea entendible por cualquier desarrollador del mundo, independientemente de su idioma. Los textos que el usuario ve sí pueden estar en el idioma local.

---

## Paso 4: el Repository (`TicketRepository.java`)

El `Repository` es la capa que se encarga de **almacenar y recuperar datos**. Hoy usamos una `List` en memoria para simular una base de datos. Cuando en lecciones futuras conectemos una base de datos real, solo tendrás que cambiar esta capa: el resto del código no sabrá la diferencia.

Crea la clase `TicketRepository` en el paquete `repository`:

```java
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketRepository {

    List<Ticket> tickets;

    public TicketRepository() {
        tickets = new ArrayList<>();
        tickets.add(new Ticket(1L, "Ticket 1", "Ticket 1", "NEW"));
        tickets.add(new Ticket(2L, "Ticket 2", "Ticket 2", "NEW"));
    }

    public List<Ticket> getAll() {
        return tickets;
    }
}
```

> **¿Qué hace `@Repository`?**
> Le dice a Spring que esta clase es un componente de acceso a datos. Spring la registra automáticamente en su contenedor y la tiene disponible para inyectarla donde sea necesaria. Sin esta anotación, Spring no sabría que esta clase existe.

> **¿Por qué los datos iniciales se cargan en el constructor?**
> El constructor se ejecuta una sola vez cuando Spring crea el objeto. Esos datos iniciales (llamados "seed data" o "datos semilla") nos permiten probar el endpoint de inmediato sin tener que crear datos manualmente. Son datos de prueba, no datos reales.

> **Importante:** como los datos viven en memoria, si reinicias la aplicación vuelven al estado inicial. Eso está bien por ahora: todavía no necesitamos persistencia real.

---

## Paso 5: el Service (`TicketService.java`)

El `Service` es la capa que contiene la **lógica de negocio**. Hoy nuestra lógica es simple (solo devolver la lista), pero esta capa existe porque en el mundo real aquí es donde irían las reglas: filtrar tickets por estado, calcular prioridades, validar que el usuario tenga permisos, etc.

Crea la clase `TicketService` en el paquete `service`:

```java
import org.springframework.stereotype.Service;
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
}
```

> **¿Qué hace `@Service`?**
> Similar a `@Repository`, le dice a Spring que este es un componente de lógica de negocio. Spring lo registra y lo tiene disponible para inyectarlo.

> **¿Por qué el `Service` recibe el `Repository` por constructor?**
> Esto se llama **inyección de dependencias por constructor**. En lugar de que `TicketService` cree él mismo su `TicketRepository` (con `new`), se lo pedimos a Spring a través del constructor. Las ventajas son claras:
> - Spring gestiona el ciclo de vida de los objetos, no tú
> - En pruebas unitarias puedes pasar un `Repository` falso (mock) sin levantar todo el sistema
> - Las dependencias son explícitas y visibles: cualquiera que lea el constructor sabe exactamente qué necesita esta clase para funcionar

---

## Paso 6: el Controller (`TicketController.java`)

El `Controller` es la única capa que "habla HTTP". Su único trabajo es:
1. Recibir la petición HTTP
2. Llamar al `Service` para que haga el trabajo
3. Devolver la respuesta HTTP

Nada más. Si ves lógica de negocio en el `Controller`, es una señal de que algo está en el lugar equivocado.

Crea la clase `TicketController` en el paquete `controller`:

```java
import org.springframework.web.bind.annotation.GetMapping;
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
}
```

> **¿Qué hace cada anotación?**
> - `@RestController`: combina `@Controller` y `@ResponseBody`. Le dice a Spring que esta clase maneja peticiones HTTP y que los objetos que retorne deben convertirse automáticamente a JSON.
> - `@RequestMapping("/tickets")`: define la URL base para todos los endpoints de este controlador. Todos los métodos de esta clase responderán bajo `/tickets`.
> - `@GetMapping`: mapea el método `getAllTickets()` a las peticiones `GET /tickets`. Si el cliente hace `GET /tickets`, Spring ejecuta este método.

> **¿Por qué el método se llama `getAllTickets()` y no solo `get()`?**
> Los nombres de los métodos en el `Controller` deben ser descriptivos. `getAllTickets` deja claro a cualquier desarrollador que ese método obtiene todos los tickets. Recuerda: el nombre del método no aparece en la URL; la URL la define `@GetMapping`.

> **Mejora pendiente:** actualmente el método devuelve `List<Ticket>` directamente. El siguiente paso (próximas lecciones) es envolverlo en `ResponseEntity<List<Ticket>>`, lo que nos dará control explícito sobre el código HTTP de la respuesta. Por ahora, Spring retorna automáticamente `200 OK` cuando el método termina sin errores.

---

## Paso 7: verificar que todo funciona

Levanta la aplicación con el botón de play en IntelliJ (o con `./mvnw spring-boot:run` en la terminal) y abre Postman o Insomnia.

Haz una petición `GET` a:

```
http://localhost:8080/tickets
```

Deberías recibir una respuesta `200 OK` con este cuerpo JSON:

```json
[
  {
    "id": 1,
    "title": "Ticket 1",
    "description": "Ticket 1",
    "status": "NEW"
  },
  {
    "id": 2,
    "title": "Ticket 2",
    "description": "Ticket 2",
    "status": "NEW"
  }
]
```

Si ves eso, ¡felicitaciones! Acabas de construir tu primera API con arquitectura por capas.

---

## Paso 8: configuración del proyecto (pendiente)

Por ahora la aplicación corre con la configuración predeterminada de Spring Boot (puerto `8080`, sin prefijo de ruta). El único parámetro configurado explícitamente es el nombre de la aplicación, en `src/main/resources/application.properties`:

```properties
spring.application.name=Tickets
```

En una próxima iteración aprenderás a personalizar la aplicación migrando a `application.yaml` y agregando:

### Cambiar el puerto

Por defecto Spring Boot usa el puerto `8080`. Si necesitas cambiar eso (por ejemplo, porque tienes otro servicio corriendo en ese puerto):

```yaml
server:
  port: 8081
```

Con esto, tu endpoint quedaría en `http://localhost:8081/tickets`.

### Agregar un prefijo global (context path)

En un entorno real, es común que todas las rutas de tu API tengan un prefijo que la identifique. Por ejemplo, si tu API se llama "tickets-app":

```yaml
server:
  servlet:
    context-path: /tickets-app
```

Con esto, el endpoint quedaría en `http://localhost:8080/tickets-app/tickets`.

> **¿Por qué esto se configura en el archivo YAML y no en el código Java?**
> Porque el puerto y el prefijo son **configuración de entorno**, no lógica de negocio. En desarrollo puedes usar el puerto 8080; en producción, el 443. Si eso estuviera hardcodeado en el `Controller`, tendrías que cambiar el código fuente cada vez que cambia el entorno. El archivo de configuración separa esas decisiones del código.

### Personalizar el banner de inicio

Cuando Spring Boot arranca, muestra un banner en la consola. Puedes personalizarlo creando el archivo `src/main/resources/banner.txt`:

```text
=== TICKETS API - CSR CLASS ===
```

Es un detalle pequeño, pero útil para identificar rápidamente qué aplicación está corriendo cuando tienes varias en tu máquina.

---

## Paso 9: reflexiona antes de cerrar

Antes de pasar a la actividad, respóndete estas preguntas mentalmente (o en voz alta):

1. Si mañana necesitas conectar una base de datos real en lugar de la `List`, ¿qué archivo modificarías? ¿Por qué solo ese?
2. Si el cliente pide que un ticket solo sea visible si está en estado `"NEW"`, ¿en qué capa agregarías ese filtro? ¿Por qué no en el `Controller`?
3. Si otro equipo quiere consumir los mismos datos de tickets pero desde una interfaz gráfica diferente, ¿tendrías que cambiar algo del `Service` o del `Repository`? ¿Por qué?

Si puedes responder estas tres preguntas con seguridad, entendiste el objetivo de esta lección.

---

## Extensión opcional

Si terminaste todo lo anterior y quieres ir un paso más, agrega un segundo endpoint que filtre por estado:

```
GET /tickets/status/{status}
```

Por ejemplo, `GET /tickets/status/NEW` debería devolver solo los tickets con `status = "NEW"`. Piensa en qué capa va la lógica de filtrado antes de escribir el código.
