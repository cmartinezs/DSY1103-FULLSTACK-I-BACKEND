# Lección 14 — Actividad Individual

## 🎯 Objetivo

Implementar comunicación HTTP entre tu aplicación Tickets y **dos servicios externos nuevos** que no se vieron en la lección. Debes aplicar de forma independiente los patrones aprendidos: RestClient, FeignClient o RestTemplate.

> ⚠️ Los servicios de esta actividad (**SearchService** y **SLAService**) aún no están implementados. Se desarrollarán en lecciones posteriores. Para esta actividad debes implementar los **clientes** en Tickets y verificar que el código compila correctamente. Puedes mockear las llamadas en tus tests.

---

## 📋 Servicios a Integrar

### SearchService (Puerto 8084)
Indexa tickets para permitir búsqueda full-text. Cada vez que se crea o actualiza un ticket, debe enviarse al índice.

```
POST /api/search/index
Body:  { ticketId: Long, title: String, description: String, status: String }
Response: 204 No Content
```

**Cliente sugerido:** RestClient (operación única, sin respuesta que procesar).

### SLAService (Puerto 8085)
Controla los tiempos de resolución (Service Level Agreement). Cuando se crea un ticket inicia el contador; también permite consultar el estado del SLA de un ticket.

```
POST /api/sla/start
Body:  { ticketId: Long, priority: String }
Response: { id: Long, ticketId: Long, priority: String, deadline: String, status: String }

GET /api/sla/{ticketId}
Response: { id: Long, ticketId: Long, priority: String, deadline: String, status: String }
```

**Cliente sugerido:** FeignClient (dos endpoints distintos → estilo declarativo más limpio).

---

## 📋 Requisitos Mínimos

### 1. Elegir Cliente HTTP

Para cada servicio decide cuál cliente usar y justifica:

| Servicio | Mi cliente elegido | Justificación |
|----------|--------------------|---------------|
| SearchService | | |
| SLAService | | |

**Guía de decisión:**
- **RestClient**: operación puntual, sin dependencias extras, Spring 6.1+
- **FeignClient**: múltiples endpoints al mismo servicio, estilo declarativo
- **RestTemplate**: solo si mantienes código legacy (Spring < 6.0)

### 2. Implementar DTOs

```java
// SearchIndexRequest.java
public record SearchIndexRequest(Long ticketId, String title, String description, String status) {}

// SlaStartRequest.java
public record SlaStartRequest(Long ticketId, String priority) {}

// SlaEvent.java
public record SlaEvent(Long id, Long ticketId, String priority, String deadline, String status) {}
```

### 3. Implementar Clientes

Crea los clientes en el paquete `clients/`:

**SearchClient** (RestClient):
- [ ] Constructor con `RestClient.Builder`, `baseUrl("http://localhost:8084")`
- [ ] Método `index(Long ticketId, String title, String description, String status)`
- [ ] Manejo de excepción (fire-and-forget, solo log si falla)

**SlaServiceClient** (FeignClient):
- [ ] Interface anotada con `@FeignClient(name = "sla-service", url = "http://localhost:8085")`
- [ ] Método `startSla(@RequestBody SlaStartRequest request)` → `SlaEvent`
- [ ] Método `getSla(@PathVariable Long ticketId)` → `SlaEvent`
- [ ] Clase fallback con respuestas vacías/nulas
- [ ] `@EnableFeignClients` en `TicketsApplication`

### 4. Integración en TicketService

- [ ] En `create()`: llama a `searchClient.index(...)` después de guardar el ticket
- [ ] En `create()`: llama a `slaClient.startSla(...)` con el `ticketId` y prioridad del ticket
- [ ] Nuevo endpoint `GET /tickets/{id}/sla` en el controller → delega en `slaClient.getSla(id)`

Ejemplo de integración esperada:
```java
public Ticket create(TicketRequest request) {
    Ticket saved = repository.save(/* ... */);
    
    searchClient.index(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getStatus());
    slaClient.startSla(new SlaStartRequest(saved.getId(), /* priority */));
    
    return saved;
}
```

### 5. Manejo de Errores

- [ ] Timeout configurado (5s conexión, 10s lectura) para SearchClient
- [ ] Fallback implementado en SlaServiceClient
- [ ] Si SearchService cae, el ticket igual se crea (no debe fallar el flujo principal)
- [ ] Si SLAService cae, el fallback retorna `null` y se loguea el error

---

## 🚀 Pasos

1. **Crear DTOs** (`SearchIndexRequest`, `SlaStartRequest`, `SlaEvent`)

2. **Implementar clientes**
   ```java
   // SearchClient (RestClient)
   searchClient.index(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getStatus());
   
   // SlaServiceClient (FeignClient, dos endpoints)
   SlaEvent sla = slaClient.startSla(new SlaStartRequest(ticketId, priority));
   SlaEvent current = slaClient.getSla(ticketId);
   ```

3. **Registrar `@EnableFeignClients`** en `TicketsApplication`

4. **Configurar timeout** para SearchClient en `RestClientConfig`
   ```java
   var factory = new SimpleClientHttpRequestFactory();
   factory.setConnectTimeout(Duration.ofSeconds(5));
   factory.setReadTimeout(Duration.ofSeconds(10));
   ```

5. **Implementar fallback** para SlaServiceClient
   ```java
   @Component
   public class SlaServiceClientFallback implements SlaServiceClient {
       public SlaEvent startSla(SlaStartRequest request) {
           log.warn("SLAService no disponible, ticket {} sin SLA", request.ticketId());
           return null;
       }
       public SlaEvent getSla(Long ticketId) { return null; }
   }
   ```

6. **Integrar en TicketService** y agregar endpoint `/tickets/{id}/sla` en el controller

7. **Probar que compila**
   ```bash
   cd Tickets-14
   mvnw.cmd package -DskipTests
   ```

8. **Escribir test con mock**
   ```java
   @MockBean
   private SlaServiceClient slaClient;
   @MockBean
   private SearchClient searchClient;
   
   @Test
   void create_callsSearchAndSla() {
       ticketService.create(request);
       verify(searchClient).index(anyLong(), anyString(), anyString(), anyString());
       verify(slaClient).startSla(any(SlaStartRequest.class));
   }
   ```

9. **Commit**
   ```bash
   git commit -m "feat: integración con SearchService y SLAService"
   ```

---

## ✅ Validación

Debes poder responder:

- [ ] "¿Por qué usé RestClient para SearchService y FeignClient para SLAService?"
- [ ] "¿Qué diferencia hay entre fire-and-forget y esperar respuesta?"
- [ ] "¿Qué pasa si SearchService o SLAService caen en producción?"
- [ ] "¿Cómo configuro timeout en RestClient?"
- [ ] "¿Por qué FeignClient es más limpio cuando hay múltiples endpoints?"
- [ ] "¿Qué hace el fallback de SlaServiceClient?"
- [ ] "¿Cómo verifico en un test que mi servicio llamó al cliente correcto?"

---

## 📦 Entrega

Sube tu código con:
- ✅ `SearchClient.java` con RestClient
- ✅ `SlaServiceClient.java` con FeignClient + fallback
- ✅ DTOs: `SearchIndexRequest`, `SlaStartRequest`, `SlaEvent`
- ✅ Integración en `TicketService.create()`
- ✅ Endpoint `GET /tickets/{id}/sla` en controller
- ✅ Timeout configurado
- ✅ Tests con mocks (verifican que se llama a ambos clientes)

---

## 🔥 Desafío Extra (Opcional)

- Implementar **circuit breaker** con Resilience4j sobre `SlaServiceClient`
- Agregar endpoint `PUT /tickets/{id}/sla/close` que cierra el SLA al resolver el ticket
- Implementar reintentos automáticos en `SearchClient`
- Comparar performance RestClient vs FeignClient con JMeter o similares

---

*[← Volver a Lección 14](01_objetivo_y_alcance.md)*

