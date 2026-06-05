# Ejemplos Prácticos

## Ejemplo 1: Endpoint POST documentado

```java
@Operation(
    summary = "Crear ticket",
    description = "Registra un nuevo ticket de soporte y devuelve el recurso creado"
)
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Ticket creado correctamente"),
    @ApiResponse(responseCode = "400", description = "Request invalido"),
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
})
@PostMapping
public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketCreateRequest request) {
    TicketResponse created = ticketService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

---

## Ejemplo 2: DTO con ejemplos

```java
public class TicketResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "No puedo ingresar al sistema")
    private String title;

    @Schema(example = "OPEN")
    private String status;

    @Schema(example = "HIGH")
    private String priority;
}
```

---

## Ejemplo 3: OpenAPI generado en JSON

Un fragmento esperado de `/ticket-app/v3/api-docs`:

```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "Tickets API",
    "version": "1.0.0"
  },
  "paths": {
    "/tickets": {
      "get": {
        "summary": "Listar tickets"
      },
      "post": {
        "summary": "Crear ticket"
      }
    }
  }
}
```

---

## Ejemplo 4: Documentar seguridad Basic Auth

Si la lección 16 está implementada:

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketsOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Tickets API")
                .version("1.0.0"))
            .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
            .components(new Components()
                .addSecuritySchemes("basicAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")));
    }
}
```

Imports habituales:

```java
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
```

---

## Ejemplo 5: Contrato para NotificationService

```java
@Tag(name = "Notifications")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Operation(summary = "Enviar notificacion")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Notificacion aceptada"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<Void> send(@Valid @RequestBody NotificationRequest request) {
        notificationService.send(request);
        return ResponseEntity.accepted().build();
    }
}
```

---

## Troubleshooting

| Problema | Causa probable | Solución |
|----------|----------------|----------|
| Swagger UI devuelve 404 | URL sin context path | Usar `/ticket-app/swagger-ui/index.html` |
| `/v3/api-docs` no existe | Falta dependencia | Revisar `pom.xml` |
| No aparecen endpoints | Controller no está dentro del package escaneado | Revisar package base |
| DTO muestra campos internos | Se está retornando entidad JPA | Usar DTOs |
| Swagger no permite endpoints protegidos | Falta security scheme | Configurar Basic Auth en OpenAPI |

