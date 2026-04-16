# Lección 11 — Tutorial paso a paso: XAMPP y Supabase

---

## Configuración con Perfiles de Spring Boot

Spring Boot permite gestionar múltiples configuraciones de base de datos usando **perfiles** (profiles). Esto evita cambiar manualmente `application.yml` cada vez que cambias de entorno.

### Archivos de configuración

- **`application.yml`** — Configuración común (puerto, contexto, perfil activo por defecto)
- **`application-h2.yml`** — BD en memoria (desarrollo/testing)
- **`application-mysql.yml`** — MySQL local (XAMPP)
- **`application-supabase.yml`** — Supabase PostgreSQL en la nube

### Activar un perfil

**Opción 1: Desde la línea de comandos**
```bash
# H2 (por defecto)
./mvnw spring-boot:run

# MySQL
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

# Supabase
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=supabase"
```

**Opción 2: Variable de entorno**
```bash
# Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE="mysql"
./mvnw spring-boot:run

# Linux/macOS
export SPRING_PROFILES_ACTIVE=mysql
./mvnw spring-boot:run
```

**Opción 3: Desde IntelliJ IDEA**
1. Abre **Run** → **Edit Configurations**
2. Busca la configuración de Maven (Spring Boot)
3. En el campo **Program arguments**, agrega: `spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"`
4. O en **VM options**, agrega: `-Dspring.profiles.active=mysql`
5. Guarda y ejecuta

---

## Variables de Entorno y Archivo `.env`

Para **no hardcodear credenciales** en el código, usamos variables de entorno. Spring Boot las inyecta automáticamente usando la sintaxis `${variable}`.

### Paso 1: Crear el archivo `.env`

En la raíz del proyecto `Tickets/`, copia `.env.example` a `.env`:

```bash
# Windows
copy .env.example .env

# Linux/macOS
cp .env.example .env
```

**Contenido de `.env`:**
```env
# MySQL Configuration
MYSQL_URL=jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
MYSQL_USERNAME=root
MYSQL_PASSWORD=

# Supabase Configuration
DB_HOST=db.xxxxxxxxxxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=your-supabase-password

# Active Profile
SPRING_PROFILES_ACTIVE=mysql
```

### Paso 2: Cargar `.env` en los archivos YAML

**`application.yml`:**
```yaml
spring:
  application:
    name: Tickets
  profiles:
    active: h2
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"
```

**`application-mysql.yml`:**
```yaml
spring:
  datasource:
    url: ${MYSQL_URL:jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago}
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:}
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    properties:
      hibernate:
        format_sql: true
```

**`application-supabase.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQL10Dialect
    properties:
      hibernate:
        format_sql: true
```

> **¿Qué significa `${VAR:valor-por-defecto}`?**
> Si la variable de entorno `VAR` no existe, Spring usa `valor-por-defecto`. Es útil para desarrollo local.

### Paso 3: Cargar variables de entorno

#### Opción A: Sistema Operativo (SO)

**Windows (PowerShell):**
```powershell
# Ver una variable
$env:MYSQL_USERNAME

# Establecer (solo en sesión actual)
$env:MYSQL_USERNAME="root"
$env:SPRING_PROFILES_ACTIVE="mysql"

# Establecer permanentemente (requiere admin)
[Environment]::SetEnvironmentVariable("MYSQL_USERNAME", "root", "User")
```

**Linux/macOS:**
```bash
# Ver una variable
echo $MYSQL_USERNAME

# Establecer en sesión actual
export MYSQL_USERNAME="root"
export SPRING_PROFILES_ACTIVE="mysql"

# Establecer permanentemente (agregar a ~/.bashrc, ~/.zshrc, etc.)
echo 'export MYSQL_USERNAME="root"' >> ~/.bashrc
source ~/.bashrc
```

#### Opción B: IntelliJ IDEA

1. **Abre Run → Edit Configurations**
2. Selecciona la configuración de Spring Boot
3. En **Environment variables**, agrega las variables:
   ```
   SPRING_PROFILES_ACTIVE=mysql;MYSQL_USERNAME=root;MYSQL_PASSWORD=;DB_HOST=db.xxxx.supabase.co;DB_PORT=5432
   ```
   (usa `;` para separar en Windows, `:` en Linux/macOS)
4. Guarda y ejecuta

**O con un archivo `.env` en IntelliJ:**
1. Instala el plugin **EnvFile** desde Preferences → Plugins
2. En **Edit Configurations**, habilita **Use env file** y selecciona tu `.env`
3. IntelliJ cargará automáticamente las variables

#### Opción C: Spring Boot con `dotenv` (Maven)

Agrega la dependencia en `pom.xml`:
```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>3.0.0</version>
</dependency>
```

Spring cargará automáticamente `Tickets/.env` al arrancar.

### Paso 4: Verificar que funcionó

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

Deberías ver en los logs:
```
The following profiles are active: mysql
```

Y luego:
```
HikariPool-1 - Starting...
HikariPool-1 - Connection is working...
```

---

## Checklist de Seguridad

✅ **Siempre:**
- Crea `.env.example` con valores de ejemplo (sin credenciales reales)
- Agrega `.env` a `.gitignore` para que no se comitee

❌ **Nunca:**
- Hagas commit de `.env` con credenciales reales
- Escribas contraseñas directamente en `application.yml` versionado
- Compartas credenciales por chat o email

---

## Opción A: MySQL con XAMPP (base de datos local)

### Paso A1: verificar que XAMPP está listo

1. Abre el panel de control de XAMPP
2. Inicia los servicios **Apache** y **MySQL** (botón "Start" en cada uno)
3. Verifica que el estado muestre "Running" en verde

### Paso A2: crear la base de datos

1. Abre `http://localhost/phpmyadmin` en el navegador
2. En el panel izquierdo, haz clic en **Nueva**
3. Nombre: `tickets_db`
4. Cotejamiento: `utf8mb4_unicode_ci`
5. Haz clic en **Crear**

> **¿Qué es el cotejamiento?**
> Define cómo se comparan y ordenan los textos. `utf8mb4_unicode_ci` soporta todos los caracteres del español (tildes, ñ) y es insensible a mayúsculas en las comparaciones (`ci` = case-insensitive). Es el estándar para aplicaciones en español.

### Paso A3: configurar `application.yml` para MySQL

```yaml
spring:
  application:
    name: Tickets

  datasource:
    url: jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
    username: root
    password:
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"
```

> **¿Qué hace `?useSSL=false&serverTimezone=America/Santiago` en la URL?**
> - `useSSL=false`: desactiva SSL para conexiones locales (XAMPP no tiene certificado)
> - `serverTimezone=America/Santiago`: sincroniza la zona horaria entre Java y MySQL para que los `LocalDateTime` se guarden y lean correctamente

### Paso A4: arrancar y verificar

```bash
./mvnw spring-boot:run
```

Revisa en phpMyAdmin: debería aparecer la tabla `tickets` creada automáticamente.

---

## Opción B: Supabase (PostgreSQL en la nube)

### Paso B1: crear una cuenta en Supabase

1. Ve a `https://supabase.com`
2. Haz clic en **Start your project**
3. Regístrate con GitHub o con correo electrónico

### Paso B2: crear un proyecto

1. En el dashboard, haz clic en **New project**
2. Nombre del proyecto: `tickets-app` (o el nombre que prefieras)
3. Contraseña de la base de datos: crea una contraseña fuerte y **guárdala** — la necesitarás
4. Región: elige la más cercana (por ejemplo, `South America (São Paulo)`)
5. Haz clic en **Create new project**

Supabase tarda 1-2 minutos en aprovisionar el proyecto.

### Paso B3: obtener la cadena de conexión

1. En tu proyecto de Supabase, ve a **Project Settings** (ícono de engranaje)
2. Haz clic en **Database** en el menú lateral
3. Baja hasta la sección **Connection string**
4. Selecciona la pestaña **JDBC**
5. Copia la cadena. Tendrá este formato:

```
jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres
```

### Paso B4: agregar el driver de PostgreSQL al `pom.xml`

```xml
<!-- Agrega esto, comenta o elimina el de MySQL mientras uses Supabase -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Paso B5: configurar `application.yml` para Supabase

```yaml
spring:
  application:
    name: Tickets

  datasource:
    url: jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres
    username: postgres
    password: tu-contraseña-de-supabase
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"
```

> **Reemplaza:**
> - `db.xxxxxxxxxxxx.supabase.co` con tu host real de Supabase
> - `tu-contraseña-de-supabase` con la contraseña que creaste en el Paso B2

### Paso B6: arrancar y verificar

```bash
./mvnw spring-boot:run
```

En el dashboard de Supabase, ve a **Table Editor** — deberías ver la tabla `tickets` creada automáticamente.

---

## Cómo cambiar entre MySQL y Supabase

El código Java no cambia. Solo modificas el `application.yml`:

```yaml
# Para usar MySQL local (XAMPP):
datasource:
  url: jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
  username: root
  password:
  driver-class-name: com.mysql.cj.jdbc.Driver

# Para usar Supabase (comentar el bloque anterior y descomentar este):
# datasource:
#   url: jdbc:postgresql://db.xxxx.supabase.co:5432/postgres
#   username: postgres
#   password: tu-contraseña
#   driver-class-name: org.postgresql.Driver
```

Y cambiar el driver correspondiente en `pom.xml`.

> **¿Por qué funciona esto?**
> JPA es una **especificación** (un contrato). Hibernate la implementa para cualquier base de datos que tenga un driver JDBC. El código Java no sabe si está hablando con MySQL o PostgreSQL — eso es responsabilidad de Hibernate y el driver. Cambias la configuración, no el código.

---

## Opciones de `ddl-auto` — cuándo usar cada una

| Valor | Comportamiento | Cuándo usarlo |
|---|---|---|
| `create` | Borra y recrea todas las tablas al arrancar | Primera vez que configuras la BD; **pierde todos los datos** |
| `create-drop` | Crea al arrancar, borra al apagar | Tests automatizados |
| `update` | Agrega columnas y tablas nuevas, no borra datos | Desarrollo activo (el más común) |
| `validate` | Verifica que el esquema coincide, no modifica nada | Producción |
| `none` | No hace nada con el esquema | Cuando el esquema lo controla otra herramienta (Flyway) |

**Para este curso:** usa `update` siempre, excepto cuando necesites partir con datos limpios, en cuyo caso usa `create` una vez y luego vuelve a `update`.

---

## Verificar la conexión sin arrancar la app

Si quieres comprobar que las credenciales son correctas antes de arrancar Spring Boot, puedes probar la conexión directamente con un cliente como **DBeaver** o **TablePlus**:

- **MySQL (XAMPP):** host `localhost`, puerto `3306`, usuario `root`, password vacío
- **Supabase:** usa la cadena de conexión de la sección "Database" → "Connection string" → pestaña "URI"
