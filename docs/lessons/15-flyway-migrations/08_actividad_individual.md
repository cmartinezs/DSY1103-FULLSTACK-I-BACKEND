# Lección 15 — Actividad Individual

## 🎯 Objetivo

Implementar **Flyway** para tu aplicación Tickets y crear al menos 2 migraciones SQL versionadas.

---

## 📋 Requisitos

### 1. Configuración Base

- [ ] Agrega Flyway al `pom.xml`
- [ ] Configura Flyway en `application-mysql.yml` y `application-supabase.yml`
- [ ] Deshabilita Flyway en `application-h2.yml`
- [ ] Cambia `ddl-auto` a `validate` en MySQL y Supabase

### 2. Primera Migración (Tabla Tickets)

Crea `V1__create_tickets_table.sql` en ambas carpetas (mysql/ y supabase/):

```
✅ Debe crear tabla tickets con columnas:
  - id (auto-increment/serial)
  - title (VARCHAR, NOT NULL, UNIQUE)
  - description (TEXT)
  - status (VARCHAR, DEFAULT 'NEW')
  - priority (VARCHAR, DEFAULT 'MEDIUM')
  - created_at (TIMESTAMP)
  - estimated_resolution_date (DATE)
  - updated_at (TIMESTAMP)
  
✅ Debe incluir índices:
  - idx_status
  - idx_created_at
```

### 3. Segunda Migración (Tabla Usuarios)

Crea `V2__create_users_table.sql`:

```
✅ Debe crear tabla users con columnas:
  - id (auto-increment/serial)
  - email (VARCHAR, UNIQUE)
  - name (VARCHAR)
  - password_hash (VARCHAR)
  - created_at (TIMESTAMP)
  
✅ Debe incluir índice:
  - idx_email
```

### 4. Tercera Migración (Relación)

Crea `V3__add_tickets_users_relation.sql`:

```
✅ Debe agregar foreign key de tickets a usuarios:
  - created_by_id INT/INTEGER
  - Constrains: FK tickets.created_by_id → users.id
  
✅ Debe incluir índice:
  - idx_created_by_id
```

---

## 🚀 Pasos a Seguir

1. **Agrega dependencia Flyway a `pom.xml`**
   ```bash
   ./mvnw clean install
   ```

2. **Crea carpeta de migraciones**
   ```
   src/main/resources/db/migration/mysql/
   src/main/resources/db/migration/supabase/
   ```

3. **Configura YAML** (cambios en application-mysql.yml y application-supabase.yml)

4. **Escribe migraciones SQL** (V1, V2, V3)

5. **Prueba con MySQL:**
   ```bash
   export SPRING_PROFILES_ACTIVE=mysql
   ./mvnw spring-boot:run
   ```

6. **Prueba con Supabase:**
   ```bash
   export SPRING_PROFILES_ACTIVE=supabase
   ./mvnw spring-boot:run
   ```

7. **Verifica tablas en BD:**
   - phpMyAdmin (MySQL)
   - Supabase console (PostgreSQL)
   - Verifica que `flyway_schema_history` tiene 3 filas

8. **Commit a Git**
   ```bash
   git add src/main/resources/db/migration/
   git add docs/lessons/15-flyway-migrations/
   git commit -m "feat: agregar Flyway migrations v1-v3"
   ```

---

## ✅ Validación

Debes poder responder:

- [ ] "¿Por qué Flyway es mejor que `ddl-auto` en producción?"
- [ ] "¿Qué hace la tabla `flyway_schema_history`?"
- [ ] "¿Puedo editar V1 después de ejecutarla?"
- [ ] "¿Por qué H2 no usa Flyway?"
- [ ] "¿Cuál es la diferencia de sintaxis SQL entre MySQL y PostgreSQL en mis migraciones?"

---

## 📦 Entrega

Sube tu código con:
- ✅ Migraciones V1, V2, V3 en ambas carpetas (mysql y supabase)
- ✅ Configuración YAML actualizada
- ✅ `pom.xml` con Flyway
- ✅ Tests pasando (API responde correctamente)

---

## 🔥 Desafío Extra (Opcional)

- Crea `V4__create_comments_table.sql` (tabla relacionada)
- Crea `V5__add_resolved_by_column.sql` (agregar FK a usuario que resuelve)
- Documenta por qué el orden de migraciones importa

---

*[← Volver a Lección 15](01_objetivo_y_alcance.md)*
