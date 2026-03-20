# Lección 07 - El debate: manejo local vs. manejo global de errores

## El problema que aparece cuando creces

Después de esta lección tu controlador maneja errores así:

```java
// En create():
} catch (IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(e.getMessage()));
}

// En getById():
.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
    .body(new ErrorResponse("Ticket con ID " + id + " no encontrado")));
```

Ahora imagina que tu API crece. Tienes `TicketController`, `CategoryController`, `UserController`, `ProjectController`. Cada uno tiene los mismos bloques try/catch y `.orElse(...)` con `new ErrorResponse(...)`.

Si decides cambiar la estructura del error — agregar un campo `timestamp`, cambiar el nombre de `message` a `error`, o agregar un código numérico — tendrías que editar **cada uno** de esos bloques en **cada controlador**.

Eso viola el principio DRY (*Don't Repeat Yourself*).

---

## La solución: `@ControllerAdvice`

Spring ofrece `@ControllerAdvice`, una anotación que marca una clase como **manejador global de excepciones**. Cualquier excepción que no sea capturada en el controlador sube hacia esta clase, que decide qué respuesta devolver.

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

Con este enfoque, los controladores **no tienen try/catch**. Si el service lanza una `IllegalArgumentException`, esta clase la intercepta automáticamente.

---

## Comparación directa

| Criterio | Manejo local (try/catch) | Manejo global (`@ControllerAdvice`) |
|---|---|---|
| **Dónde vive el código de error** | En cada método del controlador | En una sola clase centralizada |
| **DRY** | ❌ Repite lógica en cada método | ✅ Un solo lugar |
| **Claridad del controlador** | El flujo feliz y los errores están mezclados | Solo el flujo feliz; errores en otro lado |
| **Cambiar la estructura del error** | Hay que tocar cada método | Solo hay que tocar el `@ControllerAdvice` |
| **Facilidad de entendimiento inicial** | ✅ Fácil de razonar paso a paso | Requiere conocer el ciclo de vida de Spring MVC |
| **Errores específicos por endpoint** | ✅ Fácil de personalizar por caso | Posible, pero más elaborado |

---

## ¿Por qué no lo implementamos ya?

Tres razones pedagógicas:

1. **El try/catch local hace visible el flujo.** Cuando ves `try { ... } catch (IllegalArgumentException e)` en el método, entiendes exactamente qué puede salir mal y qué responde el servidor. `@ControllerAdvice` esconde ese enlace.

2. **`@ControllerAdvice` requiere conocer el ciclo de vida de Spring MVC.** Cuando una excepción no es capturada localmente, sube por el stack y Spring la intercepta. Entender eso bien — incluyendo cuándo aplica, cuándo no, y cómo interactúa con `@ResponseStatus` — merece su propio espacio.

3. **El problema de DRY no duele con un solo controlador.** Cuando tengas tres o cuatro controladores con la misma lógica de error, el dolor será concreto y la solución será obvia. Aprender la solución antes de sentir el problema dificulta recordarla.

---

## El `@ExceptionHandler` local: un punto medio

Hay una opción intermedia que usarás en la lección 08: `@ExceptionHandler` dentro del propio controlador. No es global como `@ControllerAdvice`, pero tampoco repite el mismo bloque en cada método.

```java
@RestController
@RequestMapping("/tickets")
public class TicketController {

    // ... endpoints ...

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }
}
```

Este `@ExceptionHandler` aplica **solo** a las excepciones que lanzan los métodos de ese controlador. Es útil para validaciones que son específicas de un recurso y no necesitan ser globales.

---

## El mapa de evolución

```
Lección 07: try/catch por método + orElse con body
                ↓
Lección 08: @ExceptionHandler local (por validación)
                ↓
Futuro:     @ControllerAdvice global (cuando escale)
```

Cada paso tiene su momento correcto. Lo importante ahora es que entiendas **por qué** existe cada nivel, no solo cómo implementarlo.

