# Lección 14 — Manejo de Errores y Resiliencia

## Timeouts

### RestClient (Recomendado)
```java
@Configuration
public class RestClientConfig {
    
    @Bean
    public RestClient.Builder restClientBuilder() {
        // Configuramos la request factory con timeouts para todos los clientes
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(java.time.Duration.ofSeconds(5));   // tiempo máximo para establecer conexión
        factory.setReadTimeout(java.time.Duration.ofSeconds(10));     // tiempo máximo para recibir respuesta
        
        return RestClient.builder()
            .requestFactory(factory);  // todos los clientes construidos con este builder heredan los timeouts
    }
}
```

### FeignClient
```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          audit-service:
            connect-timeout: 5000     # 5 segundos
            read-timeout: 10000       # 10 segundos
```

### RestTemplate (Legacy - No recomendado)
```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    @Deprecated(since = "6.0", forRemoval = true)
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory();
        
        factory.setConnectTimeout(5000);    // 5 segundos
        factory.setReadTimeout(10000);      // 10 segundos
        
        return new RestTemplate(factory);
    }
}
```

---

## Reintentos Automáticos

### RestClient
```java
@Service
@Slf4j
public class NotificationClient {
    
    private final RestClient restClient;
    
    public void sendWithRetry(String title, String message, String type, String recipient) {
        retry(() -> {
            NotificationRequest req = new NotificationRequest(title, message, type, recipient);
            restClient.post()
                .uri("/api/notifications")
                .body(req)
                .retrieve()
                .toBodilessEntity();
            return null;
        }, 3);
    }
    
    private <T> T retry(Supplier<T> supplier, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (attempt == maxAttempts) throw e;
                log.warn("Attempt {} failed, retrying...", attempt);
                try {
                    Thread.sleep(1000L * attempt);  // espera incremental: 1s, 2s, 3s… (backoff lineal)
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        throw new RuntimeException("Retries exhausted");
    }
}
```

### FeignClient
```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          audit-service:
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
@Slf4j
public class TicketService {
    
    private final AuditServiceClient auditClient;
    
    @CircuitBreaker(
        name = "audit-service",
        fallbackMethod = "getAuditFallback"
    )
    public List<AuditEvent> getAuditTrail(Long ticketId) {
        return auditClient.getAuditByTicket(ticketId);
    }
    
    // Fallback: ejecutarse si circuit abre
    private List<AuditEvent> getAuditFallback(Long ticketId, Exception e) {
        log.warn("Circuit abierto para AuditService, retornando lista vacía para ticket {}", ticketId);
        return List.of();
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
    instances:
      audit-service:
        base-config: default
```

---

## Manejo de Excepciones

```java
@ExceptionHandler(HttpClientErrorException.class)
public ResponseEntity<?> handleHttpClientException(HttpClientErrorException e) {
    log.error("HTTP error: {}", e.getMessage());
    
    if (e.getStatusCode().value() == 404) {
        return ResponseEntity.notFound().build();
    }
    
    return ResponseEntity.status(e.getStatusCode())
        .body("Error en llamada a microservicio");
}

@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleGenericException(Exception e) {
    log.error("Error de comunicación: {}", e.getMessage());
    return ResponseEntity.status(503)
        .body("Servicio temporalmente no disponible");
}
```

---

## Logging Detallado

```yaml
logging:
  level:
    org.springframework.cloud.openfeign: DEBUG
    org.springframework.web.client.RestClient: DEBUG
    org.springframework.web.client.RestTemplate: DEBUG
    
    # Por cliente específico
    com.example.clients: TRACE
```

```java
// Logger personalizado para Feign
@Component
public class FeignLoggingConfiguration {
    
    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}

// Logger personalizado para RestClient
@Component
@Slf4j
public class RestClientInterceptor {
    
    public void logRequest(HttpRequest request, byte[] body) {
        log.debug("REST request: {} {}", request.getMethod(), request.getURI());
    }
    
    public void logResponse(HttpResponse response) {
        log.debug("REST response: {}", response.getStatusCode());
    }
}
```

---

*[← Volver a Lección 14](01_objetivo_y_alcance.md)*
