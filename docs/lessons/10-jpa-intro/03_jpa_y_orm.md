# Lección 10 — JPA, ORM y anotaciones esenciales

## ¿Qué es un ORM?

**ORM** significa *Object-Relational Mapping* (Mapeo Objeto-Relacional). Es la técnica de traducir automáticamente entre dos mundos que hablan idiomas distintos:

| Mundo Java (orientado a objetos) | Mundo SQL (relacional) |
|---|---|
| Clase | Tabla |
| Objeto (instancia) | Fila |
| Campo / atributo | Columna |
| Tipo `String` | `VARCHAR` |
| Tipo `Long` | `BIGINT` |
| Tipo `LocalDateTime` | `DATETIME` |
| Referencia entre objetos (`ticket.user`) | Clave foránea (`ticket.user_id`) |

Sin ORM, escribirías SQL a mano para cada operación. Con JPA + Hibernate, describes tus clases con anotaciones y el framework genera el SQL por ti.

---

## Las anotaciones que debes conocer

### `@Entity`

```java
@Entity
public class Ticket { ... }
```

Le dice a JPA: "esta clase representa una tabla en la base de datos". Cada instancia del objeto corresponde a una fila en esa tabla.

**Regla:** toda clase anotada con `@Entity` debe tener un constructor sin argumentos (lo provee `@NoArgsConstructor` de Lombok).

---

### `@Table`

```java
@Entity
@Table(name = "tickets")
public class Ticket { ... }
```

Define el nombre exacto de la tabla en la base de datos. Si omites `@Table`, JPA usa el nombre de la clase en minúsculas (`ticket`). Es buena práctica explicitarlo siempre para evitar sorpresas.

---

### `@Id`

```java
@Id
private Long id;
```

Marca el campo que es la **clave primaria** de la tabla. Toda entidad JPA debe tener exactamente un `@Id`. Sin él, JPA lanza una excepción al arrancar.

---

### `@GeneratedValue`

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Le dice a JPA que la base de datos genera el valor del ID automáticamente. `IDENTITY` usa el mecanismo nativo de la base de datos:

- **MySQL**: `AUTO_INCREMENT`
- **PostgreSQL**: `SERIAL` o `GENERATED ALWAYS AS IDENTITY`

Con esto, nunca asignas el ID manualmente. Cuando llamas a `repository.save(ticket)`, la base de datos asigna el próximo ID disponible y JPA lo inyecta de vuelta en el objeto.

**Estrategias disponibles:**

| Estrategia | Cómo funciona |
|---|---|
| `IDENTITY` | Usa AUTO_INCREMENT / SERIAL de la base de datos. La más simple, la más usada |
| `SEQUENCE` | Usa una secuencia de la base de datos (PostgreSQL lo soporta nativamente) |
| `AUTO` | JPA elige la estrategia según la base de datos. Menos predecible |

Para este curso, siempre usa `IDENTITY`.

---

### `@Column`

```java
@Column(name = "created_at", nullable = false, length = 50)
private String title;
```

Personaliza la columna en la base de datos. Los atributos más usados:

| Atributo | Qué hace | Valor por defecto |
|---|---|---|
| `name` | Nombre de la columna en SQL | Nombre del campo en Java |
| `nullable` | Si la columna acepta `NULL` | `true` |
| `length` | Longitud máxima para `VARCHAR` | `255` |
| `unique` | Si los valores deben ser únicos | `false` |
| `columnDefinition` | Define el tipo SQL exacto | (lo elige Hibernate) |

Si omites `@Column`, JPA crea la columna con el nombre del campo y valores por defecto.

---

## Qué viene incluido en `JpaRepository`

Cuando tu repositorio extiende `JpaRepository<Ticket, Long>`, obtienes estos métodos sin escribir nada:

| Método | SQL equivalente |
|---|---|
| `save(ticket)` | `INSERT` o `UPDATE` según si tiene ID |
| `findById(id)` | `SELECT * FROM tickets WHERE id = ?` |
| `findAll()` | `SELECT * FROM tickets` |
| `existsById(id)` | `SELECT COUNT(*) WHERE id = ?` |
| `deleteById(id)` | `DELETE FROM tickets WHERE id = ?` |
| `count()` | `SELECT COUNT(*) FROM tickets` |

Además, puedes agregar métodos propios siguiendo una convención de nombres que Spring Data interpreta automáticamente:

```java
// Spring Data genera: SELECT * FROM tickets WHERE status = ? (insensible a mayúsculas)
List<Ticket> findByStatusIgnoreCase(String status);

// Spring Data genera: SELECT * FROM tickets WHERE title = ?
boolean existsByTitle(String title);

// Spring Data genera: SELECT * FROM tickets ORDER BY created_at ASC
List<Ticket> findAllByOrderByCreatedAtAsc();

// Spring Data genera: SELECT * FROM tickets WHERE status = ? ORDER BY created_at DESC
List<Ticket> findByStatusOrderByCreatedAtDesc(String status);
```

La convención es: `findBy` + `NombreDeCampo` + (modificadores opcionales como `IgnoreCase`, `OrderBy`, etc.).

---

## `show-sql: true` — aprende leyendo el SQL generado

En `application.yml` configuraste:

```yaml
jpa:
  show-sql: true
  properties:
    hibernate:
      format_sql: true
```

Esto muestra en consola el SQL que JPA genera para cada operación. Es invaluable para aprender:

```sql
-- Al llamar a repository.save(ticket) con un ticket nuevo:
insert
into
    tickets
    (created_at, description, effective_resolution_date, estimated_resolution_date, status, title)
values
    (?, ?, ?, ?, ?, ?)

-- Al llamar a repository.findById(1L):
select
    t1_0.id,
    t1_0.created_at,
    ...
from
    tickets t1_0
where
    t1_0.id=?
```

En producción desactivarías `show-sql` para no exponer la estructura de la base de datos en los logs.

---

## El puente desde el Map al JPA

El Map que usabas antes y JPA comparten el mismo concepto fundamental: acceso por clave primaria.

| Concepto | Con `Map<Long, Ticket>` | Con JPA |
|---|---|---|
| Guardar | `db.put(id, ticket)` | `repository.save(ticket)` |
| Buscar por ID | `db.get(id)` | `repository.findById(id)` |
| Eliminar | `db.remove(id)` | `repository.deleteById(id)` |
| ¿Existe? | `db.containsKey(id)` | `repository.existsById(id)` |
| Listar todos | `new ArrayList<>(db.values())` | `repository.findAll()` |
| Dónde viven los datos | RAM (se pierden al reiniciar) | Disco (persisten para siempre) |

El cambio conceptual es mínimo. El beneficio es enorme.

---

## El patrón `*Result` — por qué no retornamos entidades JPA

Cuando desarrollas una API REST, el Service retorna datos al Controller, quien los pone en `ResponseEntity`. **NUNCA retorno una entidad JPA directamente**. ¿Por qué?

### El problema: entidad JPA vs mundo exterior

Una entidad JPA como `Ticket` tiene muchas responsabilidades que no queremos exponer:

```java
@Entity
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;           // 🔴 JPA internals

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "created_by_id")
  private User createdBy;     // 🔴 Relación JPA — serialization circular

  @Entity
  public class User {
    @OneToMany(mappedBy = "createdBy")
    private List<Ticket> ticketsCreated; // 🔴 Relación inversa
  }
}
```

| Problema | Qué pasa |
|---|---|
| Proxies JPA lazy | Al serializar a JSON, falla si el proxy no está inicializado |
| Serialización circular | `Ticket` → `User` → `Ticket` → ... → `StackOverflowError` |
| Exposición de internals | El cliente ve campos como `hibernateLazyInitializer` |
| Acoplamiento con BD | Cambiar la entidad rompe la API pública |

### La solución: transformar a `*Result`

Creamos un DTO de salida (data transfer object) que solo contiene datos:

```java
public record TicketResult(
    Long id,
    String title,
    String description,
    String status,
    // Solo datos planos, sin relaciones JPA
    String createdBy,
    String assignedTo
) {}
```

El Service transforma la entidad a Result:

```java
public List<TicketResult> getTickets() {
  return repository.findAll().stream()
      .map(ticket -> new TicketResult(
          ticket.getId(),
          ticket.getTitle(),
          ticket.getDescription(),
          ticket.getStatus(),
          ticket.getCreatedBy(),   // String, no User
          ticket.getAssignedTo()      // String, no User
      ))
      .toList();
}
```

### El flujo completo

```
┌─────────────────────────────────────────────────────────────┐
│  Request          Entity JPA           Result           │
│  (DTO input)  →  (Repository)  →  (DTO output)  →  JSON  │
│                             ↓                            │
│                       Service transforma              │
└─────────────────────────────────────────────────────────────┘
```

| Capa | Qué usa | Por qué |
|---|---|---|
| Controller | Recibe `*Request` | Valida input con `@Valid` |
| Service | Recibe y retorna entidades | Necesita JPA para operar |
| Controller | Retorna `*Result` | Solo datos planos, sin JPA |
| HTTP Response | Serializa a JSON | El cliente recibe datos limpios |

### ¿Cuándo agregar `*Result`?

A partir de esta lección, todo endpoint que retorna datos debe usar el patrón `*Result`:

- `GET /tickets` → `List<TicketResult>`
- `GET /tickets/{id}` → `TicketResult`
- `POST /tickets` → `TicketResult`
- `PUT /tickets/{id}` → `TicketResult`

El `*Request` sigue siendo para entrada; el `*Result` para salida.
