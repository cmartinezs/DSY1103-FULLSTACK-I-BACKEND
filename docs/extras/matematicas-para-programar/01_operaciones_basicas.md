# Módulo 01 — Operaciones básicas y operadores en Java

> **Objetivo:** conocer los operadores aritméticos de Java, entender cómo el tipo de dato afecta el resultado y aprender los atajos de asignación más usados.

---

## 1.1 Los cinco operadores aritméticos

Java hereda los operadores aritméticos estándar de las matemáticas y agrega uno muy útil para programación: el **módulo** (`%`).

| Operador | Nombre | Ejemplo | Resultado |
|----------|--------|---------|-----------|
| `+` | Suma | `10 + 3` | `13` |
| `-` | Resta | `10 - 3` | `7` |
| `*` | Multiplicación | `10 * 3` | `30` |
| `/` | División | `10 / 3` | `3` ⚠️ |
| `%` | Módulo (resto) | `10 % 3` | `1` |

> ⚠️ `10 / 3` en Java da `3`, **no** `3.333...` — la división entre dos `int` descarta los decimales. Esto se llama **división entera** y es una fuente muy común de bugs.

```java
int a = 10;
int b = 3;

System.out.println(a + b);  // 13
System.out.println(a - b);  // 7
System.out.println(a * b);  // 30
System.out.println(a / b);  // 3  ← división entera, NO 3.333
System.out.println(a % b);  // 1  ← el resto de 10 / 3
```

---

## 1.2 División entera vs. división real

Para obtener decimales en una división, **al menos uno de los operandos debe ser `double`** (o `float`).

```java
// ❌ Ambos son int → división entera
int x = 7;
int y = 2;
System.out.println(x / y);          // 3  (no 3.5)

// ✅ Opción 1: declarar como double
double a = 7.0;
double b = 2.0;
System.out.println(a / b);          // 3.5

// ✅ Opción 2: casting en la operación
System.out.println((double) x / y); // 3.5

// ✅ Opción 3: multiplicar por 1.0 (truco común)
System.out.println(x * 1.0 / y);    // 3.5
```

### ¿Cuándo usar cada tipo?

| Situación | Tipo recomendado |
|-----------|-----------------|
| Contar elementos, índices, IDs | `int` |
| Cantidades grandes (millones+) | `long` |
| Cálculos con decimales simples | `double` |
| Dinero / cálculos financieros | `BigDecimal` ← ver módulo 04 |

---

## 1.3 El operador módulo `%`

El operador `%` devuelve el **resto** de la división entera. Es uno de los operadores más útiles en programación.

```
10 % 3 = 1   porque  10 = 3 × 3 + 1
15 % 5 = 0   porque  15 = 5 × 3 + 0
7  % 2 = 1   porque   7 = 2 × 3 + 1
8  % 2 = 0   porque   8 = 2 × 4 + 0
```

### Casos de uso del módulo

```java
// ✅ Caso 1: determinar si un número es par o impar
int numero = 17;
if (numero % 2 == 0) {
    System.out.println("Par");
} else {
    System.out.println("Impar");  // → "Impar"
}

// ✅ Caso 2: ciclos con patrón cada N elementos
//    (ej: aplicar descuento cada 3 productos)
for (int i = 1; i <= 9; i++) {
    if (i % 3 == 0) {
        System.out.println("Item " + i + " → DESCUENTO");
    }
}
// Salida: Item 3 → DESCUENTO | Item 6 → DESCUENTO | Item 9 → DESCUENTO

// ✅ Caso 3: limitar un contador circular (ej: turno de mesa en restaurant)
int mesasDisponibles = 4;
int clienteNumero = 9;
int mesaAsignada = (clienteNumero % mesasDisponibles) + 1;
System.out.println("Mesa asignada: " + mesaAsignada);  // Mesa 2

// ✅ Caso 4: saber si es múltiplo de algo
int minutosTranscurridos = 45;
if (minutosTranscurridos % 15 == 0) {
    System.out.println("Enviar recordatorio");  // cada 15 min
}
```

---

## 1.4 Orden de precedencia (jerarquía de operaciones)

Java respeta las mismas reglas que las matemáticas: multiplicación y división antes que suma y resta.

| Prioridad | Operadores | Evaluación |
|-----------|------------|------------|
| 1 (mayor) | `*` `/` `%` | de izquierda a derecha |
| 2 | `+` `-` | de izquierda a derecha |

```java
int resultado = 2 + 3 * 4;     // 14, NO 20 — primero 3*4=12, luego 2+12
int conParentesis = (2 + 3) * 4; // 20 — el paréntesis fuerza primero la suma
```

> 💡 Ante la duda, **usa paréntesis**. Son gratis y evitan bugs difíciles de detectar.

```java
// Ejemplo real: calcular el precio con IVA
double precio = 100.0;
double iva = 19.0;

// ❌ Error sutil — el % aquí es el operador módulo, no el símbolo de porcentaje
// double total = precio + precio * iva % 100;  // NO hace lo que parece

// ✅ Correcto
double total = precio + (precio * iva / 100);  // 119.0
```

---

## 1.5 Operadores de asignación compuesta

Son atajos que **combinan una operación aritmética con la asignación**. Hacen el código más conciso y son extremadamente comunes en bucles y acumuladores.

| Atajo | Equivalente | Descripción |
|-------|-------------|-------------|
| `x += n` | `x = x + n` | Suma n a x |
| `x -= n` | `x = x - n` | Resta n a x |
| `x *= n` | `x = x * n` | Multiplica x por n |
| `x /= n` | `x = x / n` | Divide x entre n |
| `x %= n` | `x = x % n` | Asigna el resto |

```java
int total = 100;

total += 50;   // total = 150
total -= 20;   // total = 130
total *= 2;    // total = 260
total /= 4;    // total = 65
total %= 10;   // total = 5
```

---

## 1.6 Incremento y decremento

Los operadores `++` y `--` son atajos para sumar o restar exactamente 1. Son los más usados en contadores.

| Operador | Significado |
|----------|-------------|
| `x++` | Usa x, luego suma 1 (post-incremento) |
| `++x` | Suma 1, luego usa x (pre-incremento) |
| `x--` | Usa x, luego resta 1 (post-decremento) |
| `--x` | Resta 1, luego usa x (pre-decremento) |

```java
int count = 5;
System.out.println(count++);  // imprime 5, luego count pasa a 6
System.out.println(count);    // imprime 6

System.out.println(++count);  // count pasa a 7, luego imprime 7
System.out.println(count);    // imprime 7
```

> 💡 En la práctica, **`count++` y `++count` solos en una línea son equivalentes**. La diferencia importa solo cuando se usan dentro de expresiones más complejas. Para contadores simples, elige el que prefieras y sé consistente.

---

## 1.7 Operaciones con `String` y números

Java permite concatenar (`+`) un número con un String, pero el resultado **es siempre un String**, no una suma.

```java
// ⚠️ Concatenación, NO suma
System.out.println("Resultado: " + 3 + 4);   // "Resultado: 34"

// ✅ Forzar la suma primero con paréntesis
System.out.println("Resultado: " + (3 + 4)); // "Resultado: 7"

// ✅ Formatear con String.format (más limpio)
double precio = 9990.5;
System.out.println(String.format("Precio: $%.2f", precio)); // "Precio: $9990.50"
```

---

## Resumen

| Concepto | Clave para recordar |
|----------|-------------------|
| División entera | `int / int` descarta decimales — usa `double` o cast |
| Módulo `%` | Devuelve el **resto**; detecta pares, múltiplos y ciclos |
| Precedencia | `*` `/` `%` antes que `+` `-`; usa paréntesis ante la duda |
| `+=`, `-=`, `*=` | Atajos de asignación — esenciales en acumuladores |
| `++` / `--` | Incremento/decremento en 1 — el corazón de los contadores |
| `+` con String | Concatena, no suma — usa paréntesis o `String.format` |

→ [Siguiente módulo: Contador y acumulador](./02_contador_y_acumulador.md)

