# Lección 22 — Pruebas Unitarias en Proyectos de Microservicios

**Aprende a proteger la lógica de tus microservicios con pruebas unitarias, JUnit 5, Mockito y el modelo Given When Then.**

---

## Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. Testing, JUnit 5 y Mockito** | 20 min | Conceptos base |
| **03. Guión Paso a Paso** | 30 min | Implementación práctica |
| **04. Plan de Pruebas Unitarias** | 15 min | Documento breve / README |
| **05. Ejemplos GWT** | 25 min | Given When Then |
| **06. Checklist** | 5 min | Verificación |
| **07. Actividad Individual** | - | Tu tarea |

---

## El problema

Sin pruebas, cada cambio pequeño puede romper algo que antes funcionaba:

- Una validación deja de ejecutarse
- Un repository se llama mal
- Un fallback de microservicio no se activa
- Un endpoint devuelve un código incorrecto
- Un bug llega a producción sin ser detectado

Las pruebas unitarias reducen ese riesgo.

---

## Quick Start

Dependencias habituales en Spring Boot:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Ejemplo mínimo:

```java
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void create_shouldSaveTicket_whenDataIsValid() {
        // given
        Ticket ticket = new Ticket();
        ticket.setTitle("Problema login");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // when
        Ticket result = ticketService.create(ticket);

        // then
        assertThat(result.getTitle()).isEqualTo("Problema login");
        verify(ticketRepository).save(ticket);
    }
}
```

---

## Lo que construirás

1. Crear pruebas unitarias para servicios
2. Usar JUnit 5
3. Usar Mockito para mocks
4. Aplicar Given When Then
5. Crear un plan breve de pruebas unitarias
6. Probar lógica de microservicios sin levantar todos los servicios

---

*Lección 22 - [← Volver al Índice](../INDICE_COMPLETO.md)*
