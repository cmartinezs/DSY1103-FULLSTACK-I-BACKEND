# Ejercicio 10 — Sistema de vehículos

> **Nivel:** ⭐⭐⭐⭐⭐ Avanzado · Nivel 10  
> **Conceptos:** Clase abstracta · Abstracción · Polimorfismo · Herencia · Interfaz · Encapsulamiento · `ArrayList`  
> **Tiempo estimado:** ≤ 12 minutos

---

## 🏢 Contexto

Una empresa de transporte necesita un sistema que administre su flota de vehículos. La flota incluye automóviles, camiones y motocicletas. Todos los vehículos comparten características comunes, pero cada tipo calcula su consumo de combustible de forma diferente. Además, algunos vehículos son **eléctricos** y deben cumplir un contrato especial de recarga.

---

## 📋 Enunciado

Crea la clase `Ejercicio10SistemaVehiculos` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

Este archivo debe contener **todos los elementos** (mismo archivo `.java`):

### Interfaz `Electrico`
- `void recargar(int minutos)` — simula una recarga e imprime: `"Recargando [marca] durante [minutos] minutos..."`.
- `int getNivelBateria()` — retorna el nivel de batería (0–100).

### Clase abstracta `Vehiculo`
- **Atributos protegidos:** `marca` (`String`), `modelo` (`String`), `anio` (`int`), `kilometraje` (`double`).
- **Constructor** que inicialice todos los atributos.
- **Getters** para todos los atributos.
- **Método concreto `mostrarInfo()`** que imprime la información base:
  ```
  [Automóvil] Toyota Corolla (2022) | Km: 15000.0
  ```
- **Método abstracto `calcularConsumo(double km)`** que retorna el litros (o kWh) necesarios para recorrer `km` kilómetros.

### Clase `Automovil` (extiende `Vehiculo`)
- **Atributo:** `consumoPorKm` (`double`) — litros por km.
- `calcularConsumo(double km)` → `consumoPorKm × km`.
- Sobreescribe `mostrarInfo()` llamando a `super.mostrarInfo()` y añadiendo: `"  Consumo estimado (100 km): X.X litros"`.

### Clase `Camion` (extiende `Vehiculo`)
- **Atributos:** `consumoPorKm` (`double`), `cargaToneladas` (`double`).
- `calcularConsumo(double km)` → `consumoPorKm × km × (1 + cargaToneladas / 10)` (la carga aumenta el consumo).
- Sobreescribe `mostrarInfo()` añadiendo la carga actual y el consumo estimado.

### Clase `AutomovilElectrico` (extiende `Automovil`, implementa `Electrico`)
- **Atributo:** `nivelBateria` (`int`).
- Sobreescribe `calcularConsumo(double km)` → retorna `0.15 × km` (kWh por km).
- Implementa `recargar(int minutos)`: incrementa `nivelBateria` en `minutos / 5` (máximo 100) e imprime el mensaje.
- Implementa `getNivelBateria()`.
- Sobreescribe `mostrarInfo()` añadiendo: `"  Batería: X% | Consumo estimado (100 km): 15.0 kWh"`.

### Clase `Ejercicio10SistemaVehiculos` (contiene `main`)
- Crea una `ArrayList<Vehiculo>` con un `Automovil`, un `Camion` y un `AutomovilElectrico`.
- Recorre la lista con `for-each` llamando a `mostrarInfo()` y `calcularConsumo(100.0)`.
- Para el `AutomovilElectrico`, también llama a `recargar(30)`.
- Al final, imprime el **vehículo con mayor consumo** en 100 km:
  ```
  Vehículo de mayor consumo (100 km): Camion Ford F-350 → 45.0 litros
  ```

---

## 🚫 Restricciones

- Todos los elementos en el mismo archivo `.java`.
- Solo la clase `Ejercicio10SistemaVehiculos` puede ser `public`.
- Usa `abstract` correctamente (clase abstracta y método abstracto).
- Usa `@Override` en todos los métodos sobreescritos.
- Usa `instanceof` para verificar si un `Vehiculo` es también `Electrico` antes de llamar a `recargar`.
- No uses `Scanner`.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Qué diferencia hay entre una clase abstracta y una interfaz?
> - ¿Por qué `calcularConsumo` es abstracto en `Vehiculo`?
> - ¿Cómo permite el polimorfismo llamar a `mostrarInfo()` de distintas subclases usando una lista de `Vehiculo`?
> - ¿Cómo usarías `instanceof` para detectar si un vehículo es eléctrico?
> - ¿Cómo una clase puede extender otra **y** implementar una interfaz al mismo tiempo?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Para detectar si un elemento de la lista es eléctrico y llamar `recargar`:

```java
for (Vehiculo v : flota) {
    v.mostrarInfo();
    System.out.printf("  Consumo (100 km): %.1f%n", v.calcularConsumo(100.0));
    if (v instanceof Electrico electrico) {
        electrico.recargar(30);
    }
}
```

Para encontrar el vehículo con mayor consumo:

```java
Vehiculo mayorConsumo = flota.get(0);
for (Vehiculo v : flota) {
    if (v.calcularConsumo(100.0) > mayorConsumo.calcularConsumo(100.0)) {
        mayorConsumo = v;
    }
}
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

import java.util.ArrayList;

interface Electrico {
    void recargar(int minutos);
    int getNivelBateria();
}

abstract class Vehiculo {
    protected String marca, modelo;
    protected int anio;
    protected double kilometraje;

    // constructor, getters
    public void mostrarInfo() { ... }
    public abstract double calcularConsumo(double km);
}

class Automovil extends Vehiculo {
    private double consumoPorKm;
    // constructor, @Override
}

class Camion extends Vehiculo {
    private double consumoPorKm, cargaToneladas;
    // constructor, @Override
}

class AutomovilElectrico extends Automovil implements Electrico {
    private int nivelBateria;
    // constructor, @Override, implementa Electrico
}

public class Ejercicio10SistemaVehiculos {
    public static void main(String[] args) {
        ArrayList<Vehiculo> flota = new ArrayList<>();
        // agregar vehículos
        // recorrer y mostrar info + consumo
        // recargar si es eléctrico
        // mostrar mayor consumo
    }
}
```

