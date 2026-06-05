# Tickets-12 — Lección 12: Relaciones entre Entidades JPA

Subproyecto del curso **DSY1103 - Fullstack I**.

Extiende Lección 11 incorporando **relaciones JPA** (`@ManyToOne` / `@OneToMany`) entre `Ticket` y `User`, gestión de usuarios y asignación de tickets por email.

---

## 🔄 Cambios desde Lección 11

### Entidad nueva — `User`
- `@Entity`, `@Table(name = "users")`
- Campos: `id` (PK), `name`, `email` (único)
- `@OneToMany(mappedBy = "createdBy")` y `@OneToMany(mappedBy = "assignedTo")` para navegación inversa

### Relaciones en `Ticket`
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "created_by_id")
private User createdBy;         // FK → users.id

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "assigned_to_id")
private User assignedTo;        // FK → users.id (nullable)
```

Sin `@JsonIgnoreProperties` — los entities nunca salen del Service (se convierten a DTOs).

### DTOs actualizados
| DTO | Qué se agregó |
|---|---|
| `TicketRequest` | Campo `createdByEmail` (`@NotBlank` + `@Email`) |
| `TicketCommand` | Campo `createdByEmail` |
| `TicketResult` | Campos `UserResult createdBy` y `UserResult assignedTo` |
| `TicketResponse` | Campos `UserResult createdBy` y `UserResult assignedTo` |

### DTOs nuevos
| DTO | Descripción |
|---|---|
| `UserRequest` | Entrada HTTP para crear usuario (`name`, `email`) |
| `UserResult` | Record interno con `id`, `name`, `email` |
| `AssignTicketRequest` | Body del `PATCH` — solo `@Email` (null/vacío = desasignar) |

### Excepción nueva — `BadRequestException`
Distingue dos tipos de error del cliente:

| Excepción | HTTP | Cuándo |
|---|---|---|
| `IllegalArgumentException` | `409 Conflict` | Regla de negocio (título duplicado) |
| `BadRequestException` | `400 Bad Request` | Dato inválido (email no registrado) |

### `TicketService` actualizado
- Depende de `TicketRepository` + `UserRepository`
- `create()`: busca el creador por email con `orElseThrow(BadRequestException)`
- `assignTicket()`: asigna o desasigna usuario via email; null/vacío = desasignar
- `toResult()`: mapea `Ticket` → `TicketResult` con `UserResult` anidados

### Nuevos: `UserRepository`, `UserService`, `UserController`
- CRUD de usuarios en `/users`
- Validación de email único → `409 Conflict`

### `TicketController` actualizado
- `POST /tickets`: captura también `BadRequestException` → `400`
- Nuevo `PATCH /tickets/by-id/{id}`: asignar/desasignar usuario

### `DataInitializer` actualizado
- Crea 2 usuarios de ejemplo (Ana García, Carlos López)
- Los tickets iniciales referencian esos usuarios (`setCreatedBy`)

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
│   ├── TicketController.java       # Endpoints de tickets (+ PATCH asignación)
│   └── UserController.java         # Endpoints de usuarios
├── dto/
│   ├── TicketRequest.java          # Entrada HTTP ticket (+ createdByEmail)
│   ├── TicketCommand.java          # Objeto interno Controller → Service
│   ├── TicketResult.java           # Objeto interno Service → Controller (+ UserResult)
│   ├── TicketResponse.java         # Salida HTTP ticket (+ UserResult)
│   ├── UserRequest.java            # Entrada HTTP usuario
│   ├── UserResult.java             # Record interno (id, name, email)
│   └── AssignTicketRequest.java    # Body del PATCH (assignedToEmail)
├── exception/
│   └── BadRequestException.java    # RuntimeException → 400 Bad Request
├── model/
│   ├── Ticket.java                 # Entidad JPA (+ @ManyToOne createdBy, assignedTo)
│   ├── User.java                   # Entidad JPA nueva
│   └── ErrorResponse.java          # Record para respuestas de error
├── respository/
│   ├── TicketRepository.java       # JpaRepository<Ticket, Long>
│   └── UserRepository.java         # JpaRepository<User, Long> + findByEmail
└── service/
    ├── TicketService.java          # Lógica de negocio (+ UserRepository)
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

### `User`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | PK auto-incremental |
| `name` | `String` | Nombre (requerido) |
| `email` | `String` | Email (único, requerido) |

---

## 🔌 Endpoints

Base URL: `http://localhost:8080/ticket-app`

### Tickets

| Método | Ruta | Body | Descripción | Respuesta OK |
|---|---|---|---|---|
| `GET` | `/tickets` | — | Listar todos (opcional `?status=`) | `200` lista |
| `POST` | `/tickets` | `TicketRequest` | Crear ticket (requiere `createdByEmail`) | `201` |
| `GET` | `/tickets/by-id/{id}` | — | Obtener por ID | `200` / `404` |
| `PUT` | `/tickets/by-id/{id}` | `TicketRequest` | Actualizar ticket | `200` / `404` |
| `PATCH` | `/tickets/by-id/{id}` | `AssignTicketRequest` | Asignar/desasignar usuario | `200` / `404` |
| `DELETE` | `/tickets/by-id/{id}` | — | Eliminar ticket | `204` / `404` |

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

### Ejemplo: crear ticket

```http
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Teclado no funciona",
  "description": "Las teclas F1-F4 no responden",
  "createdByEmail": "ana.garcia@empresa.com"
}
```

### Ejemplo: asignar ticket

```http
PATCH http://localhost:8080/ticket-app/tickets/by-id/1
Content-Type: application/json

{ "assignedToEmail": "carlos.lopez@empresa.com" }
```

### Ejemplo: desasignar ticket

```http
PATCH http://localhost:8080/ticket-app/tickets/by-id/1
Content-Type: application/json

{ "assignedToEmail": "" }
```

---

## 🌍 Cómo ejecutar

```bash
cd Tickets-12

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

**Base**: Lección 11 — Multi-Base de Datos  
**Stack**: Spring Boot 4.0.5 · Java 21 · JPA/Hibernate · H2 · MySQL · PostgreSQL  
**Estado**: ✅ Completada