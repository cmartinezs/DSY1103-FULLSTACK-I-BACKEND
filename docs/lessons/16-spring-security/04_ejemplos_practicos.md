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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("pass123"))
            .roles("ADMIN", "USER")
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
                .requestMatchers(HttpMethod.POST, "/tickets").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tickets/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tickets/by-id/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            .csrf().disable()
            .cors().and();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8081"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

## Código: Usar en TicketController

```java
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> create(@Valid @RequestBody Ticket ticket) {
    try {
        Ticket created = this.service.create(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }
}

@PutMapping("/by-id/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody Ticket ticket) {
    try {
        Ticket updated = this.service.updateById(id, ticket);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getMessage()));
    }
}

@DeleteMapping("/by-id/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> deleteTicketById(@PathVariable Long id) {
    Ticket found = this.service.deleteById(id);
    if (found != null) {
        return ResponseEntity.ok(found);
    }
    return ResponseEntity.notFound().build();
}

@GetMapping
public List<Ticket> getAllTickets() {
    return this.service.getTickets();
}

@GetMapping("/by-id/{id}")
public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    Ticket found = this.service.getById(id);
    if (found != null) {
        return ResponseEntity.ok(found);
    }
    return ResponseEntity.notFound().build();
}
```

## Endpoints protegidos vistos desde el cliente

### Usuario ADMIN
```
✅ GET    /tickets              → 200 OK (pública)
✅ GET    /tickets/1            → 200 OK (pública)
✅ POST   /tickets              → 201 Created
✅ PUT    /tickets/1            → 200 OK
✅ DELETE /tickets/1            → 200 OK
```

### Usuario USER
```
✅ GET    /tickets              → 200 OK (pública)
✅ GET    /tickets/1            → 200 OK (pública)
❌ POST   /tickets              → 403 Forbidden
❌ PUT    /tickets/1            → 403 Forbidden
❌ DELETE /tickets/1            → 403 Forbidden
```

### Sin autenticación
```
✅ GET    /tickets              → 200 OK (pública)
✅ GET    /tickets/1            → 200 OK (pública)
❌ POST   /tickets              → 401 Unauthorized
❌ PUT    /tickets/1            → 401 Unauthorized
❌ DELETE /tickets/1            → 401 Unauthorized
```
