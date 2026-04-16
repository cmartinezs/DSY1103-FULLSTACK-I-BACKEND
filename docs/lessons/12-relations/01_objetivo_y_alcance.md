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
3. `@Column` con personalización de nombres y restricciones
4. `@JoinColumn` para nombrar explícitamente las claves foráneas
5. Endpoints para crear usuarios y para crear tickets asociados a usuarios

### Lo que vas a poder explicar

- ¿Qué significa `@ManyToOne` y en qué lado de la relación va?
- ¿Qué hace `@JoinColumn` y por qué se necesita?
- ¿Qué es el "lado dueño" de una relación JPA?
- ¿Qué hace `@OneToMany(mappedBy = "...")` y por qué no tiene clave foránea?
- ¿Por qué `@Table(name = "users")` y no `@Table(name = "user")`?

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
├── model/
│   ├── Ticket.java              ← con @ManyToOne a User (createdBy, assignedTo)
│   └── User.java                ← nueva entidad JPA
├── respository/
│   ├── TicketRepository.java
│   └── UserRepository.java      ← nuevo
├── service/
│   ├── TicketService.java       ← actualizado para resolver usuarios
│   └── UserService.java         ← nuevo
├── controller/
│   ├── TicketController.java    ← actualizado
│   └── UserController.java      ← nuevo
└── dto/
    ├── TicketRequest.java       ← actualizado con createdById y assignedToId
    └── UserRequest.java         ← nuevo
```

---

## ¿Qué NO cubre esta lección?

| Tema | ¿Cuándo se ve? |
|---|---|
| Tabla de historial de cambios | Lección 13 |
| `@ManyToMany` (relación muchos a muchos) | Fuera del alcance del curso |
| `fetch = LAZY` vs `EAGER` y el problema N+1 | Mención breve en el archivo conceptual |
| DTOs de respuesta con datos anidados | Se usa `@JsonIgnore` para evitar serialización circular en esta etapa |
