# Lección 13 — Checklist y rúbrica mínima

---

## Checklist de `TicketHistory.java`

- ☐ La clase tiene `@Entity` y `@Table(name = "ticket_history")`
- ☐ El campo `id` tiene `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- ☐ El campo `ticket` tiene `@ManyToOne(fetch = FetchType.LAZY)` y `@JoinColumn(name = "ticket_id", nullable = false)` — **sin** `@JsonIgnore`
- ☐ El campo `previousStatus` tiene `@Column(name = "previous_status")` y **no** tiene `nullable = false` (puede ser null)
- ☐ El campo `newStatus` tiene `@Column(name = "new_status")` y **no** tiene `nullable = false` (puede ser null si el registro es solo de cambio de asignado)
- ☐ El campo `previousAssignedEmail` es `String` con `@Column(name = "previous_assigned_email")` — **no** es FK a User
- ☐ El campo `newAssignedEmail` es `String` con `@Column(name = "new_assigned_email")` — **no** es FK a User
- ☐ El campo `changedAt` tiene `@Column(name = "changed_at", nullable = false)`
- ☐ La clase tiene `@NoArgsConstructor` (requerido por JPA) y `@AllArgsConstructor`
- ☐ **No hay** `@JsonIgnore` ni imports de `com.fasterxml.jackson` en la clase

---

## Checklist de `Ticket.java` (relación OneToMany)

- ☐ Tiene el campo `history` con `@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = false)`
- ☐ El campo `history` se inicializa con `new ArrayList<>()` (nunca debe ser null)
- ☐ El valor `mappedBy = "ticket"` coincide exactamente con el nombre del campo en `TicketHistory`
- ☐ **No hay** `@JsonIgnore` en el campo `history`

---

## Checklist de `TicketHistoryResult.java`

- ☐ Es un Java `record` en el paquete `dto/`
- ☐ Tiene los campos: `Long id`, `String previousStatus`, `String newStatus`, `String previousAssignedEmail`, `String newAssignedEmail`, `LocalDateTime changedAt`, `String comment`
- ☐ **No tiene** anotaciones JPA ni Jackson

---

## Checklist de `TicketHistoryRepository.java`

- ☐ Es una interfaz que extiende `JpaRepository<TicketHistory, Long>`
- ☐ Tiene el método `List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId)`
- ☐ Está en el paquete `respository/` (typo intencional)

---

## Checklist de `TicketService.java`

- ☐ El constructor recibe `TicketHistoryRepository` (además de los repositorios de L12)
- ☐ Existe el método privado `recordChange(Ticket, String, String, String, String, String)` que recibe: ticket, previousStatus, newStatus, previousAssignedEmail, newAssignedEmail, comment
- ☐ `recordChange()` solo guarda si hay cambio real (estado diferente O email diferente)
- ☐ `create()` llama a `recordChange()` después de guardar el ticket (con `previousStatus = null`, `newStatus = "NEW"`, emails = null)
- ☐ `updateById()` usa `orElseThrow` (no `.map()`), captura estado anterior, llama a `recordChange()` solo si el estado cambió
- ☐ `assignTicket()` captura email anterior, llama a `recordChange()` con el cambio de email
- ☐ Existe el método `getHistory(Long ticketId)` que retorna `Optional<List<TicketHistoryResult>>` (vacío si el ticket no existe)
- ☐ Existe el método privado `toHistoryResult(TicketHistory)` que convierte entity a DTO

---

## Checklist de `TicketController.java`

- ☐ El constructor **no** inyecta `TicketHistoryRepository` — solo `TicketService`
- ☐ Tiene el endpoint `GET /by-id/{id}/history` que devuelve `ResponseEntity<List<TicketHistoryResult>>`
- ☐ El endpoint llama a `service.getHistory(id)` (no al repository directamente)
- ☐ Si el ticket no existe, el endpoint devuelve `404 Not Found`
- ☐ Si el ticket existe, el endpoint devuelve `200 OK` con la lista (puede estar vacía)
- ☐ La lista viene ordenada de más reciente a más antiguo

---

## Checklist de pruebas

- ☐ Crear un ticket → `GET /tickets/by-id/{id}/history` muestra 1 entrada con `previousStatus: null`, `newStatus: "NEW"`, emails null
- ☐ Actualizar estado a `IN_PROGRESS` → historial muestra 2 entradas; la nueva con `previousStatus: "NEW"`, `newStatus: "IN_PROGRESS"`
- ☐ Asignar ticket a usuario por email → historial muestra nueva entrada con `previousAssignedEmail: null`, `newAssignedEmail: "email@..."`, status fields null
- ☐ Reasignar a otro usuario → historial muestra nueva entrada con `previousAssignedEmail: "anterior@..."`, `newAssignedEmail: "nuevo@..."`
- ☐ Enviar el mismo estado sin cambio real → historial **no** agrega una entrada nueva
- ☐ `GET /tickets/by-id/999/history` → `404 Not Found`
- ☐ En phpMyAdmin / Supabase, la tabla `ticket_history` tiene las filas correctas con los emails como strings

---

## Errores comunes

| Error | Causa probable | Solución |
|---|---|---|
| `StackOverflowError` al hacer `GET /history` | El endpoint retorna `List<TicketHistory>` (entity) en lugar de `List<TicketHistoryResult>` (DTO) | Verificar que `service.getHistory()` retorna DTOs y el controller los retorna directamente |
| Historial crea entrada aunque no hay cambio | `recordChange` no verifica si hay cambio real | Agregar la verificación `!statusChanged && !assigneeChanged → return` |
| `mappedBy` error al arrancar | El valor en `mappedBy` no coincide con el nombre del campo | Verificar que `mappedBy = "ticket"` coincida con `private Ticket ticket` en `TicketHistory` |
| La tabla `ticket_history` no se crea | `TicketHistory` no tiene `@Entity` o no está en el paquete escaneado | Verificar las anotaciones y el paquete de la clase |
| El primer registro de historial falla por `previousStatus NOT NULL` | La columna tiene `nullable = false` | Cambiar a `@Column(name = "previous_status")` sin `nullable = false` |
| Controller retorna 404 para ticket existente | `service.getHistory()` no está implementado correctamente | Verificar que retorna `Optional.of(lista)` (no `Optional.empty()`) cuando el ticket existe aunque no tenga historial |