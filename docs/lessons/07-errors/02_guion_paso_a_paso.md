# Lección 07 - Tutorial paso a paso: validaciones y errores

Sigue esta guía en orden. Vas a agregar validaciones de negocio en `TicketService` y manejo de excepciones en `TicketController`.

---

## Paso 0: agregar la dependencia de validación

Para usar `@NotBlank`, `@Valid` y otras anotaciones de Bean Validation necesitas la dependencia en `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## Paso 1: agregar campos `createdBy` y `assignedTo` al modelo

Abre `model/Ticket.java` y añade dos campos nuevos:

```java
@NotBlank(message = "El creador es requerido")
private String createdBy;

private String assignedTo;
```

El modelo completo debe quedar así:

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
  @Min(5) @Max(100)
  private Long id;
  @NotBlank(message = "El titulo es requerido")
  @Size(min = 1, max = 50)
  private String title;
  @NotBlank
  private String description;
  private String status;
  private LocalDateTime createdAt;
  private LocalDate estimatedResolutionDate;
  private LocalDateTime effectiveResolutionDate;
  @NotBlank(message = "El creador es requerido")
  private String createdBy;
  private String assignedTo;
}
```

---

## Paso 2: agregar validación en `TicketService.create()`

Abre `service/TicketService.java` y actualiza el método `create()`:

```java
public Ticket create(Ticket ticket) {
    // Validación 1: Título duplicado
    boolean exists = this.repository.existsByTitle(ticket.getTitle());
    if (exists) {
        throw new IllegalArgumentException("Ya existe un ticket con el título '" + ticket.getTitle() + "'");
    }

    // Validación 2: Creador ≠ Asignado
    if (ticket.getAssignedTo() != null && 
        ticket.getAssignedTo().equals(ticket.getCreatedBy())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDate ldNow = LocalDate.now();
    LocalDate estimated = ldNow.plusDays(5L);

    ticket.setStatus("NEW");
    ticket.setCreatedAt(now);
    ticket.setEstimatedResolutionDate(estimated);
    return this.repository.save(ticket);
}
```

**¿Por qué lanzar excepción?** El Service valida reglas de negocio. Si falla, lanza `IllegalArgumentException`. El Controller la capturará y convertirá a respuesta HTTP.

---

## Paso 3: agregar validación en `TicketService.updateById()`

Actualiza el método `updateById()` para usar `Optional`:

```java
public Optional<Ticket> updateById(Long id, Ticket ticket) {
    Optional<Ticket> found = this.repository.findById(id);
    if (found.isEmpty()) {
        return Optional.empty();
    }

    Ticket toUpdate = found.get();

    // Validación: Si se intenta cambiar el asignado, verifica que ≠ creador
    if (ticket.getAssignedTo() != null && 
        ticket.getAssignedTo().equals(toUpdate.getCreatedBy())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    toUpdate.setTitle(ticket.getTitle());
    toUpdate.setDescription(ticket.getDescription());
    toUpdate.setStatus(ticket.getStatus());
    toUpdate.setEffectiveResolutionDate(ticket.getEffectiveResolutionDate());
    if (ticket.getAssignedTo() != null) {
        toUpdate.setAssignedTo(ticket.getAssignedTo());
    }
    this.repository.update(toUpdate);
    return Optional.of(toUpdate);
}
```

---

## Paso 4: actualizar `TicketController.create()`

Envuelve el `service.create()` en try/catch para capturar la excepción:

```java
@PostMapping
public ResponseEntity<Object> create(@Valid @RequestBody Ticket ticket) {
    try {
        this.service.create(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

> **¿Por qué `409 Conflict` y no `400 Bad Request`?**
> El estándar HTTP define `409 Conflict` para situaciones donde la petición entra en conflicto con el estado actual del recurso (por ejemplo, un título duplicado o un creador que es el mismo que el asignado). `400 Bad Request` se reserva para problemas de formato o validación del request en sí.

---

## Paso 5: actualizar `TicketController.updateTicketById()`

Envuelve el `service.updateById()` en try/catch y usa `Optional`:

```java
@PutMapping("/by-id/{id}")
public ResponseEntity<Object> updateTicketById(
        @PathVariable Long id,
        @Valid @RequestBody Ticket ticket) {
    try {
        Optional<Ticket> updated = this.service.updateById(id, ticket);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        }
        return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

---

## Paso 6: crear `ErrorResponse`

Crea el archivo `model/ErrorResponse.java`:

```java
package cl.duoc.fullstack.tickets.model;

public record ErrorResponse(String message) {}
```

Jackson convierte automáticamente a JSON: `{"message": "..."}`

---

## Paso 7: verificar que todo funciona

### Prueba 1: crear ticket sin asignar (válido)

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Ticket A",
  "description": "Descripción",
  "createdBy": "juan"
}
```

**Resultado:** `201 Created` con `"Ticket Creado"`

### Prueba 2: crear ticket con creador = asignado (inválido)

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Ticket B",
  "description": "Descripción",
  "createdBy": "juan",
  "assignedTo": "juan"
}
```

**Resultado:** `409 Conflict` con:

```json
{
  "message": "El creador y el asignado no pueden ser el mismo usuario"
}
```

### Prueba 3: crear ticket con creador ≠ asignado (válido)

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Ticket C",
  "description": "Descripción",
  "createdBy": "juan",
  "assignedTo": "maria"
}
```

**Resultado:** `201 Created`

### Prueba 4: modificar ticket a asignado = creador (inválido)

```
PUT http://localhost:8080/ticket-app/tickets/by-id/1
Content-Type: application/json

{
  "title": "Ticket C",
  "description": "Nueva descripción",
  "status": "IN_PROGRESS",
  "assignedTo": "juan"
}
```

**Resultado:** `409 Conflict` con el mismo error.

