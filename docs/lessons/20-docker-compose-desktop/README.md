# Lección 20 — Docker, Docker Compose y Docker Desktop

**Aprende a preparar ambientes reproducibles para proyectos Spring Boot usando Docker, Docker Compose y Docker Desktop en Windows, Linux y macOS.**

---

## Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. Docker, Compose y Desktop** | 20 min | Conceptos base |
| **03. Guión Paso a Paso** | 30 min | Implementación práctica |
| **04. Configuración por Sistema Operativo** | 20 min | Windows, Linux y macOS |
| **05. Ejemplos Prácticos** | 25 min | Dockerfile y compose |
| **06. Checklist** | 5 min | Verificación |
| **07. Actividad Individual** | - | Tu tarea |

---

## El problema

En backend, muchos errores no vienen del código Java sino del ambiente:

- MySQL no está instalado
- PostgreSQL usa otro puerto
- Una versión de Java no coincide
- Un compañero tiene Windows y otro Linux
- Un microservicio no sabe cómo conectarse a otro

Docker permite describir el ambiente como código.

---

## Quick Start

### Levantar base de datos con Docker Compose

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

volumes:
  mysql_data:
```

Ejecutar:

```bash
docker compose up -d
docker compose ps
docker compose logs -f mysql
```

---

## Lo que construirás

1. Instalar y validar Docker según tu sistema operativo
2. Crear `docker-compose.yml` para MySQL y PostgreSQL
3. Crear un `Dockerfile` para un proyecto Spring Boot
4. Levantar servicios de apoyo con Compose
5. Configurar variables de entorno para perfiles Spring
6. Documentar comandos de uso en README

---

## Lecturas recomendadas

- Lección 11: Configuración de Bases de Datos
- Lección 14: Comunicación entre Microservicios
- Lección 15: Migraciones con Flyway
- Lección 19: OpenAPI Specification

---

*Lección 20 - [← Volver al Índice](../INDICE_COMPLETO.md)*
