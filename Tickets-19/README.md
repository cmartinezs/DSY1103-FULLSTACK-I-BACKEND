# Tickets-19: Lección 19 - OpenAPI Specification (OAS)

## Descripción

Este proyecto implementa la **Lección 19: Microservicios con OpenAPI Specification (OAS)** del curso DSY1103 Fullstack I.

Documentación de la API REST usando OpenAPI y Swagger UI.

## Cambios desde Lección 18

### Nueva dependencia: springdoc-openapi
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

### Nuevo archivo: OpenApiConfig
```java
@Configuration
public class OpenApiConfig {
```

### Documentacion agregada
- Metadatos de la API: titulo, descripcion y version
- Seguridad Basic Auth en OpenAPI
- Tags para controladores
- Operaciones principales con `@Operation`
- Respuestas HTTP con `@ApiResponse`
- DTOs documentados con `@Schema`
- Modelo de error documentado

## URLs

Con la aplicacion ejecutando:

```text
http://localhost:8080/ticket-app/swagger-ui/index.html
http://localhost:8080/ticket-app/v3/api-docs
```

## Estado

✅ Completado
