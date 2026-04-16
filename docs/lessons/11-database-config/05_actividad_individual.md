# Lección 11 — Actividad individual: conectar Category a Supabase

## Contexto

Tu aplicación está conectada a MySQL local (XAMPP). Esta actividad te pide conectarla a Supabase y verificar que tanto `Ticket` como `Category` persisten correctamente en la nube.

---

## Parte 1: crear el proyecto en Supabase

1. Crea una cuenta en `https://supabase.com` si aún no tienes una
2. Crea un nuevo proyecto llamado `dsy1103-tickets`
3. Elige la región más cercana
4. Anota la contraseña — no se puede recuperar después

---

## Parte 2: cambiar la configuración a Supabase

Modifica `application.yml` para conectar a Supabase. Comenta la configuración de MySQL para poder volver a ella fácilmente:

```yaml
spring:
  application:
    name: Tickets

  datasource:
    # MySQL (XAMPP) — comentado mientras usamos Supabase
    # url: jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
    # username: root
    # password:
    # driver-class-name: com.mysql.cj.jdbc.Driver

    # Supabase (PostgreSQL)
    url: jdbc:postgresql://db.TU_HOST.supabase.co:5432/postgres
    username: postgres
    password: TU_CONTRASEÑA
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"
```

Actualiza también `pom.xml`: comenta o elimina `mysql-connector-j` y agrega `postgresql`.

---

## Parte 3: verificar ambas entidades en Supabase

Arranca la aplicación y verifica en el **Table Editor** de Supabase:

- ☐ La tabla `tickets` fue creada con todas las columnas
- ☐ La tabla `categories` fue creada con todas las columnas

Luego prueba desde tu cliente HTTP:

| Prueba | Endpoint | Resultado esperado |
|---|---|---|
| Crear ticket | `POST /ticket-app/tickets` | `201 Created`, ticket en Supabase |
| Crear categoría | `POST /ticket-app/categories` | `201 Created`, categoría en Supabase |
| Listar tickets | `GET /ticket-app/tickets` | Datos guardados en la nube |
| Reiniciar app | — | Los datos siguen presentes |

---

## Parte 4: volver a MySQL local

Comenta la configuración de Supabase, descomenta la de MySQL y verifica que la aplicación vuelve a funcionar con XAMPP.

El objetivo es que puedas cambiar entre las dos bases de datos en menos de un minuto, sin tocar el código Java.

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| `application.yml` configurado correctamente para Supabase | 30% |
| Tablas `tickets` y `categories` creadas en Supabase | 30% |
| `POST` y `GET` funcionan con datos persistidos en la nube | 25% |
| El proyecto vuelve a funcionar con MySQL cambiando solo `application.yml` | 15% |
