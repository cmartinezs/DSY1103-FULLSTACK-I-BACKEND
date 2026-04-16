# Lección 13 — Actividad Individual

## 🎯 Objetivo

Implementar comunicación HTTP entre dos microservicios usando **FeignClient o RestTemplate**. Tu aplicación Tickets debe llamar a un servicio externo (real o mock).

---

## 📋 Requisitos Mínimos

### 1. Elegir Cliente HTTP

- [ ] Decidir entre RestTemplate o FeignClient
- [ ] Justificar tu elección

### 2. Implementar Cliente

**Si usas RestTemplate:**
```java
- [ ] Crear bean RestTemplate con configuración
- [ ] Implementar método que hace llamada HTTP
- [ ] Manejar excepciones (try/catch)
```

**Si usas FeignClient:**
```java
- [ ] Crear interface anotada con @FeignClient
- [ ] Agregar @EnableFeignClients en app principal
- [ ] Implementar fallback
```

### 3. Servicio Externo

Debes comunicarte con un servicio en **http://localhost:8081** que tenga al menos:
```
GET /users/{id}        → Retorna UserDTO
GET /users/email/{email} → Retorna UserDTO
```

**Opciones:**
- Crear segunda app Spring Boot (Users Service)
- Usar servicio mock (https://jsonplaceholder.typicode.com/)
- Usar test fixture

### 4. Integración en Tu App

- [ ] Llama al servicio externo desde `TicketService`
- [ ] Usa la información para enriquecer respuesta
- [ ] Ejemplo: GET `/tickets/1` devuelve:
  ```json
  {
    "id": 1,
    "title": "Bug crítico",
    "createdBy": {
      "id": 5,
      "name": "Juan",
      "email": "juan@example.com"
    }
  }
  ```

### 5. Manejo de Errores

- [ ] Implementar timeout (5s conexión, 10s lectura)
- [ ] Implementar fallback (qué hacer si servicio cae)
- [ ] Logging de requests/responses
- [ ] Tests con mocks

---

## 🚀 Pasos

1. **Elegir enfoque**
   - [ ] RestTemplate (simple) o FeignClient (elegante)

2. **Implementar cliente**
   ```java
   // RestTemplate
   UserDTO user = restTemplate.getForObject(url, UserDTO.class, id);
   
   // FeignClient
   UserDTO user = userClient.getUserById(id);
   ```

3. **Configurar timeouts**
   ```yaml
   spring:
     cloud:
       openfeign:
         client:
           config:
             default:
               read-timeout: 10000
   ```

4. **Implementar fallback**
   ```java
   return UserDTO.builder()
       .id(id)
       .name("Usuario Desconocido")
       .build();
   ```

5. **Integrar en servicio**
   ```java
   public TicketDetail getTicketWithUser(Long id) {
       Ticket ticket = repository.findById(id);
       UserDTO user = getUser(ticket.getCreatedById());
       return new TicketDetail(ticket, user);
   }
   ```

6. **Probar**
   ```bash
   curl http://localhost:8080/ticket-app/tickets/1
   # Debe incluir información del usuario
   ```

7. **Commit**
   ```bash
   git add src/main/java/...
   git commit -m "feat: comunicación con Users Service via FeignClient"
   ```

---

## ✅ Validación

Debes poder responder:

- [ ] "¿Por qué elegí RestTemplate/FeignClient?"
- [ ] "¿Qué es un microservicio?"
- [ ] "¿Qué pasa si el servicio remoto cae?"
- [ ] "¿Cómo configuro timeouts?"
- [ ] "¿Qué muestra el log de la llamada HTTP?"

---

## 📦 Entrega

Sube tu código con:
- ✅ Cliente HTTP (RestTemplate o FeignClient)
- ✅ Fallback implementado
- ✅ Configuración de timeouts
- ✅ Integración en `TicketService`
- ✅ Tests con mocks
- ✅ Logs detallados

---

## 🔥 Desafío Extra (Opcional)

- Implementar **circuit breaker** con Resilience4j
- Crear **segunda app Spring Boot** (Users Service)
- Implementar **reintentos automáticos**
- Agregar **metrics** de llamadas HTTP

---

*[← Volver a Lección 13](01_objetivo_y_alcance.md)*
