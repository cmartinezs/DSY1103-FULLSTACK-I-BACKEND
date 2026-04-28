# Lección 16 — Spring Security: Autenticación y Autorización

**Protege tu API REST con autenticación basada en base de datos y autorización por roles. Los usuarios se cargan desde MySQL/H2, las contraseñas se almacenan con BCrypt, y cada endpoint define quién puede acceder.**

---

## 📚 Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. Guión Paso a Paso** ⭐ | 25 min | Instrucciones prácticas |
| **03. Autenticación vs Autorización** | 10 min | Conceptos clave y arquitectura |
| **04. Ejemplos Avanzados** | 15 min | Código adicional listo para usar |
| **05. Cifrado de Contraseñas** | 10 min | BCrypt en detalle |
| **06. Troubleshooting** | 10 min | Errores frecuentes y soluciones |
| **07. Checklist** | 5 min | Verificación antes de entregar |
| **08. Actividad Individual** | — | Tu tarea |

---

## 🎯 Quick Start

### Concepto

- **Autenticación:** Verificar identidad (¿quién eres?) → 401 si falla
- **Autorización:** Verificar permisos (¿qué puedes hacer?) → 403 si falla

### Flujo con HTTP Basic Auth

```
Cliente → GET /tickets
        ← 200 OK (endpoint público, sin autenticación)

Cliente → POST /tickets (sin header Authorization)
        ← 401 Unauthorized

Cliente → POST /tickets
          Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
        ← 201 Created  (USER puede crear)

Cliente → DELETE /tickets/by-id/1
          Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
        ← 403 Forbidden  (USER no puede eliminar)

Cliente → DELETE /tickets/by-id/1
          Authorization: Basic YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw==
        ← 204 No Content  (ADMIN puede eliminar)
```

> **HTTP Basic Auth:** Las credenciales viajan codificadas en Base64 en el header `Authorization`. No hay endpoint `/login`; Spring Security intercepta cada petición automáticamente.

---

## Lo que construirás

1. Agregar `spring-boot-starter-security` al `pom.xml`
2. Actualizar `User.java`: campo `password` + enum `Role` (USER / AGENT / ADMIN)
3. Crear migración Flyway `V5` — agrega columna `password` a la tabla `users`
4. Crear migración Flyway `V6` — seed de 3 usuarios con hashes BCrypt
5. Completar `UserRepository.findByEmail` — el email será el username de Basic Auth
6. Crear `PasswordEncoder` — BCrypt para validar contraseñas hasheadas
7. Crear `CustomUserDetailsService` — carga usuarios desde la BD y expone roles
8. Crear `SecurityConfig` — HTTP Basic, sesión STATELESS y reglas por rol
9. Aplicar `@PreAuthorize` cuando la autorización pertenezca al caso de uso
10. Crear `TicketSecurity` — USER edita solo tickets propios; AGENT solo tickets asignados; ADMIN cualquiera
11. Testear: sin auth → 401; rol insuficiente → 403; credenciales correctas → 200/201/204

---

## Credenciales de prueba

| Email | Contraseña | Rol | Base64 para Basic Auth |
|-------|-----------|-----|------------------------|
| admin@empresa.com | pass123 | ADMIN | `YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw==` |
| ana.garcia@empresa.com | user123 | USER | `YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz` |
| carlos.lopez@empresa.com | user123 | AGENT | `Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=` |

---

## 🚀 Sigue el Guión

Comienza con **[02. Guión Paso a Paso](02_guion_paso_a_paso.md)** para instrucciones detalladas.

---

*Lección 16 de 18 — [← Volver a Lecciones](../)*
