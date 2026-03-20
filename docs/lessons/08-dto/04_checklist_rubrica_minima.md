# Lección 08 - Checklist y rúbrica mínima

Usa esta lista para verificar que implementaste correctamente el DTO y las validaciones antes de dar la lección por terminada.

---

## Checklist de dependencia

- ☐ `pom.xml` incluye `spring-boot-starter-validation` como dependencia
- ☐ La aplicación compila y levanta correctamente después de agregar la dependencia

---

## Checklist del DTO `TicketRequest`

- ☐ Existe el archivo `dto/TicketRequest.java`
- ☐ Está en el paquete `cl.duoc.fullstack.tickets.dto`
- ☐ Tiene `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor` de Lombok
- ☐ El campo `title` tiene `@NotBlank(message = "El título no puede estar vacío")`
- ☐ **No tiene** los campos `id`, `createdAt`, `estimatedResolutionDate` (esos los asigna el servidor)
- ☐ Tiene `description` (String, sin validación obligatoria)
- ☐ Tiene `status` (String, opcional, para el caso del PUT)

---

## Checklist del Service

- ☐ `create(TicketRequest request)` recibe el DTO, **no** `Ticket` directamente
- ☐ Dentro de `create()` se construye un `new Ticket()` y se asignan sus campos manualmente
- ☐ Los campos `status`, `createdAt`, `estimatedResolutionDate` son asignados por el Service, **no** tomados del request
- ☐ `update(Long id, TicketRequest request)` también recibe el DTO
- ☐ No hay `return null` en ningún método del Service

---

## Checklist del Repository

- ☐ `update(Long id, TicketRequest request)` acepta el DTO
- ☐ El `status` del update solo se aplica si `request.getStatus()` no es `null` ni blank

---

## Checklist del Controller

### Endpoints con `@Valid`

- ☐ `POST /tickets` usa `@Valid @RequestBody TicketRequest request`
- ☐ `PUT /tickets/{id}` usa `@Valid @RequestBody TicketRequest request`
- ☐ Los endpoints `GET` y `DELETE` **no** necesitan `@Valid` (no tienen body de entrada)

### `@ExceptionHandler`

- ☐ Existe un método con `@ExceptionHandler(MethodArgumentNotValidException.class)` en el controlador
- ☐ Extrae el mensaje de los `FieldErrors` con `.getBindingResult().getFieldErrors()`
- ☐ Devuelve `ResponseEntity.badRequest().body(new ErrorResponse(message))` → `400 Bad Request`
- ☐ El formato del mensaje incluye el campo y su error: `"title: El título no puede estar vacío"`

---

## Checklist de pruebas

- ☐ `POST /tickets` con `"title": ""` → `400 Bad Request` + `{"message": "title: El título no puede estar vacío"}`
- ☐ `POST /tickets` con `"title": "   "` → `400 Bad Request` (blanco)
- ☐ `POST /tickets` sin campo `title` → `400 Bad Request`
- ☐ `PUT /tickets/1` con `"title": ""` → `400 Bad Request`
- ☐ `POST /tickets` con `"id": 99, "status": "RESOLVED", "title": "Test"` → `201 Created` con status `NEW` (el id y status del cliente son ignorados)
- ☐ `POST /tickets` válido → `201 Created` con ticket completo (flujo feliz no se rompió)
- ☐ `GET /tickets`, `GET /tickets/1`, `DELETE /tickets/1` siguen funcionando correctamente (no se rompió nada)

---

## Errores comunes a evitar

| Error | Por qué está mal | Cómo corregirlo |
|---|---|---|
| Olvidar `@Valid` en el parámetro del controller | Las anotaciones del DTO nunca se evalúan | Agregar `@Valid` antes de `@RequestBody TicketRequest` |
| Olvidar la dependencia `spring-boot-starter-validation` | `@NotBlank` compila pero no hace nada | Agregar la dependencia al `pom.xml` |
| Poner `@NotBlank` en el modelo `Ticket` | Aplica en todos los contextos, no solo en la entrada de la API | Poner las validaciones en el DTO |
| No agregar `@ExceptionHandler` | Spring devuelve su propio error en un formato diferente al `ErrorResponse` | Agregar el handler en el controller |
| Usar `ticket.setStatus(request.getStatus())` en `create()` | El cliente puede establecer el status inicial del ticket | El Service siempre asigna `"NEW"` independientemente del request |
| No tener `@NoArgsConstructor` en el DTO | Jackson no puede deserializar el JSON | Agregar `@NoArgsConstructor` con Lombok |

