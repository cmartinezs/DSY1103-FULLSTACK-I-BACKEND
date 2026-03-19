# ☕ Java y Programación Orientada a Objetos (POO)

## ¿Por qué Java en este curso?
Java es el lenguaje principal del ecosistema **Spring Boot**. Entender sus fundamentos y el paradigma orientado a objetos es esencial para desarrollar aplicaciones backend robustas, mantenibles y escalables.

---

## Los 4 Pilares de la POO

### 1. 🔒 Encapsulamiento
Ocultar el estado interno de un objeto y exponer solo lo necesario a través de métodos públicos. Protege la integridad de los datos y permite cambiar la implementación interna sin afectar al exterior.

```java
public class Ticket {
    private Long id;
    private String titulo;
    private String estado;

    // Getter: permite leer el valor
    public Long getId() {
        return id;
    }

    // Setter: permite modificar el valor con control
    public void setEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        this.estado = estado;
    }
}
```

> 💡 En Spring Boot, las clases `@Entity` y los DTOs dependen fuertemente del encapsulamiento.

---

### 2. 🧬 Herencia
Permite que una clase **hija** reutilice atributos y comportamientos de una clase **padre**, promoviendo la reutilización de código.

```java
// Clase padre: atributos comunes a todas las entidades
public class EntidadBase {
    private Long id;
    private LocalDateTime creadoEn = LocalDateTime.now();
    private LocalDateTime actualizadoEn;

    // getters y setters...
}

// Clase hija: hereda id, creadoEn y actualizadoEn
public class Ticket extends EntidadBase {
    private String titulo;
    private String descripcion;
    private String estado;

    // solo define lo específico de Ticket
}
```

> ⚠️ Prefiere **composición sobre herencia** cuando la relación no es estrictamente "es un".

---

### 3. 🎭 Polimorfismo
Un objeto puede comportarse de distintas formas según el contexto. Permite escribir código genérico que funcione con múltiples tipos.

```java
// Contrato (interface)
public interface Notificador {
    void notificar(String destinatario, String mensaje);
}

// Implementación 1
public class EmailNotificador implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("Enviando email a " + destinatario + ": " + mensaje);
    }
}

// Implementación 2
public class SmsNotificador implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
}

// Uso polimórfico: el código no sabe qué implementación usa
public class ServicioTicket {
    private final Notificador notificador;

    public ServicioTicket(Notificador notificador) {
        this.notificador = notificador;
    }

    public void cerrarTicket(Ticket ticket) {
        // lógica de cierre...
        notificador.notificar(ticket.getUsuario(), "Tu ticket fue cerrado.");
    }
}
```

---

### 4. 🧩 Abstracción
Modelar solo los aspectos relevantes de un objeto, ocultando la complejidad de la implementación.

```java
// Clase abstracta: define el contrato + comportamiento base
public abstract class ServicioBase<T, ID> {
    public abstract T buscarPorId(ID id);
    public abstract List<T> listarTodos();
    public abstract T guardar(T entidad);
    public abstract void eliminar(ID id);
}

// Implementación concreta
public class TicketService extends ServicioBase<Ticket, Long> {
    @Override
    public Ticket buscarPorId(Long id) {
        // implementación específica
    }
    // ...
}
```

---

## Interfaces vs Clases Abstractas

| Característica | `interface` | `abstract class` |
|---|---|---|
| Herencia múltiple | ✅ Sí | ❌ No (solo una) |
| Estado (atributos) | Solo constantes | ✅ Sí |
| Constructores | ❌ No | ✅ Sí |
| Métodos con implementación | Solo `default` / `static` | ✅ Sí |
| Cuándo usar | Contratos de comportamiento | Base común con lógica compartida |

---

## Conceptos Java relevantes para Spring Boot

### `Optional<T>`
Evita el `NullPointerException` envolviendo valores que pueden ser nulos.

```java
Optional<Ticket> ticket = ticketRepository.findById(1L);

// Forma segura de usar el valor
ticket.ifPresent(t -> System.out.println(t.getTitulo()));

// Lanzar excepción si no existe
Ticket t = ticket.orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

// Valor por defecto
Ticket t = ticket.orElse(new Ticket());
```

### Generics
Permiten escribir clases y métodos que funcionan con cualquier tipo.

```java
public class Respuesta<T> {
    private T data;
    private String mensaje;
    private int status;

    // constructor, getters, setters...
}

// Uso:
Respuesta<Ticket> respuesta = new Respuesta<>(ticket, "OK", 200);
Respuesta<List<Ticket>> lista = new Respuesta<>(tickets, "OK", 200);
```

### Stream API y Lambdas
Procesamiento funcional de colecciones de forma expresiva.

```java
List<Ticket> tickets = ticketRepository.findAll();

// Filtrar tickets abiertos
List<Ticket> abiertos = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .collect(Collectors.toList());

// Obtener solo los títulos
List<String> titulos = tickets.stream()
    .map(Ticket::getTitulo)
    .collect(Collectors.toList());

// Contar
long total = tickets.stream()
    .filter(t -> "CERRADO".equals(t.getEstado()))
    .count();
```

### Anotaciones (`@`)
Java permite definir metadatos sobre clases, métodos y campos. Spring Boot los usa extensivamente.

```java
@RestController                    // Define un controlador REST
@RequestMapping("/tickets")        // Prefijo de URL
public class TicketController {

    @Autowired                     // Inyección de dependencia
    private TicketService service;

    @GetMapping("/{id}")           // Mapea GET /tickets/{id}
    public ResponseEntity<Ticket> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
```

### Records (Java 14+)
Forma concisa de crear clases inmutables de datos (útiles para DTOs).

```java
public record TicketDTO(Long id, String titulo, String estado) {}

// Uso
TicketDTO dto = new TicketDTO(1L, "Error en login", "ABIERTO");
System.out.println(dto.titulo()); // "Error en login"
```

---

## Principios SOLID (resumen)

| Principio | Nombre | Idea central |
|-----------|--------|--------------|
| **S** | Single Responsibility | Una clase = una razón para cambiar |
| **O** | Open/Closed | Abierto para extensión, cerrado para modificación |
| **L** | Liskov Substitution | Las subclases deben poder reemplazar a sus bases |
| **I** | Interface Segregation | Interfaces pequeñas y específicas |
| **D** | Dependency Inversion | Depender de abstracciones, no de implementaciones |

> 📌 Spring Boot aplica SOLID de forma natural: `@Service`, `@Repository` y `@Controller` separan responsabilidades; la inyección de dependencias aplica el principio D.

---

## Estructura típica de una clase en Spring Boot

```
📦 tickets
 ├── 🎮 controller/     → Recibe peticiones HTTP (@RestController)
 ├── 🧠 service/        → Lógica de negocio (@Service)
 ├── 🗄️ repository/     → Acceso a datos (@Repository / JpaRepository)
 └── 📋 model/          → Entidades y DTOs (@Entity, Records)
```

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| Java Tutorial (Oracle) | 📖 Oficial | [docs.oracle.com/javase/tutorial](https://docs.oracle.com/javase/tutorial/) |
| Baeldung | 📄 Artículos técnicos | [baeldung.com](https://www.baeldung.com/) |
| Java Brains | 🎥 Videos | [youtube.com/@Java.Brains](https://www.youtube.com/@Java.Brains) |
| Codecademy Java | 🎓 Curso interactivo | [codecademy.com/learn/learn-java](https://www.codecademy.com/learn/learn-java) |
| Refactoring Guru — SOLID | 📖 Patrones y principios | [refactoring.guru/es](https://refactoring.guru/es/design-patterns) |

---

*[← Volver a Extras](../README.md)*

