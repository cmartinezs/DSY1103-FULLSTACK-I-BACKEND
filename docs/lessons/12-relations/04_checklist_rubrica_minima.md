# Lección 12 — Checklist y rúbrica mínima

---

## Checklist de `User.java`

- ☐ La clase tiene `@Entity` y `@Table(name = "users")` (plural, evita conflicto con palabra reservada SQL)
- ☐ El campo `id` tiene `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- ☐ El campo `name` tiene `@Column(nullable = false, length = 100)`
- ☐ El campo `email` tiene `@Column(nullable = false, unique = true, length = 150)` y `@Email`
- ☐ La clase tiene `@NoArgsConstructor` (requerido por JPA)
- ☐ Todas las importaciones son de `jakarta.persistence.*`
- ☐ Tiene campo `createdTickets` con `@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)`
- ☐ Tiene campo `assignedTickets` con `@OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)`
- ☐ Ambas colecciones inicializadas con `new ArrayList<>()`

---

## Checklist de `UserRequest.java`

- ☐ Existe el archivo `UserRequest.java` en el paquete `dto`
- ☐ Tiene los campos `name` y `email` con sus validaciones (`@NotBlank`, `@Email`)

---

## Checklist de `UserRepository.java`

- ☐ Es una interfaz que extiende `JpaRepository<User, Long>`
- ☐ Tiene `boolean existsByEmail(String email)`
- ☐ Tiene `Optional<User> findByEmail(String email)`

---

## Checklist de `UserService.java`

- ☐ Tiene `getAll()` que retorna `repository.findAll()`
- ☐ Tiene `create(UserRequest request)` que verifica duplicado por email con `existsByEmail()` antes de guardar
- ☐ Tiene `getById(Long id)` que retorna `Optional<User>`
- ☐ Lanza `IllegalArgumentException` cuando el email ya existe

---

## Checklist de `UserController.java`

- ☐ Mapeado en `/users`
- ☐ `GET /users` → lista todos los usuarios
- ☐ `POST /users` → crea usuario con `@Valid`, devuelve `201 Created` o `409 Conflict`
- ☐ `GET /users/{id}` → devuelve `200 OK` o `404 Not Found`

---

## Checklist de `Ticket.java` (relaciones)

- ☐ Tiene el campo `createdBy` con `@ManyToOne(fetch = FetchType.LAZY)` y `@JoinColumn(name = "created_by_id")`
- ☐ Tiene el campo `assignedTo` con `@ManyToOne(fetch = FetchType.LAZY)` y `@JoinColumn(name = "assigned_to_id")`
- ☐ **No** hay `@OneToMany` en `Ticket` apuntando a `User` (la dirección es Ticket → User, no al revés)

---

## Checklist de `TicketRequest.java`

- ☐ Tiene el campo `createdByEmail` con `@NotBlank` y `@Email` — requerido para POST
- ☐ **No** tiene `assignedToId` ni `createdById` — la vinculación es por email, la asignación se hace con PATCH

---

## Checklist de `BadRequestException.java`

- ☐ Existe la clase `BadRequestException` en el paquete `exception`
- ☐ Extiende `RuntimeException`
- ☐ Tiene un constructor `BadRequestException(String message)` que llama `super(message)`

---

## Checklist de `AssignTicketRequest.java`

- ☐ Existe el archivo `AssignTicketRequest.java` en el paquete `dto`
- ☐ Tiene el campo `assignedToEmail` con `@Email` (sin `@NotBlank` — null/vacío desasigna)

---

## Checklist de `TicketResult.java` y `UserResult.java`

- ☐ Existen ambos archivos en el paquete `dto`
- ☐ `UserResult` es un record con campos `id`, `name`, `email`
- ☐ `TicketResult` es un record con campos `id`, `title`, `description`, `status`, `createdAt`, `estimatedResolutionDate`, `effectiveResolutionDate`, `createdBy` (UserResult), `assignedTo` (UserResult)

---

## Checklist de `TicketService.java`

- ☐ El constructor recibe tanto `TicketRepository` como `UserRepository`
- ☐ `create()` busca el usuario por email con `userRepository.findByEmail()` (requerido)
- ☐ `create()` lanza `BadRequestException` (400) si el email no existe en el sistema
- ☐ `create()` lanza `IllegalArgumentException` (409) si el título ya existe
- ☐ `create()` **no** asigna `assignedTo` — eso se hace exclusivamente con `assignTicket()`
- ☐ `assignTicket()` asigna o desasigna un usuario a un ticket por email
- ☐ `assignTicket()` lanza `BadRequestException` si el email no existe
- ☐ `assignTicket()` retorna `Optional.empty()` si el ticket no existe → controlador responde 404
- ☐ `updateById()` actualiza título, descripción y estado, pero **no** modifica `createdBy` ni `assignedTo`
- ☐ Existe un método privado `toResult(Ticket)` que convierte la entidad en `TicketResult` con `UserResult` anidado

---

## Checklist de `TicketController.java`

- ☐ `POST /tickets` captura tanto `IllegalArgumentException` (409) como `BadRequestException` (400)
- ☐ `PATCH /tickets/{id}` acepta `AssignTicketRequest` con `@Valid`
- ☐ `PATCH /tickets/{id}` retorna 400 si email inválido, 404 si ticket no existe, 200 si OK

---

## Checklist de pruebas

- ☐ `POST /users` → crea usuario, `201 Created` con el objeto `User` incluyendo `id`
- ☐ `POST /users` con email duplicado → `409 Conflict`
- ☐ `POST /users` con email inválido → `400 Bad Request`
- ☐ `POST /tickets` con `createdByEmail` válido → ticket creado con el objeto `createdBy` anidado
- ☐ `POST /tickets` con `createdByEmail` inexistente → `400 Bad Request`
- ☐ `POST /tickets` sin `createdByEmail` → `400 Bad Request` (campo requerido)
- ☐ `PATCH /tickets/{id}` con `assignedToEmail` válido → ticket actualizado con `assignedTo` anidado
- ☐ `PATCH /tickets/{id}` con `assignedToEmail` vacío → `assignedTo` queda `null`
- ☐ `PATCH /tickets/{id}` con `assignedToEmail` inexistente → `400 Bad Request`
- ☐ `PATCH /tickets/{id}` con id que no existe → `404 Not Found`
- ☐ En la base de datos, las columnas `created_by_id` y `assigned_to_id` tienen los IDs correctos
- ☐ La tabla `users` existe en phpMyAdmin / Supabase con las columnas correctas

---

## Errores comunes

| Error | Causa probable | Solución |
|---|---|---|
| `StackOverflowError` al hacer `GET /tickets` | Se está retornando el entity directamente en vez de un DTO | Verificar que el Service retorne `TicketResult` / `UserResult`, no el entity |
| `could not initialize proxy` | Objeto LAZY accedido fuera de sesión JPA | Asegurarse de acceder a los datos dentro de la transacción del servicio |
| `Column 'created_by_id' cannot be null` | La columna tiene `nullable = false` pero se pasa null | Cambiar `@JoinColumn(name=..., nullable = false)` a `nullable = true` (la FK es opcional) |
| `Table 'users' doesn't exist` | `ddl-auto` no creó la tabla | Verificar que `User` tiene `@Entity` y reiniciar con `ddl-auto: update` |
