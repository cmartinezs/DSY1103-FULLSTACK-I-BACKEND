# Lección 05 - Por qué hacemos las cosas así: decisiones de diseño explicadas

Esta sección no es un listado de reglas. Es la explicación del razonamiento detrás de cada decisión que tomamos al agregar el `POST` a nuestra API. Un buen desarrollador no solo sabe *qué* hacer, sino *por qué* lo hace así y no de otra manera.

---

## Decisión 1: `201 Created` en lugar de `200 OK`

Esta es la decisión más visible de la lección y la que más errores comete la gente al principio.

El protocolo HTTP define los códigos de estado con precisión. No son sugerencias: son un contrato entre el servidor y el cliente. La diferencia entre `200` y `201` no es cosmética:

| Código | Nombre | Significado |
|---|---|---|
| `200 OK` | OK | La petición fue exitosa. Se usa para consultas (`GET`) o actualizaciones genéricas. |
| `201 Created` | Created | La petición fue exitosa **y** como resultado se creó un nuevo recurso. |

Cuando tu `POST /tickets` devuelve `200 OK`, le estás diciendo al cliente: "todo salió bien, pero no sé bien qué pasó". Cuando devuelves `201 Created`, le estás diciendo: "todo salió bien y se creó exactamente un recurso nuevo".

Los clientes automatizados (otras APIs, aplicaciones frontend, scripts) toman decisiones basadas en el código de estado. Un frontend que espera `201` para mostrar un mensaje de "recurso creado" no funcionará correctamente si recibe `200`.

> **La regla práctica:**
> - Operación que solo consulta → `200 OK`
> - Operación que crea un recurso nuevo → `201 Created`
> - Operación que actualiza un recurso existente → `200 OK`
> - Operación que elimina → `204 No Content`

---

## Decisión 2: el servidor asigna el ID, no el cliente

En el endpoint `POST /tickets`, el cliente manda esto:

```json
{
  "title": "Bug en login",
  "description": "...",
  "status": "NEW"
}
```

Y el servidor responde con:

```json
{
  "id": 3,
  "title": "Bug en login",
  "description": "...",
  "status": "NEW"
}
```

El cliente no mandó el `id`. El servidor lo asignó. Esto no es accidental: es una decisión de diseño deliberada.

**¿Por qué no dejar que el cliente elija su propio ID?**

Imagina que dos clientes (dos usuarios distintos usando la aplicación al mismo tiempo) envían simultáneamente un ticket con `"id": 5`. ¿Cuál de los dos tiene razón? ¿Quién gana? El sistema quedaría en un estado inconsistente.

El servidor tiene una visión centralizada del estado: sabe qué IDs ya existen. Por eso la autoridad para generar IDs siempre recae en el servidor, nunca en el cliente.

**¿Qué pasa si el cliente manda un `id` en el JSON de todas formas?**

Jackson lo leerá y lo asignará al campo `id` del objeto `Ticket`. Pero inmediatamente después, el `Repository` lo sobreescribirá con `ticket.setId(currentId++)`. El valor que mandó el cliente se descarta. El servidor siempre tiene la última palabra sobre el ID.

---

## Decisión 3: ID incremental manual en lugar de UUID

El raw material de esta lección menciona que no usamos UUID aún. Aquí está el razonamiento completo.

Un UUID se ve así: `550e8400-e29b-41d4-a716-446655440000`. Es un identificador globalmente único, generado de forma aleatoria, que prácticamente nunca colisiona con otro UUID aunque lo generes en otra máquina.

¿Por qué no usarlo desde el principio?

| Criterio | ID incremental (`Long`) | UUID (`String`) |
|---|---|---|
| Legibilidad en pruebas | Fácil: `1`, `2`, `3` | Difícil: `550e8400-...` |
| Complejidad de implementación | Mínima | Requiere `UUID.randomUUID()` y tipo `String` |
| URLs amigables | `GET /tickets/3` | `GET /tickets/550e8400-...` |
| Valor pedagógico en esta etapa | Alto: foco en el flujo | Bajo: distrae con detalles |
| Cuándo tiene sentido | APIs internas con BD relacional | APIs públicas, microservicios distribuidos |

La regla es sencilla: no agregues complejidad antes de necesitarla. En esta etapa, el contador incremental es la herramienta correcta. Cuando conectemos una base de datos real, JPA o PostgreSQL manejarán la generación de IDs automáticamente, y el contador manual desaparecerá.

---

## Decisión 4: `@NoArgsConstructor` en el modelo

Antes de esta lección, `Ticket` solo tenía `@AllArgsConstructor`. Ahora agregamos `@NoArgsConstructor`. ¿Por qué?

Cuando Spring recibe una petición con `@RequestBody`, le pide a Jackson que convierta el JSON en un objeto Java. Jackson hace esto en dos pasos:

1. Crea una instancia vacía del objeto: `new Ticket()` → necesita constructor sin argumentos
2. Llama a cada setter para asignar los valores del JSON: `ticket.setTitle("Bug en login")` → necesita setters (`@Setter`)

Sin `@NoArgsConstructor`, el paso 1 falla y Spring devuelve un error `400 Bad Request` con un mensaje confuso sobre deserialización. El error real no es que el JSON sea inválido: es que Jackson no puede construir el objeto.

> **¿No rompe esto algo?**
> No. `@NoArgsConstructor` y `@AllArgsConstructor` pueden coexistir sin problema. Java permite múltiples constructores con diferentes firmas. El código existente (los tickets semilla con `new Ticket(1L, "Ticket 1", ...)`) sigue usando `@AllArgsConstructor`. El nuevo flujo de `@RequestBody` usa `@NoArgsConstructor`.

---

## Decisión 5: `ResponseEntity` como estándar en el Controller

En la lección anterior, el `TicketController` devolvía `List<Ticket>` directamente:

```java
@GetMapping
public List<Ticket> getAllTickets() {
    return this.service.getTickets();
}
```

En esta lección, el nuevo endpoint devuelve `ResponseEntity<Ticket>`:

```java
@PostMapping
public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
    Ticket saved = service.create(ticket);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}
```

¿Por qué el cambio? Porque `ResponseEntity` nos da control completo sobre tres aspectos de la respuesta HTTP:

1. **El código de estado**: `200`, `201`, `404`, `400`, etc.
2. **Los headers**: `Content-Type`, `Location`, cabeceras personalizadas
3. **El body**: el objeto serializado como JSON

Devolver el objeto directamente le delega ese control a Spring, que simplemente asume `200 OK` siempre que no haya excepción. Eso es conveniente, pero nos quita expresividad.

A partir de esta lección, todos los endpoints nuevos usarán `ResponseEntity`. El `GET /tickets` existente se migra en la próxima iteración.

> **Criterio de calidad que te acompaña en el curso:**
> Poco alcance, buena forma. Una API con dos endpoints perfectamente estructurados es mejor que cinco endpoints que no comunican correctamente su estado HTTP.

---

## Decisión 6: el body de la respuesta incluye el objeto creado completo

Cuando el `POST` es exitoso, la respuesta incluye el ticket tal como quedó guardado:

```json
{
  "id": 3,
  "title": "Bug en login",
  "description": "...",
  "status": "NEW"
}
```

Hay APIs que responden al `POST` con el body vacío y solo el código `201`. Técnicamente es válido. Pero incluir el objeto creado en la respuesta tiene una ventaja concreta para el cliente: **no necesita hacer un GET adicional** para obtener el ID que le asignó el servidor.

Si el cliente necesita saber el ID del ticket que acaba de crear (por ejemplo, para redirigir al usuario a la pantalla de detalle), la respuesta ya lo tiene. Sin una segunda petición. Sin estado compartido. Sin condiciones de carrera.

Esta es la práctica recomendada en APIs REST modernas y es la que usaremos a lo largo del curso.

