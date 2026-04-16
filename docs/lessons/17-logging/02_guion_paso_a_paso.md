# Lección 17 - Tutorial paso a paso: Logging con SLF4J

## Paso 1: Entender SLF4J + Logback

Spring Boot incluye ambos por defecto. No necesitas agregar dependencias.

## Paso 2: Agregar @Slf4j en Service

En `TicketService.java`:

```java
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j  // ← Agrega logger automáticamente
public class TicketService {

    public Ticket create(Ticket ticket) {
        log.info("Creando ticket: '{}'", ticket.getTitle());
        
        boolean exists = this.repository.existsByTitle(ticket.getTitle());
        if (exists) {
            log.warn("Título duplicado: '{}'", ticket.getTitle());
            throw new IllegalArgumentException("Ya existe ticket con este título");
        }

        try {
            Ticket saved = this.repository.save(ticket);
            log.info("Ticket creado exitosamente: ID={}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Error al crear ticket", e);
            throw e;
        }
    }

    public Ticket updateById(Long id, Ticket ticket) {
        log.info("Actualizando ticket: ID={}", id);
        
        Ticket toUpdate = this.repository.getById(id);
        if (toUpdate == null) {
            log.warn("Ticket no encontrado: ID={}", id);
            return null;
        }

        try {
            toUpdate.setTitle(ticket.getTitle());
            toUpdate.setDescription(ticket.getDescription());
            this.repository.update(toUpdate);
            log.info("Ticket actualizado: ID={}", id);
            return toUpdate;
        } catch (Exception e) {
            log.error("Error al actualizar ticket: ID={}", id, e);
            throw e;
        }
    }

    public Ticket deleteById(Long id) {
        log.info("Eliminando ticket: ID={}", id);
        
        try {
            Ticket found = this.repository.deleteById(id);
            if (found != null) {
                log.info("Ticket eliminado exitosamente: ID={}", id);
            } else {
                log.warn("Ticket a eliminar no encontrado: ID={}", id);
            }
            return found;
        } catch (Exception e) {
            log.error("Error al eliminar ticket: ID={}", id, e);
            throw e;
        }
    }
}
```

## Paso 3: Configurar niveles en application.yml

```yaml
logging:
  level:
    root: INFO
    cl.duoc.fullstack.tickets: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%-5level] %logger{0} - %msg%n"
  file:
    name: logs/tickets.log
```

## Paso 4: Configurar por perfil

**application-dev.yml:**
```yaml
logging:
  level:
    root: DEBUG
    cl.duoc.fullstack.tickets: DEBUG
```

**application-prod.yml:**
```yaml
logging:
  level:
    root: INFO
    cl.duoc.fullstack.tickets: INFO
```

## Paso 5: Testear

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Salida esperada:
```
14:32:10.123 [INFO] TicketService - Ticket creado: ID=3
14:32:11.456 [DEBUG] TicketRepository - Guardando en lista: Ticket{id=3}
```
