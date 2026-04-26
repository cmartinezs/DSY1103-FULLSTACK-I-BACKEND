# Tickets-11 — Lección 11: Configuración Multi-Base de Datos

Subproyecto del curso **DSY1103 - Fullstack I**.

Extiende Lección 10 incorporando **perfiles de Spring Boot** para conectar a H2, MySQL o PostgreSQL/Supabase sin cambiar código Java.

---

## 🔄 Cambios desde Lección 10

### Dependencias nuevas
| Dependencia | Para qué sirve |
|---|---|
| `mysql-connector-j` | Driver JDBC para MySQL (XAMPP) |
| `postgresql` | Driver JDBC para PostgreSQL (Supabase) |
| `spring-dotenv` | Carga automática de archivos `.env` |

> `spring-boot-h2console` ya estaba en L10.

### Perfiles de Spring Boot

Un perfil = un archivo `application-{perfil}.yml` con la configuración de esa base de datos.

| Perfil | Archivo | Base de datos |
|---|---|---|
| `h2` | `application-h2.yml` | H2 en memoria (desarrollo rápido) |
| `mysql` | `application-mysql.yml` | MySQL local (XAMPP) |
| `supabase` | `application-supabase.yml` | PostgreSQL en la nube |

### Archivos `.env` por entorno

| Archivo | Perfil activo | Descripción |
|---|---|---|
| `.env.local` | `h2` | Desarrollo local sin BD externa |
| `.env.dev` | `mysql` | Desarrollo con MySQL/XAMPP |
| `.env.test` | `supabase` | Pruebas contra Supabase |
| `.env.prod` | `supabase` | Producción (mismas variables, distintos valores) |
| `.env.example` | — | Plantilla — copiar y rellenar |

> ⚠️ **Nunca** hacer commit de un `.env` con credenciales reales. Solo `.env.example` va al repositorio.

### Sin cambios de código Java
El modelo, repositorio, servicio, controlador y DTOs son idénticos a Lección 10. Solo cambia la configuración.

---

## 🌍 Cómo activar un perfil

### Opción 1 — Archivo `.env` (recomendada)

```bash
# Copiar el entorno que necesitas
copy .env.local .env      # → perfil h2
copy .env.dev .env        # → perfil mysql
copy .env.test .env       # → perfil supabase

# Ejecutar
.\mvnw.cmd spring-boot:run
```

### Opción 2 — Variable de entorno

```powershell
# PowerShell
$env:SPRING_PROFILES_ACTIVE="mysql"
.\mvnw.cmd spring-boot:run
```

### Opción 3 — Argumento de Maven

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=supabase
```

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

src/main/resources/
├── application.yml                 # Config base (sin credenciales)
├── application-h2.yml              # Perfil H2
├── application-mysql.yml           # Perfil MySQL
└── application-supabase.yml        # Perfil Supabase/PostgreSQL
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
cd Tickets-11

# 1. Seleccionar entorno
copy .env.local .env    # H2 — sin configuración adicional

# 2. Iniciar
.\mvnw.cmd spring-boot:run
```

---

## 🧪 Tests

```bash
.\mvnw.cmd test
```

---

**Base**: Lección 10 — JPA + H2  
**Stack**: Spring Boot 4.0.5 · Java 21 · JPA/Hibernate · H2 · MySQL · PostgreSQL  
**Estado**: ✅ Completada