# Lección 16 - Spring Security: ¿Qué vas a aprender?

## ¿De dónde venimos?

En Lección 15 implementaste comunicación entre microservicios. Tu API ahora coordina con otros servicios, pero sigue siendo públicamente accesible.

El problema: cualquiera puede crear, modificar o eliminar tickets. Un usuario malicioso podría sabotear todo tu sistema.

---

## ¿Qué vas a construir?

Al terminar esta lección, tu API tendrá:

1. **Usuarios registrados:** admin/contraseña1, user/contraseña2
2. **Endpoint de login:** POST `/login` que devuelve un identificador de sesión
3. **Endpoints protegidos:** POST, PUT, DELETE solo para ADMIN
4. **GET públicos:** cualquiera puede consultar (sin autenticarse)

### Flujo de usuario

```
1. Cliente intenta: POST /tickets (sin autenticarse)
   → Respuesta: 401 Unauthorized

2. Cliente hace: POST /login {"username":"admin", "password":"pass"}
   → Respuesta: 200 OK (sesión establecida)

3. Cliente intenta: POST /tickets (con sesión válida + ADMIN)
   → Respuesta: 201 Created ✅

4. Cliente intenta: DELETE /tickets/1 (sesión válida pero usuario es USER)
   → Respuesta: 403 Forbidden
```

---

## ¿Qué NO cubre esta lección? (y por qué)

| Tema | Razón |
|------|-------|
| JWT (JSON Web Tokens) | Requiere entiendas primero sesiones basadas en Spring Security |
| OAuth 2.0 | Autenticación externa; primero domina autenticación local |
| Refresh tokens | Introducido después: L16 sesiones, L16b tokens |
| Two-Factor Authentication | Nivel avanzado |
| LDAP / Active Directory | Integración empresarial, fuera de scope |

El foco: **sesiones en memoria + autenticación básica**.

---

## Requerimientos que implementamos

| ID | Requerimiento |
|----|---------------|
| **REQ-20** | POST `/login` valida usuario/contraseña |
| **REQ-21** | Endpoints POST/PUT/DELETE requieren ROLE_ADMIN |
| **REQ-22** | GET es público; no requiere autenticación |
| **REQ-23** | Sin autenticación → 401 Unauthorized |
| **REQ-24** | Con autenticación pero rol insuficiente → 403 Forbidden |

---

## Estructura inicial vs final

```
Antes:
├── controller/TicketController.java    (sin protección)
└── service/TicketService.java

Después:
├── config/SecurityConfig.java          ← NUEVO
├── controller/
│   ├── TicketController.java           (actualizado: @PreAuthorize)
│   └── AuthController.java             ← NUEVO (endpoint /login)
└── service/TicketService.java
```
