# Módulo 04 — Tipos de errores y debugging

> **Objetivo:** identificar los tres tipos de errores que existen en programación, entender por qué ocurren y conocer estrategias concretas para encontrarlos y corregirlos. El debugging es una habilidad que separa a los programadores que progresan de los que se frustran.

---

## ¿Por qué importa distinguir el tipo de error?

Cuando algo falla en un programa, la reacción instintiva es "buscar el error". Pero buscar un error de lógica como si fuera uno de sintaxis, o viceversa, es buscar en el lugar equivocado con la herramienta equivocada.

> 📌 Saber **qué tipo de error tienes** determina **dónde mirarlo** y **cómo corregirlo**.

---

## Índice

1. [Error de sintaxis](#1-error-de-sintaxis)
2. [Error en tiempo de ejecución (Runtime Error)](#2-error-en-tiempo-de-ejecución-runtime-error)
3. [Error de lógica](#3-error-de-lógica)
4. [Stack trace — cómo leerlo](#4-stack-trace--cómo-leerlo)
5. [Estrategias de debugging](#5-estrategias-de-debugging)
6. [Tabla resumen](#6-tabla-resumen)
7. [📚 Literatura recomendada](#-literatura-recomendada)
8. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Error de sintaxis

### 📖 Definición

Un **error de sintaxis** (*syntax error*) ocurre cuando el código **no respeta las reglas gramaticales del lenguaje**. El compilador o intérprete no puede entender la instrucción — como si escribieras una oración en español con palabras inventadas o sin verbo.

Son los **más fáciles de detectar**: el compilador te dice exactamente dónde está el problema antes de que el programa siquiera se ejecute.

### 🌐 Perspectiva universal

| Lenguaje | ¿Cuándo se detecta? | Herramienta |
|----------|--------------------|----|
| Java, C, C# | Al compilar | Compilador |
| Python, JavaScript | Al interpretar (en tiempo de ejecución) | Intérprete |
| Todos | En el editor moderno | IDE / Linter |

### ☕ Ejemplos en Java

```java
// ❌ Falta el punto y coma al final
int edad = 25       // SyntaxError: ';' expected

// ❌ Llaves sin cerrar
public void saludar() {
    System.out.println("Hola");
// Error: reached end of file while parsing

// ❌ Paréntesis mal balanceados
if edad >= 18 {     // Error: '(' expected
    System.out.println("Adulto");
}

// ❌ Tipo de dato mal escrito
Int edad = 25;      // Error: cannot find symbol 'Int' (es 'int' en minúscula)

// ❌ Comillas sin cerrar
String nombre = "Ana;   // Error: unclosed string literal
```

### ✅ Cómo resolverlo

1. **Lee el mensaje de error** — el compilador te dice la línea exacta.
2. **Mira la línea indicada Y la anterior** — a veces el error real está una línea antes.
3. **Usa el IDE** — subraya los errores en tiempo real antes de compilar.
4. **Formatea el código** — el IDE puede indentar automáticamente y revelar bloques mal cerrados.

### ⚠️ Errores de sintaxis comunes en Java

| Error | Causa frecuente |
|-------|----------------|
| `';' expected` | Falta `;` al final de una sentencia |
| `cannot find symbol` | Nombre mal escrito, clase no importada, variable no declarada |
| `reached end of file` | Llave de cierre `}` faltante |
| `illegal start of expression` | Código fuera de un método, palabra reservada mal usada |
| `incompatible types` | Asignar un tipo a una variable de otro tipo sin conversión |

---

## 2. Error en tiempo de ejecución (Runtime Error)

### 📖 Definición

Un **error en tiempo de ejecución** (*runtime error*) es aquel que ocurre **durante la ejecución del programa**, no al compilar. El código es sintácticamente correcto — el compilador lo acepta — pero algo falla cuando el programa corre con datos reales.

En Java (y otros lenguajes OOP), estos errores se manifiestan como **excepciones** (*exceptions*).

> **Analogía:** la receta de cocina está bien escrita (sin errores de sintaxis), pero al seguirla te das cuenta de que pide 3 huevos y tú solo tienes 2. El error ocurre al ejecutar, no al leer la receta.

### 🌐 Perspectiva universal

```python
# Python: runtime error
lista = [1, 2, 3]
print(lista[10])  # IndexError: list index out of range

numero = int("abc")  # ValueError: invalid literal for int()
```
```javascript
// JavaScript: runtime error
null.toString();   // TypeError: Cannot read properties of null

JSON.parse("texto inválido"); // SyntaxError en runtime (¡no en compilación!)
```

### ☕ Excepciones más comunes en Java

```java
// 1. NullPointerException — operar sobre una referencia null
String nombre = null;
System.out.println(nombre.length()); // ❌ NullPointerException
// Solución: verificar null antes de usar
if (nombre != null) { System.out.println(nombre.length()); }

// 2. ArrayIndexOutOfBoundsException — índice fuera del array
int[] numeros = {1, 2, 3};
System.out.println(numeros[5]); // ❌ ArrayIndexOutOfBoundsException
// Solución: verificar límites con numeros.length

// 3. ClassCastException — conversión inválida entre tipos
Object obj = "texto";
Integer numero = (Integer) obj; // ❌ ClassCastException
// Solución: verificar tipo con instanceof

// 4. NumberFormatException — parsear texto no numérico como número
String texto = "abc";
int valor = Integer.parseInt(texto); // ❌ NumberFormatException
// Solución: validar el texto antes de parsear

// 5. StackOverflowError — recursión infinita
public int factorial(int n) {
    return n * factorial(n - 1);  // ❌ nunca tiene caso base → StackOverflowError
}

// 6. ArithmeticException — división por cero en enteros
int resultado = 10 / 0; // ❌ ArithmeticException: / by zero
```

### ✅ Cómo resolverlo

1. **Lee el stack trace completo** — dice el tipo de excepción, el mensaje y la línea exacta.
2. **Busca la causa raíz** — a menudo es un dato `null`, un índice fuera de rango o un formato inesperado.
3. **Reproduce el error con los mismos datos** — si el error depende de los datos, identifica qué dato lo provoca.
4. **Agrega validaciones** antes de las operaciones riesgosas.
5. **Usa try/catch** para manejar excepciones esperadas de forma controlada.

---

## 3. Error de lógica

### 📖 Definición

Un **error de lógica** (*logic error*) es el más peligroso: el programa **compila y se ejecuta sin errores**, pero produce **resultados incorrectos**. La intención del programador y el código escrito no coinciden.

> **Analogía:** la receta está bien escrita, la sigues sin problemas, pero al final la torta sabe rara — porque pusiste sal en lugar de azúcar. No hubo error al leer ni al ejecutar; el proceso falló en la lógica.

### 🌐 Perspectiva universal

Los errores de lógica son universales y dependen del programador, no del lenguaje. Ningún compilador los detecta porque el código es perfectamente válido.

### ☕ Ejemplos en Java

```java
// ❌ Error de lógica 1: operador incorrecto
// Queremos calcular el promedio de 3 notas
int nota1 = 7, nota2 = 8, nota3 = 9;
double promedio = nota1 + nota2 + nota3 / 3; // ❌ Resultado: 7 + 8 + 3 = 18 (¡no 8!)
// El problema: la división tiene mayor precedencia que la suma
// Correcto:
double promedioOk = (nota1 + nota2 + nota3) / 3.0; // 8.0

// ❌ Error de lógica 2: condición invertida
// Queremos dar descuento a mayores de 60 años
int edad = 65;
if (edad < 60) {  // ❌ da descuento a los MENORES de 60 (al revés)
    System.out.println("Descuento adulto mayor");
}
// Correcto:
if (edad >= 60) {
    System.out.println("Descuento adulto mayor");
}

// ❌ Error de lógica 3: off-by-one (el error de "uno de más o uno de menos")
// Queremos imprimir del 1 al 10
for (int i = 0; i < 10; i++) {
    System.out.println(i); // imprime 0 al 9, no 1 al 10
}
// Correcto:
for (int i = 1; i <= 10; i++) {
    System.out.println(i);
}

// ❌ Error de lógica 4: usar = en lugar de == en condición
int stock = 0;
if (stock = 5) {  // ❌ En Java esto es un error de compilación, pero en C/C++ no
    System.out.println("Hay stock");
}
// En lenguajes que lo permiten, asigna 5 a stock y siempre evalúa como true

// ❌ Error de lógica 5: acumulador no inicializado correctamente
// Queremos el máximo de una lista
List<Integer> numeros = List.of(3, 1, 4, 1, 5, 9, 2, 6);
int maximo = 0;  // ❌ Si todos los números fueran negativos, 0 sería el "máximo" incorrecto
for (int n : numeros) {
    if (n > maximo) maximo = n;
}
// Correcto: inicializar con el primer elemento o con Integer.MIN_VALUE
int maximoOk = Integer.MIN_VALUE;
for (int n : numeros) {
    if (n > maximoOk) maximoOk = n;
}
```

### ✅ Cómo resolverlo

Los errores de lógica requieren **razonamiento**, no solo leer mensajes de error:

1. **Verificar los resultados esperados** — ¿qué valor debería tener la variable en cada paso?
2. **Trazar el código a mano** — ejecutar mentalmente o en papel con valores concretos.
3. **Agregar `System.out.println`** — imprimir el valor de las variables en puntos clave.
4. **Usar el debugger** — pausar la ejecución y ver el estado real del programa paso a paso.
5. **Escribir tests** — un test que verifica el resultado esperado falla si hay error de lógica.

---

## 4. Stack trace — cómo leerlo

### 📖 Definición

Un **stack trace** (o *stack de llamadas*) es el **informe de errores** que Java (y otros lenguajes) genera cuando ocurre una excepción. Muestra la cadena de llamadas de métodos que llevaron al error, desde el más reciente hasta el origen.

### ☕ Ejemplo de stack trace

```
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "nombre" is null
    at cl.duoc.fullstack.tickets.service.TicketService.validarNombre(TicketService.java:42)
    at cl.duoc.fullstack.tickets.service.TicketService.crearTicket(TicketService.java:28)
    at cl.duoc.fullstack.tickets.controller.TicketController.crear(TicketController.java:15)
    at cl.duoc.fullstack.Main.main(Main.java:8)
```

### 🔍 Cómo leerlo

```
Exception in thread "main"          ← hilo donde ocurrió el error
java.lang.NullPointerException      ← tipo de excepción
: Cannot invoke "String.length()"   ← mensaje descriptivo del error
because "nombre" is null            ← causa específica (Java 14+)

at TicketService.validarNombre(TicketService.java:42)   ← línea exacta del error ← EMPIEZA AQUÍ
at TicketService.crearTicket(TicketService.java:28)     ← quién llamó a validarNombre
at TicketController.crear(TicketController.java:15)     ← quién llamó a crearTicket
at Main.main(Main.java:8)                               ← punto de entrada ← LEE HASTA AQUÍ
```

### ⚠️ Reglas para leer un stack trace

1. **Empieza por arriba**: el primer `at` es donde ocurrió el error.
2. **Busca TU código**: ignora las líneas de librerías externas (Spring, Hibernate, java.util.*) y enfócate en las líneas que tienen *tu* nombre de paquete.
3. **Lee el mensaje**: a menudo dice exactamente qué es `null` o qué salió mal.
4. **Sigue la cadena**: el stack trace muestra el camino que llevó al error — puede ser que el problema real está más abajo en la cadena.

---

## 5. Estrategias de debugging

### 5.1 Print debugging (el más básico)

Agregar impresiones temporales para ver el estado del programa en distintos puntos:

```java
public double calcularTotal(List<Producto> productos) {
    System.out.println("[DEBUG] cantidad de productos: " + productos.size()); // ← agregar
    double total = 0;
    for (Producto p : productos) {
        System.out.println("[DEBUG] producto: " + p.getNombre() + " precio: " + p.getPrecio()); // ← agregar
        total += p.getPrecio();
    }
    System.out.println("[DEBUG] total calculado: " + total); // ← agregar
    return total;
}
```

> ✅ Simple y rápido.  
> ⚠️ Recuerda eliminar los `System.out.println` de debug antes de entregar o desplegar.

### 5.2 Debugger del IDE (el más poderoso)

El **debugger** permite pausar la ejecución en una línea específica (*breakpoint*) e inspeccionar el valor de todas las variables en ese momento.

```
Cómo usar el debugger en IntelliJ IDEA:
1. Haz clic en el número de línea → aparece un punto rojo (breakpoint)
2. Ejecuta con el ícono de "bug" (Debug) en lugar del play normal
3. El programa se pausa en esa línea
4. En el panel Variables ves el valor actual de cada variable
5. Usa Step Over (F8) para ejecutar línea a línea
6. Usa Step Into (F7) para entrar dentro de un método
7. Usa Resume (F9) para continuar hasta el próximo breakpoint
```

### 5.3 Dividir y conquistar (rubber duck debugging)

Cuando no encuentras el error:

1. **Divide el problema**: aisla la sección de código que falla reduciendo el código a lo mínimo que reproduce el error.
2. **Explícalo en voz alta** (o por escrito): el acto de explicar paso a paso fuerza a tu cerebro a procesar la lógica de forma diferente. Muchas veces encuentras el error mientras lo explicas.
3. **Verifica las suposiciones**: ¿estás seguro de que esa variable tiene el valor que crees? Imprímela.

### 5.4 Tests unitarios como herramienta de debugging

Escribir un test que reproduzca el error es la mejor forma de:
- Confirmar que el error existe.
- Verificar que la corrección funciona.
- Evitar que el error reaparezca en el futuro.

```java
@Test
void calcularDescuento_debeRetornarCeroSiPrecioEsCero() {
    double resultado = servicio.calcularDescuento(0.0, 15.0);
    assertEquals(0.0, resultado); // si falla, el test te dice exactamente por qué
}
```

### 5.5 Buscar el mensaje de error

Antes de invertir horas:

1. **Copia el mensaje de error exacto** (sin los nombres de tus clases).
2. **Búscalo en Google o Stack Overflow**.
3. **Lee la documentación oficial** del lenguaje o librería.

> 💡 El 90% de los errores que encuentres como principiante ya fueron encontrados y resueltos por miles de personas. Saber buscar eficientemente es una habilidad tan importante como saber programar.

---

## 6. Tabla resumen

| Tipo de error | ¿Cuándo aparece? | ¿Lo detecta el compilador? | Peligrosidad | Estrategia principal |
|--------------|-----------------|--------------------------|-------------|---------------------|
| **Sintaxis** | Al compilar / antes de ejecutar | ✅ Sí | 🟢 Baja — fácil de encontrar | Leer el mensaje del compilador |
| **Runtime** | Durante la ejecución | ❌ No | 🟡 Media — falla con datos específicos | Leer el stack trace, agregar validaciones |
| **Lógica** | Nunca — el programa "funciona" mal | ❌ No | 🔴 Alta — el programa parece correcto | Debugger, tests, trazado manual |

### Flujo de diagnóstico rápido

```
¿El código no compila?
  → Error de SINTAXIS: lee el mensaje del compilador, corrígelo

¿El código compila pero lanza una excepción al ejecutar?
  → Error de RUNTIME: lee el stack trace, identifica la línea y el tipo de excepción

¿El código corre sin errores pero el resultado es incorrecto?
  → Error de LÓGICA: usa el debugger, agrega prints, traza manualmente
```

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **The Pragmatic Programmer** | Hunt & Thomas | Principiante / Intermedio | Capítulo "Debugging" — las reglas de oro para encontrar bugs. Uno de los mejores textos prácticos sobre el tema |
| **Debugging: The 9 Indispensable Rules** | David J. Agans | Principiante | Libro delgado y directo. Las 9 reglas son universales y aplican a cualquier lenguaje o sistema |
| **Why Programs Fail: A Guide to Systematic Debugging** | Andreas Zeller | Avanzado | El texto académico más completo sobre debugging sistemático. Incluye técnicas de reducción de fallas y análisis de causa raíz |
| **Clean Code** | Robert C. Martin | Intermedio | Argumenta que el código bien escrito tiene menos errores de lógica — la mejor defensa contra los bugs es la claridad |
| **Test-Driven Development: By Example** | Kent Beck | Intermedio | Los tests como herramienta primaria para detectar errores de lógica antes de que lleguen a producción |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Debugger de IntelliJ IDEA — Guía oficial** | https://www.jetbrains.com/help/idea/debugging-code.html | Tutorial completo del debugger del IDE que usan en el curso: breakpoints, watches, step over/into |
| **Baeldung — Java Debugging Tips** | https://www.baeldung.com/java-debugging | Técnicas de debugging específicas para Java con ejemplos |
| **Stack Overflow — How to debug small programs** | https://stackoverflow.com/questions/2069367 | El post más leído de SO sobre cómo debuggear sistemáticamente — lectura obligada para principiantes |
| **Visual Debugger — Python Tutor** | https://pythontutor.com/java.html | Ejecuta Java paso a paso en el navegador y muestra el stack y el heap visualmente — perfecto para entender el flujo |
| **Common Java Exceptions — Baeldung** | https://www.baeldung.com/java-common-exceptions | Guía de las excepciones más frecuentes en Java con causas y soluciones |

