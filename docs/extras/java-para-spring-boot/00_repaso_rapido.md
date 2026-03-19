# Módulo 00 — Repaso rápido: Java y POO para Spring Boot

> **¿Para quién es este módulo?**  
> Para quien ya recuerda bien Java y los pilares de la POO y solo quiere una **referencia rápida** antes de arrancar con Spring Boot.  
> Si no recuerdas bien Java o llevas tiempo sin practicarlo, ve directo al [Módulo 01](./01_sintaxis_y_tipos.md) y recorre el curso en orden.

---

## Los 4 pilares de la POO

### 1. 🔒 Encapsulamiento
Ocultar el estado interno de un objeto y exponer solo lo necesario a través de métodos públicos. Protege la integridad de los datos y permite cambiar la implementación interna sin afectar al exterior.

```java
public class Ticket {
    private Long id;
    private String titulo;
    private String estado;

    public Long getId() {
        return id;
    }

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
public interface Notificador {
    void notificar(String destinatario, String mensaje);
}

public class EmailNotificador implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("Enviando email a " + destinatario + ": " + mensaje);
    }
}

public class SmsNotificador implements Notificador {
    @Override
    public void notificar(String destinatario, String mensaje) {
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
}

// Código que no sabe qué implementación usa — funciona con ambas
public class ServicioTicket {
    private final Notificador notificador;

    public ServicioTicket(Notificador notificador) {
        this.notificador = notificador;
    }

    public void cerrarTicket(Ticket ticket) {
        notificador.notificar(ticket.getUsuario(), "Tu ticket fue cerrado.");
    }
}
```

---

### 4. 🧩 Abstracción
Modelar solo los aspectos relevantes de un objeto, ocultando la complejidad de la implementación.

```java
public abstract class ServicioBase<T, ID> {
    public abstract T buscarPorId(ID id);
    public abstract List<T> listarTodos();
    public abstract T guardar(T entidad);
    public abstract void eliminar(ID id);
}

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

## Herramientas Java esenciales para Spring Boot

### `Optional<T>`
Evita el `NullPointerException` envolviendo valores que pueden ser nulos.

```java
Optional<Ticket> ticket = ticketRepository.findById(1L);

ticket.ifPresent(t -> System.out.println(t.getTitulo()));

Ticket t = ticket.orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

Ticket t = ticket.orElse(new Ticket());
```

### Generics
Permiten escribir clases y métodos que funcionan con cualquier tipo.

```java
public class Respuesta<T> {
    private T data;
    private String mensaje;
    private int status;
}

Respuesta<Ticket> respuesta = new Respuesta<>(ticket, "OK", 200);
Respuesta<List<Ticket>> lista = new Respuesta<>(tickets, "OK", 200);
```

### Stream API y Lambdas
Procesamiento funcional de colecciones de forma expresiva.

```java
List<Ticket> tickets = ticketRepository.findAll();

List<Ticket> abiertos = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .collect(Collectors.toList());

List<String> titulos = tickets.stream()
    .map(Ticket::getTitulo)
    .collect(Collectors.toList());

long total = tickets.stream()
    .filter(t -> "CERRADO".equals(t.getEstado()))
    .count();
```

### Anotaciones (`@`)
Java permite definir metadatos sobre clases, métodos y campos. Spring Boot los usa extensivamente.

```java
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService service;

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
```

### Records (Java 14+)
Forma concisa de crear clases inmutables de datos (útiles para DTOs).

```java
public record TicketDTO(Long id, String titulo, String estado) {}

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
> → Más detalle en [Principios SOLID](../solid/README.md)

---

## Estructura típica de un proyecto Spring Boot

```
📦 tickets
 ├── 🎮 controller/     → Recibe peticiones HTTP (@RestController)
 ├── 🧠 service/        → Lógica de negocio (@Service)
 ├── 🗄️ repository/     → Acceso a datos (@Repository / JpaRepository)
 └── 📋 model/          → Entidades y DTOs (@Entity, Records)
```

---

## ¿Qué sigue?

Si este módulo te resultó fluido, puedes ir directamente al **[Módulo 05 — Los 4 pilares de la POO aplicados](./05_poo_pilares.md)** para ver los ejemplos con mayor profundidad, o al **[Módulo 11 — El camino a Spring Boot](./11_camino_a_spring_boot.md)** si ya te sientes listo.

Si en cambio encontraste conceptos que no recordabas del todo, empieza por el **[Módulo 01 — Sintaxis esencial y tipos de datos](./01_sintaxis_y_tipos.md)**.

---

*[← Volver al índice del Mini Curso](./README.md)*

