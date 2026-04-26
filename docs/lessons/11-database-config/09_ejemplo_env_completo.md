# 📝 Ejemplo: Archivos de Entorno

## Conceptos Clave

| Concepto | Descripción |
|----------|-------------|
| **Perfil** | Archivo YAML (`application-{profile}.yml`) con configuración de BD |
| **Entorno** | Valores de las variables para un perfil específico |

### Relación Perfil-Entorno

| Entorno | Perfil | Base de Datos |
|--------|-------|-------------|
| local | h2 | H2 (memoria) |
| dev | mysql | MySQL (XAMPP) |
| test | supabase | Supabase (PostgreSQL) |
| prod | supabase | Supabase (PostgreSQL) |

---

## Entorno 1: LOCAL (Perfil: h2)

**Cuándo usarlo:** Desarrollo rápido, tests, sin persistencia de datos.

### Archivo: `.env.local`
```env
# Ambiente LOCAL - desarrollo rápido
# Perfil: h2
SPRING_PROFILES_ACTIVE=h2
```

**Sin variables adicionales** — el perfil h2 no necesita credenciales.

### Cómo ejecutarlo
```bash
# Opción A: Copiar .env.local a .env
copy .env.local .env
./mvnw spring-boot:run

# Opción B: Variable de entorno
$env:SPRING_PROFILES_ACTIVE="h2"
./mvnw.cmd spring-boot:run
```

### Resultado esperado en logs
```
The following profiles are active: h2
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

---

## Entorno 2: DEV (Perfil: mysql)

**Cuándo usarlo:** Desarrollo diario con datos persistentes (XAMPP).

### Archivo: `.env.dev`
```env
# Ambiente DEV - MySQL local
# Perfil: mysql
SPRING_PROFILES_ACTIVE=mysql

# MySQL Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=
```

### Prerequisites
1. XAMPP instalado y MySQL corriendo
2. Base de datos `tickets_db` creada en phpMyAdmin

### Cómo ejecutarlo
```bash
# Opción A: Copiar .env.dev a .env
copy .env.dev .env
./mvnw spring-boot:run

# Opción B: Variable de entorno
$env:SPRING_PROFILES_ACTIVE="mysql"
./mvnw.cmd spring-boot:run
```

### Resultado esperado en logs
```
The following profiles are active: mysql
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

---

## Entorno 3: TEST (Perfil: supabase)

**Cuándo usarlo:** Pruebas con base de datos en la nube (Supabase).

### Archivo: `.env.test`
```env
# Ambiente TEST - Supabase (pruebas)
# Perfil: supabase
SPRING_PROFILES_ACTIVE=supabase

# Supabase Configuration
DB_HOST=db.xxxxxxxxxxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=your-test-password
```

### Dónde obtener los valores

1. **DB_HOST**: Supabase → Settings → Database → Connection string
2. **DB_PORT**: Siempre `5432`
3. **DB_NAME**: Siempre `postgres`
4. **DB_USER**: Siempre `postgres`
5. **DB_PASSWORD**: La que creaste en Supabase

### Cómo ejecutarlo
```bash
# Opción A: Copiar .env.test a .env
copy .env.test .env
./mvnw spring-boot:run

# Opción B: Variable de entorno
$env:SPRING_PROFILES_ACTIVE="supabase"
./mvnw.cmd spring-boot:run
```

### Resultado esperado en logs
```
The following profiles are active: supabase
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

---

## Entorno 4: PROD (Perfil: supabase)

**Cuándo usarlo:** Entrega final, producción.

### Archivo: `.env.prod`
```env
# Ambiente PROD - Supabase (producción)
# Perfil: supabase (mismo que test, valores diferentes)
SPRING_PROFILES_ACTIVE=supabase

# Supabase Configuration
DB_HOST=db.yyyyyyyyyyyy.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=your-prod-password
```

**Nota:** Usa el mismo perfil `supabase` que test, pero con diferentes valores de conexión.

### Cómo ejecutarlo
```bash
# Opción A: Copiar .env.prod a .env
copy .env.prod .env
./mvnw spring-boot:run

# Opción B: Variable de entorno
$env:SPRING_PROFILES_ACTIVE="supabase"
./mvnw.cmd spring-boot:run
```

---

## 🎯 Cómo Cambiar de Entorno

### Cambiar solo el archivo .env
```bash
# Desarrollo rápido
copy .env.local .env

# Desarrollo con MySQL
copy .env.dev .env

# Pruebas en Supabase
copy .env.test .env

# Producción
copy .env.prod .env

./mvnw spring-boot:run
```

### Cambiar perfil sin editar .env
Edita la línea `SPRING_PROFILES_ACTIVE`:
```env
# Perfil: h2
SPRING_PROFILES_ACTIVE=h2

# Perfil: mysql
SPRING_PROFILES_ACTIVE=mysql

# Perfil: supabase
SPRING_PROFILES_ACTIVE=supabase
```

---

## 🔒 Seguridad: Checklist Final

**Antes de hacer commit:**

- ✅ `.env` **no está** en el repositorio (verificar `.gitignore`)
- ✅ `.env.example` **sí está** en el repositorio
- ✅ `.env.local`, `.env.dev`, `.env.test`, `.env.prod` están en `.gitignore`
- ✅ Nunca subas credenciales reales

**Comando para verificar:**
```bash
git status .env
# Debería mostrar: .env (not tracked)
```

---

*[← Volver a Lección 11](01_objetivo_y_alcance.md)*