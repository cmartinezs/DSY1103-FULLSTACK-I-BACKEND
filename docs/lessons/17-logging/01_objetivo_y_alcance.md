# Lección 17 - Logging: ¿Qué vas a aprender?

## ¿De dónde venimos?

En Lección 16 implementaste autenticación. Ahora sabes quién accede a tu API.

El siguiente paso: registrar qué hace cada usuario (auditoría).

---

## ¿Qué vas a construir?

Al terminar, tu aplicación registrará:

```
[2026-04-16 14:32:10] INFO  cl.duoc.fullstack.tickets.service.TicketService - Ticket creado: #5 "Software falla"
[2026-04-16 14:33:45] INFO  cl.duoc.fullstack.tickets.service.TicketService - Ticket actualizado: #5, estado: NEW → IN_PROGRESS
[2026-04-16 14:35:22] INFO  cl.duoc.fullstack.tickets.service.TicketService - Ticket eliminado: #5 por admin
[2026-04-16 14:36:01] ERROR cl.duoc.fullstack.tickets.service.TicketService - Fallo al actualizar #999: no encontrado
```

### Niveles en tu aplicación

- **DEBUG:** Entrada/salida de métodos, valores de variables (solo dev)
- **INFO:** Eventos de negocio (create, update, delete, login)
- **WARN:** Situaciones inesperadas (usuario no encontrado, reintentos)
- **ERROR:** Excepciones (violación de validación, error de BD)

---

## ¿Qué NO cubre esta lección?

| Tema | Razón |
|------|-------|
| ELK Stack (Elasticsearch + Kibana) | Herramienta externa, nivel producción |
| Distributed Tracing | Requiere correlacion-id complejo |
| Logs centralizados (Splunk, DataDog) | Servicios pagos |
| Structured logging (JSON) | Nivel avanzado |
| Async logging (performance) | Optimización posterior |

El foco: **SLF4J + Logback básico**.

---

## Requerimientos

| ID | Requerimiento |
|----|---------------|
| **REQ-25** | Loguear creación de ticket (INFO level) |
| **REQ-26** | Loguear actualización de ticket (INFO level) |
| **REQ-27** | Loguear eliminación de ticket (INFO level) |
| **REQ-28** | Loguear errores con stack trace (ERROR level) |
| **REQ-29** | Nivel de log configurable por perfil (DEBUG en dev, INFO en prod) |

---

## Estructura antes vs después

```
Antes:
├── controller/TicketController.java
└── service/TicketService.java        (sin logs)

Después:
├── controller/TicketController.java
└── service/TicketService.java        (+ @Slf4j, logger.info/error)

Nuevos archivos:
├── logback-spring.xml                (configuración de logs)
├── logback-spring-dev.xml            (DEBUG para desarrollo)
└── logback-spring-prod.xml           (INFO para producción)
```
