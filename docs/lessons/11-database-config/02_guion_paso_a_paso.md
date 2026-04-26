# Lección 11 — Tutorial paso a paso: Perfiles de base de datos

---

## Configuración con Perfiles de Spring Boot

Spring Boot permite gestionar múltiples configuraciones de base de datos usando **perfiles** (profiles). Esto evita cambiar manualmente `application.yml` cada vez que cambias de entorno.

### Archivos de perfil (configuración)

| Archivo | Perfil | Base de Datos |
|--------|-------|--------------|
| `application-h2.yml` | `h2` | H2 (memoria) |
| `application-mysql.yml` | `mysql` | MySQL (XAMPP) |
| `application-supabase.yml` | `supabase` | Supabase (PostgreSQL) |

### Archivos de entorno (valores)

| Archivo | Perfil | Entorno | Cuándo usarlo |
|--------|-------|--------|--------|-------------|
| `.env.local` | `h2` | local | Desarrollo rápido |
| `.env.dev` | `mysql` | dev | Desarrollo con MySQL |
| `.env.test` | `supabase` | test | Pruebas en Supabase |
| `.env.prod` | `supabase` | prod | Producción |

**Nota:** Los entornos `test` y `prod` comparten el perfil `supabase` (misma configuración), pero tienen diferentes valores de conexión.

### Activar un perfil

**Opción 1: Copiar archivo de entorno**
```bash
# Desarrollo rápido (H2)
copy .env.local .env
./mvnw.cmd spring-boot:run

# Desarrollo con MySQL
copy .env.dev .env
./mvnw.cmd spring-boot:run

# Pruebas en Supabase
copy .env.test .env
./mvnw.cmd spring-boot:run

# Producción
copy .env.prod .env
./mvnw.cmd spring-boot:run
```

**Opción 2: Perfil por línea de comandos**
```bash
# H2
./mvnw.cmd spring-boot:run -Dspring.profiles.active=h2

# MySQL
./mvnw.cmd spring-boot:run -Dspring.profiles.active=mysql

# Supabase
./mvnw.cmd spring-boot:run -Dspring.profiles.active=supabase
```

**Opción 3: Variable de entorno**
```bash
# Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE="mysql"
./mvnw spring-boot:run

# Linux/macOS
export SPRING_PROFILES_ACTIVE=mysql
./mvnw spring-boot:run
```

**Opción 4: Desde IntelliJ IDEA**
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
# Perfil activo (h2, mysql, supabase)
SPRING_PROFILES_ACTIVE=mysql

# MySQL Configuration (usado por el perfil mysql)
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=

# Supabase Configuration (usado por el perfil supabase)
# DB_HOST=db.xxxxxxxxxxxx.supabase.co
# DB_PORT=5432
# DB_NAME=postgres
# DB_USER=postgres
# DB_PASSWORD=your-supabase-password
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
   SPRING_PROFILES_ACTIVE=mysql;DB_HOST=localhost;DB_PORT=3306;DB_NAME=tickets_db;DB_USER=root;DB_PASSWORD=
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
    <version>4.0.0</version>
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
HikariPool-1 - Start completed.
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

### Paso A3: verificar `application-mysql.yml`

Este archivo ya existe en `src/main/resources/`. Confírmalo:

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

> **¿Qué hace `?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` en la URL?**
> - `useSSL=false`: desactiva SSL para conexiones locales (XAMPP no tiene certificado)
> - `allowPublicKeyRetrieval=true`: necesario con versiones recientes de MySQL para autenticación sin SSL
> - `serverTimezone=UTC`: sincroniza la zona horaria entre Java y MySQL para que los `LocalDateTime` se guarden y lean correctamente

Luego copia el archivo de entorno y arranca con el perfil mysql:

```bash
copy .env.dev .env
./mvnw.cmd spring-boot:run
```

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

### Paso B5: configurar `.env` con tus credenciales de Supabase

Edita tu `.env` (o `.env.test`) con los valores obtenidos en el paso anterior:

```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.xxxxxxxxxxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=tu-contraseña-de-supabase
```

El archivo `application-supabase.yml` ya existe en `src/main/resources/` y leerá estas variables automáticamente:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
```

Luego arranca con el perfil supabase:

```bash
copy .env.test .env
./mvnw.cmd spring-boot:run
```

### Paso B6: arrancar y verificar

```bash
./mvnw spring-boot:run
```

En el dashboard de Supabase, ve a **Table Editor** — deberías ver la tabla `tickets` creada automáticamente.

---

## Cómo cambiar entre MySQL y Supabase

El código Java no cambia. Solo cambias el archivo `.env` activo:

```bash
# Para usar MySQL local (XAMPP):
copy .env.dev .env
./mvnw.cmd spring-boot:run

# Para usar Supabase:
copy .env.test .env
./mvnw.cmd spring-boot:run
```

O bien pasa el perfil directamente por línea de comandos:
```bash
./mvnw.cmd spring-boot:run -Dspring.profiles.active=mysql
./mvnw.cmd spring-boot:run -Dspring.profiles.active=supabase
```

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

---

## El patrón `*Command` / `*Response` — Referencia

A partir de esta lección, el código usa el patrón de DTOs de entrada y salida. El motivo está documentado en **Lección 10 — JPA y ORM, sección "Por qué no retornamos entidades directamente"**.

| DTO | Uso |
|---|---|
| `*Command` | Input: el Controller lo recibe y el Service lo procesa |
| `*Response` | Output: el Service transforma la entidad y el Controller la retorna |

**Regla de oro:** Una entidad JPA (`@Entity`) nunca sale del Service. Siempre se convierte a `*Response` primero.
