# Tickets-11: Lección 11 - Configuración de Bases de Datos

## 📋 Descripción

Este proyecto implementa la **Lección 11: Configuración de Bases de Datos** del curso DSY1103 Fullstack I.

Implementa perfiles de Spring Boot para soportar múltiples bases de datos sin modificar código Java:
- **H2** (desarrollo en-memory)
- **MySQL** (local vía XAMPP)
- **PostgreSQL** (Supabase en la nube)

## 🔄 Cambios desde Lección 10

### 1. Drivers de BD (pom.xml)
- ✅ Agregadas: `mysql-connector-j`, `postgresql`
- H2 ya estaba presente

### 2. Perfiles de Configuración
- ✅ **application.yml**: Configuración base (servidor, puerto, contexto)
- ✅ **application-h2.yml**: H2 in-memory para desarrollo
- ✅ **application-mysql.yml**: MySQL con variables de entorno
- ✅ **application-supabase.yml**: PostgreSQL con variables de entorno

```yaml
# application-h2.yml
spring:
  config:
    activate:
      on-profile: h2
  datasource:
    url: jdbc:h2:mem:ticketsdb
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop

# application-mysql.yml
spring:
  config:
    activate:
      on-profile: mysql
  datasource:
    url: ${MYSQL_URL:jdbc:mysql://localhost:3306/tickets_db}
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:}
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: update

# application-supabase.yml
spring:
  config:
    activate:
      on-profile: supabase
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQL10Dialect
    hibernate:
      ddl-auto: update
```

### 3. Variables de Entorno (.env.example)
- ✅ Plantilla de variables para MySQL y Supabase
- ✅ Instrucciones de uso

```env
# MySQL
MYSQL_URL=jdbc:mysql://localhost:3306/tickets_db?useSSL=false
MYSQL_USERNAME=root
MYSQL_PASSWORD=

# Supabase
DB_HOST=your-project.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=your-password
```

## 🧪 Ejecutar con Diferentes Perfiles

```bash
# H2 (por defecto)
./mvnw spring-boot:run

# O explícitamente
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"

# MySQL (después de configurar variables)
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

# Supabase (después de configurar variables)
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=supabase"
```

## ⚙️ Configuración en IntelliJ IDEA

1. **Edit Configurations** → Spring Boot
2. **VM options**: `-Dspring.profiles.active=h2` (o mysql/supabase)
3. **Environment variables**: Cargar desde `.env` usando EnvFile plugin
4. Click **Apply** → **OK**

## ✅ Validación

- [x] Proyecto compila con todos los perfiles
- [x] Tests pasan (perfil por defecto: h2)
- [x] Archivo .env.example documenta variables
- [x] Cada perfil tiene su configuración JPA adecuada
- [x] Sin credenciales hardcodeadas en código

## 📚 Referencias

- Lección: `docs/lessons/11-database-config/`
- Spring Boot Profiles: https://spring.io/blog/2015/02/04/what-s-new-in-spring-boot-1-2-0-m1-boot-properties-files
- MySQL Connector: https://dev.mysql.com/downloads/connector/j/
- PostgreSQL Driver: https://jdbc.postgresql.org/

## 📦 Estructura

```
Tickets-11/
├── src/main/resources/
│   ├── application.yml              (Base)
│   ├── application-h2.yml          (H2 profile)
│   ├── application-mysql.yml       (MySQL profile)
│   ├── application-supabase.yml    (PostgreSQL profile)
│   └── ...
├── .env.example                     (Plantilla variables)
├── pom.xml                          (Drivers agregados)
└── README.md
```

---

**Base**: Lección 10 (JPA Intro)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2/MySQL/PostgreSQL  
**Estado**: ✅ Completada y testeada
