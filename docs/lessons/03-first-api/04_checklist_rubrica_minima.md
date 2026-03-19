# Lección 03 - Lista de verificación: ¿llegué al mínimo requerido?

Usa esta lista para revisar tu propio trabajo antes de presentarlo. Cada ítem explica qué significa y cómo verificarlo.

---

## ¿Qué es un indicador de evaluación (IE)?

Los indicadores de evaluación son los criterios concretos con los que se mide tu aprendizaje. En esta lección el foco está en que **el endpoint funcione y puedas explicar cada parte**, no en la cantidad de código que escribiste.

---

## IE 1.1.1 - Proyecto Spring Boot creado y ejecutable

Este indicador verifica que puedes crear un proyecto Spring Boot desde cero y levantarlo correctamente.

Checklist:

- [ ] El proyecto fue creado con IntelliJ IDEA usando Spring Initializr
- [ ] Las dependencias están declaradas en `pom.xml`: `spring-boot-starter-web`, `lombok`, `spring-boot-devtools`
- [ ] La aplicación levanta sin errores (no hay excepciones en la consola al iniciar)
- [ ] La consola muestra el mensaje `Started GreetingsApplication in X seconds`

**Cómo verificarlo:** ejecuta el proyecto y revisa la consola. Si ves el mensaje de inicio sin líneas en rojo (`ERROR`), el proyecto está correcto.

> **Problema común:** si la aplicación no levanta y ves un error como `Port 8080 was already in use`, significa que ya hay otro proceso usando ese puerto. Puedes detenerlo o cambiar el puerto en `application.properties` con `server.port=8081`.

---

## IE 1.1.2 - Endpoint `GET /greetings` funcionando

Este indicador verifica que el endpoint existe, responde al método HTTP correcto y devuelve el valor esperado.

Checklist:

- [ ] La URL `GET http://localhost:8080/greetings` responde con código `200 OK`
- [ ] El cuerpo de la respuesta contiene exactamente `Hola`
- [ ] La URL `POST http://localhost:8080/greetings` responde con `405 Method Not Allowed`
- [ ] La URL `GET http://localhost:8080/hola` responde con `404 Not Found`

**Cómo verificarlo:** usa Postman, Insomnia o el navegador para probar el endpoint. En Postman verás el código de estado en la parte superior derecha de la respuesta.

---

## IE 1.1.3 - Uso correcto de las anotaciones de Spring

Este indicador verifica que sabes qué hace cada anotación y la estás usando correctamente.

Checklist:

- [ ] La clase `GreetingsController` está anotada con `@RestController`
- [ ] La clase tiene `@RequestMapping("/greetings")` con la URL en minúsculas
- [ ] El método `greet()` está anotado con `@GetMapping`
- [ ] Las tres anotaciones están importadas desde `org.springframework.web.bind.annotation`
- [ ] No hay lógica de negocio en el controlador (solo retorna el saludo directamente)

**Cómo verificarlo:** abre `GreetingsController.java` y revisa que cada anotación esté presente. IntelliJ subraya en rojo las anotaciones que no reconoce o que faltan imports.

> **Importante:** si copias una anotación sin el import, IntelliJ no la reconocerá. Usa `Alt+Enter` sobre la anotación subrayada para que IntelliJ agregue el import automáticamente.

---

## IE 1.1.4 - Comprensión del flujo HTTP

Este indicador no se verifica con código: se verifica con tu capacidad de explicar lo que ocurre.

Checklist (para responderte mentalmente o en voz alta):

- [ ] Puedo explicar qué es una petición HTTP y qué contiene (método, ruta, cabeceras, cuerpo)
- [ ] Puedo explicar qué es una respuesta HTTP y qué contiene (código de estado, cabeceras, cuerpo)
- [ ] Puedo explicar por qué la URL es `localhost:8080` y no otra cosa
- [ ] Puedo explicar cómo Spring sabe que `GET /greetings` debe ejecutar `GreetingsController.greet()`
- [ ] Puedo decir qué pasa si hago `GET /hola` (404) y por qué
- [ ] Puedo decir qué pasa si hago `POST /greetings` (405) y por qué

**Cómo verificarlo:** explícalo a un compañero o escríbelo en tus propias palabras. Si no puedes explicarlo sin leer el código, necesitas repasar la sección `03_como_funciona_http.md`.

---

## Estructura mínima esperada del proyecto

```
src/
└── main/
    ├── java/cl/duoc/fullstack/greetings/
    │   ├── GreetingsApplication.java      ← no se modifica
    │   └── controller/
    │       └── GreetingsController.java   ← lo que creaste
    └── resources/
        └── application.properties
```

Si tu `GreetingsController` está directamente en el paquete raíz (sin la carpeta `controller`), el código puede funcionar, pero no sigue las convenciones del ecosistema Java. Mueve la clase al paquete correcto.

---

## Indicadores que se trabajan en lecciones siguientes

Los siguientes indicadores están en el horizonte del curso. No se evalúan en esta lección, pero es útil saber hacia dónde vamos:

| Indicador | Qué cubre |
|---|---|
| IE 1.2.1 | Separación de responsabilidades (Controller / Service / Repository) |
| IE 1.2.2 | Modelo de datos y persistencia en memoria |
| IE 1.1.2 | Diseño de endpoints REST (sustantivos, plural, verbos HTTP correctos) |
| IE 1.1.3 | Respuestas JSON y control de códigos HTTP con `ResponseEntity` |

---

## ¿Completé el mínimo de esta lección?

Puedes decir que completaste esta lección si:

- ✅ El proyecto levanta sin errores
- ✅ `GET http://localhost:8080/greetings` devuelve `200 OK` con cuerpo `Hola`
- ✅ Puedes explicar en tus propias palabras qué hace cada anotación (`@RestController`, `@RequestMapping`, `@GetMapping`)
- ✅ Puedes describir el flujo desde que el navegador envía la petición hasta que recibe la respuesta

