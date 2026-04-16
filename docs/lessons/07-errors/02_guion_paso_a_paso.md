# Lección 07 - Tutorial paso a paso: validaciones y errores

Sigue esta guía en orden. Vas a agregar validaciones de negocio en `TicketService` y manejo de excepciones en `TicketController`.

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

Actualiza el método `updateById()`:

```java
public Ticket updateById(Long id, Ticket ticket) {
    Ticket toUpdate = this.repository.getById(id);
    if (toUpdate == null) {
        return null;
    }

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
    return toUpdate;
}
```

---

## Paso 4: actualizar `TicketController.create()`

Envuelve el `service.create()` en try/catch para capturar la excepción:

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody Ticket ticket) {
    try {
        Ticket created = this.service.create(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

---

## Paso 5: actualizar `TicketController.updateById()`

Envuelve el `service.updateById()` en try/catch:

```java
@PutMapping("/by-id/{id}")
public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody Ticket ticket) {
    try {
        Ticket updated = this.service.updateById(id, ticket);
        if (updated != null) {
            return ResponseEntity.status(200).body(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Ticket con ID " + id + " no encontrado"));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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

**Resultado:** `400 Bad Request` con:

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

**Resultado:** `400 Bad Request` con el mismo error.

