# Lección 01 - Request, Response, Métodos y Códigos HTTP

Ahora que sabes qué es HTTP y cómo funciona la Web, es hora de ver el contenido de las peticiones y respuestas. Esta sección es especialmente importante porque vas a leer y escribir estas estructuras constantemente cuando trabajes con APIs.

---

## Anatomía de una petición HTTP (Request)

Una petición HTTP tiene siempre la misma estructura: tres partes en un orden específico, separadas por líneas en blanco.

```http
POST /usuarios HTTP/1.1
Host: api.ejemplo.com
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Accept: application/json

{
  "nombre": "Ana Torres",
  "email": "ana@ejemplo.com"
}
```

### Parte 1: Línea de inicio (Request Line)

```
POST /usuarios HTTP/1.1
```

Contiene exactamente tres elementos:

| Elemento | Qué es | En el ejemplo |
|---|---|---|
| Método | Qué tipo de operación se solicita | `POST` |
| Ruta (path) | Qué recurso se solicita | `/usuarios` |
| Versión HTTP | Qué versión del protocolo se usa | `HTTP/1.1` |

Esta línea es **obligatoria** en toda petición HTTP. Sin ella, el servidor no sabe qué se le está pidiendo.

### Parte 2: Cabeceras (Headers)

```
Host: api.ejemplo.com
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Accept: application/json
```

Las cabeceras son **metadatos** de la petición: información adicional que le da contexto al servidor sobre quién hace la petición, qué formato tiene el cuerpo, qué formato se espera en la respuesta, etc.

Son pares `Clave: Valor`, uno por línea. Algunas cabeceras comunes:

| Cabecera | Qué informa |
|---|---|
| `Host` | El dominio del servidor al que va la petición. **Obligatoria en HTTP/1.1** |
| `Content-Type` | El formato del cuerpo que se envía (ej: `application/json`, `text/plain`) |
| `Accept` | El formato que el cliente puede recibir en la respuesta |
| `Authorization` | Credenciales o token de autenticación |
| `User-Agent` | Información del cliente (navegador, sistema operativo) |
| `Content-Length` | Tamaño en bytes del cuerpo de la petición |

El cliente puede incluir tantas cabeceras como necesite. El servidor también puede ignorar las que no entiende.

### Parte 3: Cuerpo (Body)

```json
{
  "nombre": "Ana Torres",
  "email": "ana@ejemplo.com"
}
```

El cuerpo contiene **los datos que el cliente envía al servidor**. Es la "carga útil" de la petición. No siempre existe: las peticiones `GET` y `DELETE` generalmente no tienen cuerpo, porque solo piden información o solicitan eliminar algo identificado por la URL, sin necesidad de enviar datos adicionales.

Cuando el cuerpo existe, la cabecera `Content-Type` le indica al servidor cómo interpretarlo.

> **Separador importante:** entre las cabeceras y el cuerpo siempre hay **una línea en blanco**. Esta línea vacía es parte del protocolo; sin ella el servidor no sabe dónde terminan las cabeceras y dónde empieza el cuerpo.

---

## Anatomía de una respuesta HTTP (Response)

La respuesta tiene la misma estructura de tres partes, con una diferencia: la primera línea no es una línea de método sino una **línea de estado**.

```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /usuarios/98

{
  "id": 98,
  "nombre": "Ana Torres",
  "email": "ana@ejemplo.com",
  "creadoEn": "2026-03-19T10:30:00Z"
}
```

### Parte 1: Línea de estado (Status Line)

```
HTTP/1.1 201 Created
```

| Elemento | Qué es | En el ejemplo |
|---|---|---|
| Versión HTTP | Qué versión usa el servidor | `HTTP/1.1` |
| Código de estado | Un número que resume el resultado | `201` |
| Texto de estado | Una descripción legible del código | `Created` |

### Parte 2: Cabeceras de respuesta

```
Content-Type: application/json
Location: /usuarios/98
```

El servidor también usa cabeceras para dar contexto sobre la respuesta:

| Cabecera | Qué informa |
|---|---|
| `Content-Type` | El formato del cuerpo de la respuesta |
| `Content-Length` | Tamaño del cuerpo en bytes |
| `Location` | La URL del recurso recién creado (útil en respuestas `201 Created`) |
| `Cache-Control` | Instrucciones sobre caching |
| `Set-Cookie` | Solicita al cliente que guarde una cookie |

### Parte 3: Cuerpo de la respuesta

```json
{
  "id": 98,
  "nombre": "Ana Torres",
  "email": "ana@ejemplo.com",
  "creadoEn": "2026-03-19T10:30:00Z"
}
```

El cuerpo contiene la **respuesta real**: el recurso solicitado, el mensaje de error, la confirmación de la operación, etc. En las APIs modernas el formato más común es **JSON** (JavaScript Object Notation).

---

## Los métodos HTTP

Los métodos HTTP (también llamados "verbos HTTP") indican **qué tipo de operación** quiere realizar el cliente. Cada método tiene una semántica específica y convenciones sobre si puede tener cuerpo, si es seguro y si es idempotente.

### Los métodos principales

| Método | Operación | ¿Tiene cuerpo? | Uso típico |
|---|---|---|---|
| `GET` | Obtener un recurso | No | Leer datos. No modifica nada. |
| `POST` | Crear un recurso | Sí | Crear un nuevo registro |
| `PUT` | Reemplazar un recurso completo | Sí | Actualizar completamente un recurso existente |
| `PATCH` | Modificar parcialmente un recurso | Sí | Actualizar solo algunos campos |
| `DELETE` | Eliminar un recurso | No (usualmente) | Borrar un registro |

### ¿Qué significa "seguro" e "idempotente"?

Dos propiedades importantes de los métodos HTTP:

**Seguro (safe):** el método no modifica el estado del servidor. Solo *lee*. `GET` es seguro. `POST` no lo es.

**Idempotente (idempotent):** hacer la misma petición una vez o diez veces produce el mismo resultado. `PUT` es idempotente: si envías los mismos datos cinco veces, el recurso queda igual que si lo enviaste una. `POST` no es idempotente: enviarlo cinco veces crea cinco recursos distintos.

| Método | ¿Seguro? | ¿Idempotente? |
|---|---|---|
| `GET` | ✅ Sí | ✅ Sí |
| `POST` | ❌ No | ❌ No |
| `PUT` | ❌ No | ✅ Sí |
| `PATCH` | ❌ No | ❌ No (puede serlo según implementación) |
| `DELETE` | ❌ No | ✅ Sí (eliminar algo que ya no existe sigue siendo el mismo resultado) |

> Estas propiedades no las hace cumplir el protocolo automáticamente. Las cumple o viola **el código que escribes**. Si haces un `GET` que modifica la base de datos, estás violando la semántica del protocolo aunque técnicamente funcione.

### Métodos menos comunes pero útiles

| Método | Uso |
|---|---|
| `HEAD` | Igual que `GET` pero sin cuerpo en la respuesta. Útil para verificar si un recurso existe o revisar sus cabeceras |
| `OPTIONS` | Pregunta qué métodos acepta el servidor para una URL. Lo usan los navegadores en peticiones CORS |

---

## Códigos de estado HTTP

Los códigos de estado son números de tres dígitos que el servidor incluye en cada respuesta para indicar **qué pasó con la petición**. El primer dígito define la categoría.

### Las cinco categorías

| Rango | Categoría | Significado general |
|---|---|---|
| `1xx` | Informativos | La petición fue recibida; el proceso continúa |
| `2xx` | Éxito | La petición fue recibida, entendida y procesada correctamente |
| `3xx` | Redirección | Se necesita una acción adicional para completar la petición |
| `4xx` | Error del cliente | La petición tiene un problema del lado del cliente |
| `5xx` | Error del servidor | El servidor falló al procesar una petición válida |

### Los códigos más importantes para APIs

#### Éxito (2xx)

| Código | Texto | Cuándo usarlo |
|---|---|---|
| `200 OK` | OK | La petición fue exitosa. El cuerpo contiene el resultado. |
| `201 Created` | Created | Se creó un recurso nuevo exitosamente. Incluir cabecera `Location` con la URL del nuevo recurso. |
| `204 No Content` | No Content | La operación fue exitosa pero no hay contenido que devolver (ej: DELETE exitoso). |

#### Error del cliente (4xx)

| Código | Texto | Cuándo usarlo |
|---|---|---|
| `400 Bad Request` | Bad Request | La petición tiene errores de formato o datos inválidos. |
| `401 Unauthorized` | Unauthorized | El cliente no está autenticado. Falta el token o es inválido. |
| `403 Forbidden` | Forbidden | El cliente está autenticado pero no tiene permiso para esta operación. |
| `404 Not Found` | Not Found | El recurso solicitado no existe. |
| `405 Method Not Allowed` | Method Not Allowed | El método HTTP no está permitido para esta URL. |
| `409 Conflict` | Conflict | Hay un conflicto con el estado actual del recurso (ej: email duplicado). |
| `422 Unprocessable Entity` | Unprocessable Entity | Los datos tienen formato correcto pero fallan la validación de negocio. |

#### Error del servidor (5xx)

| Código | Texto | Cuándo usarlo |
|---|---|---|
| `500 Internal Server Error` | Internal Server Error | Error inesperado en el servidor. Nunca debería llegar al cliente en producción. |
| `502 Bad Gateway` | Bad Gateway | El servidor actuó como proxy y recibió una respuesta inválida del servidor de origen. |
| `503 Service Unavailable` | Service Unavailable | El servidor está temporalmente no disponible (mantenimiento o sobrecarga). |

### Cómo leer un código de estado

Un truco simple: si el primer dígito es `2`, algo salió bien. Si es `4`, el problema está en la petición que envió el cliente. Si es `5`, el servidor tiene un problema.

```
200 → éxito, hay contenido
201 → éxito, se creó algo
204 → éxito, no hay contenido

400 → la petición tiene errores
401 → no sé quién eres (falta autenticación)
403 → sé quién eres, pero no tienes permiso
404 → lo que buscas no existe
405 → ese método no está permitido aquí

500 → me rompí por dentro (error del servidor)
```

---

## El flujo completo de una interacción HTTP

Veamos un ejemplo concreto de principio a fin: un cliente que consulta el perfil de un usuario.

**Petición:**
```http
GET /usuarios/42 HTTP/1.1
Host: api.ejemplo.com
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Respuesta exitosa (el usuario existe):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 42,
  "nombre": "Carlos Martínez",
  "email": "carlos@ejemplo.com"
}
```

**Respuesta si el usuario no existe:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "error": "Usuario no encontrado",
  "codigo": 404
}
```

**Respuesta si el token es inválido:**
```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json
WWW-Authenticate: Bearer realm="api.ejemplo.com"

{
  "error": "Token inválido o expirado"
}
```

Esta interacción, con sus variantes, es el patrón que repetirás cientos de veces al construir y consumir APIs.

---

## Herramientas para inspeccionar HTTP

Para trabajar con APIs necesitas herramientas que te permitan construir y enviar peticiones HTTP arbitrarias (no solo `GET` desde el navegador):

| Herramienta | Tipo | Cuándo usarla |
|---|---|---|
| **Postman** | GUI de escritorio | Probar APIs visualmente. La más usada en equipos de desarrollo. |
| **Insomnia** | GUI de escritorio | Alternativa a Postman, más liviana. |
| **curl** | Línea de comandos | Probar APIs desde la terminal. Disponible en cualquier sistema. |
| **DevTools del navegador** | Integrado en el navegador | Ver las peticiones que hace una página web (pestaña "Network") |

> **Recomendación:** instala Postman ahora. Lo usarás desde la lección 03 en adelante para probar todos los endpoints que construyas.

Ejemplo de una petición con `curl`:
```bash
curl -X GET "http://localhost:8080/greetings" \
     -H "Accept: text/plain"
```

Este comando hace exactamente lo mismo que escribir `http://localhost:8080/greetings` en el navegador, pero desde la terminal y con control total sobre las cabeceras.

