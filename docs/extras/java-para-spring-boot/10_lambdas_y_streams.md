# Módulo 10 — Lambdas y Streams

> **Objetivo:** escribir código funcional con lambdas y la Stream API, que es la forma idiomática de procesar colecciones en Java moderno y la que usa Spring Boot internamente.

---

## 9.1 Lambdas — funciones como valores

Una lambda es una función anónima (sin nombre) que puede pasarse como argumento, asignarse a una variable o retornarse desde un método.

```
(parámetros) -> expresión
(parámetros) -> { bloque de código; }
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
Comparator<String> c1 = (a, b) -> a.compareTo(b);           // inferencia de tipos
Comparator<String> c2 = (String a, String b) -> a.compareTo(b); // tipos explícitos
Predicate<Integer> par = n -> n % 2 == 0;                    // un parámetro: sin ()
Supplier<String>   s  = () -> "Spring Boot";                  // sin parámetros
```

---

## 9.2 Referencias a métodos

Forma concisa de lambda cuando esta solo llama a un método existente:

```java
// Tipos de referencias a métodos:

// 1. Método estático:   Clase::metodoEstatico
Function<String, Integer> parsear = Integer::parseInt;
// equivale a: s -> Integer.parseInt(s)

// 2. Método de instancia de un objeto específico:  objeto::metodo
String prefijo = "TICKET-";
Function<String, String> agregar = prefijo::concat;
// equivale a: s -> prefijo.concat(s)

// 3. Método de instancia de un tipo arbitrario:  Clase::metodoInstancia
Function<String, String> upper = String::toUpperCase;
// equivale a: s -> s.toUpperCase()

Comparator<String> comp = String::compareTo;
// equivale a: (a, b) -> a.compareTo(b)

// 4. Constructor:  Clase::new
Supplier<ArrayList<String>> crear = ArrayList::new;
// equivale a: () -> new ArrayList<>()

Function<String, StringBuilder> sb = StringBuilder::new;
// equivale a: s -> new StringBuilder(s)

// Ejemplo práctico con lista:
List<String> nombres = List.of("ana", "luis", "maría");
nombres.stream()
       .map(String::toUpperCase)      // referencia a instancia
       .forEach(System.out::println); // referencia a instancia de objeto específico
```

---

## 9.3 Stream API — pipeline de datos

Un `Stream` es una secuencia de elementos sobre la que aplicas operaciones encadenadas. El stream NO modifica la colección original.

```
Fuente → Operaciones intermedias → Operación terminal
```

```java
List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Pipeline completo:
int resultado = numeros.stream()        // fuente
    .filter(n -> n % 2 == 0)           // intermedia: filtra pares
    .map(n -> n * n)                    // intermedia: eleva al cuadrado
    .reduce(0, Integer::sum);           // terminal: suma todo

System.out.println(resultado); // 4+16+36+64+100 = 220
```

### Crear streams

```java
// Desde colección
Stream<String> s1 = List.of("a", "b", "c").stream();

// Desde array
Stream<String> s2 = Arrays.stream(new String[]{"a", "b"});

// Desde valores directos
Stream<String> s3 = Stream.of("x", "y", "z");

// Stream infinito (con límite)
Stream.iterate(0, n -> n + 2).limit(5).forEach(System.out::println); // 0 2 4 6 8

// Stream de ints primitivos (más eficiente que Stream<Integer>)
IntStream.range(0, 5).forEach(System.out::println);       // 0,1,2,3,4
IntStream.rangeClosed(1, 5).forEach(System.out::println); // 1,2,3,4,5
```

---

## 9.4 Operaciones intermedias (lazy — se ejecutan solo cuando hay terminal)

```java
List<String> nombres = List.of("Ana", "Luis", "María", "Carlos", "Andrés", "Ana");

// filter: mantiene solo los que cumplen el predicate
nombres.stream()
    .filter(n -> n.startsWith("A"))
    .forEach(System.out::println); // Ana, Andrés, Ana

// map: transforma cada elemento (puede cambiar el tipo)
nombres.stream()
    .map(String::length)                    // Stream<String> → Stream<Integer>
    .forEach(System.out::println);          // 3, 4, 5, 6, 6, 3

// flatMap: aplana streams anidados
List<List<String>> listas = List.of(
    List.of("a", "b"),
    List.of("c", "d")
);
listas.stream()
    .flatMap(Collection::stream)            // aplana a Stream<String>
    .forEach(System.out::println);          // a, b, c, d

// distinct: elimina duplicados (usa equals/hashCode)
nombres.stream()
    .distinct()
    .forEach(System.out::println);          // Ana, Luis, María, Carlos, Andrés

// sorted: ordena
nombres.stream()
    .sorted()                               // orden natural (A-Z)
    .forEach(System.out::println);

nombres.stream()
    .sorted(Comparator.comparingInt(String::length).reversed())
    .forEach(System.out::println);          // más largo primero

// limit y skip: paginación
nombres.stream()
    .skip(2)        // salta los primeros 2
    .limit(2)       // toma máximo 2
    .forEach(System.out::println); // María, Carlos

// peek: inspeccionar sin modificar (útil para debug)
nombres.stream()
    .peek(n -> System.out.println("Procesando: " + n))
    .filter(n -> n.length() > 3)
    .forEach(System.out::println);
```

---

## 9.5 Operaciones terminales (eager — disparan la ejecución)

```java
List<Ticket> tickets = List.of(
    new Ticket(1L, "Bug login", "ABIERTO"),
    new Ticket(2L, "Mejora UI", "CERRADO"),
    new Ticket(3L, "Error pago", "ABIERTO"),
    new Ticket(4L, "Test falla", "CERRADO")
);

// forEach: ejecuta acción por cada elemento
tickets.forEach(t -> System.out.println(t.getTitulo()));

// collect: recoge resultados en una colección
List<Ticket> abiertos = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .collect(Collectors.toList());
    // Java 16+: .toList() (inmutable, más conciso)

Set<String> estados = tickets.stream()
    .map(Ticket::getEstado)
    .collect(Collectors.toSet()); // {ABIERTO, CERRADO}

// Joining: une strings
String titulos = tickets.stream()
    .map(Ticket::getTitulo)
    .collect(Collectors.joining(", ", "[", "]"));
// "[Bug login, Mejora UI, Error pago, Test falla]"

// Grouping: agrupa por criterio
Map<String, List<Ticket>> porEstado = tickets.stream()
    .collect(Collectors.groupingBy(Ticket::getEstado));
// {ABIERTO=[...], CERRADO=[...]}

// Counting por grupo
Map<String, Long> cantidadPorEstado = tickets.stream()
    .collect(Collectors.groupingBy(Ticket::getEstado, Collectors.counting()));
// {ABIERTO=2, CERRADO=2}

// count: cuenta elementos
long totalAbiertos = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .count(); // 2

// findFirst / findAny: busca el primero
Optional<Ticket> primero = tickets.stream()
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .findFirst(); // Optional[Ticket{id=1...}]

// anyMatch / allMatch / noneMatch: predicados de existencia
boolean hayAbiertos    = tickets.stream().anyMatch(t -> "ABIERTO".equals(t.getEstado()));
boolean todosCerrados  = tickets.stream().allMatch(t -> "CERRADO".equals(t.getEstado()));
boolean ningunoBorrado = tickets.stream().noneMatch(t -> "BORRADO".equals(t.getEstado()));

// min / max
Optional<Ticket> conIdMayor = tickets.stream()
    .max(Comparator.comparing(Ticket::getId));

// reduce: acumula
int sumaIds = tickets.stream()
    .mapToInt(t -> t.getId().intValue())
    .sum();     // IntStream tiene sum(), average(), min(), max() directamente

double promedioId = tickets.stream()
    .mapToLong(t -> t.getId())
    .average()
    .orElse(0);
```

---

## 9.6 Streams paralelos

Para grandes volúmenes de datos, `parallelStream()` distribuye el trabajo en múltiples hilos:

```java
// Solo cambiar stream() por parallelStream()
long count = archivosGrandes.parallelStream()
    .filter(f -> f.tamanio() > 1_000_000)
    .count();

// ⚠️ No usar para colecciones pequeñas (overhead > beneficio)
// ⚠️ Cuidado con estado compartido mutable en operaciones paralelas
```

---

## 9.7 Ejemplo completo: procesar tickets como en Spring Boot

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

### Ejercicio 9.1 — Pipeline básico
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

### Ejercicio 9.2 — Agrupación
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

### Ejercicio 9.3 — Stream completo
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

