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

## Paso 4: crear los DTOs del flujo de datos

En esta lección el Controller ya no pasa `TicketRequest` directamente al Service ni el Service retorna entidades JPA. Necesitas tres nuevas clases en el paquete `dto/`.

### `TicketCommand.java` — input del Service

```java
package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDateTime;

public record TicketCommand(
    String title,
    String description,
    String status,
    LocalDateTime effectiveResolutionDate
) {}
```

### `TicketResult.java` — output del Service

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
    LocalDateTime effectiveResolutionDate
) {}
```

### `TicketResponse.java` — output HTTP al cliente

```java
package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TicketResponse(
    Long id,
    String title,
    String description,
    String status,
    LocalDateTime createdAt,
    LocalDate estimatedResolutionDate,
    LocalDateTime effectiveResolutionDate
) {}
```

> **¿Por qué `TicketResult` y `TicketResponse` son iguales aquí?**
> En esta lección sí son iguales porque `Ticket` aún no tiene relaciones JPA. Cuando en L12 agregues `User` como relación, `TicketResult` expondrá el nombre del usuario como `String` mientras que `TicketResponse` puede formatear o agregar campos calculados. Tenerlos separados desde ahora evita romper la API cuando ocurra ese cambio.

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
  boolean existsByTitleIgnoreCase(String title);

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

> **`existsByTitleIgnoreCase` vs `existsByTitle`**
> Agregar `IgnoreCase` hace la verificación de duplicados insensible a mayúsculas: "Error en login" y "ERROR EN LOGIN" se consideran el mismo título. Siempre que uses `findBy` o `existsBy`, puedes agregar `IgnoreCase` al final del campo.

---

## Paso 6: actualizar `TicketService`

Actualiza `TicketService.java` para recibir `TicketCommand`, usar los métodos de JPA y retornar `TicketResult`:

```java
package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketCommand;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private final TicketRepository repository;

  public TicketService(TicketRepository repository) {
    this.repository = repository;
  }

  public List<TicketResult> getTickets() {
    return this.repository.findAllByOrderByCreatedAtAsc().stream()
        .map(this::toResult)
        .toList();
  }

  public List<TicketResult> getTickets(String statusFilter) {
    if (statusFilter == null || statusFilter.isBlank()) {
      return getTickets();
    }
    return this.repository.findByStatusIgnoreCase(statusFilter).stream()
        .map(this::toResult)
        .toList();
  }

  public TicketResult create(TicketCommand command) {
    if (this.repository.existsByTitleIgnoreCase(command.title())) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título: \"" + command.title() + "\"");
    }
    Ticket ticket = new Ticket();
    ticket.setTitle(command.title());
    ticket.setDescription(command.description());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));
    Ticket saved = this.repository.save(ticket);  // ← persiste en H2
    return toResult(saved);
  }

  public Optional<TicketResult> getById(Long id) {
    return this.repository.findById(id).map(this::toResult);
  }

  public boolean deleteById(Long id) {
    if (this.repository.existsById(id)) {
      this.repository.deleteById(id);
      return true;
    }
    return false;
  }

  public Optional<TicketResult> updateById(Long id, TicketCommand command) {
    Optional<Ticket> found = this.repository.findById(id);
    if (found.isEmpty()) {
      return Optional.empty();
    }
    Ticket toUpdate = found.get();
    toUpdate.setTitle(command.title());
    toUpdate.setDescription(command.description());
    if (command.status() != null && !command.status().isBlank()) {
      toUpdate.setStatus(command.status());
    }
    toUpdate.setEffectiveResolutionDate(command.effectiveResolutionDate());
    Ticket saved = this.repository.save(toUpdate);
    return Optional.of(toResult(saved));
  }

  private TicketResult toResult(Ticket ticket) {
    return new TicketResult(
        ticket.getId(),
        ticket.getTitle(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getCreatedAt(),
        ticket.getEstimatedResolutionDate(),
        ticket.getEffectiveResolutionDate()
    );
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

## Paso 7: actualizar `TicketController`

El Controller convierte `TicketRequest` → `TicketCommand` antes de llamar al Service, y convierte `TicketResult` → `TicketResponse` antes de retornar al cliente:

```java
@PostMapping
public ResponseEntity<Object> create(@Valid @RequestBody TicketRequest request) {
  try {
    TicketCommand command = toCommand(request);
    TicketResult result = this.service.create(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
  } catch (IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
  }
}

// Conversión Request → Command
private TicketCommand toCommand(TicketRequest request) {
  return new TicketCommand(
      request.title(),
      request.description(),
      request.status(),
      request.effectiveResolutionDate()
  );
}

// Conversión Result → Response
private TicketResponse toResponse(TicketResult result) {
  return new TicketResponse(
      result.id(),
      result.title(),
      result.description(),
      result.status(),
      result.createdAt(),
      result.estimatedResolutionDate(),
      result.effectiveResolutionDate()
  );
}
```

Los endpoints GET y PUT siguen el mismo patrón: reciben `TicketRequest`, convierten a `TicketCommand`, llaman al Service, convierten `TicketResult` a `TicketResponse`.

---

## Paso 8: sembrar datos iniciales con `DataInitializer`

Para que la aplicación arranque con tickets de ejemplo, crea la clase `DataInitializer` en el paquete `config/`:

```java
package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final TicketRepository ticketRepository;

  public DataInitializer(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (ticketRepository.count() == 0) {
      LocalDateTime now = LocalDateTime.now();
      LocalDate estimated = LocalDate.now().plusDays(5);

      Ticket t1 = new Ticket();
      t1.setTitle("Error en login");
      t1.setDescription("No se puede iniciar sesión con Google");
      t1.setStatus("NEW");
      t1.setCreatedAt(now);
      t1.setEstimatedResolutionDate(estimated);
      ticketRepository.save(t1);

      Ticket t2 = new Ticket();
      t2.setTitle("Mejora en dashboard");
      t2.setDescription("Agregar gráficos de estadísticas");
      t2.setStatus("IN_PROGRESS");
      t2.setCreatedAt(now);
      t2.setEstimatedResolutionDate(estimated);
      ticketRepository.save(t2);
    }
  }
}
```

> **¿Qué hace `CommandLineRunner`?**
> Es una interfaz de Spring Boot. El método `run()` se ejecuta automáticamente una vez que la aplicación arranca y el contexto está listo. Es el lugar ideal para sembrar datos iniciales.

> **¿Por qué el `if (count() == 0)`?**
> Evita duplicar los datos si usas `ddl-auto: update` (que no borra la tabla al reiniciar). Con `create-drop` no es estrictamente necesario, pero es buena práctica defensiva.

---

## Paso 9: verificar que la aplicación arranca

Ejecuta:

```bash
./mvnw spring-boot:run
```

En la consola deberías ver:

1. Mensajes de Hibernate creando la tabla:
   ```sql
   create table tickets (
       id bigint generated by default as identity,
       created_at timestamp(6),
       description text not null,
       ...
       primary key (id)
   )
   ```
2. Los `INSERT` del `DataInitializer` (gracias a `show-sql: true`)
3. El banner de la aplicación y el mensaje `Started TicketsApplication`

---

## Paso 10: probar que los datos persisten con H2

Con `ddl-auto: create-drop` los datos **no** persisten entre reinicios (H2 borra la tabla al cerrar). Esto es esperado en desarrollo.

### Consultar los tickets (datos sembrados)

```
GET http://localhost:8080/ticket-app/tickets
```

Resultado esperado: los dos tickets del `DataInitializer` aparecen en la lista.

### Crear un ticket nuevo

```
POST http://localhost:8080/ticket-app/tickets
Content-Type: application/json

{
  "title": "Nuevo ticket de prueba",
  "description": "Verifica que JPA persiste correctamente"
}
```

Resultado esperado: `201 Created` con el ticket en el body (con `id` asignado por la base de datos).

### Ver la base de datos en el navegador

Accede a `http://localhost:8080/ticket-app/h2-console` con:
- JDBC URL: `jdbc:h2:mem:tickets_db`
- User: `sa`
- Password: (vacío)

Ejecuta `SELECT * FROM TICKETS;` para ver los datos directamente en la tabla.

---

## Paso 11: reflexiona antes de cerrar

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
