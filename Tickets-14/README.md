# Tickets-14 — Lección 14: Comunicación entre Microservicios

Proyecto Spring Boot que extiende Tickets-13 incorporando comunicación con microservicios externos usando **RestClient** y **FeignClient**.

## Stack

- Java 21 / Spring Boot 4.0.5
- Spring Web MVC + Spring Data JPA
- Spring Cloud OpenFeign 4.0.3
- H2 (desarrollo) / MySQL / PostgreSQL (producción)
- Lombok + Jakarta Validation

## Arquitectura

```
TicketController
    └── TicketService
            ├── TicketRepository       (JPA — H2/MySQL/PostgreSQL)
            ├── TicketHistoryRepository
            ├── UserRepository
            ├── NotificationClient     (RestClient → NotificationService :8081)
            └── AuditServiceClient     (FeignClient → AuditService :8082)
```

## Microservicios integrados

| Servicio            | Puerto | Cliente        | Descripción                          |
|---------------------|--------|----------------|--------------------------------------|
| NotificationService | 8081   | RestClient     | Notifica por email al crear/asignar  |
| AuditService        | 8082   | FeignClient    | Registra cambios de estado           |

## Nuevos endpoints

| Método | Ruta                           | Descripción                          |
|--------|--------------------------------|--------------------------------------|
| GET    | `/tickets/by-id/{id}/audit`    | Historial de auditoría (AuditService)|

> Los demás endpoints son idénticos a Tickets-13.

## Comportamiento con servicios caídos

- **NotificationClient** (RestClient): patrón fire-and-forget. Si NotificationService no responde, el ticket ya fue guardado y solo se loguea el error.
- **AuditServiceClient** (FeignClient): fallback automático. Si AuditService no responde, `logEvent()` retorna `null` y `getAuditByTicket()` retorna lista vacía.

## Cómo ejecutar

```bash
# Con H2 (sin base de datos externa)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2

# Con MySQL
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql

# Con Supabase/PostgreSQL
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=supabase

# Ejecutar tests
.\mvnw.cmd test -Dspring.profiles.active=h2
```

## Variables de entorno

Copia `.env.example` a `.env` y ajusta los valores según tu entorno. **No commitees `.env` con credenciales reales.**

### Local — perfil `h2` (sin BD externa)

```env
SPRING_PROFILES_ACTIVE=h2
```

### Dev — perfil `mysql` (XAMPP/MySQL)

```env
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=
```

### Test/Prod — perfil `supabase` (PostgreSQL)

```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.xxxxxxxxxxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=your-supabase-password
```

## URL base

`http://localhost:8080/ticket-app`

## Archivos nuevos respecto a Tickets-13

```
src/main/java/.../
  client/
    NotificationClient.java        # RestClient → NotificationService
    AuditServiceClient.java        # FeignClient interface
    AuditServiceClientFallback.java# Fallback cuando AuditService no responde
  config/
    RestClientConfig.java          # Timeouts para RestClient (5s connect, 10s read)
  dto/
    NotificationRequest.java       # Payload para NotificationService
    AuditRequest.java              # Payload para AuditService
    AuditEvent.java                # Respuesta de AuditService
```
