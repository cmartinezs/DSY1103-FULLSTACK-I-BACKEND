# Guión Paso a Paso

## Paso 1: Agregar dependencia

En el `pom.xml` del proyecto Tickets agrega:

```xml
<properties>
    <springdoc.version>VERSION_COMPATIBLE</springdoc.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
    </dependency>
</dependencies>
```

> Reemplaza `VERSION_COMPATIBLE` por una versión de springdoc compatible con el Spring Boot usado por tu proyecto.

---

## Paso 2: Verificar context path

En `application.properties` o `application.yml` debe mantenerse:

```properties
server.servlet.context-path=/ticket-app
```

No cambies el context path para que Swagger funcione. La URL final incluirá `/ticket-app`.

---

## Paso 3: Crear configuración OpenAPI

Archivo sugerido:

```text
src/main/java/.../config/OpenApiConfig.java
```

```java
package cl.dsy1103.tickets.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketsOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Tickets API")
                .description("API REST para gestion de tickets de soporte")
                .version("1.0.0")
                .license(new License().name("Uso educativo DSY1103")));
    }
}
```

---

## Paso 4: Documentar el controlador

En `TicketController`:

```java
@Tag(name = "Tickets", description = "Operaciones para gestionar tickets")
@RestController
@RequestMapping("/tickets")
public class TicketController {
    // endpoints
}
```

---

## Paso 5: Documentar endpoints

Ejemplo para listar:

```java
@Operation(summary = "Listar tickets", description = "Obtiene todos los tickets registrados")
@ApiResponse(responseCode = "200", description = "Tickets obtenidos correctamente")
@GetMapping
public ResponseEntity<List<TicketResponse>> findAll() {
    return ResponseEntity.ok(ticketService.findAll());
}
```

Ejemplo para buscar por id:

```java
@Operation(summary = "Buscar ticket por id")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
    @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
})
@GetMapping("/by-id/{id}")
public ResponseEntity<TicketResponse> findById(
        @Parameter(description = "ID del ticket", example = "1")
        @PathVariable Long id) {
    return ResponseEntity.ok(ticketService.findById(id));
}
```

---

## Paso 6: Documentar DTOs

En el DTO de creación:

```java
public class TicketCreateRequest {

    @Schema(description = "Titulo corto del problema", example = "No puedo ingresar al sistema")
    @NotBlank
    private String title;

    @Schema(description = "Descripcion detallada del problema", example = "El login rechaza mis credenciales")
    @NotBlank
    private String description;

    @Schema(description = "Prioridad del ticket", example = "HIGH")
    private String priority;
}
```

---

## Paso 7: Documentar respuestas de error

Si la lección 18 ya está implementada, documenta el DTO de error:

```java
@Schema(description = "Respuesta uniforme de error")
public class ErrorResponse {

    @Schema(example = "2026-06-04T10:30:00")
    private LocalDateTime timestamp;

    @Schema(example = "404")
    private int status;

    @Schema(example = "Ticket no encontrado")
    private String message;

    @Schema(example = "/tickets/by-id/99")
    private String path;
}
```

---

## Paso 8: Ejecutar y probar

En Windows:

```bash
mvnw.cmd spring-boot:run
```

Abrir:

```text
http://localhost:8080/ticket-app/swagger-ui/index.html
```

También puedes revisar el JSON:

```text
http://localhost:8080/ticket-app/v3/api-docs
```

---

## Paso 9: Repetir en microservicios

Aplica el mismo patrón a:

| Servicio | URL Swagger esperada |
|----------|----------------------|
| NotificationService | `http://localhost:8081/swagger-ui/index.html` |
| AuditService | `http://localhost:8082/swagger-ui/index.html` |
| SearchService | `http://localhost:8084/swagger-ui/index.html` |
| SLAService | `http://localhost:8085/swagger-ui/index.html` |

> Si esos servicios no tienen context path, no agregues `/ticket-app`.

