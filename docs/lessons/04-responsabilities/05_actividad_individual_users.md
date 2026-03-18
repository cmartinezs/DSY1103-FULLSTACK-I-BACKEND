# 05 - Actividad individual: recurso `users` con CSR completo (alcance clase 1)

## Enunciado para estudiantes

Implementa un microservicio pequeño para `users` aplicando la misma estructura vista con `tickets`.

### Requerimiento funcional mínimo

Crear un endpoint:

- `GET /api/v1/users`

Debe responder con una lista JSON de usuarios cargados en memoria.

## Restricciones de la actividad

- Debes usar patrón CSR con paquetes separados
- Debes usar `List` para persistencia temporal
- Debes usar `ResponseEntity` en el `Controller`
- No implementar CRUD completo en esta clase
- Mantener configuración base en `application.properties` (sin hardcodear puertos en código)

## Sugerencia de modelo

```java
public class User {
    private Long id;
    private String nombre;
    private String correo;

    public User() {}

    public User(Long id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
    }

    // getters y setters
}
```

## Criterios de logro (evaluación formativa clase 1)

### Logro alto

- Estructura CSR correcta sin mezclar responsabilidades
- Endpoint semántico y versionado (`/api/v1/users`)
- Respuesta `200 OK` con JSON bien formado
- Explica claramente por qué la lógica no está en el `Controller`

### Logro medio

- Estructura CSR presente pero con mezcla menor entre capas
- Endpoint funcional, con pequeños problemas de nomenclatura
- Respuesta JSON correcta, pero sin justificar decisiones

### Logro inicial

- Endpoint funciona, pero el código no respeta separación por capas
- URL o estado HTTP no sigue convenciones vistas en clase

## Evidencias a entregar en clase

- Codigo en IntelliJ con paquetes por capa
- Prueba en Postman/Insomnia del `GET /api/v1/users`
- Explicación oral breve del flujo completo

## Extensión opcional (si terminas antes)

Agregar:

- `GET /api/v1/users/{id}` con `404 Not Found` cuando no exista
- Personalización mínima:
  - `server.port=8082`
  - `server.servlet.context-path=/users-app`
  - `banner.txt` con nombre del proyecto
