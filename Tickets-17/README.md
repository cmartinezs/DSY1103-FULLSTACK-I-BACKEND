# Tickets-17: Lección 17 - Logging

## Descripción

Este proyecto implementa la **Lección 17: Logging** del curso DSY1103 Fullstack I.

Agrega logs usando @Slf4j de Lombok.

## Cambios desde Lección 16

### Anotación agregada
```java
@Slf4j
@Service
public class TicketService {
```

### Logging en TicketService
- `log.info("Creando ticket: {}", request.title());`

## Configuración

El logging ya viene preconfigurado con Spring Boot. Los niveles se configuran en `application.yml`:

```yaml
logging:
  level:
    cl.duoc.fullstack.tickets: INFO
```

## Estado

✅ Completado