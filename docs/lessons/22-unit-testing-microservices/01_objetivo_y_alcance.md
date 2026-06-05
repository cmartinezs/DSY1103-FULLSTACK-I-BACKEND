# Lección 22 — Objetivo y Alcance

## ¿De dónde venimos?

Ya tienes una aplicación con múltiples piezas:

- API principal de Tickets
- Microservicios de notificación, auditoría, búsqueda y SLA
- Seguridad
- Manejo global de errores
- Documentación OpenAPI
- HATEOAS

Mientras más piezas existen, más importante es probar la lógica sin depender de levantar todo el sistema.

---

## Objetivo

Al terminar esta lección podrás:

1. Explicar qué es testing y por qué importa
2. Explicar qué pasa si un proyecto no tiene testing
3. Diferenciar pruebas unitarias de pruebas de integración
4. Usar JUnit 5
5. Usar Mockito
6. Escribir pruebas con Given When Then
7. Crear un plan breve de pruebas unitarias
8. Probar servicios que consumen otros microservicios usando mocks

---

## ¿Qué son las pruebas unitarias?

Una prueba unitaria valida una unidad pequeña de código de forma aislada.

En este curso, normalmente probaremos:

- Métodos de `service`
- Validaciones de negocio
- Mappers DTO
- Fallbacks de clientes HTTP
- Handlers simples

No probaremos como unitario:

- Base de datos real
- Servidores externos reales
- Todo Spring Boot levantado completo

---

## Alcance

Implementaremos pruebas para:

- `TicketService`
- Cliente o wrapper de `NotificationService`
- Fallback de `AuditService`
- Validaciones de estados de ticket

---

## Resultado esperado

Al ejecutar:

```bash
mvnw.cmd test
```

Debe verse:

```text
BUILD SUCCESS
```

Y debe existir un README o sección de plan de pruebas unitarias.

