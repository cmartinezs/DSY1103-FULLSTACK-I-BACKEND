# AGENTS.md

Proyecto educativo DSY1103 - Fullstack I

## Proyectos (snapshots por lección)

| Proyecto | Lección | Descripción |
|----------|--------|------------|
| `proyects/Tickets/` | base | In-memory (HashMap), sin BD |
| `proyects/Tickets-10/` | 10 | + JPA + H2 |
| `proyects/Tickets-11/` | 11 | + MySQL + PostgreSQL |
| `proyects/Tickets-12/` | 12 | igual a 11 |

Todos usan: Spring Boot 4.0.5 + Java 21

## Microservicios de apoyo

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `proyects/NotificationService/` | 8081 | Envío de notificaciones (in-memory) |
| `proyects/AuditService/` | 8082 | Registro de auditoría de tickets (in-memory) |
| `proyects/SearchService/` | 8084 | Indexación y búsqueda full-text de tickets (in-memory) |
| `proyects/SLAService/` | 8085 | Control de tiempos de resolución / SLA (in-memory) |

Todos los microservicios usan Spring Boot 4.0.5 + Java 21, sin base de datos.

## Comandos (Windows)

```bash
# Ejecutar desde el directorio del proyecto (ej: proyects/Tickets, proyects/Tickets-10, etc)
cd proyects/Tickets-10
mvnw.cmd spring-boot:run
mvnw.cmd test
mvnw.cmd test -Dtest=ClaseTest
mvnw.cmd package -DskipTests
```

## Endpoints

- Base URL: `http://localhost:8080/ticket-app`
- Rutas: `/tickets`, `/tickets/by-id/{id}`

## Datos

- `proyects/Tickets/`: in-memory (se reinicia cada ejecución)
- `proyects/Tickets-10+`: requiere H2/MySQL/PostgreSQL configurado

## Warnings

- Paquete `respository/` (sin 'o') es intencional — respetar al crear archivos
- Context path `/ticket-app` — no usar `/tickets` directamente

## Arquitectura

5 capas: controller → service → respository → model/dto
