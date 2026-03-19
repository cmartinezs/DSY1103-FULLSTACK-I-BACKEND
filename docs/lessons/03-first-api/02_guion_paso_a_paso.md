# Lección 03 - Tutorial paso a paso: tu primera API con Spring Boot

Sigue esta guía en orden. Cada paso explica qué vas a hacer y **por qué lo hacemos así**. No copies y pegues sin leer: el objetivo es que entiendas cada decisión.

---

## Paso 1: crear el proyecto con IntelliJ IDEA

Abre IntelliJ IDEA y sigue estos pasos:

1. Ve a **File → New → Project...**
2. En el panel izquierdo selecciona **Spring Boot** (o "Spring Initializr")
3. Configura el proyecto con los siguientes valores:

| Campo | Valor |
|---|---|
| Name | `Greetings` |
| Location | La carpeta donde quieras guardar el proyecto |
| Language | Java |
| Type | Maven |
| Group | `cl.duoc.fullstack` |
| Artifact | `greetings` |
| Package name | `cl.duoc.fullstack.greetings` |
| Java | 21 |

4. Haz clic en **Next**
5. Selecciona las siguientes dependencias:
   - ✅ **Spring Web** (en la categoría "Web")
   - ✅ **Lombok** (en la categoría "Developer Tools")
   - ✅ **Spring Boot DevTools** (en la categoría "Developer Tools")
6. Haz clic en **Create**

IntelliJ descargará la estructura base del proyecto y lo abrirá automáticamente.

> **¿Qué acaba de pasar?** IntelliJ se conectó a [start.spring.io](https://start.spring.io) y generó por ti una estructura de proyecto Maven con Spring Boot preconfigurado. Antes de esta herramienta, configurar todo eso manualmente tomaba horas.

---

## Paso 2: entender la estructura del proyecto

Antes de escribir código, dedica unos minutos a explorar lo que se generó. Abre el panel de archivos de IntelliJ y verás algo así:

```
Greetings/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── cl/duoc/fullstack/greetings/
│   │   │       └── GreetingsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── cl/duoc/fullstack/greetings/
│               └── GreetingsApplicationTests.java
├── pom.xml
└── mvnw
```

### `pom.xml` — el contrato del proyecto

Este archivo le dice a Maven (el gestor de dependencias de Java):
- Qué librerías necesita el proyecto (`<dependencies>`)
- Con qué versión de Java compilar (`<java.version>21</java.version>`)
- Qué plugins usar al construir el proyecto

Cada vez que agregas una dependencia, la declaras aquí. Maven la descarga automáticamente de internet la primera vez.

> **Analogía:** el `pom.xml` cumple el mismo rol que el `package.json` en proyectos Node.js: describe el proyecto y sus dependencias.

### `GreetingsApplication.java` — el punto de entrada

```java
@SpringBootApplication
public class GreetingsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreetingsApplication.class, args);
    }
}
```

Esta clase es el punto de arranque de toda la aplicación. Cuando ejecutas el proyecto, Java busca el método `main` y lo llama. Ese `main` arranca Spring Boot, que a su vez:

1. Escanea todos los paquetes en busca de clases anotadas (`@RestController`, `@Service`, `@Repository`, etc.)
2. Configura el servidor HTTP embebido (Tomcat, por defecto)
3. Levanta el servidor en el puerto `8080`

La anotación `@SpringBootApplication` es un atajo que combina tres anotaciones:
- `@SpringBootConfiguration` — marca esta clase como fuente de configuración
- `@EnableAutoConfiguration` — activa la configuración automática de Spring
- `@ComponentScan` — le dice a Spring que escanee el paquete actual y todos sus subpaquetes

> **Importante:** nunca borres ni muevas esta clase. Si la mueves a otro paquete, Spring podría dejar de encontrar tus controllers.

### `application.properties` — la configuración de la aplicación

Este archivo controla el comportamiento de Spring Boot sin tocar el código Java. Por ahora solo tiene:

```properties
spring.application.name=Greetings
```

Aquí podrías, por ejemplo, cambiar el puerto:

```properties
server.port=8081
```

O agregar un prefijo global a todas las rutas:

```properties
server.servlet.context-path=/api
```

> **Regla de oro:** cualquier valor que pueda cambiar entre entornos (desarrollo, producción) vive aquí, nunca hardcodeado en el código Java.

### `mvnw` — el wrapper de Maven

Es un script que permite ejecutar Maven sin tenerlo instalado globalmente. Desde la terminal puedes usar:

```bash
./mvnw spring-boot:run    # levanta la aplicación
./mvnw test               # ejecuta los tests
./mvnw package            # compila y empaqueta en un .jar
```

---

## Paso 3: crear el paquete `controller`

Antes de escribir el controlador, crea el paquete donde va a vivir.

En IntelliJ:
1. Haz clic derecho sobre el paquete `cl.duoc.fullstack.greetings`
2. Selecciona **New → Package**
3. Escribe `controller` y presiona Enter

Verás que se crea la carpeta `controller/` dentro del paquete principal.

> **¿Por qué un paquete separado?** En Java, los paquetes son más que carpetas: comunican intención. Al poner tu controlador en un paquete llamado `controller`, cualquier desarrollador que abra el proyecto sabe inmediatamente qué hace esa clase. Es un lenguaje común del ecosistema Java.

---

## Paso 4: crear la clase `GreetingsController`

Haz clic derecho sobre el paquete `controller` recién creado:
1. Selecciona **New → Java Class**
2. Escribe `GreetingsController` y presiona Enter

Escribe el siguiente código:

```java
package cl.duoc.fullstack.greetings.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greetings")
public class GreetingsController {

    @GetMapping
    public String greet() {
        return "Hola";
    }
}
```

Eso es todo el código que necesitas. Vamos parte por parte.

---

## Paso 5: entender cada parte del controlador

### La clase: `GreetingsController`

```java
public class GreetingsController { ... }
```

Una clase Java normal. El nombre no tiene ningún significado especial para Spring: podría llamarse de cualquier forma. La convención es terminar con `Controller` para que la intención quede clara.

---

### `@RestController`

```java
@RestController
public class GreetingsController { ... }
```

Esta anotación le dice a Spring dos cosas al mismo tiempo:

1. **Esta clase es un controlador HTTP** — Spring la registrará y comenzará a escuchar peticiones a través de ella
2. **Las respuestas se serializan directamente** — lo que retorne cada método se convierte automáticamente en el cuerpo de la respuesta HTTP

Internamente, `@RestController` es la combinación de:
- `@Controller` — registra la clase como manejador de peticiones web
- `@ResponseBody` — hace que el valor de retorno del método sea el cuerpo de la respuesta, no el nombre de una vista HTML

> **¿Por qué existe `@RestController` y no solo `@Controller`?** El `@Controller` original de Spring fue diseñado para aplicaciones que devuelven páginas HTML (vistas). Con `@RestController`, en cambio, lo que el método retorna va directo al cuerpo de la respuesta HTTP. Para una API REST, siempre usarás `@RestController`.

---

### `@RequestMapping("/greetings")`

```java
@RequestMapping("/greetings")
public class GreetingsController { ... }
```

Esta anotación define el **prefijo de URL** para todos los endpoints de esta clase. En este caso, todos los métodos de `GreetingsController` responderán bajo la ruta `/greetings`.

Cuando Spring arranca, lee esta anotación y registra internamente: _"cualquier petición HTTP que llegue a una URL que comience con `/greetings` debe ser manejada por esta clase"_.

> **¿Se puede poner `@RequestMapping` solo en el método y no en la clase?** Sí. Pero ponerlo en la clase permite agrupar todos los endpoints relacionados bajo una misma raíz de URL. Si mañana decides cambiar `/greetings` a `/saludos`, solo cambias una línea (la anotación de la clase) y todos los endpoints se actualizan automáticamente.

---

### El método: `greet()`

```java
public String greet() {
    return "Hola";
}
```

Un método Java normal que retorna un `String`. El nombre `greet` es una convención descriptiva: podría llamarse `hello`, `sayHi` o cualquier cosa, pero debe comunicar lo que hace.

Lo que este método retorna (`"Hola"`) se convierte en el **cuerpo de la respuesta HTTP**. Gracias a `@RestController`, Spring toma ese `String` y lo escribe directamente en la respuesta.

---

### `@GetMapping`

```java
@GetMapping
public String greet() { ... }
```

Esta anotación le dice a Spring que este método responde a peticiones HTTP con el método `GET`.

Combinado con el `@RequestMapping("/greetings")` de la clase, el resultado es:

```
GET /greetings → greet()
```

Cuando alguien hace una petición `GET` a la URL `/greetings`, Spring ejecuta este método y envía `"Hola"` como respuesta.

> **¿Por qué `@GetMapping` y no `@RequestMapping(method = RequestMethod.GET)`?** Ambas hacen exactamente lo mismo. `@GetMapping` es un atajo más conciso introducido en Spring 4.3. Lo mismo aplica para `@PostMapping`, `@PutMapping`, `@DeleteMapping`, etc.

---

## Paso 6: levantar la aplicación

Tienes dos formas de ejecutar el proyecto:

**Opción A — desde IntelliJ:**
Haz clic en el botón ▶ (play) verde que aparece junto al método `main` en `GreetingsApplication.java`, o usa el botón de run en la barra de herramientas.

**Opción B — desde la terminal:**
```bash
./mvnw spring-boot:run
```

En ambos casos verás en la consola un mensaje similar a este:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.0.3)

Started GreetingsApplication in 1.823 seconds (process running for 2.1)
```

Esa última línea confirma que el servidor está corriendo. El tiempo varía, pero debería ser menos de 5 segundos.

> **DevTools en acción:** gracias a la dependencia `spring-boot-devtools` que agregaste, si modificas y guardas cualquier archivo Java, Spring reinicia automáticamente la aplicación. No necesitas detenerla y volver a levantarla manualmente cada vez que cambias código.

---

## Paso 7: probar el endpoint

Tienes tres formas de probar el endpoint:

### Opción A — navegador web

Abre tu navegador y escribe en la barra de direcciones:

```
http://localhost:8080/greetings
```

Verás la palabra `Hola` en la pantalla. El navegador hace automáticamente una petición `GET` a esa URL.

### Opción B — Postman o Insomnia

1. Crea una nueva petición
2. Selecciona el método `GET`
3. Ingresa la URL: `http://localhost:8080/greetings`
4. Haz clic en **Send**

Deberías ver:
- **Status:** `200 OK`
- **Body:** `Hola`

### Opción C — curl desde la terminal

```bash
curl http://localhost:8080/greetings
```

Salida esperada:

```
Hola
```

---

## Paso 8: entender el flujo completo

Cuando escribes `http://localhost:8080/greetings` y presionas Enter, esto es lo que ocurre:

```
1. Tu navegador arma una petición HTTP:
   GET /greetings HTTP/1.1
   Host: localhost:8080

2. La petición viaja por la red (en este caso, en tu misma máquina)
   hasta el puerto 8080.

3. Tomcat (el servidor HTTP embebido de Spring Boot) la recibe.

4. Spring busca en su registro interno qué clase/método maneja
   "GET /greetings" → encuentra GreetingsController.greet()

5. Spring llama al método greet()

6. El método retorna el String "Hola"

7. Spring convierte ese String en el cuerpo de la respuesta HTTP:
   HTTP/1.1 200 OK
   Content-Type: text/plain;charset=UTF-8
   Content-Length: 4

   Hola

8. La respuesta viaja de vuelta a tu navegador, que muestra "Hola".
```

Este flujo —petición → servidor → controller → respuesta— es la base de todo lo que construirás en el curso.

---

## Paso 9: reflexiona antes de cerrar

Antes de pasar a la siguiente sección, respóndete estas preguntas:

1. ¿Qué pasaría si cambias `@GetMapping` por `@PostMapping` y vuelves a probar desde el navegador? ¿Por qué?
2. ¿Qué pasa si cambias `"/greetings"` en `@RequestMapping` por `"/hello"` y vuelves a probar la URL anterior?
3. ¿Qué rol cumple la clase `GreetingsApplication` en todo el proceso? ¿Qué pasaría si la borraras?

Si puedes responder estas tres preguntas con seguridad, entendiste el objetivo de este paso.

