# Tickets-16: Lección 16 - Spring Security

## Descripción

Este proyecto implementa la **Lección 16: Spring Security** del curso DSY1103 Fullstack I.

Agrega autenticación y autorización con roles (USER, AGENT, ADMIN).

## Cambios desde Lección 15

### Dependencia agregada
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Archivos nuevos
- `config/SecurityConfig.java` - Configuración de seguridad
- `config/CustomUserDetailsService.java` - Carga usuarios desde BD

## Roles y permisos

| Rol | Permisos |
|-----|---------|
| USER | GET /tickets, POST /tickets |
| AGENT | + PUT /tickets/{id} |
| ADMIN | + /users, /categories, /tags |

## Testing

```bash
# Users con roles (del DataInitializer):
# admin / admin123 (ADMIN)
# agent1 / agent123 (AGENT)
# user1 / user123 (USER)

curl -u admin:admin123 http://localhost:8080/ticket-app/tickets
```

## Estado

✅ Completado