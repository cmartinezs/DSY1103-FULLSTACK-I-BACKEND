# Lección 16 - Spring Security: Autenticación y Autorización

## ¿Quién accede a qué?

Hasta ahora tu API es pública: cualquiera puede crear, consultar, modificar y eliminar tickets sin autenticarse. En producción esto es inaceptable.

Spring Security agrega una capa de autenticación (¿quién eres?) y autorización (¿qué puedes hacer?).

---

## Quick Start

### Concepto

- **Autenticación:** Verificar identidad (usuario + contraseña)
- **Autorización:** Verificar permisos (rol ADMIN vs USER)

### Flujo básico

```
Cliente → POST /login (usuario, contraseña)
        ↓
   Spring Security valida credenciales
        ↓
   Si válido → devuelve token/sesión
        ↓
   Cliente accede a endpoints protegidos con token/sesión
```

---

## Lo que construirás

1. Agregar Spring Security al `pom.xml`
2. Configurar usuarios en memoria (para desarrollo)
3. Crear endpoint `/login`
4. Proteger endpoints con roles (`@PreAuthorize`)
5. Testear: login funciona, endpoints protegidos rechazan sin auth

---

## Lecturas recomendadas antes

- Lección 07: Manejo de errores
- Lección 11: Perfiles
