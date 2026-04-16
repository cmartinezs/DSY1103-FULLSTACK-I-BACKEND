# 📖 Documentación — DSY1103 Fullstack I Backend

Esta carpeta centraliza toda la documentación del curso **DSY1103 - Fullstack I (Backend)**.  
Está organizada en dos grandes secciones: las **lecciones** de clase y el **material de apoyo** complementario.

---

## 🗂️ Estructura

```
docs/
├── lessons/        # Contenido directo de cada lección
│   ├── 01-web-and-http/
│   ├── 02-apis-and-rest/
│   ├── 03-first-api/
│   ├── 04-responsabilities/
│   └── 05-post/
└── extras/         # Material de apoyo autónomo
    ├── ejercicios/
    ├── env-variables/
    ├── git-github/
    ├── gitflow/
    ├── java-para-spring-boot/
    ├── java-poo/
    ├── json/
    ├── logica-proposicional/
    ├── lombok/
    ├── markdown/
    ├── matematicas-para-programar/
    ├── maven/
    ├── richardson-maturity-model/
    ├── solid/
    ├── terminal/
    ├── tips-de-programacion/
    └── yaml/
```

---

## 📚 Lecciones

Documentación generada por cada lección del curso. Incluye objetivos, guiones, decisiones de diseño, rúbricas y actividades individuales.

| # | Lección | Descripción | Documentos |
|---|---------|-------------|------------|
| 01 | [La Web y HTTP](./lessons/01-web-and-http/) | Fundamentos de la Web, modelo cliente-servidor, DNS, HTTP, request, response, métodos y códigos de estado | [Objetivo y alcance](./lessons/01-web-and-http/01_objetivo_y_alcance.md) · [La Web y HTTP](./lessons/01-web-and-http/02_la_web_y_http.md) · [Request, Response y Códigos](./lessons/01-web-and-http/03_request_response_y_codigos.md) · [Rúbrica mínima](./lessons/01-web-and-http/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/01-web-and-http/05_actividad_individual.md) |
| 02 | [APIs y REST](./lessons/02-apis-and-rest/) | Frontend vs Backend, monolito vs microservicios, qué es una API, principios REST y buenas prácticas de diseño | [Objetivo y alcance](./lessons/02-apis-and-rest/01_objetivo_y_alcance.md) · [Arquitecturas y roles](./lessons/02-apis-and-rest/02_arquitecturas_y_roles.md) · [APIs, REST y buenas prácticas](./lessons/02-apis-and-rest/03_apis_rest_y_buenas_practicas.md) · [Rúbrica mínima](./lessons/02-apis-and-rest/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/02-apis-and-rest/05_actividad_individual.md) |
| 03 | [Tu primera API](./lessons/03-first-api/) | Crear un proyecto Spring Boot desde cero con IntelliJ, construir `GET /greetings` y entender el ciclo HTTP completo | [Objetivo y alcance](./lessons/03-first-api/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/03-first-api/02_guion_paso_a_paso.md) · [Cómo funciona HTTP](./lessons/03-first-api/03_como_funciona_http.md) · [Rúbrica mínima](./lessons/03-first-api/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/03-first-api/05_actividad_individual_greetings.md) |
| 04 | [Separación de responsabilidades](./lessons/04-responsabilities/) | Patrón Controller → Service → Repository (CSR) aplicado a una API REST de tickets | [Objetivo y alcance](./lessons/04-responsabilities/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/04-responsabilities/02_guion_paso_a_paso.md) · [Decisiones REST y CSR](./lessons/04-responsabilities/03_decisiones_rest_y_csr.md) · [Rúbrica mínima](./lessons/04-responsabilities/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/04-responsabilities/05_actividad_individual_users.md) |
| 05 | [POST y creación de recursos](./lessons/05-post/) | Recibir datos del cliente con `@RequestBody`, lógica de negocio en el `Service` (validación, estado y fechas) y respuesta `201 Created` con `ResponseEntity` | [Objetivo y alcance](./lessons/05-post/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/05-post/02_guion_paso_a_paso.md) · [Decisiones POST y HTTP](./lessons/05-post/03_decisiones_post_y_http.md) · [Rúbrica mínima](./lessons/05-post/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/05-post/05_actividad_individual_categories.md) |
| 06 | [CRUD completo](./lessons/06-crud/) | Implementar `GET /id`, `PUT /id` y `DELETE /id`; idempotencia y reglas REST para métodos de escritura | [Objetivo y alcance](./lessons/06-crud/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/06-crud/02_guion_paso_a_paso.md) · [Reglas REST e idempotencia](./lessons/06-crud/03_reglas_rest_e_idempotencia.md) · [Rúbrica mínima](./lessons/06-crud/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/06-crud/05_actividad_individual.md) |
| 07 | [Manejo de errores](./lessons/07-errors/) | Respuestas de error estructuradas con `ErrorResponse`, `@ExceptionHandler` y códigos HTTP correctos | [Objetivo y alcance](./lessons/07-errors/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/07-errors/02_guion_paso_a_paso.md) · [Manejo global vs local](./lessons/07-errors/03_manejo_global_vs_local.md) · [Rúbrica mínima](./lessons/07-errors/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/07-errors/05_actividad_individual.md) |
| 08 | [DTOs](./lessons/08-dto/) | Separar el modelo de dominio del contrato de la API con `TicketRequest`; `@Valid` y validación declarativa | [Objetivo y alcance](./lessons/08-dto/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/08-dto/02_guion_paso_a_paso.md) · [Por qué DTO](./lessons/08-dto/03_por_que_dto.md) · [Rúbrica mínima](./lessons/08-dto/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/08-dto/05_actividad_individual.md) |
| 09 | [Repository con Map](./lessons/09-map-repository/) | Refactorizar a `Map<Long, Ticket>` para acceso O(1); filtro `?status=` con `@RequestParam` | [Objetivo y alcance](./lessons/09-map-repository/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/09-map-repository/02_guion_paso_a_paso.md) · [Map vs List y CSR](./lessons/09-map-repository/03_map_vs_list_y_csr.md) · [Rúbrica mínima](./lessons/09-map-repository/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/09-map-repository/05_actividad_individual.md) |
| 10 | [JPA y ORM](./lessons/10-jpa-intro/) | Migrar de almacenamiento en memoria a base de datos real con `@Entity`, `@Id`, `@GeneratedValue` y `JpaRepository` | [Objetivo y alcance](./lessons/10-jpa-intro/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/10-jpa-intro/02_guion_paso_a_paso.md) · [JPA y ORM](./lessons/10-jpa-intro/03_jpa_y_orm.md) · [Rúbrica mínima](./lessons/10-jpa-intro/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/10-jpa-intro/05_actividad_individual.md) |
| 11 | [Configuración de base de datos](./lessons/11-database-config/) | Conectar a MySQL local (XAMPP) y a Supabase (PostgreSQL en la nube); opciones de `ddl-auto` | [Objetivo y alcance](./lessons/11-database-config/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/11-database-config/02_guion_paso_a_paso.md) · [MySQL vs PostgreSQL](./lessons/11-database-config/03_mysql_vs_postgresql.md) · [Rúbrica mínima](./lessons/11-database-config/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/11-database-config/05_actividad_individual.md) |
| 12 | [Relaciones entre entidades](./lessons/12-relations/) | `@ManyToOne`, `@OneToMany`, `@JoinColumn` y `@Column`; entidad `User` con usuario creador y asignado en `Ticket` | [Objetivo y alcance](./lessons/12-relations/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/12-relations/02_guion_paso_a_paso.md) · [Relaciones JPA](./lessons/12-relations/03_relaciones_jpa.md) · [Rúbrica mínima](./lessons/12-relations/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/12-relations/05_actividad_individual.md) |
| 13 | [Tabla de historial](./lessons/13-history/) | `@OneToMany` con `CascadeType.ALL`; entidad `TicketHistory` y registro automático de cambios de estado en el `Service` | [Objetivo y alcance](./lessons/13-history/01_objetivo_y_alcance.md) · [Guión paso a paso](./lessons/13-history/02_guion_paso_a_paso.md) · [Historial y auditoría](./lessons/13-history/03_historial_y_auditoria.md) · [Rúbrica mínima](./lessons/13-history/04_checklist_rubrica_minima.md) · [Actividad individual](./lessons/13-history/05_actividad_individual.md) |

---

## 🧩 Extras — Material de apoyo

Temas transversales necesarios para el desarrollo profesional como desarrollador Full Stack.  
Se estudian de forma **autónoma** en paralelo al curso.

| # | Tema | Descripción | Enlace |
|---|------|-------------|--------|
| 1 | Git y GitHub | Control de versiones distribuido y flujo de trabajo colaborativo | [→ Ver](./extras/git-github/README.md) |
| 2 | GitFlow | Modelo de ramificación con ramas `main`, `develop`, `feature/*`, `release/*` y `hotfix/*` | [→ Ver](./extras/gitflow/README.md) |
| 3 | Java para Spring Boot | Mini curso desde sintaxis esencial hasta arquitectura Spring Boot (Java 21 LTS) | [→ Ver](./extras/java-para-spring-boot/README.md) |
| 4 | JSON | Formato de intercambio de datos en APIs REST y su manejo con Jackson en Spring Boot | [→ Ver](./extras/json/README.md) |
| 5 | Lombok | Librería Java para eliminar código boilerplate mediante anotaciones | [→ Ver](./extras/lombok/README.md) |
| 6 | Markdown | Lenguaje de marcado para documentación técnica | [→ Ver](./extras/markdown/README.md) |
| 7 | Maven | Gestión de dependencias, ciclo de vida de build y estructura estándar de proyectos Java | [→ Ver](./extras/maven/README.md) |
| 8 | Modelo de Madurez de Richardson | Niveles 0–3 para clasificar la calidad de una API REST | [→ Ver](./extras/richardson-maturity-model/README.md) |
| 9 | Principios SOLID | Cinco principios de diseño OO para código mantenible y extensible | [→ Ver](./extras/solid/README.md) |
| 10 | Variables de Entorno | Configuración segura con `.env`, Spring Boot (`@Value`, perfiles) e IntelliJ IDEA | [→ Ver](./extras/env-variables/README.md) |
| 11 | Lógica Proposicional | Proposiciones, operadores lógicos, tablas de verdad y leyes de De Morgan aplicadas a Java | [→ Ver](./extras/logica-proposicional/README.md) |
| 12 | Ejercicios Prácticos | 20 ejercicios progresivos que integran Java, POO y Lógica Proposicional en casos de uso reales | [→ Ver](./extras/ejercicios/README.md) |
| 13 | Terminal — Bash y Windows | Comandos esenciales de terminal para desarrolladores backend (Linux/macOS y Windows) | [→ Ver](./extras/terminal/README.md) |
| 14 | YAML | Formato de configuración estándar de Spring Boot (`application.yml`) y sus conceptos clave | [→ Ver](./extras/yaml/README.md) |
| 15 | Matemáticas para Programar | Operaciones básicas, contador, acumulador, descuentos, cargos, redondeo y `BigDecimal` | [→ Ver](./extras/matematicas-para-programar/README.md) |
| 16 | Tips de Programación | 24 situaciones reales de menor a mayor complejidad: razonamiento, error común y solución en Java (consola y REST API) | [→ Ver](./extras/tips-de-programacion/README.md) |

> 📋 Ver índice completo de extras: [`extras/README.md`](./extras/README.md)
> 🗺️ Ver roadmap de estudio recomendado: [`roadmap.md`](./roadmap.md)

---

## 🔗 Referencias del repositorio

| Recurso | Enlace |
|---------|--------|
| README principal | [`../README.md`](../README.md) |
| Proyecto Tickets (API) | [`../Tickets/README.md`](../Tickets/README.md) |
| 🗺️ Roadmap de estudio | [`roadmap.md`](./roadmap.md) |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

