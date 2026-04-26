# Lección 12 — Relaciones entre entidades: usuario creador y usuario asignado

## ¿De dónde venimos?

Tu aplicación persiste tickets en base de datos real. Pero todos los tickets son anónimos: nadie sabe quién los creó ni quién está trabajando en ellos.

En un sistema de soporte real, eso no es aceptable.

---

## El problema que resolvemos

Un ticket tiene dos relaciones con personas:

- **Creador:** quién reportó el problema. Se asigna al crear el ticket y no cambia.
- **Asignado:** el técnico que está trabajando en él. Puede cambiar mientras el ticket está abierto.

En la base de datos, esto se representa con **claves foráneas** (foreign keys):

```
tabla tickets                tabla users
─────────────                ────────────
id                           id
title                        name
description                  email
status
created_by_id  ──────────►  id   (FK: quién lo creó)
assigned_to_id ──────────►  id   (FK: quién está asignado)
```

En Java, JPA traduce estas claves foráneas en referencias directas entre objetos.

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás:

1. Una nueva entidad `User` con su repositorio, servicio y controlador
2. La entidad `Ticket` con dos relaciones `@ManyToOne` a `User`
   - `createdBy`: el usuario que creó el ticket (requerido, se vincula por email)
   - `assignedTo`: el usuario asignado al ticket (opcional, se asigna con PATCH)
3. La entidad `User` con dos relaciones `@OneToMany` (el lado inverso de `@ManyToOne`):
   - `createdTickets`: tickets que el usuario ha creado
   - `assignedTickets`: tickets asignados al usuario
4. `@Column` con personalización de nombres y restricciones
5. `@JoinColumn` para nombrar explícitamente las claves foráneas
6. Endpoints para crear usuarios (`POST /users`) y crear/asignar tickets (`POST /tickets`, `PATCH /tickets/{id}`)
7. DTOs de respuesta (`TicketResult`, `UserResult`) para exponer datos anidados sin serialización circular
8. Excepción personalizada `BadRequestException` para distinguir errores de negocio (409) de errores de cliente (400)

### Lo que vas a poder explicar

- ¿Qué es el "lado dueño" de una relación JPA?
- ¿Qué significa `@ManyToOne` y en qué lado de la relación va?
- ¿Qué significa `@OneToMany` y por qué usa `mappedBy`?
- ¿Qué hace `@JoinColumn` y por qué se necesita?
- ¿Por qué `@Table(name = "users")` y no `@Table(name = "user")`?
- ¿Cuál es la diferencia entre el lado "uno" y el lado "muchos" de una relación?
- ¿Cuándo usar `@OneToOne` en lugar de `@ManyToOne`?
- ¿Por qué `@ManyToMany` casi nunca se usa si la base de datos está normalizada (3FN)?

---

## Nuevos requerimientos

| Requerimiento | Descripción |
|---|---|
| **REQ-16** | Cada ticket debe registrar qué usuario lo creó |
| **REQ-17** | Cada ticket puede ser asignado a un usuario; la asignación puede cambiar |

---

## La estructura que tienes al comenzar

```
src/main/java/cl/duoc/fullstack/tickets/
├── model/
│   └── Ticket.java              ← entidad JPA sin relaciones
├── respository/
│   └── TicketRepository.java
├── service/
│   └── TicketService.java
└── controller/
    └── TicketController.java
```

La estructura al terminar:

```
src/main/java/cl/duoc/fullstack/tickets/
├── exception/
│   └── BadRequestException.java     ← nueva excepción personalizada
├── model/
│   ├── Ticket.java              ← con @ManyToOne a User (createdBy, assignedTo)
│   └── User.java                ← nueva entidad con @OneToMany (createdTickets, assignedTickets)
├── respository/
│   ├── TicketRepository.java
│   └── UserRepository.java      ← nuevo, incluye findByEmail()
├── service/
│   ├── TicketService.java       ← actualizado: busca usuario por email, nuevo assignTicket()
│   └── UserService.java         ← nuevo
├── controller/
│   ├── TicketController.java    ← actualizado: POST acepta email, nuevo PATCH /tickets/{id}
│   └── UserController.java      ← nuevo
└── dto/
    ├── TicketRequest.java       ← actualizado con createdByEmail
    ├── TicketResult.java        ← nuevo DTO de respuesta con UserResult anidado
    ├── AssignTicketRequest.java ← nuevo DTO para PATCH
    ├── UserRequest.java         ← nuevo
    └── UserResult.java          ← nuevo DTO de respuesta de usuario
```

---

## ¿Qué NO cubre esta lección?

| Tema | ¿Cuándo se ve? |
|---|---|
| Tabla de historial de cambios | Lección 13 |
| `@ManyToMany` en profundidad | Se menciona en esta lección junto a la razón por la que no la usamos (3FN) |
| `fetch = LAZY` vs `EAGER` y el problema N+1 | Se explica en el archivo conceptual de esta lección |
| DTOs de respuesta con datos anidados | Cubierto en esta lección con `TicketResult` y `UserResult` |
