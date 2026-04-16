# AGENTS.md

Proyecto educativo DSY1103 - Fullstack I

## Proyectos (snapshots por lección)

| Proyecto | Lección | Descripción |
|----------|--------|------------|
| `Tickets/` | base | In-memory (HashMap), sin BD |
| `Tickets-10/` | 10 | + JPA + H2 |
| `Tickets-11/` | 11 | + MySQL + PostgreSQL |
| `Tickets-12/` | 12 | igual a 11 |

Todos usan: Spring Boot 4.0.5 + Java 21

## Comandos (Windows)

```bash
# Ejecutar desde el directorio del proyecto (ej: Tickets, Tickets-10, etc)
cd Tickets-10
mvnw.cmd spring-boot:run
mvnw.cmd test
mvnw.cmd test -Dtest=ClaseTest
mvnw.cmd package -DskipTests
```

## Endpoints

- Base URL: `http://localhost:8080/ticket-app`
- Rutas: `/tickets`, `/tickets/by-id/{id}`

## Datos

- `Tickets/`: in-memory (se reinicia cada ejecución)
- `Tickets-10+`: requiere H2/MySQL/PostgreSQL configurado

## Warnings

- Paquete `respository/` (sin 'o') es intencional — respetar al crear archivos
- Context path `/ticket-app` — no usar `/tickets` directamente

## Arquitectura

5 capas: controller → service → respository → model/dto
