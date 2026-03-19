# Módulo 11 — El camino a Spring Boot

> **Objetivo:** conectar todo lo aprendido en los módulos anteriores con el código real de Spring Boot, viendo exactamente qué conceptos de Java se usan en cada capa.

---

## 11.1 Mapa conceptual: Java → Spring Boot

```
Java puro                          Spring Boot
─────────────────────────────────────────────────────
Interfaz               →    Repositorio / Servicio
Clase con campos       →    @Entity (modelo)
Record                 →    DTO (request/response)
Herencia / Abstracta   →    JpaRepository<T, ID>
Excepción personalizada→    @ExceptionHandler
Lambda / Stream        →    Procesamiento en servicio
Optional<T>            →    findById() del repositorio
Genéricos              →    ResponseEntity<T>, List<T>
@interface (anotación) →    @RestController, @Service...
```

---

## 11.2 La arquitectura en capas y qué Java usa cada capa

```
HTTP Request
     │
     ▼
┌─────────────────────────────────────────────────────┐
│  @RestController  (Controlador)                      │
│  • Recibe y valida la petición HTTP                  │
│  • Delega al servicio                                │
│  • Usa: record DTO, ResponseEntity<T>, Optional      │
└─────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────┐
│  @Service  (Servicio)                                │
│  • Contiene la lógica de negocio                     │
│  • Usa: excepciones personalizadas, Streams, lambdas │
│  • Delega acceso a datos al repositorio              │
└─────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────┐
│  @Repository / JpaRepository  (Repositorio)          │
│  • Accede a la base de datos                         │
│  • Usa: genéricos (JpaRepository<Ticket, Long>)      │
│  • Retorna: Optional<T>, List<T>                     │
└─────────────────────────────────────────────────────┘
     │
     ▼
  Base de datos
```

---

## 11.3 El modelo: de clase Java a @Entity

```java
// Java puro (lo que ya sabes)
public class Ticket {
    private Long id;
    private String titulo;
    private String estado;
    // constructor, getters, setters, toString...
}

// Spring Boot con JPA (mismos conceptos + anotaciones)
import jakarta.persistence.*;

@Entity                         // Marca la clase como tabla en BD
@Table(name = "tickets")        // Nombre de la tabla (opcional)
public class Ticket {

    @Id                         // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremental
    private Long id;

    @Column(nullable = false, length = 200)  // restricciones de columna
    private String titulo;

    @Column(nullable = false)
    private String estado = "ABIERTO";

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Spring Boot con Lombok: @Data genera getters, setters, equals, hashCode, toString
    // Sin Lombok: los escribes manualmente (como en los módulos anteriores)

    public Ticket() {}  // JPA requiere constructor vacío

    public Ticket(String titulo, String descripcion) {
        this.titulo      = titulo;
        this.descripcion = descripcion;
        this.estado      = "ABIERTO";
    }

    // getters y setters...
}
```

---

## 11.4 El DTO: record en Spring Boot

Los DTOs (Data Transfer Objects) aíslan la API de la entidad interna. Los `record` son perfectos para esto:

```java
// Request: datos que llegan del cliente
public record CrearTicketRequest(
    String titulo,
    String descripcion
) {}

// Response: datos que enviamos al cliente (nunca el @Entity directamente)
public record TicketResponse(
    Long   id,
    String titulo,
    String estado,
    String descripcion
) {
    // Método de fábrica: convierte la entidad al DTO
    public static TicketResponse desde(Ticket ticket) {
        return new TicketResponse(
            ticket.getId(),
            ticket.getTitulo(),
            ticket.getEstado(),
            ticket.getDescripcion()
        );
    }
}
```

---

## 11.5 El repositorio: interfaz + genéricos

```java
// Lo que sabes: una interfaz genérica
public interface Repositorio<T, ID> {
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    T guardar(T entidad);
    void eliminar(ID id);
}

// Spring Boot: extends JpaRepository<Entidad, TipoId>
// JpaRepository YA implementa todo lo anterior y mucho más
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // marca como componente de Spring
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Spring genera las queries automáticamente por el nombre del método:
    List<Ticket> findByEstado(String estado);
    List<Ticket> findByTituloContainingIgnoreCase(String termino);
    Optional<Ticket> findFirstByEstadoOrderByIdDesc(String estado);
    long countByEstado(String estado);
    boolean existsByTituloAndEstado(String titulo, String estado);
}
// Spring implementa todo esto automáticamente. Cero SQL.
```

---

## 11.6 El servicio: lógica de negocio con Java puro

```java
import org.springframework.stereotype.Service;
import java.util.List;

@Service  // Spring lo gestiona como singleton (1 sola instancia)
public class TicketService {

    private final TicketRepository repositorio; // Inyección de dependencia

    // Constructor injection (la forma recomendada)
    // Spring inyecta automáticamente el repositorio
    public TicketService(TicketRepository repositorio) {
        this.repositorio = repositorio;
    }

    // Listar: Stream + lambda + record
    public List<TicketResponse> listarAbiertos() {
        return repositorio.findByEstado("ABIERTO")  // List<Ticket>
            .stream()
            .map(TicketResponse::desde)              // referencia a método (módulo 09)
            .toList();
    }

    // Buscar por ID: Optional + excepción personalizada (módulo 08)
    public TicketResponse buscarPorId(Long id) {
        return repositorio.findById(id)
            .map(TicketResponse::desde)
            .orElseThrow(() -> new RecursoNoEncontradoException("Ticket", id));
    }

    // Crear: validación + excepción + guardado
    public TicketResponse crear(CrearTicketRequest request) {
        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new ReglaNegocioException("El título es obligatorio");
        }
        var ticket = new Ticket(request.titulo(), request.descripcion());
        var guardado = repositorio.save(ticket);
        return TicketResponse.desde(guardado);
    }

    // Cambiar estado: buscar + modificar + guardar
    public TicketResponse cerrar(Long id, String motivo) {
        Ticket ticket = repositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Ticket", id));

        if ("CERRADO".equals(ticket.getEstado())) {
            throw new ReglaNegocioException("El ticket ya está cerrado");
        }

        ticket.setEstado("CERRADO");
        return TicketResponse.desde(repositorio.save(ticket));
    }
}
```

---

## 11.7 El controlador: HTTP + Java

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController                  // @Controller + @ResponseBody
@RequestMapping("/api/tickets")  // prefijo de todas las rutas
public class TicketController {

    private final TicketService servicio;

    // Inyección por constructor (sin @Autowired — Spring lo detecta automáticamente)
    public TicketController(TicketService servicio) {
        this.servicio = servicio;
    }

    // GET /api/tickets?estado=ABIERTO
    @GetMapping
    public ResponseEntity<List<TicketResponse>> listar(
            @RequestParam(defaultValue = "ABIERTO") String estado) {

        return ResponseEntity.ok(servicio.listarAbiertos());
        // ResponseEntity<T> es un genérico — igual que Respuesta<T> del módulo 03
    }

    // GET /api/tickets/5
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.buscarPorId(id));
    }

    // POST /api/tickets
    @PostMapping
    public ResponseEntity<TicketResponse> crear(@RequestBody CrearTicketRequest request) {
        TicketResponse creado = servicio.crear(request);
        return ResponseEntity.status(201).body(creado);
    }

    // PATCH /api/tickets/5/cerrar
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<TicketResponse> cerrar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(servicio.cerrar(id, motivo));
    }

    // DELETE /api/tickets/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.buscarPorId(id); // verifica que exista antes de eliminar
        // servicio.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
```

---

## 11.8 Manejo global de excepciones

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice  // captura excepciones de todos los @RestController
public class ManejadorExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {
        // pattern matching (módulo 10) podría usarse aquí también
        return ResponseEntity.status(404)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> manejarReglaNegocio(
            ReglaNegocioException ex) {
        return ResponseEntity.status(422)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarGeneral(Exception ex) {
        return ResponseEntity.status(500)
            .body(Map.of("error", "Error interno. Inténtalo más tarde."));
    }
}
```

---

## 11.9 Conexión completa: dónde vive cada concepto Java

| Módulo | Concepto Java | Dónde lo ves en Spring Boot |
|--------|--------------|----------------------------|
| 01 | `String`, `var`, text blocks | DTOs, logs, SQL embebido |
| 02 | `switch` expression, pattern matching | Procesamiento de estados, manejo de respuestas |
| 03 | Métodos, `Optional`, fábrica | Servicio: `orElseThrow`, métodos de fábrica en DTO |
| 04 | Clases, constructores, `record` | `@Entity`, DTOs de request/response |
| 05 | Encapsulamiento, herencia, polimorfismo | Capas separadas, `@Service` inyectable |
| 06 | Interfaces, funcionales | `JpaRepository`, `@FunctionalInterface`, lambdas |
| 07 | `List`, `Map`, `Optional`, genéricos | Retornos del repositorio, `ResponseEntity<T>` |
| 08 | Excepciones personalizadas | `@ExceptionHandler`, `orElseThrow` |
| 09 | Lambdas, Streams | Procesamiento en `@Service`, consultas derivadas |
| 10 | `record`, sealed, Java 21 | DTOs modernos, modelado de estados |

---

## 11.10 Tu próximo paso

Has completado el mini curso. Ahora estás listo para:

1. **Crear un proyecto Spring Boot** con [start.spring.io](https://start.spring.io) o IntelliJ  
   → Elige: Spring Web, Spring Data JPA, H2 Database, Lombok
2. **Seguir las lecciones del curso** — todo lo que ves allí ya tiene base aquí
3. **Practicar**: implementa una API CRUD completa de cualquier entidad que te interese

---

## 🏋️ Ejercicio integrador final

Construye una API mínima funcional (sin base de datos real — usa una `List` en memoria) para gestionar **Productos** con las siguientes operaciones:

| Método HTTP | Ruta | Descripción |
|-------------|------|-------------|
| GET | `/api/productos` | Lista todos los productos |
| GET | `/api/productos/{id}` | Obtiene uno por ID |
| POST | `/api/productos` | Crea un nuevo producto |
| PUT | `/api/productos/{id}` | Actualiza precio y stock |
| DELETE | `/api/productos/{id}` | Elimina un producto |

**Requisitos de implementación:**
- `record CrearProductoRequest(String nombre, double precio)` como DTO de entrada
- `record ProductoResponse(Long id, String nombre, double precio, boolean disponible)` como salida
- Excepción personalizada `ProductoNoEncontradoException` para 404
- Stream para filtrar/mapear en el servicio
- `Optional` en la búsqueda por ID

<details>
<summary>🔍 Ver estructura de solución</summary>

```java
// ProductoController.java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService servicio;
    public ProductoController(ProductoService servicio) { this.servicio = servicio; }

    @GetMapping    public ResponseEntity<List<ProductoResponse>> listar() { ... }
    @GetMapping("/{id}") public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) { ... }
    @PostMapping   public ResponseEntity<ProductoResponse> crear(@RequestBody CrearProductoRequest req) { ... }
    @PutMapping("/{id}") public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, @RequestBody ActualizarProductoRequest req) { ... }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) { ... }
}

// ProductoService.java
@Service
public class ProductoService {
    private final List<Producto> productos = new ArrayList<>();
    private long contador = 1L;

    public List<ProductoResponse> listar() {
        return productos.stream().map(ProductoResponse::desde).toList();
    }

    public ProductoResponse buscarPorId(Long id) {
        return productos.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .map(ProductoResponse::desde)
            .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }
    // ... resto de métodos
}
```
</details>

---

## Recursos para continuar aprendiendo

| Recurso | Tipo | Descripción |
|---------|------|-------------|
| [spring.io/guides](https://spring.io/guides) | 📖 Guías | Tutoriales oficiales paso a paso |
| [Baeldung — Spring Boot](https://www.baeldung.com/spring-boot) | 📄 Artículos | Artículos técnicos detallados |
| [Java 21 Release Notes](https://openjdk.org/projects/jdk/21/) | 📖 Oficial | Notas de lanzamiento de Java 21 |
| [Refactoring Guru](https://refactoring.guru/es) | 📖 Patrones | Patrones de diseño y SOLID |
| [roadmap.sh/java](https://roadmap.sh/java) | 🗺️ Hoja de ruta | Qué aprender y en qué orden |

---

*[← Módulo 10](./10_java21.md) | [← Índice del mini curso](./README.md) | [← Volver a Extras](../README.md)*

