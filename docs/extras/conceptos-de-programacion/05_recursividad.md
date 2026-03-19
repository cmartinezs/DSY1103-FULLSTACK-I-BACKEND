# Módulo 05 — Recursividad

> **Objetivo:** entender qué es la recursividad, cómo funciona internamente, cuándo es la herramienta correcta y cuándo no. Es uno de los conceptos que más cuesta al principio y más se disfruta cuando hace clic.

---

## ¿Qué es la recursividad?

Una función/método es **recursivo** cuando **se llama a sí mismo** como parte de su propia definición para resolver una versión más pequeña del mismo problema.

> **Analogía:** para saber en qué piso estás de un edificio sin mirar los números, le preguntas a la persona del piso de abajo. Esa persona hace lo mismo: le pregunta a la de más abajo. Hasta que alguien llega a la planta baja y dice "estoy en el piso 0". A partir de ahí, cada persona suma 1 y responde al de arriba. Ese proceso es recursividad.

La recursividad es, en esencia, una **alternativa a los bucles**: ambos sirven para repetir algo, pero con enfoques distintos.

---

## Índice

1. [Anatomía de una función recursiva](#1-anatomía-de-una-función-recursiva)
2. [Cómo funciona internamente](#2-cómo-funciona-internamente)
3. [Ejemplos clásicos](#3-ejemplos-clásicos)
4. [Recursividad vs iteración](#4-recursividad-vs-iteración)
5. [Cuándo usar (y cuándo no)](#5-cuándo-usar-y-cuándo-no)
6. [Errores frecuentes](#6-errores-frecuentes)
7. [📚 Literatura recomendada](#-literatura-recomendada)
8. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Anatomía de una función recursiva

Toda función recursiva válida tiene **dos partes obligatorias**:

```
función recursiva(problema)
├── CASO BASE    → condición de parada, resuelve el caso más simple sin recursión
└── CASO RECURSIVO → reduce el problema y se llama a sí misma con esa versión reducida
```

Si falta el **caso base** → el programa cae en recursión infinita → `StackOverflowError`.  
Si el **caso recursivo** no reduce el problema → ídem.

### ☕ En Java

```java
// Calcular el factorial de n (n! = n × (n-1) × (n-2) × ... × 1)
public int factorial(int n) {
    // CASO BASE: el problema más simple que podemos resolver directamente
    if (n == 0 || n == 1) {
        return 1;
    }
    // CASO RECURSIVO: reducimos el problema y nos llamamos con la versión reducida
    return n * factorial(n - 1);
}
```

```
factorial(5)
  = 5 * factorial(4)
        = 4 * factorial(3)
              = 3 * factorial(2)
                    = 2 * factorial(1)
                          = 1           ← caso base: devuelve 1
                    = 2 * 1 = 2
              = 3 * 2 = 6
        = 4 * 6 = 24
  = 5 * 24 = 120
```

---

## 2. Cómo funciona internamente

Cada llamada recursiva crea un nuevo **frame** en el **call stack** (pila de llamadas). Cuando se alcanza el caso base, los frames se van resolviendo de vuelta en orden inverso.

```
Estado del call stack durante factorial(4):

┌─────────────────┐  ← tope del stack
│ factorial(1)=1  │
├─────────────────┤
│ factorial(2)    │  espera resultado de factorial(1)
├─────────────────┤
│ factorial(3)    │  espera resultado de factorial(2)
├─────────────────┤
│ factorial(4)    │  espera resultado de factorial(3)
├─────────────────┤
│     main()      │  espera resultado de factorial(4)
└─────────────────┘  ← base del stack
```

Cada frame ocupa memoria. Con entradas muy grandes, el stack se agota → `StackOverflowError`.

---

## 3. Ejemplos clásicos

### Suma de los primeros N números

```java
public int sumar(int n) {
    if (n <= 0) return 0;           // caso base
    return n + sumar(n - 1);        // caso recursivo
}
// sumar(4) = 4 + sumar(3) = 4 + 3 + sumar(2) = 4 + 3 + 2 + sumar(1) = 4+3+2+1 = 10
```

### Contar hacia atrás

```java
public void contarAtras(int n) {
    if (n < 0) return;              // caso base: para en 0
    System.out.println(n);
    contarAtras(n - 1);             // caso recursivo
}
// contarAtras(3) → imprime: 3, 2, 1, 0
```

### Búsqueda binaria (recursiva)

```java
// Busca un elemento en un array ORDENADO
public int busquedaBinaria(int[] arr, int objetivo, int inicio, int fin) {
    if (inicio > fin) return -1;    // caso base: no encontrado

    int medio = (inicio + fin) / 2;

    if (arr[medio] == objetivo) return medio;               // caso base: encontrado
    if (arr[medio] > objetivo)  return busquedaBinaria(arr, objetivo, inicio, medio - 1); // busca izquierda
    else                        return busquedaBinaria(arr, objetivo, medio + 1, fin);    // busca derecha
}
```

### Recorrer una estructura en árbol

La recursividad brilla con estructuras anidadas o en árbol, donde la profundidad es desconocida:

```java
// Calcular la profundidad de un JSON/árbol anidado
// (pseudocódigo — la idea aplica a cualquier estructura árbol)
public int profundidad(Nodo nodo) {
    if (nodo.esHoja()) return 0;    // caso base: sin hijos
    int maxHijo = 0;
    for (Nodo hijo : nodo.getHijos()) {
        maxHijo = Math.max(maxHijo, profundidad(hijo)); // recursivo
    }
    return maxHijo + 1;
}
```

---

## 4. Recursividad vs iteración

El mismo problema se puede resolver con recursividad o con un bucle. A veces una es claramente mejor:

```java
// ITERATIVO — factorial con bucle
public int factorialIterativo(int n) {
    int resultado = 1;
    for (int i = 2; i <= n; i++) {
        resultado *= i;
    }
    return resultado;
}

// RECURSIVO — factorial con recursión
public int factorialRecursivo(int n) {
    if (n <= 1) return 1;
    return n * factorialRecursivo(n - 1);
}
```

| | Iterativo | Recursivo |
|--|-----------|-----------|
| **Legibilidad** | Clara para lógica lineal | Clara para problemas con subestructura |
| **Memoria** | O(1) — usa variables locales | O(n) — crea frames en el stack |
| **Riesgo** | Bucle infinito (más fácil de detectar) | StackOverflowError |
| **Velocidad** | Generalmente más rápido | Overhead por llamadas al stack |
| **Cuándo brilla** | Repetición lineal | Árboles, grafos, divide y vencerás |

---

## 5. Cuándo usar (y cuándo no)

### ✅ Úsala cuando

- El problema tiene **subestructura recursiva natural**: se reduce a versiones más pequeñas del mismo problema.
- Trabajas con **árboles, grafos, estructuras anidadas** (directorios, JSON, XML, menús).
- El algoritmo se basa en **divide y vencerás**: merge sort, quick sort, búsqueda binaria.
- La versión recursiva es **significativamente más legible** que la iterativa.

### ❌ Evítala cuando

- El problema es simplemente **repetición lineal** — un bucle es más eficiente y claro.
- La entrada puede ser **muy grande**: riesgo de `StackOverflowError`.
- Necesitas **máxima performance**: la overhead del stack puede ser significativa.

---

## 6. Errores frecuentes

### ❌ Sin caso base (recursión infinita)

```java
public int factorial(int n) {
    return n * factorial(n - 1); // ❌ nunca para → StackOverflowError
}
```

### ❌ Caso base inalcanzable

```java
public int factorial(int n) {
    if (n == 0) return 1;
    return n * factorial(n + 1); // ❌ n crece en lugar de reducirse → nunca llega a 0
}
```

### ❌ Olvidar el return en el caso recursivo

```java
public int factorial(int n) {
    if (n <= 1) return 1;
    n * factorial(n - 1); // ❌ falta 'return' → el compilador lo detecta en Java
}
```

### ✅ Regla mnemotécnica para diseñar recursividad

```
1. ¿Cuál es el caso más simple que puedo resolver directamente? → CASO BASE
2. ¿Cómo reduzco el problema actual a una versión más pequeña? → CASO RECURSIVO
3. ¿Confío en que la llamada recursiva resolverá la versión más pequeña? → SÍ (fe recursiva)
```

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Grokking Algorithms** | Aditya Bhargava | Principiante | Capítulo dedicado a recursividad con diagramas que muestran el call stack visualmente. El mejor punto de partida |
| **The Little Schemer** | Friedman & Felleisen | Principiante / Intermedio | Libro de diálogo que enseña a pensar recursivamente desde cero. Aunque usa Lisp, el pensamiento es universal y muy efectivo |
| **Structure and Interpretation of Computer Programs (SICP)** | Abelson & Sussman | Avanzado | Los primeros capítulos construyen todo el pensamiento computacional sobre recursividad y abstracción |
| **Introduction to Algorithms (CLRS)** | Cormen et al. | Avanzado | Divide y vencerás (capítulo 4) — la aplicación más potente de la recursividad en algoritmia |
| **Algorithms** | Sedgewick & Wayne | Intermedio | Recursividad aplicada a sorting (mergesort, quicksort) y búsqueda binaria con implementaciones Java |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **VisuAlgo — Recursion** | https://visualgo.net/es/recursion | Visualización animada del call stack durante una ejecución recursiva |
| **Python Tutor (Java)** | https://pythontutor.com/java.html | Ejecuta código Java recursivo paso a paso y muestra cada frame del stack en tiempo real |
| **Khan Academy — Recursión** | https://es.khanacademy.org/computing/computer-science/algorithms/recursive-algorithms/a/recursion | Introducción visual a la recursividad con ejercicios |
| **Baeldung — Recursion in Java** | https://www.baeldung.com/java-recursion | Ejemplos de recursividad en Java con análisis de complejidad |
| **The Recursion Fairy (artículo)** | https://jeffe.cs.illinois.edu/teaching/algorithms/book/01-recursion.pdf | Capítulo gratuito del libro de Jeff Erickson — explica la "fe recursiva" de forma magistral |

