# Lección 12 — Actividad Personal: Extender con Category

## ¿Qué es esta actividad?

Esta es una **ACTIVIDAD PERSONAL** que complementa el tutorial base.

En `02_guion_paso_a_paso.md` cubrimos:
- ✓ User (Entity, DTO, Repository, Service, Controller)
- ✓ Ticket (relaciones @ManyToOne a User, búsqueda por email)
- ✓ User con @OneToMany (Paso 9)
- ✓ DTOs de respuesta TicketResult / UserResult (Paso 8.7)
- ✓ Asignación con PATCH /tickets/{id} (Paso 8.8)

**Category** no tiene guion paso-a-paso. En su lugar, tienes directrices para diseñarla e implementarla autónomamente.

Esto es tu oportunidad de practicar el patrón completo:
Entity → DTO → Repository → Service → Controller

---

## Directrices: Implementa Category Autónomamente

Sigue el mismo patrón que `User` del tutorial:

### 1. Crear la entidad `Category`

En `src/main/java/cl/duoc/fullstack/tickets/model/Category.java`:

- `@Entity` y `@Table(name = "categories")`
- Campo `id` con `@Id` y `@GeneratedValue`
- Campo `name` con `@Column(nullable = false, unique = true, length = 100)` y `@NotBlank`
- Campo `description` con `@Column(columnDefinition = "TEXT")` y `@NotBlank`
- Anotaciones Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`

### 2. Crear `CategoryRequest` DTO

En `src/main/java/cl/duoc/fullstack/tickets/dto/CategoryRequest.java`:

- Campos: `name`, `description`
- Validaciones: `@NotBlank` en ambos
- Lombok: `@Getter`, `@Setter`

### 3. Crear `CategoryRepository`

En `src/main/java/cl/duoc/fullstack/tickets/respository/CategoryRepository.java`:

- Extiende `JpaRepository<Category, Long>`
- Métodos útiles: `existsByName()`, `findByName()`

### 4. Crear `CategoryService`

En `src/main/java/cl/duoc/fullstack/tickets/service/CategoryService.java`:

- `getAll()` lista todas
- `create(CategoryRequest)` valida duplicado por name
- `getById(Long id)` retorna Optional
- Excepciones: `IllegalArgumentException` si name duplicado

### 5. Crear `CategoryController`

En `src/main/java/cl/duoc/fullstack/tickets/controller/CategoryController.java`:

- `@RestController` en `/categories`
- `GET /categories` lista
- `POST /categories` crea con `@Valid`, `201 Created` o `409 Conflict`
- `GET /categories/{id}` por id

### 6. Agregar @ManyToOne a Ticket

En `Ticket.java`:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
private Category category;
```

### 7. Agregar `categoryId` a `TicketRequest`

```java
private Long categoryId;  // opcional
```

### 8. Actualizar `TicketService`

En el método `create()`, resuelve la categoría si se proporciona `categoryId`
(análogo a `createdByEmail` — busca en repositorio y lanza excepción si no existe).

### 9. Pruebas

- POST /categories (crear categorías)
- GET /categories (listar)
- POST /tickets con categoryId válido
- GET /tickets (verificar vinculación)

### 10. Desafío Opcional

Implementa filtro por categoría:

```
GET /tickets?categoryId=1
```

Agrega a `TicketRepository`:
```java
List<Ticket> findByCategoryId(Long categoryId);
```

Agrega a `TicketController`:
```java
@GetMapping
public List<Ticket> list(@RequestParam(required = false) Long categoryId) {
  if (categoryId != null) {
    return ticketService.findByCategory(categoryId);
  }
  return ticketService.getAll();
}
```
