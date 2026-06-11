# Ejemplos Prácticos

## Ejemplo 1: Compose solo para bases de datos

Archivo: `compose.yml`

```yaml
services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_DATABASE: tickets_db
      MYSQL_USER: tickets
      MYSQL_PASSWORD: tickets123
      MYSQL_ROOT_PASSWORD: root123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: tickets_db
      POSTGRES_USER: tickets
      POSTGRES_PASSWORD: tickets123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  mysql_data:
  postgres_data:
```

Este enfoque es ideal para clases: Java corre localmente y las bases de datos corren en Docker.

---

## Ejemplo 2: Dockerfile multi-stage para Spring Boot

```dockerfile
FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace
COPY . .

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

RUN addgroup -S authgroup && adduser -S authuser -G authgroup
USER authuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

La etapa `build` compila con JDK; la etapa final usa JRE y corre como usuario no-root (`authuser`).

En Windows, si usas `mvnw.cmd`, puedes empaquetar fuera de Docker y usar el Dockerfile simple de la guía.

---

## Ejemplo 3: Compose con Tickets API y MySQL

Archivo: `compose.yml`

```yaml
services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_DATABASE: tickets_db
      MYSQL_USER: tickets
      MYSQL_PASSWORD: tickets123
      MYSQL_ROOT_PASSWORD: root123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  tickets-api:
    build:
      context: ./proyects/Tickets-19
    depends_on:
      - mysql
    environment:
      SPRING_PROFILES_ACTIVE: mysql
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: tickets_db
      DB_USER: tickets
      DB_PASSWORD: tickets123
    ports:
      - "8080:8080"

volumes:
  mysql_data:
```

Dentro de Docker, `DB_HOST` debe ser `mysql`, no `localhost`.

---

## Ejemplo 4: Compose para microservicios de apoyo

Archivo: `compose.yml`

```yaml
services:
  notification-service:
    build:
      context: ./proyects/NotificationService
    ports:
      - "8081:8081"

  audit-service:
    build:
      context: ./proyects/AuditService
    ports:
      - "8082:8082"

  search-service:
    build:
      context: ./proyects/SearchService
    ports:
      - "8084:8084"

  sla-service:
    build:
      context: ./proyects/SLAService
    ports:
      - "8085:8085"
```

---

## Ejemplo 5: `.env.example`

```dotenv
MYSQL_DATABASE=tickets_db
MYSQL_USER=tickets
MYSQL_PASSWORD=tickets123
MYSQL_ROOT_PASSWORD=root123

POSTGRES_DB=tickets_db
POSTGRES_USER=tickets
POSTGRES_PASSWORD=tickets123
```

Y en Compose:

```yaml
environment:
  MYSQL_DATABASE: ${MYSQL_DATABASE}
  MYSQL_USER: ${MYSQL_USER}
  MYSQL_PASSWORD: ${MYSQL_PASSWORD}
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
```

---

## Troubleshooting

| Problema | Causa probable | Solución |
|----------|----------------|----------|
| `docker: command not found` | Docker no instalado o terminal no reiniciada | Instalar Docker y reabrir terminal |
| `Cannot connect to Docker daemon` | Engine detenido | Abrir Docker Desktop o iniciar servicio Docker |
| Puerto 3306 ocupado | MySQL local activo | Detener MySQL local o cambiar puerto |
| Spring no conecta a DB | Host incorrecto | Usar `localhost` fuera de Docker, nombre de servicio dentro |
| Datos viejos aparecen | Volumen persistente | `docker compose down -v` |
| Build falla por permisos en Linux/macOS | `mvnw` sin permiso | `chmod +x mvnw` |

