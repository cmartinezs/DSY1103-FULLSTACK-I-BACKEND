# Lección 18 - Capturar validaciones de Spring

## @Valid y MethodArgumentNotValidException

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody Ticket ticket) {
    // Si validación falla → MethodArgumentNotValidException
    // GlobalExceptionHandler la captura
    return ResponseEntity.status(201).body(service.create(ticket));
}
```

## Handler para MethodArgumentNotValidException

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e) {
    
    String message = e.getBindingResult()
        .getAllErrors().stream()
        .map(ObjectError::getDefaultMessage)
        .collect(Collectors.joining(", "));
    
    log.warn("Error de validación: {}", message);
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(message));
}
```

## Handler para ConstraintViolationException

```java
@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<?> handleConstraintViolation(
        ConstraintViolationException e) {
    
    String message = e.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(", "));
    
    log.warn("Violación de restricción: {}", message);
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(message));
}
```

## Ejemplos de validaciones capturadas

```
1. POST /tickets sin "title"
   @NotBlank(message = "El titulo es requerido")
   → Capturado: "El titulo es requerido"

2. POST /tickets con title.length() > 50
   @Size(max = 50)
   → Capturado: "Debe tener máximo 50 caracteres"

3. PUT /tickets/999 (no existe)
   → Capturado: "Ticket con ID 999 no encontrado"
```
