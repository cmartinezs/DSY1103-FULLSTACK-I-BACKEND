# Lección 14 — Comunicación entre Microservicios: Guión Paso a Paso

---

## Parte 1: RestClient (Recomendado - Spring 6.1+)

### Paso 1: Crear el Cliente HTTP

El primer paso es crear una clase dedicada que encapsule toda la lógica de comunicación con el microservicio externo. Separar esta responsabilidad en su propio `@Service` mantiene al `TicketService` limpio de detalles de red y facilita reemplazar o mockear el cliente en pruebas.

> **¿Por qué inyectar `RestClient.Builder` y no `RestClient` directamente?**  
> Spring Boot auto-configura un bean `RestClient.Builder`. Cada cliente HTTP recibe ese builder, lo personaliza con su propia `baseUrl` y lo convierte en un `RestClient` inmutable. Si inyectáramos `RestClient` directamente, todos los clientes compartirían la misma instancia con la misma URL base.

```java
// NotificationClient.java
import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationClient {
    
    private final RestClient restClient;
    
    // Spring inyecta el RestClient.Builder preconfigurado (no lo instanciamos nosotros)
    public NotificationClient(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("http://localhost:8081")  // URL base de NotificationService
            .build();                           // materializa el cliente inmutable para esta clase
    }
    
    // Patrón fire-and-forget: si falla, el llamador ya completó su operación principal
    public void send(String title, String message, String type, String recipient) {
        try {
            NotificationRequest request = new NotificationRequest(title, message, type, recipient);
            
            restClient.post()              // 1. Método HTTP: POST
                .uri("/api/notifications") // 2. Ruta en NotificationService
                .body(request)            // 3. Cuerpo: Java → JSON automáticamente (Jackson)
                .retrieve()               // 4. Ejecuta la solicitud HTTP
                .toBodilessEntity();      // 5. Descarta el cuerpo de respuesta (solo nos importa el éxito)
                
            log.info("Notificación enviada a '{}': {}", recipient, title);
        } catch (Exception e) {
            // Si la notificación falla, el ticket ya fue guardado: no revertimos nada.
            // Solo logueamos para monitoreo.
            log.error("Error enviando notificación a '{}': {}", recipient, e.getMessage());
        }
    }
}
```

### Paso 2: Configurar RestClient en Spring

Spring Boot ya registra un `RestClient.Builder` disponible para inyectar, por lo que esta configuración es **opcional**. Se vuelve útil cuando necesitas aplicar comportamiento transversal a todos los clientes: interceptores de logging, headers globales, o una `requestFactory` con timeouts personalizados.

> Si no defines este `@Bean`, Spring Boot usa su propio builder por defecto, que es suficiente para empezar.

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
        // BufferingClientHttpRequestFactory permite leer el cuerpo de la respuesta
        // más de una vez (útil para interceptores de logging que también lo necesitan).
        return RestClient.builder()
            .requestFactory(new org.springframework.http.client.BufferingClientHttpRequestFactory(
                new org.springframework.http.client.SimpleClientHttpRequestFactory()
            ));
    }
}
```

### Paso 3: Integrar en el Servicio Existente

`NotificationClient` se inyecta en `TicketService` exactamente igual que un repositorio: como campo `final`, para que `@RequiredArgsConstructor` lo incluya en el constructor generado. No hay ninguna diferencia en la forma de inyectarlo respecto a un bean local.

```java
// TicketService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final NotificationClient notificationClient;  // ← nuevo campo; Spring lo inyecta igual que el repositorio
    
    public TicketResult create(TicketRequest request) {
        // ... lógica existente de creación ...
        Ticket saved = ticketRepository.save(ticket);
        
        // Notificar al asignado si existe, via NotificationService (puerto 8081)
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
}
```

---

## Parte 2: FeignClient (Alternativa - Múltiples Llamadas)

FeignClient es un cliente HTTP **declarativo**: defines una interfaz Java con los métodos que quieres llamar, y Spring genera la implementación en tiempo de ejecución. Es ideal cuando tienes muchas llamadas distintas al mismo servicio, ya que centraliza todo el contrato en una sola interfaz sin escribir código HTTP repetitivo.

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

### Paso 3: Declarar el Contrato del Servicio Remoto

En lugar de escribir código HTTP, defines la interfaz del servicio remoto usando anotaciones familiares (`@GetMapping`, `@PathVariable`). Feign genera la implementación en tiempo de ejecución e inyecta el resultado como cualquier `@Bean`.

> **Atributos de `@FeignClient`:**
> - `name`: identificador lógico del servicio; se usa como clave en `application.yml` para configurar timeouts y nivel de log por cliente
> - `url`: URL base del microservicio remoto
> - `fallback`: clase que implementa esta misma interfaz y se ejecuta automáticamente cuando el servicio no responde

```java
// AuditServiceClient.java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(
    name = "audit-service",                    // clave para configuración en application.yml
    url = "http://localhost:8082",             // URL base de AuditService
    fallback = AuditServiceClientFallback.class
)
public interface AuditServiceClient {
    
    @PostMapping("/api/audit")
    AuditEvent logEvent(@RequestBody AuditRequest request);
    
    @GetMapping("/api/audit/ticket/{ticketId}")
    List<AuditEvent> getAuditByTicket(@PathVariable Long ticketId);
}
```

### Paso 4: Implementar el Fallback

El fallback debe implementar la misma interfaz que el `@FeignClient`. Cuando el servicio remoto no responde (timeout, error 5xx, red caída), Feign llama automáticamente al método equivalente del fallback en lugar de lanzar una excepción, permitiendo que la aplicación continúe funcionando con datos parciales o valores por defecto.

```java
// AuditServiceClientFallback.java
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Component
@Slf4j
public class AuditServiceClientFallback implements AuditServiceClient {
    
    @Override
    public AuditEvent logEvent(AuditRequest request) {
        log.warn("AuditService no disponible, evento no registrado: {}", request.action());
        return null;  // el ticket ya fue guardado; solo se pierde el log de auditoría
    }
    
    @Override
    public List<AuditEvent> getAuditByTicket(Long ticketId) {
        log.warn("AuditService no disponible, sin historial de auditoría para ticket {}", ticketId);
        return List.of();  // retornar lista vacía en lugar de lanzar excepción
    }
}
```

### Paso 5: Integrar en el Servicio

La ventaja de Feign se aprecia al integrar en el servicio: el cliente se inyecta igual que cualquier `@Service` o repositorio, y las llamadas HTTP parecen llamadas a métodos locales.

```java
// TicketService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final AuditServiceClient auditClient;  // ← Feign inyecta la implementación generada
    
    public TicketResult updateById(Long id, TicketRequest request) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        String previousStatus = ticket.getStatus();
        
        // ... actualizar campos ...
        Ticket saved = ticketRepository.save(ticket);
        
        // Registrar en AuditService (automático via Feign)
        auditClient.logEvent(new AuditRequest(
            "STATUS_CHANGE",
            "Ticket",
            saved.getId(),
            null,       // sin usuario autenticado en este ejemplo
            "system",
            previousStatus + " → " + saved.getStatus()
        ));
        
        return toResult(saved);
    }
    
    public List<AuditEvent> getAuditTrail(Long ticketId) {
        return auditClient.getAuditByTicket(ticketId);  // Feign hace el HTTP GET por detrás
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
    public RestClient.Builder restClientBuilder() {
        // Configuramos la request factory con timeouts para todos los clientes
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(java.time.Duration.ofSeconds(5));   // máximo para establecer conexión
        factory.setReadTimeout(java.time.Duration.ofSeconds(10));     // máximo para recibir respuesta
        
        return RestClient.builder()
            .requestFactory(factory);  // NotificationClient (y cualquier otro) heredarán estos timeouts
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
          audit-service:
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

> El siguiente ejemplo usa **ReportsService** (puerto 8083), un servicio que se implementará en una lección posterior. Se incluye aquí únicamente para ilustrar el patrón RestTemplate en contraste con RestClient.

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
// ReportsClient.java (DEPRECADO — usar RestClient en proyectos nuevos)
@Service
@Slf4j
public class ReportsClient {
    
    private final RestTemplate restTemplate;
    
    public ReportsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
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

## Resumen: ¿Cuál Usar?

| Situación | Recomendación |
|-----------|---------------|
| **Proyecto nuevo, Spring 6.1+** | ✅ **RestClient** |
| **Múltiples servicios, código limpio** | ✅ **FeignClient** |
| **Código legacy, Spring <6.0** | ⚠️ **RestTemplate** |
| **Una llamada simple** | ✅ **RestClient** |

---

*[← Volver a Lección 14](01_objetivo_y_alcance.md)*
