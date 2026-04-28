# Lección 16 — Cifrado de Contraseñas con BCrypt

## ¿Por qué no guardar contraseñas en texto plano?

```
Base de datos filtrada (texto plano):
  admin@empresa.com  → pass123   ← atacante puede entrar inmediatamente
  ana@empresa.com    → user123   ← y en todos los otros sistemas donde usen la misma contraseña
```

```
Base de datos filtrada (BCrypt):
  admin@empresa.com  → $2a$10$gT.PsFi3xTq9xc3virQAf...  ← inútil sin la contraseña original
  ana@empresa.com    → $2a$10$LAK58ME84bgotvy2eL.eWe...  ← requiere fuerza bruta por cada usuario
```

**Solución:** Usar hashing irreversible con salt aleatorio.

---

## ¿Qué es un hash de contraseña?

Un hash es una función de una sola vía:

```
"pass123"  →  función hash  →  "$2a$10$gT.PsFi3xTq9xc3virQAf..."
                     ↑
          No existe función inversa
```

No puedes recuperar `"pass123"` a partir del hash. Lo único que puedes hacer es verificar si una contraseña candidata produce el mismo hash.

---

## BCryptPasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // cost factor 10 por defecto
}
```

### Cómo funciona

```java
PasswordEncoder encoder = new BCryptPasswordEncoder(10);  // cost factor = 10

// Al registrar/seedear un usuario (operación lenta, ~100ms)
String hash = encoder.encode("pass123");
// → $2a$10$gT.PsFi3xTq9xc3virQAfesYBesY5g53tQ5R7lgJGqgVdVMH0I8qa

// Llama otra vez con la misma contraseña → hash DIFERENTE (salt aleatorio)
String otrohash = encoder.encode("pass123");
// → $2a$10$SomeOtherHashBecauseTheSaltIsRandom...

// Al verificar en cada login (operación lenta, ~100ms)
boolean ok = encoder.matches("pass123", hash);  // true
boolean ko = encoder.matches("malpass", hash);  // false
```

### Anatomía de un hash BCrypt

```
$2a$10$gT.PsFi3xTq9xc3virQAfesYBesY5g53tQ5R7lgJGqgVdVMH0I8qa
 │   │  │                  │                               │
 │   │  └─ Salt (22 chars) └─ Hash de la contraseña (31 chars)
 │   └─ Cost factor: 2^10 = 1024 iteraciones
 └─ Versión del algoritmo BCrypt
```

---

## El cost factor: velocidad vs seguridad

BCrypt es **intencionalmente lento**. El cost factor controla cuántas iteraciones realiza:

| Cost | Iteraciones | Tiempo (~) | Uso |
|------|-------------|------------|-----|
| 4 | 16 | < 1ms | Tests automáticos |
| 10 | 1,024 | ~100ms | **Producción (defecto)** |
| 12 | 4,096 | ~400ms | Alta seguridad |
| 14 | 16,384 | ~1.5s | Máxima seguridad (lento para el usuario) |

**¿Por qué queremos que sea lento?**

Un atacante que roba la BD con hashes BCrypt no puede hacer fuerza bruta eficientemente:
- Con SHA-256 (rápido): puede probar ~10,000,000,000 contraseñas por segundo
- Con BCrypt cost=10: puede probar ~100 contraseñas por segundo

Para el usuario final, 100ms por login es imperceptible. Para un atacante, probar millones de contraseñas toma años.

---

## El salt: por qué cada hash es diferente

El salt es un valor aleatorio que se agrega a la contraseña antes de hashear:

```
Sin salt:
  "pass123" → siempre produce el mismo hash
  Si dos usuarios tienen la misma contraseña → mismo hash en la BD
  → Un atacante puede usar tablas arcoíris (rainbow tables) precomputadas

Con salt (BCrypt):
  "pass123" + salt1 → hash1
  "pass123" + salt2 → hash2 (completamente diferente)
  El salt se guarda dentro del propio hash ($2a$10$SALT_AQUI...)
  → Las rainbow tables no funcionan porque cada hash tiene su propio salt
```

**Consecuencia práctica:** No puedes comparar dos hashes BCrypt directamente. Solo puedes verificar con `encoder.matches(password, hash)`.

---

## Flujo completo de verificación en Spring Security

```
Cliente envía:
  Authorization: Basic YW5hLmdhcmNpYUBlbXByZXNhLmNvbTp1c2VyMTIz

Spring Security:
  1. Decodifica Base64
     → email = "ana.garcia@empresa.com", password = "user123"

  2. Llama a CustomUserDetailsService.loadUserByUsername("ana.garcia@empresa.com")
     → Consulta la BD → devuelve UserDetails con hash BCrypt

  3. Compara con BCryptPasswordEncoder.matches("user123", hashDeLaBD)
     → true → autentica al usuario
     → false → devuelve 401 Unauthorized

(Spring Security hace la comparación automáticamente.
 Tú nunca llamas a encoder.matches() manualmente para el login.)
```

---

## Comparativa de algoritmos

| Algoritmo | Seguridad | Velocidad | ¿Usar en producción? |
|-----------|-----------|-----------|----------------------|
| Texto plano | ❌ Crítico | ⚡⚡⚡ | ❌ Nunca |
| MD5 | ❌ Roto | ⚡⚡⚡ | ❌ Nunca |
| SHA-1 | ❌ Roto | ⚡⚡⚡ | ❌ Nunca |
| SHA-256 sin salt | ⚠️ Vulnerable | ⚡⚡⚡ | ❌ No (rainbow tables) |
| SHA-256 + salt | ⚠️ Aceptable | ⚡⚡ | ⚠️ Solo si BCrypt no está disponible |
| **BCrypt** | ✅ Excelente | 🐢 Lento intencional | ✅ **SÍ (esta lección)** |
| Argon2 | ✅✅ Superior | 🐢🐢 Muy lento | ✅ Para máxima seguridad |

**Recomendación:** BCrypt para proyectos Spring Boot estándar. Es el estándar de la industria y Spring Security lo soporta nativamente.

---

## Reglas de oro

1. **Nunca** guardes contraseñas en texto plano — ni en la BD, ni en logs, ni en variables de entorno
2. **Nunca** construyas tu propio algoritmo de hashing — usa BCrypt o Argon2
3. **Siempre** usa `encoder.matches()` para comparar — nunca `hashAlmacenado.equals(hashNuevo)`
4. **Siempre** hashea en el backend — nunca confíes en un hash enviado desde el cliente
5. **Nunca** pongas el `PasswordEncoder` en el `CustomUserDetailsService` — ponlo en `SecurityConfig` como `@Bean` para evitar dependencias circulares
