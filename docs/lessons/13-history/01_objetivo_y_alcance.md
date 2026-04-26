# Lección 13 — Tabla de historial: @OneToMany y registro automático de cambios

## ¿De dónde venimos?

Tu aplicación ahora:

- Persiste tickets en base de datos real (L10)
- Conecta a MySQL local o Supabase en la nube (L11)
- Relaciona tickets con usuarios creadores y asignados (L12)

Pero hay un vacío importante: **cuando el estado de un ticket cambia, esa información se pierde**. Si un ticket pasa de `NEW` a `IN_PROGRESS` y luego a `RESOLVED`, no hay registro de cuándo ocurrió cada cambio ni quién lo hizo.

En soporte técnico, esa trazabilidad es fundamental: permite auditar tiempos de respuesta, identificar cuellos de botella y cumplir con acuerdos de nivel de servicio (SLA).

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás:

1. Una nueva entidad `TicketHistory` que registra cada cambio de estado Y de asignado de un ticket
2. La relación `@OneToMany` en `Ticket` → `TicketHistory`
3. Un DTO de respuesta `TicketHistoryResult` para exponer el historial sin exponer la entidad directamente
4. El `TicketService` actualizado para registrar historial automáticamente en `updateById()` y `assignTicket()`
5. Un endpoint `GET /tickets/by-id/{id}/history` en `TicketController` (no hay `TicketHistoryController`)

### Lo que vas a poder explicar

- ¿Qué hace `@OneToMany(mappedBy = "...", cascade = CascadeType.ALL)` en `Ticket`?
- ¿Qué hace `@ManyToOne` en `TicketHistory` apuntando a `Ticket`?
- ¿Qué significa `CascadeType.ALL` y cuándo usarlo?
- ¿Por qué el historial nunca se debe borrar?
- ¿Cómo registra el Service el historial sin que el Controller lo sepa?
- ¿Por qué `TicketHistory` no tiene un controller propio? ¿Qué es una entidad débil?
- ¿Por qué el historial de asignado guarda el email como String y no como FK a User?
- ¿Por qué no se necesita `@JsonIgnore` en las entities de historial?

---

## Nuevo requerimiento

| Requerimiento | Descripción |
|---|---|
| **REQ-18** | El sistema debe registrar automáticamente un historial de cambios de cada ticket: cambios de estado (con el estado anterior y el nuevo) y cambios de asignado (con el email del asignado anterior y el nuevo), incluyendo la fecha y hora de cada cambio |

---

## La estructura que tienes al comenzar

```
src/main/java/cl/duoc/fullstack/tickets/
├── model/
│   ├── Ticket.java
│   └── User.java
├── respository/
│   ├── TicketRepository.java
│   └── UserRepository.java
├── service/
│   ├── TicketService.java
│   └── UserService.java
└── controller/
    ├── TicketController.java
    └── UserController.java
```

La estructura al terminar:

```
src/main/java/cl/duoc/fullstack/tickets/
├── model/
│   ├── Ticket.java              ← con @OneToMany a TicketHistory
│   ├── User.java
│   └── TicketHistory.java       ← nueva entidad
├── dto/
│   ├── TicketRequest.java
│   ├── TicketCommand.java
│   ├── TicketResult.java
│   ├── TicketResponse.java
│   ├── UserRequest.java
│   ├── UserResult.java
│   ├── AssignTicketRequest.java
│   └── TicketHistoryResult.java  ← nuevo DTO de respuesta
├── respository/
│   ├── TicketRepository.java
│   ├── UserRepository.java
│   └── TicketHistoryRepository.java   ← nuevo
├── service/
│   ├── TicketService.java       ← registra historial en updateById() y assignTicket()
│   └── UserService.java
└── controller/
    ├── TicketController.java    ← nuevo endpoint GET /by-id/{id}/history
    └── UserController.java
```

---

## ¿Qué NO cubre esta lección?

| Tema | ¿Por qué queda afuera? |
|---|---|
| `@CreatedDate`, `@LastModifiedDate` (Spring Data Auditing) | Configuración adicional; el patrón manual es más claro para aprender |
| Quién realizó el cambio (`changedByEmail`) | Lo verás en la actividad individual |
| Notificaciones al cambiar estado | Fuera del alcance del curso |
| Paginación del historial | Requiere `Pageable`; el historial por ticket es pequeño en este contexto |