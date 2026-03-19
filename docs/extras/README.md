# 📚 Extras — Contenido de Apoyo

Esta sección reúne material complementario sobre temas que **no son parte directa de las lecciones**, pero que son fundamentales para el desarrollo profesional de un desarrollador Full Stack.

> 📌 Se espera que el alumno estudie y practique estos contenidos de forma autónoma. Cada tema tiene su propia carpeta con documentación detallada.

---

## Índice de temas

| # | Tema | Carpeta |
|---|------|---------|
| 1 | [Git y GitHub](#1-git-y-github) | [`git-github/`](./git-github/README.md) |
| 2 | [GitFlow](#2-gitflow) | [`gitflow/`](./gitflow/README.md) |
| 3 | [Java para Spring Boot (Mini Curso)](#3-java-para-spring-boot-mini-curso) | [`java-para-spring-boot/`](./java-para-spring-boot/README.md) |
| 4 | [JSON](#4-json) | [`json/`](./json/README.md) |
| 5 | [Lombok](#5-lombok) | [`lombok/`](./lombok/README.md) |
| 6 | [Markdown](#6-markdown) | [`markdown/`](./markdown/README.md) |
| 7 | [Maven](#7-maven) | [`maven/`](./maven/README.md) |
| 8 | [Niveles de Madurez de Richardson](#8-niveles-de-madurez-de-richardson) | [`richardson-maturity-model/`](./richardson-maturity-model/README.md) |
| 9 | [Principios SOLID](#9-principios-solid) | [`solid/`](./solid/README.md) |
| 10 | [Variables de Entorno](#10-variables-de-entorno) | [`env-variables/`](./env-variables/README.md) |
| 11 | [Lógica Proposicional](#11-lógica-proposicional) | [`logica-proposicional/`](./logica-proposicional/README.md) |
| 12 | [🏋️ Ejercicios Prácticos](#12-️-ejercicios-prácticos) | [`ejercicios/`](./ejercicios/README.md) |
| 13 | [Próximos temas](#13-próximos-temas) | — |

---

## 1. Git y GitHub

**Git** es un sistema de control de versiones distribuido. **GitHub** es la plataforma en la nube más usada para alojar repositorios Git y colaborar en equipo.

**Conceptos clave:** `commit`, `branch`, `merge`, `pull request`, `push`, `clone`, `.gitignore`, Conventional Commits.

**¿Por qué importa?** Todo proyecto profesional usa Git. Sin él, colaborar en equipo o mantener un historial de cambios es imposible.

→ [Ver documentación completa de Git y GitHub](./git-github/README.md)

---

## 2. GitFlow

**GitFlow** es un **modelo de ramificación** para Git que define cómo organizar las ramas de un proyecto y cómo fluyen los cambios entre ellas. Establece ramas permanentes (`main`, `develop`) y temporales (`feature/*`, `release/*`, `hotfix/*`).

**Ramas clave:** `main` · `develop` · `feature/*` · `release/*` · `hotfix/*`

**¿Por qué importa?** Estandariza el trabajo en equipo, protege el código de producción y facilita la gestión de versiones y correcciones urgentes.

→ [Ver documentación completa de GitFlow](./gitflow/README.md)

---

## 3. Java para Spring Boot (Mini Curso)

Java es el lenguaje del ecosistema **Spring Boot**. Este mini curso cubre desde la sintaxis esencial hasta los conceptos mínimos para escribir y entender código Spring Boot con soltura. Pensado para **cualquier nivel**: el módulo 00 es un repaso rápido (cheat sheet) para quien ya recuerda Java; los módulos 01-12 parten desde los conceptos más básicos y avanzan de forma progresiva.

**Temas cubiertos:** repaso rápido POO · conceptos base (variables, operadores, bloques, bucles, parámetros) · sintaxis y tipos · control de flujo moderno · métodos y sobrecarga · clases, objetos y records · los 4 pilares POO · interfaces y abstracción · colecciones y genéricos · manejo de excepciones · lambdas y Streams · novedades Java 21 · arquitectura de Spring Boot.

**Java:** 21 LTS — incluye `var`, text blocks, `record`, sealed classes, pattern matching, virtual threads y Sequenced Collections.

→ [Ir al Mini Curso: Java para Spring Boot](./java-para-spring-boot/README.md)

---

## 4. JSON

**JSON** (JavaScript Object Notation) es el formato estándar de intercambio de datos en APIs REST. Spring Boot lo gestiona automáticamente con **Jackson**, serializando objetos Java a JSON y viceversa.

**Tipos de datos:** `string` · `number` · `boolean` · `null` · `object` · `array`

**¿Por qué importa?** Es el lenguaje universal de comunicación entre cliente y servidor en APIs REST modernas.

→ [Ver documentación completa de JSON](./json/README.md)

---

## 5. Lombok

**Lombok** es una librería Java que usa **procesamiento de anotaciones** para generar automáticamente código repetitivo (*boilerplate*): getters, setters, constructores, `toString`, `equals`, loggers y más.

**Anotaciones clave:** `@Getter` · `@Setter` · `@AllArgsConstructor` · `@NoArgsConstructor` · `@RequiredArgsConstructor` · `@Data` · `@Builder` · `@Slf4j`

**¿Por qué importa?** Reduce drásticamente el tamaño de las clases Java, evita errores humanos en código repetitivo y mejora la legibilidad. Es estándar en proyectos Spring Boot.

→ [Ver documentación completa de Lombok](./lombok/README.md)

---

## 6. Markdown

Markdown es el **lenguaje de marcado estándar** para documentación técnica. Se usa en `README.md`, issues, pull requests, wikis y más.

**Sintaxis básica:** títulos, listas, tablas, bloques de código, enlaces, imágenes, citas.

**¿Por qué importa?** Todo proyecto necesita documentación. Markdown es la forma más universal y eficiente de escribirla.

→ [Ver documentación completa de Markdown](./markdown/README.md)

---

## 7. Maven

**Apache Maven** es la herramienta de **gestión de proyectos y automatización de builds** estándar en el ecosistema Java/Spring Boot. Gestiona dependencias, compila, testea y empaqueta la aplicación.

**Conceptos clave:** `pom.xml` · coordenadas GAV · ciclo de vida (`compile`, `test`, `package`) · Maven Wrapper (`mvnw`) · scopes de dependencias.

**¿Por qué importa?** Es la herramienta que une todas las piezas del proyecto: descarga librerías, compila el código y produce el JAR ejecutable.

→ [Ver documentación completa de Maven](./maven/README.md)

---

## 8. Niveles de Madurez de Richardson

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

## 9. Principios SOLID

**SOLID** es un acrónimo de cinco principios de diseño orientado a objetos que producen código mantenible, extensible y testeable.

| Letra | Principio | Idea central |
|-------|-----------|--------------|
| **S** | Single Responsibility | Una clase = una razón para cambiar |
| **O** | Open/Closed | Abierta para extensión, cerrada para modificación |
| **L** | Liskov Substitution | Las subclases reemplazan a su base sin sorpresas |
| **I** | Interface Segregation | Interfaces pequeñas y específicas |
| **D** | Dependency Inversion | Depender de abstracciones, no de implementaciones |

**¿Por qué importa?** Spring Boot aplica SOLID de forma natural: las capas Controller/Service/Repository separan responsabilidades y la inyección de dependencias aplica el principio D.

→ [Ver documentación completa de Principios SOLID](./solid/README.md)

---

## 10. Variables de Entorno

Las **variables de entorno** permiten configurar la aplicación de forma diferente según el entorno (desarrollo, staging, producción) sin modificar el código fuente. Incluye uso de archivos `.env`, integración con Spring Boot y configuración en IntelliJ IDEA.

**Conceptos clave:** archivo `.env` · `.env.example` · `application.properties` con `${VAR}` · `@Value` · `@ConfigurationProperties` · perfiles de Spring Boot · plugin EnvFile para IntelliJ.

**¿Por qué importa?** Ningún valor sensible (contraseñas, tokens, claves de API) debe estar en el código fuente ni en el repositorio.

→ [Ver documentación completa de Variables de Entorno](./env-variables/README.md)

---

## 11. Lógica Proposicional

La **lógica proposicional** es la rama de la lógica matemática que estudia las relaciones entre proposiciones y las leyes que las rigen. Es la base del razonamiento formal y —muy directamente— de las **condiciones, validaciones y reglas de negocio** en cualquier lenguaje de programación.

**Conceptos clave:** proposiciones · negación (`!`) · conjunción (`&&`) · disyunción (`||`) · condicional (`→`) · bicondicional (`↔`) · tablas de verdad · tautologías · contradicciones · leyes de De Morgan · precedencia de operadores.

**¿Por qué importa?** Cada `if`, `while` o expresión booleana en Java es lógica proposicional aplicada. Comprender estas leyes permite escribir condiciones correctas, simplificar lógica compleja y evitar errores como condiciones contradictorias o código muerto.

→ [Ver documentación completa de Lógica Proposicional](./logica-proposicional/README.md)

---

## 12. 🏋️ Ejercicios Prácticos

**20 ejercicios progresivos** organizados en dos series que integran todos los conceptos del Mini Curso de Java, los 4 pilares de la POO y Lógica Proposicional mediante **casos de uso reales**.

**Serie I — Con ejemplos de código (ejercicios 01 al 15):** cada enunciado incluye snippets de referencia, ejemplos de entrada y salidas esperadas formateadas.

**Serie II — Enunciado abierto (ejercicios 16 al 20):** el sistema a construir se describe de forma funcional. El alumno decide el diseño y la implementación de forma autónoma.

| Nivel | Serie I | Serie II |
|-------|---------|----------|
| ⭐ Básico | 01 – 03 | 16 |
| ⭐⭐ Básico-Medio | 04 – 06 | 17 |
| ⭐⭐⭐ Medio | 07 – 10 | 18 |
| ⭐⭐⭐⭐ Medio-Avanzado | 11 – 13 | 19 |
| ⭐⭐⭐⭐⭐ Avanzado | 14 – 15 | 20 |

**Contextos reales:** acceso a sistemas · descuentos en retail · sensores de temperatura · gestión universitaria · parking · RRHH · tienda de mascotas · restaurante · biblioteca · farmacia · aerolínea · e-learning · e-commerce · red social · banco digital · supermercado · clínica · hospital · concesionaria · logística.

→ [Ver todos los ejercicios](./ejercicios/README.md)

---

## 13. Próximos temas

Los siguientes contenidos se irán incorporando a medida que avance el curso:

| Tema | Descripción |
|------|-------------|
| **HTTP y Status Codes** | Verbos, cabeceras y códigos de respuesta en profundidad |
| **Inyección de Dependencias** | Principio DI e Inversión de Control (IoC) en Spring |
| **JPA e Hibernate** | Persistencia de datos con ORM y anotaciones `@Entity` |
| **Postman / curl** | Herramientas para probar y documentar APIs REST |
| **Docker (básico)** | Contenedorización de aplicaciones Spring Boot |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
