# Módulo 02 — Contador y acumulador

> **Objetivo:** dominar los dos patrones matemáticos más frecuentes en programación: el contador (cuántos) y el acumulador (cuánto en total), y derivar de ellos el promedio.

---

## 2.1 ¿Qué es un contador?

Un **contador** es una variable que **aumenta (o disminuye) en una cantidad fija**, generalmente 1, cada vez que ocurre un evento.

```
contador = contador + 1   →   contador++
```

### Estructura básica

```java
int contador = 0;  // siempre se inicializa antes de usarlo

// Cada vez que ocurre el evento:
contador++;        // equivale a: contador = contador + 1
```

> ⚠️ El error más común: olvidar inicializar el contador en `0` antes del bucle.

---

## 2.2 Casos de uso del contador

### Contar elementos de una lista

```java
List<String> tickets = List.of("ABIERTO", "CERRADO", "ABIERTO", "ABIERTO", "CERRADO");

int abiertos = 0;
int cerrados = 0;

for (String estado : tickets) {
    if (estado.equals("ABIERTO")) {
        abiertos++;
    } else {
        cerrados++;
    }
}

System.out.println("Tickets abiertos: " + abiertos);   // 3
System.out.println("Tickets cerrados: " + cerrados);   // 2
```

### Contar intentos de login fallidos

```java
int intentosFallidos = 0;
final int LIMITE = 3;

while (intentosFallidos < LIMITE) {
    boolean loginOk = intentarLogin(usuario, password);

    if (loginOk) {
        System.out.println("Bienvenido");
        break;
    } else {
        intentosFallidos++;
        System.out.println("Contraseña incorrecta. Intento " + intentosFallidos + " de " + LIMITE);
    }
}

if (intentosFallidos == LIMITE) {
    System.out.println("Cuenta bloqueada por demasiados intentos");
}
```

### Contador descendente (cuenta regresiva)

```java
// Temporizador de 5 segundos
int segundos = 5;

while (segundos > 0) {
    System.out.println("Tiempo restante: " + segundos + "s");
    segundos--;  // decremento
}

System.out.println("¡Tiempo!");
```

### Contar con Spring Boot (ejemplo de servicio)

```java
@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    // Contar tickets por estado usando el repositorio
    public long contarAbiertos() {
        return ticketRepository.findAll()
                .stream()
                .filter(t -> t.getStatus().equals("ABIERTO"))
                .count();  // Stream tiene su propio contador
    }

    // Con query derivada de Spring Data
    public long contarPorEstado(String status) {
        return ticketRepository.countByStatus(status);
    }
}
```

---

## 2.3 ¿Qué es un acumulador?

Un **acumulador** es una variable que **suma (o acumula) valores variables** en cada iteración. A diferencia del contador, no suma siempre 1, sino el valor de cada elemento.

```
acumulador = acumulador + valorActual   →   acumulador += valorActual
```

### Estructura básica

```java
double total = 0.0;  // siempre inicializado en 0

for (double valor : valores) {
    total += valor;  // acumula cada valor
}
```

---

## 2.4 Casos de uso del acumulador

### Sumar precios de un carrito

```java
List<Double> precios = List.of(9990.0, 15500.0, 3200.0, 8750.0);

double totalCarrito = 0.0;

for (double precio : precios) {
    totalCarrito += precio;
}

System.out.printf("Total del carrito: $%.2f%n", totalCarrito);
// Total del carrito: $37440.00
```

### Acumular puntos de fidelidad

```java
List<Integer> compras = List.of(5000, 12000, 3500, 8000);
int puntosAcumulados = 0;
final int PUNTOS_POR_CADA_1000 = 1;  // 1 punto por cada $1000 gastados

for (int compra : compras) {
    puntosAcumulados += compra / 1000;  // división entera intencional
}

System.out.println("Puntos acumulados: " + puntosAcumulados);  // 28 puntos
```

### Calcular total de horas trabajadas

```java
double[] horasPorDia = {8.0, 7.5, 8.0, 6.5, 9.0};
double totalHoras = 0.0;

for (double horas : horasPorDia) {
    totalHoras += horas;
}

System.out.printf("Total semanal: %.1f horas%n", totalHoras);  // 39.0 horas
```

---

## 2.5 Combinando contador y acumulador: el promedio

El **promedio** (o media aritmética) combina ambos patrones:

```
promedio = total / cantidad
```

```java
List<Integer> notas = List.of(65, 80, 72, 90, 58, 88);

int sumaNotas = 0;      // acumulador
int cantidadNotas = 0;  // contador

for (int nota : notas) {
    sumaNotas    += nota;   // acumula
    cantidadNotas++;         // cuenta
}

double promedio = (double) sumaNotas / cantidadNotas;  // cast para no perder decimales

System.out.printf("Promedio: %.2f%n", promedio);  // 75.50
```

> ⚠️ **Error clásico:** dividir dos `int` sin hacer cast resulta en división entera. Si la suma es 453 y la cantidad es 6, `453 / 6 = 75` en lugar de `75.5`.

### Promedio con Streams (forma moderna en Java)

```java
List<Integer> notas = List.of(65, 80, 72, 90, 58, 88);

double promedio = notas.stream()
        .mapToInt(Integer::intValue)
        .average()
        .orElse(0.0);

System.out.printf("Promedio: %.2f%n", promedio);  // 75.50
```

---

## 2.6 Máximo, mínimo y rango

Derivados naturales de recorrer una colección:

```java
List<Integer> ventas = List.of(1200, 8500, 450, 3300, 9900, 670);

int maxVenta = ventas.get(0);  // se inicializa con el primer elemento
int minVenta = ventas.get(0);

for (int venta : ventas) {
    if (venta > maxVenta) maxVenta = venta;
    if (venta < minVenta) minVenta = venta;
}

int rango = maxVenta - minVenta;

System.out.println("Venta máxima:  $" + maxVenta);  // $9900
System.out.println("Venta mínima:  $" + minVenta);  // $450
System.out.println("Rango:         $" + rango);      // $9450
```

### Con Streams

```java
int max = ventas.stream().mapToInt(Integer::intValue).max().orElse(0);
int min = ventas.stream().mapToInt(Integer::intValue).min().orElse(0);
```

---

## 2.7 Tabla resumen de patrones

| Patrón | Variable inicial | Operación en bucle | Para qué sirve |
|--------|-----------------|-------------------|----------------|
| **Contador** | `0` | `count++` o `count--` | Cuántas veces ocurre algo |
| **Acumulador** | `0` o `0.0` | `total += valor` | Suma de valores variables |
| **Promedio** | `0` y `0` | `suma += val; count++` | Media de un conjunto |
| **Máximo** | `lista[0]` | `if (v > max) max = v` | Valor más alto |
| **Mínimo** | `lista[0]` | `if (v < min) min = v` | Valor más bajo |

---

## Resumen

- El **contador** suma 1 en cada evento → cuenta ocurrencias.
- El **acumulador** suma el valor actual → totaliza cantidades.
- El **promedio** = acumulador / contador → siempre castear a `double`.
- Inicializar siempre en `0` (o `lista[0]` para máx/mín).
- En Java moderno, `Stream.count()`, `.sum()`, `.average()`, `.max()` y `.min()` hacen el trabajo por ti.

→ [Siguiente módulo: Porcentajes, descuentos y cargos](./03_porcentajes_descuentos_y_cargos.md)

