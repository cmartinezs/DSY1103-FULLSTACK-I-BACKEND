# Ejercicio 07 — Agenda de contactos

> **Nivel:** ⭐⭐⭐ Medio · Nivel 7  
> **Conceptos:** `HashMap` · Recorrido de colecciones clave-valor · `entrySet` · `for-each`  
> **Tiempo estimado:** ≤ 9 minutos

---

## 🏢 Contexto

Estás desarrollando una agenda de contactos para una empresa de telecomunicaciones. Necesitas almacenar los números de teléfono de los clientes usando su nombre como clave de búsqueda, y brindar operaciones básicas: agregar, buscar y listar.

---

## 📋 Enunciado

Crea la clase `Ejercicio07AgendaContactos` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

El programa debe usar un `HashMap<String, String>` llamado `agenda` donde la **clave** es el nombre del contacto y el **valor** es su número de teléfono. Realiza las siguientes operaciones **en orden**:

1. **Agregar** los siguientes contactos:
   - `"Ana López"` → `"+56 9 1234 5678"`
   - `"Carlos Ruiz"` → `"+56 9 8765 4321"`
   - `"María González"` → `"+56 9 5555 0000"`
   - `"Pedro Soto"` → `"+56 9 1111 2222"`

2. **Mostrar** todos los contactos ordenados alfabéticamente por nombre:
   ```
   === Agenda de Contactos ===
   Ana López          → +56 9 1234 5678
   Carlos Ruiz        → +56 9 8765 4321
   María González     → +56 9 5555 0000
   Pedro Soto         → +56 9 1111 2222
   Total contactos: 4
   ```

3. **Buscar** el número de `"Carlos Ruiz"` e imprimir:
   - `"Contacto encontrado → Carlos Ruiz: +56 9 8765 4321"` si existe.
   - `"Contacto no encontrado: Carlos Ruiz"` si no existe.

4. **Actualizar** el teléfono de `"Ana López"` a `"+56 9 9999 8888"` e imprimir `"Contacto actualizado."`.

5. **Eliminar** a `"Pedro Soto"` de la agenda e imprimir `"Contacto eliminado: Pedro Soto"`.

6. **Mostrar** el estado final de la agenda con el mismo formato del paso 2.

---

## 🚫 Restricciones

- Una sola clase con método `main`.
- Usa `HashMap<String, String>` de `java.util`.
- Para listar los contactos ordenados, usa `new ArrayList<>(agenda.keySet())` y luego `Collections.sort()`.
- Usa `.containsKey()` para la búsqueda.
- No uses `Scanner`.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Qué es una clave y un valor en un `HashMap`?
> - ¿Por qué el `HashMap` es útil para una agenda de contactos?
> - ¿Cómo recorres todos los pares clave-valor de un `HashMap`?
> - ¿Qué ocurre si usas `put` con una clave que ya existe?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Para recorrer el `HashMap` con las claves ordenadas:

```java
List<String> nombres = new ArrayList<>(agenda.keySet());
Collections.sort(nombres);
for (String nombre : nombres) {
    System.out.printf("%-18s → %s%n", nombre, agenda.get(nombre));
}
```

Para actualizar un valor, simplemente usa `put` con la misma clave:

```java
agenda.put("Ana López", "+56 9 9999 8888");
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Ejercicio07AgendaContactos {

    public static void main(String[] args) {
        HashMap<String, String> agenda = new HashMap<>();

        // 1. Agregar contactos
        // 2. Mostrar agenda ordenada
        // 3. Buscar "Carlos Ruiz"
        // 4. Actualizar "Ana López"
        // 5. Eliminar "Pedro Soto"
        // 6. Mostrar agenda final
    }
}
```

