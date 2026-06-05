# Guión Paso a Paso

## Paso 1: Verificar dependencia de test

En `pom.xml` debe existir:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Spring Boot Starter Test incluye JUnit 5, Mockito, AssertJ y herramientas de Spring Test.

---

## Paso 2: Crear estructura de pruebas

Ubicación:

```text
src/test/java/
└── .../
    ├── service/
    │   └── TicketServiceTest.java
    ├── clients/
    │   └── NotificationClientTest.java
    └── controller/
        └── TicketControllerTest.java
```

Para pruebas unitarias de service, no necesitas levantar todo Spring.

---

## Paso 3: Crear prueba de service con Mockito

```java
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void create_shouldSaveTicket_whenDataIsValid() {
        // given
        Ticket ticket = new Ticket();
        ticket.setTitle("No puedo ingresar");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // when
        Ticket result = ticketService.create(ticket);

        // then
        assertThat(result.getTitle()).isEqualTo("No puedo ingresar");
        verify(ticketRepository).save(ticket);
    }
}
```

---

## Paso 4: Probar error esperado

```java
@Test
void findById_shouldThrowException_whenTicketDoesNotExist() {
    // given
    when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> ticketService.findById(99L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Ticket");

    verify(ticketRepository).findById(99L);
}
```

---

## Paso 5: Probar integración con microservicio usando mock

La prueba no debe llamar a `NotificationService` real.

```java
@Test
void create_shouldSendNotification_whenTicketIsCreated() {
    // given
    Ticket ticket = new Ticket();
    ticket.setTitle("Error de acceso");

    Ticket saved = new Ticket();
    saved.setId(1L);
    saved.setTitle("Error de acceso");

    when(ticketRepository.save(any(Ticket.class))).thenReturn(saved);

    // when
    ticketService.create(ticket);

    // then
    verify(notificationClient).send(
        eq("Ticket creado"),
        contains("Error de acceso"),
        eq("INFO"),
        anyString()
    );
}
```

---

## Paso 6: Probar fallback

```java
@Test
void sendNotification_shouldNotFailTicketCreation_whenNotificationServiceIsDown() {
    // given
    Ticket ticket = new Ticket();
    ticket.setTitle("Problema correo");

    when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
    doThrow(new RestClientException("Service down"))
        .when(notificationClient)
        .send(anyString(), anyString(), anyString(), anyString());

    // when / then
    assertThatCode(() -> ticketService.create(ticket))
        .doesNotThrowAnyException();
}
```

> Esta prueba solo es válida si tu regla de negocio dice que la creación del ticket no debe fallar cuando falla la notificación.

---

## Paso 7: Ejecutar pruebas

En Windows:

```bash
mvnw.cmd test
```

Una clase específica:

```bash
mvnw.cmd test -Dtest=TicketServiceTest
```

---

## Paso 8: Mantener pruebas legibles

Estructura recomendada:

```java
@Test
void method_shouldExpectedResult_whenCondition() {
    // given

    // when

    // then
}
```

