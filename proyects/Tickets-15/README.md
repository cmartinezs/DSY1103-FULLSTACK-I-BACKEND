# Tickets-15: Lección 15 - Comunicación entre Microservicios

## 📋 Descripción

Este proyecto implementa la **Lección 15: Comunicación entre Microservicios** del curso DSY1103 Fullstack I.

Implementa comunicación HTTP con servicio externo de notificaciones usando OpenFeign y RestClient.

## 🎯 Caso de Uso Extendido (Sistema de Tickets con Gestión de Usuarios)

### Roles definidos
| Rol     | Descripción              |
|---------|--------------------------|
| USER    | Crea tickets, ve estado  |
| AGENT   | Recibe tickets asignados |
| ADMIN   | Supervisa y gestiona     |

### Modelo de datos
- **User**: id, name, email, role (USER/AGENT/ADMIN), active
- **Ticket**: relaciones con User, Category, Tags
- **Category**: One-to-Many con Ticket
- **Tag**: Many-to-Many con Ticket
- **TicketHistory**: historial de cambios de estado

---

## 🔄 Cambios desde Lección 14

### 1. Dependencia Agregada
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>5.0.1</version>
</dependency>
```

### 2. Notificación Cliente (FeignClient)
```java
@FeignClient(
    name = "notificationService",
    url = "${notification.service.url:http://localhost:8081}",
    fallback = NotificationClientFallback.class
)
public interface NotificationClient {
    @PostMapping("/api/notifications/send")
    Map<String, Object> sendNotification(@RequestBody Map<String, String> notification);
}
```

### 3. Fallback para Fallos
```java
@Component
public class NotificationClientFallback implements NotificationClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public Map<String, Object> sendNotification(Map<String, String> notification) {
        logger.warn("Notification service unavailable. Notification not sent: {}", notification.get("title"));
        return Collections.singletonMap("status", "fallback");
    }
}
```

### 4. Configuración de Timeouts
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

### 5. Integración en TicketService
- Notificaciones al crear ticket
- Notificaciones al actualizar ticket

### 6. Habilitar Feign
```java
@SpringBootApplication
@EnableFeignClients
public class TicketsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }
}
```

---

## 📊 Requisitos del Caso Extendido por Lección

| Lección | Requisitos del Caso Extendido |
|---------|------------------------------|
| 10 | ✅ User entity con roles, Ticket con User relaciones, seed de datos |
| 11 | ✅ Perfiles con diferentes configs de BD para usuarios (H2, MySQL, Supabase) |
| 12 | ✅ Category (One-to-Many), Tag (Many-to-Many), CRUD completo |
| 13 | ✅ TicketHistory, registro automático, endpoint de historial |
| 14 | ✅ Flyway migrations con Foreign Keys a users |
| 15 | ✅ FeignClient + RestClient, notificaciones en crear/actualizar |
| 16 | Security con 3 roles (USER/AGENT/ADMIN) |
| 17 | Logging de operaciones de usuarios |
| 18 | Excepciones para casos de usuarios |

---

## 🧪 Uso

```bash
# Desarrollo (H2)
./mvnw spring-boot:run

# MySQL
./mvnw spring-boot:run -Dspring.profiles.active=mysql

# Supabase
./mvnw spring-boot:run -Dspring.profiles.active=supabase
```

### Probar notificaciones

El **NotificationService** debe estar corriendo en `http://localhost:8081`:

```bash
cd NotificationService
./mvnw spring-boot:run
```

Desde outra terminal, ejecutar Tickets:

```bash
cd Tickets-15
./mvnw spring-boot:run
```

Las notificaciones se envían automáticamente al crear o actualizar tickets.

---

## 📦 NotificationService (Microservicio Externo)

El proyecto `NotificationService/` es un microservicio independiente que recibe notificaciones del cliente.

**Puerto**: 8081  
**Endpoint**: `POST /api/notifications/send`

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] FeignClient configurado con fallback
- [x] Timeouts configurados (5 segundos)
- [x] Integración en TicketService funciona

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `pom.xml` | Dependencia OpenFeign |
| `client/NotificationClient.java` | FeignClient para notificaciones |
| `client/NotificationClientFallback.java` | Fallback cuando servicio no está disponible |
| `client/NotificationRestClient.java` | RestClient alternativo |
| `TicketsApplication.java` | @EnableFeignClients |
| `service/TicketService.java` | Notificaciones al crear/actualizar |
| `application.yml` | Configuración de Feign/RestClient |

**NotificationService/** (microservicio externo):
| Archivo | Descripción |
|---------|-------------|
| `pom.xml` | Dependencias Spring Boot |
| `NotificationsApplication.java` | Aplicación principal |
| `controller/NotificationController.java` | Endpoint de notificaciones |

---

**Base**: Lección 14 (Flyway)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, OpenFeign, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada