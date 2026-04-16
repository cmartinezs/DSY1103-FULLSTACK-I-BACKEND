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
@Configuration
@EnableWebSecurity
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
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/tickets").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tickets/by-id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tickets/by-id/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf().disable();

        return http.build();
    }
}
```

**¿Qué hace?**
- `permitAll()`: sin autenticación requerida
- `hasRole("ADMIN")`: solo para usuarios con rol ADMIN
- `httpBasic()`: autenticación básica (usuario:contraseña en header Authorization)
- `csrf().disable()`: desactiva CSRF para desarrollo (activar en producción)

---

## Paso 3: Proteger endpoints con `@PreAuthorize`

En `TicketController`:

```java
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> create(@Valid @RequestBody Ticket ticket) {
    // ...
}

@PutMapping("/by-id/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody Ticket ticket) {
    // ...
}

@DeleteMapping("/by-id/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> deleteTicketById(@PathVariable Long id) {
    // ...
}
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

En Postman/Thunder Client, agrega header:

```
Authorization: Basic YWRtaW46cGFzczEyMw==
```

(El base64 codifica: `admin:pass123`)

Resultado: `201 Created`

### Prueba 4: POST con autenticación USER (debe fallar)

Header:

```
Authorization: Basic dXNlcjp1c2VyMTIz
```

(El base64 codifica: `user:user123`)

Resultado: `403 Forbidden`
