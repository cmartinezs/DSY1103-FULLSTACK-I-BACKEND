# Módulo 00 — Cómo pensar un problema antes de codear

> Este módulo no tiene código. Tiene algo más valioso: el proceso mental que separa a quien resuelve problemas de quien solo copia sintaxis.

---

## El error más común

Cuando aparece un problema, la mayoría de estudiantes hace esto:

```
Lee el enunciado → Abre el editor → Empieza a escribir código
```

Y después de 20 minutos tiene un código que no funciona, no sabe por qué, y no sabe por dónde empezar a arreglarlo.

El problema no es Java. El problema es que **no había un plan**.

---

## El proceso correcto

```
Lee el enunciado → Entiende el problema → Diseña la solución → Escribe el código
```

Puede parecer que el paso intermedio toma tiempo. En realidad te **ahorra** tiempo, porque no vuelves a empezar tres veces.

---

## Las tres preguntas fundamentales

Antes de escribir una sola línea de Java, responde estas tres preguntas en papel o en comentarios:

### 1. ¿Qué tengo? (datos de entrada)
¿Con qué datos trabajo? ¿De dónde vienen? ¿Qué tipo son?

```
Ejemplos:
- Una lista de precios (List<Double>)
- Un nombre de usuario (String)
- Una edad (int)
- Un objeto Ticket con status y prioridad
```

### 2. ¿Qué quiero? (resultado esperado)
¿Qué debe producir el programa? ¿Un número, un mensaje, una lista filtrada, un booleano?

```
Ejemplos:
- El total de la compra (double)
- Si el usuario puede acceder o no (boolean)
- La lista de tickets con status "ABIERTO" (List<Ticket>)
- Un mensaje de error si algo falla (String)
```

### 3. ¿Cómo llego? (el algoritmo)
¿Qué pasos necesito para transformar lo que tengo en lo que quiero?

```
Ejemplos:
- Recorrer la lista → sumar cada precio → devolver el total
- Verificar si la edad >= 18 → y si el rol es "ADMIN" → devolver true/false
- Filtrar los tickets por status → devolver solo los que coinciden
```

---

## Pseudocódigo: escribe primero en español

El **pseudocódigo** es escribir el algoritmo en lenguaje natural estructurado, antes de traducirlo a Java. No tiene sintaxis exacta — es para pensar, no para ejecutar.

**Ejemplo:** calcular si un estudiante aprueba o reprueba

```
// Pseudocódigo
1. Recibir las notas del estudiante
2. Sumar todas las notas
3. Dividir por la cantidad de notas → obtener promedio
4. Si promedio >= 4.0:
       el estudiante aprueba
   Si no:
       el estudiante reprueba
5. Mostrar el resultado
```

Ahora sí, traducir a Java es casi mecánico:

```java
List<Double> notas = List.of(5.5, 4.0, 6.2, 3.8);

double suma = 0;
for (double nota : notas) {
    suma += nota;
}

double promedio = suma / notas.size();

if (promedio >= 4.0) {
    System.out.println("Aprueba con promedio: " + promedio);
} else {
    System.out.println("Reprueba con promedio: " + promedio);
}
```

---

## Divide y vencerás

Si el problema parece grande, **divídelo en partes más pequeñas** y resuelve una a la vez.

**Ejemplo:** sistema de registro de tickets

❌ Forma incorrecta de pensar:
> "Tengo que hacer todo el sistema de tickets"

✅ Forma correcta de pensar:
> 1. Primero: representar un ticket (clase o record)
> 2. Luego: poder crear tickets nuevos
> 3. Luego: poder listar los tickets existentes
> 4. Luego: poder cambiar el estado de un ticket
> 5. Luego: exponer todo eso como endpoints

Cada uno de esos pasos es manejable. Juntos forman el sistema completo.

---

## La plantilla mental (úsala siempre)

```
┌─────────────────────────────────────────────┐
│  PROBLEMA: [descripción en una oración]      │
│                                             │
│  TENGO:                                     │
│    - dato 1: tipo                           │
│    - dato 2: tipo                           │
│                                             │
│  QUIERO:                                    │
│    - resultado: tipo                        │
│                                             │
│  PASOS:                                     │
│    1.                                       │
│    2.                                       │
│    3.                                       │
└─────────────────────────────────────────────┘
```

---

## Aplicando la plantilla: ejemplo completo guiado

**Enunciado:** "Dado un carrito de compras con productos y precios, calcular el total aplicando un descuento del 10% si el subtotal supera los $50.000."

```
PROBLEMA: calcular el total del carrito con descuento condicional

TENGO:
  - lista de productos: List<Producto>
  - cada Producto tiene: nombre (String), precio (double), cantidad (int)
  - tasa de descuento: 10% si subtotal > 50.000

QUIERO:
  - subtotal: double  (suma de precio × cantidad de cada producto)
  - descuento aplicado: double
  - total final: double

PASOS:
  1. Recorrer la lista de productos
  2. Por cada producto: acumular (precio × cantidad) al subtotal
  3. Verificar si subtotal > 50.000
  4. Si sí → calcular descuento = subtotal × 0.10
  5. Si no → descuento = 0
  6. total = subtotal - descuento
  7. Mostrar subtotal, descuento y total
```

Con ese plan, el código se escribe solo:

```java
record Producto(String nombre, double precio, int cantidad) {}

List<Producto> carrito = List.of(
    new Producto("Teclado",  29_990, 1),
    new Producto("Mouse",   15_500, 2),
    new Producto("Pad",      8_000, 1)
);

// Paso 1-2: acumular subtotal
double subtotal = 0;
for (Producto p : carrito) {
    subtotal += p.precio() * p.cantidad();
}

// Paso 3-5: descuento condicional
double descuento = subtotal > 50_000 ? subtotal * 0.10 : 0;

// Paso 6: total
double total = subtotal - descuento;

// Paso 7: mostrar
System.out.printf("Subtotal:  $%,.2f%n", subtotal);
System.out.printf("Descuento: $%,.2f%n", descuento);
System.out.printf("Total:     $%,.2f%n", total);
```

---

## Resumen

| Hábito | Por qué importa |
|--------|----------------|
| Responde las 3 preguntas antes de codear | Evita reescribir el código 3 veces |
| Escribe pseudocódigo primero | El código se convierte en traducción, no en invención |
| Divide el problema en partes | Cada parte es manejable; el conjunto es el sistema |
| Lee el resultado esperado antes de escribir código | Sabes cuándo terminaste |

> 💡 Un buen programador no es el que escribe más rápido. Es el que **piensa mejor** antes de escribir.

→ [Siguiente: Situaciones básicas](./01_situaciones_basicas.md)

