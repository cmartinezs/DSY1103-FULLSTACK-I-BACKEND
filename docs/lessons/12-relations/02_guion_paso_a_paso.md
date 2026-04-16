# Lección 12 — Tutorial paso a paso: relaciones entre entidades

---

## Paso 1: crear la entidad `User`

Crea el archivo `src/main/java/cl/duoc/fullstack/tickets/model/User.java`:

```java
package cl.duoc.fullstack.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre es requerido")
  @Column(nullable = false, length = 100)
  private String name;

  @NotBlank(message = "El email es requerido")
  @Email(message = "El email no tiene un formato válido")
  @Column(nullable = false, unique = true, length = 150)
  private String email;
}
```

> **¿Por qué `@Table(name = "users")` y no `@Table(name = "user")`?**
> `USER` es una función reservada en SQL (tanto MySQL como PostgreSQL la usan para obtener el usuario conectado). Si nombras la tabla `user`, el motor de base de datos puede confundirse al parsear las consultas. Usar `users` (plural) evita el conflicto y sigue la convención de nombrar tablas en plural.

> **¿Qué hace `@Email`?**
> Es una anotación de validación de Jakarta Bean Validation que verifica que el valor tenga formato de correo electrónico (`algo@dominio.com`). Funciona junto con `@Valid` en el controlador, igual que `@NotBlank`.

---

## Paso 2: crear `UserRequest` (DTO de entrada)

Crea `src/main/java/cl/duoc/fullstack/tickets/dto/UserRequest.java`:

```java
package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

  @NotBlank(message = "El nombre es requerido")
  private String name;

  @NotBlank(message = "El email es requerido")
  @Email(message = "El email no tiene un formato válido")
  private String email;
}
```

---

## Paso 3: crear `UserRepository`

Crea `src/main/java/cl/duoc/fullstack/tickets/respository/UserRepository.java`:

```java
package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
```

---

## Paso 4: crear `UserService`

Crea `src/main/java/cl/duoc/fullstack/tickets/service/UserService.java`:

```java
package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.UserRequest;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private UserRepository repository;

  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public List<User> getAll() {
    return repository.findAll();
  }

  public User create(UserRequest request) {
    if (repository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException(
          "Ya existe un usuario con el email '" + request.getEmail() + "'");
    }
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    return repository.save(user);
  }

  public Optional<User> getById(Long id) {
    return repository.findById(id);
  }
}
```

---

## Paso 5: crear `UserController`

Crea `src/main/java/cl/duoc/fullstack/tickets/controller/UserController.java`:

```java
package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.dto.UserRequest;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public List<User> getAll() {
    return service.getAll();
  }

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody UserRequest request) {
    try {
      User created = service.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(created);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new cl.duoc.fullstack.tickets.model.ErrorResponse(e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getById(@PathVariable Long id) {
    return service.getById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
```

---

## Paso 6: agregar las relaciones a `Ticket`

Abre `Ticket.java` y agrega los dos campos de relación. Primero las importaciones necesarias:

```java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
```

Luego los campos dentro de la clase, después de `effectiveResolutionDate`:

```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id")
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to_id")
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User assignedTo;
```

**¿Qué hace cada anotación?**

| Anotación | Qué hace |
|---|---|
| `@ManyToOne` | Define la relación: muchos tickets pueden pertenecer a un mismo usuario |
| `fetch = FetchType.LAZY` | No carga el `User` de la base de datos hasta que se accede al campo |
| `@JoinColumn(name = "created_by_id")` | Nombra la columna FK en la tabla `tickets` |
| `@JsonIgnoreProperties(...)` | Evita errores de serialización con objetos LAZY no cargados |

> **¿Qué es `FetchType.LAZY`?**
> Cuando cargas un `Ticket`, JPA no carga automáticamente el `User` asociado. Lo carga solo si accedes a `ticket.getCreatedBy()`. Esto mejora el rendimiento: si listas 100 tickets, no haces 100 consultas adicionales a la tabla `users`.
>
> El alternativo `FetchType.EAGER` carga el `User` siempre junto con el `Ticket`. Para relaciones `@ManyToOne` el defecto es `EAGER`, por eso lo especificamos explícitamente como `LAZY`.

---

## Paso 7: actualizar `TicketRequest`

Agrega dos campos opcionales a `TicketRequest.java`:

```java
  // Campos ya existentes:
  @NotBlank(message = "El titulo es requerido")
  private String title;

  @NotBlank
  private String description;

  // Campos nuevos — opcionales (pueden ser null):
  private Long createdById;    // ID del usuario que crea el ticket

  private Long assignedToId;   // ID del usuario asignado (puede dejarse sin asignar)
```

---

## Paso 8: actualizar `TicketService`

`TicketService` ahora necesita acceder a `UserRepository` para resolver los IDs a objetos `User`:

```java
@Service
public class TicketService {

  private TicketRepository repository;
  private UserRepository userRepository;   // ← nuevo

  public TicketService(TicketRepository repository, UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  public Ticket create(TicketRequest request) {
    if (repository.existsByTitle(request.getTitle())) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título '" + request.getTitle() + "'");
    }

    Ticket ticket = new Ticket();
    ticket.setTitle(request.getTitle());
    ticket.setDescription(request.getDescription());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));

    // Resolver el usuario creador (si se proporcionó)
    if (request.getCreatedById() != null) {
      User creator = userRepository.findById(request.getCreatedById())
          .orElseThrow(() -> new IllegalArgumentException(
              "No existe un usuario con ID " + request.getCreatedById()));
      ticket.setCreatedBy(creator);
    }

    // Resolver el usuario asignado (si se proporcionó)
    if (request.getAssignedToId() != null) {
      User assignee = userRepository.findById(request.getAssignedToId())
          .orElseThrow(() -> new IllegalArgumentException(
              "No existe un usuario con ID " + request.getAssignedToId()));
      ticket.setAssignedTo(assignee);
    }

    return repository.save(ticket);
  }

  public Optional<Ticket> updateById(Long id, TicketRequest request) {
    return repository.findById(id).map(ticket -> {
      ticket.setTitle(request.getTitle());
      ticket.setDescription(request.getDescription());
      if (request.getStatus() != null && !request.getStatus().isBlank()) {
        ticket.setStatus(request.getStatus());
      }
      // Actualizar usuario asignado si se proporcionó
      if (request.getAssignedToId() != null) {
        userRepository.findById(request.getAssignedToId())
            .ifPresent(ticket::setAssignedTo);
      }
      return repository.save(ticket);
    });
  }

  // ... resto de métodos sin cambios
}
```

> **¿Por qué el Service usa `UserRepository` directamente?**
> Porque el Service coordina entre repositorios. Crear un ticket implica verificar que el usuario existe — eso es lógica de negocio que pertenece al Service, no al Controller. El Controller solo recibe el ID y lo pasa; el Service valida que ese ID corresponde a un usuario real.

---

## Paso 9: probar la funcionalidad completa

### Crear un usuario

```
POST http://localhost:8080/ticket-app/users
Content-Type: application/json

{
  "name": "Ana García",
  "email": "ana.garcia@empresa.com"
}
```

Respuesta esperada: `201 Created` con el usuario incluyendo su `id` (por ejemplo, `1`).

### Crear un ticket con creador y asignado

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Teclado no funciona",
  "description": "Las teclas F1-F4 no responden",
  "createdById": 1,
  "assignedToId": 1
}
```

Respuesta esperada: `201 Created` con el ticket, incluyendo los objetos `createdBy` y `assignedTo`.

### Crear un ticket sin asignar

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Monitor parpadeante",
  "description": "El monitor parpadea al encender"
}
```

Respuesta esperada: `201 Created`. Los campos `createdBy` y `assignedTo` serán `null`.

### Asignar un ticket a un usuario (actualización)

```
PUT http://localhost:8080/ticket-app/tickets/1
Content-Type: application/json

{
  "title": "Teclado no funciona",
  "description": "Las teclas F1-F4 no responden",
  "status": "IN_PROGRESS",
  "assignedToId": 1
}
```

### Verificar en la base de datos

En phpMyAdmin o el Table Editor de Supabase, la tabla `tickets` debería mostrar las columnas `created_by_id` y `assigned_to_id` con los IDs correspondientes.

---

## El patrón `*Result` con entidades Relacionadas

Cuando hay relaciones `@ManyToOne`, el `*Result` debe incluir objetos anidados pero como datos planos, no como entidades JPA:

```java
// TicketResult con relaciones a User
public record TicketResult(
    Long id,
    String title,
    String description,
    String status,
    UserResult createdBy,      // ← No es la entidad User, es UserResult
    UserResult assignedTo,     // ← Cada UserResult es un POJO plano
    CategoryResult category,
    List<TagResult> tags
) {}

// UserResult — solo datos, sin JPA
public record UserResult(
    Long id,
    String name,
    String email
) {}

// CategoryResult
public record CategoryResult(
    Long id,
    String name,
    String description
) {}

// TagResult
public record TagResult(
    Long id,
    String name,
    String color
) {}
```

### Transformación en el Service

```java
private TicketResult toResult(Ticket ticket) {
  UserResult createdBy = ticket.getCreatedBy() != null
      ? new UserResult(
          ticket.getCreatedBy().getId(),
          ticket.getCreatedBy().getName(),
          ticket.getCreatedBy().getEmail())
      : null;

  UserResult assignedTo = ticket.getAssignedTo() != null
      ? new UserResult(
          ticket.getAssignedTo().getId(),
          ticket.getAssignedTo().getName(),
          ticket.getAssignedTo().getEmail())
      : null;

  CategoryResult category = ticket.getCategory() != null
      ? new CategoryResult(
          ticket.getCategory().getId(),
          ticket.getCategory().getName(),
          ticket.getCategory().getDescription())
      : null;

  List<TagResult> tags = ticket.getTags() != null
      ? ticket.getTags().stream()
          .map(tag -> new TagResult(
              tag.getId(),
              tag.getName(),
              tag.getColor()))
          .toList()
      : List.of();

  return new TicketResult(
      ticket.getId(),
      ticket.getTitle(),
      ticket.getDescription(),
      ticket.getStatus(),
      createdBy,
      assignedTo,
      category,
      tags
  );
}
```

### Por qué no usar la entidad directamente

| En Entity (`Ticket`) | En Result (`TicketResult`) |
|---|---|
| `@ManyToOne User createdBy` | `UserResult createdBy` |
| JPA proxy lazy | Datos planos |
| Serialización circular | Sin relaciones bidireccionales |
| Expone internals de Hibernate | Solo lo que quieres mostrar |

**Conclusión:** El Service transforma cada entidad a su `*Result` correspondiente. Nunca retornas una entidad JPA por `ResponseEntity`.
