# Módulo 09 — Manejo de excepciones

> **Objetivo:** distinguir entre excepciones checked y unchecked, usar `try-catch-finally` con confianza, crear excepciones personalizadas y aplicar el patrón de manejo de errores de Spring Boot.

---

## 9.1 ¿Qué es una excepción?

Una excepción es un **evento anormal** que interrumpe el flujo normal del programa. En Java las excepciones son **objetos** que representan ese evento; no son códigos de error numéricos ni mensajes de texto: son instancias de clases que cargan información sobre qué salió mal, dónde y por qué.

Cuando ocurre una excepción, Java crea el objeto correspondiente y lo *lanza* (throws). Si el código actual no la *captura* (catch), sube por la pila de llamadas hasta encontrar alguien que la maneje o hasta llegar al tope y terminar el programa con un mensaje de error.

La jerarquía de excepciones de Java parte de `Throwable` y se divide en dos grandes ramas:

```
Throwable
├── Error         → Errores graves e irrecuperables del sistema (OutOfMemoryError, StackOverflowError)
│                   → NO deberías capturarlos: indican que la JVM está en estado crítico
└── Exception     → Situaciones anómalas pero recuperables
    ├── RuntimeException (unchecked)
    │   ├── NullPointerException       → acceso a referencia null
    │   ├── IllegalArgumentException   → argumento inválido pasado a un método
    │   ├── IllegalStateException      → el objeto no está en estado válido para la operación
    │   ├── IndexOutOfBoundsException  → índice fuera del rango de un array o lista
    │   └── ClassCastException         → cast inválido entre tipos incompatibles
    └── IOException (checked)          → error de entrada/salida (archivos, red)
    └── SQLException (checked)         → error de base de datos
    └── ParseException (checked)       → error al parsear texto con formato esperado
```

---

## 9.2 Checked vs. Unchecked

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

## 9.3 `try-catch-finally`

El bloque `try` envuelve el código que puede lanzar excepciones. Si ocurre una excepción dentro, Java busca el primer `catch` cuyo tipo sea compatible con la excepción lanzada y ejecuta ese bloque. Si ningún `catch` coincide, la excepción sigue subiendo por la pila de llamadas.

Reglas importantes:
- Los `catch` se evalúan **en orden**: pon los tipos más específicos primero y los más generales al final. Si pones `Exception` primero, todos los siguientes `catch` son inalcanzables.
- El bloque `finally` se ejecuta **siempre**, independientemente de si hubo excepción o no. Es el lugar ideal para liberar recursos (cerrar conexiones, archivos, etc.).
- Capturar `Exception` genérica está bien como último recurso, pero nunca silencies una excepción sin registrarla o relanzarla.

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
    // Catch general: captura cualquier otra excepción no manejada arriba
    System.err.println("Error inesperado: " + e.getMessage());
    e.printStackTrace(); // útil para debug; en producción usa un logger

} finally {
    // Siempre se ejecuta, con o sin excepción, incluso si hay return en el try
    System.out.println("Proceso terminado");
}
```

### Multi-catch (Java 7+)

Cuando varios tipos de excepción merecen exactamente el mismo tratamiento, puedes capturarlos en un solo `catch` separándolos con `|`. Esto es más limpio que duplicar el bloque.

```java
try {
    // ...
} catch (NumberFormatException | IllegalArgumentException e) {
    // Captura múltiples tipos en un solo catch — 'e' es efectivamente final aquí
    System.err.println("Error de formato o argumento: " + e.getMessage());
}
```

---

## 9.4 `try-with-resources` (Java 7+)

Muchos recursos en Java (archivos, conexiones de red, streams de datos) necesitan cerrarse explícitamente después de usarlos para liberar los recursos del sistema. Sin `try-with-resources`, debes hacerlo en el `finally`, lo que produce código verboso y propenso a errores (especialmente cuando el `close()` en sí puede lanzar una excepción).

`try-with-resources` automatiza este cierre: cualquier objeto que implemente `AutoCloseable` declarado entre los paréntesis del `try` **se cierra automáticamente** al salir del bloque, tanto si termina normalmente como si se lanza una excepción. El compilador genera el código `finally` por ti.

```java
// Sin try-with-resources: debes cerrar manualmente en finally
// Este patrón tiene varios puntos de fallo y mucho código repetitivo
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("archivo.txt"));
    String linea = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try { br.close(); } catch (IOException e) { /* ignorar... */ }
    }
}

// Con try-with-resources: limpio, conciso y seguro
// br.close() se llama automáticamente al salir del bloque try, incluso si hay excepción
try (var br = new BufferedReader(new FileReader("archivo.txt"))) {
    String linea = br.readLine();
    System.out.println(linea);
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
}

// Puedes declarar múltiples recursos; se cierran en orden inverso al de declaración
try (var in  = new FileInputStream("entrada.txt");
     var out = new FileOutputStream("salida.txt")) {
    out.write(in.readAllBytes());
}
// out se cierra primero, luego in
```

---

## 9.5 Lanzar excepciones

Para lanzar una excepción usas `throw` seguido de una instancia del tipo de excepción. Es importante incluir un mensaje descriptivo que ayude a entender qué salió mal y con qué valores — ese mensaje es lo que verás en los logs y en los mensajes de error de la API.

Cuando capturas una excepción y quieres relanzarla envuelta en otro tipo (para agregar contexto), pasa la excepción original como segundo argumento del constructor. Así la causa original queda registrada en el `getCause()` y aparece en el stack trace.

```java
// throw: lanza una excepción específica
// Usa IllegalArgumentException cuando el argumento recibido es inválido
// Usa IllegalStateException cuando el objeto no está en estado adecuado para la operación
public Ticket buscarPorId(Long id) {
    if (id == null || id <= 0) {
        throw new IllegalArgumentException("El ID debe ser un número positivo, recibido: " + id);
    }
    // orElseThrow: forma idiomática de lanzar excepción desde un Optional vacío
    return repositorio.findById(id)
        .orElseThrow(() -> new RuntimeException("Ticket no encontrado con ID: " + id));
}

// Re-lanzar con contexto adicional: preserva la causa original para el stack trace
// Nunca pierdas la excepción original — siempre pásala como 'causa'
try {
    procesarPago(monto);
} catch (IOException e) {
    // Relanzas como RuntimeException para no propagar checked exceptions hacia arriba,
    // pero preservas 'e' como causa para que aparezca en los logs
    throw new RuntimeException("Error al procesar el pago de $" + monto, e);
    //                                                                       ^ causa original
}
```

---

## 9.6 Excepciones personalizadas ⭐

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

## 9.7 Patrón de manejo global en Spring Boot (preview)

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

### Ejercicio 9.1 — Try-catch
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

### Ejercicio 9.2 — Excepciones personalizadas
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

