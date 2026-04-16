# Caso Extendido: Sistema de Tickets con Gestión de Usuarios

## 📋 Resumen Ejecutivo

Extender el sistema de tickets desde la Lección 10 en adelante para incluir gestión completa de usuarios con roles y capacidad de asignar tickets a usuarios específicos.

---

## 🎯 Caso de Uso Extendido

```
USER (reporta) → ticket → AGENT (trabaja) → USER (verifica) → AGENT (cierra)
                    ↓
              ADMIN (supervisa)
```

### Flujo Completo

1. **USER** reporta un problema creando un ticket
2. **ADMIN** revisa y asigna el ticket a un **AGENT**
3. **AGENT** trabaja en el ticket y actualiza su estado
4. **USER** puede ver el estado del ticket
5. **ADMIN** supervisa y gestiona categorías/tags

---

## 👥 Definición de Roles

| Rol     | Permisos                                                                 |
|---------|--------------------------------------------------------------------------|
| USER    | Crear tickets, ver tickets propios, ver estado                         |
| AGENT   | Ver tickets asignados, actualizar estado, resolver tickets            |
| ADMIN   | Gestionar usuarios, categorías, tags, asignar tickets, ver historial   |

---

## 📊 Modelo de Datos Actual vs Necesario

### User (actual)
```java
@Entity
public class User {
    private Long id;
    private String name;
    private String email;
    // ❌ Falta role
}
```

### User (necesario)
```java
@Entity
public class User {
    private Long id;
    private String name;
    private String email;
    private String role;  // ADMIN, AGENT, USER
    private boolean active;
}
```

### Ticket (ya existe)
```java
@Entity
public class Ticket {
    // ...
    private User createdBy;    // ✅ Ya existe
    private User assignedTo;  // ✅ Ya existe, necesita endpoint
}
```

---

## 📝 Cambios Requeridos por Lección

### Lección 10: JPA + User con Roles

**Cambios:**
- Agregar campo `role` a User entity
- Agregar campo `active` a User entity
- Actualizar TicketRequest para incluir `assignedToId`
- Crear endpoint de asignación básica

**Archivos a modificar:**
- `model/User.java` - agregar role, active
- `dto/TicketRequest.java` - agregar assignedToId
- `service/TicketService.java` - lógica de asignación
- `controller/TicketController.java` - endpoint PUT /assign

---

### Lección 11: Perfiles + seed de usuarios

**Cambios:**
- Crear script de usuarios iniciales en DataInitializer:
  - admin/admin123 (ADMIN)
  - agent1/agent123 (AGENT)
  - user1/user123 (USER)
- Documentar roles en .env.example

---

### Lección 12: Relaciones JPA

**Sin cambios adicionales** - las relaciones ya existen (createdBy, assignedTo)

---

### Lección 13: Historial

**Sin cambios adicionales** - el historial ya registra cambios

---

### Lección 14: Flyway

**Cambios:**
- Actualizar V1__Initial_schema.sql:
  ```sql
  CREATE TABLE users (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      name VARCHAR(100) NOT NULL,
      email VARCHAR(150) NOT NULL UNIQUE,
      role VARCHAR(20) NOT NULL DEFAULT 'USER',
      active BOOLEAN DEFAULT TRUE,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );
  
  ALTER TABLE tickets 
  ADD COLUMN created_by_id BIGINT,
  ADD COLUMN assigned_to_id BIGINT,
  ADD FOREIGN KEY (created_by_id) REFERENCES users(id),
  ADD FOREIGN KEY (assigned_to_id) REFERENCES users(id);
  ```

---

### Lección 15: Microservicios (sin cambios)

---

### Lección 16: Security

**Cambios:**
- SecurityConfig con 3 roles:
  ```java
  .requestMatchers(HttpMethod.POST, "/tickets").hasAnyRole("USER", "AGENT", "ADMIN")
  .requestMatchers(HttpMethod.PUT, "/tickets/**").hasAnyRole("AGENT", "ADMIN")
  .requestMatchers(HttpMethod.DELETE, "/tickets/**").hasRole("ADMIN")
  .requestMatchers("/users/**").hasRole("ADMIN")
  .requestMatchers("/categories/**").hasRole("ADMIN")
  .requestMatchers("/tags/**").hasRole("ADMIN")
  ```
- Agregar endpoint de asignación con autenticación

---

### Lección 17: Logging

**Agregar logs de:**
- Asignación de ticket
- Cambio de estado por agente
- Intentos de acceso no autorizado

---

### Lección 18: Exception Handling

**Agregar excepciones:**
- `UserNotFoundException`
- `TicketAssignmentException`
- `UnauthorizedOperationException`

---

## 🔄 Endpoints Nuevos/Requeridos

| Método | Endpoint                        | Rol        | Descripción                    |
|--------|----------------------------------|------------|--------------------------------|
| GET    | /users                           | ADMIN      | Listar usuarios               |
| POST   | /users                           | ADMIN      | Crear usuario                 |
| GET    | /users/by-id/{id}                | ADMIN      | Ver usuario                   |
| PUT    | /users/by-id/{id}                | ADMIN      | Actualizar usuario           |
| DELETE | /users/by-id/{id}                | ADMIN      | Desactivar usuario            |
| GET    | /tickets/assigned-to-me          | AGENT      | Ver tickets asignados        |
| PUT    | /tickets/{id}/assign/{userId}    | ADMIN      | Asignar ticket a usuario     |
| PUT    | /tickets/{id}/unassign          | ADMIN      | Desasignar ticket            |

---

## 📋 Plan de Implementación

### Opción A: Retroalimentar desde Lección 10

1. Modificar modelo de User desde Lección 10
2. Regenerar todos los proyectos desde 10
3. Requiere más trabajo pero resultado más limpio

### Opción B: Agregar desde Lección 16

1. Mantener proyectos actuales como están
2. Agregar cambios solo desde Lección 16+
3. Menos trabajo, pero modelo incompleto en lecciones anteriores

### Recomendación: Opción A

Crear los proyectos actualizados desde Lección 10 para coherencia del modelo.

---

## ✅ Checklist de Implementación

- [ ] Actualizar User entity con role
- [ ] Crear migración Flyway V1 con users
- [ ] Crear DataInitializer con usuarios de diferentes roles
- [ ] Agregar endpoint PUT /tickets/{id}/assign/{userId}
- [ ] Agregar endpoint GET /tickets/assigned-to-me
- [ ] Actualizar SecurityConfig con 3 roles
- [ ] Agregar logging de asignaciones
- [ ] Agregar excepciones para casos de error

---

## 🚀 Próximos Pasos

1. Confirmar estrategia de implementación (Opción A o B)
2. Actualizar modelo de User
3. Regenerar proyectos desde Lección 10 si es Opción A
4. Implementar endpoints de asignación
5. Actualizar documentación de lecciones

---

**Fecha**: Abril 2026  
**Estado**: Planificado