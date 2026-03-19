# Ejercicio 08 — Sistema de reservas de restaurante

> **Nivel:** ⭐⭐⭐ Medio  
> **Conceptos:** Interfaces · Polimorfismo · `implements` · Múltiples implementaciones · Lógica proposicional en decisiones  
> **Tiempo estimado:** 55–75 min

---

## 🍽️ Contexto

El restaurante **GourmetExpress** ofrece tres modalidades de servicio: comer en local, pedido para llevar y delivery a domicilio. Cada modalidad tiene sus propias reglas de tiempo, costo y disponibilidad. Tu tarea es modelar este sistema usando **interfaces y polimorfismo**.

---

## 📋 Enunciado

### Interface `Pedido`

```java
public interface Pedido {
    String getId();
    String getCliente();
    List<String> getItems();
    double calcularSubtotal();
    double calcularTotal();      // incluye todos los costos adicionales
    int estimarTiempoMinutos();
    String obtenerResumen();
    boolean puedeConfirmarse();  // regla de negocio propia de cada modalidad
}
```

### Interface `ConDescuento`

```java
public interface ConDescuento {
    double aplicarDescuento(double porcentaje);
    String obtenerDescuentoInfo();
}
```

---

### Clase `PedidoEnLocal` (implements `Pedido`)

**Atributos adicionales:**
- `numeroMesa` (`int`): número de mesa (1–20).
- `cantidadPersonas` (`int`): personas en la mesa.

**Reglas:**
- `calcularTotal()` → subtotal (sin cargo adicional).
- `estimarTiempoMinutos()` → 15 + (5 × cantidad de items).
- `puedeConfirmarse()`:
  - **p:** `numeroMesa >= 1 && numeroMesa <= 20`
  - **q:** `cantidadPersonas >= 1 && cantidadPersonas <= 8`
  - Condición: `p ∧ q`

---

### Clase `PedidoParaLlevar` (implements `Pedido`, `ConDescuento`)

**Atributos adicionales:**
- `nombreCliente` (`String`)
- `horaRetiro` (`int`): hora de retiro (0–23).

**Reglas:**
- `calcularTotal()` → subtotal − descuento aplicado.
- `estimarTiempoMinutos()` → 20 + (3 × cantidad de items).
- Descuento disponible: 10% sobre el subtotal.
- `puedeConfirmarse()`:
  - **p:** `horaRetiro >= 11 && horaRetiro <= 22` (horario de atención)
  - **q:** `!items.isEmpty()`
  - Condición: `p ∧ q`

---

### Clase `PedidoDelivery` (implements `Pedido`, `ConDescuento`)

**Atributos adicionales:**
- `direccion` (`String`)
- `distanciaKm` (`double`): distancia al domicilio.
- `esHoraPunta` (`boolean`): si es hora punta (mayor demanda).

**Reglas:**
- Costo de delivery: `$1.500 + (distanciaKm × $500)`.
- Si `esHoraPunta`, el costo de delivery se duplica.
- `calcularTotal()` → subtotal + costoDelivery − descuento.
- `estimarTiempoMinutos()` → 30 + (distanciaKm × 5) + (esHoraPunta ? 15 : 0).
- `puedeConfirmarse()`:
  - **p:** `distanciaKm <= 10.0` (radio máximo de delivery)
  - **q:** `direccion != null && !direccion.isBlank()`
  - **r:** `calcularSubtotal() >= 5000` (monto mínimo)
  - Condición: `p ∧ q ∧ r`

---

### Clase `GestorPedidos`

Crea una `List<Pedido>` con pedidos de distintos tipos y los procesa de forma uniforme (polimorfismo).

---

## 🚫 Restricciones

- Las clases de pedido deben implementar las interfaces, no extender clases base.
- El método `puedeConfirmarse()` debe declarar las proposiciones explícitamente como variables `boolean`.
- El método `obtenerResumen()` debe retornar un `String` multi-línea (usa `\n`).
- En el `main`, usa `List<Pedido>` y recorre con `for-each` (polimorfismo).
- Para aplicar `ConDescuento`, usa `instanceof` antes del cast.

---

## 📥 Ejemplos de entrada

```java
List<String> items1 = List.of("Lomo saltado", "Pisco sour", "Ensalada mixta");
PedidoEnLocal p1 = new PedidoEnLocal("P001", "Mesa 5", items1, 5, 3);

List<String> items2 = List.of("Pizza familiar", "Bebida 1.5L");
PedidoParaLlevar p2 = new PedidoParaLlevar("P002", "Roberto Silva", items2, 13);
p2.aplicarDescuento(10);

List<String> items3 = List.of("Hamburguesa doble", "Papas fritas", "Malteada");
PedidoDelivery p3 = new PedidoDelivery("P003", "Laura Pérez", items3, "Av. Providencia 1234", 3.5, true);
p3.aplicarDescuento(5);

// Precios por item (definidos en el constructor o en una constante):
// Lomo saltado: $9.500, Pisco sour: $4.800, Ensalada mixta: $3.200
// Pizza familiar: $18.900, Bebida 1.5L: $1.990
// Hamburguesa doble: $7.500, Papas fritas: $2.500, Malteada: $3.800

List<Pedido> pedidos = List.of(p1, p2, p3);
for (Pedido pedido : pedidos) {
    System.out.println(pedido.obtenerResumen());
    System.out.println("¿Puede confirmarse? " + (pedido.puedeConfirmarse() ? "✅ Sí" : "❌ No"));
    System.out.println("─".repeat(45));
}
```

---

## 📤 Salidas esperadas

```
🍽️  PEDIDO EN LOCAL — P001
Mesa: 5 | Personas: 3
Items:
  · Lomo saltado     $9.500
  · Pisco sour       $4.800
  · Ensalada mixta   $3.200
Subtotal:     $17.500
Total:        $17.500
Tiempo est.:  30 min
¿Puede confirmarse? ✅ Sí
─────────────────────────────────────────────

🥡 PARA LLEVAR — P002
Cliente: Roberto Silva | Retiro: 13:00
Items:
  · Pizza familiar   $18.900
  · Bebida 1.5L      $1.990
Subtotal:     $20.890
Descuento:   -$2.089 (10%)
Total:        $18.801
Tiempo est.:  26 min
¿Puede confirmarse? ✅ Sí
─────────────────────────────────────────────

🚴 DELIVERY — P003
Cliente: Laura Pérez
Dirección: Av. Providencia 1234
Distancia: 3.5 km | Hora punta: Sí
Items:
  · Hamburguesa doble $7.500
  · Papas fritas      $2.500
  · Malteada          $3.800
Subtotal:     $13.800
Delivery:    +$5.500 (tarifa × 2 hora punta)
Descuento:   -$690 (5%)
Total:        $18.610
Tiempo est.:  62 min
¿Puede confirmarse? ✅ Sí
─────────────────────────────────────────────
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Interfaz con precios de items</summary>

Para simplificar, puedes definir un `Map<String, Double>` estático con los precios y que `calcularSubtotal()` itere los items y sume precios.
</details>

<details>
<summary>Pista 2 — instanceof para ConDescuento</summary>

```java
for (Pedido pedido : pedidos) {
    if (pedido instanceof ConDescuento cd) {  // pattern matching Java 16+
        System.out.println(cd.obtenerDescuentoInfo());
    }
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Por qué se usa una interfaz en lugar de una clase abstracta para modelar `Pedido`?
2. ¿Qué ventaja ofrece el polimorfismo al iterar `List<Pedido>` con distintos tipos dentro?
3. ¿Cómo cambiaría el diseño si en el futuro se agrega una modalidad `PedidoMesa Virtual` para eventos? ¿Qué deberías implementar?

---

*[← Ejercicio anterior](./07_tienda_mascotas.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./09_biblioteca_digital.md)*

