# 🗺️ Mapa de Decisiones: Qué Perfil Usar

```mermaid
flowchart TD
    q{"¿Cuál es tu<br/>caso de uso?"}
    q -->|"Desarrollo<br/>ultra rápido"| h2[H2 MEMORY<br/>en memoria]
    q -->|"Desarrollo local<br/>datos persiste" | mysql[MYSQL<br/>XAMPP]
    q -->|"Producción /<br/>Entrega final" | supabase[SUPABASE<br/>PostgreSQL]
    h2 --> perf_h2[Perfil: h2]
    mysql --> perf_mysql[Perfil: mysql]
    supabase --> perf_supabase[Perfil: supabase]
    perf_h2 --> res_h2[BD se reinicia<br/>cada ejecución<br/>Requiere: nada]
    perf_mysql --> res_mysql[BD persistente<br/>en tu PC<br/>Requiere: XAMPP]
    perf_supabase --> res_supabase[BD en nube<br/>accesible siempre<br/>Requiere: cuenta]
```

---

## 🎯 Por Etapa del Proyecto

```mermaid
flowchart LR
    subgraph "Semana 1-2: Aprendizaje"
        step1[./mvnw spring-boot:run<br/>H2 por defecto]
        step2[./mvnw spring-boot:run<br/>--spring.profiles.active=mysql]
    end
    step1 -->|"o bien"| step2
    step2 --> semana3[Semana 3: Entrega Final]
    semana3 --> f1[Copia .env.example a .env]
    f1--> f2[Rellena credenciales]
    f2--> f3[export SPRING_PROFILES_ACTIVE=supabase]
    f3--> f4[./mvnw spring-boot:run]
```

---

## 🔀 Cambiar de Perfil en 3 Formas

### Forma 1️⃣: Línea de Comandos
```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

### Forma 2️⃣: Variable de Entorno (Recomendado)
```bash
# Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE="mysql"
./mvnw spring-boot:run

# Linux/macOS
export SPRING_PROFILES_ACTIVE=mysql
./mvnw spring-boot:run
```

### Forma 3️⃣: Archivo `.env` + IntelliJ
1. Crea `.env` con `SPRING_PROFILES_ACTIVE=mysql`
2. Instala plugin **EnvFile** en IntelliJ
3. Configura Run Configuration para cargar `.env`
4. Ejecuta (botón ▶)

---

## 📊 Matriz de Compatibilidad

| Característica | H2 | MySQL | Supabase |
|---|:---:|:---:|:---:|
| BD en memoria | ✅ | ❌ | ❌ |
| Datos persistentes | ❌ | ✅ | ✅ |
| Requiere software adicional | ❌ | XAMPP | Cuenta online |
| Acceso desde otro PC | ❌ | ❌ | ✅ |
| Gratis | ✅ | ✅ | ✅ (limits) |
| Para producción | ❌ | ✅ | ✅ |
| Requiere variables de entorno | ❌ | ❌ | ✅ |

---

## 🛠️ Troubleshooting Rápido

### "No puedo conectarme a MySQL"
```bash
# Verifica que XAMPP está corriendo
# Verifica que la BD "tickets_db" existe en phpMyAdmin
# Verifica application-mysql.yml tiene URL correcta
```

### "Variables de entorno no cargan"
```bash
# En IntelliJ: Instala plugin EnvFile
# O define manualmente en Edit Configurations → Environment variables
# O usa: spring-dotenv (ver pom.xml)
```

### "Supabase connection refused"
```bash
# Verifica que DB_HOST, DB_USER, DB_PASSWORD son correctos
# Verifica que la IP de tu PC está en IP whitelist de Supabase (Settings)
```

---

*Para más detalles, ve a [Guión Paso a Paso](02_guion_paso_a_paso.md)*
