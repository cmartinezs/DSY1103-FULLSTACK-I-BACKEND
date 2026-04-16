# Caso Extendido: Sistema de Tickets con Gestión de Usuarios

## 📋 Resumen

Sistema de tickets con gestión completa de usuarios, roles, y auditoría. Extiende funcionalidad base del curso DSY1103 (lecciones 1-9) para lecciones 10-18.

---

## 🎯 Modelo de Datos

### Entidades Core

```
User (id, name, email, role, active, createdAt)
Ticket (id, title, description, status, priority, createdBy, assignedTo, category, tags, createdAt, updatedAt)
Category (id, name, description)
Tag (id, name, color)
TicketHistory (id, ticketId, oldStatus, newStatus, changedBy, changedAt)
```

### Relaciones

- **User → Ticket**: createdBy (One-to-Many), assignedTo (One-to-Many)
- **Category → Ticket**: One-to-Many
- **Tag ↔ Ticket**: Many-to-Many
- **Ticket → TicketHistory**: One-to-Many

---

## 📊 Requisitos por Lección

### Lecciones 1-9 (Base)
| Lección | Requisitos |
|---------|------------|
| 01 | Estructura proyecto, Spring Boot |
| 02 | Model/Repository básico |
| 03 | Thymeleaf/Listar tickets |
| 04 | Crear ticket (form) |
| 05 | Validaciones |
| 06 | Actualizar ticket |
| 07 | Eliminar ticket |
| 08 | Detalle ticket |
| 09 | Mejoras UI/styling |

### Lecciones 10-18 (Extendido)
| Lección | Requisitos Adicionales |
|---------|-------------------|
| 10 | JPA + User entity con roles (USER/AGENT/ADMIN), Ticket con User relaciones, seed de usuarios |
| 11 | Perfiles (H2/MySQL/PostgreSQL) |
| 12 | Category (One-to-Many), Tag (Many-to-Many), CRUD completo |
| 13 | TicketHistory, registro automático de cambios |
| 14 | Flyway migrations, Foreign Keys |
| 15 | Microservicios: Feign + RestClient para notificaciones y auditoría |
| 16 | Spring Security con 3 roles (USER/AGENT/ADMIN) |
| 17 | Logging de operaciones |
| 18 | Exception handling |

---

## 👥 Roles y Permisos

| Rol | Permisos |
|-----|---------|
| **USER** | Crear tickets, ver tickets propios, ver estado |
| **AGENT** | Ver tickets asignados, actualizar estado, resolver |
| **ADMIN** | Gestionar usuarios, categorías, tags, asignar tickets, ver historial, auditoría |

---

## 🔄 Flujo de Usuario

```
USER reporta problema → ticket creado → ADMIN revisa → ADMIN asigna a AGENT
                                           → AGENT trabaja → USER verifica → AGENT cierra
```

---

## 📡 Microservicios (Lección 15)

### NotificationService (Puerto 8081)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/notifications | Crear notificación |
| GET | /api/notifications | Listar notificaciones |
| GET | /api/notifications/{id} | Ver notificación |

### AuditService (Puerto 8082)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/audit | Registrar evento |
| GET | /api/audit | Listar eventos |
| GET | /api/audit/ticket/{ticketId} | Ver eventos por ticket |

### Integración en TicketService
- Notificación al crear ticket
- Notificación al asignar agente
- Notificación al cambiar estado
- Registro de auditoría en cada operación

---

## 🔐 Spring Security (Lección 16)

### Endpoints por Rol
| Método | Endpoint | Rol |
|--------|---------|-----|
| GET | /tickets | USER, AGENT, ADMIN |
| POST | /tickets | USER, AGENT, ADMIN |
| PUT | /tickets/{id} | AGENT, ADMIN |
| DELETE | /tickets/{id} | ADMIN |
| GET | /users | ADMIN |
| POST | /users | ADMIN |
| PUT | /tickets/{id}/assign/{userId} | ADMIN |
| GET | /tickets/assigned-to-me | AGENT |

---

## 📝 Excepciones (Lección 18)

- `UserNotFoundException` - Usuario no encontrado
- `TicketNotFoundException` - Ticket no encontrado
- `TicketAssignmentException` - Error al asignar
- `UnauthorizedOperationException` - Operación no autorizada
- `RoleNotAllowedException` - Rol insuficiente

---

## ✅ Checklist

- [x] Modelo de datos con roles
- [x] seed de usuarios (admin/admin123, agent/agent123, user/user123)
- [x] Relaciones User ↔ Ticket
- [x] Category, Tag CRUD
- [x] TicketHistory automático
- [x] Flyway migrations
- [ ] Microservicios (Feign + RestClient)
- [ ] Notificaciones en crear/asignar
- [ ] AuditService
- [ ] Spring Security
- [ ] Logging
- [ ] Exception handling

---

## 🚀 Próximos Pasos

1. Completar NotificationService con GET
2. Crear AuditService
3. Integrar en TicketService
4. Documentar README de cada proyecto

---

**Fecha**: Abril 2026  
**Estado**: En desarrollo