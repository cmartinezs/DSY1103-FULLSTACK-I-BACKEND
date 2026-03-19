# 📚 Extras — Contenido de Apoyo

Esta sección reúne material complementario sobre temas que **no son parte directa de las lecciones**, pero que son fundamentales para el desarrollo profesional de un desarrollador Full Stack.

> 📌 Se espera que el alumno estudie y practique estos contenidos de forma autónoma. Cada tema tiene su propia carpeta con documentación detallada.

---

## Índice de temas

| # | Tema | Carpeta |
|---|------|---------|
| 1 | [Git y GitHub](#1-git-y-github) | [`git-github/`](./git-github/README.md) |
| 2 | [Java y POO](#2-java-y-programación-orientada-a-objetos-poo) | [`java-poo/`](./java-poo/README.md) |
| 3 | [Markdown](#3-markdown) | [`markdown/`](./markdown/README.md) |
| 4 | [Niveles de Madurez de Richardson](#4-niveles-de-madurez-de-richardson) | [`richardson-maturity-model/`](./richardson-maturity-model/README.md) |
| 5 | [Próximos temas](#5-próximos-temas) | — |

---

## 1. Git y GitHub

**Git** es un sistema de control de versiones distribuido. **GitHub** es la plataforma en la nube más usada para alojar repositorios Git y colaborar en equipo.

**Conceptos clave:** `commit`, `branch`, `merge`, `pull request`, `push`, `clone`, `.gitignore`, Conventional Commits.

**¿Por qué importa?** Todo proyecto profesional usa Git. Sin él, colaborar en equipo o mantener un historial de cambios es imposible.

→ [Ver documentación completa de Git y GitHub](./git-github/README.md)

---

## 2. Java y Programación Orientada a Objetos (POO)

Java es el lenguaje del ecosistema **Spring Boot**. La POO organiza el código en torno a objetos que encapsulan estado y comportamiento.

**Los 4 pilares:** Encapsulamiento · Herencia · Polimorfismo · Abstracción

**Conceptos Java relevantes:** `interface`, `Optional<T>`, generics, lambdas, Stream API, anotaciones (`@`), Records.

→ [Ver documentación completa de Java y POO](./java-poo/README.md)

---

## 3. Markdown

Markdown es el **lenguaje de marcado estándar** para documentación técnica. Se usa en `README.md`, issues, pull requests, wikis y más.

**Sintaxis básica:** títulos, listas, tablas, bloques de código, enlaces, imágenes, citas.

**¿Por qué importa?** Todo proyecto necesita documentación. Markdown es la forma más universal y eficiente de escribirla.

→ [Ver documentación completa de Markdown](./markdown/README.md)

---

## 4. Niveles de Madurez de Richardson

El **Richardson Maturity Model (RMM)** clasifica las APIs REST en 4 niveles según qué tan bien aplican los principios REST.

| Nivel | Nombre | Resumen |
|-------|--------|---------|
| 0 | Pantano de POX | Un solo endpoint, sin estructura REST |
| 1 | Recursos | URLs por recurso, aún sin verbos HTTP |
| 2 | Verbos HTTP | Verbos y códigos de estado correctos ⭐ |
| 3 | HATEOAS | La API guía al cliente con hipervínculos |

> ⭐ El **Nivel 2 es el mínimo esperado** en este curso.

→ [Ver documentación completa del Modelo de Richardson](./richardson-maturity-model/README.md)

---

## 5. Próximos temas

Los siguientes contenidos se irán incorporando a medida que avance el curso:

| Tema | Descripción |
|------|-------------|
| **HTTP y Status Codes** | Verbos, cabeceras y códigos de respuesta |
| **JSON** | Formato de intercambio, validación y serialización |
| **Maven** | Gestión de dependencias y ciclo de vida en Java |
| **Spring Boot** | Framework para backend en Java |
| **Inyección de Dependencias** | Principio DI e Inversión de Control (IoC) |
| **JPA e Hibernate** | Persistencia de datos con ORM |
| **Postman / curl** | Herramientas para probar APIs REST |
| **Variables de Entorno** | Configuración segura de aplicaciones |
| **Principios SOLID** | Principios de diseño orientado a objetos |
| **Docker (básico)** | Contenedorización de aplicaciones |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
