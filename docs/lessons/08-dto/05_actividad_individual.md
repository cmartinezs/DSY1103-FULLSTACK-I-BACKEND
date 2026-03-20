# Lección 08 - Actividad individual: DTO y validación para categorías

## Contexto

Tu `CategoryController` actualmente recibe `@RequestBody Category` directamente. Esto tiene los mismos problemas que tuvo `TicketController` antes de esta lección: el cliente puede enviar campos que no le corresponden, y no hay validación automática.

---

## ¿Qué vas a construir?

Vas a crear un DTO de entrada para categorías y agregar validación, siguiendo el mismo patrón que usaste con `TicketRequest`.

---

## Paso 1: crear `CategoryRequest`

Crea el archivo `dto/CategoryRequest.java`:

| Campo | Tipo | Validación |
|---|---|---|
| `name` | `String` | `@NotBlank(message = "El nombre no puede estar vacío")` |
| `description` | `String` | Sin validación obligatoria |

```java
package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    private String description;
}
```

---

## Paso 2: actualizar `CategoryService`

Modifica los métodos `create()` y `update()` para que acepten `CategoryRequest` en lugar de `Category`:

```java
public Category create(CategoryRequest request) {
    if (this.repository.existsByName(request.getName())) {
        throw new IllegalArgumentException(
            "Ya existe una categoría con el nombre '" + request.getName() + "'");
    }
    Category category = new Category();
    category.setName(request.getName());
    category.setDescription(request.getDescription());
    return this.repository.save(category);
}

public Optional<Category> update(Long id, CategoryRequest request) {
    return this.repository.update(id, request);
}
```

---

## Paso 3: actualizar `CategoryRepository.update()`

```java
public Optional<Category> update(Long id, CategoryRequest request) {
    Optional<Category> found = findById(id);
    found.ifPresent(category -> {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
    });
    return found;
}
```

---

## Paso 4: actualizar `CategoryController`

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody CategoryRequest request) {
    try {
        Category saved = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }
}

@PutMapping("/{id}")
public ResponseEntity<?> update(@PathVariable Long id,
                                @Valid @RequestBody CategoryRequest request) {
    return service.update(id, request)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Categoría con ID " + id + " no encontrada")));
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(java.util.stream.Collectors.joining(", "));
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
}
```

---

## Pruebas requeridas

| Prueba | Resultado esperado |
|---|---|
| `POST /categories` con `"name": ""` | `400 Bad Request` + `{"message": "name: El nombre no puede estar vacío"}` |
| `POST /categories` con `"name": "   "` | `400 Bad Request` |
| `POST /categories` con nombre válido | `201 Created` con categoría |
| `POST /categories` con nombre duplicado | `409 Conflict` + `{"message": "..."}` |
| `PUT /categories/1` con `"name": ""` | `400 Bad Request` |
| `PUT /categories/999` | `404 Not Found` + `{"message": "..."}` |
| `GET /categories` | `200 OK` (no se rompió) |

---

## Desafío opcional

Agrega validación de longitud mínima en el nombre:

```java
@NotBlank(message = "El nombre no puede estar vacío")
@Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
private String name;
```

Prueba con `"name": "AB"` y verifica que el mensaje de error refleja la nueva validación.

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| `CategoryRequest` en paquete `dto` con `@NotBlank` en `name` | 25% |
| `CategoryService.create()` y `update()` aceptan `CategoryRequest` y construyen `Category` internamente | 25% |
| `@Valid` en los endpoints `POST` y `PUT` del controller | 20% |
| `@ExceptionHandler` en `CategoryController` devuelve `ErrorResponse` con `400` | 20% |
| El cliente no puede fijar campos como `id` desde el body | 10% |

