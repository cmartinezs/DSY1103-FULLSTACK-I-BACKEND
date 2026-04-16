# 📝 Ejemplo: Archivo `.env` Completado

## Escenario 1: Desarrollo Local con MySQL

```env
# .env — Desarrollo local con XAMPP

# Perfil activo
SPRING_PROFILES_ACTIVE=mysql

# MySQL Configuration
MYSQL_URL=jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
MYSQL_USERNAME=root
MYSQL_PASSWORD=

# Supabase Configuration (vacío, no lo usamos ahora)
DB_HOST=
DB_PORT=5432
DB_NAME=
DB_USER=
DB_PASSWORD=
```

**Cómo usar:**
```bash
cd Tickets

# Opción A: Exportar variables (Linux/macOS)
export SPRING_PROFILES_ACTIVE=mysql
./mvnw spring-boot:run

# Opción B: En PowerShell (Windows)
$env:SPRING_PROFILES_ACTIVE="mysql"
./mvnw spring-boot:run

# Opción C: IntelliJ IDEA (con plugin EnvFile)
# → Run → Edit Configurations → Enable EnvFile → Select .env
# → Clic en ▶ para ejecutar
```

---

## Escenario 2: Desarrollo Local con H2

```env
# .env — Tests rápidos, sin BD

# Perfil activo
SPRING_PROFILES_ACTIVE=h2

# MySQL Configuration (vacío, no lo usamos)
MYSQL_URL=
MYSQL_USERNAME=
MYSQL_PASSWORD=

# Supabase Configuration (vacío, no lo usamos)
DB_HOST=
DB_PORT=5432
DB_NAME=
DB_USER=
DB_PASSWORD=
```

**Cómo usar:**
```bash
cd Tickets
./mvnw spring-boot:run
# Ya está en H2 por defecto, no necesitas hacer nada más
```

---

## Escenario 3: Entrega Final con Supabase

```env
# .env — Producción/Entrega en Supabase

# Perfil activo
SPRING_PROFILES_ACTIVE=supabase

# MySQL Configuration (vacío, no lo usamos)
MYSQL_URL=
MYSQL_USERNAME=
MYSQL_PASSWORD=

# Supabase Configuration (¡VALORES REALES!)
DB_HOST=db.xyzabcdefgh.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=pbkdf2$260000$2a3f5c8d9e1b4c2f$a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t
```

### ¿De dónde sacas estos valores?

1. **DB_HOST**: Ve a Supabase → tu proyecto → Settings → Database → Connection string → copia el host
   ```
   DB_HOST=db.xyzabcdefgh.supabase.co
   ```

2. **DB_PORT**: Siempre es `5432` para Supabase

3. **DB_NAME**: Siempre es `postgres`

4. **DB_USER**: Siempre es `postgres` (a menos que hayas creado otro usuario)

5. **DB_PASSWORD**: La contraseña que creaste cuando hiciste el proyecto en Supabase

### Ejemplo completo de Connection String de Supabase

```
jdbc:postgresql://db.xyzabcdefgh.supabase.co:5432/postgres
```

**Lo que extraes para `.env`:**
```env
DB_HOST=db.xyzabcdefgh.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=tu-password-aqui
```

**Cómo usar:**
```bash
cd Tickets

# Opción A: Exportar variables (Linux/macOS)
export SPRING_PROFILES_ACTIVE=supabase
./mvnw spring-boot:run

# Opción B: En PowerShell (Windows)
$env:SPRING_PROFILES_ACTIVE="supabase"
./mvnw spring-boot:run

# Opción C: IntelliJ IDEA (con plugin EnvFile)
# → Run → Edit Configurations → Enable EnvFile → Select .env
# → Clic en ▶ para ejecutar
```

---

## ⚠️ Validación

Después de completar tu `.env`, verifica en los logs:

```
The following profiles are active: mysql
The following 1 profile is active: "mysql"

HikariPool-1 - Starting...
HikariPool-1 - Connection is working...
```

Si ves esto, ¡todo funciona! 🎉

---

## 🔒 Seguridad: Checklist Final

**Antes de hacer commit:**

- ✅ `.env` **no está** en el repositorio (verificar `.gitignore`)
- ✅ `.env.example` **sí está** en el repositorio (plantilla pública)
- ✅ Tu archivo `.env` local contiene credenciales reales, pero solo en tu PC
- ✅ Si el `.env` se subió accidentalmente, **cambia todos los passwords inmediatamente**

**Comando para verificar:**
```bash
git status .env
# Debería mostrar: .env (not tracked)

git status .env.example
# Debería mostrar: .env.example (tracked)
```

---

## 🔄 Cambiar Entre Perfiles sin Editar `.env`

También puedes cambiar el perfil simplemente editando esta línea en `.env`:

```env
# Opción 1: H2
SPRING_PROFILES_ACTIVE=h2

# Opción 2: MySQL
SPRING_PROFILES_ACTIVE=mysql

# Opción 3: Supabase
SPRING_PROFILES_ACTIVE=supabase
```

Y luego arrancas:
```bash
./mvnw spring-boot:run
```

El perfil se cargará automáticamente desde `.env`. 🚀

---

*[← Volver a Lección 11](00_indice.md)*
