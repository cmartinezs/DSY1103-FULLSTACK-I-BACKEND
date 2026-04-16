# Lección 13 — Debugging y Troubleshooting

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
# Verificar que el servicio está corriendo
lsof -i :8081  # Ver qué está en puerto 8081

# Probar conexión manualmente
curl http://localhost:8081/users/1
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
          users-service:
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
Error creating bean with name 'userServiceClient'
No bean of type 'UserServiceClient' found
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
curl -v http://localhost:8081/users/1
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
[UserServiceClient#getUserById] ---> GET http://localhost:8081/users/1 HTTP/1.1
[UserServiceClient#getUserById] Accept: application/json
[UserServiceClient#getUserById] ---> END HTTP (0-byte body)
[UserServiceClient#getUserById] <--- HTTP/1.1 200 OK (45ms)
[UserServiceClient#getUserById] Content-Type: application/json
[UserServiceClient#getUserById] {"id":1,"name":"John","email":"john@example.com"}
[UserServiceClient#getUserById] <--- END HTTP (65-byte body)
```

---

## Testing: Mock de Microservicios

```java
@SpringBootTest
public class TicketServiceTest {
    
    @MockBean
    private UserServiceClient userClient;  // Funciona para RestClient o Feign
    
    @Autowired
    private TicketService ticketService;
    
    @Test
    public void testGetTicketDetail() {
        // Mock del servicio remoto
        UserDTO mockUser = UserDTO.builder()
            .id(1L)
            .name("Test User")
            .build();
        
        when(userClient.getUserById(1L))
            .thenReturn(mockUser);
        
        // Ejecutar
        TicketDetailDTO result = ticketService.getTicketDetail(1L);
        
        // Verificar
        assertEquals("Test User", result.getCreator().getName());
        verify(userClient).getUserById(1L);
    }
    
    @Test
    public void testGetTicketDetailWhenUserServiceFails() {
        // Simular fallo
        when(userClient.getUserById(1L))
            .thenThrow(new FeignException.ServiceUnavailable(
                "Server is down", null, null, null
            ));
        
        // Debe devolver resultado con fallback
        TicketDetailDTO result = ticketService.getTicketDetail(1L);
        assertNotNull(result);
    }
}
```

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
