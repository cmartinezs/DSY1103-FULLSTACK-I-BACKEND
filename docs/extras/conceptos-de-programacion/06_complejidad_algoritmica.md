# Módulo 06 — Complejidad algorítmica (Big O)

> **Objetivo:** entender cómo medir si un algoritmo es eficiente, qué es la notación Big O y por qué elegir el algoritmo correcto importa más que elegir el lenguaje correcto.

---

## ¿Qué es la complejidad algorítmica?

La **complejidad algorítmica** mide **cómo crece el tiempo de ejecución (o el uso de memoria) de un algoritmo a medida que crece el tamaño de la entrada**. No mide segundos — mide cómo escala.

> **Analogía:** buscar un nombre en una lista de 10 personas es casi instantáneo en cualquier estrategia. Pero buscar en una lista de 10 millones de personas es donde la estrategia lo cambia todo.

La herramienta estándar para expresar complejidad es la **notación Big O** (`O(…)`), que describe el comportamiento en el **peor caso** conforme la entrada `n` tiende a infinito.

---

## Índice

1. [Notación Big O](#1-notación-big-o)
2. [O(1) — Constante](#2-o1--constante)
3. [O(log n) — Logarítmica](#3-olog-n--logarítmica)
4. [O(n) — Lineal](#4-on--lineal)
5. [O(n log n) — Lineal-logarítmica](#5-on-log-n--lineal-logarítmica)
6. [O(n²) — Cuadrática](#6-on--cuadrática)
7. [O(2ⁿ) — Exponencial](#7-o2ⁿ--exponencial)
8. [Complejidad espacial](#8-complejidad-espacial)
9. [Comparación visual y reglas prácticas](#9-comparación-visual-y-reglas-prácticas)
10. [📚 Literatura recomendada](#-literatura-recomendada)
11. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Notación Big O

Big O describe el **límite superior** del crecimiento de un algoritmo. Se ignoran constantes y términos menores porque lo que importa es el comportamiento a escala.

```
T(n) = 3n² + 5n + 100
        ↑      ↑    ↑
  dominante  menor constante
                         → O(n²)
```

**Reglas de simplificación:**

| Regla | Ejemplo |
|-------|---------|
| Eliminar constantes multiplicativas | `O(5n)` → `O(n)` |
| Eliminar términos menores | `O(n² + n)` → `O(n²)` |
| El peor caso importa | Si hay un `for` dentro de otro `for` → `O(n²)` |

---

## 2. O(1) — Constante

### 📖 Definición

El algoritmo siempre tarda **lo mismo**, sin importar el tamaño de la entrada. Es la complejidad ideal.

### ☕ Ejemplos en Java

```java
// Acceder a un elemento de un array por índice
int primero = numeros[0]; // O(1) — siempre una operación, sin importar el tamaño

// Insertar/consultar en un HashMap
map.put("clave", valor);  // O(1) amortizado
map.get("clave");         // O(1) amortizado

// Apilar o desapilar en un Stack
pila.push(elemento);      // O(1)
pila.pop();               // O(1)
```

```
n=10     → 1 operación
n=1.000  → 1 operación
n=1.000.000 → 1 operación
```

---

## 3. O(log n) — Logarítmica

### 📖 Definición

Cada paso **divide el problema a la mitad** (o en alguna fracción constante). Crece muy lentamente — duplicar la entrada solo añade un paso.

> **Analogía:** buscar una palabra en un diccionario físico. Abres por la mitad, decides si está antes o después y descartas la mitad. Repites. En 30 pasos puedes buscar entre mil millones de palabras.

### ☕ Ejemplos en Java

```java
// Búsqueda binaria (requiere array ORDENADO)
int[] arr = {1, 3, 5, 7, 9, 11, 13, 15};
int idx = Arrays.binarySearch(arr, 7); // O(log n)

// TreeMap y TreeSet (árbol rojo-negro)
TreeMap<String, Integer> mapa = new TreeMap<>();
mapa.put("clave", 1);  // O(log n) — mantiene orden
mapa.get("clave");     // O(log n)
```

```
n=8      → 3 pasos   (log₂ 8 = 3)
n=1.000  → 10 pasos
n=1.000.000 → 20 pasos
n=1.000.000.000 → 30 pasos
```

---

## 4. O(n) — Lineal

### 📖 Definición

El tiempo crece **proporcionalmente** al tamaño de la entrada. Doblar `n` dobla el tiempo.

### ☕ Ejemplos en Java

```java
// Recorrer una lista: debes visitar cada elemento una vez
for (String nombre : nombres) {      // O(n)
    System.out.println(nombre);
}

// Búsqueda lineal (en lista no ordenada): en el peor caso recorres todo
boolean encontrado = lista.contains("objetivo"); // O(n) en ArrayList

// Suma de todos los elementos
int suma = lista.stream().mapToInt(Integer::intValue).sum(); // O(n)
```

```
n=10     → 10 operaciones
n=1.000  → 1.000 operaciones
n=1.000.000 → 1.000.000 operaciones
```

---

## 5. O(n log n) — Lineal-logarítmica

### 📖 Definición

Más lento que lineal pero mucho mejor que cuadrático. Es la complejidad de los **algoritmos de ordenamiento eficientes**.

### ☕ Ejemplos en Java

```java
// Arrays.sort() y Collections.sort() usan Timsort → O(n log n)
Arrays.sort(numeros);            // O(n log n)
Collections.sort(lista);         // O(n log n)

lista.stream()
    .sorted()
    .collect(Collectors.toList()); // O(n log n)
```

```
n=10     → ~33 operaciones
n=1.000  → ~10.000 operaciones
n=1.000.000 → ~20.000.000 operaciones (vs 1.000.000.000.000 en O(n²))
```

---

## 6. O(n²) — Cuadrática

### 📖 Definición

Aparece cuando hay **un bucle dentro de otro bucle**, ambos dependientes de `n`. Duplicar la entrada **cuadruplica** el tiempo.

### ☕ Ejemplos en Java

```java
// ❌ Buscar duplicados con dos bucles anidados — O(n²)
for (int i = 0; i < lista.size(); i++) {
    for (int j = i + 1; j < lista.size(); j++) {
        if (lista.get(i).equals(lista.get(j))) {
            System.out.println("Duplicado: " + lista.get(i));
        }
    }
}

// ✅ Con HashSet — O(n) para el mismo problema
Set<String> vistos = new HashSet<>();
for (String elemento : lista) {
    if (!vistos.add(elemento)) {
        System.out.println("Duplicado: " + elemento);
    }
}
```

```
n=10     → 100 operaciones
n=1.000  → 1.000.000 operaciones
n=10.000 → 100.000.000 operaciones  ← empieza a doler
```

### ⚠️ Señal de alarma

Cuando ves dos `for` anidados sobre la misma colección, pregúntate: *¿hay una forma de hacerlo con una sola pasada o usando un Map/Set?*

---

## 7. O(2ⁿ) — Exponencial

### 📖 Definición

El tiempo **se duplica con cada elemento adicional**. Solo es aceptable con entradas muy pequeñas.

### ☕ Ejemplo en Java

```java
// Fibonacci recursivo ingenuo — O(2ⁿ)
public int fibonacci(int n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2); // calcula los mismos valores MUCHAS veces
}
```

```
fibonacci(5)  → ~15 llamadas
fibonacci(20) → ~21.891 llamadas
fibonacci(40) → ~2.692.537.527 llamadas  ← inaceptable
```

```java
// ✅ Fibonacci con memoización — O(n)
Map<Integer, Long> memo = new HashMap<>();
public long fibonacciMemo(int n) {
    if (n <= 1) return n;
    if (memo.containsKey(n)) return memo.get(n); // reutiliza resultado ya calculado
    long resultado = fibonacciMemo(n - 1) + fibonacciMemo(n - 2);
    memo.put(n, resultado);
    return resultado;
}
```

---

## 8. Complejidad espacial

La notación Big O también mide **cuánta memoria extra** usa un algoritmo:

```java
// O(1) espacial — usa solo variables escalares, sin importar n
public int suma(List<Integer> lista) {
    int total = 0;
    for (int n : lista) total += n;
    return total;
}

// O(n) espacial — crea una estructura del mismo tamaño que la entrada
public List<Integer> duplicar(List<Integer> lista) {
    List<Integer> resultado = new ArrayList<>(); // nueva lista de tamaño n
    for (int n : lista) resultado.add(n * 2);
    return resultado;
}

// O(n) espacial — recursividad: n frames en el stack
public int factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1); // n llamadas en el stack simultáneamente
}
```

---

## 9. Comparación visual y reglas prácticas

### Crecimiento según tamaño de entrada

| n | O(1) | O(log n) | O(n) | O(n log n) | O(n²) | O(2ⁿ) |
|---|------|---------|------|-----------|------|------|
| 10 | 1 | 3 | 10 | 33 | 100 | 1.024 |
| 100 | 1 | 7 | 100 | 664 | 10.000 | 10³⁰ |
| 1.000 | 1 | 10 | 1.000 | 9.966 | 1.000.000 | 💥 |
| 1.000.000 | 1 | 20 | 1.000.000 | ~20M | 10¹² | 💥 |

### Jerarquía de mejor a peor

```
O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(2ⁿ)
 ↑                                               ↑
mejor                                           peor
```

### Reglas prácticas para el día a día

| Si ves esto en el código... | Complejidad probable |
|-----------------------------|---------------------|
| Acceso por índice o clave en Map | O(1) |
| Un solo `for` / `while` sobre la colección | O(n) |
| `Collections.sort()` o `.sorted()` | O(n log n) |
| Un `for` dentro de otro `for` | O(n²) — revisa si se puede mejorar |
| Búsqueda binaria, `TreeMap`, `TreeSet` | O(log n) |
| Recursión que se llama a sí misma dos veces | Posible O(2ⁿ) — usa memoización |
| Eliminar duplicados con `HashSet` | O(n) |

### ¿Cuándo importa optimizar?

```
n < 1.000       → casi cualquier algoritmo funciona en milisegundos
n ~ 10.000      → O(n²) puede volverse perceptible (~100M ops)
n ~ 100.000     → O(n²) es inaceptable; apunta a O(n log n) o mejor
n > 1.000.000   → necesitas O(n) o O(log n) para que sea práctico
```

> 💡 **Regla de oro:** primero haz que funcione correctamente. Luego mide. Luego optimiza — solo si hay un problema real de rendimiento.

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Grokking Algorithms** | Aditya Bhargava | Principiante | El capítulo de Big O notation es el más claro para principiantes. Explica O(n), O(log n) y O(n²) con dibujos y analogías cotidianas |
| **Introduction to Algorithms (CLRS)** | Cormen, Leiserson, Rivest, Stein | Avanzado | El tratamiento formal y riguroso de la notación asintótica. Capítulo 3: "Growth of Functions" |
| **The Algorithm Design Manual** | Steven Skiena | Intermedio / Avanzado | La segunda parte ("The Hitchhiker's Guide to Algorithms") es una enciclopedia práctica de complejidades |
| **Algorithms** | Sedgewick & Wayne | Intermedio | Analiza la complejidad de cada estructura y algoritmo de forma práctica con Java |
| **Programming Pearls** | Jon Bentley | Intermedio | Casos reales donde elegir el algoritmo correcto marcó la diferencia de horas a milisegundos |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Big-O Cheat Sheet** | https://www.bigocheatsheet.com | Referencia rápida de la complejidad temporal y espacial de las estructuras y algoritmos más comunes |
| **VisuAlgo — Sorting** | https://visualgo.net/es/sorting | Ve visualmente cómo distintos algoritmos de ordenamiento (O(n²) vs O(n log n)) se comportan con los mismos datos |
| **CS50 — Algorithms (Harvard)** | https://cs50.harvard.edu/x/2024/weeks/3/ | La clase de algoritmos y Big O de CS50, gratuita y muy accesible |
| **Baeldung — Big O in Java** | https://www.baeldung.com/java-algorithm-complexity | Análisis de complejidad de operaciones comunes de las colecciones Java |
| **Coursera — Algorithms I & II (Princeton)** | https://www.coursera.org/learn/algorithms-part1 | El curso de Sedgewick gratis en Coursera. Una de las mejores formaciones de algoritmos disponibles |

