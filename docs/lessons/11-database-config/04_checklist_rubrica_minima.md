# Lección 11 — Checklist y rúbrica mínima

---

## Checklist para MySQL (XAMPP)

- ☐ XAMPP tiene Apache y MySQL corriendo (estado "Running")
- ☐ La base de datos `tickets_db` existe en phpMyAdmin con cotejamiento `utf8mb4_unicode_ci`
- ☐ `application.yml` tiene `url: jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago`
- ☐ `application.yml` tiene `driver-class-name: com.mysql.cj.jdbc.Driver`
- ☐ `pom.xml` tiene la dependencia `mysql-connector-j` con `scope: runtime`
- ☐ La aplicación arranca sin errores de conexión
- ☐ phpMyAdmin muestra las tablas creadas automáticamente por JPA

## Checklist para Supabase (PostgreSQL)

- ☐ El proyecto en Supabase fue creado correctamente
- ☐ La contraseña de la base de datos está guardada (no se puede recuperar)
- ☐ `application.yml` tiene la URL JDBC de Supabase (formato `jdbc:postgresql://...`)
- ☐ `application.yml` tiene `driver-class-name: org.postgresql.Driver`
- ☐ `pom.xml` tiene la dependencia `postgresql` con `scope: runtime`
- ☐ La aplicación arranca y conecta a Supabase sin errores
- ☐ En el Table Editor de Supabase aparece la tabla `tickets`

---

## Checklist de pruebas

- ☐ `POST /ticket-app/tickets` persiste el ticket en la base de datos activa
- ☐ `GET /ticket-app/tickets` devuelve los tickets almacenados
- ☐ Tras reiniciar la aplicación, los datos siguen presentes
- ☐ El mismo código Java funciona con ambas bases de datos cambiando solo el `application.yml`

---

## Errores comunes

| Error | Causa probable | Solución |
|---|---|---|
| `Communications link failure` | MySQL no está corriendo | Iniciar MySQL en XAMPP |
| `Access denied for user 'root'` | Contraseña incorrecta en MySQL | En XAMPP local, dejar `password:` vacío |
| `Connection to db.xxx.supabase.co refused` | URL o puerto incorrecto | Copiar la URL JDBC exactamente desde Supabase → Settings → Database |
| `No suitable driver found` | Driver faltante o `driver-class-name` incorrecto | Verificar la dependencia en `pom.xml` y el driver en `application.yml` |
| `org.postgresql.util.PSQLException: FATAL: password authentication failed` | Contraseña de Supabase incorrecta | Regenerar la contraseña en Supabase → Settings → Database → Reset database password |
| `Table 'tickets_db.tickets' doesn't exist` | `ddl-auto` es `none` o `validate` y la tabla no fue creada | Cambiar a `ddl-auto: update` |
