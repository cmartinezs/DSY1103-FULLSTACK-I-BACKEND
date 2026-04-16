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
    tickets.add(new Ticket(currentId++, "Ticket 1", "Ticket 1", "NEW", LocalDateTime.now(), null, null, "admin", null));
    tickets.add(new Ticket(currentId++, "Ticket 2", "Ticket 2", "NEW", LocalDateTime.now(), null, null, "admin", null));
}
```

**Después:**

```java
private final Map<Long, Ticket> db = new HashMap<>();
private long currentId = 1L;

public TicketRepository() {
    LocalDateTime now = LocalDateTime.now();
    LocalDate estimated = LocalDate.now().plusDays(5);

    Ticket t1 = new Ticket(currentId, "Ticket 1", "Descripción del ticket 1", "NEW", now, estimated, null, "admin", null);
    db.put(currentId++, t1);

    Ticket t2 = new Ticket(currentId, "Ticket 2", "Descripción del ticket 2", "NEW", now, estimated, null, "admin", null);
    db.put(currentId++, t2);
    // currentId queda en 3, listo para el siguiente ticket nuevo
}
```

> **¿Por qué `currentId` empieza en `1L` y no en `0L`?**
> Los IDs que empiezan en cero son inusuales y confusos: cuando el cliente recibe `"id": 0`, puede asumir que es un estado nulo o por defecto. Empezar en `1` es el estándar: bases de datos, frameworks y APIs del mundo real usan IDs que parten desde 1.

> **¿Por qué `Map<Long, Ticket>` y no `Map<Integer, Ticket>`?**
> El campo `id` del `Ticket` es `Long`. Si usáramos `Integer`, habría que convertir constantemente entre tipos, lo que añade ruido sin valor. La clave del Map debe ser del mismo tipo que el ID del modelo.

> **¿Por qué `final` en la declaración del mapa?**
> `private final Map<Long, Ticket> db` no significa que el mapa sea inmutable — puedes seguir agregando y eliminando entradas. Significa que la referencia `db` no se puede reasignar a otro objeto. Es una buena práctica en Java: si la referencia no necesita cambiar, márcala como `final`.

---

## Paso 3: refactorizar `getAll()` y agregar `getAll(String statusFilter)`

```java
public List<Ticket> getAll() {
    return db.values().stream()
        .sorted(Comparator.comparing(Ticket::getCreatedAt))
        .toList();
}

public List<Ticket> getAll(String statusFilter) {
    if (statusFilter == null || statusFilter.isBlank()) {
        return getAll();
    }
    return db.values().stream()
        .filter(t -> t.getStatus().equalsIgnoreCase(statusFilter))
        .sorted(Comparator.comparing(Ticket::getCreatedAt))
        .toList();
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

> **¿Por qué `.toList()` y no `.collect(Collectors.toList())`?**
> `.toList()` es un método disponible desde Java 16 que devuelve una lista inmodificable. Es más conciso que `collect(Collectors.toList())` y comunica la intención más claramente. La lista devuelta no necesita ser modificable: solo se usa para serializar a JSON en la respuesta.

> **¿Por qué ordenamos por `createdAt`?**
> `db.values()` devuelve los valores del mapa en un orden no garantizado (depende de la implementación interna del `HashMap`). Ordenar por `createdAt` asegura que el cliente siempre reciba los tickets en un orden consistente y predecible.

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
public Ticket save(Ticket newTicket) {
    newTicket.setId(currentId);
    db.put(currentId++, newTicket);
    return newTicket;
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
        ticket.setTitle(request.title());
        // ...
    });
    return found;
}
```

**Después (con Map):**

La lógica de actualización se mueve al `Service` (donde pertenece según CSR), y el `Repository` solo se encarga de persistir:

```java
public void update(Ticket toUpdate) {
    db.put(toUpdate.getId(), toUpdate);
}
```

> **¿Por qué la lógica de mapeo se mueve al Service?**
> En la versión anterior, el `Repository` aplicaba los campos del DTO al ticket. Eso mezclaba lógica de transformación (responsabilidad del `Service`) con lógica de persistencia (responsabilidad del `Repository`). Con el `Map`, el `Service` ya tiene el ticket (obtenido con `findById`), lo modifica, y llama a `repository.update(toUpdate)` solo para persistir.

---

## Paso 7: refactorizar `deleteById()`

**Antes:**

```java
public boolean delete(Long id) {
    return tickets.removeIf(t -> t.getId().equals(id));
}
```

**Después:**

```java
public boolean deleteById(Long id) {
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

**Código equivalente sin expresiones lambda:**

```java
public boolean existsByTitle(String title) {
    for (Ticket ticket : db.values()) {
        if (ticket.getTitle().equalsIgnoreCase(title)) {
            return true;
        }
    }
    return false;
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
    List<Ticket> tickets = status != null
        ? this.service.getTickets(status)
        : this.service.getTickets();
    return ResponseEntity.ok(tickets);
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
GET http://localhost:8080/ticket-app/tickets?status=NEW
```

Resultado esperado: `200 OK` con la lista de tickets cuyo status es `NEW`, ordenados por `createdAt`.

### Prueba 2: filtrar insensible a mayúsculas

```
GET http://localhost:8080/ticket-app/tickets?status=new
```

Resultado esperado: el mismo que `?status=NEW`.

### Prueba 3: sin parámetro — todos los tickets

```
GET http://localhost:8080/ticket-app/tickets
```

Resultado esperado: `200 OK` con todos los tickets, ordenados por `createdAt`.

### Prueba 4: estado que no existe

```
GET http://localhost:8080/ticket-app/tickets?status=UNKNOWN
```

Resultado esperado: `200 OK` con lista vacía `[]`. No es un error — simplemente no hay tickets con ese estado.

### Prueba 5: operaciones CRUD siguen funcionando

Confirma que `POST`, `GET /by-id/{id}`, `PUT /by-id/{id}` y `DELETE /by-id/{id}` siguen respondiendo igual que antes. La refactorización interna no debe cambiar el comportamiento observable de la API.

---

## Paso 12: reflexiona antes de cerrar

1. ¿Qué hace `Optional.ofNullable(db.get(id))` cuando el ID no existe en el mapa? ¿Y cuando sí existe?
2. Después de llamar a `db.get(id)` y obtener un `Ticket`, ¿por qué modificar ese objeto con `ticket.setTitle(...)` también modifica lo que está guardado en el mapa?
3. Si agregas 1.000.000 de tickets al mapa, ¿cuánto tarda `findById(id)`? ¿Eso cambiaría si fuera una `List`?
4. ¿Por qué `existsByTitle()` sigue siendo O(n) incluso con el mapa? ¿Qué cambio harías para que fuera O(1)?
5. ¿Qué ventaja tiene `.toList()` sobre `.collect(Collectors.toList())`?
