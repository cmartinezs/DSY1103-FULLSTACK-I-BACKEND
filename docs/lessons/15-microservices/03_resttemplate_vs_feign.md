# Lección 13 — RestClient vs RestTemplate vs FeignClient

## Tabla Comparativa

| Aspecto | RestClient | RestTemplate | FeignClient |
|---------|-----------|-------------|-------------|
| **Complejidad** | Baja | Media | Baja |
| **Dependencias** | Spring Web 6.1+ | Spring Web (ya incluido) | Spring Cloud |
| **Configuración** | Mínima | Manual | Automática |
| **Código** | Moderno | Verboso | Declarativo |
| **Casos de Uso** | Estándar moderno | Legacy | Múltiples llamadas |
| **Timeout** | Fácil | Manual | Configuración |
| **Reintentos** | Integrado | Manual | Automático |
| **Fallback** | Manual | Manual | Anotación |
| **Estado** | ✅ Recomendado | ⚠️ Deprecated | ✅ Alternativa |

---

## Ejemplo 1: RestClient (Recomendado - Spring 6.1+)

```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RestClient restClient;
    
    public NotificationService(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("http://localhost:8082")
            .build();
    }
    
    // Notificar a otro servicio
    public void notifyUser(Long userId, String message) {
        NotificationRequest req = new NotificationRequest(userId, message);
        
        try {
            restClient.post()
                .uri("/notifications")
                .body(req)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error enviando notificación", e);
        }
    }
    
    // Obtener notificación
    public NotificationDTO getNotification(Long id) {
        return restClient.get()
            .uri("/notifications/{id}", id)
            .retrieve()
            .body(NotificationDTO.class);
    }
}
```

---

## Ejemplo 2: RestTemplate (Legacy - No recomendado)

```java
@Service
public class NotificationService {
    private final RestTemplate restTemplate;
    
    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    // Notificar a otro servicio
    public void notifyUser(Long userId, String message) {
        String url = "http://localhost:8082/notifications";
        
        NotificationRequest req = new NotificationRequest(userId, message);
        
        try {
            restTemplate.postForObject(url, req, Void.class);
        } catch (Exception e) {
            log.error("Error enviando notificación", e);
        }
    }
}
```

---

## Ejemplo 3: FeignClient (Alternativa - Múltiples Llamadas)

```java
@FeignClient(name = "notification-service", url = "http://localhost:8082")
public interface NotificationClient {
    
    @PostMapping("/notifications")
    void sendNotification(@RequestBody NotificationRequest req);
    
    @GetMapping("/notifications/{id}")
    NotificationDTO getNotification(@PathVariable Long id);
    
    @PutMapping("/notifications/{id}")
    void updateNotification(@PathVariable Long id, @RequestBody NotificationDTO dto);
    
    @DeleteMapping("/notifications/{id}")
    void deleteNotification(@PathVariable Long id);
}
```

**Uso:**
```java
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final NotificationClient notificationClient;
    
    public void processTicket(Ticket ticket) {
        // ... procesar ticket
        
        // Notificar al usuario (limpio y automático)
        NotificationRequest req = new NotificationRequest(
            ticket.getCreatedById(),
            "Tu ticket fue actualizado"
        );
        notificationClient.sendNotification(req);
    }
}
```

---

## Decisión: ¿Cuándo usar cada uno?

```
¿Usas Spring 6.1 o superior?
├─ SÍ → RestClient (estándar moderno, recomendado)
└─ NO → ¿Necesitas proyecto legacy?
         ├─ SÍ → RestTemplate (deprecado pero disponible)
         └─ NO → FeignClient (si prefieres)

¿Múltiples llamadas al mismo servicio?
├─ SÍ → FeignClient (código más limpio)
└─ NO → RestClient (sin dependencias extras)
```

---

## Ventajas de Cada Uno

### RestClient ✅ RECOMENDADO
✅ Estándar moderno (Spring 6.1+)  
✅ API fluida y moderna  
✅ Sin dependencias adicionales  
✅ Máximo control  
✅ Debugging fácil  
✅ Timeouts y reintentos integrados  

### RestTemplate ⚠️ DEPRECATED
❌ Deprecado desde Spring 6.0  
❌ No usar en proyectos nuevos  
✅ Aún funciona en código legacy  
✅ Sin dependencias adicionales  
❌ Código repetitivo  
❌ Manejo manual de errores  

### FeignClient ✅ ALTERNATIVA
✅ Código muy limpio y declarativo  
✅ Automático (serialización, errores)  
✅ Fallbacks integrados  
✅ Ideal para múltiples servicios  
❌ Dependencia adicional (Spring Cloud)  
❌ Menos control  
❌ Mayor complejidad si no está acostumbrado  

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
