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
│   └── 04-responsabilities/
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
    ├── maven/
    ├── richardson-maturity-model/
    ├── solid/
    ├── terminal/
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

