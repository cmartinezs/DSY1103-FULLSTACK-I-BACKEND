# Módulo 10 — Lambdas y Streams

> **Objetivo:** escribir código funcional con lambdas y la Stream API, que es la forma idiomática de procesar colecciones en Java moderno y la que usa Spring Boot internamente.

---

## 10.1 Lambdas — funciones como valores

Una lambda es una **función anónima** (sin nombre ni clase) que puedes escribir directamente donde se espera una interfaz funcional. En lugar de crear una clase que implemente la interfaz y luego instanciarla, simplemente describes qué hace la función.

La sintaxis es `(parámetros) -> cuerpo`. Si el cuerpo es una sola expresión, el resultado se retorna implícitamente. Si necesitas múltiples líneas, usa `{}` y `return` explícito. Las lambdas pueden asignarse a variables, pasarse como argumentos o retornarse desde métodos — igual que cualquier otro valor.

```
(parámetros) -> expresión                   // retorno implícito
(parámetros) -> { bloque de código; }       // retorno explícito con return
```

```java
// Comparando estilos para el mismo comportamiento:

// 1. Clase completa (Java muy antiguo)
Runnable r1 = new Runnable() {
    @Override
    public void run() { System.out.println("Hola"); }
};

// 2. Lambda (Java 8+)
Runnable r2 = () -> System.out.println("Hola");

// 3. Referencia a método (cuando la lambda solo llama un método)
Runnable r3 = System.out::println; // equivalente a () -> System.out.println(...)

// Distintas formas de escribir lambdas:
Comparator<String> c1 = (a, b) -> a.compareTo(b);           // Java infiere los tipos de a y b
Comparator<String> c2 = (String a, String b) -> a.compareTo(b); // tipos explícitos (opcional)
Predicate<Integer> par = n -> n % 2 == 0;                    // un solo parámetro: sin paréntesis
Supplier<String>   s  = () -> "Spring Boot";                  // sin parámetros: () obligatorio
```

---

## 10.2 Referencias a métodos

Una referencia a método (`Clase::metodo`) es una sintaxis aún más concisa que una lambda cuando el único trabajo de la lambda es llamar a un método ya existente. Son equivalentes en comportamiento pero más legibles, especialmente al encadenar operaciones de Stream.

Existen cuatro variantes según el contexto del método que se referencia:

- **Método estático** (`Clase::metodoEstatico`): el método pertenece a la clase, no a una instancia.
- **Método de instancia de un objeto específico** (`objeto::metodo`): se fija un objeto concreto.
- **Método de instancia de un tipo arbitrario** (`Clase::metodoInstancia`): el primer parámetro de la función es el objeto receptor.
- **Constructor** (`Clase::new`): crea una nueva instancia.

```java
// 1. Método estático:   Clase::metodoEstatico
Function<String, Integer> parsear = Integer::parseInt;
// equivale a la lambda: s -> Integer.parseInt(s)

// 2. Método de instancia de un objeto específico:  objeto::metodo
String prefijo = "TICKET-";
Function<String, String> agregar = prefijo::concat;
// equivale a la lambda: s -> prefijo.concat(s)
// Útil cuando quieres "anclar" un objeto concreto a una operación

// 3. Método de instancia de un tipo arbitrario:  Clase::metodoInstancia
// El objeto sobre el que se llama el método viene del propio stream
Function<String, String> upper = String::toUpperCase;
// equivale a la lambda: s -> s.toUpperCase()

Comparator<String> comp = String::compareTo;
// equivale a la lambda: (a, b) -> a.compareTo(b)
// El primer parámetro es el receptor, el segundo el argumento

// 4. Constructor:  Clase::new
// Útil cuando necesitas un Supplier o Function que cree objetos
Supplier<ArrayList<String>> crear = ArrayList::new;
// equivale a la lambda: () -> new ArrayList<>()

Function<String, StringBuilder> sb = StringBuilder::new;
// equivale a la lambda: s -> new StringBuilder(s)

// Ejemplo práctico con lista — compara la legibilidad con lambdas equivalentes:
List<String> nombres = List.of("ana", "luis", "maría");
nombres.stream()
       .map(String::toUpperCase)      // más claro que: .map(s -> s.toUpperCase())
       .forEach(System.out::println); // más claro que: .forEach(n -> System.out.println(n))
```

---

## 10.3 Stream API — pipeline de datos

Un `Stream` es una secuencia de elementos sobre la que aplicas operaciones encadenadas. A diferencia de las colecciones, un stream **no almacena datos**: es un canal por el que fluyen los elementos desde una fuente hasta un resultado final. Tampoco modifica la colección original.

La estructura de un pipeline es siempre: **fuente → operaciones intermedias → operación terminal**. Las operaciones intermedias son *lazy* (no se ejecutan hasta que hay una terminal) y se pueden encadenar libremente. La operación terminal dispara todo el procesamiento y consume el stream, que no puede reutilizarse después.

```
Fuente → Operaciones intermedias → Operación terminal
```

```java
List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Pipeline completo: cada número pasa por filter y map antes de que reduce acumule el total
int resultado = numeros.stream()        // fuente: convierte la lista en un stream
    .filter(n -> n % 2 == 0)           // intermedia: deja pasar solo 2, 4, 6, 8, 10
    .map(n -> n * n)                    // intermedia: eleva al cuadrado → 4, 16, 36, 64, 100
    .reduce(0, Integer::sum);           // terminal: suma todos → 220

System.out.println(resultado); // 4+16+36+64+100 = 220
```

### Crear streams

Puedes crear un stream desde casi cualquier fuente de datos en Java. Los más comunes son desde colecciones (`.stream()`) y desde valores directos (`Stream.of(...)`). Para trabajar con enteros, `IntStream` es más eficiente que `Stream<Integer>` porque evita el boxing/unboxing.

```java
// Desde colección — la forma más frecuente
Stream<String> s1 = List.of("a", "b", "c").stream();

// Desde array
Stream<String> s2 = Arrays.stream(new String[]{"a", "b"});

// Desde valores directos
Stream<String> s3 = Stream.of("x", "y", "z");

// Stream infinito — siempre usa limit() para que no sea infinito de verdad
// iterate genera: 0, 2, 4, 6, 8 (cada elemento = anterior + 2)
Stream.iterate(0, n -> n + 2).limit(5).forEach(System.out::println);

// IntStream: más eficiente para números que Stream<Integer>
IntStream.range(0, 5).forEach(System.out::println);       // 0,1,2,3,4 (excluye el 5)
IntStream.rangeClosed(1, 5).forEach(System.out::println); // 1,2,3,4,5 (incluye el 5)
```

---

## 10.4 Operaciones intermedias (lazy — se ejecutan solo cuando hay terminal)

Las operaciones intermedias **transforman o filtran** el stream y devuelven un nuevo stream. Son *lazy*: no se ejecutan hasta que se encuentre una operación terminal al final del pipeline. Puedes encadenar todas las que quieras sin que Java haga ningún trabajo aún.

Cada operación intermedia recibe como argumento una función (lambda o referencia a método) que define qué hacer con cada elemento. El elemento original nunca se modifica; si necesitas cambiarlo, `map` genera un elemento nuevo.

```java
List<String> nombres = List.of("Ana", "Luis", "María", "Carlos", "Andrés", "Ana");

// filter: descarta los elementos que NO cumplen el Predicate.
// Solo pasan al siguiente paso los que devuelven true.
nombres.stream()
    .filter(n -> n.startsWith("A"))
    .forEach(System.out::println); // Ana, Andrés, Ana

// map: transforma cada elemento aplicando una Function.
// Puede cambiar el tipo del stream (String → Integer en este caso).
nombres.stream()
    .map(String::length)                    // Stream<String> → Stream<Integer>
    .forEach(System.out::println);          // 3, 4, 5, 6, 6, 3

// flatMap: útil cuando cada elemento es a su vez una colección.
// "Aplana" un Stream<List<X>> en un Stream<X>, concatenando todas las listas.
List<List<String>> listas = List.of(
    List.of("a", "b"),
    List.of("c", "d")
);
listas.stream()
    .flatMap(Collection::stream)            // Stream<List<String>> → Stream<String>
    .forEach(System.out::println);          // a, b, c, d

// distinct: elimina duplicados usando equals()/hashCode() de cada elemento.
// Ideal para obtener un listado de valores únicos a partir de datos repetidos.
nombres.stream()
    .distinct()
    .forEach(System.out::println);          // Ana, Luis, María, Carlos, Andrés

// sorted: ordena los elementos. Sin argumentos usa el orden natural del tipo.
// Con un Comparator puedes definir cualquier criterio de ordenación.
nombres.stream()
    .sorted()                               // orden natural (A-Z)
    .forEach(System.out::println);

nombres.stream()
    .sorted(Comparator.comparingInt(String::length).reversed())
    .forEach(System.out::println);          // más largo primero

// limit y skip: combinados implementan paginación de forma muy sencilla.
// skip(n) descarta los primeros n elementos; limit(n) toma como máximo n.
nombres.stream()
    .skip(2)        // salta los primeros 2
    .limit(2)       // toma máximo 2
    .forEach(System.out::println); // María, Carlos

// peek: permite "espiar" cada elemento sin modificarlo, útil para debug.
// No debe usarse con efectos secundarios en código de producción.
nombres.stream()
    .peek(n -> System.out.println("Procesando: " + n))
    .filter(n -> n.length() > 3)
    .forEach(System.out::println);
```

---

## 10.5 Operaciones terminales (eager — disparan la ejecución)

Las operaciones terminales **consumen el stream** y producen un resultado final: una colección, un valor, un booleano o ningún valor (side-effect). Una vez que se llama a una operación terminal, el stream no puede volver a usarse.

Es aquí donde Java realmente procesa todos los elementos, ejecutando en cadena las operaciones intermedias que habías declarado antes. Esto es lo que significa que las intermedias sean *lazy* y las terminales *eager*.

```java
List<Ticket> tickets = List.of(
    new Ticket(1L, "Bug login", "ABIERTO"),
    new Ticket(2L, "Mejora UI", "CERRADO"),
    new Ticket(3L, "Error pago", "ABIERTO"),
    new Ticket(4L, "Test falla", "CERRADO")
);

// forEach: ejecuta una acción por cada elemento, sin retornar nada.
// Es el equivalente funcional de un bucle for-each.
tickets.forEach(t -> System.out.println(t.getTitulo()));

// collect: recoge los elementos en una colección o estructura de datos.
// Es la operación terminal más versátil gracias a los Collectors.
List<Ticket> abiertos = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .collect(Collectors.toList());
    // Java 16+: .toList() — más conciso, retorna lista inmutable

Set<String> estados = tickets.stream()
    .map(Ticket::getEstado)
    .collect(Collectors.toSet()); // {ABIERTO, CERRADO}

// joining: une todos los Strings del stream en uno solo, con separador opcional.
String titulos = tickets.stream()
    .map(Ticket::getTitulo)
    .collect(Collectors.joining(", ", "[", "]"));
// "[Bug login, Mejora UI, Error pago, Test falla]"

// groupingBy: agrupa los elementos en un Map según un criterio (classifier).
// Cada valor del mapa es una List con los elementos que comparten esa clave.
Map<String, List<Ticket>> porEstado = tickets.stream()
    .collect(Collectors.groupingBy(Ticket::getEstado));
// {ABIERTO=[...], CERRADO=[...]}

// Puedes combinar groupingBy con un downstream collector como counting():
Map<String, Long> cantidadPorEstado = tickets.stream()
    .collect(Collectors.groupingBy(Ticket::getEstado, Collectors.counting()));
// {ABIERTO=2, CERRADO=2}

// count: cuenta cuántos elementos pasan por el stream. Simple pero muy usado.
long totalAbiertos = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .count(); // 2

// findFirst / findAny: buscan un elemento que cumpla las operaciones previas.
// Retornan Optional<T> porque puede no existir ninguno que lo cumpla.
Optional<Ticket> primero = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .findFirst(); // Optional[Ticket{id=1...}]

// anyMatch / allMatch / noneMatch: verifican si algún / todos / ningún elemento
// cumple el predicado. Son operaciones de cortocircuito: terminan en cuanto
// pueden dar la respuesta definitiva, sin procesar el resto del stream.
boolean hayAbiertos    = tickets.stream().anyMatch(t -> "ABIERTO".equals(t.getEstado()));
boolean todosCerrados  = tickets.stream().allMatch(t -> "CERRADO".equals(t.getEstado()));
boolean ningunoBorrado = tickets.stream().noneMatch(t -> "BORRADO".equals(t.getEstado()));

// min / max: buscan el elemento mínimo o máximo según un Comparator.
Optional<Ticket> conIdMayor = tickets.stream()
    .max(Comparator.comparing(Ticket::getId));

// reduce / mapToInt / sum: para operaciones matemáticas sobre los elementos.
// Los streams de primitivos (IntStream, LongStream) tienen métodos directos
// como sum(), average(), min(), max() que son más eficientes que sus equivalentes
// en Stream<Integer> porque evitan el autoboxing/unboxing.
int sumaIds = tickets.stream()
    .mapToInt(t -> t.getId().intValue())
    .sum();

double promedioId = tickets.stream()
    .mapToLong(t -> t.getId())
    .average()
    .orElse(0);
```

---

## 10.6 Streams paralelos

Por defecto, un stream procesa los elementos **secuencialmente** en un solo hilo. Con `parallelStream()` (o `.parallel()` en un stream ya creado), Java distribuye el trabajo entre varios hilos del pool común del ForkJoinPool, lo que puede acelerar operaciones sobre colecciones muy grandes.

El uso de streams paralelos, sin embargo, tiene un costo de coordinación entre hilos que para colecciones pequeñas hace que sea **más lento** que el equivalente secuencial. Además, las operaciones deben ser **sin estado** y **sin efectos secundarios** para que el resultado sea correcto — modificar una variable compartida desde múltiples hilos produce resultados no deterministas.

```java
// Solo cambiar stream() por parallelStream()
long count = archivosGrandes.parallelStream()
    .filter(f -> f.tamanio() > 1_000_000)
    .count();
// Los filtros se ejecutan en paralelo sobre partes de la colección

// ⚠️ No usar para colecciones pequeñas: el overhead de coordinar hilos
//    supera el beneficio de la paralelización.
// ⚠️ Nunca modificar estado compartido mutable desde operaciones paralelas:
//    el resultado puede variar entre ejecuciones (race condition).
// ✅ Caso de uso adecuado: colecciones de miles de elementos con operaciones costosas
//    (cálculos intensivos, transformaciones complejas) y sin efectos secundarios.
```

> 💡 En el contexto de una API Spring Boot, los streams paralelos rara vez son necesarios. La concurrencia en APIs se maneja a nivel de solicitudes HTTP, no dentro del procesamiento de una sola solicitud.

---

## 10.7 Ejemplo completo: procesar tickets como en Spring Boot

```java
public class TicketService {
    private List<Ticket> tickets = new ArrayList<>();

    // Retorna todos los tickets abiertos ordenados por ID, mapeados a DTO
    public List<TicketDTO> listarAbiertosOrdenados() {
        return tickets.stream()
            .filter(t -> "ABIERTO".equals(t.getEstado()))
            .sorted(Comparator.comparing(Ticket::getId))
            .map(t -> new TicketDTO(t.getId(), t.getTitulo(), t.getEstado()))
            .toList(); // Java 16+
    }

    // Busca por título (búsqueda parcial, insensible a mayúsculas)
    public Optional<Ticket> buscarPorTitulo(String termino) {
        return tickets.stream()
            .filter(t -> t.getTitulo().toLowerCase().contains(termino.toLowerCase()))
            .findFirst();
    }

    // Estadísticas de tickets por estado
    public Map<String, Long> contarPorEstado() {
        return tickets.stream()
            .collect(Collectors.groupingBy(Ticket::getEstado, Collectors.counting()));
    }

    // Verificar si existe algún ticket crítico
    public boolean hayTicketsCriticos() {
        return tickets.stream()
            .anyMatch(t -> "CRITICO".equals(t.getPrioridad()));
    }
}
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 10.1 — Pipeline básico
Dada la lista de números `[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]`, usa Stream para:
1. Filtrar los números pares
2. Elevar cada uno al cubo
3. Retornar solo los que sean mayores a 50
4. Colectar en una `List<Integer>`

<details>
<summary>🔍 Ver solución</summary>

```java
List<Integer> resultado = List.of(1,2,3,4,5,6,7,8,9,10).stream()
    .filter(n -> n % 2 == 0)        // pares: 2,4,6,8,10
    .map(n -> n * n * n)            // cubos: 8,64,216,512,1000
    .filter(n -> n > 50)            // >50: 64,216,512,1000
    .toList();

System.out.println(resultado); // [64, 216, 512, 1000]
```
</details>

---

### Ejercicio 10.2 — Agrupación
Dada una lista de nombres `["Ana", "Luis", "María", "Carlos", "Alejandra", "Miguel"]`, agrúpalos por la primera letra y muestra cuántos hay en cada grupo.

<details>
<summary>🔍 Ver solución</summary>

```java
List<String> nombres = List.of("Ana", "Luis", "María", "Carlos", "Alejandra", "Miguel");

Map<Character, Long> porLetra = nombres.stream()
    .collect(Collectors.groupingBy(
        n -> n.charAt(0),     // clave: primera letra
        Collectors.counting() // valor: cantidad
    ));

porLetra.forEach((letra, cantidad) ->
    System.out.println(letra + ": " + cantidad));
// A: 2, L: 1, M: 2, C: 1
```
</details>

---

### Ejercicio 10.3 — Stream completo
Tienes una lista de `Producto(String nombre, double precio, String categoria)`. Usando Stream:
1. Filtra los productos de categoría `"ELECTRONICA"`
2. Aplica un descuento del 10% a cada precio
3. Ordénalos por precio ascendente
4. Retorna los títulos de los 3 más baratos como `List<String>`

<details>
<summary>🔍 Ver solución</summary>

```java
public record Producto(String nombre, double precio, String categoria) {}

List<Producto> productos = List.of(
    new Producto("Laptop",  1200.0, "ELECTRONICA"),
    new Producto("Mesa",     200.0, "HOGAR"),
    new Producto("Teléfono", 800.0, "ELECTRONICA"),
    new Producto("Monitor",  450.0, "ELECTRONICA"),
    new Producto("Teclado",  80.0,  "ELECTRONICA"),
    new Producto("Mouse",    40.0,  "ELECTRONICA")
);

List<String> masBaratos = productos.stream()
    .filter(p -> "ELECTRONICA".equals(p.categoria()))
    .map(p -> new Producto(p.nombre(), p.precio() * 0.9, p.categoria()))
    .sorted(Comparator.comparingDouble(Producto::precio))
    .limit(3)
    .map(Producto::nombre)
    .toList();

System.out.println(masBaratos); // [Mouse, Teclado, Monitor]
```
</details>

---

*[← Módulo 08](./08_excepciones.md) | [Índice](./README.md) | [Módulo 10 → Java 21](./10_java21.md)*

