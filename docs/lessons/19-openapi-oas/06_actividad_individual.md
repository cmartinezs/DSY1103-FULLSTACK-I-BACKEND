# Lección 19 — Actividad Individual

## Objetivo

Documentar la API de Tickets y un microservicio de apoyo usando OpenAPI Specification y Swagger UI.

---

## Instrucciones

### Parte 1: Tickets API

1. Agrega la dependencia de springdoc al `pom.xml`
2. Crea `OpenApiConfig`
3. Documenta `TicketController`
4. Documenta los DTOs principales
5. Ejecuta la aplicación
6. Verifica Swagger UI en:

```text
http://localhost:8080/ticket-app/swagger-ui/index.html
```

### Parte 2: Microservicio de apoyo

Elige uno:

- `NotificationService`
- `AuditService`
- `SearchService`
- `SLAService`

Agrega OpenAPI y documenta al menos:

- Un endpoint `POST`
- Un endpoint `GET`, si existe
- El DTO de request
- Una respuesta de error

---

## Entregable

Agrega al README de tu proyecto una sección breve:

```md
## Documentacion OpenAPI

- Tickets API: http://localhost:8080/ticket-app/swagger-ui/index.html
- NotificationService: http://localhost:8081/swagger-ui/index.html
```

Incluye capturas o evidencia de:

- Swagger UI cargando
- Endpoint probado desde Swagger
- JSON OpenAPI disponible

---

## Preguntas para responder

1. ¿Qué diferencia hay entre OpenAPI y Swagger?
2. ¿Por qué OpenAPI ayuda en microservicios?
3. ¿Qué problemas aparecen si la documentación no coincide con el código?
4. ¿Por qué conviene documentar DTOs y no entidades JPA?

