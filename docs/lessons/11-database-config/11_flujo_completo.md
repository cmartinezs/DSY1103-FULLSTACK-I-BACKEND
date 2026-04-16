# 🔗 Flujo Completo: Cómo Todo se Conecta

## 1️⃣ Flujo de Carga al Arrancar Spring Boot

```mermaid
flowchart TD
    start[./mvnw spring-boot:run] --> config[Lee application.yml<br/>Puerto: 8080<br/>Perfil: h2]
    config --> env{"¿Hay .env<br/>cargado?"}
    env -->|"Sí"| load_env[Lee SPRING_PROFILES_ACTIVE]
    env -->|"No"| use_default[Usa valores por defecto]
    load_env --> use_default
    use_default --> profile{"¿Perfil activo?"}
    profile -->|h2| h2[application-h2.yml]
    profile -->|mysql| mysql[application-mysql.yml]
    profile -->|supabase| supabase[application-supabase.yml]
    h2 --> var1{"¿Tiene ${VAR}?"}
    mysql --> var2{"¿Tiene ${VAR}?"}
    supabase --> var3{"¿Tiene ${VAR}?"}
    var1 -->|"Sí"| resolve[Busca en .env]
    var2 --> resolve
    var3 --> resolve
    resolve --> final[Configuración FINAL]
    var1 -->|"No"| final
    var2 -->|"No"| final
    var3 -->|"No"| final
    final --> hikari[Hikari Connection Pool]
    hikari --> connect{Connection?}
    connect -->|"Éxito"| ok[App arrancada]
    connect -->|"Fallo"| fail[Error: Connection refused]
```

---

## 2️⃣ Ejemplo Práctico: Cambiar de MySQL a Supabase

```mermaid
flowchart LR
    subgraph Antes["ANTES (MySQL local)"]
        E1[.env<br/>SPRING_PROFILES_ACTIVE=mysql<br/>MYSQL_USERNAME=root]
    end
    subgraph Después["DESPUÉS (Supabase)"]
        E2[.env MODIFICADO<br/>SPRING_PROFILES_ACTIVE=supabase<br/>DB_HOST=db.xxxx.supabase.co]
    end
    E1 --> Y1[application-mysql.yml] --> Conn1[jdbc:mysql://localhost:3306]
    E2 --> Y2[application-supabase.yml] --> Conn2[jdbc:postgresql://db.xxxx.supabase.co:5432]
```

---

## 3️⃣ Dónde Entra cada Concepto

```mermaid
flowchart TB
    subgraph Java["Tu código Java (Controller, Service, Repository, Model)"]
        code[NO CAMBIA]
    end
    
    subgraph Config["Spring Boot Configuration<br/>application-{profile}.yml + .env"]
        conf[AQUÍ CAMBIA<br/>DB URL, User, Password]
    end
    
    subgraph Hibernate["Hibernate + Driver JDBC"]
        hib[MySQL Driver<br/>PostgreSQL Driver<br/>H2 Driver]
    end
    
    subgraph DB["Base de Datos Real"]
        dbs["H2 | MySQL | PostgreSQL<br/>Solo una activa a la vez"]
    end
    
    code --> conf
    conf --> hib
    hib --> dbs
```

---

## 4️⃣ Checklist: Verification at Each Step

| Paso | Verificación |
|------|----------------|
| 1 | application.yml, -h2.yml, -mysql.yml, -supabase.yml existen |
| 2 | .env.example existe, .env local NO en Git, .gitignore protege .env |
| 3 | Plugin EnvFile instalado o variables de entorno definidas |
| 4 | Logs muestran perfil activo y "HikariPool-1 - Connection is working..." |
| 5 | .env NO en repositorio, .env.example SÍ |

---

## 5️⃣ Traducción: De Concepto a Acción

```mermaid
flowchart TD
    C["CONCEPTO: MySQL sin credenciales hardcodeadas"] --> A1["Crea application-mysql.yml<br/>spring.datasource.username: ${MYSQL_USERNAME}"]
    A1 --> A2[Crea .env<br/>MYSQL_USERNAME=root]
    A2 --> A3[IntelliJ lee .env]
    A3 --> A4[Spring Boot lee variables]
    A4 --> R[Conecta a MySQL sin exponer credenciales]
```

---

## 6️⃣ Flujo de Diagnóstico: Si Algo No Funciona

```
🔴 Error: "Connection refused"
• ¿Verificaste que la BD está corriendo? (XAMPP, Supabase, H2)
• ¿Verificaste las credenciales? (Host, Puerto, User/Password)
• ¿Verificaste que cargó el perfil? (busca en logs)

🔴 Error: "Variables vacías"
• ¿Instalaste plugin EnvFile?
• ¿Configuraste Run Configuration para usar .env?
• O define manualmente en Environment variables

🔴 Logs dicen "The following profiles are active: []"
• Verifica application.yml tiene spring.profiles.active
• O pasa --spring.profiles.active=mysql

✅ Logs dicen "HikariPool-1 - Connection is working..."
→ TODO FUNCIONA
```

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
