# Ejercicio 08 — Empleado y gerente

> **Nivel:** ⭐⭐⭐⭐ Medio-Avanzado · Nivel 8  
> **Conceptos:** Encapsulamiento · Herencia · `extends` · `super` · Sobreescritura de métodos  
> **Tiempo estimado:** ≤ 10 minutos

---

## 🏢 Contexto

El departamento de recursos humanos de una empresa de software necesita un sistema básico para representar a sus empleados. Existen dos tipos: empleados normales y gerentes. Un gerente **es un** empleado, pero con un bono adicional y la responsabilidad de liderar un equipo.

---

## 📋 Enunciado

Crea la clase `Ejercicio08EmpleadoGerente` dentro del package `cl.duoc.diagnostico.minombredeusuario`.

Este archivo debe contener **tres clases** (todas en el mismo archivo `.java`):

### Clase `Empleado`
- **Atributos privados:** `nombre` (`String`), `rut` (`String`), `salarioBase` (`double`).
- **Constructor** que inicialice los tres atributos.
- **Getters y setters** para todos los atributos.
- **Método `calcularSalario()`** que retorna el `salarioBase`.
- **Método `mostrarInfo()`** que imprime:
  ```
  Empleado: Juan Pérez | RUT: 12.345.678-9 | Salario: $850000.0
  ```

### Clase `Gerente` (extiende `Empleado`)
- **Atributo privado:** `bono` (`double`), `equipo` (`String`).
- **Constructor** que llame a `super(...)` y reciba también `bono` y `equipo`.
- **Sobreescribe `calcularSalario()`** retornando `salarioBase + bono`.
- **Sobreescribe `mostrarInfo()`** imprimiendo:
  ```
  Gerente: Ana Torres | RUT: 98.765.432-1 | Salario: $1700000.0 (incluye bono $200000.0) | Equipo: Backend
  ```

### Clase `Ejercicio08EmpleadoGerente` (contiene `main`)
- Crea **un** objeto `Empleado` y **un** objeto `Gerente`.
- Llama a `mostrarInfo()` de cada uno.
- Llama a `calcularSalario()` e imprime el resultado para ambos.
- Muestra la diferencia salarial entre el gerente y el empleado:
  ```
  Diferencia salarial: $850000.0
  ```

---

## 🚫 Restricciones

- Las tres clases deben estar en el mismo archivo `Ejercicio08EmpleadoGerente.java`.
- Solo la clase `Ejercicio08EmpleadoGerente` puede ser `public`.
- Usa `private` en todos los atributos.
- Usa `@Override` al sobreescribir métodos.
- No uses `Scanner`.

---

## 🧠 ¿Cómo lo resolverías?

> ✏️ **Escribe aquí, con tus propias palabras:**
> - ¿Qué significa que `Gerente` extiende `Empleado`?
> - ¿Para qué sirve `super(...)` en el constructor del `Gerente`?
> - ¿Qué es encapsulamiento y cómo lo aplicas con `private` y getters/setters?
> - ¿Por qué se usa `@Override` al sobreescribir `calcularSalario()`?

_(Completa esta sección antes de escribir código)_

---

## 💡 Pista

Recuerda que `super(...)` llama al constructor de la clase padre:

```java
public Gerente(String nombre, String rut, double salarioBase, double bono, String equipo) {
    super(nombre, rut, salarioBase);
    this.bono = bono;
    this.equipo = equipo;
}
```

Y al sobreescribir `calcularSalario()`, puedes llamar al método del padre con `super.calcularSalario()`:

```java
@Override
public double calcularSalario() {
    return super.calcularSalario() + bono;
}
```

---

## 📐 Estructura esperada

```java
package cl.duoc.diagnostico.minombredeusuario;

class Empleado {
    private String nombre;
    private String rut;
    private double salarioBase;

    // constructor, getters, setters
    public double calcularSalario() { ... }
    public void mostrarInfo() { ... }
}

class Gerente extends Empleado {
    private double bono;
    private String equipo;

    // constructor con super
    @Override
    public double calcularSalario() { ... }
    @Override
    public void mostrarInfo() { ... }
}

public class Ejercicio08EmpleadoGerente {
    public static void main(String[] args) {
        // crear Empleado y Gerente
        // llamar mostrarInfo() y calcularSalario()
        // mostrar diferencia salarial
    }
}
```

