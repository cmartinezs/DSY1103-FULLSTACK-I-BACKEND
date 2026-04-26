# Tickets-10 — Lección 10: Introducción a JPA

Subproyecto del curso **DSY1103 - Fullstack I**.

Migra el almacenamiento de HashMap en memoria a **Spring Data JPA** con base de datos H2 embebida.

---

## 🔄 Cambios desde Lección 09 (base Tickets)

### Dependencias nuevas
| Dependencia | Para qué sirve |
|---|---|
| `spring-boot-starter-data-jpa` | Habilita JPA/Hibernate |
| `h2` | Base de datos en memoria para desarrollo |
| `spring-boot-h2console` | Consola web para inspeccionar H2 |

### Modelo — `Ticket.java`
- Convertida a entidad JPA: `@Entity`, `@Table(name = "tickets")`
- PK con auto-increment: `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- Campos nuevos: `createdAt`, `estimatedResolutionDate`, `effectiveResolutionDate`
- Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`

### Repositorio — `TicketRepository.java`
- Reemplaza el HashMap por `JpaRepository<Ticket, Long>`
- Derivación de queries: `existsByTitleIgnoreCase`, `findByStatusIgnoreCase`, `findAllByOrderByCreatedAtAsc`

### Servicio — `TicketService.java`
- CRUD completo delegando a JPA
- Validación de título único → lanza `IllegalArgumentException` (409 Conflict)

### Patrón de DTOs (Java records)
- `TicketRequest` → entrada HTTP con validaciones (`@NotBlank`, `@Size`)
- `TicketCommand` → objeto interno que lleva los datos hasta el Service
- `TicketResult` → objeto de retorno del Service
- `TicketResponse` → salida HTTP (mapeada desde `TicketResult` en el Controller)

### Controlador — `TicketController.java`
- CRUD completo: GET, POST, GET/by-id, PUT/by-id, DELETE/by-id
- Filtro por estado: `GET /tickets?status=NEW`
- Manejo de errores de validación (`@ExceptionHandler`)
- Error de negocio → `409 Conflict`

### Configuración
- `application.yml`: context-path `/ticket-app`, puerto 8080
- H2 en memoria: `jdbc:h2:mem:tickets_db`, `ddl-auto: create-drop`
- Consola H2 disponible en `/ticket-app/h2-console`

### Inicialización de datos — `DataInitializer.java`
- Crea tickets de ejemplo al arrancar si la tabla está vacía

---

## 🛠️ Tecnologías

| Herramienta | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.5 |
| Spring Web MVC | (incluido) |
| Spring Data JPA / Hibernate | (incluido) |
| H2 Database | (incluido) |
| Lombok | (incluido) |
| Jakarta Validation | (incluido) |
| Maven Wrapper | (incluido) |

---

## 📁 Estructura del proyecto

```
src/main/java/cl/duoc/fullstack/tickets/
├── TicketsApplication.java
├── config/
│   └── DataInitializer.java        # Datos iniciales de ejemplo
├── controller/
│   └── TicketController.java       # Endpoints REST
├── dto/
│   ├── TicketRequest.java          # Entrada HTTP (con validaciones)
│   ├── TicketCommand.java          # Objeto interno Controller → Service
│   ├── TicketResult.java           # Objeto interno Service → Controller
│   └── TicketResponse.java         # Salida HTTP
├── model/
│   ├── Ticket.java                 # Entidad JPA
│   └── ErrorResponse.java          # Record para respuestas de error
├── respository/
│   └── TicketRepository.java       # JpaRepository<Ticket, Long>
└── service/
    └── TicketService.java          # Lógica de negocio
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

---

## 🔌 Endpoints

Base URL: `http://localhost:8080/ticket-app`

| Método | Ruta | Body | Descripción | Respuesta OK |
|---|---|---|---|---|
| `GET` | `/tickets` | — | Listar todos (opcional `?status=`) | `200` lista |
| `POST` | `/tickets` | `TicketRequest` | Crear ticket | `201` ticket creado |
| `GET` | `/tickets/by-id/{id}` | — | Obtener por ID | `200` / `404` |
| `PUT` | `/tickets/by-id/{id}` | `TicketRequest` | Actualizar ticket | `200` / `404` |
| `DELETE` | `/tickets/by-id/{id}` | — | Eliminar ticket | `204` / `404` |

### Errores posibles

| Código | Causa |
|---|---|
| `400 Bad Request` | Validación fallida (`@NotBlank`, `@Size`) |
| `404 Not Found` | ID no existe |
| `409 Conflict` | Título duplicado |

---

## 🚀 Ejecutar

```bash
cd Tickets-10

# Windows
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

La aplicación arranca con H2 en memoria. Sin configuración adicional.

Consola H2: `http://localhost:8080/ticket-app/h2-console`
JDBC URL: `jdbc:h2:mem:tickets_db`

---

## 🧪 Tests

```bash
.\mvnw.cmd test
```

---

**Base**: Lección 09 — Repositorio Customizado  
**Stack**: Spring Boot 4.0.5 · Java 21 · JPA/Hibernate · H2  
**Estado**: ✅ Completada