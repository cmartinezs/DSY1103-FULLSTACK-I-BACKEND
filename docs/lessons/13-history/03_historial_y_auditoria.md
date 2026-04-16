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
Ticket (id=1, status="RESOLVED")
│
└── TicketHistory (id=1, previous=null,     new="NEW",         changedAt=10:30)
└── TicketHistory (id=2, previous="NEW",    new="IN_PROGRESS", changedAt=10:35)
└── TicketHistory (id=3, previous="IN_PROGRESS", new="RESOLVED", changedAt=11:00)
```

En la base de datos:

```
tabla tickets:
id | title              | status
1  | Red caída piso 3   | RESOLVED

tabla ticket_history:
id | ticket_id | previous_status | new_status   | changed_at
1  | 1         | NULL            | NEW          | 2026-04-15 10:30:00
2  | 1         | NEW             | IN_PROGRESS  | 2026-04-15 10:35:00
3  | 1         | IN_PROGRESS     | RESOLVED     | 2026-04-15 11:00:00
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
  │ Llama a registrarCambioDeEstado(ticket, "NEW", "IN_PROGRESS", null)
  │   └─ crea TicketHistory y llama a historyRepository.save(entrada)
  │ Llama a repository.save(ticket) → persiste el nuevo estado
  ↓
[Cliente]
  │ 200 OK con el ticket actualizado
```

El Controller es ajeno al historial. Toda la lógica de auditoría vive en el Service. Este es el principio de **responsabilidad única** aplicado a la capa de servicio.

---

## ¿Qué significa `@JsonIgnore` en `TicketHistory.ticket`?

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ticket_id", nullable = false)
@JsonIgnore
private Ticket ticket;
```

Sin `@JsonIgnore`:
1. Jackson serializa `TicketHistory`
2. Encuentra el campo `ticket` → intenta serializar el `Ticket`
3. El `Ticket` tiene una lista `history` → intenta serializar cada `TicketHistory`
4. Cada `TicketHistory` tiene un campo `ticket` → vuelve al paso 2
5. **Bucle infinito → `StackOverflowError`**

Con `@JsonIgnore`:
1. Jackson serializa `TicketHistory`
2. Encuentra `ticket` → lo ignora
3. Serialización completa sin bucle

La alternativa a `@JsonIgnore` son los DTOs de respuesta (que excluyen los campos problemáticos), pero para el alcance de este curso `@JsonIgnore` es suficiente.
