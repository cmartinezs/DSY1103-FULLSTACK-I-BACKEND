# Tickets-11: Lección 11 - Configuración de Bases de Datos

## 📋 Descripción

Este proyecto implementa la **Lección 11: Configuración de Bases de Datos** del curso DSY1103 Fullstack I.

Perfiles de Spring Boot para múltiples bases de datos (H2, MySQL, PostgreSQL/Supabase).

---

## 🔄 Cambios desde Lección 10

### 1. Dependencias (pom.xml)
- ✅ `mysql-connector-j`
- ✅ `postgresql`
- ✅ `spring-dotenv` para cargar .env
- ✅ `spring-boot-h2console`

### 2. Perfiles vs Entornos

| Concepto | Descripción |
|----------|-------------|
| **Perfil** | Archivo de configuración (`application-{profile}.yml`) |
| **Entorno** | Valores de las variables para ese perfil |

| Perfil | Archivo | Entornos que lo usan |
|--------|---------|---------------------|
| `h2` | `application-h2.yml` | local |
| `mysql` | `application-mysql.yml` | dev |
| `supabase` | `application-supabase.yml` | test, prod |

### 3. Archivos de Perfil

| Archivo | Perfil | Base de Datos |
|---------|-------|--------------|
| `application-h2.yml` | `h2` | H2 (memoria) |
| `application-mysql.yml` | `mysql` | MySQL (XAMPP) |
| `application-supabase.yml` | `supabase` | Supabase (PostgreSQL) |

### 4. Archivos de Entorno

| Archivo | Perfil | Entorno | Descripción |
|---------|-------|---------|-------------|
| `.env.local` | `h2` | local | Desarrollo rápido |
| `.env.dev` | `mysql` | dev | Desarrollo con MySQL |
| `.env.test` | `supabase` | test | Pruebas en Supabase |
| `.env.prod` | `supabase` | prod | Producción (mismo perfil, diferentes valores) |

### 5. application.yml (Base)
- ✅ Configuración base sin credenciales
- ✅ Niveles de logging por perfil

### 6. Carga de Variables de Entorno (.env)
- ✅ Dependencia `spring-dotenv` para cargar `.env` automáticamente
- ✅ Archivo `.env.example` con plantilla
- ⚠️ **Variables sensibles**: NUNCA hacer commit de `.env` con credenciales reales

---

## 🌍 Cómo Usar Perfiles y Entornos

### Opción 1: Copiar archivo .env

```bash
# Elegir el entorno que necesitas
copy .env.local .env    # Perfil: h2
copy .env.dev .env      # Perfil: mysql
copy .env.test .env     # Perfil: supabase
copy .env.prod .env     # Perfil: supabase (valores diferentes)

# Ejecutar la aplicación
./mvnw spring-boot:run
```

### Opción 2: Con variable de entorno

```bash
# PowerShell (Windows)
$env:SPRING_PROFILES_ACTIVE="mysql"
./mvnw.cmd spring-boot:run

# bash/Linux/macOS
SPRING_PROFILES_ACTIVE=mysql ./mvnw spring-boot:run
```

### Opción 3: Por línea de comandos

```bash
./mvnw spring-boot:run -Dspring.profiles.active=h2
./mvnw spring-boot:run -Dspring.profiles.active=mysql
./mvnw spring-boot:run -Dspring.profiles.active=supabase
```

---

## 📝 Archivos de Configuración

| Archivo | Descripción |
|---------|-------------|
| `application.yml` | Configuración base |
| `application-h2.yml` | Perfil H2 |
| `application-mysql.yml` | Perfil MySQL |
| `application-supabase.yml` | Perfil Supabase |
| `.env.local` | Entorno local (perfil h2) |
| `.env.dev` | Entorno dev (perfil mysql) |
| `.env.test` | Entorno test (perfil supabase) |
| `.env.prod` | Entorno prod (perfil supabase) |
| `.env.example` | Plantilla de variables |

---

## 🧪 Validación

- [x] Perfil h2 funciona con entorno local
- [x] Perfil mysql funciona con entorno dev
- [x] Perfil supabase funciona con entorno test
- [x] Perfil supabase funciona con entorno prod (mismos valores de perfil, diferentes variables)

---

**Base**: Lección 10 (JPA Intro)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada