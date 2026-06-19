# AuditService

Microservicio de **registro de auditoría**. Guarda un historial de eventos ocurridos sobre entidades del sistema (creación, cambios de estado, asignaciones), permitiendo trazabilidad de todas las acciones.

Forma parte del ecosistema educativo DSY1103 y de la implementación de pruebas de la lección 22.

## Arquitectura

```text
AuditController -> AuditService -> almacenamiento in-memory
```

- `AuditController` administra las rutas HTTP.
- `AuditService` contiene el registro, listado y filtrado de eventos.

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

## Docker

Este microservicio incluye `Dockerfile` y `.dockerignore` para la Leccion 20.

Construir solo este servicio:

```bash
docker build -t dsy1103-audit-service .
docker run --rm -p 8082:8082 dsy1103-audit-service
```

Ejecutarlo junto a Tickets y los demas servicios:

```bash
cd ../Tickets-22
docker compose up --build audit-service
```

---

## Pruebas y cobertura

Incluye pruebas aisladas de controller con MockMvc y pruebas unitarias de service.

```bash
../Tickets-22/mvnw clean verify
```

Resultado verificado:

- 6 pruebas aprobadas
- 90.00% de cobertura de líneas
- JaCoCo exige un mínimo de 85%
- reporte: `target/site/jacoco/index.html`

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
