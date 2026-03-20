# Ejercicio 04 — Tabla de multiplicar

> **Nivel:** ⭐⭐ Básico-Medio · Nivel 4  
> **Conceptos:** Bucle `for` · Variables acumuladoras · Aritmética básica  
> **Tiempo estimado:** ≤ 7 minutos

---

## 🏢 Contexto

Una aplicación educativa para niños de primaria necesita un módulo que genere tablas de multiplicar. Como desarrollador del equipo, debes implementar el generador.

---

## 📋 Enunciado

Crea la clase `Ejercicio04TablaMultiplicar` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

El programa debe:

1. Declarar una variable `int numero` con el número cuya tabla se quiere imprimir (valor fijo entre 1 y 12).
2. Usar un bucle `for` que itere del 1 al 12.
3. En cada iteración, imprimir la línea de la tabla con el siguiente formato:

```
=== Tabla del 7 ===
7  x  1  =   7
7  x  2  =  14
7  x  3  =  21
7  x  4  =  28
7  x  5  =  35
7  x  6  =  42
7  x  7  =  49
7  x  8  =  56
7  x  9  =  63
7  x 10  =  70
7  x 11  =  77
7  x 12  =  84
```

4. Al final, imprimir la **suma de todos los resultados** de la tabla:

```
Suma total de la tabla: 546
```

---

## 🚫 Restricciones

- Una sola clase con método `main`.
- No uses `Scanner`.
- Usa un único bucle `for` para generar la tabla y acumular la suma.
- El formato de alineación de columnas es obligatorio (usa `printf` o `String.format`).

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Cuántas veces debe repetirse el bucle?
> - ¿Qué operación realizas en cada iteración?
> - ¿Cómo calcularías la suma total dentro del mismo bucle?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Para mantener el alineado de columnas puedes usar `printf`:

```java
System.out.printf("%d  x %2d  = %3d%n", numero, i, numero * i);
```

Y para acumular la suma declara una variable `suma` antes del bucle e incrementa en cada iteración:

```java
int suma = 0;
for (int i = 1; i <= 12; i++) {
    suma += numero * i;
}
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

public class Ejercicio04TablaMultiplicar {

    public static void main(String[] args) {
        int numero = 7;
        int suma = 0;

        System.out.println("=== Tabla del " + numero + " ===");
        for (int i = 1; i <= 12; i++) {
            // imprimir línea
            // acumular suma
        }
        // imprimir suma total
    }
}
```

