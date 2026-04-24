# Tickets-12: Lección 12 - Relaciones JPA

## 📋 Descripción

Este proyecto implementa la **Lección 12: Relaciones JPA** del curso DSY1103 Fullstack I.

Agrega entidad User con relaciones One-to-Many a Ticket.

---

## 🔄 Cambios desde Lección 11

### 1. Perfiles de Configuración (heredados de Lección 11)
- ✅ application.yml (base)
- ✅ application-h2.yml
- ✅ application-mysql.yml
- ✅ application-supabase.yml
- ✅ spring-dotenv para cargar .env
- ✅ Variables de entorno

### 2. Nueva Entidad User.java
```java
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String email;
  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;
  private boolean active = true;

  public enum Role { USER, AGENT, ADMIN }
}
```

### 3. Relaciones en Ticket
- `@ManyToOne` createdBy (quién crea el ticket)
- `@ManyToOne` assignedTo (a quién se asigna)

### 4. Repositorios
- UserRepository (con findByEmail)

### 5. Servicios
- UserService (listar usuarios)

### 6. Controladores
- UserController (/users)

### 7. DataInitializer
- Usuarios: admin@tickets.com (ADMIN), agent@tickets.com (AGENT), john@tickets.com (USER)

---

## 🧪 Endpoints

### Tickets
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /tickets | Listar tickets |
| POST | /tickets | Crear ticket |
| GET | /tickets/by-id/{id} | Ver ticket |
| PUT | /tickets/by-id/{id} | Actualizar ticket |
| DELETE | /tickets/by-id/{id} | Eliminar ticket |

### Users
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /users | Listar usuarios |
| POST | /users | Crear usuario |
| GET | /users/by-id/{id} | Ver usuario |
| PUT | /users/by-id/{id} | Actualizar usuario |
| DELETE | /users/by-id/{id} | Eliminar usuario |

---

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] User entity con roles
- [x] Ticket con relaciones ManyToOne
- [x] Serialización JSON sin loops infinitos

---

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `model/User.java` | Entidad User con roles |
| `model/Ticket.java` | Entidad Ticket con relaciones |
| `respository/UserRepository.java` | Repository de User |
| `service/UserService.java` | Servicio de usuarios |
| `controller/UserController.java` | Endpoints /users |
| `config/DataInitializer.java` | Datos iniciales |

---

**Base**: Lección 11 (Configuración de BD)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada