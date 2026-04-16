# Lección 07 - Errores bien hechos: ¿qué vas a aprender?

## ¿De dónde venimos?

En la lección anterior completaste el CRUD de tickets. Tu API ya puede crear, leer, actualizar y eliminar recursos. Pero hay un problema silencioso que se esconde en cada endpoint: los errores no tienen una forma consistente.

Observa lo que ocurre ahora cuando algo falla:

| Situación | Respuesta actual |
|---|---|
| `POST /tickets` con título duplicado | `409 Conflict` + body: `"Ya existe un ticket con el título..."` (texto plano) |
| `GET /tickets/999` (no existe) | `404 Not Found` + **sin body** |
| `PUT /tickets/999` (no existe) | `404 Not Found` + **sin body** |
| `DELETE /tickets/999` (no existe) | `404 Not Found` + **sin body** |

El problema es doble:

1. **Inconsistencia:** el `POST` devuelve algo (aunque sea texto plano), los demás no devuelven nada.
2. **Inutilidad para el cliente:** cuando un cliente recibe un `404` vacío, no sabe qué estaba buscando ni por qué falló. Tiene que adivinar.

Una API profesional tiene un contrato de errores claro. El cliente siempre sabe qué esperar cuando algo sale mal.

---

## ¿Qué vas a construir?

Al terminar esta lección, **todos** los errores de tu API tendrán la misma estructura JSON:

```json
{
  "message": "Ticket con ID 999 no encontrado"
}
```

Concretamente, vas a:

1. Crear una clase `ErrorResponse` que representa esa estructura
2. Actualizar el controlador para que todos los errores devuelvan esta estructura con cuerpo
3. Garantizar que el `Service` lanza excepciones con mensajes claros

### Lo que vas a ser capaz de explicar

Al terminar deberías poder responder:

- ¿Por qué una API devuelve `text/plain` cuando el body es un `String` y `application/json` cuando es un objeto?
- ¿Qué ventaja tiene `{"message": "..."}` sobre devolver un `String` directamente?
- ¿Por qué un `404` sin body es problemático para el cliente que consume la API?
- ¿Qué es un "contrato de errores" y por qué importa?
- ¿En qué se diferencia manejar errores localmente (try/catch por método) del manejo global (`@ControllerAdvice`)?

---

## ¿Qué requerimientos implementamos en esta lección?

> El proyecto completo está descrito en [`00_enunciado_proyecto.md`](../00_enunciado_proyecto.md).

| Requerimiento | Lo que construimos |
|---|---|
| **REQ-11** — Error con cuerpo JSON `{"message":"..."}` | La clase `ErrorResponse` + actualización de todos los endpoints para usarla |
| **REQ-12** — El creador y asignado no pueden ser el mismo usuario | Validación en `create()` y `updateById()` del Service |

---

## ¿Qué NO cubre esta lección? (y por qué)

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| `@ControllerAdvice` | Requiere comprender el ciclo de errores de Spring; lo presentamos como debate hoy sin implementarlo |
| Validación de entrada (`@NotBlank`, `@Valid`) | Es una capa adicional; primero consolidamos la estructura de errores |
| Jerarquía de excepciones propias | `IllegalArgumentException` es suficiente por ahora; las excepciones de dominio llegan con más contexto |
| Códigos de error de la base de datos | Aún trabajamos en memoria |

El foco de esta lección es uno solo: **que todos los errores hablen el mismo idioma JSON**.

---

## La estructura que tienes al comenzar

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← CRUD completo, errores inconsistentes
├── model/
│   └── Ticket.java
├── respository/
│   └── TicketRepository.java   ← List-based, findById/update/delete con Optional
├── service/
│   └── TicketService.java      ← create() lanza IllegalArgumentException
└── TicketsApplication.java
```

Y la estructura que tendrás al terminar:

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← todos los errores devuelven ErrorResponse
├── model/
│   ├── Ticket.java
│   └── ErrorResponse.java      ← nueva: estructura estándar de error
├── respository/
│   └── TicketRepository.java   (sin cambios)
├── service/
│   └── TicketService.java      (sin cambios)
└── TicketsApplication.java
```

