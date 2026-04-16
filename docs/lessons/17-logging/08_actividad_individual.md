# Lección 17 - Actividad individual

## Objetivo

Agregar logging completo a tu API de Tickets usando SLF4J + Logback.

---

## Requisitos

1. **Agregar @Slf4j** a TicketService y TicketController
2. **Loguear eventos principales:**
   - Ticket creado (INFO)
   - Ticket actualizado (INFO)
   - Ticket eliminado (INFO)
   - Error al crear/actualizar/eliminar (ERROR)
3. **Configurar niveles por perfil:**
   - Dev: DEBUG
   - Prod: INFO/WARN
4. **Generar archivo de log** en `logs/tickets.log`

---

## Instrucciones

### Paso 1: Agregar @Slf4j

En TicketService:
```java
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketService {
    // ...
}
```

### Paso 2: Loguear operaciones CRUD

```java
public Ticket create(Ticket ticket) {
    log.info("Creando ticket: '{}'", ticket.getTitle());
    // ... resto del código
    log.info("Ticket creado: ID={}", saved.getId());
    return saved;
}
```

### Paso 3: Configurar YAML

En application.yml:
```yaml
logging:
  level:
    cl.duoc.fullstack.tickets: DEBUG
  file:
    name: logs/tickets.log
```

### Paso 4: Testear

```bash
mvn spring-boot:run
# Crear, actualizar, eliminar un ticket
# Verificar: logs/tickets.log se generó
```

---

## Desafío extra

1. Agregar correlation-id para rastrear operaciones
2. Loguear quién realizó cada acción (desde Spring Security)
3. Crear dashboard con logs en tiempo real
