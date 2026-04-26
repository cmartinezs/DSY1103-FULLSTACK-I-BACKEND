# Lección 15 — Flyway: Guión Paso a Paso

---

## Paso 1: Entender qué es Flyway

Flyway es una herramienta que **versionea cambios de base de datos** como si fuera Git, pero para SQL:

```
Versión 1: V1__create_tickets_table.sql
           → CREATE TABLE tickets (id INT, title VARCHAR...)

Versión 2: V2__add_priority_column.sql
           → ALTER TABLE tickets ADD COLUMN priority VARCHAR

Versión 3: V3__create_users_table.sql
           → CREATE TABLE users (id INT, name VARCHAR...)
```

Cada migración se ejecuta **una sola vez**, en orden. Si la BD no tiene el cambio, Flyway lo aplica automáticamente.

---

## Paso 2: Agregar Flyway al `pom.xml`

```xml
<!-- pom.xml -->

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.3</version>
</dependency>

<!-- Si usas Supabase o MySQL, también agrega el driver de PostgreSQL/MySQL -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <version>9.22.3</version>
</dependency>
```

Ejecuta:
```bash
./mvnw clean install
```

---

## Paso 3: Crear la Carpeta de Migraciones

En `src/main/resources/`, crea esta estructura:

```
Tickets/src/main/resources/
└── db/
    └── migration/
        ├── mysql/
        │   ├── V1__create_tickets_table.sql
        │   └── V2__add_status_column.sql
        └── supabase/
            ├── V1__create_tickets_table.sql
            └── V2__add_status_column.sql
```

> **¿Por qué dos carpetas?** Porque MySQL y PostgreSQL (Supabase) tienen pequeñas diferencias en SQL. H2 no necesita carpeta porque usa JPA automático.

---

## Paso 4: Configurar Flyway en YAML (Solo MySQL y Supabase)

**`application-mysql.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:tickets_db}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}
  
  flyway:
    enabled: true
    locations: classpath:db/migration/mysql
    baseline-on-migrate: true
  
  jpa:
    hibernate:
      ddl-auto: validate  # ← CAMBIAR a 'validate' (no auto, Flyway controla el esquema)
    database-platform: org.hibernate.dialect.MySQL8Dialect
```

**`application-supabase.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/${DB_NAME:postgres}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  
  flyway:
    enabled: true
    locations: classpath:db/migration/supabase
    baseline-on-migrate: true
  
  jpa:
    hibernate:
      ddl-auto: validate  # ← CAMBIAR a 'validate' (no auto, Flyway controla el esquema)
    database-platform: org.hibernate.dialect.PostgreSQL10Dialect
```

**`application-h2.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:ticketsdb
    driver-class-name: org.h2.Driver
  
  flyway:
    enabled: false  # ← Flyway DESHABILITADO para H2
  
  jpa:
    hibernate:
      ddl-auto: create-drop  # ← H2 sigue usando JPA automático
    database-platform: org.hibernate.dialect.H2Dialect
```

---

## Paso 5: Restringir DataInitializer al perfil H2

El proyecto base (`Tickets-14`) incluye un `DataInitializer` que inserta tickets y usuarios de prueba al arrancar. Como es un `@Component` sin condición de perfil, actualmente **se ejecuta en todos los perfiles** (H2, MySQL, Supabase).

En esta lección lo restringimos a H2 y delegamos el seed de MySQL/Supabase a una migración Flyway.

### 5.1 Agregar `@Profile("h2")` al DataInitializer

```java
// src/main/java/.../config/DataInitializer.java
@Component
@Profile("h2")          // ← solo corre cuando el perfil activo es h2
public class DataInitializer implements CommandLineRunner {
    // ... sin más cambios
}
```

### 5.2 Crear la migración de seed para MySQL/Supabase

Crea `V2__insert_initial_data.sql` (o el número que corresponda en tu secuencia):

**MySQL** — `src/main/resources/db/migration/mysql/V2__insert_initial_data.sql`
```sql
-- V2__insert_initial_data.sql
INSERT INTO users (name, email) VALUES
  ('Ana Garcia',    'ana.garcia@empresa.com'),
  ('Carlos Lopez',  'carlos.lopez@empresa.com');

INSERT INTO tickets (title, description, status, created_at, estimated_resolution_date, created_by_id) VALUES
  ('Error en login',      'No se puede iniciar sesion con Google',  'NEW',         NOW(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1),
  ('Mejora en dashboard', 'Agregar graficos de estadisticas',        'IN_PROGRESS', NOW(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 2),
  ('Documentacion API',   'Falta documentacion de endpoints',        'NEW',         NOW(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1);
```

**Supabase** — `src/main/resources/db/migration/supabase/V2__insert_initial_data.sql`
```sql
-- V2__insert_initial_data.sql
INSERT INTO users (name, email) VALUES
  ('Ana Garcia',    'ana.garcia@empresa.com'),
  ('Carlos Lopez',  'carlos.lopez@empresa.com');

INSERT INTO tickets (title, description, status, created_at, estimated_resolution_date, created_by_id) VALUES
  ('Error en login',      'No se puede iniciar sesion con Google',  'NEW',         NOW(), CURRENT_DATE + INTERVAL '5 days', 1),
  ('Mejora en dashboard', 'Agregar graficos de estadisticas',        'IN_PROGRESS', NOW(), CURRENT_DATE + INTERVAL '5 days', 2),
  ('Documentacion API',   'Falta documentacion de endpoints',        'NEW',         NOW(), CURRENT_DATE + INTERVAL '5 days', 1);
```

> **Resultado:** H2 sigue usando `DataInitializer` en memoria. MySQL y Supabase usan la migración Flyway, que se ejecuta una sola vez y queda registrada en `flyway_schema_history`.

---

## Paso 6: Crear tu Primera Migración

**Archivo:** `src/main/resources/db/migration/mysql/V1__create_tickets_table.sql`

```sql
-- V1__create_tickets_table.sql
-- Crea la tabla inicial de tickets

CREATE TABLE tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estimated_resolution_date DATE NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_status ON tickets(status);
```

**Archivo:** `src/main/resources/db/migration/supabase/V1__create_tickets_table.sql`

```sql
-- V1__create_tickets_table.sql
-- Crea la tabla inicial de tickets (PostgreSQL)

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estimated_resolution_date DATE NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_status ON tickets(status);
```

---

## Paso 7: Ejecutar la App (Flyway Aplica Automáticamente)

```bash
cd Tickets

# Con MySQL
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

# Con Supabase
export SPRING_PROFILES_ACTIVE=supabase
./mvnw spring-boot:run
```

**Verifica en los logs:**
```
Successfully validated 1 migration (execution time 12ms)
Successfully applied 1 migration to schema public (execution time 123ms)
```

¡Migración aplicada! ✅

---

## Paso 8: Agregar una Segunda Migración

Cuando necesites cambiar el esquema (ej: agregar columna), crea un nuevo archivo **sin editar el anterior**:

**Archivo:** `src/main/resources/db/migration/mysql/V2__add_priority_column.sql`

```sql
-- V2__add_priority_column.sql
-- Agrega columna de prioridad a tickets

ALTER TABLE tickets ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM' AFTER status;
```

**Archivo:** `src/main/resources/db/migration/supabase/V2__add_priority_column.sql`

```sql
-- V2__add_priority_column.sql
-- Agrega columna de prioridad a tickets (PostgreSQL)

ALTER TABLE tickets ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM';
```

Ejecuta de nuevo: `./mvnw spring-boot:run`

Flyway detecta V2 y la aplica automáticamente. 🚀

---

## Paso 9: Convención de Nombres

Flyway busca archivos con este patrón exacto:

```
V{versión}__{verbo}_{sujeto}.sql
│           │        │
│           │        └─ Tabla o columna afectada (snake_case, en inglés)
│           └─ Acción que realiza la migración (en inglés)
└─ Número secuencial sin ceros a la izquierda (V1, V2, V10…)
```

> **La descripción siempre va en inglés y snake_case.** Esto es estándar en entornos profesionales y facilita la colaboración en equipos multilenguaje.

### Verbos comunes

| Verbo | Uso |
|-------|-----|
| `create_` | Crear tabla o vista: `V1__create_tickets_table.sql` |
| `add_` | Agregar columna o constraint: `V2__add_priority_column.sql` |
| `alter_` | Modificar columna existente: `V3__alter_status_column_type.sql` |
| `drop_` | Eliminar tabla o columna: `V4__drop_legacy_table.sql` |
| `insert_` | Datos iniciales (seed): `V5__insert_initial_data.sql` |
| `rename_` | Renombrar tabla o columna: `V6__rename_users_table.sql` |
| `remove_` | Quitar columna o index: `V7__remove_old_column.sql` |
| `create_idx_` | Crear índice: `V8__create_idx_status.sql` |

### ✅ Correcto

```
V1__create_tickets_table.sql
V2__add_priority_column.sql
V3__create_users_table.sql
V10__create_user_index.sql
```

### ❌ Incorrecto

```
v1_crear_tabla.sql          → minúscula en V, descripción en español
V1_create_table.sql         → un solo guión bajo (se ignora)
V1 create table.sql         → espacios no permitidos
V01__create_table.sql       → cero a la izquierda
V1__CreateTable.sql         → CamelCase en lugar de snake_case
```

---

## Paso 10: Control de Versiones (Git)

Agrupa tus migraciones por versión del proyecto:

```
v1.0:
  - V1__create_tickets_table.sql
  - V2__create_users_table.sql

v1.1:
  - V3__add_priority_column.sql

v2.0:
  - V4__refactor_users_table.sql
```

**Nunca borres una migración** del repositorio. Si necesitas revertir:

```sql
-- V5__revert_incorrect_change.sql
-- Revierte el cambio de V4

DROP COLUMN priority FROM tickets;
```

---

## Paso 11: Tabla de Control (Flyway Schema History)

Flyway crea automáticamente una tabla que registra todas las migraciones:

```
flyway_schema_history
┌────┬────────────────────────────────────┬─────────┬────────────────┐
│ id │ version                            │ success │ execution_time │
├────┼────────────────────────────────────┼─────────┼────────────────┤
│ 1  │ V1__create_tickets_table            │ TRUE    │ 123ms          │
│ 2  │ V2__add_priority_column      │ TRUE    │ 45ms           │
│ 3  │ V3__create_users_table           │ TRUE    │ 89ms           │
└────┴────────────────────────────────────┴─────────┴────────────────┘
```

Flyway consulta esta tabla antes de ejecutar migraciones. Si la migración ya está ahí, **no la ejecuta de nuevo**.

---

## Resumen de Pasos

```
1. Agregar Flyway al pom.xml
         ↓
2. Configurar locations en application-*.yml
         ↓
3. Cambiar ddl-auto a 'validate' (para MySQL/Supabase)
         ↓
4. Crear carpeta db/migration/{mysql,supabase}
         ↓
5. Agregar @Profile("h2") a DataInitializer
         ↓
6. Crear V1__create_tickets_table.sql (esquema)
         ↓
7. Crear V2__insert_initial_data.sql (seed)
         ↓
8. Ejecutar app (Flyway aplica automáticamente)
         ↓
9. Cuando necesites cambios, crear V3__... (sin editar anteriores)
         ↓
✅ Flyway aplica solo las migraciones nuevas
```

---

*[← Volver a Lección 15](01_objetivo_y_alcance.md)*
