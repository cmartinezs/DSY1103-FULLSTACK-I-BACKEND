# Lección 18 - Actividad individual

## Objetivo

Crear un GlobalExceptionHandler que capture todas las excepciones de tu API y devuelva respuestas uniformes.

---

## Requisitos

1. **Crear GlobalExceptionHandler**
   - Anotación `@ControllerAdvice`
   - Mínimo 4 handlers para diferentes excepciones

2. **Handlers requeridos**
   - `IllegalArgumentException` → 400
   - `MethodArgumentNotValidException` → 400
   - `EntityNotFoundException` → 404
   - `Exception` genérico → 500

3. **Simplificar endpoints**
   - Remover try/catch innecesario
   - Dejar que GlobalExceptionHandler maneje errores

4. **Testear múltiples escenarios**
   - POST sin datos requeridos (validación)
   - GET con ID inexistente
   - POST con creador == asignado (negocio)
   - Error genérico (500)

---

## Instrucciones paso a paso

### Paso 1: Crear GlobalExceptionHandler

```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegal(IllegalArgumentException e) {
        log.warn("Validación: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getMessage()));
    }
    // ... resto de handlers
}
```

### Paso 2: Simplificar TicketController

Remueve try/catch y deja que handler los capture.

### Paso 3: Testear

```
POST /tickets (sin title)
→ 400 + {"message": "El titulo es requerido"}

GET /tickets/999
→ 404 + {"message": "Ticket no encontrado"}

POST /tickets (creador == asignado)
→ 400 + {"message": "No pueden ser iguales"}

Excepción inesperada
→ 500 + {"message": "Error interno"}
```

---

## Desafío extra

1. Incluir timestamp en respuesta
2. Stack trace solo en dev
3. Diferentes mensajes para dev vs prod
