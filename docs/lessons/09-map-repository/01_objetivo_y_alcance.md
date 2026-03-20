# Lección 09 - Repository con Map: ¿qué vas a aprender?

## ¿De dónde venimos?

En la lección anterior separaste la entrada de la API del modelo de dominio con `TicketRequest`, y agregaste validación automática con `@NotBlank` y `@Valid`. Tu API ahora:

- Rechaza datos inválidos antes de llegar al Service
- Devuelve errores estructurados en todos los casos
- Protege los campos del modelo que el cliente no debería controlar

Todo eso funciona sobre un `Repository` que guarda tickets en una `List<Ticket>`. Y ahí está el próximo problema.

---

## El problema con la `List`

Cuando buscas un ticket por ID con la implementación actual, el Repository recorre **todos los tickets** uno por uno hasta encontrar el que coincide:

```java
// Con List: O(n) — en el peor caso recorre toda la lista
public Optional<Ticket> findById(Long id) {
    return tickets.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst();
}
```

Si tienes 10 tickets, recorre hasta 10. Si tienes 10.000, recorre hasta 10.000. El tiempo de búsqueda crece de forma **lineal** con la cantidad de datos: eso se llama complejidad O(n).

Para una API de soporte técnico pequeña, esto no es un problema. Pero el patrón importa: aprenderlo mal ahora crea hábitos malos para cuando trabajen con bases de datos reales.

Hay una estructura de datos diseñada específicamente para acceso por clave: el `HashMap`.

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás:

1. Un `TicketRepository` que usa `Map<Long, Ticket>` como almacenamiento: acceso O(1) por ID
2. Todos los métodos del repository refactorizados para aprovechar el Map
3. Soporte para filtrar tickets por estado con `GET /tickets?status=NEW`
4. La lista de tickets devuelta siempre ordenada por fecha de creación

### Lo que vas a ser capaz de explicar

Al terminar deberías poder responder:

- ¿Qué significa O(n) vs O(1) en el contexto de una búsqueda?
- ¿Por qué `HashMap.get(key)` es O(1) y `List.stream().filter(...)` es O(n)?
- ¿Cómo se usa `Map<Long, Ticket>` en lugar de `List<Ticket>` para almacenar y buscar tickets?
- ¿Por qué `Optional.ofNullable(db.get(id))` es el patrón correcto para `findById`?
- ¿Cómo se agrega un parámetro de query opcional con `@RequestParam(required = false)`?

---

## ¿Qué requerimientos implementamos en esta lección?

> El proyecto completo está descrito en [`00_enunciado_proyecto.md`](../00_enunciado_proyecto.md).

| Requerimiento | Lo que construimos |
|---|---|
| **REQ-14** — Filtro por estado `?status=` | El parámetro `@RequestParam(required = false) String status` en el controlador + `getAll(String status)` en el repository |

La refactorización a `Map` no agrega un requerimiento funcional nuevo — el comportamiento de los endpoints no cambia para el cliente. Pero el código interno es más correcto, más eficiente y más cercano a cómo funciona JPA con bases de datos reales.

---

## ¿Qué NO cubre esta lección? (y por qué)

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| Paginación (`page`, `size`) | Requiere colecciones grandes y criterios de ordenamiento múltiples; lo abordamos con JPA |
| Filtros compuestos (estado + fecha + texto) | Complejidad adicional sin valor pedagógico adicional en esta etapa |
| Ordenamiento dinámico por campo | El orden por `createdAt` es suficiente; otros campos los manejará la base de datos |
| `ConcurrentHashMap` para concurrencia | La aplicación es de un solo hilo en esta etapa |
| JPA / `@Entity` / bases de datos reales | El siguiente gran paso; este Map es el puente conceptual |

---

## La estructura que tienes al comenzar

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← getAllTickets() sin filtro
├── dto/
│   └── TicketRequest.java
├── model/
│   ├── Ticket.java
│   └── ErrorResponse.java
├── respository/
│   └── TicketRepository.java   ← usa List<Ticket>, O(n) en findById
├── service/
│   └── TicketService.java      ← getTickets() sin parámetro de filtro
└── TicketsApplication.java
```

Y la estructura que tendrás al terminar (misma, pero con comportamiento diferente internamente):

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← getAllTickets(@RequestParam status)
├── dto/
│   └── TicketRequest.java
├── model/
│   ├── Ticket.java
│   └── ErrorResponse.java
├── respository/
│   └── TicketRepository.java   ← usa Map<Long, Ticket>, O(1) en findById + filter
├── service/
│   └── TicketService.java      ← getTickets(String status)
└── TicketsApplication.java
```

