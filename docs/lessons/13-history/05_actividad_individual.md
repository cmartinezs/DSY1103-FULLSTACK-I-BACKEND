# Lección 13 — Actividad individual: registrar quién hizo el cambio

## Contexto

Tu sistema ya registra qué cambió (estado y asignado) y cuándo. Esta actividad extiende el historial para registrar también **quién** realizó el cambio: el email del usuario que ejecutó la operación.

En sistemas con autenticación (Spring Security), este dato vendría automáticamente del usuario autenticado en el contexto de la petición. Por ahora, lo recibiremos explícitamente en el cuerpo de la petición.

---

## Parte 1: agregar `changedByEmail` a `TicketHistory`

Agrega este campo a `TicketHistory.java`:

```java
@Column(name = "changed_by_email", length = 150)
private String changedByEmail;
```

- Es `String`, no FK a `User` — mismo razonamiento que `previousAssignedEmail` y `newAssignedEmail`: el historial es inmutable, debe guardar snapshots, no referencias.
- Puede ser `null` si no se proporciona (por compatibilidad retroactiva).

---

## Parte 2: actualizar `TicketHistoryResult`

Agrega el campo al record:

```java
public record TicketHistoryResult(
    Long id,
    String previousStatus,
    String newStatus,
    String previousAssignedEmail,
    String newAssignedEmail,
    String changedByEmail,       // ← nuevo
    LocalDateTime changedAt,
    String comment
) {}
```

---

## Parte 3: actualizar `recordChange` en `TicketService`

Agrega el parámetro `changedByEmail` al método privado:

```java
private void recordChange(
    Ticket ticket,
    String previousStatus,
    String newStatus,
    String previousAssignedEmail,
    String newAssignedEmail,
    String changedByEmail,       // ← nuevo parámetro
    String comment) {

  boolean statusChanged = newStatus != null
      && !newStatus.equalsIgnoreCase(previousStatus == null ? "" : previousStatus);
  boolean assigneeChanged = !java.util.Objects.equals(previousAssignedEmail, newAssignedEmail);

  if (!statusChanged && !assigneeChanged) {
    return;
  }

  TicketHistory entry = new TicketHistory();
  entry.setTicket(ticket);
  entry.setPreviousStatus(statusChanged ? previousStatus : null);
  entry.setNewStatus(statusChanged ? newStatus : null);
  entry.setPreviousAssignedEmail(assigneeChanged ? previousAssignedEmail : null);
  entry.setNewAssignedEmail(assigneeChanged ? newAssignedEmail : null);
  entry.setChangedByEmail(changedByEmail);  // ← nuevo
  entry.setChangedAt(LocalDateTime.now());
  entry.setComment(comment);
  historyRepository.save(entry);
}
```

Actualiza también `toHistoryResult()`:

```java
private TicketHistoryResult toHistoryResult(TicketHistory h) {
  return new TicketHistoryResult(
      h.getId(),
      h.getPreviousStatus(),
      h.getNewStatus(),
      h.getPreviousAssignedEmail(),
      h.getNewAssignedEmail(),
      h.getChangedByEmail(),     // ← nuevo
      h.getChangedAt(),
      h.getComment()
  );
}
```

---

## Parte 4: recibir `changedByEmail` desde el cliente

Para que el cliente pueda informar quién realiza el cambio, agrega el campo a los DTOs de comando:

**`TicketCommand.java`** — agrega el campo:
```java
public record TicketCommand(
    String title,
    String description,
    String status,
    String createdByEmail,
    String changedByEmail       // ← nuevo (puede ser null)
) {}
```

**`AssignTicketRequest.java`** — agrega el campo:
```java
private String changedByEmail;   // ← nuevo (puede ser null)
```

Actualiza `updateById()` y `assignTicket()` en `TicketService` para pasar `changedByEmail` a `recordChange`:

```java
// En updateById():
recordChange(saved, previousStatus, saved.getStatus(),
    previousAssignedEmail, previousAssignedEmail,
    command.changedByEmail(), null);    // ← pasar changedByEmail

// En assignTicket():
recordChange(saved, null, null,
    previousAssignedEmail, newAssignedEmail,
    request.getChangedByEmail(), null); // ← pasar changedByEmail
```

---

## Pruebas requeridas

| Prueba | Resultado esperado |
|---|---|
| Actualizar estado enviando `changedByEmail` | La entrada de historial muestra el email en `changedByEmail` |
| Asignar ticket enviando `changedByEmail` | La entrada de historial muestra el email en `changedByEmail` |
| Actualizar sin enviar `changedByEmail` | La entrada de historial tiene `changedByEmail: null` (no falla) |
| `GET /tickets/by-id/{id}/history` | El campo `changedByEmail` aparece en todas las entradas (puede ser null en las anteriores) |

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| `TicketHistory` tiene el campo `changedByEmail` como `String` (no FK a User) | 20% |
| `TicketHistoryResult` incluye `changedByEmail` | 15% |
| `recordChange` acepta y registra `changedByEmail` | 25% |
| `updateById()` y `assignTicket()` pasan `changedByEmail` al método de registro | 25% |
| El campo es opcional (`null` cuando no se proporciona) y no provoca errores | 15% |

---

## Reflexión final

Cuando implementes Spring Security en un proyecto real, el `changedByEmail` no vendrá del cuerpo de la petición — vendrá del `SecurityContext`:

```java
String changedByEmail = SecurityContextHolder.getContext()
    .getAuthentication().getName();
```

El cliente ya no necesita enviarlo porque el servidor sabe quién es el usuario autenticado. El diseño con email como parámetro explícito que usaste aquí es la misma arquitectura — solo cambia de dónde viene el dato.