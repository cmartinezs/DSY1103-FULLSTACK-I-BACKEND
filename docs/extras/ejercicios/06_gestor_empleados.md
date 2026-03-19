# Ejercicio 06 — Gestor de empleados

> **Nivel:** ⭐⭐ Básico-Medio  
> **Conceptos:** Herencia · `extends` · `super` · Sobrescritura (`@Override`) · Polimorfismo básico · Lógica proposicional en reglas de negocio  
> **Tiempo estimado:** 45–60 min

---

## 👥 Contexto

La empresa **ConstructoraAndes** tiene distintos tipos de empleados: algunos trabajan a tiempo completo con sueldo fijo, otros trabajan por horas y reciben pago variable. El área de RRHH necesita un sistema que **calcule el sueldo líquido** de cada empleado según su tipo y aplique los descuentos legales correspondientes.

---

## 📋 Enunciado

### Clase base abstracta `Empleado`

**Atributos protegidos:**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `rut` | `String` | RUT del empleado |
| `nombre` | `String` | Nombre completo |
| `cargo` | `String` | Cargo en la empresa |
| `activo` | `boolean` | Si está contratado actualmente |

**Constructor:** `Empleado(String rut, String nombre, String cargo)`  
**Métodos:**
- Getters para todos los atributos. Setter solo para `activo`.
- `calcularSueldoBruto()` → método **abstracto** que retorna `double`.
- `calcularDescuentos()` → calcula descuentos legales (AFP 10% + Salud 7% del sueldo bruto).
- `calcularSueldoLiquido()` → `sueldoBruto - descuentos`.
- `imprimirLiquidacion()` → imprime la liquidación de sueldo (método **final**, no se puede sobreescribir).
- `toString()` → resumen básico del empleado.

### Clase `EmpleadoTiempoCompleto` (extiende `Empleado`)

**Atributos adicionales:**
- `sueldoBase` (`double`): sueldo mensual fijo.
- `bonoDesempeno` (`double`): bono adicional (puede ser 0).

**Constructor:** `EmpleadoTiempoCompleto(String rut, String nombre, String cargo, double sueldoBase)`

**Regla de bono de desempeño:**  
El bono se asigna con `asignarBono(double porcentaje)` donde `porcentaje` es entre 0 y 100.  
El bono es `sueldoBase * porcentaje / 100`.

`calcularSueldoBruto()` → `sueldoBase + bonoDesempeno`.

### Clase `EmpleadoPorHoras` (extiende `Empleado`)

**Atributos adicionales:**
- `valorHora` (`double`): valor pagado por hora trabajada.
- `horasTrabajadas` (`int`): horas trabajadas en el mes.

**Constructor:** `EmpleadoPorHoras(String rut, String nombre, String cargo, double valorHora)`

**Regla de horas extra (lógica proposicional):**
- **p:** `horasTrabajadas > 45` (superó jornada normal)
- **q:** `horasTrabajadas <= 90` (dentro del máximo legal)
- Si `p ∧ q` → las horas sobre 45 se pagan al 150% del valor normal.
- Si `¬q` (más de 90 horas) → las horas sobre 90 se pagan al 200%, y las de 45–90 al 150%.
- Si `¬p` → sin horas extra.

`calcularSueldoBruto()` → aplica las reglas anteriores.

### Clase `GestorEmpleados` (clase principal)

Crea una lista de empleados de distintos tipos, los procesa con polimorfismo e imprime la liquidación de cada uno.

---

## 🚫 Restricciones

- `Empleado` debe ser clase abstracta (`abstract class`).
- `calcularSueldoBruto()` debe ser `abstract` en `Empleado`.
- `imprimirLiquidacion()` debe ser `final` (no se puede sobreescribir en subclases).
- Usa `@Override` en todos los métodos sobreescritos.
- El método `main` debe iterar sobre un arreglo o lista de tipo `Empleado[]` (polimorfismo).
- Los descuentos AFP (10%) y Salud (7%) son fijos para todos los tipos.

---

## 📥 Ejemplos de entrada

```java
EmpleadoTiempoCompleto e1 = new EmpleadoTiempoCompleto("11111111-1", "Ana Mora", "Jefe de Obra", 1_200_000);
e1.asignarBono(15);  // 15% de bono

EmpleadoPorHoras e2 = new EmpleadoPorHoras("22222222-2", "Pedro Soto", "Operario", 8_500);
e2.registrarHoras(52);  // 52 horas trabajadas

EmpleadoTiempoCompleto e3 = new EmpleadoTiempoCompleto("33333333-3", "Laura Vega", "Administrativa", 850_000);
// sin bono

EmpleadoPorHoras e4 = new EmpleadoPorHoras("44444444-4", "Marcos Silva", "Electricista", 12_000);
e4.registrarHoras(95);  // supera máximo legal

Empleado[] empleados = { e1, e2, e3, e4 };
for (Empleado e : empleados) {
    e.imprimirLiquidacion();
}
```

---

## 📤 Salidas esperadas

```
╔══════════════════════════════════════════╗
║          LIQUIDACIÓN DE SUELDO          ║
╠══════════════════════════════════════════╣
║ RUT:      11111111-1                    ║
║ Nombre:   Ana Mora                      ║
║ Cargo:    Jefe de Obra                  ║
║ Tipo:     Tiempo Completo               ║
╠══════════════════════════════════════════╣
║ Sueldo base:     $1.200.000             ║
║ Bono desempeño:  $180.000 (15%)         ║
║ Sueldo bruto:    $1.380.000             ║
║ AFP (10%):      -$138.000               ║
║ Salud (7%):     -$96.600                ║
║ Sueldo líquido:  $1.145.400             ║
╚══════════════════════════════════════════╝

╔══════════════════════════════════════════╗
║          LIQUIDACIÓN DE SUELDO          ║
╠══════════════════════════════════════════╣
║ RUT:      22222222-2                    ║
║ Nombre:   Pedro Soto                    ║
║ Cargo:    Operario                      ║
║ Tipo:     Por Horas                     ║
╠══════════════════════════════════════════╣
║ Horas normales:  45h × $8.500 = $382.500║
║ Horas extra:      7h × $12.750= $89.250 ║
║ Sueldo bruto:    $471.750               ║
║ AFP (10%):      -$47.175                ║
║ Salud (7%):     -$33.022               ║
║ Sueldo líquido:  $391.553               ║
╚══════════════════════════════════════════╝
```
> *(continúa para e3 y e4 con el mismo formato)*

---

## 💡 Pistas

<details>
<summary>Pista 1 — Clase abstracta</summary>

```java
public abstract class Empleado {
    // ...atributos protegidos
    public abstract double calcularSueldoBruto();

    public final void imprimirLiquidacion() {
        double bruto = calcularSueldoBruto();  // polimorfismo en acción
        double descuentos = calcularDescuentos();
        // ...imprimir
    }
}
```
</details>

<details>
<summary>Pista 2 — Horas extra con lógica proposicional</summary>

```java
boolean p = horasTrabajadas > 45;
boolean q = horasTrabajadas <= 90;

if (!p) {
    return valorHora * horasTrabajadas;
} else if (p && q) {
    double normal = 45 * valorHora;
    double extra = (horasTrabajadas - 45) * valorHora * 1.5;
    return normal + extra;
} else {
    // más de 90 horas
    double normal = 45 * valorHora;
    double extra = (90 - 45) * valorHora * 1.5;
    double superExtra = (horasTrabajadas - 90) * valorHora * 2.0;
    return normal + extra + superExtra;
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Por qué `imprimirLiquidacion()` es `final`? ¿Qué problema previene?
2. ¿Cómo funciona el polimorfismo cuando el bucle llama a `calcularSueldoBruto()` sobre un arreglo de tipo `Empleado`?
3. Si mañana se agrega un nuevo tipo `EmpleadoContrato`, ¿qué métodos **obligatoriamente** debe implementar? ¿Por qué?

---

*[← Ejercicio anterior](./05_sistema_parking.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./07_tienda_mascotas.md)*

