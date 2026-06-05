# Guión Paso a Paso

## Paso 1: Verificar Docker

```bash
docker --version
docker compose version
docker run hello-world
```

Si `hello-world` se ejecuta correctamente, Docker está listo.

---

## Paso 2: Crear `docker-compose.yml`

En la raíz del repositorio:

```yaml
services:
  mysql:
    image: mysql:8.4
    container_name: dsy1103-mysql
    restart: unless-stopped
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
    container_name: dsy1103-postgres
    restart: unless-stopped
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

---

## Paso 3: Levantar bases de datos

```bash
docker compose up -d
docker compose ps
```

Ver logs:

```bash
docker compose logs -f mysql
docker compose logs -f postgres
```

---

## Paso 4: Configurar Spring Boot

Para MySQL:

```bash
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=tickets
DB_PASSWORD=tickets123
```

Para PostgreSQL:

```bash
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=localhost
DB_PORT=5432
DB_NAME=tickets_db
DB_USER=tickets
DB_PASSWORD=tickets123
```

---

## Paso 5: Ejecutar proyecto local contra Docker

Desde `proyects/Tickets-11`, `proyects/Tickets-12` o superior:

```bash
mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

---

## Paso 6: Crear Dockerfile para Spring Boot

Archivo `Dockerfile` dentro del proyecto:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Primero empaqueta:

```bash
mvnw.cmd package -DskipTests
```

Luego construye:

```bash
docker build -t tickets-api:lesson-20 .
```

---

## Paso 7: Conectar contenedores por nombre de servicio

Dentro de Compose, no uses `localhost` para llamar a otro contenedor.

Correcto:

```text
jdbc:mysql://mysql:3306/tickets_db
```

Incorrecto dentro de Docker:

```text
jdbc:mysql://localhost:3306/tickets_db
```

`localhost` dentro de un contenedor significa "este mismo contenedor", no tu computador.

---

## Paso 8: Apagar servicios

Detener sin borrar datos:

```bash
docker compose down
```

Detener y borrar volúmenes:

```bash
docker compose down -v
```

Usa `-v` solo si quieres reiniciar las bases de datos desde cero.
