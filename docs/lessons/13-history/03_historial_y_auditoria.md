# Lección 13 — Historial, auditoría y CascadeType

## ¿Por qué un historial en tabla separada?

Una alternativa ingenua sería guardar el historial como texto en el propio ticket:

```java
// Opción mala: historial como texto en el ticket
private String historial = "NEW → IN_PROGRESS (15/04/2026)\nIN_PROGRESS → RESOLVED (16/04/2026)";
```

Esto parece simple, pero tiene varios problemas:

| Problema | Consecuencia |
|---|---|
| No se puede consultar por fecha o estado | Imposible filtrar "todos los cambios de hoy" |
| No se puede indexar | Búsquedas lentas |
| No tiene estructura | Difícil de parsear desde el cliente |
| Crece sin límite | La columna se hace enorme |

Una tabla de historial resuelve todo esto: cada cambio es una **fila independiente** con sus propios campos indexables.

---

## El patrón de auditoría

El patrón que usaste en esta lección se llama **tabla de auditoría** o **tabla de historial**:

```
Acción en la entidad principal    →    Se registra en la tabla de historial
──────────────────────────────         ──────────────────────────────────────
Crear ticket (estado = NEW)       →    { previous: null, new: "NEW", fecha: ahora }
Actualizar estado a IN_PROGRESS   →    { previous: "NEW", new: "IN_PROGRESS", fecha: ahora }
Actualizar estado a RESOLVED      →    { previous: "IN_PROGRESS", new: "RESOLVED", fecha: ahora }
```

La regla fundamental de una tabla de auditoría es: **nunca se borran sus registros**. Es un log inmutable del ciclo de vida de la entidad.

---

## `CascadeType` — qué operaciones se propagan

`CascadeType` define qué operaciones de JPA sobre la entidad padre se propagan automáticamente a las entidades hijas:

```java
@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
private List<TicketHistory> history = new ArrayList<>();
```

| Valor | Qué propaga |
|---|---|
| `PERSIST` | Si guardas el ticket, también guarda los `TicketHistory` en la lista |
| `MERGE` | Si actualizas el ticket con `merge()`, también actualiza los hijos |
| `REMOVE` | Si borras el ticket, también borra todos sus `TicketHistory` |
| `REFRESH` | Si recargas el ticket desde la BD, también recarga los hijos |
| `ALL` | Todos los anteriores combinados |

> **¿Por qué en este caso `CascadeType.ALL` puede ser riesgoso?**
> Porque incluye `REMOVE`: si alguien borra un ticket, todos sus registros históricos también se borran. En un sistema de auditoría real, eso no es aceptable.
>
> Para el contexto del curso (sin borrado de tickets en producción) es aceptable. Si quisieras proteger el historial, usarías solo `CascadeType.PERSIST` en lugar de `ALL`.

---

## `orphanRemoval` — qué pasa con los huérfanos

```java
@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = false)
private List<TicketHistory> history = new ArrayList<>();
```

`orphanRemoval = true` elimina un `TicketHistory` si lo remueves de la lista `history` del `Ticket`. Como el historial no debe borrarse nunca, lo dejamos en `false`.

| Valor | Comportamiento |
|---|---|
| `orphanRemoval = true` | Si sacas un elemento de la lista, JPA lo borra de la BD |
| `orphanRemoval = false` | Si sacas un elemento de la lista, JPA no lo borra de la BD |

---

## La relación completa vista desde ambos lados

```
Ticket (id=1, status="RESOLVED", assignedTo=soporte@empresa.cl)
│
├── TicketHistory (id=1, prevStatus=null,         newStatus="NEW",        prevEmail=null,  newEmail=null,                changedAt=10:30)
├── TicketHistory (id=2, prevStatus="NEW",        newStatus="IN_PROGRESS",prevEmail=null,  newEmail=null,                changedAt=10:35)
├── TicketHistory (id=3, prevStatus=null,         newStatus=null,         prevEmail=null,  newEmail="soporte@empresa.cl",changedAt=10:40)
└── TicketHistory (id=4, prevStatus="IN_PROGRESS",newStatus="RESOLVED",  prevEmail=null,  newEmail=null,                changedAt=11:00)
```

En la base de datos:

```
tabla tickets:
id | title              | status
1  | Red caída piso 3   | RESOLVED

tabla ticket_history:
id | ticket_id | previous_status | new_status   | previous_assigned_email | new_assigned_email  | changed_at
1  | 1         | NULL            | NEW          | NULL                    | NULL                | 2026-04-15 10:30:00
2  | 1         | NEW             | IN_PROGRESS  | NULL                    | NULL                | 2026-04-15 10:35:00
3  | 1         | NULL            | NULL         | NULL                    | soporte@empresa.cl  | 2026-04-15 10:40:00
4  | 1         | IN_PROGRESS     | RESOLVED     | NULL                    | NULL                | 2026-04-15 11:00:00
```

La tabla `tickets` solo guarda el **estado actual**. La tabla `ticket_history` guarda **toda la evolución**.

---

## El flujo completo de un cambio de estado

```
[Cliente]
  │ PUT /tickets/1  {"status": "IN_PROGRESS"}
  ↓
[TicketController.updateById(id=1, request)]
  │ Llama a service.updateById(1, request)
  │ No sabe nada sobre historial
  ↓
[TicketService.updateById(id=1, request)]
  │ Carga el ticket de la BD → status actual = "NEW"
  │ request.getStatus() = "IN_PROGRESS" ≠ "NEW" → hay cambio
  │ Actualiza ticket.status = "IN_PROGRESS"
  │ Llama a recordChange(ticket, "NEW", "IN_PROGRESS", null, null, null)
  │   └─ crea TicketHistory y llama a historyRepository.save(entry)
  │ Llama a repository.save(ticket) → persiste el nuevo estado
  ↓
[Cliente]
  │ 200 OK con TicketResult (DTO)
```

El Controller es ajeno al historial. Toda la lógica de auditoría vive en el Service. Este es el principio de **responsabilidad única** aplicado a la capa de servicio.

El mismo principio aplica para el endpoint de historial: `TicketController` llama a `service.getHistory(id)`, que retorna `List<TicketHistoryResult>`. El Controller nunca toca el `TicketHistoryRepository` directamente.

---

## ¿Por qué no necesitamos `@JsonIgnore`?

En otros tutoriales verás `@JsonIgnore` en el campo `ticket` de `TicketHistory` y en la lista `history` de `Ticket`. Esto se hace para evitar el bucle de serialización:

```
Jackson serializa TicketHistory
  → intenta serializar Ticket
    → intenta serializar cada TicketHistory de la lista
      → intenta serializar Ticket (de nuevo)
        → bucle infinito → StackOverflowError
```

En este proyecto **no usamos `@JsonIgnore`** porque el Service nunca retorna entities directamente:

```
[TicketController] llama a service.getHistory(id)
     ↓
[TicketService] consulta el repository → obtiene List<TicketHistory>
     │ convierte cada entrada a TicketHistoryResult (record)
     ↓
[TicketController] retorna List<TicketHistoryResult>
     ↓
[Jackson] serializa TicketHistoryResult — un record plano, sin referencias circulares
```

`TicketHistoryResult` es un record con campos simples (Strings, LocalDateTime). Jackson lo serializa sin problema. La entity `TicketHistory` nunca llega a Jackson.

---

## ¿Por qué email y no FK a `User` para registrar el asignado?

Es tentador guardar el asignado como FK:

```java
// Tentación: FK a User
@ManyToOne
private User previousAssignedTo;

// Lo que usamos: email como String
private String previousAssignedEmail;
```

La tabla de historial es un **log inmutable**. Cada fila es un snapshot del estado en un momento dado. Hay varias razones para preferir el email:

| Razón | Explicación |
|---|---|
| **Independencia referencial** | Si el User se elimina, el historial no queda huérfano ni se borra en cascada |
| **Inmutabilidad real** | El email capturado en el momento del cambio no cambia aunque el usuario actualice su perfil |
| **Lectura directa** | En un log de auditoría, quieres ver el dato directamente, no hacer JOIN a otra tabla |
| **Coherencia con L12** | La asignación ya se hace por email en `AssignTicketRequest` — es natural mantener ese identificador en el historial |

Este patrón es común en sistemas de auditoría y Event Sourcing: guardar el valor en el momento del evento, no una referencia al objeto actual.

---

## ¿Por qué no hay `TicketHistoryController`?

`TicketHistory` es una **entidad débil** (weak entity): no tiene identidad propia ni puede existir sin un `Ticket` padre. Sus características:

- No tiene significado fuera del contexto de un Ticket
- Siempre se consulta en relación a un Ticket específico: "dame el historial del ticket N"
- No tiene operaciones de creación, actualización o borrado propias — su ciclo de vida está 100% gestionado por `TicketService`

Por eso el endpoint de historial vive en `TicketController`:

```
GET /tickets/by-id/{id}/history
```

Si tuviéramos un `TicketHistoryController` con `POST /ticket-history`, alguien podría insertar entradas falsas en el historial — violando la integridad del log de auditoría. Al no tener controller propio, la única forma de escribir en el historial es a través de las operaciones de negocio del Service.