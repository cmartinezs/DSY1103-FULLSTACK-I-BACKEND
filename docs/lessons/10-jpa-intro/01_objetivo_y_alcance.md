# Lección 10 — JPA y ORM: del Map a la base de datos

## ¿De dónde venimos?

En la lección 09 refactorizaste el repositorio para usar `Map<Long, Ticket>` con acceso O(1). Tu API:

- Almacena tickets en memoria con búsqueda eficiente por clave
- Filtra por estado con `?status=`
- Sigue el patrón CSR con responsabilidades bien delimitadas

Pero hay un problema crítico: **cuando la aplicación se reinicia, todos los datos desaparecen**. El `HashMap` vive en la memoria del proceso y muere con él.

Para que los datos sobrevivan reinicios necesitas una base de datos real. Eso es exactamente lo que esta lección resuelve.

---

## ¿Qué es JPA y qué problema resuelve?

**JPA** (Jakarta Persistence API) es la especificación de Java para mapear objetos a tablas de base de datos.

**Hibernate** es la implementación más usada de JPA. Spring Boot lo incluye automáticamente cuando agregas la dependencia correspondiente.

El problema que resuelve se llama "desajuste de impedancia": el código Java trabaja con **objetos**, las bases de datos almacenan **filas en tablas**. JPA actúa como **traductor automático**:

```
Java (objeto)               JPA traduce              Base de datos (tabla)
──────────────              ───────────              ─────────────────────
clase Ticket           →    CREATE TABLE             tabla tickets
campo title            →    columna title            VARCHAR(50)
repository.save(t)     →    INSERT INTO tickets      nueva fila en disco
repository.findById(1) →    SELECT * WHERE id=1      fila recuperada
```

No escribes SQL. JPA lo genera por ti según las anotaciones que agregas a tus clases.

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás:

1. La dependencia `spring-boot-starter-data-jpa` agregada al `pom.xml`
2. La clase `Ticket` anotada como entidad JPA (`@Entity`, `@Id`, `@GeneratedValue`, `@Column`)
3. `TicketRepository` convertido de **clase** a **interfaz** que extiende `JpaRepository`
4. `TicketService` actualizado para usar los métodos que Spring Data JPA provee automáticamente
5. La aplicación funcionando con base de datos H2 (en memoria)

### Lo que vas a poder explicar

- ¿Qué hace `@Entity` en una clase?
- ¿Qué es `@Id` y por qué no puede faltar en una entidad?
- ¿Qué genera `@GeneratedValue(strategy = GenerationType.IDENTITY)`?
- ¿Qué métodos vienen incluidos en `JpaRepository<Ticket, Long>`?
- ¿Por qué el repositorio ahora es una **interfaz** y no una clase?

---

## Nuevo requerimiento

| Requerimiento | Descripción |
|---|---|
| **REQ-15** | Los tickets deben persistirse en base de datos real: los datos sobreviven reinicios de la aplicación |

---

## La estructura que tienes al comenzar

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java
├── dto/
│   └── TicketRequest.java
├── model/
│   ├── Ticket.java              ← POJO Lombok, sin anotaciones JPA
│   └── ErrorResponse.java
├── respository/
│   └── TicketRepository.java   ← clase con Map<Long, Ticket>
├── service/
│   └── TicketService.java
└── TicketsApplication.java
```

La estructura al terminar:

```
src/main/java/cl/duoc/fullstack/tickets/
├── controller/
│   └── TicketController.java   ← sin cambios
├── dto/
│   └── TicketRequest.java      ← sin cambios
├── model/
│   ├── Ticket.java             ← con @Entity, @Id, @GeneratedValue, @Column
│   └── ErrorResponse.java      ← sin cambios
├── respository/
│   └── TicketRepository.java   ← ahora es una interfaz que extiende JpaRepository
├── service/
│   └── TicketService.java      ← usa métodos de JpaRepository
└── TicketsApplication.java
```

---

## ¿Qué NO cubre esta lección?

| Tema | ¿Cuándo se ve? |
|---|---|
| MySQL (XAMPP) | Lección 11 |
| Configurar Supabase (PostgreSQL en la nube) | Lección 11 |
| Relaciones entre tablas (`@ManyToOne`, `@OneToMany`) | Lección 12 |
| Tabla de historial de cambios | Lección 13 |
| Paginación (`Pageable`) | Fuera del alcance del curso |
| JPQL y consultas personalizadas complejas | Fuera del alcance del curso |
