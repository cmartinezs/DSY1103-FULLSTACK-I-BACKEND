# 03 - Decisiones de diseño: REST + responsabilidades

## Decisión 1: URL como recurso, no como acción

Se adopta desde hoy:

- `GET /api/v1/tickets`

Porque:

- `/api` separa la API del resto
- `/v1` habilita evolución sin romper clientes
- `tickets` (plural) representa recurso

Evitar por ahora:

- `/getTickets`
- `/obtenerTickets`
- `/ticket-list`

## Decisión 2: `ResponseEntity` desde el primer endpoint

No esperar a "cuando haya errores" para introducirlo.

Ventajas didácticas tempranas:

- Hace visible el código HTTP
- Entrena criterio REST desde el inicio
- Prepara para `201`, `404`, `400` en clases siguientes

Ejemplo base:

```java
return ResponseEntity.ok(lista);
```

## Decisión 3: inyección por constructor en todas las capas

Se usa siempre:

```java
private final TicketService service;

public TicketController(TicketService service) {
    this.service = service;
}
```

Motivo:

- Dependencias explícitas
- Facilita testeo
- Práctica recomendada de Spring

## Decisión 4: cada capa responde una pregunta distinta

- `Controller`: ¿cómo entra/sale HTTP?
- `Service`: ¿qué regla de negocio aplica?
- `Repository`: ¿dónde y cómo obtengo/guardo datos?

Regla docente para revisar código:

- Si ves `ResponseEntity` fuera del `Controller`, probablemente hay mezcla de responsabilidades.
- Si ves lógica de negocio relevante en `Controller`, mover a `Service`.
- Si ves reglas en `Repository`, mover a `Service` (salvo reglas técnicas de acceso a datos).

## Decisión 5: persistencia temporal en memoria para foco pedagógico

`List` permite:

- Iterar rápido sin fricción de infraestructura
- Visualizar claramente operaciones de lectura/filtrado
- Preparar salto posterior a base de datos real

No confundir:

- Persistencia en memoria != persistencia real
- Reiniciar aplicación borra datos

## Decisión 6: personalización operativa mínima sin romper CSR

Se incorpora una personalización básica de arranque para reforzar criterio de entorno:

- `server.port` para mover la app de puerto
- `server.servlet.context-path` para prefijo global de rutas
- `banner.txt` para identificar la aplicación al iniciar

Regla docente:

- Estos ajustes viven en `resources` y configuración.
- No se deben resolver metiendo lógica en `Controller`, `Service` o `Repository`.

Ejemplo:

```properties
server.port=8081
server.servlet.context-path=/tickets-app
```

## Criterio de calidad para esta clase

"Poco alcance, buena forma".

Es preferible un único endpoint bien diseñado en CSR que varios endpoints acoplados y desordenados.
