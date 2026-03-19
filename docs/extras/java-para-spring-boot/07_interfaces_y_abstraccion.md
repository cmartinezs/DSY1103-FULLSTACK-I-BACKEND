# Módulo 07 — Interfaces y clases abstractas

> **Objetivo:** dominar las interfaces como contratos de comportamiento, entender cuándo usar interfaces vs. clases abstractas, y conocer las interfaces funcionales que son la base de las lambdas.

---

## 6.1 ¿Qué es una interfaz?

Una interfaz define **qué** puede hacer algo, sin especificar **cómo**. Es un contrato que las clases se comprometen a cumplir al implementarla.

```java
// Definición del contrato
public interface Almacenable<T, ID> {
    T   guardar(T entidad);
    T   buscarPorId(ID id);
    List<T> listarTodos();
    void eliminar(ID id);
}

// Clase que cumple el contrato
public class TicketRepositorio implements Almacenable<Ticket, Long> {
    private List<Ticket> tickets = new ArrayList<>();

    @Override
    public Ticket guardar(Ticket ticket) {
        tickets.add(ticket);
        return ticket;
    }

    @Override
    public Ticket buscarPorId(Long id) {
        return tickets.stream()
                      .filter(t -> t.getId().equals(id))
                      .findFirst()
                      .orElseThrow(() -> new RuntimeException("No encontrado: " + id));
    }

    @Override
    public List<Ticket> listarTodos() { return List.copyOf(tickets); }

    @Override
    public void eliminar(Long id) { tickets.removeIf(t -> t.getId().equals(id)); }
}
```

---

## 6.2 Herencia múltiple con interfaces

Una clase puede implementar **múltiples interfaces** (algo imposible con `extends`):

```java
public interface Exportable {
    String exportarJson();
    String exportarCsv();
}

public interface Auditable {
    LocalDateTime getCreadoEn();
    LocalDateTime getActualizadoEn();
}

public interface Validable {
    boolean esValido();
    List<String> obtenerErrores();
}

// Una clase puede implementar todas:
public class Ticket implements Exportable, Auditable, Validable {
    private String titulo;
    private String estado;
    private LocalDateTime creadoEn = LocalDateTime.now();
    private LocalDateTime actualizadoEn;

    @Override
    public String exportarJson() {
        return """
               {"titulo": "%s", "estado": "%s"}
               """.formatted(titulo, estado);
    }

    @Override
    public String exportarCsv() {
        return titulo + "," + estado;
    }

    @Override
    public LocalDateTime getCreadoEn() { return creadoEn; }

    @Override
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    @Override
    public boolean esValido() { return titulo != null && !titulo.isBlank(); }

    @Override
    public List<String> obtenerErrores() {
        List<String> errores = new ArrayList<>();
        if (titulo == null || titulo.isBlank()) errores.add("El título es obligatorio");
        if (estado == null) errores.add("El estado es obligatorio");
        return errores;
    }
}
```

---

## 6.3 Métodos `default` en interfaces (Java 8+)

Las interfaces pueden tener métodos con implementación usando `default`. Esto permite agregar comportamiento sin romper las clases que ya implementan la interfaz.

```java
public interface Notificador {
    // Método abstracto: obligatorio implementar
    void enviar(String destinatario, String mensaje);

    // Método default: implementación por defecto (puede sobreescribirse)
    default void enviarConAsunto(String destinatario, String asunto, String cuerpo) {
        enviar(destinatario, "[" + asunto + "] " + cuerpo);
    }

    // Método estático en interfaz: utilitario
    static String formatearMensaje(String plantilla, Object... argumentos) {
        return plantilla.formatted(argumentos);
    }
}

public class EmailNotificador implements Notificador {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("📧 EMAIL → " + destinatario + ": " + mensaje);
    }

    // Puede sobreescribir el default o heredarlo tal cual
}

// Uso:
var email = new EmailNotificador();
email.enviar("ana@duoc.cl", "Hola");
email.enviarConAsunto("ana@duoc.cl", "ALERTA", "Tu ticket fue cerrado"); // usa default
```

---

## 6.4 Interfaces funcionales — la base de las lambdas ⭐

Una **interfaz funcional** es una interfaz con exactamente **un método abstracto** (puede tener métodos `default` y `static`, pero solo un abstracto). La anotación `@FunctionalInterface` le pide al compilador que verifique esto y falle si alguien agrega un segundo método abstracto accidentalmente.

Son la base de las lambdas: cuando asignas una lambda a una variable o pasas una lambda como argumento, Java la está convirtiendo implícitamente en una implementación de la interfaz funcional correspondiente.

El paquete `java.util.function` define las interfaces funcionales más comunes para no tener que crearlas desde cero cada vez:

| Interfaz | Firma | Descripción | Cuándo usarla |
|----------|-------|-------------|---------------|
| `Predicate<T>` | `boolean test(T t)` | Evalúa una condición | Filtrar elementos |
| `Function<T, R>` | `R apply(T t)` | Transforma T en R | Convertir/mapear |
| `Consumer<T>` | `void accept(T t)` | Consume sin retornar | Imprimir, guardar |
| `Supplier<T>` | `T get()` | Produce sin recibir | Crear objetos bajo demanda |
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | Transforma dos entradas | Combinar dos valores |
| `UnaryOperator<T>` | `T apply(T t)` | Transforma del mismo tipo | Modificar sin cambiar tipo |

```java
import java.util.function.*;

// Predicate: evalúa una condición y retorna true o false
// Útil en stream.filter() y para validaciones
Predicate<Ticket> estaAbierto = ticket -> "ABIERTO".equals(ticket.getEstado());

// Function: transforma un tipo en otro
// Útil en stream.map() y para convertir entidades a DTOs
Function<Ticket, String> resumir = ticket ->
    "#" + ticket.getId() + " — " + ticket.getTitulo();

// Consumer: realiza una acción con el elemento sin retornar nada
// Útil en stream.forEach() y para side effects controlados
Consumer<Ticket> imprimir = ticket ->
    System.out.println(ticket.getTitulo() + " [" + ticket.getEstado() + "]");

// Supplier: produce un valor sin recibir ningún argumento
// Útil para crear objetos de forma diferida (lazy) o en orElseGet()
Supplier<Ticket> ticketDePrueba = () -> new Ticket("Test", "Descripción de prueba");

// Los Predicate tienen métodos de composición que los hacen muy potentes
Predicate<Ticket> estaCerrado  = estaAbierto.negate();          // invierte
Predicate<Ticket> tieneTitulo  = t -> t.getTitulo() != null && !t.getTitulo().isBlank();
Predicate<Ticket> esValido     = estaAbierto.and(tieneTitulo);  // ambas condiciones
Predicate<Ticket> cualquiera   = estaAbierto.or(tieneTitulo);   // al menos una

// Uso con colecciones — aquí se ve por qué son tan útiles:
List<Ticket> tickets = obtenerTickets();
tickets.stream()
       .filter(estaAbierto)   // reutilizas el mismo Predicate en distintos streams
       .map(resumir)          // reutilizas la misma Function
       .forEach(System.out::println);
```

---

## 6.5 Interface vs. Clase abstracta — ¿cuándo usar cada una?

```
¿Comparten SOLO un contrato de comportamiento?
→ Usa interfaz

¿Comparten también estado (campos) y lógica concreta?
→ Usa clase abstracta (o combina ambas)
```

| Característica | `interface` | `abstract class` |
|---|---|---|
| Herencia múltiple | ✅ Sí | ❌ Solo una |
| Campos de instancia | ❌ Solo constantes | ✅ Sí |
| Constructores | ❌ No | ✅ Sí |
| Métodos concretos | Solo `default`/`static` | ✅ Sí |
| Cuándo usar | Contrato de comportamiento | Base común con lógica |

```java
// Combinación: clase abstracta implementa parte de una interfaz
public interface Repositorio<T, ID> {
    T guardar(T entidad);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void eliminar(ID id);
}

// Clase abstracta provee lógica común, deja lo específico abstracto
public abstract class RepositorioEnMemoria<T, ID> implements Repositorio<T, ID> {
    protected List<T> almacen = new ArrayList<>();

    @Override
    public T guardar(T entidad) {
        almacen.add(entidad);
        return entidad;
    }

    @Override
    public List<T> listarTodos() { return List.copyOf(almacen); }

    @Override
    public void eliminar(ID id) {
        almacen.removeIf(e -> obtenerIdDe(e).equals(id));
    }

    // Abstracto: cada subclase sabe cómo obtener su ID
    protected abstract ID obtenerIdDe(T entidad);
}

// Implementación concreta: solo debe definir cómo buscar y obtener ID
public class TicketRepositorioMemoria extends RepositorioEnMemoria<Ticket, Long> {
    @Override
    public Optional<Ticket> buscarPorId(Long id) {
        return almacen.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    protected Long obtenerIdDe(Ticket ticket) { return ticket.getId(); }
}
```

---

## 6.6 Clases anónimas y lambdas

Antes de Java 8, la única forma de implementar una interfaz funcional "al vuelo" era con una **clase anónima**: una clase sin nombre declarada e instanciada en el mismo lugar. Su sintaxis es verbosa porque debes declarar la clase completa con `@Override` aunque solo tenga una función.

Java 8 introdujo las lambdas precisamente para reemplazar las clases anónimas en este caso. Son semánticamente equivalentes — el compilador convierte internamente la lambda en una implementación de la interfaz — pero con una fracción del código. Cuando la lambda se reduce a llamar un único método existente, las referencias a método son aún más concisas.

```java
// Estilo antiguo: clase anónima — funcional pero muy verboso
Comparator<String> comparadorAntiguo = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.length() - b.length();   // compara por longitud
    }
};

// Estilo moderno: lambda — mismo comportamiento, 90% menos código
Comparator<String> comparadorLambda = (a, b) -> a.length() - b.length();

// Aún más conciso con referencia a método estático de la clase Comparator
// Cuando existe un método que ya hace exactamente lo que necesitas, úsalo
Comparator<String> comparadorMetodo = Comparator.comparingInt(String::length);

// Uso — los tres comparadores se comportan de forma idéntica:
List<String> nombres = new ArrayList<>(List.of("Ana", "Carlos", "Bo", "Valentina"));
nombres.sort(comparadorLambda);
System.out.println(nombres); // [Bo, Ana, Carlos, Valentina] — orden por longitud
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 6.1 — Diseño con interfaz
Define una interfaz `Calculable` con el método `double calcular()`. Implementa tres clases: `Suma(double a, double b)`, `Producto(double a, double b)` y `Potencia(double base, double exponente)`. Crea una lista `List<Calculable>` con instancias de cada tipo y usa un bucle para imprimir el resultado de cada una.

<details>
<summary>🔍 Ver solución</summary>

```java
@FunctionalInterface
public interface Calculable {
    double calcular();
}

public record Suma(double a, double b) implements Calculable {
    public double calcular() { return a + b; }
}

public record Producto(double a, double b) implements Calculable {
    public double calcular() { return a * b; }
}

public record Potencia(double base, double exponente) implements Calculable {
    public double calcular() { return Math.pow(base, exponente); }
}

// Main:
List<Calculable> operaciones = List.of(
    new Suma(3, 4),
    new Producto(5, 6),
    new Potencia(2, 10)
);

operaciones.forEach(op -> System.out.printf("%.2f%n", op.calcular()));
// 7.00, 30.00, 1024.00
```
</details>

---

### Ejercicio 6.2 — Interfaces funcionales
Sin usar Stream, aplica manualmente una `Function<String, String>` que convierta un email a `"usuario"` (todo lo que está antes del `@`). Luego usa un `Predicate<String>` para filtrar solo los emails que terminen en `.cl` de una lista.

<details>
<summary>🔍 Ver solución</summary>

```java
Function<String, String> extraerUsuario = email -> email.split("@")[0];
Predicate<String> esChileno = email -> email.endsWith(".cl");

List<String> emails = List.of("ana@duoc.cl", "luis@gmail.com", "pedro@uc.cl");

for (String email : emails) {
    if (esChileno.test(email)) {
        System.out.println(extraerUsuario.apply(email));
    }
}
// ana
// pedro
```
</details>

---

*[← Módulo 05](./05_poo_pilares.md) | [Índice](./README.md) | [Módulo 07 → Colecciones](./07_colecciones_y_genericos.md)*

