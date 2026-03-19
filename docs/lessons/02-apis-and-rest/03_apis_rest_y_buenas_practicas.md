# Lección 02 - APIs, REST y buenas prácticas de diseño

Esta sección te enseña qué es una API, qué es REST, y cómo diseñar una API que sea intuitiva, predecible y mantenible.

---

## ¿Qué es una API?

**API** significa *Application Programming Interface* (Interfaz de Programación de Aplicaciones). Es un **contrato** que define cómo dos programas pueden comunicarse entre sí.

Una API especifica:
- Qué operaciones están disponibles
- Cómo pedirlas (formato de la petición)
- Qué esperar como resultado (formato de la respuesta)
- Qué errores pueden ocurrir

### La analogía del menú de restaurante

Imagina un restaurante:

- El **cliente** (tú) no entra a la cocina a preparar su comida directamente
- El **menú** define qué puedes pedir, cómo pedirlo (nombre del plato) y qué recibirás
- El **mozo** actúa como intermediario entre el cliente y la cocina
- La **cocina** (el servidor) procesa el pedido y devuelve el resultado

La API es el menú: define las opciones disponibles sin exponer cómo se prepara cada plato internamente. Puedes cambiar completamente la receta (la implementación) sin que el cliente note la diferencia, siempre que el resultado en el plato (la respuesta) sea el mismo.

### Tipos de APIs

| Tipo | Descripción | Ejemplo |
|---|---|---|
| **API Web / HTTP** | Se accede por HTTP. Es la más común en sistemas modernos. | La API de GitHub, Twitter, Google Maps |
| **API de biblioteca** | Funciones que ofrece una librería para ser usadas en el mismo proceso | `java.util.List`, el SDK de AWS |
| **API del sistema operativo** | Interfaz para acceder a recursos del SO | Llamadas del sistema en Linux |

> **En este curso**, cuando decimos "API", nos referimos siempre a una **API Web HTTP**: un servidor que expone URLs y responde peticiones HTTP.

---

## ¿Qué es REST?

**REST** (Representational State Transfer) es un **estilo arquitectónico** para diseñar APIs web. No es un protocolo ni un estándar; es un conjunto de principios y restricciones propuesto por Roy Fielding en su tesis doctoral del año 2000.

Una API que sigue los principios de REST se llama **API RESTful** o simplemente **API REST**.

> **Importante:** REST usa HTTP, pero no toda API que usa HTTP es REST. Una API es REST cuando sigue los principios de Fielding, no simplemente porque use el protocolo.

---

## Los 6 principios (restricciones) de REST

Fielding definió seis restricciones que una arquitectura debe cumplir para ser considerada REST. Vamos a verlas con ejemplos prácticos.

### 1. Interfaz uniforme (Uniform Interface)

Este es el principio más importante. La interfaz entre cliente y servidor debe ser uniforme y consistente. Se descompone en cuatro sub-restricciones:

**a) Identificación de recursos:** cada recurso tiene una URL única que lo identifica.
```
/usuarios/42        → el usuario con ID 42
/productos/15       → el producto con ID 15
/pedidos/7/items    → los ítems del pedido 7
```

**b) Manipulación a través de representaciones:** el cliente interactúa con los recursos a través de representaciones (JSON, XML), no con los datos internos del servidor.

**c) Mensajes auto-descriptivos:** cada mensaje contiene toda la información necesaria para procesarlo (método HTTP, cabeceras, cuerpo con tipo de contenido declarado).

**d) HATEOAS:** Hypermedia As The Engine Of Application State. Las respuestas incluyen enlaces a acciones relacionadas. Es el principio más avanzado y el menos implementado en la práctica.

### 2. Sin estado (Stateless)

Ya lo viste en la lección anterior: cada petición debe contener toda la información necesaria para ser procesada. El servidor no guarda estado de sesión entre peticiones.

```http
GET /pedidos/123 HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

El token de autorización viaja en CADA petición. El servidor no "recuerda" que ya te identificaste en la petición anterior.

**Consecuencia:** la escalabilidad horizontal es trivial. Puedes agregar más servidores porque ninguno guarda estado; cualquier servidor puede atender cualquier petición.

### 3. Caché (Cacheable)

Las respuestas deben indicar si pueden ser cacheadas o no. Si una respuesta puede cachearse, el cliente puede reutilizarla sin hacer una nueva petición al servidor.

```http
HTTP/1.1 200 OK
Cache-Control: max-age=3600
```

Esto le dice al cliente que puede guardar esta respuesta y reutilizarla durante 1 hora sin volver a pedirla.

**Beneficio:** reduce la carga del servidor y mejora el rendimiento del cliente.

### 4. Sistema de capas (Layered System)

El cliente no necesita saber si está hablando directamente con el servidor final o con un intermediario (proxy, load balancer, CDN, gateway). Cada capa solo conoce la capa inmediatamente adyacente.

```
[Cliente] → [CDN / Cache] → [Load Balancer] → [Servidor A]
                                             → [Servidor B]
```

El cliente envía sus peticiones al mismo endpoint sin importar cuántas capas hay en el medio. Esto permite agregar infraestructura sin cambiar el contrato.

### 5. Code on Demand (opcional)

El servidor puede enviar código ejecutable al cliente (ej: JavaScript). Esta es la única restricción opcional de REST. En la práctica, cuando accedes a un sitio web, el servidor envía HTML, CSS y JavaScript que el navegador ejecuta.

### 6. Cliente-Servidor (Client-Server)

El cliente y el servidor deben estar separados y comunicarse solo a través de la interfaz. El cliente no conoce la implementación interna del servidor; el servidor no conoce la implementación interna del cliente.

**Consecuencia:** pueden evolucionar de forma independiente.

---

## REST en la práctica: diseñando una API

Los principios de Fielding son abstractos. En la práctica diaria, "diseñar una API REST" significa tomar decisiones concretas sobre URLs, métodos y respuestas. Estas son las convenciones más importantes.

---

### Regla 1: Los recursos son sustantivos, no verbos

La URL identifica **qué** se está manipulando, no **qué acción** se realiza. La acción la indica el método HTTP.

```
❌ Mal diseño (verbos en la URL)
GET  /getUsuarios
POST /crearUsuario
POST /eliminarUsuario/42
GET  /buscarProductosPorCategoria?cat=ropa

✅ Buen diseño (sustantivos en la URL)
GET    /usuarios
POST   /usuarios
DELETE /usuarios/42
GET    /productos?categoria=ropa
```

> Si sientes la necesidad de poner un verbo en la URL, casi siempre es señal de que el método HTTP no está siendo usado correctamente.

### Regla 2: Usa sustantivos en plural para las colecciones

Las URLs de colecciones (todos los elementos de un tipo) usan el plural. Las URLs de recursos individuales identifican el elemento con un ID.

```
/usuarios          → colección de todos los usuarios
/usuarios/42       → el usuario con ID 42

/productos         → colección de todos los productos
/productos/15      → el producto con ID 15
```

### Regla 3: Jerarquía para recursos relacionados

Cuando un recurso pertenece a otro, la URL refleja esa jerarquía:

```
/pedidos/7/items         → todos los ítems del pedido 7
/pedidos/7/items/3       → el ítem 3 del pedido 7
/usuarios/42/direcciones → las direcciones del usuario 42
```

**Cuidado:** no anides más de 2-3 niveles. Las URLs demasiado largas se vuelven difíciles de manejar.

```
❌ Demasiada jerarquía
/paises/1/regiones/5/comunas/22/direcciones/8/usuarios
```

Si necesitas más contexto, usa query parameters o rediseña el modelo de datos.

### Regla 4: Los métodos HTTP definen la operación

Aplica consistentemente la semántica de los métodos:

| Operación | Método | URL | Respuesta |
|---|---|---|---|
| Listar todos | `GET` | `/usuarios` | `200 OK` + array |
| Ver uno | `GET` | `/usuarios/42` | `200 OK` + objeto |
| Crear | `POST` | `/usuarios` | `201 Created` + objeto creado |
| Reemplazar completo | `PUT` | `/usuarios/42` | `200 OK` + objeto actualizado |
| Actualizar parcial | `PATCH` | `/usuarios/42` | `200 OK` + objeto actualizado |
| Eliminar | `DELETE` | `/usuarios/42` | `204 No Content` |

### Regla 5: Usa los códigos de estado correctamente

No devuelvas siempre `200 OK`. El código de estado es parte del contrato.

```
❌ Mal: devolver 200 con mensaje de error en el cuerpo
HTTP/1.1 200 OK
{ "status": "error", "message": "Usuario no encontrado" }

✅ Bien: usar el código correcto
HTTP/1.1 404 Not Found
{ "error": "Usuario no encontrado" }
```

El cliente (y las herramientas de monitoreo) toman decisiones basadas en el código de estado. Si siempre devuelves `200`, esas herramientas no pueden distinguir éxito de error.

### Regla 6: Consistencia en los nombres

Elige una convención y aplícala en toda la API:

| Aspecto | Opciones | Recomendación |
|---|---|---|
| Caso de URLs | `camelCase`, `kebab-case`, `snake_case` | `kebab-case` (ej: `/mis-pedidos`) |
| Caso de campos JSON | `camelCase`, `snake_case` | `camelCase` en Java/JS (ej: `fechaCreacion`) |
| Idioma | Español, inglés | Elige uno y no lo mezcles |

```
❌ Inconsistente
/usuarios/{userId}/mis_pedidos
{ "fecha_Creacion": "2026-03-19", "Total": 15000 }

✅ Consistente
/usuarios/{userId}/pedidos
{ "fechaCreacion": "2026-03-19", "total": 15000 }
```

### Regla 7: Versionado de la API

Cuando evoluciona una API, hay que gestionar los cambios sin romper a los clientes que ya la usan. El versionado es la estrategia para esto.

Estrategias comunes:

**a) Versión en la URL (más común y explícita):**
```
/v1/usuarios
/v2/usuarios
```

**b) Versión en la cabecera:**
```http
GET /usuarios HTTP/1.1
Accept: application/vnd.miapp.v2+json
```

**c) Versión como query parameter:**
```
/usuarios?version=2
```

> **Recomendación para este curso:** usa versión en la URL (`/v1/...`). Es la más visible, la más fácil de entender y la más fácil de probar.

### Regla 8: Respuestas de error consistentes

Define una estructura de error uniforme para toda la API y úsala siempre:

```json
{
  "timestamp": "2026-03-19T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "No existe un usuario con ID 42",
  "path": "/v1/usuarios/42"
}
```

Si tu API siempre devuelve errores en el mismo formato, el frontend puede manejarlos de forma genérica sin casos especiales.

### Regla 9: Filtrado, paginación y ordenamiento

Para colecciones grandes, no devuelvas todos los registros en una sola respuesta. Usa query parameters:

```
/productos?categoria=ropa                  → filtrar
/productos?pagina=2&tamano=20              → paginar
/productos?orden=precio&direccion=asc      → ordenar
/productos?categoria=ropa&pagina=1&orden=precio  → combinar
```

La respuesta debe incluir metadata sobre la paginación:

```json
{
  "datos": [{...}, {...}],
  "pagina": 2,
  "tamano": 20,
  "total": 347,
  "paginas": 18
}
```

---

## REST vs otras alternativas

REST no es la única forma de diseñar APIs. Existen otras que vale conocer:

| Tecnología | Descripción | Cuándo usarla |
|---|---|---|
| **REST** | Basada en HTTP y recursos. Simple y ampliamente soportada. | La mayoría de los casos: APIs públicas, servicios web generales |
| **GraphQL** | El cliente define exactamente qué datos quiere. Un solo endpoint. | Cuando el cliente necesita mucha flexibilidad en los datos (ej: apps móviles con datos variables) |
| **gRPC** | Basado en Protocol Buffers (binario, eficiente). Tipado estricto. | Comunicación interna entre microservicios que necesitan alto rendimiento |
| **WebSocket** | Conexión bidireccional persistente. El servidor puede enviar datos sin que el cliente pregunte. | Aplicaciones en tiempo real: chat, notificaciones en vivo, juegos |

> **Para este curso:** REST es suficiente para todo lo que haremos. GraphQL y gRPC son temas avanzados que encontrarás en proyectos más complejos.

---

## El Modelo de Madurez de Richardson

Leonard Richardson propuso un modelo para medir qué tan "RESTful" es una API, con cuatro niveles:

| Nivel | Nombre | Descripción |
|---|---|---|
| **Nivel 0** | The Swamp of POX | Un solo endpoint, todo vía POST. (Ej: SOAP, XML-RPC) |
| **Nivel 1** | Resources | Múltiples URLs, una por recurso. Pero un solo método HTTP para todo. |
| **Nivel 2** | HTTP Verbs | Usa correctamente los métodos HTTP y los códigos de estado. |
| **Nivel 3** | Hypermedia Controls (HATEOAS) | Las respuestas incluyen enlaces a acciones relacionadas. |

La mayoría de las APIs "REST" reales están en el **Nivel 2**. El Nivel 3 (HATEOAS) es el verdadero REST según Fielding, pero rara vez se implementa completamente en la práctica.

> **En este curso:** construirás APIs de Nivel 2. Es el estándar de la industria. Conocer el Nivel 3 es importante conceptualmente, pero no es el foco práctico.

---

## Resumen: checklist de diseño de una API REST

Antes de implementar un endpoint, hazte estas preguntas:

- [ ] ¿La URL usa sustantivos (no verbos)?
- [ ] ¿Las colecciones están en plural?
- [ ] ¿El método HTTP refleja correctamente la operación?
- [ ] ¿El código de estado HTTP refleja correctamente el resultado?
- [ ] ¿Los nombres de campos son consistentes con el resto de la API?
- [ ] ¿Las respuestas de error tienen el mismo formato en toda la API?
- [ ] ¿Las colecciones grandes están paginadas?
- [ ] ¿La URL incluye un número de versión?

Si puedes marcar todos estos ítems, tienes una API bien diseñada.

