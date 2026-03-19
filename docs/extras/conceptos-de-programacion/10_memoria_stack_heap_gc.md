# Módulo 10 — Memoria: Stack, Heap y Garbage Collection

> **Objetivo:** entender cómo gestiona la memoria un programa en ejecución, qué diferencia hay entre el stack y el heap, qué es una referencia y cómo funciona el garbage collector. Estos conceptos explican muchos comportamientos que de otro modo parecen "magia".

---

## ¿Por qué importa entender la memoria?

- Explica por qué `==` no compara el contenido de objetos en Java.
- Explica el `NullPointerException` — uno de los errores más frecuentes.
- Explica por qué pasar un objeto a un método puede modificarlo, pero pasar un `int` no.
- Explica el `StackOverflowError` de la recursividad infinita.
- Explica por qué Java no tiene `free()` como C, pero aun así puede tener fugas de memoria.

---

## Índice

1. [Las dos zonas de memoria: Stack y Heap](#1-las-dos-zonas-de-memoria-stack-y-heap)
2. [El Stack (Pila de llamadas)](#2-el-stack-pila-de-llamadas)
3. [El Heap](#3-el-heap)
4. [Referencias](#4-referencias)
5. [Paso por valor vs paso por referencia](#5-paso-por-valor-vs-paso-por-referencia)
6. [null — la referencia vacía](#6-null--la-referencia-vacía)
7. [Garbage Collection](#7-garbage-collection)
8. [Fugas de memoria en lenguajes con GC](#8-fugas-de-memoria-en-lenguajes-con-gc)
9. [Tabla resumen](#9-tabla-resumen)
10. [📚 Literatura recomendada](#-literatura-recomendada)
11. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Las dos zonas de memoria: Stack y Heap

Cuando una aplicación Java se ejecuta, la JVM divide la memoria en (al menos) dos zonas fundamentales:

```
┌──────────────────────────────────────────────┐
│                   MEMORIA JVM                │
│                                              │
│  ┌─────────────┐       ┌──────────────────┐  │
│  │    STACK    │       │      HEAP        │  │
│  │             │       │                  │  │
│  │ Variables   │       │ Objetos creados  │  │
│  │ primitivas  │       │ con 'new'        │  │
│  │             │       │                  │  │
│  │ Referencias │──────▶│ String, List,    │  │
│  │ a objetos   │       │ arrays, POJOs... │  │
│  │             │       │                  │  │
│  │ Frames de   │       │ Vive mientras    │  │
│  │ métodos     │       │ haya referencias │  │
│  └─────────────┘       └──────────────────┘  │
│   Tamaño fijo            Tamaño dinámico      │
│   Rápido                 Más lento            │
│   LIFO                   Gestionado por GC    │
└──────────────────────────────────────────────┘
```

| | Stack | Heap |
|--|-------|------|
| **Qué almacena** | Variables primitivas, referencias, frames de métodos | Objetos (instancias de clases) |
| **Tamaño** | Fijo y limitado (~512KB–1MB por hilo) | Grande y dinámico (toda la RAM disponible) |
| **Gestión** | Automática (LIFO) | Automática (Garbage Collector) |
| **Velocidad** | Muy rápido | Más lento (fragmentación, GC) |
| **Error típico** | `StackOverflowError` | `OutOfMemoryError` |
| **Alcance** | Solo el método actual | Toda la aplicación (mientras haya referencia) |

---

## 2. El Stack (Pila de llamadas)

### 📖 Definición

El **stack** almacena los **frames de ejecución** de los métodos. Cada vez que se llama un método, se crea un **frame** con:
- Las **variables locales** del método
- Los **parámetros** recibidos
- El **punto de retorno** (a dónde volver cuando el método termine)

Cuando el método termina, su frame se elimina automáticamente del stack (LIFO).

### ☕ Visualización en Java

```java
public class EjemploStack {

    public static void main(String[] args) {
        int a = 5;              // 'a' vive en el frame de main
        int resultado = doble(a);
        System.out.println(resultado);
    }

    static int doble(int numero) {
        int multiplicador = 2;  // vive en el frame de doble
        return numero * multiplicador;
    }
}
```

```
Cuando se ejecuta doble(5):

┌──────────────────────┐ ← tope
│ Frame: doble(5)      │
│   numero = 5         │
│   multiplicador = 2  │
│   retorna a: main:3  │
├──────────────────────┤
│ Frame: main          │
│   a = 5              │
│   resultado = ???    │  (esperando)
│   args = [referencia]│
└──────────────────────┘

Cuando doble() retorna 10:
└─ Frame de doble se elimina
└─ Frame de main: resultado = 10
```

### ⚠️ StackOverflowError

Cuando la recursión es infinita (o muy profunda), el stack se llena:

```java
public int infinito(int n) {
    return infinito(n + 1); // ❌ sin caso base → frame tras frame → stack overflow
}
```

---

## 3. El Heap

### 📖 Definición

El **heap** es la región de memoria donde se almacenan **todos los objetos** creados con `new`. Es grande, dinámico y compartido entre todos los hilos de la aplicación.

### ☕ Visualización en Java

```java
public static void main(String[] args) {
    // Stack: 'persona' guarda una REFERENCIA (una dirección de memoria)
    // Heap: el objeto Persona con sus campos se crea aquí
    Persona persona = new Persona("Ana", 25);
    //        ↑                   ↑
    //   referencia         objeto en el heap
    //   (en el stack)

    String nombre = "Luis";       // también va al heap (String es objeto)
    int    edad   = 30;           // va al STACK (primitivo, no objeto)
    int[]  notas  = {7, 8, 9};    // el array va al heap; 'notas' es referencia en stack
}
```

```
STACK                    HEAP
┌────────────────┐       ┌──────────────────────────┐
│ persona → ─────┼──────▶│ Persona { "Ana", 25 }    │
│ nombre  → ─────┼──────▶│ String "Luis"            │
│ edad    = 30   │       │ int[] { 7, 8, 9 }        │
│ notas   → ─────┼──────▶│                          │
└────────────────┘       └──────────────────────────┘
```

---

## 4. Referencias

### 📖 Definición

Una **referencia** es una variable que almacena la **dirección de memoria** de un objeto en el heap, no el objeto en sí. Piénsala como un "número de casa": no es la casa, pero te dice dónde está.

### ☕ Consecuencias en Java

```java
// Dos referencias al MISMO objeto
Persona p1 = new Persona("Ana", 25);
Persona p2 = p1;                     // p2 copia la REFERENCIA, no el objeto

p2.setNombre("María");               // modifica el objeto al que apuntan AMBAS
System.out.println(p1.getNombre()); // "María" — p1 y p2 son el mismo objeto

// Comparación de referencias vs contenido
String s1 = new String("Hola");
String s2 = new String("Hola");

System.out.println(s1 == s2);       // false — referencias distintas (objetos distintos)
System.out.println(s1.equals(s2));  // true  — contenido igual
```

```
ANTES de p2.setNombre():        DESPUÉS de p2.setNombre():

STACK        HEAP               STACK        HEAP
p1 → ──┐                       p1 → ──┐
       ▼                              ▼
   { "Ana", 25 }                  { "María", 25 }
       ▲                              ▲
p2 → ──┘                       p2 → ──┘
```

---

## 5. Paso por valor vs paso por referencia

### 📖 Definición

Cuando pasas algo a un método, ¿qué recibe exactamente? Depende del tipo:

- **Tipos primitivos** (`int`, `double`, `boolean`, etc.): se pasa una **copia del valor**. El método no puede modificar la variable original.
- **Objetos**: se pasa una **copia de la referencia**. El método puede modificar el objeto al que apunta (pero no puede hacer que la variable original apunte a otro objeto).

> 📌 Java es siempre **paso por valor** — pero cuando el valor es una referencia, el efecto parece "paso por referencia".

### ☕ En Java

```java
// Tipo primitivo: el método recibe una COPIA del valor
public void incrementar(int numero) {
    numero++;  // modifica solo la copia local
}

int x = 5;
incrementar(x);
System.out.println(x); // 5 — x no cambió

// Objeto: el método recibe una COPIA DE LA REFERENCIA
public void cambiarNombre(Persona p) {
    p.setNombre("María"); // modifica el objeto en el heap → SÍ afecta al original
}

Persona persona = new Persona("Ana", 25);
cambiarNombre(persona);
System.out.println(persona.getNombre()); // "María" — el objeto fue modificado

// PERO no puede cambiar a qué apunta la variable original
public void reasignar(Persona p) {
    p = new Persona("Luis", 30); // crea nuevo objeto, reasigna la copia local de la referencia
                                  // NO afecta a la variable 'persona' del llamador
}

reasignar(persona);
System.out.println(persona.getNombre()); // "María" — persona sigue apuntando al mismo objeto
```

---

## 6. null — la referencia vacía

### 📖 Definición

`null` significa que una referencia **no apunta a ningún objeto**. Es como tener el número de una casa que no existe. Intentar usar una referencia `null` lanza `NullPointerException`.

### ☕ En Java

```java
Persona persona = null; // la referencia existe, pero no apunta a ningún objeto

// Esto lanza NullPointerException:
persona.getNombre(); // ❌ ¿getNombre() de qué objeto? No hay objeto.

// Verificar antes de usar
if (persona != null) {
    System.out.println(persona.getNombre()); // seguro
}

// Java 8+: Optional como alternativa a null
Optional<Persona> posiblePersona = Optional.ofNullable(encontrarPersona(1L));
posiblePersona.ifPresent(p -> System.out.println(p.getNombre()));
String nombre = posiblePersona.map(Persona::getNombre).orElse("Desconocido");
```

### 🌐 Perspectiva universal

`null` (o su equivalente) existe en casi todos los lenguajes:

| Lenguaje | Valor nulo | Protección |
|----------|-----------|-----------|
| Java | `null` | `Optional<T>`, `if != null` |
| Kotlin | `null` | Tipos nullables (`String?`), operador `?.` |
| Python | `None` | `if valor is not None` |
| JavaScript | `null` / `undefined` | Optional chaining (`?.`) |
| C# | `null` | Nullable reference types, `??` |
| Go | `nil` | Verificación explícita |

> 💡 Tony Hoare, inventor del `null`, lo llamó su "error de mil millones de dólares".

---

## 7. Garbage Collection

### 📖 Definición

El **Garbage Collector** (GC) es un proceso automático que **detecta y libera la memoria** de los objetos en el heap que ya no son accesibles (ya no tienen ninguna referencia que apunte a ellos).

> **En C/C++**: el programador debe liberar la memoria manualmente con `free()` / `delete`. Olvidarse = **fuga de memoria**. Liberar dos veces = **crash**.  
> **En Java, Python, C#, JavaScript**: el GC lo hace automáticamente. Pero no es magia: puede haber problemas si el código mantiene referencias innecesariamente.

### ☕ Cómo funciona en Java (conceptual)

```java
// Paso 1: crear objetos
Persona p1 = new Persona("Ana");    // objeto A en el heap
Persona p2 = new Persona("Luis");   // objeto B en el heap

// Paso 2: reasignar la referencia
p1 = p2; // p1 ahora apunta al objeto B ("Luis")
           // objeto A ("Ana") ya no tiene ninguna referencia → elegible para GC

// Paso 3: el GC eventualmente libera el objeto A
// (no ocurre en un momento determinístico — el GC decide cuándo)
```

```
ANTES:                              DESPUÉS de p1 = p2:
p1 ──▶ { "Ana" }                   p1 ─┐
p2 ──▶ { "Luis" }                  p2 ─┴──▶ { "Luis" }
                                   
                                   { "Ana" } ← sin referencia → GC lo elimina
```

### El ciclo de vida de un objeto

```
new Persona() → objeto creado en heap
     ↓
en uso (alcanzable desde el código)
     ↓
ninguna referencia apunta a él → objeto "muerto"
     ↓
GC lo detecta y libera la memoria
```

### ⚠️ No puedes controlar cuándo actúa el GC

```java
System.gc(); // solo es una *sugerencia* — la JVM puede ignorarla
             // nunca dependas de que el GC actúe en un momento específico
```

---

## 8. Fugas de memoria en lenguajes con GC

Aunque el GC evita la mayoría de fugas, aún pueden ocurrir si el código **mantiene referencias a objetos que ya no necesita**.

### ☕ Ejemplos en Java

```java
// ❌ Fuga: lista estática que crece indefinidamente
public class Cache {
    private static final List<Objeto> cache = new ArrayList<>();

    public static void agregar(Objeto obj) {
        cache.add(obj); // se agrega, pero nunca se elimina
        // el GC no puede liberar estos objetos porque la lista los referencia
    }
}

// ❌ Fuga: listeners/observers que no se dan de baja
boton.addActionListener(miListener); // agrega referencia a miListener
// Si nunca se hace boton.removeActionListener(miListener),
// miListener (y todo lo que él referencia) no puede ser recolectado

// ✅ Solución: eliminar referencias cuando ya no se necesitan
cache.remove(obj);                          // limpiar explícitamente
boton.removeActionListener(miListener);     // dar de baja el listener
```

### Síntomas de fuga de memoria

- El uso de memoria crece **continuamente** sin bajar.
- Eventualmente aparece `java.lang.OutOfMemoryError: Java heap space`.
- El GC se ejecuta **muy frecuentemente** pero no libera mucho.

---

## 9. Tabla resumen

| Concepto | Definición | Error relacionado |
|----------|-----------|-------------------|
| **Stack** | Zona de memoria para frames de métodos y variables locales | `StackOverflowError` (recursión infinita) |
| **Heap** | Zona para objetos creados con `new` | `OutOfMemoryError` (heap lleno) |
| **Referencia** | Variable que guarda la dirección de un objeto en el heap | `NullPointerException` (referencia sin objeto) |
| **null** | Referencia que no apunta a ningún objeto | `NullPointerException` al invocar métodos |
| **Paso por valor** | Se copia el valor (primitivo) o la referencia (objeto) | Confusión sobre si el original se modifica |
| **Garbage Collector** | Proceso que libera objetos sin referencias en el heap | Fugas de memoria si se mantienen refs innecesarias |

### Regla mnemotécnica

```
Primitivos → van al STACK → se copian al pasar a métodos → desaparecen solos al salir del bloque
Objetos    → van al HEAP  → se acceden por REFERENCIA   → el GC los elimina cuando nadie los referencia
```

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Java Performance: The Definitive Guide** | Scott Oaks | Avanzado | El libro más completo sobre la JVM, el heap, el GC y cómo tunarlos. Imprescindible para entender cómo Java gestiona la memoria en producción |
| **Effective Java** | Joshua Bloch | Intermedio | Ítem 7: "Eliminate obsolete object references" — fugas de memoria en Java. Ítem 6: evitar crear objetos innecesarios. Directamente sobre el heap |
| **Understanding the JVM: Advanced Features and Best Practices** | Zhou Zhiming | Avanzado | El tratamiento más profundo del modelo de memoria JVM, GC algorithms (G1, ZGC, Shenandoah) y la gestión del heap |
| **Java in a Nutshell** | Evans & Flanagan | Principiante / Intermedio | Cubre la gestión de memoria de Java de forma accesible — buena introducción antes de los libros más avanzados |
| **Optimizing Java** | Evans, Gough & Newland | Avanzado | Capítulos dedicados al heap, GC tuning y análisis de fugas de memoria con herramientas reales |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Python Tutor (Java)** | https://pythontutor.com/java.html | Visualiza en tiempo real el stack y el heap mientras ejecutas código Java paso a paso — la mejor herramienta para entender referencias y frames |
| **Oracle — Java GC Tuning Guide** | https://docs.oracle.com/en/java/javase/21/gctuning/ | Guía oficial del Garbage Collector de Java 21: G1GC, ZGC, parámetros y monitoreo |
| **Baeldung — Java Memory Management** | https://www.baeldung.com/java-memory-management-interview-questions | Conceptos de memoria en Java explicados con preguntas y respuestas de entrevista |
| **JVM Internals — artículo** | https://blog.jamesdbloom.com/JVMInternals.html | Explicación visual y detallada de la arquitectura interna de la JVM: stack, heap, method area y GC |
| **VisualVM — Herramienta** | https://visualvm.github.io | Herramienta gratuita para monitorear el heap, el GC y las fugas de memoria en aplicaciones Java en tiempo real |
| **Eclipse Memory Analyzer (MAT)** | https://eclipse.dev/mat/ | Herramienta de análisis de heap dumps — para diagnosticar fugas de memoria en producción |

