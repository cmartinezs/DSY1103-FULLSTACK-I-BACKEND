# Ejercicio 10 — Farmacia online

> **Nivel:** ⭐⭐⭐ Medio  
> **Conceptos:** Excepciones personalizadas · `try/catch/finally` · Excepciones checked vs unchecked · Lógica proposicional en validaciones · Jerarquía de excepciones  
> **Tiempo estimado:** 60–80 min

---

## 💊 Contexto

La cadena **FarmaTech** lanzó su plataforma de ventas online. El sistema debe manejar múltiples tipos de errores que pueden ocurrir durante una venta: productos sin stock, medicamentos sin receta, montos inválidos y errores de pago. Tu tarea es implementar la **jerarquía de excepciones** y el **flujo de compra** robusto.

---

## 📋 Enunciado

### Jerarquía de excepciones personalizadas

```
Exception
└── FarmaTechException  (checked)
    ├── ProductoNoEncontradoException
    ├── StockInsuficienteException
    └── RecetaRequeridaException

RuntimeException
└── FarmaTechRuntimeException  (unchecked)
    ├── PagoInvalidoException
    └── CarritoVacioException
```

**`FarmaTechException`** (checked): constructor que recibe `String mensaje` y otro con `String mensaje, Throwable causa`.

**`StockInsuficienteException`**: incluye `stockDisponible` (`int`) y `cantidadSolicitada` (`int`) como atributos. Su `getMessage()` debe retornar:  
`"Stock insuficiente: solicitados %d, disponibles %d"`.

**`RecetaRequeridaException`**: incluye el `nombreProducto` (`String`). Su `getMessage()` debe retornar:  
`"El medicamento '%s' requiere receta médica vigente"`.

**`PagoInvalidoException`**: incluye el `montoPago` (`double`) y `totalCompra` (`double`). Su `getMessage()`:  
`"Pago insuficiente: se pagó $%.0f pero el total es $%.0f"`.

---

### Clase `CarritoCompra`

**Atributos:**
- `Map<String, Integer> items`: código del producto → cantidad.
- `Map<String, Double> precios`: código → precio unitario.
- `Map<String, Boolean> requierenReceta`: código → requiere receta.
- `Map<String, Integer> stockDisponible`: código → stock actual.

**Métodos:**

- `agregarItem(String codigo, String nombre, double precio, int cantidad, boolean requiereReceta, int stock)`:  
  - Lanza `ProductoNoEncontradoException` si `codigo` es `null` o vacío.  
  - Lanza `StockInsuficienteException` si `cantidad > stock`.
  
- `procesarVenta(boolean clienteTieneReceta, double montoPago)`:  
  Lógica de negocio con las siguientes validaciones en orden:
  1. Lanza `CarritoVacioException` si no hay items.
  2. Para cada item que `requiereReceta`:  
     - **Proposición:** `¬clienteTieneReceta` → lanza `RecetaRequeridaException`.
  3. Calcula el total.
  4. Lanza `PagoInvalidoException` si `montoPago < total`.
  5. Retorna un objeto `TicketVenta` con el resumen.

- `calcularTotal()`: suma `precio × cantidad` para todos los items.

---

### Clase `TicketVenta`

Record (Java 16+) con:  
`record TicketVenta(String folio, List<String> items, double total, double montoPago, double vuelto) {}`

Método `imprimir()` que formatea la boleta.

---

### Clase `FarmaTechApp` (clase principal)

Implementa escenarios con `try/catch` para cada tipo de excepción.

---

## 🚫 Restricciones

- Las excepciones `FarmaTechException` y sus hijas son **checked** (heredan de `Exception`).
- Las `FarmaTechRuntimeException` y sus hijas son **unchecked** (heredan de `RuntimeException`).
- El bloque `procesarVenta` debe usar un único `try/catch` en el `main` que capture cada excepción específica antes de la genérica.
- Usa `finally` para imprimir siempre `"--- Fin de transacción ---"`.
- Usa `@Override` en `getMessage()` de las excepciones que lo sobreescriban.
- `TicketVenta` debe ser un `record`.

---

## 📥 Ejemplos de entrada

### Escenario A — Compra exitosa
```java
CarritoCompra carrito = new CarritoCompra();
carrito.agregarItem("MED-001", "Paracetamol 500mg", 2990, 2, false, 50);
carrito.agregarItem("ALM-001", "Vitamina C", 4500, 1, false, 30);
TicketVenta ticket = carrito.procesarVenta(false, 15000);
ticket.imprimir();
```

### Escenario B — Medicamento sin receta
```java
CarritoCompra carrito2 = new CarritoCompra();
carrito2.agregarItem("MED-002", "Amoxicilina 500mg", 8990, 1, true, 20);
carrito2.procesarVenta(false, 20000);  // sin receta → excepción
```

### Escenario C — Stock insuficiente
```java
CarritoCompra carrito3 = new CarritoCompra();
carrito3.agregarItem("MED-003", "Ibuprofeno 400mg", 3500, 100, false, 10);  // pide 100, hay 10
```

### Escenario D — Carrito vacío
```java
CarritoCompra carrito4 = new CarritoCompra();
carrito4.procesarVenta(false, 5000);
```

### Escenario E — Pago insuficiente
```java
CarritoCompra carrito5 = new CarritoCompra();
carrito5.agregarItem("ACC-001", "Termómetro digital", 12990, 1, false, 8);
carrito5.procesarVenta(false, 5000);  // paga 5000, total 12990
```

---

## 📤 Salidas esperadas

### Escenario A
```
════════════════════════════
       BOLETA FARMATECH
════════════════════════════
Folio: FT-2024-001
  Paracetamol 500mg x2   $5.980
  Vitamina C x1          $4.500
────────────────────────────
Total:                   $10.480
Pago:                    $15.000
Vuelto:                  $4.520
════════════════════════════
--- Fin de transacción ---
```

### Escenario B
```
❌ RecetaRequeridaException: El medicamento 'Amoxicilina 500mg' requiere receta médica vigente
--- Fin de transacción ---
```

### Escenario C
```
❌ StockInsuficienteException: Stock insuficiente: solicitados 100, disponibles 10
```

### Escenario D
```
❌ CarritoVacioException: No hay productos en el carrito
--- Fin de transacción ---
```

### Escenario E
```
❌ PagoInvalidoException: Pago insuficiente: se pagó $5.000 pero el total es $12.990
--- Fin de transacción ---
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Excepción checked con atributos</summary>

```java
public class StockInsuficienteException extends FarmaTechException {
    private final int stockDisponible;
    private final int cantidadSolicitada;

    public StockInsuficienteException(int stockDisponible, int cantidadSolicitada) {
        super(String.format("Stock insuficiente: solicitados %d, disponibles %d",
                cantidadSolicitada, stockDisponible));
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }
}
```
</details>

<details>
<summary>Pista 2 — try/catch con múltiples excepciones en el main</summary>

```java
try {
    TicketVenta ticket = carrito.procesarVenta(clienteTieneReceta, montoPago);
    ticket.imprimir();
} catch (RecetaRequeridaException e) {
    System.out.println("❌ RecetaRequeridaException: " + e.getMessage());
} catch (PagoInvalidoException e) {
    System.out.println("❌ PagoInvalidoException: " + e.getMessage());
} catch (CarritoVacioException e) {
    System.out.println("❌ CarritoVacioException: " + e.getMessage());
} catch (FarmaTechException e) {
    System.out.println("❌ Error general: " + e.getMessage());
} finally {
    System.out.println("--- Fin de transacción ---");
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Por qué `StockInsuficienteException` es **checked** y `CarritoVacioException` es **unchecked**? ¿Cuándo conviene cada tipo?
2. ¿Qué garantiza el bloque `finally`? ¿Puede una excepción en `finally` "tapar" una anterior?
3. Si el sistema tuviera un `Logger` que registra cada error en base de datos, ¿dónde lo llamarías: en el `catch` o en el `finally`?

---

*[← Ejercicio anterior](./09_biblioteca_digital.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./11_sistema_vuelos.md)*

