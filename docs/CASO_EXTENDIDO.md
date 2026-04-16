# Caso Extendido: Sistema de Tickets - DSY1103 Fullstack I

> Documento base del proyecto para lecciones 04-18
> 参考: `docs/lessons/00_enunciado_proyecto.md`

---

## 📋 Resumen Ejecutivo

Sistema de soporte técnico para gestión de tickets con arquitectura distribuida. El proyecto evoluciona desde un CRUD básico (lecciones 01-09) hacia un sistema distribuido con microservicios (lecciones 10-18).

---

## 🎯 Scope 1: Lecciones 01-09 (CRUD Básico)

### Modelo de Datos (Solo Ticket)

```java
@Entity
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;          // NEW, IN_PROGRESS, RESOLVED, CLOSED
    private LocalDateTime createdAt;
    private LocalDate estimatedResolutionDate;
    private LocalDateTime effectiveResolutionDate;
}
```

### Endpoints CRUD

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /tickets | Listar todos (REQ-01) |
| GET | /tickets/{id} | Ver ticket por ID (REQ-07) |
| POST | /tickets | Crear ticket (REQ-02) |
| PUT | /tickets/{id} | Actualizar ticket (REQ-08) |
| DELETE | /tickets/{id} | Eliminar ticket (REQ-09) |

### Requerimientos Implementados

| ID | Requerimiento | Lección |
|----|-------------|---------|
| REQ-01 | Consultar todos los tickets | 04 |
| REQ-02 | Registrar nuevo ticket | 05 |
| REQ-03 | Estado inicial `NEW` | 05 |
| REQ-04 | Sin títulos duplicados | 05 |
| REQ-05 | Fecha de creación automática | 05 |
| REQ-06 | Fecha estimada de resolución (5 días) | 05 |
| REQ-07 | Consultar ticket por ID | 06 |
| REQ-08 | Actualizar ticket | 06 |
| REQ-09 | Eliminar ticket | 07 |
| REQ-10 | Error 404 si no existe | 06 |
| REQ-11 | Error JSON con message | 07 |
| REQ-12 | Creador ≠ Asignado | 07 |
| REQ-13 | Título no vacío | 08 |
| REQ-14 | DTO separado del modelo | 08 |
| REQ-15 | Filtro ?status= | 09 |
| REQ-16 | Persistencia BD real | 09 |

### Lo que NO está en scope 01-09

| Funcionalidad | Razón |
|--------------|-------|
| Autenticación/Spring Security | Requiere conocimientos adicionales |
| Notificaciones | Fuera de API REST básica |
| Manejo global de errores | Se implementa formalmente después |
| Paginación | Requiere JPA avanzado |
| Flyway/Liquibase | Herramientas producción |
| Auditoría completa | Requiere autenticación |

---

## 🎯 Scope 2: Lecciones 10-18 (Sistema Distribuido)

### Modelo de Datos Extendido

```java
@Entity
public class User {
    private Long id;
    private String name;
    private String email;
    private Role role;           // USER, AGENT, ADMIN
    private boolean active;
    private LocalDateTime createdAt;
}

@Entity
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDate estimatedResolutionDate;
    private LocalDateTime effectiveResolutionDate;
    private User createdBy;      // Usuario que creó
    private User assignedTo;       // Usuario asignado
    private Category category;    // Categoría
    private Set<Tag> tags;       // Etiquetas
}

@Entity
public class Category {
    private Long id;
    private String name;
    private String description;
}

@Entity
public class Tag {
    private Long id;
    private String name;
    private String color;
}

@Entity
public class TicketHistory {
    private Long id;
    private Long ticketId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime changedAt;
}
```

### Roles y Permisos

| Rol | Permisos |
|-----|---------|
| **USER** | Crear tickets, ver tickets propios, ver estado |
| **AGENT** | Ver tickets asignados, actualizar estado, resolver |
| **ADMIN** | Gestionar usuarios, categorías, tags, asignar tickets, ver historial |

### Flujo de Usuario

```
USER reporta problema → ticket creado → ADMIN revisa → ADMIN asigna
                                                    → AGENT trabaja
                                                    → USER verifica
                                                    → AGENT cierra
```

### Microservicios

#### NotificationService (Puerto 8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/notifications | Crear notificación |
| GET | /api/notifications | Listar notificaciones |
| GET | /api/notifications/{id} | Ver notificación |

#### AuditService (Puerto 8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/audit | Registrar evento |
| GET | /api/audit | Listar eventos |
| GET | /api/audit/ticket/{ticketId} | Ver eventos por ticket |

### Endpoints Extendidos

| Método | Endpoint | Rol | Descripción |
|--------|---------|-----|-------------|
| GET | /users | ADMIN | Listar usuarios |
| POST | /users | ADMIN | Crear usuario |
| GET | /users/{id} | ADMIN | Ver usuario |
| PUT | /users/{id} | ADMIN | Actualizar usuario |
| DELETE | /users/{id} | ADMIN | Desactivar usuario |
| GET | /categories | ADMIN | Listar categorías |
| POST | /categories | ADMIN | Crear categoría |
| GET | /tags | ADMIN | Listar tags |
| POST | /tags | ADMIN | Crear tag |
| PUT | /tickets/{id}/assign/{userId} | ADMIN | Asignar ticket |
| GET | /tickets/assigned-to-me | AGENT | Ver tickets asignados |

### Requerimientos por Lección

| Lección | Requisitos Adicionales |
|---------|----------------------|
| 10 | JPA + User entity con roles, Ticket con User relaciones, seed de usuarios |
| 11 | Perfiles (H2/MySQL/PostgreSQL) |
| 12 | Category (One-to-Many), Tag (Many-to-Many), CRUD completo |
| 13 | TicketHistory, registro automático de cambios |
| 14 | Flyway migrations, Foreign Keys |
| 15 | Microservicios: Feign + RestClient para notificaciones y auditoría |
| 16 | Spring Security con 3 roles |
| 17 | Logging de operaciones |
| 18 | Exception handling |

---

## 🏗 Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    Tickets (8080)                       │
│  Controller → Service → Repository → Model/DTO           │
└───────────────────────┬─────────────────────────────────┘
                       │
           ┌──────────┴──────────┐
           │                     │
           ▼                     ▼
┌─────────────────┐   ┌─────────────────┐
│ Notification    │   │ Audit           │
│ Service (8081)  │   │ Service (8082)   │
│ POST /api/notif  │   │ POST /api/audit  │
│ GET /api/notif  │   │ GET /api/audit  │
└─────────────────┘   └─────────────────┘
```

### Tecnologías

| Componente | Tecnología |
|------------|-------------|
| Framework | Spring Boot 4.0.5 |
| Lenguaje | Java 21 |
| ORM | JPA/Hibernate |
| BD (dev) | H2 |
| BD (prod) | MySQL, PostgreSQL (Supabase) |
| Client HTTP | OpenFeign, RestClient |
| Migration | Flyway |

---

## ✅ Checklist de Implementación

### Scope 1 (01-09)

- [x] Ticket entity básico
- [x] GET /tickets (listar)
- [x] GET /tickets/{id} (ver uno)
- [x] POST /tickets (crear)
- [x] PUT /tickets/{id} (actualizar)
- [x] DELETE /tickets/{id} (eliminar)
- [x] Validaciones
- [x] DTO TicketRequest
- [x] Filtro ?status=
- [x] Persistencia H2

### Scope 2 (10-18)

| Lección | Estado |
|---------|--------|
| 10 - User con roles | ✅ Completado |
| 11 - Perfiles BD | ✅ Completado |
| 12 - Category/Tag | ✅ Completado |
| 13 - TicketHistory | ✅ Completado |
| 14 - Flyway | ✅ Completado |
| 15 - Microservicios | ✅ Completado |
| 16 - Security | ⏳ Pendiente |
| 17 - Logging | ⏳ Pendiente |
| 18 - Exceptions | ⏳ Pendiente |

---

## 📝seed de Usuarios

| Usuario | Contraseña | Rol |
|--------|----------|-----|
| admin | admin123 | ADMIN |
| agent1 | agent123 | AGENT |
| user1 | user123 | USER |

---

## 🚀 Ejecución

```bash
# Scope 1: Tickets (Puerto 8080)
cd Tickets-09
./mvnw spring-boot:run

# NotificationService (Puerto 8081)
cd NotificationService
./mvnw spring-boot:run

# AuditService (Puerto 8082)
cd AuditService
./mvnw spring-boot:run
```

---

## 📅 Historial de Versiones

| Fecha | Versión | Cambios |
|-------|---------|---------|
| 2026-04-16 | 1.0 | Caso extendido documentado |

---

**Estado**: En desarrollo
**Última actualización**: Abril 2026