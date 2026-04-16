# Lección 16 - Cifrado de contraseñas

## ¿Por qué no guardar contraseñas en texto plano?

```
RIESGO: Si la BD se filtra, todas las contraseñas están expuestas.
```

**Solución:** Usar hashing irreversible + salt.

---

## BCryptPasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### Cómo funciona

```java
String rawPassword = "pass123";
PasswordEncoder encoder = new BCryptPasswordEncoder();

// Hashear contraseña al crear usuario
String hashedPassword = encoder.encode(rawPassword);
// Resultado: $2a$10$R9XzqLwA2lHEGhG9a...zBjW3BN8VN.jvM (diferente cada vez)

// Verificar al login
boolean matches = encoder.matches(rawPassword, hashedPassword);
// Resultado: true
```

### Propiedades de BCrypt

- **Irreversible:** No puedes recuperar "pass123" del hash
- **Salted:** Cada hash es diferente (salt único agregado)
- **Lento:** Requiere múltiples iteraciones (caro computacionalmente)
  - Esto ralentiza ataques de fuerza bruta

---

## Comparativa: PasswordEncoder vs alternativas

| Estrategia | Seguridad | Velocidad | ¿Usar? |
|-----------|-----------|-----------|--------|
| **Texto plano** | ❌ Crítico | ⚡ Rápido | ❌ NUNCA |
| **MD5 / SHA1** | ❌ Vulnerable | ⚡ Rápido | ❌ NO |
| **SHA-256** | ⚠️ Mejor | ⚡ Rápido | ⚠️ Solo + salt |
| **BCrypt** | ✅ Excelente | 🐢 Lento | ✅ SÍ |
| **Argon2** | ✅✅ Excelente | 🐢 Muy lento | ✅ Mejor que BCrypt |

**Recomendación:** BCrypt para Spring Security básico, Argon2 para máxima seguridad.

---

## En la práctica

```java
// En SecurityConfig:
@Bean
public UserDetailsService userDetailsService() {
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("pass123"))  // ← Hash aquí
        .roles("ADMIN")
        .build();
    return new InMemoryUserDetailsManager(admin);
}

// Al login, Spring automáticamente:
// 1. Recibe la contraseña del cliente
// 2. Hashea: encoder.encode("pass123")
// 3. Compara: encoder.matches(clientPassword, hashedPassword)
// 4. Devuelve true/false
```

---

## ¿Qué pasa si cambias la contraseña?

```java
// Viejo hash (en la BD)
$2a$10$R9XzqLwA2lHEGhG9a...zBjW3BN8VN.jvM

// Usuario ingresa nueva contraseña "newpass"
// Spring hashea: $2a$10$DifferentHashCompletely...

// No coinciden → acceso denegado
// Flujo correcto: invalidar sesión, pedir re-autenticación
```
