# Módulo 03 — Porcentajes, descuentos y cargos

> **Objetivo:** traducir operaciones de porcentaje al código Java que aparece en sistemas reales: descuentos, recargos, impuestos y precio final combinado.

---

## 3.1 ¿Qué es un porcentaje?

Un **porcentaje** es una forma de expresar una fracción de 100. Decir "el 15%" es lo mismo que decir "15 de cada 100" o, expresado matemáticamente:

```
porcentaje% = porcentaje / 100
```

En código, los porcentajes se manejan de dos formas:

| Forma | Ejemplo 15% | Cuándo usar |
|-------|------------|-------------|
| Como entero | `15` | Cuando el usuario o la BD lo guarda como `int` |
| Como decimal | `0.15` | Cuando ya está listo para multiplicar |

---

## 3.2 Calcular el porcentaje de un valor

**Fórmula:**
```
porción = total × (tasa / 100)
```

```java
double precio    = 50_000.0;
double tasa      = 20.0;  // 20%

double porcion   = precio * (tasa / 100);   // 10_000.0
```

> ⚠️ Si `tasa` fuera `int` y `precio` también, escribir `tasa / 100` daría `0` (división entera). Usa siempre `double` o añade el cast: `(double) tasa / 100`.

```java
// ❌ Bug clásico con enteros
int tasa  = 20;
int total = 50_000;
int error = total * (tasa / 100);   // tasa/100 = 0 → resultado 0

// ✅ Correcto con cast
int correcto = (int)(total * ((double) tasa / 100));  // 10_000
```

---

## 3.3 Descuento

Un **descuento** reduce el precio original en una proporción dada.

### Fórmulas

```
monto descuento  = precio × (descuento / 100)
precio final     = precio - monto descuento

  o directamente:

precio final     = precio × (1 - descuento / 100)
```

### Implementación paso a paso

```java
double precioOriginal = 80_000.0;
double porcentajeDescuento = 25.0;  // 25%

// Opción 1: en dos pasos (más legible, mejor para mostrar el desglose)
double montoDescuento = precioOriginal * (porcentajeDescuento / 100);  // 20_000
double precioFinal    = precioOriginal - montoDescuento;                // 60_000

System.out.printf("Precio original:  $%,.2f%n", precioOriginal);   // $80,000.00
System.out.printf("Descuento (25%%): -$%,.2f%n", montoDescuento);  // -$20,000.00
System.out.printf("Precio final:     $%,.2f%n", precioFinal);      // $60,000.00
```

```java
// Opción 2: en un paso (más compacto)
double precioFinal = precioOriginal * (1 - porcentajeDescuento / 100);  // 60_000
```

### Descuentos escalonados (según monto de compra)

```java
double calcularDescuento(double totalCompra) {
    double porcentaje;

    if (totalCompra >= 100_000) {
        porcentaje = 20.0;  // 20% para compras sobre $100.000
    } else if (totalCompra >= 50_000) {
        porcentaje = 10.0;  // 10% para compras entre $50.000 y $99.999
    } else if (totalCompra >= 20_000) {
        porcentaje = 5.0;   // 5% para compras entre $20.000 y $49.999
    } else {
        porcentaje = 0.0;   // sin descuento
    }

    return totalCompra * (porcentaje / 100);
}

// Uso
double total = 75_000.0;
double descuento = calcularDescuento(total);
double aPagar    = total - descuento;

System.out.printf("Total:     $%,.2f%n", total);      // $75,000.00
System.out.printf("Descuento: $%,.2f%n", descuento);  // $7,500.00
System.out.printf("A pagar:   $%,.2f%n", aPagar);     // $67,500.00
```

---

## 3.4 Cargo (recargo / interés)

Un **cargo** aumenta el precio original: impuestos (IVA), intereses, gastos de envío porcentuales, recargos por pago en cuotas, etc.

### Fórmulas

```
monto cargo   = precio × (tasa / 100)
precio final  = precio + monto cargo

  o directamente:

precio final  = precio × (1 + tasa / 100)
```

### Implementación

```java
double precioNeto = 100_000.0;
double tasaIVA    = 19.0;  // IVA 19%

double montoIVA   = precioNeto * (tasaIVA / 100);  // 19_000
double precioFinal = precioNeto + montoIVA;         // 119_000

System.out.printf("Precio neto:  $%,.2f%n", precioNeto);    // $100,000.00
System.out.printf("IVA (19%%):  +$%,.2f%n", montoIVA);      // +$19,000.00
System.out.printf("Total:        $%,.2f%n", precioFinal);    // $119,000.00
```

### Recargo por pago en cuotas

```java
double precioContado = 200_000.0;
int numeroCuotas     = 12;
double recargoAnual  = 24.0;  // 24% anual

double montoRecargo  = precioContado * (recargoAnual / 100);
double totalConRecargo = precioContado + montoRecargo;
double valorCuota    = totalConRecargo / numeroCuotas;

System.out.printf("Precio contado: $%,.2f%n", precioContado);    // $200,000.00
System.out.printf("Recargo 24%%:  +$%,.2f%n", montoRecargo);     // +$48,000.00
System.out.printf("Total cuotas:   $%,.2f%n", totalConRecargo);  // $248,000.00
System.out.printf("Valor cuota:    $%,.2f%n", valorCuota);       // $20,666.67
```

---

## 3.5 Precio final: descuento + cargo combinados

En muchos sistemas el precio final aplica **primero el descuento y luego el impuesto** sobre el precio descontado.

```
precio con descuento = precio × (1 - descuento / 100)
precio final         = precio con descuento × (1 + impuesto / 100)
```

> ⚠️ El orden importa: aplicar el impuesto sobre el precio original (sin descontar) primero es un error común que favorece al vendedor en perjuicio del comprador.

```java
double precioOriginal     = 90_000.0;
double porcentajeDescuento = 10.0;  // 10% de descuento
double tasaIVA            = 19.0;  // 19% de IVA

// Paso 1: aplicar descuento
double precioConDescuento = precioOriginal * (1 - porcentajeDescuento / 100);
// 90_000 × 0.90 = 81_000

// Paso 2: aplicar impuesto sobre precio ya descontado
double precioFinal = precioConDescuento * (1 + tasaIVA / 100);
// 81_000 × 1.19 = 96_390

System.out.printf("Precio original:      $%,.2f%n", precioOriginal);      // $90,000.00
System.out.printf("Precio con descuento: $%,.2f%n", precioConDescuento);  // $81,000.00
System.out.printf("Precio final c/IVA:   $%,.2f%n", precioFinal);         // $96,390.00
```

---

## 3.6 Calcular el porcentaje que representa una parte

A veces necesitas saber **qué porcentaje representa un valor respecto a un total**:

```
porcentaje = (parte / total) × 100
```

```java
// ¿Qué % de los tickets está resuelto?
int totalTickets    = 120;
int ticketsResueltos = 84;

double porcentajeResuelto = ((double) ticketsResueltos / totalTickets) * 100;
System.out.printf("Resueltos: %.1f%%%n", porcentajeResuelto);  // 70.0%
```

```java
// ¿Cuánto representa una venta del total mensual?
double ventaMes      = 4_500_000.0;
double ventaVendedor = 1_125_000.0;

double participacion = (ventaVendedor / ventaMes) * 100;
System.out.printf("Participación: %.1f%%%n", participacion);  // 25.0%
```

---

## 3.7 Extraer el valor neto desde un precio con impuesto incluido

Cuando el precio ya **incluye** el impuesto y necesitas saber cuánto es el precio neto:

```
precio neto = precio con impuesto / (1 + tasa / 100)
impuesto    = precio con impuesto - precio neto
```

```java
double precioConIVA = 119_000.0;
double tasaIVA      = 19.0;

double precioNeto   = precioConIVA / (1 + tasaIVA / 100);  // 100_000
double montoIVA     = precioConIVA - precioNeto;            //  19_000

System.out.printf("Precio neto: $%,.2f%n", precioNeto);   // $100,000.00
System.out.printf("IVA:         $%,.2f%n", montoIVA);     //  $19,000.00
```

---

## 3.8 Tabla de fórmulas de referencia rápida

| Cálculo | Fórmula | Java |
|---------|---------|------|
| Porcentaje de un valor | `total × tasa / 100` | `total * (tasa / 100.0)` |
| Precio con descuento | `precio × (1 - desc/100)` | `precio * (1 - desc / 100.0)` |
| Monto de descuento | `precio × desc / 100` | `precio * desc / 100.0` |
| Precio con cargo / IVA | `precio × (1 + tasa/100)` | `precio * (1 + tasa / 100.0)` |
| Descuento + IVA | `precio × (1 - desc/100) × (1 + iva/100)` | encadenar ambas |
| Qué % es una parte | `(parte / total) × 100` | `(double) parte / total * 100` |
| Extraer neto de precio c/IVA | `precioConIVA / (1 + tasa/100)` | `precioConIVA / (1 + tasa / 100.0)` |

---

## Resumen

- Porcentaje = `total * tasa / 100` — siempre usar `double` para evitar división entera.
- Descuento = restar; cargo = sumar; el operador `1 ±` acorta el código.
- **Primero descuento, luego impuesto** — el orden afecta el resultado.
- Para desglosar en una respuesta API, calcula cada parte por separado y devuélvelas como campos independientes.

→ [Siguiente módulo: Redondeo y precisión decimal](./04_redondeo_y_precision.md)

