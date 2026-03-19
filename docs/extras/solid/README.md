# 🧱 Principios SOLID

## ¿Qué son los principios SOLID?

**SOLID** es un acrónimo que agrupa cinco principios de diseño orientado a objetos formulados por Robert C. Martin ("Uncle Bob"). Aplicarlos produce código que es:

- **Fácil de mantener** — los cambios tienen un impacto localizado
- **Fácil de extender** — agregar funcionalidades no rompe lo existente
- **Fácil de testear** — las clases son pequeñas y cohesivas
- **Fácil de entender** — cada clase tiene una responsabilidad clara

> 📌 Spring Boot aplica estos principios de forma natural con su arquitectura de capas (`@Controller`, `@Service`, `@Repository`) y el sistema de inyección de dependencias (`@Autowired`, `@RequiredArgsConstructor`).

---

## Los 5 principios

| Letra | Principio | Idea en una frase |
|-------|-----------|-------------------|
| **S** | Single Responsibility | Una clase = una razón para cambiar |
| **O** | Open/Closed | Abierta para extensión, cerrada para modificación |
| **L** | Liskov Substitution | Las subclases deben poder reemplazar a su clase base |
| **I** | Interface Segregation | Interfaces pequeñas y específicas |
| **D** | Dependency Inversion | Depender de abstracciones, no de implementaciones concretas |

---

## S — Single Responsibility Principle (SRP)

> *"Una clase debe tener una sola razón para cambiar."*

Cada clase debe tener **una única responsabilidad**. Si una clase hace demasiadas cosas, un cambio en cualquiera de ellas obliga a modificar esa clase, aumentando el riesgo de introducir errores.

### ❌ Violación de SRP

```java
// Esta clase hace demasiado: lógica de negocio + acceso a datos + envío de emails
public class TicketService {

    public void crearTicket(Ticket ticket) {
        // 1. Validar
        if (ticket.getTitle() == null || ticket.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }

        // 2. Guardar en base de datos (responsabilidad del repositorio)
        String sql = "INSERT INTO tickets (title, status) VALUES (?, ?)";
        // ... lógica JDBC directamente aquí ...

        // 3. Enviar email (responsabilidad de un servicio de notificaciones)
        String mensaje = "Tu ticket '" + ticket.getTitle() + "' fue creado.";
        // ... lógica de envío de email directamente aquí ...
    }
}
```

### ✅ Aplicando SRP

```java
// Cada clase tiene UNA responsabilidad

@Repository
public class TicketRepository {       // responsabilidad: acceso a datos
    public Ticket save(Ticket ticket) { /* ... */ }
    public Optional<Ticket> findById(Long id) { /* ... */ }
}

@Service
public class EmailService {           // responsabilidad: notificaciones
    public void enviarConfirmacion(String email, String titulo) { /* ... */ }
}

@Service
@RequiredArgsConstructor
public class TicketService {          // responsabilidad: lógica de negocio
    private final TicketRepository ticketRepository;
    private final EmailService emailService;

    public Ticket crear(Ticket ticket) {
        validar(ticket);
        Ticket guardado = ticketRepository.save(ticket);
        emailService.enviarConfirmacion(ticket.getUserEmail(), ticket.getTitle());
        return guardado;
    }

    private void validar(Ticket ticket) {
        if (ticket.getTitle() == null || ticket.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
    }
}
```

> 💡 En Spring Boot, la arquitectura Controller → Service → Repository **aplica SRP por diseño**: cada capa tiene exactamente una responsabilidad.

---

## O — Open/Closed Principle (OCP)

> *"Las entidades de software deben estar abiertas para extensión, pero cerradas para modificación."*

Puedes **agregar** nuevo comportamiento sin **modificar** el código existente. Esto se logra principalmente mediante **interfaces** y **polimorfismo**.

### ❌ Violación de OCP

```java
// Cada vez que se agrega un nuevo tipo de notificación, hay que modificar esta clase
public class NotificadorService {

    public void notificar(String tipo, String destinatario, String mensaje) {
        if ("EMAIL".equals(tipo)) {
            // lógica de email...
        } else if ("SMS".equals(tipo)) {
            // lógica de SMS...
        } else if ("PUSH".equals(tipo)) {
            // lógica de notificación push...
            // cada nuevo tipo requiere modificar este método ❌
        }
    }
}
```

### ✅ Aplicando OCP

```java
// Contrato cerrado para modificación
public interface Notificador {
    void notificar(String destinatario, String mensaje);
}

// Implementaciones cerradas para modificación, pero la familia es abierta para extensión
@Service("emailNotificador")
public class EmailNotificador implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("Email a " + destinatario + ": " + mensaje);
    }
}

@Service("smsNotificador")
public class SmsNotificador implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("SMS a " + destinatario + ": " + mensaje);
    }
}

// Si necesito WhatsApp, solo agrego una nueva clase — no modifico nada existente ✅
@Service("whatsappNotificador")
public class WhatsAppNotificador implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("WhatsApp a " + destinatario + ": " + mensaje);
    }
}
```

---

## L — Liskov Substitution Principle (LSP)

> *"Los objetos de una subclase deben poder reemplazar a los de su clase base sin alterar el comportamiento correcto del programa."*

Si una clase `B` extiende a `A`, debería poder usarse en cualquier lugar donde se use `A`, sin efectos inesperados.

### ❌ Violación de LSP

```java
public class TicketRepository {
    public List<Ticket> findAll() {
        return /* todos los tickets */;
    }
}

public class TicketReadOnlyRepository extends TicketRepository {
    @Override
    public List<Ticket> findAll() {
        return /* solo los tickets del usuario actual */;
        // ⚠️ El comportamiento cambió: findAll() ya no devuelve TODOS los tickets
        // Quien esperaba la clase base obtendrá resultados inesperados
    }
}
```

### ✅ Aplicando LSP

```java
// Contrato claro mediante interfaces
public interface TicketReader {
    List<Ticket> findAll();
    Optional<Ticket> findById(Long id);
}

public interface TicketWriter {
    Ticket save(Ticket ticket);
    void deleteById(Long id);
}

// Implementación completa: puede usarse donde se espere TicketReader o TicketWriter
@Repository
public class TicketJpaRepository implements TicketReader, TicketWriter {
    @Override public List<Ticket> findAll() { /* ... */ }
    @Override public Optional<Ticket> findById(Long id) { /* ... */ }
    @Override public Ticket save(Ticket ticket) { /* ... */ }
    @Override public void deleteById(Long id) { /* ... */ }
}

// Implementación solo de lectura: cumple el contrato de TicketReader íntegramente
public class TicketCacheRepository implements TicketReader {
    @Override public List<Ticket> findAll() { /* devuelve desde caché */ }
    @Override public Optional<Ticket> findById(Long id) { /* desde caché */ }
}
```

---

## I — Interface Segregation Principle (ISP)

> *"Los clientes no deberían verse obligados a depender de interfaces que no usan."*

Prefiere **interfaces pequeñas y específicas** sobre interfaces grandes y generales. Una clase que implementa una interfaz no debería tener que implementar métodos que no necesita.

### ❌ Violación de ISP

```java
// Interfaz demasiado grande: obliga a implementar métodos que no aplican a todos
public interface TicketOperations {
    List<Ticket> findAll();
    Optional<Ticket> findById(Long id);
    Ticket save(Ticket ticket);
    void deleteById(Long id);
    List<Ticket> findByUserAndStatus(Long userId, String status);
    void exportToCsv(String filePath);            // no todos los repos pueden hacer esto
    void sendDigestEmail(String recipientEmail);   // ¿esto debería estar en el repositorio?
}
```

### ✅ Aplicando ISP

```java
// Interfaces pequeñas y enfocadas
public interface TicketFinder {
    List<Ticket> findAll();
    Optional<Ticket> findById(Long id);
    List<Ticket> findByStatus(String status);
}

public interface TicketPersistence {
    Ticket save(Ticket ticket);
    void deleteById(Long id);
}

public interface TicketExporter {
    void exportToCsv(String filePath);
}

// Cada implementación solo depende de lo que realmente usa
@Repository
public class TicketRepository implements TicketFinder, TicketPersistence {
    @Override public List<Ticket> findAll() { /* ... */ }
    @Override public Optional<Ticket> findById(Long id) { /* ... */ }
    @Override public List<Ticket> findByStatus(String status) { /* ... */ }
    @Override public Ticket save(Ticket ticket) { /* ... */ }
    @Override public void deleteById(Long id) { /* ... */ }
    // No implementa exportToCsv porque no le corresponde
}

@Service
public class TicketCsvExporter implements TicketExporter {
    @Override
    public void exportToCsv(String filePath) { /* ... */ }
}
```

---

## D — Dependency Inversion Principle (DIP)

> *"Los módulos de alto nivel no deben depender de módulos de bajo nivel. Ambos deben depender de abstracciones."*

Las clases de negocio (alto nivel) no deben instanciar directamente sus dependencias (bajo nivel). Deben recibir abstracciones (interfaces) que el framework o el sistema de inyección de dependencias resuelve.

### ❌ Violación de DIP

```java
public class TicketService {

    // ❌ TicketService instancia directamente su dependencia
    // Está acoplado a la implementación concreta TicketRepository
    private TicketRepository ticketRepository = new TicketRepository();

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
        // Si cambia TicketRepository, hay que modificar TicketService también
    }
}
```

### ✅ Aplicando DIP

```java
// Abstracción (interfaz) — lo que TicketService realmente necesita
public interface TicketFinder {
    List<Ticket> findAll();
    Optional<Ticket> findById(Long id);
}

// Implementación concreta — módulo de bajo nivel
@Repository
public class TicketRepository implements TicketFinder {
    @Override
    public List<Ticket> findAll() { /* ... */ }

    @Override
    public Optional<Ticket> findById(Long id) { /* ... */ }
}

// TicketService depende de la ABSTRACCIÓN, no de la implementación concreta
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketFinder ticketFinder;  // ✅ depende de la interfaz

    public List<Ticket> listarTodos() {
        return ticketFinder.findAll();
        // Spring inyectará automáticamente TicketRepository (que implementa TicketFinder)
        // Si mañana cambiamos por TicketCacheRepository, TicketService no cambia ✅
    }
}
```

> 💡 **Spring Boot aplica DIP automáticamente** con su contenedor de inyección de dependencias. Declaras la dependencia como interfaz (`TicketFinder`) y Spring resuelve qué implementación concreta inyectar en tiempo de ejecución.

---

## SOLID en la arquitectura del proyecto Tickets

```
@RestController                    @Service                      @Repository
TicketController                   TicketService                 TicketRepository
     │                                  │                              │
     │  depende de (DIP)                │  depende de (DIP)            │
     ▼                                  ▼                              ▼
 (interfaz)                        (interfaz)                    (implementación)
 TicketService                     TicketRepository               JpaRepository
     │                                                                  │
     │  SRP: solo HTTP                  │  SRP: solo lógica             │  SRP: solo datos
     │  OCP: nuevos endpoints           │  OCP: nueva lógica            │  OCP: nueva query
     │       = nueva clase              │       = nuevo servicio        │       = nuevo método
```

| Principio | Dónde se aplica en el proyecto |
|-----------|-------------------------------|
| **S** | Controller solo maneja HTTP; Service solo contiene lógica; Repository solo accede a datos |
| **O** | Agregar un nuevo endpoint = nueva clase, no modificar las existentes |
| **L** | `JpaRepository` puede reemplazar a cualquier `Repository` genérico |
| **I** | Interfaces de servicio específicas en lugar de una mega-interfaz |
| **D** | Controller inyecta interfaz de servicio; Service inyecta interfaz de repositorio |

---

## Señales de que estás violando SOLID

| Señal | Principio violado |
|-------|------------------|
| La clase tiene más de 200 líneas | **S** — demasiadas responsabilidades |
| Cada nueva funcionalidad requiere modificar la misma clase | **O** — no está cerrada para modificación |
| Una subclase lanza `UnsupportedOperationException` | **L** — no puede reemplazar a la base |
| Una clase implementa métodos que no usa (los deja vacíos) | **I** — interfaz demasiado grande |
| Usas `new ClaseConcretas()` dentro de servicios | **D** — acoplado a implementaciones |

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| SOLID (Refactoring Guru) | 📖 Guía visual | [refactoring.guru/es/solid](https://refactoring.guru/es/design-patterns) |
| Uncle Bob — Clean Code | 📚 Libro | Robert C. Martin — *Clean Code* |
| Baeldung — SOLID Principles | 📄 Artículo | [baeldung.com/solid-principles](https://www.baeldung.com/solid-principles) |
| SOLID in Spring Boot (YouTube) | 🎥 Video | Buscar "SOLID principles Spring Boot" |

---

*[← Volver a Extras](../README.md)*

