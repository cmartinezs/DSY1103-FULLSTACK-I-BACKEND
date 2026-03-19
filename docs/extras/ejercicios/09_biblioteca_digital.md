# Ejercicio 09 — Biblioteca digital

> **Nivel:** ⭐⭐⭐ Medio  
> **Conceptos:** Clases abstractas · Herencia multi-nivel · `@Override` · Colecciones · `Map<K,V>` · Lógica proposicional en disponibilidad  
> **Tiempo estimado:** 60–80 min

---

## 📚 Contexto

La **Biblioteca Nacional Digital** está construyendo su nuevo sistema de préstamos. El catálogo tiene distintos tipos de recursos: libros físicos, libros digitales, revistas y audiolibros. Cada uno tiene sus propias reglas de préstamo y devolución. Tu tarea es modelar este sistema con herencia y abstracción.

---

## 📋 Enunciado

### Clase abstracta `RecursoBiblioteca`

**Atributos protegidos:**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `isbn` | `String` | Identificador único |
| `titulo` | `String` | Título del recurso |
| `autor` | `String` | Autor o autores |
| `anioPublicacion` | `int` | Año de publicación |
| `disponible` | `boolean` | Si puede prestarse ahora |

**Métodos abstractos:**
- `getTipo()` → `String` (ej: `"Libro Físico"`, `"Libro Digital"`)
- `getDiasPrestamo()` → `int` (días máximos de préstamo permitidos)
- `getDescripcionFormato()` → `String` (describe el formato del recurso)

**Métodos concretos:**
- `prestar()`: marca como no disponible. Lanza `IllegalStateException` si ya está prestado.
- `devolver()`: marca como disponible.
- `toString()`: resumen del recurso.
- `estaDisponible()`: getter de `disponible`.

---

### Clase `LibroFisico` (extiende `RecursoBiblioteca`)

**Atributos adicionales:**
- `numeroPaginas` (`int`)
- `ubicacionEstante` (`String`) ej: `"A-12-3"`

`getDiasPrestamo()` → 14 días.  
`getDescripcionFormato()` → `"Libro impreso, %d páginas, Estante: %s"`.

---

### Clase `LibroDigital` (extiende `RecursoBiblioteca`)

**Atributos adicionales:**
- `formatoArchivo` (`String`): `"PDF"`, `"EPUB"` o `"MOBI"`.
- `tamañoMB` (`double`)

`getDiasPrestamo()` → 30 días (digital, no se deteriora).  
`getDescripcionFormato()` → `"E-book en formato %s, %.1f MB"`.  
**Importante:** un `LibroDigital` **siempre está disponible** (puede prestarse a múltiples usuarios). `prestar()` no cambia el estado.

---

### Clase `Revista` (extiende `RecursoBiblioteca`)

**Atributos adicionales:**
- `numeroEdicion` (`int`)
- `mes` (`String`): mes de publicación.

`getDiasPrestamo()` → 7 días.  
`getDescripcionFormato()` → `"Revista, Edición %d, %s %d"`.

---

### Clase `Audiolibro` (extiende `LibroDigital`)

**Atributos adicionales:**
- `duracionMinutos` (`int`)
- `narrador` (`String`)

`getTipo()` → `"Audiolibro"`.  
`getDiasPrestamo()` → 21 días.  
`getDescripcionFormato()` → `"Audio, %d min, narrado por %s"`.

---

### Clase `SistemaBiblioteca`

**Atributos:**
- `Map<String, RecursoBiblioteca> catalogo` (clave: ISBN).
- `Map<String, String> prestamosActivos` (clave: ISBN, valor: RUT del lector).

**Métodos:**
- `registrar(RecursoBiblioteca r)`: agrega al catálogo.
- `prestar(String isbn, String rutLector)`: realiza el préstamo.
  - **Lógica proposicional:**
    - **p:** el recurso existe en el catálogo.
    - **q:** el recurso está disponible (`estaDisponible()`).
    - **r:** el lector no tiene más de 3 préstamos activos.
    - Puede prestarse: `p ∧ (q ∨ esDigital) ∧ r` (donde `esDigital` identifica si es `LibroDigital`).
- `devolver(String isbn)`: procesa la devolución.
- `buscarPorAutor(String autor)`: retorna lista de recursos.
- `imprimirCatalogo()`: imprime todos los recursos agrupados por tipo.
- `imprimirPrestamosActivos()`: lista los préstamos en curso.

---

## 🚫 Restricciones

- `RecursoBiblioteca` debe ser `abstract`.
- `LibroDigital.prestar()` debe sobreescribir el método padre y **no** cambiar `disponible`.
- Usa `Map<String, RecursoBiblioteca>` para el catálogo (no `List`).
- Cuenta los préstamos activos por lector iterando el `Map` de préstamos.
- En `prestar`, declara explícitamente las proposiciones `p`, `q`, `r` como `boolean`.

---

## 📥 Ejemplos de entrada

```java
SistemaBiblioteca sistema = new SistemaBiblioteca();

sistema.registrar(new LibroFisico("ISBN-001", "Clean Code", "Robert C. Martin", 2008, 431, "B-03-1"));
sistema.registrar(new LibroFisico("ISBN-002", "El Principito", "Antoine de Saint-Exupéry", 1943, 96, "A-01-5"));
sistema.registrar(new LibroDigital("ISBN-003", "Spring Boot in Action", "Craig Walls", 2016, "PDF", 12.4));
sistema.registrar(new Revista("ISBN-004", "National Geographic", "Varios", 2024, 312, "Marzo"));
sistema.registrar(new Audiolibro("ISBN-005", "Sapiens", "Yuval Noah Harari", 2011, "PDF", 8.5, 1483, "John Lee"));

sistema.imprimirCatalogo();

sistema.prestar("ISBN-001", "12345678-9");
sistema.prestar("ISBN-003", "12345678-9");  // digital, siempre disponible
sistema.prestar("ISBN-003", "98765432-1");  // mismo digital, otro lector
sistema.prestar("ISBN-001", "11111111-1");  // ya prestado, debe fallar

sistema.imprimirPrestamosActivos();

sistema.devolver("ISBN-001");
sistema.prestar("ISBN-001", "11111111-1");  // ahora sí
```

---

## 📤 Salidas esperadas

```
══════════════════════════════════════════════
       CATÁLOGO — BIBLIOTECA NACIONAL DIGITAL
══════════════════════════════════════════════
📖 Libros Físicos
  [ISBN-001] Clean Code — Robert C. Martin (2008)
             Libro impreso, 431 páginas, Estante: B-03-1
             Disponible: ✅ | Préstamo máx: 14 días

  [ISBN-002] El Principito — Antoine de Saint-Exupéry (1943)
             Libro impreso, 96 páginas, Estante: A-01-5
             Disponible: ✅ | Préstamo máx: 14 días

💻 Libros Digitales
  [ISBN-003] Spring Boot in Action — Craig Walls (2016)
             E-book en formato PDF, 12.4 MB
             Disponible: ✅ (siempre) | Préstamo máx: 30 días

📰 Revistas
  [ISBN-004] National Geographic — Varios (2024)
             Revista, Edición 312, Marzo 2024
             Disponible: ✅ | Préstamo máx: 7 días

🎧 Audiolibros
  [ISBN-005] Sapiens — Yuval Noah Harari (2011)
             Audio, 1483 min, narrado por John Lee
             Disponible: ✅ (siempre) | Préstamo máx: 21 días

✅ Préstamo registrado: ISBN-001 → lector 12345678-9
✅ Préstamo registrado: ISBN-003 → lector 12345678-9 (digital)
✅ Préstamo registrado: ISBN-003 → lector 98765432-1 (digital)
❌ No se pudo prestar ISBN-001: recurso no disponible.

══ Préstamos activos ══
  ISBN-001 → 12345678-9 (Clean Code)
  ISBN-003 → 12345678-9 (Spring Boot in Action)
  ISBN-003 → 98765432-1 (Spring Boot in Action)

✅ Devolución registrada: ISBN-001
✅ Préstamo registrado: ISBN-001 → lector 11111111-1
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Detectar si es digital</summary>

```java
boolean esDigital = catalogo.get(isbn) instanceof LibroDigital;
```
</details>

<details>
<summary>Pista 2 — Contar préstamos activos del lector</summary>

```java
long prestamosDelLector = prestamosActivos.values().stream()
    .filter(rut -> rut.equals(rutLector))
    .count();
boolean r = prestamosDelLector < 3;
```
> Puedes usar un bucle `for` sin Stream si aún no llegaste a ese módulo.
</details>

---

## 🧠 Reflexión final

1. ¿Por qué `Audiolibro` extiende `LibroDigital` y no directamente `RecursoBiblioteca`?
2. ¿Qué patrón de diseño refleja el hecho de que `prestar()` en `LibroDigital` sobreescribe sin cambiar el estado?
3. Expresa la condición de préstamo `p ∧ (q ∨ esDigital) ∧ r` con una tabla de verdad para los casos más relevantes.

---

*[← Ejercicio anterior](./08_reservas_restaurante.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./10_farmacia_online.md)*

