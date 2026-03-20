# Lección 08 - Por qué necesitas un DTO

## El problema con exponer el modelo directamente

Una API que usa su modelo de dominio como entrada directa tiene un problema fundamental: el **modelo está diseñado para el sistema**, no para el cliente.

```java
// Mal: el modelo de dominio es la entrada
@PostMapping
public ResponseEntity<?> create(@RequestBody Ticket ticket) { ... }
```

Esto tiene tres consecuencias:

---

## Razón 1: Seguridad — el cliente no debería poder controlar todo

El modelo `Ticket` tiene campos que el servidor asigna: `id`, `status`, `createdAt`, `estimatedResolutionDate`. Si expones el modelo directamente, el cliente puede intentar enviar esos campos.

**Ejemplo de ataque (mass assignment):**

```json
{
  "title": "Bug legítimo",
  "description": "...",
  "status": "RESOLVED",
  "createdAt": "2020-01-01T00:00:00"
}
```

Si el código del `Service` no tiene cuidado (o alguien lo modifica inadvertidamente), esos campos podrían ser tomados tal cual. Con un DTO, ese peligro desaparece: el objeto de entrada solo tiene los campos que declaraste explícitamente.

```java
// Seguro: TicketRequest no tiene id, status, createdAt, estimatedResolutionDate
public class TicketRequest {
    @NotBlank
    private String title;
    private String description;
}
```

---

## Razón 2: Control — el servidor decide qué calcula

Algunos campos del `Ticket` son **calculados por el servidor** según reglas de negocio:

- `id` → asignado automáticamente
- `status` → siempre empieza en `NEW`
- `createdAt` → momento exacto del servidor
- `estimatedResolutionDate` → 5 días después de la creación

Si el modelo fuera la entrada, el `Service` tendría que **ignorar activamente** lo que el cliente mandó. Con un DTO, estos campos simplemente no existen en la entrada — no hay nada que ignorar.

```java
// El Service construye el Ticket con sus propias reglas
Ticket ticket = new Ticket();
ticket.setTitle(request.getTitle());     // del cliente
ticket.setDescription(request.getDescription()); // del cliente
ticket.setStatus("NEW");                 // servidor
ticket.setCreatedAt(LocalDateTime.now()); // servidor
ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5)); // servidor
```

---

## Razón 3: Desacoplamiento — el modelo puede evolucionar sin romper la API

Si en el futuro agregas un campo `priority` al modelo `Ticket` (con base de datos real), el contrato de entrada de tu API **no necesita cambiar**. El DTO sigue siendo el mismo. Solo el `Service` sabe cómo mapear `request → ticket` con el campo nuevo.

Si expusieras el modelo directamente, cualquier cambio en el modelo rompería el contrato con los clientes que ya consumen tu API.

---

## Razón 4: Validación — dónde poner las restricciones

Las anotaciones `@NotBlank`, `@Size`, `@Min` pertenecen a la entrada, no al modelo de dominio.

¿Por qué? Porque la misma regla puede ser diferente según de dónde viene el dato:

- El `title` puede ser obligatorio cuando lo envía el cliente por HTTP
- Pero puede ser `null` legítimamente si se genera internamente (por ej., un sistema de importación)

Si pones `@NotBlank` en el modelo, aplica en **todos los contextos**. Si lo pones en el DTO, aplica solo cuando el cliente envía datos por la API.

```java
// DTO: @NotBlank aplica a la entrada de la API
public class TicketRequest {
    @NotBlank(message = "El título no puede estar vacío")
    private String title;
}

// Modelo: sin @NotBlank, libre para otros contextos
public class Ticket {
    private String title; // puede ser generado internamente sin restricción
}
```

---

## Los tres tipos de DTO que existen

Esta lección introduce solo uno de ellos, pero es útil conocer el panorama:

| Tipo | Nombre convencional | ¿Cuándo se usa? |
|---|---|---|
| **DTO de entrada** (lo que implementamos hoy) | `XxxRequest`, `XxxDto`, `CreateXxxCommand` | Para recibir datos del cliente: `@RequestBody` |
| **DTO de salida** | `XxxResponse`, `XxxView` | Para controlar qué campos devuelve la API al cliente |
| **DTO de capa** | `XxxDto` (interno) | Para pasar datos entre capas sin exponer el modelo |

Por ahora usamos el modelo `Ticket` directamente como respuesta (`200 OK` devuelve el `Ticket` completo). En una API más madura, tendrías un `TicketResponse` que oculta campos internos y puede transformar formatos.

---

## El patrón completo con DTO

```
Cliente
    ↓ envía JSON
    ↓
TicketRequest (DTO de entrada)
    ↓ @Valid lo valida
    ↓
TicketController.create(TicketRequest)
    ↓ llama al service
    ↓
TicketService.create(TicketRequest)
    ↓ valida reglas de negocio
    ↓ construye Ticket
    ↓
TicketRepository.save(Ticket)
    ↓
Ticket (modelo de dominio, persiste en memoria)
    ↓ devuelve al controller
    ↓
ResponseEntity con Ticket como body
    ↓ Jackson serializa a JSON
    ↓
Cliente recibe { id, title, description, status, createdAt, ... }
```

Cada capa trabaja con lo que necesita. El cliente nunca ve el código interno ni puede manipular campos que no le corresponden.

