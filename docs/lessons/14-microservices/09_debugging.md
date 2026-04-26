# Lección 14 — Debugging y Troubleshooting

## Error 1: "Connection refused"

**Síntoma:**
```
java.net.ConnectException: Connection refused
```

**Causas:**
- El otro microservicio no está corriendo
- URL incorrecta (puerto, host)
- Firewall bloqueando

**Solución:**
```bash
# Linux/macOS: verificar que los servicios están corriendo
lsof -i :8081  # NotificationService
lsof -i :8082  # AuditService

# Windows: equivalente
netstat -aon | findstr :8081
netstat -aon | findstr :8082

# Probar conexión manualmente
curl http://localhost:8081/api/notifications
curl http://localhost:8082/api/audit
```

---

## Error 2: "Read timed out"

**Síntoma:**
```
java.net.SocketTimeoutException: Read timed out
```

**Causa:** El servicio tarda más del timeout configurado.

**Solución:**

### Con RestClient
```java
// Configurar en RestClient builder
restClient = builder
    .baseUrl("http://localhost:8081")
    .build();
```

### Con FeignClient
```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          audit-service:
            read-timeout: 30000  # Aumentar a 30 segundos
```

### Con RestTemplate (Legacy)
```java
@Bean
public RestTemplate restTemplate() {
    HttpComponentsClientHttpRequestFactory factory = 
        new HttpComponentsClientHttpRequestFactory();
    factory.setReadTimeout(30000);
    return new RestTemplate(factory);
}
```

---

## Error 3: "Bean not found"

**Síntoma (RestClient):**
```
Error creating bean with name 'restClientBuilder'
```

**Síntoma (FeignClient):**
```
Error creating bean with name 'auditServiceClient'
No bean of type 'AuditServiceClient' found
```

**Causa FeignClient:** `@EnableFeignClients` no está en la app principal.

**Solución:**
```java
@SpringBootApplication
@EnableFeignClients  // ← AGREGAR solo para Feign
public class TicketsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }
}
```

---

## Error 4: "Invalid content-type"

**Síntoma:**
```
Content-Type: text/plain; charset=UTF-8
Could not deserialize response body of type [class UserDTO]
```

**Causa:** Respuesta no es JSON.

**Solución:**
1. Verificar que el servicio devuelve `Content-Type: application/json`
2. Probar manualmente:
```bash
curl -v http://localhost:8081/api/notifications
curl -v http://localhost:8082/api/audit
# Ver headers en respuesta
```

---

## Debugging: Logs Detallados

### RestClient
```yaml
logging:
  level:
    org.springframework.web.client.RestClient: DEBUG
```

### FeignClient
```yaml
logging:
  level:
    org.springframework.cloud.openfeign: DEBUG
    feign.Logger: DEBUG
```

### RestTemplate (Legacy)
```yaml
logging:
  level:
    org.springframework.web.client.RestTemplate: DEBUG
```

**Verás en logs:**
```
[AuditServiceClient#logEvent] ---> POST http://localhost:8082/api/audit HTTP/1.1
[AuditServiceClient#logEvent] Content-Type: application/json
[AuditServiceClient#logEvent] ---> END HTTP (87-byte body)
[AuditServiceClient#logEvent] <--- HTTP/1.1 200 OK (45ms)
[AuditServiceClient#logEvent] Content-Type: application/json
[AuditServiceClient#logEvent] {"id":1,"action":"STATUS_CHANGE","entityType":"Ticket"}
[AuditServiceClient#logEvent] <--- END HTTP (102-byte body)
```

---

## Testing: Mock de Microservicios

```java
@SpringBootTest
public class TicketServiceTest {
    
    @MockBean
    private AuditServiceClient auditClient;  // Funciona para FeignClient
    
    @MockBean
    private NotificationClient notificationClient;  // Funciona para RestClient
    
    @Autowired
    private TicketService ticketService;
    
    @Test
    public void testUpdateById_registersAuditEvent() {
        // Mock del servicio remoto
        AuditEvent mockEvent = new AuditEvent(1L, "STATUS_CHANGE", "Ticket", 1L, null, "system", "NEW → IN_PROGRESS", 0L);
        
        when(auditClient.logEvent(any()))
            .thenReturn(mockEvent);
        
        // Ejecutar
        ticketService.updateById(1L, request);
        
        // Verificar que se llamó a AuditService
        verify(auditClient).logEvent(any(AuditRequest.class));
    }
    
    @Test
    public void testGetAuditTrailWhenAuditServiceFails() {
        // Simular fallo — el fallback retorna lista vacía
        when(auditClient.getAuditByTicket(1L))
            .thenReturn(List.of());
        
        List<AuditEvent> result = ticketService.getAuditTrail(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
```

---

*[← Volver a Lección 14](01_objetivo_y_alcance.md)*
