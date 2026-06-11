# Tickets-21: Leccion 21 - HATEOAS y Documentacion de APIs

## Descripcion

Este proyecto aplica la **Leccion 21: HATEOAS** del curso DSY1103 Fullstack I.

El snapshot parte desde `Tickets-20` y enriquece las respuestas REST con enlaces navegables (`_links`) usando Spring HATEOAS, y actualiza la documentacion OpenAPI para reflejar el nuevo formato.

## Cambios desde Leccion 20

### Dependencia HATEOAS

Se agrego `spring-boot-starter-hateoas` en `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

### TicketLinkAssembler

Nuevo componente `service/TicketLinkAssembler.java` que centraliza la construccion de links:

```java
@Component
public class TicketLinkAssembler {
    public EntityModel<TicketResult> toModel(TicketResult ticket) {
        EntityModel<TicketResult> model = EntityModel.of(ticket);
        model.add(linkTo(methodOn(TicketController.class).getTicketById(ticket.id())).withSelfRel());
        model.add(linkTo(methodOn(TicketController.class).getAllTickets(null)).withRel("all"));
        if ("OPEN".equals(ticket.status())) {
            model.add(linkTo(methodOn(TicketController.class).updateTicketById(ticket.id(), null)).withRel("update"));
            model.add(linkTo(methodOn(TicketController.class).deleteTicketById(ticket.id())).withRel("delete"));
        }
        return model;
    }
}
```

### TicketController actualizado

- `GET /tickets` retorna `CollectionModel<EntityModel<TicketResult>>` con link `self` en la coleccion
- `GET /tickets/by-id/{id}` retorna `EntityModel<TicketResult>` con `_links`
- `POST /tickets` y `PUT /tickets/by-id/{id}` tambien retornan el recurso con `_links`
- Anotaciones `@Operation` actualizadas para documentar HATEOAS en Swagger

## HATEOAS

Las respuestas de tickets incluyen enlaces en `_links`.

- `self`: URL directa al ticket
- `all`: lista completa de tickets
- `update`: disponible solo para tickets con status `OPEN`
- `delete`: disponible solo para tickets con status `OPEN`

### Respuesta individual (`GET /tickets/by-id/{id}`)

```json
{
  "id": 1,
  "title": "No puedo ingresar al sistema",
  "description": "El login rechaza mis credenciales",
  "status": "OPEN",
  "createdAt": "2026-06-04T09:15:00",
  "estimatedResolutionDate": "2026-06-09",
  "_links": {
    "self":   { "href": "http://localhost:8080/ticket-app/tickets/by-id/1" },
    "all":    { "href": "http://localhost:8080/ticket-app/tickets" },
    "update": { "href": "http://localhost:8080/ticket-app/tickets/by-id/1" },
    "delete": { "href": "http://localhost:8080/ticket-app/tickets/by-id/1" }
  }
}
```

### Coleccion (`GET /tickets`)

```json
{
  "_embedded": {
    "ticketResultList": [
      {
        "id": 1,
        "title": "No puedo ingresar al sistema",
        "status": "OPEN",
        "_links": {
          "self": { "href": "http://localhost:8080/ticket-app/tickets/by-id/1" }
        }
      }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8080/ticket-app/tickets" }
  }
}
```

## Ejecutar

```bash
cd projects/Tickets-21
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Con Docker Compose (incluye bases de datos y microservicios):

```bash
docker compose up --build
```

## URLs

```text
Tickets API:   http://localhost:8080/ticket-app
Swagger UI:    http://localhost:8080/ticket-app/swagger-ui/index.html
OpenAPI JSON:  http://localhost:8080/ticket-app/v3/api-docs
```

## Probar HATEOAS

```bash
# Ticket individual con _links
curl http://localhost:8080/ticket-app/tickets/by-id/1

# Coleccion con CollectionModel
curl http://localhost:8080/ticket-app/tickets
```

## Estado

Completado para HATEOAS con `EntityModel`, `CollectionModel`, links condicionales por estado y documentacion OpenAPI actualizada.
