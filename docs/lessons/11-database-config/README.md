# 📚 Lección 11 — Configurar Bases de Datos Reales con Perfiles de Spring Boot

> Aprende a usar **perfiles de Spring Boot** para manejar múltiples configuraciones de base de datos (H2, MySQL, Supabase) y **variables de entorno** para proteger credenciales.

---

## 🎯 ¿Qué Aprenderás?

✅ Configurar múltiples bases de datos con perfiles de Spring Boot  
✅ Manejar variables de entorno de forma segura con `.env`  
✅ Conectar a H2 (en memoria), MySQL (local) y Supabase (en la nube)  
✅ Cambiar entre perfiles sin modificar el código Java  
✅ Cargar variables de entorno desde IntelliJ IDEA  

---

## 📖 Documentos

| Documento | Duración | Para |
|-----------|----------|------|
| **[01. Objetivo y Alcance](01_objetivo_y_alcance.md)** | 5 min | Entender qué aprenderás |
| **[02. Guión Paso a Paso](02_guion_paso_a_paso.md)** ⭐ | 30 min | Instrucciones prácticas |
| **[03. MySQL vs PostgreSQL](03_mysql_vs_postgresql.md)** | 10 min | Entender diferencias |
| **[06. Guía IntelliJ](06_guia_intellij_env.md)** | 5 min | Cargar `.env` en IDE |
| **[07. Resumen de Archivos](07_resumen_archivos.md)** | 5 min | Referencia rápida |
| **[08. Mapa de Decisiones](08_mapa_de_decisiones.md)** | 5 min | Decisiones visuales |
| **[04. Checklist](04_checklist_rubrica_minima.md)** | 5 min | Verificar completitud |
| **[05. Actividad Individual](05_actividad_individual.md)** | - | Tu tarea |

---

## 🚀 Quick Start (2 min)

### Opción 1: H2 (el más fácil, sin instalar nada)
```bash
cd Tickets
./mvnw spring-boot:run
```
✅ Accede a http://localhost:8080/ticket-app/tickets

### Opción 2: MySQL Local
```bash
cd Tickets
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

### Opción 3: Supabase (en la nube)
```bash
cd Tickets
# 1. Copia .env.example a .env
cp .env.example .env
# 2. Edita .env con tus credenciales de Supabase
# 3. Ejecuta:
export SPRING_PROFILES_ACTIVE=supabase
./mvnw spring-boot:run
```

---

## 📂 Estructura de Archivos

```
Tickets/
├── src/main/resources/
│   ├── application.yml              ← Base común (todos los perfiles)
│   ├── application-h2.yml           ← H2 en memoria
│   ├── application-mysql.yml        ← MySQL local
│   └── application-supabase.yml     ← Supabase PostgreSQL
│
├── .env.example                     ← Plantilla (subir a Git ✅)
├── .env.local                      ← Ambiente local (H2)
├── .env.dev                        ← Ambiente dev (MySQL)
├── .env.test                       ← Ambiente test (Supabase)
├── .env.prod                       ← Ambiente prod (Supabase)
└── .gitignore                     ← Incluye .env
```

---

## 🎯 Los Tres Perfiles

| Perfil | BD | Dónde | Cuándo Usar | Arranca | Requiere |
|--------|-----|-------|------------|---------|----------|
| **h2** | H2 (memoria) | Tu PC | Desarrollo rápido | `./mvnw spring-boot:run` | - |
| **mysql** | MySQL | Tu PC | Desarrollo con datos | `-Dspring.profiles.active=mysql` | XAMPP |
| **supabase** | PostgreSQL | Nube | Test/Producción | `-Dspring.profiles.active=supabase` | Variables de entorno |

---

## 🏠 Ambientes (Environments)

Cada ambiente usa un perfil diferente y tiene su propio archivo `.env`:

| Ambiente | Perfil | BD | Archivo .env |
|----------|--------|-----|--------------|
| **local** | h2 | H2 (memoria) | `.env.local` |
| **dev** | mysql | MySQL (XAMPP) | `.env.dev` |
| **test** | supabase | Supabase | `.env.test` |
| **prod** | supabase | Supabase | `.env.prod` |

### Cómo usar

```bash
# Copiar el archivo de ambiente que necesites
cp .env.local .env    # Para desarrollo rápido
cp .env.dev .env       # Para desarrollo con MySQL
cp .env.test .env     # Para pruebas
cp .env.prod .env     # Para producción

# Ejecutar la aplicación
./mvnw spring-boot:run
```

---

## 🔐 Variables de Entorno

Cada archivo `.env` incluye el perfil a usar:

```bash
# .env.local (desarrollo rápido)
SPRING_PROFILES_ACTIVE=h2

# .env.dev (desarrollo con MySQL)
SPRING_PROFILES_ACTIVE=mysql
DB_URL=jdbc:mysql://localhost:3306/tickets_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=root
DB_PASSWORD=

# .env.test / .env.prod (Supabase)
SPRING_PROFILES_ACTIVE=supabase
DB_URL=jdbc:postgresql://[HOST]:5432/postgres
DB_USER=postgres
DB_PASSWORD=[TU_PASSWORD]
```

**Protección:**
- ✅ `.env.*` → NO commitear (contienen credenciales)
- ✅ `.gitignore` → incluye `.env*`
- ✅ Solo `.env.example` → commitear (plantilla sin datos reales)

---

## 💡 Cómo Funcionan los Perfiles

```mermaid
flowchart TD
    base[application.yml<br/>Puerto: 8080<br/>Perfil: h2] --> q{Cuál perfil?}
    q -->|"spring.profiles.active=h2"| h2[h2 → application-h2.yml]
    q -->|"spring.profiles.active=mysql"| mysql[mysql → application-mysql.yml]
    q -->|"spring.profiles.active=supabase"| supabase[supabase → application-supabase.yml]
    h2 --> boot1[Spring Boot]
    mysql --> boot2[Spring Boot + .env]
    supabase --> boot3[Spring Boot + .env]
    boot1 --> run[App iniciada]
    boot2 --> run
    boot3 --> run
```

---

## 🧪 Verificación

Después de arrancar, deberías ver en los logs:

```
The following profiles are active: mysql
HikariPool-1 - Starting...
HikariPool-1 - Connection is working...
```

Luego accede a: **http://localhost:8080/ticket-app/tickets**

---

## 🛠️ Para IntelliJ IDEA

1. Instala plugin **EnvFile**
2. Ve a **[Guía IntelliJ](06_guia_intellij_env.md)**
3. Configura tu Run Configuration
4. Ejecuta (botón ▶)

---

## 📝 Tu Actividad

Ve a **[Actividad Individual](05_actividad_individual.md)** para tu tarea.

---

## 🤔 Dudas Frecuentes

**P: ¿Cuál perfil debo usar en desarrollo?**  
R: H2 para empezar (rápido), luego MySQL si necesitas datos persistentes.

**P: ¿Y si no puedo conectar a Supabase?**  
R: Ve a **[Mapa de Decisiones](08_mapa_de_decisiones.md)** → Troubleshooting

**P: ¿Debo commitear `.env`?**  
R: **Nunca.** Commitea `.env.example`, protege `.env` en `.gitignore`.

**P: ¿Puedo usar esta configuración en producción?**  
R: Sí, Supabase está listo para producción. Para más control, usa Docker/Kubernetes.

---

**[← Volver a Lecciones](../)**
