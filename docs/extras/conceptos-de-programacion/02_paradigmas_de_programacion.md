# Módulo 02 — Paradigmas de programación

> **Objetivo:** entender qué es un paradigma, por qué existen varios y cómo se relacionan con los lenguajes que usamos. Sin este mapa mental, muchas decisiones de diseño de código parecen arbitrarias.

---

## ¿Qué es un paradigma?

Un **paradigma de programación** es una **forma de pensar y estructurar el código**: un conjunto de principios, ideas y restricciones que guían cómo se organiza la solución a un problema.

> **Analogía:** imagina que tienes que construir una mesa. Un carpintero la hace con madera y ensamble; un soldador la hace con metal y soldadura; un diseñador de mobiliario modular la hace con piezas intercambiables. El resultado es una mesa en los tres casos, pero el enfoque, las herramientas y el proceso son completamente distintos. Los paradigmas son esos enfoques.

Los paradigmas **no son exclusivos ni excluyentes**: un lenguaje moderno como Java o Python soporta varios a la vez.

---

## Índice

1. [Imperativo](#1-imperativo)
2. [Declarativo](#2-declarativo)
3. [Orientado a Objetos (OOP)](#3-orientado-a-objetos-oop)
4. [Funcional](#4-funcional)
5. [¿Cuál usar?](#5-cuál-usar)
6. [Java y los paradigmas](#6-java-y-los-paradigmas)
7. [Tabla resumen](#7-tabla-resumen)
8. [📚 Literatura recomendada](#-literatura-recomendada)
9. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Imperativo

### 📖 Definición

El paradigma **imperativo** es el más cercano a cómo funciona el hardware: le dices al programa **exactamente qué hacer, paso a paso, en qué orden**. El código describe *cómo* resolver el problema mediante una secuencia de instrucciones que modifican el estado del programa.

> **Analogía:** una receta de cocina con pasos numerados. "1. Hervir agua. 2. Añadir pasta. 3. Esperar 8 minutos. 4. Escurrir."

### 🌐 Perspectiva universal

Es el paradigma más antiguo y el que la mayoría aprende primero. Lenguajes como C, Pascal y el código de bajo nivel son puramente imperativos.

### ☕ En Java

```java
// Imperativo: le digo al programa exactamente QUÉ HACER y CÓMO
List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
List<Integer> paresMayoresA5 = new ArrayList<>();

for (int numero : numeros) {            // paso 1: recorrer uno a uno
    if (numero % 2 == 0) {              // paso 2: verificar si es par
        if (numero > 5) {               // paso 3: verificar si es mayor a 5
            paresMayoresA5.add(numero); // paso 4: agregar a la lista resultado
        }
    }
}
// Resultado: [6, 8, 10]
```

### ⚠️ Características clave

- **Estado mutable**: las variables cambian su valor.
- **Flujo explícito**: tú controlas cada paso.
- **Fácil de seguir** para principiantes, pero puede volverse difícil de mantener.

---

## 2. Declarativo

### 📖 Definición

El paradigma **declarativo** se enfoca en describir **qué quieres obtener**, no *cómo* obtenerlo. Le dices al programa el resultado deseado y el lenguaje/framework se encarga de decidir los pasos internos.

> **Analogía:** en lugar de dar la receta paso a paso, le dices al chef: *"quiero una pasta al dente con salsa de tomate"*. Él decide cómo hacerla.

### 🌐 Perspectiva universal

SQL es el ejemplo más conocido de paradigma declarativo:

```sql
-- Le digo QUÉ quiero, no CÓMO buscarlo
SELECT nombre, edad FROM usuarios WHERE edad >= 18 ORDER BY nombre;
```

HTML también es declarativo: declaras la estructura, el navegador decide cómo renderizarla.

### ☕ En Java (Streams)

```java
// Declarativo: le digo al programa QUÉ QUIERO, no cómo lograrlo
List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

List<Integer> paresMayoresA5 = numeros.stream()
    .filter(n -> n % 2 == 0)  // quiero los pares
    .filter(n -> n > 5)        // quiero los mayores a 5
    .collect(Collectors.toList());
// Resultado: [6, 8, 10]
```

### ⚠️ Imperativo vs Declarativo — mismo resultado, diferente enfoque

| | Imperativo | Declarativo |
|--|-----------|-------------|
| Se enfoca en | *Cómo* hacer las cosas | *Qué* se quiere obtener |
| Control del flujo | El programador | El lenguaje/framework |
| Verbosidad | Más código | Más conciso |
| Legibilidad | Explícito, fácil de seguir | Expresivo, más abstracto |

---

## 3. Orientado a Objetos (OOP)

### 📖 Definición

La **Programación Orientada a Objetos** (OOP, del inglés *Object-Oriented Programming*) organiza el código en **objetos**: entidades que combinan **datos** (atributos) y **comportamiento** (métodos). El mundo real se modela como una colección de objetos que interactúan entre sí.

> **Analogía:** en el mundo real, un auto *tiene* marca, modelo y color (datos), y *puede* arrancar, frenar y acelerar (comportamiento). En OOP, `Auto` es una clase con esos atributos y métodos.

### 🌐 Perspectiva universal

OOP es el paradigma dominante en el desarrollo de software empresarial. Lo soportan Java, C#, Python, Ruby, JavaScript, Kotlin, Swift, etc.

```python
# Python
class Auto:
    def __init__(self, marca, modelo):
        self.marca = marca
        self.modelo = modelo

    def arrancar(self):
        print(f"{self.marca} {self.modelo} arrancado")
```

### ☕ En Java

```java
public class Auto {
    private String marca;
    private String modelo;
    private boolean encendido;

    public Auto(String marca, String modelo) {
        this.marca = marca;
        this.modelo = modelo;
        this.encendido = false;
    }

    public void arrancar() {
        this.encendido = true;
        System.out.println(marca + " " + modelo + " arrancado");
    }

    public void apagar() {
        this.encendido = false;
    }
}

// Uso: los objetos interactúan
Auto miAuto = new Auto("Toyota", "Corolla");
miAuto.arrancar();
```

### ⚠️ Los 4 pilares de OOP

| Pilar | Qué significa |
|-------|---------------|
| **Encapsulamiento** | Ocultar los datos internos del objeto; solo accederlos por métodos controlados |
| **Herencia** | Una clase puede reutilizar la definición de otra (`class Gato extends Animal`) |
| **Polimorfismo** | El mismo método puede comportarse distinto según el objeto que lo ejecuta |
| **Abstracción** | Modelar solo lo relevante del mundo real para el problema concreto |

---

## 4. Funcional

### 📖 Definición

La **programación funcional** trata al código como una colección de **funciones puras** que transforman datos sin modificar el estado. Se evita el estado mutable y los efectos secundarios (*side effects*).

> **Analogía:** una calculadora. Le das `5 + 3` y siempre devuelve `8`. No "recuerda" resultados anteriores, no modifica nada externo. Cada operación es independiente y predecible.

### 🌐 Perspectiva universal

Lenguajes puramente funcionales: Haskell, Erlang, Clojure.  
Lenguajes con soporte funcional: Python, JavaScript, Kotlin, Scala, **Java** (desde Java 8 con lambdas y Streams).

```javascript
// JavaScript funcional
const doble = x => x * 2;          // función pura
const esPar = x => x % 2 === 0;    // función pura

const resultado = [1, 2, 3, 4, 5]
    .filter(esPar)      // [2, 4]
    .map(doble);        // [4, 8]
```

### ☕ En Java

```java
// Función pura: mismo input → siempre mismo output, sin efectos secundarios
Function<Integer, Integer> doble = x -> x * 2;
Predicate<Integer> esPar = x -> x % 2 == 0;

List<Integer> resultado = List.of(1, 2, 3, 4, 5).stream()
    .filter(esPar)          // [2, 4]
    .map(doble)             // [4, 8]
    .collect(Collectors.toList());

// Reducción: combinar todos los elementos en uno
int suma = List.of(1, 2, 3, 4, 5).stream()
    .reduce(0, Integer::sum);  // 15
```

### ⚠️ Conceptos clave del paradigma funcional

| Concepto | Qué significa |
|----------|---------------|
| **Función pura** | Dado el mismo input, siempre retorna el mismo output. No modifica nada externo |
| **Inmutabilidad** | Los datos no se modifican; se crean nuevos con los cambios |
| **Sin efectos secundarios** | La función no modifica variables globales, no hace I/O, no modifica parámetros |
| **Función de orden superior** | Una función que recibe o retorna otras funciones (`map`, `filter`, `reduce`) |
| **Lambda / Función anónima** | Función sin nombre, definida inline: `x -> x * 2` |

---

## 5. ¿Cuál usar?

No hay un paradigma universalmente superior. La elección depende del problema:

| Situación | Paradigma sugerido |
|-----------|-------------------|
| Lógica de negocio compleja con muchas entidades | **OOP** — modela el dominio con clases y objetos |
| Transformar, filtrar o mapear colecciones de datos | **Funcional** — streams, lambdas |
| Consultar bases de datos | **Declarativo** — SQL |
| Scripts simples o secuencias de pasos lineales | **Imperativo** — fácil de leer y seguir |
| Problemas de concurrencia y datos compartidos | **Funcional** — la inmutabilidad evita bugs de estado compartido |

> 💡 En la práctica, un buen desarrollador **mezcla paradigmas** según la situación: OOP para estructurar la aplicación, funcional para procesar datos, declarativo para consultas.

---

## 6. Java y los paradigmas

Java nació como un lenguaje **principalmente OOP**, pero ha evolucionado incorporando soporte funcional y declarativo:

```
Java 1.0 (1995)  → OOP puro
Java 5   (2004)  → Genéricos, for-each (más expresivo)
Java 8   (2014)  → Lambdas, Streams, Optional → soporte funcional real
Java 14+ (2020+) → Records, Switch Expressions → más declarativo
Java 21  (2023)  → Records, Pattern Matching, Sealed Classes
```

```java
// En un mismo archivo Java coexisten los tres paradigmas:

public class PedidoService {

    // OOP: el dominio está modelado con clases y objetos
    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public List<Pedido> obtenerPedidosActivos(List<Pedido> todos) {
        // Funcional: transformar y filtrar con streams y lambdas
        return todos.stream()
            .filter(p -> p.getEstado() == Estado.ACTIVO)
            .sorted(Comparator.comparing(Pedido::getFecha))
            .collect(Collectors.toList());
    }

    // Declarativo: el "qué" está en la consulta, el "cómo" lo maneja JPA
    // @Query("SELECT p FROM Pedido p WHERE p.estado = 'ACTIVO'")
    // List<Pedido> findActivos();
}
```

---

## 7. Tabla resumen

| Paradigma | Pregunta central | Foco | Ejemplo típico |
|-----------|-----------------|------|----------------|
| **Imperativo** | ¿Cómo lo hago paso a paso? | Instrucciones y estado | `for`, `if`, variables mutables |
| **Declarativo** | ¿Qué quiero obtener? | Resultado deseado | SQL, HTML, Streams |
| **OOP** | ¿Qué objetos existen y cómo interactúan? | Entidades con datos y comportamiento | Clases, objetos, herencia |
| **Funcional** | ¿Cómo transformo datos sin efectos secundarios? | Funciones puras, inmutabilidad | Lambdas, `map`, `filter`, `reduce` |

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Object-Oriented Software Construction** | Bertrand Meyer | Avanzado | La referencia académica del paradigma OOP. Introduce el concepto de "diseño por contrato" que complementa la orientación a objetos |
| **Functional Programming in Java** | Venkat Subramaniam | Intermedio | La guía más accesible para entender lambdas, streams y programación funcional en Java 8+. Directamente aplicable al curso |
| **Structure and Interpretation of Computer Programs (SICP)** | Abelson & Sussman | Avanzado | El libro que popularizó el paradigma funcional en la academia. Usa Scheme (Lisp) pero los conceptos aplican a cualquier lenguaje |
| **Java: The Complete Reference** | Herbert Schildt | Principiante / Intermedio | Cubre OOP en Java con detalle, incluyendo herencia, interfaces y polimorfismo |
| **Kotlin in Action** | Jemerov & Isakova | Intermedio | Kotlin mezcla OOP y funcional de forma elegante; leerlo da perspectiva sobre cómo un lenguaje moderno equilibra ambos paradigmas |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Functional Programming en Java — Baeldung** | https://www.baeldung.com/java-functional-programming | Guía práctica de programación funcional en Java con lambdas y streams |
| **¿Qué es OOP? — Oracle Docs** | https://docs.oracle.com/javase/tutorial/java/concepts/index.html | Tutorial oficial de Oracle sobre los conceptos de OOP en Java |
| **Imperativo vs Declarativo — freeCodeCamp** | https://www.freecodecamp.org/news/imperative-vs-declarative-programming-difference | Explicación clara con ejemplos de código |
| **Paradigmas de programación — Computerphile** | https://www.youtube.com/watch?v=sqV3pL5x8PI | Video de 10 minutos explicando los paradigmas de forma visual |
| **Java Streams — Oracle** | https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html | Documentación oficial de Streams — el paradigma declarativo/funcional en Java |

