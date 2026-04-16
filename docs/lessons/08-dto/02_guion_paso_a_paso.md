# Lección 08 - Tutorial paso a paso: DTO y validaciones

Sigue esta guía en orden. Vas a separar la entrada de la API del modelo de dominio e introducir validación automática.

---

## Paso 1: verificar la dependencia de validación

En la lección anterior ya agregaste la dependencia de Bean Validation en `pom.xml`. Verifica que esté presente:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Esta dependencia trae Hibernate Validator (la implementación de referencia de Bean Validation / JSR-380), que provee anotaciones como `@NotBlank`, `@NotNull`, `@Size`, `@Min`, `@Max` y `@Pattern`.

---

## Paso 2: entender por qué necesitamos un DTO

Antes de crear `TicketRequest`, entiende el problema que resuelve.

Tu endpoint actual acepta esto:

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) { ... }
```

El cliente puede enviar un JSON con **cualquier campo** de `Ticket`:

```json
{
  "id": 999,
  "title": "Bug crítico",
  "status": "RESOLVED",
  "createdAt": "2020-01-01T00:00:00",
  "estimatedResolutionDate": "2020-01-06"
}
```

Algunos de esos campos se ignoran en el `Service` (el `id`, `createdAt`, `estimatedResolutionDate`), pero `status` **podría ser leído** en una versión futura del código. El modelo expuesto como entrada es una bomba de tiempo.

La solución: un DTO que declara **solo** lo que el cliente puede enviar.

---

## Paso 3: crear el paquete `dto` y el `record TicketRequest`

Crea el directorio `dto` dentro de `cl/duoc/fullstack/tickets/` y luego el archivo:

```
src/main/java/cl/duoc/fullstack/tickets/dto/TicketRequest.java
```

```java
package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record TicketRequest(
    @NotBlank(message = "El titulo es requerido")
    @Size(min = 1, max = 50)
    String title,
    @NotBlank(message = "La descripción es requerida")
    String description,
    @NotBlank(message = "El creador es requerido")
    String createdBy,
    String assignedTo,
    String status,
    LocalDateTime effectiveResolutionDate
) {}
```

> **¿Qué es un `record` en Java?**
> Un `record` es una clase especial introducida en Java 16 que genera automáticamente:
> - Un constructor con todos los campos como parámetros
> - Métodos de acceso por nombre de campo (ej: `title()`, `description()`)
> - `equals()`, `hashCode()` y `toString()` basados en todos los campos
>
> Los records son **inmutables**: una vez creados, sus valores no pueden cambiar. Esto los hace ideales para DTOs, donde solo necesitas transportar datos de un lugar a otro sin modificarlos.

> **¿Por qué un `record` y no una clase con Lombok?**
> Para el modelo `Ticket` usamos Lombok porque necesitamos setters (mutabilidad) — el `Service` modifica campos como `status`, `createdAt`, etc. Pero un DTO de entrada no necesita setters: Jackson lo crea una vez a partir del JSON y nadie lo modifica después. El `record` expresa esa intención con menos código y sin dependencias externas.
>
> Jackson (la librería de serialización que usa Spring) soporta records de forma nativa desde la versión 2.12 — no necesitas `@JsonCreator` ni configuración adicional.

> **¿Por qué `@NotBlank` y no `@NotNull`?**
> `@NotNull` solo verifica que el campo no sea `null`. `@NotBlank` es más estricto: verifica que no sea null, no sea una cadena vacía `""`, y no sea solo espacios en blanco `"   "`. Para un título de ticket, `"   "` es tan inválido como `null`, por eso usamos `@NotBlank`.
>
> **Diferencias resumidas:**
> - `@NotNull` → `null` falla; `""` y `"   "` pasan
> - `@NotEmpty` → `null` y `""` fallan; `"   "` pasa
> - `@NotBlank` → `null`, `""` y `"   "` fallan

> **¿Por qué incluir `status` y `effectiveResolutionDate` en el DTO si el servidor los controla en el `create`?**
> Porque el mismo `TicketRequest` se reutiliza para el `PUT /tickets/by-id/{id}`, donde el cliente sí puede actualizar el estado y la fecha de resolución. Un campo opcional en el DTO es válido: simplemente se ignora si no viene.

---

## Paso 4: quitar las anotaciones de validación del modelo `Ticket`

Ahora que la validación vive en el DTO, el modelo `Ticket` queda como un POJO puro de Lombok — sin anotaciones de Jakarta Validation:

```java
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
  private String createdBy;
  private String assignedTo;
}
```

> **¿Por qué quitamos las anotaciones del modelo?**
> Porque la validación de entrada es responsabilidad del DTO, no del modelo de dominio. El `Ticket` representa lo que el sistema almacena internamente — el `TicketRequest` representa lo que el cliente puede enviar. Mezclar ambas responsabilidades en la misma clase fue exactamente el problema que identificamos al principio.

---

## Paso 5: actualizar `TicketService.create()` para aceptar `TicketRequest`

El `Service` recibe el DTO y construye el `Ticket` internamente. Esta transformación es responsabilidad del `Service`: es el puente entre la capa de entrada y el modelo de dominio.

**Antes:**

```java
public Ticket create(Ticket ticket) {
    // validaciones...
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));
    return this.repository.save(ticket);
}
```

**Después:**

```java
public Ticket create(TicketRequest request) {
    if (this.repository.existsByTitle(request.title())) {
        throw new IllegalArgumentException(
            "Ya existe un ticket con el título: \"" + request.title() + "\"");
    }

    if (request.assignedTo() != null
        && request.assignedTo().equals(request.createdBy())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    Ticket ticket = new Ticket();
    ticket.setTitle(request.title());
    ticket.setDescription(request.description());
    ticket.setCreatedBy(request.createdBy());
    ticket.setAssignedTo(request.assignedTo());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));

    return this.repository.save(ticket);
}
```

La diferencia clave: el `Service` **construye** el `Ticket` a partir del DTO. El cliente nunca puede inyectar un `Ticket` preformado con campos no permitidos.

> **¿Por qué se accede a los campos con `request.title()` y no `request.getTitle()`?**
> Los records en Java generan métodos de acceso con el mismo nombre que el campo, sin el prefijo `get`. Es una convención del lenguaje para records.

> **¿Por qué crear `new Ticket()` en el Service y no en el Repository?**
> El `Service` tiene la responsabilidad de aplicar las reglas de negocio: qué campos asigna el servidor, cuáles vienen del cliente, cuáles se calculan. El `Repository` solo guarda. Si el `Repository` construyera el `Ticket`, mezclaría lógica de negocio con lógica de persistencia.

---

## Paso 6: actualizar `TicketService.updateById()` para aceptar `TicketRequest`

```java
public Optional<Ticket> updateById(Long id, TicketRequest request) {
    Optional<Ticket> found = this.repository.findById(id);
    if (found.isEmpty()) {
        return Optional.empty();
    }

    Ticket toUpdate = found.get();

    if (request.assignedTo() != null
        && request.assignedTo().equals(toUpdate.getCreatedBy())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    toUpdate.setTitle(request.title());
    toUpdate.setDescription(request.description());
    if (request.status() != null && !request.status().isBlank()) {
        toUpdate.setStatus(request.status());
    }
    toUpdate.setEffectiveResolutionDate(request.effectiveResolutionDate());
    if (request.assignedTo() != null) {
        toUpdate.setAssignedTo(request.assignedTo());
    }
    this.repository.update(toUpdate);
    return Optional.of(toUpdate);
}
```

El `status` en el `PUT` es opcional: si el cliente no lo envía (llega como `null`), el status del ticket no cambia. Si lo envía, sí se actualiza.

---

## Paso 7: actualizar el controlador para usar `@Valid` y `TicketRequest`

**POST — antes y después:**

```java
// Antes:
public ResponseEntity<Object> create(@RequestBody Ticket ticket) { ... }

// Después:
public ResponseEntity<Object> create(@Valid @RequestBody TicketRequest request) { ... }
```

**PUT — antes y después:**

```java
// Antes:
public ResponseEntity<Object> updateTicketById(@PathVariable Long id, @RequestBody Ticket ticket) { ... }

// Después:
public ResponseEntity<Object> updateTicketById(@PathVariable Long id, @Valid @RequestBody TicketRequest request) { ... }
```

> **¿Qué hace `@Valid`?**
> Le indica a Spring que debe validar el objeto anotado con las restricciones de Bean Validation (`@NotBlank`, etc.) antes de ejecutar el método. Si la validación falla, Spring lanza `MethodArgumentNotValidException` **antes** de entrar al cuerpo del método. El método nunca se ejecuta.
>
> Si olvidaras `@Valid`, las anotaciones `@NotBlank` estarían en el DTO pero nunca se evaluarían. El `@RequestBody` deserializa el JSON independientemente de si `@Valid` está presente o no.

---

## Paso 8: agregar el `@ExceptionHandler` para errores de validación

Cuando `@Valid` falla, Spring lanza `MethodArgumentNotValidException`. Si no capturas esa excepción, Spring devuelve su propio formato de error por defecto — que no coincide con tu `ErrorResponse`.

Agrega este método en `TicketController`:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
}
```

**Código equivalente sin expresiones lambda:**

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldErrors.size(); i++) {
        FieldError err = fieldErrors.get(i);
        sb.append(err.getField()).append(": ").append(err.getDefaultMessage());
        if (i < fieldErrors.size() - 1) {
            sb.append(", ");
        }
    }
    return ResponseEntity.badRequest().body(new ErrorResponse(sb.toString()));
}
```

> **¿Por qué `@ExceptionHandler` y no try/catch dentro del método?**
> Con `@Valid`, la excepción se lanza **antes** de entrar al método — no puedes capturarla con try/catch porque el método nunca empieza. `@ExceptionHandler` es el mecanismo correcto: intercepta la excepción en la capa del controlador y devuelve la respuesta adecuada. Es el primer nivel de centralización de errores, antes de `@ControllerAdvice`.

> **¿Este `@ExceptionHandler` aplica a todos los controladores?**
> No. Un `@ExceptionHandler` dentro de un `@RestController` aplica **solo** a las excepciones que lanza ese controlador. Para que aplique globalmente, necesitarías moverlo a una clase con `@ControllerAdvice` — exactamente lo que discutimos en la lección 07.

---

## Paso 9: probar la validación

### Prueba 1: título vacío

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "", "description": "Descripción", "createdBy": "juan" }
```

Resultado esperado (`400 Bad Request`):

```json
{
  "message": "title: El titulo es requerido"
}
```

### Prueba 2: título con solo espacios

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "   ", "description": "Descripción", "createdBy": "juan" }
```

Resultado esperado: `400 Bad Request` con el mismo mensaje de error.

### Prueba 3: título ausente (null)

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "description": "Sin título", "createdBy": "juan" }
```

Resultado esperado: `400 Bad Request` con mensaje de validación.

### Prueba 4: creación válida (el flujo feliz no se rompió)

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "Bug en facturación", "description": "El sistema falla al generar PDF", "createdBy": "juan" }
```

Resultado esperado: `201 Created` con `"Ticket Creado"`.

### Prueba 5: validación en PUT también funciona

```
PUT http://localhost:8080/ticket-app/tickets/by-id/1
Content-Type: application/json

{ "title": "", "description": "Descripción", "createdBy": "juan" }
```

Resultado esperado: `400 Bad Request` con mensaje de validación.

---

## Paso 10: reflexiona antes de cerrar

1. Si el cliente envía `{"id": 99, "title": "Test", "status": "RESOLVED", "createdBy": "juan"}` al `POST /tickets`, ¿qué ocurre con el `id` y el `status` que envió? ¿El servidor los usa o los descarta?
2. ¿Por qué el `Service` construye el `Ticket` a partir del `TicketRequest` en lugar de recibirlo directamente? ¿Qué pasaría si un futuro desarrollador agrega `ticket.setStatus(request.status())` al service?
3. Si agregas `@NotBlank` en `description` también, ¿qué cambiaría en la respuesta de error si ambos campos son inválidos?
4. ¿Qué diferencia hay entre un `record` de Java y una clase con Lombok? ¿Cuándo preferirías uno sobre otro?
