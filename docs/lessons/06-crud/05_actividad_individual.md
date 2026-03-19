# Lección 06 - Actividad individual: CRUD de categorías

## Contexto

En la lección anterior (POST) ya tenías una actividad sobre `Category`. En esta lección vas a completar ese trabajo implementando el **CRUD completo** para ese mismo recurso.

Si en la lección anterior implementaste `Category` con sus propios atributos, adapta la actividad a lo que ya tienes. Si no la implementaste, esta es tu oportunidad.

---

## ¿Qué vas a construir?

Una API REST para gestionar categorías de tickets. Cada categoría agrupa tickets por tipo de problema: `"Hardware"`, `"Software"`, `"Acceso"`, etc.

### Atributos mínimos del recurso `Category`

| Campo         | Tipo     | Descripción                                         |
|---------------|----------|-----------------------------------------------------|
| `id`          | `Long`   | Identificador único asignado por el servidor        |
| `name`        | `String` | Nombre de la categoría (ej: `"Hardware"`)           |
| `description` | `String` | Descripción breve del tipo de problemas que agrupa  |

---

## Endpoints requeridos

| Método | Endpoint           | Descripción                              | Código exitoso | Código de error |
|--------|--------------------|------------------------------------------|----------------|-----------------|
| GET    | `/categories`      | Devuelve todas las categorías            | 200 OK         | —               |
| GET    | `/categories/{id}` | Devuelve una categoría por ID            | 200 OK         | 404 Not Found   |
| POST   | `/categories`      | Crea una nueva categoría                 | 201 Created    | 409 Conflict    |
| PUT    | `/categories/{id}` | Actualiza una categoría existente        | 200 OK         | 404 Not Found   |
| DELETE | `/categories/{id}` | Elimina una categoría existente          | 204 No Content | 404 Not Found   |

---

## Estructura de archivos esperada

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   ├── TicketController.java      (ya existe)
│   └── CategoryController.java    (nuevo)
├── model/
│   ├── Ticket.java                (ya existe)
│   └── Category.java              (nuevo)
├── respository/
│   ├── TicketRepository.java      (ya existe)
│   └── CategoryRepository.java    (nuevo)
├── service/
│   ├── TicketService.java         (ya existe)
│   └── CategoryService.java       (nuevo)
└── TicketsApplication.java
```

---

## Reglas de negocio mínimas

Implementa estas reglas **en el `Service`**, igual que hiciste con los tickets:

1. **No se pueden crear dos categorías con el mismo nombre** (comparación sin distinguir mayúsculas/minúsculas) → `409 Conflict`
2. **El ID lo asigna el servidor**, nunca el cliente
3. **Los datos semilla:** crea al menos 2 categorías de ejemplo en el constructor del `Repository`

---

## Guía de implementación

Sigue exactamente el mismo patrón que usaste para `Ticket`:

### 1. `Category.java`
- Usa Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Campos: `id`, `name`, `description`

### 2. `CategoryRepository.java`
- Lista en memoria como almacenamiento
- Contador incremental para los IDs
- Métodos:
  - `getAll()` → `List<Category>`
  - `findById(Long id)` → `Optional<Category>` (usa stream + `findFirst()`, sin `null`)
  - `existsByName(String name)` → `boolean`
  - `save(Category category)` → `Category`
  - `update(Long id, Category category)` → `Optional<Category>` (reutiliza `findById()` + `ifPresent()`)
  - `delete(Long id)` → `boolean` (usa `removeIf()`)

### 3. `CategoryService.java`
- Validación de nombre duplicado en `create()`
- `findById()` devuelve `Optional<Category>` sin llamar a `.get()` ni `.orElse(null)`
- `update()` devuelve `Optional<Category>` y delega al Repository
- `delete()` devuelve `boolean` y delega al Repository

### 4. `CategoryController.java`
- `@RestController` + `@RequestMapping("/categories")`
- Un método por endpoint
- `ResponseEntity` en todos los métodos con los códigos correctos
- GET y PUT usan `.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())`
- No hay ningún `null` explícito en el controlador

---

## Ejemplos de prueba

### Crear una categoría

```
POST http://localhost:8080/categories
Content-Type: application/json

{
  "name": "Hardware",
  "description": "Problemas relacionados con componentes físicos"
}
```

Resultado esperado: `201 Created`

```json
{
  "id": 3,
  "name": "Hardware",
  "description": "Problemas relacionados con componentes físicos"
}
```

### Actualizar una categoría

```
PUT http://localhost:8080/categories/1
Content-Type: application/json

{
  "name": "Hardware y periféricos",
  "description": "Problemas con hardware, teclados, monitores y otros periféricos"
}
```

Resultado esperado: `200 OK` con la categoría actualizada.

### Eliminar una categoría

```
DELETE http://localhost:8080/categories/2
```

Resultado esperado: `204 No Content`

---

## Desafío opcional

Si terminaste antes, agrega una validación para impedir:

- Nombres vacíos o en blanco → `400 Bad Request`
- Descripciones de menos de 10 caracteres → `400 Bad Request`

Impleméntalas como `if` simples en el `Service`, lanzando `IllegalArgumentException`. El `Controller` las captura y devuelve `ResponseEntity.badRequest().build()`.

---

## Criterios de evaluación

| Criterio                                                         | Puntaje |
|------------------------------------------------------------------|---------|
| Los 5 endpoints están implementados y responden correctamente    | 40%     |
| Los códigos HTTP son correctos para éxito y error               | 20%     |
| La estructura de capas es correcta (no hay lógica de negocio en el Controller) | 20%     |
| Las URLs siguen las reglas REST (plural, sin verbos)             | 10%     |
| Se probaron los casos negativos (IDs inexistentes, nombre duplicado) | 10%  |

