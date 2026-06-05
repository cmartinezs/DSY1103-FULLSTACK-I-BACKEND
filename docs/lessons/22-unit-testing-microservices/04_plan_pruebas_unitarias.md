# Plan de Pruebas Unitarias

## ¿Qué es?

Un plan de pruebas unitarias es un documento breve que define qué se va a probar, por qué y cómo.

No debe ser largo. Debe ayudar al equipo a revisar cobertura de reglas importantes.

---

## Formato recomendado para README

```md
## Plan de Pruebas Unitarias

### Objetivo
Validar reglas de negocio principales de Tickets y comportamiento frente a fallas de microservicios.

### Alcance
- TicketService
- NotificationClient fallback
- AuditServiceClient fallback
- Validaciones de estado

### Fuera de alcance
- Base de datos real
- Servicios externos reales
- Pruebas end-to-end

### Casos
| ID | Clase | Escenario | Resultado esperado |
|----|-------|-----------|-------------------|
| UT-01 | TicketService | Crear ticket valido | Guarda y retorna ticket |
| UT-02 | TicketService | Buscar id inexistente | Lanza EntityNotFoundException |
| UT-03 | TicketService | Crear ticket con NotificationService caido | No rompe la creacion |
| UT-04 | AuditFallback | AuditService caido | Retorna respuesta segura |

### Comando
`mvnw.cmd test`
```

---

## Casos mínimos para Tickets

| ID | Unidad | Given | When | Then |
|----|--------|-------|------|------|
| UT-01 | `TicketService` | Ticket válido | `create()` | Guarda ticket |
| UT-02 | `TicketService` | ID existente | `findById()` | Retorna ticket |
| UT-03 | `TicketService` | ID inexistente | `findById()` | Lanza 404 o excepción esperada |
| UT-04 | `TicketService` | Datos inválidos | `create()` | Lanza validación |
| UT-05 | `NotificationClient` | Servicio externo falla | `send()` | Maneja error según regla |
| UT-06 | `AuditFallback` | AuditService no responde | fallback | Retorna valor seguro |
| UT-07 | Mapper DTO | Entidad completa | mapear a response | No expone campos internos |

---

## Criterios de aceptación

- Todas las pruebas unitarias pasan
- No dependen de base de datos real
- No dependen de puertos 8081, 8082, 8084 o 8085
- Cada prueba usa Given When Then
- Los nombres de pruebas explican escenario y resultado

