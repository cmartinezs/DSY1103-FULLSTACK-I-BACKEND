# Módulo 03 — Situaciones en REST API

> **Nivel:** 🔴 Avanzado — Spring Boot, REST API, capas Controller / Service / Repository.  
> **Prerequisito:** haber leído los [módulos 01](./01_situaciones_basicas.md) y [02](./02_situaciones_intermedias.md).

---

## Tip 31 — "Tengo lógica en consola y quiero exponerla como API bien estructurada"

### 📋 El escenario
Ya tienes la lógica funcionando en un `main`. Ahora necesitas que esa misma lógica responda a peticiones HTTP y que el código sea mantenible a medida que crezca.

*Ejemplo:* el cálculo de descuentos necesita ser accesible desde `POST /descuentos/calcular`.

### ❌ El error común
```java
// ❌ Toda la lógica dentro del Controller
@PostMapping("/calcular")
public ResponseEntity<?> calcular(@RequestBody DescuentoRequest req) {
    if (req.precio() <= 0) return ResponseEntity.badRequest().build();
    double monto  = req.precio() * (req.porcentaje() / 100);
    double total  = req.precio() - monto;
    return ResponseEntity.ok(total);
}
```

### 🧠 ¿Cómo pienso esto?
```
En REST API cada capa tiene UNA responsabilidad:
  Controller  → recibe la petición, delega, responde. Nada más.
  Service     → toda la lógica de decisión vive aquí.
  Repository  → acceso a datos.
```

### ✅ La solución

```java
// 1. DTOs
record DescuentoRequest(double precio, double porcentaje) {}
record DescuentoResponse(double precioOriginal, double montoDescuento, double precioFinal) {}

// 2. Service — aquí vive la lógica (igual que en el main)
@Service
public class DescuentoService {
    public DescuentoResponse calcular(double precio, double porcentaje) {
        if (precio <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        if (porcentaje < 0 || porcentaje > 100)
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        double monto  = precio * (porcentaje / 100);
        return new DescuentoResponse(precio, monto, precio - monto);
    }
}

// 3. Controller — delgado: recibe, delega, responde
@RestController
@RequestMapping("/descuentos")
public class DescuentoController {
    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        this.descuentoService = descuentoService;
    }

    @PostMapping("/calcular")
    public ResponseEntity<DescuentoResponse> calcular(@RequestBody DescuentoRequest req) {
        return ResponseEntity.ok(descuentoService.calcular(req.precio(), req.porcentaje()));
    }
}
```

```http
POST /descuentos/calcular
{ "precio": 50000, "porcentaje": 20 }
→ { "precioOriginal": 50000.0, "montoDescuento": 10000.0, "precioFinal": 40000.0 }
```

> 💡 **Regla de oro:** si el Controller hace más de 3-5 líneas de lógica en un método, algo está en el lugar incorrecto. El Controller nunca calcula ni decide; solo traduce la petición HTTP en una llamada al Service y devuelve la respuesta.

---

## Tip 32 — "No sé cómo leer los datos que envía el cliente ni cómo validarlos"

### 📋 El escenario
La petición HTTP puede traer datos en tres lugares distintos: en la URL, como parámetros de query o en el cuerpo JSON. Confundirlos genera que el endpoint no reciba los datos que espera.

### ❌ El error común
```java
// ❌ Usar @RequestParam para recibir un objeto complejo
@PostMapping
public ResponseEntity<?> crear(@RequestParam String titulo,
                                @RequestParam String descripcion,
                                @RequestParam int prioridad) { ... }
```

### 🧠 ¿Cómo pienso esto?
```
¿Dónde viene el dato?              ¿Qué anotación usar?
──────────────────────────────────────────────────────
/tickets/42                     → @PathVariable
/tickets?status=ABIERTO         → @RequestParam
Body: { "titulo": "..." }       → @RequestBody  (objetos complejos)
```

### ✅ La solución

```java
// 1. DTO con validaciones declarativas
record TicketRequest(
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    String titulo,

    @NotBlank(message = "La descripción es obligatoria")
    String descripcion,

    @Min(value = 1, message = "La prioridad mínima es 1")
    @Max(value = 5, message = "La prioridad máxima es 5")
    int prioridad
) {}

// 2. Controller con las tres anotaciones
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @GetMapping("/{id}")   // @PathVariable — ID en la URL
    public ResponseEntity<Ticket> buscar(@PathVariable int id) { ... }

    @GetMapping           // @RequestParam — filtros opcionales
    public ResponseEntity<List<Ticket>> listar(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int pagina) { ... }

    @PostMapping          // @RequestBody + @Valid — objeto JSON en el cuerpo
    public ResponseEntity<Ticket> crear(@Valid @RequestBody TicketRequest req) { ... }
}

// 3. Manejador global de errores de validación
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}
```

> 💡 **Regla:** `@PathVariable` para IDs en la ruta, `@RequestParam` para filtros opcionales, `@RequestBody` para objetos complejos. Las validaciones de formato van en el DTO con anotaciones Bean Validation — no en el Controller a mano.

---

## Tip 33 — "No sé qué status HTTP devolver ni cómo comunicar errores al cliente"

### 📋 El escenario
El endpoint siempre devuelve `200 OK` sin importar lo que ocurra, o cuando hay un error de negocio el cliente recibe un `500` con un stacktrace inútil.

### 🧠 ¿Cómo pienso esto?
```
Pregúntate: ¿qué pasó en el servidor?

✅ Todo fue bien:
  200 OK          → GET exitoso, PUT/PATCH exitoso
  201 Created     → POST que creó un recurso nuevo
  204 No Content  → DELETE exitoso (sin cuerpo)

⚠️ Error del cliente:
  400 Bad Request    → datos inválidos / malformados
  404 Not Found      → el recurso no existe
  409 Conflict       → regla de negocio violada / ya existe

❌ Error del servidor:
  500 Internal Server Error → excepción no manejada
```

### ✅ La solución

```java
@GetMapping("/{id}")
public ResponseEntity<Ticket> buscar(@PathVariable int id) {
    return ticketService.buscarPorId(id)
            .map(ResponseEntity::ok)                    // 200
            .orElse(ResponseEntity.notFound().build()); // 404
}

@PostMapping
public ResponseEntity<Ticket> crear(@Valid @RequestBody TicketRequest req) {
    Ticket creado = ticketService.crear(req);
    URI location = URI.create("/tickets/" + creado.getId());
    return ResponseEntity.created(location).body(creado);   // 201
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable int id) {
    ticketService.eliminar(id);
    return ResponseEntity.noContent().build();   // 204
}

// Excepciones → @RestControllerAdvice
record ErrorResponse(int status, String error, String mensaje) {}

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(TicketNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse(404, "Not Found", ex.getMessage()));
    }
    @ExceptionHandler(TicketCerradoException.class)
    public ResponseEntity<ErrorResponse> handleConflict(TicketCerradoException ex) {
        return ResponseEntity.status(409).body(new ErrorResponse(409, "Conflict", ex.getMessage()));
    }
}
```

> 💡 **Regla:** el Controller nunca tiene `try/catch`. Cada tipo de error tiene su excepción personalizada; el `@RestControllerAdvice` las mapea al status HTTP correcto. Una excepción por cada tipo de error de negocio.

---

## Tip 34 — "Mi API devuelve todos los registros de golpe o el cliente no puede filtrar"

### 📋 El escenario
El endpoint devuelve todos los registros sin límite (con 10.000 filas tarda segundos) o los clientes no tienen forma de filtrar los resultados desde la URL.

### ❌ El error común
```java
@GetMapping
public ResponseEntity<List<Ticket>> listar() {
    return ResponseEntity.ok(ticketRepository.findAll());  // potencialmente ilimitado
}
```

### ✅ La solución

```java
// GET /tickets?status=ABIERTO&prioridad=4&q=login&pagina=0&tamanio=10
@GetMapping
public ResponseEntity<PaginaResponse<TicketResponse>> listar(
        @RequestParam(required = false)    String status,
        @RequestParam(required = false)    Integer prioridad,
        @RequestParam(required = false)    String q,
        @RequestParam(defaultValue = "0")  int pagina,
        @RequestParam(defaultValue = "10") int tamanio) {
    return ResponseEntity.ok(ticketService.listar(status, prioridad, q, pagina, tamanio));
}

// Paginación con Spring Data
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Page<Ticket> findByStatus(String status, Pageable pageable);
}

// Service con filtros + paginación
public PaginaResponse<TicketResponse> listar(String status, Integer prioridad,
                                              String q, int pagina, int tamanio) {
    List<Ticket> todos = ticketRepository.findAll();
    List<Ticket> filtrados = todos.stream()
            .filter(t -> status    == null || t.getStatus().equalsIgnoreCase(status))
            .filter(t -> prioridad == null || t.getPrioridad() == prioridad)
            .filter(t -> q == null || t.getTitulo().toLowerCase().contains(q.toLowerCase()))
            .toList();

    int inicio = pagina * tamanio;
    int fin    = Math.min(inicio + tamanio, filtrados.size());
    List<TicketResponse> pagActual = filtrados.subList(inicio, fin).stream()
            .map(t -> new TicketResponse(t.getId(), t.getTitulo(), t.getStatus()))
            .toList();

    return new PaginaResponse<>(pagActual, pagina, tamanio, filtrados.size(),
            (int) Math.ceil((double) filtrados.size() / tamanio),
            fin >= filtrados.size());
}

record PaginaResponse<T>(List<T> contenido, int pagina, int tamanio,
                          long totalElementos, int totalPaginas, boolean esUltima) {}
```

> 💡 **Regla:** todo endpoint de colección debe paginar. Los filtros son siempre `required = false` — si no llegan, no filtres por ese campo. Nunca hagas `findAll()` sin límite en producción.

---

## Tip 35 — "El cliente quiere actualizar solo algunos campos, no todo el objeto"

### 📋 El escenario
Con `PUT`, el cliente debe enviar el objeto completo aunque solo quiera cambiar un campo. Si olvida alguno, ese campo queda en `null` en la base de datos.

### ✅ La solución

```java
// DTO de actualización parcial — todos los campos son opcionales
record TicketPatchRequest(
    String       titulo,       // null = no cambiar
    String       descripcion,
    TicketStatus status,
    Integer      prioridad     // Integer (no int) para poder ser null
) {}

// Service: aplicar solo lo que llegó
public Ticket actualizarParcial(int id, TicketPatchRequest patch) {
    Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException(id));

    if (patch.titulo()      != null) ticket.setTitulo(patch.titulo());
    if (patch.descripcion() != null) ticket.setDescripcion(patch.descripcion());
    if (patch.status()      != null) {
        if (ticket.getStatus() == TicketStatus.CERRADO) throw new TicketCerradoException(id);
        ticket.setStatus(patch.status());
    }
    if (patch.prioridad()   != null) ticket.setPrioridad(patch.prioridad());

    return ticketRepository.save(ticket);
}

// Controller
@PatchMapping("/{id}")
public ResponseEntity<Ticket> actualizarParcial(@PathVariable int id,
                                                 @RequestBody TicketPatchRequest patch) {
    return ResponseEntity.ok(ticketService.actualizarParcial(id, patch));
}
```

```http
PUT  /tickets/42  →  body completo (todos los campos)
PATCH /tickets/42 →  { "status": "EN_PROGRESO" }  (solo lo que cambia)
```

> 💡 **Regla:** `PUT` reemplaza todo el recurso; `PATCH` modifica solo los campos enviados. Usa `Integer` (no `int`) en el DTO de patch para que los campos opcionales puedan ser `null`.

---

## Tip 36 — "Tengo URLs, puertos y claves escritas directamente en el código"

### 📋 El escenario
La URL de la base de datos, el puerto del servidor, las claves de APIs externas están hardcodeadas en el código. Cambiarlas para distintos entornos (dev, staging, producción) obliga a recompilar.

### ❌ El error común
```java
// ❌ Valores hardcodeados — hay que cambiarlos manualmente por entorno
String dbUrl   = "jdbc:postgresql://localhost:5432/tickets_dev";
String apiKey  = "sk-1234567890abcdef";
int    timeout = 30;
```

### 🧠 ¿Cómo pienso esto?
```
Principio: separar CONFIGURACIÓN de CÓDIGO.

Todo valor que cambia entre entornos va en:
  application.yml (o application.properties)  → configuración del proyecto
  Variables de entorno del sistema            → secretos y valores sensibles

Spring inyecta los valores con:
  @Value("${clave}")   → valor individual
  @ConfigurationProperties(prefix = "mi.config")  → grupo de valores
```

### ✅ La solución

```yaml
# application.yml
app:
  nombre: "Sistema de Tickets"
  version: "1.0"
  tickets:
    prioridad-maxima: 5
    dias-vencimiento: 30
  api-externa:
    url: "https://api.ejemplo.com"
    timeout-segundos: 30
```

```java
// ── @Value: inyectar valores individuales ─────────────────────────────────────
@Service
public class TicketService {

    @Value("${app.tickets.prioridad-maxima}")
    private int prioridadMaxima;

    @Value("${app.tickets.dias-vencimiento}")
    private int diasVencimiento;

    @Value("${app.nombre}")
    private String nombreApp;

    public void validarPrioridad(int prioridad) {
        if (prioridad < 1 || prioridad > prioridadMaxima)
            throw new IllegalArgumentException("Prioridad fuera de rango [1-" + prioridadMaxima + "]");
    }
}

// ── @ConfigurationProperties: grupo de valores con clase propia ───────────────
@ConfigurationProperties(prefix = "app.api-externa")
record ApiExternaConfig(String url, int timeoutSegundos) {}

@Service
public class IntegracionService {

    private final ApiExternaConfig config;

    public IntegracionService(ApiExternaConfig config) {
        this.config = config;
    }

    public void llamarApi() {
        System.out.println("Llamando a: " + config.url());
        // timeout: config.timeoutSegundos()
    }
}
```

```java
// Habilitar @ConfigurationProperties en la clase principal
@SpringBootApplication
@EnableConfigurationProperties(ApiExternaConfig.class)
public class TicketsApplication { ... }
```

> 💡 **Regla:** ningún valor que cambie entre entornos debe estar hardcodeado en el código. Los valores van en `application.yml` y se inyectan con `@Value` o `@ConfigurationProperties`. Los secretos (claves, contraseñas) van en variables de entorno del sistema operativo, nunca en el código fuente.

---

## Tip 37 — "Necesito saber qué está ocurriendo en mi API cuando algo falla"

### 📋 El escenario
El programa falla y solo tienes el stacktrace. No sabes qué datos llegaron, qué flujo siguió, en qué punto falló. Los `System.out.println` para depurar son difíciles de controlar y no se pueden desactivar en producción.

### ❌ El error común
```java
// ❌ Prints que quedan en producción o que hay que quitar manualmente
System.out.println("Entrando a crear ticket...");
System.out.println("Ticket creado: " + ticket.getId());
System.out.println("ERROR: " + e.getMessage());
```

### 🧠 ¿Cómo pienso esto?
```
El sistema de logging tiene NIVELES (de menor a mayor gravedad):
  TRACE / DEBUG → detalles internos (desarrollo)
  INFO          → eventos importantes del negocio (producción)
  WARN          → algo raro pero no fatal
  ERROR         → algo falló y necesita atención

En producción: WARN o INFO.
En desarrollo: DEBUG.
Nunca System.out.println en producción.
```

### ✅ La solución

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TicketService {

    // Una instancia de Logger por clase
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    public Ticket crear(TicketRequest req) {
        log.debug("Intentando crear ticket: {}", req.titulo());

        Ticket ticket = mapear(req);
        Ticket guardado = ticketRepository.save(ticket);

        log.info("Ticket creado exitosamente — ID: {}, título: '{}'",
                guardado.getId(), guardado.getTitulo());
        return guardado;
    }

    public void eliminar(int id) {
        if (!ticketRepository.existsById(id)) {
            log.warn("Intento de eliminar ticket inexistente — ID: {}", id);
            throw new TicketNotFoundException(id);
        }
        ticketRepository.deleteById(id);
        log.info("Ticket #{} eliminado", id);
    }
}

// En el @RestControllerAdvice: siempre loguear errores inesperados
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    log.error("Error inesperado no manejado: {}", ex.getMessage(), ex);
    return ResponseEntity.internalServerError()
            .body(new ErrorResponse(500, "Internal Server Error", "Error interno del servidor"));
}
```

```yaml
# application.yml — controlar nivel por paquete
logging:
  level:
    root: INFO
    com.ejemplo.tickets: DEBUG   # más detalle en tu propio código
```

> 💡 **Regla:** usa `log.info()` para eventos de negocio importantes, `log.debug()` para flujo interno, `log.warn()` para situaciones inesperadas no fatales, `log.error()` cuando algo falló. El formato `{}` de SLF4J es más eficiente que concatenar Strings con `+`.

---

## Tip 38 — "El JSON de mi respuesta tiene el campo de fecha como un número extraño"

### 📋 El escenario
Tu endpoint devuelve un campo `LocalDateTime` o `LocalDate` y en el JSON aparece como un array de números `[2026, 3, 19, 14, 30]` o como un timestamp en milisegundos en lugar de `"2026-03-19T14:30:00"`.

### ❌ El error común
```json
// ❌ Sin configurar Jackson para fechas:
{ "id": 1, "titulo": "Error", "creadoEn": [2026, 3, 19, 14, 30, 0] }

// o en milisegundos:
{ "id": 1, "titulo": "Error", "creadoEn": 1742390400000 }
```

### 🧠 ¿Cómo pienso esto?
```
Por defecto, Jackson serializa las fechas de java.time como arrays o timestamps.
Para obtener el formato ISO-8601 legible hay dos caminos:

  Opción 1: configuración global en application.yml
  Opción 2: anotación @JsonFormat en el campo específico
```

### ✅ La solución

```yaml
# application.yml — configuración global (recomendado)
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: false
    time-zone: America/Santiago
```

```java
// DTO con fechas — Jackson las serializa como ISO-8601 con la config global
record TicketResponse(
    int           id,
    String        titulo,
    LocalDateTime creadoEn,    // JSON: "2026-03-19T14:30:00"
    LocalDate     vencimiento  // JSON: "2026-03-30"
) {}

// ── Si necesitas un formato específico → @JsonFormat ──────────────────────────
public class Ticket {

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "America/Santiago")
    private LocalDateTime creadoEn;      // JSON: "19/03/2026 14:30"

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate vencimiento;       // JSON: "30/03/2026"
}

// ── Deserializar fechas que llegan del cliente ─────────────────────────────────
record TicketRequest(
    String titulo,

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate vencimiento         // el cliente envía: "2026-04-30"
) {}
```

```java
// Si usas records o necesitas el módulo JavaTime de Jackson:
// Asegúrate de tener en pom.xml:
// <dependency>
//   <groupId>com.fasterxml.jackson.datatype</groupId>
//   <artifactId>jackson-datatype-jsr310</artifactId>
// </dependency>
// Spring Boot lo incluye automáticamente con spring-boot-starter-web
```

> 💡 **Regla:** agrega `write-dates-as-timestamps: false` en `application.yml` — esa única línea hace que todas las fechas de tu API se serialicen como `"2026-03-19T14:30:00"`. Usa `@JsonFormat` solo cuando necesitas un formato personalizado distinto del ISO-8601.

---

## Tip 39 — "Mi API necesita obtener datos de otra API externa"

### 📋 El escenario
Tu servicio necesita llamar a una API de terceros o a otro microservicio: obtener el tipo de cambio, consultar datos de un usuario en otro sistema, enviar una notificación.

### ❌ El error común
```java
// ❌ Construir la petición HTTP a mano con URLConnection
URL url = new URL("https://api.ejemplo.com/datos");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
// ... 30 líneas de código para leer la respuesta
```

### 🧠 ¿Cómo pienso esto?
```
Spring Boot ofrece dos clientes HTTP modernos:
  RestClient (Spring 6.1+, recomendado) → síncrono, fluent API
  RestTemplate (anterior, aún vigente)  → síncrono, más verboso

Para cada llamada externa:
  1. Construir la URL
  2. Hacer la petición (GET, POST, etc.)
  3. Deserializar la respuesta al DTO esperado
  4. Manejar errores de red y errores HTTP
```

### ✅ La solución

```java
// ── Con RestClient (Spring 6.1+) ──────────────────────────────────────────────
@Service
public class TipoCambioService {

    private final RestClient restClient;

    public TipoCambioService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tipocambio.ejemplo.com")
                .build();
    }

    // GET /v1/tipo-cambio?moneda=USD
    public double obtenerTipoCambio(String moneda) {
        TipoCambioResponse respuesta = restClient.get()
                .uri("/v1/tipo-cambio?moneda={moneda}", moneda)
                .retrieve()
                .body(TipoCambioResponse.class);

        if (respuesta == null) throw new RuntimeException("Sin respuesta del servicio externo");
        return respuesta.valor();
    }

    // POST con body
    public void notificar(NotificacionRequest req) {
        restClient.post()
                .uri("/v1/notificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toBodilessEntity();  // no nos interesa el body de la respuesta
    }
}

// DTOs de la API externa
record TipoCambioResponse(String moneda, double valor, LocalDate fecha) {}
record NotificacionRequest(String destinatario, String mensaje) {}
```

```java
// ── Con RestTemplate (compatible con versiones anteriores) ────────────────────
@Service
public class TipoCambioServiceLegacy {

    private final RestTemplate restTemplate = new RestTemplate();

    public double obtenerTipoCambio(String moneda) {
        String url = "https://api.tipocambio.ejemplo.com/v1/tipo-cambio?moneda=" + moneda;
        TipoCambioResponse respuesta = restTemplate.getForObject(url, TipoCambioResponse.class);
        return respuesta != null ? respuesta.valor() : 0.0;
    }
}
```

> 💡 **Regla:** centraliza todas las llamadas a una API externa en un `@Service` dedicado. Nunca llames APIs externas desde el Controller. Maneja siempre el caso de respuesta nula o error de red — las APIs externas pueden fallar.

---

## Tip 40 — "Todos mis endpoints del mismo controlador repiten el path base"

### 📋 El escenario
Tienes 8 endpoints en un mismo Controller y todos empiezan con `/api/v1/tickets`. Tienes que repetir el prefijo en cada `@GetMapping`, `@PostMapping`, etc.

### ❌ El error común
```java
@RestController
public class TicketController {
    @GetMapping("/api/v1/tickets")           public ResponseEntity<?> listar() {...}
    @GetMapping("/api/v1/tickets/{id}")      public ResponseEntity<?> buscar(...) {...}
    @PostMapping("/api/v1/tickets")          public ResponseEntity<?> crear(...) {...}
    @PutMapping("/api/v1/tickets/{id}")      public ResponseEntity<?> actualizar(...) {...}
    @DeleteMapping("/api/v1/tickets/{id}")   public ResponseEntity<?> eliminar(...) {...}
}
```

### 🧠 ¿Cómo pienso esto?
```
@RequestMapping a nivel de clase define el prefijo común.
Cada método solo indica el sufijo específico (vacío si comparte exactamente la ruta base).

También se puede configurar un prefijo global para toda la API en application.yml.
```

### ✅ La solución

```java
// ── @RequestMapping en la clase — prefijo compartido ─────────────────────────
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @GetMapping              public ResponseEntity<List<Ticket>> listar() {...}
    @GetMapping("/{id}")     public ResponseEntity<Ticket>       buscar(@PathVariable int id) {...}
    @PostMapping             public ResponseEntity<Ticket>       crear(...) {...}
    @PutMapping("/{id}")     public ResponseEntity<Ticket>       actualizar(...) {...}
    @PatchMapping("/{id}")   public ResponseEntity<Ticket>       actualizarParcial(...) {...}
    @DeleteMapping("/{id}")  public ResponseEntity<Void>         eliminar(...) {...}
}

// ── Prefijo global para toda la API en application.yml ────────────────────────
// spring:
//   mvc:
//     servlet:
//       path: /api
//
// Así todos los controladores quedan bajo /api/**
// y los @RequestMapping solo necesitan: /v1/tickets

// ── Versionado por path ───────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketControllerV1 { ... }

@RestController
@RequestMapping("/api/v2/tickets")
public class TicketControllerV2 { ... }  // nueva versión con cambios incompatibles
```

> 💡 **Regla:** pon siempre `@RequestMapping` en la clase del Controller con el path base. Los métodos solo añaden el sufijo específico. Si cambias el path base, lo cambias en un solo lugar.

---

## Tip 41 — "No sé cómo exponer información del estado de mi API sin escribir endpoints a mano"

### 📋 El escenario
Necesitas saber si tu API está corriendo, cuánta memoria usa, qué configuración tiene activa, sin tener que escribir endpoints propios para cada cosa.

### 🧠 ¿Cómo pienso esto?
```
Spring Boot Actuator expone endpoints de monitoreo automáticamente:
  /actuator/health   → estado de la aplicación
  /actuator/info     → información del proyecto
  /actuator/metrics  → métricas de la JVM y de la aplicación
  /actuator/env      → variables de entorno (con cuidado en producción)
```

### ✅ La solución

```xml
<!-- pom.xml: agregar la dependencia -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics   # exponer solo estos
  endpoint:
    health:
      show-details: always              # mostrar detalles del estado

info:
  app:
    nombre: Sistema de Tickets
    version: 1.0.0
    descripcion: API de gestión de tickets de soporte
```

```http
# Verificar el estado de la API
GET /actuator/health
→ {
    "status": "UP",
    "components": {
      "db": { "status": "UP", "details": { "database": "PostgreSQL" } },
      "diskSpace": { "status": "UP" }
    }
  }

GET /actuator/info
→ { "app": { "nombre": "Sistema de Tickets", "version": "1.0.0" } }

GET /actuator/metrics/jvm.memory.used
→ { "name": "jvm.memory.used", "measurements": [{"statistic": "VALUE", "value": 123456789}] }
```

> 💡 **Regla:** en producción, expón solo `health` e `info` públicamente. Los endpoints como `env` o `beans` exponen información sensible — protégelos con Spring Security o desactívalos. Actuator es la forma estándar de monitorear tu API sin escribir endpoints propios.

---

## Tip 42 — "Mi endpoint responde muy lento porque consulta siempre lo mismo a la base de datos"

### 📋 El escenario
Ciertos datos cambian muy poco (las categorías, los roles, la configuración) pero tu API los consulta en la BD en cada petición. Esto genera latencia innecesaria y carga en la base de datos.

### 🧠 ¿Cómo pienso esto?
```
Caché: guardar el resultado de una operación costosa para reutilizarlo.

Spring @Cacheable:
  1. Primera llamada → ejecuta el método, guarda el resultado en caché
  2. Llamadas siguientes con los mismos parámetros → devuelve el caché
  3. @CacheEvict → invalida la caché cuando los datos cambian
```

### ✅ La solución

```xml
<!-- pom.xml: caché simple (en memoria, sin dependencias extra) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

```java
// Habilitar el caché en la aplicación
@SpringBootApplication
@EnableCaching
public class TicketsApplication { ... }
```

```java
@Service
public class CategoriaService {

    // ── @Cacheable: el resultado se guarda la primera vez ─────────────────────
    @Cacheable("categorias")
    public List<Categoria> listarCategorias() {
        // Esta consulta solo se ejecuta en la PRIMERA llamada
        // Las siguientes llamadas devuelven el resultado en caché
        System.out.println("Consultando BD...");   // solo aparece una vez
        return categoriaRepository.findAll();
    }

    // ── @Cacheable con clave por parámetro ────────────────────────────────────
    @Cacheable(value = "categorias", key = "#id")
    public Categoria buscarPorId(int id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    // ── @CacheEvict: invalida el caché cuando los datos cambian ───────────────
    @CacheEvict(value = "categorias", allEntries = true)
    public Categoria crear(CategoriaRequest req) {
        Categoria nueva = categoriaRepository.save(mapear(req));
        // La próxima llamada a listarCategorias() irá a la BD
        return nueva;
    }

    @CacheEvict(value = "categorias", key = "#id")
    public void eliminar(int id) {
        categoriaRepository.deleteById(id);
    }
}
```

> 💡 **Regla:** usa `@Cacheable` solo en métodos cuyos resultados cambian poco y cuya consulta es costosa. Siempre implementa `@CacheEvict` cuando los datos se modifican — si no, la caché devuelve datos desactualizados. Para producción con múltiples instancias, usa Redis en lugar del caché en memoria.

---

## Tip 43 — "Quiero documentar mis endpoints automáticamente"

### 📋 El escenario
Otros desarrolladores (o tú mismo en 3 meses) necesitan saber qué endpoints tiene tu API, qué datos reciben, qué devuelven y cómo usarlos. Mantener documentación manual es costoso y queda desactualizada.

### ✅ La solución

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

```yaml
# application.yml
springdoc:
  swagger-ui:
    path: /swagger-ui.html   # URL para ver la UI
  api-docs:
    path: /v3/api-docs        # URL del JSON de la especificación
```

```java
// Documentar el Controller con anotaciones OpenAPI
@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Tickets", description = "Gestión de tickets de soporte")
public class TicketController {

    @Operation(summary = "Listar todos los tickets",
               description = "Devuelve la lista paginada de tickets, con filtros opcionales")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<PaginaResponse<TicketResponse>> listar(...) { ... }

    @Operation(summary = "Crear un nuevo ticket")
    @ApiResponse(responseCode = "201", description = "Ticket creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @PostMapping
    public ResponseEntity<Ticket> crear(@Valid @RequestBody TicketRequest req) { ... }

    @Operation(summary = "Buscar ticket por ID")
    @ApiResponse(responseCode = "200", description = "Ticket encontrado")
    @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> buscar(@PathVariable int id) { ... }
}

// Documentar el DTO
record TicketRequest(
    @Schema(description = "Título del ticket", example = "Error en el login")
    @NotBlank String titulo,

    @Schema(description = "Prioridad de 1 (baja) a 5 (urgente)", example = "4")
    @Min(1) @Max(5) int prioridad
) {}
```

```
Acceder a la documentación:
  http://localhost:8080/swagger-ui.html  → interfaz visual interactiva
  http://localhost:8080/v3/api-docs      → especificación JSON (OpenAPI 3.0)
```

> 💡 **Regla:** SpringDoc genera automáticamente la documentación básica sin ninguna anotación. Agrega `@Operation` y `@ApiResponse` solo en endpoints donde el comportamiento no es obvio o cuando quieres indicar los posibles status codes de error. La interfaz Swagger UI permite probar los endpoints directamente desde el navegador.

---

## Tip 44 — "Necesito ejecutar código antes de que llegue la petición al Controller"

### 📋 El escenario
Quieres hacer algo para todas (o algunas) peticiones antes de que lleguen al Controller: registrar el tiempo de respuesta, agregar headers comunes, verificar un token básico, rechazar peticiones sin ciertos parámetros.

### 🧠 ¿Cómo pienso esto?
```
Spring tiene dos mecanismos para interceptar peticiones:
  HandlerInterceptor → más flexible, accede al Controller y al modelo
  Filter (javax/jakarta) → más bajo nivel, intercepta antes del DispatcherServlet

Para la mayoría de casos: HandlerInterceptor es suficiente.
```

### ✅ La solución

```java
// ── HandlerInterceptor: interceptar antes y después del Controller ─────────────
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest req,
                             HttpServletResponse res,
                             Object handler) {
        // Ejecutado ANTES del Controller
        req.setAttribute("tiempoInicio", System.currentTimeMillis());
        log.info("→ {} {}", req.getMethod(), req.getRequestURI());
        return true;   // true = continuar con la petición; false = abortar
    }

    @Override
    public void afterCompletion(HttpServletRequest req,
                                HttpServletResponse res,
                                Object handler,
                                Exception ex) {
        // Ejecutado DESPUÉS del Controller (siempre, incluso con excepción)
        long inicio   = (long) req.getAttribute("tiempoInicio");
        long duracion = System.currentTimeMillis() - inicio;
        log.info("← {} {} → {} ({}ms)",
                req.getMethod(), req.getRequestURI(), res.getStatus(), duracion);
    }
}

// ── Registrar el interceptor ──────────────────────────────────────────────────
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public WebConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/**")      // aplicar solo a /api/**
                .excludePathPatterns("/actuator/**"); // excluir actuator
    }
}
```

> 💡 **Regla:** usa `HandlerInterceptor` para lógica transversal que aplica a múltiples endpoints (logging, métricas, headers comunes). Para autenticación y autorización, Spring Security es la herramienta correcta. El método `preHandle` debe devolver `true` para que la petición continúe.

---

## Tip 45 — "Quiero enviar correos, notificaciones u otras tareas que no deben bloquear la respuesta"

### 📋 El escenario
Después de crear un ticket, necesitas enviar un correo de confirmación. Si enviar el correo tarda 2 segundos, la petición del cliente espera esos 2 segundos innecesariamente — la respuesta debería llegar antes, y el correo enviarse en segundo plano.

### 🧠 ¿Cómo pienso esto?
```
@Async: ejecuta el método en un hilo separado del pool.
  1. La petición principal recibe la respuesta inmediatamente
  2. El método marcado como @Async continúa en segundo plano

Requiere: @EnableAsync en la clase de configuración principal.
```

### ✅ La solución

```java
// Habilitar @Async
@SpringBootApplication
@EnableAsync
public class TicketsApplication { ... }
```

```java
// ── Service de notificaciones — métodos async ─────────────────────────────────
@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Async   // ← se ejecuta en un hilo del pool, no bloquea al llamador
    public void enviarConfirmacionCreacion(Ticket ticket) {
        log.info("Enviando correo para ticket #{}...", ticket.getId());
        // simular el envío
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        log.info("Correo enviado para ticket #{}", ticket.getId());
    }

    @Async
    public void notificarCambioStatus(Ticket ticket, TicketStatus statusAnterior) {
        log.info("Notificando cambio de estado #{}: {} → {}",
                ticket.getId(), statusAnterior, ticket.getStatus());
        // lógica de notificación...
    }
}

// ── Service principal: llama al async y responde inmediatamente ────────────────
@Service
public class TicketService {

    private final TicketRepository    ticketRepository;
    private final NotificacionService notificacionService;

    public Ticket crear(TicketRequest req) {
        Ticket ticket = ticketRepository.save(mapear(req));

        // Esta llamada NO bloquea — el correo se envía en segundo plano
        notificacionService.enviarConfirmacionCreacion(ticket);

        return ticket;   // respuesta inmediata al cliente
    }
}
```

> 💡 **Regla:** usa `@Async` para operaciones que no afectan el resultado de la petición: envío de correos, notificaciones, auditoría, sincronización con sistemas externos. El método `@Async` debe estar en una clase diferente al que lo llama — Spring no puede interceptar métodos `@Async` llamados desde la misma clase.

---

## Resumen del módulo 03

| Tip | Situación | Herramienta clave |
|-----|-----------|------------------|
| 31 | Migrar lógica a API con buena estructura | `@Service` + `@RestController` delgado |
| 32 | Leer y validar datos de la petición | `@PathVariable` · `@RequestParam` · `@RequestBody` · `@Valid` |
| 33 | Status HTTP correcto y errores claros | `ResponseEntity` · excepciones custom · `@RestControllerAdvice` |
| 34 | Filtrar y paginar resultados | `@RequestParam` opcionales · `Pageable` · `Page<T>` |
| 35 | Actualizar solo algunos campos | `@PatchMapping` · DTO con campos anulables |
| 36 | Valores de configuración fuera del código | `@Value` · `@ConfigurationProperties` · `application.yml` |
| 37 | Registrar lo que ocurre en la API | SLF4J `Logger` · niveles INFO / DEBUG / WARN / ERROR |
| 38 | Fechas en JSON con formato incorrecto | `write-dates-as-timestamps: false` · `@JsonFormat` |
| 39 | Consumir datos de otra API externa | `RestClient` · `RestTemplate` |
| 40 | Repetir el path base en cada endpoint | `@RequestMapping` en la clase |
| 41 | Monitorear el estado de la API | Spring Actuator · `/actuator/health` |
| 42 | Consultas lentas que siempre devuelven lo mismo | `@Cacheable` · `@CacheEvict` · `@EnableCaching` |
| 43 | Documentar endpoints automáticamente | SpringDoc OpenAPI · Swagger UI |
| 44 | Ejecutar código antes de cada petición | `HandlerInterceptor` · `WebMvcConfigurer` |
| 45 | Tareas en segundo plano sin bloquear la respuesta | `@Async` · `@EnableAsync` |

→ [Ver tabla global de tips](./README.md#tabla-global-de-tips)
