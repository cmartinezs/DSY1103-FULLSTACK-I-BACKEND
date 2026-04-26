# Lección 15 — Ejemplos de Migraciones SQL

## Patrón de Nombres

```
V{versión}__{verbo}_{sujeto}.sql
```

| Parte | Regla | Ejemplo |
|-------|-------|---------|
| `V` | Mayúscula obligatoria | `V1`, `V2`, `V10` |
| `{versión}` | Número entero, sin ceros a la izquierda | `1` ✅ — `01` ❌ |
| `__` | Dos guiones bajos (separador obligatorio) | `V1__` ✅ — `V1_` ❌ |
| `{verbo}` | Acción en inglés, snake_case | `create_`, `add_`, `drop_` |
| `{sujeto}` | Tabla o columna afectada, en inglés | `tickets_table`, `priority_column` |
| `.sql` | Extensión en minúsculas | `.sql` ✅ — `.SQL` ❌ |

> **Siempre en inglés.** Es el estándar de la industria. Los nombres en español generan problemas en entornos donde el equipo o las herramientas CI/CD son multilenguaje.

### Verbos más usados

```
create_   →  V1__create_tickets_table.sql
add_      →  V2__add_priority_column.sql
alter_    →  V3__alter_status_column_type.sql
insert_   →  V4__insert_initial_data.sql
drop_     →  V5__drop_legacy_column.sql
rename_   →  V6__rename_users_table.sql
```

---

## Migración 1: Tabla Inicial (Tickets)

### MySQL

```sql
-- V1__create_tickets_table.sql
CREATE TABLE tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estimated_resolution_date DATE NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_title UNIQUE (title),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

### PostgreSQL (Supabase)

```sql
-- V1__create_tickets_table.sql
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estimated_resolution_date DATE NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_valid_status CHECK (status IN ('NEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'))
);

CREATE INDEX idx_status ON tickets(status);
CREATE INDEX idx_created_at ON tickets(created_at);
```

---

## Migración 2: Agregar Columnas

### MySQL

```sql
-- V2__add_priority_column.sql
ALTER TABLE tickets 
ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM' AFTER status,
ADD COLUMN assigned_to VARCHAR(255);

CREATE INDEX idx_assigned_to ON tickets(assigned_to);
```

### PostgreSQL (Supabase)

```sql
-- V2__add_priority_column.sql
ALTER TABLE tickets
ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM',
ADD COLUMN assigned_to VARCHAR(255);

CREATE INDEX idx_assigned_to ON tickets(assigned_to);
```

---

## Migración 3: Crear Tabla Usuarios

### MySQL

```sql
-- V3__create_users_table.sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email)
);
```

### PostgreSQL (Supabase)

```sql
-- V3__create_users_table.sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_email ON users(email);
```

---

## Migración 4: Agregar Foreign Key

### MySQL

```sql
-- V4__add_tickets_users_relation.sql
-- Vincula tickets con usuarios

ALTER TABLE tickets
ADD COLUMN created_by_id INT NOT NULL DEFAULT 1,
ADD COLUMN assigned_to_id INT;

ALTER TABLE tickets
ADD CONSTRAINT fk_tickets_created_by 
FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE RESTRICT,
ADD CONSTRAINT fk_tickets_assigned_to 
FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL;

-- Agregar índice para performance
CREATE INDEX idx_created_by_id ON tickets(created_by_id);
CREATE INDEX idx_assigned_to_id ON tickets(assigned_to_id);
```

### PostgreSQL (Supabase)

```sql
-- V4__add_tickets_users_relation.sql
ALTER TABLE tickets
ADD COLUMN created_by_id INTEGER NOT NULL DEFAULT 1,
ADD COLUMN assigned_to_id INTEGER;

ALTER TABLE tickets
ADD CONSTRAINT fk_tickets_created_by 
FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE RESTRICT,
ADD CONSTRAINT fk_tickets_assigned_to 
FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_created_by_id ON tickets(created_by_id);
CREATE INDEX idx_assigned_to_id ON tickets(assigned_to_id);
```

---

## Migración de Seed: Datos Iniciales

Flyway también gestiona la inserción de datos iniciales (seed). Es el mecanismo correcto para MySQL y Supabase.

> ⚠️ **El `DataInitializer` actual corre en todos los perfiles**
>
> `Tickets-14` incluye `DataInitializer.java` (`@Component` + `CommandLineRunner`) que inserta datos de prueba al arrancar. Sin la anotación `@Profile`, se ejecuta en **todos** los perfiles (H2, MySQL, Supabase).
>
> En esta lección debes:
> 1. Agregar `@Profile("h2")` a `DataInitializer` → solo corre en H2
> 2. Crear `V2__insert_initial_data.sql` con los mismos datos para MySQL y Supabase
>
> | Mecanismo | H2 | MySQL | Supabase |
> |-----------|:--:|:-----:|:--------:|
> | `DataInitializer` (`@Profile("h2")`) | ✅ | ❌ | ❌ |
> | `V2__insert_initial_data.sql` (Flyway) | ❌ | ✅ | ✅ |

### MySQL

```sql
-- V5__insert_initial_data.sql
-- Inserta usuarios y tickets de ejemplo

INSERT INTO users (email, name, password_hash) VALUES
('admin@example.com', 'Admin User', '$2a$10$...'),
('developer@example.com', 'Developer User', '$2a$10$...');

INSERT INTO tickets (title, description, status, priority, created_by_id) VALUES
('Bug: Login no funciona', 'El login falla con email/password incorrectos', 'NEW', 'HIGH', 1),
('Feature: Dark mode', 'Agregar tema oscuro a la aplicación', 'IN_PROGRESS', 'MEDIUM', 2);
```

### PostgreSQL (Supabase)

```sql
-- V5__insert_initial_data.sql
INSERT INTO users (email, name, password_hash) VALUES
('admin@example.com', 'Admin User', '$2a$10$...'),
('developer@example.com', 'Developer User', '$2a$10$...');

INSERT INTO tickets (title, description, status, priority, created_by_id) VALUES
('Bug: Login no funciona', 'El login falla con email/password incorrectos', 'NEW', 'HIGH', 1),
('Feature: Dark mode', 'Agregar tema oscuro a la aplicación', 'IN_PROGRESS', 'MEDIUM', 2);
```

---

## Migración 6: Cambiar Tipo de Dato

### MySQL

```sql
-- V6__change_description_column_type.sql
-- Amplía la columna description de TEXT a LONGTEXT

ALTER TABLE tickets MODIFY COLUMN description LONGTEXT;
```

### PostgreSQL (Supabase)

```sql
-- V6__change_description_column_type.sql
ALTER TABLE tickets ALTER COLUMN description TYPE TEXT;
```

---

## Migración 7: Crear Vista (View)

### MySQL

```sql
-- V7__create_tickets_by_user_view.sql
-- Vista que muestra tickets agrupados por usuario

CREATE VIEW tickets_by_user AS
SELECT 
    u.id as user_id,
    u.name as user_name,
    COUNT(t.id) as total_tickets,
    SUM(CASE WHEN t.status = 'NEW' THEN 1 ELSE 0 END) as new_tickets
FROM users u
LEFT JOIN tickets t ON t.created_by_id = u.id
GROUP BY u.id, u.name;
```

### PostgreSQL (Supabase)

```sql
-- V7__create_tickets_by_user_view.sql
CREATE VIEW tickets_by_user AS
SELECT 
    u.id as user_id,
    u.name as user_name,
    COUNT(t.id) as total_tickets,
    SUM(CASE WHEN t.status = 'NEW' THEN 1 ELSE 0 END) as new_tickets
FROM users u
LEFT JOIN tickets t ON t.created_by_id = u.id
GROUP BY u.id, u.name;
```

---

## Migración 8: Revertir Cambio (Rollback Manual)

Si cometiste un error en V4 y necesitas revertirlo:

### MySQL

```sql
-- V8__revert_foreign_keys_v4.sql
-- Revierte los cambios de V4

ALTER TABLE tickets DROP FOREIGN KEY fk_tickets_assigned_to;
ALTER TABLE tickets DROP FOREIGN KEY fk_tickets_created_by;
ALTER TABLE tickets DROP COLUMN assigned_to_id;
ALTER TABLE tickets DROP COLUMN created_by_id;
```

### PostgreSQL (Supabase)

```sql
-- V8__revert_foreign_keys_v4.sql
ALTER TABLE tickets DROP CONSTRAINT fk_tickets_assigned_to;
ALTER TABLE tickets DROP CONSTRAINT fk_tickets_created_by;
ALTER TABLE tickets DROP COLUMN assigned_to_id;
ALTER TABLE tickets DROP COLUMN created_by_id;
```

---

## Diferencias Clave: MySQL vs PostgreSQL

| Aspecto | MySQL | PostgreSQL |
|---------|-------|-----------|
| Auto-increment | `AUTO_INCREMENT` | `SERIAL` |
| Timestamp actual | `DEFAULT CURRENT_TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` |
| Alterar columna | `MODIFY COLUMN` | `ALTER COLUMN ... TYPE` |
| Foreign keys | `CONSTRAINT fk_... FOREIGN KEY` | `CONSTRAINT fk_... FOREIGN KEY` |
| Índices | `INDEX idx_name` | `CREATE INDEX idx_name` |
| Comentarios | `-- comentario` | `-- comentario` |
| Booleanos | `TINYINT(1)` | `BOOLEAN` |
| Strings ilimitados | `LONGTEXT` | `TEXT` |

---

## ✅ Checklist para Migraciones

Antes de crear una migración, verifica:

- ✅ El nombre sigue patrón `V[número]__[descripción].sql`
- ✅ Dos guiones bajos obligatorios `__`
- ✅ Número es secuencial (V1, V2, V3, no saltes)
- ✅ SQL sintácticamente correcto (prueba en tu BD)
- ✅ Existe para MySQL y PostgreSQL si ambos usan Flyway
- ✅ No editas migraciones viejas (solo creas nuevas)
- ✅ Commitas los archivos al repositorio
- ✅ Documentaste qué hace en un comentario al inicio

---

*[← Volver a Lección 15](01_objetivo_y_alcance.md)*
