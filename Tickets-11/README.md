# Tickets-11: Lección 11 - Configuración de Bases de Datos

## 📋 Descripción

Este proyecto implementa la **Lección 11: Configuración de Bases de Datos** del curso DSY1103 Fullstack I.

Perfiles de Spring Boot para múltiples bases de datos (H2, MySQL, PostgreSQL/Supabase).

## 🎯 Caso de Uso Extendido (Sistema de Tickets con Gestión de Usuarios)

### Roles definidos
| Rol     | Descripción              |
|---------|--------------------------|
| USER    | Crea tickets, ve estado  |
| AGENT   | Recibe tickets asignados |
| ADMIN   | Supervisa y gestiona     |

### Modelo de datos
- **User**: id, name, email, role (USER/AGENT/ADMIN), active
- **Ticket**: id, title, description, status, createdAt, estimatedResolutionDate, effectiveResolutionDate, createdBy (User), assignedTo (User)

---

## 🔄 Cambios desde Lección 10

### 1. Dependencias (pom.xml)
- ✅ Agregadas: `mysql-connector-j`, `postgresql`

### 2. Perfiles de Configuración

#### application-h2.yml (Desarrollo en memoria)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:ticketsdb
    driverClassName: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

#### application-mysql.yml (MySQL local XAMPP)
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/ticketsdb}
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
    url: ${DB_URL:jdbc:postgresql://localhost:5432/ticketsdb}
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

---

## 📊 Requisitos del Caso Extendido por Lección

| Lección | Requisitos del Caso Extendido |
|---------|------------------------------|
| 10 | ✅ User entity con roles, Ticket con User relaciones, seed de datos |
| 11 | ✅ Perfiles con diferentes configs de BD para usuarios (H2, MySQL, Supabase) |
| 12 | Category/Tag relaciones con User |
| 13 | Historial con User |
| 14 | Flyway migrations con Foreign Keys a users |
| 15 | Notificaciones con User |
| 16 | Security con 3 roles (USER/AGENT/ADMIN) |
| 17 | Logging de operaciones de usuarios |
| 18 | Excepciones para casos de usuarios |

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

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] Perfil H2 funciona (desarrollo)
- [x] MySQL configurado (requiere BD local)
- [x] Supabase/PostgreSQL configurado (requiere cloud)
- [x] Sin credenciales hardcodeadas

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `application-h2.yml` | Configuración H2 (desarrollo) |
| `application-mysql.yml` | Configuración MySQL |
| `application-supabase.yml` | Configuración PostgreSQL/Supabase |
| `.env.example` | Plantilla de variables de entorno |

---

**Base**: Lección 10 (JPA Intro)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada