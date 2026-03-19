# Ejercicio 05 — Sistema de parking

> **Nivel:** ⭐⭐ Básico-Medio  
> **Conceptos:** Encapsulamiento · Estado interno · Métodos con lógica de negocio · `this` · Lógica proposicional (bicondicional)  
> **Tiempo estimado:** 40–55 min

---

## 🚗 Contexto

Una empresa administradora de estacionamientos llamada **ParkSmart** necesita digitalizar su operación. Actualmente llevan el control en papel: registran cuándo entra un vehículo, cuándo sale y cuánto cobra. Tu misión es implementar el **sistema de control de un estacionamiento** con lógica de negocio real.

---

## 📋 Enunciado

### Clase `Vehiculo`

**Atributos (todos privados):**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `patente` | `String` | Patente del vehículo (ej: `"ABCD12"`) |
| `tipo` | `String` | `"AUTO"`, `"MOTO"` o `"CAMIONETA"` |
| `horaEntrada` | `int` | Hora de entrada (0–23) |

Constructor: `Vehiculo(String patente, String tipo, int horaEntrada)`  
Getters para todos los atributos. Sin setters (inmutable una vez creado).

---

### Clase `Estacionamiento`

**Atributos privados:**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `nombre` | `String` | Nombre del estacionamiento |
| `capacidadMaxima` | `int` | Máximo de vehículos que puede albergar |
| `vehiculosActuales` | `int` | Vehículos actualmente dentro |
| `totalRecaudado` | `double` | Total acumulado de cobros |
| `abierto` | `boolean` | Si el estacionamiento está operativo |

**Tarifas (constantes estáticas):**

| Tipo | Tarifa por hora |
|------|----------------|
| `"AUTO"` | $1.500 |
| `"MOTO"` | $800 |
| `"CAMIONETA"` | $2.000 |

**Métodos:**

- `ingresar(Vehiculo v)`: registra el ingreso del vehículo.  
  **Condición lógica:** un vehículo puede ingresar **si y solo si** (`↔`) el estacionamiento está abierto **Y** hay espacio disponible.  
  Lanza `IllegalStateException` si no puede ingresar (con mensaje descriptivo).

- `salir(Vehiculo v, int horaSalida)`: calcula el cobro, descuenta el vehículo y acumula recaudación.  
  **Regla:** si `horaSalida < horaEntrada`, asume que el vehículo estuvo hasta medianoche y continuó al día siguiente.  
  Lanza `IllegalArgumentException` si `horaSalida` no está en rango 0–23.

- `calcularCobro(Vehiculo v, int horaSalida)`: retorna el monto a cobrar (`double`). Si la estadía es menor a 1 hora, se cobra mínimo 1 hora.

- `estaLleno()`: retorna `boolean`.

- `getDisponibles()`: retorna cuántos espacios libres hay.

- `imprimirEstado()`: imprime el estado actual del estacionamiento.

---

## 🚫 Restricciones

- Todos los atributos deben ser `private`.
- Las tarifas deben ser `private static final double`.
- El método `ingresar` debe validar la condición lógica **explícitamente** con variables `boolean`:  
  `boolean puedeEntrar = estaAbierto && hayEspacio;`
- No uses arreglos para guardar los vehículos; solo lleva el conteo numérico.
- El método `calcularCobro` no debe modificar el estado del objeto (debe ser puro).

---

## 📥 Ejemplos de entrada

```java
Estacionamiento park = new Estacionamiento("ParkSmart Centro", 3);

Vehiculo v1 = new Vehiculo("ABCD12", "AUTO", 9);
Vehiculo v2 = new Vehiculo("XY5678", "MOTO", 10);
Vehiculo v3 = new Vehiculo("ZZ9900", "CAMIONETA", 8);
Vehiculo v4 = new Vehiculo("KK1111", "AUTO", 11);  // el 4to debería fallar

park.ingresar(v1);
park.ingresar(v2);
park.ingresar(v3);
park.imprimirEstado();

park.salir(v1, 12);   // 3 horas
park.salir(v2, 14);   // 4 horas
park.imprimirEstado();

park.ingresar(v4);    // ahora sí hay espacio
park.salir(v3, 18);   // 10 horas
park.salir(v4, 13);   // 2 horas
park.imprimirEstado();
```

---

## 📤 Salidas esperadas

```
=== ParkSmart Centro ===
Capacidad:    3 espacios
Ocupados:     3
Disponibles:  0
Recaudado:    $0
Estado:       🔴 LLENO

🚗 ABCD12 (AUTO)    — salida 12:00 — estadía: 3h — cobro: $4.500
🏍️ XY5678 (MOTO)   — salida 14:00 — estadía: 4h — cobro: $3.200
=== ParkSmart Centro ===
Capacidad:    3 espacios
Ocupados:     1
Disponibles:  2
Recaudado:    $7.700
Estado:       🟢 CON ESPACIO

🚗 KK1111 (AUTO)     ingresó a las 11:00
🚚 ZZ9900 (CAMIONETA)— salida 18:00 — estadía: 10h — cobro: $20.000
🚗 KK1111 (AUTO)    — salida 13:00 — estadía: 2h  — cobro: $3.000
=== ParkSmart Centro ===
Capacidad:    3 espacios
Ocupados:     0
Disponibles:  3
Recaudado:    $30.700
Estado:       🟢 CON ESPACIO
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Calcular horas de estadía</summary>

```java
int horas = horaSalida - v.getHoraEntrada();
if (horas <= 0) horas += 24;   // cruzó la medianoche o misma hora (mínimo 1h)
if (horas == 0) horas = 1;
```
</details>

<details>
<summary>Pista 2 — Bicondicional en ingresar</summary>

La condición `p ↔ q` equivale a `(p && q) || (!p && !q)`. Aquí el "si y solo si" es que **ambas condiciones deben ser verdaderas** simultáneamente para que la entrada sea posible.
```java
boolean estaAbierto = this.abierto;
boolean hayEspacio = this.vehiculosActuales < this.capacidadMaxima;
boolean puedeEntrar = estaAbierto && hayEspacio;
```
</details>

---

## 🧠 Reflexión final

1. ¿Por qué `calcularCobro` debería ser un método "puro" (sin efectos secundarios)?
2. ¿Qué problema habría si `vehiculosActuales` fuera `public`?
3. La condición de ingreso usa `&&`: ¿cómo se expresaría con un bicondicional completo si quisiéramos que el estacionamiento estuviera cerrado cuando está lleno también?

---

*[← Ejercicio anterior](./04_registro_estudiantes.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./06_gestor_empleados.md)*

