# Lección 18 - Troubleshooting

## Problema 1: Handler no se ejecuta

**Causa:** `@ControllerAdvice` mal declarado.

**Solución:**
```java
@ControllerAdvice  // ← Falta esta anotación
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(...);
    }
}
```

## Problema 2: Spring Security lanza excepción antes que handler

**Causa:** Orden de ejecución (autenticación antes de lógica).

**Síntoma:** 401/403 sin pasar por handler.

**Solución:** Crear handler separado para excepciones de Security:

```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse("Acceso denegado"));
}
```

## Problema 3: Varios handlers pero solo uno se ejecuta

**Causa:** Order de herencia (mas específico primero).

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> handleValidation(...) { }  // Primero (más específico)

@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleGeneric(...) { }      // Último (genérico)
```

## Problema 4: Stack trace no aparece en dev

**Causa:** Chequeo de environment incorrecto.

**Solución:**
```java
@Value("${app.environment:dev}")
private String environment;

private boolean isDev() {
    return "dev".equals(environment);
}
```

## Problema 5: Handler ignora validaciones @Valid

**Causa:** `@Valid` pero sin handler para `MethodArgumentNotValidException`.

**Solución:**
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> handle(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getAllErrors()
        .stream().map(ObjectError::getDefaultMessage)
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(new ErrorResponse(msg));
}
```

## Problema 6: Mensaje de error muy genérico

**Causa:** `catch (Exception e) { return error("Algo falló"); }`

**Solución:** Log primero, respuesta después:

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handle(Exception e) {
    log.error("Excepción: ", e);  // ← Log con stack
    return ResponseEntity.status(500)
        .body(new ErrorResponse(e.getMessage()));
}
```
