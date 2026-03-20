# Ejercicio 06 — Lista de compras

> **Nivel:** ⭐⭐⭐ Medio · Nivel 6  
> **Conceptos:** `ArrayList` · `for-each` · Métodos de colecciones · Lógica de búsqueda  
> **Tiempo estimado:** ≤ 8 minutos

---

## 🏢 Contexto

Estás desarrollando una pequeña app de supermercado. El módulo de carrito de compras necesita almacenar productos, mostrarlos, buscar si un producto ya está agregado y eliminar uno por nombre.

---

## 📋 Enunciado

Crea la clase `Ejercicio06ListaCompras` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

El programa debe usar un `ArrayList<String>` llamado `carrito` y realizar las siguientes operaciones **en orden**, mostrando el estado del carrito después de cada una:

1. **Agregar** estos productos al carrito: `"Leche"`, `"Pan"`, `"Huevos"`, `"Mantequilla"`, `"Jugo"`.
2. **Mostrar** todos los productos numerados:
   ```
   === Carrito de compras ===
   1. Leche
   2. Pan
   3. Huevos
   4. Mantequilla
   5. Jugo
   Total de productos: 5
   ```
3. **Buscar** si el producto `"Huevos"` está en el carrito e imprimir:
   - `"✔ Huevos está en el carrito."` si existe.
   - `"✘ Huevos no está en el carrito."` si no existe.
4. **Eliminar** el producto `"Pan"` del carrito.
5. **Mostrar** el carrito actualizado con el mismo formato del paso 2.

---

## 🚫 Restricciones

- Una sola clase con método `main`.
- Usa `ArrayList<String>` de `java.util`.
- Usa un bucle `for-each` para mostrar la lista (y una variable `int contador` para numerarla).
- Usa el método `.contains()` para la búsqueda y `.remove()` para eliminar.
- No uses `Scanner`.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Por qué se usa `ArrayList` en lugar de un arreglo normal?
> - ¿Cómo recorres una lista con `for-each`?
> - ¿Qué hace el método `contains` y cuál es su tipo de retorno?
> - ¿Qué diferencia hay entre eliminar por índice y eliminar por objeto?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Para numerar elementos con `for-each`, lleva un contador externo:

```java
int contador = 1;
for (String producto : carrito) {
    System.out.println(contador + ". " + producto);
    contador++;
}
```

Para eliminar por nombre (objeto `String`):

```java
carrito.remove("Pan");
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

import java.util.ArrayList;

public class Ejercicio06ListaCompras {

    public static void main(String[] args) {
        ArrayList<String> carrito = new ArrayList<>();

        // 1. Agregar productos
        // 2. Mostrar carrito
        // 3. Buscar "Huevos"
        // 4. Eliminar "Pan"
        // 5. Mostrar carrito actualizado
    }
}
```

