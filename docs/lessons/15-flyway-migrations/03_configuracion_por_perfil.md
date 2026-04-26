# Lección 15 — Configuración por Perfil

## `application.yml` (Base Común)

```yaml
spring:
  application:
    name: Tickets
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: true
  cloud:
    openfeign:
      client:
        config:
          audit-service:
            connect-timeout: 5000
            read-timeout: 10000
            logger-level: BASIC
          default:
            connect-timeout: 5000
            read-timeout: 10000

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"

logging:
  level:
    root: INFO
    cl.duoc.fullstack: DEBUG
```

---

## `application-h2.yml` (H2 — Sin Flyway)

H2 sigue usando JPA automático. **Flyway deshabilitado**.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:tickets_db
    driverClassName: org.h2.Driver
    username: sa
    password: ''
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop  # ← H2 sigue con automático
  flyway:
    enabled: false  # ← H2 NO usa Flyway
```

> **Datos iniciales en H2:** el proyecto usa `DataInitializer.java` (`@Component CommandLineRunner`) que inserta tickets y usuarios de prueba al arrancar. En esta lección agrégale `@Profile("h2")` para que solo corra en H2. Para MySQL y Supabase usa una migración Flyway (`V2__insert_initial_data.sql`).

---

## `application-mysql.yml` (MySQL — Con Flyway)

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:tickets_db}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}
  flyway:
    enabled: true                              # ← Flyway HABILITADO
    locations: classpath:db/migration/mysql    # ← Carpeta con migraciones
    baseline-on-migrate: true                  # ← Crea tabla de control si no existe
    out-of-order: false                        # ← Migraciones deben ser ordenadas
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: validate                       # ← VALIDAR SOLO (no auto)
    properties:
      hibernate:
        format_sql: true
```

---

## `application-supabase.yml` (Supabase/PostgreSQL — Con Flyway)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/${DB_NAME:postgres}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD}
  flyway:
    enabled: true                                # ← Flyway HABILITADO
    locations: classpath:db/migration/supabase   # ← Carpeta con migraciones
    baseline-on-migrate: true                    # ← Crea tabla de control si no existe
    out-of-order: false                          # ← Migraciones deben ser ordenadas
    schemas: public                              # ← Schema por defecto
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate                        # ← VALIDAR SOLO (no auto)
    properties:
      hibernate:
        format_sql: true
```

---

## Opciones Importantes de Flyway

| Propiedad | Valor | Descripción |
|-----------|-------|-------------|
| `enabled` | `true`/`false` | Habilita/deshabilita Flyway |
| `locations` | `classpath:db/migration/mysql` | Dónde buscar las migraciones |
| `baseline-on-migrate` | `true` | Crea tabla `flyway_schema_history` si no existe |
| `out-of-order` | `false` | Las migraciones deben aplicarse en orden (recomendado) |
| `schemas` | `public` | Schema de BD donde aplicar (PostgreSQL) |
| `validate-on-migrate` | `true` | Valida migraciones antes de ejecutar |

---

## Cambio Importante en `ddl-auto`

### ❌ Antes (Lección 14 - Sin Flyway)
```yaml
jpa:
  hibernate:
    ddl-auto: update  # Crea/modifica automáticamente
```

### ✅ Después (Lección 15 - Con Flyway)
```yaml
jpa:
  hibernate:
    ddl-auto: validate  # Solo valida, no modifica
```

**¿Por qué?** Porque Flyway **controla** el esquema. Si JPA también modifica, entra en conflicto.

- `update` = JPA puede cambiar la BD (riesgo)
- `validate` = JPA solo valida que todo coincida (seguro)

---

## Estructura de Carpetas en el Proyecto

```
Tickets/
├── src/main/resources/
│   ├── application.yml
│   ├── application-h2.yml
│   ├── application-mysql.yml
│   ├── application-supabase.yml
│   └── db/
│       └── migration/
│           ├── mysql/
│           │   ├── V1__create_tickets_table.sql
│           │   ├── V2__add_priority_column.sql
│           │   └── V3__...
│           └── supabase/
│               ├── V1__create_tickets_table.sql
│               ├── V2__add_priority_column.sql
│               └── V3__...
│
├── pom.xml (con Flyway agregado)
└── .env (con variables de BD)
```

---

## `.env` (Variables de Entorno — Igual que antes)

```env
# Perfil activo
SPRING_PROFILES_ACTIVE=mysql

# MySQL
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=

# Supabase (reemplaza los valores anteriores)
# DB_HOST=db.xxxx.supabase.co
# DB_PORT=5432
# DB_NAME=postgres
# DB_USER=postgres
# DB_PASSWORD=your-password
```

**Flyway usa automáticamente las mismas variables de datasource.**

---

## Verificación en Logs

Cuando ejecutas la app, deberías ver:

```
2026-04-16 14:23:45.123 INFO  o.f.c.i.database.DatabaseFactory - Database: MySQL 5.7.43-0-log (detected version 5.7.43-0-log)
2026-04-16 14:23:45.234 INFO  o.f.c.i.s.JdbcTableSchemaHistory - Schema history table "tickets_db"."flyway_schema_history" does not exist yet
2026-04-16 14:23:45.250 INFO  o.f.core.internal.command.DbMigrate - Creating Schema History table "tickets_db"."flyway_schema_history"
2026-04-16 14:23:45.315 INFO  o.f.core.internal.command.DbMigrate - Successfully validated 3 migrations (execution time 15ms)
2026-04-16 14:23:45.401 INFO  o.f.core.internal.command.DbMigrate - Successfully applied 3 migrations to schema "tickets_db" (execution time 198ms)
```

---

*[← Volver a Lección 15](01_objetivo_y_alcance.md)*
