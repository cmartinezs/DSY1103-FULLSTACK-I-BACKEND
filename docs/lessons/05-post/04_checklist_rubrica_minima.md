# Lección 05 - Lista de verificación: ¿llegué al mínimo requerido?

Usa esta lista para revisar tu propio trabajo antes de presentarlo. Cada ítem tiene una breve explicación de qué significa y cómo verificarlo.

---

## ¿Qué es un indicador de evaluación (IE)?

Los indicadores de evaluación son los criterios concretos con los que se mide tu aprendizaje. Esta lección construye directamente sobre la anterior: los mismos indicadores de la lección 04 siguen vigentes, y ahora se agrega uno nuevo relacionado con la creación de recursos.

---

## IE 1.2.3 - Creación de recursos con POST

Este indicador mide si eres capaz de extender una API existente para que pueda recibir datos del cliente y persistirlos correctamente.

Checklist:

- [ ] El endpoint `POST /tickets` existe en `TicketController`
- [ ] El método del controlador usa `@PostMapping` sin argumentos adicionales
- [ ] El parámetro recibe el body con `@RequestBody Ticket ticket`
- [ ] El método retorna `ResponseEntity<Ticket>`, no `Ticket` directamente
- [ ] El código de respuesta es `201 Created`, no `200 OK`
- [ ] El body de la respuesta incluye el ticket con el `id` asignado por el servidor
- [ ] El `id` es asignado en el `Repository`, no en el `Controller` ni en el `Service`

**Cómo verificarlo:** haz una petición `POST http://localhost:8080/tickets` en Postman con un body JSON. Observa el código de estado en la esquina superior derecha: debe decir `201 Created`. El body de la respuesta debe incluir el objeto con un campo `id` con valor `3` (o el siguiente en la secuencia).

**Flujo correcto:**

```
POST /tickets (body JSON) → TicketController.create(@RequestBody)
                          → TicketService.create(ticket)
                          → TicketRepository.save(ticket)
                          → ticket.setId(currentId++)
                          → ResponseEntity 201 Created + body
```

---

## IE 1.1.3 - Respuestas REST y códigos HTTP

Este indicador ahora incluye el manejo explícito de `ResponseEntity`, que en la lección anterior estaba marcado como pendiente.

Checklist:

- [ ] `POST /tickets` responde con `201 Created`
- [ ] `GET /tickets` responde con `200 OK`
- [ ] El body de la respuesta en ambos casos es JSON válido
- [ ] `ResponseEntity` se usa en el método `create()` del controlador
- [ ] El método `getAllTickets()` existente no fue modificado ni roto por los cambios

**Cómo verificarlo:**
- Postman `POST /tickets` → `201 Created` con body
- Postman `GET /tickets` → `200 OK` con arreglo de tickets (incluyendo el recién creado)

> **¿Por qué el GET sigue sin `ResponseEntity`?**
> Porque aún no lo hemos migrado. Está planificado para la próxima iteración. Lo importante es que el nuevo endpoint `POST` ya lo usa correctamente desde el inicio.

---

## IE 1.2.1 - Estructura CSR preservada

Este indicador viene de la lección anterior y sigue vigente. Agregar un nuevo endpoint no debería romper la separación de responsabilidades que ya tenías.

Checklist:

- [ ] `TicketController` no accede directamente a la lista de tickets
- [ ] `TicketController` no tiene `setId()` ni lógica de generación de IDs
- [ ] `TicketService` tiene el método `create()` que llama a `repository.save()`
- [ ] `TicketRepository` tiene el método `save()` con el contador `currentId`
- [ ] El contador `currentId` arranca en `3L` para no colisionar con los tickets semilla

**Cómo verificarlo:** abre cada clase y pregúntate si tiene código que no le corresponde. El `Controller` solo debería tener anotaciones HTTP y llamadas al `Service`. El `Service` solo debería contener lógica de negocio. El `Repository` solo debería manejar la lista.

---

## IE 1.2.2 - Modelo actualizado correctamente

El modelo `Ticket` necesita una modificación específica para que `@RequestBody` funcione.

Checklist:

- [ ] La clase `Ticket` tiene `@NoArgsConstructor`
- [ ] La clase `Ticket` conserva `@AllArgsConstructor` (los tickets semilla lo necesitan)
- [ ] La clase `Ticket` tiene `@Getter` y `@Setter` (Jackson los necesita para la deserialización)
- [ ] Los tickets semilla en el constructor de `TicketRepository` siguen funcionando

**Cómo verificarlo:** si el JSON llega correctamente al servidor (el ticket se crea con los datos que mandaste), significa que `@NoArgsConstructor` está en su lugar. Si recibes un error `400 Bad Request` con mención a "deserialization" o "no suitable constructor", falta `@NoArgsConstructor`.

---

## IE 1.1.2 - Diseño de endpoints REST

Este indicador también viene de la lección anterior. El nuevo endpoint debe seguir las mismas convenciones.

Checklist:

- [ ] El recurso sigue en plural: `/tickets`
- [ ] El método HTTP es el correcto para crear: `POST`
- [ ] La URL no contiene verbos: no hay `/createTicket` ni `/nuevo`
- [ ] Un solo `@RequestMapping("/tickets")` a nivel de clase cubre ambos métodos

**Cómo verificarlo:** el `@PostMapping` del método no necesita argumentos porque hereda el path `/tickets` de `@RequestMapping`. Si ves `@PostMapping("/tickets")` en el método, hay duplicación innecesaria.

---

## Indicadores que se trabajan en lecciones siguientes

| Indicador | Qué cubre |
|---|---|
| IE 1.3.1 | Validaciones de entrada: `@Valid`, `@NotNull`, `@NotBlank` para evitar nombres vacíos en `POST` |
| IE 1.3.2 | Manejo global de excepciones con `@ControllerAdvice` |
| IE 1.3.3 | Pruebas automáticas de los endpoints REST |
| IE 1.2.3 (extensión) | `PUT` para actualizar y `DELETE` para eliminar: CRUD completo |

---

## ¿Completé el mínimo de esta lección?

Completaste el mínimo si:

- ✅ `POST http://localhost:8080/tickets` con un body JSON devuelve `201 Created` con el ticket creado (incluyendo un `id` asignado por el servidor)
- ✅ `GET http://localhost:8080/tickets` después del POST incluye el ticket recién creado en la lista
- ✅ El `id` del nuevo ticket es `3` (o mayor), nunca `null` ni el valor que el cliente intentara mandar
- ✅ Puedes explicar en tus propias palabras por qué `201` y no `200`, y por qué el servidor asigna el ID

