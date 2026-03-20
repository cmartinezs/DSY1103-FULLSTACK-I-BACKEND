# Lección 09 - Tutorial paso a paso: Repository con Map y filtro por estado

Sigue esta guía en orden. Vas a refactorizar el almacenamiento en memoria de `List` a `Map` y agregar soporte para filtrar tickets por estado.

---

## Paso 1: entender O(n) vs O(1)

Antes de tocar código, entiende el problema que vamos a resolver.

### Complejidad O(n) — lo que tienes ahora

Con una `List`, buscar un elemento por ID requiere recorrer la lista entera en el peor caso:

```java
// O(n): si hay n tickets, en el peor caso compara n veces
public Optional<Ticket> findById(Long id) {
    return tickets.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst();
}
```

| Tickets en memoria | Comparaciones en el peor caso |
|---|---|
| 10 | 10 |
| 1.000 | 1.000 |
| 1.000.000 | 1.000.000 |

La búsqueda crece proporcionalmente con los datos.

### Complejidad O(1) — lo que vas a construir

Con un `HashMap<Long, Ticket>`, el acceso por clave es prácticamente instantáneo, sin importar cuántos elementos haya:

```java
// O(1): accede directamente por clave, sin recorrer nada
public Optional<Ticket> findById(Long id) {
    return Optional.ofNullable(db.get(id));
}
```

| Tickets en memoria | Operaciones de acceso |
|---|---|
| 10 | ~1 |
| 1.000 | ~1 |
| 1.000.000 | ~1 |

> **¿Por qué `HashMap.get()` es O(1)?**
> Un `HashMap` convierte la clave (el ID) en una posición de memoria mediante una función `hash`. Para buscar por clave, calcula el hash y va directo a esa posición — sin recorrer nada. El tiempo de acceso no depende de cuántos elementos haya en el mapa.
>
> Hay casos extremos (colisiones de hash) donde el acceso puede degradarse a O(n), pero son infrecuentes y se resuelven internamente. Para todos los efectos prácticos, `HashMap.get()` es O(1).

---

## Paso 2: refactorizar `TicketRepository` — el almacenamiento

Abre `TicketRepository` y cambia la estructura de almacenamiento.

**Antes:**

```java
List<Ticket> tickets;
long currentId = 0L;

public TicketRepository() {
    tickets = new ArrayList<>();
    tickets.add(new Ticket(currentId++, "Ticket 1", "Ticket 1", "NEW", LocalDateTime.now(), null, null));
    tickets.add(new Ticket(currentId++, "Ticket 2", "Ticket 2", "NEW", LocalDateTime.now(), null, null));
}
```

**Después:**

```java
private Map<Long, Ticket> db = new HashMap<>();
private long currentId = 1L;

public TicketRepository() {
    LocalDateTime now = LocalDateTime.now();
    LocalDate estimated = LocalDate.now().plusDays(5);

    Ticket t1 = new Ticket(currentId, "Ticket 1", "Descripción del ticket 1", "NEW", now, estimated, null);
    db.put(currentId++, t1);

    Ticket t2 = new Ticket(currentId, "Ticket 2", "Descripción del ticket 2", "NEW", now, estimated, null);
    db.put(currentId++, t2);
    // currentId queda en 3, listo para el siguiente ticket nuevo
}
```

> **¿Por qué `currentId` empieza en `1L` y no en `0L`?**
> Los IDs que empiezan en cero son inusuales y confusos: cuando el cliente recibe `"id": 0`, puede asumir que es un estado nulo o por defecto. Empezar en `1` es el estándar: bases de datos, frameworks y APIs del mundo real usan IDs que parten desde 1.

> **¿Por qué `Map<Long, Ticket>` y no `Map<Integer, Ticket>`?**
> El campo `id` del `Ticket` es `Long`. Si usáramos `Integer`, habría que convertir constantemente entre tipos, lo que añade ruido sin valor. La clave del Map debe ser del mismo tipo que el ID del modelo.

---

## Paso 3: refactorizar `getAll()` y agregar `getAll(String statusFilter)`

```java
public List<Ticket> getAll() {
    List<Ticket> all = new ArrayList<>(db.values());
    all.sort(Comparator.comparing(Ticket::getCreatedAt));
    return all;
}

public List<Ticket> getAll(String statusFilter) {
    List<Ticket> all = new ArrayList<>(db.values());
    all.sort(Comparator.comparing(Ticket::getCreatedAt));
    if (statusFilter != null && !statusFilter.isBlank()) {
        return all.stream()
            .filter(t -> t.getStatus().equalsIgnoreCase(statusFilter))
            .collect(Collectors.toList());
    }
    return all;
}
```

**Código equivalente sin expresiones lambda:**

```java
public List<Ticket> getAll(String statusFilter) {
    List<Ticket> all = new ArrayList<>(db.values());
    all.sort(Comparator.comparing(Ticket::getCreatedAt));

    if (statusFilter == null || statusFilter.isBlank()) {
        return all;
    }

    List<Ticket> filtered = new ArrayList<>();
    for (Ticket ticket : all) {
        if (ticket.getStatus().equalsIgnoreCase(statusFilter)) {
            filtered.add(ticket);
        }
    }
    return filtered;
}
```

> **¿Por qué `equalsIgnoreCase` y no `equals`?**
> Para que `?status=new`, `?status=NEW` y `?status=New` funcionen igual. Las APIs bien diseñadas son flexibles con los parámetros de consulta: el cliente no debería tener que saber si el estado es en mayúsculas o minúsculas.

> **¿Por qué ordenamos antes de filtrar?**
> Porque la lista resultante (filtrada o no) siempre debe estar ordenada por `createdAt`. Si ordenáramos después del filtro, el resultado sería el mismo, pero si ordenáramos antes, garantizamos que el orden es siempre consistente, incluso si el filtro no se aplica.

> **¿Por qué `new ArrayList<>(db.values())` y no `db.values()` directamente?**
> `db.values()` devuelve una **vista** de los valores del mapa — una colección que no tiene orden garantizado y no puede ordenarse directamente. Al convertirla a `ArrayList`, tienes una copia independiente que puedes ordenar y modificar sin afectar el mapa original.

---

## Paso 4: refactorizar `findById()`

**Antes:**

```java
public Optional<Ticket> findById(Long id) {
    return tickets.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst();
}
```

**Después:**

```java
public Optional<Ticket> findById(Long id) {
    return Optional.ofNullable(db.get(id));
}
```

`db.get(id)` devuelve el `Ticket` si existe, o `null` si no. `Optional.ofNullable()` convierte ese resultado en un `Optional` — si es `null`, devuelve `Optional.empty()`.

> **¿Por qué `Optional.ofNullable()` y no `Optional.of()`?**
> `Optional.of(valor)` lanza una excepción si el valor es `null`. `Optional.ofNullable(valor)` maneja el `null` silenciosamente, devolviendo `Optional.empty()`. Como `db.get(id)` puede devolver `null` (cuando el ID no existe), debemos usar `ofNullable`.

---

## Paso 5: refactorizar `save()`

**Antes:**

```java
public Ticket save(Ticket newTicket) {
    newTicket.setId(currentId++);
    tickets.add(newTicket);
    return newTicket;
}
```

**Después:**

```java
public Ticket save(Ticket ticket) {
    ticket.setId(currentId);
    db.put(currentId++, ticket);
    return ticket;
}
```

Primero asignamos el ID al ticket (para que el objeto devuelto ya tenga su ID), luego lo guardamos en el mapa con ese mismo ID como clave, y finalmente incrementamos el contador.

---

## Paso 6: refactorizar `update()`

**Antes (con List):**

```java
public Optional<Ticket> update(Long id, TicketRequest request) {
    Optional<Ticket> found = findById(id); // O(n)
    found.ifPresent(ticket -> {
        ticket.setTitle(request.getTitle());
        // ...
    });
    return found;
}
```

**Después (con Map):**

```java
public Optional<Ticket> update(Long id, TicketRequest request) {
    Optional<Ticket> found = findById(id); // O(1) ahora
    found.ifPresent(ticket -> {
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            ticket.setStatus(request.getStatus());
        }
    });
    return found;
}
```

La lógica no cambia — lo que cambia es que `findById(id)` ahora es O(1) en lugar de O(n). El beneficio es transparente para quien llama al método.

> **¿Por qué no necesitamos `db.put()` después del `ifPresent`?**
> Porque `db.get(id)` devuelve una **referencia** al mismo objeto `Ticket` que está en el mapa. Cuando llamamos a `ticket.setTitle(...)`, estamos modificando directamente el objeto que ya está almacenado. No necesitamos "guardarlo de nuevo" — ya está ahí.

---

## Paso 7: refactorizar `delete()`

**Antes:**

```java
public boolean delete(Long id) {
    return tickets.removeIf(t -> t.getId().equals(id));
}
```

**Después:**

```java
public boolean delete(Long id) {
    return db.remove(id) != null;
}
```

`Map.remove(key)` elimina la entrada con esa clave y devuelve el valor eliminado (el `Ticket`), o `null` si la clave no existía. Si el resultado es `!= null`, significa que se eliminó algo.

> **¿Por qué `db.remove(id) != null` y no guardar el resultado en una variable?**
> Porque solo nos interesa si se eliminó algo, no qué se eliminó. Si necesitáramos el ticket eliminado (por ejemplo, para devolverlo en la respuesta), usaríamos `Ticket removed = db.remove(id)` y luego `Optional.ofNullable(removed)`.

---

## Paso 8: actualizar `existsByTitle()`

```java
public boolean existsByTitle(String title) {
    return db.values().stream()
        .anyMatch(t -> t.getTitle().equalsIgnoreCase(title));
}
```

`existsByTitle` sigue siendo O(n) porque no hay otra forma de buscar por título sin recorrer todos los valores. Para hacerlo O(1) necesitaríamos un segundo Map<String, Ticket> indexado por título. En esta etapa, O(n) para esta búsqueda es aceptable.

---

## Paso 9: actualizar `TicketService` para pasar el filtro

Agrega el método sobrecargado en el `Service`:

```java
public List<Ticket> getTickets(String status) {
    return this.repository.getAll(status);
}
```

El `Service` delega directamente al `Repository`. No hay lógica de negocio en un simple filtro de lectura.

---

## Paso 10: actualizar `TicketController` para aceptar `?status=`

Modifica el endpoint `GET /tickets`:

**Antes:**

```java
@GetMapping
public ResponseEntity<List<Ticket>> getAllTickets() {
    return ResponseEntity.ok(service.getTickets());
}
```

**Después:**

```java
@GetMapping
public ResponseEntity<List<Ticket>> getAllTickets(
        @RequestParam(required = false) String status) {
    return ResponseEntity.ok(service.getTickets(status));
}
```

> **¿Qué hace `@RequestParam(required = false)`?**
> Le indica a Spring que el parámetro `status` en el query string es **opcional**. Si el cliente llama a `GET /tickets`, `status` llega como `null` y se devuelven todos los tickets. Si llama a `GET /tickets?status=NEW`, `status` llega como `"NEW"` y se filtran.
>
> Sin `required = false`, Spring lanzaría un error si el cliente no incluye el parámetro en la URL.

---

## Paso 11: verificar que todo funciona

### Prueba 1: filtrar por estado existente

```
GET http://localhost:8080/tickets?status=NEW
```

Resultado esperado: `200 OK` con la lista de tickets cuyo status es `NEW`, ordenados por `createdAt`.

### Prueba 2: filtrar insensible a mayúsculas

```
GET http://localhost:8080/tickets?status=new
```

Resultado esperado: el mismo que `?status=NEW`.

### Prueba 3: sin parámetro — todos los tickets

```
GET http://localhost:8080/tickets
```

Resultado esperado: `200 OK` con todos los tickets, ordenados por `createdAt`.

### Prueba 4: estado que no existe

```
GET http://localhost:8080/tickets?status=UNKNOWN
```

Resultado esperado: `200 OK` con lista vacía `[]`. No es un error — simplemente no hay tickets con ese estado.

### Prueba 5: operaciones CRUD siguen funcionando

Confirma que `POST`, `GET/{id}`, `PUT/{id}` y `DELETE/{id}` siguen respondiendo igual que antes. La refactorización interna no debe cambiar el comportamiento observable de la API.

---

## Paso 12: reflexiona antes de cerrar

1. ¿Qué hace `Optional.ofNullable(db.get(id))` cuando el ID no existe en el mapa? ¿Y cuando sí existe?
2. Después de llamar a `db.get(id)` y obtener un `Ticket`, ¿por qué modificar ese objeto con `ticket.setTitle(...)` también modifica lo que está guardado en el mapa?
3. Si agregas 1.000.000 de tickets al mapa, ¿cuánto tarda `findById(id)`? ¿Eso cambiaría si fuera una `List`?
4. ¿Por qué `existsByTitle()` sigue siendo O(n) incluso con el mapa? ¿Qué cambio harías para que fuera O(1)?

