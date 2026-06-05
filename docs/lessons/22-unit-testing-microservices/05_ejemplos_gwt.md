# Ejemplos GWT — Given When Then

## ¿Qué es Given When Then?

GWT es una forma de ordenar pruebas:

- **Given:** contexto inicial
- **When:** acción que se ejecuta
- **Then:** resultado esperado

Ayuda a que la prueba se lea como una historia corta.

---

## Ejemplo 1: Caso feliz

```java
@Test
void findById_shouldReturnTicket_whenTicketExists() {
    // given
    Ticket ticket = new Ticket();
    ticket.setId(1L);
    ticket.setTitle("Problema login");

    when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

    // when
    Ticket result = ticketService.findById(1L);

    // then
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getTitle()).isEqualTo("Problema login");
    verify(ticketRepository).findById(1L);
}
```

---

## Ejemplo 2: Caso de error

```java
@Test
void findById_shouldThrowException_whenTicketDoesNotExist() {
    // given
    when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> ticketService.findById(99L))
        .isInstanceOf(EntityNotFoundException.class);

    verify(ticketRepository).findById(99L);
}
```

---

## Ejemplo 3: No llamar microservicio real

```java
@Test
void create_shouldCallAuditClient_whenTicketIsCreated() {
    // given
    Ticket saved = new Ticket();
    saved.setId(10L);
    saved.setTitle("Problema impresora");

    when(ticketRepository.save(any(Ticket.class))).thenReturn(saved);

    // when
    ticketService.create(saved);

    // then
    verify(auditClient).logEvent(argThat(request ->
        request.entityId().equals(10L)
            && request.eventType().equals("TICKET_CREATED")
    ));
}
```

---

## Ejemplo 4: Verificar que algo no ocurre

```java
@Test
void create_shouldNotSendNotification_whenTicketIsDraft() {
    // given
    Ticket draft = new Ticket();
    draft.setTitle("Borrador");
    draft.setStatus("DRAFT");

    when(ticketRepository.save(any(Ticket.class))).thenReturn(draft);

    // when
    ticketService.create(draft);

    // then
    verify(notificationClient, never())
        .send(anyString(), anyString(), anyString(), anyString());
}
```

---

## Ejemplo 5: Parametrized test

```java
@ParameterizedTest
@ValueSource(strings = {"", " ", "   "})
void create_shouldRejectBlankTitle(String invalidTitle) {
    // given
    Ticket ticket = new Ticket();
    ticket.setTitle(invalidTitle);

    // when / then
    assertThatThrownBy(() -> ticketService.create(ticket))
        .isInstanceOf(IllegalArgumentException.class);

    verify(ticketRepository, never()).save(any(Ticket.class));
}
```

---

## Naming recomendado

Usa este patrón:

```text
metodo_shouldResultadoEsperado_whenCondicion
```

Ejemplos:

- `create_shouldSaveTicket_whenDataIsValid`
- `findById_shouldThrowException_whenTicketDoesNotExist`
- `delete_shouldCallRepository_whenUserIsAdmin`
- `send_shouldUseFallback_whenNotificationServiceIsDown`

