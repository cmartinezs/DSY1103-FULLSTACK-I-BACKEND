# Lección 04 - Lista de verificación: ¿llegué al mínimo requerido?

Usa esta lista para revisar tu propio trabajo antes de presentarlo. Cada ítem tiene una breve explicación de qué significa y cómo verificarlo, para que no sea solo un tick en una casilla.

---

## ¿Qué es un indicador de evaluación (IE)?

Los indicadores de evaluación son los criterios concretos con los que se mide tu aprendizaje. Esta lección no busca cubrir todos los indicadores de la unidad, sino construir una base sólida sobre la que se apoyarán las siguientes.

---

## IE 1.2.1 - Estructura CSR con separación real

Este indicador mide si tu código está organizado por responsabilidades o si todo está mezclado en un mismo lugar.

Checklist:

- [x] Existen los paquetes `controller`, `service`, `repository`, `model`
- [x] `TicketController` no accede directamente a la lista de tickets
- [x] `TicketService` llama a `TicketRepository.getAll()` a través de `getTickets()`
- [x] `TicketRepository` es el único lugar donde vive y se accede a la lista

**Cómo verificarlo:** abre cada clase y pregúntate "¿esta clase hace algo que no le corresponde?". Si `TicketController` tiene un `ArrayList` o un `new TicketRepository()`, algo está mal. El flujo correcto es:

```
GET /tickets → TicketController.getAllTickets()
             → TicketService.getTickets()
             → TicketRepository.getAll()
             → [ lista de tickets ]
```

---

## IE 1.1.2 - Diseño de endpoints REST

Este indicador mide si tus URLs siguen las convenciones de REST. Una API bien diseñada es predecible: alguien que nunca la vio puede intuir cómo usarla.

Checklist:

- [x] El recurso está en plural: `/tickets`
- [x] El método HTTP es el correcto para listar: `GET`
- [x] La URL no contiene verbos: no hay `/getTickets` ni `/listar`
- [ ] Base path versionado: `/api/v1` *(pendiente para próximas lecciones)*

**Cómo verificarlo:** haz la petición `GET http://localhost:8080/tickets` en Postman o Insomnia y confirma que recibes `200 OK` con un arreglo JSON.

---

## IE 1.1.3 - Respuestas REST y códigos HTTP

Este indicador mide si tu API comunica correctamente el resultado de cada operación a través de los códigos de estado HTTP. Los códigos no son un detalle menor: le dicen al cliente si su petición fue exitosa, si el recurso no existe, o si cometió un error.

Checklist:

- [x] La petición `GET /tickets` responde con `200 OK`
- [x] El cuerpo de la respuesta es JSON válido (arreglo de objetos `Ticket`)
- [ ] Uso de `ResponseEntity` para control explícito del código HTTP *(pendiente)*

> **¿Por qué está pendiente `ResponseEntity`?** Actualmente Spring retorna `200 OK` automáticamente porque el método no lanza ninguna excepción. Eso funciona para el caso feliz, pero no nos da control cuando las cosas salen mal. En las próximas lecciones usaremos `ResponseEntity` para poder retornar `404`, `400`, `201`, etc. según el caso.

**Cómo verificarlo:** en Postman, observa el código de estado en la esquina superior derecha de la respuesta. Debe decir `200 OK`.

---

## IE 1.2.2 - Modelo y persistencia en memoria

Este indicador mide si tu entidad de dominio está bien definida y si el mecanismo de almacenamiento temporal funciona correctamente.

Checklist:

- [x] La clase `Ticket` tiene campos coherentes: `id` (`Long`), `title`, `description`, `status`
- [x] `TicketRepository` usa `List<Ticket>` para almacenamiento temporal
- [x] El campo `id` está definido y se ve en la respuesta JSON

**Cómo verificarlo:** en la respuesta JSON del endpoint, cada objeto ticket debe tener los cuatro campos. Si falta alguno, revisa que Lombok esté instalado y que `@Getter` esté en la clase `Ticket`.

> **Pista sobre Lombok:** si los campos no aparecen en el JSON, probablemente Lombok no está generando los getters. Verifica que la dependencia esté en el `pom.xml` y que el plugin de Lombok esté habilitado en IntelliJ (Preferences → Plugins → Lombok).

---

## Configuración mínima Spring Boot

Este ítem no es un indicador de evaluación formal, pero es parte de las buenas prácticas que debes incorporar desde el inicio.

Checklist:

- [x] Existe `src/main/resources/application.properties` con `spring.application.name=Tickets`
- [ ] Migrar configuración a `application.yaml` *(pendiente)*
- [ ] Configurar `server.port` con un puerto personalizado *(pendiente)*
- [ ] Configurar `server.servlet.context-path` con un prefijo global *(pendiente)*
- [ ] Crear `src/main/resources/banner.txt` con un texto personalizado *(pendiente)*

**¿Por qué esto importa?** Un proyecto real siempre tiene configuración externa. Aprender a usarla desde el principio te evita el mal hábito de hardcodear valores en el código.

---

## Indicadores que se trabajan en lecciones siguientes

Los siguientes indicadores están en el horizonte del curso. No se evalúan en esta lección, pero es útil que sepas hacia dónde vamos:

| Indicador | Qué cubre |
|---|---|
| IE 1.2.3 | CRUD completo: crear, leer, actualizar y eliminar tickets |
| IE 1.3.1 | Validaciones de entrada con `@Valid`, `@NotNull`, `@NotBlank` |
| IE 1.3.2 | Manejo global de excepciones con `@ControllerAdvice` |
| IE 1.3.3 | Pruebas automáticas de los endpoints REST |

---

## ¿Completé el mínimo de esta lección?

Marcaste todo lo que corresponde si:

- ✅ Tu proyecto tiene los cuatro paquetes con sus clases (`TicketController`, `TicketService`, `TicketRepository`, `Ticket`)
- ✅ Puedes hacer `GET http://localhost:8080/tickets` y recibir un arreglo JSON con los dos tickets semilla
- ✅ Puedes explicar en tus propias palabras qué hace cada clase y por qué está en su paquete correspondiente
