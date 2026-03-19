# Ejercicio 07 — Tienda de mascotas

> **Nivel:** ⭐⭐⭐ Medio  
> **Conceptos:** Colecciones `List<T>` · `ArrayList` · Iteración con `for-each` · Búsqueda manual · Lógica proposicional en filtros · `toString()`  
> **Tiempo estimado:** 50–70 min

---

## 🐾 Contexto

**PetWorld** es una cadena de tiendas de mascotas que necesita un sistema para gestionar su **catálogo de productos** y procesar ventas. El inventario tiene distintos tipos de productos: alimentos, accesorios y medicamentos. Tu tarea es implementar el sistema de gestión del catálogo.

---

## 📋 Enunciado

### Clase `Producto`

**Atributos (privados):**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `codigo` | `String` | Código único del producto (ej: `"ALM-001"`) |
| `nombre` | `String` | Nombre del producto |
| `categoria` | `String` | `"ALIMENTO"`, `"ACCESORIO"` o `"MEDICAMENTO"` |
| `precio` | `double` | Precio de venta |
| `stock` | `int` | Unidades disponibles |
| `requiereReceta` | `boolean` | Solo aplica para medicamentos |

Constructor, getters y setter solo para `precio` y `stock`.

---

### Clase `CatalogoProductos`

**Atributo:** `List<Producto> productos` (privado).

**Métodos:**

- `agregarProducto(Producto p)`: agrega un producto. Lanza `IllegalArgumentException` si el código ya existe.
- `buscarPorCodigo(String codigo)`: retorna el `Producto` o `null` si no existe.
- `buscarPorCategoria(String categoria)`: retorna una nueva `List<Producto>` con los productos de esa categoría.
- `productosConStock()`: retorna lista de productos con `stock > 0`.
- `productosAgotados()`: retorna lista de productos con `stock == 0`.
- `actualizarStock(String codigo, int cantidad)`: suma o resta del stock. Lanza `IllegalStateException` si el nuevo stock sería negativo.
- `imprimirCatalogo()`: imprime todos los productos ordenados por categoría.
- `calcularValorInventario()`: retorna `double` con el valor total (`precio × stock`) de todos los productos.

**Lógica proposicional en `puedeVender`:**  
Implementa el método `boolean puedeVender(String codigo, int cantidad, boolean clienteTienReceta)`:
- **p:** el producto existe en el catálogo.
- **q:** hay suficiente stock (`stock >= cantidad`).
- **r:** el producto **no** requiere receta `(¬requiereReceta)`.
- **s:** el cliente tiene receta.
- Puede venderse si: `p ∧ q ∧ (r ∨ s)`

---

## 🚫 Restricciones

- Usa `ArrayList<Producto>` internamente; **no uses arreglos**.
- Los métodos de búsqueda que retornan listas deben retornar una **nueva lista** (no la interna).
- Valida en `agregarProducto` que el código no se repita (iteración manual, sin Streams).
- En `puedeVender`, declara explícitamente las proposiciones como variables `boolean`.
- No uses `Collections.sort()`; el orden en `imprimirCatalogo` puede hacerse agrupando por categoría con `if`.

---

## 📥 Ejemplos de entrada

```java
CatalogoProductos catalogo = new CatalogoProductos();

catalogo.agregarProducto(new Producto("ALM-001", "Royal Canin Adulto 15kg", "ALIMENTO", 32990, 20, false));
catalogo.agregarProducto(new Producto("ALM-002", "Whiskas Atún 1kg", "ALIMENTO", 4990, 0, false));
catalogo.agregarProducto(new Producto("ACC-001", "Correa ajustable nylon", "ACCESORIO", 8990, 15, false));
catalogo.agregarProducto(new Producto("ACC-002", "Cama ortopédica para perro", "ACCESORIO", 45990, 5, false));
catalogo.agregarProducto(new Producto("MED-001", "Antibiótico Amoxicilina 500mg", "MEDICAMENTO", 12990, 30, true));
catalogo.agregarProducto(new Producto("MED-002", "Vitamina C Masticable", "MEDICAMENTO", 5990, 10, false));

catalogo.imprimirCatalogo();

System.out.println("\n¿Puede vender MED-001 x2 sin receta? " + catalogo.puedeVender("MED-001", 2, false));
System.out.println("¿Puede vender MED-001 x2 con receta? " + catalogo.puedeVender("MED-001", 2, true));
System.out.println("¿Puede vender ALM-002 x1?             " + catalogo.puedeVender("ALM-002", 1, false));

catalogo.actualizarStock("ALM-001", -3);
System.out.println("\nValor total del inventario: $" + catalogo.calcularValorInventario());
```

---

## 📤 Salidas esperadas

```
════════════════════════════════════════════
          CATÁLOGO PETWORLD
════════════════════════════════════════════
🥩 ALIMENTOS
  [ALM-001] Royal Canin Adulto 15kg   - $32.990  Stock: 20
  [ALM-002] Whiskas Atún 1kg          - $4.990   Stock: 0  ⚠️ AGOTADO

🎾 ACCESORIOS
  [ACC-001] Correa ajustable nylon    - $8.990   Stock: 15
  [ACC-002] Cama ortopédica para perro- $45.990  Stock: 5

💊 MEDICAMENTOS
  [MED-001] Antibiótico Amoxicilina   - $12.990  Stock: 30  🔒 Requiere receta
  [MED-002] Vitamina C Masticable     - $5.990   Stock: 10

¿Puede vender MED-001 x2 sin receta? false
¿Puede vender MED-001 x2 con receta? true
¿Puede vender ALM-002 x1?             false

Valor total del inventario: $1.244.570
```

> 💡 El valor del inventario se calcula como:  
> `(32990×17) + (4990×0) + (8990×15) + (45990×5) + (12990×30) + (5990×10)`

---

## 💡 Pistas

<details>
<summary>Pista 1 — Verificar código duplicado</summary>

```java
public void agregarProducto(Producto p) {
    for (Producto existente : productos) {
        if (existente.getCodigo().equals(p.getCodigo())) {
            throw new IllegalArgumentException("Ya existe el código: " + p.getCodigo());
        }
    }
    productos.add(p);
}
```
</details>

<details>
<summary>Pista 2 — puedeVender con proposiciones</summary>

```java
public boolean puedeVender(String codigo, int cantidad, boolean clienteTieneReceta) {
    Producto prod = buscarPorCodigo(codigo);
    boolean p = prod != null;
    if (!p) return false;
    boolean q = prod.getStock() >= cantidad;
    boolean r = !prod.isRequiereReceta();
    boolean s = clienteTieneReceta;
    return p && q && (r || s);
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Por qué retornar una **nueva lista** en los métodos de búsqueda y no la interna?
2. ¿Qué pasa si el código de `puedeVender` recibe un `codigo` que no existe? ¿Cómo lo manejas?
3. La fórmula `p ∧ q ∧ (r ∨ s)` — ¿cómo se lee en lenguaje natural? Construye la tabla de verdad para los casos `r=false, s=false` y `r=false, s=true`.

---

*[← Ejercicio anterior](./06_gestor_empleados.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./08_reservas_restaurante.md)*

