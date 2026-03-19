# Lección 05 - Actividad individual: recurso `categories`

Ahora es tu turno. Esta actividad replica lo que hiciste con `Ticket` en clase, pero esta vez para un recurso `Category` que crearás desde cero. El objetivo es que apliques el patrón CSR con `POST` de forma autónoma, tomando las mismas decisiones de diseño que aprendiste.

> Si no estuviste en clase, lee primero el tutorial paso a paso (`02_guion_paso_a_paso.md`) y el documento de decisiones de diseño (`03_decisiones_post_y_http.md`) antes de comenzar esta actividad.

---

## ¿Qué vas a construir?

Un recurso `Category` completamente nuevo dentro del mismo proyecto `Tickets`, con la arquitectura por capas que ya conoces. El entregable incluye dos endpoints:

```
GET  /api/categories       → devuelve la lista de categorías (con datos semilla)
POST /api/categories       → recibe una categoría nueva y la guarda
```

Nota el prefijo `/api` en la ruta. A partir de esta actividad empezamos a incorporarlo como práctica profesional para separar semánticamente la API del resto del servidor.

---

## Restricciones de la actividad

| Restricción | Por qué |
|---|---|
| Usar el patrón CSR con paquetes separados | Es el núcleo de la arquitectura que se evalúa |
| Usar `List` para persistencia temporal | No usamos BD todavía |
| El servidor asigna el ID, no el cliente | Regla de diseño REST explicada en clase |
| `POST` debe responder `201 Created` | Semántica correcta de HTTP |
| `GET` debe responder `200 OK` | Semántica correcta de HTTP |
| Usar `ResponseEntity` en ambos endpoints | Estándar que adoptamos a partir de esta lección |
| La URL debe usar el prefijo `/api` | Práctica profesional para identificar la API |

---

## Modelo sugerido

Crea la clase `Category` en el paquete `model`. Una categoría de ticket tiene un identificador, un nombre y una descripción:

```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private String name;
    private String description;
}
```

> **¿Por qué necesitas `@NoArgsConstructor` desde el inicio?**
> Porque este recurso tendrá un endpoint `POST` con `@RequestBody`. Jackson necesita el constructor vacío para deserializar el JSON entrante. Si no lo pones desde el principio, tendrás un `400 Bad Request` confuso cuando pruebes el endpoint.

> **¿Qué significa cada campo?**
> - `id`: identificador único asignado por el servidor
> - `name`: nombre corto de la categoría (por ejemplo, `"Bug"`, `"Feature"`, `"Mejora"`)
> - `description`: explicación más detallada de qué tickets entran en esta categoría

---

## Guía de implementación

Sigue este orden. Cada paso construye sobre el anterior.

### 1. Crea el paquete y la clase `Category`

La clase va en el paquete `model`, junto a `Ticket.java`. Usa las cuatro anotaciones Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`.

### 2. Crea `CategoryRepository`

- Anótala con `@Repository`
- Declara `private List<Category> categories` y `private Long currentId = 3L`
- En el constructor, inicializa la lista con al menos 2 categorías de prueba:
  - `Bug` / `"Problema o error que afecta el funcionamiento esperado"`
  - `Feature` / `"Nueva funcionalidad solicitada por el usuario"`
- Crea el método `getAll()` que retorne la lista completa
- Crea el método `save(Category category)` que asigne el ID, agregue a la lista y retorne la categoría

### 3. Crea `CategoryService`

- Anótala con `@Service`
- Recibe `CategoryRepository` por constructor (inyección de dependencias)
- Crea el método `getCategories()` que llame a `repository.getAll()`
- Crea el método `create(Category category)` que llame a `repository.save(category)`

### 4. Crea `CategoryController`

- Anótalo con `@RestController` y `@RequestMapping("/api/categories")`
- Recibe `CategoryService` por constructor
- Crea el método `getAllCategories()` con `@GetMapping` que retorne `ResponseEntity.ok(service.getCategories())`
- Crea el método `create()` con `@PostMapping` y `@RequestBody Category category` que retorne `ResponseEntity.status(HttpStatus.CREATED).body(service.create(category))`

### 5. Prueba ambos endpoints

**Prueba GET:**

```
GET http://localhost:8080/api/categories
```

Resultado esperado (`200 OK`):

```json
[
  { "id": 1, "name": "Bug", "description": "Problema o error que afecta el funcionamiento esperado" },
  { "id": 2, "name": "Feature", "description": "Nueva funcionalidad solicitada por el usuario" }
]
```

**Prueba POST:**

```
POST http://localhost:8080/api/categories
Content-Type: application/json

{
  "name": "Mejora",
  "description": "Cambio menor que optimiza una funcionalidad existente"
}
```

Resultado esperado (`201 Created`):

```json
{
  "id": 3,
  "name": "Mejora",
  "description": "Cambio menor que optimiza una funcionalidad existente"
}
```

**Prueba de integridad (GET después del POST):**

Después del POST, vuelve a hacer `GET /api/categories`. Deberías ver las 3 categorías: las 2 semilla más la que acabas de crear.

---

## ¿Cómo sé si lo hice bien?

### Logro alto

- Los cuatro paquetes existen con sus clases: `Category`, `CategoryRepository`, `CategoryService`, `CategoryController`
- `GET /api/categories` responde `200 OK` con un arreglo JSON de categorías
- `POST /api/categories` responde `201 Created` con la categoría creada (incluyendo `id`)
- El `id` es asignado por el servidor, nunca viene `null` en la respuesta
- `CategoryController` usa `ResponseEntity` en ambos métodos
- Puedes explicar en voz alta por qué cada clase está en su paquete y por qué `201` en el POST

### Logro medio

- La estructura CSR existe pero algún método está en la capa equivocada (por ejemplo, la asignación de ID en el `Service` o en el `Controller`)
- El POST funciona pero devuelve `200` en lugar de `201`
- El GET funciona pero no usa `ResponseEntity`
- El endpoint responde correctamente pero no puedes justificar las decisiones

### Logro inicial

- El endpoint funciona, pero todo está en el `Controller` sin separación de capas
- La URL contiene verbos (`/crearCategoria`, `/nuevaCategoria`)
- El campo `id` llega `null` en la respuesta (el servidor no lo está asignando)
- No hay datos semilla y el GET devuelve un arreglo vacío

---

## Extensión opcional: si terminas antes

### Opción A: validación manual de campo vacío

Antes de guardar la categoría, verifica que el campo `name` no sea `null` ni una cadena vacía. Si el nombre está vacío, devuelve `400 Bad Request` con un mensaje claro:

```json
{
  "error": "El nombre de la categoría no puede estar vacío"
}
```

Piensa en qué capa va esa validación. ¿En el `Controller`? ¿En el `Service`? ¿En el `Repository`? Justifica tu decisión antes de escribir el código.

### Opción B: buscar categoría por ID

Agrega el endpoint:

```
GET /api/categories/{id}
```

- Si la categoría existe: `200 OK` con el objeto
- Si no existe: `404 Not Found`

Usa `Optional<Category>` en el `Repository` para manejar el caso donde el ID no se encuentra.

### Opción C: prefijo `/api` en Tickets también

Ahora que `CategoryController` usa `/api/categories`, es consistente migrar `TicketController` para que también use `/api/tickets`. Hazlo y verifica que ambos endpoints siguen funcionando.

---

## Antes de entregar: pregúntate esto

1. Si alguien hace `POST /api/categories` con `{ "id": 99, "name": "Test" }`, ¿qué `id` aparece en la respuesta? ¿Por qué?
2. ¿Qué código de estado devuelve tu `POST` cuando todo sale bien? ¿Y tu `GET`?
3. Si mañana necesitas que las categorías se guarden en una base de datos, ¿qué archivo modificarías? ¿Qué archivos **no** necesitarías tocar?

Si las tres respuestas son claras para ti, completaste el objetivo de esta actividad.

