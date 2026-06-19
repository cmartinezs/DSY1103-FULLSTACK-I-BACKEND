# Tickets-22: Leccion 22 - Pruebas Unitarias en Microservicios

## Descripcion

Este proyecto aplica la **Leccion 22: Pruebas Unitarias en Proyectos de Microservicios** del curso DSY1103 Fullstack I.

El snapshot parte desde `Tickets-21`, conserva HATEOAS y agrega pruebas unitarias con JUnit 5, Mockito, AssertJ y el modelo Given When Then.

## Cambios desde Leccion 21

### Configuracion de pruebas

Se usa `spring-boot-starter-test`, que incluye JUnit 5, Mockito y AssertJ:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

JaCoCo se ejecuta durante la fase `verify` y exige un mínimo de **85% de cobertura de líneas**. El build falla si no se alcanza el umbral.

Se excluyen del cálculo:

- `dto`
- `model`
- `config`
- `client`
- `repository`
- `respository` (nombre intencional del proyecto)

### Pruebas implementadas

La API principal incluye pruebas de controller con MockMvc y pruebas unitarias de service con Mockito:

- `TicketControllerTest` y `TicketServiceTest`
- `CategoryControllerTest` y `CategoryServiceTest`
- `TagControllerTest` y `TagServiceTest`
- `UserControllerTest` y `UserServiceTest`
- `NotificationClientFallbackTest`

Los microservicios de apoyo tambien separan la logica en controller y service, y prueban ambas capas:

- `AuditControllerTest` y `AuditServiceTest`
- `NotificationControllerTest` y `NotificationServiceTest`
- `SearchControllerTest` y `SearchServiceTest`
- `SlaControllerTest` y `SlaServiceTest`

Las pruebas son unitarias: no levantan Spring, una base de datos ni los microservicios de los puertos 8081, 8082, 8084 o 8085.

## Plan de Pruebas Unitarias

### Objetivo

Validar las reglas principales de `TicketService` y el comportamiento seguro frente a fallas de NotificationService.

### Alcance

- Controllers de Ticket, Category, Tag y User
- Services de Ticket, Category, Tag y User
- Controllers y services de Audit, Notification, Search y SLA
- Validaciones HTTP y reglas de negocio
- Respuestas 200, 201, 204, 400, 404 y 409
- Persistencia in-memory, filtros, busqueda y calculo de SLA
- Envio de notificaciones mediante mock
- Tolerancia a fallas y fallback de NotificationService

### Fuera de alcance

- Base de datos real
- Servicios externos reales
- Pruebas de integracion y end-to-end

### Casos

| ID | Unidad | Escenario | Resultado esperado |
|----|--------|-----------|-------------------|
| UT-01 | TicketService | Crear ticket valido | Guarda y retorna el ticket |
| UT-02 | TicketService | Buscar ID existente | Retorna el ticket |
| UT-03 | TicketService | Buscar ID inexistente | Retorna `Optional.empty()` |
| UT-04 | TicketService | Crear con titulo en blanco | Lanza `IllegalArgumentException` |
| UT-05 | TicketService | Crear ticket | Envia notificacion con los datos esperados |
| UT-06 | TicketService | NotificationService falla | La creacion no falla |
| UT-07 | NotificationClientFallback | Servicio no disponible | Retorna respuestas seguras |
| UT-08 | Ticket controllers | Requests validos e invalidos | Retorna status y JSON esperados |
| UT-09 | Category/Tag/User services | CRUD y duplicados | Ejecuta reglas y repository esperado |
| UT-10 | Audit | Registrar, listar y filtrar eventos | Controller y service responden correctamente |
| UT-11 | Notification | Crear, listar y consultar | Controller y service responden correctamente |
| UT-12 | Search | Indexar, reindexar y buscar | Mantiene y filtra el indice |
| UT-13 | SLA | Iniciar, consultar y cerrar | Calcula plazo y controla estado |

## Ejecutar

```bash
cd projects/Tickets-22
./mvnw clean verify
```

En Windows:

```powershell
.\mvnw.cmd clean verify
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

Una clase especifica:

```bash
./mvnw test -Dtest=TicketServiceTest
```

Microservicios de apoyo:

```bash
cd projects/AuditService && ../Tickets-22/mvnw clean verify
cd projects/NotificationService && ../Tickets-22/mvnw clean verify
cd projects/SearchService && ../Tickets-22/mvnw clean verify
cd projects/SLAService && ../Tickets-22/mvnw clean verify
```

## Evidencia

| Proyecto | Pruebas | Cobertura de lineas | Resultado |
|----------|---------|---------------------|-----------|
| Tickets-22 | 65 | 89.84% | BUILD SUCCESS |
| AuditService | 6 | 90.00% | BUILD SUCCESS |
| NotificationService | 7 | 89.66% | BUILD SUCCESS |
| SearchService | 8 | 93.33% | BUILD SUCCESS |
| SLAService | 9 | 94.00% | BUILD SUCCESS |
| **Total** | **95** | **Todos sobre 85%** | **100% tests aprobados** |

Los reportes HTML se generan en:

```text
target/site/jacoco/index.html
```

## Estado

Completado con pruebas aisladas de controller y service para la API principal y los cuatro microservicios de apoyo.
