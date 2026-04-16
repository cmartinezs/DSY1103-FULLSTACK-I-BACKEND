# Lección 18 - Tutorial paso a paso

## Paso 1: Crear GlobalExceptionHandler

```java
package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Validación fallida: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validación fallida");
        log.warn("Validación de argumentos: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        log.warn("Credenciales inválidas: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("Credenciales inválidas"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("Acceso denegado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("Acceso denegado"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e) {
        log.error("Excepción no capturada", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Error interno del servidor"));
    }
}
```

## Paso 2: Simplificar endpoints

Antes (con try/catch):
```java
@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) {
    try {
        Ticket saved = service.create(ticket);
        return ResponseEntity.status(201).body(saved);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
```

Después (GlobalExceptionHandler maneja la excepción):
```java
@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) {
    Ticket saved = service.create(ticket);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    // Si IllegalArgumentException ocurre → GlobalExceptionHandler la captura
}
```

## Paso 3: Testear

```
POST /tickets con datos inválidos
```

Handler captura → Respuesta: 400 + `{"message": "..."}`

Sin handler, sería: 500 + stack trace

## Paso 4: Stack trace en dev

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleGeneric(Exception e) {
    log.error("Excepción no capturada", e);
    
    String message = (isDev() ? e.getStackTrace().toString() : "Error interno");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(message));
}

private boolean isDev() {
    return System.getProperty("app.env", "prod").equals("dev");
}
```
