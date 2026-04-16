# Lección 10 — Actividad individual: migrar Category a JPA

## Contexto

Tu entidad `Category` (creada en la lección 05) todavía usa un repositorio en memoria. Esta actividad aplica exactamente la misma migración que hiciste con `Ticket`.

---

## Lo que debes entregar

### Parte 1: anotar `Category` como entidad JPA

Modifica `Category.java` con las anotaciones JPA correspondientes:

| Campo | Tipo Java | Anotaciones requeridas |
|---|---|---|
| `id` | `Long` | `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `name` | `String` | `@Column(nullable = false, unique = true, length = 100)` |
| `description` | `String` | `@Column(columnDefinition = "TEXT")` |

La clase debe seguir teniendo `@Entity`, `@Table(name = "categories")`, y `@NoArgsConstructor`.

### Parte 2: convertir `CategoryRepository` a interfaz JPA

```java
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsByName(String name);

  List<Category> findAllByOrderByNameAsc();

  List<Category> findByNameContainingIgnoreCase(String name);
}
```

> **¿Qué hace `findByNameContainingIgnoreCase`?**
> Genera `SELECT * FROM categories WHERE LOWER(name) LIKE LOWER('%?%')`. Es el equivalente JPA al filtro de nombre parcial que implementaste en L09 manualmente.

### Parte 3: actualizar `CategoryService`

Adapta los métodos del servicio para usar `JpaRepository`:

- `getCategories(String nameFilter)`: usa `findAllByOrderByNameAsc()` o `findByNameContainingIgnoreCase(nameFilter)` según corresponda
- `create(CategoryRequest request)`: usa `existsByName()` + `save()`
- `getById(Long id)`: usa `findById(id)` — devuelve `Optional<Category>`
- `deleteById(Long id)`: usa `existsById()` + `deleteById()`
- `updateById(Long id, CategoryRequest request)`: usa `findById().map(...)` + `save()`

---

## Pruebas requeridas

| Prueba | Resultado esperado |
|---|---|
| Aplicación arranca sin errores | Tabla `categories` creada en MySQL automáticamente |
| `POST /categories` | Crea y persiste la categoría en MySQL |
| `GET /categories` | Devuelve todas las categorías ordenadas por nombre |
| `GET /categories?name=hard` | Filtra por nombre parcial |
| `GET /categories/{id}` | Devuelve la categoría o `404` |
| `DELETE /categories/{id}` | Elimina el registro de MySQL |
| Reiniciar la app | Las categorías persisten |

---

## Criterios de evaluación

| Criterio | Puntaje |
|---|---|
| `Category` tiene `@Entity`, `@Id`, `@GeneratedValue`, `@Column` correctos | 30% |
| `CategoryRepository` es interfaz y extiende `JpaRepository<Category, Long>` | 25% |
| `CategoryService` usa los métodos de JPA correctamente | 25% |
| Las pruebas pasan y los datos persisten tras reiniciar | 20% |

---

## Desafío opcional

Agrega el siguiente método al repositorio y úsalo en el servicio:

```java
// Devuelve categorías que tienen al menos un ticket asociado
// (lo implementaremos cuando tengamos la relación en L12, por ahora solo decláralo)
long countByNameContainingIgnoreCase(String name);
```

Este método retorna cuántas categorías contienen el texto buscado. Úsalo en el servicio para loguear el resultado: `log.info("Encontradas {} categorías", count)`.
