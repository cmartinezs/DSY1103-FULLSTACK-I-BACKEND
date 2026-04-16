# Lección 16 - Tutorial paso a paso: Spring Security básico

Sigue esta guía para proteger tu API con autenticación y autorización.

---

## Paso 1: Agregar dependencias

En `pom.xml`, dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Ejecuta: `mvn clean install`

---

## Paso 2: Crear `SecurityConfig`

Crea `config/SecurityConfig.java`:

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

**Usuarios configurados:**

| Usuario | Contraseña | Rol   |
|---------|------------|-------|
| admin   | pass123    | ADMIN |
| user    | user123    | USER  |

**¿Qué hace?**
- `permitAll()`: sin autenticación requerida
- `hasRole("ADMIN")`: solo para usuarios con rol ADMIN
- `httpBasic()`: autenticación básica (usuario:contraseña en header Authorization)
- `csrf(csrf -> csrf.disable())`: desactiva CSRF para desarrollo (activar en producción)

---

## Paso 3: Autenticación

La API usa **Basic Auth**. Codifica en Base64 el par `usuario:contraseña`:

| Usuario | Contraseña | Base64                    |
|---------|------------|---------------------------|
| admin   | pass123    | `YWRtaW46cGFzczEyMw==`    |
| user    | user123    | `dXNlcjp1c2VyMTIz`        |

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46cGFzczEyMw==" \
  -d '{"title": "Nuevo", "description": "test"}'
```

---

## Paso 4: Testear

### Prueba 1: GET sin autenticación (debe funcionar)

```
GET http://localhost:8080/ticket-app/tickets
```

Resultado: `200 OK` con lista de tickets

### Prueba 2: POST sin autenticación (debe fallar)

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{ "title": "Nuevo", "description": "test" }
```

Resultado: `401 Unauthorized`

### Prueba 3: POST con autenticación ADMIN (debe funcionar)

```bash
curl -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46cGFzczEyMw==" \
  -d '{"title": "Nuevo", "description": "test"}'
```

Resultado: `201 Created`

### Prueba 4: POST con autenticación USER (debe fallar)

```bash
curl -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic dXNlcjp1c2VyMTIz" \
  -d '{"title": "Nuevo", "description": "test"}'
```

Resultado: `403 Forbidden`