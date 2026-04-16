# Lección 16 - Actividad individual

## Objetivo

Proteger tu API REST de Tickets con autenticación y autorización usando Spring Security.

---

## Requisitos mínimos

1. **Crear SecurityConfig**
   - Definir 2 usuarios: "admin" (ROLE_ADMIN) y "user" (ROLE_USER)
   - Configurar BCryptPasswordEncoder
   - Proteger endpoints según tabla

2. **Proteger endpoints**

| Endpoint | Método | Quien accede | Rol requerido |
|----------|--------|--------------|---------------|
| `/tickets` | GET | Todos | Público |
| `/tickets/by-id/{id}` | GET | Todos | Público |
| `/tickets` | POST | Admin | ADMIN |
| `/tickets/by-id/{id}` | PUT | Admin | ADMIN |
| `/tickets/by-id/{id}` | DELETE | Admin | ADMIN |

3. **Testear**
   - GET sin auth → 200 OK
   - POST sin auth → 401 Unauthorized
   - POST con user → 403 Forbidden
   - POST con admin → 201 Created

---

## Instrucciones paso a paso

### Paso 1: Agregar dependencia

En `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Paso 2: Crear SecurityConfig

Archivo: `src/main/java/cl/duoc/fullstack/tickets/config/SecurityConfig.java`

Usa la plantilla del documento `04_ejemplos_practicos.md`.

### Paso 3: Proteger endpoints

En TicketController, agrega `@PreAuthorize` a POST, PUT, DELETE.

### Paso 4: Testear con Postman/Thunder Client

#### Test 1: GET sin autenticación
```
GET http://localhost:8080/ticket-app/tickets
```
Esperado: `200 OK`

#### Test 2: POST sin autenticación
```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "Test", "description": "test" }
```
Esperado: `401 Unauthorized`

#### Test 3: POST con USER
```
POST http://localhost:8080/ticket-app/tickets
Authorization: Basic dXNlcjp1c2VyMTIz
Content-Type: application/json

{ "title": "Test", "description": "test", "createdBy": "user", "assignedTo": "admin" }
```
Esperado: `403 Forbidden`

#### Test 4: POST con ADMIN
```
POST http://localhost:8080/ticket-app/tickets
Authorization: Basic YWRtaW46cGFzczEyMw==
Content-Type: application/json

{ "title": "Test", "description": "test", "createdBy": "admin", "assignedTo": "user" }
```
Esperado: `201 Created`

---

## Desafío extra

1. **Agregar endpoint de perfil:** `GET /auth/me` que devuelva datos del usuario autenticado
2. **Agregar roles adicionales:** MANAGER que puede consultar y actualizar, pero no eliminar
3. **Implementar logout:** Invalidar sesión actual

---

## Checklist de entrega

- [ ] SecurityConfig compilable y sin errores
- [ ] 2 usuarios configurados (admin, user)
- [ ] Contraseñas hasheadas con BCrypt
- [ ] GET es público
- [ ] POST/PUT/DELETE requieren ADMIN
- [ ] Todas las pruebas pasan
- [ ] Código comentado en puntos clave
