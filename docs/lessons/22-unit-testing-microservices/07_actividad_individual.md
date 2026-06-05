# Lección 21 — Actividad Individual

## Objetivo

Crear pruebas unitarias para la lógica principal de Tickets y para al menos una interacción con microservicios.

---

## Instrucciones

### Parte 1: TicketService

Crea `TicketServiceTest` con al menos:

1. `create_shouldSaveTicket_whenDataIsValid`
2. `findById_shouldReturnTicket_whenTicketExists`
3. `findById_shouldThrowException_whenTicketDoesNotExist`
4. `create_shouldRejectBlankTitle_whenTitleIsInvalid`

### Parte 2: Microservicio

Elige una opción:

- Probar que `NotificationClient` se llama al crear un ticket
- Probar que `AuditServiceClientFallback` retorna una respuesta segura
- Probar que una falla de `NotificationService` no rompe la creación del ticket
- Probar que `SearchService` no se llama si el ticket no fue guardado

### Parte 3: Plan breve

Agrega al README una sección:

```md
## Plan de Pruebas Unitarias

### Alcance
- TicketService
- Notification/Audit fallback

### Fuera de alcance
- Base de datos real
- Servicios externos reales

### Comando
`mvnw.cmd test`
```

---

## Entregable

Debes entregar:

- Código de pruebas en `src/test/java`
- Plan breve en README
- Evidencia de `mvnw.cmd test` exitoso

---

## Preguntas para responder

1. ¿Qué es una prueba unitaria?
2. ¿Por qué no debe llamar a un microservicio real?
3. ¿Qué rol cumple Mockito?
4. ¿Qué significa Given When Then?
5. ¿Qué riesgo queda si solo se prueban casos felices?

