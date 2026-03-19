# Módulo 08 — Colecciones y Genéricos

> **Objetivo:** manejar con soltura las colecciones más comunes de Java (`List`, `Map`, `Set`) y entender los genéricos, que son la base de Spring Boot, JPA y la API de Streams.

---

## 7.1 El framework de colecciones de Java

```
Iterable
└── Collection
    ├── List     → Ordenada, permite duplicados
    │   ├── ArrayList   (acceso rápido por índice)
    │   └── LinkedList  (inserción/eliminación rápidas)
    ├── Set      → Sin duplicados
    │   ├── HashSet     (sin orden garantizado)
    │   └── LinkedHashSet (mantiene orden de inserción)
    └── Queue    → Cola FIFO
        └── PriorityQueue
Map          → Pares clave-valor (no es Collection)
    ├── HashMap     (sin orden garantizado)
    └── LinkedHashMap (mantiene orden de inserción)
```

---

## 7.2 `List` — la más usada

```java
import java.util.ArrayList;
import java.util.List;

// Crear listas
List<String> vacia    = new ArrayList<>();
List<String> de       = List.of("Ana", "Luis", "María"); // inmutable
List<String> mutable  = new ArrayList<>(de);             // mutable, inicializada con de

// CRUD básico
mutable.add("Carlos");                  // agrega al final
mutable.add(0, "Primero");              // agrega en posición 0
mutable.set(1, "Ana Modificada");       // reemplaza en posición 1
mutable.remove("Luis");                 // elimina por valor
mutable.remove(0);                      // elimina por índice

// Consultas
System.out.println(mutable.get(0));     // obtiene por índice
System.out.println(mutable.size());     // tamaño
System.out.println(mutable.isEmpty());  // ¿vacía?
System.out.println(mutable.contains("María")); // ¿contiene?
System.out.println(mutable.indexOf("María"));  // índice de la primera ocurrencia

// Iterar
for (String nombre : mutable) {
    System.out.println(nombre);
}

// Iterar con índice
for (int i = 0; i < mutable.size(); i++) {
    System.out.println(i + ": " + mutable.get(i));
}

// Ordenar
mutable.sort(Comparator.naturalOrder());            // A-Z
mutable.sort(Comparator.reverseOrder());            // Z-A
mutable.sort(Comparator.comparingInt(String::length)); // por longitud

// Convertir entre tipos
String[] arreglo = mutable.toArray(new String[0]);
List<String> deArreglo = List.of(arreglo);
```

---

## 7.3 `Map` — pares clave-valor

```java
import java.util.HashMap;
import java.util.Map;

// Crear
Map<String, Integer> edades = new HashMap<>();
Map<String, String>  inmutable = Map.of("Ana", "ADMIN", "Luis", "EDITOR");

// Agregar y actualizar
edades.put("Ana", 25);
edades.put("Luis", 30);
edades.put("Ana", 26);  // reemplaza el valor existente para "Ana"

// Leer
System.out.println(edades.get("Ana"));              // 26
System.out.println(edades.getOrDefault("Pedro", 0)); // 0 — si no existe, valor por defecto
System.out.println(edades.containsKey("Luis"));      // true
System.out.println(edades.containsValue(30));        // true

// Eliminar
edades.remove("Luis");

// Iterar
for (Map.Entry<String, Integer> entry : edades.entrySet()) {
    System.out.println(entry.getKey() + " → " + entry.getValue());
}

// Iterar con forEach (lambda)
edades.forEach((nombre, edad) -> System.out.println(nombre + " tiene " + edad + " años"));

// Obtener solo claves o valores
Set<String>    claves  = edades.keySet();
Collection<Integer> valores = edades.values();

// computeIfAbsent: agrega solo si no existe
edades.computeIfAbsent("María", k -> 28);

// merge: combina valores existentes
edades.merge("Ana", 1, Integer::sum); // Ana = 26 + 1 = 27

// putIfAbsent: agrega solo si la clave no existe
edades.putIfAbsent("Carlos", 22);
```

---

## 7.4 `Set` — sin duplicados

```java
import java.util.HashSet;
import java.util.Set;

Set<String> roles = new HashSet<>();
roles.add("ADMIN");
roles.add("EDITOR");
roles.add("ADMIN");  // ignorado: ya existe

System.out.println(roles.size());        // 2
System.out.println(roles.contains("ADMIN")); // true

// Operaciones de conjunto
Set<String> rolesA = new HashSet<>(Set.of("ADMIN", "EDITOR"));
Set<String> rolesB = new HashSet<>(Set.of("EDITOR", "LECTOR"));

// Intersección
rolesA.retainAll(rolesB);
System.out.println(rolesA); // [EDITOR]

// Unión
rolesA.addAll(rolesB);

// Diferencia
rolesA.removeAll(rolesB);
```

---

## 7.5 Colecciones inmutables — fábricas modernas (Java 9+)

```java
// Java 9+: List.of, Set.of, Map.of — inmutables y sin nulls
List<String> lista = List.of("a", "b", "c");
Set<Integer> conjunto = Set.of(1, 2, 3);
Map<String, Integer> mapa = Map.of("uno", 1, "dos", 2);

// Para crear mutable a partir de inmutable:
var mutableLista = new ArrayList<>(lista);
var mutableMapa  = new HashMap<>(mapa);

// Java 10+: List.copyOf, Map.copyOf — copia inmutable de existente
List<String> copia = List.copyOf(mutableLista);
```

---

## 7.6 Genéricos — escribir código tipo-seguro y reutilizable

Los genéricos permiten crear clases y métodos que trabajan con **cualquier tipo**, con verificación en tiempo de compilación.

```java
// Sin genéricos (Java antiguo): peligroso
List lista = new ArrayList();
lista.add("hola");
lista.add(42);        // compila, pero puede fallar en runtime
String s = (String) lista.get(0); // cast manual
String x = (String) lista.get(1); // ❌ ClassCastException en runtime

// Con genéricos: seguro en compilación
List<String> listaSegura = new ArrayList<>();
listaSegura.add("hola");
// listaSegura.add(42); // ❌ Error de COMPILACIÓN — mucho mejor
String y = listaSegura.get(0); // sin cast
```

### Clase genérica

```java
// T es un parámetro de tipo (puede tener cualquier nombre, pero T es convención)
public class Caja<T> {
    private T contenido;

    public Caja(T contenido) {
        this.contenido = contenido;
    }

    public T getContenido() { return contenido; }

    public void setContenido(T contenido) { this.contenido = contenido; }

    public boolean estaVacia() { return contenido == null; }

    @Override
    public String toString() {
        return "Caja[" + contenido + "]";
    }
}

// Uso: el compilador verifica los tipos
Caja<String>  cajaTexto  = new Caja<>("Spring Boot");
Caja<Integer> cajaNúmero = new Caja<>(42);
Caja<Ticket>  cajaTicket = new Caja<>(new Ticket("Bug", "Descripción"));

// Con var:
var caja = new Caja<>("hola"); // infiere Caja<String>
```

### Método genérico

```java
// El tipo T se declara antes del tipo de retorno
public static <T> List<T> repetir(T elemento, int veces) {
    List<T> lista = new ArrayList<>();
    for (int i = 0; i < veces; i++) {
        lista.add(elemento);
    }
    return lista;
}

// El compilador infiere T:
List<String>  strings  = repetir("hola", 3); // [hola, hola, hola]
List<Integer> enteros  = repetir(0, 5);       // [0, 0, 0, 0, 0]
```

### Bounded type parameters (límites)

```java
// T debe ser Comparable (puede compararse con otros T)
public static <T extends Comparable<T>> T maximo(List<T> lista) {
    if (lista.isEmpty()) throw new NoSuchElementException("Lista vacía");
    T max = lista.get(0);
    for (T elemento : lista) {
        if (elemento.compareTo(max) > 0) max = elemento;
    }
    return max;
}

// Funciona con cualquier tipo que implemente Comparable
System.out.println(maximo(List.of(3, 1, 4, 1, 5, 9, 2))); // 9
System.out.println(maximo(List.of("banana", "apple", "cherry"))); // cherry

// Wildcard (?): cuando no necesitas referenciar el tipo
public static double sumarNumeros(List<? extends Number> numeros) {
    double suma = 0;
    for (Number n : numeros) {
        suma += n.doubleValue();
    }
    return suma;
}

sumarNumeros(List.of(1, 2, 3));     // acepta List<Integer>
sumarNumeros(List.of(1.5, 2.5));    // acepta List<Double>
```

---

## 7.7 `Optional<T>` como colección de 0 o 1 elemento

```java
// Optional como alternativa a null (revisado en módulo 03, ampliado aquí)
Optional<Ticket> opt = repositorio.buscarPorId(99L);

// Transformaciones encadenadas (igual que Stream)
String titulo = opt
    .filter(t -> "ABIERTO".equals(t.getEstado()))
    .map(Ticket::getTitulo)
    .map(String::toUpperCase)
    .orElse("SIN TÍTULO");

// ifPresentOrElse (Java 9+)
opt.ifPresentOrElse(
    t -> System.out.println("Encontrado: " + t.getTitulo()),
    () -> System.out.println("No encontrado")
);

// or: alternativa opcional (Java 9+)
Optional<Ticket> resultado = opt.or(() -> Optional.of(ticketPorDefecto));
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 7.1 — List y Map
Dado una lista de palabras, crea un `Map<String, Integer>` que cuente cuántas veces aparece cada palabra (frecuencia de palabras):

```java
List<String> palabras = List.of("java", "spring", "java", "boot", "spring", "java");
// Resultado esperado: {java=3, spring=2, boot=1}
```

<details>
<summary>🔍 Ver solución</summary>

```java
List<String> palabras = List.of("java", "spring", "java", "boot", "spring", "java");

Map<String, Integer> frecuencia = new HashMap<>();
for (String palabra : palabras) {
    frecuencia.merge(palabra, 1, Integer::sum);
}
System.out.println(frecuencia); // {java=3, spring=2, boot=1}

// Alternativa con getOrDefault:
Map<String, Integer> frecuencia2 = new HashMap<>();
for (String palabra : palabras) {
    frecuencia2.put(palabra, frecuencia2.getOrDefault(palabra, 0) + 1);
}
```
</details>

---

### Ejercicio 7.2 — Genéricos
Crea una clase genérica `Par<A, B>` (similar a un Tuple) con dos campos de tipos diferentes, sus getters, `toString`, `equals` y `hashCode`. Luego crea un método estático `List<Par<String, Integer>> contarPalabras(List<String> palabras)` que retorne pares (palabra, frecuencia).

<details>
<summary>🔍 Ver solución</summary>

```java
// Con record (mucho más conciso)
public record Par<A, B>(A primero, B segundo) {}

// Método:
public static List<Par<String, Integer>> contarPalabras(List<String> palabras) {
    Map<String, Integer> frecuencia = new HashMap<>();
    for (String p : palabras) {
        frecuencia.merge(p, 1, Integer::sum);
    }
    List<Par<String, Integer>> resultado = new ArrayList<>();
    frecuencia.forEach((palabra, count) -> resultado.add(new Par<>(palabra, count)));
    return resultado;
}

// Prueba:
var pares = contarPalabras(List.of("a", "b", "a", "c", "b", "a"));
pares.forEach(p -> System.out.println(p.primero() + ": " + p.segundo()));
```
</details>

---

### Ejercicio 7.3 — Colecciones inmutables
¿Por qué el siguiente código lanza una excepción? ¿Cómo lo corriges?

```java
List<String> nombres = List.of("Ana", "Luis");
nombres.add("María"); // ¿Qué pasa?
```

<details>
<summary>🔍 Ver solución</summary>

`List.of()` crea una lista **inmutable**. Llamar a `add()` lanza `UnsupportedOperationException`.

```java
// Corrección: envolver en ArrayList mutable
List<String> nombres = new ArrayList<>(List.of("Ana", "Luis"));
nombres.add("María"); // ✅ funciona
```
</details>

---

*[← Módulo 06](./06_interfaces_y_abstraccion.md) | [Índice](./README.md) | [Módulo 08 → Excepciones](./08_excepciones.md)*

