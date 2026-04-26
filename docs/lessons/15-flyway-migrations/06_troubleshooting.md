# Lección 15 — Troubleshooting: Errores Comunes

## Error 1: "Migration checksum mismatch"

**Síntoma:**
```
org.flywaydb.core.api.FlywayException: Validate failed: 
Migration checksum mismatch for migration V1__create_tickets_table.sql
```

**Causa:** Modificaste un archivo de migración después de haberlo ejecutado.

**Solución:**
❌ **No hagas esto:**
```sql
-- V1__create_tickets_table.sql (ORIGINAL - ya ejecutado)
CREATE TABLE tickets (id INT, title VARCHAR);

-- Luego lo editaste (INCORRECTO)
CREATE TABLE tickets (id INT, title VARCHAR, description TEXT);
```

✅ **Haz esto:**
```sql
-- V1__create_tickets_table.sql (ORIGINAL - sin cambios)
CREATE TABLE tickets (id INT, title VARCHAR);

-- V2__add_description_column.sql (NUEVA migración)
ALTER TABLE tickets ADD COLUMN description TEXT;
```

**Pasos para recuperarse:**
1. Revert los cambios a V1
2. Crea V2 con los cambios
3. Ejecuta app de nuevo

---

## Error 2: "Failed to validate migration"

**Síntoma:**
```
org.flywaydb.core.api.FlywayException: Validate failed: 
Schema contains object 'flyway_schema_history' which is not found in migration
```

**Causa:** Conflicto entre Flyway y JPA.

**Solución:**
Asegúrate que `ddl-auto` sea `validate`, no `update`:

```yaml
jpa:
  hibernate:
    ddl-auto: validate  # ← DEBE ser validate, no update
```

Si ya cambió a `update`, cambia a `validate` y reinicia.

---

## Error 3: "No migrations found"

**Síntoma:**
```
org.flywaydb.core.api.FlywayException: 
No migrations found at location 'classpath:db/migration/mysql'
```

**Causa:** Carpeta de migraciones no existe o está mal nombrada.

**Solución:**
Verifica la estructura:

```
✅ Correcto:
src/main/resources/
└── db/
    └── migration/
        ├── mysql/
        │   └── V1__create_table.sql
        └── supabase/
            └── V1__create_table.sql

❌ Incorrecto:
src/main/resources/
└── migrations/         (mal nombre)
    └── V1__create_table.sql

❌ Incorrecto:
src/main/resources/
└── db/
    └── migration/
        └── V1__create_table.sql (sin subcarpeta mysql/supabase)
```

---

## Error 4: "Syntax Error in migration"

**Síntoma:**
```
org.flywaydb.core.api.FlywayException: 
Unable to parse statement in migration file 'V1__create_table.sql'
```

**Causa:** SQL sintácticamente incorrecto.

**Solución:**
1. Prueba el SQL directamente en tu BD (phpMyAdmin, DBeaver, etc)
2. Verifica que sea la sintaxis correcta para **MySQL** o **PostgreSQL**

**Errores comunes:**

MySQL vs PostgreSQL:
```sql
❌ MySQL: AUTO_INCREMENT (MySQL es específico)
✅ PostgreSQL: SERIAL

❌ PostgreSQL: CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP (MySQL específico)
✅ PostgreSQL: DEFAULT CURRENT_TIMESTAMP
```

---

## Error 5: "Foreign key constraint fails"

**Síntoma:**
```
org.flywaydb.core.api.FlywayException: 
Unable to execute migration: Syntax error or access violation: 
1064 Cannot delete or update a parent row: a foreign key constraint fails
```

**Causa:** Estás intentando crear o modificar un FK con datos incompatibles.

**Solución:**
Verifica el orden de las migraciones:

```sql
❌ Incorrecto (V1 intenta FK a tabla que no existe):
-- V1__add_users_fk.sql
ALTER TABLE tickets ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES users(id);

✅ Correcto (crear tabla primero):
-- V1__create_users_table.sql
CREATE TABLE users (id INT PRIMARY KEY);

-- V2__create_tickets_table.sql
CREATE TABLE tickets (id INT, user_id INT);

-- V3__add_users_fk.sql
ALTER TABLE tickets ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES users(id);
```

---

## Error 6: "Connection refused" durante migración

**Síntoma:**
```
java.sql.SQLException: Connection refused
```

**Causa:** Flyway intenta conectar a la BD pero no está disponible.

**Solución:**

Para MySQL:
```bash
# Verifica que XAMPP está corriendo
# Ve a: http://localhost/phpmyadmin
# Verifica que la BD "tickets_db" existe
```

Para Supabase:
```bash
# Verifica credenciales en .env
echo $DB_HOST
echo $DB_USER
echo $DB_PASSWORD

# Verifica que el IP está en IP whitelist (Supabase → Settings)
# Verifica conexión a internet
```

---

## Error 7: "Flyway schema history table is read-only"

**Síntoma:**
```
org.flywaydb.core.api.FlywayException: 
Schema history table is read-only
```

**Causa:** No tienes permisos para escribir en la tabla.

**Solución:**
Verifica permisos de usuario en la BD:

MySQL:
```sql
GRANT ALL PRIVILEGES ON tickets_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

PostgreSQL (Supabase):
- Asegúrate que usas el usuario correcto (`postgres`)
- Verifica en Supabase → Settings que el usuario tiene permisos

---

## Error 8: "Timeout waiting for migration"

**Síntoma:**
```
org.flywaydb.core.api.FlywayException: 
Timeout waiting for migration to complete
```

**Causa:** La migración tarda demasiado (probablemente por datos grandes).

**Solución:**
1. Aumenta el timeout en `application-*.yml`:
```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 60000  # 60 segundos
      maximum-pool-size: 10
```

2. O optimiza tu SQL (agrega índices, etc)

---

## Error 9: "Cannot drop table/column in production"

**Síntoma:** No error específico, pero Flyway rechaza el cambio.

**Causa:** Por seguridad, algunos hosts bloquean DROP.

**Solución:**
Si realmente quieres borrar (después de auditar):

```sql
-- V10__remove_old_column.sql
-- Después de auditar que nada la usa

ALTER TABLE tickets DROP COLUMN deprecated_column;
```

En producción, mejor crear una migración de "soft delete":

```sql
-- V10__mark_column_deprecated.sql
ALTER TABLE tickets ADD COLUMN is_deprecated BOOLEAN DEFAULT false;

-- Luego en V11:
-- ALTER TABLE tickets DROP COLUMN old_column;  (después de confirmar)
```

---

## Verificación Rápida

```bash
# Ver todas las migraciones aplicadas
# En MySQL:
SELECT * FROM flyway_schema_history;

# En Supabase:
SELECT * FROM flyway_schema_history;
```

---

*[← Volver a Lección 15](01_objetivo_y_alcance.md)*
