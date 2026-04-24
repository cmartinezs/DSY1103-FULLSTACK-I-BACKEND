# Lección 11 — Checklist y rúbrica mínima

---

## Prerrequisito (Lección 10)

- ☐ La lección 10 está completa: JPA con H2 funciona

---

## Checklist de configuración de perfiles

- ☐ `application.yml` tiene la configuración base
- ☐ `application-h2.yml` existe con H2 en memoria
- ☐ `application-mysql.yml` existe con MySQL (XAMPP)
- ☐ `application-supabase.yml` existe con PostgreSQL (Supabase)
- ☐ `.env.example` existe con plantilla de variables
- ☐ `spring-dotenv` en `pom.xml` para cargar `.env`

---

## Checklist para MySQL (XAMPP)

- ☐ XAMPP tiene Apache y MySQL corriendo
- ☐ La base de datos `tickets_db` existe en phpMyAdmin
- ☐ `application-mysql.yml` tiene `url: jdbc:mysql://localhost:3306/tickets_db`
- ☐ `application-mysql.yml` tiene `driver-class-name: com.mysql.cj.jdbc.Driver`
- ☐ `pom.xml` tiene la dependencia `mysql-connector-j` con `scope: runtime`
- ☐ La aplicación arranca sin errores de conexión

---

## Checklist para Supabase (PostgreSQL)

- ☐ El proyecto en Supabase fue creado correctamente
- ☐ La contraseña de la base de datos está guardada
- ☐ `application-supabase.yml` tiene la URL JDBC de Supabase
- ☐ `application-supabase.yml` tiene `driver-class-name: org.postgresql.Driver`
- ☐ `pom.xml` tiene la dependencia `postgresql` con `scope: runtime`

---

## Checklist de pruebas

- ☐ `./mvnw spring-boot:run` funciona con H2 (perfil por defecto)
- ☐ `./mvnw spring-boot:run -Dspring.profiles.active=mysql` funciona con MySQL
- ☐ `./mvnw spring-boot:run -Dspring.profiles.active=supabase` funciona con Supabase
- ☐ `POST /ticket-app/tickets` persiste en la base de datos activa
- ☐ Los datos persisten tras reiniciar (excepto H2 con create-drop)

---

## Errores comunes

| Error | Causa probable | Solución |
|---|---|---|
| `Communications link failure` | MySQL no está corriendo | Iniciar MySQL en XAMPP |
| `Access denied for user 'root'` | Contraseña incorrecta | Dejar `password:` vacío en local |
| `Connection to db.xxx.supabase.co refused` | URL incorrecta | Copiar URL desde Supabase → Settings → Database |
| `No suitable driver found` | Driver faltante | Verificar dependencia en `pom.xml` |
