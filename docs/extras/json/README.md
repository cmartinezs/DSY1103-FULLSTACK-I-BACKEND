# 📦 JSON — JavaScript Object Notation

## ¿Qué es JSON?

**JSON** (JavaScript Object Notation) es un formato de **intercambio de datos** ligero, legible por humanos y fácilmente procesable por máquinas. Es el estándar de facto para comunicar datos entre un cliente y un servidor en APIs REST.

> 📌 Aunque nació en el ecosistema JavaScript, JSON es **independiente del lenguaje**: Java, Python, Go, PHP y prácticamente todos los lenguajes modernos lo soportan de forma nativa o mediante librerías.

---

## Tipos de datos en JSON

JSON soporta exactamente **6 tipos de datos**:

| Tipo | Descripción | Ejemplo |
|------|-------------|---------|
| `string` | Texto entre comillas dobles | `"ABIERTO"` |
| `number` | Entero o decimal (sin comillas) | `42`, `3.14` |
| `boolean` | Verdadero o falso | `true`, `false` |
| `null` | Ausencia de valor | `null` |
| `object` | Par clave-valor entre `{}` | `{ "id": 1 }` |
| `array` | Lista ordenada entre `[]` | `[1, 2, 3]` |

---

## Sintaxis

### Objeto JSON

Un **objeto** es una colección de pares `"clave": valor` entre llaves `{}`. Las claves **siempre van entre comillas dobles**.

```json
{
  "id": 1,
  "title": "Error en login",
  "description": "El usuario no puede iniciar sesión",
  "status": "ABIERTO",
  "priority": 3,
  "resolved": false,
  "resolvedAt": null
}
```

### Array JSON

Un **array** es una lista ordenada de valores entre corchetes `[]`. Puede contener cualquier tipo de dato, incluyendo objetos.

```json
[
  {
    "id": 1,
    "title": "Error en login",
    "status": "ABIERTO"
  },
  {
    "id": 2,
    "title": "Botón roto en checkout",
    "status": "CERRADO"
  }
]
```

### Anidamiento

Los objetos y arrays se pueden anidar libremente:

```json
{
  "id": 1,
  "title": "Error en login",
  "assignee": {
    "id": 42,
    "name": "Ana García",
    "email": "ana@empresa.com"
  },
  "tags": ["auth", "producción", "urgente"],
  "history": [
    { "status": "ABIERTO", "date": "2026-03-01" },
    { "status": "EN_PROGRESO", "date": "2026-03-05" }
  ]
}
```

---

## Reglas de sintaxis

| Regla | Correcto ✅ | Incorrecto ❌ |
|-------|------------|--------------|
| Claves entre comillas dobles | `"title": "texto"` | `title: "texto"` |
| Strings entre comillas dobles | `"valor"` | `'valor'` |
| Sin coma en el último elemento | `{ "a": 1, "b": 2 }` | `{ "a": 1, "b": 2, }` |
| Sin comentarios | — | `// esto falla` |
| `null` en minúsculas | `null` | `NULL`, `Null` |
| Booleanos en minúsculas | `true`, `false` | `True`, `False` |

---

## JSON en Spring Boot — Jackson

Spring Boot incluye **Jackson** como librería de serialización/deserialización JSON de forma automática. Cuando un `@RestController` retorna un objeto Java, Jackson lo convierte a JSON; cuando recibe JSON en el cuerpo de una petición, Jackson lo convierte al objeto Java correspondiente.

### Serialización: Java → JSON

```java
// Clase Java (modelo)
@Getter
@AllArgsConstructor
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
}

// Controlador
@GetMapping("/{id}")
public ResponseEntity<Ticket> getById(@PathVariable Long id) {
    Ticket ticket = service.findById(id);
    return ResponseEntity.ok(ticket);  // Jackson serializa el objeto
}
```

**JSON resultante en la respuesta HTTP:**

```json
{
  "id": 1,
  "title": "Error en login",
  "description": "El usuario no puede iniciar sesión",
  "status": "ABIERTO"
}
```

### Deserialización: JSON → Java

```java
// El cuerpo de la petición POST llega como JSON:
// { "title": "Nuevo ticket", "description": "...", "status": "ABIERTO" }

@PostMapping
public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
    // Jackson deserializa el JSON al objeto Ticket automáticamente
    Ticket created = service.create(ticket);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

---

## Anotaciones Jackson más usadas

### `@JsonProperty` — Renombrar campos

```java
public class Ticket {
    @JsonProperty("ticket_id")   // en JSON aparece como "ticket_id"
    private Long id;

    @JsonProperty("ticket_title")
    private String title;
}
```

**JSON resultante:**
```json
{
  "ticket_id": 1,
  "ticket_title": "Error en login"
}
```

### `@JsonIgnore` — Excluir un campo del JSON

```java
public class Usuario {
    private String email;

    @JsonIgnore           // nunca se incluye en la respuesta JSON
    private String password;
}
```

### `@JsonInclude` — Omitir valores nulos

```java
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)  // omite campos con valor null
public class Ticket {
    private Long id;
    private String title;
    private String resolvedAt;  // si es null, no aparece en el JSON
}
```

### `@JsonFormat` — Formato de fechas

```java
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Ticket {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
```

**JSON resultante:**
```json
{
  "createdAt": "2026-03-19 10:30:00"
}
```

---

## Manejo global con `application.properties`

Puedes configurar el comportamiento de Jackson globalmente en Spring Boot:

```properties
# No incluir campos con valor null en las respuestas
spring.jackson.default-property-inclusion=non_null

# Formato de fechas ISO 8601
spring.jackson.serialization.write-dates-as-timestamps=false

# Indentar el JSON de respuesta (útil en desarrollo)
spring.jackson.serialization.indent-output=true

# No fallar si el JSON tiene campos desconocidos
spring.jackson.deserialization.fail-on-unknown-properties=false
```

---

## JSON válido vs inválido — Ejemplos

```json
// ✅ JSON válido
{
  "id": 1,
  "title": "Error crítico",
  "tags": ["urgente", "producción"],
  "resolved": false
}
```

```json
// ❌ JSON inválido — coma final
{
  "id": 1,
  "title": "Error crítico",
}
```

```json
// ❌ JSON inválido — comillas simples
{
  'id': 1,
  'title': 'Error crítico'
}
```

---

## Herramientas útiles

| Herramienta | Uso | Enlace |
|-------------|-----|--------|
| JSONLint | Validar JSON online | [jsonlint.com](https://jsonlint.com/) |
| JSON Formatter | Formatear y visualizar JSON | [jsonformatter.curiousconcept.com](https://jsonformatter.curiousconcept.com/) |
| Postman | Enviar/recibir JSON en peticiones HTTP | [postman.com](https://www.postman.com/) |
| json.org | Especificación oficial | [json.org/json-es.html](https://www.json.org/json-es.html) |
| Jackson Docs | Documentación de Jackson | [github.com/FasterXML/jackson](https://github.com/FasterXML/jackson) |

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| Introducción a JSON (MDN) | 📄 Artículo | [developer.mozilla.org/es/docs/Learn/JavaScript/Objects/JSON](https://developer.mozilla.org/es/docs/Learn/JavaScript/Objects/JSON) |
| Jackson en Spring Boot (Baeldung) | 📄 Artículo | [baeldung.com/jackson](https://www.baeldung.com/jackson) |
| Especificación JSON oficial | 📖 RFC 8259 | [datatracker.ietf.org/doc/html/rfc8259](https://datatracker.ietf.org/doc/html/rfc8259) |

---

*[← Volver a Extras](../README.md)*

