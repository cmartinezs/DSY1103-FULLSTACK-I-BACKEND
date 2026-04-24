# Tickets-13: Lección 13 - Historial y Auditoría

## 📋 Descripción

Este proyecto implementa la **Lección 13: Historial y Auditoría** del curso DSY1103 Fullstack I.

Agrega seguimiento automático de cambios de estado de tickets.

---

## 🔄 Cambios desde Lección 12

### 1. Perfiles de Configuración (heredados de Lección 11)
- ✅ application.yml (base)
- ✅ application-h2.yml
- ✅ application-mysql.yml
- ✅ application-supabase.yml
- ✅ spring-dotenv para cargar .env

### 2. Nueva Entidad TicketHistory
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

### 3. TicketHistoryRepository
```java
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
  List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}
```

### 4. Actualización de Ticket
- `@OneToMany(mappedBy = "ticket")` para historial

### 5. Actualización de TicketService
- Agregado TicketHistoryRepository
- Método `registrarHistorial()` para guardar cambios
- Registro automático al crear ticket (estado NEW)
- Registro automático al cambiar estado
- Método `getTicketHistory()` para obtener historial

### 6. Nuevo Endpoint
- GET `/tickets/{id}/history` - Ver historial de cambios

---

## 🧪 Endpoints

### Tickets
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /tickets | Listar tickets |
| POST | /tickets | Crear ticket |
| GET | /tickets/by-id/{id} | Ver ticket |
| PUT | /tickets/by-id/{id} | Actualizar ticket |
| DELETE | /tickets/by-id/{id} | Eliminar ticket |
| GET | /tickets/{id}/history | Ver historial |

### Users
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /users | Listar usuarios |
| POST | /users | Crear usuario |
| GET | /users/by-id/{id} | Ver usuario |
| PUT | /users/by-id/{id} | Actualizar usuario |
| DELETE | /users/by-id/{id} | Eliminar usuario |

---

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] Registro automático al crear ticket
- [x] Registro automático al cambiar estado
- [x] Endpoint de historial funciona
- [x] Historial en orden cronológico descendente

---

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `model/TicketHistory.java` | Entidad historial |
| `model/Ticket.java` | Con relación OneToMany a history |
| `respository/TicketHistoryRepository.java` | Repository de historial |
| `service/TicketService.java` | Actualizado con historial |
| `controller/TicketController.java` | Endpoint history |

---

**Base**: Lección 12 (Relaciones JPA)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2, MySQL, PostgreSQL  
**Estado**: ✅ Completada