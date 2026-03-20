# Lección 09 - Actividad individual: Map y filtro en categorías

## Contexto

Tu `CategoryRepository` todavía usa `List<Category>`. Esta actividad aplica el mismo patrón de refactorización que usaste con `TicketRepository`.

Adicionalmente, vas a agregar un filtro de búsqueda por nombre al endpoint `GET /categories`.

---

## Parte 1: refactorizar `CategoryRepository` a Map

### Qué debes cambiar

1. **Almacenamiento:** de `List<Category>` a `Map<Long, Category> db = new HashMap<>()`
2. **`currentId`:** empieza en `1L`
3. **Constructor:** datos semilla con `db.put(currentId++, category)`
4. **`getAll()`:** devuelve `new ArrayList<>(db.values())` ordenado por `name`
5. **`findById(Long id)`:** `Optional.ofNullable(db.get(id))`
6. **`save(Category category)`:** `category.setId(currentId); db.put(currentId++, category); return category`
7. **`update(Long id, CategoryRequest request)`:** `findById(id)` (ahora O(1)) + `ifPresent`
8. **`delete(Long id)`:** `db.remove(id) != null`

### Código de referencia

```java
@Repository
public class CategoryRepository {

    private Map<Long, Category> db = new HashMap<>();
    private long currentId = 1L;

    public CategoryRepository() {
        Category c1 = new Category(currentId, "Hardware", "Problemas con equipos físicos");
        db.put(currentId++, c1);

        Category c2 = new Category(currentId, "Software", "Problemas con aplicaciones");
        db.put(currentId++, c2);
    }

    public List<Category> getAll() {
        List<Category> all = new ArrayList<>(db.values());
        all.sort(Comparator.comparing(Category::getName));
        return all;
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    public boolean existsByName(String name) {
        return db.values().stream()
            .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    public Category save(Category category) {
        category.setId(currentId);
        db.put(currentId++, category);
        return category;
    }

    public Optional<Category> update(Long id, CategoryRequest request) {
        Optional<Category> found = findById(id);
        found.ifPresent(category -> {
            category.setName(request.getName());
            category.setDescription(request.getDescription());
        });
        return found;
    }

    public boolean delete(Long id) {
        return db.remove(id) != null;
    }
}
```

---

## Parte 2: agregar filtro por nombre en `GET /categories`

A diferencia de `Ticket` (donde filtramos por estado), en categorías el filtro útil es por **nombre parcial**: encontrar categorías cuyo nombre contenga una palabra clave.

### Lo que vas a implementar

```
GET /categories?name=hard  → devuelve categorías cuyo nombre contiene "hard" (insensible a mayúsculas)
GET /categories             → devuelve todas las categorías
```

### Cambios en `CategoryRepository`

Agrega el método sobrecargado:

```java
public List<Category> getAll(String nameFilter) {
    List<Category> all = new ArrayList<>(db.values());
    all.sort(Comparator.comparing(Category::getName));
    if (nameFilter != null && !nameFilter.isBlank()) {
        return all.stream()
            .filter(c -> c.getName().toLowerCase().contains(nameFilter.toLowerCase()))
            .collect(Collectors.toList());
    }
    return all;
}
```

### Cambios en `CategoryService`

```java
public List<Category> getCategories() {
    return repository.getAll();
}

public List<Category> getCategories(String name) {
    return repository.getAll(name);
}
```

### Cambios en `CategoryController`

```java
@GetMapping
public ResponseEntity<List<Category>> getAllCategories(
        @RequestParam(required = false) String name) {
    return ResponseEntity.ok(service.getCategories(name));
}
```

---

## Pruebas requeridas

| Prueba | Resultado esperado |
|---|---|
| `GET /categories` | `200 OK` con todas las categorías, ordenadas por nombre |
| `GET /categories?name=hard` | Solo categorías con "hard" en el nombre |
| `GET /categories?name=HARD` | Mismo resultado (insensible a mayúsculas) |
| `GET /categories?name=xyz` | `200 OK` con `[]` |
| `GET /categories/1` | `200 OK` con la categoría (findById O(1)) |
| `GET /categories/999` | `404 Not Found` + `{"message": "..."}` |
| `DELETE /categories/1` | `204 No Content` |
| `DELETE /categories/999` | `404 Not Found` + `{"message": "..."}` |

---

## Desafío opcional

Agrega un segundo parámetro de filtro: `?hasDescription=true` devuelve solo categorías que tienen una descripción no vacía, `?hasDescription=false` devuelve las que tienen descripción vacía o nula.

```
GET /categories?hasDescription=true
```

Implementa la lógica en `CategoryRepository.getAll(String nameFilter, Boolean hasDescription)`.

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| `CategoryRepository` usa `Map<Long, Category>` correctamente (findById, save, update, delete O(1)) | 35% |
| `GET /categories?name=` filtra por nombre parcial insensible a mayúsculas | 25% |
| `GET /categories` sin parámetro sigue devolviendo todas ordenadas | 20% |
| Los errores 404 y 409 siguen devolviendo `ErrorResponse` correctamente | 10% |
| Las validaciones de `CategoryRequest` siguen funcionando (`@NotBlank`) | 10% |

