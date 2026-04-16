# Lección 16 - Checklist y rúbrica mínima

## Checklist antes de entregar

- [ ] `pom.xml` incluye `spring-boot-starter-security`
- [ ] `SecurityConfig.java` existe en `config/`
- [ ] `@EnableWebSecurity` presente
- [ ] `@EnableMethodSecurity` con `prePostEnabled = true`
- [ ] `UserDetailsService` devuelve al menos 2 usuarios (admin, user)
- [ ] `PasswordEncoder` es `BCryptPasswordEncoder`
- [ ] GET `/tickets` y `/tickets/by-id/{id}` son públicos
- [ ] POST, PUT, DELETE tienen `@PreAuthorize("hasRole('ADMIN')")`
- [ ] `csrf().disable()` incluido (para desarrollo)
- [ ] Prueba manual: sin auth → 401; con USER → 403; con ADMIN → 200/201

---

## Rúbrica de evaluación

### Autenticación (20 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Usuario "admin" existe y login funciona | 10 | POST sin auth → 401; con credenciales admin → 200 |
| Usuario "user" existe con rol limitado | 10 | POST con credenciales user → 403 Forbidden |

### Autorización (20 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Endpoints GET son públicos | 5 | GET sin auth → 200 OK |
| Endpoints POST/PUT/DELETE requieren ADMIN | 15 | Cada uno devuelve 403 para USER, 201/200 para ADMIN |

### Seguridad (20 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Contraseñas hasheadas con BCrypt | 10 | `passwordEncoder().encode()` utilizado |
| CORS configurado si hay frontend | 10 | Frontend accede sin error de CORS |

### Código (20 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| SecurityConfig correctamente estructurado | 10 | @Configuration, @EnableWebSecurity presentes |
| @PreAuthorize en endpoints adecuados | 10 | Métodos protegidos tienen anotación correcta |

### Documentación (20 pts)

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| README en proyecto explica roles | 10 | Usuario y contraseña de cada rol documentados |
| Descripción de flujo autenticación | 10 | Explicación clara: user envía creds → server valida → respuesta |

---

## Red flags (falla automática)

❌ Contraseña en texto plano en SecurityConfig  
❌ GET endpoints requieren autenticación  
❌ No hay validación de roles (todos los usuarios pueden eliminar)  
❌ `csrf().disable()` comentado pero sin justificación  
❌ No compila o tests fallan  

**Total: 100 puntos**
