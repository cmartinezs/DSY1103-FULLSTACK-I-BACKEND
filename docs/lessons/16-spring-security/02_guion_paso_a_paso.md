# Lección 16 — Guion Paso a Paso: Spring Security con Base de Datos

Sigue esta guía en orden. El objetivo es proteger la API con HTTP Basic Auth, cargar usuarios desde la base de datos y autorizar endpoints por rol.

> **Idea central:** no crearás un endpoint `/login`. Spring Security intercepta cada petición, lee el header `Authorization: Basic ...`, autentica al usuario y luego decide si puede ejecutar el endpoint solicitado.

---

## Paso 1: Agregar la dependencia de Spring Security

En `pom.xml`, dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Ejecuta:

```bash
# Linux / macOS
./mvnw clean install

# Windows
mvnw.cmd clean install
```

---

## Paso 2: Observar el comportamiento por defecto

Después de agregar la dependencia y antes de crear `SecurityConfig`, Spring Boot protege todos los endpoints automáticamente.

Al arrancar la aplicación verás algo parecido a:

```text
Using generated security password: 3f2a1b4c-8d7e-4f3a-9c1b-2d6e8f0a4b5c
```

Si llamas a cualquier endpoint:

```http
GET http://localhost:8080/ticket-app/tickets
```

Resultado esperado en este momento:

```text
401 Unauthorized
```

Esto ocurre porque Spring Security encontró la dependencia y aplicó una configuración temporal. En los siguientes pasos crearás tu propia configuración.

---

## Paso 3: Preparar `User.java` para autenticación

La entidad `User` debe tener:

- `password`: hash BCrypt de la contraseña.
- `role`: enum con `USER`, `AGENT`, `ADMIN`.
- `active`: permite deshabilitar usuarios sin eliminarlos.

Código completo de `model/User.java`:

```java
package cl.duoc.fullstack.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es requerido")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email no tiene un formato válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 255)
    private String password;

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
}
```

Puntos clave:

- `@Enumerated(EnumType.STRING)` guarda `USER`, `AGENT` o `ADMIN` como texto, no como número.
- `password` guarda el hash BCrypt, no la contraseña en texto plano.
- Si `active=false`, el usuario existe, pero no debe poder autenticarse.

---

## Paso 4: Preparar la base de datos

### V5: agregar columna `password`

Archivo: `src/main/resources/db/migration/V5__lesson_16_add_password_to_users.sql`

```sql
ALTER TABLE users ADD COLUMN password VARCHAR(255);
```

> Si tu proyecto ya tiene más migraciones, usa el siguiente número disponible. Revisa `flyway_schema_history` si tienes dudas.

### V6: sembrar usuarios con hashes BCrypt

Archivo: `src/main/resources/db/migration/V6__lesson_16_seed_users_with_auth.sql`

```sql
INSERT INTO users (name, email, role, active, password) VALUES
  ('Administrador',  'admin@empresa.com',       'ADMIN', true, '$2a$10$gT.PsFi3xTq9xc3virQAfesYBesY5g53tQ5R7lgJGqgVdVMH0I8qa'),
  ('Ana Garcia',     'ana.garcia@empresa.com',  'USER',  true, '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m'),
  ('Carlos Lopez',   'carlos.lopez@empresa.com','AGENT', true, '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m');
```

Credenciales de prueba:

| Email | Contraseña | Rol |
|-------|------------|-----|
| `admin@empresa.com` | `pass123` | ADMIN |
| `ana.garcia@empresa.com` | `user123` | USER |
| `carlos.lopez@empresa.com` | `user123` | AGENT |

Nunca guardes contraseñas en texto plano en la base de datos. Spring Security comparará la contraseña enviada por el cliente contra estos hashes usando BCrypt.

### H2: actualizar `DataInitializer`

Si usas H2 con datos en memoria y Flyway está deshabilitado, `DataInitializer` debe insertar usuarios con `PasswordEncoder`.

Fragmento relevante:

```java
@Component
@Profile("h2")
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
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);

            User ana = new User();
            ana.setName("Ana Garcia");
            ana.setEmail("ana.garcia@empresa.com");
            ana.setPassword(passwordEncoder.encode("user123"));
            ana.setRole(Role.USER);
            ana.setActive(true);
            userRepository.save(ana);

            User carlos = new User();
            carlos.setName("Carlos Lopez");
            carlos.setEmail("carlos.lopez@empresa.com");
            carlos.setPassword(passwordEncoder.encode("user123"));
            carlos.setRole(Role.AGENT);
            carlos.setActive(true);
            userRepository.save(carlos);
        }
    }
}
```

Puntos clave:

- `@Profile("h2")` evita duplicar datos en MySQL/PostgreSQL/Supabase.
- `passwordEncoder.encode(...)` genera un hash BCrypt distinto en cada ejecución, y eso está bien.

---

## Paso 5: Completar `UserRepository`

Spring Security recibirá un username desde Basic Auth. En esta lección usaremos el email como username, por eso el repositorio debe poder buscar usuarios por email.

Código de `respository/UserRepository.java`:

```java
package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

Spring Data JPA implementa automáticamente `findByEmail` por convención de nombre.

---

## Paso 6: Crear el `PasswordEncoder`

Spring Security necesita un `PasswordEncoder` para comparar la contraseña plana enviada por el cliente contra el hash BCrypt guardado en la base de datos.

Este bean irá en `SecurityConfig`, pero conviene entenderlo antes:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Qué hace:

- `encode("user123")`: genera un hash BCrypt para guardar o sembrar usuarios.
- `matches("user123", hashBCrypt)`: valida una contraseña plana contra un hash.
- Spring Security llama internamente a `matches(...)` durante la autenticación.

No hagas esto:

```java
password.equals(user.getPassword())
```

Eso compara texto plano contra hash y siempre fallará. Además, no es seguro.

---

## Paso 7: Crear `CustomUserDetailsService`

`UserDetailsService` es el puente entre Spring Security y tu tabla `users`.

Cuando llega este header:

```text
Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
```

Spring Security lo decodifica como:

```text
ana.garcia@empresa.com:user123
```

Luego llama a:

```java
loadUserByUsername("ana.garcia@empresa.com")
```

Crea `config/CustomUserDetailsService.java`:

```java
package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .disabled(!user.isActive())
            .build();
    }
}
```

Qué hace cada parte:

```text
userRepository.findByEmail(email)
    Busca al usuario por email.

.filter(u -> u.getPassword() != null && !u.getPassword().isBlank())
    Rechaza usuarios que existen, pero no tienen contraseña configurada.

.password(user.getPassword())
    Entrega a Spring Security el hash BCrypt guardado en la BD.

.roles(user.getRole().name())
    Recibe USER, AGENT o ADMIN. Spring agrega el prefijo ROLE_ automáticamente.

.disabled(!user.isActive())
    Si active=false, la autenticación falla con 401.
```

> **Importante:** `.roles("ADMIN")` produce internamente `ROLE_ADMIN`. Por eso después puedes usar `.hasRole("ADMIN")` y `@PreAuthorize("hasRole('ADMIN')")`.

Alternativa válida, pero no la mezcles con `.roles(...)` en el código principal:

```java
.authorities("ROLE_" + user.getRole().name())
```

---

## Paso 8: Crear `SecurityConfig`

`SecurityConfig` define el `SecurityFilterChain`: la cadena de filtros que se ejecuta antes de los controladores.

Crea `config/SecurityConfig.java`:

```java
package cl.duoc.fullstack.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                .requestMatchers(HttpMethod.GET, "/categories", "/categories/by-id/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tags", "/tags/by-id/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/users", "/users/by-id/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tickets/{id}/history").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/tickets").hasAnyRole("USER", "AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tickets/by-id/**").hasAnyRole("USER", "AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tickets/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/categories/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/categories/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/tags").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tags/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tags/by-id/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasRole("ADMIN")
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

Explicación por bloque:

```text
@EnableMethodSecurity
    Habilita @PreAuthorize en controladores y servicios.

csrf(csrf -> csrf.disable())
    Deshabilita CSRF. Es correcto para esta API REST stateless sin cookies de sesión.

SessionCreationPolicy.STATELESS
    Spring no crea sesión. Cada request debe traer Authorization: Basic ...

authorizeHttpRequests(...)
    Define autorización por método HTTP y ruta.

httpBasic(Customizer.withDefaults())
    Activa BasicAuthenticationFilter, el filtro que lee Authorization: Basic ...

PasswordEncoder
    Define BCrypt como algoritmo de comparación de contraseñas.
```

> **Rutas sin context-path:** en `SecurityConfig` se escribe `/tickets`, no `/ticket-app/tickets`. El context path `/ticket-app` ya fue procesado antes.

---

## Paso 9: Entender el flujo interno de autenticación

Este es el recorrido completo cuando un cliente envía una petición protegida:

```text
Cliente
  Authorization: Basic base64(email:password)
        |
        v
BasicAuthenticationFilter
        |
        v
AuthenticationManager
        |
        v
DaoAuthenticationProvider
        |
        v
CustomUserDetailsService.loadUserByUsername(email)
        |
        v
PasswordEncoder.matches(passwordPlano, hashBCrypt)
        |
        v
SecurityContext: Authentication autenticado con ROLE_...
        |
        v
AuthorizationFilter / @PreAuthorize
        |
        v
Controlador
```

Responsabilidades:

- `BasicAuthenticationFilter`: lee y decodifica el header Basic.
- `CustomUserDetailsService`: carga usuario, hash y rol desde la base de datos.
- `PasswordEncoder`: valida la contraseña enviada contra el hash.
- `AuthorizationFilter`: revisa reglas de `SecurityConfig`.
- `@PreAuthorize`: revisa reglas declaradas sobre métodos.

Respuestas esperadas:

- Sin header, header inválido, usuario inexistente, contraseña incorrecta o usuario inactivo: `401 Unauthorized`.
- Usuario autenticado, pero sin rol suficiente: `403 Forbidden`.
- Usuario autenticado y autorizado: se ejecuta el controlador.

---

## Paso 10: Autorizar con `@PreAuthorize`

Las reglas de `SecurityConfig` sirven bien para permisos generales por ruta. `@PreAuthorize` es útil cuando quieres que la regla quede cerca del caso de uso o dependa de parámetros del método.

Ejemplo para eliminar tickets solo con ADMIN:

```java
import org.springframework.security.access.prepost.PreAuthorize;

@DeleteMapping("/by-id/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    ticketService.deleteById(id);
    return ResponseEntity.noContent().build();
}
```

Ejemplo para crear tickets con cualquier usuario autenticado de los roles permitidos:

```java
import org.springframework.security.access.prepost.PreAuthorize;

@PostMapping
@PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.create(request));
}
```

Regla práctica:

- Usa `SecurityConfig` para reglas simples y globales por método HTTP y ruta.
- Usa `@PreAuthorize` para reglas específicas del caso de uso.
- No dupliques reglas contradictorias. Si `SecurityConfig` bloquea antes, el método anotado nunca se ejecuta.

Expresiones frecuentes:

| Expresión | Significado |
|-----------|-------------|
| `hasRole('ADMIN')` | Requiere `ROLE_ADMIN` |
| `hasAnyRole('USER', 'AGENT', 'ADMIN')` | Requiere cualquiera de esos roles |
| `isAuthenticated()` | Requiere cualquier usuario autenticado |
| `authentication.name == 'admin@empresa.com'` | Requiere un usuario específico |

---

## Paso 11: Autorizar edición según el ticket específico

La regla para editar tickets no depende solo del rol. También depende del ticket específico:

- `USER`: puede editar solo tickets que él creó (`ticket.createdBy.email == authentication.name`).
- `AGENT`: puede editar solo tickets que tiene asignados (`ticket.assignedTo.email == authentication.name`).
- `ADMIN`: puede editar cualquier ticket.

Esta regla no se puede expresar correctamente solo con `hasRole(...)`, porque `hasRole(...)` no sabe quién creó el ticket ni a quién está asignado. Para resolverlo, delegaremos la decisión a un bean de seguridad.

### Crear `TicketSecurity`

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

Qué hace:

```text
@Component("ticketSecurity")
    Registra el bean con ese nombre para poder llamarlo desde @PreAuthorize.

canEdit(Long ticketId, Authentication authentication)
    Recibe el id del ticket y el usuario autenticado.

authentication.getName()
    Devuelve el username autenticado. En esta lección es el email.

ticketRepository.findById(ticketId)
    Carga el ticket para revisar createdBy y assignedTo.

ROLE_ADMIN
    Puede editar sin revisar propietario ni asignación.

ROLE_USER
    Solo puede editar si ticket.createdBy.email coincide con authentication.name.

ROLE_AGENT
    Solo puede editar si ticket.assignedTo.email coincide con authentication.name.
```

### Aplicar la regla en el controlador

Actualiza el método `PUT /tickets/by-id/{id}` en `TicketController`:

```java
import org.springframework.security.access.prepost.PreAuthorize;

@PutMapping("/by-id/{id}")
@PreAuthorize("@ticketSecurity.canEdit(#id, authentication)")
public ResponseEntity<Object> updateTicketById(
    @PathVariable Long id,
    @Valid @RequestBody TicketRequest request) {
    try {
        Optional<TicketResult> updated = this.service.updateById(id, request);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        }
        return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
}
```

La expresión SpEL se lee así:

```text
@ticketSecurity
    Bean llamado ticketSecurity.

canEdit(#id, authentication)
    Ejecuta el método canEdit usando el @PathVariable id y el usuario autenticado.

#id
    Parámetro id del método updateTicketById.

authentication
    Objeto Authentication actual creado por Spring Security después de validar Basic Auth.
```

### Mantener la regla general en `SecurityConfig`

Deja esta regla en `SecurityConfig`:

```java
.requestMatchers(HttpMethod.PUT, "/tickets/by-id/**").hasAnyRole("USER", "AGENT", "ADMIN")
```

Esto produce dos niveles de autorización:

| Nivel | Pregunta | Ejemplo |
|-------|----------|---------|
| `SecurityConfig` | ¿Este rol puede intentar editar tickets? | USER, AGENT y ADMIN sí |
| `@PreAuthorize` | ¿Este usuario puede editar este ticket específico? | USER solo si lo creó; AGENT solo si lo tiene asignado |

---

## Paso 12: Tabla de autorización esperada

| Endpoint | Sin auth | USER | AGENT | ADMIN |
|----------|----------|------|-------|-------|
| `GET /tickets` | 200 | 200 | 200 | 200 |
| `GET /tickets/by-id/{id}` | 200 | 200 | 200 | 200 |
| `GET /tickets/{id}/history` | 401 | 403 | 403 | 200 |
| `POST /tickets` | 401 | 201 | 201 | 201 |
| `PUT /tickets/by-id/{id}` propio/permitido | 401 | 200 | 200 | 200 |
| `PUT /tickets/by-id/{id}` ajeno/no asignado | 401 | 403 | 403 | 200 |
| `DELETE /tickets/by-id/{id}` | 401 | 403 | 403 | 204 |
| `GET /users` | 200 | 200 | 200 | 200 |
| `POST /users` | 401 | 403 | 403 | 200/201 |

> Los códigos `200`, `201` y `204` pueden variar si falla una validación de negocio o si el recurso no existe. Lo importante para seguridad es distinguir `401` de `403`.

---

## Paso 13: Probar con Postman, Thunder Client o curl

### Generar Base64 en PowerShell

```powershell
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("ana.garcia@empresa.com:user123"))
```

Resultado esperado:

```text
YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz
```

### Generar Base64 en Linux / macOS

```bash
echo -n "ana.garcia@empresa.com:user123" | base64
```

### Credenciales disponibles

| Email | Contraseña | Rol | Base64 |
|-------|------------|-----|--------|
| `admin@empresa.com` | `pass123` | ADMIN | `YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw==` |
| `ana.garcia@empresa.com` | `user123` | USER | `YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz` |
| `carlos.lopez@empresa.com` | `user123` | AGENT | `Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=` |

### Casos mínimos

```bash
# 1. GET público: debe devolver 200
curl -i http://localhost:8080/ticket-app/tickets

# 2. POST sin autenticación: debe devolver 401
curl -i -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","description":"test"}'

# 3. POST con password incorrecta: debe devolver 401
curl -i -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTptYWxh" \
  -d '{"title":"Test","description":"test"}'

# 4. POST con USER válido: debe devolver 201 si el payload es válido
curl -i -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz" \
  -d '{"title":"Nuevo ticket","description":"descripcion"}'

# 5. DELETE con USER válido: debe devolver 403
curl -i -X DELETE http://localhost:8080/ticket-app/tickets/by-id/1 \
  -H "Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz"

# 6. DELETE con ADMIN válido: debe devolver 204 si el ticket existe
curl -i -X DELETE http://localhost:8080/ticket-app/tickets/by-id/1 \
  -H "Authorization: Basic YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw=="
```

### Casos de edición por propietario/asignado

Usa tickets existentes donde conozcas `createdBy.email` y `assignedTo.email`.

```bash
# USER edita un ticket creado por él: debe devolver 200 si el payload es válido
curl -i -X PUT http://localhost:8080/ticket-app/tickets/by-id/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz" \
  -d '{"title":"Ticket actualizado","description":"descripcion","status":"OPEN"}'

# USER edita un ticket creado por otro usuario: debe devolver 403
curl -i -X PUT http://localhost:8080/ticket-app/tickets/by-id/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz" \
  -d '{"title":"Intento no permitido","description":"descripcion","status":"OPEN"}'

# AGENT edita un ticket asignado a él: debe devolver 200 si el payload es válido
curl -i -X PUT http://localhost:8080/ticket-app/tickets/by-id/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=" \
  -d '{"title":"Ticket gestionado","description":"descripcion","status":"IN_PROGRESS"}'

# AGENT edita un ticket no asignado a él: debe devolver 403
curl -i -X PUT http://localhost:8080/ticket-app/tickets/by-id/4 \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic Y2FybG9zLmxvcGV6QGVtcHJlc2EuY29tOnVzZXIxMjM=" \
  -d '{"title":"Intento no permitido","description":"descripcion","status":"IN_PROGRESS"}'
```

### Caso opcional: usuario inactivo

En la base de datos:

```sql
UPDATE users SET active = false WHERE email = 'ana.garcia@empresa.com';
```

Luego repite el POST con Ana. Debe responder `401 Unauthorized`.

---

## Resumen de pasos

```text
1. Agregar spring-boot-starter-security
        ↓
2. Observar la seguridad por defecto
        ↓
3. Preparar User: password, role, active
        ↓
4. Crear migraciones o actualizar DataInitializer
        ↓
5. Agregar UserRepository.findByEmail
        ↓
6. Declarar PasswordEncoder BCrypt
        ↓
7. Crear CustomUserDetailsService
        ↓
8. Crear SecurityConfig con httpBasic y STATELESS
        ↓
9. Entender BasicAuthenticationFilter y el flujo interno
        ↓
10. Aplicar @PreAuthorize donde corresponda
        ↓
11. Crear TicketSecurity para edición por propietario/asignado
        ↓
12. Probar 200, 201, 204, 401 y 403
```

---

*[← Volver a Objetivo](01_objetivo_y_alcance.md) | [Conceptos: Autenticación vs Autorización →](03_autenticacion_vs_autorizacion.md)*
