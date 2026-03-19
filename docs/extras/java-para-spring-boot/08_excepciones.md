# Módulo 08 — Manejo de excepciones

> **Objetivo:** distinguir entre excepciones checked y unchecked, usar `try-catch-finally` con confianza, crear excepciones personalizadas y aplicar el patrón de manejo de errores de Spring Boot.

---

## 8.1 ¿Qué es una excepción?

Una excepción es un **evento anormal** que interrumpe el flujo normal del programa. En Java las excepciones son objetos que representan ese evento.

```
Throwable
├── Error         → Errores graves del sistema (OutOfMemoryError, StackOverflowError)
│                   → NO deberías capturarlos
└── Exception     → Situaciones recuperables
    ├── RuntimeException (unchecked)
    │   ├── NullPointerException
    │   ├── IllegalArgumentException
    │   ├── IllegalStateException
    │   ├── IndexOutOfBoundsException
    │   └── ClassCastException
    └── IOException (checked)
    └── SQLException (checked)
    └── ParseException (checked)
```

---

## 8.2 Checked vs. Unchecked

| | Checked | Unchecked (RuntimeException) |
|---|---|---|
| El compilador obliga a manejarlas | ✅ Sí | ❌ No |
| Cuándo ocurren | Situaciones externas (archivos, red, BD) | Errores de programación |
| `throws` en firma | Obligatorio | Opcional |
| Ejemplos | `IOException`, `SQLException` | `NullPointerException`, `IllegalArgumentException` |

```java
// Checked: el compilador te obliga a manejarla
public String leerArchivo(String ruta) throws IOException {
    // Si no capturas ni declaras throws, no compila
    return Files.readString(Path.of(ruta));
}

// Unchecked: no requiere declaración
public int dividir(int a, int b) {
    if (b == 0) throw new ArithmeticException("División por cero");
    return a / b;
}
```

> 💡 En Spring Boot, la práctica moderna es usar **excepciones unchecked** para la lógica de negocio y dejar que Spring las capture con `@ExceptionHandler` o `@ControllerAdvice`.

---

## 8.3 `try-catch-finally`

```java
// Estructura completa
try {
    // código que puede lanzar excepciones
    String contenido = Files.readString(Path.of("/archivo.txt"));
    int numero = Integer.parseInt(contenido.trim());
    System.out.println("Número: " + numero);

} catch (NumberFormatException e) {
    // Captura excepción específica primero (más específica → más general)
    System.err.println("El archivo no contiene un número válido: " + e.getMessage());

} catch (IOException e) {
    System.err.println("Error al leer el archivo: " + e.getMessage());

} catch (Exception e) {
    // Catch general: captura cualquier otra excepción
    System.err.println("Error inesperado: " + e.getMessage());
    e.printStackTrace(); // útil para debug, no para producción

} finally {
    // Siempre se ejecuta, con o sin excepción
    // Ideal para cerrar recursos
    System.out.println("Proceso terminado");
}
```

### Multi-catch (Java 7+)

```java
try {
    // ...
} catch (NumberFormatException | IllegalArgumentException e) {
    // Captura múltiples tipos en un solo catch
    System.err.println("Error de formato o argumento: " + e.getMessage());
}
```

---

## 8.4 `try-with-resources` (Java 7+)

Cierra automáticamente los recursos que implementan `AutoCloseable` al terminar el bloque:

```java
// Sin try-with-resources: debes cerrar manualmente en finally
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("archivo.txt"));
    String linea = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try { br.close(); } catch (IOException e) { /* ... */ }
    }
}

// Con try-with-resources: automático y limpio
try (var br = new BufferedReader(new FileReader("archivo.txt"))) {
    String linea = br.readLine();
    System.out.println(linea);
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
}
// br.close() se llama automáticamente

// Múltiples recursos:
try (var in  = new FileInputStream("entrada.txt");
     var out = new FileOutputStream("salida.txt")) {
    out.write(in.readAllBytes());
}
```

---

## 8.5 Lanzar excepciones

```java
// throw: lanza una excepción específica
public Ticket buscarPorId(Long id) {
    if (id == null || id <= 0) {
        throw new IllegalArgumentException("El ID debe ser positivo, recibido: " + id);
    }

    return repositorio.findById(id)
        .orElseThrow(() -> new RuntimeException("Ticket no encontrado con ID: " + id));
}

// Re-lanzar una excepción con más contexto
try {
    procesarPago(monto);
} catch (IOException e) {
    throw new RuntimeException("Error al procesar el pago de $" + monto, e);
    //                                                                       ^ causa original
}
```

---

## 8.6 Excepciones personalizadas ⭐

En Spring Boot es fundamental crear excepciones propias para representar errores de dominio:

```java
// Excepción base del dominio (unchecked)
public class DominioException extends RuntimeException {
    private final int codigoHttp;

    public DominioException(String mensaje, int codigoHttp) {
        super(mensaje);
        this.codigoHttp = codigoHttp;
    }

    public DominioException(String mensaje, int codigoHttp, Throwable causa) {
        super(mensaje, causa);
        this.codigoHttp = codigoHttp;
    }

    public int getCodigoHttp() { return codigoHttp; }
}

// Excepciones específicas del dominio
public class RecursoNoEncontradoException extends DominioException {
    public RecursoNoEncontradoException(String recurso, Object id) {
        super("%s con ID '%s' no encontrado".formatted(recurso, id), 404);
    }
}

public class RecursoYaExisteException extends DominioException {
    public RecursoYaExisteException(String recurso, Object identificador) {
        super("%s '%s' ya existe".formatted(recurso, identificador), 409);
    }
}

public class ReglaNegocioException extends DominioException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje, 422);
    }
}

// Uso en el servicio:
public class TicketService {
    public Ticket buscarPorId(Long id) {
        return repositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Ticket", id));
    }

    public Ticket crearTicket(Ticket ticket) {
        if (ticket.getTitulo() == null || ticket.getTitulo().isBlank()) {
            throw new ReglaNegocioException("El título del ticket no puede estar vacío");
        }
        return repositorio.save(ticket);
    }
}
```

---

## 8.7 Patrón de manejo global en Spring Boot (preview)

En Spring Boot verás este patrón para manejar excepciones de forma centralizada:

```java
// @ControllerAdvice captura excepciones de todos los controladores
@ControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {
        return ResponseEntity
            .status(ex.getCodigoHttp())
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> manejarReglaNegocio(
            ReglaNegocioException ex) {
        return ResponseEntity
            .status(ex.getCodigoHttp())
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarGeneral(Exception ex) {
        return ResponseEntity
            .status(500)
            .body(Map.of("error", "Error interno del servidor"));
    }
}
```

> 💡 No te preocupes por entender todo el código Spring Boot ahora — lo verás en detalle en las lecciones. Lo importante es que veas **por qué** las excepciones personalizadas son tan importantes.

---

## 🏋️ Ejercicios de práctica

### Ejercicio 8.1 — Try-catch
Escribe un método `Optional<Integer> parsearEntero(String texto)` que retorne `Optional.empty()` si el texto no es un entero válido, en lugar de lanzar una excepción al exterior.

<details>
<summary>🔍 Ver solución</summary>

```java
public static Optional<Integer> parsearEntero(String texto) {
    try {
        return Optional.of(Integer.parseInt(texto));
    } catch (NumberFormatException e) {
        return Optional.empty();
    }
}

// Uso:
parsearEntero("42").ifPresent(n -> System.out.println("Número: " + n));
parsearEntero("abc").ifPresentOrElse(
    n -> System.out.println("Número: " + n),
    () -> System.out.println("No es un número válido")
);
```
</details>

---

### Ejercicio 8.2 — Excepciones personalizadas
Crea una jerarquía de excepciones para un sistema de biblioteca:
- `BibliotecaException` (base, unchecked)
- `LibroNoDisponibleException`: cuando el libro está prestado
- `UsuarioSinPermisoException`: cuando el usuario no puede realizar la operación
- Úsalas en un método `prestar(Libro libro, Usuario usuario)` que valide ambas condiciones.

<details>
<summary>🔍 Ver solución</summary>

```java
public class BibliotecaException extends RuntimeException {
    public BibliotecaException(String mensaje) { super(mensaje); }
}

public class LibroNoDisponibleException extends BibliotecaException {
    public LibroNoDisponibleException(String titulo) {
        super("El libro '%s' no está disponible para préstamo".formatted(titulo));
    }
}

public class UsuarioSinPermisoException extends BibliotecaException {
    public UsuarioSinPermisoException(String usuario, String accion) {
        super("El usuario '%s' no tiene permiso para: %s".formatted(usuario, accion));
    }
}

// Servicio:
public void prestar(Libro libro, Usuario usuario) {
    if (!usuario.puedePrestar()) {
        throw new UsuarioSinPermisoException(usuario.getNombre(), "prestar libros");
    }
    if (!libro.estaDisponible()) {
        throw new LibroNoDisponibleException(libro.getTitulo());
    }
    libro.prestar(usuario);
}

// Uso:
try {
    servicio.prestar(libro, usuario);
} catch (LibroNoDisponibleException e) {
    System.out.println("Libro no disponible: " + e.getMessage());
} catch (UsuarioSinPermisoException e) {
    System.out.println("Sin permiso: " + e.getMessage());
}
```
</details>

---

*[← Módulo 07](./07_colecciones_y_genericos.md) | [Índice](./README.md) | [Módulo 09 → Lambdas y Streams](./09_lambdas_y_streams.md)*

