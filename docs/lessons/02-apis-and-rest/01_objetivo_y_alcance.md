# Lección 02 - Arquitecturas, APIs y REST: ¿qué vas a aprender?

## ¿De dónde venimos?

En la lección anterior aprendiste los fundamentos de la comunicación web: qué es HTTP, cómo funciona el intercambio de peticiones y respuestas, qué significan los métodos y los códigos de estado. Tienes el vocabulario del protocolo.

Ahora vamos a subir un nivel de abstracción. Vamos a hablar de **cómo se organizan los sistemas** que usan ese protocolo: quién hace qué, cómo se dividen las responsabilidades, cómo los sistemas se comunican entre sí, y qué convenciones seguimos para que esa comunicación sea predecible y mantenible.

Esta lección te lleva desde "entiendo HTTP" hasta "entiendo cómo se diseña un sistema web moderno". Y eso es exactamente lo que necesitas para que la lección 03 —donde construirás tu primera API— tenga sentido completo.

---

## ¿Qué vas a aprender?

Al terminar esta lección serás capaz de explicar:

- Qué es el **frontend** y qué es el **backend**, y cómo se comunican
- Qué es una arquitectura **monolítica** y cuáles son sus ventajas y desventajas
- Qué son los **microservicios** y en qué contextos tiene sentido usarlos
- Qué es una **API** y para qué sirve
- Qué es **REST** y qué lo diferencia de otros estilos de arquitectura
- Cuáles son las **restricciones de REST** (los 6 principios de Fielding)
- Cuáles son las **buenas prácticas de diseño de APIs REST**: nombres de recursos, uso de métodos, versionado, respuestas consistentes

Esta lección es 100% teórica. No escribirás código, pero al terminarla tendrás los criterios para evaluar si una API está bien o mal diseñada, incluso antes de haber construido la tuya.

---

## ¿Qué NO cubre esta lección? (y por qué)

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| Implementación en Spring Boot | La lección 03 se encarga de eso |
| Autenticación y autorización (JWT, OAuth) | Requiere entender primero qué es una API y cómo funciona |
| GraphQL o gRPC | Son alternativas a REST; primero dominas REST, luego comparas |
| Documentación de APIs (OpenAPI/Swagger) | Se aborda cuando ya tienes endpoints reales que documentar |
| CORS (Cross-Origin Resource Sharing) | Es un problema que aparece cuando el frontend y el backend están en dominios distintos; lo resolvemos cuando lo encontremos |

---

## La idea central de esta lección

> "Una API REST no es solo un servidor que responde JSON. Es un contrato de comunicación con reglas claras. Aprende las reglas antes de romperlas."

Muchos desarrolladores crean APIs que "funcionan" pero que no siguen las convenciones REST. Eso no importa mientras trabajan solos, pero se convierte en un problema cuando el equipo crece, cuando hay que mantener el sistema o cuando otro servicio necesita consumir esa API sin documentación.

Aprender las convenciones desde el principio te ahorra deuda técnica en el futuro.

---

## Estructura de esta lección

| Archivo | Contenido |
|---|---|
| `01_objetivo_y_alcance.md` | Este archivo: qué aprenderás y por qué |
| `02_arquitecturas_y_roles.md` | Frontend vs Backend, monolito vs microservicios |
| `03_apis_rest_y_buenas_practicas.md` | Qué es una API, qué es REST, principios y buenas prácticas |
| `04_checklist_rubrica_minima.md` | Criterios mínimos de evaluación |
| `05_actividad_individual.md` | Actividades investigativas y reflexivas |

