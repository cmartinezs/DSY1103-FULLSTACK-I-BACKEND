# Ejercicio 13 — E-commerce de tecnología

> **Nivel:** ⭐⭐⭐⭐ Medio-Avanzado  
> **Conceptos:** `Stream` avanzado · `Optional` · `flatMap` · `reduce` · `Collectors.toMap` · `groupingBy` · `partitioningBy` · Lógica proposicional en reglas de negocio  
> **Tiempo estimado:** 75–100 min

---

## 🛒 Contexto

**TechMart** es un e-commerce de tecnología. Tienen un sistema de pedidos con múltiples líneas (cada pedido tiene varios productos). El área de analítica necesita **reportes avanzados** sobre ventas, clientes y productos. Tu tarea es procesar estos datos usando **Streams avanzados**.

---

## 📋 Enunciado

### Records de datos

```java
public record Producto(String sku, String nombre, String categoria, double precio, int stock) {}

public record LineaPedido(Producto producto, int cantidad) {
    public double subtotal() { return producto.precio() * cantidad; }
}

public record Pedido(
    String id,
    String clienteRut,
    String clienteNombre,
    List<LineaPedido> lineas,
    String estado,           // "PENDIENTE", "ENVIADO", "ENTREGADO", "CANCELADO"
    String region
) {
    public double total() {
        return lineas.stream().mapToDouble(LineaPedido::subtotal).sum();
    }
}
```

---

### Clase `MotorAnalitica`

Recibe `List<Pedido>` en el constructor. Todos los métodos deben usar **Streams**.

#### Métodos requeridos:

1. **`getTotalVentas()`** → `double`: suma de totales de pedidos `ENTREGADO`.

2. **`getPedidosPorEstado()`** → `Map<String, Long>`: cantidad de pedidos por estado.

3. **`getPedidosPorEstadoParticionados()`** → `Map<Boolean, List<Pedido>>`: activos (PENDIENTE o ENVIADO) vs inactivos.

4. **`getTopProductosMasVendidos(int n)`** → `List<String>`: nombres de los `n` productos más vendidos por cantidad (usando `flatMap`).

5. **`getIngresoPorRegion()`** → `Map<String, Double>`: suma de ventas entregadas por región.

6. **`getClientesConMasGasto(int n)`** → `List<String>`: los `n` clientes (por nombre) con mayor gasto acumulado en pedidos `ENTREGADO`.

7. **`getProductoMasVendidoPorCategoria()`** → `Map<String, String>`: para cada categoría, el nombre del producto más vendido.

8. **`getTicketPromedio()`** → `double`: promedio del total de pedidos `ENTREGADO`.

9. **`hayStockSuficiente(String sku, int cantidad)`** → `boolean`:  
   **Proposición lógica:**  
   - **p:** el producto existe en alguna línea de pedido.  
   - **q:** `stock >= cantidad`.  
   - Retorna `p ∧ q`.

10. **`getPedidosConDescuentoPotencial()`** → `List<Pedido>`: pedidos `PENDIENTE` con total > $100.000 (candidatos a descuento).

11. **`imprimirReporteEjecutivo()`**: imprime un dashboard con los datos anteriores.

---

## 🚫 Restricciones

- **No uses `for`, `while` ni `if` fuera de lambdas** en ningún método del 1 al 10.
- Usa `flatMap` en `getTopProductosMasVendidos`.
- Usa `Collectors.partitioningBy` en `getPedidosPorEstadoParticionados`.
- Usa `Collectors.toMap` o `groupingBy` con downstream en `getProductoMasVendidoPorCategoria`.
- Usa `reduce` o `mapToDouble(...).sum()` donde corresponda.
- `imprimirReporteEjecutivo()` puede usar `forEach`.
- Todos los `record` son inmutables (no los modifiques).

---

## 📥 Ejemplos de entrada

```java
// Productos
Producto p1 = new Producto("SKU-001", "MacBook Pro 14\"", "Laptops", 1_899_990, 5);
Producto p2 = new Producto("SKU-002", "iPhone 15 Pro",    "Smartphones", 999_990, 12);
Producto p3 = new Producto("SKU-003", "AirPods Pro",      "Audio", 249_990, 30);
Producto p4 = new Producto("SKU-004", "Samsung Galaxy S24","Smartphones", 799_990, 8);
Producto p5 = new Producto("SKU-005", "Monitor LG 27\"",  "Monitores", 399_990, 6);
Producto p6 = new Producto("SKU-006", "Teclado mecánico", "Accesorios", 89_990, 25);

// Pedidos
List<Pedido> pedidos = List.of(
    new Pedido("PED-001", "11111111-1", "Ana García",
        List.of(new LineaPedido(p1, 1), new LineaPedido(p3, 2)),
        "ENTREGADO", "Metropolitana"),

    new Pedido("PED-002", "22222222-2", "Carlos López",
        List.of(new LineaPedido(p2, 2), new LineaPedido(p6, 1)),
        "ENVIADO", "Valparaíso"),

    new Pedido("PED-003", "11111111-1", "Ana García",
        List.of(new LineaPedido(p4, 1)),
        "ENTREGADO", "Metropolitana"),

    new Pedido("PED-004", "33333333-3", "María Torres",
        List.of(new LineaPedido(p5, 2), new LineaPedido(p6, 3)),
        "PENDIENTE", "Biobío"),

    new Pedido("PED-005", "44444444-4", "Juan Pérez",
        List.of(new LineaPedido(p2, 1), new LineaPedido(p3, 1)),
        "ENTREGADO", "Metropolitana"),

    new Pedido("PED-006", "22222222-2", "Carlos López",
        List.of(new LineaPedido(p1, 1)),
        "CANCELADO", "Valparaíso"),

    new Pedido("PED-007", "33333333-3", "María Torres",
        List.of(new LineaPedido(p4, 2), new LineaPedido(p3, 3)),
        "PENDIENTE", "Biobío")
);

MotorAnalitica motor = new MotorAnalitica(pedidos);
motor.imprimirReporteEjecutivo();
```

---

## 📤 Salidas esperadas

```
╔══════════════════════════════════════════════════════╗
║         REPORTE EJECUTIVO — TECHMART                ║
╚══════════════════════════════════════════════════════╝

💰 MÉTRICAS DE VENTAS
  Total ventas (entregados): $4.349.950
  Ticket promedio:           $1.449.983
  Pedidos por estado:
    ENTREGADO : 3
    ENVIADO   : 1
    PENDIENTE : 2
    CANCELADO : 1

🗺️  INGRESOS POR REGIÓN (entregados)
  Metropolitana : $3.549.960
  Valparaíso    : $0
  Biobío        : $0

👑 TOP 3 CLIENTES POR GASTO
  1. Ana García    — $2.699.980
  2. Juan Pérez    — $1.249.970
  3. Carlos López  — $0 (cancelado)

📦 TOP 3 PRODUCTOS MÁS VENDIDOS (unidades)
  1. AirPods Pro          — 6 unidades
  2. iPhone 15 Pro        — 3 unidades
  3. Samsung Galaxy S24   — 3 unidades

🏆 MEJOR PRODUCTO POR CATEGORÍA
  Laptops     : MacBook Pro 14"
  Smartphones : iPhone 15 Pro
  Audio       : AirPods Pro
  Monitores   : Monitor LG 27"
  Accesorios  : Teclado mecánico

🏷️  PEDIDOS CANDIDATOS A DESCUENTO (pendientes > $100.000)
  PED-004 — María Torres  — $1.069.950
  PED-007 — María Torres  — $2.348.950
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — flatMap para aplanar líneas de pedidos</summary>

```java
public List<String> getTopProductosMasVendidos(int n) {
    return pedidos.stream()
        .flatMap(p -> p.lineas().stream())
        .collect(Collectors.groupingBy(
            l -> l.producto().nombre(),
            Collectors.summingInt(LineaPedido::cantidad)
        ))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(n)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
}
```
</details>

<details>
<summary>Pista 2 — Mejor producto por categoría</summary>

```java
public Map<String, String> getProductoMasVendidoPorCategoria() {
    // 1. Aplanar todas las líneas
    // 2. groupingBy(categoria, luego sumar cantidades por producto)
    // 3. Para cada categoria, encontrar el producto con más cantidad
}
```
</details>

<details>
<summary>Pista 3 — partitioningBy</summary>

```java
public Map<Boolean, List<Pedido>> getPedidosPorEstadoParticionados() {
    return pedidos.stream()
        .collect(Collectors.partitioningBy(p ->
            p.estado().equals("PENDIENTE") || p.estado().equals("ENVIADO")
        ));
}
// true  → activos
// false → inactivos (ENTREGADO, CANCELADO)
```
</details>

---

## 🧠 Reflexión final

1. ¿Cuándo usarías `flatMap` en lugar de `map`? Da un ejemplo de cuándo `map` sería insuficiente.
2. ¿Por qué `partitioningBy` es preferible a `groupingBy` cuando la condición es binaria?
3. ¿Cómo expresarías la condición `getTopProductosMasVendidos` como una consulta SQL? ¿Qué cláusulas usarías?

---

*[← Ejercicio anterior](./12_plataforma_cursos.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./14_red_social_universitaria.md)*

