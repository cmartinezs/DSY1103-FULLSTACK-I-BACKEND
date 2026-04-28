# Lección 16 — Ejemplos Avanzados

Esta sección contiene código adicional que extiende la implementación base. No es obligatorio para la actividad, pero es útil para entender el potencial de Spring Security.

---

## 1. Obtener el usuario autenticado en un controlador

Muchas veces necesitas saber **quién está haciendo la petición** dentro del método del controlador. Puedes inyectar el `Principal` o el `Authentication` directamente:

```java
import org.springframework.security.core.Authentication;

@GetMapping("/auth/me")
public ResponseEntity<Map<String, String>> whoAmI(Authentication authentication) {
    Map<String, String> info = new HashMap<>();
    info.put("email", authentication.getName());
    info.put("role", authentication.getAuthorities().iterator().next().getAuthority());
    return ResponseEntity.ok(info);
}
```

**Ejemplo de respuesta:**
```json
{
    "email": "ana.garcia@empresa.com",
    "role": "ROLE_USER"
}
```

También puedes obtener el usuario completo de la BD inyectando `UserRepository`:

```java
@GetMapping("/auth/me")
public ResponseEntity<User> whoAmI(Authentication authentication) {
    String email = authentication.getName();
    return userRepository.findByEmail(email)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

> **¿Cuándo usar esto?** Cuando un usuario solo debe ver sus propios tickets, o cuando quieres registrar quién realizó una acción.

---

## 2. Autorización con `@PreAuthorize` en el controlador

En lugar de (o además de) las reglas en `SecurityConfig`, puedes anotar métodos individuales del controlador:

```java
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    // Solo ADMIN puede ver el historial
    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketHistory>> getHistory(@PathVariable Long id) {
        // ...
    }

    // USER, AGENT o ADMIN pueden crear tickets
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<String> create(@Valid @RequestBody TicketRequest dto) {
        // ...
    }
}
```

**Expresiones SpEL disponibles en `@PreAuthorize`:**

| Expresión | Descripción |
|-----------|-------------|
| `hasRole('ADMIN')` | Tiene el rol ADMIN |
| `hasAnyRole('USER', 'AGENT')` | Tiene cualquiera de los roles |
| `isAuthenticated()` | Está autenticado (cualquier rol) |
| `isAnonymous()` | No está autenticado |
| `authentication.name == 'admin@empresa.com'` | Es exactamente este usuario |
| `#id == authentication.principal.id` | El parámetro `id` coincide con el id del usuario autenticado |

> **`SecurityConfig` vs `@PreAuthorize`:** Para proyectos simples, las reglas en `SecurityConfig` son suficientes. `@PreAuthorize` es mejor cuando las condiciones dependen de los datos de la petición (ej: solo el creador puede editar su propio ticket).

---

## 3. Autorización por recurso con un bean de seguridad

Cuando la autorización depende del ticket específico, delega la decisión a un bean llamado desde `@PreAuthorize`.

Regla de ejemplo:

- `USER`: solo edita tickets creados por él.
- `AGENT`: solo edita tickets asignados a él.
- `ADMIN`: edita cualquier ticket.

```java
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

Uso en el controlador:

```java
@PutMapping("/by-id/{id}")
@PreAuthorize("@ticketSecurity.canEdit(#id, authentication)")
public ResponseEntity<Object> updateTicketById(
    @PathVariable Long id,
    @Valid @RequestBody TicketRequest request) {
    // ...
}
```

> **Por qué usar un bean:** `hasRole('USER')` solo sabe el rol. `TicketSecurity` puede consultar el ticket y comparar `createdBy.email` o `assignedTo.email` con `authentication.name`.

---

## 4. Alternativa: usar `authorities(...)` en lugar de `roles(...)`

La implementación base del guion usa `.roles(user.getRole().name())`, que agrega automáticamente el prefijo `ROLE_`. También puedes construir la authority completa manualmente:

```java
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .filter(u -> u.getPassword() != null && !u.getPassword().isBlank())
        .orElseThrow(() -> new UsernameNotFoundException(
            "Usuario no encontrado o sin contraseña: " + email));

    GrantedAuthority authority = () -> "ROLE_" + user.getRole().name();

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities(authority)
        .disabled(!user.isActive())
        .build();
}
```

Si usas esta alternativa, recuerda que `hasRole("ADMIN")` busca `ROLE_ADMIN`. Por eso la authority debe incluir el prefijo `ROLE_`.

---

## 5. Configurar CORS para desarrollo con frontend

Si tienes un frontend (React, Angular, etc.) corriendo en `localhost:3000`, necesitas habilitar CORS en `SecurityConfig`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ← Agrega esto
        .csrf(csrf -> csrf.disable())
        // ... resto de la configuración igual
        ;
    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

**Imports necesarios:**
```java
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
```

---

## 6. Generar hashes BCrypt manualmente

Si necesitas generar nuevos hashes para las migraciones, puedes hacerlo con una clase main temporal:

```java
// Crea este archivo temporalmente, ejecuta, copia los hashes y luego bórralo
public class GenerateHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("user456:  " + encoder.encode("user456"));
    }
}
```

**Salida de ejemplo:**
```
admin123: $2a$10$5Cx.xR2y7...Qz3KaB1mP
user456:  $2a$10$9Dw.qR4z8...Rp4LcC2nQ
```

Copia esos hashes directamente en el SQL de la migración.

> **Importante:** Cada ejecución produce hashes diferentes (por el salt aleatorio), pero todos son válidos. Elige uno y ponlo en la migración.

---

## 7. Probar la seguridad con `curl`

```bash
# Test 1: GET público (debe funcionar sin auth)
curl -i http://localhost:8080/ticket-app/tickets

# Test 2: POST sin auth (debe devolver 401)
curl -i -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","description":"test"}'

# Test 3: POST con credenciales USER
curl -i -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz" \
  -d '{"title":"Nuevo ticket","description":"descripcion"}'

# Test 4: DELETE con ADMIN
curl -i -X DELETE http://localhost:8080/ticket-app/tickets/by-id/1 \
  -H "Authorization: Basic YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw=="

# Generar el Base64 de cualquier credencial
echo -n "mi.email@empresa.com:micontraseña" | base64
```
