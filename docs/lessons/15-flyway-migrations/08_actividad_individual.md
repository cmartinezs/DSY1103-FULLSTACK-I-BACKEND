# Lección 15 — Actividad Individual: Migración Flyway de `Category`

## Contexto

En la lección 05 creaste el recurso `Category` con persistencia en memoria. En la lección 10 lo migraste a JPA con `@Entity` y `JpaRepository`. Ahora que el proyecto usa Flyway para controlar el esquema, **JPA ya no puede crear ni modificar tablas automáticamente** en MySQL y Supabase (`ddl-auto: validate`).

Tu tarea es escribir las migraciones Flyway que crean la tabla `categories` y la vinculan con `tickets`, continuando la secuencia de versiones del proyecto.

---

## Lo que debes entregar

Las migraciones van dentro de `src/main/resources/db/migration/` y siguen la convención establecida en clase:

```
V{versión}__lesson_15_{verbo}_{sujeto}.sql
```

Las versiones deben continuar la secuencia existente (V7 es la última del guión), así que tu actividad parte desde **V8**.

---

## Parte 1: Crear la tabla `categories`

Crea los archivos para ambos motores:

- `db/migration/mysql/V8__lesson_15_create_categories_table.sql`
- `db/migration/supabase/V8__lesson_15_create_categories_table.sql`

La tabla debe reflejar exactamente la entidad `Category` que definiste en la lección 10:

| Campo | Tipo Java | Columna MySQL | Columna PostgreSQL |
|-------|-----------|---------------|--------------------|
| `id` | `Long` | `BIGINT AUTO_INCREMENT` | `BIGSERIAL` |
| `name` | `String` | `VARCHAR(100) NOT NULL UNIQUE` | `VARCHAR(100) NOT NULL UNIQUE` |
| `description` | `String` | `TEXT` | `TEXT` |

Incluye al menos un índice sobre `name`.

---

## Parte 2: Seed de categorías iniciales

Cada bloque de lección debe terminar con un seed. Crea:

- `db/migration/mysql/V9__lesson_15_insert_initial_categories.sql`
- `db/migration/supabase/V9__lesson_15_insert_initial_categories.sql`

Inserta las mismas categorías que usabas como datos de prueba en memoria desde la lección 05:

| name | description |
|------|-------------|
| `Bug` | `Problema o error que afecta el funcionamiento esperado` |
| `Feature` | `Nueva funcionalidad solicitada por el usuario` |
| `Mejora` | `Cambio menor que optimiza una funcionalidad existente` |

---

## Parte 3 (opcional): Vincular `Category` con `Ticket`

Si en la lección 12 ya agregaste `category_id` a `Ticket`, crea la migración que lo formaliza en la BD:

- `db/migration/mysql/V10__lesson_15_add_category_to_tickets.sql`
- `db/migration/supabase/V10__lesson_15_add_category_to_tickets.sql`

```sql
-- MySQL
ALTER TABLE tickets
    ADD COLUMN category_id BIGINT,
    ADD CONSTRAINT fk_tickets_category
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;
```

```sql
-- PostgreSQL
ALTER TABLE tickets
    ADD COLUMN category_id BIGINT,
    ADD CONSTRAINT fk_tickets_category
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;
```

---

## Verificación

Arranca la app con MySQL o Supabase y comprueba los logs:

```
Successfully applied 2 migrations to schema ... (V8, V9)
```

Luego verifica en tu cliente de BD:

| Verificación | Resultado esperado |
|---|---|
| `SELECT * FROM flyway_schema_history` | 9 filas (V1-V9) |
| `SELECT * FROM categories` | 3 filas (Bug, Feature, Mejora) |
| `DESCRIBE categories` / `\d categories` | columnas `id`, `name`, `description` |
| App arranca sin errores con `ddl-auto: validate` | Hibernate valida sin crear nada |

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| V8 crea la tabla con columnas y tipos correctos (MySQL y Supabase) | 35% |
| V9 inserta las 3 categorías iniciales | 25% |
| Los nombres de archivo siguen el patrón `V{n}__lesson_15_{verbo}_{sujeto}.sql` | 20% |
| La app arranca sin errores en el perfil mysql o supabase | 20% |

---

## Desafío opcional

Si V10 (relación `category_id` en `tickets`) ya funciona, agrega el seed que vincula los tickets existentes a una categoría:

```sql
-- V11__lesson_15_update_ticket_categories.sql
UPDATE tickets SET category_id = 1 WHERE title = 'Error en login';
UPDATE tickets SET category_id = 3 WHERE title = 'Mejora en dashboard';
UPDATE tickets SET category_id = 2 WHERE title = 'Documentacion API';
```

> **¿Por qué UPDATE y no INSERT?** Los tickets ya existen (fueron insertados en V2 y vinculados a usuarios en V5). Aquí solo les asignamos su categoría, no los recreamos.

---

*[← Volver a Lección 15](01_objetivo_y_alcance.md)*

