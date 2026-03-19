# Ejercicio 04 — Registro de estudiantes

> **Nivel:** ⭐⭐ Básico-Medio  
> **Conceptos:** Clases · Constructores · Getters y setters · Encapsulamiento básico · `toString()` · Lógica proposicional en validaciones  
> **Tiempo estimado:** 35–50 min

---

## 🎓 Contexto

Eres el desarrollador asignado a un instituto de educación superior llamado **InstitutoDuoc**. El departamento de admisiones necesita un sistema para **registrar y consultar información** de sus estudiantes. Tu primera tarea es modelar la clase `Estudiante` y crear un pequeño programa de prueba.

---

## 📋 Enunciado

Implementa las siguientes clases:

### Clase `Estudiante`

**Atributos (todos privados):**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `rut` | `String` | RUT del estudiante (formato: `12345678-9`) |
| `nombre` | `String` | Nombre completo |
| `carrera` | `String` | Nombre de la carrera |
| `anioIngreso` | `int` | Año de ingreso (entre 2000 y el año actual) |
| `notaPromedio` | `double` | Promedio de notas (entre 1.0 y 7.0) |
| `activo` | `boolean` | Si está matriculado actualmente |

**Constructor:** `Estudiante(String rut, String nombre, String carrera, int anioIngreso)`
- La nota promedio inicia en `1.0` y `activo` en `true`.
- Valida que `anioIngreso` sea entre 2000 y 2026; si no, lanza `IllegalArgumentException`.
- Valida que ningún campo de texto sea `null` o vacío.

**Métodos:**
- Getters para todos los atributos.
- Setter solo para `notaPromedio` (valida que esté entre 1.0 y 7.0).
- Setter solo para `activo`.
- `toString()`: retorna un resumen del estudiante.
- `estaEnRiesgo()`: retorna `true` si el promedio es menor a 4.0 **y** el estudiante está activo.
- `obtenerEstado()`: retorna `"En riesgo académico"`, `"Aprobado"`, `"Egresado"` o `"Inactivo"` según corresponda.

**Regla para `obtenerEstado()` (expresada con lógica proposicional):**
- **p:** `activo == true`
- **q:** `notaPromedio >= 4.0`
- **r:** `notaPromedio >= 6.0`
- Si `¬p` → `"Inactivo"`
- Si `p ∧ r` → `"Destacado"`
- Si `p ∧ q ∧ ¬r` → `"Aprobado"`
- Si `p ∧ ¬q` → `"En riesgo académico"`

### Clase `RegistroEstudiantes`

Clase principal (`main`) que crea **al menos 4 estudiantes** con distintos escenarios y llama a `toString()` y `obtenerEstado()` para cada uno.

---

## 🚫 Restricciones

- Todos los atributos deben ser `private`.
- El constructor debe validar los datos; ante cualquier error, lanza `IllegalArgumentException` con mensaje descriptivo.
- No uses `Scanner`; los datos se definen directamente en el `main`.
- El método `estaEnRiesgo()` debe usar explícitamente los operadores `&&` y `!`.
- `toString()` debe sobreescribir el método de `Object` (usar `@Override`).

---

## 📥 Ejemplos de entrada

```java
// En el main:
Estudiante e1 = new Estudiante("12345678-9", "Ana García", "Ingeniería en Software", 2022);
e1.setNotaPromedio(6.2);

Estudiante e2 = new Estudiante("98765432-1", "Carlos López", "Diseño Gráfico", 2023);
e2.setNotaPromedio(3.5);

Estudiante e3 = new Estudiante("11111111-1", "María Torres", "Administración", 2020);
e3.setActivo(false);
e3.setNotaPromedio(5.0);

Estudiante e4 = new Estudiante("22222222-2", "Juan Pérez", "Ingeniería en Software", 2024);
e4.setNotaPromedio(4.5);
```

---

## 📤 Salidas esperadas

```
========================================
RUT:          12345678-9
Nombre:       Ana García
Carrera:      Ingeniería en Software
Año ingreso:  2022
Promedio:     6.2
Estado:       Destacado
En riesgo:    No
========================================
RUT:          98765432-1
Nombre:       Carlos López
Carrera:      Diseño Gráfico
Año ingreso:  2023
Promedio:     3.5
Estado:       En riesgo académico
En riesgo:    Sí
========================================
RUT:          11111111-1
Nombre:       María Torres
Carrera:      Administración
Año ingreso:  2020
Promedio:     5.0
Estado:       Inactivo
En riesgo:    No
========================================
RUT:          22222222-2
Nombre:       Juan Pérez
Carrera:      Ingeniería en Software
Año ingreso:  2024
Promedio:     4.5
Estado:       Aprobado
En riesgo:    No
========================================
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Validación en constructor</summary>

```java
public Estudiante(String rut, String nombre, String carrera, int anioIngreso) {
    if (rut == null || rut.isBlank()) throw new IllegalArgumentException("El RUT no puede estar vacío");
    if (anioIngreso < 2000 || anioIngreso > 2026) 
        throw new IllegalArgumentException("Año de ingreso inválido: " + anioIngreso);
    // ...asignaciones
}
```
</details>

<details>
<summary>Pista 2 — Método obtenerEstado con proposiciones</summary>

```java
public String obtenerEstado() {
    boolean p = this.activo;
    boolean q = this.notaPromedio >= 4.0;
    boolean r = this.notaPromedio >= 6.0;

    if (!p) return "Inactivo";
    if (p && r) return "Destacado";
    if (p && q) return "Aprobado";
    return "En riesgo académico";
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Por qué los atributos deben ser `private` y no `public`?
2. ¿Qué pasaría si no validamos el `anioIngreso` en el constructor?
3. ¿Por qué el estado `"Inactivo"` tiene prioridad sobre los demás? Exprésalo con una tabla de verdad para `p`, `q` y `r`.

---

*[← Ejercicio anterior](./03_clasificador_temperatura.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./05_sistema_parking.md)*

