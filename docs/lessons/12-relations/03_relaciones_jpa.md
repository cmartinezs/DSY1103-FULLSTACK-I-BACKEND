# Lección 12 — Relaciones JPA: @ManyToOne, @OneToMany y @JoinColumn

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
@JsonIgnore
private List<Ticket> createdTickets = new ArrayList<>();
```

`mappedBy = "createdBy"` le dice a JPA: "la FK está en el campo `createdBy` de la clase `Ticket`". El lado `@OneToMany` no crea columna propia — apunta al `@ManyToOne` que ya tiene la FK.

> **Regla importante:** si tienes `@OneToMany`, siempre necesitas `@JsonIgnore` (o un DTO de respuesta) para evitar serialización circular.
>
> Sin `@JsonIgnore`: Jackson serializa `User` → busca `createdTickets` → serializa cada `Ticket` → cada `Ticket` tiene `createdBy` (el mismo `User`) → vuelve a serializar `User` → bucle infinito → error en tiempo de ejecución.

**Para esta lección, el `@OneToMany` en `User` es opcional.** Solo agrégalo si lo necesitas. Lo que sí es necesario es el `@ManyToOne` en `Ticket`.

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

## Resumen: qué va dónde

| Anotación | En qué clase va | Para qué sirve |
|---|---|---|
| `@ManyToOne` | La que tiene la FK (ej: `Ticket`) | "Este Ticket apunta a un User" |
| `@JoinColumn` | Junto con `@ManyToOne` | Define el nombre de la columna FK |
| `@OneToMany(mappedBy=...)` | La que NO tiene la FK (ej: `User`) | "Un User tiene muchos Tickets" |
| `@JsonIgnore` | Junto con `@OneToMany` | Evita bucle infinito al serializar |

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
