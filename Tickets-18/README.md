# Tickets-18: Lección 18 - Global Exception Handling

## Descripción

Este proyecto implementa la **Lección 18: Global Exception Handling** del curso DSY1103 Fullstack I.

Manejo centralizado de excepciones con @ControllerAdvice.

## Cambios desde Lección 17

### Nuevo archivo: GlobalExceptionHandler
```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
```

### Excepciones manejadas
- `IllegalArgumentException` → 400 Bad Request
- `MethodArgumentNotValidException` → 400 Bad Request
- `EntityNotFoundException` → 404 Not Found
- `BadCredentialsException` → 401 Unauthorized
- `AccessDeniedException` → 403 Forbidden
- `Exception` genérico → 500 Internal Server Error

## Estado

✅ Completado