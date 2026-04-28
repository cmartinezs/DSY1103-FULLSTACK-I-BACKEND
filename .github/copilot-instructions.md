# Copilot Instructions — DSY1103 Fullstack I Backend

## Repository layout

Each `Tickets-N/` folder is a **standalone lesson snapshot** — independent Spring Boot projects that progressively add features. The highest-numbered one is the most complete.

| Project | Lesson | Key addition |
|---------|--------|-------------|
| `Tickets/` | base | In-memory `HashMap`, no DB |
| `Tickets-10/` | 10 | JPA + H2 |
| `Tickets-11/` | 11 | MySQL + PostgreSQL profiles |
| `Tickets-12/` | 12 | Same as 11 |
| `Tickets-13/` | 13 | TicketHistory entity |
| `Tickets-14/` | 14 | Flyway + RestClient/FeignClient (AuditService) |
| `Tickets-15/` | 15 | OpenFeign to NotificationService |
| `Tickets-16/` | 16 | Spring Security (HTTP Basic, 3 roles) |
| `Tickets-17/` | 17 | `@Slf4j` logging |
| `Tickets-18/` | 18 | `@ControllerAdvice` global exception handler |

Supporting microservices (all in-memory, Spring Boot 4 + Java 21):

| Service | Port | Endpoint |
|---------|------|---------|
| `NotificationService/` | 8081 | `POST /api/notifications` |
| `AuditService/` | 8082 | varies |
| `SearchService/` | 8084 | varies |
| `SLAService/` | 8085 | varies |

`Homologacion/` is an independent workspace — not part of the main app.

## Commands (run from inside the project directory)

```cmd
# Windows — always use mvnw.cmd
cd Tickets-18

mvnw.cmd spring-boot:run
mvnw.cmd spring-boot:run -Dspring.profiles.active=mysql
mvnw.cmd spring-boot:run -Dspring.profiles.active=supabase

mvnw.cmd test
mvnw.cmd test -Dtest=TicketServiceTest

mvnw.cmd package -DskipTests
mvnw.cmd clean package
```

## Architecture (all Tickets-N projects)

5-layer package structure under `cl.duoc.fullstack.tickets`:

```
controller/   HTTP mapping, @Valid on DTOs, ResponseEntity responses
service/      Business rules, DTO→model mapping, Optional<T> returns
respository/  JPA repositories (or HashMap in base Tickets/)
model/        JPA entities (Lombok POJOs)
dto/          Java records — requests, results, ErrorResponse
```

> **`respository` (missing the first 'o') is intentional.** Always match this spelling when adding new files.

## Key conventions

### Models vs DTOs
- Models (`model/`) are Lombok entities: `@Getter @Setter @NoArgsConstructor @AllArgsConstructor`
- DTOs (`dto/`) are Java `record` types — requests, result projections, and `ErrorResponse`
- Validation annotations (`@NotBlank`, `@Size`) go on DTOs only, never on models

### Endpoints
- Context path: `/ticket-app` — always required in full URLs (`http://localhost:8080/ticket-app/tickets`)
- Paths use kebab-case: `/by-id/{id}`, `/ticket-app`
- Single-entity lookups: service returns `Optional<T>`, controller uses `.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())`
- DELETE: service returns `boolean` → controller returns `204 No Content`

### Error handling
- `IllegalArgumentException` (business validation) → `409 Conflict` in earlier lessons; `400 Bad Request` in Tickets-18+ via `@ControllerAdvice`
- Bean Validation (`@Valid`) → `400 Bad Request` with field error messages joined by `", "`
- Tickets-18+ uses `GlobalExceptionHandler` in `config/` with `@ControllerAdvice @Slf4j`
- `ErrorResponse` is always `public record ErrorResponse(String message) {}`

### Database profiles
- `h2` — default dev profile (`ddl-auto: create-drop`, Flyway disabled)
- `mysql` — XAMPP/local MySQL (env vars: `DB_URL`, `DB_USER`, `DB_PASSWORD`)
- `supabase` — PostgreSQL on Supabase (same env vars)
- Profile activation: `SPRING_PROFILES_ACTIVE=h2` in `.env` (copy from `.env.example`)

### Security (Tickets-16+)
- HTTP Basic Auth, stateless sessions
- Roles: `USER`, `AGENT`, `ADMIN`
- Seed users from `DataInitializer`: `admin/admin123`, `agent1/agent123`, `user1/user123`
- Permissions: `/tickets/**` → USER+AGENT+ADMIN; `/users/**`, `/categories/**`, `/tags/**` → ADMIN only

### FeignClient pattern (Tickets-14+)
- Interface in `client/` annotated with `@FeignClient`
- Always has a `*Fallback` companion `@Component` class
- Timeouts configured in `application.yml` under `feign.client.config.default`
- `@EnableFeignClients` on `TicketsApplication`

### "Homologar A con B"
When asked to "homologar A con B": empty project A completely and replace with the full content of B. This is a total replacement, not a diff/patch.
