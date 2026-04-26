# 🚀 Cheat Sheet — Referencia Rápida Lección 11

## Perfiles vs Entornos

| Concepto | Descripción |
|----------|-------------|
| **Perfil** | Archivo YAML (`application-{profile}.yml`) |
| **Entorno** | Valores de variables para ese perfil |

### Relación Perfil-Entorno

| Entorno | Perfil | Base de Datos |
|--------|-------|-------------|
| local | h2 | H2 (memoria) |
| dev | mysql | MySQL (XAMPP) |
| test | supabase | Supabase (PostgreSQL) |
| prod | supabase | Supabase (PostgreSQL) |

---

## Cuatro Comandos para Arrancar

```bash
# Entorno LOCAL (H2)
copy .env.local .env
./mvnw.cmd spring-boot:run

# Entorno DEV (MySQL/XAMPP)
copy .env.dev .env
./mvnw.cmd spring-boot:run

# Entorno TEST (Supabase)
copy .env.test .env
./mvnw.cmd spring-boot:run

# Entorno PROD (Supabase)
copy .env.prod .env
./mvnw.cmd spring-boot:run
```

O directamente con perfil:
```bash
./mvnw.cmd spring-boot:run -Dspring.profiles.active=h2
./mvnw.cmd spring-boot:run -Dspring.profiles.active=mysql
./mvnw.cmd spring-boot:run -Dspring.profiles.active=supabase
```

---

## Estructura de Archivos

```
src/main/resources/
├── application.yml              ← Base (común a todos)
├── application-h2.yml        ← Perfil: h2
├── application-mysql.yml     ← Perfil: mysql
└── application-supabase.yml ← Perfil: supabase

.env.local                  ← Entorno: local (perfil: h2)
.env.dev                  ← Entorno: dev (perfil: mysql)
.env.test                 ← Entorno: test (perfil: supabase)
.env.prod                 ← Entorno: prod (perfil: supabase)
.env.example               ← Plantilla
```

---

## Variables por Entorno

### LOCAL (no necesita variables)
```env
SPRING_PROFILES_ACTIVE=h2
```

### DEV
```env
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tickets_db
DB_USER=root
DB_PASSWORD=
```

### TEST / PROD
```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.xxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=tu-password
```

---

## Matriz de Decisión

| Caso | Entorno | Perfil | Cuándo |
|------|--------|-------|--------|
| Tests rápidos | local | h2 | Sin persistencia |
| Desarrollo diario | dev | mysql | Con XAMPP |
| Pruebas en nube | test | supabase | Supabase test |
| Entrega final | prod | supabase | Supabase prod |

---

## Cómo Sé que Funcionó?

Busca en los logs:
```
The following profiles are active: [tu-perfil]
HikariPool-1 - Start completed.
```

Luego prueba: http://localhost:8080/ticket-app/tickets

---

## Seguridad

- ✅ `.env.example` → commitear
- ❌ `.env`, `.env.local`, `.env.dev`, `.env.test`, `.env.prod` → NO commitear
- 🔒 Credenciales reales → solo en `.env` local

---

## Troubleshooting

**"Connection refused"**
→ Verifica que la base de datos esté corriendo y credenciales sean correctas

**"No profile is active"**
→ Define `SPRING_PROFILES_ACTIVE` en variable de entorno o `.env`

**"¿Cuál perfil estoy usando?"**
→ Mira los logs: `The following profile is active: ...`

---

*[← Volver a Lección 11](01_objetivo_y_alcance.md)*