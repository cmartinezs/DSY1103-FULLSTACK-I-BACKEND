# Lección 10 — Tutorial paso a paso: migrar a JPA con H2

Sigue esta guía en orden. Al finalizar, tu aplicación guardará los tickets en una base de datos H2 (en memoria para desarrollo) en lugar de un `HashMap` en memoria.

---

## Paso 1: agregar las dependencias en `pom.xml`

Abre `pom.xml` y agrega dentro de `<dependencies>`:

```xml
<!-- Spring Data JPA: incluye Hibernate como implementación -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Driver H2 (base de datos en memoria) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

> **¿Por qué H2?**
> H2 es una base de datos en memoria escrita en Java. No requiere instalación externa, es ideales para desarrollo y testing. Los datos se pierden al cerrar la aplicación (a menos que uses modo archivo).

---

## Paso 2: configurar `application.yml`

Reemplaza el contenido de `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: Tickets

  datasource:
    url: jdbc:h2:mem:tickets_db
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"
```

> **¿Qué es `ddl-auto: create-drop`?**
> - `create`: crea las tablas al iniciar
> - `drop`: las borra al cerrar
> Es útil para desarrollo. Los datos no persisten entre ejecuciones (se pierden al cerrar la app).
>
> Para datos persistentes usa `jdbc:h2:file:./data/tickets_db`

> **¿Para qué sirve la consola H2?**
> Accede en `http://localhost:8080/ticket-app/h2-console` para ver la base de datos desde el navegador.Útil para debugging.

---

## Paso 3: anotar `Ticket` como entidad JPA

Abre `Ticket.java` y modifícala así:

```java
package cl.duoc.fullstack.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El titulo es requerido")
  @Size(max = 50)
  @Column(nullable = false, length = 50)
  private String title;

  @NotBlank
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "estimated_resolution_date")
  private LocalDate estimatedResolutionDate;

  @Column(name = "effective_resolution_date")
  private LocalDateTime effectiveResolutionDate;
}
```

**Cambios respecto a la versión anterior:**

| Qué cambió | Por qué |
|---|---|
| Se eliminó `@Min(5) @Max(100)` del id | El id lo asigna la base de datos; no tiene sentido validar su valor antes de guardarlo |
| Se agregó `@Entity` | Le dice a JPA "esta clase es una tabla" |
| Se agregó `@Table(name = "tickets")` | Define el nombre exacto de la tabla en la base de datos |
| Se agregó `@Id` | Marca cuál campo es la clave primaria |
| Se agregó `@GeneratedValue(strategy = GenerationType.IDENTITY)` | La base de datos genera el ID automáticamente (AUTO_INCREMENT en MySQL) |
| Se agregaron `@Column(...)` | Personalizan las columnas: nombre, si acepta nulo, longitud máxima |

> **¿Por qué `@NoArgsConstructor` es obligatorio con JPA?**
> Hibernate necesita crear instancias de la entidad usando el constructor sin argumentos para poder poblarla con los datos de la base de datos. Sin `@NoArgsConstructor`, JPA lanzará un error al arrancar.

---

## Paso 5: convertir `TicketRepository` a interfaz

Reemplaza completamente el contenido de `TicketRepository.java`:

```java
package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

  // Spring Data JPA genera el SQL automáticamente a partir del nombre del método
  boolean existsByTitle(String title);

  List<Ticket> findByStatusIgnoreCase(String status);

  List<Ticket> findAllByOrderByCreatedAtAsc();
}
```

> **¿Por qué ahora es una interfaz y no una clase?**
> Spring Data JPA genera en tiempo de ejecución una implementación completa de esta interfaz. Tú defines **qué** quieres hacer (los métodos), Spring Data JPA decide **cómo** hacerlo (el SQL). No necesitas escribir ni una línea de acceso a datos.

> **¿De dónde vienen los métodos `findById`, `save`, `deleteById`, `existsById`?**
> Los hereda `JpaRepository<Ticket, Long>`. El primer tipo (`Ticket`) es la entidad; el segundo (`Long`) es el tipo del ID. Todos estos métodos ya vienen implementados.

> **¿Cómo sabe Spring Data que `findByStatusIgnoreCase` busca por el campo `status`?**
> Interpreta el nombre del método: `findBy` + `Status` (campo) + `IgnoreCase` (modificador). Es una convención que el framework entiende y traduce a `SELECT * FROM tickets WHERE LOWER(status) = LOWER(?)`.

---

## Paso 6: actualizar `TicketService`

Actualiza `TicketService.java` para usar los métodos de JPA:

```java
package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private TicketRepository repository;

  public TicketService(TicketRepository repository) {
    this.repository = repository;
  }

  public List<Ticket> getTickets(String status) {
    if (status == null || status.isBlank()) {
      return repository.findAllByOrderByCreatedAtAsc();
    }
    return repository.findByStatusIgnoreCase(status);
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
    return repository.save(ticket);  // ← ahora persiste en MySQL
  }

  public Optional<Ticket> getById(Long id) {
    return repository.findById(id);
  }

  public boolean deleteById(Long id) {
    if (!repository.existsById(id)) {
      return false;
    }
    repository.deleteById(id);
    return true;
  }

  public Optional<Ticket> updateById(Long id, TicketRequest request) {
    return repository.findById(id).map(ticket -> {
      ticket.setTitle(request.getTitle());
      ticket.setDescription(request.getDescription());
      if (request.getStatus() != null && !request.getStatus().isBlank()) {
        ticket.setStatus(request.getStatus());
      }
      return repository.save(ticket);
    });
  }
}
```

**Cambios clave respecto a la versión anterior:**

| Método | Antes (Map) | Ahora (JPA) |
|---|---|---|
| `getTickets` | `db.values()` + sort manual | `findAllByOrderByCreatedAtAsc()` |
| `create` | `db.put(currentId++, ticket)` | `repository.save(ticket)` |
| `getById` | `Optional.ofNullable(db.get(id))` | `repository.findById(id)` |
| `deleteById` | `db.remove(id) != null` | `existsById(id)` + `deleteById(id)` |
| `updateById` | busca + modifica en Map | `findById(id).map(...)` + `save(ticket)` |

> **¿Por qué `repository.save(ticket)` sirve tanto para crear como para actualizar?**
> `save()` revisa si el objeto tiene ID asignado:
> - Sin ID (null): ejecuta `INSERT` → crea un registro nuevo
> - Con ID: ejecuta `UPDATE` → actualiza el registro existente

---

## Paso 7: verificar que la aplicación arranca

Ejecuta:

```bash
./mvnw spring-boot:run
```

En la consola deberías ver:

1. Mensajes de Hibernate creando la tabla:
   ```sql
   create table tickets (
       id bigint not null auto_increment,
       created_at datetime(6),
       description text not null,
       ...
       primary key (id)
   )
   ```
2. El banner de la aplicación y el mensaje `Started TicketsApplication`

Si ves errores de conexión, verifica que MySQL está corriendo en XAMPP y que el nombre de la base de datos es `tickets_db`.

---

## Paso 8: probar que los datos persisten

### Crear un ticket

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Ticket persistido",
  "description": "Este ticket sobrevive reinicios"
}
```

Resultado esperado: `201 Created`

### Reiniciar la aplicación

Detén la app con `Ctrl+C` y vuelve a ejecutar `./mvnw spring-boot:run`.

### Consultar los tickets

```
GET http://localhost:8080/ticket-app/tickets
```

Resultado esperado: el ticket creado antes del reinicio aparece en la lista. Los datos persistieron en MySQL.

---

## Paso 9: reflexiona antes de cerrar

1. ¿Qué diferencia hay entre `ddl-auto: create-drop` y `ddl-auto: update`? ¿Cuál usarías en producción?
2. Antes, `TicketRepository` era una clase con 150 líneas. Ahora es una interfaz con 3 métodos. ¿Quién escribe el código que falta?
3. ¿Qué pasa si quitas `@NoArgsConstructor` de `Ticket` y reinicias la aplicación?
4. ¿Cómo sabe Spring Data JPA que `findByStatusIgnoreCase` busca por el campo `status` y no por `title`?

---

## ¿Qué sigue?

| Lección | Contenido |
|---------|----------|
| 11 | MySQL (XAMPP) y PostgreSQL (Supabase) con perfiles |
| 12 | User entity y relaciones ManyToOne |
| 13 | TicketHistory para historial de cambios |
