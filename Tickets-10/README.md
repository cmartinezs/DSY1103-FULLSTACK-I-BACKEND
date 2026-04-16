# Tickets-10: Lección 10 - Introducción a JPA

## 📋 Descripción

Este proyecto implementa la **Lección 10: Introducción a JPA** del curso DSY1103 Fullstack I.

Migración del repositorio en-memoria basado en HashMap a **Spring Data JPA** con Hibernate.

## 🎯 Caso de Uso Extendido (Sistema de Tickets con Gestión de Usuarios)

### Roles definidos
| Rol     | Descripción              |
|---------|--------------------------|
| USER    | Crea tickets, ve estado  |
| AGENT   | Recibe tickets asignados |
| ADMIN   | Supervisa y gestiona     |

### Modelo de datos
- **User**: id, name, email, role (USER/AGENT/ADMIN), active
- **Ticket**: id, title, description, status, createdAt, estimatedResolutionDate, effectiveResolutionDate, createdBy (User), assignedTo (User)

### Datos iniciales
- admin@tickets.com (ADMIN)
- agent@tickets.com (AGENT)
- john@tickets.com (USER)

---

## 🔄 Cambios desde Lección 09

### 1. Dependencias (pom.xml)
- ✅ Agregadas: `spring-boot-starter-data-jpa`, `h2`
- Remover dependencias manuales de repositorio in-memory

### 2. Modelo (Ticket.java)
- ✅ Convertida a entidad JPA con `@Entity`
- ✅ `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` para auto-increment
- ✅ `@Table(name = "tickets")` para mapeo
- ✅ Relaciones `@ManyToOne` con User (createdBy, assignedTo)

### 3. Nuevo Modelo (User.java)
- ✅ Entidad con roles: USER, AGENT, ADMIN
- ✅ Campos: id, name, email, role, active
- ✅ Enumeración para tipo de rol

```java
@Entity
@Table(name = "users")
@Getter @Setter
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

### 4. Repositorios
- **TicketRepository**: JpaRepository con métodos de query
- **UserRepository**: JpaRepository con métodos de búsqueda por email

### 5. Servicio (TicketService.java)
- ✅ Actualizado para usar User entity
- ✅ Soporte para crear tickets con creador y asignado
- ✅ Validación de que creador y asignado no sean el mismo

### 6. DTOs
- **TicketRequest**: title, description, createdByName, assignedToId, status, effectiveResolutionDate
- **TicketResult**: incluye objetos User para createdBy y assignedTo

### 7. Configuración (application.yml)
- ✅ Agregada configuración de H2 (in-memory)
- ✅ JPA/Hibernate settings
- ✅ `ddl-auto: create-drop` para desarrollo

### 8. Inicializador de Datos (DataInitializer.java)
- ✅ Carga usuarios iniciales con diferentes roles
- ✅ Crea tickets de ejemplo con relaciones

---

## 📊 Requisitos del Caso Extendido por Lección

| Lección | Requisitos del Caso Extendido |
|---------|------------------------------|
| 10 | ✅ User entity con roles, Ticket con User relaciones, seed de datos |
| 11 | Perfiles con diferentes configs de BD para usuarios |
| 12 | Category/Tag relaciones con User |
| 13 | Historial con User |
| 14 | Flyway migrations con Foreign Keys a users |
| 15 | Notificaciones con User |
| 16 | Security con 3 roles (USER/AGENT/ADMIN) |
| 17 | Logging de operaciones de usuarios |
| 18 | Excepciones para casos de usuarios |

---

## 🧪 Testing

```bash
# Compilar
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run
```

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] Endpoints CRUD funcionan con JPA
- [x] Datos iniciales se cargan al iniciar (usuarios con roles)
- [x] H2 en-memory funciona correctamente

## 📚 Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /tickets | Listar todos los tickets |
| GET | /tickets/by-id/{id} | Ver ticket por ID |
| POST | /tickets | Crear nuevo ticket |
| PUT | /tickets/by-id/{id} | Actualizar ticket |
| DELETE | /tickets/by-id/{id} | Eliminar ticket |

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `model/User.java` | Entidad User con roles |
| `model/Ticket.java` | Entidad Ticket con relaciones a User |
| `respository/UserRepository.java` | Repository de User |
| `dto/TicketRequest.java` | DTO para crear tickets |
| `dto/TicketResult.java` | DTO con datos de User |
| `config/DataInitializer.java` | Carga usuarios y tickets iniciales |

---

**Base**: Lección 09 (Repositorio Customizado)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2  
**Estado**: ✅ Completada