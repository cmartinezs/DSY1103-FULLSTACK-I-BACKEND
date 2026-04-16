# Lección 12 — Checklist y rúbrica mínima

---

## Checklist de `User.java`

- ☐ La clase tiene `@Entity` y `@Table(name = "users")` (plural, evita conflicto con palabra reservada SQL)
- ☐ El campo `id` tiene `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- ☐ El campo `name` tiene `@Column(nullable = false, length = 100)`
- ☐ El campo `email` tiene `@Column(nullable = false, unique = true, length = 150)` y `@Email`
- ☐ La clase tiene `@NoArgsConstructor` (requerido por JPA)
- ☐ Todas las importaciones son de `jakarta.persistence.*`

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
- ☐ Tiene `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` en ambos campos de relación
- ☐ **No** hay `@OneToMany` en `Ticket` apuntando a `User` (la dirección es Ticket → User, no al revés)

---

## Checklist de `TicketRequest.java`

- ☐ Tiene los campos `createdById` (`Long`) y `assignedToId` (`Long`)
- ☐ Ambos son opcionales (sin `@NotNull`) — un ticket puede crearse sin usuarios

---

## Checklist de `TicketService.java`

- ☐ El constructor recibe tanto `TicketRepository` como `UserRepository`
- ☐ `create()` resuelve `createdById` y `assignedToId` consultando `userRepository.findById()` si no son null
- ☐ `create()` lanza `IllegalArgumentException` si el ID de usuario no existe en la base de datos
- ☐ `updateById()` permite actualizar `assignedToId` pero **no** el `createdBy` (quien crea no cambia)

---

## Checklist de pruebas

- ☐ `POST /users` → crea usuario, `201 Created` con el objeto `User` incluyendo `id`
- ☐ `POST /users` con email duplicado → `409 Conflict`
- ☐ `POST /users` con email inválido → `400 Bad Request`
- ☐ `POST /tickets` con `createdById` válido → ticket creado con el usuario vinculado
- ☐ `POST /tickets` sin `createdById` → ticket creado con `createdBy: null`
- ☐ `POST /tickets` con `createdById` inexistente → `400 Bad Request` o `404`
- ☐ `PUT /tickets/{id}` con `assignedToId` → actualiza el usuario asignado
- ☐ En la base de datos, las columnas `created_by_id` y `assigned_to_id` tienen los valores correctos
- ☐ La tabla `users` existe en phpMyAdmin / Supabase con las columnas correctas

---

## Errores comunes

| Error | Causa probable | Solución |
|---|---|---|
| `StackOverflowError` al hacer `GET /tickets` | Bucle infinito de serialización | Agregar `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` en los campos `@ManyToOne` |
| `could not initialize proxy` | Objeto LAZY accedido fuera de sesión JPA | Asegurarse de acceder a los datos dentro de la transacción del servicio |
| `Column 'created_by_id' cannot be null` | La columna tiene `nullable = false` pero se pasa null | Cambiar `@JoinColumn(name=..., nullable = false)` a `nullable = true` (la FK es opcional) |
| `Table 'users' doesn't exist` | `ddl-auto` no creó la tabla | Verificar que `User` tiene `@Entity` y reiniciar con `ddl-auto: update` |
| `No serializer found for class org.hibernate.proxy` | Mismo problema de serialización LAZY | Agregar `@JsonIgnoreProperties` o configurar `spring.jackson.serialization.fail-on-empty-beans=false` |
