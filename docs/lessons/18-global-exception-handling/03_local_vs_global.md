# Lección 18 - Try/Catch local vs handler global

## Comparativa

| Aspecto | Try/Catch local | @ControllerAdvice |
|--------|---------|---------|
| **Dónde captura** | Cada endpoint | Una clase centralizada |
| **Repetición de código** | Mucha | Ninguna |
| **Mantenimiento** | Difícil | Fácil |
| **Lógica de error** | Dispersa | Centralizada |
| **Para debugging** | Acceso directo | Log centralizado |

## Ejemplo: Try/Catch local

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) {
    try {
        return ResponseEntity.status(201).body(service.create(ticket));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}

@PutMapping("/{id}")
public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ticket ticket) {
    try {
        Ticket updated = service.updateById(id, ticket);
        return ResponseEntity.ok(updated);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
```

**Problema:** El catch se repite 3 veces.

## Ejemplo: GlobalExceptionHandler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}

@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) {
    return ResponseEntity.status(201).body(service.create(ticket));
}

@PutMapping("/{id}")
public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ticket ticket) {
    Ticket updated = service.updateById(id, ticket);
    return ResponseEntity.ok(updated);
}

@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable Long id) {
    service.deleteById(id);
    return ResponseEntity.noContent().build();
}
```

**Ventaja:** Endpoints limpios, lógica centralizada.

## Cuándo cada uno

- **Try/Catch local:** 
  - Excepción específica del endpoint
  - Lógica de recuperación personalizada
  
- **Global handler:**
  - Excepciones comunes (validation, not found)
  - Respuesta uniforme requerida
  - Muchos endpoints
