# Ejemplos Prácticos

## Ejemplo 1: Respuesta individual

```json
{
  "id": 1,
  "title": "No puedo ingresar",
  "description": "El sistema rechaza mis credenciales",
  "status": "OPEN",
  "_links": {
    "self": {
      "href": "http://localhost:8080/ticket-app/tickets/by-id/1"
    },
    "all": {
      "href": "http://localhost:8080/ticket-app/tickets"
    },
    "update": {
      "href": "http://localhost:8080/ticket-app/tickets/by-id/1"
    }
  }
}
```

---

## Ejemplo 2: Lista con CollectionModel

```json
{
  "_embedded": {
    "ticketResponseList": [
      {
        "id": 1,
        "title": "No puedo ingresar",
        "status": "OPEN",
        "_links": {
          "self": {
            "href": "http://localhost:8080/ticket-app/tickets/by-id/1"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/ticket-app/tickets"
    }
  }
}
```

---

## Ejemplo 3: Assembler completo

```java
@Component
public class TicketLinkAssembler {

    public EntityModel<TicketResponse> toModel(TicketResponse ticket) {
        EntityModel<TicketResponse> model = EntityModel.of(ticket);

        model.add(linkTo(methodOn(TicketController.class)
            .findById(ticket.getId())).withSelfRel());

        model.add(linkTo(methodOn(TicketController.class)
            .findAll()).withRel("all"));

        if ("OPEN".equals(ticket.getStatus())) {
            model.add(linkTo(methodOn(TicketController.class)
                .update(ticket.getId(), null)).withRel("update"));
        }

        return model;
    }
}
```

---

## Ejemplo 4: Link hacia auditoría

Si integraste `AuditService`:

```java
model.add(Link.of(
    "http://localhost:8082/api/audit/ticket/" + ticket.getId(),
    "audit-history"
));
```

Esto permite que el consumidor encuentre el historial del ticket.

---

## Ejemplo 5: Documentación con OpenAPI

```java
@Operation(
    summary = "Listar tickets",
    description = "Obtiene todos los tickets. Cada item incluye enlaces HATEOAS en _links."
)
@ApiResponse(responseCode = "200", description = "Tickets obtenidos correctamente")
@GetMapping
public ResponseEntity<CollectionModel<EntityModel<TicketResponse>>> findAll() {
    // ...
}
```

---

## Troubleshooting

| Problema | Causa probable | Solución |
|----------|----------------|----------|
| No aparece `_links` | Se retorna DTO directo | Retornar `EntityModel` o `CollectionModel` |
| Links apuntan sin `/ticket-app` | Context path mal configurado o test aislado | Probar con app real levantada |
| Error en `methodOn` | Firma del método no coincide | Ajustar parámetros del controller |
| JSON cambia demasiado | HATEOAS usa formato HAL | Avisar al consumidor y documentar |
| Swagger se ve confuso | Tipo generico complejo | Agregar descripción clara en `@Operation` |

