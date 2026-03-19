# DSY1103 - Fullstack I Backend

Repositorio del curso **Fullstack I** orientado al desarrollo backend con **Spring Boot 4** y **Java 21**.

## Objetivo

Construir APIs REST con buenas prácticas desde el inicio:

- separación por capas (CSR): `Controller`, `Service`, `Repository`, `Model`
- uso correcto de métodos HTTP y códigos de respuesta
- diseño de endpoints versionados (por ejemplo `/api/v1/...`)
- configuración base del proyecto (`application.properties`, `banner.txt`, puerto y `context-path`)

## Stack técnico (verificado en el proyecto)

- Java 21
- Spring Boot `4.0.3`
- Maven Wrapper (`mvnw`)
- Spring Web MVC
- Pruebas con Spring Boot Test

## Estructura del repositorio

- [`Tickets/`](./Tickets/README.md): proyecto Spring Boot principal del curso
- [`docs/`](./docs/README.md): documentación del curso (lecciones y material de apoyo)

## Requisitos

- JDK 21 instalado
- Terminal con permisos de ejecución sobre `mvnw`

## Ejecutar el proyecto

Desde la carpeta `Tickets/`:

```bash
cd Tickets
./mvnw spring-boot:run
```

## Ejecutar pruebas

Desde la carpeta `Tickets/`:

```bash
cd Tickets
./mvnw test
```

## Material de apoyo de la unidad

Toda la documentación del curso está centralizada en [`docs/`](./docs/README.md), incluyendo lecciones y material complementario.

> 🗺️ ¿No sabes qué estudiar primero? Revisa el **[Roadmap de estudio](./docs/roadmap.md)** — organiza todos los extras según el tiempo disponible.

## Estado del repositorio

Este repositorio se usa con foco académico para practicar y evaluar avances por clase.





