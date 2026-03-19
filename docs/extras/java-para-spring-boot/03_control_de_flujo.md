# Módulo 03 — Control de flujo moderno

> **Objetivo:** dominar todas las estructuras de control de Java: condicionales (`if/else`, `switch` clásico y expression), bucles (`for`, `for-each`, `while`, `do-while`, `break`, `continue`) y pattern matching moderno con `instanceof` y `switch` (Java 16-21).

---

## 3.1 `if / else if / else`

```java
int codigo = 404;

if (codigo == 200) {
    System.out.println("OK");
} else if (codigo == 404) {
    System.out.println("No encontrado");
} else if (codigo >= 500) {
    System.out.println("Error del servidor");
} else {
    System.out.println("Código desconocido");
}
```

### Operador ternario

Para asignaciones condicionales simples, el operador ternario es más expresivo:

```java
// if/else verboso
String mensaje;
if (activo) {
    mensaje = "Usuario activo";
} else {
    mensaje = "Usuario inactivo";
}

// Equivalente con ternario — una sola línea
String mensaje = activo ? "Usuario activo" : "Usuario inactivo";

// Ternario anidado (evitar más de un nivel de anidamiento)
String rol = esAdmin ? "ADMIN" : esEditor ? "EDITOR" : "LECTOR";
```

---

## 3.2 `switch` — la versión clásica

```java
String dia = "LUNES";

switch (dia) {
    case "LUNES":
    case "MARTES":
    case "MIERCOLES":
    case "JUEVES":
    case "VIERNES":
        System.out.println("Día laboral");
        break;          // SIN break, cae al siguiente case (fall-through)
    case "SABADO":
    case "DOMINGO":
        System.out.println("Fin de semana");
        break;
    default:
        System.out.println("Día inválido");
}
```

> ⚠️ El `break` olvidado es uno de los bugs más frecuentes en Java clásico. La nueva sintaxis lo elimina.

---

## 3.3 `switch` expression (Java 14+) ⭐

La versión moderna del `switch` es una **expresión** (retorna un valor) y usa `->` en lugar de `:` + `break`.

```java
String dia = "LUNES";

// Switch como expresión: asigna directamente
String tipo = switch (dia) {
    case "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" -> "Laboral";
    case "SABADO", "DOMINGO"                                   -> "Fin de semana";
    default -> throw new IllegalArgumentException("Día desconocido: " + dia);
};

System.out.println(tipo); // "Laboral"

// Con yield: cuando necesitas múltiples líneas en un branch
int numeroDia = switch (dia) {
    case "LUNES"    -> 1;
    case "MARTES"   -> 2;
    case "MIERCOLES"-> 3;
    default         -> {
        System.out.println("Procesando día: " + dia);
        yield -1;  // yield devuelve el valor en bloques multilínea
    }
};
```

> ✅ Usa la switch expression moderna siempre que puedas. Elimina bugs de `break` y es más expresiva.

---

## 3.4 Pattern Matching en `instanceof` (Java 16+)

Antes de Java 16 tenías que castear manualmente después de `instanceof`. Ahora el casting es parte del patrón:

```java
// Estilo antiguo (Java 15 y anterior)
Object objeto = "Hola";
if (objeto instanceof String) {
    String s = (String) objeto;  // cast manual
    System.out.println(s.toUpperCase());
}

// Estilo moderno con pattern matching — Java 16+
if (objeto instanceof String s) {
    // 's' ya está disponible como String, sin cast manual
    System.out.println(s.toUpperCase());
}

// Ejemplo más real: procesar distintos tipos de respuesta
public String describir(Object respuesta) {
    if (respuesta instanceof String s) {
        return "Texto: " + s.toUpperCase();
    } else if (respuesta instanceof Integer n) {
        return "Número: " + (n * 2);
    } else if (respuesta instanceof List<?> lista) {
        return "Lista con " + lista.size() + " elementos";
    } else {
        return "Tipo desconocido";
    }
}
```

---

## 3.5 Pattern Matching en `switch` (Java 21) ⭐

Java 21 lleva el pattern matching al switch, combinando el poder de ambos:

```java
// Procesar distintos tipos con switch + pattern matching
public String procesarRespuestaHttp(Object cuerpo) {
    return switch (cuerpo) {
        case String s   -> "Texto plano (%d caracteres)".formatted(s.length());
        case Integer n  -> "Código numérico: " + n;
        case List<?> l  -> "Lista de %d elementos".formatted(l.size());
        case null       -> "Cuerpo vacío";
        default         -> "Formato desconocido: " + cuerpo.getClass().getSimpleName();
    };
}

// Guarded patterns: condición adicional en el case
public String clasificarPrecio(Object valor) {
    return switch (valor) {
        case Integer n when n < 0       -> "Precio inválido";
        case Integer n when n == 0      -> "Gratis";
        case Integer n when n < 10_000  -> "Económico";
        case Integer n                  -> "Premium ($" + n + ")";
        case Double d when d < 0        -> "Precio inválido";
        case Double d                   -> "Precio: $%.2f".formatted(d);
        default                         -> "Tipo no soportado";
    };
}
```

---

## 3.6 Bucles

### `for` clásico

```java
for (int i = 0; i < 5; i++) {
    System.out.println("Iteración: " + i);
}

// Hacia atrás
for (int i = 10; i >= 0; i--) {
    System.out.println(i);
}

// Con paso diferente
for (int i = 0; i <= 100; i += 10) {
    System.out.println(i);  // 0, 10, 20, ... 100
}
```

### `for-each` (enhanced for) — el más usado en Java

```java
List<String> nombres = List.of("Ana", "Luis", "María");

// Forma más limpia cuando no necesitas el índice
for (String nombre : nombres) {
    System.out.println("Hola, " + nombre);
}

// Con arrays también funciona
int[] numeros = {1, 2, 3, 4, 5};
for (int n : numeros) {
    System.out.println(n);
}
```

### `while` y `do-while`

```java
// while: verifica ANTES de ejecutar (puede no ejecutarse nunca)
int intentos = 0;
while (intentos < 3) {
    System.out.println("Intento " + (intentos + 1));
    intentos++;
}

// do-while: verifica DESPUÉS de ejecutar (se ejecuta AL MENOS una vez)
String respuesta;
do {
    respuesta = leerEntradaUsuario();
} while (!respuesta.equals("salir"));
```

### `break` y `continue`

```java
// break: sale del bucle completo
for (int i = 0; i < 10; i++) {
    if (i == 5) break;       // para en 5, no imprime 5
    System.out.println(i);   // imprime 0, 1, 2, 3, 4
}

// continue: salta a la siguiente iteración
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue;  // salta los pares
    System.out.println(i);      // imprime 1, 3, 5, 7, 9
}

// Etiquetas (labels): para break/continue en bucles anidados
externo:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) break externo;  // sale del bucle externo
        System.out.println(i + "," + j);
    }
}
```

---

## 3.7 Operadores útiles

```java
// Aritméticos
int suma  = 10 + 3;   // 13
int resta = 10 - 3;   // 7
int mult  = 10 * 3;   // 30
int div   = 10 / 3;   // 3 (entero)
int mod   = 10 % 3;   // 1 (resto)

// Compuestos
int n = 5;
n += 3;  // n = 8
n -= 2;  // n = 6
n *= 4;  // n = 24
n /= 6;  // n = 4
n++;     // n = 5 (post-incremento)
++n;     // n = 6 (pre-incremento)

// Lógicos
boolean a = true, b = false;
System.out.println(a && b);  // false (AND)
System.out.println(a || b);  // true  (OR)
System.out.println(!a);      // false (NOT)

// Cortocircuito: en &&, si el primero es false, el segundo no se evalúa
// Útil para evitar NullPointerException
String s = null;
if (s != null && s.length() > 0) { // safe — s.length() solo se llama si s != null
    System.out.println("No vacío");
}
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 3.1 — Switch expression
Reescribe el siguiente switch clásico como switch expression moderna:

```java
int mes = 3;
String estacion;
switch (mes) {
    case 12: case 1: case 2:
        estacion = "Verano";
        break;
    case 3: case 4: case 5:
        estacion = "Otoño";
        break;
    case 6: case 7: case 8:
        estacion = "Invierno";
        break;
    case 9: case 10: case 11:
        estacion = "Primavera";
        break;
    default:
        estacion = "Mes inválido";
}
```

<details>
<summary>🔍 Ver solución</summary>

```java
int mes = 3;
String estacion = switch (mes) {
    case 12, 1, 2  -> "Verano";
    case 3, 4, 5   -> "Otoño";
    case 6, 7, 8   -> "Invierno";
    case 9, 10, 11 -> "Primavera";
    default        -> throw new IllegalArgumentException("Mes inválido: " + mes);
};
```
</details>

---

### Ejercicio 3.2 — FizzBuzz con control de flujo
Imprime los números del 1 al 30. Si el número es divisible por 3 imprime "Fizz", si es divisible por 5 imprime "Buzz", si es divisible por ambos imprime "FizzBuzz".

<details>
<summary>🔍 Ver solución</summary>

```java
for (int i = 1; i <= 30; i++) {
    String resultado = switch (0) {
        default -> {
            if      (i % 15 == 0) yield "FizzBuzz";
            else if (i % 3  == 0) yield "Fizz";
            else if (i % 5  == 0) yield "Buzz";
            else                  yield String.valueOf(i);
        }
    };
    System.out.println(resultado);
}

// Alternativa más simple con ternario:
for (int i = 1; i <= 30; i++) {
    if      (i % 15 == 0) System.out.println("FizzBuzz");
    else if (i % 3  == 0) System.out.println("Fizz");
    else if (i % 5  == 0) System.out.println("Buzz");
    else                  System.out.println(i);
}
```
</details>

---

### Ejercicio 3.3 — Pattern matching
Escribe un método `describir(Object obj)` que use pattern matching con switch (Java 21) para retornar:
- Si es `String`: `"Texto de N letras: [valor en mayúsculas]"`
- Si es `Integer` mayor o igual a 0: `"Entero positivo: N"`
- Si es `Integer` negativo: `"Entero negativo: N"`
- Si es `Double`: `"Decimal: N.NN"`
- Si es `null`: `"Nulo"`
- Cualquier otro: `"Desconocido"`

<details>
<summary>🔍 Ver solución</summary>

```java
public static String describir(Object obj) {
    return switch (obj) {
        case null                   -> "Nulo";
        case String s               -> "Texto de %d letras: %s".formatted(s.length(), s.toUpperCase());
        case Integer n when n >= 0  -> "Entero positivo: " + n;
        case Integer n              -> "Entero negativo: " + n;
        case Double d               -> "Decimal: %.2f".formatted(d);
        default                     -> "Desconocido";
    };
}
```
</details>

---

*[← Módulo 02 — Sintaxis y tipos](./02_sintaxis_y_tipos.md) | [Índice](./README.md) | [Módulo 04 — Métodos →](./04_metodos.md)*

