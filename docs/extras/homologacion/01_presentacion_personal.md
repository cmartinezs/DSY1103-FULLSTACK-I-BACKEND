# Ejercicio 01 — Presentación personal

> **Nivel:** ⭐ Básico · Nivel 1  
> **Conceptos:** Variables · Tipos de datos primitivos · Asignación · `System.out.println`  
> **Tiempo estimado:** ≤ 3 minutos

---

## 🏢 Contexto

Eres el nuevo desarrollador en el equipo. Como primer task, el líder técnico te pide crear un pequeño programa que muestre por consola los datos básicos de tu perfil profesional, tal como aparecería en una tarjeta de presentación digital.

---

## 📋 Enunciado

Crea la clase `Ejercicio01PresentacionPersonal` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

El programa debe:

1. Declarar las siguientes variables con **valores fijos** (sin usar `Scanner`):
   - `nombre` (`String`): tu nombre completo.
   - `edad` (`int`): tu edad.
   - `carrera` (`String`): el nombre de tu carrera.
   - `semestre` (`int`): el semestre en que te encuentras.
   - `tieneExperienciaLaboral` (`boolean`): `true` o `false` según corresponda.

2. Imprimir por consola una presentación con el siguiente formato exacto:

```
=== Tarjeta de Presentación ===
Nombre   : Juan Pérez
Edad     : 22 años
Carrera  : Ingeniería en Informática
Semestre : 4
Experiencia laboral: Sí
```

> Si `tieneExperienciaLaboral` es `false`, debe imprimir `No` en lugar de `Sí`.

---

## 🚫 Restricciones

- Una sola clase con método `main`.
- No uses `Scanner`.
- Usa **variables declaradas explícitamente** (no valores literales directo en el `println`).
- El valor `Sí` / `No` debe obtenerse evaluando la variable booleana con un operador ternario o un `if`.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Qué entiendes que pide el ejercicio?
> - ¿Qué pasos seguirías para resolverlo?
> - ¿Qué tipo de datos usarías para cada dato y por qué?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Recuerda que en Java puedes usar el operador ternario para transformar un `boolean` en texto:

```java
String respuesta = tieneExperienciaLaboral ? "Sí" : "No";
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

public class Ejercicio01PresentacionPersonal {

    public static void main(String[] args) {
        // 1. Declarar variables
        // 2. Imprimir presentación
    }
}
```

