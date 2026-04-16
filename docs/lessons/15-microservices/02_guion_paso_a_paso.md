# Lección 13 — Comunicación entre Microservicios: Guión Paso a Paso

---

## Parte 1: RestTemplate (Simple y Flexible)

### Paso 1: Crear un Cliente HTTP Simple

```java
// Inyectar RestTemplate desde Spring
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    
    private final RestTemplate restTemplate;
    
    public TicketService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    // Llamar a otro microservicio
    public UserDTO getUser(Long userId) {
        String url = "http://localhost:8081/users/{id}";
        
        try {
            UserDTO user = restTemplate.getForObject(url, UserDTO.class, userId);
            return user;
        } catch (Exception e) {
            log.error("Error fetching user {}: {}", userId, e.getMessage());
            return null;  // o lanzar excepción
        }
    }
}
```

### Paso 2: Registrar RestTemplate en Spring

```java
// TicketsApplication.java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TicketsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### Paso 3: Usar en Tu Controlador

```java
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicket(@PathVariable Long id) {
        Ticket ticket = ticketService.findById(id);
        
        // Llamar a Users Service
        UserDTO creator = ticketService.getUser(ticket.getCreatedById());
        
        return ResponseEntity.ok(new TicketResponse(ticket, creator));
    }
}
```

---

## Parte 2: FeignClient (Automático y Elegante)

### Paso 1: Agregar Dependencias

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>4.0.3</version>
</dependency>
```

### Paso 2: Habilitar Feign en la App

```java
// TicketsApplication.java
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients  // ← AGREGAR esta anotación
public class TicketsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }
}
```

### Paso 3: Crear Cliente Feign

```java
// UserServiceClient.java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "users-service",
    url = "http://localhost:8081"  // URL del otro microservicio
)
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
    
    @GetMapping("/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable String email);
}
```

### Paso 4: Usar en Servicio

```java
// TicketService.java
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final UserServiceClient userClient;  // ← Inyectar automáticamente
    
    public TicketDetail getTicketWithUser(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        
        // Llamada automática y limpia
        UserDTO creator = userClient.getUserById(ticket.getCreatedById());
        
        return new TicketDetail(ticket, creator);
    }
}
```

---

## Parte 3: Configuración Avanzada (Timeouts, Reintentos)

### Configuración en `application.yml`

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          users-service:
            connect-timeout: 5000      # 5 segundos para conectar
            read-timeout: 10000        # 10 segundos para leer
            logger-level: BASIC        # Log de requests/responses
```

### Implementar Fallback (Qué Hacer si Falla)

```java
// UserServiceClient.java
@FeignClient(
    name = "users-service",
    url = "http://localhost:8081",
    fallback = UserServiceClientFallback.class  // ← Fallback
)
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}

// UserServiceClientFallback.java (ejecutar si falla)
@Component
public class UserServiceClientFallback implements UserServiceClient {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public UserDTO getUserById(Long id) {
        log.warn("Users Service indisponible, usando fallback para usuario {}", id);
        
        // Retornar objeto por defecto
        return UserDTO.builder()
            .id(id)
            .name("Usuario Desconocido")
            .email("unknown@example.com")
            .build();
    }
}
```

---

## Parte 4: RestTemplate con Configuración

```java
// RestTemplateConfig.java
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(5))    // Timeout conexión
            .setReadTimeout(Duration.ofSeconds(10))       // Timeout lectura
            .build();
    }
}
```

---

## Parte 5: Manejo de Errores

```java
// Con RestTemplate
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final RestTemplate restTemplate;
    
    public UserDTO getUser(Long userId) {
        String url = "http://localhost:8081/users/{id}";
        
        try {
            UserDTO user = restTemplate.getForObject(url, UserDTO.class, userId);
            return user;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Usuario {} no encontrado", userId);
            return null;
        } catch (HttpServerErrorException e) {
            log.error("Error en Users Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Users Service temporalmente indisponible");
        } catch (ResourceAccessException e) {
            log.error("No puedo conectar a Users Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Users Service no responde");
        }
    }
}
```

---

## Comparación Rápida

```
RestTemplate:
GET http://localhost:8081/users/1
POST http://localhost:8081/users
DELETE http://localhost:8081/users/1

Código:
    UserDTO user = restTemplate.getForObject(url, UserDTO.class, 1);

─────────────────────────────────────

FeignClient (más limpio):
    UserDTO user = userClient.getUserById(1L);

Automático:
✓ Serialización/deserialización
✓ Manejo de errores
✓ Timeouts
✓ Reintentos
```

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
