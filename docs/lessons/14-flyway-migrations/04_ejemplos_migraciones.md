# Lección 12 — Ejemplos de Migraciones SQL

## Patrón de Nombres

```
V[número]__[descripción_clara].sql
  1        2                    3

1. V + número (V1, V2, V10, etc)
2. Dos guiones bajos obligatorios (__)
3. Descripción sin espacios, con guiones bajos
```

---

## Migración 1: Tabla Inicial (Tickets)

### MySQL

```sql
-- V1__Crear_tabla_tickets.sql
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
-- V1__Crear_tabla_tickets.sql
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
-- V2__Agregar_columna_prioridad.sql
ALTER TABLE tickets 
ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM' AFTER status,
ADD COLUMN assigned_to VARCHAR(255);

CREATE INDEX idx_assigned_to ON tickets(assigned_to);
```

### PostgreSQL (Supabase)

```sql
-- V2__Agregar_columna_prioridad.sql
ALTER TABLE tickets
ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM',
ADD COLUMN assigned_to VARCHAR(255);

CREATE INDEX idx_assigned_to ON tickets(assigned_to);
```

---

## Migración 3: Crear Tabla Usuarios

### MySQL

```sql
-- V3__Crear_tabla_usuarios.sql
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
-- V3__Crear_tabla_usuarios.sql
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
-- V4__Agregar_relacion_tickets_usuarios.sql
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
-- V4__Agregar_relacion_tickets_usuarios.sql
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

## Migración 5: Datos Iniciales (Seed Data)

### MySQL

```sql
-- V5__Insertar_datos_iniciales.sql
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
-- V5__Insertar_datos_iniciales.sql
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
-- V6__Cambiar_tipo_columna_description.sql
-- Amplía la columna description de TEXT a LONGTEXT

ALTER TABLE tickets MODIFY COLUMN description LONGTEXT;
```

### PostgreSQL (Supabase)

```sql
-- V6__Cambiar_tipo_columna_description.sql
ALTER TABLE tickets ALTER COLUMN description TYPE TEXT;
```

---

## Migración 7: Crear Vista (View)

### MySQL

```sql
-- V7__Crear_vista_tickets_por_usuario.sql
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
-- V7__Crear_vista_tickets_por_usuario.sql
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
-- V8__Revertir_foreign_key_v4.sql
-- Revierte los cambios de V4

ALTER TABLE tickets DROP FOREIGN KEY fk_tickets_assigned_to;
ALTER TABLE tickets DROP FOREIGN KEY fk_tickets_created_by;
ALTER TABLE tickets DROP COLUMN assigned_to_id;
ALTER TABLE tickets DROP COLUMN created_by_id;
```

### PostgreSQL (Supabase)

```sql
-- V8__Revertir_foreign_key_v4.sql
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

*[← Volver a Lección 12](01_objetivo_y_alcance.md)*
