# Lección 04 - Actividad individual: recurso `users`

Ahora es tu turno. Esta actividad replica lo que construiste con `Ticket`, pero esta vez para un recurso `User`. El objetivo es que apliques el patrón CSR de forma autónoma, tomando las mismas decisiones de diseño que aprendiste.

> Si no estuviste en clase, lee primero el tutorial paso a paso (`02_guion_paso_a_paso.md`) y el documento de decisiones de diseño (`03_decisiones_rest_y_csr.md`) antes de comenzar esta actividad.

---

## ¿Qué vas a construir?

Un microservicio independiente para gestionar usuarios, con la misma estructura por capas que el proyecto `Tickets`. El entregable mínimo es un único endpoint:

```
GET /users
```

Que devuelve una lista JSON de usuarios cargados en memoria.

---

## Restricciones de la actividad

Estas restricciones no son caprichosas: están pensadas para que practiques exactamente lo que se evaluará.

| Restricción | Por qué |
|---|---|
| Usar el patrón CSR con paquetes separados | Es el núcleo de esta lección |
| Usar `List` para persistencia temporal | No usamos BD todavía; el foco es la arquitectura |
| No implementar CRUD completo | Primero estructura, después alcance |
| No hardcodear el puerto en el código Java | La configuración vive en archivos de configuración, nunca en el código |

---

## Modelo sugerido

Crea la clase `User` en el paquete `model`. Usa Lombok para no escribir getters, setters ni constructores manualmente:

```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
```

> **¿Por qué `name` y `email` en inglés?** Seguimos la misma convención que en `Ticket`: los identificadores de código en inglés. Si los datos que el usuario ve en pantalla deben estar en español, eso se maneja en la capa de presentación, no en el modelo.

---

## Guía de implementación

Sigue exactamente el mismo orden que en el tutorial de tickets:

### 1. Crea el paquete y la clase `User`

Campos: `id` (`Long`), `name` (`String`), `email` (`String`) con las anotaciones Lombok.

### 2. Crea `UserRepository`

- Anótala con `@Repository`
- Inicializa una `List<User>` en el constructor con al menos 2 usuarios de prueba
- Crea el método `getAll()` que retorne la lista

### 3. Crea `UserService`

- Anótala con `@Service`
- Recibe `UserRepository` por constructor (inyección de dependencias)
- Crea el método `getUsers()` que llame a `repository.getAll()`

### 4. Crea `UserController`

- Anótalo con `@RestController` y `@RequestMapping("/users")`
- Recibe `UserService` por constructor
- Crea el método `getAllUsers()` con `@GetMapping` que retorne `this.service.getUsers()`

### 5. Prueba el endpoint

Levanta la aplicación y haz una petición `GET http://localhost:8080/users` en Postman o Insomnia. Deberías recibir:

```json
[
  { "id": 1, "name": "...", "email": "..." },
  { "id": 2, "name": "...", "email": "..." }
]
```

---

## ¿Cómo sé si lo hice bien?

### Logro alto

- Los cuatro paquetes existen: `controller`, `service`, `repository`, `model`
- `UserController` no tiene ninguna lista ni lógica de negocio: solo llama al `service`
- El endpoint `GET /users` responde `200 OK` con JSON válido
- El método del controlador se llama `getAllUsers()` y la URL es `/users` (sin verbos)
- Puedes explicar en voz alta por qué cada clase está en su paquete

### Logro medio

- La estructura CSR existe pero hay alguna mezcla menor (por ejemplo, lógica simple en el `Controller`)
- El endpoint funciona pero el nombre de la URL o del método no sigue las convenciones
- La respuesta JSON es correcta, pero no puedes justificar las decisiones tomadas

### Logro inicial

- El endpoint funciona, pero todo (o casi todo) está en una sola clase
- La URL contiene verbos o no sigue convenciones REST
- No hay separación clara entre lo que hace cada capa

---

## Extensión opcional: si terminas antes

Si completaste todo lo anterior y quieres un desafío adicional:

### Opción A: buscar por ID

Agrega un endpoint que devuelva un usuario por su `id`:

```
GET /users/{id}
```

- Si el usuario existe, devuelve `200 OK` con el objeto JSON
- Si el usuario **no** existe, devuelve `404 Not Found`

Piensa en qué capa va la lógica de búsqueda antes de escribir el código. Pista: `Optional<User>` puede ser útil aquí.

### Opción B: configurar la aplicación

Aunque es una tarea pendiente, puedes practicar creando un `application.yaml` con:

```yaml
server:
  port: 8082
  servlet:
    context-path: /users-app
```

Y creando `src/main/resources/banner.txt` con el nombre de tu proyecto. Recuerda que si agregas el `context-path`, tu endpoint quedaría en `GET /users-app/users`.

---

## Antes de entregar: pregúntate esto

1. ¿Puedo tocar solo `UserRepository` para cambiar la fuente de datos (de `List` a base de datos) sin modificar `UserService` ni `UserController`?
2. Si un compañero abre mi proyecto, ¿entiende a simple vista dónde vive cada responsabilidad?
3. ¿Mi URL (`/users`) describe un recurso o una acción?

Si las tres respuestas son "sí", completaste el objetivo de esta actividad.
