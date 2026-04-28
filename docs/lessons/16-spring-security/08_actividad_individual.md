# Lección 16 — Actividad Individual

## Objetivo

Proteger tu API REST de Tickets con autenticación basada en base de datos, autorización por roles y autorización por datos del ticket, usando Spring Security y Flyway.

> **Conexión con Lecciones 10–15:** En lecciones anteriores creaste la entidad `User` con JPA y migraciones Flyway. En esta actividad extenderás esa entidad para soportar autenticación real: los usuarios se cargarán desde la base de datos, con contraseñas guardadas como hashes BCrypt.

---

## Tabla de permisos esperada

| Endpoint | Método | Sin auth | USER | AGENT | ADMIN |
|----------|--------|:--------:|:----:|:-----:|:-----:|
| `/tickets` | GET | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `/tickets/by-id/{id}` | GET | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `/tickets` | POST | ❌ 401 | ✅ 201 | ✅ 201 | ✅ 201 |
| `/tickets/by-id/{id}` propio/asignado | PUT | ❌ 401 | ✅ 200 | ✅ 200 | ✅ 200 |
| `/tickets/by-id/{id}` ajeno/no asignado | PUT | ❌ 401 | ❌ 403 | ❌ 403 | ✅ 200 |
| `/tickets/by-id/{id}` | DELETE | ❌ 401 | ❌ 403 | ❌ 403 | ✅ 204 |

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

### Paso 2: Actualizar `User.java`

La entidad debe tener el campo `password`, el rol como enum y el campo `active`:

```java
@Column(length = 255)
private String password;   // BCrypt hash; nullable para usuarios sin acceso al sistema

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private Role role = Role.USER;

@Column(nullable = false)
private boolean active = true;

public enum Role {
    USER,
    AGENT,
    ADMIN
}
```

> Si tu entidad ya tiene `role` como `String`, migra a enum: es más seguro y evita valores inválidos.

### Paso 3: Crear migración para la columna `password`

Archivo: `V{n}__lesson_16_add_password_to_users.sql`

```sql
-- Agrega columna password a la tabla users para Spring Security
ALTER TABLE users ADD COLUMN password VARCHAR(255);
```

Donde `{n}` es el número que sigue a tu última migración existente.

### Paso 4: Crear migración seed con hashes BCrypt

Archivo: `V{n+1}__lesson_16_seed_users_with_auth.sql`

Los hashes a continuación corresponden a las contraseñas `pass123` y `user123`. Puedes generar los tuyos con:

```java
// Ejecuta esto una vez para generar tus propios hashes:
System.out.println(new BCryptPasswordEncoder(10).encode("tu_contraseña"));
```

```sql
-- Seed de usuarios con contraseñas BCrypt (cost factor 10)
-- admin@empresa.com  → pass123
-- ana.garcia@empresa.com → user123
-- carlos.lopez@empresa.com → user123
INSERT INTO users (name, email, role, active, password) VALUES
  ('Administrador',  'admin@empresa.com',         'ADMIN', true,
   '$2a$10$gT.PsFi3xTq9xc3virQAfesYBesY5g53tQ5R7lgJGqgVdVMH0I8qa'),
  ('Ana Garcia',     'ana.garcia@empresa.com',     'USER',  true,
   '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m'),
  ('Carlos Lopez',   'carlos.lopez@empresa.com',   'AGENT', true,
   '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m');
```

> **No uses placeholders.** Los hashes deben ser valores BCrypt reales. Puedes usar los de arriba (que ya están verificados) o generar los tuyos propios.

### Paso 5: Actualizar `DataInitializer` para H2

Agrega `@Profile("h2")` e inyecta `PasswordEncoder`:

```java
@Component
@Profile("h2")   // Solo corre con H2; MySQL/Supabase usan la migración Flyway
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@empresa.com");
            admin.setPassword(passwordEncoder.encode("pass123"));
            admin.setRole(User.Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);

            User ana = new User();
            ana.setName("Ana Garcia");
            ana.setEmail("ana.garcia@empresa.com");
            ana.setPassword(passwordEncoder.encode("user123"));
            ana.setRole(User.Role.USER);
            ana.setActive(true);
            userRepository.save(ana);

            User carlos = new User();
            carlos.setName("Carlos Lopez");
            carlos.setEmail("carlos.lopez@empresa.com");
            carlos.setPassword(passwordEncoder.encode("user123"));
            carlos.setRole(User.Role.AGENT);
            carlos.setActive(true);
            userRepository.save(carlos);
        }
    }
}
```

### Paso 6: Crear `CustomUserDetailsService`

Archivo: `src/main/java/.../config/CustomUserDetailsService.java`

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .filter(u -> u.getPassword() != null && !u.getPassword().isBlank())
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado o sin contraseña: " + email));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .disabled(!user.isActive())
            .build();
    }
}
```

> **Recuerda:** `user.getRole()` devuelve el enum. Llama a `.name()` para obtener `USER`, `AGENT` o `ADMIN`. El builder `.roles(...)` agrega internamente el prefijo `ROLE_`.

### Paso 7: Crear `SecurityConfig`

Archivo: `src/main/java/.../config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/tickets", "/tickets/by-id/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/tickets").hasAnyRole("USER", "AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tickets/by-id/**").hasAnyRole("USER", "AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tickets/by-id/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

> `CustomUserDetailsService` está anotado con `@Service`, así que Spring lo detecta automáticamente. No necesitas declararlo como bean aquí.

### Paso 8: Restringir edición por propietario/asignado

`PUT /tickets/by-id/{id}` debe aplicar una regla más fina que solo rol:

- `USER` solo edita tickets donde `createdBy.email` coincide con el usuario autenticado.
- `AGENT` solo edita tickets donde `assignedTo.email` coincide con el usuario autenticado.
- `ADMIN` edita cualquier ticket.

Crea `config/TicketSecurity.java`:

```java
package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("ticketSecurity")
public class TicketSecurity {

    private final TicketRepository ticketRepository;

    public TicketSecurity(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public boolean canEdit(Long ticketId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        String email = authentication.getName();

        return ticketRepository.findById(ticketId)
            .map(ticket -> canEditTicket(ticket, email, authentication))
            .orElse(false);
    }

    private boolean canEditTicket(Ticket ticket, String email, Authentication authentication) {
        if (hasRole(authentication, "ROLE_USER")) {
            return ticket.getCreatedBy() != null
                && email.equals(ticket.getCreatedBy().getEmail());
        }

        if (hasRole(authentication, "ROLE_AGENT")) {
            return ticket.getAssignedTo() != null
                && email.equals(ticket.getAssignedTo().getEmail());
        }

        return false;
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
```

Anota el método de actualización en `TicketController`:

```java
@PutMapping("/by-id/{id}")
@PreAuthorize("@ticketSecurity.canEdit(#id, authentication)")
public ResponseEntity<Object> updateTicketById(
    @PathVariable Long id,
    @Valid @RequestBody TicketRequest request) {
    // implementación existente
}
```

### Paso 9: Testear con Postman/Thunder Client

Usa el modo **Basic Auth** de Postman (pestaña Authorization → Type: Basic Auth) o el header manual:

| Email | Contraseña | Rol | Base64 |
|-------|-----------|-----|--------|
| admin@empresa.com | pass123 | ADMIN | `YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw==` |
| ana.garcia@empresa.com | user123 | USER | `YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz` |
| carlos.lopez@empresa.com | user123 | AGENT | `Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=` |

#### Test 1: GET sin auth → debe ser 200
```
GET http://localhost:8080/ticket-app/tickets
```

#### Test 2: POST sin auth → debe ser 401
```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "Test", "description": "test" }
```

#### Test 3: POST con USER → debe ser 201
```
POST http://localhost:8080/ticket-app/tickets
Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
Content-Type: application/json

{ "title": "Test User", "description": "test" }
```

#### Test 4: POST con AGENT → debe ser 201
```
POST http://localhost:8080/ticket-app/tickets
Authorization: Basic Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=
Content-Type: application/json

{ "title": "Test Agent", "description": "test" }
```

#### Test 5: DELETE con USER → debe ser 403
```
DELETE http://localhost:8080/ticket-app/tickets/by-id/1
Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
```

#### Test 6: DELETE con ADMIN → debe ser 204
```
DELETE http://localhost:8080/ticket-app/tickets/by-id/1
Authorization: Basic YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw==
```

#### Test 7: PUT con USER creador → debe ser 200
```
PUT http://localhost:8080/ticket-app/tickets/by-id/{ticketCreadoPorAna}
Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
Content-Type: application/json

{ "title": "Editado por creador", "description": "test", "status": "OPEN" }
```

#### Test 8: PUT con USER no creador → debe ser 403
```
PUT http://localhost:8080/ticket-app/tickets/by-id/{ticketCreadoPorOtroUsuario}
Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
Content-Type: application/json

{ "title": "Intento ajeno", "description": "test", "status": "OPEN" }
```

#### Test 9: PUT con AGENT asignado → debe ser 200
```
PUT http://localhost:8080/ticket-app/tickets/by-id/{ticketAsignadoACarlos}
Authorization: Basic Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=
Content-Type: application/json

{ "title": "Editado por agente", "description": "test", "status": "IN_PROGRESS" }
```

#### Test 10: PUT con AGENT no asignado → debe ser 403
```
PUT http://localhost:8080/ticket-app/tickets/by-id/{ticketNoAsignadoACarlos}
Authorization: Basic Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=
Content-Type: application/json

{ "title": "Intento no asignado", "description": "test", "status": "IN_PROGRESS" }
```

---

## Desafío extra

1. **Endpoint de perfil:** `GET /auth/me` que devuelva el email y rol del usuario autenticado
2. **Respetar `active`:** Modifica `CustomUserDetailsService` para rechazar usuarios con `active = false` (usa `.disabled(!user.isActive())`)
3. **Agregar CORS:** Configura CORS en `SecurityConfig` para permitir peticiones desde `localhost:3000`
4. **Rol VIEWER:** Agrega un cuarto rol `VIEWER` que solo pueda hacer GET, ni POST ni PUT

---

## Checklist de entrega

- [ ] `User.java` tiene campo `password` (nullable) y `role` como enum con AGENT
- [ ] Migración Flyway agrega columna `password` a tabla `users`
- [ ] Migración Flyway seed inserta 3 usuarios con hashes BCrypt **reales**
- [ ] `DataInitializer` tiene `@Profile("h2")` e inyecta `PasswordEncoder`
- [ ] `CustomUserDetailsService` carga usuarios desde `UserRepository`
- [ ] `loadUserByUsername` usa `.name()` para convertir el enum a `String`
- [ ] `SecurityConfig` tiene `STATELESS`, `csrf.disable()` y `@EnableMethodSecurity`
- [ ] `PasswordEncoder` es `BCryptPasswordEncoder` declarado como `@Bean`
- [ ] GET `/tickets` y GET `/tickets/by-id/{id}` son públicos
- [ ] POST `/tickets` permite USER, AGENT y ADMIN
- [ ] PUT `/tickets/by-id/{id}` usa `@PreAuthorize("@ticketSecurity.canEdit(#id, authentication)")`
- [ ] USER solo puede editar tickets creados por él
- [ ] AGENT solo puede editar tickets asignados a él
- [ ] ADMIN puede editar cualquier ticket
- [ ] DELETE `/tickets/by-id/{id}` requiere ADMIN
- [ ] Los 10 tests pasan correctamente
