# Lección 16 — Troubleshooting

Errores más frecuentes al implementar Spring Security con base de datos.

---

## Error 1: `401 Unauthorized` aunque las credenciales son correctas

**Síntoma:** Envías el header `Authorization: Basic ...` pero recibes 401.

**Causa más común:** El Base64 está mal generado.

**Diagnóstico:**
```bash
# Genera el Base64 correcto en la terminal
echo -n "admin@empresa.com:pass123" | base64
# → YWRtaW5AZW1wcmVzYS5jb206cGFzczEyMw==

# Si tu sistema añade un salto de línea al final, el Base64 será incorrecto.
# El flag -n evita eso. En Windows (PowerShell):
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("admin@empresa.com:pass123"))
```

**Otras causas:**
1. La contraseña en la BD no corresponde al hash BCrypt del texto que usas
2. El email no existe en la BD
3. El campo `password` del usuario es `null` o vacío
4. La migración V6 no se ejecutó y los usuarios no tienen contraseña
5. `active=false`, por lo que Spring Security considera el usuario deshabilitado
6. Falta `UserRepository.findByEmail(email)` o no está siendo usado por `CustomUserDetailsService`

**Verificación:**
```sql
-- Comprueba que los usuarios tienen password en la BD
SELECT email, role, active, LEFT(password, 20) AS password_preview FROM users;
-- Debe mostrar: $2a$10$... en la columna password
```

---

## Error 2: `403 Forbidden` aunque estoy autenticado

**Síntoma:** El login funciona (no es 401), pero el endpoint devuelve 403.

**Causa:** El rol del usuario no tiene permiso para ese endpoint.

**Diagnóstico:**
```bash
# Verifica el rol del usuario con:
# GET /auth/me (si lo implementaste) o consultando la BD directamente:
SELECT email, role FROM users WHERE email = 'ana.garcia@empresa.com';
```

**Verifica en `SecurityConfig`:**
```java
// ¿Está el rol del usuario en la regla correcta?
.requestMatchers(HttpMethod.DELETE, "/tickets/by-id/**").hasRole("ADMIN")
// → USER y AGENT reciben 403. Solo ADMIN puede eliminar.
```

**Verifica cómo construyes los roles:**
```java
// Opción recomendada en el guion: Spring agrega ROLE_ automáticamente.
.roles(user.getRole().name())

// Alternativa válida: tú agregas ROLE_ manualmente.
.authorities("ROLE_" + user.getRole().name())

// Incorrecto si luego usas hasRole("ADMIN"):
.authorities(user.getRole().name())
```

---

## Error 3: El compilador falla con `user.getRole()` — tipo incorrecto

**Síntoma:** Error de compilación en `CustomUserDetailsService`:
```
error: incompatible types: Role cannot be converted to String
```

**Causa:** El campo `role` en `User` es un **enum** (`Role`), no un `String`.

**Solución:** Llamar a `.name()` para convertir el enum a String:
```java
// ❌ INCORRECTO
.roles(user.getRole())

// ✅ CORRECTO
.roles(user.getRole().name())
// user.getRole() → Role.ADMIN (enum)
// .name()         → "ADMIN"  (String)
```

---

## Error 4: Los endpoints GET también requieren autenticación

**Síntoma:** `GET /tickets` devuelve 401 en lugar de 200.

**Causa:** Falta la regla `permitAll()` en `SecurityConfig`, o las reglas están en el orden incorrecto.

**Solución:**
```java
// En filterChain(), las reglas se evalúan en orden. El GET debe ir ANTES de anyRequest():
http.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.GET, "/tickets", "/tickets/by-id/**").permitAll()  // ← PRIMERO
    .requestMatchers(HttpMethod.GET, "/categories", "/categories/by-id/**").permitAll()
    // ... otras reglas ...
    .anyRequest().authenticated()   // ← SIEMPRE al final
);
```

> Si `.anyRequest().authenticated()` estuviera antes de las reglas `permitAll()`, Spring Security nunca llegaría a evaluarlas.

---

## Error 5: `NullPointerException` en `loadUserByUsername`

**Síntoma:**
```
java.lang.NullPointerException: Cannot invoke "cl.duoc.fullstack.tickets.model.User$Role.name()"
because the return value of "User.getRole()" is null
```

**Causa:** Un usuario en la BD tiene el campo `role` como `NULL`.

**Solución:**
```sql
-- Verifica usuarios sin rol asignado
SELECT id, email, role FROM users WHERE role IS NULL;

-- Corrige con un UPDATE
UPDATE users SET role = 'USER' WHERE role IS NULL;
```

También puedes agregar el filtro en el servicio:
```java
User user = userRepository.findByEmail(email)
    .filter(u -> u.getPassword() != null && !u.getPassword().isBlank())
    .filter(u -> u.getRole() != null)   // ← Previene NullPointerException
    .orElseThrow(() -> new UsernameNotFoundException("..."));
```

---

## Error 6: Spring Security no encontró `UserDetailsService`

**Síntoma:**
```
java.lang.IllegalStateException: No AuthenticationProvider found for
org.springframework.security.authentication.UsernamePasswordAuthenticationToken
```

**Causa:** `CustomUserDetailsService` no está siendo detectado por Spring.

**Verificación:**
1. ¿Tiene la anotación `@Service`?
   ```java
   @Service   // ← obligatorio
   public class CustomUserDetailsService implements UserDetailsService {
   ```
2. ¿Está en un paquete que Spring escanea? El paquete `config` dentro del paquete base de la aplicación está escaneado automáticamente.
3. ¿Hay más de una clase que implementa `UserDetailsService`? Spring no sabe cuál usar.

---

## Error 7: `@PreAuthorize` no funciona (siempre pasa o siempre falla)

**Síntoma:** La anotación `@PreAuthorize("hasRole('ADMIN')")` es ignorada.

**Causa:** Falta `@EnableMethodSecurity` en `SecurityConfig`.

**Solución:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // ← Agrega esto para habilitar @PreAuthorize
public class SecurityConfig {
    // ...
}
```

---

## Error 8: Rutas con context-path no matchean

**Síntoma:** Las reglas de `SecurityConfig` no se aplican; todos los endpoints quedan sin protección o todos quedan bloqueados.

**Causa:** Incluir el context-path (`/ticket-app`) en las rutas de `requestMatchers`.

**Solución:** Los `requestMatchers` se escriben **sin** el context-path. Spring Security evalúa la ruta después de que el context-path es procesado:

```java
// ❌ INCORRECTO: incluye el context-path
.requestMatchers(HttpMethod.GET, "/ticket-app/tickets").permitAll()

// ✅ CORRECTO: solo la ruta del endpoint
.requestMatchers(HttpMethod.GET, "/tickets", "/tickets/by-id/**").permitAll()
```

El `context-path` se configura en `application.yml`:
```yaml
server:
  servlet:
    context-path: /ticket-app  # Spring Security no lo ve en requestMatchers
```

---

## Error 9: POST devuelve 403 aunque tengo rol correcto (CSRF)

**Síntoma:** GET funciona con autenticación, pero POST/PUT/DELETE devuelve 403 con el cuerpo:
```json
{"error": "Forbidden", "message": "...CSRF..."}
```

**Causa:** La protección CSRF está habilitada (es el comportamiento por defecto).

**Solución:** Deshabilitarla explícitamente en `SecurityConfig`:
```java
http
    .csrf(csrf -> csrf.disable())   // ← Agregar esto
    // ... resto de la configuración
```

> **¿Por qué deshabilitamos CSRF?** Las API REST STATELESS no usan cookies de sesión, por lo que la protección CSRF no aplica. CSRF solo es necesario en aplicaciones web que usan sesiones (formularios HTML).

---

## Error 10: `DataInitializer` duplica los datos en MySQL

**Síntoma:** La BD MySQL tiene el doble de usuarios esperados — los de la migración V6 y los del `DataInitializer`.

**Causa:** `DataInitializer` no tiene `@Profile("h2")` y se ejecuta en todos los perfiles.

**Solución:**
```java
@Component
@Profile("h2")   // ← Solo para H2; MySQL y Supabase usan las migraciones Flyway
public class DataInitializer implements CommandLineRunner {
    // ...
}
```

Después de agregar `@Profile("h2")`, con perfil MySQL el `DataInitializer` no se ejecuta y los datos solo vienen de las migraciones V5 y V6.

---

## Error 11: Error de dependencia circular con `PasswordEncoder`

**Síntoma:** La aplicación no arranca y muestra un error de ciclo de dependencias entre configuración, servicios y seguridad.

**Causa común:** Crear o inyectar dependencias de seguridad en lugares incorrectos, por ejemplo intentar construir el encoder dentro de `CustomUserDetailsService` o declarar beans duplicados.

**Solución:** Declara un único bean `PasswordEncoder`, normalmente en `SecurityConfig`, e inyéctalo solo donde necesites generar hashes, como `DataInitializer`.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

`CustomUserDetailsService` no necesita inyectar `PasswordEncoder`. Solo debe cargar el usuario y entregar a Spring Security el hash guardado.

---

## Error 12: H2 funciona, pero MySQL/PostgreSQL no autentica

**Síntoma:** Con H2 puedes autenticar, pero con otro perfil recibes `401`.

**Causas probables:**

1. Flyway no ejecutó la migración que agrega `password`.
2. Los usuarios de la BD externa no tienen hash BCrypt.
3. El seed de datos de H2 no existe en MySQL/PostgreSQL.
4. Estás probando contra una base de datos antigua sin la columna `active` o sin roles válidos.

**Verificación:**

```sql
SELECT email, role, active, password FROM users;
```

La columna `password` debe contener valores que comiencen con `$2a$`, `$2b$` o `$2y$`.

---

## Error 13: `@ticketSecurity` no se encuentra

**Síntoma:** La aplicación falla al evaluar `@PreAuthorize("@ticketSecurity.canEdit(#id, authentication)")`.

**Causa:** El bean no existe con el nombre `ticketSecurity` o está fuera del paquete escaneado por Spring.

**Solución:** Declara el componente con nombre explícito dentro del paquete base de la aplicación:

```java
@Component("ticketSecurity")
public class TicketSecurity {
    // ...
}
```

---

## Error 14: USER o AGENT siempre reciben `403` al editar

**Síntoma:** El usuario está autenticado, tiene rol `USER` o `AGENT`, pero `PUT /tickets/by-id/{id}` siempre devuelve `403`.

**Causas probables:**

1. El ticket no existe y `ticketRepository.findById(id)` retorna vacío.
2. El ticket no tiene `createdBy` o `assignedTo`.
3. `authentication.getName()` no coincide con el email guardado en `createdBy.email` o `assignedTo.email`.
4. El usuario tiene una authority mal construida, por ejemplo `USER` en vez de `ROLE_USER`.
5. El `@PathVariable` se llama distinto y la expresión SpEL usa `#id`.

**Verificación:**

```sql
SELECT t.id, creator.email AS created_by, agent.email AS assigned_to
FROM tickets t
LEFT JOIN users creator ON creator.id = t.created_by_id
LEFT JOIN users agent ON agent.id = t.assigned_to_id
WHERE t.id = 1;
```

Y revisa que el método tenga el mismo nombre de parámetro usado por SpEL:

```java
@PutMapping("/by-id/{id}")
@PreAuthorize("@ticketSecurity.canEdit(#id, authentication)")
public ResponseEntity<Object> updateTicketById(@PathVariable Long id, ...) {
    // ...
}
```

---

## Error 15: ADMIN recibe `403` al editar tickets

**Síntoma:** Un usuario ADMIN autenticado no puede editar tickets.

**Causa:** `TicketSecurity` no está reconociendo la authority `ROLE_ADMIN`.

**Solución:** Verifica que `CustomUserDetailsService` construya roles con `.roles(user.getRole().name())` o authorities con el prefijo completo:

```java
.roles(user.getRole().name())
```

También verifica que el usuario admin tenga `role = 'ADMIN'` en la base de datos.
