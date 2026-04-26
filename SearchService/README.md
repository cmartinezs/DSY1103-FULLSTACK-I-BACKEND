# SearchService

Microservicio de **indexación y búsqueda full-text de tickets**. Mantiene un índice en memoria con el contenido de cada ticket, permitiendo búsquedas por texto libre sobre título y descripción.

Forma parte del ecosistema educativo DSY1103. Los alumnos deben implementar el cliente en su aplicación Tickets (lección 14).

---

## Puerto

`8084`

---

## Cómo ejecutar

```bash
cd SearchService
mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run          # macOS / Linux
```

---

## API

### `POST /api/search/index`

Indexa o reindexar un ticket. Si el ticket ya tiene una entrada en el índice, la reemplaza.

**Body:**
```json
{
  "ticketId": "1",
  "title": "Bug en login",
  "description": "El formulario no valida el campo email",
  "status": "IN_PROGRESS"
}
```

**Response:** `204 No Content`

**Ejemplo:**
```bash
curl -X POST http://localhost:8084/api/search/index \
  -H "Content-Type: application/json" \
  -d '{"ticketId":"1","title":"Bug en login","description":"Falla validación email","status":"NEW"}'
```

---

### `GET /api/search?q={texto}`

Busca tickets cuyo título o descripción contenga el texto indicado. Sin parámetro retorna todos los registros indexados.

**Response:**
```json
[
  {
    "id": 1,
    "ticketId": 1,
    "title": "Bug en login",
    "description": "Falla validación email",
    "status": "NEW",
    "indexedAt": 1714000000000
  }
]
```

**Ejemplos:**
```bash
# Buscar por texto
curl "http://localhost:8084/api/search?q=login"

# Listar todo el índice
curl http://localhost:8084/api/search
```

---

### `GET /api/search/ticket/{ticketId}`

Obtiene la entrada de índice de un ticket específico.

**Response:** `200 OK` con el objeto indexado, o `404 Not Found` si no está indexado.

```bash
curl http://localhost:8084/api/search/ticket/1
```

---

## Notas

- **Almacenamiento en memoria**: el índice se pierde al reiniciar el servicio.
- **Reindexación**: enviar `POST /api/search/index` con el mismo `ticketId` actualiza la entrada existente.
- **Búsqueda**: es case-insensitive y busca por subcadena en título y descripción.
