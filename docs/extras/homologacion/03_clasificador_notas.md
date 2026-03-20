# Ejercicio 03 — Clasificador de notas

> **Nivel:** ⭐ Básico · Nivel 3  
> **Conceptos:** Variables · `if` / `else if` / `else` · Rangos numéricos  
> **Tiempo estimado:** ≤ 6 minutos

---

## 🏢 Contexto

El sistema de gestión académica de DUOC UC necesita una función que clasifique automáticamente la nota de un alumno y le entregue un mensaje motivador según su rendimiento.

---

## 📋 Enunciado

Crea la clase `Ejercicio03ClasificadorNotas` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

El programa debe:

1. Declarar una variable `double nota` con un valor entre **1.0 y 7.0**.
2. Declarar una variable `String nombreAlumno` con el nombre del estudiante.
3. Clasificar la nota según la siguiente tabla y mostrar el resultado:

| Rango | Clasificación | Mensaje |
|-------|--------------|---------|
| 6.0 – 7.0 | Excelente | `"¡Felicitaciones! Rendimiento sobresaliente."` |
| 5.0 – 5.9 | Bueno | `"Buen trabajo, sigue así."` |
| 4.0 – 4.9 | Suficiente | `"Aprobado, pero puedes mejorar."` |
| 1.0 – 3.9 | Reprobado | `"No aprobaste. Debes rendir el examen de repetición."` |

**Formato de salida:**
```
Alumno  : María González
Nota    : 5.8
Estado  : Bueno
Mensaje : Buen trabajo, sigue así.
```

---

## 🚫 Restricciones

- Una sola clase con método `main`.
- No uses `Scanner`.
- Usa `if / else if / else` (no `switch`).
- Los rangos deben evaluarse correctamente con `>=` y `<`.
- La nota debe validarse: si está fuera del rango [1.0, 7.0], imprime `"Nota inválida."`.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Cómo defines los rangos de nota con condicionales encadenados?
> - ¿Qué diferencia hay entre `if/else if` y múltiples `if` independientes?
> - ¿Cómo aseguras que los rangos no se superpongan?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Evalúa los rangos de mayor a menor (de 6.0 a 1.0) para evitar condiciones superpuestas:

```java
if (nota >= 6.0) {
    // Excelente
} else if (nota >= 5.0) {
    // Bueno
} // ...
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

public class Ejercicio03ClasificadorNotas {

    public static void main(String[] args) {
        String nombreAlumno = "...";
        double nota = ...;

        String clasificacion;
        String mensaje;

        if (nota < 1.0 || nota > 7.0) {
            // nota inválida
        } else if (...) {
            // rangos
        }

        // imprimir resultado
    }
}
```

