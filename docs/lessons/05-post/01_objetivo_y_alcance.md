# Lección 05 - POST y creación de recursos: ¿qué vas a aprender?

## ¿De dónde venimos?

En la lección anterior construiste una API con arquitectura por capas: `Controller`, `Service`, `Repository` y `Model`. El endpoint que expusiste fue `GET /tickets`, que devolvía una lista de tickets almacenados en memoria.

Era una API que solo sabía leer. Funciona, y la estructura era correcta, pero en la práctica una API que solo lee sirve de muy poco: los datos tienen que entrar desde algún lugar.

Esta lección existe para resolver eso.

---

## ¿Qué vas a construir?

Al terminar esta lección habrás extendido tu API de tickets para que también sea capaz de **recibir y guardar información nueva**. Concretamente:

- Agregarás el endpoint `POST /tickets` al controlador existente
- Recibirás datos JSON desde el cliente usando `@RequestBody`
- Asignarás IDs de forma automática dentro del `Repository`
- Devolverás el ticket creado con el código de estado correcto: `201 Created`

### Lo que vas a ser capaz de explicar

Más que ejecutar el código, el objetivo es que entiendas cada decisión. Al terminar deberías poder responder:

- ¿Para qué sirve `@RequestBody` y qué problema resuelve?
- ¿Por qué el servidor asigna el ID y no el cliente?
- ¿Por qué una creación exitosa responde `201` y no `200`?
- ¿Qué diferencia hay entre devolver un objeto directamente y usar `ResponseEntity`?
- ¿Por qué el modelo necesita un constructor vacío para que `@RequestBody` funcione?

---

## ¿Qué requerimientos implementamos en esta lección?

> El proyecto completo está descrito en [`00_enunciado_proyecto.md`](../00_enunciado_proyecto.md).
> Ahí encontrarás el escenario, los actores y la lista completa de requerimientos numerados.

De esa lista, esta lección implementa **cinco**:

| Requerimiento | Lo que construimos |
|---------------|--------------------|
| **REQ-02** — Registrar un nuevo ticket con título y descripción | El endpoint `POST /tickets` con `@RequestBody` |
| **REQ-03** — Estado inicial `NEW` automático | El `Service` asigna `status = "NEW"` al crear |
| **REQ-04** — Sin títulos duplicados | El `Service` valida con `existsByTitle()` antes de guardar |
| **REQ-05** — Fecha y hora de creación automática | El `Service` asigna `createdAt = LocalDateTime.now()` |
| **REQ-06** — Fecha estimada de resolución | El `Service` calcula `estimatedResolutionDate = hoy + 5 días` |

Nota que REQ-03 a REQ-06 **no los envía el cliente** en el body del `POST`. Los calcula y asigna el servidor. Eso no es un detalle técnico: es una regla de negocio, y el lugar correcto para esa lógica es el `Service`.

---

## ¿Qué NO cubre esta lección? (y por qué)

Hay cosas que intencionalmente dejamos para más adelante:

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| Validaciones (`@Valid`, `@NotNull`, `@NotBlank`) | Primero entendemos el flujo básico de creación; las validaciones son una capa adicional |
| Manejo global de errores (`@ControllerAdvice`) | Requiere conocer las excepciones que puede lanzar una API; lo trabajaremos cuando tengamos más endpoints |
| IDs auto-generados por la base de datos | Aún no usamos JPA ni PostgreSQL; la estrategia manual es suficiente para esta etapa |
| `PUT`, `PATCH` y `DELETE` | Completaremos el CRUD una vez que `POST` esté dominado |
| UUID como identificador | Agrega complejidad sin aportar valor en esta etapa del aprendizaje |

El foco de esta lección es uno solo: **entender cómo entra información a la API y cómo se responde correctamente cuando algo se crea**.

---

## El problema que resuelve `POST`

Hasta ahora, los datos de tu API estaban cargados de forma fija en el constructor del `TicketRepository`. Eso funcionaba para probar el `GET`, pero tiene un problema obvio: nadie puede agregar tickets nuevos mientras la aplicación está corriendo.

El método `POST` es la solución. Cuando un cliente quiere crear un recurso nuevo, envía los datos en el cuerpo de la petición HTTP y tu API los recibe, los procesa y los persiste (en memoria por ahora).

El flujo completo de una petición `POST` es:

```
Cliente → POST /tickets (con body JSON)
       → TicketController.create()
       → TicketService.create()
       → TicketRepository.save()
       → [ ticket con ID asignado ]
       → 201 Created (con el ticket creado en el body)
```

Cada capa sigue haciendo exactamente lo mismo que en la lección anterior, con la diferencia de que ahora el dato entra desde afuera en lugar de estar hardcodeado.

---

## La idea central de esta lección

> "El código de estado HTTP no es un detalle de implementación. Es parte del contrato de tu API."

Devolver `200 OK` cuando el usuario espera `201 Created` no es solo incorrecto semánticamente: es un error de comunicación. Tu API le está mintiendo al cliente sobre lo que acaba de ocurrir. Desde esta lección, el código de respuesta siempre será explícito y correcto.

