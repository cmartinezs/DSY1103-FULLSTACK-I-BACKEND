# Lección 04 - Por qué hacemos las cosas así: decisiones de diseño explicadas

Esta sección no es un listado de reglas para memorizar. Es una explicación de las decisiones que tomamos al construir la API, para que entiendas el razonamiento detrás de cada una. En el mundo real, un buen desarrollador no solo sabe *qué* hacer, sino *por qué* lo hace.

---

## Decisión 1: la URL es un sustantivo, no un verbo

Cuando defines una URL en una API REST, la URL debe representar un **recurso** (una "cosa"), no una acción. Por eso usamos:

```
GET /tickets
```

Y no:

```
GET /getTickets        ← MAL: el verbo ya está en el método HTTP (GET)
GET /getAllTickets      ← MAL: la URL no es un nombre de función Java
GET /ticket-list       ← MAL: no describe un recurso, describe una estructura
```

El método HTTP (`GET`, `POST`, `PUT`, `DELETE`) es quien expresa la acción. La URL expresa *sobre qué recurso* se realiza esa acción. Separar ambas responsabilidades hace que tu API sea predecible: cualquier desarrollador que la consuma puede intuir qué hace cada endpoint sin leer documentación.

> **¿Por qué el recurso va en plural (`/tickets` y no `/ticket`)?**
> Porque el endpoint devuelve una colección. Cuando dices `/tickets`, estás describiendo "el conjunto de tickets del sistema". Es una convención ampliamente adoptada en APIs REST del mundo real.

> **Diseño objetivo (pendiente):** en una API de producción, además agregaríamos un prefijo `/api` para separar la API del resto de rutas, y `/v1` para indicar la versión, quedando `GET /api/v1/tickets`. Eso permite que en el futuro exista una `v2` sin romper a los clientes que ya consumen la `v1`. Lo incorporaremos en lecciones futuras.

---

## Decisión 2: devolver `ResponseEntity` en lugar del objeto directo

Actualmente el controlador devuelve `List<Ticket>` directamente:

```java
public List<Ticket> getAllTickets() {
    return this.service.getTickets();
}
```

Spring Boot detecta que el método retornó sin error y envía automáticamente un `200 OK`. Eso funciona, pero oculta algo importante: **el código de estado HTTP es parte de la respuesta** y debería ser explícito en tu código.

La forma profesional es usar `ResponseEntity`:

```java
public ResponseEntity<List<Ticket>> getAllTickets() {
    return ResponseEntity.ok(this.service.getTickets());
}
```

¿Por qué es mejor? Porque cuando más adelante necesites devolver un `404 Not Found` (ticket no existe) o un `201 Created` (ticket creado exitosamente), ya tendrás la estructura lista. Si empiezas devolviendo el objeto directo, después tendrás que refactorizar todos tus endpoints.

> **Estado actual:** aún retornamos `List<Ticket>` directamente. Incorporar `ResponseEntity` es el siguiente paso planificado.

---

## Decisión 3: inyección de dependencias por constructor

En el proyecto usamos este patrón en todas las capas:

```java
public class TicketController {

    private TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }
}
```

La pregunta natural es: ¿por qué no hacemos simplemente `new TicketService()` dentro del constructor? La respuesta es que estaríamos violando un principio clave: **la clase que necesita una dependencia no debería ser responsable de crearla**.

Cuando inyectas por constructor:

- **Spring gestiona los objetos por ti.** No tienes que preocuparte de cuándo crear o destruir instancias.
- **Las dependencias son visibles.** Cualquiera que lea el constructor sabe exactamente qué necesita esa clase para funcionar. No hay dependencias ocultas.
- **Las pruebas unitarias se simplifican.** Puedes pasar un objeto falso (`mock`) en lugar del real sin modificar el código de producción.

Esto contrasta con la inyección por campo (usando `@Autowired` directamente sobre el atributo), que aunque es más corta, esconde las dependencias y hace las pruebas más difíciles.

---

## Decisión 4: cada capa tiene una sola pregunta que responder

Una forma práctica de recordar para qué sirve cada capa es asociarla con una pregunta:

| Capa | Su única pregunta |
|---|---|
| `Controller` | ¿Cómo entra y sale la petición HTTP? |
| `Service` | ¿Qué regla de negocio aplica aquí? |
| `Repository` | ¿Dónde y cómo se almacenan o recuperan los datos? |
| `Model` | ¿Cómo se ve la estructura del dato? |

Cuando revisas tu código, puedes hacer la siguiente prueba de cordura:

- ¿Hay `ResponseEntity` fuera del `Controller`? → Probablemente algo del HTTP se está filtrando hacia capas que no deberían saber de HTTP.
- ¿Hay lógica de negocio (`if`, cálculos, reglas) en el `Controller`? → Moverla al `Service`.
- ¿Hay reglas de negocio en el `Repository`? → Moverlas al `Service`. El `Repository` solo debe saber cómo guardar y recuperar datos, no qué hacer con ellos.

Seguir estas reglas hace que tu código sea predecible: siempre sabes dónde buscar cuando algo falla.

---

## Decisión 5: datos en memoria en lugar de base de datos real

Para esta lección usamos una `List<Ticket>` en el `TicketRepository` como almacenamiento:

```java
List<Ticket> tickets = new ArrayList<>();
tickets.add(new Ticket(1L, "Ticket 1", "Ticket 1", "NEW"));
```

Esto no es una limitación técnica: es una decisión pedagógica intencional.

Si conectáramos una base de datos desde el primer día, el 80% del tiempo lo pasaríamos configurando drivers, credenciales, esquemas y conexiones, en lugar de aprender la arquitectura en sí. Al usar memoria, el foco es completamente la separación de responsabilidades.

Además, la decisión tiene una ventaja arquitectónica: cuando más adelante conectes JPA y PostgreSQL, **solo modificarás el `TicketRepository`**. El `TicketService` y el `TicketController` no necesitarán cambiar, porque no saben (ni deben saber) dónde viven los datos.

> **Importante:** los datos en memoria se pierden al reiniciar la aplicación. Eso es esperado por ahora.

---

## Decisión 6: la configuración vive en archivos de configuración, no en el código

Actualmente el único parámetro configurado es:

```properties
spring.application.name=Tickets
```

La regla es simple: cualquier valor que pueda cambiar entre entornos (desarrollo, staging, producción) debe vivir en el archivo de configuración, **nunca hardcodeado en el código Java**.

Por ejemplo, el puerto de la aplicación puede ser diferente en cada ambiente. La forma correcta de cambiarlo es en `application.yaml`:

```yaml
server:
  port: 8081
  servlet:
    context-path: /tickets-app
```

Si ese valor estuviera escrito directamente en el `Controller`, tendrías que modificar y recompilar el código fuente cada vez que cambias de ambiente. Eso es un error grave en cualquier proyecto profesional.

> **Pendiente:** migrar a `application.yaml` y agregar configuraciones de puerto, context path y banner personalizado.

---

## Criterio de calidad que te acompaña en el curso

> "Poco alcance, buena forma."

En este curso preferimos que construyas un único endpoint perfectamente estructurado antes que cinco endpoints desorganizados. La forma profesional se aprende desde el primer día, no "cuando el proyecto crezca".
