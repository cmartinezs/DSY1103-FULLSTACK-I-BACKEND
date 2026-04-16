# Lección 18 - Global Exception Handling

## El problema

```java
// ❌ Código repetitivo
@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) {
    try {
        Ticket saved = service.create(ticket);
        return ResponseEntity.status(201).body(saved);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("Error interno"));
    }
}
```

Cada endpoint repite el mismo pattern. Con `@ControllerAdvice`, un solo handler captura TODAS las excepciones.

---

## Quick Start

### Concepto

`@ControllerAdvice` = handler global de excepciones

```
Usuario → Request con datos inválidos
    ↓
Spring valida → MethodArgumentNotValidException
    ↓
GlobalExceptionHandler.handle() ← CAPTURA AQUÍ
    ↓
Respuesta: 400 + {"message": "Título no puede estar vacío"}
```

---

## Lo que construirás

1. Crear `GlobalExceptionHandler`
2. Agregar handlers para:
   - `IllegalArgumentException` → 400 Bad Request
   - `EntityNotFoundException` → 404 Not Found
   - `ValidationException` → 400 Bad Request
   - `Exception` genérico → 500 Internal Server Error
3. Diferencia entre dev (stack trace) y prod (solo mensaje)

---

## Lecturas recomendadas

- Lección 07: Manejo de errores local (base)
- Lección 17: Logging (registrar excepciones)
