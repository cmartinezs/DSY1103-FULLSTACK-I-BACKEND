# Tickets-13: Lección 13 - Historial y Auditoría

## 📋 Descripción

Este proyecto implementa la **Lección 13: Historial y Auditoría** del curso DSY1103 Fullstack I.

Agrega seguimiento automático de cambios de estado de tickets.

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

## 🔄 Cambios desde Lección 12

### 1. Nueva Entidad TicketHistory
```java
@Entity
@Table(name = "ticket_history")
public class TicketHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "ticket_id")
  private Ticket ticket;
  
  private String previousStatus;
  private String newStatus;
  private LocalDateTime changedAt;
  private String comment;
}
```

### 2. TicketHistoryRepository
```java
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
  List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}
```

### 3. Actualización de TicketService
- Agregado TicketHistoryRepository
- Método `registrarHistorial()` para guardar cambios
- Registro automático al crear ticket (estado NEW)
- Registro automático al cambiar estado
- Método `getTicketHistory()` para obtener historial

### 4. Nuevo Endpoint
- GET `/tickets/{id}/history` - Ver historial de cambios

---

## 📊 Requisitos del Caso Extendido por Lección

| Lección | Requisitos del Caso Extendido |
|---------|------------------------------|
| 10 | ✅ User entity con roles, Ticket con User relaciones, seed de datos |
| 11 | ✅ Perfiles con diferentes configs de BD para usuarios (H2, MySQL, Supabase) |
| 12 | ✅ Category (One-to-Many), Tag (Many-to-Many), CRUD completo |
| 13 | ✅ TicketHistory, registro automático, endpoint de historial |
| 14 | Flyway migrations con Foreign Keys a users |
| 15 | Notificaciones con User |
| 16 | Security con 3 roles (USER/AGENT/ADMIN) |
| 17 | Logging de operaciones de usuarios |
| 18 | Excepciones para casos de usuarios |

---

## 🧪 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /tickets | Listar tickets |
| GET | /tickets/{id}/history | Ver historial de ticket |
| POST | /tickets | Crear ticket (registra NEW en historial) |
| PUT | /tickets/by-id/{id} | Actualizar ticket (registra cambio de estado) |

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] Registro automático al crear ticket
- [x] Registro automático al cambiar estado
- [x] Endpoint de historial funciona
- [x] Historial en orden cronológico descendente

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `model/TicketHistory.java` | Entidad historial |
| `respository/TicketHistoryRepository.java` | Repository de historial |
| `service/TicketService.java` | Actualizado con historial |
| `controller/TicketController.java` | Actualizado con endpoint history |

---

**Base**: Lección 12 (Relaciones JPA)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada