# Módulo 02 — Sintaxis esencial y tipos de datos

> **Objetivo:** dominar la sintaxis fundamental de Java y entender cómo Java 21 la moderniza con `var`, text blocks y records.

---

## 1.1 Estructura mínima de un programa Java

En Java, **todo el código debe vivir dentro de una clase**. No existen funciones sueltas ni código ejecutable fuera de una clase, como en otros lenguajes. El punto de entrada de cualquier programa es el método `main`, cuya firma exacta (`public static void main(String[] args)`) debe respetarse para que la JVM lo reconozca.

Para mostrar texto en consola, Java ofrece tres variantes: `println` (imprime y añade salto de línea), `print` (imprime sin salto) y `printf` (permite formatear la salida con marcadores de posición como `%s` para texto o `%d` para enteros, similar al `printf` de C).

```java
// Nombre del archivo debe coincidir EXACTAMENTE con el nombre de la clase pública
public class HolaMundo {

    // Punto de entrada: el método main
    public static void main(String[] args) {
        System.out.println("Hola, mundo!"); // imprime con salto de línea al final
        System.out.print("Sin salto ");     // imprime sin salto de línea
        System.out.printf("Nombre: %s, Edad: %d%n", "Ana", 25); // formato: %s=texto, %d=entero
    }
}
```

> 💡 En Java, **todo va dentro de una clase**. No existen funciones sueltas fuera de una clase.

---

## 1.2 Tipos primitivos

Java tiene 8 tipos primitivos que viven en la pila (stack), no en el heap. Son los bloques de construcción más básicos y representan valores simples, no objetos. Conocerlos evita errores de desbordamiento (overflow) y pérdida de precisión en cálculos.

Para el día a día, usarás principalmente: `int` para números enteros, `double` para decimales, `boolean` para condiciones y `char` para caracteres individuales. Los otros tipos (`byte`, `short`, `long`, `float`) se usan en contextos específicos donde el tamaño del dato importa.

| Tipo | Tamaño | Rango | Ejemplo |
|------|--------|-------|---------|
| `byte` | 8 bits | −128 a 127 | `byte b = 100;` |
| `short` | 16 bits | −32 768 a 32 767 | `short s = 1000;` |
| `int` | 32 bits | −2 147 483 648 a 2 147 483 647 | `int n = 42;` |
| `long` | 64 bits | muy grande | `long l = 10_000_000_000L;` |
| `float` | 32 bits | ~7 dígitos decimales | `float f = 3.14f;` |
| `double` | 64 bits | ~15 dígitos decimales | `double d = 3.14159;` |
| `boolean` | 1 bit lógico | `true` / `false` | `boolean ok = true;` |
| `char` | 16 bits | carácter Unicode | `char c = 'A';` |

```java
// Los guiones bajos en números (Java 7+) mejoran la legibilidad sin afectar el valor
long poblacionMundial = 8_100_000_000L;  // la L indica que es long, no int
int hexadecimal       = 0xFF_EC_D1_12;

// Casting: convierte un tipo en otro compatible
// De mayor a menor precisión debes hacer el cast explícito (puedes perder datos)
double precio = 9.99;
int precioEntero = (int) precio;   // 9 — trunca los decimales, NO redondea
// De menor a mayor precisión el casting es automático (widening)
int entero = 5;
double exacto = entero;  // 5.0 — no necesitas cast
```

---

## 1.3 Tipos de referencia y `String`

Los tipos de referencia viven en el heap. A diferencia de los primitivos, una variable de referencia **no contiene el dato en sí**, sino la dirección de memoria donde está el objeto. Esto tiene una consecuencia importante: comparar dos referencias con `==` compara las direcciones, no los contenidos.

`String` es el tipo de referencia más usado. Es **inmutable**: ningún método modifica el String original, siempre retorna uno nuevo. Esto hace que sea seguro compartirlo y que Java pueda optimizar su almacenamiento en un pool interno.

```java
String nombre  = "Spring Boot";  // literal (internado en pool de strings)
String vacio   = "";
String nulo    = null;            // referencia que apunta a nada

// Métodos útiles de String
String s = "  Hola, Mundo!  ";
System.out.println(s.trim());           // "Hola, Mundo!"
System.out.println(s.toLowerCase());    // "  hola, mundo!  "
System.out.println(s.contains("Mundo"));// true
System.out.println(s.replace("Mundo", "Spring")); // "  Hola, Spring!  "
System.out.println(s.split(",")[0]);    // "  Hola"

// Concatenación: usa + para pocas concatenaciones
String saludo = "Hola, " + nombre + "!";

// StringBuilder: usa para concatenaciones en bucles (más eficiente)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 5; i++) {
    sb.append("item").append(i).append("\n");
}
String resultado = sb.toString();

// Comparar strings: SIEMPRE con .equals(), nunca con ==
String a = "Java";
String b = "Java";
System.out.println(a == b);       // true a veces (pool), pero NO confíes en esto
System.out.println(a.equals(b));  // true — siempre correcto
```

---

## 1.4 `var` — inferencia de tipo local (Java 10+)

`var` permite que el compilador **infiera el tipo** en variables locales. El tipo sigue siendo estático (no es como JavaScript); solo evitas escribirlo.

```java
// Sin var (verboso)
ArrayList<String> nombres = new ArrayList<String>();

// Con var (el compilador sabe que es ArrayList<String>)
var nombres = new ArrayList<String>();
var precio  = 99.99;          // double
var mensaje = "Hola";         // String
var usuario = new Usuario();  // Usuario

// var NO se puede usar para:
// - campos de clase
// - parámetros de métodos
// - tipos de retorno
// - cuando el lado derecho es null (no se puede inferir el tipo)
var x = null; // ❌ Error de compilación
```

> ✅ Usa `var` cuando el tipo sea **obvio** por el contexto (lado derecho claro). Evítalo cuando haga el código menos legible.

---

## 1.5 Text Blocks (Java 15+)

Los text blocks permiten escribir strings multilínea de forma limpia, sin concatenaciones ni `\n`.

```java
// Antes — difícil de leer y mantener
String jsonAntes = "{\n" +
    "  \"nombre\": \"Ana\",\n" +
    "  \"edad\": 25\n" +
    "}";

// Ahora con text block — lo que ves es lo que obtienes
String json = """
        {
          "nombre": "Ana",
          "edad": 25
        }
        """;

// Muy útil para SQL embebido en Java
String sql = """
        SELECT u.nombre, u.email
        FROM usuarios u
        WHERE u.activo = true
        ORDER BY u.nombre
        """;

// Muy útil para HTML en respuestas de prueba
String html = """
        <html>
            <body>
                <h1>Hola desde Spring Boot</h1>
            </body>
        </html>
        """;
```

> 💡 La **indentación de cierre** (`"""`) determina cuántos espacios se eliminan automáticamente del inicio de cada línea.

---

## 1.6 Constantes

Una constante es un valor que **no cambia** durante la ejecución del programa. En Java se declaran con `static final`: `static` porque pertenece a la clase (no a cada instancia) y `final` porque no puede reasignarse una vez asignada. Por convención, se escriben en `MAYUSCULAS_CON_GUIONES_BAJOS` para que sean fácilmente identificables en el código.

Agrupar constantes relacionadas en una clase dedicada (como `EstadoTicket`) centraliza los valores y evita el uso de "magic strings" dispersas por el código.

```java
public class EstadoTicket {
    public static final String ABIERTO   = "ABIERTO";
    public static final String EN_PROGRESO = "EN_PROGRESO";
    public static final String CERRADO   = "CERRADO";
    public static final int    MAXIMO_TICKETS = 100;
}

// Uso: accedes al valor por el nombre de la constante, no por el valor literal
// Si el valor cambia, solo lo cambias en un lugar
String estado = EstadoTicket.ABIERTO;
```

---

## 1.7 Arrays

Un array es una **colección de tamaño fijo** del mismo tipo. Una vez creado, no puedes cambiar su capacidad. Los índices empiezan en `0` y el último elemento está en la posición `length - 1`. Acceder a un índice fuera de rango lanza `ArrayIndexOutOfBoundsException` en tiempo de ejecución.

En Spring Boot casi siempre usarás `List<T>` en lugar de arrays porque las listas son dinámicas y ofrecen muchos más métodos de utilidad. Sin embargo, los arrays aparecen frecuentemente en el código del framework (como el parámetro `String[] args` de `main`) y en algunas APIs de bajo nivel.

```java
// Declaración e inicialización
int[]    numeros = {1, 2, 3, 4, 5};
String[] nombres = new String[3];   // crea un array de 3 elementos, todos null
nombres[0] = "Ana";
nombres[1] = "Luis";
nombres[2] = "María";

// Acceso y longitud
System.out.println(numeros[0]);     // 1 — primer elemento
System.out.println(numeros.length); // 5 — propiedad, sin paréntesis (no es método)

// Array bidimensional: array de arrays
int[][] matriz = {
    {1, 2, 3},
    {4, 5, 6}
};
System.out.println(matriz[1][2]);   // 6 — fila 1, columna 2
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 1.1 — Tipos y conversión
Dado el siguiente código, predice el resultado **antes** de ejecutarlo:

```java
int a = 10;
int b = 3;
double resultado = a / b;
System.out.println(resultado); // ¿Qué imprime?

double resultado2 = (double) a / b;
System.out.println(resultado2); // ¿Y esto?
```

<details>
<summary>🔍 Ver solución</summary>

```
3.0    // La división entera da 3, luego se convierte a double 3.0
3.3333333333333335  // Ahora la división es de doubles
```
El casting `(double)` se aplica **antes** de la división, cambiando la aritmética.
</details>

---

### Ejercicio 1.2 — Manipulación de Strings
Escribe un método que reciba un email como `String` y retorne `true` si el email contiene exactamente un `@` y termina en `.cl` o `.com`.

```java
public static boolean esEmailValido(String email) {
    // Tu código aquí
}
```

<details>
<summary>🔍 Ver solución</summary>

```java
public static boolean esEmailValido(String email) {
    if (email == null || email.isBlank()) return false;

    long arrobas = email.chars()
                        .filter(c -> c == '@')
                        .count();

    return arrobas == 1 && (email.endsWith(".cl") || email.endsWith(".com"));
}

// Pruebas:
// esEmailValido("ana@duoc.cl")   → true
// esEmailValido("sin-arroba.cl") → false
// esEmailValido("dos@@test.com") → false
```
</details>

---

### Ejercicio 1.3 — Text Block
Reescribe el siguiente string usando un text block:

```java
String xml = "<usuario>\n  <nombre>Ana</nombre>\n  <rol>ADMIN</rol>\n</usuario>\n";
```

<details>
<summary>🔍 Ver solución</summary>

```java
String xml = """
        <usuario>
          <nombre>Ana</nombre>
          <rol>ADMIN</rol>
        </usuario>
        """;
```
</details>

---

### Ejercicio 1.4 — var y legibilidad
Decide en cuáles líneas es apropiado usar `var` y en cuáles no. Justifica:

```java
// a)
ArrayList<Map<String, List<Integer>>> datos = new ArrayList<Map<String, List<Integer>>>();

// b)
double precio = calcularPrecioFinal(producto, descuento);

// c)
Scanner sc = new Scanner(System.in);
```

<details>
<summary>🔍 Ver solución</summary>

- **a)** ✅ `var datos = new ArrayList<Map<String, List<Integer>>>();` — el tipo es largo y repetitivo, var mejora legibilidad.
- **b)** ⚠️ `var precio = calcularPrecioFinal(...)` — es discutible. El nombre del método sugiere que es un número, pero `var` oculta que es `double`. Mejor dejarlo explícito.
- **c)** ✅ `var sc = new Scanner(System.in);` — el tipo es obvio por el constructor.
</details>

---

*[← Índice del mini curso](./README.md) | [Módulo 02 → Control de flujo](./02_control_de_flujo.md)*

