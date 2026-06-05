# Tickets-20: Leccion 20 - Docker, Compose y Docker Desktop

## Descripcion

Este proyecto aplica la **Leccion 20: Docker, Docker Compose y Docker Desktop** del curso DSY1103 Fullstack I.

El snapshot parte desde `Tickets-19` y agrega un ambiente reproducible con contenedores para:

- Tickets API (`8080`)
- MySQL (`3306`)
- PostgreSQL (`5432`)
- NotificationService (`8081`)
- AuditService (`8082`)
- SearchService (`8084`)
- SLAService (`8085`)

## Cambios desde Leccion 19

### Dockerfile para Tickets

`Dockerfile` usa build multi-stage:

```dockerfile
FROM eclipse-temurin:21-jdk AS build
RUN chmod +x mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /workspace/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose completo

`docker-compose.yml` levanta base de datos, API principal y microservicios auxiliares.

MySQL monta las migraciones existentes como scripts de inicializacion:

```yaml
./src/main/resources/db/migration:/docker-entrypoint-initdb.d:ro
```

Esto permite que `tickets-api` mantenga `ddl-auto: validate` y aun asi arranque contra una base nueva creada por Compose.

Dentro de Docker, la comunicacion entre contenedores usa nombres de servicio:

```text
mysql
notification-service
audit-service
search-service
sla-service
```

No se usa `localhost` entre contenedores.

### Variables de entorno

`.env.example` documenta credenciales locales y URLs internas:

```dotenv
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=mysql
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=tickets
DB_PASSWORD=tickets123

NOTIFICATION_SERVICE_URL=http://notification-service:8081
AUDIT_SERVICE_URL=http://audit-service:8082
SEARCH_SERVICE_URL=http://search-service:8084
SLA_SERVICE_URL=http://sla-service:8085
```

### Microservicios auxiliares

Se agrego `Dockerfile` y `.dockerignore` en:

- `projects/NotificationService`
- `projects/AuditService`
- `projects/SearchService`
- `projects/SLAService`

## Requisitos

- Docker Desktop en Windows/macOS, o Docker Engine en Linux
- Docker Compose v2

Validar:

```bash
docker --version
docker compose version
docker run hello-world
```

## Ejecutar todo con Docker Compose

Desde este directorio:

```bash
cd projects/Tickets-20
docker compose up --build
```

En segundo plano:

```bash
docker compose up --build -d
docker compose ps
```

Ver logs:

```bash
docker compose logs -f tickets-api
docker compose logs -f mysql
docker compose logs -f notification-service
docker compose logs -f audit-service
docker compose logs -f search-service
docker compose logs -f sla-service
```

Detener sin borrar datos:

```bash
docker compose down
```

Detener y borrar volumenes de base de datos:

```bash
docker compose down -v
```

Usa `down -v` cuando cambies scripts SQL o si MySQL quedo inicializado sin tablas durante pruebas anteriores.

## Ejecutar Java local contra bases Docker

Si quieres correr Tickets desde el IDE o terminal local, levanta solo las bases:

```bash
docker compose up -d mysql postgres
```

Luego ejecuta Tickets con variables para MySQL local:

```bash
SPRING_PROFILES_ACTIVE=mysql \
DB_HOST=localhost \
DB_PORT=3306 \
DB_NAME=tickets_db \
DB_USER=tickets \
DB_PASSWORD=tickets123 \
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="mysql"
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="tickets_db"
$env:DB_USER="tickets"
$env:DB_PASSWORD="tickets123"
.\mvnw.cmd spring-boot:run
```

Para usar PostgreSQL local desde Docker Compose:

```bash
SPRING_PROFILES_ACTIVE=supabase \
DB_HOST=localhost \
DB_PORT=5432 \
DB_NAME=tickets_db \
DB_USER=tickets \
DB_PASSWORD=tickets123 \
./mvnw spring-boot:run
```

Dentro de Compose, el equivalente es cambiar estas variables en `.env`:

```dotenv
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=postgres
DB_PORT=5432
```

## URLs

Con Compose ejecutando:

```text
Tickets API:          http://localhost:8080/ticket-app
Swagger UI:           http://localhost:8080/ticket-app/swagger-ui/index.html
OpenAPI JSON:         http://localhost:8080/ticket-app/v3/api-docs
NotificationService:  http://localhost:8081/api/notifications
AuditService:         http://localhost:8082/api/audit
SearchService:        http://localhost:8084/api/search
SLAService:           http://localhost:8085/api/sla
```

## Probar rapidamente

```bash
curl http://localhost:8080/ticket-app/tickets
curl http://localhost:8081/api/notifications
curl http://localhost:8082/api/audit
curl http://localhost:8084/api/search
curl http://localhost:8085/api/sla
```

## Troubleshooting

| Problema | Causa probable | Solucion |
|----------|----------------|----------|
| `docker: command not found` | Docker no instalado o terminal sin reiniciar | Instalar Docker Desktop y reabrir terminal |
| `Cannot connect to Docker daemon` | Docker Engine detenido | Abrir Docker Desktop |
| Puerto `3306` ocupado | MySQL local activo | Detener MySQL local o cambiar el puerto en Compose |
| Tickets no conecta a DB | Host incorrecto | En Docker usar `mysql`; local usar `localhost` |
| Cambios de schema no aparecen | Volumen persistente con datos viejos | `docker compose down -v` |
| Build falla por permisos | `mvnw` sin permiso en Linux/macOS | El Dockerfile ejecuta `chmod +x mvnw` |

## Estado

Completado para Docker Compose con Tickets API, bases de datos y microservicios auxiliares.
