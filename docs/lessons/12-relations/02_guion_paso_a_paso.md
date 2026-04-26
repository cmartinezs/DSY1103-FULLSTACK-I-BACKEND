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
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
```

Luego los campos dentro de la clase, después de `effectiveResolutionDate`:

```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id")
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to_id")
  private User assignedTo;
```

**¿Qué hace cada anotación?**

| Anotación | Qué hace |
|---|---|
| `@ManyToOne` | Define la relación: muchos tickets pueden pertenecer a un mismo usuario |
| `fetch = FetchType.LAZY` | No carga el `User` de la base de datos hasta que se accede al campo |
| `@JoinColumn(name = "created_by_id")` | Nombra la columna FK en la tabla `tickets` |

> **¿Por qué no se necesita `@JsonIgnoreProperties`?**
> El entity `Ticket` nunca sale directamente del controlador — `TicketService` lo convierte a `TicketResult` antes de retornarlo. Como Jackson nunca serializa el entity, no hay riesgo de error con objetos LAZY.

> **¿Qué es `FetchType.LAZY`?**
> Cuando cargas un `Ticket`, JPA no carga automáticamente el `User` asociado. Lo carga solo si accedes a `ticket.getCreatedBy()`. Esto mejora el rendimiento: si listas 100 tickets, no haces 100 consultas adicionales a la tabla `users`.
>
> El alternativo `FetchType.EAGER` carga el `User` siempre junto con el `Ticket`. Para relaciones `@ManyToOne` el defecto es `EAGER`, por eso lo especificamos explícitamente como `LAZY`.

---

## Paso 7: actualizar `TicketRequest`

Agrega el campo `createdByEmail` (requerido) a `TicketRequest.java`:

```java
  // Campos ya existentes:
  @NotBlank(message = "El titulo es requerido")
  private String title;

  @NotBlank
  private String description;

  // Campo nuevo — requerido para POST:
  @NotBlank(message = "El email del creador es requerido")
  @Email(message = "El email no tiene un formato válido")
  private String createdByEmail;
```

> **Nota:** La asignación a un usuario (`assignedToEmail`) se realiza por separado mediante `PATCH /tickets/{id}`. No se incluye en el POST.

---

## Paso 8: actualizar `TicketService`

Antes de actualizar el servicio, crea la excepción personalizada que usaremos para distinguir errores de cliente (400) de errores de negocio (409).

Crea `src/main/java/cl/duoc/fullstack/tickets/exception/BadRequestException.java`:

```java
package cl.duoc.fullstack.tickets.exception;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}
```

Ahora actualiza `TicketService.java`. El método `create()` busca el usuario **por email** antes de crear el ticket:

```java
@Service
public class TicketService {

  private TicketRepository repository;
  private UserRepository userRepository;

  public TicketService(TicketRepository repository, UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  public TicketResult create(TicketRequest request) {
    // 1. Validar título duplicado → 409 Conflict (regla de negocio)
    if (repository.existsByTitle(request.getTitle())) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título '" + request.getTitle() + "'");
    }

    // 2. Buscar usuario creador por email → 400 Bad Request si no existe
    User creator = userRepository.findByEmail(request.getCreatedByEmail())
        .orElseThrow(() -> new BadRequestException(
            "El email '" + request.getCreatedByEmail() + "' no existe en el sistema"));

    // 3. Crear el ticket
    Ticket ticket = new Ticket();
    ticket.setTitle(request.getTitle());
    ticket.setDescription(request.getDescription());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));
    ticket.setCreatedBy(creator);

    return toResult(repository.save(ticket));
  }

  public TicketResult updateById(Long id, TicketRequest request) {
    Ticket ticket = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Ticket con id " + id + " no encontrado"));
    ticket.setTitle(request.getTitle());
    ticket.setDescription(request.getDescription());
    if (request.getStatus() != null && !request.getStatus().isBlank()) {
      ticket.setStatus(request.getStatus());
    }
    return toResult(repository.save(ticket));
  }

  // ... getById(), deleteById(), getTickets() sin cambios

  private TicketResult toResult(Ticket ticket) {
    UserResult createdBy = ticket.getCreatedBy() != null
        ? new UserResult(ticket.getCreatedBy().getId(),
                         ticket.getCreatedBy().getName(),
                         ticket.getCreatedBy().getEmail())
        : null;
    UserResult assignedTo = ticket.getAssignedTo() != null
        ? new UserResult(ticket.getAssignedTo().getId(),
                         ticket.getAssignedTo().getName(),
                         ticket.getAssignedTo().getEmail())
        : null;
    return new TicketResult(
        ticket.getId(), ticket.getTitle(), ticket.getDescription(),
        ticket.getStatus(), ticket.getCreatedAt(), ticket.getEstimatedResolutionDate(),
        ticket.getEffectiveResolutionDate(), createdBy, assignedTo);
  }
}
```

> **¿Por qué `BadRequestException` (400) y no `IllegalArgumentException` (409)?**
> - `IllegalArgumentException` → **409 Conflict**: el cliente rompe una regla de negocio (título duplicado que ya existe en el sistema).
> - `BadRequestException` → **400 Bad Request**: el cliente envió datos inválidos (un email que no corresponde a ningún usuario).
> La distinción es semántica: 409 es "colisión", 400 es "dato incorrecto".

> **¿Por qué el Service usa `UserRepository` directamente?**
> Porque el Service coordina entre repositorios. Crear un ticket implica verificar que el usuario existe — esa es lógica de negocio que pertenece al Service, no al Controller.

---

## Paso 8.5: agregar `assignTicket()` a `TicketService`

La asignación de usuario es una operación separada del POST. Agrega este método al servicio:

```java
public Optional<TicketResult> assignTicket(Long ticketId, String assignedToEmail) {
  // 1. Si email vacío o null → desasignar
  if (assignedToEmail == null || assignedToEmail.isBlank()) {
    Optional<Ticket> ticketOpt = repository.findById(ticketId);
    if (!ticketOpt.isPresent()) {
      return Optional.empty();
    }
    Ticket ticket = ticketOpt.get();
    ticket.setAssignedTo(null);
    return Optional.of(toResult(repository.save(ticket)));
  }

  // 2. Validar que el usuario existe ANTES de buscar el ticket (400 Bad Request)
  User assignee = userRepository.findByEmail(assignedToEmail)
      .orElseThrow(() -> new BadRequestException(
          "El email '" + assignedToEmail + "' no existe en el sistema"));

  // 3. Buscar el ticket (404 si no existe)
  Optional<Ticket> ticketOpt = repository.findById(ticketId);
  if (!ticketOpt.isPresent()) {
    return Optional.empty();
  }

  // 4. Asignar y guardar
  Ticket ticket = ticketOpt.get();
  ticket.setAssignedTo(assignee);
  return Optional.of(toResult(repository.save(ticket)));
}
```

> **¿Por qué validar el email ANTES de buscar el ticket?**
> Principio de "fallo rápido": si el email es inválido, retornamos 400 inmediatamente sin hacer la consulta del ticket. Es más eficiente y da mejor feedback al cliente.

---

## Paso 8.6: crear `AssignTicketRequest` DTO

Crea `src/main/java/cl/duoc/fullstack/tickets/dto/AssignTicketRequest.java`:

```java
package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTicketRequest {

  @Email(message = "El email no tiene un formato válido")
  private String assignedToEmail;  // Opcional — null o vacío desasigna el ticket
}
```

> **¿Por qué solo `@Email` y no `@NotBlank`?**
> `@Email` valida el formato solo si el campo tiene un valor. Un campo `null` o vacío pasa la validación — eso es exactamente lo que queremos, porque vacío significa "desasignar".

---

## Paso 8.7: crear `TicketResult` y `UserResult` DTOs

Estos DTOs permiten que el JSON de respuesta incluya los datos completos del usuario (id, nombre, email) en lugar de solo el ID de la FK.

Crea `src/main/java/cl/duoc/fullstack/tickets/dto/UserResult.java`:

```java
package cl.duoc.fullstack.tickets.dto;

public record UserResult(
    Long id,
    String name,
    String email
) {}
```

Crea `src/main/java/cl/duoc/fullstack/tickets/dto/TicketResult.java`:

```java
package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TicketResult(
    Long id,
    String title,
    String description,
    String status,
    LocalDateTime createdAt,
    LocalDate estimatedResolutionDate,
    LocalDateTime effectiveResolutionDate,
    UserResult createdBy,
    UserResult assignedTo
) {}
```

El JSON de respuesta resultante tendrá esta forma:

```json
{
  "id": 1,
  "title": "Teclado no funciona",
  "status": "NEW",
  "createdBy": {
    "id": 1,
    "name": "Ana García",
    "email": "ana.garcia@empresa.com"
  },
  "assignedTo": null
}
```

---

## Paso 8.8: agregar `PATCH /tickets/{id}` al controlador

En `TicketController.java`, agrega el endpoint de asignación y actualiza el POST para capturar `BadRequestException`:

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody TicketRequest request) {
  try {
    TicketResult result = service.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  } catch (IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
  } catch (BadRequestException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
  }
}

@PatchMapping("/{id}")
public ResponseEntity<?> assignTicket(
    @PathVariable Long id,
    @Valid @RequestBody AssignTicketRequest request) {
  try {
    return service.assignTicket(id, request.getAssignedToEmail())
        .map(result -> ResponseEntity.ok(result))
        .orElse(ResponseEntity.notFound().build());
  } catch (BadRequestException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(e.getMessage()));
  }
}
```

| Caso | HTTP |
|---|---|
| Email no existe en el sistema | `400 Bad Request` |
| Ticket no encontrado | `404 Not Found` |
| Asignación/desasignación exitosa | `200 OK` |

---

## Paso 9: Agregar @OneToMany en User

En `User.java`, agrega los imports necesarios:

```java
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
```

Luego, dentro de la clase User después del campo email, agrega:

```java
@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
private List<Ticket> createdTickets = new ArrayList<>();

@OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
private List<Ticket> assignedTickets = new ArrayList<>();
```

**¿Qué hace cada parte?**

| Elemento | Propósito |
|----------|-----------|
| `@OneToMany` | Un User tiene muchos Tickets |
| `mappedBy = "createdBy"` | Apunta al campo @ManyToOne en Ticket |
| `fetch = FetchType.LAZY` | No carga tickets al obtener User (eficiente) |
| `new ArrayList<>()` | Inicializar vacía |

> **¿Por qué no se necesita `@JsonIgnore`?**
> `User` tampoco se serializa directamente — el servicio lo convierte a `UserResult` antes de retornarlo. Las listas `createdTickets` / `assignedTickets` nunca son expuestas al JSON de respuesta.

**Trade-off: LAZY vs EAGER**

Usamos `LAZY` porque:
- Si un User tiene 1000 tickets, no cargarlos todos es mucho más eficiente
- Cargamos solo cuando el cliente los necesita
- Por defecto `@OneToMany` es LAZY

Si un User tenía pocos tickets (< 10) y los necesitabas siempre: usarías `EAGER`

**Alternativa si el User tiene MÁS DE 100 tickets:**

En lugar de `@OneToMany`, usa función en TicketRepository:

```java
List<Ticket> findByCreatedById(Long userId);
List<Ticket> findByAssignedToId(Long userId);
```

El cliente controla cuándo cargarlos con paginación.

---

## Paso 10: probar la funcionalidad completa

### Crear un usuario

```
POST http://localhost:8080/ticket-app/users
Content-Type: application/json

{
  "name": "Ana García",
  "email": "ana.garcia@empresa.com"
}
```

Respuesta esperada: `201 Created` con el usuario incluyendo su `id`.

### Crear un segundo usuario

```
POST http://localhost:8080/ticket-app/users
Content-Type: application/json

{
  "name": "Carlos López",
  "email": "carlos.lopez@empresa.com"
}
```

### Crear un ticket con creador por email

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Teclado no funciona",
  "description": "Las teclas F1-F4 no responden",
  "createdByEmail": "ana.garcia@empresa.com"
}
```

Respuesta esperada: `201 Created`. El campo `createdBy` incluirá el objeto `User` completo. El campo `assignedTo` será `null`.

### Crear un ticket con email inexistente

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Monitor parpadeante",
  "description": "El monitor parpadea al encender",
  "createdByEmail": "no-existe@empresa.com"
}
```

Respuesta esperada: `400 Bad Request` — el email no existe en el sistema.

### Asignar un ticket a un usuario (PATCH)

```
PATCH http://localhost:8080/ticket-app/tickets/1
Content-Type: application/json

{
  "assignedToEmail": "carlos.lopez@empresa.com"
}
```

Respuesta esperada: `200 OK` con el ticket actualizado y `assignedTo` con los datos de Carlos.

### Desasignar un ticket

```
PATCH http://localhost:8080/ticket-app/tickets/1
Content-Type: application/json

{
  "assignedToEmail": ""
}
```

Respuesta esperada: `200 OK` con `assignedTo: null`.

### Verificar en la base de datos

En phpMyAdmin o el Table Editor de Supabase, la tabla `tickets` debería mostrar las columnas `created_by_id` y `assigned_to_id` con los IDs correspondientes.

---

> Los DTOs `TicketResult` y `UserResult` se implementaron en **Paso 8.7**. Son requeridos en esta lección para exponer datos de usuario anidados en la respuesta JSON sin serialización circular.
