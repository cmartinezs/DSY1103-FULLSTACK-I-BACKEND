# 02 - Guion paso a paso (docente + alumnos)

## Bloque 1 (20 min): del endpoint simple a una API mantenible

### Pregunta gatillo

Si el `Controller` valida, busca datos, transforma salida y maneja errores, ¿qué pasa cuando el proyecto crece?

### Idea central

Separar responsabilidades no es burocracia: reduce acoplamiento, facilita pruebas y mejora mantenibilidad.

## Bloque 2 (35 min): construir la estructura CSR

Crear paquetes:

- `controller`
- `service`
- `repository`
- `model`

Flujo que deben memorizar:

`HTTP -> Controller -> Service -> Repository -> Service -> Controller -> HTTP`

## Bloque 3 (35 min): implementar caso base Ticket

### 1) Modelo

```java
public class Ticket {
    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;

    public Ticket() {}

    public Ticket(Long id, String titulo, String descripcion, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // getters y setters
}
```

### 2) Repository en memoria

```java
@Repository
public class TicketRepository {

    private final List<Ticket> tickets = new ArrayList<>();

    public TicketRepository() {
        tickets.add(new Ticket(1L, "No funciona login", "Usuario no puede ingresar", "ABIERTO"));
        tickets.add(new Ticket(2L, "Error dashboard", "Pantalla principal falla", "EN_PROCESO"));
    }

    public List<Ticket> findAll() {
        return tickets;
    }
}
```

### 3) Service (lógica)

```java
@Service
public class TicketService {

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public List<Ticket> obtenerTodos() {
        return repository.findAll();
    }
}
```

### 4) Controller (HTTP)

```java
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }
}
```

## Bloque 4 (20 min): pruebas funcionales

Probar en Postman/Insomnia:

- `GET /api/v1/tickets` -> `200 OK`
- Body JSON de arreglo de tickets

## Bloque 4.5 (10 min): configuración mínima y personalización

Objetivo: mostrar que la app se puede adaptar sin tocar código de capas CSR.

### 1) Cambiar puerto y context path

En `src/main/resources/application.properties`:

```properties
server.port=8081
server.servlet.context-path=/tickets-app
```

Con esto, el endpoint queda:

- `GET /tickets-app/api/v1/tickets`

### 2) Personalizar banner de inicio

Crear `src/main/resources/banner.txt`:

```text
=== TICKETS API - CSR CLASS ===
```

Al levantar la aplicación, Spring Boot mostrará este texto en consola.

### 3) Verificar comportamiento

- Reiniciar aplicación
- Confirmar que arranca en puerto `8081`
- Probar `GET /tickets-app/api/v1/tickets` -> `200 OK`

## Bloque 5 (10 min): cierre conceptual

Preguntas de cierre:

- ¿Qué parte fue HTTP puro?
- ¿Dónde iría una regla de negocio?
- ¿Dónde iría acceso a BD cuando cambiemos de memoria a JPA?

## Extensión opcional (solo si el ritmo del curso lo permite)

Agregar `GET /api/v1/tickets/estado/{estado}` para mostrar cómo escalar sin romper CSR.
