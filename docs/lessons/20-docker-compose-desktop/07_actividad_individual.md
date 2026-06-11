# Lección 20 — Actividad Individual

## Objetivo

Preparar un ambiente Docker para ejecutar bases de datos y, opcionalmente, servicios Spring Boot del curso.

---

## Instrucciones

### Parte 1: Verificación de instalación

Ejecuta:

```bash
docker --version
docker compose version
docker run hello-world
```

Guarda evidencia de los comandos.

---

### Parte 2: Compose para bases de datos

Crea `compose.yml` (no `docker-compose.yml`) con:

- MySQL
- PostgreSQL
- Volúmenes persistentes
- Puertos publicados
- Variables de entorno

> No incluyas `version:` al inicio del archivo.

Ejecuta:

```bash
docker compose up -d
docker compose ps
```

---

### Parte 3: Conexión con Spring Boot

Conecta un proyecto `proyects/Tickets-11` o superior a una base de datos Docker.

Prueba:

```text
GET http://localhost:8080/ticket-app/tickets
```

---

### Parte 4: Dockerfile

Crea un Dockerfile para un proyecto Spring Boot que incluya usuario no-root:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

RUN addgroup -S authgroup && adduser -S authuser -G authgroup
USER authuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Construye la imagen:

```bash
docker build -t tickets-api:lesson-20 .
```

Verifica que el proceso no corre como root:

```bash
docker run --rm tickets-api:lesson-20 whoami
```

El resultado debe ser `authuser`.

---

## Entregable

Incluye en README:

```md
## Docker

### Requisitos
- Docker
- Docker Compose

### Comandos
docker compose up -d
docker compose ps
docker compose logs -f
docker compose down
docker compose down -v

### Sistemas operativos
- Windows: Docker Desktop + WSL2
- Linux: Docker Engine
- macOS: Docker Desktop
```

---

## Preguntas para responder

1. ¿Qué problema resuelve Docker?
2. ¿Qué diferencia hay entre imagen y contenedor?
3. ¿Para qué sirve Docker Compose?
4. ¿Por qué dentro de Docker no se usa `localhost` para llamar a otro contenedor?
5. ¿Qué hace `docker compose down -v`?
6. ¿Por qué se prefiere `compose.yml` sobre `docker-compose.yml`?
7. ¿Qué riesgo de seguridad existe al correr un proceso como `root` dentro de un contenedor?
8. ¿Qué diferencia hay entre `-S` (sistema) y un usuario normal en el contexto de `adduser`?
