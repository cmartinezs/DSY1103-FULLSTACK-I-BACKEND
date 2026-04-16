# Tickets-14: Lección 14 - Migraciones Flyway

## 📋 Descripción

Este proyecto implementa la **Lección 14: Migraciones Flyway** del curso DSY1103 Fullstack I.

Agrega versionado de base de datos con Flyway.

## 🎯 Caso de Uso Extendido (Sistema de Tickets con Gestión de Usuarios)

### Roles definidos
| Rol     | Descripción              |
|---------|--------------------------|
| USER    | Crea tickets, ve estado  |
| AGENT   | Recibe tickets asignados |
| ADMIN   | Supervisa y gestiona     |

### Modelo de datos
- **User**: id, name, email, role (USER/AGENT/ADMIN), active
- **Ticket**: relaciones con User, Category, Tags
- **Category**: One-to-Many con Ticket
- **Tag**: Many-to-Many con Ticket
- **TicketHistory**: historial de cambios de estado

---

## 🔄 Cambios desde Lección 13

### 1. Dependencias Agregadas
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

> **Nota**: `flyway-core` incluye soporte para PostgreSQL/Supabase. No se necesita dependencia adicional.

### 2. Migraciones SQL

#### V1__Initial_schema.sql
- Tabla users con campos role y active
- Tabla tickets con Foreign Keys a users

#### V2__Add_categories.sql
- Tabla categories
- Foreign Key category_id en tickets

#### V3__Add_tags.sql
- Tabla tags
- Tabla ticket_tags (Many-to-Many)

#### V4__Add_audit_tables.sql
- Tabla ticket_history

### 3. Configuración de Flyway por Perfil

#### H2 (application-h2.yml)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
```
> H2 usa JPA para crear las tablas automáticamente (ddl-auto: create-drop). Flyway deshabilitado.

#### MySQL (application-mysql.yml)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```
> MySQL usa Flyway. JPA valida que el esquema coincida.

#### Supabase/PostgreSQL (application-supabase.yml)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```
> PostgreSQL/Supabase usa Flyway. JPA valida el esquema.

---

## 📊 Requisitos del Caso Extendido por Lección

| Lección | Requisitos del Caso Extendido |
|---------|------------------------------|
| 10 | ✅ User entity con roles, Ticket con User relaciones, seed de datos |
| 11 | ✅ Perfiles con diferentes configs de BD para usuarios (H2, MySQL, Supabase) |
| 12 | ✅ Category (One-to-Many), Tag (Many-to-Many), CRUD completo |
| 13 | ✅ TicketHistory, registro automático, endpoint de historial |
| 14 | ✅ Flyway migrations con Foreign Keys a users |
| 15 | Notificaciones con User |
| 16 | Security con 3 roles (USER/AGENT/ADMIN) |
| 17 | Logging de operaciones de usuarios |
| 18 | Excepciones para casos de usuarios |

---

## 🧪 Uso

```bash
# H2 (desarrollo sin Flyway - JPA crea tablas)
./mvnw spring-boot:run

# MySQL (con Flyway)
./mvnw spring-boot:run -Dspring.profiles.active=mysql

# Supabase (con Flyway)
./mvnw spring-boot:run -Dspring.profiles.active=supabase
```

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] Flyway configurado para MySQL/Supabase
- [x] H2 mantiene ddl-auto: create-drop (sin Flyway)
- [x] Migraciones con Foreign Keys correctas
- [x] flyway-mysql incluido para soporte MySQL

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `pom.xml` | Dependencias Flyway |
| `db/migration/V1__Initial_schema.sql` | Tablas users, tickets |
| `db/migration/V2__Add_categories.sql` | Tabla categories |
| `db/migration/V3__Add_tags.sql` | Tabla tags, ticket_tags |
| `db/migration/V4__Add_audit_tables.sql` | Tabla ticket_history |
| `application-h2.yml` | H2 sin Flyway (JPA crea) |
| `application-mysql.yml` | Flyway habilitado |
| `application-supabase.yml` | Flyway habilitado |

---

**Base**: Lección 13 (Historial y Auditoría)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, Flyway, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada