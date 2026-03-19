# Ejercicio 03 — Clasificador de temperatura ambiental

> **Nivel:** ⭐ Básico  
> **Conceptos:** `switch` expression · Rangos numéricos · Lógica proposicional (condicional `→`) · Constantes · Métodos estáticos  
> **Tiempo estimado:** 25–35 min

---

## 🌡️ Contexto

Formas parte del equipo de desarrollo de una empresa de **monitoreo ambiental** llamada **ClimaTech**. El sistema recibe mediciones de temperatura de distintos sensores instalados en almacenes de alimentos. Tu tarea es crear un **clasificador** que, dado el valor de temperatura, determine el estado del ambiente y emita una alerta si es necesario.

---

## 📋 Enunciado

Crea una clase `ClasificadorTemperatura` con los siguientes métodos:

1. **`clasificar(double temperatura)`** → retorna un `String` con la categoría:

| Rango (°C) | Categoría | Color de alerta |
|-----------|-----------|-----------------|
| Menor a -10 | `"CONGELAMIENTO EXTREMO"` | 🔵 Azul |
| -10 a 0 (inclusive) | `"CONGELADO"` | 🔵 Azul |
| 0 a 8 (inclusive) | `"REFRIGERADO"` | 🟢 Verde |
| 8 a 15 (inclusive) | `"FRESCO"` | 🟢 Verde |
| 15 a 25 (exclusive) | `"TEMPERATURA AMBIENTE"` | 🟡 Amarillo |
| 25 a 35 (inclusive) | `"CALUROSO"` | 🟠 Naranja |
| Mayor a 35 | `"CRÍTICO"` | 🔴 Rojo |

2. **`requiereAlerta(double temperatura, String tipoAlmacen)`** → retorna `boolean`.

**Regla lógica (lógica proposicional):**
- **p:** la temperatura está fuera del rango seguro para el tipo de almacén.
- **q:** el tipo de almacén es `"FARMACIA"` o `"ALIMENTOS"`.
- **r:** la temperatura es mayor a 30°C o menor a -5°C.
- **Condición de alerta:** `(p ∧ q) ∨ r`

**Rangos seguros por tipo de almacén:**

| Tipo | Rango seguro |
|------|-------------|
| `"FARMACIA"` | 2°C – 8°C |
| `"ALIMENTOS"` | 0°C – 6°C |
| `"ELECTRONICA"` | 15°C – 25°C |
| Otro | 10°C – 30°C |

3. **`generarReporte(double temperatura, String tipoAlmacen)`** → imprime el reporte completo.

---

## 🚫 Restricciones

- Usa `if/else if` para los rangos de temperatura (no se puede mapear con `switch` directamente).
- Declara los rangos como **constantes** (`static final double`).
- El método `requiereAlerta` debe declarar explícitamente las proposiciones `p`, `q`, `r` como variables `boolean`.
- No uses librerías externas.
- El método `clasificar` debe retornar la cadena exacta indicada en la tabla.

---

## 📥 Ejemplos de entrada

### Caso 1
```
temperatura   = 4.5
tipoAlmacen   = "FARMACIA"
```

### Caso 2
```
temperatura   = 12.0
tipoAlmacen   = "ALIMENTOS"
```

### Caso 3
```
temperatura   = 36.8
tipoAlmacen   = "ELECTRONICA"
```

### Caso 4
```
temperatura   = -3.0
tipoAlmacen   = "FARMACIA"
```

### Caso 5
```
temperatura   = 22.0
tipoAlmacen   = "ELECTRONICA"
```

---

## 📤 Salidas esperadas

### Caso 1
```
🌡️ Reporte de temperatura — Almacén: FARMACIA
Temperatura medida: 4.5°C
Clasificación:      REFRIGERADO 🟢
Rango seguro:       2.0°C – 8.0°C
Estado:             ✅ Dentro del rango seguro
Alerta activa:      No
```

### Caso 2
```
🌡️ Reporte de temperatura — Almacén: ALIMENTOS
Temperatura medida: 12.0°C
Clasificación:      FRESCO 🟢
Rango seguro:       0.0°C – 6.0°C
Estado:             ⚠️ Fuera del rango seguro
Alerta activa:      Sí — Verificar almacén de inmediato
```

### Caso 3
```
🌡️ Reporte de temperatura — Almacén: ELECTRONICA
Temperatura medida: 36.8°C
Clasificación:      CRÍTICO 🔴
Rango seguro:       15.0°C – 25.0°C
Estado:             ⚠️ Fuera del rango seguro
Alerta activa:      Sí — Verificar almacén de inmediato
```

### Caso 4
```
🌡️ Reporte de temperatura — Almacén: FARMACIA
Temperatura medida: -3.0°C
Clasificación:      CONGELADO 🔵
Rango seguro:       2.0°C – 8.0°C
Estado:             ⚠️ Fuera del rango seguro
Alerta activa:      Sí — Verificar almacén de inmediato
```

### Caso 5
```
🌡️ Reporte de temperatura — Almacén: ELECTRONICA
Temperatura medida: 22.0°C
Clasificación:      TEMPERATURA AMBIENTE 🟡
Rango seguro:       15.0°C – 25.0°C
Estado:             ✅ Dentro del rango seguro
Alerta activa:      No
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Constantes de rango</summary>

```java
static final double MIN_FARMACIA = 2.0;
static final double MAX_FARMACIA = 8.0;
// ...etc
```
</details>

<details>
<summary>Pista 2 — Método requiereAlerta</summary>

```java
static boolean requiereAlerta(double temp, String tipo) {
    boolean p = esFueraDeRango(temp, tipo);       // fuera del rango seguro
    boolean q = tipo.equals("FARMACIA") || tipo.equals("ALIMENTOS");
    boolean r = temp > 30 || temp < -5;
    return (p && q) || r;
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Cuál es la diferencia entre `→` (condicional) y `↔` (bicondicional) en lógica proposicional? ¿Alguno de ellos está implícito en este ejercicio?
2. ¿Por qué separar la lógica en métodos distintos mejora la legibilidad del código?
3. ¿Cómo aplicarías el principio **DRY** (Don't Repeat Yourself) al momento de verificar los rangos para distintos tipos de almacén?

---

*[← Ejercicio anterior](./02_calculadora_descuentos.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./04_registro_estudiantes.md)*

