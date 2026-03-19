# Ejercicio 12 — Plataforma de cursos online

> **Nivel:** ⭐⭐⭐⭐ Medio-Avanzado  
> **Conceptos:** Lambdas · `Stream` · `filter` · `map` · `sorted` · `collect` · `Comparator` · `Optional` · Lógica proposicional en predicados  
> **Tiempo estimado:** 70–90 min

---

## 💻 Contexto

**EduTech** es una plataforma de cursos online al estilo Udemy. Tienen miles de cursos con instructores, categorías y calificaciones. El equipo de analítica necesita generar distintos reportes del catálogo. Tu tarea es implementar las **consultas usando Streams y lambdas**.

---

## 📋 Enunciado

### Clase `Curso` (usa `record`)

```java
public record Curso(
    String id,
    String titulo,
    String instructor,
    String categoria,
    double precio,
    double calificacion,   // 0.0 a 5.0
    int totalEstudiantes,
    int duracionHoras,
    boolean esPremium,
    boolean disponible
) {}
```

---

### Clase `AnaliticaCursos`

Recibe en el constructor una `List<Curso>` y expone los siguientes métodos. **Todos deben usar Streams y lambdas** (sin bucles `for`).

#### Métodos de filtrado:

1. **`getCursosGratuitos()`** → `List<Curso>`: cursos con `precio == 0`.

2. **`getCursosPorCategoria(String categoria)`** → `List<Curso>`.

3. **`getCursosBienCalificados(double minCalificacion)`** → `List<Curso>`: calificación >= minCalificacion y disponible.  
   **Proposición lógica (como `Predicate`):**  
   `p: calificacion >= minCalificacion` ∧ `q: disponible == true`

4. **`getCursosRecomendados(String categoria, boolean soloPremium)`** → `List<Curso>`:  
   Filtra por categoría, calificación >= 4.0, disponible, y si `soloPremium` entonces `esPremium == true`.  
   Ordena por calificación descendente.

#### Métodos de transformación:

5. **`getTitulosCursos()`** → `List<String>`: solo los títulos de todos los cursos.

6. **`getInstructoresUnicos()`** → `List<String>`: nombres de instructores únicos, ordenados alfabéticamente.

7. **`getTitulosConPrecio()`** → `List<String>`: `"Título — $precio"` para cursos con precio > 0.

#### Métodos de agregación:

8. **`getCalificacionPromedio()`** → `double`: promedio de calificaciones de todos los cursos disponibles.

9. **`getTotalEstudiantes()`** → `long`: suma de todos los estudiantes.

10. **`getCursoMasPopular()`** → `Optional<Curso>`: el de mayor cantidad de estudiantes.

11. **`getCursoMejorCalificado()`** → `Optional<Curso>`: el de mayor calificación disponible.

#### Métodos de agrupación:

12. **`getCursosPorCategoriaAgrupados()`** → `Map<String, List<Curso>>`: agrupa por categoría.

13. **`getResumenPorCategoria()`** → `Map<String, Long>`: cantidad de cursos por categoría.

14. **`imprimirReporte()`**: imprime un reporte general usando los métodos anteriores.

---

## 🚫 Restricciones

- **Todos los métodos 1–13 deben usar `stream()`**; no se permite `for`, `while` ni `forEach` con lógica de negocio.
- El `record Curso` no puede tener métodos adicionales.
- Los predicados complejos deben usar variables `Predicate<Curso>` con nombres descriptivos.
- `imprimirReporte()` puede usar `forEach` para imprimir.
- Usa `Collectors.groupingBy` para los métodos de agrupación.

---

## 📥 Ejemplos de entrada

```java
List<Curso> cursos = List.of(
    new Curso("C001", "Java para principiantes", "Ana López", "Programación", 0, 4.7, 15000, 12, false, true),
    new Curso("C002", "Spring Boot avanzado",    "Carlos Mora", "Programación", 49990, 4.9, 8500, 30, true, true),
    new Curso("C003", "Diseño UI con Figma",      "María Soto", "Diseño", 29990, 4.3, 5200, 8, false, true),
    new Curso("C004", "Machine Learning con Python","Carlos Mora","Datos", 79990, 4.8, 12000, 40, true, true),
    new Curso("C005", "Excel básico",              "Juan Vera", "Ofimática", 0, 3.9, 22000, 5, false, true),
    new Curso("C006", "React y Next.js",           "Ana López", "Programación", 39990, 4.6, 7800, 20, false, true),
    new Curso("C007", "Python desde cero",         "Luis Pino", "Programación", 0, 4.5, 18000, 15, false, false),
    new Curso("C008", "Data Science con R",        "María Soto", "Datos", 59990, 4.1, 3100, 35, true, true),
    new Curso("C009", "Adobe Photoshop pro",       "Juan Vera", "Diseño", 34990, 4.4, 6400, 14, true, true),
    new Curso("C010", "Kubernetes y Docker",       "Carlos Mora", "DevOps", 69990, 4.7, 4200, 25, true, true)
);

AnaliticaCursos analitica = new AnaliticaCursos(cursos);
analitica.imprimirReporte();
```

---

## 📤 Salidas esperadas

```
══════════════════════════════════════════════════
           REPORTE EDUTECH — CATÁLOGO
══════════════════════════════════════════════════

📊 RESUMEN GENERAL
  Total de cursos:       10
  Cursos disponibles:    9
  Total de estudiantes:  102.200
  Calificación promedio: 4.49 ★
  Curso más popular:     Excel básico (22.000 estudiantes)
  Mejor calificado:      Spring Boot avanzado (4.9 ★)

🆓 CURSOS GRATUITOS (3)
  · Java para principiantes — Ana López (4.7 ★, 15.000 est.)
  · Excel básico            — Juan Vera (3.9 ★, 22.000 est.)
  · Python desde cero       — Luis Pino (4.5 ★) ⚠️ No disponible

⭐ CURSOS BIEN CALIFICADOS >= 4.5 y disponibles (6)
  · Spring Boot avanzado   4.9 ★
  · Machine Learning       4.8 ★
  · Java para principiantes 4.7 ★
  · Kubernetes y Docker    4.7 ★
  · React y Next.js        4.6 ★
  · Python desde cero      4.5 ★ ⚠️ No disponible

🏫 CURSOS POR CATEGORÍA
  Programación : 4 cursos
  Datos        : 2 cursos
  Diseño       : 2 cursos
  Ofimática    : 1 curso
  DevOps       : 1 curso

👨‍🏫 INSTRUCTORES ÚNICOS
  Ana López, Carlos Mora, Juan Vera, Luis Pino, María Soto

💎 RECOMENDADOS EN "Programación" (Premium) (1)
  · Spring Boot avanzado — Carlos Mora (4.9 ★)
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Predicados nombrados</summary>

```java
Predicate<Curso> disponibleYBienCalificado = c ->
    c.disponible() && c.calificacion() >= minCalificacion;

return cursos.stream()
    .filter(disponibleYBienCalificado)
    .sorted(Comparator.comparingDouble(Curso::calificacion).reversed())
    .collect(Collectors.toList());
```
</details>

<details>
<summary>Pista 2 — Agrupación por categoría</summary>

```java
public Map<String, List<Curso>> getCursosPorCategoriaAgrupados() {
    return cursos.stream()
        .collect(Collectors.groupingBy(Curso::categoria));
}

public Map<String, Long> getResumenPorCategoria() {
    return cursos.stream()
        .collect(Collectors.groupingBy(Curso::categoria, Collectors.counting()));
}
```
</details>

<details>
<summary>Pista 3 — Curso más popular con Optional</summary>

```java
public Optional<Curso> getCursoMasPopular() {
    return cursos.stream()
        .max(Comparator.comparingInt(Curso::totalEstudiantes));
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Qué diferencia hay entre `map()` y `filter()` en un Stream?
2. ¿Por qué `getCursoMasPopular()` retorna `Optional<Curso>` y no directamente `Curso`?
3. Expresa el predicado de `getCursosRecomendados` como una fórmula de lógica proposicional con 4 variables (`p`, `q`, `r`, `s`).

---

*[← Ejercicio anterior](./11_sistema_vuelos.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./13_ecommerce_tecnologia.md)*

