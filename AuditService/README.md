# AuditService

Microservicio de **registro de auditoría**. Guarda un historial de eventos ocurridos sobre entidades del sistema (creación, cambios de estado, asignaciones), permitiendo trazabilidad de todas las acciones.

Forma parte del ecosistema educativo DSY1103. La aplicación Tickets lo consume via FeignClient (lección 14).

---

## Puerto

`8082`

---

## Cómo ejecutar

```bash
cd AuditService
mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run          # macOS / Linux
```

---

## API

### `POST /api/audit`

Registra un evento de auditoría.

**Body:**
```json
{
  "action": "STATUS_CHANGE",
  "entityType": "Ticket",
  "entityId": "1",
  "userId": "10",
  "username": "juan@example.com",
  "details": "Estado cambió de NEW a IN_PROGRESS"
}
```

Campos opcionales: `entityType` (default `"Ticket"`), `userId` (default `0`), `username` (default `"system"`), `details` (default `""`).

**Response:**
```json
{
  "id": 1,
  "action": "STATUS_CHANGE",
  "entityType": "Ticket",
  "entityId": 1,
  "userId": 10,
  "username": "juan@example.com",
  "details": "Estado cambió de NEW a IN_PROGRESS",
  "timestamp": 1714000000000
}
```

**Ejemplo:**
```bash
curl -X POST http://localhost:8082/api/audit \
  -H "Content-Type: application/json" \
  -d '{"action":"TICKET_CREATED","entityType":"Ticket","entityId":"1","username":"system"}'
```

---

### `GET /api/audit/ticket/{ticketId}`

Obtiene todos los eventos de auditoría de un ticket específico.

```bash
curl http://localhost:8082/api/audit/ticket/1
```

---

### `GET /api/audit`

Lista todos los eventos de auditoría registrados.

```bash
curl http://localhost:8082/api/audit
```

---

## Notas

- **Almacenamiento en memoria**: los registros se pierden al reiniciar el servicio.
- **`entityId` y `userId` como String en el body**: el servicio los parsea a `Long` internamente.
- **Acciones comunes**: `TICKET_CREATED`, `STATUS_CHANGE`, `TICKET_ASSIGNED`, `TICKET_CLOSED`.
