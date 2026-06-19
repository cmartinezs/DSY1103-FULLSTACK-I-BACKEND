# SearchService

Microservicio de **indexación y búsqueda full-text de tickets**. Mantiene un índice en memoria con el contenido de cada ticket, permitiendo búsquedas por texto libre sobre título y descripción.

Forma parte del ecosistema educativo DSY1103 y de la implementación de pruebas de la lección 22.

## Arquitectura

```text
SearchController -> SearchService -> índice in-memory
```

- `SearchController` administra indexación y consultas HTTP.
- `SearchService` contiene reindexación, búsqueda y recuperación por ticket.

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

## Docker

Este microservicio incluye `Dockerfile` y `.dockerignore` para la Leccion 20.

Construir solo este servicio:

```bash
docker build -t dsy1103-search-service .
docker run --rm -p 8084:8084 dsy1103-search-service
```

Ejecutarlo junto a Tickets y los demas servicios:

```bash
cd ../Tickets-22
docker compose up --build search-service
```

---

## Pruebas y cobertura

Incluye pruebas aisladas de controller con MockMvc y pruebas unitarias de service.

```bash
../Tickets-22/mvnw clean verify
```

Resultado verificado:

- 8 pruebas aprobadas
- 93.33% de cobertura de líneas
- JaCoCo exige un mínimo de 85%
- reporte: `target/site/jacoco/index.html`

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
