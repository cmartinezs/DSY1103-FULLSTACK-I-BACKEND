# Lección 13 — Checklist y rúbrica mínima

---

## Checklist de `TicketHistory.java`

- ☐ La clase tiene `@Entity` y `@Table(name = "ticket_history")`
- ☐ El campo `id` tiene `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- ☐ El campo `ticket` tiene `@ManyToOne(fetch = FetchType.LAZY)`, `@JoinColumn(name = "ticket_id", nullable = false)` y `@JsonIgnore`
- ☐ El campo `previousStatus` tiene `@Column(name = "previous_status")` y **no** tiene `nullable = false` (puede ser null en la primera entrada)
- ☐ El campo `newStatus` tiene `@Column(name = "new_status", nullable = false)`
- ☐ El campo `changedAt` tiene `@Column(name = "changed_at", nullable = false)`
- ☐ La clase tiene `@NoArgsConstructor` (requerido por JPA)

---

## Checklist de `Ticket.java` (relación OneToMany)

- ☐ Tiene el campo `history` con `@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = false)`
- ☐ El campo `history` tiene `@JsonIgnore`
- ☐ El campo `history` se inicializa con `new ArrayList<>()` (nunca debe ser null)
- ☐ El valor `mappedBy = "ticket"` coincide exactamente con el nombre del campo en `TicketHistory`

---

## Checklist de `TicketHistoryRepository.java`

- ☐ Es una interfaz que extiende `JpaRepository<TicketHistory, Long>`
- ☐ Tiene el método `List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId)`

---

## Checklist de `TicketService.java`

- ☐ El constructor recibe `TicketHistoryRepository` (además de los repositorios de L12)
- ☐ Existe el método privado `registrarCambioDeEstado(Ticket, String, String, String)`
- ☐ `create()` llama a `registrarCambioDeEstado()` después de guardar el ticket (con `previousStatus = null`, `newStatus = "NEW"`)
- ☐ `updateById()` llama a `registrarCambioDeEstado()` **solo si el estado realmente cambió** (comparación `equalsIgnoreCase` para verificar que es distinto)
- ☐ Si el estado enviado es igual al actual, **no** se crea registro de historial
- ☐ Tiene el método `existsById(Long id)` que retorna `repository.existsById(id)`

---

## Checklist de `TicketController.java`

- ☐ Tiene inyectado `TicketHistoryRepository` en el constructor
- ☐ Tiene el endpoint `GET /{id}/history` que devuelve `200 OK` con la lista de historial
- ☐ Si el ticket no existe, el endpoint devuelve `404 Not Found`
- ☐ La lista viene ordenada de más reciente a más antiguo (`OrderByChangedAtDesc`)

---

## Checklist de pruebas

- ☐ Crear un ticket → `GET /tickets/{id}/history` muestra 1 entrada con `previousStatus: null` y `newStatus: "NEW"`
- ☐ Actualizar estado a `IN_PROGRESS` → historial muestra 2 entradas
- ☐ Actualizar estado a `RESOLVED` → historial muestra 3 entradas
- ☐ Enviar el mismo estado sin cambio → historial **no** agrega una entrada nueva
- ☐ `GET /tickets/999/history` → `404 Not Found`
- ☐ En phpMyAdmin / Supabase, la tabla `ticket_history` tiene las filas correctas con las FKs apuntando al ticket correcto
- ☐ No hay `StackOverflowError` al serializar `TicketHistory`

---

## Errores comunes

| Error | Causa probable | Solución |
|---|---|---|
| `StackOverflowError` al hacer `GET /history` | Falta `@JsonIgnore` en `TicketHistory.ticket` | Agregar `@JsonIgnore` sobre el campo `ticket` en `TicketHistory` |
| Historial crea entrada duplicada en cada actualización | No se compara el estado anterior con el nuevo | Agregar `!request.getStatus().equalsIgnoreCase(ticket.getStatus())` antes de registrar |
| `mappedBy` error al arrancar | El valor en `mappedBy` no coincide con el nombre del campo | Verificar que `mappedBy = "ticket"` coincida con `private Ticket ticket` en `TicketHistory` |
| La tabla `ticket_history` no se crea | `TicketHistory` no tiene `@Entity` o no está en el paquete escaneado | Verificar las anotaciones y el paquete de la clase |
| El primer registro de historial falla por `previousStatus NOT NULL` | La columna tiene `nullable = false` | Cambiar a `@Column(name = "previous_status")` sin `nullable = false` |
