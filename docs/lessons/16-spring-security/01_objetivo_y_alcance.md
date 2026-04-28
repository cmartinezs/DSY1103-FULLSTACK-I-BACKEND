# Lección 16 — Spring Security: Objetivo y Alcance

## ¿De dónde venimos?

En la Lección 15 implementaste **migraciones de base de datos con Flyway**: tu esquema está versionado, controlado y reproducible en cualquier entorno.

El siguiente paso natural es proteger esa API: actualmente cualquiera puede crear, modificar o eliminar tickets sin identificarse. Un usuario malicioso podría sabotear todo el sistema con una sola petición HTTP.

> 💡 **Conexión con Flyway:** Los usuarios que se autenticarán existen en la tabla `users`. En esta lección agregarás el campo `password` con una nueva migración Flyway — exactamente como aprendiste en la lección anterior.

---

## ¿Qué vas a construir?

Al terminar esta lección tu API tendrá:

1. **Entidad `User` actualizada:** campo `password` (hash BCrypt) + enum `Role` con tres niveles
2. **Migración V5:** agrega columna `password` a la tabla `users`
3. **Migración V6:** seed de 3 usuarios con contraseñas hasheadas
4. **`CustomUserDetailsService`:** carga credenciales desde `UserRepository` en tiempo real
5. **Autenticación HTTP Basic:** credenciales en el header `Authorization`
6. **Sesión STATELESS:** la API no guarda estado de sesión — cada petición se autentica por sí sola
7. **Tres niveles de autorización:**
   - `GET` tickets/categories/tags/users → público (sin autenticación)
   - `POST` y `PUT` tickets → ROLE_USER, ROLE_AGENT o ROLE_ADMIN
   - `DELETE` tickets + gestión de categorías/tags/usuarios → solo ROLE_ADMIN

### Flujo completo

```
1. POST /tickets — sin Authorization
   → 401 Unauthorized

2. POST /tickets — Authorization: Basic (ana.garcia / user123 = rol USER)
   → 201 Created ✅

3. POST /tickets — Authorization: Basic (carlos.lopez / user123 = rol AGENT)
   → 201 Created ✅

4. DELETE /tickets/by-id/1 — Authorization: Basic (ana.garcia = USER)
   → 403 Forbidden ❌

5. DELETE /tickets/by-id/1 — Authorization: Basic (admin = ADMIN)
   → 204 No Content ✅
```

---

## Los tres roles del sistema

| Rol | Descripción | Puede crear tickets | Puede editar tickets | Puede eliminar tickets |
|-----|-------------|:-------------------:|:--------------------:|:----------------------:|
| `USER` | Usuario final que reporta problemas | ✅ | Solo tickets propios | ❌ |
| `AGENT` | Agente de soporte que gestiona tickets | ✅ | Solo tickets asignados | ❌ |
| `ADMIN` | Administrador del sistema | ✅ | ✅ | ✅ |

---

## ¿Qué NO cubre esta lección? (y por qué)

| Tema | Razón |
|------|-------|
| JWT (JSON Web Tokens) | Requiere entender primero sesiones y autenticación básica |
| OAuth 2.0 (Google, GitHub) | Autenticación externa — primero domina autenticación local |
| Refresh tokens | Nivel siguiente después de dominar los fundamentos |
| Two-Factor Authentication | Nivel avanzado |
| Registro de usuarios desde la API | Se cubre en lecciones posteriores |

El foco de esta lección: **HTTP Basic Auth + usuarios en base de datos + reglas por rol + autorización por recurso + sesión STATELESS**.

---

## Requerimientos que implementamos

| ID | Requerimiento |
|----|---------------|
| **REQ-16-01** | `GET /tickets` y `GET /tickets/by-id/{id}` son públicos |
| **REQ-16-02** | `POST /tickets` requiere ROLE_USER, ROLE_AGENT o ROLE_ADMIN |
| **REQ-16-03** | `DELETE /tickets/by-id/{id}` requiere ROLE_ADMIN |
| **REQ-16-04** | Sin autenticación → 401 Unauthorized |
| **REQ-16-05** | Autenticación con rol insuficiente → 403 Forbidden |
| **REQ-16-06** | Contraseñas almacenadas con BCrypt (cost 10) en la base de datos |
| **REQ-16-07** | `CustomUserDetailsService` carga usuarios desde `UserRepository` |
| **REQ-16-08** | Migración Flyway agrega columna `password` (V5) |
| **REQ-16-09** | Migración Flyway seed de usuarios con hashes (V6) |
| **REQ-16-10** | API STATELESS — sin cookies ni sesiones HTTP |
| **REQ-16-11** | `PUT /tickets/by-id/{id}` permite a USER editar solo tickets propios, a AGENT solo tickets asignados y a ADMIN cualquier ticket |

---

## Estructura inicial vs final

```
Antes (Lección 15):
├── model/User.java                     (sin campo password; role es String)
├── config/DataInitializer.java         (seed sin contraseñas)
└── resources/db/migration/
    └── V4__Add_audit_tables.sql

Después (Lección 16):
├── model/User.java                     ← MODIFICADO: +password, role como enum
├── config/
│   ├── SecurityConfig.java             ← NUEVO
│   ├── CustomUserDetailsService.java   ← NUEVO
│   └── TicketSecurity.java             ← NUEVO: autorización por ticket específico
├── config/DataInitializer.java         ← MODIFICADO: +PasswordEncoder, +passwords, @Profile("h2")
└── resources/db/migration/
    ├── V5__lesson_16_add_password_to_users.sql   ← NUEVO
    └── V6__lesson_16_seed_users_with_auth.sql    ← NUEVO
```

---

## Estructura de la Lección

1. **[Este documento](01_objetivo_y_alcance.md)** — Objetivo y alcance
2. **[Guión Paso a Paso](02_guion_paso_a_paso.md)** ⭐ — Instrucciones prácticas
3. **[Autenticación vs Autorización](03_autenticacion_vs_autorizacion.md)** — Conceptos y arquitectura
4. **[Ejemplos Avanzados](04_ejemplos_practicos.md)** — Código adicional listo para usar
5. **[Cifrado de Contraseñas](05_password_encoding.md)** — BCrypt en detalle
6. **[Troubleshooting](06_troubleshooting.md)** — Errores frecuentes y soluciones
7. **[Checklist](07_checklist_rubrica_minima.md)** — Verificación antes de entregar
8. **[Actividad Individual](08_actividad_individual.md)** — Tu tarea
