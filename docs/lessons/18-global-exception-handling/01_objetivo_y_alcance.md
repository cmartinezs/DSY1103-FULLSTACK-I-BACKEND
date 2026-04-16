# Lección 18 - Global Exception Handling: ¿Qué vas a aprender?

## ¿De dónde venimos?

En Lección 17 aprendiste a loguear eventos. Ahora registras qué pasó, pero si una excepción inesperada ocurre, el cliente recibe un `500` genérico sin detalles.

---

## ¿Qué vas a construir?

Al terminar, tu API manejará automáticamente TODAS las excepciones:

```
POST /tickets con título vacío
    ↓
@Valid valida → MethodArgumentNotValidException
    ↓
GlobalExceptionHandler captura
    ↓
Respuesta: 400 + {"message": "Título no puede estar vacío"}
```

### Excepciones capturadas

- `IllegalArgumentException` → 400 Bad Request
- `ConstraintViolationException` → 400 Bad Request
- `MethodArgumentNotValidException` → 400 Bad Request
- `EntityNotFoundException` → 404 Not Found
- `Exception` genérico → 500 Internal Server Error + log

---

## ¿Qué NO cubre?

| Tema | Razón |
|------|-------|
| Excepciones de Base de Datos | Llegan después de JPA avanzado |
| Custom exceptions propias | Nivel intermedio |
| Retry logic | Patrón avanzado |

El foco: **handler centralizado para excepciones comunes**.

---

## Requerimientos

| ID | Requerimiento |
|----|---------------|
| **REQ-30** | Crear GlobalExceptionHandler con @ControllerAdvice |
| **REQ-31** | Capturar IllegalArgumentException → 400 |
| **REQ-32** | Capturar MethodArgumentNotValidException → 400 |
| **REQ-33** | Capturar Exception genérico → 500 + log |
| **REQ-34** | Stack trace solo en dev, oculto en prod |

---

## Estructura antes vs después

```
Antes:
└── controller/TicketController.java   (try/catch en cada endpoint)

Después:
├── controller/TicketController.java   (sin try/catch innecesario)
├── config/GlobalExceptionHandler.java ← NUEVO (@ControllerAdvice)
└── model/ErrorResponse.java           (reutilizado)
```
