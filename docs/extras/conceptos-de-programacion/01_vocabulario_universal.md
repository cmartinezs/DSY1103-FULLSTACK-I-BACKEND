# Módulo 01 — Vocabulario universal del programador

> **Objetivo:** conocer y distinguir los términos fundamentales que se usan en programación **independientemente del lenguaje**. Son el "alfabeto conceptual" que todo desarrollador debe manejar.

---

## Índice

1. [Identificador](#1-identificador)
2. [Tipo de dato](#2-tipo-de-dato)
3. [Literal](#3-literal)
4. [Variable](#4-variable)
5. [Constante](#5-constante)
6. [Declaración](#6-declaración)
7. [Asignación](#7-asignación)
8. [Expresión](#8-expresión)
9. [Operador](#9-operador)
10. [Sentencia / Instrucción](#10-sentencia--instrucción)
11. [Bloque](#11-bloque)
12. [Comentario](#12-comentario)
13. [Condicional](#13-condicional)
14. [Bucle / Ciclo](#14-bucle--ciclo)
15. [Función / Método](#15-función--método)
16. [Parámetro y argumento](#16-parámetro-y-argumento)
17. [Retorno](#17-retorno)
18. [Ámbito (Scope)](#18-ámbito-scope)
19. [Clase](#19-clase)
20. [Objeto / Instancia](#20-objeto--instancia)
21. [Interfaz / Contrato](#21-interfaz--contrato)
22. [Módulo / Paquete / Namespace](#22-módulo--paquete--namespace)
23. [Array / Arreglo](#23-array--arreglo)
24. [Excepción / Error](#24-excepción--error)
25. [Algoritmo](#25-algoritmo)
26. [Referencia](#26-referencia)
27. [📚 Literatura recomendada](#-literatura-recomendada)
28. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Identificador

### 📖 Definición

Un **identificador** es el **nombre** que le das a algo en el código: una variable, una función, una clase, un paquete. Es la etiqueta que usas para referirte a ese elemento más adelante.

### 🌐 En otros lenguajes

| Lenguaje | Ejemplo de identificador |
|----------|--------------------------|
| Java | `nombreUsuario`, `calcularPrecio`, `TicketService` |
| Python | `nombre_usuario`, `calcular_precio`, `TicketService` |
| JavaScript | `nombreUsuario`, `calcularPrecio`, `TicketService` |
| C# | `NombreUsuario`, `CalcularPrecio`, `TicketService` |

> La sintaxis cambia, la idea no: **es solo un nombre que tú inventas**.

### ☕ En Java

```java
int edad;               // 'edad' es el identificador
String nombreCompleto;  // 'nombreCompleto' es el identificador
void calcularTotal() {} // 'calcularTotal' es el identificador
```

### ⚠️ Reglas universales

- No puede empezar con un número (`1nombre` ❌, `nombre1` ✅).
- No puede contener espacios (`mi variable` ❌, `miVariable` ✅).
- No puede ser una palabra reservada del lenguaje (`class`, `if`, `return` ❌).

---

## 2. Tipo de dato

### 📖 Definición

El **tipo de dato** define **qué clase de información** puede almacenar una variable: un número entero, un texto, un valor verdadero/falso, una fecha, etc. También define cuánta memoria ocupa y qué operaciones se pueden hacer con ese dato.

### 🌐 Tipos comunes en diferentes lenguajes

| Categoría | Java | Python | JavaScript | C# |
|-----------|------|--------|------------|----|
| Ente<br/>ro | `int`, `long` | `int` | `number` | `int`, `long` |
| Decimal | `double`, `float` | `float` | `number` | `double`, `float` |
| Texto | `String` | `str` | `string` | `string` |
| Booleano | `boolean` | `bool` | `boolean` | `bool` |
| Nulo | `null` | `None` | `null` / `undefined` | `null` |

### ☕ En Java

```java
int      cantidad  = 10;        // número entero
double   precio    = 99.90;     // número decimal
String   nombre    = "Ana";     // texto
boolean  activo    = true;      // verdadero o falso
```

### ⚠️ Importante

Los tipos existen en **todos** los lenguajes, aunque algunos (como Python o JavaScript) sean de tipado dinámico y no requieran que los declares explícitamente.

---

## 3. Literal

### 📖 Definición

Un **literal** es un **valor fijo escrito directamente en el código fuente**. No es una variable, no se calcula: es el valor tal cual aparece escrito.

### 🌐 Perspectiva universal

En todos los lenguajes existen literales. Son los valores "duros" que el programador escribe en el código.

### ☕ En Java

```java
42           // literal entero
3.14         // literal decimal (double)
'A'          // literal carácter
"Hola mundo" // literal String
true         // literal booleano
null         // literal nulo
```

```java
// Aquí 18 y "Permitido" son literales; 'edad' y 'mensaje' son variables
int edad = 18;
String mensaje = "Permitido";
```

### ⚠️ No confundas literal con variable

| Concepto | Ejemplo | ¿Puede cambiar? |
|----------|---------|-----------------|
| Literal | `"Hola"`, `42`, `true` | No — está fijo en el código |
| Variable | `nombre`, `cantidad` | Sí — su valor puede variar |

---

## 4. Variable

### 📖 Definición

Una **variable** es un **espacio con nombre en memoria** donde se guarda un valor que **puede cambiar** durante la ejecución del programa. Funciona como una caja con una etiqueta: la etiqueta es el nombre, y dentro de la caja va el valor.

### 🌐 En otros lenguajes

```python
# Python
edad = 25
edad = 26  # se puede cambiar
```
```javascript
// JavaScript
let edad = 25;
edad = 26;  // se puede cambiar
```
```java
// Java
int edad = 25;
edad = 26;  // se puede cambiar
```

### ☕ En Java

```java
int contador = 0;
contador = 1;           // cambia el valor
contador = contador + 1; // cambia a partir de sí misma

String estado = "ABIERTO";
estado = "CERRADO";     // se puede reasignar
```

### ⚠️ La confusión más frecuente

> "Variable" no significa que el valor **siempre** cambie — significa que **puede** cambiar. Una variable que nunca cambia su valor es técnicamente posible, pero en ese caso probablemente debería ser una constante.

---

## 5. Constante

### 📖 Definición

Una **constante** es como una variable, pero su valor **no puede cambiar** una vez asignado. Sirve para valores fijos y significativos: el número PI, el límite máximo de intentos, el nombre de la aplicación.

### 🌐 En otros lenguajes

```python
# Python: convencion UPPER_CASE (el lenguaje no lo impone, pero se respeta)
MAX_INTENTOS = 3
PI = 3.14159
```
```javascript
// JavaScript: const impide la reasignación
const MAX_INTENTOS = 3;
const PI = 3.14159;
```
```java
// Java: final impide la reasignación; static final para constantes de clase
static final int MAX_INTENTOS = 3;
static final double PI = 3.14159;
```

### ☕ En Java

```java
public class Config {
    static final int    MAX_REINTENTOS    = 3;
    static final String VERSION_APP      = "1.0.0";
    static final double IVA              = 0.19;
}

// Uso
double total = subtotal * (1 + Config.IVA);
```

```java
// Intentar cambiarla → error de compilación
Config.IVA = 0.21; // ❌ ERROR: no se puede reasignar una variable final
```

### ⚠️ Constante vs literal

| | Literal | Constante |
|--|---------|-----------|
| Tiene nombre | ❌ | ✅ |
| Se puede reutilizar con nombre | ❌ | ✅ |
| Fácil de cambiar en un lugar | ❌ | ✅ |

Prefiere constantes con nombre sobre literales sueltos (*magic numbers*).

---

## 6. Declaración

### 📖 Definición

**Declarar** algo es **presentárselo al compilador/intérprete**: decirle que existe una variable, función o clase con ese nombre y ese tipo. Es el acto de registrar el elemento en el programa.

### 🌐 Perspectiva universal

```python
# Python: declaración implícita (solo con la asignación)
nombre = "Ana"
```
```javascript
// JavaScript: declaración explícita con let/const/var
let nombre;
```
```java
// Java: declaración explícita con tipo
String nombre;
```

### ☕ En Java

```java
// Declaración sin valor inicial (solo presenta la variable)
int edad;

// Declaración con valor inicial (declaración + asignación en una línea)
int edad = 25;

// Declaración de método
int sumar(int a, int b) { return a + b; }

// Declaración de clase
class Persona { }
```

### ⚠️ Declaración ≠ Asignación (aunque se hagan juntas)

Son dos cosas distintas que muchas veces se escriben en una sola línea:

```java
String nombre = "Ana";
//     ↑ declaración     ↑ asignación
```

---

## 7. Asignación

### 📖 Definición

La **asignación** es el acto de **guardar un valor dentro de una variable**. Se usa el operador de asignación (`=` en la mayoría de lenguajes) y siempre se lee de **derecha a izquierda**: primero se evalúa el lado derecho, luego se guarda en el lado izquierdo.

### 🌐 Perspectiva universal

En prácticamente todos los lenguajes, `=` es el operador de asignación (no de igualdad matemática).

```python
edad = 25          # Python
```
```javascript
edad = 25;         // JavaScript
```
```java
edad = 25;         // Java
```

### ☕ En Java

```java
int total;
total = 100;            // asignación simple

total = total + 50;     // primero evalúa 'total + 50', luego guarda el resultado
total += 50;            // equivalente al anterior (asignación compuesta)

String estado = "ABIERTO";
estado = "CERRADO";     // reasignación
```

### ⚠️ `=` NO es igualdad

| Símbolo | Significado |
|---------|-------------|
| `=` | Asignación — guarda un valor en una variable |
| `==` | Comparación — pregunta si dos valores son iguales |

```java
int x = 5;     // guarda 5 en x
x == 5;        // pregunta "¿x es igual a 5?" → true (pero no hace nada solo)
```

---

## 8. Expresión

### 📖 Definición

Una **expresión** es cualquier combinación de valores, variables y operadores que **se evalúa y produce un resultado**. Toda expresión "vale" algo.

### 🌐 Perspectiva universal

Las expresiones son universales. Cualquier cosa que puedas "calcular" o "evaluar" es una expresión.

### ☕ En Java

```java
// Expresiones aritméticas
2 + 3           // → 5
precio * 0.19   // → el IVA del precio

// Expresiones de comparación (producen true o false)
edad >= 18      // → true o false
nombre.equals("Ana")  // → true o false

// Expresiones lógicas
edad >= 18 && tieneCuenta  // → true o false

// Expresiones de texto
"Hola, " + nombre          // → "Hola, Ana"

// Las expresiones se pueden combinar y anidar
(precio * cantidad) * (1 + IVA)
```

### ⚠️ Expresión vs Sentencia

Una expresión produce un valor. Una sentencia es una instrucción que **hace** algo (puede contener expresiones).

```java
precio * 1.19          // expresión — calcula pero no "hace" nada por sí sola
total = precio * 1.19; // sentencia de asignación — usa la expresión y guarda el resultado
```

---

## 9. Operador

### 📖 Definición

Un **operador** es un símbolo especial que realiza una operación sobre uno o más valores (**operandos**) y produce un resultado. Son los "verbos" de las expresiones.

### 🌐 Tipos de operadores (universales)

| Categoría | Operadores comunes | Qué hacen |
|-----------|-------------------|-----------|
| **Aritméticos** | `+`, `-`, `*`, `/`, `%` | Operaciones matemáticas |
| **Comparación** | `==`, `!=`, `<`, `>`, `<=`, `>=` | Comparan dos valores → `true`/`false` |
| **Lógicos** | `&&`, `\|\|`, `!` | Combinan condiciones booleanas |
| **Asignación** | `=`, `+=`, `-=`, `*=`, `/=` | Guardan valores en variables |
| **Concatenación** | `+` (en texto) | Une cadenas de texto |

### ☕ En Java

```java
int a = 10, b = 3;

// Aritméticos
a + b   // 13
a - b   // 7
a * b   // 30
a / b   // 3 (división entera)
a % b   // 1 (módulo / resto)

// Comparación
a > b   // true
a == b  // false
a != b  // true

// Lógicos
(a > 0) && (b > 0)  // true — ambas condiciones deben ser verdaderas
(a > 0) || (b < 0)  // true — al menos una es verdadera
!(a > 0)            // false — niega el resultado

// Asignación compuesta
a += 5;  // a = a + 5  → 15
a -= 2;  // a = a - 2  → 13
```

---

## 10. Sentencia / Instrucción

### 📖 Definición

Una **sentencia** (también llamada **instrucción**) es la **unidad mínima de ejecución** de un programa: una orden completa que el programa realiza. Si las expresiones son los cálculos, las sentencias son las acciones.

En la mayoría de lenguajes, una sentencia termina con `;` (Java, JavaScript, C#) o con un salto de línea (Python).

### 🌐 Perspectiva universal

```python
# Python: cada línea es una sentencia
nombre = "Ana"
print(nombre)
```
```javascript
// JavaScript
let nombre = "Ana";
console.log(nombre);
```
```java
// Java
String nombre = "Ana";
System.out.println(nombre);
```

### ☕ En Java

```java
// Sentencia de declaración y asignación
int edad = 25;

// Sentencia de llamada a método
System.out.println("Hola");

// Sentencia de control (condicional)
if (edad >= 18) { ... }

// Sentencia de control (bucle)
for (int i = 0; i < 10; i++) { ... }

// Sentencia de retorno
return resultado;
```

### ⚠️ Importante

No toda sentencia produce un resultado visible. Algunas solo **hacen algo** (guardar un valor, imprimir, llamar un método).

---

## 11. Bloque

### 📖 Definición

Un **bloque** es un **grupo de sentencias** que se tratan como una unidad. Se delimitan con llaves `{ }` en la mayoría de lenguajes (o con indentación en Python). Los bloques definen el **ámbito** de las variables declaradas dentro.

### 🌐 En otros lenguajes

```python
# Python: el bloque se define por indentación
if edad >= 18:
    mensaje = "Adulto"   # dentro del bloque
    print(mensaje)       # dentro del bloque
# aquí termina el bloque
```
```java
// Java: el bloque se define con llaves
if (edad >= 18) {
    String mensaje = "Adulto";  // dentro del bloque
    System.out.println(mensaje); // dentro del bloque
}
// aquí termina el bloque
```

### ☕ En Java

```java
// Bloque de un método
public void saludar() {
    // todo esto es el bloque del método
    String saludo = "Hola";
    System.out.println(saludo);
}

// Bloque de un if
if (precio > 1000) {
    double descuento = precio * 0.10;
    precio = precio - descuento;
}

// Bloque de un for
for (int i = 0; i < 5; i++) {
    System.out.println("Vuelta " + i);
}
```

### ⚠️ Variables dentro de un bloque

Una variable declarada dentro de un bloque **solo existe dentro de ese bloque**:

```java
if (activo) {
    String mensaje = "Sistema activo"; // existe solo aquí
    System.out.println(mensaje);       // ✅ OK
}
System.out.println(mensaje);           // ❌ ERROR: 'mensaje' no existe aquí
```

---

## 12. Comentario

### 📖 Definición

Un **comentario** es texto que el programador escribe en el código **para explicar o documentar**, pero que el compilador/intérprete **ignora completamente**. No afecta la ejecución.

### 🌐 Sintaxis en distintos lenguajes

| Lenguaje | Una línea | Múltiples líneas |
|----------|-----------|-----------------|
| Java / C# / JavaScript | `// comentario` | `/* comentario */` |
| Python | `# comentario` | `""" comentario """` |
| HTML | No aplica | `<!-- comentario -->` |
| SQL | `-- comentario` | `/* comentario */` |

### ☕ En Java

```java
// Esto es un comentario de una línea — el compilador lo ignora

/*
 * Esto es un comentario de varias líneas.
 * Útil para explicaciones largas.
 */

/**
 * Esto es un comentario Javadoc.
 * Sirve para generar documentación automática.
 * @param nombre el nombre del usuario
 * @return saludo personalizado
 */
public String saludar(String nombre) {
    return "Hola, " + nombre; // comentario inline al final de la línea
}
```

### ⚠️ Buenos vs malos comentarios

```java
// ❌ Malo: repite lo que el código ya dice
int edad = 25; // asigna 25 a edad

// ✅ Bueno: explica el POR QUÉ, no el QUÉ
// El IVA en Chile es del 19% según Ley 825
double IVA = 0.19;
```

---

## 13. Condicional

### 📖 Definición

Un **condicional** es una estructura de control que permite que el programa **tome decisiones**: ejecuta un bloque de código **si** se cumple una condición, y otro bloque (o ninguno) si no se cumple. Es el "¿qué hago según la situación?" del código.

### 🌐 Perspectiva universal

Todos los lenguajes tienen alguna forma de condicional. El concepto es idéntico; la sintaxis varía.

```python
# Python
if edad >= 18:
    print("Adulto")
else:
    print("Menor")
```
```javascript
// JavaScript
if (edad >= 18) {
    console.log("Adulto");
} else {
    console.log("Menor");
}
```

### ☕ En Java

```java
// if / else — para condiciones de verdadero/falso
int edad = 20;
if (edad >= 18) {
    System.out.println("Adulto");
} else {
    System.out.println("Menor de edad");
}

// if / else if / else — múltiples condiciones
int nota = 75;
if (nota >= 90) {
    System.out.println("Sobresaliente");
} else if (nota >= 70) {
    System.out.println("Aprobado");
} else {
    System.out.println("Reprobado");
}

// switch — para valores discretos conocidos
String dia = "LUNES";
String tipo = switch (dia) {
    case "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" -> "Día hábil";
    case "SABADO", "DOMINGO" -> "Fin de semana";
    default -> "Día inválido";
};

// Operador ternario — condicional en una sola línea
String acceso = (edad >= 18) ? "Permitido" : "Denegado";
```

---

## 14. Bucle / Ciclo

### 📖 Definición

Un **bucle** (también llamado **ciclo** o **loop**) es una estructura de control que **repite un bloque de código** mientras se cumpla una condición, o una cantidad determinada de veces. Evita escribir el mismo código repetido manualmente.

### 🌐 Perspectiva universal

```python
# Python
for i in range(5):
    print(i)

while condicion:
    hacer_algo()
```
```javascript
// JavaScript
for (let i = 0; i < 5; i++) {
    console.log(i);
}
```

### ☕ En Java

```java
// for — cuando se sabe cuántas veces iterar
for (int i = 0; i < 5; i++) {
    System.out.println("Vuelta " + i);  // 0, 1, 2, 3, 4
}

// for-each — para recorrer colecciones
List<String> nombres = List.of("Ana", "Luis", "María");
for (String nombre : nombres) {
    System.out.println("Hola, " + nombre);
}

// while — cuando no se sabe cuántas veces se itera
int intentos = 0;
while (intentos < 3) {
    System.out.println("Intento " + (intentos + 1));
    intentos++;
}

// do-while — se ejecuta al menos una vez
int opcion;
do {
    opcion = leerOpcionDelUsuario();
} while (opcion < 1 || opcion > 5);
```

### ⚠️ Bucle infinito

Un bucle que nunca termina porque su condición nunca se vuelve `false`:

```java
// ❌ Bucle infinito por error
int i = 0;
while (i < 10) {
    System.out.println(i);
    // i nunca aumenta → la condición nunca se vuelve false → loop infinito
}

// ✅ Correcto
while (i < 10) {
    System.out.println(i);
    i++; // avanza hacia el fin del bucle
}
```

---

## 15. Función / Método

### 📖 Definición

Una **función** es un **bloque de código reutilizable con nombre** que realiza una tarea específica y puede ser llamado (invocado) desde distintos puntos del programa. En lenguajes orientados a objetos, cuando una función pertenece a una clase, se la llama **método**.

- **Función** → término general (Python, JavaScript, C, etc.)
- **Método** → función que pertenece a una clase (Java, C#, etc.)

### 🌐 En otros lenguajes

```python
# Python: función con def
def calcular_total(precio, cantidad):
    return precio * cantidad
```
```javascript
// JavaScript: función con function o arrow
function calcularTotal(precio, cantidad) {
    return precio * cantidad;
}
const calcularTotal = (precio, cantidad) => precio * cantidad;
```

### ☕ En Java

```java
// Método que retorna un valor
public double calcularTotal(double precio, int cantidad) {
    return precio * cantidad;
}

// Método que no retorna nada (void)
public void imprimirSaludo(String nombre) {
    System.out.println("Hola, " + nombre);
}

// Llamada al método (invocación)
double total = calcularTotal(29.99, 3);  // → 89.97
imprimirSaludo("Ana");                   // imprime "Hola, Ana"
```

### ⚠️ Ventajas de usar funciones/métodos

1. **Reutilización** — escríbelo una vez, úsalo muchas veces.
2. **Legibilidad** — un buen nombre de función hace el código autodocumentado.
3. **Mantenimiento** — si cambia la lógica, solo cambias un lugar.

---

## 16. Parámetro y argumento

### 📖 Definición

Son dos conceptos que la gente mezcla constantemente:

- **Parámetro** — la variable que **recibe** el valor en la **definición** de la función. Es el "hueco" que la función declara.
- **Argumento** — el valor **concreto** que se **pasa** cuando se **llama** a la función. Es lo que entra en ese hueco.

### ☕ En Java

```java
//           ↓ parámetros: están en la definición
public double calcularDescuento(double precio, double porcentaje) {
    return precio * porcentaje / 100;
}

//                                ↓ argumentos: están en la llamada
double resultado = calcularDescuento(1000.0, 15.0);
//                                   ↑          ↑
//                          precio=1000.0  porcentaje=15.0
```

### ⚠️ Regla mnemotécnica

> **P**arámetro = **P**lantilla (en la definición)  
> **A**rgumento = **A**ctual (en la llamada, el valor real)

---

## 17. Retorno

### 📖 Definición

El **retorno** es el valor que una función/método **devuelve** a quien la llamó. Cuando una función termina con `return valor`, ese valor "sale" de la función y puede ser usado en el lugar donde se hizo la llamada.

### 🌐 Perspectiva universal

```python
# Python
def sumar(a, b):
    return a + b

resultado = sumar(3, 4)  # resultado = 7
```
```javascript
// JavaScript
function sumar(a, b) {
    return a + b;
}
const resultado = sumar(3, 4);  // resultado = 7
```

### ☕ En Java

```java
// Función con retorno: el tipo de retorno va antes del nombre
public int sumar(int a, int b) {
    return a + b;  // devuelve el resultado al que llamó
}

// Función sin retorno: void
public void saludar(String nombre) {
    System.out.println("Hola, " + nombre);
    // no necesita return (puede usarse return; para salir anticipadamente)
}

// Uso del retorno
int resultado = sumar(3, 4);  // resultado = 7
System.out.println(resultado * 2); // también se puede usar directamente: sumar(3,4) * 2
```

### ⚠️ `return` termina la función

```java
public String clasificar(int nota) {
    if (nota >= 70) {
        return "Aprobado";  // la función termina aquí si nota >= 70
    }
    return "Reprobado";     // solo llega aquí si nota < 70
}
```

---

## 18. Ámbito (Scope)

### 📖 Definición

El **ámbito** (en inglés, *scope*) es **el área del código donde una variable existe y puede ser usada**. Fuera de su ámbito, la variable no existe (o es una variable distinta con el mismo nombre).

### 🌐 Perspectiva universal

El concepto de scope existe en todos los lenguajes, aunque las reglas exactas varían.

### ☕ En Java

```java
public class EjemploScope {

    // Ámbito de clase: existe durante toda la vida del objeto
    private String nombre = "Global";

    public void ejemploScope() {
        // Ámbito de método: existe mientras el método se ejecuta
        int x = 10;

        if (x > 5) {
            // Ámbito de bloque: solo existe dentro de este if
            int y = 20;
            System.out.println(x); // ✅ x existe aquí
            System.out.println(y); // ✅ y existe aquí
        }

        System.out.println(x); // ✅ x todavía existe
        System.out.println(y); // ❌ ERROR: y no existe fuera del bloque if
    }
}
```

### ⚠️ Por qué importa el scope

- Evita que variables de diferentes partes del programa se "pisen" entre sí.
- Permite reutilizar nombres de variables en distintas funciones sin conflicto.
- Libera memoria automáticamente cuando la variable sale de su ámbito.

---

## 19. Clase

### 📖 Definición

Una **clase** es una **plantilla o molde** que define la estructura y el comportamiento de un tipo de objeto. Especifica qué datos tiene (atributos/campos) y qué puede hacer (métodos). La clase en sí no es un objeto: es el diseño a partir del cual se crean objetos.

> **Analogía:** la clase es el plano de un edificio; los objetos son los edificios construidos a partir de ese plano.

### 🌐 Perspectiva universal

Las clases existen en todos los lenguajes orientados a objetos:

```python
# Python
class Persona:
    def __init__(self, nombre, edad):
        self.nombre = nombre
        self.edad = edad
```
```javascript
// JavaScript
class Persona {
    constructor(nombre, edad) {
        this.nombre = nombre;
        this.edad = edad;
    }
}
```

### ☕ En Java

```java
public class Persona {
    // Atributos (datos que tiene cada persona)
    private String nombre;
    private int    edad;

    // Constructor (cómo se crea una persona)
    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad   = edad;
    }

    // Método (qué puede hacer una persona)
    public String saludar() {
        return "Hola, soy " + nombre + " y tengo " + edad + " años.";
    }
}
```

---

## 20. Objeto / Instancia

### 📖 Definición

Un **objeto** (también llamado **instancia**) es una **realización concreta de una clase**. Si la clase es el plano, el objeto es la cosa construida. Cada objeto tiene su propia copia de los atributos definidos en la clase.

- **Instanciar** = crear un objeto a partir de una clase.
- **Instancia** = sinónimo de objeto cuando se quiere enfatizar que fue creado desde una clase.

### ☕ En Java

```java
// Crear objetos (instanciar la clase Persona)
Persona persona1 = new Persona("Ana", 25);
Persona persona2 = new Persona("Luis", 30);

// Cada objeto tiene sus propios datos
System.out.println(persona1.saludar()); // "Hola, soy Ana y tengo 25 años."
System.out.println(persona2.saludar()); // "Hola, soy Luis y tengo 30 años."

// Son objetos distintos aunque sean del mismo tipo
persona1 == persona2; // false (diferentes objetos en memoria)
```

### ⚠️ Clase vs Objeto

| | Clase | Objeto |
|--|-------|--------|
| ¿Qué es? | Plantilla / diseño | Cosa concreta |
| ¿Cuántos hay? | Una sola | Muchos (uno por cada `new`) |
| ¿Ocupa memoria? | Sí (el código) | Sí (los datos de cada instancia) |
| Ejemplo | `Persona` | `persona1`, `persona2` |

---

## 21. Interfaz / Contrato

### 📖 Definición

Una **interfaz** es un **contrato** que define qué métodos debe tener una clase, sin decir cómo los implementa. Es una forma de decir: *"cualquier clase que implemente esta interfaz debe ser capaz de hacer estas cosas"*.

> **Analogía:** una interfaz es como los requisitos de un cargo de trabajo: define qué debes saber hacer, pero no cómo lo haces.

### 🌐 Perspectiva universal

El concepto existe bajo distintos nombres:
- Java, C#: `interface`
- TypeScript: `interface`
- Python: clases abstractas (protocolo similar)
- Go: `interface` (pero sin declararlo explícitamente)

### ☕ En Java

```java
// Definición de la interfaz (el contrato)
public interface Pagable {
    double calcularTotal();
    void procesarPago(double monto);
}

// Clase que cumple el contrato
public class Ticket implements Pagable {

    @Override
    public double calcularTotal() {
        return 150.0; // implementación concreta
    }

    @Override
    public void procesarPago(double monto) {
        System.out.println("Pagando $" + monto);
    }
}
```

---

## 22. Módulo / Paquete / Namespace

### 📖 Definición

Son formas de **organizar y agrupar** el código en unidades lógicas para evitar conflictos de nombres y facilitar el mantenimiento. El nombre varía según el lenguaje:

| Lenguaje | Término | Ejemplo |
|----------|---------|---------|
| Java | Paquete (`package`) | `cl.duoc.fullstack.tickets` |
| Python | Módulo / paquete | `services/ticket_service.py` |
| JavaScript / TypeScript | Módulo (ES modules) | `import { Ticket } from './ticket'` |
| C# | Namespace | `namespace DuocFullstack.Tickets` |
| Go | Package | `package tickets` |

### ☕ En Java

```java
// Declarar a qué paquete pertenece esta clase
package cl.duoc.fullstack.tickets.service;

// Importar clases de otro paquete
import cl.duoc.fullstack.tickets.model.Ticket;
import java.util.List;

public class TicketService {
    // ...
}
```

### ⚠️ Por qué existen

Sin paquetes/módulos, dos clases llamadas `Usuario` en distintas partes de un proyecto grande generarían conflictos. Los paquetes los distinguen:

- `com.empresa.rrhh.Usuario`  
- `com.empresa.ecommerce.Usuario`

---

## 23. Array / Arreglo

### 📖 Definición

Un **array** (o **arreglo**) es una **colección ordenada de elementos del mismo tipo**, almacenados de forma contigua en memoria. Cada elemento se accede mediante un **índice** (posición), que en la mayoría de los lenguajes empieza en **0**.

### 🌐 Perspectiva universal

```python
# Python: lista (más flexible que array clásico)
nombres = ["Ana", "Luis", "María"]
print(nombres[0])  # "Ana"
```
```javascript
// JavaScript
const nombres = ["Ana", "Luis", "María"];
console.log(nombres[0]);  // "Ana"
```

### ☕ En Java

```java
// Declarar y crear un array de enteros con 5 posiciones
int[] numeros = new int[5];
numeros[0] = 10;
numeros[1] = 20;
// ...

// Declarar con valores iniciales
String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};

// Acceder por índice
System.out.println(dias[0]);  // "Lunes"
System.out.println(dias[4]);  // "Viernes"
System.out.println(dias[5]);  // ❌ ArrayIndexOutOfBoundsException

// Recorrer con for-each
for (String dia : dias) {
    System.out.println(dia);
}

// Longitud del array
System.out.println(dias.length);  // 5
```

### ⚠️ El índice empieza en 0

```
Posición:   0       1         2          3        4
           "Lunes" "Martes" "Miércoles" "Jueves" "Viernes"
```

Un array de 5 elementos tiene índices del **0 al 4**. Acceder al índice 5 es un error.

---

## 24. Excepción / Error

### 📖 Definición

Una **excepción** es un evento inesperado que ocurre durante la ejecución del programa y que **interrumpe el flujo normal**. En lugar de que el programa simplemente "crashee", los lenguajes modernos permiten **capturar** esa excepción y manejarla de forma controlada.

> **Analogía:** si estás cocinando y se acaba el gas, no dejas de cocinar para siempre — buscas una solución alternativa. Las excepciones son esos "problemas inesperados" y el manejo de excepciones es "qué haces cuando ocurren".

### 🌐 Perspectiva universal

```python
# Python
try:
    resultado = 10 / 0
except ZeroDivisionError:
    print("No se puede dividir entre cero")
```
```javascript
// JavaScript
try {
    JSON.parse("texto inválido");
} catch (error) {
    console.log("Error al parsear:", error.message);
}
```

### ☕ En Java

```java
// Lanzar una excepción
public double dividir(double a, double b) {
    if (b == 0) {
        throw new IllegalArgumentException("No se puede dividir entre cero");
    }
    return a / b;
}

// Capturar una excepción
try {
    double resultado = dividir(10, 0);
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    System.out.println("Esto siempre se ejecuta");
}
```

---

## 25. Algoritmo

### 📖 Definición

Un **algoritmo** es una **secuencia finita y ordenada de pasos** que resuelve un problema o realiza una tarea. No es código — es la lógica de la solución, independiente del lenguaje. El código es la implementación de un algoritmo.

> **Analogía:** una receta de cocina es un algoritmo. Puedes seguirla en cualquier cocina (lenguaje) y obtendrás el mismo plato (resultado).

### 🌐 Perspectiva universal

Un mismo algoritmo puede implementarse en cualquier lenguaje:

```
Algoritmo: encontrar el mayor de dos números
1. Recibir número A y número B
2. Si A > B → retornar A
3. Si no → retornar B
```

```java   // En Java
public int mayor(int a, int b) { return a > b ? a : b; }
```
```python  # En Python
def mayor(a, b): return a if a > b else b
```
```javascript  // En JavaScript
const mayor = (a, b) => a > b ? a : b;
```

### ⚠️ Algoritmo ≠ Código

Primero piensas el algoritmo (la lógica), luego lo escribes en código (la implementación). Si el algoritmo está mal, el código también estará mal, sin importar si la sintaxis es perfecta.

---

## 26. Referencia

### 📖 Definición

Una **referencia** es un "puntero" o "dirección" que apunta a **dónde está un objeto en memoria**, en lugar de contener el objeto en sí mismo. En Java, cuando trabajas con objetos, las variables no contienen el objeto — contienen una referencia a él.

### ☕ En Java

```java
// 'p1' no contiene el objeto Persona — contiene la REFERENCIA (dirección de memoria)
Persona p1 = new Persona("Ana", 25);

// 'p2' apunta al MISMO objeto que 'p1'
Persona p2 = p1;

// Si modificas a través de p2, también afecta a p1 (son el mismo objeto)
p2.setNombre("María");
System.out.println(p1.getNombre()); // "María" — porque p1 y p2 apuntan al mismo objeto
```

```java
// Con tipos primitivos NO hay referencias — se copia el valor
int a = 5;
int b = a;   // b tiene una COPIA del valor 5
b = 10;
System.out.println(a); // sigue siendo 5 — a y b son independientes
```

### ⚠️ Por qué `==` no compara contenido en objetos

```java
String s1 = new String("Hola");
String s2 = new String("Hola");

s1 == s2       // false — son referencias a objetos distintos (aunque igual contenido)
s1.equals(s2)  // true  — compara el contenido
```

---

## Tabla resumen

| # | Concepto | En una línea |
|---|----------|--------------|
| 1 | **Identificador** | El nombre que le das a algo en el código |
| 2 | **Tipo de dato** | Qué clase de información almacena una variable |
| 3 | **Literal** | Un valor fijo escrito directamente en el código |
| 4 | **Variable** | Espacio con nombre que guarda un valor que puede cambiar |
| 5 | **Constante** | Como una variable, pero su valor no puede cambiar |
| 6 | **Declaración** | Presentar al compilador que existe algo con ese nombre |
| 7 | **Asignación** | Guardar un valor en una variable |
| 8 | **Expresión** | Combinación que se evalúa y produce un resultado |
| 9 | **Operador** | Símbolo que realiza una operación sobre valores |
| 10 | **Sentencia** | Una instrucción completa y ejecutable |
| 11 | **Bloque** | Grupo de sentencias tratadas como unidad (`{ }`) |
| 12 | **Comentario** | Texto explicativo que el compilador ignora |
| 13 | **Condicional** | Estructura que decide qué ejecutar según una condición |
| 14 | **Bucle / Ciclo** | Estructura que repite un bloque de código |
| 15 | **Función / Método** | Bloque de código reutilizable con nombre |
| 16 | **Parámetro / Argumento** | Hueco en la definición / valor concreto en la llamada |
| 17 | **Retorno** | El valor que devuelve una función al terminar |
| 18 | **Ámbito (Scope)** | Área del código donde una variable existe |
| 19 | **Clase** | Plantilla que define atributos y métodos de un objeto |
| 20 | **Objeto / Instancia** | Realización concreta de una clase |
| 21 | **Interfaz / Contrato** | Define QUÉ debe hacer una clase, sin decir CÓMO |
| 22 | **Módulo / Paquete** | Agrupación lógica de código para organizar y evitar conflictos |
| 23 | **Array / Arreglo** | Colección ordenada de elementos del mismo tipo |
| 24 | **Excepción / Error** | Evento inesperado que interrumpe el flujo normal |
| 25 | **Algoritmo** | Secuencia de pasos que resuelve un problema |
| 26 | **Referencia** | Dirección de memoria que apunta a un objeto |

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Code Complete** | Steve McConnell | Intermedio | El estándar de la industria en construcción de software. Dedica capítulos enteros a variables, funciones, naming y sentencias — exactamente este vocabulario en profundidad |
| **Clean Code** | Robert C. Martin | Intermedio | Profundiza en naming, funciones, comentarios y clases con ejemplos Java reales |
| **The Pragmatic Programmer** | Hunt & Thomas | Principiante / Intermedio | Capítulo "Your Code as Communication": por qué los nombres importan y cómo pensar en los conceptos básicos del código |
| **Structure and Interpretation of Computer Programs (SICP)** | Abelson & Sussman | Avanzado | El libro que enseña a pensar en expresiones, procedimientos y abstracción desde los fundamentos. Referencia clásica del MIT |
| **A Mind for Numbers** | Barbara Oakley | Cualquiera | No es de programación, pero explica cómo aprender vocabulario técnico nuevo de forma efectiva — útil al empezar |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Glosario de CS de MDN** | https://developer.mozilla.org/es/docs/Glossary | Definiciones concisas de términos de programación y web en español |
| **CS50x de Harvard (gratuito)** | https://cs50.harvard.edu/x | El mejor curso introductorio de ciencias de la computación. Enseña este vocabulario desde cero con ejercicios |
| **Roadmap.sh — Backend** | https://roadmap.sh/backend | Mapa visual de los conceptos que un desarrollador backend debe conocer, con los términos de este módulo como base |
| **Programiz — Glosario** | https://www.programiz.com/article/key-terms-in-programming | Términos clave explicados con ejemplos en múltiples lenguajes |
| **Wikipedia — Programming paradigm** | https://es.wikipedia.org/wiki/Paradigma_de_programaci%C3%B3n | Punto de partida para explorar la historia y relación entre los conceptos |

