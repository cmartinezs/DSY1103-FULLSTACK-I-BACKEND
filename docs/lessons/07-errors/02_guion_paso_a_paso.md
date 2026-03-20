# Lección 07 - Tutorial paso a paso: errores con estructura JSON

Sigue esta guía en orden. Vas a agregar una estructura de error consistente a todos los endpoints de tickets.

---

## Paso 1: entender qué está mal ahora

Antes de tocar código, abre Postman o Thunder Client y reproduce los problemas actuales.

### Problema A — el body del error POST no es JSON

Envía un `POST /tickets` con un título que ya existe:

```
POST http://localhost:8080/tickets
Content-Type: application/json

{ "title": "Ticket 1", "description": "ya existe" }
```

Respuesta actual:

```
HTTP/1.1 409 Conflict
Content-Type: text/plain;charset=UTF-8

Ya existe un ticket con el título 'Ticket 1'
```

El `Content-Type` dice `text/plain`. El cliente esperaba JSON pero recibió texto plano.

### Problema B — los 404 no tienen body

```
GET http://localhost:8080/tickets/999
```

Respuesta actual:

```
HTTP/1.1 404 Not Found
(sin body)
```

El cliente no puede saber **qué** no se encontró ni **por qué**.

### El efecto práctico

Si el front-end intenta hacer `response.body.message` después de un error 409, obtiene `undefined` porque el body es texto, no JSON. Tiene que adivinar el formato según el endpoint. Eso es un contrato roto.

---

## Paso 2: entender `record` en Java

Antes de crear `ErrorResponse`, revisa brevemente qué es un `record`.

Un `record` es una clase inmutable de datos que Java genera automáticamente con:
- Constructor con todos los parámetros
- Getter para cada campo (sin el prefijo `get`: se accede como `response.message()`)
- `equals()`, `hashCode()` y `toString()` incluidos

```java
// Esto:
public record ErrorResponse(String message) {}

// Es equivalente a esta clase con Lombok:
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ErrorResponse {
    private final String message;
}
```

Jackson (la librería que convierte objetos a JSON en Spring) puede serializar un `record` exactamente igual que una clase normal. El resultado es `{"message": "..."}`.

> **¿Cuándo usar `record` y cuándo una clase?**
> Usa `record` cuando el objeto solo transporta datos y no tiene comportamiento. `ErrorResponse` es el caso perfecto: solo lleva un mensaje. Si necesitas lógica adicional o mutabilidad, usa una clase. En esta lección usaremos `record`.

---

## Paso 3: crear `ErrorResponse`

Crea el archivo `model/ErrorResponse.java`:

```java
package cl.duoc.fullstack.tickets.model;

public record ErrorResponse(String message) {
}
```

Es todo lo que necesita este archivo. Jackson lo convierte automáticamente a `{"message": "..."}` cuando lo pones como body de una `ResponseEntity`.

---

## Paso 4: actualizar el endpoint `POST /tickets`

Abre `TicketController` y localiza el método `create()`. El cambio es mínimo: reemplaza `body(e.getMessage())` por `body(new ErrorResponse(e.getMessage()))`.

**Antes:**

```java
@PostMapping
public ResponseEntity<Object> create(@RequestBody Ticket ticket) {
    try {
        Ticket saved = service.create(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // ← texto plano
    }
}
```

**Después:**

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) {
    try {
        Ticket saved = service.create(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage())); // ← JSON {"message": "..."}
    }
}
```

El tipo del retorno cambia de `ResponseEntity<Object>` a `ResponseEntity<?>`. El `?` (wildcard) le dice al compilador que este método puede devolver diferentes tipos según el caso: `Ticket` en éxito, `ErrorResponse` en error.

> **¿Por qué `ResponseEntity<?>` y no `ResponseEntity<Object>`?**
> Ambos funcionan, pero `ResponseEntity<?>` comunica mejor la intención: "este método puede devolver distintos tipos, y es intencional". `ResponseEntity<Object>` funciona pero puede dar falsa sensación de que el tipo de retorno está definido.

---

## Paso 5: actualizar los endpoints que devuelven 404 vacío

Los tres endpoints que buscan por ID (`getById`, `update`, `delete`) actualmente devuelven un `404` sin body. Hay que agregarles el cuerpo de error.

### `GET /tickets/{id}` — antes y después

**Antes:**

```java
@GetMapping("/{id}")
public ResponseEntity<Ticket> getById(@PathVariable Long id) {
    return service.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build()); // sin body
}
```

**Después:**

```java
@GetMapping("/{id}")
public ResponseEntity<?> getById(@PathVariable Long id) {
    return service.findById(id)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Ticket con ID " + id + " no encontrado")));
}
```

> **¿Qué hace `.<ResponseEntity<?>>map(ResponseEntity::ok)`?**
> La anotación de tipo `<ResponseEntity<?>>` antes de `.map()` le indica al compilador cuál es el tipo del `Optional` que estamos transformando. Es necesaria porque el `Optional<Ticket>` se transforma en `ResponseEntity<?>` — y sin la anotación el compilador no puede inferir los tipos correctamente cuando el `.orElse()` devuelve un tipo diferente al que produciría `.map(ResponseEntity::ok)` sin ella.

> **¿Por qué incluir el ID en el mensaje?**
> El mensaje `"Ticket con ID 999 no encontrado"` le dice al cliente exactamente qué buscó y que no existía. Un mensaje como `"no encontrado"` es ambiguo — ¿no encontrado dónde? ¿qué? Siempre incluye el valor que buscaste.

### `PUT /tickets/{id}` — antes y después

**Antes:**

```java
@PutMapping("/{id}")
public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket ticket) {
    return service.update(id, ticket)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build()); // sin body
}
```

**Después:**

```java
@PutMapping("/{id}")
public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ticket ticket) {
    return service.update(id, ticket)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Ticket con ID " + id + " no encontrado")));
}
```

### `DELETE /tickets/{id}` — antes y después

**Antes:**

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!service.delete(id)) {
        return ResponseEntity.notFound().build(); // sin body
    }
    return ResponseEntity.noContent().build();
}
```

**Después:**

```java
@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable Long id) {
    if (!service.delete(id)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Ticket con ID " + id + " no encontrado"));
    }
    return ResponseEntity.noContent().build();
}
```

---

## Paso 6: el controlador completo después de esta lección

```java
package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.model.ErrorResponse;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.service.TicketService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(service.getTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.findById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Ticket con ID " + id + " no encontrado")));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Ticket ticket) {
        try {
            Ticket saved = service.create(ticket);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ticket ticket) {
        return service.update(id, ticket)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Ticket con ID " + id + " no encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!service.delete(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Ticket con ID " + id + " no encontrado"));
        }
        return ResponseEntity.noContent().build();
    }
}
```

---

## Paso 7: verificar que todo funciona

Levanta la aplicación y prueba cada caso de error.

### Prueba 1: POST con título duplicado

```
POST http://localhost:8080/tickets
Content-Type: application/json

{ "title": "Ticket 1", "description": "ya existe" }
```

**Resultado esperado:**

```
HTTP/1.1 409 Conflict
Content-Type: application/json

{
  "message": "Ya existe un ticket con el título 'Ticket 1'"
}
```

Observa que ahora el `Content-Type` es `application/json`, no `text/plain`.

### Prueba 2: GET con ID inexistente

```
GET http://localhost:8080/tickets/999
```

**Resultado esperado:**

```
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Ticket con ID 999 no encontrado"
}
```

### Prueba 3: PUT con ID inexistente

```
PUT http://localhost:8080/tickets/999
Content-Type: application/json

{ "title": "X", "description": "Y" }
```

**Resultado esperado:** `404 Not Found` con `{"message": "Ticket con ID 999 no encontrado"}`.

### Prueba 4: DELETE con ID inexistente

```
DELETE http://localhost:8080/tickets/999
```

**Resultado esperado:** `404 Not Found` con `{"message": "Ticket con ID 999 no encontrado"}`.

### Prueba 5: GET exitoso (verificar que el cuerpo correcto no se rompió)

```
GET http://localhost:8080/tickets/1
```

**Resultado esperado:** `200 OK` con el ticket completo en JSON (no `ErrorResponse`).

---

## Paso 8: reflexiona antes de cerrar

Antes de pasar a la actividad, respóndete estas preguntas:

1. ¿Por qué devolver `body(e.getMessage())` produce `Content-Type: text/plain` y `body(new ErrorResponse(e.getMessage()))` produce `Content-Type: application/json`?
2. Si el front-end hace `const data = await response.json()` y recibe texto plano, ¿qué ocurre?
3. ¿Por qué todos los errores deben tener la misma estructura? Piensa en un cliente que consume tu API y maneja errores.
4. ¿Qué ventaja tiene centralizar la estructura de errores en una sola clase vs escribir el objeto de error en cada lugar?

