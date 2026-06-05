# Lección 20 — Actividad Individual

## Objetivo

Agregar HATEOAS a la API de Tickets y documentar el nuevo formato de respuesta.

---

## Instrucciones

1. Agrega `spring-boot-starter-hateoas`
2. Crea un assembler para tickets
3. Modifica `GET /tickets/by-id/{id}` para devolver `EntityModel<TicketResponse>`
4. Modifica `GET /tickets` para devolver `CollectionModel`
5. Agrega links:
   - `self`
   - `all`
   - `update` si el ticket está abierto
   - `audit-history` si tienes `AuditService`
6. Actualiza la documentación OpenAPI
7. Prueba desde navegador, Postman o Swagger UI

---

## Entregable

Incluye en tu README:

```md
## HATEOAS

Las respuestas de tickets incluyen enlaces en `_links`.

- `self`: URL del ticket
- `all`: lista completa
- `update`: disponible para tickets abiertos
- `audit-history`: historial del ticket
```

Agrega un ejemplo real de respuesta JSON.

---

## Preguntas para responder

1. ¿Qué significa HATEOAS?
2. ¿Qué problema resuelve?
3. ¿Qué diferencia hay entre OpenAPI y HATEOAS?
4. ¿Qué link no debería aparecer si un ticket está cerrado?
5. ¿Por qué conviene centralizar la creación de links en un assembler?

