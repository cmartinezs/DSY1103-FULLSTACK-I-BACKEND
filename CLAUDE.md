# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository layout

- `Tickets/` — Spring Boot 4 project (the active backend application)
- `docs/` — Course documentation and supplementary study material (read-only reference)
- `Homologacion/` — Separate workspace (independent, not part of the main app)

All development work happens inside `Tickets/`. Run every command from that directory.

## Commands

```bash
cd Tickets

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=TicketsApplicationTests

# Build without running tests
./mvnw package -DskipTests

# Clean build
./mvnw clean package
```

On Windows use `mvnw.cmd` instead of `./mvnw` if the shell does not support Unix-style scripts.

## Stack

- Java 21 / Spring Boot 4.0.5
- Spring Web MVC (no Spring Data JPA — storage is in-memory)
- Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` on models)
- Jakarta Validation (`@NotBlank`, `@Size`) on DTOs with `@Valid` in controllers
- Spring Boot DevTools (hot reload during development)

## Application configuration

`src/main/resources/application.yml`:
- Port: `8080`
- Context path: `/ticket-app`
- Base URL: `http://localhost:8080/ticket-app`

## Architecture

The project follows a strict 5-layer separation (Controller → Service → Repository → Model / DTO):

| Layer | Package | Responsibility |
|---|---|---|
| `controller/` | `TicketController` | HTTP mapping, `ResponseEntity` responses, `@Valid` on DTOs |
| `service/` | `TicketService` | Business rules (duplicate check, auto-set `status`/`createdAt`/`estimatedResolutionDate`); maps DTO → model |
| `respository/` | `TicketRepository` | In-memory `HashMap`-based store; auto-increments `id` |
| `model/` | `Ticket`, `ErrorResponse` | `Ticket` is a Lombok POJO; `ErrorResponse` is a Java `record` |
| `dto/` | `TicketRequest` | Java `record` with Jakarta Validation annotations (`@NotBlank`, `@Size`) |

**Note:** The package name `respository` (missing the first `o`) is intentional — match it exactly when adding new files.

**Data persistence:** There is no database. `TicketRepository` holds data in a `Map<Long, Ticket>` in memory, pre-seeded with two tickets. All data resets on restart.

**Ticket lifecycle set by `TicketService.create()`:**
- `status` → `"NEW"`
- `createdAt` → `LocalDateTime.now()`
- `estimatedResolutionDate` → `LocalDate.now().plusDays(5)`

## Endpoints

All routes are relative to `/ticket-app/tickets`:

| Method | Path | Description |
|---|---|---|
| `GET` | `/tickets` | List all tickets |
| `POST` | `/tickets` | Create ticket (body validated; rejects duplicate titles) |
| `GET` | `/tickets/by-id/{id}` | Get ticket by id |
| `PUT` | `/tickets/by-id/{id}` | Update ticket by id |
| `DELETE` | `/tickets/by-id/{id}` | Delete ticket by id |

Service methods return `Optional<Ticket>` for single-entity lookups; controllers use `.map()` / `.orElse()` to convert to `ResponseEntity`. DELETE returns `boolean` → `204 No Content`.

## Error handling

- Business validation exceptions (`IllegalArgumentException`) are caught in the controller and returned as `409 Conflict` with an `ErrorResponse` body.
- Bean Validation errors (`@Valid`) are handled by an `@ExceptionHandler(MethodArgumentNotValidException.class)` in the controller, returned as `400 Bad Request` with an `ErrorResponse` body.
- `ErrorResponse` is a Java `record`: `public record ErrorResponse(String message) {}`.

## Conventions

- Endpoint paths use kebab-case (e.g., `/by-id/{id}`).
- Responses for create operations return a plain `String` body (`"Ticket Creado"`), not the created object.
- New domain models should use the same Lombok pattern as `Ticket`.
- DTOs, value objects, and error responses use Java `record` types.
- Validation annotations (`@NotBlank`, `@Size`) go on DTOs, not on domain models.
