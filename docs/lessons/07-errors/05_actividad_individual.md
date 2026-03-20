# Lección 07 - Actividad individual: errores estructurados en categorías

## Contexto

En las lecciones anteriores implementaste el CRUD de `Category`. En este momento, sus errores siguen el mismo patrón deficiente que tenía `Ticket` antes de esta lección: cuerpos de texto plano o respuestas 404 vacías.

Esta actividad es aplicar exactamente lo que aprendiste hoy, pero sobre el recurso que tú construiste.

---

## ¿Qué vas a construir?

Vas a actualizar `CategoryController` y `CategoryService` para que todos los errores devuelvan una estructura JSON consistente:

```json
{
  "message": "Categoría con ID 999 no encontrada"
}
```

---

## Requerimientos

### 1. Reutiliza `ErrorResponse`

**No** crees una segunda clase de error. La clase `ErrorResponse` que creaste hoy existe en el paquete `model` y está disponible para todos los controladores. Impórtala y úsala directamente en `CategoryController`.

### 2. Actualiza `CategoryService`

Verifica que `create()` lanza `IllegalArgumentException` con un mensaje descriptivo cuando el nombre ya existe:

```java
throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + request.getName() + "'");
```

Si ya lo hiciste en la lección 06, no hay nada que cambiar aquí.

### 3. Actualiza `CategoryController`

Aplica exactamente el mismo patrón que `TicketController`:

| Endpoint | Código exitoso | Error con body |
|---|---|---|
| `GET /categories` | 200 + lista | — |
| `GET /categories/{id}` | 200 + categoría | 404 + `{"message": "Categoría con ID X no encontrada"}` |
| `POST /categories` | 201 + categoría | 409 + `{"message": "Ya existe una categoría con el nombre '...'"}` |
| `PUT /categories/{id}` | 200 + categoría | 404 + `{"message": "Categoría con ID X no encontrada"}` |
| `DELETE /categories/{id}` | 204 sin body | 404 + `{"message": "Categoría con ID X no encontrada"}` |

---

## Guía de implementación

### `CategoryController.java`

```java
// Todos los métodos con posible error devuelven ResponseEntity<?>

@GetMapping("/{id}")
public ResponseEntity<?> getById(@PathVariable Long id) {
    return service.findById(id)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Categoría con ID " + id + " no encontrada")));
}

@PostMapping
public ResponseEntity<?> create(@RequestBody Category category) {
    try {
        Category saved = service.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

Replica el mismo patrón para `PUT` y `DELETE`.

---

## Ejemplos de prueba

### Crear categoría duplicada

```
POST http://localhost:8080/categories
Content-Type: application/json

{ "name": "Hardware", "description": "Ya existe" }
```

Resultado esperado:

```json
{
  "message": "Ya existe una categoría con el nombre 'Hardware'"
}
```

Código: `409 Conflict`, `Content-Type: application/json`.

### Buscar categoría inexistente

```
GET http://localhost:8080/categories/999
```

Resultado esperado:

```json
{
  "message": "Categoría con ID 999 no encontrada"
}
```

Código: `404 Not Found`.

---

## Extensión opcional

Si terminaste antes, agrega mensajes de error más detallados al `DELETE`:

- Si la categoría tiene tickets asociados, que el servidor responda `409 Conflict` con el mensaje: `"No se puede eliminar la categoría 'Hardware' porque tiene tickets asociados"`

Por ahora no hay una relación real entre categorías y tickets, así que puedes simularlo con un contador fijo: si el ID de la categoría a eliminar es `1`, asume que tiene tickets asociados.

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| Todos los errores de `CategoryController` devuelven `Content-Type: application/json` | 30% |
| Los códigos HTTP son correctos (404 para no encontrado, 409 para conflicto) | 25% |
| Se reutiliza `ErrorResponse` del paquete `model` sin duplicarla | 25% |
| El mensaje incluye el valor que causó el error (ID o nombre) | 20% |

