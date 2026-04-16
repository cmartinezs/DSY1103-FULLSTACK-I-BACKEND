# Lección 13 — Tutorial paso a paso: tabla de historial

---

## Paso 1: crear la entidad `TicketHistory`

Crea `src/main/java/cl/duoc/fullstack/tickets/model/TicketHistory.java`:

```java
package cl.duoc.fullstack.tickets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private Ticket ticket;

  @Column(name = "previous_status", length = 20)
  private String previousStatus;

  @Column(name = "new_status", nullable = false, length = 20)
  private String newStatus;

  @Column(name = "changed_at", nullable = false)
  private LocalDateTime changedAt;

  @Column(length = 255)
  private String comment;
}
```

**Notas importantes:**

- `@JsonIgnore` en el campo `ticket` es **obligatorio**: cuando se serializa un `TicketHistory`, Jackson no debe intentar serializar el `Ticket` padre (que contiene la lista de historiales), lo que causaría un bucle infinito.
- `previousStatus` puede ser `null` — el primer registro de historial (cuando se crea el ticket con estado `NEW`) no tiene estado anterior.
- `comment` es opcional — permite agregar una nota al cambio de estado.

---

## Paso 2: agregar la relación `@OneToMany` en `Ticket`

Abre `Ticket.java` y agrega al final del cuerpo de la clase:

```java
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

// ... dentro de la clase Ticket:

  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = false)
  @JsonIgnore
  private List<TicketHistory> history = new ArrayList<>();
```

> **¿Qué hace `mappedBy = "ticket"`?**
> Le dice a JPA que la clave foránea está en el campo `ticket` de la clase `TicketHistory`. JPA no crea una columna nueva en la tabla `tickets` — la FK ya existe en `ticket_history.ticket_id`.

> **¿Qué hace `cascade = CascadeType.ALL`?**
> Propaga las operaciones de persistencia desde `Ticket` hacia sus `TicketHistory`. Si guardas un `Ticket` que tiene entradas en `history`, JPA también guarda los `TicketHistory` automáticamente.

> **¿Por qué `@JsonIgnore` en la lista?**
> Mismo motivo que en `@ManyToOne`: evitar serialización circular. El historial se expone a través del endpoint dedicado `GET /tickets/{id}/history`, no dentro del ticket.

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

## Paso 4: actualizar `TicketService` para registrar el historial

El historial debe registrarse automáticamente cuando el estado de un ticket cambia. El Controller no se entera — es responsabilidad del Service.

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

Agrega un método privado de ayuda para crear un registro de historial:

```java
  private void registrarCambioDeEstado(Ticket ticket, String estadoAnterior, String estadoNuevo, String comentario) {
    TicketHistory entrada = new TicketHistory();
    entrada.setTicket(ticket);
    entrada.setPreviousStatus(estadoAnterior);
    entrada.setNewStatus(estadoNuevo);
    entrada.setChangedAt(LocalDateTime.now());
    entrada.setComment(comentario);
    historyRepository.save(entrada);
  }
```

Actualiza `create()` para registrar el historial de creación:

```java
  public Ticket create(TicketRequest request) {
    if (repository.existsByTitle(request.getTitle())) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título '" + request.getTitle() + "'");
    }

    Ticket ticket = new Ticket();
    ticket.setTitle(request.getTitle());
    ticket.setDescription(request.getDescription());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));

    // Resolver usuarios (igual que en L12)
    if (request.getCreatedById() != null) {
      userRepository.findById(request.getCreatedById()).ifPresent(ticket::setCreatedBy);
    }
    if (request.getAssignedToId() != null) {
      userRepository.findById(request.getAssignedToId()).ifPresent(ticket::setAssignedTo);
    }

    Ticket guardado = repository.save(ticket);

    // Registrar historial: el ticket nació en estado NEW (sin estado anterior)
    registrarCambioDeEstado(guardado, null, "NEW", "Ticket creado");

    return guardado;
  }
```

Actualiza `updateById()` para registrar el historial cuando el estado cambia:

```java
  public Optional<Ticket> updateById(Long id, TicketRequest request) {
    return repository.findById(id).map(ticket -> {
      ticket.setTitle(request.getTitle());
      ticket.setDescription(request.getDescription());

      // Registrar historial solo si el estado realmente cambió
      if (request.getStatus() != null
          && !request.getStatus().isBlank()
          && !request.getStatus().equalsIgnoreCase(ticket.getStatus())) {

        String estadoAnterior = ticket.getStatus();
        ticket.setStatus(request.getStatus());
        registrarCambioDeEstado(ticket, estadoAnterior, request.getStatus(), null);
      }

      // Actualizar usuario asignado si se proporcionó
      if (request.getAssignedToId() != null) {
        userRepository.findById(request.getAssignedToId())
            .ifPresent(ticket::setAssignedTo);
      }

      return repository.save(ticket);
    });
  }
```

> **¿Por qué comparamos `!request.getStatus().equalsIgnoreCase(ticket.getStatus())`?**
> Si el cliente envía el mismo estado que ya tiene el ticket, no hay cambio real y no debe crearse un registro de historial. El historial debe reflejar cambios reales, no actualizaciones sin efecto.

---

## Paso 5: agregar el endpoint de historial en `TicketController`

Agrega en `TicketController`:

```java
import cl.duoc.fullstack.tickets.model.TicketHistory;
import cl.duoc.fullstack.tickets.respository.TicketHistoryRepository;

// Inyectar en el constructor:
private TicketHistoryRepository historyRepository;

public TicketController(TicketService service, TicketHistoryRepository historyRepository) {
  this.service = service;
  this.historyRepository = historyRepository;
}

// Nuevo endpoint:
@GetMapping("/{id}/history")
public ResponseEntity<List<TicketHistory>> getHistory(@PathVariable Long id) {
  if (!service.existsById(id)) {
    return ResponseEntity.notFound().build();
  }
  List<TicketHistory> history = historyRepository.findByTicketIdOrderByChangedAtDesc(id);
  return ResponseEntity.ok(history);
}
```

Agrega también `existsById` al `TicketService`:

```java
public boolean existsById(Long id) {
  return repository.existsById(id);
}
```

---

## Paso 6: probar el flujo completo

### Crear un ticket

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "Red caída en piso 3", "description": "Sin internet desde las 9am" }
```

Respuesta: `201 Created`, ticket con `id: 1` en estado `NEW`.

### Consultar historial inicial

```
GET http://localhost:8080/ticket-app/tickets/1/history
```

Respuesta esperada:
```json
[
  {
    "id": 1,
    "previousStatus": null,
    "newStatus": "NEW",
    "changedAt": "2026-04-15T10:30:00",
    "comment": "Ticket creado"
  }
]
```

### Cambiar el estado del ticket

```
PUT http://localhost:8080/ticket-app/tickets/1
Content-Type: application/json

{
  "title": "Red caída en piso 3",
  "description": "Sin internet desde las 9am",
  "status": "IN_PROGRESS"
}
```

### Consultar historial actualizado

```
GET http://localhost:8080/ticket-app/tickets/1/history
```

Respuesta esperada:
```json
[
  {
    "id": 2,
    "previousStatus": "NEW",
    "newStatus": "IN_PROGRESS",
    "changedAt": "2026-04-15T10:35:00",
    "comment": null
  },
  {
    "id": 1,
    "previousStatus": null,
    "newStatus": "NEW",
    "changedAt": "2026-04-15T10:30:00",
    "comment": "Ticket creado"
  }
]
```

### Cerrar el ticket

```
PUT http://localhost:8080/ticket-app/tickets/1
Content-Type: application/json

{
  "title": "Red caída en piso 3",
  "description": "Sin internet desde las 9am",
  "status": "RESOLVED"
}
```

El historial tendrá ahora 3 entradas ordenadas del más reciente al más antiguo.

### Verificar en la base de datos

En phpMyAdmin o Supabase, la tabla `ticket_history` debe mostrar todos los registros con los estados y fechas correctos.

---

## Paso 7: reflexiona antes de cerrar

1. ¿Qué pasaría si `orphanRemoval = true`? ¿El historial se borraría si se borra el ticket?
2. ¿Por qué el `Controller` no sabe que se está registrando historial cuando llama a `updateById()`?
3. Si el mismo estado se envía dos veces (`NEW` → `NEW`), ¿se crea un registro de historial? ¿Por qué?
4. El historial se guarda en una tabla separada. ¿Qué ventaja tiene esto frente a guardar el historial como texto en el mismo ticket?
