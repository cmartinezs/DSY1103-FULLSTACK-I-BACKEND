# Módulo 01 — Conceptos base de programación en Java

> **Objetivo:** internalizar los bloques de construcción del código: convenciones de nombres, variables, asignación, operadores, sentencias, bloques, condicionales (`if`, `switch`), bucles y parámetros. Todo con enfoque en Java.

---

## 1.1 Convenciones de nombres — el error más frecuente

Java tiene reglas de escritura que **todo el ecosistema respeta**. Escribir mal el case de un identificador no da error de compilación, pero sí genera confusión, bugs sutiles y rechazo en revisiones de código.

| Qué | Convención | Ejemplos correctos | Ejemplos incorrectos |
|-----|------------|-------------------|----------------------|
| Clases e interfaces | `PascalCase` | `Ticket`, `TicketService`, `UserDTO` | `ticket`, `ticketservice`, `ticket_service` |
| Variables y métodos | `camelCase` | `nombreUsuario`, `calcularPrecio()` | `NombreUsuario`, `nombre_usuario` |
| Constantes | `UPPER_SNAKE_CASE` | `MAX_INTENTOS`, `PI` | `maxIntentos`, `MaxIntentos` |
| Paquetes | `minúsculas.sin.guiones` | `cl.duoc.fullstack.tickets` | `cl.Duoc.FullStack` |
| Archivos `.java` | Igual que la clase pública | `TicketService.java` | `ticketService.java` |

```java
// ✅ Correcto
public class TicketService {          // PascalCase → clase
    private static final int MAX_REINTENTOS = 3;  // UPPER_SNAKE_CASE → constante
    private String nombreUsuario;     // camelCase → variable

    public String obtenerNombre() {   // camelCase → método
        return nombreUsuario;
    }
}

// ❌ Incorrecto (compila, pero está mal)
public class ticketservice {
    private String NombreUsuario;
    private static final int max_reintentos = 3;

    public String ObtenerNombre() {
        return NombreUsuario;
    }
}
```

> 💡 **Regla práctica:** si ves una letra mayúscula al inicio → es una clase. Si ves letras seguidas con mayúsculas en el medio → es una variable o método. Si ves todo en mayúsculas con `_` → es una constante.

---

## 1.2 Estructura mínima de un archivo Java

Cada archivo `.java` sigue esta estructura. La clase pública **debe** tener el mismo nombre que el archivo.

```
NombreArchivo.java
│
└── package cl.duoc.fullstack.tickets;   ← paquete (carpeta)
│
└── import java.util.List;               ← importaciones
│
└── public class NombreArchivo {         ← clase pública (mismo nombre que el archivo)
        // campos, métodos...
    }
```

```java
// Archivo: Ticket.java
package cl.duoc.fullstack.tickets.model;   // en qué "carpeta lógica" vive esta clase

import java.time.LocalDateTime;            // importar clases externas que usaremos

public class Ticket {                      // DEBE llamarse igual que el archivo
    // contenido de la clase...
}
```

> ⚠️ Si llamas a la clase `ticket` pero el archivo se llama `Ticket.java`, **el compilador lanzará un error**.

---

## 1.3 Variables — la caja con nombre

Una **variable** es un espacio en memoria con:
- Un **tipo** (qué clase de dato guarda)
- Un **nombre** (cómo la llamamos en el código)
- Un **valor** (lo que hay guardado dentro)

```
tipo   nombre   = valor ;
 │       │          │    │
 │       │          │    └── fin de sentencia
 │       │          └─────── valor inicial (opcional al declarar)
 │       └────────────────── identificador único en su ámbito
 └────────────────────────── int, String, boolean, double...
```

```java
// Declarar sin inicializar (la variable existe pero no tiene valor)
int edad;

// Declarar e inicializar en la misma línea
int edad = 22;

// Declarar primero, asignar después
String nombre;
nombre = "Ana";

// Constante: una vez asignada no puede cambiar (final)
final double PI = 3.14159;
// PI = 3;  ← Error de compilación: no se puede reasignar una constante
```

### Variables locales vs campos de clase

```java
public class Ejemplo {

    // Campo de clase (field): existe mientras exista el objeto
    private String titulo;

    public void metodoEjemplo() {
        // Variable local: solo existe dentro de este método
        int contador = 0;
        // 'contador' no existe fuera de este bloque
    }
}
```

---

## 1.4 Asignación — el operador `=`

El operador `=` **no significa "igual"** matemáticamente. Significa **"guarda este valor en esta variable"**. Se lee de derecha a izquierda.

```java
int x = 5;       // "guarda 5 en x"
x = x + 1;       // "calcula x+1 (=6), luego guárdalo en x"
// Ahora x vale 6

// NO confundir con ==
x = 5;    // asignación: guarda 5 en x
x == 5;   // comparación: ¿x es igual a 5? (devuelve true/false)
```

```java
// Secuencia de asignaciones — se ejecuta línea por línea
int a = 10;
int b = 20;
int suma = a + b;   // suma = 30
a = suma;           // ahora a = 30, b sigue siendo 20
```

> 🚨 Error clásico: usar `=` donde va `==` (o viceversa) es un bug muy frecuente.
> ```java
> if (estado = "ABIERTO")  // ❌ asignación dentro de un if
> if (estado.equals("ABIERTO"))  // ✅ comparación correcta para objetos
> ```

---

## 1.5 Operadores

### Aritméticos

```java
int a = 10, b = 3;

int suma        = a + b;   // 13
int resta       = a - b;   // 7
int producto    = a * b;   // 30
int cociente    = a / b;   // 3  ← división entera (descarta decimales)
int resto       = a % b;   // 1  ← módulo (resto de la división)

double exacto   = (double) a / b;  // 3.333... ← casting para no perder decimales
```

### Asignación compuesta — forma abreviada

```java
int x = 10;
x += 5;   // equivale a: x = x + 5  → x = 15
x -= 3;   // equivale a: x = x - 3  → x = 12
x *= 2;   // equivale a: x = x * 2  → x = 24
x /= 4;   // equivale a: x = x / 4  → x = 6
x %= 4;   // equivale a: x = x % 4  → x = 2
```

### Incremento y decremento

```java
int i = 5;
i++;   // i pasa a ser 6 (post-incremento)
i--;   // i pasa a ser 5 (post-decremento)
++i;   // i pasa a ser 6 (pre-incremento)

// Diferencia pre vs post cuando se usa dentro de una expresión:
int a = 5;
int b = a++;   // b = 5, luego a = 6  (primero asigna, luego incrementa)
int c = ++a;   // a = 7, luego c = 7  (primero incrementa, luego asigna)
```

### Comparación — siempre devuelven `boolean`

```java
int x = 10;
boolean r1 = x == 10;   // true  (igual a)
boolean r2 = x != 10;   // false (distinto de)
boolean r3 = x > 5;     // true  (mayor que)
boolean r4 = x < 5;     // false (menor que)
boolean r5 = x >= 10;   // true  (mayor o igual)
boolean r6 = x <= 9;    // false (menor o igual)

// Para objetos (String, etc.) usa .equals(), NO ==
String a = "hola";
String b = "hola";
a == b;         // ❌ compara referencias en memoria, no el contenido
a.equals(b);    // ✅ compara el contenido → true
```

### Lógicos — combinan condiciones `boolean`

```java
boolean activo = true;
boolean admin  = false;

boolean ambos   = activo && admin;   // AND → true solo si los dos son true → false
boolean alguno  = activo || admin;   // OR  → true si al menos uno es true  → true
boolean noActivo = !activo;          // NOT → invierte → false

// Ejemplo real
if (activo && !admin) {
    System.out.println("Usuario activo sin permisos de admin");
}
```

> 💡 En el **Módulo 03 (sección 3.8)** encontrarás la guía completa: tabla de verdad, cortocircuito, leyes de De Morgan y las seis buenas prácticas para escribir condiciones compuestas legibles y sin bugs.

---

## 1.6 Sentencias — por qué termina todo con `;`

Una **sentencia** (statement) es la unidad mínima de instrucción. En Java, **toda sentencia termina con punto y coma** (`;`). Es como el punto final de una oración.

```java
int edad = 22;                           // sentencia de declaración
edad = edad + 1;                         // sentencia de asignación
System.out.println("Hola");             // sentencia de llamada a método
boolean mayorDeEdad = edad >= 18;        // sentencia de declaración con expresión
```

```java
// ❌ Sin ; el compilador da error
int x = 5    // SyntaxError: ';' expected

// ❌ Partir una sentencia en múltiples líneas es válido, pero el ; va al final
int resultado =
    10 + 20 +
    30;        // ✅ el ; cierra la sentencia aunque esté en varias líneas
```

> 💡 Las estructuras de control (`if`, `for`, `while`, `class`) **no llevan** `;` al cerrar su bloque `{}`.

---

## 1.7 Bloques de código — las llaves `{}`

Un **bloque** agrupa varias sentencias y define el **ámbito (scope)** de las variables. Toda variable declarada dentro de un bloque **deja de existir** al cerrar ese bloque.

```java
{
    // inicio del bloque
    int x = 10;       // x existe aquí
    System.out.println(x);
}   // fin del bloque — x ya no existe
// System.out.println(x);  ← Error: x no existe aquí
```

```java
public class Demo {                    // bloque de clase
    private int campo = 0;            // existe mientras exista el objeto

    public void metodo() {            // bloque de método
        int local = 5;               // existe solo en este método

        if (local > 0) {             // bloque de if
            int interno = 1;         // existe solo en este if
            System.out.println(local + interno);
        }
        // 'interno' ya no existe aquí
        System.out.println(local);   // ✅ 'local' sí existe aquí
    }
}
```

### Regla de indentación

Cada nivel de bloque se **indenta 4 espacios** (o 1 tab). IntelliJ lo hace automáticamente.

```java
public class Ejemplo {                // nivel 0
    public void metodo() {            // nivel 1
        if (true) {                   // nivel 2
            System.out.println("ok"); // nivel 3
        }
    }
}
```

---

## 1.8 Condicionales — `if / else if / else`

Un bloque `if` ejecuta código **solo si una condición es `true`**. Java evalúa la condición (entre paréntesis) y, si da `true`, entra al bloque `{}`.

```
if (condición) {
     │
     └── debe ser boolean: true o false
```

```java
// Solo if — hace algo si se cumple la condición
int temperatura = 35;
if (temperatura > 30) {
    System.out.println("Hace calor");
}

// if + else — una rama u otra
int edad = 16;
if (edad >= 18) {
    System.out.println("Mayor de edad");
} else {
    System.out.println("Menor de edad");
}

// if + else if + else — múltiples condiciones en cadena
int nota = 75;
if (nota >= 90) {
    System.out.println("Sobresaliente");
} else if (nota >= 70) {
    System.out.println("Aprobado");
} else if (nota >= 50) {
    System.out.println("Suficiente");
} else {
    System.out.println("Reprobado");
}
```

> ⚠️ **Errores frecuentes:**
> - Poner `;` después del `if (condición)` → el bloque nunca se ejecuta
> - Usar `=` en lugar de `==` dentro de la condición → asigna en vez de comparar
> - Comparar `String` con `==` en vez de `.equals()`

```java
// ❌ Incorrecto: ; después del if — el bloque {} no está ligado al if
if (nota >= 70);          // esta línea es el "cuerpo" vacío del if
{
    System.out.println("Aprobado");  // esto SIEMPRE se ejecuta
}

// ❌ Incorrecto: == en String compara referencias, no contenido
String estado = "ABIERTO";
if (estado == "ABIERTO") { ... }    // puede fallar

// ✅ Correcto
if (estado.equals("ABIERTO")) { ... }
```

---

## 1.9 `switch` — elegir entre múltiples valores fijos

Cuando necesitas comparar **una misma variable contra varios valores posibles**, `switch` es más legible que una cadena de `if/else if`.

```java
// switch clásico — compara una variable contra cases fijos
String estado = "EN_PROCESO";

switch (estado) {
    case "ABIERTO":
        System.out.println("Ticket recién creado");
        break;              // ← OBLIGATORIO: sin él, el código "cae" al siguiente case
    case "EN_PROCESO":
        System.out.println("Ticket siendo atendido");
        break;
    case "CERRADO":
        System.out.println("Ticket resuelto");
        break;
    default:                // se ejecuta si ningún case coincide
        System.out.println("Estado desconocido: " + estado);
}
```

> ⚠️ **El `break` es obligatorio** en el switch clásico. Si lo olvidas, Java sigue ejecutando los cases siguientes (*fall-through*), lo que casi siempre es un bug.

```java
// Ejemplo de fall-through accidental (bug frecuente)
switch (estado) {
    case "ABIERTO":
        System.out.println("Abierto");
        // ← falta break → ejecuta también el case siguiente!
    case "EN_PROCESO":
        System.out.println("En proceso");
        break;
}
// Si estado = "ABIERTO" imprime: "Abierto" Y "En proceso"
```

```java
// switch con int
int diaSemana = 3;
switch (diaSemana) {
    case 1: System.out.println("Lunes");    break;
    case 2: System.out.println("Martes");   break;
    case 3: System.out.println("Miércoles"); break;
    case 4: System.out.println("Jueves");   break;
    case 5: System.out.println("Viernes");  break;
    default: System.out.println("Fin de semana");
}
```

> 💡 En el **Módulo 03** verás el `switch` moderno (Java 14+) que usa `->` en lugar de `:` + `break`, elimina el fall-through y puede retornar un valor directamente.

---

## 1.10 Bucles — repetir acciones

### `for` clásico — cuando sabes cuántas veces iterar

```
for (inicio ; condición ; paso) {
      │           │         │
      │           │         └── qué cambia en cada vuelta
      │           └──────────── mientras esto sea true, sigue
      └──────────────────────── valor inicial del contador
```

```java
// Imprimir números del 1 al 5
for (int i = 1; i <= 5; i++) {
    System.out.println(i);   // imprime 1, 2, 3, 4, 5
}

// Recorrer un array por índice
String[] nombres = {"Ana", "Luis", "Marta"};
for (int i = 0; i < nombres.length; i++) {
    System.out.println(nombres[i]);  // Ana, Luis, Marta
}
```

### `while` — cuando no sabes cuántas veces iterar

```java
int intentos = 0;
while (intentos < 3) {
    System.out.println("Intento " + (intentos + 1));
    intentos++;
}
// Si intentos empieza en 3 o más, el bloque NUNCA se ejecuta
```

### `do-while` — se ejecuta al menos una vez

```java
int numero;
do {
    numero = pedirNumeroAlUsuario();   // se ejecuta al menos una vez
} while (numero < 0);                 // repite si el número es negativo
```

### `for-each` — la forma más limpia de recorrer colecciones

```java
// En lugar de:
for (int i = 0; i < nombres.length; i++) {
    System.out.println(nombres[i]);
}

// Usa esto cuando no necesitas el índice:
for (String nombre : nombres) {
    System.out.println(nombre);   // más limpio, menos error-prone
}
```

---

## 1.11 Arrays y listas — cómo iterar

### Arrays — tamaño fijo

```java
// Declarar un array de 5 enteros (todos en 0 por defecto)
int[] numeros = new int[5];

// Declarar e inicializar directamente
int[] numeros = {10, 20, 30, 40, 50};
String[] estados = {"ABIERTO", "EN_PROCESO", "CERRADO"};

// Acceder por índice (empieza en 0)
System.out.println(numeros[0]);   // 10
System.out.println(numeros[4]);   // 50
// numeros[5]  ← Error en tiempo de ejecución: ArrayIndexOutOfBoundsException

// Recorrer
for (int n : numeros) {
    System.out.println(n);
}

// Tamaño de un array
System.out.println(numeros.length);  // 5 (es un campo, sin paréntesis)
```

### Listas — tamaño dinámico

```java
import java.util.ArrayList;
import java.util.List;

// Crear una lista vacía de Strings
List<String> nombres = new ArrayList<>();

// Agregar elementos
nombres.add("Ana");
nombres.add("Luis");
nombres.add("Marta");

// Acceder por índice
System.out.println(nombres.get(0));    // Ana
System.out.println(nombres.size());    // 3 (es un método, lleva paréntesis)

// Eliminar
nombres.remove("Luis");               // por valor
nombres.remove(0);                    // por índice

// Recorrer con for-each
for (String nombre : nombres) {
    System.out.println(nombre);
}

// Recorrer con índice (cuando necesitas saber la posición)
for (int i = 0; i < nombres.size(); i++) {
    System.out.println(i + ": " + nombres.get(i));
}
```

> 💡 **Array vs List:** usa `int[]` cuando el tamaño es fijo y conocido. Usa `List<String>` cuando el tamaño puede cambiar (agregar/quitar elementos).

---

## 1.12 Parámetros — datos que entran a un método

Los **parámetros** son las "entradas" que un método recibe para hacer su trabajo. Se declaran entre los paréntesis del método.

```
tipo nombreMétodo(tipo1 param1, tipo2 param2) {
                   │               │
                   └──────┬────────┘
                     parámetros: las entradas del método
```

```java
// Método con 2 parámetros: recibe dos números y devuelve su suma
public int sumar(int a, int b) {
    return a + b;
}

// Llamada al método — los valores que se pasan se llaman "argumentos"
int resultado = sumar(5, 3);   // 5 y 3 son los argumentos
```

### Diferencia: parámetro vs argumento

```java
// En la DEFINICIÓN del método: se llaman parámetros
public void saludar(String nombre) {     // 'nombre' es el PARÁMETRO
    System.out.println("Hola, " + nombre);
}

// En la LLAMADA al método: se llaman argumentos
saludar("Ana");    // "Ana" es el ARGUMENTO que se pasa al parámetro 'nombre'
```

### Los parámetros tienen scope local

```java
public void procesar(int numero) {
    int doble = numero * 2;
    System.out.println(doble);
}
// Fuera del método, 'numero' y 'doble' no existen
```

### Métodos sin parámetros y métodos con múltiples parámetros

```java
// Sin parámetros: paréntesis vacíos
public void imprimirFecha() {
    System.out.println(LocalDate.now());
}

// Múltiples parámetros — se separan con coma
public String crearTicket(String titulo, String descripcion, String estado) {
    return titulo + " | " + descripcion + " | " + estado;
}

// Llamada
crearTicket("Bug login", "No carga la pantalla", "ABIERTO");
```

---

## Resumen visual

```
Archivo: Ticket.java
         ──────
         PascalCase = clase

┌─────────────────────────────────────────────────────────┐
│ package cl.duoc.fullstack.tickets.model;  ← paquete      │
│                                                           │
│ public class Ticket {                     ← clase         │
│                                                           │
│   private String titulo;  ← campo (variable de clase)     │
│   private static final int MAX = 5;  ← constante          │
│                                                           │
│   public void setTitulo(String titulo) {  ← método        │
│   //                    ───────────────                   │
│   //                    parámetro                         │
│                                                           │
│       this.titulo = titulo;  ← sentencia de asignación    │
│   //              ↑                                       │
│   //              operador de asignación                  │
│                                                           │
│   }  ← cierre de bloque del método                       │
│                                                           │
│ }  ← cierre de bloque de la clase                        │
└─────────────────────────────────────────────────────────┘
```

---

## Ejercicio

Dado el siguiente código con **6 errores** (de naming, asignación y estructura), identifícalos y corrígelos:

```java
public class ticket_service {

    private String NombreCliente;
    private static final int maxReintentos = 3;

    Public void ProcesarTicket(String titulo, int intentos) {
        boolean completado = intentos == maxReintentos
        if (completado = true) {
            System.out.println("Completado: " + titulo)
        }
    }
}
```

<details>
<summary>Ver solución</summary>

```java
// Errores corregidos:
// 1. ticket_service → TicketService (PascalCase)
// 2. NombreCliente → nombreCliente (camelCase)
// 3. maxReintentos → MAX_REINTENTOS (UPPER_SNAKE_CASE para constante)
// 4. Public → public (minúscula)
// 5. ProcesarTicket → procesarTicket (camelCase)
// 6. completado = true → completado == true (o simplemente 'completado')
// + Faltan ; al final de sentencias

public class TicketService {

    private String nombreCliente;
    private static final int MAX_REINTENTOS = 3;

    public void procesarTicket(String titulo, int intentos) {
        boolean completado = intentos == MAX_REINTENTOS;
        if (completado) {
            System.out.println("Completado: " + titulo);
        }
    }
}
```
</details>

---

*[← Módulo 00 — Repaso rápido](./00_repaso_rapido.md) | [Módulo 02 — Sintaxis esencial y tipos →](./02_sintaxis_y_tipos.md)*

