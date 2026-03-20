# Lección 07 - Checklist y rúbrica mínima

Usa esta lista para verificar que implementaste correctamente la estructura de error antes de dar la lección por terminada.

---

## Checklist de la clase `ErrorResponse`

- ☐ Existe el archivo `model/ErrorResponse.java`
- ☐ Está declarada como `record`: `public record ErrorResponse(String message) {}`
- ☐ No tiene dependencias externas ni anotaciones adicionales
- ☐ Está en el paquete `cl.duoc.fullstack.tickets.model`

---

## Checklist de endpoints

| Endpoint | Código exitoso | Error + body `{"message":"..."}` |
|---|---|---|
| `GET /tickets` | 200 + lista | — |
| `GET /tickets/{id}` existente | 200 + ticket | — |
| `GET /tickets/{id}` inexistente | — | ☐ 404 + `{"message": "Ticket con ID X no encontrado"}` |
| `POST /tickets` título nuevo | 201 + ticket | — |
| `POST /tickets` título duplicado | — | ☐ 409 + `{"message": "Ya existe un ticket..."}` |
| `PUT /tickets/{id}` existente | 200 + ticket | — |
| `PUT /tickets/{id}` inexistente | — | ☐ 404 + `{"message": "Ticket con ID X no encontrado"}` |
| `DELETE /tickets/{id}` existente | 204 sin body | — |
| `DELETE /tickets/{id}` inexistente | — | ☐ 404 + `{"message": "Ticket con ID X no encontrado"}` |

---

## Checklist de código

### `TicketController.java`

- ☐ Todos los métodos de error devuelven `ResponseEntity<?>` (no `ResponseEntity<Object>`, no `ResponseEntity<Void>` en los que pueden fallar)
- ☐ El método `create()` usa `body(new ErrorResponse(e.getMessage()))`, **no** `body(e.getMessage())`
- ☐ Los métodos `getById()`, `update()` y `delete()` usan `body(new ErrorResponse("..."))` en el caso 404, **no** `.notFound().build()`
- ☐ El tipo en `.map()` está anotado: `.<ResponseEntity<?>>map(ResponseEntity::ok)` en `getById()` y `update()`
- ☐ No hay `null` explícito en el controlador

### `ErrorResponse.java`

- ☐ Jackson puede serializarla: al incluirla en el body de una `ResponseEntity`, Spring devuelve `Content-Type: application/json`
- ☐ El campo se llama `message` (sin prefijo `get`, es un record)

### `TicketService.java`

- ☐ El método `create()` lanza `IllegalArgumentException` con un mensaje descriptivo cuando el título ya existe
- ☐ No hay `return null` en ningún método del service

---

## Checklist de pruebas

Hiciste las siguientes pruebas en Postman / Thunder Client:

- ☐ `POST /tickets` con título existente → `409 Conflict` + `Content-Type: application/json` + `{"message": "..."}`
- ☐ `GET /tickets/999` → `404 Not Found` + `Content-Type: application/json` + `{"message": "Ticket con ID 999 no encontrado"}`
- ☐ `PUT /tickets/999` → `404 Not Found` + body JSON
- ☐ `DELETE /tickets/999` → `404 Not Found` + body JSON
- ☐ `GET /tickets/1` → `200 OK` + ticket completo (el body exitoso no se rompió)
- ☐ `POST /tickets` con título nuevo → `201 Created` + ticket completo (el flujo feliz no se rompió)

---

## Errores comunes a evitar

| Error | Por qué está mal | Cómo corregirlo |
|---|---|---|
| `body(e.getMessage())` | Devuelve `text/plain`, no JSON | Usar `body(new ErrorResponse(e.getMessage()))` |
| `ResponseEntity.notFound().build()` en error | Sin cuerpo; el cliente no sabe qué falló | Usar `.status(NOT_FOUND).body(new ErrorResponse("..."))` |
| `ResponseEntity<Object>` cuando el tipo varía | Funciona, pero oculta la intención | Usar `ResponseEntity<?>` para tipos mixtos |
| Olvidar `.<ResponseEntity<?>>map(...)` | Error de compilación por tipos incompatibles | Anotar el tipo antes del `.map()` |
| Mensaje de error genérico ("Error") | El cliente no puede actuar sobre él | Incluir el valor que causó el error en el mensaje |

