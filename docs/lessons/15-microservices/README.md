# Lección 13 — Comunicación entre Microservicios

**Aprende a comunicar dos microservicios independientes usando FeignClient y RestTemplate. Implementa resilencia, timeouts y fallbacks.**

---

## 📚 Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. Guión Paso a Paso** ⭐ | 20 min | Instrucciones prácticas |
| **03. RestTemplate vs FeignClient** | 10 min | Comparación y decisión |
| **04. Ejemplos Prácticos** | 15 min | Código completo |
| **05. Manejo de Errores** | 10 min | Timeouts, fallbacks, resilencia |
| **06. Debugging** | 10 min | Logs y troubleshooting |
| **07. Checklist** | 5 min | Verificación |
| **08. Actividad Individual** | - | Tu tarea |

---

## 🎯 Quick Start (10 min)

### Con FeignClient (Recomendado)

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

### Con RestTemplate (Simple)

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

### RestTemplate vs FeignClient

| Aspecto | RestTemplate | FeignClient |
|---------|-------------|-------------|
| **Líneas de código** | Muchas | Pocas |
| **Aprendizaje** | Fácil | Intermedio |
| **Automático** | No | Sí |
| **Ideal para** | 1-2 llamadas | Múltiples llamadas |

---

## 📂 Estructura

```
src/main/java/
├── clients/
│   ├── UserServiceClient.java      (Feign)
│   └── UserServiceClientFallback.java
├── services/
│   └── TicketService.java          (usa client)
└── TicketsApplication.java         (@EnableFeignClients)
```

---

## ✅ Checklist

- [ ] Dependencia FeignClient o RestTemplate
- [ ] Cliente HTTP creado
- [ ] Fallback para errores
- [ ] Timeouts configurados
- [ ] Integrado en TicketService
- [ ] API responde con datos enriquecidos
- [ ] Tests con mocks

---

## 🚀 Sigue el Guión

Comienza con **[02. Guión Paso a Paso](02_guion_paso_a_paso.md)** para instrucciones detalladas.

---

*Lección 13 de 13 - [← Volver a Lecciones](../)*
