# Tickets-10: Lección 10 - Introducción a JPA

## 📋 Descripción

Este proyecto implementa la **Lección 10: Introducción a JPA** del curso DSY1103 Fullstack I.

Migración del repositorio en-memoria basado en HashMap a **Spring Data JPA** con Hibernate.

## 🔄 Cambios desde Lección 09

### 1. Dependencias (pom.xml)
- ✅ Agregadas: `spring-boot-starter-data-jpa`, `h2`
- Remover dependencias manuales de repositorio in-memory

### 2. Modelo (Ticket.java)
- ✅ Convertida a entidad JPA con `@Entity`
- ✅ `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` para auto-increment
- ✅ `@Table(name = "tickets")` para mapeo

```java
@Entity
@Table(name = "tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  // ... resto de campos
}
```

### 3. Repositorio (TicketRepository.java)
- ✅ Convertido de clase a interface
- ✅ Extiende `JpaRepository<Ticket, Long>`
- ✅ Métodos de query personalizados con `@Query`

```java
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
  boolean existsByTitleIgnoreCase(String title);
  
  @Query("SELECT t FROM Ticket t WHERE UPPER(t.status) = UPPER(:status) ORDER BY t.createdAt")
  List<Ticket> findAllByStatusIgnoreCase(@Param("status") String status);
  
  @Query("SELECT t FROM Ticket t ORDER BY t.createdAt")
  List<Ticket> findAllOrderByCreatedAt();
}
```

### 4. Servicio (TicketService.java)
- ✅ Actualizado para usar métodos de JpaRepository
- ✅ Eliminados métodos no estándar (getAll() → findAllOrderByCreatedAt())
- ✅ Corregido deleteById() para retornar boolean

### 5. Configuración (application.yml)
- ✅ Agregada configuración de H2 (in-memory)
- ✅ JPA/Hibernate settings
- ✅ `ddl-auto: create-drop` para desarrollo

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:ticketsdb
    driverClassName: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

### 6. Inicializador de Datos (DataInitializer.java)
- ✅ Nuevo componente con `@Component`
- ✅ Implementa `CommandLineRunner`
- ✅ Carga datos iniciales al iniciar la aplicación
- ✅ Reemplaza constructor de TicketRepository

```java
@Component
public class DataInitializer implements CommandLineRunner {
  @Override
  public void run(String... args) throws Exception {
    if (ticketRepository.count() == 0) {
      // Crear tickets iniciales
    }
  }
}
```

## 🧪 Testing

```bash
# Compilar
./mvnw clean compile

# Tests
./mvnw test

# Ejecutar
./mvnw spring-boot:run
```

## ✅ Validación

- [x] Proyecto compila sin errores
- [x] Todos los tests pasan
- [x] Endpoints CRUD funcionan con JPA
- [x] Datos iniciales se cargan al iniciar
- [x] H2 en-memory funciona correctamente

## 📚 Referencias

- Lección: `docs/lessons/10-jpa-intro/`
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Hibernate: https://hibernate.org/

## 📦 Estructura

```
Tickets-10/
├── src/main/java/cl/duoc/fullstack/tickets/
│   ├── config/
│   │   └── DataInitializer.java        (NUEVO)
│   ├── controller/
│   │   └── TicketController.java
│   ├── dto/
│   │   └── TicketRequest.java
│   ├── model/
│   │   ├── Ticket.java                 (MODIFICADO)
│   │   └── ErrorResponse.java
│   ├── respository/
│   │   └── TicketRepository.java       (REFACTORIZADO)
│   ├── service/
│   │   └── TicketService.java          (MODIFICADO)
│   └── TicketsApplication.java
├── src/main/resources/
│   └── application.yml                 (MODIFICADO)
├── pom.xml                             (MODIFICADO)
└── README.md
```

---

**Base**: Lección 09 (Repositorio Customizado)  
**Stack**: Spring Boot 4.0.5, Java 21, JPA/Hibernate, H2  
**Estado**: ✅ Completada y testeada

