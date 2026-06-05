# Guión Paso a Paso

## Paso 1: Agregar dependencia

En `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

---

## Paso 2: Elegir la estrategia

Hay dos formas comunes:

| Estrategia | Cuándo usar |
|-----------|-------------|
| DTO extiende `RepresentationModel` | Simple y directo para el curso |
| `EntityModel<T>` | Útil si no quieres modificar el DTO |

En esta lección usaremos `EntityModel<TicketResponse>` para mantener los DTOs limpios.

---

## Paso 3: Crear helper para links

Archivo sugerido:

```text
src/main/java/.../service/TicketLinkAssembler.java
```

```java
@Component
public class TicketLinkAssembler {

    public EntityModel<TicketResponse> toModel(TicketResponse ticket) {
        EntityModel<TicketResponse> model = EntityModel.of(ticket);

        model.add(linkTo(methodOn(TicketController.class)
            .findById(ticket.getId())).withSelfRel());

        model.add(linkTo(methodOn(TicketController.class)
            .findAll()).withRel("all"));

        model.add(linkTo(methodOn(TicketController.class)
            .update(ticket.getId(), null)).withRel("update"));

        return model;
    }
}
```

Imports habituales:

```java
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
```

---

## Paso 4: Ajustar GET por id

Antes:

```java
@GetMapping("/by-id/{id}")
public ResponseEntity<TicketResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(ticketService.findById(id));
}
```

Después:

```java
@GetMapping("/by-id/{id}")
public ResponseEntity<EntityModel<TicketResponse>> findById(@PathVariable Long id) {
    TicketResponse ticket = ticketService.findById(id);
    return ResponseEntity.ok(ticketLinkAssembler.toModel(ticket));
}
```

---

## Paso 5: Ajustar GET lista

```java
@GetMapping
public ResponseEntity<CollectionModel<EntityModel<TicketResponse>>> findAll() {
    List<EntityModel<TicketResponse>> tickets = ticketService.findAll().stream()
        .map(ticketLinkAssembler::toModel)
        .toList();

    CollectionModel<EntityModel<TicketResponse>> collection = CollectionModel.of(tickets);
    collection.add(linkTo(methodOn(TicketController.class).findAll()).withSelfRel());

    return ResponseEntity.ok(collection);
}
```

---

## Paso 6: Agregar links condicionales

No todos los tickets deben tener las mismas acciones.

```java
if ("OPEN".equals(ticket.getStatus())) {
    model.add(linkTo(methodOn(TicketController.class)
        .update(ticket.getId(), null)).withRel("update"));
}
```

Ejemplo de regla:

- Ticket `OPEN`: puede actualizarse
- Ticket `CLOSED`: no muestra link `update`
- Usuario `ADMIN`: puede ver link `delete`

---

## Paso 7: Documentar en OpenAPI

En la descripción del endpoint:

```java
@Operation(
    summary = "Buscar ticket por id",
    description = "Devuelve el ticket con enlaces HATEOAS en _links"
)
```

Si quieres documentar explícitamente la estructura, crea un DTO de ejemplo:

```java
public class TicketHateoasExample {
    private TicketResponse content;
    private Map<String, LinkExample> links;
}
```

Para el curso basta con indicar en la descripción que la respuesta incluye `_links`.

---

## Paso 8: Probar

Ejecuta:

```bash
mvnw.cmd spring-boot:run
```

Consulta:

```text
GET http://localhost:8080/ticket-app/tickets/by-id/1
```

Debe aparecer `_links` en la respuesta.

