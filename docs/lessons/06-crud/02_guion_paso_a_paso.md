# Lección 06 - Tutorial paso a paso: CRUD completo de tickets

Sigue esta guía en orden. Vas a extender el proyecto de tickets para que pueda buscar, actualizar y eliminar un ticket por su ID.

---

## Paso 1: entender qué cambios necesitamos

Antes de tocar el código, piensa en lo que falta. Tu API actualmente tiene esto:

```
GET  /tickets        → devuelve la lista completa  (ya existe)
POST /tickets        → crea un ticket nuevo        (ya existe)
```

Y lo que necesita tener al final de esta lección:

```
GET    /tickets             → devuelve la lista completa    (ya existe)
POST   /tickets             → crea un ticket nuevo          (ya existe)
GET    /tickets/by-id/{id}  → busca un ticket por ID        (lo que vamos a construir)
PUT    /tickets/by-id/{id}  → actualiza un ticket           (lo que vamos a construir)
DELETE /tickets/by-id/{id}  → elimina un ticket             (lo que vamos a construir)
```

Para que los tres nuevos endpoints funcionen, necesitas modificar **tres capas**:

1. **`TicketRepository`:** agregar `findById()`, `update()` y `delete()`
2. **`TicketService`:** agregar `findById()`, `update()` y `delete()`
3. **`TicketController`:** agregar los tres nuevos endpoints con `@PathVariable`

El `Model` (`Ticket.java`) **no cambia**: los campos que ya tiene son suficientes para todas estas operaciones.

---

## Paso 2: `Optional<T>` — por qué no retornamos `null`

Antes de escribir el primer método nuevo, hay una decisión de diseño que atraviesa **toda** esta lección: ningún método devolverá `null` para representar "no encontré nada".

### El problema con `null`

Cuando un método devuelve `null`, el código que lo llama **puede olvidar verificarlo**. Si lo olvida, el programa explota en tiempo de ejecución con un `NullPointerException`. Tony Hoare, el inventor del `null`, lo llamó su *"error de mil millones de dólares"*.

```java
// Peligroso: el compilador NO te avisa si olvidas el null check
Ticket ticket = repository.findById(id);
System.out.println(ticket.getTitle()); // NullPointerException si ticket == null
```

### La solución: `Optional<T>`

`Optional<T>` es un contenedor que **puede o no** tener un valor adentro. Lo que lo hace valioso no es el contenedor en sí, sino que **obliga al código que lo recibe a manejar explícitamente el caso "no existe"**. El compilador y el tipo mismo te lo recuerdan.

```java
// Seguro: Optional hace visible la posibilidad de ausencia
Optional<Ticket> ticket = repository.findById(id);
ticket.map(Ticket::getTitle).ifPresent(System.out::println); // nunca explota
```

### Las operaciones clave de Optional

| Operación                | ¿Qué hace?                                                    |
|--------------------------|---------------------------------------------------------------|
| `Optional.of(valor)`     | Crea un Optional con valor (lanza excepción si es null)       |
| `Optional.empty()`       | Crea un Optional vacío                                        |
| `optional.map(fn)`       | Si tiene valor, transforma; si está vacío, devuelve vacío     |
| `optional.orElse(otro)`  | Devuelve el valor si existe, u `otro` si está vacío           |
| `optional.ifPresent(fn)` | Ejecuta la función solo si hay valor                          |
| `optional.isPresent()`   | `true` si tiene valor (evitar: es casi igual a un null check) |

> **¿Cuándo usar `Optional` y cuándo no?**
> `Optional` está diseñado para **valores de retorno** de métodos que pueden no encontrar algo. No debe usarse como parámetro de método ni como campo de una clase: para esos casos hay mejores alternativas. En esta lección lo usarás exactamente donde corresponde: en los retornos de `findById()` y `update()`.

---

## Paso 3: agregar `findById()` al Repository

El `Repository` es quien sabe dónde están guardados los tickets. Abre `TicketRepository` y agrega el método `findById()`:

```java
public Optional<Ticket> findById(Long id) {
    return tickets.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst();
}
```

El stream de `findFirst()` ya devuelve un `Optional<T>` de forma nativa: si encuentra un elemento que pasa el filtro, devuelve `Optional.of(ese elemento)`; si no, devuelve `Optional.empty()`. No hay que hacer nada más.

**Código equivalente sin expresiones lambda:**

```java
public Optional<Ticket> findById(Long id) {
    for (Ticket ticket : tickets) {
        if (ticket.getId().equals(id)) {
            return Optional.of(ticket);
        }
    }
    return Optional.empty();
}
```

Ambas versiones son correctas y producen exactamente el mismo resultado. La versión con stream es más concisa; la versión con `for` es más explícita paso a paso. Cuando trabajes con JPA, `findById()` ya vendrá implementado por el framework y no tendrás que escribir ninguna de las dos.

> **¿Por qué usamos stream aquí y no un `for`?**
> Porque `findFirst()` devuelve `Optional<T>` de forma natural. Escribir el mismo comportamiento con un `for` obligaría a retornar `Optional.of(ticket)` o `Optional.empty()` manualmente al final, que es más verboso sin ningún beneficio. Cada herramienta en su lugar.

> **¿Qué pasa si hay dos tickets con el mismo ID?**
> En nuestro almacenamiento en memoria eso no puede ocurrir porque el ID se asigna con un contador incremental. Pero si ocurriera, `findFirst()` devolvería el primero que encuentre. Cuando migremos a JPA, el motor de base de datos garantiza unicidad con una restricción `PRIMARY KEY`.

---

## Paso 4: agregar `getById()` al Service

El `Service` delega al `Repository` y propaga el `Optional` hacia arriba, sin desnudarlo. No hay reglas de negocio que aplicar en una simple búsqueda por ID.

Abre `TicketService` y agrega:

```java
public Optional<Ticket> getById(Long id) {
    return this.repository.findById(id);
}
```

> **¿Por qué el `Service` no "abre" el Optional aquí?**
> Porque "abrir" el Optional (llamar a `.get()` o `.orElse(null)`) en el `Service` descargaría la responsabilidad de manejar el caso vacío en el `Controller`. Propagar el `Optional` hacia arriba preserva la información de "puede no existir" hasta la capa que sabe qué respuesta HTTP dar. Cada capa hace lo que le corresponde.

---

## Paso 5: agregar `GET /tickets/by-id/{id}` al Controller

Abre `TicketController` y agrega el endpoint:

```java
@GetMapping("/by-id/{id}")
public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    return service.getById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

**Código equivalente sin expresiones lambda:**

```java
@GetMapping("/by-id/{id}")
public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    Optional<Ticket> found = service.getById(id);
    if (found.isPresent()) {
        return ResponseEntity.ok(found.get());
    }
    return ResponseEntity.notFound().build();
}
```

Ambas versiones hacen exactamente lo mismo. El `.get()` es seguro aquí porque está protegido por `isPresent()` en la línea anterior.

> **¿Qué hace `@PathVariable`?**
> Captura el valor dinámico que viene en la URL. Si el cliente llama a `GET /tickets/by-id/3`, Spring extrae el `3` de la URL y lo asigna a la variable `id`. Sin `@PathVariable`, el controlador no sabría qué ID está buscando el cliente.

> **¿En qué se diferencia `@PathVariable` de `@RequestParam`?**
> `@PathVariable` extrae valores que forman parte de la estructura de la URL: `/tickets/3`. `@RequestParam` extrae parámetros del query string: `/tickets?id=3`. En REST, los identificadores de recursos van en la URL, no en el query string. Por eso usamos `@PathVariable`.

> **¿Qué hace `.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())`?**
> Si el `Optional` tiene un ticket adentro, `.map()` lo transforma en `ResponseEntity.ok(ticket)` → `200 OK`. Si está vacío, `.orElse()` devuelve `ResponseEntity.notFound().build()` → `404 Not Found`. Todo sin un solo `if` ni riesgo de `NullPointerException`.

> **¿Por qué `ResponseEntity.notFound().build()` y no `ResponseEntity.notFound().body(...)`?**
> Porque un 404 no lleva cuerpo en esta API: solo comunica que el recurso no existe. El `.build()` construye la respuesta sin body.

---

## Paso 6: agregar `update()` al Repository

El `Repository` necesita saber cómo actualizar un ticket existente. Reutilizamos `findById()` para no duplicar la lógica de búsqueda, y usamos `ifPresent()` para modificar el ticket solo si existe.

Abre `TicketRepository` y agrega:

```java
public Optional<Ticket> update(Long id, Ticket updatedTicket) {
    Optional<Ticket> found = findById(id);
    found.ifPresent(ticket -> {
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setDescription(updatedTicket.getDescription());
        ticket.setStatus(updatedTicket.getStatus());
    });
    return found;
}
```

**Código equivalente sin expresiones lambda:**

```java
public Optional<Ticket> update(Long id, Ticket updatedTicket) {
    Optional<Ticket> found = findById(id);
    if (found.isPresent()) {
        Ticket ticket = found.get();
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setDescription(updatedTicket.getDescription());
        ticket.setStatus(updatedTicket.getStatus());
    }
    return found;
}
```

Nuevamente el `.get()` es seguro porque está dentro del bloque `if (found.isPresent())`.

> **¿Por qué reutilizamos `findById()` en lugar de iterar de nuevo con un `for`?**
> Porque `findById()` ya resuelve el problema de búsqueda y devuelve un `Optional`. Duplicar la lógica de iteración sería una violación del principio DRY (*Don't Repeat Yourself*). Si mañana cambia cómo se busca un ticket (por ejemplo, en una base de datos), solo hay que cambiar `findById()`.

> **¿Qué hace `ifPresent()`?**
> Ejecuta el bloque de código solo si el `Optional` tiene un valor adentro. Si está vacío, no hace nada. Es el equivalente seguro de `if (found != null) { ... }`, pero sin null.

> **¿Por qué no actualizamos el ID?**
> El ID es el identificador único e inmutable del recurso. En REST, el recurso se identifica por su URL: `PUT /tickets/by-id/1` siempre modifica el ticket con ID `1`, independientemente de lo que el body diga sobre el ID.

---

## Paso 7: agregar `updateById()` al Service

```java
public Optional<Ticket> updateById(Long id, Ticket updatedTicket) {
    return this.repository.update(id, updatedTicket);
}
```

En esta lección el `Service` delega directamente al `Repository`. El desafío opcional al final de esta guía propone agregar validaciones aquí.

---

## Paso 8: agregar `PUT /tickets/by-id/{id}` al Controller

```java
@PutMapping("/by-id/{id}")
public ResponseEntity<Ticket> updateTicketById(@PathVariable Long id, @RequestBody Ticket ticket) {
    return service.updateById(id, ticket)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

**Código equivalente sin expresiones lambda:**

```java
@PutMapping("/by-id/{id}")
public ResponseEntity<Ticket> updateTicketById(@PathVariable Long id, @RequestBody Ticket ticket) {
    Optional<Ticket> updated = service.updateById(id, ticket);
    if (updated.isPresent()) {
        return ResponseEntity.ok(updated.get());
    }
    return ResponseEntity.notFound().build();
}
```

> **¿Por qué usamos el `id` de la URL y no el que pudiera venir en el body?**
> Porque la URL identifica el recurso de forma autoritativa. Si el cliente manda `PUT /tickets/by-id/1` con un body que tiene `"id": 99`, eso es una inconsistencia. La URL dice claramente cuál recurso se está modificando. El `id` del body se ignora: el `Repository` actualiza el ticket cuyo `id` coincide con el de la URL.

> **¿Por qué `PUT` devuelve `200 OK` con el ticket actualizado y no `204 No Content`?**
> Porque devolver el recurso actualizado le permite al cliente confirmar que los cambios se aplicaron correctamente, sin necesidad de hacer un `GET` adicional. Aunque la especificación HTTP permite `204` en un `PUT`, devolver `200` con el cuerpo actualizado es más útil en la práctica.

---

## Paso 9: agregar `delete()` al Repository

Para el borrado, el resultado es binario: o se eliminó o no existía. `boolean` es el tipo correcto aquí — no `Optional`, porque no hay ningún valor de retorno significativo si la operación fue exitosa.

Abre `TicketRepository` y agrega:

```java
public boolean delete(Long id) {
    return tickets.removeIf(t -> t.getId().equals(id));
}
```

**Código equivalente sin expresiones lambda:**

```java
public boolean delete(Long id) {
    for (Ticket ticket : tickets) {
        if (ticket.getId().equals(id)) {
            tickets.remove(ticket);
            return true;
        }
    }
    return false;
}
```

`removeIf()` elimina todos los elementos que satisfacen la condición y devuelve `true` si eliminó al menos uno, `false` si la colección no cambió (el ticket no existía).

> **¿Por qué devolvemos `boolean` y no `Optional<Ticket>`?**
> Porque `Optional` está diseñado para "puede haber un valor útil que necesitas". Después de un borrado, el ticket ya no existe: no hay nada que envolver en un `Optional`. `boolean` comunica exactamente lo que importa: ¿se eliminó algo? Usar `Optional` aquí sería forzar el patrón donde no corresponde.

> **¿Por qué `removeIf()` y no el `for` + `remove()` del paso de update?**
> Porque aquí no necesitamos el objeto después de borrarlo. `removeIf()` es la herramienta correcta cuando solo nos interesa la eliminación, no el valor eliminado. Elegir la herramienta correcta para cada caso hace el código más claro e intencional.

---

## Paso 10: agregar `deleteById()` al Service

```java
public boolean deleteById(Long id) {
    return this.repository.delete(id);
}
```

---

## Paso 11: agregar `DELETE /tickets/by-id/{id}` al Controller

```java
@DeleteMapping("/by-id/{id}")
public ResponseEntity<Void> deleteTicketById(@PathVariable Long id) {
    if (!service.deleteById(id)) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
}
```

> **¿Por qué el tipo de retorno es `ResponseEntity<Void>`?**
> Porque una eliminación exitosa no devuelve contenido: solo el código `204 No Content`. `Void` expresa esa intención con claridad: este endpoint nunca tendrá un cuerpo en la respuesta exitosa.

> **¿Por qué `204 No Content` y no `200 OK`?**
> `200 OK` implica que hay un cuerpo con información útil. `204 No Content` dice exactamente lo contrario: la operación fue exitosa, pero no hay nada que devolver. En una eliminación, el recurso ya no existe, por lo que devolver su estado anterior sería incoherente.

---

## Paso 12: el controlador completo

Este es el estado final de `TicketController` al terminar la lección:

```java
package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.service.TicketService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(this.service.getTickets());
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return service.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Ticket ticket) {
        try {
            service.create(ticket);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/by-id/{id}")
    public ResponseEntity<Ticket> updateTicketById(@PathVariable Long id, @RequestBody Ticket ticket) {
        return service.updateById(id, ticket)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/by-id/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable Long id) {
        if (!service.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
```

Observa el patrón: donde el recurso puede no existir, el `Optional` del `Service` se convierte directamente en la `ResponseEntity` correcta con `.map().orElse()`. No hay un solo `null` ni un solo `if (x == null)` en el controlador.

---

## Paso 13: verificar que todo funciona

Levanta la aplicación y abre Postman, Insomnia o Thunder Client.

### Prueba 1: obtener todos los tickets

```
GET http://localhost:8080/tickets
```

Resultado esperado: `200 OK` con la lista de 2 tickets semilla.

---

### Prueba 2: obtener un ticket existente

```
GET http://localhost:8080/tickets/by-id/1
```

Resultado esperado (`200 OK`):

```json
{
  "id": 1,
  "title": "Ticket 1",
  "description": "Descripción del ticket 1",
  "status": "NEW",
  "createdAt": "2026-03-15T09:00:00",
  "estimatedResolutionDate": "2026-03-22",
  "effectiveResolutionDate": null
}
```

---

### Prueba 3: obtener un ticket inexistente

```
GET http://localhost:8080/tickets/by-id/999
```

Resultado esperado: `404 Not Found` (sin cuerpo).

---

### Prueba 4: crear un ticket

```
POST http://localhost:8080/tickets
Content-Type: application/json
```

Body:

```json
{
  "title": "Error en dashboard",
  "description": "El gráfico de ventas no carga al filtrar por semana"
}
```

Resultado esperado: `201 Created` con el ticket creado (ID 3, status NEW, fechas asignadas por el servidor).

---

### Prueba 5: actualizar un ticket existente

```
PUT http://localhost:8080/tickets/by-id/1
Content-Type: application/json
```

Body:

```json
{
  "title": "Ticket 1 - Revisado",
  "description": "Descripción actualizada después de la revisión",
  "status": "IN_PROGRESS"
}
```

Resultado esperado: `200 OK` con el ticket actualizado. Observa que `createdAt` y `estimatedResolutionDate` **no cambiaron**.

---

### Prueba 6: actualizar un ticket inexistente

```
PUT http://localhost:8080/tickets/by-id/999
Content-Type: application/json
```

Body:

```json
{
  "title": "Ticket fantasma",
  "description": "Este ticket no existe",
  "status": "NEW"
}
```

Resultado esperado: `404 Not Found`.

---

### Prueba 7: eliminar un ticket existente

```
DELETE http://localhost:8080/tickets/by-id/2
```

Resultado esperado: `204 No Content` (sin cuerpo).

Verifica con un `GET /tickets` que el ticket 2 ya no aparece en la lista.

---

### Prueba 8: eliminar un ticket inexistente

```
DELETE http://localhost:8080/tickets/by-id/999
```

Resultado esperado: `404 Not Found`.

---

## Paso 14: reflexiona antes de cerrar

Antes de pasar a la actividad, respóndete estas preguntas:

1. Si un cliente manda `PUT /tickets/by-id/1` con el body `{"id": 99, "title": "Nuevo título", ...}`, ¿qué ID usa el servidor para buscar el ticket a actualizar? ¿Por qué?
2. Si ejecutas `DELETE /tickets/by-id/1` cinco veces seguidas, ¿qué responde el servidor la segunda, tercera, cuarta y quinta vez? ¿Eso lo hace idempotente?
3. ¿Por qué `findById()` y `update()` devuelven `Optional<Ticket>` mientras que `delete()` devuelve `boolean`? ¿Qué comunica cada tipo de retorno al código que lo llama?
4. ¿Qué pasaría si el `Service` llamara a `repository.findById(id).get()` sin verificar si el `Optional` está vacío? ¿Cuándo y cómo fallaría?

---

## Extensión opcional

Si terminaste todo lo anterior y quieres ir un paso más, agrega validaciones en el `Service` para el `update()`:

- Si el título está vacío o en blanco, que el controlador responda `400 Bad Request`
- Si el estado no es `NEW`, `IN_PROGRESS` o `RESOLVED`, que el controlador responda `400 Bad Request`

Por ahora sin `@Valid` ni Bean Validation. Solo con `if` simples en el `Service` y una excepción propia (o `IllegalArgumentException`) que el controlador capture.

