# Lección 16 - Troubleshooting

## Error 1: 403 Forbidden aunque estoy logueado

**Causa:** Rol insuficiente.

**Solución:**
```
Verifica:
1. ¿Header Authorization incluye credenciales correctas?
2. ¿Usuario tiene rol ADMIN?
3. ¿@PreAuthorize("hasRole('ADMIN')") es correcto?

En SecurityConfig:
UserDetails admin = User.builder()
    .username("admin")
    .password(passwordEncoder().encode("pass123"))
    .roles("ADMIN")  ← Debe incluir rol
    .build();
```

## Error 2: 401 Unauthorized aunque tengo credenciales

**Causa:** Contraseña incorrecta o usuario no existe.

**Solución:**
```
1. Verifica base64: echo -n "admin:pass123" | base64
2. Genera header correcto: Authorization: Basic YWRtaW46cGFzczEyMw==
3. En Postman: usa Basic Auth tab (no manual header)
4. Verifica contraseña en SecurityConfig coincide
```

## Error 3: No puedo acceder a GET aunque es público

**Causa:** `csrf().disable()` no está en SecurityConfig.

**Solución:**
```java
http.csrf().disable();  // En filterChain()
```

## Error 4: CORS error al acceder desde frontend

**Causa:** SecurityConfig no configura CORS.

**Solución:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

## Error 5: "No AuthenticationProvider found"

**Causa:** `@EnableWebSecurity` no presente o falta `UserDetailsService`.

**Solución:**
```java
@Configuration
@EnableWebSecurity      // ← Agrega esto
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {  // ← Y esto
        return new InMemoryUserDetailsManager(...);
    }
}
```

## Error 6: Contraseña no funciona aunque escribo correcta

**Causa:** Comparación de contraseña sin PasswordEncoder.

**Síntoma:** Contraseña en texto plano en SecurityConfig.

**Solución:**
```java
// ❌ INCORRECTO
.password("pass123")

// ✅ CORRECTO
.password(passwordEncoder().encode("pass123"))
```

## Error 7: @PreAuthorize no funciona

**Causa:** `@EnableMethodSecurity` no presente.

**Solución:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // ← Agrega esto
public class SecurityConfig {
    // ...
}
```

## Error 8: Usuario "admin" no tiene rol aunque lo definí

**Causa:** Confusión con `.roles()` vs `.authorities()`.

**Solución:**
```java
// ✅ CORRECTO: .roles() agrega ROLE_ automáticamente
.roles("ADMIN")  // Interno: ROLE_ADMIN

// ✅ TAMBIÉN CORRECTO: .authorities() manual
.authorities("ROLE_ADMIN")

// ❌ INCORRECTO: olvidas ROLE_
.authorities("ADMIN")  // No matchea con hasRole('ADMIN')
```
