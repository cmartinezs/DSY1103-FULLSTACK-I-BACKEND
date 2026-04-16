# Lección 12: Relaciones JPA

## 📚 Objetivo

Implementar relaciones JPA en la aplicación Ticket:
- **One-to-Many**: Ticket ↔ Category
- **Many-to-Many**: Ticket ↔ Tag

## 🔄 Cambios Implementados

### 1. Nuevas Entidades

#### **Category** (Relación One-to-Many)
```java
@Entity
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<Ticket> tickets = new ArrayList<>();
}
```

#### **Tag** (Relación Many-to-Many)
```java
@Entity
@Table(name = "tags")
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String color;
  
  @ManyToMany(mappedBy = "tags")
  @JsonIgnore
  private List<Ticket> tickets = new ArrayList<>();
}
```

#### **Ticket** (Actualizado con relaciones)
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
@JsonBackReference
private Category category;

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "ticket_tags",
    joinColumns = @JoinColumn(name = "ticket_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags = new ArrayList<>();
```

### 2. Repositorios

- **CategoryRepository**: `JpaRepository<Category, Long>`
  - Método: `existsByNameIgnoreCase(String name)`

- **TagRepository**: `JpaRepository<Tag, Long>`
  - Método: `existsByNameIgnoreCase(String name)`

### 3. DTOs de Entrada

- **CategoryRequest**: `CategoryRequest(String name, String description)`
- **TagRequest**: `TagRequest(String name, String color)`
- **TicketRequest**: Actualizado con `Long categoryId` y `List<Long> tagIds`

### 4. Servicios

- **CategoryService**: CRUD completo + validación de duplicados
- **TagService**: CRUD completo + validación de duplicados
- **TicketService**: Actualizado para manejar categorías y tags

### 5. Controladores REST

#### **CategoryController** (`/ticket-app/categories`)
```
GET    /                        # Listar todas
GET    /by-id/{id}             # Obtener por ID
POST   /                        # Crear (validado)
PUT    /by-id/{id}             # Actualizar (validado)
DELETE /by-id/{id}             # Eliminar
```

#### **TagController** (`/ticket-app/tags`)
```
GET    /                        # Listar todas
GET    /by-id/{id}             # Obtener por ID
POST   /                        # Crear (validado)
PUT    /by-id/{id}             # Actualizar (validado)
DELETE /by-id/{id}             # Eliminar
```

### 6. Manejo de Serialización Circular

Uso de anotaciones Jackson para evitar loops infinitos:
- `@JsonManagedReference` en Category.tickets
- `@JsonBackReference` en Ticket.category
- `@JsonIgnore` en Tag.tickets

### 7. DataInitializer Actualizado

Carga inicial de datos:
- 2 Categorías: "Bug", "Feature"
- 3 Tags: "Urgent" (rojo), "Backend" (azul), "UI" (verde)
- 2 Tickets con relaciones asignadas

## 📊 Base de Datos

Nuevas tablas creadas:
```sql
CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(255)
);

CREATE TABLE tags (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  color VARCHAR(7)
);

CREATE TABLE ticket_tags (
  ticket_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (ticket_id, tag_id),
  FOREIGN KEY (ticket_id) REFERENCES tickets(id),
  FOREIGN KEY (tag_id) REFERENCES tags(id)
);

ALTER TABLE tickets ADD COLUMN category_id BIGINT;
ALTER TABLE tickets ADD FOREIGN KEY (category_id) REFERENCES categories(id);
```

## ✅ Validación

- [x] Compilación sin errores
- [x] Todos los tests pasan
- [x] Aplicación arranca correctamente
- [x] Endpoints REST funcionan correctamente
- [x] Serialización JSON sin loops infinitos
- [x] Validación de duplicados en categorías y tags
- [x] Relaciones One-to-Many funcionan (cascade)
- [x] Relaciones Many-to-Many funcionan
- [x] Datos iniciales se cargan correctamente

## 🧪 Pruebas Manuales

### Crear Categoría
```bash
curl -X POST http://localhost:8080/ticket-app/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Enhancement", "description": "Mejoras"}'
```

### Crear Tag
```bash
curl -X POST http://localhost:8080/ticket-app/tags \
  -H "Content-Type: application/json" \
  -d '{"name": "Documentation", "color": "#FFA500"}'
```

### Crear Ticket con Categoría y Tags
```bash
curl -X POST http://localhost:8080/ticket-app/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nuevo Ticket",
    "description": "Con relaciones",
    "createdBy": "admin",
    "categoryId": 1,
    "tagIds": [1, 2]
  }'
```

### Listar Tickets con Relaciones
```bash
curl http://localhost:8080/ticket-app/tickets | jq '.[] | {id, title, category, tags}'
```

## 📝 Archivos Modificados/Creados

| Archivo | Tipo | Descripción |
|---------|------|-------------|
| `model/Category.java` | ✨ Nuevo | Entidad One-to-Many |
| `model/Tag.java` | ✨ Nuevo | Entidad Many-to-Many |
| `model/Ticket.java` | 📝 Actualizado | Agregadas relaciones |
| `respository/CategoryRepository.java` | ✨ Nuevo | JPA Repository |
| `respository/TagRepository.java` | ✨ Nuevo | JPA Repository |
| `service/CategoryService.java` | ✨ Nuevo | CRUD Service |
| `service/TagService.java` | ✨ Nuevo | CRUD Service |
| `service/TicketService.java` | 📝 Actualizado | Manejo de relaciones |
| `controller/CategoryController.java` | ✨ Nuevo | REST Controller |
| `controller/TagController.java` | ✨ Nuevo | REST Controller |
| `dto/CategoryRequest.java` | ✨ Nuevo | Request DTO |
| `dto/TagRequest.java` | ✨ Nuevo | Request DTO |
| `dto/TicketRequest.java` | 📝 Actualizado | Agregados IDs |
| `config/DataInitializer.java` | 📝 Actualizado | Datos de prueba |

## 🚀 Próximo Paso

Lección 13: Auditoría (TicketAudit, @CreationTimestamp, endpoint de historial)
