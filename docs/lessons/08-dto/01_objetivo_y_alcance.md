# Lección 08 - Validaciones y DTO: ¿qué vas a aprender?

## ¿De dónde venimos?

En la lección anterior conseguiste que todos los errores de tu API devuelvan el mismo formato JSON: `{"message": "..."}`. Ahora el contrato de errores es claro y consistente.

Pero hay un problema diferente que aún existe: el cliente puede enviarte **campos que no debería poder enviar**.

Observa el body que acepta tu `POST /tickets` ahora mismo:

```json
{
  "id": 99,
  "title": "Bug en login",
  "description": "No puedo iniciar sesión",
  "status": "RESOLVED",
  "createdAt": "2020-01-01T00:00:00",
  "estimatedResolutionDate": "2020-01-06"
}
```

Tu API acepta este body sin quejarse. El campo `id` se ignora porque el `Repository` lo sobreescribe, y `createdAt` se ignora porque el `Service` asigna su propio valor. Pero `status` **sí podría ser tomado** si no tienes cuidado.

El problema no es solo este caso concreto. El problema de fondo es que estás usando `Ticket` — tu modelo de dominio — como la entrada directa de la API. Eso mezcla dos responsabilidades que deben estar separadas:

- **Lo que el cliente puede enviar** (campos de entrada)
- **Lo que el sistema almacena y procesa** (modelo de dominio)

Esta lección existe para resolver eso.

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás:

1. Un **DTO de entrada** (`TicketRequest`) implementado como un **Java `record`** — una característica de Java 21 que genera automáticamente constructor, getters, `equals()`, `hashCode()` y `toString()`
2. **Validación automática** del título con `@NotBlank`: si viene vacío, la API responde `400 Bad Request` con `{"message": "título: El titulo es requerido"}`
3. Un `@ExceptionHandler` en el controlador que convierte los errores de validación al formato `ErrorResponse`

### Lo que vas a ser capaz de explicar

Al terminar deberías poder responder:

- ¿Qué es un DTO y para qué sirve?
- ¿Por qué el modelo de dominio no debería ser la forma de entrada de la API?
- ¿Qué es un `record` en Java y por qué es ideal para DTOs?
- ¿Qué hace `@NotBlank` y en qué se diferencia de `@NotNull` y `@NotEmpty`?
- ¿Qué hace `@Valid` en el parámetro del controlador?
- ¿Qué excepción lanza Spring cuando la validación falla y cómo se captura?
- ¿Por qué el `@ExceptionHandler` local es preferible al try/catch por cada método para errores de validación?

---

## ¿Qué requerimientos implementamos en esta lección?

> El proyecto completo está descrito en [`00_enunciado_proyecto.md`](../00_enunciado_proyecto.md).

| Requerimiento | Lo que construimos |
|---|---|
| **REQ-12** — Título no puede estar vacío | La anotación `@NotBlank` en `TicketRequest.title` + respuesta `400` |
| **REQ-13** — DTO separado del modelo | El `record TicketRequest` en el paquete `dto` |

---

## ¿Qué NO cubre esta lección? (y por qué)

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| `@ControllerAdvice` global para validaciones | Requiere entender bien la jerarquía de excepciones; el `@ExceptionHandler` local es suficiente por ahora |
| Validaciones complejas (`@Min`, `@Max`, `@Pattern`, `@Size`) | Primero entiende el flujo básico; agregarlas es trivial una vez que entiendes `@NotBlank` |
| MapStruct u otras librerías de mapeo DTO → modelo | El mapeo manual hace visible la transformación; una librería lo oculta |
| DTO de respuesta (Response DTO) | Ahora solo controlamos la **entrada**; los DTOs de salida son un tema separado |
| Validaciones de negocio en el DTO | Las reglas de negocio (como "no duplicados") pertenecen al Service, no al DTO |

El foco de esta lección es uno: **separar la entrada del modelo y validarla antes de que llegue al Service**.

---

## La estructura que tienes al comenzar

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← acepta @RequestBody Ticket (modelo directo)
├── model/
│   ├── Ticket.java
│   └── ErrorResponse.java
├── respository/
│   └── TicketRepository.java
├── service/
│   └── TicketService.java      ← create(Ticket ticket)
└── TicketsApplication.java
```

Y la estructura que tendrás al terminar:

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← acepta @Valid @RequestBody TicketRequest + @ExceptionHandler
├── dto/
│   └── TicketRequest.java      ← nueva: record de entrada con @NotBlank
├── model/
│   ├── Ticket.java             ← sin anotaciones de validación (modelo puro)
│   └── ErrorResponse.java
├── respository/
│   └── TicketRepository.java
├── service/
│   └── TicketService.java      ← create(TicketRequest request), updateById(Long id, TicketRequest request)
└── TicketsApplication.java
```
