# Módulo 09 — Entrada y salida (I/O)

> **Objetivo:** entender qué es la entrada/salida (I/O), por qué es una de las operaciones más costosas en un sistema, y conocer las formas esenciales en que un programa se comunica con el mundo exterior.

---

## ¿Qué es I/O?

**I/O** (*Input/Output*, Entrada/Salida) es cualquier operación mediante la cual un programa **recibe o envía datos** hacia o desde el mundo exterior a la memoria RAM del proceso. "Mundo exterior" incluye:

- La consola (teclado y pantalla)
- El sistema de archivos (disco)
- La red (HTTP, sockets, bases de datos)
- Otros procesos o dispositivos (sensores, impresoras, etc.)

> **Por qué importa:** la CPU puede ejecutar miles de millones de instrucciones por segundo. Leer un archivo del disco, hacer una consulta a la base de datos o esperar una respuesta HTTP puede tardar millones de veces más. El I/O es el cuello de botella más común en aplicaciones backend.

---

## Índice

1. [I/O por consola](#1-io-por-consola)
2. [I/O de archivos](#2-io-de-archivos)
3. [I/O de red](#3-io-de-red)
4. [Streams — el concepto unificador](#4-streams--el-concepto-unificador)
5. [I/O bloqueante vs no bloqueante](#5-io-bloqueante-vs-no-bloqueante)
6. [I/O en el contexto backend](#6-io-en-el-contexto-backend)
7. [Tabla resumen](#7-tabla-resumen)
8. [📚 Literatura recomendada](#-literatura-recomendada)
9. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. I/O por consola

### 📖 Definición

La **consola** (terminal) es la forma más básica de interacción con el usuario: leer texto que escribe el usuario (stdin) y mostrar texto en pantalla (stdout/stderr).

### 🌐 Perspectiva universal

```python
# Python
nombre = input("¿Cuál es tu nombre? ")  # lee de consola
print(f"Hola, {nombre}")                 # escribe en consola
```
```javascript
// Node.js
const readline = require('readline');
const rl = readline.createInterface({ input: process.stdin });
rl.question('¿Cuál es tu nombre? ', (nombre) => {
    console.log(`Hola, ${nombre}`);
});
```

### ☕ En Java

```java
import java.util.Scanner;

// Salida estándar (stdout)
System.out.println("Hola, mundo");          // con salto de línea
System.out.print("Sin salto");              // sin salto de línea
System.out.printf("Precio: $%,.2f%n", 9990.5); // formato → "Precio: $9,990.50"

// Salida de error (stderr — se muestra en rojo en muchos terminales)
System.err.println("ERROR: algo falló");

// Entrada estándar (stdin)
Scanner scanner = new Scanner(System.in);
System.out.print("Ingresa tu nombre: ");
String nombre = scanner.nextLine();          // lee una línea
System.out.print("Ingresa tu edad: ");
int edad = scanner.nextInt();                // lee un entero

System.out.printf("Hola %s, tienes %d años%n", nombre, edad);
scanner.close(); // siempre cerrar recursos
```

### ⚠️ `Scanner` en proyectos reales

`Scanner` con `System.in` es útil para aprender y para aplicaciones de consola. En aplicaciones web (Spring Boot), la entrada/salida ocurre a través de HTTP, no de la consola.

---

## 2. I/O de archivos

### 📖 Definición

Las operaciones de **archivos** permiten leer y escribir datos de forma **persistente** en el disco. A diferencia de la memoria RAM, los archivos sobreviven al apagado del programa.

### 🌐 Perspectiva universal

```python
# Python: leer un archivo
with open("datos.txt", "r") as archivo:
    contenido = archivo.read()

# Python: escribir un archivo
with open("salida.txt", "w") as archivo:
    archivo.write("Hola mundo\n")
```
```javascript
// Node.js
const fs = require('fs');
const contenido = fs.readFileSync('datos.txt', 'utf8');
fs.writeFileSync('salida.txt', 'Hola mundo\n');
```

### ☕ En Java moderno (NIO.2)

Java tiene dos APIs para archivos:
- **API clásica** (`java.io.File`) — antigua, verbosa.
- **NIO.2** (`java.nio.file.Files`, `Path`) — moderna, recomendada desde Java 7.

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

// ── Leer ─────────────────────────────────────────────────────────────────────

// Leer todo el contenido como String
String contenido = Files.readString(Path.of("datos.txt")); // Java 11+

// Leer línea por línea como lista
List<String> lineas = Files.readAllLines(Path.of("datos.txt"));

// Leer línea por línea con Stream (eficiente para archivos grandes)
try (var stream = Files.lines(Path.of("datos.txt"))) {
    stream.filter(linea -> !linea.isBlank())
          .forEach(System.out::println);
}

// ── Escribir ──────────────────────────────────────────────────────────────────

// Escribir un String (sobrescribe si existe)
Files.writeString(Path.of("salida.txt"), "Hola mundo\n");

// Agregar contenido (sin sobrescribir)
Files.writeString(Path.of("salida.txt"), "Nueva línea\n", StandardOpenOption.APPEND);

// Escribir varias líneas
Files.write(Path.of("salida.txt"), List.of("línea 1", "línea 2", "línea 3"));

// ── Metadatos y operaciones ───────────────────────────────────────────────────
Path ruta = Path.of("datos.txt");

boolean existe  = Files.exists(ruta);
long    tamanio = Files.size(ruta);        // en bytes
Files.copy(ruta, Path.of("backup.txt"));
Files.delete(ruta);
Files.createDirectories(Path.of("carpeta/subcarpeta"));
```

### ⚠️ Siempre cerrar los recursos

Los recursos de I/O (streams, readers, connections) deben cerrarse para evitar fugas. Usa **try-with-resources**:

```java
// ✅ Try-with-resources cierra automáticamente el recurso al terminar (o si hay excepción)
try (BufferedReader reader = Files.newBufferedReader(Path.of("datos.txt"))) {
    String linea;
    while ((linea = reader.readLine()) != null) {
        System.out.println(linea);
    }
} // reader.close() se llama automáticamente aquí

// ❌ Sin try-with-resources: fácil olvidar el close()
BufferedReader reader = Files.newBufferedReader(Path.of("datos.txt"));
// ... si hay una excepción aquí, reader nunca se cierra → resource leak
reader.close();
```

---

## 3. I/O de red

### 📖 Definición

La **I/O de red** es la comunicación entre un programa y otro sistema a través de una red (LAN, internet). Incluye:

- Peticiones **HTTP** (la más común en backend)
- Comunicación con **bases de datos** (JDBC, JPA)
- **Sockets** (comunicación TCP/UDP de bajo nivel)
- Consumo de **APIs externas**

### 🌐 Perspectiva universal

```python
# Python: petición HTTP
import requests
respuesta = requests.get("https://api.ejemplo.com/usuarios")
datos = respuesta.json()
```
```javascript
// JavaScript (Node.js / browser)
const respuesta = await fetch("https://api.ejemplo.com/usuarios");
const datos = await respuesta.json();
```

### ☕ En Java

```java
// Java 11+: HttpClient moderno
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.ejemplo.com/usuarios"))
    .header("Accept", "application/json")
    .GET()
    .build();

HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

System.out.println("Status: " + response.statusCode());    // 200
System.out.println("Cuerpo: " + response.body());          // JSON como String

// En Spring Boot, se usa RestTemplate o WebClient (más moderno)
// RestTemplate
RestTemplate restTemplate = new RestTemplate();
String json = restTemplate.getForObject("https://api.ejemplo.com/usuarios", String.class);
```

---

## 4. Streams — el concepto unificador

### 📖 Definición

En I/O, un **stream** (flujo) es una **secuencia de datos que fluye de un origen a un destino**. Es el concepto unificador detrás de toda I/O: ya sea consola, archivo, red o memoria, todos se tratan como flujos de bytes.

> ⚠️ No confundir con los **Streams de Java 8** (para procesar colecciones). Aquí hablamos del concepto original de I/O streams.

```
Origen → [Stream de lectura] → Tu programa → [Stream de escritura] → Destino
```

### Jerarquía en Java

```
java.io
├── InputStream  (bytes de entrada: archivos, red, consola)
│   ├── FileInputStream
│   ├── BufferedInputStream (agrega buffer para eficiencia)
│   └── System.in
├── OutputStream (bytes de salida: archivos, red, consola)
│   ├── FileOutputStream
│   ├── BufferedOutputStream
│   └── System.out / System.err
├── Reader (caracteres de entrada — maneja encoding)
│   ├── FileReader
│   └── BufferedReader
└── Writer (caracteres de salida)
    ├── FileWriter
    └── BufferedWriter
```

### ☕ Ejemplo: BufferedReader (stream con buffer)

```java
// BufferedReader agrega un buffer para reducir las llamadas reales al disco
// Leer byte a byte del disco = muy lento
// Leer en bloques (buffer) y servir desde memoria = mucho más rápido
try (BufferedReader br = new BufferedReader(new FileReader("datos.txt"))) {
    String linea;
    while ((linea = br.readLine()) != null) { // lee del buffer en memoria
        System.out.println(linea);
    }
}
```

---

## 5. I/O bloqueante vs no bloqueante

### 📖 Definición

| | I/O Bloqueante (Blocking) | I/O No Bloqueante (Non-blocking) |
|--|--------------------------|----------------------------------|
| **Qué hace el hilo** | Espera hasta que el I/O termina | Registra la operación y sigue haciendo otras cosas |
| **Simplicidad** | Código simple y secuencial | Código más complejo (callbacks, async/await) |
| **Eficiencia** | Hilo ocioso mientras espera | Hilo aprovecha la espera |
| **Cuándo usarlo** | La mayoría de casos en backend tradicional | Alta concurrencia con muchas conexiones simultáneas |

```
I/O Bloqueante:
Hilo: [solicita datos] → [esperando...] → [procesa datos]
                              ↑ hilo bloqueado, desperdiciado

I/O No Bloqueante:
Hilo: [solicita datos] → [hace otras cosas] → [notificado] → [procesa datos]
                                 ↑ hilo útil mientras espera
```

### ☕ En Java

```java
// Bloqueante (tradicional) — Spring MVC por defecto
@GetMapping("/usuarios/{id}")
public Usuario obtener(@PathVariable Long id) {
    return repository.findById(id) // ← hilo bloqueado hasta que la DB responda
        .orElseThrow();
}

// No bloqueante (reactivo) — Spring WebFlux
@GetMapping("/usuarios/{id}")
public Mono<Usuario> obtener(@PathVariable Long id) {
    return repository.findById(id); // ← retorna inmediatamente; la DB llama de vuelta
}
```

---

## 6. I/O en el contexto backend

En una aplicación Spring Boot típica, la mayor parte del tiempo de ejecución se pasa haciendo I/O:

```
Request HTTP
    ↓
Controller (CPU — microsegundos)
    ↓
Service (CPU — microsegundos)
    ↓
Repository → Base de datos (I/O de red — milisegundos o más) ← EL CUELLO DE BOTELLA
    ↓
Response HTTP
```

### Buenas prácticas

| Práctica | Por qué |
|----------|---------|
| Usar **connection pools** (HikariCP en Spring Boot) | Reutilizar conexiones DB en lugar de abrir una nueva por request |
| Usar **paginación** en consultas grandes | No traer 1M de registros a memoria de una vez |
| **Cerrar** siempre los recursos (try-with-resources) | Evitar fugas de conexiones o descriptores de archivo |
| Leer archivos grandes con **streams** en lugar de `readAllBytes()` | No cargar todo en memoria |
| **Cachear** resultados de I/O costoso y repetitivo | Reducir llamadas a DB o APIs externas |
| **Validar** antes de escribir | Evitar operaciones de I/O innecesarias |

---

## 7. Tabla resumen

| Tipo de I/O | Origen/Destino | Clase Java | Velocidad relativa |
|-------------|----------------|------------|-------------------|
| **Consola** | Terminal | `System.in` / `System.out` / `Scanner` | Rápida |
| **Archivo** | Disco | `Files`, `BufferedReader/Writer` | Lenta (miles de µs) |
| **Base de datos** | Red + disco | JDBC, JPA/Hibernate | Lenta (ms) |
| **HTTP externo** | Red | `HttpClient`, `RestTemplate`, `WebClient` | Variable (ms a s) |
| **Memoria** | RAM | `ByteArrayInputStream/OutputStream` | Muy rápida |

### Regla de oro

```
Cuanto más lejos de la CPU → más lento
  CPU registers → L1/L2 cache → RAM → SSD → HDD → Red local → Internet
  (nanoseg)       (nanoseg)   (µseg) (µseg) (mseg)  (mseg)     (mseg a seg)
```

> 💡 Un desarrollador backend eficiente sabe que **minimizar, cachear y hacer eficiente el I/O** tiene más impacto en el rendimiento que cualquier micro-optimización de CPU.

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Effective Java** | Joshua Bloch | Intermedio | Ítems sobre I/O: usar try-with-resources, preferir NIO.2, y evitar resource leaks — directamente aplicables |
| **Java I/O, NIO and NIO.2** | Jeff Friesen | Intermedio | El libro más completo sobre el sistema de I/O de Java: streams, channels, buffers, file watching y async I/O |
| **Java Performance: The Definitive Guide** | Scott Oaks | Avanzado | Analiza el impacto del I/O en el rendimiento real de aplicaciones Java — connection pools, buffering y latencia |
| **Designing Data-Intensive Applications** | Martin Kleppmann | Avanzado | Aunque no es de Java, es la referencia del estado del arte en I/O de alto rendimiento: bases de datos, mensajería, streams en tiempo real |
| **Spring Boot in Action** | Craig Walls | Principiante / Intermedio | Cómo Spring Boot abstrae el I/O de red (HTTP, JDBC, JPA) — directamente aplicable al curso |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Oracle — Java NIO.2 Tutorial** | https://docs.oracle.com/javase/tutorial/essential/io/ | Tutorial oficial de I/O en Java: Files, Paths, streams, y operaciones de directorio |
| **Baeldung — Java NIO2** | https://www.baeldung.com/java-nio-2-file-api | Guía práctica de la API de archivos moderna (Files, Path) con ejemplos |
| **Baeldung — Java HttpClient** | https://www.baeldung.com/java-9-http-client | Cómo usar el HttpClient moderno de Java 11+ para peticiones HTTP |
| **Spring WebFlux — Docs oficiales** | https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html | I/O no bloqueante y reactivo en Spring — el siguiente nivel después del I/O bloqueante tradicional |
| **Latency Numbers Every Programmer Should Know** | https://gist.github.com/jboner/2841832 | Tabla de latencias reales de distintas operaciones de I/O — muestra de forma contundente por qué el I/O importa |

