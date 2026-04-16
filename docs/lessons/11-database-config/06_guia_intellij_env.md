# 🚀 Guía Rápida: Cargar Variables de Entorno en IntelliJ IDEA

## El Problema

Cuando ejecutas tu aplicación desde IntelliJ, Spring Boot no carga automáticamente el archivo `.env`. Las credenciales de base de datos se quedan vacías y la conexión falla.

---

## Solución Rápida en IntelliJ IDEA

### Paso 1: Instala el plugin EnvFile

1. Abre IntelliJ IDEA
2. Ve a **File** → **Settings** (o **Preferences** en macOS)
3. Busca **Plugins** en el buscador
4. Escribe `"EnvFile"` y busca
5. Haz clic en **Install** en el resultado `"EnvFile"`
6. Reinicia IntelliJ

### Paso 2: Configura tu Run Configuration

1. En IntelliJ, ve a **Run** → **Edit Configurations...**
2. Busca la configuración de Spring Boot (probablemente se llama `TicketsApplication`)
3. Si no existe, haz clic en `+` → busca `Spring Boot` → llámalo `TicketsApplication`
4. En la ventana de configuración, busca la pestaña **"EnvFile"** (debería aparecer después de instalar el plugin)
5. Haz clic en el checkbox **"Enable EnvFile"**
6. Haz clic en el botón `+` verde
7. Selecciona tu archivo `.env` (en la raíz del proyecto `Tickets/`)
8. Haz clic en **Apply** → **OK**

### Paso 3: Ejecuta la aplicación

Ahora cuando ejecutes desde IntelliJ (botón ▶ verde o Shift+F10), las variables de `.env` estarán disponibles.

---

## Solución Alternativa: Definir Variables Manualmente

Si el plugin no funciona o prefieres no instalarlo:

1. **Run** → **Edit Configurations...**
2. En el campo **"Environment variables"**, haz clic en el ícono de editar (📋)
3. Agrega manualmente cada variable. Ejemplo:

```
SPRING_PROFILES_ACTIVE=mysql
MYSQL_USERNAME=root
MYSQL_PASSWORD=
DB_HOST=db.xxxxxxxxxxxx.supabase.co
DB_PORT=5432
```

(En Windows, separa con `;` | En Linux/macOS, separa con `:`)

4. **Apply** → **OK**

---

## Solución Avanzada: Usar `spring-dotenv`

Agrega esta dependencia al `pom.xml`:

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

Con esto, Spring carga `.env` automáticamente sin necesidad de plugin. Funciona en IntelliJ, línea de comandos y en todos lados.

---

## Verificar que Funcionó

Ejecuta la aplicación y busca en los logs:

```
The following profiles are active: mysql
```

Si ves esto, las variables se cargaron correctamente. Luego deberías ver:

```
HikariPool-1 - Starting...
HikariPool-1 - Connection is working...
```

---

## Referencia de Variables

```env
# Perfil activo
SPRING_PROFILES_ACTIVE=h2           # o: mysql, supabase

# MySQL (si usas application-mysql.yml)
MYSQL_URL=jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
MYSQL_USERNAME=root
MYSQL_PASSWORD=

# Supabase (si usas application-supabase.yml)
DB_HOST=db.xxxxxxxxxxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=tu-contraseña-supabase
```

---

*Para más información, ver [Variables de Entorno - Documentación Completa](../extras/env-variables/README.md)*
