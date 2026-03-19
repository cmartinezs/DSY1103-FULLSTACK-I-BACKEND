# Lección 06 - Reglas REST e idempotencia

## Reglas REST que estamos aplicando

Esta lección consolida las reglas REST que hemos venido aplicando desde la lección 03. Ahora que tienes el CRUD completo es un buen momento para revisarlas en conjunto.

---

### Regla 1: URLs en plural y sin verbos

El recurso se llama `Ticket` (singular), pero la URL usa el plural:

```
/tickets
```

Esto se hace porque la URL representa una **colección** de recursos. Usar el singular `/ticket` puede generar ambigüedad: ¿es la colección o un elemento?

**Correcto:**

```
GET    /tickets
GET    /tickets/1
POST   /tickets
PUT    /tickets/1
DELETE /tickets/1
```

**Incorrecto:**

```
GET  /getTickets
POST /createTicket
PUT  /updateTicket/1
GET  /obtenerTicketPorId/1
```

La **acción** la indica el verbo HTTP, no la URL. La URL solo identifica el recurso.

---

### Regla 2: la acción la define el método HTTP

El mismo endpoint `/tickets/1` puede hacer cosas diferentes dependiendo del método:

| Método | Endpoint        | Acción                       |
|--------|-----------------|------------------------------|
| GET    | `/tickets/1`    | Obtener el ticket con ID 1   |
| PUT    | `/tickets/1`    | Actualizar el ticket con ID 1|
| DELETE | `/tickets/1`    | Eliminar el ticket con ID 1  |

Si pusieras la acción en la URL, tendrías que crear una URL diferente por cada operación. Eso rompe el diseño REST.

---

### Regla 3: el código de estado debe reflejar lo que ocurrió

Una API no debe responder siempre `200 OK`. El código de estado le dice al cliente exactamente qué pasó.

| Situación                            | Código HTTP         |
|--------------------------------------|---------------------|
| Consulta o actualización exitosa     | `200 OK`            |
| Ticket creado correctamente          | `201 Created`       |
| Eliminación exitosa                  | `204 No Content`    |
| Ticket no encontrado                 | `404 Not Found`     |
| Título duplicado al crear            | `409 Conflict`      |

Si tu API devuelve `200 OK` para todo, el cliente no puede saber si algo falló. Un cliente bien diseñado (o un desarrollador que usa Postman) depende de estos códigos para saber cómo actuar.

---

### Regla 4: el ID del recurso va en la URL, no en el body

Cuando haces `PUT /tickets/1`, el ID `1` identifica **qué recurso estás modificando**. Es parte de la URL.

Si el body también trae un `id`, el servidor debe ignorarlo (o asumir que el de la URL es el correcto). No debes confiar en el `id` del body para decidir qué ticket actualizar.

```java
// Correcto: usas el id de la URL
@PutMapping("/{id}")
public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket ticket) {
    Ticket updatedTicket = service.update(id, ticket); // el id viene de la URL
    ...
}
```

---

## Idempotencia explicada con tickets

### ¿Qué significa idempotente?

Una operación es **idempotente** si ejecutarla una vez produce el mismo resultado que ejecutarla múltiples veces.

En otras palabras: no importa cuántas veces repitas la operación, el **estado final** del sistema siempre es el mismo.

---

### `PUT` es idempotente

Supón que haces esta petición:

```
PUT /tickets/1
Content-Type: application/json

{
  "title": "Ticket 1 - Revisado",
  "description": "Descripción actualizada",
  "status": "IN_PROGRESS"
}
```

Si la ejecutas una vez, el ticket 1 queda con ese título, descripción y estado.

Si la ejecutas cinco veces más, el ticket 1 **sigue quedando exactamente igual**: mismo título, misma descripción, mismo estado.

El resultado final no cambia sin importar cuántas veces repitas la petición. Por eso `PUT` es idempotente.

**Idea clave:** `PUT` reemplaza el estado del recurso hacia un valor definido. No acumula cambios: reemplaza.

---

### `DELETE` también es idempotente

```
DELETE /tickets/2
```

La primera vez: el ticket 2 se elimina → `204 No Content`.

La segunda vez: el ticket 2 ya no existe → `404 Not Found`.

Aunque el código de respuesta cambia, el **estado del sistema** es el mismo en ambos casos: el ticket 2 no existe. Por eso `DELETE` se considera idempotente.

---

### `POST` **no** es idempotente

```
POST /tickets
{
  "title": "Error en login",
  "description": "..."
}
```

Cada vez que ejecutas esta petición, se crea un ticket nuevo (si el título no existía) o se rechaza con `409` (si ya existe). El resultado puede variar, y si el título es distinto cada vez, crearás múltiples tickets nuevos.

Por eso `POST` normalmente no es idempotente.

---

### Resumen de idempotencia

| Método | ¿Idempotente? | Razón                                                  |
|--------|---------------|--------------------------------------------------------|
| GET    | ✅ Sí          | Solo lee, nunca modifica el estado                     |
| PUT    | ✅ Sí          | Reemplaza el recurso hacia un estado definido          |
| DELETE | ✅ Sí          | El resultado final es siempre "el recurso no existe"   |
| POST   | ❌ No          | Puede crear múltiples recursos si se repite            |

---

## Códigos HTTP de esta lección: resumen visual

```
GET /tickets          → 200 OK         (lista de tickets)
GET /tickets/1        → 200 OK         (ticket encontrado)
GET /tickets/999      → 404 Not Found  (ticket no existe)

POST /tickets         → 201 Created    (ticket creado)
POST /tickets         → 409 Conflict   (título duplicado)

PUT /tickets/1        → 200 OK         (ticket actualizado)
PUT /tickets/999      → 404 Not Found  (ticket no existe)

DELETE /tickets/2     → 204 No Content (ticket eliminado)
DELETE /tickets/999   → 404 Not Found  (ticket no existe)
```

