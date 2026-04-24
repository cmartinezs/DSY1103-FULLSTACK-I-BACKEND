# Lección 10 — Checklist y rúbrica mínima

Usa esta lista para verificar que la migración a JPA está completa antes de continuar.

---

## Checklist del `pom.xml`

- ☐ Tiene la dependencia `spring-boot-starter-data-jpa`
- ☐ Tiene la dependencia `h2` con `scope runtime`

---

## Checklist de `application.yml`

- ☐ `spring.datasource.url` apunta a `jdbc:h2:mem:tickets_db`
- ☐ `spring.datasource.driver-class-name` es `org.h2.Driver`
- ☐ `spring.jpa.hibernate.ddl-auto` está configurado como `create-drop`
- ☐ `spring.jpa.show-sql` es `true` (para aprendizaje)
- ☐ `spring.h2.console.enabled` es `true`

---

## Checklist de `Ticket.java`

- ☐ La clase tiene `@Entity` y `@Table(name = "tickets")`
- ☐ El campo `id` tiene `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- ☐ **No** hay `@Min` ni `@Max` sobre el campo `id` (el ID lo asigna la base de datos)
- ☐ El campo `title` tiene `@Column(nullable = false, length = 50)`
- ☐ El campo `description` tiene `@Column(nullable = false, columnDefinition = "TEXT")`
- ☐ El campo `status` tiene `@Column(nullable = false, length = 20)`
- ☐ Los campos de fecha tienen `@Column(name = "...")` con nombre en snake_case
- ☐ La clase sigue teniendo `@NoArgsConstructor` (requerido por JPA)
- ☐ Las importaciones son de `jakarta.persistence.*` (no `javax.persistence.*`)

---

## Checklist de `TicketRepository.java`

- ☐ Es una **interfaz** (no una clase)
- ☐ Extiende `JpaRepository<Ticket, Long>`
- ☐ Tiene el método `boolean existsByTitle(String title)`
- ☐ Tiene el método `List<Ticket> findByStatusIgnoreCase(String status)`
- ☐ Tiene el método `List<Ticket> findAllByOrderByCreatedAtAsc()`
- ☐ **No** tiene campos como `Map<Long, Ticket> db` ni `long currentId` (eso era la versión manual)

---

## Checklist de `TicketService.java`

- ☐ `getTickets(String status)` usa `findAllByOrderByCreatedAtAsc()` cuando `status` es null/blank
- ☐ `getTickets(String status)` usa `findByStatusIgnoreCase(status)` cuando `status` tiene valor
- ☐ `create(TicketRequest request)` verifica duplicados con `existsByTitle()` y luego llama a `save()`
- ☐ `getById(Long id)` retorna `repository.findById(id)` (devuelve `Optional<Ticket>`)
- ☐ `deleteById(Long id)` usa `existsById()` + `deleteById()`
- ☐ `updateById(Long id, TicketRequest request)` usa `findById().map(...)` + `save(ticket)`
- ☐ El Service **no** asigna el `id` manualmente (eso lo hace la base de datos)

---

## Checklist de pruebas

- ☐ La aplicación arrêté sin errores (`./mvnw spring-boot:run`)
- ☐ En la consola se ve el SQL de creación de la tabla `tickets`
- ☐ `POST /ticket-app/tickets` crea un ticket
- ☐ `GET /ticket-app/tickets` devuelve los tickets
- ☐ Al reiniciar la aplicación (con `ddl-auto: create-drop`), los datos se pierden (comportamiento esperado)

---

## Errores comunes

| Error | Causa probable | Solución |
|---|---|---|
| `Unable to create bean 'entityManagerFactory'` | Anotaciones JPA incorrectas o falta `@NoArgsConstructor` | Revisar la clase `Ticket` |
| `No property 'status' found for type 'Ticket'` | El nombre del campo en el método del repositorio no coincide con el campo de la clase | Verificar que el campo en `Ticket` se llame exactamente `status` |
| Importaciones de `javax.persistence.*` | Versión incorrecta del paquete | Cambiar a `jakarta.persistence.*` |
