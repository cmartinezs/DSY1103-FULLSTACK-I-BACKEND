# Lección 06 - CRUD completo: ¿qué vas a aprender?

## ¿De dónde venimos?

En la lección anterior extendiste la API de tickets para que también pudiera **crear** recursos. Ahora tienes dos endpoints funcionando:

```
GET  /tickets → devuelve todos los tickets
POST /tickets → recibe un ticket nuevo y lo guarda
```

Eso está bien. Pero una API que solo sabe leer y crear tiene una limitación evidente: no puede buscar un ticket específico, no puede actualizarlo y no puede eliminarlo.

Esta lección existe para resolver eso.

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás un **CRUD completo** sobre el recurso `Ticket`. Concretamente, agregarás:

- `GET /tickets/{id}` → buscar un ticket por su ID
- `PUT /tickets/{id}` → actualizar un ticket existente
- `DELETE /tickets/{id}` → eliminar un ticket

Con esto el CRUD queda completo:

| Operación | Método HTTP | Endpoint          |
|-----------|-------------|-------------------|
| Create    | POST        | `/tickets`        |
| Read all  | GET         | `/tickets`        |
| Read one  | GET         | `/tickets/{id}`   |
| Update    | PUT         | `/tickets/{id}`   |
| Delete    | DELETE      | `/tickets/{id}`   |

### Lo que vas a ser capaz de explicar

Más que ejecutar el código, el objetivo es que entiendas cada decisión. Al terminar deberías poder responder:

- ¿Para qué sirve `@PathVariable` y en qué se diferencia de `@RequestBody`?
- ¿Por qué `GET /tickets/{id}` devuelve `404` cuando el ticket no existe?
- ¿Por qué un `DELETE` exitoso devuelve `204 No Content` y no `200 OK`?
- ¿Qué significa que una operación sea **idempotente**?
- ¿Por qué el ID correcto para una actualización viene de la URL y no del body?

---

## ¿Qué requerimientos implementamos en esta lección?

> El proyecto completo está descrito en [`00_enunciado_proyecto.md`](../00_enunciado_proyecto.md).
> Ahí encontrarás el escenario, los actores y la lista completa de requerimientos numerados.

De esa lista, esta lección implementa los **cuatro restantes**:

| Requerimiento | Lo que construimos |
|---------------|--------------------|
| **REQ-07** — Consultar un ticket por ID | El endpoint `GET /tickets/{id}` con `@PathVariable` |
| **REQ-08** — Actualizar título o descripción | El endpoint `PUT /tickets/{id}` |
| **REQ-09** — Eliminar un ticket | El endpoint `DELETE /tickets/{id}` |
| **REQ-10** — Error claro cuando el ticket no existe | `Optional<T>` en las capas internas + respuesta `404 Not Found` en el controlador |

Con esta lección el sistema cumple **todos** los requerimientos del enunciado. El proyecto Tickets tiene un CRUD completo y funcional.

---

## ¿Qué NO cubre esta lección? (y por qué)

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| Manejo global de errores (`@ControllerAdvice`) | Requiere conocer las excepciones típicas de una API; lo trabajaremos con más endpoints disponibles |
| Validaciones (`@Valid`, `@NotBlank`) | Son una capa adicional; primero el flujo básico |
| `PATCH` para actualizaciones parciales | Más complejo que `PUT`; cubrirlo ahora distrae del CRUD básico |
| Base de datos real (JPA + PostgreSQL) | Aún trabajamos en memoria; el salto a persistencia real viene después |
| Paginación y filtros en el `GET /tickets` | Requiere comprender primero los endpoints individuales |

El foco de esta lección es uno solo: **completar el ciclo de vida de un recurso REST con los cuatro verbos HTTP fundamentales**.

---


## La estructura que tienes al comenzar

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← solo GET y POST
├── model/
│   └── Ticket.java
├── respository/
│   └── TicketRepository.java   ← solo getAll(), existsByTitle(), save()
├── service/
│   └── TicketService.java      ← solo getTickets(), create()
└── TicketsApplication.java
```

Y la estructura que tendrás al terminar:

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← GET, GET/{id}, POST, PUT/{id}, DELETE/{id}
├── model/
│   └── Ticket.java
├── respository/
│   └── TicketRepository.java   ← getAll(), findById(), existsByTitle(), save(), update(), delete()
├── service/
│   └── TicketService.java      ← getTickets(), findById(), create(), update(), delete()
└── TicketsApplication.java
```

Los cambios son incrementales: cada nueva operación agrega un método a cada capa, sin romper lo que ya existe.

