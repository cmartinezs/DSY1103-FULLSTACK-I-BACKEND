# 🚀 Guía: Cargar Variables de Entorno en IntelliJ IDEA

## El Problema

Cuando ejecutas tu aplicación desde IntelliJ, Spring Boot no carga automáticamente el archivo `.env`. Las credenciales de base de datos se quedan vacías y la conexión falla.

---

## Conceptos Clave

| Concepto | Descripción |
|----------|-------------|
| **Perfil** | Archivo YAML (`application-{profile}.yml`) con configuración |
| **Entorno** | Valores de variables para ese perfil |

### Relación Perfil-Entorno

| Entorno | Perfil | Archivo |
|--------|-------|---------|
| local | h2 | `.env.local` |
| dev | mysql | `.env.dev` |
| test | supabase | `.env.test` |
| prod | supabase | `.env.prod` |

---

## Solución: Plugin EnvFile

### Paso 1: Instala el plugin

1. **File** → **Settings** → **Plugins**
2. Busca "EnvFile" e instala
3. Reinicia IntelliJ

### Paso 2: Configura Run Configuration

1. **Run** → **Edit Configurations...**
2. Selecciona la configuración de Spring Boot
3. En la pestaña **"EnvFile"**, habilita **"Enable EnvFile"**
4. Selecciona el archivo `.env` que corresponda:
   - `.env.local` → desarrollo rápido
   - `.env.dev` → MySQL
   - `.env.test` → Supabase (pruebas)
   - `.env.prod` → Supabase (producción)
5. **Apply** → **OK**

### Paso 3: Ejecuta

Usa el botón ▶ o Shift+F10

Verifica en los logs:
```
The following profiles are active: mysql
HikariPool-1 - Start completed.
```

---

## Solución Alternativa: Variables Manuales

1. **Run** → **Edit Configurations...**
2. En **Environment variables**, agrega:
   ```
   SPRING_PROFILES_ACTIVE=mysql;DB_HOST=localhost;DB_PORT=3306;DB_NAME=tickets_db;DB_USER=root;DB_PASSWORD=
   ```
   (Windows usa `;`, Linux/macOS usa `:`)
3. **Apply** → **OK**

---

## Referencia de Variables por Entorno

### LOCAL (.env.local)
```env
SPRING_PROFILES_ACTIVE=h2
```
Sin variables adicionales.

### DEV (.env.dev)
```env
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=
```

### TEST (.env.test)
```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.xxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=your-password
```

### PROD (.env.prod)
```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.yyy.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=prod-password
```

---

## Solución Automática: spring-dotenv

Agrega al `pom.xml`:
```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

Spring cargará `.env` automáticamente.

---

## Troubleshooting

| Error | Solución |
|-------|----------|
| "Connection refused" | Verifica que la BD esté corriendo |
| "No profile active" | Define `SPRING_PROFILES_ACTIVE` |
| "Credential errors" | Verifica usuario y password en `.env` |

---

*[← Volver a Lección 11](01_objetivo_y_alcance.md)*