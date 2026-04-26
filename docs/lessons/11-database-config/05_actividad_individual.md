# Lección 11 — Actividad individual: conectar Tickets a Supabase

## Contexto

Tu aplicación ya corre con H2 (perfil por defecto) o MySQL local (XAMPP). Esta actividad te pide conectarla a Supabase (PostgreSQL en la nube) y verificar que los tickets persisten correctamente.

---

## Parte 1: crear el proyecto en Supabase

1. Crea una cuenta en `https://supabase.com` si aún no tienes una
2. Crea un nuevo proyecto llamado `dsy1103-tickets`
3. Elige la región más cercana (São Paulo es la más cercana de Chile)
4. Anota la contraseña de la base de datos — no se puede recuperar después

---

## Parte 2: configurar el entorno para Supabase

Edita `.env.test` con los valores de tu proyecto en Supabase:

```env
SPRING_PROFILES_ACTIVE=supabase
DB_HOST=db.TU_HOST.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=TU_CONTRASEÑA
```

> **¿Dónde encuentro el host?**
> En Supabase → **Project Settings** → **Database** → **Connection string** → pestaña **JDBC**. El host es la parte `db.xxxx.supabase.co`.

Luego copia ese archivo como `.env` activo:

```bash
copy .env.test .env
```

---

## Parte 3: arrancar con el perfil Supabase

```bash
./mvnw.cmd spring-boot:run
```

Verifica en los logs:
```
The following profiles are active: supabase
HikariPool-1 - Start completed.
```

---

## Parte 4: verificar la tabla en Supabase

En el **Table Editor** de Supabase, verifica que la tabla `tickets` fue creada automáticamente por Hibernate.

Luego prueba desde tu cliente HTTP:

| Prueba | Endpoint | Resultado esperado |
|---|---|---|
| Crear ticket | `POST /ticket-app/tickets` | `201 Created`, ticket guardado en Supabase |
| Listar tickets | `GET /ticket-app/tickets` | Datos en la nube |
| Reiniciar app | — | Los tickets siguen presentes |

---

## Parte 5: volver a MySQL local

Cambia el entorno activo a MySQL:

```bash
copy .env.dev .env
./mvnw.cmd spring-boot:run
```

Verifica que la aplicación vuelve a usar MySQL local sin modificar ningún archivo Java.

El objetivo es que puedas cambiar entre bases de datos en menos de un minuto, cambiando solo el `.env`.

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| `.env.test` configurado correctamente con credenciales de Supabase | 25% |
| Aplicación arranca con perfil `supabase` y logs confirman conexión | 25% |
| Tabla `tickets` visible en Supabase Table Editor | 20% |
| `POST` y `GET` funcionan con datos persistidos en la nube | 20% |
| La aplicación vuelve a MySQL cambiando solo el `.env` | 10% |
