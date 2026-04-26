# 📋 Resumen: Archivos de Configuración

## Perfiles vs Entornos

| Concepto | Descripción |
|----------|-------------|
| **Perfil** | Archivo de configuración (`application-{profile}.yml`) |
| **Entorno** | Valores de las variables para ese perfil |

### Relación Perfil-Entorno

| Perfil | Archivo | Entornos que lo usan |
|--------|---------|---------------------|
| `h2` | `application-h2.yml` | local |
| `mysql` | `application-mysql.yml` | dev |
| `supabase` | `application-supabase.yml` | test, prod |

---

## Estructura de Archivos

```
Tickets-11/
├── src/main/resources/
│   ├── application.yml              ← Configuración base (todos los perfiles)
│   ├── application-h2.yml           ← Perfil: H2 (memoria)
│   ├── application-mysql.yml        ← Perfil: MySQL (XAMPP)
│   └── application-supabase.yml     ← Perfil: Supabase (PostgreSQL)
├── .env.example                     ← Plantilla de variables (commitear)
├── .env.local                      ← Entorno local (perfil: h2)
├── .env.dev                        ← Entorno dev (perfil: mysql)
├── .env.test                       ← Entorno test (perfil: supabase)
├── .env.prod                       ← Entorno prod (perfil: supabase)
└── .gitignore                     ← Contiene .env
```

---

## 1. `application.yml` — Base

**Uso:** Configuración común a todos los perfiles.

```yaml
spring:
  application:
    name: Tickets
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: true

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

## 2. `application-h2.yml` — Perfil H2

**Uso:** Desarrollo rápido, tests. La BD se reinicia cada vez que arranca la app.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:tickets_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: ''
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

**Activar:** Perfil `h2` (usado por entorno `local`)

---

## 3. `application-mysql.yml` — Perfil MySQL

**Uso:** Base de datos persistente local (XAMPP).

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:tickets_db}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
```

**Variables de entorno (para entorno dev):**
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=
```

**Activar:** Perfil `mysql` (usado por entorno `dev`)

---

## 4. `application-supabase.yml` — Perfil Supabase

**Uso:** Base de datos PostgreSQL en la nube (Supabase).

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/${DB_NAME:postgres}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    root: WARN
    cl.duoc.fullstack: INFO
```

**Variables de entorno:**
```env
DB_HOST=db.xxxxxxxxxxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=tu-contraseña
```

**Activar:** Perfil `supabase` (usado por entornos `test` y `prod`)

---

## 5. Archivos de Entorno

### `.env.local` (Perfil: h2)
```env
SPRING_PROFILES_ACTIVE=h2
```
Ningún valor adicional necesario.

### `.env.dev` (Perfil: mysql)
```env
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=
```

### `.env.test` (Perfil: supabase)
```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.test.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=test-password
```

### `.env.prod` (Perfil: supabase)
```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.prod.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=prod-password
```

---

## 6. `.env.example` — Plantilla

**Uso:** Referencia pública. Se sube al repositorio.

```env
# Environment variables for Tickets application
# Copy this file to .env and configure your values

# Perfil activo (h2, mysql, supabase)
SPRING_PROFILES_ACTIVE=h2

# MySQL Configuration (used by perfil: mysql)
# DB_HOST=localhost
# DB_PORT=3306
# DB_NAME=tickets_db
# DB_USER=root
# DB_PASSWORD=

# Supabase Configuration (used by perfil: supabase)
# DB_HOST=db.xxxxxxxxxxxx.supabase.co
# DB_PORT=5432
# DB_NAME=postgres
# DB_USER=postgres
# DB_PASSWORD=your-password
```

---

## Matriz: Cuándo Usar Cada Entorno

| Entorno | Perfil | Variables | Cuándo Usar |
|--------|-------|----------|-------------|
| **local** | `h2` | - | Desarrollo rápido, tests |
| **dev** | `mysql` | `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | Desarrollo con MySQL/XAMPP |
| **test** | `supabase` | Supabase test credentials | Pruebas en la nube |
| **prod** | `supabase` | Supabase prod credentials | Entrega final |

---

## Cómo Activar un Entorno

### Opción 1: Copiar archivo .env
```bash
copy .env.local .env    # Perfil: h2
copy .env.dev .env      # Perfil: mysql
copy .env.test .env     # Perfil: supabase
copy .env.prod .env     # Perfil: supabase

./mvnw spring-boot:run
```

### Opción 2: Variable de entorno
```bash
# PowerShell
$env:SPRING_PROFILES_ACTIVE="mysql"
./mvnw.cmd spring-boot:run

# bash
SPRING_PROFILES_ACTIVE=mysql ./mvnw spring-boot:run
```

### Opción 3: Línea de comandos
```bash
./mvnw spring-boot:run -Dspring.profiles.active=mysql
```

---

## Checklist de Configuración

- ✅ Creaste `.env` copiando `.env.local`, `.env.dev`, `.env.test` o `.env.prod`
- ✅ Llenaste las credenciales en `.env` (excepto en local)
- ✅ `.env` está en `.gitignore`
- ✅ Tienes la base de datos corriendo (XAMPP o Supabase)
- ✅ Ejecutaste la app y verificaste los logs

---

## Flujo de Carga

```
1. application.yml (base)
        ↓
2. application-{perfil}.yml (perfil activo)
        ↓
3. Variables de entorno (.env o sistema)
        ↓
App configurada y corriendo
```

---

*[← Volver a Lección 11](01_objetivo_y_alcance.md)*