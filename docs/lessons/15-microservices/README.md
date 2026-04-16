# Lección 13 — Comunicación entre Microservicios

**Aprende a comunicar dos microservicios independientes usando RestClient, FeignClient o RestTemplate. Implementa resilencia, timeouts y fallbacks.**

---

## 📚 Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. Guión Paso a Paso** ⭐ | 20 min | Instrucciones prácticas |
| **03. RestClient vs RestTemplate vs FeignClient** | 10 min | Comparación y decisión |
| **04. Ejemplos Prácticos** | 15 min | Código completo |
| **05. Manejo de Errores** | 10 min | Timeouts, fallbacks, resilencia |
| **06. Debugging** | 10 min | Logs y troubleshooting |
| **07. Checklist** | 5 min | Verificación |
| **08. Actividad Individual** | - | Tu tarea |

---

## 🎯 Quick Start (10 min)

### Con RestClient (Recomendado - Spring 6.1+)

```java
// 1. Inyectar builder
@Service
public class TicketService {
    private final RestClient restClient;
    
    public TicketService(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("http://localhost:8081")
            .build();
    }
    
    // 2. Usar
    public UserDTO getUser(Long id) {
        return restClient.get()
            .uri("/users/{id}", id)
            .retrieve()
            .body(UserDTO.class);
    }
}
```

### Con FeignClient (Alternativa)

```java
// 1. Habilitar
@SpringBootApplication
@EnableFeignClients
public class TicketsApplication {}

// 2. Crear cliente
@FeignClient(name = "users", url = "http://localhost:8081")
public interface UsersClient {
    @GetMapping("/users/{id}")
    UserDTO getUser(@PathVariable Long id);
}

// 3. Inyectar y usar
@Service
@RequiredArgsConstructor
public class TicketService {
    private final UsersClient usersClient;
    
    public TicketDetail getTicket(Long id) {
        UserDTO user = usersClient.getUser(1L);
        return new TicketDetail(ticket, user);
    }
}
```

### Con RestTemplate (Legacy - No recomendado)

```java
// 1. Registrar
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// 2. Usar
@Service
public class TicketService {
    private final RestTemplate rest;
    
    public void getTicket(Long id) {
        UserDTO user = rest.getForObject(
            "http://localhost:8081/users/{id}",
            UserDTO.class,
            1L
        );
    }
}
```

---

## 🔑 Conceptos Clave

### Microservicios

Arquitectura donde una aplicación se divide en múltiples servicios independientes:

```
Antes (Monolito):
┌──────────────────┐
│ Tickets Service  │
├────────────────  │
│ Users code       │
│ Tickets code     │
│ Notifications    │
└──────────────────┘

Después (Microservicios):
┌─────────────┐  ┌─────────────┐  ┌──────────────┐
│   Tickets   │→→│    Users    │→→│Notifications│
│  (8080)     │  │   (8081)    │  │   (8082)     │
└─────────────┘  └─────────────┘  └──────────────┘
```

### RestClient vs RestTemplate vs FeignClient

| Aspecto | RestClient | RestTemplate | FeignClient |
|---------|-----------|-------------|-------------|
| **Líneas de código** | Pocas | Muchas | Pocas |
| **Aprendizaje** | Fácil | Fácil | Intermedio |
| **Automático** | Parcial | No | Sí |
| **Ideal para** | Estándar moderno | Legacy | Múltiples llamadas |
| **Estado** | ✅ Recomendado | ⚠️ Deprecated | ✅ Alternativa |

---

## 📂 Estructura

```
src/main/java/
├── clients/
│   ├── NotificationClient.java          (RestClient o Feign)
│   └── NotificationClientFallback.java  (optional)
├── services/
│   └── TicketService.java               (usa client)
└── TicketsApplication.java              (@EnableFeignClients si aplica)
```

---

## ✅ Checklist

- [ ] Dependencia de cliente HTTP elegida (RestClient / RestTemplate / FeignClient)
- [ ] Cliente HTTP creado
- [ ] Fallback para errores (si aplica)
- [ ] Timeouts configurados
- [ ] Integrado en TicketService
- [ ] API responde con datos enriquecidos
- [ ] Tests con mocks

---

## 🚀 Sigue el Guión

Comienza con **[02. Guión Paso a Paso](02_guion_paso_a_paso.md)** para instrucciones detalladas.

---

*Lección 13 de 13 - [← Volver a Lecciones](../)*
