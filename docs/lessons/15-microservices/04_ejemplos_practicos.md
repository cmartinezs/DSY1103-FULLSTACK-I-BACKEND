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

#### Opción A: Con RestTemplate

```java
// TicketsApplication.java
@SpringBootApplication
public class TicketsApplication {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// TicketService.java
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    
    public TicketDetailDTO getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        // Llamar a Users Service
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
            UserDTO user = restTemplate.getForObject(url, UserDTO.class, userId);
            return user;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User {} not found in Users Service", userId);
            return null;
        } catch (Exception e) {
            log.error("Error communicating with Users Service: {}", e.getMessage(), e);
            return null;
        }
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

// UserServiceClient.java
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
public class UserServiceClientFallback implements UserServiceClient {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceClientFallback.class);
    
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
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final UserServiceClient userClient;  // ← Inyectar automáticamente
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    
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

---

## Configuración en `application.yml`

```yaml
spring:
  application:
    name: tickets-service
  
  cloud:
    openfeign:
      client:
        config:
          # Global
          default:
            connect-timeout: 5000
            read-timeout: 10000
            logger-level: BASIC
          
          # Por servicio específico
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
    org.springframework.web.client.RestTemplate: DEBUG
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
