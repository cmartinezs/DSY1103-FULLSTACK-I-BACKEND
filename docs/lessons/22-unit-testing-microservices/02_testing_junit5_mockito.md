# Testing, JUnit 5 y Mockito

## ¿Qué es Testing?

Testing es el proceso de verificar que el software hace lo que se espera.

No significa demostrar que el sistema nunca fallará. Significa reducir incertidumbre y detectar errores temprano.

---

## ¿Qué pasa si un proyecto no tiene Testing?

Sin pruebas:

- Cada cambio requiere probar manualmente muchas rutas
- Los errores vuelven sin ser detectados
- El equipo teme refactorizar
- Integrar microservicios se vuelve más riesgoso
- Los bugs aparecen tarde, cuando corregirlos cuesta más

En microservicios, el problema se multiplica porque un servicio puede romper a otro.

---

## Tipos de pruebas

| Tipo | Qué valida | Ejemplo |
|------|------------|---------|
| **Unitaria** | Una clase aislada | `TicketService` con repository mock |
| **Integración** | Varias piezas juntas | Controller + Spring + H2 |
| **Contrato** | Compatibilidad entre servicios | Tickets envía request válido a Audit |
| **End-to-end** | Flujo completo | Crear ticket y verificar notificación |

Esta lección se enfoca en **unitarias**.

---

## ¿Qué es JUnit 5?

JUnit 5 es el framework principal para escribir y ejecutar pruebas en Java.

Anotaciones frecuentes:

| Anotación | Uso |
|-----------|-----|
| `@Test` | Marca un método como prueba |
| `@BeforeEach` | Ejecuta setup antes de cada prueba |
| `@DisplayName` | Nombre legible para la prueba |
| `@ParameterizedTest` | Prueba con varios datos |

Ejemplo:

```java
@Test
void shouldReturnTicketWhenIdExists() {
    // prueba
}
```

---

## ¿Qué es Mockito?

Mockito permite crear objetos falsos controlados para aislar la unidad probada.

Ejemplo:

```java
when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

Ticket result = ticketService.findById(1L);

verify(ticketRepository).findById(1L);
```

Con Mockito puedes probar `TicketService` sin conectarte a una base de datos.

---

## Beneficios de las pruebas unitarias

- Son rápidas
- Detectan errores temprano
- Permiten refactorizar con menos riesgo
- Documentan reglas de negocio
- Facilitan revisar trabajos de otros compañeros
- Ayudan a validar fallbacks de microservicios

---

## Buenas prácticas de Testing

- Una prueba debe verificar una idea principal
- El nombre debe explicar escenario y resultado
- Usa Given When Then
- Evita depender de orden entre pruebas
- No llames servicios reales en pruebas unitarias
- No pruebes detalles internos innecesarios
- Usa datos simples y explícitos
- Verifica interacciones importantes con `verify`
- Prueba casos felices y casos de error

