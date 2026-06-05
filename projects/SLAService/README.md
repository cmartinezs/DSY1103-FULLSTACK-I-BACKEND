# SLAService

Microservicio de **control de tiempos de resolución (Service Level Agreement)**. Registra cuándo se abrió un ticket y calcula automáticamente el plazo de resolución según su prioridad. Permite consultar el estado del SLA y cerrarlo cuando el ticket se resuelve.

Forma parte del ecosistema educativo DSY1103. Los alumnos deben implementar el cliente en su aplicación Tickets (lección 14).

---

## Puerto

`8085`

---

## Cómo ejecutar

```bash
cd SLAService
mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run          # macOS / Linux
```

---

## Plazos por prioridad

| Prioridad | Plazo |
|-----------|-------|
| `HIGH`    | 24 horas |
| `MEDIUM`  | 72 horas (3 días) |
| `LOW`     | 168 horas (7 días) |

---

## API

### `POST /api/sla/start`

Inicia el SLA de un ticket. Calcula el `deadline` a partir de la prioridad. Si el ticket ya tiene un SLA abierto, retorna el existente sin crear uno nuevo.

**Body:**
```json
{
  "ticketId": "1",
  "priority": "HIGH"
}
```

**Response:**
```json
{
  "id": 1,
  "ticketId": 1,
  "priority": "HIGH",
  "deadline": "2025-01-02T10:00:00Z",
  "status": "OPEN",
  "startedAt": "2025-01-01T10:00:00Z"
}
```

**Ejemplo:**
```bash
curl -X POST http://localhost:8085/api/sla/start \
  -H "Content-Type: application/json" \
  -d '{"ticketId":"1","priority":"HIGH"}'
```

---

### `GET /api/sla/{ticketId}`

Obtiene el estado actual del SLA de un ticket.

**Response:** `200 OK` con el registro SLA, o `404 Not Found` si no existe.

```bash
curl http://localhost:8085/api/sla/1
```

---

### `PUT /api/sla/{ticketId}/close`

Cierra el SLA de un ticket. Debe llamarse cuando el ticket se resuelve o cierra.

**Response:** `200 OK` con el registro actualizado (`status: "CLOSED"`, `closedAt` añadido).

```bash
curl -X PUT http://localhost:8085/api/sla/1/close
```

---

### `GET /api/sla`

Lista todos los registros SLA (abiertos y cerrados).

```bash
curl http://localhost:8085/api/sla
```

---

## Notas

- **Almacenamiento en memoria**: los registros se pierden al reiniciar el servicio.
- **Un SLA por ticket**: si ya existe un SLA abierto para un ticket, `POST /api/sla/start` retorna el existente.
- **Prioridad por defecto**: si no se envía `priority`, se asume `MEDIUM` (72 horas).
