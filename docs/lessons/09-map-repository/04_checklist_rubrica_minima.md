# Lección 09 - Checklist y rúbrica mínima

Usa esta lista para verificar que implementaste correctamente el Repository con Map y el filtro por estado antes de dar la lección por terminada.

---

## Checklist del `TicketRepository`

### Almacenamiento

- ☐ El campo de almacenamiento es `Map<Long, Ticket> db = new HashMap<>()`
- ☐ **No existe** `List<Ticket> tickets` (fue reemplazada)
- ☐ `currentId` empieza en `1L`
- ☐ Los datos semilla tienen IDs `1` y `2` con descripciones y `estimatedResolutionDate` correctos

### Método `getAll()`

- ☐ Devuelve `new ArrayList<>(db.values())` ordenado por `createdAt`
- ☐ No devuelve `db.values()` directamente (esa es una vista no ordenable)

### Método `getAll(String statusFilter)` (sobrecarga)

- ☐ Si `statusFilter` es `null` o blank, devuelve todos los tickets ordenados
- ☐ Si `statusFilter` tiene valor, filtra usando `equalsIgnoreCase`
- ☐ La lista resultante está ordenada por `createdAt` antes de filtrar

### Método `findById(Long id)`

- ☐ Usa `Optional.ofNullable(db.get(id))`
- ☐ **No** usa `stream().filter()` (eso era la versión List con O(n))
- ☐ El acceso es O(1)

### Método `save(Ticket ticket)`

- ☐ Asigna `ticket.setId(currentId)` **antes** de guardarlo
- ☐ Usa `db.put(currentId++, ticket)`
- ☐ Devuelve el `ticket` con el ID ya asignado

### Método `update(Long id, TicketRequest request)`

- ☐ Llama a `findById(id)` que ahora es O(1)
- ☐ Usa `found.ifPresent(ticket -> {...})` para modificar
- ☐ El `status` solo se actualiza si `request.getStatus()` no es null ni blank
- ☐ **No** necesita `db.put()` al final (el objeto se modifica por referencia)

### Método `delete(Long id)`

- ☐ Usa `db.remove(id) != null`
- ☐ **No** usa `removeIf` (eso era la versión List)
- ☐ Devuelve `true` si se eliminó, `false` si no existía

### Método `existsByTitle(String title)`

- ☐ Usa `db.values().stream().anyMatch(t -> t.getTitle().equalsIgnoreCase(title))`
- ☐ Se acepta que sea O(n) (no hay índice por título)

---

## Checklist del `TicketService`

- ☐ Tiene `getTickets()` sin parámetro que llama a `repository.getAll()`
- ☐ Tiene `getTickets(String status)` que llama a `repository.getAll(status)`
- ☐ El resto de los métodos (`findById`, `create`, `update`, `delete`) no cambian su firma

---

## Checklist del `TicketController`

- ☐ `getAllTickets(@RequestParam(required = false) String status)` — el parámetro `required = false` está presente
- ☐ Llama a `service.getTickets(status)`, no a `service.getTickets()` (sin parámetro)
- ☐ Los demás endpoints no cambian

---

## Checklist de pruebas

- ☐ `GET /tickets` (sin parámetro) → `200 OK` con todos los tickets, ordenados por `createdAt`
- ☐ `GET /tickets?status=NEW` → `200 OK` con solo los tickets en estado `NEW`
- ☐ `GET /tickets?status=new` (minúsculas) → mismo resultado que `?status=NEW`
- ☐ `GET /tickets?status=RESOLVED` cuando no hay tickets resueltos → `200 OK` con `[]`
- ☐ `GET /tickets/by-id/1` → `200 OK` + ticket 1 (findById O(1) funciona)
- ☐ `GET /tickets/by-id/999` → `404 Not Found`
- ☐ `POST /tickets` + `PUT /tickets/by-id/{id}` + `DELETE /tickets/by-id/{id}` siguen funcionando igual
- ☐ `POST /tickets` con título vacío → `400 Bad Request` (validaciones de lección 08 no se rompieron)
- ☐ `POST /tickets` con título duplicado → `409 Conflict` (errores de lección 07 no se rompieron)

---

## Errores comunes a evitar

| Error | Por qué está mal | Cómo corregirlo |
|---|---|---|
| Devolver `db.values()` directamente en `getAll()` | La colección no tiene orden garantizado y no es modificable | Convertir a `new ArrayList<>(db.values())` antes de ordenar |
| Olvidar `required = false` en `@RequestParam` | Spring lanza error si el cliente no manda el parámetro | Agregar `@RequestParam(required = false)` |
| `currentId` empieza en `0L` | IDs que parten de cero son confusos y atípicos | Inicializar en `1L` |
| `db.put(currentId++, ticket)` sin setear el ID al ticket primero | El ticket devuelto tendrá `id = null` | `ticket.setId(currentId); db.put(currentId++, ticket)` |
| `db.get(id)` sin `Optional.ofNullable()` | Si el ID no existe, devuelve `null` — riesgo de NullPointerException | Usar `Optional.ofNullable(db.get(id))` |
| Dejar `List<Ticket>` junto con `Map<Long, Ticket>` | Doble almacenamiento, inconsistencia de datos | Eliminar completamente la List |

