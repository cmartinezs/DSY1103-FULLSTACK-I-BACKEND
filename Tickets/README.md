# Tickets API

Subproyecto backend del repositorio del curso **DSY1103 - Fullstack I**.

API REST construida con **Spring Boot 4** y **Java 21** para la gestión de tickets de soporte.

---

## 🛠️ Tecnologías

| Herramienta          | Versión  |
|----------------------|----------|
| Java                 | 21       |
| Spring Boot          | 4.0.3    |
| Spring Web MVC       | (incluido en Boot) |
| Lombok               | (incluido en Boot) |
| Spring Boot DevTools | (incluido en Boot) |
| Maven Wrapper        | (incluido) |

---

## 📁 Estructura del proyecto

```
src/
└── main/
    ├── java/cl/duoc/fullstack/tickets/
    │   ├── TicketsApplication.java      # Punto de entrada
    │   ├── controller/
    │   │   └── TicketController.java    # Controlador REST
    │   ├── model/
    │   │   └── Ticket.java              # Modelo de dominio
    │   ├── respository/
    │   │   └── TicketRepository.java    # Repositorio en memoria
    │   └── service/
    │       └── TicketService.java       # Lógica de negocio
    └── resources/
        └── application.properties
```

---

## 📦 Modelo

### `Ticket`

| Campo         | Tipo     | Descripción               |
|---------------|----------|---------------------------|
| `id`          | `Long`   | Identificador del ticket  |
| `title`       | `String` | Título del ticket         |
| `description` | `String` | Descripción del ticket    |
| `status`      | `String` | Estado del ticket (`NEW`, etc.) |

---

## 🔌 Endpoints

Base URL: `http://localhost:8080`

| Método | Ruta       | Descripción                     |
|--------|------------|---------------------------------|
| `GET`  | `/tickets` | Retorna la lista de todos los tickets |

### Ejemplo de respuesta `GET /tickets`

```json
[
  {
    "id": 1,
    "title": "Ticket 1",
    "description": "Ticket 1",
    "status": "NEW"
  },
  {
    "id": 2,
    "title": "Ticket 2",
    "description": "Ticket 2",
    "status": "NEW"
  }
]
```

---

## 🚀 Ejecutar

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

---

## 🧪 Probar

```bash
./mvnw test
```

---

## 📚 Referencia

- README principal: [`../README.md`](../README.md)
- Material de clases: [`../docs/lessons/04-responsabilities/`](../docs/lessons/04-responsabilities/)
