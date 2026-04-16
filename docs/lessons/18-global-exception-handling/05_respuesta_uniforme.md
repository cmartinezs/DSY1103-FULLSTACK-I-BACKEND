# Lección 18 - Respuesta uniforme de errores

## Estructura base

```java
public record ErrorResponse(String message) {}
```

## En dev: incluir stack trace

```java
public record ErrorResponse(String message, String details, String timestamp) {}

// Handler
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleGeneric(Exception e) {
    String details = isDevelopment() ? 
        Arrays.toString(e.getStackTrace()) : 
        null;
    
    ErrorResponse response = new ErrorResponse(
        e.getMessage(),
        details,
        LocalDateTime.now().toString()
    );
    
    return ResponseEntity.status(500).body(response);
}
```

## En prod: solo mensaje

```json
{
  "message": "Error interno del servidor",
  "timestamp": "2026-04-16T14:32:10"
}
```

## En dev: con stack trace

```json
{
  "message": "NullPointerException: valor es null",
  "details": "[cl.duoc.fullstack.tickets.service.TicketService.create(...), ...]",
  "timestamp": "2026-04-16T14:32:10"
}
```

## Controlar con propiedad

```yaml
# application.yml
app.environment: ${APP_ENV:dev}

# application-prod.yml
app.environment: prod
```

```java
@Value("${app.environment}")
private String environment;

private boolean isDevelopment() {
    return "dev".equals(environment);
}
```
