# Lección 12 — Relaciones JPA: @ManyToOne, @OneToMany, @OneToOne y @ManyToMany

## Las dos caras de una relación

En una relación entre dos tablas hay siempre dos perspectivas:

```
Un User puede tener muchos Tickets   →  @OneToMany  (perspectiva del User)
Un Ticket pertenece a un solo User   →  @ManyToOne  (perspectiva del Ticket)
```

Son la misma relación vista desde cada extremo. JPA necesita que definas **al menos una de las dos perspectivas**. La otra es opcional y se llama "lado inverso".

---

## `@ManyToOne` — el lado dueño de la relación

```java
// En la clase Ticket:
@ManyToOne
@JoinColumn(name = "created_by_id")
private User createdBy;
```

`@ManyToOne` significa: "muchos `Ticket` pueden apuntar a un mismo `User`".

Este lado se llama **dueño** (*owner side*) de la relación porque es el que tiene la clave foránea en la tabla. La columna `created_by_id` existe en la tabla `tickets`, no en la tabla `users`.

```
tabla tickets              tabla users
──────────────             ────────────
id                         id
title                      name
created_by_id  ──────►    id     ← la FK vive en tickets
```

---

## `@JoinColumn` — el nombre de la clave foránea

```java
@ManyToOne
@JoinColumn(name = "created_by_id")
private User createdBy;
```

`@JoinColumn(name = "created_by_id")` define el nombre exacto de la columna FK en la tabla. Si omites `@JoinColumn`, Hibernate genera un nombre automático (generalmente `fieldname_id`). Es buena práctica siempre explicitarlo.

| Atributo | Qué hace | Ejemplo |
|---|---|---|
| `name` | Nombre de la columna FK | `created_by_id` |
| `nullable` | Si la FK puede ser NULL | `nullable = false` |
| `referencedColumnName` | Columna referenciada en la tabla destino (por defecto: PK) | Raramente se usa |

---

## `@OneToMany` — el lado inverso (opcional)

Si además quieres navegar desde un `User` hacia sus tickets:

```java
// En la clase User (lado inverso — no tiene FK propia):
@OneToMany(mappedBy = "createdBy")
private List<Ticket> createdTickets = new ArrayList<>();
```

`mappedBy = "createdBy"` le dice a JPA: "la FK está en el campo `createdBy` de la clase `Ticket`". El lado `@OneToMany` no crea columna propia — apunta al `@ManyToOne` que ya tiene la FK.

> **Sobre serialización circular:** `@OneToMany` puede causar bucle infinito si el entity se serializa directamente a JSON (`User` → `createdTickets` → `Ticket.createdBy` → `User` → ...). La solución correcta — y la que usa esta lección — es **no exponer el entity**: el Service convierte `Ticket` a `TicketResult` y `User` a `UserResult` antes de retornarlos. Jackson nunca ve el entity directamente.

**En esta lección, `@OneToMany` en `User` ES REQUERIDO.** Lo implementaremos en Paso 9 del tutorial.

---

## Trade-off: @OneToMany con LAZY vs EAGER

`@OneToMany` por defecto es `LAZY` (no carga automáticamente todos los tickets).

| Estrategia | Comportamiento | Cuándo usar |
|-----------|----------------|------------|
| **LAZY** (defecto) | Carga tickets solo si llamas `user.getCreatedTickets()` | Usuario puede tener muchos tickets (100+, 1000+) |
| **EAGER** | Carga TODOS los tickets siempre | Usuario tiene pocos tickets típicamente (< 10) |

**Ejemplo LAZY (recomendado):**
```java
@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
private List<Ticket> createdTickets = new ArrayList<>();
```
Cuando cargas un User, los tickets NO se cargan. Se cargan solo si accedes a `user.getCreatedTickets()`.

**Alternativa: Si el User puede tener > 100 tickets**

No uses `@OneToMany`. En su lugar, crea función en TicketRepository:

```java
public interface TicketRepository extends JpaRepository<Ticket, Long> {
  List<Ticket> findByCreatedById(Long userId);
  Page<Ticket> findByCreatedById(Long userId, Pageable pageable);
}
```

El cliente carga tickets ON-DEMAND con paginación. Mejor rendimiento.

---

## `@Column` — personalizar columnas

Atributos más usados y cuándo aplicarlos:

```java
// Texto obligatorio con longitud máxima
@Column(nullable = false, length = 100)
private String name;

// Texto largo sin límite de longitud
@Column(nullable = false, columnDefinition = "TEXT")
private String description;

// Valor único en toda la tabla (como un email)
@Column(nullable = false, unique = true, length = 150)
private String email;

// Nombre distinto al del campo Java (para seguir convención snake_case en SQL)
@Column(name = "created_at")
private LocalDateTime createdAt;

// Número con precisión exacta (para precios)
@Column(precision = 10, scale = 2)
private BigDecimal price;
```

Si omites `@Column`, Hibernate crea la columna con el nombre del campo, acepta NULL y usa el tipo por defecto para ese tipo Java.

---

## `FetchType.LAZY` vs `FetchType.EAGER`

Cuando cargas un `Ticket`, ¿cuándo se carga el `User` asociado?

| `FetchType` | Comportamiento | SQL generado |
|---|---|---|
| `LAZY` | Carga el `User` solo cuando accedes a `ticket.getCreatedBy()` | 1 query para `Ticket` + 1 query para `User` solo si se accede |
| `EAGER` | Carga el `User` siempre, junto con el `Ticket` | 1 query con JOIN que ya incluye el `User` |

**¿Cuál usar?**

- `@ManyToOne` tiene `EAGER` por defecto en JPA, pero conviene cambiarlo a `LAZY` para evitar cargas innecesarias
- `@OneToMany` tiene `LAZY` por defecto — es el comportamiento correcto

```java
@ManyToOne(fetch = FetchType.LAZY)   // ← especificar explícitamente
@JoinColumn(name = "created_by_id")
private User createdBy;
```

> **¿Qué es el problema N+1?**
> Si cargas N tickets con EAGER y cada uno tiene un User, JPA hace 1 query para los tickets + N queries para los usuarios = N+1 queries. Con LAZY + un JOIN cuando sea necesario, lo reduces a 1 o 2 queries. Para este curso, LAZY es suficiente. En producción, esto se gestiona con `@EntityGraph` o JPQL con `JOIN FETCH`.

---

## Resumen: las 4 anotaciones de relación

| Anotación | En qué clase va | Para qué sirve | FK |
|---|---|---|---|
| `@ManyToOne` | La que tiene la FK (ej: `Ticket`) | "Este Ticket apunta a un User" | En esta tabla |
| `@JoinColumn` | Junto con `@ManyToOne` o `@OneToOne` | Define el nombre de la columna FK | — |
| `@OneToMany(mappedBy=...)` | La que NO tiene la FK (ej: `User`) | "Un User tiene muchos Tickets" | En la otra tabla |
| `@OneToOne` | La que tiene la FK única | "Esta entidad pertenece a exactamente otra" | En esta tabla (`UNIQUE`) |
| `@ManyToMany` | Cualquiera de las dos | "Muchos A ↔ muchos B" (raro en producción, reemplazar con entidad intermedia) | Tabla intermedia |


---

## El esquema resultante en la base de datos

Después de agregar las relaciones, Hibernate crea este esquema:

```sql
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE tickets (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                     VARCHAR(50) NOT NULL,
    description               TEXT NOT NULL,
    status                    VARCHAR(20) NOT NULL,
    created_at                DATETIME,
    estimated_resolution_date DATE,
    effective_resolution_date DATETIME,
    created_by_id             BIGINT,     -- FK → users.id
    assigned_to_id            BIGINT,     -- FK → users.id
    FOREIGN KEY (created_by_id)  REFERENCES users(id),
    FOREIGN KEY (assigned_to_id) REFERENCES users(id)
);
```

No escribes este SQL. Hibernate lo genera según las anotaciones.

---

## `@OneToOne` — Relación 1 a 1

Usa `@OneToOne` cuando **una entidad pertenece exactamente a otra, y viceversa**.

**Ejemplo en nuestro sistema:** un Ticket puede tener un `AuditLog` que registra exactamente cuándo fue creado y por quién, de forma única e irrepetible — un log por ticket, un ticket por log.

```java
// En la clase Ticket:
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "audit_log_id", unique = true)
private AuditLog auditLog;
```

La diferencia clave con `@ManyToOne` es que la FK tiene restricción `UNIQUE`: no puede haber dos tickets apuntando al mismo `AuditLog`.

```
tabla tickets              tabla audit_logs
──────────────             ─────────────────
id                         id
audit_log_id (UNIQUE) ──►  id
```

**Cuando elegir `@OneToOne` vs `@ManyToOne`:**

| Pregunta | `@OneToOne` | `@ManyToOne` |
|---|---|---|
| ¿Puede haber dos A apuntando a la misma B? | No | Sí |
| ¿La FK en la tabla tiene `UNIQUE`? | Sí | No |
| Ejemplo | Ticket → AuditLog | Ticket → User |

> **Consejo:** Si no estás seguro, pregúntate: "¿puede otro registro usar la misma entidad destino?". Si la respuesta es no → `@OneToOne`. Si es sí → `@ManyToOne`.

---

## `@ManyToMany` — Por qué casi nunca la usamos

`@ManyToMany` modela una relación donde muchos registros de A se relacionan con muchos de B.

**Ejemplo teórico:** un Ticket puede tener varias Etiquetas (`Tag`), y una etiqueta puede estar en varios tickets.

En JPA se vería así:

```java
// En Ticket:
@ManyToMany
@JoinTable(
    name = "ticket_tags",
    joinColumns = @JoinColumn(name = "ticket_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags = new ArrayList<>();
```

JPA crea automáticamente una **tabla intermedia** (`ticket_tags`) con dos columnas FK.

**¿Por qué casi nunca la usamos en producción?**

La **Tercera Forma Normal (3FN)** establece que toda dependencia funcional debe pasar por la clave primaria. En una `@ManyToMany` pura, la tabla intermedia solo tiene dos FKs — no puede guardar atributos adicionales sobre la relación (¿cuándo se asignó el tag?, ¿quién lo asignó?).

En cuanto necesitas guardar datos SOBRE la relación, la tabla intermedia se convierte en una entidad propia:

```
❌ @ManyToMany puro (tabla intermedia sin atributos):
   ticket_tags(ticket_id, tag_id)

✅ Entidad intermedia normalizada (3FN):
   ticket_tags(id, ticket_id, tag_id, assigned_at, assigned_by_id)
   → Ahora es @ManyToOne desde TicketTag a Ticket y a Tag
```

**Regla práctica:** si la relación tiene o podría tener atributos propios en el futuro → usa dos `@ManyToOne` apuntando a una entidad intermedia. Es más flexible y cumple 3FN.

```java
// Entidad intermedia (la forma correcta normalizada):
@Entity
@Table(name = "ticket_tags")
public class TicketTag {

  @ManyToOne
  @JoinColumn(name = "ticket_id")
  private Ticket ticket;

  @ManyToOne
  @JoinColumn(name = "tag_id")
  private Tag tag;

  @Column(name = "assigned_at")
  private LocalDateTime assignedAt;
}
```

> **Resumen:** `@ManyToMany` existe en JPA pero, gracias a la normalización de bases de datos (3FN), en la práctica casi siempre la reemplazamos por una entidad intermedia con dos `@ManyToOne`. Esto es más mantenible, extensible y coherente con el modelo relacional.

---
