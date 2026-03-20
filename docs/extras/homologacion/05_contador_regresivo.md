# Ejercicio 05 — Contador regresivo

> **Nivel:** ⭐⭐ Básico-Medio · Nivel 5  
> **Conceptos:** Bucle `while` · Bucle `do-while` · Variables de control  
> **Tiempo estimado:** ≤ 8 minutos

---

## 🏢 Contexto

Estás desarrollando el módulo de lanzamiento de una aplicación de cohetes educativa. El sistema necesita dos contadores: uno que valide si el usuario ingresó un número válido antes de iniciar (usando `do-while`) y otro que realice la cuenta regresiva (usando `while`).

---

## 📋 Enunciado

Crea la clase `Ejercicio05ContadorRegresivo` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

El programa debe resolver **dos partes** independientes:

### Parte A — Validación con `do-while`

Simula que un operador ingresa un número de inicio para la cuenta regresiva. Define una variable `int intentos` que comience en 0 y una variable `int valorIngresado`. Usando un bucle `do-while`:

- El bloque se ejecuta **al menos una vez**.
- Si `valorIngresado` es menor o igual a 0, incrementa `intentos` y muestra: `"Valor inválido. Intento #X. Ingresa un número mayor a 0."`.
- Cuando el valor sea válido (> 0), muestra: `"Valor aceptado: X"`.

> Simula el comportamiento definiendo de antemano en el código qué valores se "ingresan" (ej: -5, 0, 10). Itera sobre un arreglo de valores de prueba.

### Parte B — Cuenta regresiva con `while`

Con el `valorIngresado` válido obtenido en la Parte A, realiza la cuenta regresiva usando un bucle `while`:

```
Iniciando cuenta regresiva desde 10...
10
9
8
7
6
5
4
3
2
1
0
¡Despegue! 🚀
```

---

## 🚫 Restricciones

- Una sola clase con método `main`.
- No uses `Scanner`.
- La Parte A **debe** usar `do-while`.
- La Parte B **debe** usar `while`.
- Ambas partes deben estar separadas con comentarios en el código.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Cuál es la diferencia clave entre `while` y `do-while`?
> - ¿Por qué `do-while` es adecuado para la validación de entrada?
> - ¿Cuál es la condición de parada del bucle de cuenta regresiva?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Para simular múltiples intentos sin `Scanner`, usa un arreglo y un índice:

```java
int[] valoresPrueba = {-5, 0, 10};
int indice = 0;
int valorIngresado;
int intentos = 0;

do {
    valorIngresado = valoresPrueba[indice++];
    // evaluar y acumular intentos
} while (valorIngresado <= 0);
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

public class Ejercicio05ContadorRegresivo {

    public static void main(String[] args) {

        // === PARTE A: Validación con do-while ===
        int[] valoresPrueba = {-5, 0, 10};
        int indice = 0;
        int valorIngresado;
        int intentos = 0;

        do {
            // leer siguiente valor simulado
            // evaluar
        } while (...);

        // === PARTE B: Cuenta regresiva con while ===
        int contador = valorIngresado;
        System.out.println("Iniciando cuenta regresiva desde " + contador + "...");
        while (...) {
            // imprimir y decrementar
        }
        System.out.println("¡Despegue! 🚀");
    }
}
```

