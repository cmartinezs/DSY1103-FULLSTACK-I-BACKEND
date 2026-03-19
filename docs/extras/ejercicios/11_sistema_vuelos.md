# Ejercicio 11 — Sistema de gestión de vuelos

> **Nivel:** ⭐⭐⭐⭐ Medio-Avanzado  
> **Conceptos:** Genéricos `<T>` · `Map<K, V>` · `Enum` · Lógica proposicional compleja · Interfaces genéricas · Validaciones compuestas  
> **Tiempo estimado:** 70–90 min

---

## ✈️ Contexto

**AeroChile** es una aerolínea que necesita un sistema de gestión de vuelos. El sistema maneja distintos tipos de asientos (Economy, Business, First Class), cada uno con sus propias reglas de reserva, equipaje y precio. Tu tarea es implementar el sistema usando **genéricos** para que sea extensible a futuro.

---

## 📋 Enunciado

### Enum `ClaseVuelo`

```java
public enum ClaseVuelo {
    ECONOMY("Economy", 1.0),
    BUSINESS("Business", 2.5),
    FIRST_CLASS("Primera Clase", 4.0);

    private final String nombre;
    private final double multiplicadorPrecio;
    // constructor, getters
}
```

### Enum `EstadoVuelo`

```java
public enum EstadoVuelo { PROGRAMADO, ABORDANDO, EN_VUELO, ATERRIZADO, CANCELADO }
```

### Interface genérica `Reservable<T>`

```java
public interface Reservable<T> {
    boolean reservar(T asiento, String pasajero);
    boolean cancelar(T asiento);
    boolean estaDisponible(T asiento);
    List<T> getDisponibles();
}
```

### Clase `Asiento`

**Atributos:** `numero` (`String`, ej: `"12A"`), `clase` (`ClaseVuelo`), `disponible` (`boolean`), `pasajeroAsignado` (`String`).

Constructor, getters, `toString()`.

---

### Clase `Vuelo` (implementa `Reservable<Asiento>`)

**Atributos:**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `codigoVuelo` | `String` | ej: `"LA501"` |
| `origen` | `String` | Ciudad de origen |
| `destino` | `String` | Ciudad de destino |
| `precioBase` | `double` | Precio base del ticket Economy |
| `estado` | `EstadoVuelo` | Estado actual del vuelo |
| `asientos` | `Map<String, Asiento>` | Clave: número de asiento |

**Métodos:**

- `agregarAsiento(Asiento a)`: agrega al mapa.
- `reservar(Asiento asiento, String pasajero)` (de la interfaz):
  - **Lógica proposicional:**
    - **p:** `asiento != null`
    - **q:** el asiento existe en el vuelo (`asientos.containsKey(asiento.getNumero())`)
    - **r:** `asiento.isDisponible()`
    - **s:** el estado del vuelo es `PROGRAMADO` o `ABORDANDO`
    - Puede reservarse: `p ∧ q ∧ r ∧ s`
- `cancelar(Asiento asiento)`: libera el asiento. Solo si estado es `PROGRAMADO`.
- `calcularPrecio(Asiento asiento)`: `precioBase × clase.getMultiplicadorPrecio()`.
- `getAsientosPorClase(ClaseVuelo clase)`: retorna lista de asientos de esa clase.
- `getOcupacion()`: retorna porcentaje de asientos ocupados (0.0–100.0).
- `imprimirManifiesto()`: imprime todos los pasajeros por clase.

---

### Clase genérica `Repositorio<T, ID>`

```java
public class Repositorio<T, ID> {
    private final Map<ID, T> almacen = new HashMap<>();

    public void guardar(ID id, T entidad) { ... }
    public T buscar(ID id) { ... }           // retorna null si no existe
    public boolean existe(ID id) { ... }
    public List<T> listarTodos() { ... }
    public void eliminar(ID id) { ... }
    public int total() { ... }
}
```

### Clase `GestorVuelos`

Usa `Repositorio<Vuelo, String>` para almacenar vuelos y tiene métodos:
- `registrarVuelo(Vuelo v)`.
- `buscarVuelosDisponibles(String origen, String destino)`: retorna vuelos con asientos Economy disponibles.
- `imprimirResumen()`: lista todos los vuelos con su ocupación.

---

## 🚫 Restricciones

- `Reservable<T>` debe ser una interfaz genérica real (con `<T>`).
- `Repositorio<T, ID>` debe ser una clase genérica real; no puede contener lógica específica de `Vuelo`.
- El método `reservar` debe declarar explícitamente las proposiciones `p`, `q`, `r`, `s`.
- Los `Enum` deben tener atributos y métodos (no ser simples listas de constantes).
- No uses `null` como valor de retorno en `Repositorio.buscar`; puedes usar `Optional<T>` si llegaste al módulo 08.

---

## 📥 Ejemplos de entrada

```java
Vuelo v1 = new Vuelo("LA501", "Santiago", "Lima", 120000, EstadoVuelo.PROGRAMADO);
// Agregar asientos
v1.agregarAsiento(new Asiento("10A", ClaseVuelo.FIRST_CLASS));
v1.agregarAsiento(new Asiento("10B", ClaseVuelo.FIRST_CLASS));
v1.agregarAsiento(new Asiento("20A", ClaseVuelo.BUSINESS));
v1.agregarAsiento(new Asiento("30A", ClaseVuelo.ECONOMY));
v1.agregarAsiento(new Asiento("30B", ClaseVuelo.ECONOMY));
v1.agregarAsiento(new Asiento("30C", ClaseVuelo.ECONOMY));

Asiento a1 = v1.getAsientos().get("10A");
Asiento a2 = v1.getAsientos().get("30A");
Asiento a3 = v1.getAsientos().get("20A");

v1.reservar(a1, "María González");
v1.reservar(a2, "Pedro Soto");
v1.reservar(a2, "Juan Díaz");  // ya reservado, debe fallar

GestorVuelos gestor = new GestorVuelos();
gestor.registrarVuelo(v1);
gestor.imprimirResumen();
v1.imprimirManifiesto();

System.out.println("\nPrecio First Class: $" + v1.calcularPrecio(a1));
System.out.println("Precio Economy:     $" + v1.calcularPrecio(a2));
System.out.println("Ocupación:          " + v1.getOcupacion() + "%");
```

---

## 📤 Salidas esperadas

```
══════════════════════════════════════════
   GESTOR AEROCHILE — RESUMEN DE VUELOS
══════════════════════════════════════════
✈️  LA501 | Santiago → Lima
   Estado:    PROGRAMADO
   Asientos:  6 total | 2 ocupados | 4 disponibles
   Ocupación: 33.3%

══════════════════════════════════════════
   MANIFIESTO DE VUELO LA501
   Santiago → Lima
══════════════════════════════════════════
👑 Primera Clase
   10A — María González
   10B — (disponible)

💼 Business
   20A — (disponible)

🛫 Economy
   30A — Pedro Soto
   30B — (disponible)
   30C — (disponible)

❌ Reserva fallida: asiento 30A no disponible (ya asignado a Pedro Soto)

Precio First Class: $480.000
Precio Economy:     $120.000
Ocupación:          33.3%
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Clase genérica Repositorio</summary>

```java
public class Repositorio<T, ID> {
    private final Map<ID, T> almacen = new HashMap<>();

    public void guardar(ID id, T entidad) {
        almacen.put(id, entidad);
    }
    public T buscar(ID id) {
        return almacen.get(id);  // null si no existe
    }
    public List<T> listarTodos() {
        return new ArrayList<>(almacen.values());
    }
}
```
</details>

<details>
<summary>Pista 2 — Método reservar con proposiciones</summary>

```java
@Override
public boolean reservar(Asiento asiento, String pasajero) {
    boolean p = asiento != null;
    boolean q = p && asientos.containsKey(asiento.getNumero());
    boolean r = q && asiento.isDisponible();
    boolean s = estado == EstadoVuelo.PROGRAMADO || estado == EstadoVuelo.ABORDANDO;
    
    if (p && q && r && s) {
        asiento.asignar(pasajero);
        return true;
    }
    return false;
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Qué ventaja aporta `Repositorio<T, ID>` frente a tener un `Map` directamente en `GestorVuelos`?
2. ¿Por qué la proposición `q` depende de `p`? ¿Qué ocurriría si evaluaras `q` sin verificar `p` antes?
3. ¿Cómo modificarías `Repositorio` para que `buscar` retorne `Optional<T>` en lugar de `null`?

---

*[← Ejercicio anterior](./10_farmacia_online.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./12_plataforma_cursos.md)*

