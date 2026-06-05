# Lección 19 — Microservicios con OpenAPI Specification (OAS)

**Aprende a documentar APIs de microservicios con OpenAPI Specification, Swagger UI y ejemplos aplicados al ecosistema Tickets.**

---

## Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. OAS, Beneficios y Swagger** | 15 min | Conceptos base |
| **03. Guión Paso a Paso** | 25 min | Implementación práctica |
| **04. Ejemplos Prácticos** | 20 min | Código y especificación |
| **05. Checklist** | 5 min | Verificación |
| **06. Actividad Individual** | - | Tu tarea |

---

## El problema

En una arquitectura con microservicios, cada equipo necesita saber:

- Qué endpoints existen
- Qué datos recibe cada endpoint
- Qué respuestas puede devolver
- Qué códigos HTTP son esperados
- Cómo probar la API sin leer todo el código fuente

Sin documentación, la integración se vuelve lenta y frágil.

---

## Quick Start

### Dependencia base

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

> Define `${springdoc.version}` en `pom.xml` según la versión compatible con tu versión de Spring Boot.

### URL esperada

Con el context path del proyecto:

```text
http://localhost:8080/ticket-app/swagger-ui/index.html
http://localhost:8080/ticket-app/v3/api-docs
```

---

## Conceptos clave

| Concepto | Significado |
|----------|-------------|
| **OAS** | Especificación estándar para describir APIs HTTP |
| **OpenAPI** | Formato JSON/YAML legible por humanos y herramientas |
| **Swagger UI** | Interfaz web que muestra y permite probar la API |
| **Contrato** | Acuerdo técnico entre quien expone y quien consume la API |
| **Schema** | Descripción de la estructura de un request o response |

---

## Lo que construirás

1. Agregar documentación OpenAPI al proyecto Tickets
2. Publicar Swagger UI
3. Documentar endpoints principales
4. Documentar DTOs de entrada y salida
5. Agregar ejemplos de request y response
6. Aplicar el mismo patrón a microservicios de apoyo

---

## Lecturas recomendadas

- Lección 02: APIs y REST
- Lección 14: Comunicación entre Microservicios
- Lección 18: Global Exception Handling

---

*Lección 19 - [← Volver al Índice](../INDICE_COMPLETO.md)*
