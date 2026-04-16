# Lección 13 — Ejemplos Prácticos Completos

## Escenario: Tickets + Users Microservices

### Servicio 1: Users Service (Puerto 8081)

```java
// UserController.java
@RestController
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserDTO.from(user));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(UserDTO.from(user));
    }
}
```

---

### Servicio 2: Tickets Service (Puerto 8080 - Cliente)

#### Opción A: Con RestClient (Recomendado)

```java
// RestClientConfig.java
@Configuration
public class RestClientConfig {
    
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}

// UserServiceClient.java (con RestClient)
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    
    private final RestClient.Builder restClientBuilder;
    private RestClient restClient;
    
    @PostConstruct
    public void init() {
        this.restClient = restClientBuilder
            .baseUrl("http://localhost:8081")
            .build();
    }
    
    public UserDTO getUserById(Long id) {
        try {
            log.info("Fetching user {} from Users Service", id);
            return restClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .body(UserDTO.class);
        } catch (Exception e) {
            log.error("Error fetching user {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    public UserDTO getUserByEmail(String email) {
        try {
            return restClient.get()
                .uri("/users/email/{email}", email)
                .retrieve()
                .body(UserDTO.class);
        } catch (Exception e) {
            log.error("Error fetching user by email {}: {}", email, e.getMessage());
            return null;
        }
    }
}

// TicketService.java (con RestClient)
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final UserServiceClient userClient;
    
    public TicketDetailDTO getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        // Llamar a Users Service
        UserDTO creator = userClient.getUserById(ticket.getCreatedById());
        
        return TicketDetailDTO.builder()
            .ticket(ticket)
            .creator(creator)
            .build();
    }
}
```

#### Opción B: Con FeignClient

```java
// TicketsApplication.java
@SpringBootApplication
@EnableFeignClients  // ← AGREGAR esta anotación
public class TicketsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }
}

// UserServiceClient.java (con Feign)
@FeignClient(
    name = "users-service",
    url = "http://localhost:8081",
    fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
    
    @GetMapping("/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable String email);
}

// UserServiceClientFallback.java
@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {
    
    @Override
    public UserDTO getUserById(Long id) {
        log.warn("Users Service unavailable, using fallback for user {}", id);
        return UserDTO.builder()
            .id(id)
            .name("Usuario Desconocido")
            .email("unknown@example.com")
            .build();
    }
    
    @Override
    public UserDTO getUserByEmail(String email) {
        log.warn("Users Service unavailable, using fallback for email {}", email);
        return null;
    }
}

// TicketService.java (con FeignClient)
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final UserServiceClient userClient;  // ← Inyectar automáticamente
    
    public TicketDetailDTO getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        // Llamar a Users Service (automático)
        UserDTO creator = userClient.getUserById(ticket.getCreatedById());
        
        return TicketDetailDTO.builder()
            .ticket(ticket)
            .creator(creator)
            .build();
    }
}
```

#### Opción C: Con RestTemplate (Legacy - No recomendado)

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

// TicketService.java (con RestTemplate - DEPRECADO)
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final RestTemplate restTemplate;
    
    public TicketDetailDTO getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        UserDTO creator = getUserFromService(ticket.getCreatedById());
        
        return TicketDetailDTO.builder()
            .ticket(ticket)
            .creator(creator)
            .build();
    }
    
    private UserDTO getUserFromService(Long userId) {
        String url = "http://localhost:8081/users/{id}";
        
        try {
            log.info("Fetching user {} from Users Service", userId);
            return restTemplate.getForObject(url, UserDTO.class, userId);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User {} not found", userId);
            return null;
        } catch (Exception e) {
            log.error("Error communicating with Users Service: {}", e.getMessage());
            return null;
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
          
          users-service:
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

## Modelos DTO

```java
// UserDTO.java
@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    
    public static UserDTO from(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }
}

// TicketDetailDTO.java
@Data
@Builder
public class TicketDetailDTO {
    private Ticket ticket;
    private UserDTO creator;
}

// NotificationRequest.java
@Data
@AllArgsConstructor
public class NotificationRequest {
    private Long userId;
    private String message;
    private String type;
}
```

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
