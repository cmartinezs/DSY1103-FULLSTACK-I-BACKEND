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

Crea `docker-compose.yml` con:

- MySQL
- PostgreSQL
- Volúmenes persistentes
- Puertos publicados
- Variables de entorno

Ejecuta:

```bash
docker compose up -d
docker compose ps
```

---

### Parte 3: Conexión con Spring Boot

Conecta un proyecto `Tickets-11` o superior a una base de datos Docker.

Prueba:

```text
GET http://localhost:8080/ticket-app/tickets
```

---

### Parte 4: Dockerfile

Crea un Dockerfile para un proyecto Spring Boot y construye la imagen:

```bash
docker build -t tickets-api:lesson-20 .
```

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

