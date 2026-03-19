# 🏗️ Niveles de Madurez de Richardson

## ¿Qué es el Modelo de Madurez de Richardson?
El **Richardson Maturity Model (RMM)** es un modelo propuesto por **Leonard Richardson** y popularizado por **Martin Fowler** que clasifica las APIs en **4 niveles (0 al 3)** según qué tan bien aplican los principios de la arquitectura REST. Es una herramienta de diagnóstico y guía para el diseño de APIs.

> 📖 Artículo original de Martin Fowler: [martinfowler.com/articles/richardsonMaturityModel.html](https://martinfowler.com/articles/richardsonMaturityModel.html)

---

## Los 4 Niveles

```
Nivel 3 ██████████  HATEOAS
Nivel 2 ████████    Verbos HTTP        ← Mínimo esperado en este curso
Nivel 1 ██████      Recursos
Nivel 0 ████        Un solo endpoint
```

---

## 🔴 Nivel 0 — El Pantano de POX *(The Swamp of POX)*

> Un único endpoint que actúa como un túnel de mensajes. No se aprovecha HTTP como protocolo; solo se usa como transporte.

**POX** = *Plain Old XML* (aunque hoy suele ser JSON).

### Características
- Un único endpoint (ej. `/api`, `/service`, `/rpc`)
- Se usa un solo verbo HTTP (generalmente `POST` para todo)
- La acción y los datos van en el cuerpo del mensaje
- No hay diferenciación entre recursos

### Ejemplo

```http
POST /api HTTP/1.1
Content-Type: application/json

{ "accion": "obtenerTicket", "id": 1 }

---

POST /api HTTP/1.1
Content-Type: application/json

{ "accion": "crearTicket", "titulo": "Error en login", "estado": "ABIERTO" }

---

POST /api HTTP/1.1
Content-Type: application/json

{ "accion": "eliminarTicket", "id": 1 }
```

### Problemas
- Difícil de mantener y documentar
- No aprovecha caché HTTP
- Viola el principio de recursos identificables
- Típico de APIs SOAP o RPC mal migradas a HTTP

---

## 🟡 Nivel 1 — Recursos *(Resources)*

> Se identifican los **recursos** del dominio como entidades separadas con sus propias URLs. Sin embargo, aún no se usan correctamente los verbos HTTP.

### Características
- Múltiples endpoints, uno por tipo de recurso
- Los recursos se identifican en la URL
- Se sigue usando generalmente `POST` para todas las operaciones
- La acción puede seguir estando en la URL o en el cuerpo

### Ejemplo

```http
POST /tickets          ← "obtener todos los tickets"
POST /tickets/1        ← "obtener el ticket con id 1"
POST /tickets/crear    ← "crear un nuevo ticket"
POST /tickets/1/cerrar ← "cerrar el ticket con id 1"
```

### Mejora respecto al Nivel 0
- Las URLs identifican recursos específicos
- Es más fácil entender qué entidad está involucrada

### Problema que persiste
- Los verbos HTTP no tienen semántica: todo sigue siendo `POST`
- Las URLs contienen verbos (antipatrón REST)

---

## 🟠 Nivel 2 — Verbos HTTP *(HTTP Verbs)*

> Se utilizan los **verbos HTTP** con su semántica correcta y los **códigos de estado** de respuesta son significativos.

### Características
- `GET` → leer, sin efectos secundarios, **cacheable**
- `POST` → crear un nuevo recurso
- `PUT` → reemplazar completamente un recurso existente
- `PATCH` → actualizar parcialmente un recurso
- `DELETE` → eliminar un recurso
- Los códigos de estado HTTP comunican el resultado de la operación

### Códigos de estado más comunes

| Código | Significado | Cuándo usarlo |
|--------|-------------|---------------|
| `200 OK` | Operación exitosa | GET, PUT, PATCH exitosos |
| `201 Created` | Recurso creado | POST exitoso |
| `204 No Content` | Sin contenido en respuesta | DELETE exitoso |
| `400 Bad Request` | Petición inválida | Datos mal formados o inválidos |
| `401 Unauthorized` | Sin autenticación | Token faltante o inválido |
| `403 Forbidden` | Sin autorización | Usuario autenticado pero sin permisos |
| `404 Not Found` | Recurso no encontrado | El ID no existe |
| `409 Conflict` | Conflicto de estado | Recurso ya existe, estado inválido |
| `500 Internal Server Error` | Error del servidor | Excepción no controlada |

### Ejemplo

```http
GET    /tickets          → 200 OK           (lista de tickets)
GET    /tickets/1        → 200 OK           (ticket encontrado)
GET    /tickets/99       → 404 Not Found    (ticket no existe)

POST   /tickets          → 201 Created      (ticket creado)
       Body: { "titulo": "Error en login", "estado": "ABIERTO" }

PUT    /tickets/1        → 200 OK           (ticket actualizado completo)
       Body: { "titulo": "Error en login v2", "estado": "EN_PROGRESO" }

PATCH  /tickets/1        → 200 OK           (actualización parcial)
       Body: { "estado": "CERRADO" }

DELETE /tickets/1        → 204 No Content   (ticket eliminado)
DELETE /tickets/99       → 404 Not Found    (ticket no existe)
```

### Implementación en Spring Boot

```java
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @GetMapping
    public ResponseEntity<List<Ticket>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ticket> crear(@RequestBody Ticket ticket) {
        Ticket creado = service.guardar(ticket);
        URI location = URI.create("/tickets/" + creado.getId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> actualizar(
            @PathVariable Long id,
            @RequestBody Ticket ticket) {
        return ResponseEntity.ok(service.actualizar(id, ticket));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

> ⭐ **Este es el nivel mínimo esperado en este curso.**

---

## 🟢 Nivel 3 — Controles Hipermedia *(HATEOAS)*

> La API incluye en cada respuesta **enlaces hipermedia** que guían al cliente sobre las acciones disponibles desde el estado actual del recurso.

**HATEOAS** = *Hypermedia As The Engine Of Application State*

### Concepto
El cliente **no necesita conocer de antemano** la estructura de URLs. En cambio, la API le dice qué puede hacer a continuación a través de enlaces embebidos en la respuesta.

Es similar a navegar por una página web: no escribes URLs manualmente, sino que haces clic en los enlaces que la página te ofrece.

### Ejemplo de respuesta con HATEOAS

```json
{
  "id": 1,
  "titulo": "Error en login",
  "estado": "ABIERTO",
  "_links": {
    "self": {
      "href": "/tickets/1",
      "method": "GET"
    },
    "actualizar": {
      "href": "/tickets/1",
      "method": "PUT"
    },
    "cerrar": {
      "href": "/tickets/1/cerrar",
      "method": "POST"
    },
    "eliminar": {
      "href": "/tickets/1",
      "method": "DELETE"
    },
    "todos": {
      "href": "/tickets",
      "method": "GET"
    }
  }
}
```

Si el ticket ya está `CERRADO`, la respuesta no incluiría el enlace `cerrar`:

```json
{
  "id": 1,
  "titulo": "Error en login",
  "estado": "CERRADO",
  "_links": {
    "self":   { "href": "/tickets/1", "method": "GET" },
    "reabrir": { "href": "/tickets/1/reabrir", "method": "POST" },
    "todos":  { "href": "/tickets", "method": "GET" }
  }
}
```

### Implementación con Spring HATEOAS

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

```java
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @GetMapping("/{id}")
    public EntityModel<Ticket> obtener(@PathVariable Long id) {
        Ticket ticket = service.buscarPorId(id)
            .orElseThrow(() -> new TicketNotFoundException(id));

        EntityModel<Ticket> model = EntityModel.of(ticket);

        model.add(linkTo(methodOn(TicketController.class).obtener(id))
            .withSelfRel());
        model.add(linkTo(methodOn(TicketController.class).listar())
            .withRel("todos"));

        if ("ABIERTO".equals(ticket.getEstado())) {
            model.add(linkTo(methodOn(TicketController.class).cerrar(id))
                .withRel("cerrar"));
        }

        return model;
    }
}
```

### Ventajas de HATEOAS
- El cliente es más desacoplado de la API
- La API puede evolucionar sin romper a los clientes
- Autodescubrimiento: los clientes aprenden qué pueden hacer

### Desventajas / Consideraciones
- Mayor complejidad de implementación
- No todos los clientes (SPAs, móviles) lo aprovechan
- Puede aumentar el tamaño de las respuestas

---

## Resumen comparativo

| Nivel | Nombre | URLs | Verbos HTTP | Códigos Estado | Hipermedia |
|-------|--------|------|-------------|----------------|------------|
| 0 | Pantano de POX | ❌ Una sola | ❌ Solo POST | ❌ Ignorados | ❌ |
| 1 | Recursos | ✅ Por recurso | ❌ Solo POST | ❌ Ignorados | ❌ |
| 2 | Verbos HTTP | ✅ Por recurso | ✅ Correctos | ✅ Correctos | ❌ |
| 3 | HATEOAS | ✅ Por recurso | ✅ Correctos | ✅ Correctos | ✅ |

---

## ¿Debo implementar siempre el Nivel 3?

No necesariamente. El **Nivel 2 es el estándar de la industria** para la mayoría de APIs REST. HATEOAS (Nivel 3) es más apropiado cuando:

- La API es consumida por clientes que no conocen la estructura de antemano
- Se busca máximo desacoplamiento entre cliente y servidor
- La API es pública y debe ser autodescubrible
- El estado del recurso determina dinámicamente qué acciones están disponibles

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| Richardson Maturity Model | 📖 Artículo (Martin Fowler) | [martinfowler.com](https://martinfowler.com/articles/richardsonMaturityModel.html) |
| Spring HATEOAS | 📄 Documentación oficial | [spring.io/projects/spring-hateoas](https://spring.io/projects/spring-hateoas) |
| REST API Tutorial | 📖 Guía | [restfulapi.net](https://restfulapi.net/) |
| HTTP Status Codes | 📄 Referencia | [developer.mozilla.org](https://developer.mozilla.org/es/docs/Web/HTTP/Status) |

---

*[← Volver a Extras](../README.md)*

