# Módulo 04 — Redondeo y precisión decimal

> **Objetivo:** entender por qué los decimales en programación no son exactos, cómo redondear correctamente y cuándo usar `BigDecimal` en lugar de `double`.

---

## 4.1 El problema con los decimales en computación

Las computadoras almacenan los números decimales en binario (base 2). Algunos decimales como `0.1` o `0.3` **no pueden representarse de forma exacta** en binario, del mismo modo que `1/3` no puede escribirse de forma exacta en decimal (0.333…).

```java
System.out.println(0.1 + 0.2);          // 0.30000000000000004  ← NO es 0.3
System.out.println(1.0 - 0.9);          // 0.09999999999999998  ← NO es 0.1
System.out.println(0.1 * 3);            // 0.30000000000000004
System.out.println(10.0 / 3.0 * 3.0);  // 10.000000000000002   ← NO es 10
```

> 📌 Esto no es un bug de Java — es el estándar IEEE 754 que usan prácticamente todos los lenguajes (JavaScript, Python, C, etc.).

### ¿Cuándo importa?

| Contexto | ¿Importa la precisión exacta? | Solución |
|----------|------------------------------|----------|
| Cálculos de física o estadística | A veces | `double` suele ser suficiente |
| Mostrar precios en pantalla | Sí (2 decimales) | Redondear con `String.format` |
| Operaciones financieras / dinero | **Siempre** | `BigDecimal` |
| Comparar decimales con `==` | **Nunca uses `==`** | Usar diferencia o `BigDecimal` |

---

## 4.2 La clase `Math`

Java incluye la clase `Math` con métodos estáticos para operaciones matemáticas comunes. No necesitas importarla.

### Métodos de redondeo

| Método | Descripción | Ejemplo | Resultado |
|--------|-------------|---------|-----------|
| `Math.round(x)` | Redondea al entero más cercano (≥0.5 sube) | `Math.round(4.5)` | `5` |
| `Math.round(x)` | | `Math.round(4.4)` | `4` |
| `Math.floor(x)` | Redondea **hacia abajo** (piso) | `Math.floor(4.9)` | `4.0` |
| `Math.ceil(x)` | Redondea **hacia arriba** (techo) | `Math.ceil(4.1)` | `5.0` |
| `Math.abs(x)` | Valor absoluto (quita el signo negativo) | `Math.abs(-7)` | `7` |

```java
double valor = 4.567;

System.out.println(Math.round(valor));  // 5   (long)
System.out.println(Math.floor(valor));  // 4.0 (double)
System.out.println(Math.ceil(valor));   // 5.0 (double)
```

### Redondear a N decimales con `Math.round`

```java
double precio = 9990.567;

// Redondear a 2 decimales
double redondeado = Math.round(precio * 100.0) / 100.0;
System.out.println(redondeado);  // 9990.57

// Fórmula general: Math.round(x * 10^n) / 10^n
// Para 3 decimales: Math.round(precio * 1000.0) / 1000.0
```

### Otros métodos útiles de `Math`

```java
// Potencia: x elevado a la n
Math.pow(2, 10);      // 1024.0

// Raíz cuadrada
Math.sqrt(16);        // 4.0
Math.sqrt(2);         // 1.4142135623730951

// Máximo y mínimo entre dos valores
Math.max(15, 8);      // 15
Math.min(15, 8);      // 8

// Valor absoluto
Math.abs(-42);        // 42
Math.abs(-3.14);      // 3.14

// Número aleatorio entre 0.0 (inclusive) y 1.0 (exclusivo)
double aleatorio = Math.random();  // ej: 0.7432...

// Número entero aleatorio entre min y max (inclusive)
int min = 1, max = 6;
int dado = (int)(Math.random() * (max - min + 1)) + min;  // 1 a 6
```

---

## 4.3 Formatear decimales con `String.format`

Para **mostrar** un número con cierta cantidad de decimales (sin necesariamente redondear el valor en memoria):

```java
double precio = 9990.5;

// %.2f → decimal con 2 cifras después del punto
String formateado = String.format("%.2f", precio);
System.out.println(formateado);  // "9990.50"

// %,.2f → con separador de miles (la coma)
System.out.printf("Precio: $%,.2f%n", precio);   // "Precio: $9,990.50"

// Formatear porcentaje
double porcentaje = 0.1567;
System.out.printf("Tasa: %.1f%%%n", porcentaje * 100);  // "Tasa: 15.7%"
```

> 💡 `String.format` no modifica la variable — solo formatea la representación textual para mostrarla. La variable `precio` sigue valiendo `9990.5`.

---

## 4.4 `BigDecimal` — precisión exacta para dinero

Cuando manejas **dinero** en una API backend, los errores de punto flotante pueden generar diferencias de centavos que en auditorías o cierres contables son inaceptables. La solución es `BigDecimal`.

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
```

### Crear un `BigDecimal`

```java
// ✅ Forma correcta: desde String (garantiza representación exacta)
BigDecimal precio = new BigDecimal("9990.50");

// ⚠️ Forma que puede heredar el error de double:
BigDecimal dudoso  = new BigDecimal(0.1);  // 0.1000000000000000055511151231257827021181583404541015625
BigDecimal correcto = new BigDecimal("0.1");  // exactamente 0.1
// O desde double de forma segura:
BigDecimal seguro   = BigDecimal.valueOf(0.1);  // usa la representación string internamente
```

### Operaciones con `BigDecimal`

`BigDecimal` es **inmutable** — cada operación retorna un nuevo objeto.

```java
BigDecimal precio    = new BigDecimal("50000.00");
BigDecimal descuento = new BigDecimal("0.10");  // 10%
BigDecimal tasa_iva  = new BigDecimal("0.19");  // 19%

// Descuento
BigDecimal montoDescuento  = precio.multiply(descuento);
// 50000 × 0.10 = 5000.00

BigDecimal precioDescontado = precio.subtract(montoDescuento);
// 50000 - 5000 = 45000.00

// IVA sobre precio descontado
BigDecimal montoIVA    = precioDescontado.multiply(tasa_iva);
// 45000 × 0.19 = 8550.00

BigDecimal precioFinal = precioDescontado.add(montoIVA);
// 45000 + 8550 = 53550.00

System.out.println("Precio final: $" + precioFinal);  // $53550.00
```

| Operación matemática | Método `BigDecimal` |
|---------------------|---------------------|
| `a + b` | `a.add(b)` |
| `a - b` | `a.subtract(b)` |
| `a * b` | `a.multiply(b)` |
| `a / b` | `a.divide(b, 2, RoundingMode.HALF_UP)` |
| Redondear | `a.setScale(2, RoundingMode.HALF_UP)` |
| Comparar | `a.compareTo(b)` (no usar `.equals` para igualdad numérica) |

### Modos de redondeo más usados

| `RoundingMode` | Comportamiento | Ejemplo (2 dec.) |
|----------------|---------------|-----------------|
| `HALF_UP` | El estándar matemático (≥0.5 sube) | `2.345` → `2.35` |
| `HALF_DOWN` | ≥0.5 baja | `2.345` → `2.34` |
| `FLOOR` | Siempre hacia abajo | `2.349` → `2.34` |
| `CEILING` | Siempre hacia arriba | `2.341` → `2.35` |

```java
BigDecimal resultado = new BigDecimal("10").divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
System.out.println(resultado);  // 3.33
```

---

## 4.5 Comparar decimales correctamente

```java
// ❌ NUNCA comparar doubles con ==
double a = 0.1 + 0.2;
double b = 0.3;
System.out.println(a == b);  // false ← bug

// ✅ Opción 1: comparar con una tolerancia (epsilon)
double epsilon = 1e-9;  // 0.000000001
System.out.println(Math.abs(a - b) < epsilon);  // true

// ✅ Opción 2: usar BigDecimal con compareTo
BigDecimal x = new BigDecimal("0.1").add(new BigDecimal("0.2"));
BigDecimal y = new BigDecimal("0.3");
System.out.println(x.compareTo(y) == 0);  // true
```

---

## 4.6 Cuándo usar `double` vs `BigDecimal`

| Criterio | `double` | `BigDecimal` |
|----------|----------|-------------|
| Velocidad | ⚡ Muy rápido | 🐢 Más lento |
| Precisión exacta | ❌ No garantizada | ✅ Garantizada |
| Ideal para... | Estadísticas, física, UI | Dinero, contabilidad, auditorías |
| API REST (precios) | Puede usarse con `String.format` | Recomendado para lógica de negocio |
| Base de datos | `FLOAT` / `DOUBLE` | `DECIMAL(10,2)` con `BigDecimal` en JPA |

---

## Resumen

- `double` tiene errores de precisión inherentes al estándar binario — nunca compares con `==`.
- `Math.round`, `Math.floor`, `Math.ceil` para redondear valores `double`.
- `String.format("%.2f", valor)` para mostrar 2 decimales en pantalla.
- `BigDecimal` para operaciones financieras — siempre crear desde `String` o `BigDecimal.valueOf()`.
- En JPA, mapear campos de dinero como `DECIMAL(10,2)` en BD y `BigDecimal` en la entidad Java.

→ [Siguiente módulo: Casos de uso reales integrados](./05_casos_de_uso_reales.md)

