# Lección 16 - Ejemplos prácticos

## Código completo: SecurityConfig

```java
package cl.duoc.fullstack.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("pass123"))
            .roles("ADMIN")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.GET, "/tickets", "/tickets/by-id/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories", "/categories/by-id/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tags", "/tags/by-id/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/users", "/users/by-id/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tickets/{id}/history").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/tickets").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tickets/by-id/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tickets/by-id/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/categories/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/categories/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/tags").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tags/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tags/by-id/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
```

## Roles y Permisos

| Rol    | Permisos                                                                 |
|--------|--------------------------------------------------------------------------|
| USER   | Crear, editar y eliminar tickets (CRUD completo)                       |
| ADMIN  | Todo lo de USER + gestionar categorías/tags + ver historial de tickets |

## Autenticación

La API usa **Basic Auth**. Codifica en Base64 el par `usuario:contraseña`:

| Usuario | Contraseña | Base64                    |
|---------|------------|---------------------------|
| admin   | pass123    | `YWRtaW46cGFzczEyMw==`    |
| user    | user123    | `dXNlcjp1c2VyMTIz`        |

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic dXNlcjp1c2VyMTIz" \
  -d '{"title": "Nuevo", "description": "test"}'
```

## Endpoints protegidos

### GET (públicos - sin autenticación)
```
GET    /tickets              → 200 OK
GET    /tickets/by-id/1     → 200 OK
GET    /categories          → 200 OK
GET    /categories/by-id/1  → 200 OK
GET    /tags                → 200 OK
GET    /tags/by-id/1        → 200 OK
GET    /users               → 200 OK
GET    /users/by-id/1       → 200 OK
```

### USER (puede gestionar tickets)
```
POST   /tickets              → USER: 201 | ADMIN: 201 | Sin auth: 401
PUT    /tickets/by-id/1     → USER: 200 | ADMIN: 200 | Sin auth: 401
DELETE /tickets/by-id/1     → USER: 204 | ADMIN: 204 | Sin auth: 401
```

### ADMIN (gestiona tickets + categorías + tags + historial)
```
POST   /categories          → ADMIN: 201 | USER: 403 | Sin auth: 401
PUT    /categories/by-id/1 → ADMIN: 200 | USER: 403 | Sin auth: 401
DELETE /categories/by-id/1 → ADMIN: 204 | USER: 403 | Sin auth: 401
POST   /tags                → ADMIN: 201 | USER: 403 | Sin auth: 401
PUT    /tags/by-id/1        → ADMIN: 200 | USER: 403 | Sin auth: 401
DELETE /tags/by-id/1        → ADMIN: 204 | USER: 403 | Sin auth: 401
GET    /tickets/1/history   → ADMIN: 200 | USER: 403 | Sin auth: 401
```

## Respuestas esperadas

**Sin autenticación:**
```
✅ GET    /tickets          → 200 OK (pública)
❌ POST   /tickets          → 401 Unauthorized
❌ PUT    /tickets/1        → 401 Unauthorized
❌ DELETE /tickets/1        → 401 Unauthorized
❌ POST   /categories       → 401 Unauthorized
```

**Usuario USER (user/user123):**
```
✅ GET    /tickets          → 200 OK (pública)
✅ POST   /tickets          → 201 Created
✅ PUT    /tickets/1        → 200 OK
✅ DELETE /tickets/1        → 204 No Content
❌ POST   /categories       → 403 Forbidden
❌ GET    /tickets/1/history → 403 Forbidden
```

**Usuario ADMIN (admin/pass123):**
```
✅ GET    /tickets          → 200 OK (pública)
✅ POST   /tickets          → 201 Created
✅ PUT    /tickets/1        → 200 OK
✅ DELETE /tickets/1        → 204 No Content
✅ POST   /categories       → 201 Created
✅ GET    /tickets/1/history → 200 OK
```