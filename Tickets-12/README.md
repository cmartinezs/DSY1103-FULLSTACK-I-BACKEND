# Tickets-12: Lección 12 - Relaciones JPA

## 📋 Descripción

Este proyecto implementa la **Lección 12: Relaciones JPA** del curso DSY1103 Fullstack I.

Agrega entidades Category y Tag con relaciones One-to-Many y Many-to-Many.

## 🎯 Caso de Uso Extendido (Sistema de Tickets con Gestión de Usuarios)

### Roles definidos
| Rol     | Descripción              |
|---------|--------------------------|
| USER    | Crea tickets, ve estado  |
| AGENT   | Recibe tickets asignados |
| ADMIN   | Supervisa y gestiona     |

### Modelo de datos
- **User**: id, name, email, role (USER/AGENT/ADMIN), active
- **Ticket**: relaciones con User (createdBy, assignedTo), Category, Tags
- **Category**: One-to-Many con Ticket
- **Tag**: Many-to-Many con Ticket

---

## 🔄 Cambios desde Lección 11

### 1. Nuevas Entidades

#### Category.java
```java
@Entity
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  
  @OneToMany(mappedBy = "category")
  private List<Ticket> tickets = new ArrayList<>();
}
```

#### Tag.java
```java
@Entity
@Table(name = "tags")
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String color;
  
  @ManyToMany
  @JoinTable(
    name = "ticket_tags",
    joinColumns = @JoinColumn(name = "tag_id"),
    inverseJoinColumns = @JoinColumn(name = "ticket_id")
  )
  private List<Ticket> tickets = new ArrayList<>();
}
```

### 2. Relaciones en Ticket
- `@ManyToOne` Category
- `@ManyToMany` Tags (vía JoinTable)

### 3. Repositorios
- CategoryRepository
- TagRepository

### 4. Servicios
- CategoryService (CRUD)
- TagService (CRUD)

### 5. Controladores
- CategoryController (/categories)
- TagController (/tags)

### 6. DataInitializer
- Categorías: Bug, Feature, Support
- Tags: urgent, backend, frontend

---

## 📊 Requisitos del Caso Extendido por Lección

| Lección | Requisitos del Caso Extendido |
|---------|------------------------------|
| 10 | ✅ User entity con roles, Ticket con User relaciones, seed de datos |
| 11 | ✅ Perfiles con diferentes configs de BD para usuarios (H2, MySQL, Supabase) |
| 12 | ✅ Category (One-to-Many), Tag (Many-to-Many), CRUD completo |
| 13 | Historial con User |
| 14 | Flyway migrations con Foreign Keys a users |
| 15 | Notificaciones con User |
| 16 | Security con 3 roles (USER/AGENT/ADMIN) |
| 17 | Logging de operaciones de usuarios |
| 18 | Excepciones para casos de usuarios |

---

## 🧪 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /categories | Listar categorías |
| POST | /categories | Crear categoría |
| GET | /categories/by-id/{id} | Ver categoría |
| PUT | /categories/by-id/{id} | Actualizar categoría |
| DELETE | /categories/by-id/{id} | Eliminar categoría |
| GET | /tags | Listar tags |
| POST | /tags | Crear tag |
| GET | /tags/by-id/{id} | Ver tag |
| PUT | /tags/by-id/{id} | Actualizar tag |
| DELETE | /tags/by-id/{id} | Eliminar tag |

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] CRUD de categorías funciona
- [x] CRUD de tags funciona
- [x] Relaciones JPA correctas
- [x] Serialización JSON sin loops infinitos

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `model/Category.java` | Entidad Category con One-to-Many |
| `model/Tag.java` | Entidad Tag con Many-to-Many |
| `respository/CategoryRepository.java` | Repository de Category |
| `respository/TagRepository.java` | Repository de Tag |
| `service/CategoryService.java` | CRUD de categorías |
| `service/TagService.java` | CRUD de tags |
| `controller/CategoryController.java` | Endpoints /categories |
| `controller/TagController.java` | Endpoints /tags |

---

**Base**: Lección 11 (Configuración de BD)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada