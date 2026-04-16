# Plan de Implementación: Proyecto Ticket - Lecciones 10-18

## 📋 Objetivo

Crear proyectos independientes para cada lección (Tickets-10, Tickets-11, ..., Tickets-18), siendo cada uno un snapshot del progreso. Cada proyecto es un clone del anterior con los cambios de esa lección agregados.

## 🎯 Enfoque

1. **Base**: Lección 09 existente (proyecto Tickets actual)
2. **Clones incrementales**: 
   - Copiar Tickets → Tickets-10, implementar cambios de Lección 10
   - Copiar Tickets-10 → Tickets-11, implementar cambios de Lección 11
   - Y así sucesivamente...
3. **Cada proyecto es independiente**: Puede iniciarse, probarse y ejecutarse por sí solo
4. **Validación**: Verificar que cada lección funciona antes de crear el siguiente proyecto
5. **Documentación**: Cada proyecto tiene su README con cambios de esa lección

## 📚 Lecciones a Implementar (10-18)

### Lección 09: Repositorio Customizado (Base)
- **Estado**: ✅ Base existente
- **Propósito**: Referencia base, no se modifica
- **Proyecto**: `Tickets/`

---

### Lección 10: Introducción a JPA
- **Tema**: Migración de repositorio en-memoria a Spring Data JPA
- **Proyecto**: `Tickets-10/` ✅ **COMPLETADA**
- **Cambios**:
  - ✅ Agregar dependencias JPA (spring-boot-starter-data-jpa, hibernate)
  - ✅ Refactorizar `TicketRepository` de HashMap a Spring Data JPA
  - ✅ Crear `application.yml` con configuración JPA
  - ✅ Implementar auto-increment de IDs con `@GeneratedValue`
  - ✅ Migrar datos iniciales a `DataInitializer` (CommandLineRunner)
  - ✅ Tests de repositorio JPA
- **Validación**: 
  - ✅ `./mvnw test` pasa sin errores
  - ✅ `./mvnw spring-boot:run` funciona
  - ✅ Endpoints CRUD funcionan con JPA
- **Status**: Directorio `Tickets-10/` creado y funcional

---

### Lección 11: Configuración de Bases de Datos
- **Tema**: Perfiles de Spring Boot + Variables de Entorno
- **Proyecto**: `Tickets-11/` ✅ **COMPLETADA**
- **Cambios**:
  - ✅ Crear `application.yml` base (sin credenciales)
  - ✅ Crear `application-h2.yml` (desarrollo con H2 en memoria)
  - ✅ Crear `application-mysql.yml` (local XAMPP)
  - ✅ Crear `application-supabase.yml` (PostgreSQL en nube)
  - ✅ Crear `.env.example` con plantilla de variables
  - ✅ Agregar drivers: mysql-connector-j, postgresql
  - ✅ Verificar que la app arranca con todos los perfiles
- **Validación**: 
  - ✅ `./mvnw spring-boot:run` funciona con `-Dspring.profiles.active=h2`
  - ✅ `./mvnw test` pasa
  - ✅ Archivo .env.example documenta todas las variables
  - ✅ Sin credenciales hardcodeadas
- **Status**: Directorio `Tickets-11/` creado y funcional

---

### Lección 12: Relaciones JPA
- **Tema**: Modelar relaciones entre entidades (One-to-Many, Many-to-Many)
- **Proyecto**: `Tickets-12/` ✅ **COMPLETADA**
- **Cambios**:
  - ✅ Crear entidad `Category` (One-to-Many con Ticket)
  - ✅ Crear entidad `Tag` (Many-to-Many con Ticket)
  - ✅ Implementar endpoints para CRUD de categorías
  - ✅ Implementar endpoints para CRUD de tags
  - ✅ Validar cascadas y comportamiento de relaciones
  - ✅ Manejar serialización circular (Jackson annotations)
  - ✅ Actualizar TicketService para manejar relaciones
  - ✅ DataInitializer con datos de prueba
- **Validación**:
  - ✅ CRUD completo de categorías funciona
  - ✅ CRUD completo de tags funciona
  - ✅ Relaciones se guardan correctamente en BD
  - ✅ Tests de relaciones JPA
  - ✅ Serialización JSON sin loops infinitos
- **Status**: Directorio `Tickets-12/` creado y funcional

---

### Lección 13: Historial y Auditoría
- **Tema**: Tracking automático de cambios en la BD
- **Proyecto**: `Tickets-13/` ✅ **COMPLETADA**
- **Cambios**:
  - ✅ Crear entidad `TicketHistory` (historial de cambios de estado)
  - ✅ Crear relación `@OneToMany` en Ticket → TicketHistory
  - ✅ Crear `TicketHistoryRepository`
  - ✅ Implementar registro automático en TicketService
  - ✅ Crear endpoint GET `/tickets/{id}/history` para ver historial
- **Validación**:
  - ✅ Registro automático al crear ticket (estado NEW)
  - ✅ Registro automático al cambiar estado (solo si cambia)
  - ✅ Endpoint de historial retorna cambios en orden cronológico (más reciente primero)
  - ✅ Tests pasando
- **Status**: Directorio `Tickets-13/` creado y funcional

---

### Lección 14: Migraciones Flyway
- **Tema**: Versionado profesional de cambios de BD
- **Proyecto**: `Tickets-14/` ✅ **COMPLETADA**
- **Cambios**:
  - ✅ Agregar dependencia Flyway (`flyway-core`, `flyway-mysql`) en `pom.xml`
  - ✅ Crear directorio `src/main/resources/db/migration`
  - ✅ Crear migraciones SQL:
    - ✅ `V1__Initial_schema.sql` (users, tickets)
    - ✅ `V2__Add_categories.sql` (categories)
    - ✅ `V3__Add_tags.sql` (tags, ticket_tags)
    - ✅ `V4__Add_audit_tables.sql` (ticket_history)
  - ✅ Configurar Flyway en `application-mysql.yml` y `application-supabase.yml`
  - ✅ Mantener JPA automático para `application-h2.yml` (create-drop)
  - ✅ Crear índices y constraints en migraciones
- **Validación**:
  - ✅ Tests pasando con H2 (ddl-auto: create-drop)
  - ✅ Compilación exitosa
  - ✅ Flyway configurado para MySQL/Supabase (ddl-auto: validate)
- **Status**: Directorio `Tickets-14/` creado y funcional

---

### Lección 15: Comunicación entre Microservicios
- **Tema**: RestClient y FeignClient para comunicación HTTP entre servicios
- **Proyecto**: `Tickets-15/` ⏳ **PENDIENTE**
- **Cambios**:
  - Crear un segundo servicio `NotificationService` (mock externo)
  - Implementar `NotificationClient` con `RestClient` (Spring Framework 6.1+)
  - Alternativa: Agregar dependencia OpenFeign e implementar `@FeignClient`
  - Implementar fallback para llamadas fallidas
  - Integrar notificaciones en creación/actualización de tickets
  - Configurar timeouts y reintentos en RestClient
  - Crear tests con `@MockBean` y RestAssured
  - Documentar manejo de errores de comunicación
- **Validación**:
  - RestClient se conecta a servicio externo correctamente
  - Fallback funciona si servicio no está disponible
  - Timeouts se respetan
  - Tests de integración con mocks
- **Status**: Pendiente

---

### Lección 16: Spring Security
- **Tema**: Autenticación y Autorización (Roles ADMIN/USER)
- **Proyecto**: `Tickets-16/` ⏳ **PENDIENTE**
- **Cambios**:
  - Agregar dependencia Spring Security en `pom.xml`
  - Crear entidad `User` con roles (ADMIN, USER)
  - Crear `UserService` y `UserRepository`
  - Implementar `UserDetailsService` personalizado
  - Crear controlador `/auth` con login/logout
  - Configurar `SecurityConfig` con:
    - Endpoint públicos: `/auth/**`, `/tickets/by-id/**` (GET)
    - Endpoints protegidos: POST, PUT, DELETE requieren autenticación
    - ADMIN: puede ver historial, cambiar estados especiales
    - USER: puede ver y editar sus propios tickets
  - Implementar JWT (opcional pero recomendado)
  - Tests de autenticación y autorización
- **Validación**:
  - Login funciona con usuario válido
  - Tokens de sesión/JWT se generan correctamente
  - Endpoints protegidos requieren autenticación
  - Roles se verifican correctamente
  - Tests de seguridad
- **Status**: Pendiente

---

### Lección 17: Logging
- **Tema**: SLF4J + Logback con niveles DEBUG/INFO/WARN/ERROR
- **Proyecto**: `Tickets-17/` ⏳ **PENDIENTE**
- **Cambios**:
  - Crear `logback-spring.xml` en `src/main/resources`
  - Configurar niveles de logging:
    - DEBUG: Detalles de ejecución
    - INFO: Eventos importantes (crear ticket, login)
    - WARN: Situaciones anómalas (ticket sin categoría)
    - ERROR: Errores (fallos de BD, servicios externos)
  - Agregar logging en:
    - Controllers (requests/responses)
    - Services (lógica de negocio)
    - Repositories (operaciones BD)
    - SecurityConfig (intentos de login)
    - FeignClient calls (comunicación inter-servicios)
  - Configurar profiles diferentes (dev=DEBUG, prod=INFO)
  - Crear logs en archivo `logs/app.log`
- **Validación**:
  - Logs se generan correctamente en consola y archivo
  - Niveles se respetan según perfil
  - Información sensible no se loguea
  - Tests verifican logs esperados
- **Status**: Pendiente

---

### Lección 18: Exception Handling Global
- **Tema**: @ControllerAdvice y respuestas uniformes
- **Proyecto**: `Tickets-18/` ⏳ **PENDIENTE**
- **Cambios**:
  - Crear `GlobalExceptionHandler` con `@ControllerAdvice`
  - Crear `ApiResponse<T>` record con respuesta uniforme
  - Crear custom exceptions:
    - `EntityNotFoundException` → 404
    - `DuplicateTicketException` → 409
    - `UnauthorizedException` → 403
    - `ValidationException` → 400
  - Capturar excepciones genéricas → 500
  - Refactorizar controllers para usar excepciones
  - Agregar context path en respuestas de error
  - Tests de manejo de excepciones
- **Validación**:
  - Todas las excepciones retornan formato uniforme
  - Códigos HTTP correctos
  - Información sensible no se expone
  - Tests de cada tipo de excepción
- **Status**: Pendiente

---

## 📊 Progreso General

### ✅ Completadas (5/9)

| # | Lección | Proyecto | Status |
|---|---------|----------|--------|
| 10 | JPA Intro | `Tickets-10/` | ✅ HECHO |
| 11 | Database Config | `Tickets-11/` | ✅ HECHO |
| 12 | JPA Relations | `Tickets-12/` | ✅ HECHO |
| 13 | Historial y Auditoría | `Tickets-13/` | ✅ HECHO |
| 14 | Flyway Migrations | `Tickets-14/` | ✅ HECHO |

### ⏳ Pendientes (4/9)

| # | Lección | Proyecto | Status |
|---|---------|----------|--------|
| 15 | Microservices | `Tickets-15/` | ⏳ TODO |
| 16 | Spring Security | `Tickets-16/` | ⏳ TODO |
| 17 | Logging | `Tickets-17/` | ⏳ TODO |
| 18 | Exception Handling | `Tickets-18/` | ⏳ TODO |

## 🛠️ Estructura de Proyectos

Cada proyecto es independiente y funcional:

```
DSY1103-FULLSTACK-I-BACKEND/
├── Tickets/              # Base (Lección 09)
├── Tickets-10/          # JPA Intro ✅
│   ├── src/
│   ├── pom.xml
│   ├── README.md        # Cambios específicos de L10
│   └── .mvnw (Maven Wrapper)
├── Tickets-11/          # Database Config ✅
│   ├── src/
│   ├── pom.xml
│   ├── README.md        # Cambios específicos de L11
│   ├── .env.example
│   └── .mvnw (Maven Wrapper)
├── Tickets-12/          # JPA Relations ✅
├── ...
└── Tickets-18/          # Exception Handling ⏳
```

## 📋 Proceso de Creación

Para cada lección N (10-18):

1. **Clone**: `cp -r Tickets-(N-1) Tickets-N`
2. **Implementar**: Cambios específicos de Lección N
3. **Compilar**: `cd Tickets-N && ./mvnw clean compile` ✅
4. **Probar**: `cd Tickets-N && ./mvnw test` ✅
5. **Validar**: `cd Tickets-N && ./mvnw spring-boot:run` ✅
6. **Documentar**: Actualizar README.md con cambios

## ✅ Checklist de Validación

Antes de marcar una lección como completa:

- [x] Código compila sin errores
- [x] Todos los tests pasan
- [x] Aplicación arranca correctamente
- [x] Funcionalidad verificada manualmente
- [x] README.md documenta cambios
- [x] Proyecto independiente y funcional

## 🚀 Próximos Pasos

**Estado Actual**: Lecciones 10, 11, 12, 13 y 14 completadas (56% de progreso)

**Siguiente**: Lección 15 - Comunicación entre Microservicios
- Clone `Tickets-14/` → `Tickets-15/`
- Agregar RestClient o FeignClient
- Crear segundo servicio mock externo
- Implementar notificacionesHttp

## 📝 Notas Importantes

- **Base actual**: Proyecto `Tickets/` es Lección 09 (Repositorio Customizado)
- **Proyectos separados**: Tickets-10, Tickets-11, ..., Tickets-18
- **Cada proyecto**: Independiente, compilable, testeable por sí solo
- **Framework**: Spring Boot 4.0.5, Java 21
- **ORM**: Spring Data JPA + Hibernate
- **Perfiles**: H2 (desarrollo), MySQL (local), Supabase (cloud)
- **Testing**: JUnit 5, Mockito, RestAssured
- **Versionado**: Maven para builds

---

**Última actualización**: Abril 2026  
**Estado**: Plan actualizado para proyectos separados, listo para implementar
