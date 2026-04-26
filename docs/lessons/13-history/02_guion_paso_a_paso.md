# Lección 13 — Tutorial paso a paso: tabla de historial

---

## Paso 1: crear la entidad `TicketHistory`

Crea `src/main/java/cl/duoc/fullstack/tickets/model/TicketHistory.java`:

```java
package cl.duoc.fullstack.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id", nullable = false)
  private Ticket ticket;

  @Column(name = "previous_status", length = 20)
  private String previousStatus;

  @Column(name = "new_status", length = 20)
  private String newStatus;

  @Column(name = "previous_assigned_email", length = 150)
  private String previousAssignedEmail;

  @Column(name = "new_assigned_email", length = 150)
  private String newAssignedEmail;

  @Column(name = "changed_at", nullable = false)
  private LocalDateTime changedAt;

  @Column(length = 255)
  private String comment;
}
```

**Notas importantes:**

- `previousStatus` y `newStatus` registran el cambio de estado. Ambos pueden ser `null` si el registro es solo de cambio de asignado.
- `previousAssignedEmail` y `newAssignedEmail` registran el email del asignado antes y después del cambio. Son `String`, no FK a `User`.
- `comment` es opcional — permite agregar una nota al cambio.
- **No hay `@JsonIgnore`**: esta entity nunca se expone directamente al cliente. El Service la convierte a `TicketHistoryResult` antes de retornar, por lo que Jackson no la serializa y no hay riesgo de bucle circular.

> **¿Por qué el asignado se guarda como email y no como FK a User?**
> El historial es un **log inmutable** que registra un snapshot del estado en el momento del cambio. Si usáramos FK a User y ese usuario fuera eliminado o modificado en el futuro, el historial quedaría inconsistente. El email es el dato identitario que existía en ese momento — autosuficiente y no referencial.

---

## Paso 2: agregar la relación `@OneToMany` en `Ticket`

Abre `Ticket.java` y agrega al final del cuerpo de la clase:

```java
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

// ... dentro de la clase Ticket:

  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<TicketHistory> history = new ArrayList<>();
```

> **¿Qué hace `mappedBy = "ticket"`?**
> Le dice a JPA que la clave foránea está en el campo `ticket` de la clase `TicketHistory`. JPA no crea una columna nueva en la tabla `tickets` — la FK ya existe en `ticket_history.ticket_id`.

> **¿Qué hace `cascade = CascadeType.ALL`?**
> Propaga las operaciones de persistencia desde `Ticket` hacia sus `TicketHistory`. Si guardas un `Ticket` que tiene entradas en `history`, JPA también guarda los `TicketHistory` automáticamente.

> **¿Por qué no hay `@JsonIgnore` en la lista `history`?**
> El historial se expone a través del endpoint `GET /tickets/by-id/{id}/history`, que retorna `List<TicketHistoryResult>` (un DTO record). Jackson nunca serializa el `Ticket` ni sus colecciones directamente — el Service convierte todo a DTOs antes de retornar al controller.

> **¿Por qué `orphanRemoval = false`?**
> El historial nunca debe borrarse, aunque se borre el ticket. Lo dejamos en `false` para que los registros históricos persistan incluso si el ticket es eliminado. (En producción se usaría borrado lógico, pero eso está fuera del alcance del curso.)

---

## Paso 3: crear `TicketHistoryRepository`

Crea `src/main/java/cl/duoc/fullstack/tickets/respository/TicketHistoryRepository.java`:

```java
package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.TicketHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

  // Devuelve el historial de un ticket ordenado del más reciente al más antiguo
  List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}
```

---

## Paso 4: crear el DTO `TicketHistoryResult`

Crea `src/main/java/cl/duoc/fullstack/tickets/dto/TicketHistoryResult.java`:

```java
package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDateTime;

public record TicketHistoryResult(
    Long id,
    String previousStatus,
    String newStatus,
    String previousAssignedEmail,
    String newAssignedEmail,
    LocalDateTime changedAt,
    String comment
) {}
```

Este record es el contrato de respuesta del historial. El Service construye instancias de este record a partir de la entity `TicketHistory`, asegurando que Jackson solo serialice el DTO — nunca la entity.

---

## Paso 5: actualizar `TicketService` para registrar el historial

El historial debe registrarse automáticamente cuando el estado o el asignado cambian. El Controller no se entera — es responsabilidad del Service.

Inyecta `TicketHistoryRepository` en `TicketService`:

```java
@Service
public class TicketService {

  private TicketRepository repository;
  private UserRepository userRepository;
  private TicketHistoryRepository historyRepository;   // ← nuevo

  public TicketService(
      TicketRepository repository,
      UserRepository userRepository,
      TicketHistoryRepository historyRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
    this.historyRepository = historyRepository;
  }
```

Agrega un método privado `recordChange` y otro de conversión a DTO:

```java
  private void recordChange(
      Ticket ticket,
      String previousStatus,
      String newStatus,
      String previousAssignedEmail,
      String newAssignedEmail,
      String comment) {

    boolean statusChanged = newStatus != null
        && !newStatus.equalsIgnoreCase(previousStatus == null ? "" : previousStatus);
    boolean assigneeChanged = !java.util.Objects.equals(previousAssignedEmail, newAssignedEmail);

    if (!statusChanged && !assigneeChanged) {
      return; // no hay cambio real → no registrar
    }

    TicketHistory entry = new TicketHistory();
    entry.setTicket(ticket);
    entry.setPreviousStatus(statusChanged ? previousStatus : null);
    entry.setNewStatus(statusChanged ? newStatus : null);
    entry.setPreviousAssignedEmail(assigneeChanged ? previousAssignedEmail : null);
    entry.setNewAssignedEmail(assigneeChanged ? newAssignedEmail : null);
    entry.setChangedAt(LocalDateTime.now());
    entry.setComment(comment);
    historyRepository.save(entry);
  }

  private TicketHistoryResult toHistoryResult(TicketHistory h) {
    return new TicketHistoryResult(
        h.getId(),
        h.getPreviousStatus(),
        h.getNewStatus(),
        h.getPreviousAssignedEmail(),
        h.getNewAssignedEmail(),
        h.getChangedAt(),
        h.getComment()
    );
  }
```

Actualiza `create()` para registrar el historial de creación:

```java
  public TicketResult create(TicketCommand command) {
    if (repository.existsByTitle(command.title())) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título '" + command.title() + "'");
    }

    User createdBy = userRepository.findByEmail(command.createdByEmail())
        .orElseThrow(() -> new BadRequestException(
            "No existe un usuario con el email '" + command.createdByEmail() + "'"));

    Ticket ticket = new Ticket();
    ticket.setTitle(command.title());
    ticket.setDescription(command.description());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));
    ticket.setCreatedBy(createdBy);

    Ticket saved = repository.save(ticket);

    // Registrar historial: el ticket nació en estado NEW (sin estado anterior)
    recordChange(saved, null, "NEW", null, null, "Ticket creado");

    return toResult(saved);
  }
```

Actualiza `updateById()` para registrar el historial cuando cambia el estado:

```java
  public TicketResult updateById(Long id, TicketCommand command) {
    Ticket ticket = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Ticket con id " + id + " no existe"));

    // Capturar valores anteriores para el historial
    String previousStatus = ticket.getStatus();
    String previousAssignedEmail = ticket.getAssignedTo() != null
        ? ticket.getAssignedTo().getEmail()
        : null;

    ticket.setTitle(command.title());
    ticket.setDescription(command.description());

    if (command.status() != null && !command.status().isBlank()) {
      ticket.setStatus(command.status());
    }

    Ticket saved = repository.save(ticket);

    recordChange(saved, previousStatus, saved.getStatus(), previousAssignedEmail, previousAssignedEmail, null);

    return toResult(saved);
  }
```

Actualiza `assignTicket()` para registrar el cambio de asignado:

```java
  public Optional<TicketResult> assignTicket(Long id, AssignTicketRequest request) {
    if (!repository.existsById(id)) {
      return Optional.empty();
    }

    Ticket ticket = repository.findById(id).orElseThrow();

    String previousAssignedEmail = ticket.getAssignedTo() != null
        ? ticket.getAssignedTo().getEmail()
        : null;
    String newAssignedEmail;

    if (request.getAssignedToEmail() == null || request.getAssignedToEmail().isBlank()) {
      ticket.setAssignedTo(null);
      newAssignedEmail = null;
    } else {
      User assignee = userRepository.findByEmail(request.getAssignedToEmail())
          .orElseThrow(() -> new BadRequestException(
              "No existe un usuario con el email '" + request.getAssignedToEmail() + "'"));
      ticket.setAssignedTo(assignee);
      newAssignedEmail = assignee.getEmail();
    }

    Ticket saved = repository.save(ticket);

    recordChange(saved, null, null, previousAssignedEmail, newAssignedEmail, null);

    return Optional.of(toResult(saved));
  }
```

Agrega el método `getHistory()` que el Controller usará:

```java
  public Optional<List<TicketHistoryResult>> getHistory(Long ticketId) {
    if (!repository.existsById(ticketId)) {
      return Optional.empty();
    }
    List<TicketHistoryResult> historial = historyRepository
        .findByTicketIdOrderByChangedAtDesc(ticketId)
        .stream()
        .map(this::toHistoryResult)
        .toList();
    return Optional.of(historial);
  }
```

> **¿Por qué el Controller no sabe que se está registrando historial?**
> `updateById()` y `assignTicket()` registran el historial internamente. Esto aplica el principio de **responsabilidad única**: el Service es dueño de la lógica de negocio, incluida la auditoría. El Controller solo orquesta la petición HTTP.

---

## Paso 6: agregar el endpoint de historial en `TicketController`

Agrega en `TicketController`:

```java
import cl.duoc.fullstack.tickets.dto.TicketHistoryResult;
import java.util.List;

// El constructor NO cambia — TicketController solo inyecta TicketService:
public TicketController(TicketService service) {
  this.service = service;
}

// Nuevo endpoint:
@GetMapping("/by-id/{id}/history")
public ResponseEntity<List<TicketHistoryResult>> getHistory(@PathVariable Long id) {
  return service.getHistory(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
}
```

> **¿Por qué el Controller no inyecta `TicketHistoryRepository`?**
> La arquitectura de 5 capas establece que el Controller nunca habla directamente con el Repository. La consulta del historial es responsabilidad del Service, que también se encarga de convertir las entities a DTOs.

> **¿Por qué la URL es `/by-id/{id}/history` y no `/{id}/history`?**
> Para ser coherente con el patrón existente en el mismo Controller: `GET /tickets/by-id/{id}`, `PUT /tickets/by-id/{id}`, etc. El historial es un subrecurso del ticket, representado como `/by-id/{id}/history`.

> **¿Por qué no hay `TicketHistoryController`?**
> `TicketHistory` es una **entidad débil** (weak entity): no puede existir ni tiene sentido sin su Ticket padre. Acceder al historial siempre requiere el ID del ticket. No hay caso de uso donde se consulte el historial sin saber a qué ticket pertenece, por lo que un controller dedicado añadiría complejidad sin valor.

---

## Paso 7: probar el flujo completo

### Crear un ticket

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "Red caída en piso 3", "description": "Sin internet desde las 9am", "createdByEmail": "admin@empresa.cl" }
```

Respuesta: `201 Created`, ticket con `id: 1` en estado `NEW`.

### Consultar historial inicial

```
GET http://localhost:8080/ticket-app/tickets/by-id/1/history
```

Respuesta esperada:
```json
[
  {
    "id": 1,
    "previousStatus": null,
    "newStatus": "NEW",
    "previousAssignedEmail": null,
    "newAssignedEmail": null,
    "changedAt": "2026-04-15T10:30:00",
    "comment": "Ticket creado"
  }
]
```

### Cambiar el estado del ticket

```
PUT http://localhost:8080/ticket-app/tickets/by-id/1
Content-Type: application/json

{
  "title": "Red caída en piso 3",
  "description": "Sin internet desde las 9am",
  "status": "IN_PROGRESS",
  "createdByEmail": "admin@empresa.cl"
}
```

### Asignar el ticket a un usuario

```
PATCH http://localhost:8080/ticket-app/tickets/by-id/1/assign
Content-Type: application/json

{ "assignedToEmail": "soporte@empresa.cl" }
```

### Consultar historial actualizado

```
GET http://localhost:8080/ticket-app/tickets/by-id/1/history
```

Respuesta esperada:
```json
[
  {
    "id": 3,
    "previousStatus": null,
    "newStatus": null,
    "previousAssignedEmail": null,
    "newAssignedEmail": "soporte@empresa.cl",
    "changedAt": "2026-04-15T10:40:00",
    "comment": null
  },
  {
    "id": 2,
    "previousStatus": "NEW",
    "newStatus": "IN_PROGRESS",
    "previousAssignedEmail": null,
    "newAssignedEmail": null,
    "changedAt": "2026-04-15T10:35:00",
    "comment": null
  },
  {
    "id": 1,
    "previousStatus": null,
    "newStatus": "NEW",
    "previousAssignedEmail": null,
    "newAssignedEmail": null,
    "changedAt": "2026-04-15T10:30:00",
    "comment": "Ticket creado"
  }
]
```

### Verificar en la base de datos

En phpMyAdmin o Supabase, la tabla `ticket_history` debe mostrar todos los registros con los estados, emails y fechas correctos.

---

## Paso 8: reflexiona antes de cerrar

1. ¿Qué pasaría si `orphanRemoval = true`? ¿El historial se borraría si se borra el ticket?
2. ¿Por qué el `Controller` no sabe que se está registrando historial cuando llama a `updateById()`?
3. Si el mismo estado se envía dos veces (`NEW` → `NEW`) y el asignado no cambia, ¿se crea un registro de historial? ¿Por qué?
4. ¿Por qué el email del asignado es más adecuado que el ID de usuario para un log de auditoría?
5. Si `TicketHistory` tuviera su propio Controller, ¿qué problemas aparecerían? ¿Podría alguien crear un historial falso via POST?