# Lección 18 - Ejemplos prácticos

```java
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
        String message = e.getBindingResult()
            .getAllErrors().stream()
            .map(err -> err.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("Error de validación: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
            .map(v -> v.getMessage())
            .collect(Collectors.joining(", "));
        log.warn("Violación de restricción: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(message));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e) {
        log.error("Excepción no capturada", e);
        String message = isDevelopment() ? 
            e.getStackTrace().toString() : 
            "Error interno del servidor";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(message));
    }

    private boolean isDevelopment() {
        return System.getProperty("app.env", "prod").equals("dev");
    }
}
```

## Endpoints sin try/catch

```java
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Ticket ticket) {
        Ticket saved = service.create(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        Ticket found = service.getById(id);
        return ResponseEntity.ok(found);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

## Flujos de captura

```
1. POST /tickets (sin "title")
   → @Valid valida
   → MethodArgumentNotValidException
   → GlobalExceptionHandler.handleValidation()
   → Respuesta: 400 + {"message": "El titulo es requerido"}

2. DELETE /tickets/999 (no existe)
   → service.deleteById(999)
   → return null
   → EntityNotFoundException lanzada
   → GlobalExceptionHandler.handleNotFound()
   → Respuesta: 404 + {"message": "..."}

3. POST /tickets con creador == asignado
   → service.create()
   → IllegalArgumentException
   → GlobalExceptionHandler.handleIllegalArgument()
   → Respuesta: 400 + {"message": "El creador y asignado no pueden ser iguales"}
```
