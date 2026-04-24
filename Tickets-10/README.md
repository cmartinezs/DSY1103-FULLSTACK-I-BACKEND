# Tickets-10: Lección 10 - Introducción a JPA

## 📋 Descripción

Este proyecto implementa la **Lección 10: Introducción a JPA** del curso DSY1103 Fullstack I.

Migración del repositorio en-memoria basado en HashMap a **Spring Data JPA** con Hibernate.

---

## 🔄 Cambios desde Lección 09

### 1. Dependencias (pom.xml)
- ✅ Agregadas: `spring-boot-starter-data-jpa`, `h2`

### 2. Modelo (Ticket.java)
- ✅ Convertida a entidad JPA con `@Entity`
- ✅ `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` para auto-increment
- ✅ `@Table(name = "tickets")` para mapeo
- ✅ Campos: id, title, description, status, createdAt, estimatedResolutionDate, effectiveResolutionDate

### 3. Repositorio (TicketRepository.java)
- ✅ Extiende `JpaRepository<Ticket, Long>`
- ✅ Métodos de query: `findAllOrderByCreatedAt()`, `findAllByStatusIgnoreCase()`
- ✅ Validación: `existsByTitleIgnoreCase()`

### 4. Servicio (TicketService.java)
- ✅ CRUD básico usando JPA Repository
- ✅ Validación de título único

### 5. DTOs
- **TicketRequest**: title, description, status, effectiveResolutionDate
- **TicketResult**: id, title, description, status, createdAt, estimatedResolutionDate, effectiveResolutionDate

### 6. Configuración (application.yml)
- ✅ H2 en memoria (`jdbc:h2:mem:tickets_db`)
- ✅ JPA/Hibernate: `ddl-auto: create-drop`

### 7. DataInitializer
- ✅ Tickets iniciales de ejemplo

---

## 📊 Modelo de Datos

| Campo | Tipo | Descripción |
|-------|------|------------|
| id | Long | PK auto-incremental |
| title | String | Título del ticket |
| description | String | Descripción |
| status | String | NEW, IN_PROGRESS, RESOLVED, CLOSED |
| createdAt | LocalDateTime | Fecha de creación |
| estimatedResolutionDate | LocalDate | Fecha estimada de resolución |
| effectiveResolutionDate | LocalDateTime | Fecha real de resolución |

---

## 🧪 Endpoints

| Método | Endpoint | Descripción |
|--------|---------|-------------|
| GET | /tickets | Listar todos los tickets |
| GET | /tickets/by-id/{id} | Ver ticket por ID |
| POST | /tickets | Crear nuevo ticket |
| PUT | /tickets/by-id/{id} | Actualizar ticket |
| DELETE | /tickets/by-id/{id} | Eliminar ticket |

---

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] CRUD con JPA funciona
- [x] Datos iniciales se cargan al iniciar

---

## 📝 Archivos

| Archivo | Descripción |
|---------|-------------|
| `model/Ticket.java` | Entidad JPA |
| `respository/TicketRepository.java` | Repository JPA |
| `service/TicketService.java` | Lógica de negocio |
| `dto/TicketRequest.java` | DTO entrada |
| `dto/TicketResult.java` | DTO salida |
| `config/DataInitializer.java` | Datos iniciales |

---

**Base**: Lección 09 (Repositorio Customizado)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2  
**Estado**: ✅ Completada