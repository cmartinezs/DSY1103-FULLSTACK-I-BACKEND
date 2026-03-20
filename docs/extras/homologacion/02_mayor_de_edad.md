# Ejercicio 02 — Mayor de edad

> **Nivel:** ⭐ Básico · Nivel 2  
> **Conceptos:** Variables · `if` / `else` · Comparación de enteros  
> **Tiempo estimado:** ≤ 5 minutos

---

## 🏢 Contexto

Trabajas en el área de sistemas de una municipalidad. Se te pide implementar la validación de edad para un trámite en línea: solo los ciudadanos **mayores de 18 años** pueden completar el formulario por sí solos; los menores deben ser acompañados por un tutor.

---

## 📋 Enunciado

Crea la clase `Ejercicio02MayorDeEdad` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

El programa debe:

1. Declarar una variable `int edad` con un valor fijo (simula el dato ingresado por el ciudadano).
2. Declarar una variable `String nombre` con el nombre del ciudadano.
3. Evaluar la edad y mostrar un mensaje diferente según el caso:

**Si la persona es mayor o igual a 18 años:**
```
Bienvenido/a, Juan Pérez.
Puedes completar el trámite de forma autónoma.
```

**Si la persona es menor de 18 años:**
```
Hola, Juan Pérez.
Debes asistir con tu tutor legal para completar este trámite.
Tu edad registrada: 15 años.
```

---

## 🚫 Restricciones

- Una sola clase con método `main`.
- No uses `Scanner`.
- La evaluación debe realizarse con una sentencia `if / else`.
- Los mensajes deben adaptarse dinámicamente usando las variables declaradas.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Qué condición defines para determinar si alguien es mayor de edad?
> - ¿Qué pasa en el caso verdadero y en el caso falso?
> - ¿Cómo incluyes el nombre y la edad en los mensajes de salida?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Prueba tu solución con al menos dos valores de `edad`: uno mayor o igual a 18 y otro menor.

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

public class Ejercicio02MayorDeEdad {

    public static void main(String[] args) {
        String nombre = "...";
        int edad = ...;

        if (...) {
            // mayor de edad
        } else {
            // menor de edad
        }
    }
}
```

