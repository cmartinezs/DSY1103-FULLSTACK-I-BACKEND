# Lección 14 — Ejemplos Prácticos Completos

## Escenario: Tickets + NotificationService + AuditService

### Servicio 1: NotificationService (Puerto 8081)

```java
// NotificationController.java — endpoints relevantes para el cliente
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NotificationRequest body) {
        // body: { title, message, type (default "INFO"), recipient (default "all") }
        // response: { id, title, message, type, recipient, sent: false, timestamp }
        ...
    }
    
    @GetMapping
    public ResponseEntity<List<?>> getAll() { ... }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) { ... }
}
```

---

### Servicio 2: AuditService (Puerto 8082)

```java
// AuditController.java — endpoints relevantes para el cliente
@RestController
@RequestMapping("/api/audit")
public class AuditController {
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AuditRequest body) {
        // body: { action, entityType (default "Ticket"), entityId, userId, username (default "system"), details }
        // response: { id, action, entityType, entityId, userId, username, details, timestamp }
        ...
    }
    
    @GetMapping
    public ResponseEntity<List<?>> getAll() { ... }
    
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<?>> getByTicket(@PathVariable Long ticketId) { ... }
}
```

---

### Servicio 3: Tickets Service (Puerto 8080 — Cliente)

#### Opción A: Con RestClient (NotificationClient → NotificationService)

```java
// RestClientConfig.java
@Configuration
public class RestClientConfig {
    
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
            .requestFactory(new org.springframework.http.client.BufferingClientHttpRequestFactory(
                new org.springframework.http.client.SimpleClientHttpRequestFactory()
            ));
    }
}

// NotificationClient.java
@Service
@Slf4j
public class NotificationClient {
    
    private final RestClient restClient;
    
    public NotificationClient(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("http://localhost:8081")
            .build();
    }
    
    public void send(String title, String message, String type, String recipient) {
        try {
            NotificationRequest request = new NotificationRequest(title, message, type, recipient);
            restClient.post()
                .uri("/api/notifications")
                .body(request)
                .retrieve()
                .toBodilessEntity();
            log.info("Notificación enviada a '{}': {}", recipient, title);
        } catch (Exception e) {
            log.error("Error enviando notificación a '{}': {}", recipient, e.getMessage());
        }
    }
}

// TicketService.java (con RestClient)
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final NotificationClient notificationClient;
    
    public TicketResult create(TicketRequest request) {
        // ... lógica de creación ...
        Ticket saved = ticketRepository.save(ticket);
        
        if (saved.getAssignedTo() != null) {
            notificationClient.send(
                "Nuevo ticket asignado",
                "Se te ha asignado el ticket '" + saved.getTitle() + "'",
                "INFO",
                saved.getAssignedTo().getEmail()
            );
        }
        
        return toResult(saved);
    }
    
    public TicketResult assignTicket(Long id, Long userId) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        // ... lógica de asignación ...
        Ticket saved = ticketRepository.save(ticket);
        
        notificationClient.send(
            "Ticket asignado",
            "Se te ha asignado el ticket '" + saved.getTitle() + "'",
            "INFO",
            saved.getAssignedTo().getEmail()
        );
        
        return toResult(saved);
    }
}
```

#### Opción B: Con FeignClient (AuditServiceClient → AuditService)

```java
// TicketsApplication.java
@SpringBootApplication
@EnableFeignClients
public class TicketsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }
}

// AuditServiceClient.java (con Feign)
@FeignClient(
    name = "audit-service",
    url = "http://localhost:8082",
    fallback = AuditServiceClientFallback.class
)
public interface AuditServiceClient {
    
    @PostMapping("/api/audit")
    AuditEvent logEvent(@RequestBody AuditRequest request);
    
    @GetMapping("/api/audit/ticket/{ticketId}")
    List<AuditEvent> getAuditByTicket(@PathVariable Long ticketId);
}

// AuditServiceClientFallback.java
@Component
@Slf4j
public class AuditServiceClientFallback implements AuditServiceClient {
    
    @Override
    public AuditEvent logEvent(AuditRequest request) {
        log.warn("AuditService no disponible, evento no registrado: {}", request.action());
        return null;
    }
    
    @Override
    public List<AuditEvent> getAuditByTicket(Long ticketId) {
        log.warn("AuditService no disponible, sin historial para ticket {}", ticketId);
        return List.of();
    }
}

// TicketService.java (con FeignClient)
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final AuditServiceClient auditClient;
    
    public TicketResult updateById(Long id, TicketRequest request) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        String previousStatus = ticket.getStatus();
        
        // ... actualizar campos ...
        Ticket saved = ticketRepository.save(ticket);
        
        auditClient.logEvent(new AuditRequest(
            "STATUS_CHANGE",
            "Ticket",
            saved.getId(),
            null,
            "system",
            previousStatus + " → " + saved.getStatus()
        ));
        
        return toResult(saved);
    }
    
    public List<AuditEvent> getAuditTrail(Long ticketId) {
        return auditClient.getAuditByTicket(ticketId);
    }
}
```

#### Opción C: Con RestTemplate (Legacy — No recomendado)

> Ilustra el patrón con **ReportsService** (puerto 8083), un servicio que se implementará en una lección futura.

```java
// TicketsApplication.java (DEPRECADO)
@SpringBootApplication
public class TicketsApplication {
    
    @Bean
    @Deprecated(since = "6.0", forRemoval = true)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// ReportsClient.java (con RestTemplate — DEPRECADO)
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsClient {
    
    private final RestTemplate restTemplate;
    
    public void generateReport(Long ticketId, String type) {
        String url = "http://localhost:8083/api/reports";  // ReportsService (lección futura)
        ReportRequest req = new ReportRequest(ticketId, type);
        
        try {
            restTemplate.postForObject(url, req, Void.class);
            log.info("Reporte '{}' solicitado para ticket {}", type, ticketId);
        } catch (Exception e) {
            log.error("Error solicitando reporte: {}", e.getMessage());
        }
    }
}
```

---

## Configuración en `application.yml`

### Con RestClient
```yaml
spring:
  application:
    name: tickets-service

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"

logging:
  level:
    org.springframework.web.client: DEBUG
```

### Con FeignClient
```yaml
spring:
  application:
    name: tickets-service
  
  cloud:
    openfeign:
      client:
        config:
          default:
            connect-timeout: 5000
            read-timeout: 10000
            logger-level: BASIC
          
          audit-service:
            connect-timeout: 3000
            read-timeout: 8000
            logger-level: FULL

server:
  port: 8080
  servlet:
    context-path: "/ticket-app"

logging:
  level:
    org.springframework.cloud.openfeign: DEBUG
```

---

## DTOs

```java
// NotificationRequest.java — cuerpo enviado a NotificationService
public record NotificationRequest(String title, String message, String type, String recipient) {}

// AuditRequest.java — cuerpo enviado a AuditService
public record AuditRequest(String action, String entityType, Long entityId, Long userId, String username, String details) {}

// AuditEvent.java — respuesta de AuditService
public record AuditEvent(Long id, String action, String entityType, Long entityId, Long userId, String username, String details, Long timestamp) {}
```

---

*[← Volver a Lección 14](01_objetivo_y_alcance.md)*
