# Lección 19 — Checklist y Rúbrica Mínima

## Checklist antes de entregar

### Configuración

- [ ] `pom.xml` incluye `springdoc-openapi-starter-webmvc-ui`
- [ ] La versión de springdoc es compatible con el Spring Boot del proyecto
- [ ] La aplicación arranca sin errores
- [ ] El context path `/ticket-app` se mantiene
- [ ] Swagger UI abre en `/ticket-app/swagger-ui/index.html`
- [ ] OpenAPI JSON abre en `/ticket-app/v3/api-docs`

### Documentación

- [ ] Existe configuración `OpenApiConfig`
- [ ] La API tiene título, descripción y versión
- [ ] `TicketController` usa `@Tag`
- [ ] Endpoints principales usan `@Operation`
- [ ] Endpoints principales documentan códigos HTTP con `@ApiResponse`
- [ ] Parámetros de path usan `@Parameter`
- [ ] DTOs usan `@Schema` con ejemplos
- [ ] Errores 400, 401, 403, 404 y 500 están documentados cuando corresponda

### Microservicios

- [ ] Al menos un microservicio de apoyo también publica Swagger UI
- [ ] Cada microservicio documenta su base path real
- [ ] Los contratos no exponen detalles internos innecesarios

---

## Rúbrica

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Dependencia y Swagger UI funcionando | 20 | URL Swagger y JSON disponibles |
| Configuración OpenAPI correcta | 15 | Título, versión y descripción |
| Endpoints documentados | 25 | `@Operation` y `@ApiResponse` en CRUD |
| DTOs documentados | 15 | `@Schema` con ejemplos claros |
| Manejo de errores documentado | 15 | Códigos HTTP y estructura de error |
| Aplicación a microservicio de apoyo | 10 | Swagger en Notification, Audit, Search o SLA |

**Total: 100 puntos**

---

## Red Flags

- Swagger UI no abre
- Se cambió o eliminó el context path `/ticket-app`
- La documentación muestra rutas que no existen
- Se documentan entidades JPA con datos internos innecesarios
- La aplicación no compila

