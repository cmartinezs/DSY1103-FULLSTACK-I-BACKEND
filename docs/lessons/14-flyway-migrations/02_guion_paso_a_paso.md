# Lección 12 — Flyway: Guión Paso a Paso

---

## Paso 1: Entender qué es Flyway

Flyway es una herramienta que **versionea cambios de base de datos** como si fuera Git, pero para SQL:

```
Versión 1: V1__Crear_tabla_tickets.sql
           → CREATE TABLE tickets (id INT, title VARCHAR...)

Versión 2: V2__Agregar_columna_prioridad.sql
           → ALTER TABLE tickets ADD COLUMN priority VARCHAR

Versión 3: V3__Crear_tabla_usuarios.sql
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
        │   ├── V1__Crear_tabla_tickets.sql
        │   └── V2__Agregar_columna_estado.sql
        └── supabase/
            ├── V1__Crear_tabla_tickets.sql
            └── V2__Agregar_columna_estado.sql
```

> **¿Por qué dos carpetas?** Porque MySQL y PostgreSQL (Supabase) tienen pequeñas diferencias en SQL. H2 no necesita carpeta porque usa JPA automático.

---

## Paso 4: Configurar Flyway en YAML (Solo MySQL y Supabase)

**`application-mysql.yml`:**
```yaml
spring:
  datasource:
    url: ${MYSQL_URL:jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago}
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
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
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
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

## Paso 5: Crear tu Primera Migración

**Archivo:** `src/main/resources/db/migration/mysql/V1__Crear_tabla_tickets.sql`

```sql
-- V1__Crear_tabla_tickets.sql
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

**Archivo:** `src/main/resources/db/migration/supabase/V1__Crear_tabla_tickets.sql`

```sql
-- V1__Crear_tabla_tickets.sql
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

## Paso 6: Ejecutar la App (Flyway Aplica Automáticamente)

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

## Paso 7: Agregar una Segunda Migración

Cuando necesites cambiar el esquema (ej: agregar columna), crea un nuevo archivo **sin editar el anterior**:

**Archivo:** `src/main/resources/db/migration/mysql/V2__Agregar_columna_prioridad.sql`

```sql
-- V2__Agregar_columna_prioridad.sql
-- Agrega columna de prioridad a tickets

ALTER TABLE tickets ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM' AFTER status;
```

**Archivo:** `src/main/resources/db/migration/supabase/V2__Agregar_columna_prioridad.sql`

```sql
-- V2__Agregar_columna_prioridad.sql
-- Agrega columna de prioridad a tickets (PostgreSQL)

ALTER TABLE tickets ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM';
```

Ejecuta de nuevo: `./mvnw spring-boot:run`

Flyway detecta V2 y la aplica automáticamente. 🚀

---

## Paso 8: Convención de Nombres

Flyway busca archivos con este patrón:

```
V[número]__[descripción].sql
   ↑         ↑
   |         └─ Dos guiones bajos, sin espacios
   └─ Número secuencial (V1, V2, V3, ...)
```

✅ **Correcto:**
- `V1__Crear_tabla_tickets.sql`
- `V2__Agregar_columna_estado.sql`
- `V10__Crear_indice_usuario.sql`

❌ **Incorrecto:**
- `v1_crear_tabla.sql` (minúscula)
- `V1_Crear_tabla.sql` (un guión)
- `V1 Crear tabla.sql` (espacios)
- `V01__Crear_tabla.sql` (cero a la izquierda)

---

## Paso 9: Control de Versiones (Git)

Agrupa tus migraciones por versión del proyecto:

```
v1.0:
  - V1__Crear_tabla_tickets.sql
  - V2__Crear_tabla_usuarios.sql

v1.1:
  - V3__Agregar_columna_prioridad.sql

v2.0:
  - V4__Refactorizar_usuarios_table.sql
```

**Nunca borres una migración** del repositorio. Si necesitas revertir:

```sql
-- V5__Revertir_cambio_incorrecto.sql
-- Revierte el cambio de V4

DROP COLUMN priority FROM tickets;
```

---

## Paso 10: Tabla de Control (Flyway Schema History)

Flyway crea automáticamente una tabla que registra todas las migraciones:

```
flyway_schema_history
┌────┬────────────────────────────────────┬─────────┬────────────────┐
│ id │ version                            │ success │ execution_time │
├────┼────────────────────────────────────┼─────────┼────────────────┤
│ 1  │ V1__Crear_tabla_tickets            │ TRUE    │ 123ms          │
│ 2  │ V2__Agregar_columna_prioridad      │ TRUE    │ 45ms           │
│ 3  │ V3__Crear_tabla_usuarios           │ TRUE    │ 89ms           │
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
5. Escribir V1__Inicial.sql
         ↓
6. Ejecutar app (Flyway aplica automáticamente)
         ↓
7. Cuando necesites cambios, crear V2__Cambio.sql (sin editar V1)
         ↓
8. Ejecutar app de nuevo
         ↓
✅ Flyway aplica solo V2
```

---

*[← Volver a Lección 12](01_objetivo_y_alcance.md)*
