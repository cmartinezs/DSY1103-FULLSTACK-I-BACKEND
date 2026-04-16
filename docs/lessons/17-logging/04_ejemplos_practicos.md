# Lección 17 - Ejemplos prácticos

## Loguear en Controller

```java
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tickets")
@Slf4j
public class TicketController {

    @GetMapping
    public List<Ticket> getAllTickets() {
        log.debug("GET /tickets solicitado");
        return this.service.getTickets();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Ticket ticket) {
        log.info("POST /tickets - creando: {}", ticket.getTitle());
        try {
            Ticket created = this.service.create(ticket);
            log.info("Ticket creado exitosamente: ID={}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
        } catch (IllegalArgumentException e) {
            log.warn("Validación fallida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/by-id/{id}")
    public ResponseEntity<?> deleteTicketById(@PathVariable Long id) {
        log.info("DELETE /tickets/{} solicitado", id);
        try {
            Ticket found = this.service.deleteById(id);
            if (found != null) {
                log.info("Ticket eliminado: ID={}", id);
                return ResponseEntity.ok(found);
            }
            log.warn("Ticket a eliminar no encontrado: ID={}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al eliminar ticket: ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

## Loguear excepciones

```java
// Captura y loguea con stack trace
try {
    service.delete(id);
} catch (IllegalArgumentException e) {
    log.error("Error de validación en delete: ID={}", id, e);  // ← e incluye stack trace
    throw e;
}

// Salida:
// 14:32:10 [ERROR] TicketService - Error de validación en delete: ID=999
// java.lang.IllegalArgumentException: Ticket no encontrado
//   at cl.duoc.fullstack.tickets.service.TicketService.deleteById(...)
//   ...
```

## Patrones útiles

```java
// Con más contexto
log.info("Usuario {} intenta eliminar ticket {}", username, id);

// Con condición (evita concatenación si no se loguea)
if (log.isDebugEnabled()) {
    log.debug("Variable compleja: {}", complexObject.toString());
}

// Con nivel apropiado
log.debug("Valor variable x={}", x);      // Dev only
log.info("Operación exitosa");            // Todos los ambientes
log.warn("Reintentando conexión BD");     // Situación inesperada
log.error("Excepción no capturada", e);   // Error crítico
```
