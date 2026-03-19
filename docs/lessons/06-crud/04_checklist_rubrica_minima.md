# Lección 06 - Checklist y rúbrica mínima

Usa esta lista para verificar que implementaste correctamente el CRUD completo de tickets antes de dar la lección por terminada.

---

## Checklist de endpoints

| Endpoint             | Método | ¿Implementado? | Código exitoso | Código de error |
|----------------------|--------|----------------|----------------|-----------------|
| `/tickets`           | GET    | ☐              | 200 OK         | —               |
| `/tickets/{id}`      | GET    | ☐              | 200 OK         | 404 Not Found   |
| `/tickets`           | POST   | ☐              | 201 Created    | 409 Conflict    |
| `/tickets/{id}`      | PUT    | ☐              | 200 OK         | 404 Not Found   |
| `/tickets/{id}`      | DELETE | ☐              | 204 No Content | 404 Not Found   |

---

## Checklist de código

### Model (`Ticket.java`)

- ☐ Tiene `@NoArgsConstructor` para que Jackson pueda deserializar el body del POST y PUT
- ☐ Tiene los campos: `id`, `title`, `description`, `status`, `createdAt`, `estimatedResolutionDate`, `effectiveResolutionDate`

### Repository (`TicketRepository.java`)

- ☐ Tiene `findById(Long id)` que devuelve `Optional<Ticket>` (usa stream + `findFirst()`, no retorna `null`)
- ☐ Tiene `update(Long id, Ticket updatedTicket)` que devuelve `Optional<Ticket>` (reutiliza `findById()` + `ifPresent()`)
- ☐ Tiene `delete(Long id)` que devuelve `boolean` (usa `removeIf()`)
- ☐ El contador de IDs (`currentId`) empieza en `3L` para no colisionar con los tickets semilla

### Service (`TicketService.java`)

- ☐ Tiene `findById(Long id)` que devuelve `Optional<Ticket>` y propaga el Optional del Repository hacia arriba
- ☐ Tiene `update(Long id, Ticket updatedTicket)` que devuelve `Optional<Ticket>`
- ☐ Tiene `delete(Long id)` que devuelve `boolean`
- ☐ Ningún método llama a `.get()` ni a `.orElse(null)` en el Optional (eso se hace en el Controller)
- ☐ El método `create()` sigue asignando `status`, `createdAt`, `estimatedResolutionDate` y `effectiveResolutionDate` en el servidor

### Controller (`TicketController.java`)

- ☐ `GET /tickets/{id}` usa `@GetMapping("/{id}")` y `@PathVariable Long id`
- ☐ `PUT /tickets/{id}` usa `@PutMapping("/{id}")` con `@PathVariable` y `@RequestBody`
- ☐ `DELETE /tickets/{id}` usa `@DeleteMapping("/{id}")` y devuelve `ResponseEntity<Void>`
- ☐ Todos los endpoints usan `ResponseEntity` con el código correcto
- ☐ Los endpoints de GET y PUT usan `.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())` (sin `if (x == null)`)
- ☐ El DELETE exitoso devuelve `ResponseEntity.noContent().build()`
- ☐ No hay ningún `null` explícito en el controlador

---

## Checklist de reglas REST

- ☐ La URL del recurso es `/tickets` (plural, sin verbos)
- ☐ No hay rutas con verbos como `/getTicket`, `/deleteTicket`, `/updateTicket`
- ☐ El ID del recurso en PUT viene de la URL (`@PathVariable`), no del body
- ☐ Los códigos de estado son correctos para cada situación

---

## Checklist de pruebas

Hiciste las siguientes pruebas en Postman/Thunder Client:

- ☐ `GET /tickets` devuelve los 2 tickets semilla → `200 OK`
- ☐ `GET /tickets/1` devuelve el primer ticket → `200 OK`
- ☐ `GET /tickets/999` → `404 Not Found`
- ☐ `POST /tickets` con título nuevo → `201 Created` (el servidor asignó ID, status y fechas)
- ☐ `POST /tickets` con el mismo título → `409 Conflict`
- ☐ `PUT /tickets/1` con datos nuevos → `200 OK` (fechas no cambiaron)
- ☐ `PUT /tickets/999` → `404 Not Found`
- ☐ `DELETE /tickets/2` → `204 No Content`
- ☐ `DELETE /tickets/999` → `404 Not Found`
- ☐ `GET /tickets` después del DELETE → el ticket eliminado ya no aparece

---

## Errores comunes a evitar

| Error | Por qué está mal | Cómo corregirlo |
|---|---|---|
| Devolver `null` desde Repository o Service | Puede causar `NullPointerException` si el llamador olvida verificar | Usa `Optional.empty()` o `Optional.of(valor)` |
| Llamar a `optional.get()` sin verificar | Si el Optional está vacío, lanza `NoSuchElementException` | Usa `.map()`, `.orElse()` o `.ifPresent()` |
| Usar el `id` del body en el PUT | El ID autoritativo es el de la URL | Usa solo `@PathVariable Long id` para buscar el ticket |
| Devolver `200` en el DELETE | Una eliminación exitosa no devuelve contenido | Usa `ResponseEntity.noContent().build()` (`204`) |
| Devolver `200` cuando no existe el recurso | `200` indica éxito; un recurso inexistente es un error | Usa `ResponseEntity.notFound().build()` (`404`) |
| Poner verbos en la URL | El verbo HTTP ya indica la acción | Usa `DELETE /tickets/1` en vez de `GET /deleteTicket/1` |
| No probar casos negativos | La API debe responder correctamente a errores también | Prueba siempre con IDs que no existen |

