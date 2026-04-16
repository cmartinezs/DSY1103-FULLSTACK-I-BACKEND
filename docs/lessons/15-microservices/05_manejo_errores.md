# Lección 13 — Manejo de Errores y Resiliencia

## Timeouts

```java
// RestTemplate con timeout
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory();
        
        factory.setConnectTimeout(5000);    // 5 segundos
        factory.setReadTimeout(10000);      // 10 segundos
        
        return new RestTemplate(factory);
    }
}
```

```yaml
# FeignClient con timeout
spring:
  cloud:
    openfeign:
      client:
        config:
          users-service:
            connect-timeout: 5000
            read-timeout: 10000
```

---

## Reintentos Automáticos

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          users-service:
            # Reintentos
            max-attempts: 3
            retry-delay: 1000  # 1 segundo entre intentos
```

---

## Circuit Breaker (Resilience4j)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>
```

```java
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final UserServiceClient userClient;
    
    @CircuitBreaker(
        name = "users-service",
        fallbackMethod = "getUserFallback"
    )
    public UserDTO getUser(Long userId) {
        return userClient.getUserById(userId);
    }
    
    // Fallback: ejecutarse si circuit abre
    private UserDTO getUserFallback(Long userId, Exception e) {
        log.warn("Circuit abierto para Users Service, usando fallback", e);
        return UserDTO.builder()
            .id(userId)
            .name("Usuario Temporal")
            .build();
    }
}
```

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: 50         # Abrir si 50% de llamadas falla
        wait-duration-in-open-state: 10s   # Esperar 10s antes de reintentar
        permitted-number-of-calls-in-half-open-state: 3
```

---

## Manejo de Excepciones

```java
@ExceptionHandler(FeignException.class)
public ResponseEntity<?> handleFeignException(FeignException e) {
    log.error("Feign error: {}", e.getMessage());
    
    if (e.status() == 404) {
        return ResponseEntity.notFound().build();
    }
    
    if (e.status() >= 500) {
        return ResponseEntity.status(503)
            .body("Servicio temporalmente no disponible");
    }
    
    return ResponseEntity.status(e.status())
        .body("Error en llamada a microservicio");
}
```

---

## Logging Detallado

```yaml
logging:
  level:
    org.springframework.cloud.openfeign: DEBUG
    org.springframework.web.client: DEBUG
    
    # Por cliente específico
    com.example.clients: TRACE
```

```java
// Logger personalizado
@Component
public class FeignLoggingConfiguration {
    
    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
```

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
