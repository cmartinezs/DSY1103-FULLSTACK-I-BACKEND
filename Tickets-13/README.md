# Tickets-13 — Lección 13: Historial de Cambios

Subproyecto del curso **DSY1103 - Fullstack I**.

Extiende Lección 12 incorporando un **historial persistente de cambios** en el ticket: registra automáticamente cada vez que cambia el estado o el asignado, exponiendo la auditoría a través de un endpoint dedicado.

---

## 🔄 Cambios desde Lección 12

### Entidad nueva — `TicketHistory`
- `@Entity`, `@Table(name = "ticket_history")`
- Relación `@ManyToOne(fetch = FetchType.LAZY)` a `Ticket` (FK `ticket_id`, NOT NULL)
- Campos de auditoría:

| Campo | Tipo | Descripción |
|---|---|---|
| `previousStatus` | `String` | Estado antes del cambio (null si el cambio fue solo de asignado) |
| `newStatus` | `String` | Estado después del cambio (null si el cambio fue solo de asignado) |
| `previousAssignedEmail` | `String` | Email del asignado anterior (String, no FK) |
| `newAssignedEmail` | `String` | Email del nuevo asignado (String, no FK) |
| `changedAt` | `LocalDateTime` | Momento del cambio (NOT NULL) |
| `comment` | `String` | Nota opcional |

Sin `@JsonIgnore` — la entidad nunca sale del Service (se convierte a `TicketHistoryResult`).

> **¿Por qué email y no FK a User?**  
> El historial es un log inmutable que registra un snapshot del estado en el momento del cambio. Si el usuario fuera eliminado o modificado, el historial quedaría inconsistente con una FK. El email es el dato identitario autosuficiente que existía en ese momento.

### Relación nueva en `Ticket`
```java
@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = false)
private List<TicketHistory> history = new ArrayList<>();
```
`orphanRemoval = false`: el historial nunca debe borrarse, incluso si se elimina el ticket.

### DTO nuevo — `TicketHistoryResult`
Record que expone el historial al cliente. El Service construye instancias a partir de `TicketHistory`:
```java
public record TicketHistoryResult(
    Long id, String previousStatus, String newStatus,
    String previousAssignedEmail, String newAssignedEmail,
    LocalDateTime changedAt, String comment
) {}
```

### Repositorio nuevo — `TicketHistoryRepository`
```java
List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
```

### `TicketService` actualizado
- Inyecta `TicketHistoryRepository` (constructor injection)
- `create()` → llama `recordChange(saved, null, "NEW", null, null, "Ticket creado")` después del `save`
- `updateById()` → ahora retorna `TicketResult` (lanza `NoSuchElementException` si no existe en lugar de `Optional.empty()`); captura estado anterior antes de guardar y llama `recordChange`
- `assignTicket()` → captura email del asignado anterior, llama `recordChange` después de guardar
- `recordChange()` (privado): solo persiste un `TicketHistory` si hay cambio real de estado o de asignado
- `getHistory(Long ticketId)` → retorna `Optional<List<TicketHistoryResult>>`; vacío si el ticket no existe
- `toHistoryResult()` (privado): mapea `TicketHistory` → `TicketHistoryResult`

### `TicketController` actualizado
- `updateTicketById`: captura `NoSuchElementException` → `404` (antes manejaba `Optional.empty()`)
- Nuevo endpoint `GET /tickets/by-id/{id}/history` que llama `service.getHistory(id)`

> **¿Por qué no hay `TicketHistoryController`?**  
> `TicketHistory` es una entidad débil: no tiene sentido sin su ticket padre. El historial se accede siempre como subrecurso del ticket, por lo que el endpoint vive en `TicketController` y no hay controller dedicado.

---

## 🛠️ Tecnologías

| Herramienta | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.5 |
| Spring Web MVC | (incluido) |
| Spring Data JPA / Hibernate | (incluido) |
| H2 Database | (incluido) |
| MySQL Connector/J | (incluido) |
| PostgreSQL JDBC | (incluido) |
| spring-dotenv | 4.0.0 |
| Lombok | (incluido) |
| Jakarta Validation | (incluido) |
| Maven Wrapper | (incluido) |

---

## 📁 Estructura del proyecto

```
src/main/java/cl/duoc/fullstack/tickets/
├── TicketsApplication.java
├── config/
│   └── DataInitializer.java        # Crea usuarios + tickets de ejemplo
├── controller/
│   ├── TicketController.java       # Endpoints de tickets (+ GET historial)
│   └── UserController.java         # Endpoints de usuarios
├── dto/
│   ├── TicketRequest.java          # Entrada HTTP ticket
│   ├── TicketCommand.java          # Objeto interno Controller → Service
│   ├── TicketResult.java           # Objeto interno Service → Controller
│   ├── TicketResponse.java         # Salida HTTP ticket
│   ├── TicketHistoryResult.java    # Salida HTTP historial (record)   ← NUEVO
│   ├── UserRequest.java            # Entrada HTTP usuario
│   ├── UserResult.java             # Record interno (id, name, email)
│   └── AssignTicketRequest.java    # Body del PATCH (assignedToEmail)
├── exception/
│   └── BadRequestException.java    # RuntimeException → 400 Bad Request
├── model/
│   ├── Ticket.java                 # Entidad JPA (+ @OneToMany history)  ← ACTUALIZADO
│   ├── TicketHistory.java          # Entidad JPA de auditoría            ← NUEVO
│   ├── User.java                   # Entidad JPA
│   └── ErrorResponse.java          # Record para respuestas de error
├── respository/
│   ├── TicketRepository.java       # JpaRepository<Ticket, Long>
│   ├── TicketHistoryRepository.java # JpaRepository + findByTicketId... ← NUEVO
│   └── UserRepository.java         # JpaRepository<User, Long> + findByEmail
└── service/
    ├── TicketService.java          # Lógica de negocio (+ historial)     ← ACTUALIZADO
    └── UserService.java            # Lógica de usuarios

src/main/resources/
├── application.yml
├── application-h2.yml
├── application-mysql.yml
└── application-supabase.yml
```

---

## 📦 Modelo de datos

### `Ticket`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | PK auto-incremental |
| `title` | `String` | Título (único, requerido) |
| `description` | `String` | Descripción (requerida) |
| `status` | `String` | `NEW`, `IN_PROGRESS`, `RESOLVED`, `CLOSED` |
| `createdAt` | `LocalDateTime` | Fecha/hora de creación (auto) |
| `estimatedResolutionDate` | `LocalDate` | Fecha estimada (createdAt + 5 días) |
| `effectiveResolutionDate` | `LocalDateTime` | Fecha real de resolución |
| `createdBy` | `User` | FK `created_by_id` — requerido |
| `assignedTo` | `User` | FK `assigned_to_id` — nullable |
| `history` | `List<TicketHistory>` | Historial de cambios (lazy, cascade ALL) |

### `User`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | PK auto-incremental |
| `name` | `String` | Nombre (requerido) |
| `email` | `String` | Email (único, requerido) |

### `TicketHistory`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | PK auto-incremental |
| `ticket` | `Ticket` | FK `ticket_id` NOT NULL |
| `previousStatus` | `String` | Estado anterior (null si solo cambió asignado) |
| `newStatus` | `String` | Estado nuevo (null si solo cambió asignado) |
| `previousAssignedEmail` | `String` | Email anterior (null si solo cambió estado) |
| `newAssignedEmail` | `String` | Email nuevo (null si solo cambió estado) |
| `changedAt` | `LocalDateTime` | Timestamp del cambio (NOT NULL) |
| `comment` | `String` | Nota opcional |

---

## 🔌 Endpoints

Base URL: `http://localhost:8080/ticket-app`

### Tickets

| Método | Ruta | Body | Descripción | Respuesta OK |
|---|---|---|---|---|
| `GET` | `/tickets` | — | Listar todos (opcional `?status=`) | `200` lista |
| `POST` | `/tickets` | `TicketRequest` | Crear ticket | `201` |
| `GET` | `/tickets/by-id/{id}` | — | Obtener por ID | `200` / `404` |
| `PUT` | `/tickets/by-id/{id}` | `TicketRequest` | Actualizar ticket | `200` / `404` |
| `PATCH` | `/tickets/by-id/{id}` | `AssignTicketRequest` | Asignar/desasignar usuario | `200` / `404` |
| `DELETE` | `/tickets/by-id/{id}` | — | Eliminar ticket | `204` / `404` |
| `GET` | `/tickets/by-id/{id}/history` | — | Historial de cambios del ticket | `200` / `404` |

### Usuarios

| Método | Ruta | Body | Descripción | Respuesta OK |
|---|---|---|---|---|
| `GET` | `/users` | — | Listar todos los usuarios | `200` lista |
| `POST` | `/users` | `UserRequest` | Crear usuario | `201` |
| `GET` | `/users/{id}` | — | Obtener usuario por ID | `200` / `404` |

### Errores posibles

| Código | Causa |
|---|---|
| `400 Bad Request` | Validación fallida o email de creador/asignado no existe |
| `404 Not Found` | ID no existe |
| `409 Conflict` | Título de ticket duplicado o email de usuario duplicado |

### Ejemplo: consultar historial

```http
GET http://localhost:8080/ticket-app/tickets/by-id/1/history
```

Respuesta:
```json
[
  {
    "id": 2,
    "previousStatus": "NEW",
    "newStatus": "IN_PROGRESS",
    "previousAssignedEmail": null,
    "newAssignedEmail": null,
    "changedAt": "2026-04-15T10:35:00",
    "comment": null
  },
  {
    "id": 1,
    "previousStatus": null,
    "newStatus": "NEW",
    "previousAssignedEmail": null,
    "newAssignedEmail": null,
    "changedAt": "2026-04-15T10:30:00",
    "comment": "Ticket creado"
  }
]
```

---

## 🌍 Cómo ejecutar

```bash
cd Tickets-13

# 1. Seleccionar entorno
copy .env.local .env    # H2 — sin configuración adicional
copy .env.dev .env      # MySQL (XAMPP)
copy .env.test .env     # Supabase

# 2. Iniciar
.\mvnw.cmd spring-boot:run
```

---

## 🧪 Tests

```bash
.\mvnw.cmd test
```

---

**Base**: Lección 12 — Relaciones JPA  
**Stack**: Spring Boot 4.0.5 · Java 21 · JPA/Hibernate · H2 · MySQL · PostgreSQL  
**Estado**: ✅ Completada