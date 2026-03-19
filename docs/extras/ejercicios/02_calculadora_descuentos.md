# Ejercicio 02 — Calculadora de descuentos en tienda

> **Nivel:** ⭐ Básico  
> **Conceptos:** Variables · Tipos de datos · Operadores aritméticos · `if/else if/else` · Lógica proposicional (conjunción y disyunción)  
> **Tiempo estimado:** 25–35 min

---

## 🏪 Contexto

Trabajas para una cadena de retail llamada **TechStore**. El área de marketing lanzó una campaña de descuentos por temporada de rebajas. Tu tarea es implementar la **calculadora de precio final** que aplicará los descuentos según las reglas del negocio.

---

## 📋 Enunciado

Crea una clase `CalculadoraDescuentos` con un método `calcularPrecioFinal` que reciba:

- `precioOriginal` (`double`): precio base del producto.
- `esClienteVip` (`boolean`): indica si el cliente pertenece al programa de fidelidad.
- `esDiaDeRebajas` (`boolean`): indica si hoy es día de rebajas (aplica descuento adicional).
- `cantidadProductos` (`int`): cantidad de unidades compradas.

**Reglas de descuento (aplicar en orden):**

| Condición | Descuento |
|-----------|-----------|
| Cliente VIP **Y** día de rebajas | 30% |
| Solo cliente VIP | 20% |
| Solo día de rebajas | 15% |
| Ninguna de las anteriores | 0% |

**Descuento adicional por volumen (se suma al anterior):**

| Cantidad | Descuento adicional |
|----------|---------------------|
| 10 o más unidades | +10% |
| 5–9 unidades | +5% |
| Menos de 5 | 0% |

> ⚠️ El descuento total **no puede superar el 40%** del precio original.

El programa debe imprimir:
- Precio original
- Descuento aplicado (en %)
- Precio final por unidad
- Total a pagar (precio final × cantidad)

---

## 🚫 Restricciones

- Usa `double` para los precios y `int` para la cantidad.
- No uses librerías externas; solo Java puro.
- Aplica la lógica proposicional explícitamente: declara las proposiciones como variables `boolean`.
- El descuento máximo es 40%; si la suma supera ese valor, se aplica exactamente 40%.
- Redondea el precio final a 2 decimales usando `Math.round`.
- La clase debe tener el método `calcularPrecioFinal` que **retorne** el precio final (`double`), y un método `imprimirResumen` que muestre el desglose.

---

## 📥 Ejemplos de entrada

### Caso 1 — Cliente VIP en día de rebajas, compra 12 unidades
```
precioOriginal    = 50000.0
esClienteVip      = true
esDiaDeRebajas    = true
cantidadProductos = 12
```

### Caso 2 — Cliente normal en día de rebajas, compra 3 unidades
```
precioOriginal    = 25000.0
esClienteVip      = false
esDiaDeRebajas    = true
cantidadProductos = 3
```

### Caso 3 — Cliente VIP, día normal, compra 7 unidades
```
precioOriginal    = 80000.0
esClienteVip      = true
esDiaDeRebajas    = false
cantidadProductos = 7
```

### Caso 4 — Cliente normal, día normal, compra 1 unidad
```
precioOriginal    = 15000.0
esClienteVip      = false
esDiaDeRebajas    = false
cantidadProductos = 1
```

---

## 📤 Salidas esperadas

### Caso 1
```
=== Resumen de compra ===
Precio original:   $50.000
Descuento base:    30% (VIP + Día de rebajas)
Descuento volumen: 10% (12 unidades)
Descuento total:   40% (máximo aplicado)
Precio por unidad: $30.000
Cantidad:          12
Total a pagar:     $360.000
```

### Caso 2
```
=== Resumen de compra ===
Precio original:   $25.000
Descuento base:    15% (Día de rebajas)
Descuento volumen: 0%
Descuento total:   15%
Precio por unidad: $21.250
Cantidad:          3
Total a pagar:     $63.750
```

### Caso 3
```
=== Resumen de compra ===
Precio original:   $80.000
Descuento base:    20% (VIP)
Descuento volumen: 5% (7 unidades)
Descuento total:   25%
Precio por unidad: $60.000
Cantidad:          7
Total a pagar:     $420.000
```

### Caso 4
```
=== Resumen de compra ===
Precio original:   $15.000
Descuento base:    0%
Descuento volumen: 0%
Descuento total:   0%
Precio por unidad: $15.000
Cantidad:          1
Total a pagar:     $15.000
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Variables proposicionales</summary>

```java
boolean esVip = esClienteVip;
boolean esRebajas = esDiaDeRebajas;
boolean esVipYRebajas = esVip && esRebajas;
boolean tieneDescuentoBase = esVip || esRebajas;
```
</details>

<details>
<summary>Pista 2 — Cálculo del descuento</summary>

Calcula el descuento como un valor entre 0.0 y 1.0 (ej: 30% = 0.30), luego aplica:
`precioFinal = precioOriginal * (1 - descuentoTotal)`
</details>

---

## 🧠 Reflexión final

1. ¿Por qué usamos `boolean` para representar las condiciones en lugar de `String`?
2. ¿Qué conectivo lógico expresa la condición «cliente VIP **o** día de rebajas»?
3. ¿Cómo cambiaría el código si hubiera un tercer tipo de descuento: «primera compra del mes»?

---

*[← Ejercicio anterior](./01_validador_acceso.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./03_clasificador_temperatura.md)*

