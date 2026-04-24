# Tickets-11: Lección 11 - Configuración de Bases de Datos

## 📋 Descripción

Este proyecto implementa la **Lección 11: Configuración de Bases de Datos** del curso DSY1103 Fullstack I.

Perfiles de Spring Boot para múltiples bases de datos (H2, MySQL, PostgreSQL/Supabase).

---

## 🔄 Cambios desde Lección 10

### 1. Dependencias (pom.xml)
- ✅ Agregadas: `mysql-connector-j`, `postgresql`

### 2. Perfiles de Configuración

#### application-h2.yml (Desarrollo)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:tickets_db
    driverClassName: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

#### application-mysql.yml (MySQL local XAMPP)
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/tickets_db}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: validate
```

#### application-supabase.yml (PostgreSQL en la nube)
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/tickets_db}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: validate
```

### 3. Variables de Entorno (.env.example)
- ✅ Plantilla para configuración de BD
- ✅ Sin credenciales hardcodeadas

### 4. application.yml (Base)
- ✅ Configuración base sin credenciales
- ✅ Niveles de logging por perfil

### 5. Carga de Variables de Entorno (.env)
- ✅ Dependencia `spring-dotenv` para cargar `.env` automáticamente
- ✅ Archivo `.env.example` con plantilla
- ⚠️ **Variables sensibles**: NUNCA hacer commit de `.env` con credenciales reales

---

## 🧪 Uso de Perfiles

```bash
# H2 (desarrollo, por defecto)
./mvnw spring-boot:run

# MySQL local
./mvnw spring-boot:run -Dspring.profiles.active=mysql

# Supabase (PostgreSQL cloud)
./mvnw spring-boot:run -Dspring.profiles.active=supabase
```

## 📝 Archivos de Configuración

| Archivo | Descripción |
|---------|-------------|
| `application.yml` | Configuración base |
| `application-h2.yml` | Perfil H2 (desarrollo) |
| `application-mysql.yml` | Perfil MySQL |
| `application-supabase.yml` | Perfil PostgreSQL/Supabase |
| `.env.example` | Plantilla de variables de entorno |

---

**Base**: Lección 10 (JPA Intro)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada