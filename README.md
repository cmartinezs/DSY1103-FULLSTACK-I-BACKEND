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

- `Tickets/`: proyecto Spring Boot principal del curso
- `docs/lessons/`: guías y material de clases

## Requisitos

- JDK 21 instalado
- Terminal con permisos de ejecución sobre `mvnw`

## Ejecutar el proyecto

Desde la carpeta `Tickets/`:

```bash
cd /home/cmartinezs/Github/cmartinezs/DSY1103-FULLSTACK-I-BACKEND/Tickets
./mvnw spring-boot:run
```

## Ejecutar pruebas

Desde la carpeta `Tickets/`:

```bash
cd /home/cmartinezs/Github/cmartinezs/DSY1103-FULLSTACK-I-BACKEND/Tickets
./mvnw test
```

## Material de apoyo de la unidad

Documentos clave de la clase de responsabilidades:

- `docs/lessons/04-responsabilities/01_objetivo_y_alcance.md`
- `docs/lessons/04-responsabilities/02_guion_paso_a_paso.md`
- `docs/lessons/04-responsabilities/03_decisiones_rest_y_csr.md`
- `docs/lessons/04-responsabilities/04_checklist_rubrica_minima.md`
- `docs/lessons/04-responsabilities/05_actividad_individual_users.md`

## Estado del repositorio

Este repositorio se usa con foco académico para practicar y evaluar avances por clase.





