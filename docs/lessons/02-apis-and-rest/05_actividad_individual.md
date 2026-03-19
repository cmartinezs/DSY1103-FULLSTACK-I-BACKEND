# Lección 02 - Actividad individual: investigación y reflexión

Esta actividad no tiene código. Requiere que investigues, analices sistemas reales y defiendas tus propias conclusiones. Las actividades de investigación buscan que salgas de los ejemplos del curso; las reflexivas buscan que desarrolles criterio propio.

> **Formato de entrega:** un documento Markdown (`.md`) con tus respuestas. Cada sección debe tener título y respuesta redactada en tus propias palabras. No copies definiciones de Wikipedia o ChatGPT sin reelaborarlas.

---

## Parte 1: Actividades investigativas

---

### 🔍 Investigación 2.1 — Analiza una API pública real

**Objetivo:** aplicar los conceptos de diseño REST a una API del mundo real.

**Instrucciones:**

Elige UNA de las siguientes APIs públicas y gratuitas:
- [GitHub REST API](https://docs.github.com/en/rest)
- [The Dog API](https://thedogapi.com/)
- [Open-Meteo (meteorología)](https://open-meteo.com/)
- [PokéAPI](https://pokeapi.co/)
- [JSONPlaceholder (API de prueba)](https://jsonplaceholder.typicode.com/)

**Responde en tu documento:**

a) ¿Qué API elegiste? ¿Cuál es su propósito?

b) Encuentra al menos 3 endpoints diferentes en su documentación. Para cada uno, indica:
   - URL completa
   - Método HTTP
   - Qué hace
   - Qué código de estado devuelve en caso de éxito

c) Usando curl o el navegador, haz una petición real a uno de esos endpoints y copia la respuesta completa (URL, código de estado, y primeras líneas del cuerpo JSON).

d) ¿Qué nivel del Modelo de Madurez de Richardson tiene esta API? Justifica con evidencia de su documentación o comportamiento.

e) Identifica al menos UN aspecto de diseño que te parezca bueno y explica por qué. Identifica al menos UN aspecto que mejorarías y explica cómo.

---

### 🔍 Investigación 2.2 — Monolito vs Microservicios: casos reales

**Objetivo:** entender las decisiones arquitectónicas reales y sus consecuencias.

**Instrucciones:** investiga los siguientes casos documentados públicamente:

- **Amazon** pasó de monolito a microservicios alrededor de 2001-2002
- **Netflix** migró de DVD-por-correo a streaming y restructuró completamente su arquitectura
- **Shopify** es una empresa grande que todavía defiende el monolito (busca "Shopify monolith")
- **Stack Overflow** sirve millones de páginas con muy pocos servidores y arquitectura simple

**Responde en tu documento:**

a) Elige DOS de estos casos. Para cada uno, explica:
   - ¿Cuál era el problema que tenían con su arquitectura anterior?
   - ¿Qué decisión arquitectónica tomaron?
   - ¿Qué resultado obtuvieron?

b) ¿Por qué crees que empresas tan grandes como Shopify o Stack Overflow defienden el monolito si tienen los recursos para hacer microservicios?

c) Un desarrollador junior te dice "voy a usar microservicios para mi proyecto de portafolio personal". ¿Qué le respondes?

---

### 🔍 Investigación 2.3 — Tesis doctoral de Roy Fielding

**Objetivo:** conocer el origen de REST desde la fuente primaria.

**Instrucciones:** Lee el capítulo 5 de la tesis de Fielding, disponible gratuitamente en inglés en: [https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm)

No tienes que entenderla completamente. Es un documento académico denso. Lee con el objetivo de responder las siguientes preguntas.

**Responde en tu documento:**

a) Según Fielding, ¿qué motivó el diseño de REST? ¿Qué problema intentaba resolver?

b) Fielding escribió REST específicamente pensando en la Web (HTTP). ¿Crees que REST sigue siendo relevante en 2026? ¿Por qué?

c) Muchas APIs que se llaman a sí mismas "REST" no implementan HATEOAS (el nivel 3 de Richardson). Fielding ha criticado esto públicamente varias veces. ¿Quién tiene razón: la industria que ignora HATEOAS o Fielding que lo considera obligatorio? Defiende tu posición.

---

## Parte 2: Actividades reflexivas

---

### 💭 Reflexión 2.4 — Diseña una API desde cero

**Objetivo:** aplicar las buenas prácticas de diseño REST a un problema real.

**Escenario:** estás diseñando la API backend para una aplicación de gestión de tareas (como Trello o Todoist). Los usuarios pueden crear proyectos, y dentro de cada proyecto pueden crear tareas. Las tareas tienen estado (pendiente, en progreso, completada) y pueden tener comentarios.

**Responde en tu documento:**

a) Define todos los recursos del sistema (mínimo 3).

b) Diseña las URLs para el CRUD completo de cada recurso. Incluye método HTTP y código de estado esperado para cada operación.

c) Diseña la estructura JSON de respuesta para al menos uno de los recursos (incluye todos los campos que tendría).

d) ¿Cómo implementarías filtrado? (ej: "solo tareas completadas", "tareas del proyecto X"). Muestra ejemplos de URLs.

e) Si un usuario intenta crear una tarea en un proyecto que no existe, ¿qué código de estado devuelves? ¿Qué cuerpo de respuesta?

---

### 💭 Reflexión 2.5 — Los límites de REST

REST es la arquitectura dominante para APIs web, pero no es perfecta para todos los casos.

**Responde en tu documento:**

a) Imagina una aplicación de chat en tiempo real (como Slack o WhatsApp Web). ¿Por qué el modelo petición-respuesta de REST no es ideal para este caso? ¿Qué alternativa usarías?

b) Imagina una aplicación móvil que necesita mostrar el perfil de un usuario, sus últimas 5 publicaciones y sus 3 amigos más recientes, todo en una sola pantalla. Con REST, ¿cuántas peticiones HTTP necesitarías mínimo? ¿Qué problema crea esto en redes lentas? ¿Cómo lo resolverías?

c) GraphQL fue diseñado explícitamente para resolver algunos de estos problemas. ¿Qué problema concreto del punto anterior resuelve GraphQL? ¿Significa eso que GraphQL es "mejor" que REST? ¿En qué contexto elegiría cada uno?

---

### 💭 Reflexión 2.6 — APIs como producto

Las APIs no son solo código técnico: son interfaces que otros equipos o empresas dependen. Un mal diseño de API puede generar meses de trabajo adicional para quienes la consumen.

**Responde en tu documento:**

a) Si construyes una API que usa rutas como `/getUsers`, `/createUser` y `/deleteUser`, ¿qué problemas concretos le causas a un desarrollador frontend que la consume?

b) Imagina que lanzas la versión 1 de tu API con el campo `nombre_completo` en los usuarios. Seis meses después quieres cambiar ese campo a `nombreCompleto` (camelCase). ¿Cómo manejarías este cambio sin romper a los clientes que ya usan la v1?

c) Stripe es conocida en la industria por tener una de las mejores APIs del mundo. Investiga brevemente por qué la comunidad de desarrolladores la considera tan buena. ¿Qué principios de diseño aplica que la hacen destacar?

---

## Preparación para la lección 03

Al terminar esta actividad, ya tienes todo el modelo mental necesario para la lección siguiente. Como preparación adicional:

**Haz esto antes de la lección 03:**

- [ ] Descarga e instala [IntelliJ IDEA Community o Ultimate](https://www.jetbrains.com/idea/download/)
- [ ] Descarga e instala [Java 21 (JDK)](https://adoptium.net/) si no lo tienes
- [ ] Instala [Postman](https://www.postman.com/downloads/) para probar endpoints
- [ ] Verifica que Java está instalado ejecutando `java -version` en la terminal (debe mostrar versión 21)

**Pregunta para traer respondida a clase:**

> "¿Qué crees que hace Spring Boot que hace que crear un servidor HTTP sea tan simple?" Busca una respuesta de no más de 3 oraciones. La verificaremos y profundizaremos en la lección 03.

---

## Criterios de evaluación de la actividad

| Criterio | Descripción |
|---|---|
| **Profundidad** | Las respuestas van más allá de la definición superficial; demuestran comprensión real |
| **Evidencia** | Las investigaciones citan fuentes, muestran URLs probadas o capturas de respuestas reales |
| **Argumento** | Las reflexiones defienden una posición con argumentos concretos, no solo opiniones |
| **Aplicación** | El diseño de la API del ejercicio 2.4 aplica correctamente las buenas prácticas vistas |
| **Formato** | El documento está en Markdown con estructura clara, títulos y secciones bien organizadas |

---

## Recursos sugeridos para investigar

- [Roy Fielding's Dissertation - Chapter 5 REST](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm) — La fuente original de REST
- [Richardson Maturity Model - Martin Fowler](https://martinfowler.com/articles/richardsonMaturityModel.html) — Explicación clara del modelo de madurez
- [REST API Design Best Practices - Microsoft](https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design) — Guía práctica de Microsoft
- [Stripe API Reference](https://stripe.com/docs/api) — Ejemplo de API bien diseñada
- [GitHub REST API](https://docs.github.com/en/rest) — Otra API pública bien diseñada para estudiar
- [Microservices - Martin Fowler](https://martinfowler.com/articles/microservices.html) — El artículo que popularizó el término microservicios
- [Monolith First - Martin Fowler](https://martinfowler.com/bliki/MonolithFirst.html) — El argumento a favor de empezar con monolito

