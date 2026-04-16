# Lección 13 — Comunicación entre Microservicios: Guión Paso a Paso

---

## Parte 1: RestClient (Recomendado - Spring 6.1+)

### Paso 1: Crear un Cliente HTTP Moderno

```java
// NotificationClient.java
import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationClient {
    
    private final RestClient restClient;
    
    public NotificationClient(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("http://localhost:8082")  // URL del servicio de notificaciones
            .build();
    }
    
    // Enviar notificación
    public void sendNotification(Long userId, String message) {
        try {
            NotificationRequest request = new NotificationRequest(userId, message);
            
            restClient.post()
                .uri("/notifications")
                .body(request)
                .retrieve()
                .toBodilessEntity();
                
            log.info("Notificación enviada a usuario {}", userId);
        } catch (Exception e) {
            log.error("Error enviando notificación a usuario {}: {}", userId, e.getMessage());
        }
    }
    
    // Obtener notificación
    public NotificationDTO getNotification(Long notificationId) {
        try {
            return restClient.get()
                .uri("/notifications/{id}", notificationId)
                .retrieve()
                .body(NotificationDTO.class);
        } catch (Exception e) {
            log.error("Error obteniendo notificación {}: {}", notificationId, e.getMessage());
            return null;
        }
    }
}
```

### Paso 2: Configurar RestClient en Spring

```java
// RestClientConfig.java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import java.time.Duration;

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
```

### Paso 3: Usar en Tu Servicio

```java
// TicketService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final NotificationClient notificationClient;
    
    public Ticket createTicket(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        // ... más campos
        
        Ticket saved = ticketRepository.save(ticket);
        
        // Notificar a través del microservicio
        notificationClient.sendNotification(
            ticket.getCreatedById(),
            "Tu ticket '" + ticket.getTitle() + "' ha sido creado"
        );
        
        return saved;
    }
}
```

---

## Parte 2: FeignClient (Alternativa - Múltiples Llamadas)

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
    url = "http://localhost:8081",  // URL del otro microservicio
    fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
    
    @GetMapping("/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable String email);
}
```

### Paso 4: Implementar Fallback

```java
// UserServiceClientFallback.java
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {
    
    @Override
    public UserDTO getUserById(Long id) {
        log.warn("Users Service no disponible, usando fallback para usuario {}", id);
        
        // Retornar objeto por defecto
        return UserDTO.builder()
            .id(id)
            .name("Usuario Desconocido")
            .email("unknown@example.com")
            .build();
    }
    
    @Override
    public UserDTO getUserByEmail(String email) {
        log.warn("Users Service no disponible, usando fallback para email {}", email);
        return null;  // o un valor por defecto
    }
}
```

### Paso 5: Usar en Servicio

```java
// TicketService.java
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final UserServiceClient userClient;
    
    public TicketDetail getTicketWithUser(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        
        // Llamada automática y limpia
        UserDTO creator = userClient.getUserById(ticket.getCreatedById());
        
        return new TicketDetail(ticket, creator);
    }
}
```

---

## Parte 3: Configuración de Timeouts y Reintentos

### RestClient con Timeouts

```java
// RestClientConfig.java
@Configuration
public class RestClientConfig {
    
    @Bean
    public RestClient notificationClient(RestClient.Builder builder) {
        return builder
            .baseUrl("http://localhost:8082")
            .requestInitializer(request -> {
                request.getHeaders().set("Connection", "Keep-Alive");
            })
            .build();
    }
}
```

### FeignClient con Timeouts

```yaml
# application.yml
spring:
  cloud:
    openfeign:
      client:
        config:
          users-service:
            connect-timeout: 5000      # 5 segundos para conectar
            read-timeout: 10000        # 10 segundos para leer
            logger-level: BASIC        # Log de requests/responses
          default:
            connect-timeout: 5000
            read-timeout: 10000
```

---

## Parte 4: RestTemplate (Legacy - No Recomendado)

⚠️ **RestTemplate está deprecado desde Spring 6.0. Solo usar si necesitas mantener código legacy.**

```java
// RestTemplateConfig.java (DEPRECADO)
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    @Deprecated(since = "6.0", forRemoval = true)
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(10))
            .build();
    }
}
```

```java
// Uso (DEPRECADO)
@Service
public class NotificationServiceLegacy {
    
    private final RestTemplate restTemplate;
    
    public void notifyUser(Long userId, String message) {
        String url = "http://localhost:8082/notifications";
        NotificationRequest req = new NotificationRequest(userId, message);
        
        try {
            restTemplate.postForObject(url, req, Void.class);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
```

---

## Resumen: ¿Cuál Usar?

| Situación | Recomendación |
|-----------|---------------|
| **Proyecto nuevo, Spring 6.1+** | ✅ **RestClient** |
| **Múltiples servicios, código limpio** | ✅ **FeignClient** |
| **Código legacy, Spring <6.0** | ⚠️ **RestTemplate** |
| **Una llamada simple** | ✅ **RestClient** |

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
