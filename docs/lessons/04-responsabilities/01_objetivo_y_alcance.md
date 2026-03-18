# 01 - Objetivo y alcance de la clase

## Objetivo pedagógico (2 horas)

Construir un microservicio inicial bien estructurado, aplicando separación de responsabilidades desde el primer endpoint.

Al terminar, el estudiante debe poder explicar y demostrar:

- Qué hace cada capa en CSR (`Controller`, `Service`, `Repository`, `Model`)
- Por qué no conviene concentrar lógica en el controlador
- Cómo retornar JSON con control explícito de estado HTTP usando `ResponseEntity`
- Cómo definir una URL REST base con versionado y recurso en plural

## Contexto de continuidad del curso

Punto de partida real del grupo:

- Ya hicieron `@RestController` + `@RequestMapping` + `@GetMapping`
- Ya probaron un endpoint simple (`/greetings`) que devuelve texto plano

Evolución de esta clase:

- Pasamos de string simple a objeto serializado JSON
- Pasamos de controlador único a arquitectura por capas
- Mantenemos complejidad baja usando persistencia en memoria

## Límites de esta clase (no adelantar)

Para respetar la progresión de la unidad:

- No se exige CRUD completo
- No se exige `@Valid`, `@NotNull`, `@NotBlank` aún
- No se exige `@ControllerAdvice`
- No se usa base de datos real

Si hay tiempo, solo se permite una mejora opcional: filtro por estado o una validación de negocio mínima en `Service`.

## Configuración mínima y personalización base

También se deja instalada una capa mínima de configuración para reforzar operación y orden del proyecto:

- Archivo `application.properties` con parámetros visibles del entorno
- Personalización de arranque con `src/main/resources/banner.txt`
- Cambio de puerto con `server.port`
- Ajuste de prefijo global de rutas con `server.servlet.context-path`

Esta personalización no cambia el foco CSR, pero ayuda a que estudiantes entiendan cómo adaptar una API a distintos ambientes.

## Entregable mínimo de la sesión

Microservicio ejecutable con:

- Estructura por capas y paquetes
- `GET /api/v1/tickets` con `ResponseEntity<List<Ticket>>`
- Datos seed en memoria con `List`

## Mensaje clave para estudiantes

"Hoy no buscamos cantidad de endpoints; buscamos escribir un endpoint pequeño, pero con forma profesional desde el inicio."
