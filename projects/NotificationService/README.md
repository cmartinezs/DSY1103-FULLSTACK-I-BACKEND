# NotificationService

Microservicio de **envío de notificaciones**. Recibe solicitudes de notificación desde otros servicios y las almacena, simulando un sistema de mensajería (email, push, etc.).

Forma parte del ecosistema educativo DSY1103 y de la implementación de pruebas de la lección 22.

## Arquitectura

```text
NotificationController -> NotificationService -> almacenamiento in-memory
```

- `NotificationController` administra las rutas HTTP.
- `NotificationService` contiene la creación y consulta de notificaciones.

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

## Docker

Este microservicio incluye `Dockerfile` y `.dockerignore` para la Leccion 20.

Construir solo este servicio:

```bash
docker build -t dsy1103-notification-service .
docker run --rm -p 8081:8081 dsy1103-notification-service
```

Ejecutarlo junto a Tickets y los demas servicios:

```bash
cd ../Tickets-22
docker compose up --build notification-service
```

---

## Pruebas y cobertura

Incluye pruebas aisladas de controller con MockMvc y pruebas unitarias de service.

```bash
../Tickets-22/mvnw clean verify
```

Resultado verificado:

- 7 pruebas aprobadas
- 89.66% de cobertura de líneas
- JaCoCo exige un mínimo de 85%
- reporte: `target/site/jacoco/index.html`

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
