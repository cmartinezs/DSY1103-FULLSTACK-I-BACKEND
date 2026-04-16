# Lección 13 — RestTemplate vs FeignClient

## Tabla Comparativa

| Aspecto | RestTemplate | FeignClient |
|---------|-------------|-------------|
| **Complejidad** | Media | Baja |
| **Dependencias** | Spring Web (ya incluido) | Spring Cloud |
| **Configuración** | Manual | Automática |
| **Código** | Verboso | Declarativo |
| **Casos de Uso** | Llamadas ocasionales | Múltiples llamadas |
| **Timeout** | Manual | Configuración |
| **Reintentos** | Manual | Automático |
| **Fallback** | Manual | Anotación |

---

## Ejemplo 1: RestTemplate (Llamada Simple)

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

## Ejemplo 2: FeignClient (Múltiples Llamadas)

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
¿Es tu única llamada o ocasional?
├─ SÍ → RestTemplate (sin dependencia extra)
└─ NO → ¿Llamadas frecuentes al mismo servicio?
         ├─ SÍ → FeignClient (más limpio)
         └─ NO → RestTemplate está bien
```

---

## Ventajas de Cada Uno

### RestTemplate
✅ Sin dependencias adicionales  
✅ Máximo control  
✅ Debugging fácil  
❌ Código repetitivo  
❌ Manejo manual de errores  

### FeignClient
✅ Código muy limpio  
✅ Automático (serialización, errores)  
✅ Declarativo (como JPA)  
✅ Fallbacks integrados  
❌ Dependencia adicional  
❌ Menos control  

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
