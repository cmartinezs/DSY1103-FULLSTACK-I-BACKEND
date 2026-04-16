# Lección 13 — Actividad individual: historial de asignaciones

## Contexto

Tu sistema registra el historial de cambios de estado. Esta actividad extiende esa funcionalidad para registrar también los cambios de usuario asignado: cada vez que un ticket es asignado o reasignado, queda un registro del usuario anterior y el nuevo.

---

## Parte 1: extender `TicketHistory` con el campo de asignación

Agrega dos campos a `TicketHistory.java`:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "previous_assigned_to_id")
@JsonIgnore
private User previousAssignedTo;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "new_assigned_to_id")
@JsonIgnore
private User newAssignedTo;
```

Ambos campos son opcionales (pueden ser null): un registro de historial puede ser de cambio de estado, de cambio de asignado, o de ambas cosas a la vez.

---

## Parte 2: actualizar `registrarCambioDeEstado` en `TicketService`

Modifica el método privado para que también acepte información de asignación:

```java
private void registrarCambio(
    Ticket ticket,
    String estadoAnterior,
    String estadoNuevo,
    User asignadoAnterior,
    User asignadoNuevo,
    String comentario) {

  // Solo registrar si hay algún cambio real
  boolean cambioEstado = estadoNuevo != null && !estadoNuevo.equalsIgnoreCase(estadoAnterior);
  boolean cambioAsignado = !java.util.Objects.equals(
      asignadoAnterior == null ? null : asignadoAnterior.getId(),
      asignadoNuevo == null ? null : asignadoNuevo.getId());

  if (!cambioEstado && !cambioAsignado) {
    return;   // No hay nada que registrar
  }

  TicketHistory entrada = new TicketHistory();
  entrada.setTicket(ticket);
  entrada.setPreviousStatus(estadoAnterior);
  entrada.setNewStatus(estadoNuevo != null ? estadoNuevo : ticket.getStatus());
  entrada.setPreviousAssignedTo(asignadoAnterior);
  entrada.setNewAssignedTo(asignadoNuevo);
  entrada.setChangedAt(LocalDateTime.now());
  entrada.setComment(comentario);
  historyRepository.save(entrada);
}
```

---

## Parte 3: actualizar `updateById` para registrar el cambio de asignado

```java
public Optional<Ticket> updateById(Long id, TicketRequest request) {
  return repository.findById(id).map(ticket -> {
    ticket.setTitle(request.getTitle());
    ticket.setDescription(request.getDescription());

    String estadoAnterior = ticket.getStatus();
    String estadoNuevo = request.getStatus();
    User asignadoAnterior = ticket.getAssignedTo();
    User asignadoNuevo = ticket.getAssignedTo();   // por defecto, no cambia

    if (estadoNuevo != null && !estadoNuevo.isBlank()) {
      ticket.setStatus(estadoNuevo);
    }

    if (request.getAssignedToId() != null) {
      User nuevoAsignado = userRepository.findById(request.getAssignedToId())
          .orElseThrow(() -> new IllegalArgumentException(
              "No existe un usuario con ID " + request.getAssignedToId()));
      ticket.setAssignedTo(nuevoAsignado);
      asignadoNuevo = nuevoAsignado;
    }

    registrarCambio(ticket, estadoAnterior, estadoNuevo, asignadoAnterior, asignadoNuevo, null);

    return repository.save(ticket);
  });
}
```

---

## Pruebas requeridas

| Prueba | Resultado esperado |
|---|---|
| Crear ticket y ver historial | 1 entrada: `previousAssignedTo: null`, `newAssignedTo: null` |
| Asignar ticket a usuario 1 | 1 nueva entrada con `newAssignedTo.id = 1` |
| Reasignar ticket a usuario 2 | 1 nueva entrada con `previousAssignedTo.id = 1`, `newAssignedTo.id = 2` |
| Cambiar estado sin cambiar asignado | 1 nueva entrada con el cambio de estado, `previousAssignedTo == newAssignedTo` |
| Enviar mismos datos (sin cambio real) | **No** se crea nueva entrada |

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| `TicketHistory` tiene los campos `previousAssignedTo` y `newAssignedTo` con las anotaciones correctas | 25% |
| `registrarCambio` detecta correctamente si hay cambio en asignado y/o estado | 30% |
| No se registra historial cuando no hay cambio real | 20% |
| El endpoint `GET /tickets/{id}/history` devuelve los nuevos campos | 15% |
| No hay errores de serialización (`@JsonIgnore` en los campos `User` del historial) | 10% |

---

## Desafío opcional

Agrega el campo `changedBy` a `TicketHistory`: el usuario que realizó el cambio (quién llamó al endpoint de actualización).

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "changed_by_id")
@JsonIgnore
private User changedBy;
```

Actualiza `TicketRequest` con un campo `changedById` y pásalo al servicio para registrarlo en el historial. Cuando el sistema tenga autenticación (Spring Security), este campo se obtendrá automáticamente del usuario autenticado — por ahora lo recibimos explícitamente del cliente.
