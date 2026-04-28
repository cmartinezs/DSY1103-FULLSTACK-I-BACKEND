# Lección 16 — Checklist y Rúbrica Mínima

## ✅ Checklist antes de entregar

### Dependencia y compilación
- [ ] `pom.xml` incluye `spring-boot-starter-security`
- [ ] El proyecto compila sin errores (`mvnw.cmd clean install`)

### Entidad y Base de Datos
- [ ] `User.java` tiene campo `password` anotado con `@Column(length = 255)` — nullable
- [ ] `User.java` tiene campo `role` como **enum** (`Role.USER`, `Role.AGENT`, `Role.ADMIN`) con `@Enumerated(EnumType.STRING)`
- [ ] `User.java` tiene campo `active` con valor por defecto `true`
- [ ] Migración V5 (`V5__lesson_16_add_password_to_users.sql`) — agrega columna `password` con `ALTER TABLE`
- [ ] Migración V6 (`V6__lesson_16_seed_users_with_auth.sql`) — inserta 3 usuarios con hashes BCrypt reales (no placeholders)
- [ ] `DataInitializer` tiene `@Profile("h2")` para no ejecutarse en MySQL/Supabase
- [ ] `DataInitializer` inyecta `PasswordEncoder` y llama a `passwordEncoder.encode()`

### Autenticación
- [ ] `CustomUserDetailsService.java` existe en el paquete `config/`
- [ ] Implementa `UserDetailsService` con `@Service`
- [ ] `loadUserByUsername` usa `UserRepository.findByEmail(email)`
- [ ] Filtra usuarios sin contraseña (`!= null && !isBlank()`)
- [ ] Construye roles con `.roles(user.getRole().name())` o authorities con `"ROLE_" + user.getRole().name()`
- [ ] Usa `.disabled(!user.isActive())` para bloquear usuarios inactivos
- [ ] `PasswordEncoder` declarado como `@Bean` y usando `BCryptPasswordEncoder`
- [ ] `httpBasic(Customizer.withDefaults())` está habilitado
- [ ] La autenticación funciona: POST con credenciales correctas → no es 401

### Autorización
- [ ] `SecurityConfig.java` existe en el paquete `config/`
- [ ] Tiene `@Configuration`, `@EnableWebSecurity`, `@EnableMethodSecurity`
- [ ] Tiene `SessionCreationPolicy.STATELESS`
- [ ] `csrf(csrf -> csrf.disable())` incluido
- [ ] GET `/tickets` y `/tickets/by-id/**` son `permitAll()`
- [ ] GET `/categories`, `/tags`, `/users` son `permitAll()`
- [ ] POST `/tickets` y PUT `/tickets/by-id/**` requieren `hasAnyRole("USER", "AGENT", "ADMIN")` como regla general
- [ ] DELETE `/tickets/by-id/**` requiere `hasRole("ADMIN")`
- [ ] Si se usa `@PreAuthorize`, `@EnableMethodSecurity` está presente
- [ ] Existe bean `TicketSecurity` con `@Component("ticketSecurity")`
- [ ] `PUT /tickets/by-id/{id}` usa `@PreAuthorize("@ticketSecurity.canEdit(#id, authentication)")`
- [ ] `TicketSecurity` permite a USER editar solo tickets donde `createdBy.email` coincide con `authentication.name`
- [ ] `TicketSecurity` permite a AGENT editar solo tickets donde `assignedTo.email` coincide con `authentication.name`
- [ ] `TicketSecurity` permite a ADMIN editar cualquier ticket

### Pruebas funcionales
- [ ] `GET /tickets` sin auth → `200 OK`
- [ ] `POST /tickets` sin auth → `401 Unauthorized`
- [ ] `POST /tickets` con USER → `201 Created`
- [ ] `POST /tickets` con AGENT → `201 Created`
- [ ] `PUT /tickets/by-id/{id}` con USER creador → `200 OK`
- [ ] `PUT /tickets/by-id/{id}` con USER no creador → `403 Forbidden`
- [ ] `PUT /tickets/by-id/{id}` con AGENT asignado → `200 OK`
- [ ] `PUT /tickets/by-id/{id}` con AGENT no asignado → `403 Forbidden`
- [ ] `DELETE /tickets/by-id/{id}` con USER → `403 Forbidden`
- [ ] `DELETE /tickets/by-id/{id}` con ADMIN → `204 No Content`
- [ ] Usuario `active=false` → `401 Unauthorized`

---

## 🎓 Rúbrica de Evaluación

### Entidad y Migración (20 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Campo `password` en `User.java` (`@Column`, nullable) | 5 | Campo presente con anotación correcta |
| Campo `role` como enum con tres valores | 3 | `Role.USER`, `Role.AGENT`, `Role.ADMIN` |
| Migración V5 agrega columna `password` | 5 | Archivo `V5__lesson_16_add_password_to_users.sql` con `ALTER TABLE` |
| Migración V6 inserta usuarios con hashes BCrypt reales | 7 | Hashes con formato `$2a$10$...`, no texto plano ni placeholders |

### Autenticación (25 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| `CustomUserDetailsService` implementa `UserDetailsService` | 5 | `implements UserDetailsService` + `@Service` |
| `loadUserByUsername` carga usuario desde `UserRepository` | 10 | Usa `userRepository.findByEmail(email)` |
| Rol usa `.name()` para el enum | 5 | `.roles(user.getRole().name())` o `"ROLE_" + user.getRole().name()` |
| Login funciona con credenciales de la BD | 5 | POST con auth correcta → no es 401 |

### Autorización (25 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| GET endpoints son públicos (`permitAll`) | 5 | GET sin auth → 200 OK |
| POST `/tickets` permite USER y AGENT | 5 | USER → 201; AGENT → 201 |
| PUT aplica propiedad/asignación | 8 | USER propio → 200; USER ajeno → 403; AGENT asignado → 200; AGENT no asignado → 403 |
| DELETE solo para ADMIN | 7 | USER → 403; AGENT → 403; ADMIN → 204 |
| `SessionCreationPolicy.STATELESS` configurado | 5 | Presente en `SecurityConfig` |

### Seguridad (15 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| `PasswordEncoder` es `BCryptPasswordEncoder` | 5 | Bean declarado en `SecurityConfig` |
| Contraseñas almacenadas como hash | 5 | BD y migraciones guardan hashes BCrypt, no texto plano |
| `csrf(csrf -> csrf.disable())` incluido | 5 | POST/PUT/DELETE no devuelven 403 por CSRF |

### Documentación (15 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| README o comentarios explican los roles y credenciales | 8 | Email, contraseña y rol de cada usuario documentados |
| Explica flujo de autenticación | 7 | Descripción: email → `CustomUserDetailsService` → BD → BCrypt |

---

## 🚩 Red Flags (Falla automática)

- ❌ Contraseñas almacenadas en texto plano en la BD o en migraciones SQL
- ❌ GET endpoints requieren autenticación (rompe la especificación)
- ❌ `CustomUserDetailsService` no existe (usa `InMemoryUserDetailsManager` u otro)
- ❌ El campo `password` no está en la entidad `User`
- ❌ `csrf(csrf -> csrf.disable())` ausente (POST/PUT/DELETE fallan con 403)
- ❌ El proyecto no compila
- ❌ `user.getRole()` sin `.name()` en `UserDetails` → error de compilación o roles incorrectos
- ❌ Migración V6 con hashes en texto plano o con placeholder `$2a$10$...hash...`
- ❌ USER puede editar tickets creados por otro usuario
- ❌ AGENT puede editar tickets que no tiene asignados

**Total: 100 puntos**
