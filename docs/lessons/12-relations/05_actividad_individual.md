# Lección 12 — Actividad individual: relacionar Category con Ticket

## Contexto

Actualmente los tickets no tienen categoría. En un sistema de soporte real, cada ticket pertenece a una categoría (Hardware, Software, Red, etc.) para poder filtrar y reportar por tipo de problema.

Esta actividad agrega la relación `Ticket` → `Category`.

---

## Parte 1: agregar la relación en `Ticket`

Agrega el campo `category` a `Ticket.java`:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Category category;
```

La columna `category_id` se creará automáticamente en la tabla `tickets`.

---

## Parte 2: actualizar `TicketRequest`

Agrega el campo opcional `categoryId` al DTO:

```java
private Long categoryId;   // opcional — un ticket puede no tener categoría aún
```

---

## Parte 3: actualizar `TicketService`

En el método `create()`, agrega la resolución de la categoría junto con los usuarios:

```java
if (request.getCategoryId() != null) {
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new IllegalArgumentException(
            "No existe una categoría con ID " + request.getCategoryId()));
    ticket.setCategory(category);
}
```

Recuerda inyectar `CategoryRepository` en el constructor de `TicketService`.

---

## Parte 4: agregar filtro por categoría

Agrega al `TicketRepository`:

```java
List<Ticket> findByCategoryId(Long categoryId);
List<Ticket> findByCategoryIdAndStatusIgnoreCase(Long categoryId, String status);
```

Y agrega el endpoint en `TicketController`:

```
GET /tickets?categoryId=1          → tickets de la categoría con id=1
GET /tickets?categoryId=1&status=NEW → tickets de la categoría 1 en estado NEW
```

---

## Pruebas requeridas

| Prueba | Resultado esperado |
|---|---|
| `POST /tickets` con `categoryId` válido | Ticket creado con la categoría vinculada |
| `POST /tickets` sin `categoryId` | Ticket creado con `category: null` |
| `POST /tickets` con `categoryId` inexistente | Error descriptivo |
| `GET /tickets?categoryId=1` | Solo tickets de esa categoría |
| `GET /tickets?categoryId=1&status=NEW` | Tickets de esa categoría en estado NEW |
| Base de datos | Columna `category_id` en tabla `tickets` con valores correctos |

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| Campo `category` en `Ticket` con `@ManyToOne`, `@JoinColumn` y `@JsonIgnoreProperties` | 30% |
| `categoryId` en `TicketRequest` (opcional) | 15% |
| `TicketService.create()` resuelve la categoría correctamente | 25% |
| Filtro `GET /tickets?categoryId=` funciona | 20% |
| No hay `StackOverflowError` al serializar | 10% |

---

## Desafío opcional

Agrega el endpoint inverso: dado el id de una categoría, lista los tickets asociados a ella.

```
GET /categories/{id}/tickets
```

Para implementarlo, agrega el método al `CategoryService` (que delega al `TicketRepository.findByCategoryId(id)`) y el endpoint al `CategoryController`.
