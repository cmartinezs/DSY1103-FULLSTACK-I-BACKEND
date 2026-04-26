# NotificationService

Microservicio de **envío de notificaciones**. Recibe solicitudes de notificación desde otros servicios y las almacena, simulando un sistema de mensajería (email, push, etc.).

Forma parte del ecosistema educativo DSY1103. La aplicación Tickets lo consume via RestClient (lección 14).

---

## Puerto

`8081`

---

## Cómo ejecutar

```bash
cd NotificationService
mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run          # macOS / Linux
```

---

## API

### `POST /api/notifications`

Crea una nueva notificación.

**Body:**
```json
{
  "title": "Ticket asignado",
  "message": "Se te ha asignado el ticket 'Bug en login'",
  "type": "INFO",
  "recipient": "juan@example.com"
}
```

Campos opcionales: `type` (default `"INFO"`), `recipient` (default `"all"`).

**Response:**
```json
{
  "id": 1,
  "title": "Ticket asignado",
  "message": "Se te ha asignado el ticket 'Bug en login'",
  "type": "INFO",
  "recipient": "juan@example.com",
  "sent": false,
  "timestamp": 1714000000000
}
```

**Ejemplo:**
```bash
curl -X POST http://localhost:8081/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"title":"Ticket asignado","message":"Se te asignó Bug en login","type":"INFO","recipient":"juan@example.com"}'
```

---

### `GET /api/notifications`

Lista todas las notificaciones registradas.

```bash
curl http://localhost:8081/api/notifications
```

---

### `GET /api/notifications/{id}`

Obtiene una notificación por su ID.

```bash
curl http://localhost:8081/api/notifications/1
```

---

## Notas

- **Almacenamiento en memoria**: las notificaciones se pierden al reiniciar el servicio.
- **`sent: false`**: el campo simula que la notificación fue recibida pero no despachada; en un sistema real este servicio dispararía el envío.
