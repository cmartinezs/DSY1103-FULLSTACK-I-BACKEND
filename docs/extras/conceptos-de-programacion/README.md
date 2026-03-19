# 🧠 Conceptos de Programación — Vocabulario Universal

> **Nivel de entrada:** cualquiera — este material es previo o paralelo a cualquier lenguaje.  
> **Objetivo:** dominar el vocabulario fundamental que se usa en **cualquier lenguaje de programación**, no solo en Java.  
> **Lenguaje de referencia para ejemplos:** Java 21 (aunque los conceptos aplican a Python, JavaScript, C#, etc.)

---

## ¿Por qué aprender vocabulario antes de aprender sintaxis?

Cuando aprendes un lenguaje de programación, el instructor dice cosas como:
*"declaras una variable", "asignas un literal", "el método retorna un valor"*.

Si no tienes claro qué significa cada uno de esos términos, **la explicación no llega**. Este extra te da ese vocabulario.

> 📌 Estos conceptos son transversales: si los entiendes en Java, los reconocerás en Python, JavaScript, C#, Go, etc.

---

## Índice de módulos

| # | Tema | Archivo |
|---|------|---------|
| 01 | Vocabulario universal del programador | [`01_vocabulario_universal.md`](./01_vocabulario_universal.md) |
| 02 | Paradigmas de programación | [`02_paradigmas_de_programacion.md`](./02_paradigmas_de_programacion.md) |
| 03 | Estructuras de datos comunes | [`03_estructuras_de_datos.md`](./03_estructuras_de_datos.md) |
| 04 | Tipos de errores y debugging | [`04_tipos_de_errores_y_debugging.md`](./04_tipos_de_errores_y_debugging.md) |
| 05 | Recursividad | [`05_recursividad.md`](./05_recursividad.md) |
| 06 | Complejidad algorítmica (Big O) | [`06_complejidad_algoritmica.md`](./06_complejidad_algoritmica.md) |
| 07 | Principios de buen código | [`07_principios_de_buen_codigo.md`](./07_principios_de_buen_codigo.md) |
| 08 | Concurrencia e hilos | [`08_concurrencia_e_hilos.md`](./08_concurrencia_e_hilos.md) |
| 09 | Entrada y salida (I/O) | [`09_entrada_y_salida.md`](./09_entrada_y_salida.md) |
| 10 | Memoria: Stack, Heap y Garbage Collection | [`10_memoria_stack_heap_gc.md`](./10_memoria_stack_heap_gc.md) |
| 11 | Patrones de diseño: Creacionales | [`11_patrones_creacionales.md`](./11_patrones_creacionales.md) |
| 12 | Patrones de diseño: Estructurales | [`12_patrones_estructurales.md`](./12_patrones_estructurales.md) |
| 13 | Patrones de diseño: Comportamiento | [`13_patrones_comportamiento.md`](./13_patrones_comportamiento.md) |

---

## ¿Qué encontrarás aquí?

Cada concepto viene con:

- 📖 **Definición clara** — qué es y para qué sirve
- 🌐 **Perspectiva universal** — cómo existe en otros lenguajes
- ☕ **Ejemplo en Java** — para anclarlo al lenguaje del curso
- ⚠️ **Errores comunes o confusiones frecuentes**

---

## Resumen de contenidos

### 01 — Vocabulario universal
Los 26 términos que todo programador usa sin importar el lenguaje: variable, constante, literal, asignación, sentencia, bloque, bucle, función, clase, objeto, ámbito, referencia y más.

### 02 — Paradigmas de programación
Qué es un paradigma y cuáles existen: **imperativo** (paso a paso), **declarativo** (qué quiero, no cómo), **orientado a objetos** (clases y objetos) y **funcional** (funciones puras e inmutabilidad). Cómo Java mezcla los cuatro.

### 03 — Estructuras de datos comunes
Las estructuras de datos esenciales: **Array** (tamaño fijo), **Lista** (dinámica), **Pila/Stack** (LIFO), **Cola/Queue** (FIFO), **Mapa/Map** (clave→valor) y **Conjunto/Set** (sin duplicados). Cuándo usar cada una.

### 04 — Tipos de errores y debugging
Los tres tipos de errores: **sintaxis** (el compilador lo detecta), **runtime** (falla al ejecutar) y **lógica** (funciona mal sin avisar). Cómo leer un stack trace y estrategias de debugging: print, debugger del IDE, tests unitarios.

### 05 — Recursividad
Qué es una función recursiva, sus dos partes obligatorias (**caso base** y **caso recursivo**), cómo funciona el call stack durante la ejecución, ejemplos clásicos (factorial, búsqueda binaria, árboles) y cuándo elegirla sobre la iteración.

### 06 — Complejidad algorítmica (Big O)
Cómo medir si un algoritmo escala: notación Big O, las complejidades más comunes (**O(1), O(log n), O(n), O(n log n), O(n²), O(2ⁿ)**), complejidad espacial y reglas prácticas para el día a día.

### 07 — Principios de buen código
Los principios universales de artesanía del software: **DRY** (no te repitas), **KISS** (mantenlo simple), **YAGNI** (no lo vas a necesitar), **Fail Fast**, **SRP**, nombres que revelan intención, funciones pequeñas y el problema de los números mágicos.

### 08 — Concurrencia e hilos
Qué es un hilo, diferencia entre concurrencia y paralelismo, por qué usarla, los problemas clásicos (**race condition, deadlock, starvation**), cómo sincronizar (`synchronized`, `volatile`, `AtomicInteger`), `ExecutorService`, `CompletableFuture` y cómo aplica en Spring Boot.

### 09 — Entrada y salida (I/O)
Qué es I/O y por qué es el mayor cuello de botella en backend. I/O por **consola** (`Scanner`, `System.out`), **archivos** (NIO.2, `Files`, try-with-resources), **red** (`HttpClient`, REST), el concepto de **stream de bytes**, I/O **bloqueante vs no bloqueante** y buenas prácticas en Spring Boot.

### 10 — Memoria: Stack, Heap y Garbage Collection
Cómo divide Java la memoria en **Stack** (frames de métodos, variables locales) y **Heap** (objetos). Qué es una **referencia**, por qué `==` no compara contenido de objetos, **paso por valor vs referencia**, qué es `null` y el `NullPointerException`, cómo funciona el **Garbage Collector** y cómo ocurren las fugas de memoria en lenguajes con GC.

### 11 — Patrones de diseño: Creacionales
Los patrones de **creación de objetos** del catálogo GoF: **Singleton** (una sola instancia), **Factory Method** (delegar la creación a subclases), **Abstract Factory** (familias de objetos relacionados), **Builder** (construcción paso a paso con campos opcionales) y **Prototype** (clonar objetos existentes). Cada uno con definición, casos de uso reales, implementación Java, literatura y enlaces.

### 12 — Patrones de diseño: Estructurales
Los patrones de **composición de clases y objetos** del catálogo GoF: **Adapter** (compatibilizar interfaces incompatibles), **Decorator** (agregar comportamiento sin modificar la clase), **Facade** (simplificar subsistemas complejos), **Composite** (tratar igual hojas y contenedores en árboles) y **Proxy** (controlar el acceso a un objeto). Cada uno con definición, casos de uso reales, implementación Java, literatura y enlaces.

### 13 — Patrones de diseño: Comportamiento
Los patrones de **comunicación y distribución de responsabilidades** del catálogo GoF: **Observer** (notificación uno-a-muchos), **Strategy** (algoritmos intercambiables), **Command** (encapsular acciones para ejecutar/deshacer), **Template Method** (esqueleto de algoritmo con pasos variables), **Chain of Responsibility** (cadena de filtros/manejadores) y **State** (comportamiento que cambia con el estado del objeto). Cada uno con definición, casos de uso reales, implementación Java, literatura y enlaces.


