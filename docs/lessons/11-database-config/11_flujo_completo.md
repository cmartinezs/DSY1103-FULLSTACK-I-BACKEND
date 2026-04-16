# 🔗 Flujo Completo: Cómo Todo se Conecta

## 1️⃣ Flujo de Carga al Arrancar Spring Boot

```
┌─────────────────────────────────────────────────────────────┐
│  INICIO: ./mvnw spring-boot:run                             │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
    ┌──────────────────────────────────────┐
    │  Spring Lee application.yml          │
    │  - Nome: Tickets                     │
    │  - Puerto: 8080                      │
    │  - Perfil activo: h2 (default)       │
    │  - JPA config                        │
    └──────────────┬───────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────┐
    │  ¿Hay .env cargado?                  │
    │  (vía plugin EnvFile o variables SO) │
    └──┬─────────────────────────────┬─────┘
       │                             │
      SÍ                             NO
       │                             │
       ▼                             ▼
    Leer                      Usar valores por defecto
    SPRING_PROFILES_ACTIVE    de application.yml
       │                             │
       └──────────┬──────────────────┘
                  │
                  ▼
    ┌──────────────────────────────────┐
    │  ¿Cuál es el perfil activo?      │
    │  - h2                            │
    │  - mysql                         │
    │  - supabase                      │
    └──┬──────────┬──────────────┬──────┘
       │          │              │
      h2         mysql       supabase
       │          │              │
       ▼          ▼              ▼
    Cargar    Cargar         Cargar
    app-h2   app-mysql     app-supabase
       │          │              │
       └──────────┼──────────────┘
                  │
                  ▼
    ┌──────────────────────────────────────┐
    │  Si tiene variables: ${VAR}          │
    │  Spring busca en .env o SO           │
    │                                      │
    │  Si ${MYSQL_URL} → busca en .env     │
    │  Si no existe → usa valor por defecto│
    └──────────────┬───────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────┐
    │  Configuración FINAL para este       │
    │  perfil + variables                  │
    │                                      │
    │  Ejemplo con mysql:                  │
    │  - DB URL: jdbc:mysql://localhost... │
    │  - User: root                        │
    │  - Password: (vacío)                 │
    └──────────────┬───────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────┐
    │  Hikari Connection Pool              │
    │  Intenta conectar a la BD            │
    └──┬──────────────────────────┬────────┘
       │                          │
      ✅ SUCCESS                ❌ FAILED
       │                          │
       ▼                          ▼
    App arranca           Error: Connection refused
    con la BD listo        (revisa credenciales)
```

---

## 2️⃣ Ejemplo Práctico: Cambiar de MySQL a Supabase

```
ANTES (MySQL local):
┌──────────────────────────────────┐
│ .env                             │
├──────────────────────────────────┤
│ SPRING_PROFILES_ACTIVE=mysql     │
│ MYSQL_USERNAME=root              │
│ MYSQL_PASSWORD=                  │
└──────────────────────────────────┘
         ▼
    application-mysql.yml cargado
         ▼
    spring.datasource.url = jdbc:mysql://localhost:3306/...
         ▼
    ✅ Conecta a XAMPP


DESPUÉS (Supabase):
┌──────────────────────────────────┐
│ .env (MODIFICADO)                │
├──────────────────────────────────┤
│ SPRING_PROFILES_ACTIVE=supabase  │
│ DB_HOST=db.xxxx.supabase.co      │
│ DB_USER=postgres                 │
│ DB_PASSWORD=mi_password          │
└──────────────────────────────────┘
         ▼
    application-supabase.yml cargado
         ▼
    spring.datasource.url = jdbc:postgresql://db.xxxx.supabase.co:5432/postgres
         ▼
    ✅ Conecta a Supabase en la nube


SIN CAMBIAR NADA EN JAVA ✨
El código es idéntico, solo configuración.
```

---

## 3️⃣ Dónde Entra cada Concepto

```
              ╔═══════════════════════════════════╗
              ║  Tu código Java (Controller,      ║
              ║   Service, Repository, Model)    ║
              ║                                   ║
              ║  👉 NO CAMBIA 👈                 ║
              ║                                   ║
              ║  @Repository, @Service etc       ║
              ║  Usan JPA/Hibernate              ║
              ║  abstracto, no saben de BD       ║
              ╚═══════════════════════════════════╝
                            ▲
                            │ (usa)
                            │
              ╔═══════════════════════════════════╗
              ║  Spring Boot Configuration       ║
              ║  (Perfiles + Variables)          ║
              ║                                   ║
              ║  ← AQUÍ CAMBIA ←                 ║
              ║                                   ║
              ║  application-{profile}.yml       ║
              ║  + .env                          ║
              ║                                   ║
              ║  Define:                         ║
              ║  - DB URL                        ║
              ║  - Username/Password             ║
              ║  - Driver JDBC                   ║
              ║  - Hibernate dialect             ║
              ╚═══════════════════════════════════╝
                            ▲
                            │ (inyecta)
                            │
              ╔═══════════════════════════════════╗
              ║  Hibernate + Driver JDBC         ║
              ║                                   ║
              ║  ✓ MySQL Driver                  ║
              ║  ✓ PostgreSQL Driver             ║
              ║  ✓ H2 Driver                     ║
              ║                                   ║
              ║  La config dice "usa MySQL"      ║
              ║  → Hibernate carga el driver     ║
              ║  → Conexión abierta              ║
              ╚═══════════════════════════════════╝
                            ▲
                            │ (conecta a)
                            │
              ╔═══════════════════════════════════╗
              ║  Base de Datos Real              ║
              ║                                   ║
              ║  ┌─ H2 (en memoria)             ║
              ║  ├─ MySQL (XAMPP)               ║
              ║  └─ PostgreSQL (Supabase)       ║
              ║                                   ║
              ║  Solo una activa a la vez       ║
              ╚═══════════════════════════════════╝
```

---

## 4️⃣ Checklist: Verification at Each Step

```
PASO 1: Estructua
  ✓ application.yml existe
  ✓ application-h2.yml existe
  ✓ application-mysql.yml existe
  ✓ application-supabase.yml existe

PASO 2: Variables
  ✓ .env.example existe (template)
  ✓ .env existe (local, NO en Git)
  ✓ .env contiene SPRING_PROFILES_ACTIVE
  ✓ .env.gitignore protege .env

PASO 3: IntelliJ (si usas IDE)
  ✓ Plugin EnvFile instalado
  ✓ Run Configuration tiene .env configurado
  ✓ O: Variables de entorno definidas manualmente

PASO 4: Ejecución
  ✓ Logs muestran: "The following profiles are active: ..."
  ✓ Logs muestran: "HikariPool-1 - Connection is working..."
  ✓ API responde en http://localhost:8080/ticket-app/tickets

PASO 5: Seguridad
  ✓ .env NO está en el repositorio
  ✓ .env.example SÍ está en el repositorio
  ✓ Credenciales reales solo en .env local
```

---

## 5️⃣ Traducción: De Concepto a Acción

```
CONCEPTO: "Quiero usar MySQL sin hardcodear credenciales"
                        ▼
ACCIÓN 1: Crea application-mysql.yml con variables:
          spring.datasource.username: ${MYSQL_USERNAME}
                        ▼
ACCIÓN 2: Crea .env con valores reales:
          MYSQL_USERNAME=root
                        ▼
ACCIÓN 3: IntelliJ lee .env (via plugin)
                        ▼
ACCIÓN 4: Spring Boot lee las variables
                        ▼
RESULTADO: Conecta a MySQL sin exponer credenciales ✅
```

---

## 6️⃣ Flujo de Diagnóstico: Si Algo No Funciona

```
🔴 Error: "Connection refused"
├─ ¿Verificaste que la BD está corriendo?
│  ├─ MySQL: XAMPP iniciado ✓
│  ├─ Supabase: Accesible desde Internet ✓
│  └─ H2: Siempre está disponible ✓
│
├─ ¿Verificaste las credenciales?
│  ├─ Host: correcto
│  ├─ Puerto: 3306 (MySQL), 5432 (Supabase)
│  └─ Usuario/password: sin typos
│
└─ ¿Verificaste que cargó el perfil?
   └─ Busca en logs: "The following profiles are active: ..."

🔴 Error: "Variables vacías"
├─ ¿Instalaste plugin EnvFile?
├─ ¿Configuraste tu Run Configuration para usar .env?
├─ O define manualmente en Edit Configurations → Environment variables
└─ O usa librería spring-dotenv en pom.xml

🔴 Logs dicen "The following profiles are active: []"
├─ Verifica application.yml tiene spring.profiles.active
└─ O pasa -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

✅ Logs dicen "HikariPool-1 - Connection is working..."
└─ ¡TODO FUNCIONA! Accede a http://localhost:8080/ticket-app/tickets
```

---

*[← Volver a Lección 11](00_indice.md)*
