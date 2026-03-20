# Ejercicio 09 — Formas geométricas

> **Nivel:** ⭐⭐⭐⭐ Medio-Avanzado · Nivel 9  
> **Conceptos:** Interfaz (`interface`) · `implements` · Polimorfismo · `ArrayList` de tipos  
> **Tiempo estimado:** ≤ 12 minutos

---

## 🏢 Contexto

El equipo de una aplicación de diseño gráfico necesita un módulo que calcule el área y el perímetro de distintas figuras geométricas de manera uniforme. Sin importar el tipo de figura, el sistema debe poder operar con todas ellas usando el mismo contrato.

---

## 📋 Enunciado

Crea la clase `Ejercicio09FormasGeometricas` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

Este archivo debe contener **cinco elementos** (todos en el mismo archivo `.java`):

### Interfaz `Figura`
Define el **contrato** que deben cumplir todas las figuras:
- `double calcularArea()` — retorna el área.
- `double calcularPerimetro()` — retorna el perímetro.
- `String getNombre()` — retorna el nombre de la figura.

### Clase `Circulo` (implementa `Figura`)
- **Atributo:** `radio` (`double`).
- Nombre: `"Círculo"`.
- Área: `π × radio²`.
- Perímetro: `2 × π × radio`.

### Clase `Rectangulo` (implementa `Figura`)
- **Atributos:** `base` (`double`), `altura` (`double`).
- Nombre: `"Rectángulo"`.
- Área: `base × altura`.
- Perímetro: `2 × (base + altura)`.

### Clase `TrianguloEquilatero` (implementa `Figura`)
- **Atributo:** `lado` (`double`).
- Nombre: `"Triángulo Equilátero"`.
- Área: `(√3 / 4) × lado²`.
- Perímetro: `3 × lado`.

### Clase `Ejercicio09FormasGeometricas` (contiene `main`)
- Crea una lista `ArrayList<Figura>` y agrega un objeto de cada tipo.
- Recorre la lista con `for-each` e imprime para cada figura:
  ```
  === Círculo ===
  Área      : 78.54 u²
  Perímetro : 31.42 u
  ```
- Al final, muestra cuál es la figura con **mayor área**:
  ```
  Figura con mayor área: Rectángulo (120.00 u²)
  ```

---

## 🚫 Restricciones

- Todos los elementos en el mismo archivo `Ejercicio09FormasGeometricas.java`.
- Solo la clase `Ejercicio09FormasGeometricas` puede ser `public`.
- Usa `Math.PI` y `Math.sqrt()` donde corresponda.
- Redondea los resultados a 2 decimales con `String.format("%.2f", valor)`.
- Para encontrar la figura con mayor área, usa un bucle `for-each` y una variable auxiliar.
- No uses `Scanner`.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Qué es una interfaz y en qué se diferencia de una clase abstracta?
> - ¿Qué significa que `Circulo` implementa `Figura`?
> - ¿Cómo permite el polimorfismo tratar objetos distintos (`Circulo`, `Rectangulo`) de forma uniforme en la lista?
> - ¿Cómo encontrarías la figura con mayor área recorriendo la lista?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Al declarar la lista como `ArrayList<Figura>`, puedes llamar los métodos de la interfaz sin importar el tipo concreto:

```java
for (Figura figura : figuras) {
    System.out.println("=== " + figura.getNombre() + " ===");
    System.out.printf("Área      : %.2f u²%n", figura.calcularArea());
}
```

Para encontrar el máximo:

```java
Figura mayorFigura = figuras.get(0);
for (Figura figura : figuras) {
    if (figura.calcularArea() > mayorFigura.calcularArea()) {
        mayorFigura = figura;
    }
}
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

import java.util.ArrayList;

interface Figura {
    double calcularArea();
    double calcularPerimetro();
    String getNombre();
}

class Circulo implements Figura {
    private double radio;
    // constructor, @Override métodos
}

class Rectangulo implements Figura {
    private double base, altura;
    // constructor, @Override métodos
}

class TrianguloEquilatero implements Figura {
    private double lado;
    // constructor, @Override métodos
}

public class Ejercicio09FormasGeometricas {
    public static void main(String[] args) {
        ArrayList<Figura> figuras = new ArrayList<>();
        // agregar figuras
        // recorrer y mostrar info
        // mostrar figura con mayor área
    }
}
```

