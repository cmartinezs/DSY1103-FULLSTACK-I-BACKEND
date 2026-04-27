# 📚 Extras — Contenido de Apoyo

Esta sección reúne material complementario sobre temas que **no son parte directa de las lecciones**, pero que son fundamentales para el desarrollo profesional de un desarrollador Full Stack.

> 📌 Se espera que el alumno estudie y practique estos contenidos de forma autónoma. Cada tema tiene su propia carpeta con documentación detallada.

> 🗺️ **¿No sabes por dónde empezar?** Consulta el [Roadmap de Estudio](../roadmap.md) — te indica qué leer según el tiempo que tienes disponible.

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
| 13 | [Terminal — Bash y Windows](#13-terminal--bash-y-windows) | [`terminal/`](./terminal/README.md) |
| 14 | [YAML](#14-yaml) | [`yaml/`](./yaml/README.md) |
| 15 | [➕ Matemáticas para Programar](#15-matemáticas-para-programar) | [`matematicas-para-programar/`](./matematicas-para-programar/README.md) |
| 16 | [💡 Tips de Programación](#16-tips-de-programación) | [`tips-de-programacion/`](./tips-de-programacion/README.md) |
| 17 | [🧠 Conceptos de Programación — Vocabulario Universal](#17-conceptos-de-programación--vocabulario-universal) | [`conceptos-de-programacion/`](./conceptos-de-programacion/README.md) |
| 18 | [🛠️ Herramientas para Desarrolladores](#18-️-herramientas-para-desarrolladores) | [`herramientas-para-desarrolladores/`](./herramientas-para-desarrolladores/README.md) |
| 19 | [🖥️ Uso de PC para Programadores](#19-️-uso-de-pc-para-programadores) | [`uso-de-pc/`](./uso-de-pc/README.md) |
| 20 | [🕰️ Historia de la Computación e Informática](#20-️-historia-de-la-computación-e-informática) | [`historia-de-la-computacion/`](./historia-de-la-computacion/README.md) |
| 21 | [Próximos temas](#21-próximos-temas) | — |
| 22 | [🎯 Homologación — Diagnóstico Java](#22-homologación--diagnóstico-java) | [`homologacion/`](./homologacion/README.md) |
| 23 | [🏗️ De Monolito a Microservicios](#23-️-de-monolito-a-microservicios) | [`monolito-a-microservicios/`](./monolito-a-microservicios/README.md) |

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

## 13. Terminal — Bash y Windows

La **terminal** (o línea de comandos) es la herramienta fundamental de cualquier desarrollador backend. Todo servidor de producción corre Linux sin interfaz gráfica; saber moverse en una terminal es la diferencia entre poder deployar una aplicación o depender de otra persona.

Este extra cubre los mismos conceptos en **dos guías paralelas** según el sistema operativo:

| Guía | Shell | Archivo |
|------|-------|---------|
| 🐧 Linux / macOS | `bash` / `zsh` | [`bash.md`](./terminal/bash.md) |
| 🪟 Windows | CMD y PowerShell | [`windows.md`](./terminal/windows.md) |

**Conceptos clave:** navegación de directorios · listar, crear, mover, copiar y eliminar archivos · leer archivos · variables de entorno · redirección y pipes · búsqueda de texto (`grep` / `Select-String`) · permisos de ejecución · comandos de red (`ping`, `curl`) · atajos de productividad · cheat sheet completo.

**Conexión con el curso:** ejecutar `./mvnw spring-boot:run`, leer variables de entorno, hacer peticiones con `curl` a la API, leer logs en tiempo real.

→ [Ver índice completo de Terminal](./terminal/README.md)

---

## 14. YAML

**YAML** (*YAML Ain't Markup Language*) es el formato estándar para configurar aplicaciones Spring Boot. El archivo `application.yml` reemplaza (o convive) con `application.properties` y escala mucho mejor gracias a su estructura jerárquica por indentación, eliminando la repetición de prefijos en cada línea.

**Conceptos clave:** indentación obligatoria · tipos de datos (string, número, booleano, null) · strings con comillas · multilínea (`|` y `>`) · listas · objetos anidados · comentarios · variables de entorno (`${VAR:default}`) · perfiles de Spring Boot (`application-dev.yml`, `application-prod.yml`, separador `---`) · `@ConfigurationProperties` · errores comunes (tabs, espacios, claves duplicadas).

**¿Por qué importa?** Toda la configuración de Spring Boot — puerto, base de datos, JPA, logging, perfiles, seguridad — vive en `application.yml`. No entender YAML significa no entender cómo se configura la aplicación.

→ [Ver documentación completa de YAML](./yaml/README.md)

---

## 15. ➕ Matemáticas para Programar

Las **matemáticas para programar** cubren el conjunto de operaciones y patrones numéricos que aparecen constantemente en cualquier sistema real: sumar precios, contar eventos, aplicar descuentos, calcular impuestos, promediar calificaciones y redondear correctamente valores monetarios.

**Módulos:** operaciones básicas y operadores Java (`+` `-` `*` `/` `%`) · contador y acumulador · promedio · porcentajes, descuentos y cargos / IVA · redondeo con `Math` · precisión decimal con `BigDecimal` · casos de uso reales (carrito, nómina, parking, notas).

**¿Por qué importa?** El 90% de la lógica de negocio en un backend involucra algún tipo de cálculo. Saber cómo representar y ejecutar esas operaciones correctamente en Java evita bugs de división entera, errores de precisión con `double` y resultados inconsistentes en contextos financieros.

→ [Ir a Matemáticas para Programar](./matematicas-para-programar/README.md)

---

## 16. 💡 Tips de Programación

Los **tips de programación** presentan situaciones reales organizadas de menor a mayor complejidad, con el objetivo de desarrollar el razonamiento lógico para definir algoritmos. Cada tip explica el escenario en lenguaje natural, identifica el error más común, guía el razonamiento paso a paso y ofrece la solución en código Java.

**Módulos:** cómo pensar un problema antes de codear · situaciones básicas (if/else, bucles, listas, validaciones) · situaciones intermedias (objetos, NullPointerException, Streams, agrupación, ordenamiento) · situaciones en REST API (traducir lógica a endpoints, leer parámetros, validar body, status codes, excepciones, separación de capas, filtros y respuestas estructuradas).

**¿Por qué importa?** Saber la sintaxis no es suficiente. La diferencia entre un programador principiante y uno efectivo es la capacidad de leer una situación, entender qué necesita el algoritmo y traducirlo a código correcto sin reescribirlo tres veces.

→ [Ir a Tips de Programación](./tips-de-programacion/README.md)

---

## 17. 🧠 Conceptos de Programación — Vocabulario Universal

El **vocabulario universal del programador** reúne los conceptos fundamentales que se usan en **cualquier lenguaje de programación**: variable, constante, literal, asignación, sentencia, bloque, bucle, función, clase, objeto, ámbito, referencia y muchos más. Cada concepto incluye definición, perspectiva en otros lenguajes, ejemplo en Java y las confusiones más frecuentes.

**¿Por qué importa?** Cuando un instructor dice *"declaras una variable y le asignas un literal"*, si no tienes claro qué significa cada término, la explicación no llega. Este módulo te da ese vocabulario previo — o te lo consolida si ya programas pero lo tenías difuso.

→ [Ir a Conceptos de Programación](./conceptos-de-programacion/README.md)

---

## 18. 🛠️ Herramientas para Desarrolladores

Las **herramientas para desarrolladores** cubren el ecosistema de software que un programador backend usa en el día a día: IDEs, clientes HTTP, gestores de bases de datos, contenedores, clientes Git, plugins de calidad de código, utilidades online y documentación de APIs.

**Categorías:** IDEs (IntelliJ IDEA, VS Code) · Clientes HTTP (Postman, Insomnia, Hoppscotch, curl) · Bases de datos (DBeaver, pgAdmin, MySQL Workbench) · Contenedores (Docker Desktop) · Git GUI (GitKraken, GitHub Desktop) · Calidad (SonarLint) · Utilidades online (start.spring.io, jwt.io, regex101, JSONLint, crontab.guru) · DevTools del navegador · Documentación de API (SpringDoc OpenAPI / Swagger UI).

**¿Por qué importa?** Conocer la herramienta correcta para cada tarea es tan importante como saber programar. Un buen entorno de trabajo multiplica la productividad y reduce errores en el desarrollo.

→ [Ver documentación completa de Herramientas para Desarrolladores](./herramientas-para-desarrolladores/README.md)

---

## 19. 🖥️ Uso de PC para Programadores

Muchos estudiantes abren IntelliJ, escriben código y no saben dónde está ese código en su computador. Este extra cubre los conceptos básicos del sistema de archivos que todo desarrollador debe dominar: cómo funciona el árbol de carpetas, dónde se guardan los proyectos por defecto, cómo encontrarlos, moverlos, organizarlos y evitar los errores más comunes.

Cubre exactamente los mismos conceptos en **dos guías paralelas** según el sistema operativo:

| Guía | Sistema | Archivo |
|------|---------|---------|
| 🪟 [Windows 10 / 11](./uso-de-pc/windows.md) | File Explorer, rutas con `\`, disco `C:\` | `windows.md` |
| 🐧 [Linux (Ubuntu / Mint)](./uso-de-pc/linux.md) | Administrador de archivos, rutas con `/`, carpeta `home` | `linux.md` |

**Conceptos clave:** sistema de archivos · rutas absolutas y relativas · carpeta home · dónde se guardan los proyectos de IntelliJ · cómo abrirlos desde el explorador · crear / renombrar / mover / eliminar · archivos ocultos y extensiones · buscar archivos · organización recomendada · descomprimir un proyecto de start.spring.io · atajos de teclado · cheat sheet.

**¿Por qué importa?** No entender el sistema de archivos propio es una de las causas más frecuentes de proyectos perdidos, errores al clonar repositorios y confusión al trabajar en equipo. Este extra lo resuelve desde cero.

→ [Ver índice completo de Uso de PC](./uso-de-pc/README.md)

---

## 20. 🕰️ Historia de la Computación e Informática

Desde el ábaco mesopotámico del 3000 a.C. hasta los agentes de IA de 2026 — la historia completa de cómo la humanidad construyó la era digital. Cubre los hitos técnicos, los cambios de paradigma, el hardware, el software, los lenguajes de programación, el nacimiento de Internet y la Web, la revolución móvil, la nube, y la inteligencia artificial. Incluye perfiles de las personas que hicieron posible todo lo que usamos hoy.

**9 capítulos:**

| Cap. | Título | Período |
|------|--------|---------|
| 1 | Antes de las máquinas — Los precursores | ~3000 a.C. – 1935 |
| 2 | El nacimiento de la computación | 1936 – 1951 |
| 3 | La era del mainframe | 1952 – 1974 |
| 4 | La revolución personal — La PC llega al hogar | 1975 – 1994 |
| 5 | Internet y la Web — El mundo conectado | 1969 – 2003 |
| 6 | La era móvil y la nube | 2000 – 2015 |
| 7 | Inteligencia Artificial y el presente | 1950 – hoy |
| 8 | Historia de los lenguajes de programación | 1843 – hoy |
| 9 | Pioneros y visionarios — Las personas detrás de todo | Todos |

**Personas clave:** Ada Lovelace · Alan Turing · John von Neumann · Claude Shannon · Grace Hopper · Dennis Ritchie · Linus Torvalds · Tim Berners-Lee · Bill Gates · Steve Jobs · Larry Page · Sergey Brin y muchos más.

**¿Por qué importa?** Cada línea de código que escribes hoy se apoya en décadas de trabajo de personas reales. Entender la historia da contexto, perspectiva y una intuición profunda sobre por qué las tecnologías son como son.

→ [Ver índice completo de Historia de la Computación](./historia-de-la-computacion/README.md)

---

## 21. Próximos temas

Los siguientes contenidos se irán incorporando a medida que avance el curso:

| Tema | Descripción |
|------|-------------|
| **HTTP y Status Codes** | Verbos, cabeceras y códigos de respuesta en profundidad |
| **Inyección de Dependencias** | Principio DI e Inversión de Control (IoC) en Spring |
| **JPA e Hibernate** | Persistencia de datos con ORM y anotaciones `@Entity` |
| **Postman / curl** | Herramientas para probar y documentar APIs REST |
| **Docker (básico)** | Contenedorización de aplicaciones Spring Boot |

---

## 22. 🎯 Homologación — Diagnóstico Java

Evaluación diagnóstica de 10 ejercicios progresivos para desarrollar en **80 minutos de clase**. Cada ejercicio aplica conceptos fundamentales de Java: variables, estructuras de control (`if`, `while`, `do-while`, `for`), colecciones (`ArrayList`, `HashMap`) y los cuatro pilares de POO (encapsulamiento, herencia, abstracción y polimorfismo). Los alumnos deben expresar su pensamiento lógico con sus propias palabras antes de codificar.

**10 ejercicios progresivos (Nivel 1 → 10):**

| # | Ejercicio | Conceptos | Tiempo |
|---|-----------|-----------|--------|
| 1 | Presentación personal | Variables, `println` | ≤ 3 min |
| 2 | Mayor de edad | `if / else` | ≤ 5 min |
| 3 | Clasificador de notas | `if / else if / else`, rangos | ≤ 6 min |
| 4 | Tabla de multiplicar | Bucle `for`, acumuladores | ≤ 7 min |
| 5 | Contador regresivo | `while` y `do-while` | ≤ 8 min |
| 6 | Lista de compras | `ArrayList`, `for-each` | ≤ 8 min |
| 7 | Agenda de contactos | `HashMap`, colecciones clave-valor | ≤ 9 min |
| 8 | Empleado y gerente | Encapsulamiento, herencia, `extends` | ≤ 10 min |
| 9 | Formas geométricas | Interfaz, polimorfismo, `implements` | ≤ 12 min |
| 10 | Sistema de vehículos | Clase abstracta, integración POO total | ≤ 12 min |

**Package:** `cl.duoc.diagnostico.minombredeusuario`

→ [Ver todos los ejercicios de homologación](./homologacion/README.md)

---

## 23. 🏗️ De Monolito a Microservicios

Guía completa de buenas prácticas para dividir un sistema monolítico en microservicios, con un **caso de estudio real** de una empresa manufacturera con múltiples sucursales, bodega central, fabricación propia, proveedores, clientes, fidelización y logística propia + terceros.

**Temas cubiertos:** cuándo y por qué migrar · identificación de Bounded Contexts (DDD + Event Storming) · mapa completo de 15 microservicios (10 de dominio + 5 auxiliares) · comunicación síncrona (REST + Circuit Breaker) vs. asíncrona (eventos + Saga) · estrategia de migración gradual (Strangler Fig) · buenas prácticas (BD por servicio, API versioning, observabilidad, contratos OpenAPI) · anti-patrones a evitar (monolito distribuido, chatty services, big bang) · checklist de migración.

**Servicios del caso FabriTech S.A.:**

| Tipo | Servicios |
|------|-----------|
| Dominio | `catalog` · `manufacturing` · `procurement` · `inventory` · `branch` · `customer` · `order` · `loyalty` · `shipping` · `payment` |
| Auxiliares | `auth` · `notification` · `email` · `pdf` · `report` |

**¿Por qué importa?** La mayoría de los sistemas empresariales modernos son microservicios que empezaron como monolitos. Saber cuándo y cómo hacer esa transición —y qué errores evitar— es una de las habilidades más valoradas en un desarrollador backend senior.

→ [Ver guía completa: De Monolito a Microservicios](./monolito-a-microservicios/README.md)

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
