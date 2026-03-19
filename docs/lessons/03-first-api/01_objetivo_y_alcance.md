# Lección 03 - Tu primera API: ¿qué vas a aprender?

## ¿De dónde venimos?

En las lecciones anteriores exploraste los conceptos teóricos de las APIs REST y el protocolo HTTP. Sabes qué es un recurso, qué es un verbo HTTP y qué significa un código de estado. Pero todavía no has escrito una sola línea de código que funcione en un servidor real.

Esta lección cambia eso. Vamos a pasar de la teoría a la práctica por primera vez.

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás un servidor HTTP real, corriendo en tu máquina, que responde peticiones. Concretamente:

- Un proyecto **Spring Boot** creado desde cero con IntelliJ IDEA
- Un único endpoint que escucha en:

```
GET /greetings
```

- Que devuelve la siguiente respuesta con código `200 OK`:

```
Hola
```

Es simple. Intencionalmente simple. El objetivo no es el endpoint en sí, sino entender **cada pieza que lo hace funcionar**.

---

## ¿Qué vas a ser capaz de explicar?

Más importante que escribir el código es que entiendas el razonamiento detrás de cada parte. Al terminar esta lección deberías poder responder:

- ¿Qué hace Spring Boot y por qué lo usamos?
- ¿Qué es un controlador y cuál es su responsabilidad?
- ¿Por qué la clase tiene la anotación `@RestController`?
- ¿Qué hace `@RequestMapping` y cómo le dice a Spring en qué URL escuchar?
- ¿Cómo sabe Spring que ese método responde a una petición `GET`?
- ¿Qué ocurre entre que escribes `localhost:8080/greetings` en el navegador y ves "Hola"?

---

## ¿Qué NO cubre esta lección? (y por qué)

Esta lección se limita intencionalmente a lo esencial. Los siguientes temas se abordarán más adelante:

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| Separación en capas (Controller / Service / Repository) | Primero entendemos el Controller; las demás capas se agregan una a una |
| Responder con JSON (objetos, listas) | Antes de responder objetos, hay que entender cómo funciona una respuesta básica |
| Recibir parámetros en la URL o en el cuerpo | Primero el caso más simple: un GET sin parámetros |
| Base de datos | Todavía no hay datos que persistir |
| `ResponseEntity` | Lo incorporamos cuando necesitemos controlar el código de respuesta explícitamente |
| Validaciones | No hay datos de entrada que validar aún |

El objetivo es hacer **una cosa, bien hecha y completamente entendida**. Nada más.

---

## La herramienta: IntelliJ IDEA con Spring Initializr

Vas a crear el proyecto usando el asistente integrado de IntelliJ IDEA, que conecta con **Spring Initializr** (start.spring.io). Este asistente genera automáticamente la estructura base del proyecto con las dependencias que tú elijas.

Lo que seleccionarás:
- **Lenguaje:** Java
- **Gestor de dependencias:** Maven
- **Versión de Spring Boot:** la estable más reciente (4.x)
- **Java:** 21
- **Dependencias:** Spring Web, Lombok, Spring Boot DevTools

Cada una de esas decisiones tiene un por qué, y lo explicaremos en el tutorial paso a paso.

---

## La idea central de esta lección

> "Antes de agregar capas, entiende qué hace cada una."

El patrón que vas a aprender en lecciones siguientes (Controller → Service → Repository) solo tiene sentido si primero entiendes qué es un controlador, cómo recibe una petición HTTP y cómo devuelve una respuesta. Esta lección construye esa base.

