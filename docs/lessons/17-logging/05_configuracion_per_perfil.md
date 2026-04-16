# Lección 17 - Configuración por perfil

**application.yml** (base):
```yaml
logging:
  pattern:
    console: "%d{HH:mm:ss.SSS} [%-5level] %logger{0} - %msg%n"
  file:
    name: logs/tickets.log
    max-size: 10MB
    max-history: 10
```

**application-dev.yml:**
```yaml
logging:
  level:
    root: DEBUG
    cl.duoc.fullstack.tickets: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
```

**application-prod.yml:**
```yaml
logging:
  level:
    root: WARN
    cl.duoc.fullstack.tickets: INFO
    org.springframework: WARN
  file:
    name: /var/log/tickets/tickets.log
    max-size: 100MB
    max-history: 30
```

## Explicación

- **root: DEBUG** (dev) — todo es verbose
- **root: WARN** (prod) — solo warnings y errores
- **cl.duoc.fullstack.tickets: DEBUG** — package específico es verbose
- **max-size: 100MB** — rotar cuando archivo alcanza 100MB
- **max-history: 30** — guardar últimos 30 días
