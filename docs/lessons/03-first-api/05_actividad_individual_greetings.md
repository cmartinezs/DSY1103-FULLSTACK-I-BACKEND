# Lección 03 - Actividad individual: agrega tu propio saludo

Ahora es tu turno. Esta actividad extiende lo que construiste en clase con `GET /greetings`, pero ahora tomas tus propias decisiones de diseño.

> Si no estuviste en clase, lee primero el tutorial paso a paso (`02_guion_paso_a_paso.md`) y la explicación de HTTP (`03_como_funciona_http.md`) antes de comenzar.

---

## ¿Qué vas a construir?

Vas a agregar un segundo endpoint al mismo controlador `GreetingsController`. El endpoint debe responder a:

```
GET /greetings/formal
```

Y retornar la cadena:

```
Buenos días
```

---

## Restricciones de la actividad

| Restricción | Por qué |
|---|---|
| Usar el mismo `GreetingsController`, no crear otro | Un controlador agrupa los endpoints del mismo recurso |
| Solo `@GetMapping` con una ruta (`"/formal"`) | Practica la combinación de `@RequestMapping` de clase + `@GetMapping` de método |
| El método debe llamarse `formalGreet()` | Los nombres de métodos deben ser descriptivos |
| No modificar el endpoint `GET /greetings` existente | El nuevo endpoint es una adición, no un reemplazo |

---

## Guía de implementación

### 1. Abre `GreetingsController.java`

El archivo está en:
```
src/main/java/cl/duoc/fullstack/greetings/controller/GreetingsController.java
```

### 2. Agrega el nuevo método

Dentro de la clase, junto al método `greet()` existente, escribe:

```java
@GetMapping("/formal")
public String formalGreet() {
    return "Buenos días";
}
```

La clase completa debería verse así:

```java
@RestController
@RequestMapping("/greetings")
public class GreetingsController {

    @GetMapping
    public String greet() {
        return "Hola";
    }

    @GetMapping("/formal")
    public String formalGreet() {
        return "Buenos días";
    }
}
```

### 3. Levanta (o recarga) la aplicación

Si DevTools está activo, la aplicación se recargará sola al guardar. Si no, detén y vuelve a levantar el servidor.

### 4. Prueba ambos endpoints

Verifica que **ambos endpoints** funcionen correctamente:

| Petición | Respuesta esperada | Código esperado |
|---|---|---|
| `GET http://localhost:8080/greetings` | `Hola` | `200 OK` |
| `GET http://localhost:8080/greetings/formal` | `Buenos días` | `200 OK` |

---

## ¿Cómo sé si lo hice bien?

### Logro alto

- Ambos endpoints responden correctamente
- Puedes explicar por qué `@GetMapping("/formal")` genera la URL `/greetings/formal` (y no solo `/formal`)
- Puedes explicar qué pasaría si moverías el método a una clase diferente sin `@RequestMapping("/greetings")`
- El código del controlador es limpio: sin lógica extra, sin comentarios innecesarios, sin código duplicado

### Logro medio

- Ambos endpoints responden correctamente
- La URL del nuevo endpoint funciona pero no puedes explicar el mecanismo de combinación de rutas
- Creaste una segunda clase controller en lugar de agregar el método a la existente

### Logro inicial

- Solo uno de los dos endpoints funciona
- La URL del endpoint no es correcta (por ejemplo, responde en `/formal` en vez de `/greetings/formal`)
- El código tiene errores de compilación o la aplicación no levanta

---

## Extensión opcional: si terminas antes

Si completaste la actividad principal y quieres un desafío adicional, prueba una o más de estas opciones:

### Opción A: cambiar el puerto

Sin tocar el código Java, haz que la aplicación corra en el puerto `9090` en lugar del `8080`. Verifica que el endpoint sigue funcionando en la nueva URL.

### Opción B: agregar un `context-path`

Configura un prefijo global `/api` para todas las rutas. Después de hacerlo, el endpoint debe responder en:

```
GET http://localhost:8080/api/greetings
```

> Pista: busca `server.servlet.context-path` en la documentación de Spring Boot.

---

## Antes de entregar: pregúntate esto

1. Si mañana necesitas agregar un saludo en otro idioma, ¿dónde agregarías el código? ¿Por qué?
2. Si el controlador tuviera `@RequestMapping("/api/greetings")`, ¿qué URL tendría el método `formalGreet()`?
3. ¿Cuál es la diferencia entre `@RequestMapping` en la clase y `@GetMapping` en el método? ¿Pueden intercambiarse?

Si puedes responder estas tres preguntas, completaste el objetivo de esta actividad.




