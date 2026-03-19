# Ejercicio 14 — Red social universitaria

> **Nivel:** ⭐⭐⭐⭐⭐ Avanzado  
> **Conceptos:** `record` · `sealed` classes · Pattern matching (`instanceof`, `switch`) · `Optional` · Streams · Lógica proposicional compleja · Diseño POO integrado  
> **Tiempo estimado:** 90–120 min

---

## 🎓 Contexto

**CampusConnect** es la red social interna de una universidad. Permite que estudiantes, docentes y personal administrativo compartan publicaciones, interactúen con contenido y reciban notificaciones. Tu tarea es modelar el sistema usando las **características modernas de Java 21**.

---

## 📋 Enunciado

### Sealed class `Usuario`

```java
public sealed class Usuario permits Estudiante, Docente, Administrativo {
    private final String id;
    private final String nombre;
    private final String email;
    private final boolean activo;

    // constructor, getters
    public abstract String getRol();
    public abstract boolean puedePublicar();
    public abstract boolean puedeModerar();
}
```

### Subclases permitidas

```java
public final class Estudiante extends Usuario {
    private final String carrera;
    private final int semestre;

    @Override public String getRol() { return "Estudiante"; }
    // puedePublicar: true si activo y semestre >= 1
    // puedeModerar: siempre false
}

public final class Docente extends Usuario {
    private final String departamento;
    private final boolean esJefeDepto;

    @Override public String getRol() { return "Docente"; }
    // puedePublicar: true si activo
    // puedeModerar: true si activo && esJefeDepto
}

public final class Administrativo extends Usuario {
    private final String unidad;

    @Override public String getRol() { return "Administrativo"; }
    // puedePublicar: true si activo
    // puedeModerar: true si activo
}
```

---

### Sealed class `Publicacion`

```java
public sealed class Publicacion permits Texto, Imagen, Encuesta, Evento {
    protected final String id;
    protected final Usuario autor;
    protected final String contenido;
    protected int likes;
    protected final List<String> comentarios;

    // constructor, getters
    public abstract String getTipo();
    public abstract String getResumen();
}
```

```java
public final class Texto extends Publicacion {
    // getTipo() → "📝 Texto"
    // getResumen() → primeras 50 chars del contenido
}

public final class Imagen extends Publicacion {
    private final String urlImagen;
    private final String descripcionAlt;
    // getTipo() → "📷 Imagen"
    // getResumen() → "[Imagen] " + descripcionAlt
}

public final class Encuesta extends Publicacion {
    private final List<String> opciones;
    private final Map<String, Integer> votos;  // opción → cantidad de votos
    // getTipo() → "📊 Encuesta"
    // votar(String opcion): agrega un voto a la opción
    // getResumen() → "Encuesta: " + contenido + " (" + opciones.size() + " opciones)"
    // getResultados(): retorna la opción más votada
}

public final class Evento extends Publicacion {
    private final String lugar;
    private final String fecha;   // "dd/MM/yyyy HH:mm"
    private final int capacidadMaxima;
    private final List<String> inscritos;
    // getTipo() → "📅 Evento"
    // inscribir(String usuarioId): agrega si hay cupo
    // getResumen() → "Evento: " + contenido + " — " + lugar + " — " + fecha
}
```

---

### Clase `FeedCampusConnect`

**Atributos:**
- `List<Publicacion> publicaciones`
- `List<Usuario> usuarios`

**Métodos con pattern matching:**

- `procesarPublicacion(Publicacion pub)`:  
  Usa `switch` con pattern matching para imprimir información según el tipo:
  ```java
  String info = switch (pub) {
      case Texto t    -> "Texto de " + t.getAutor().getNombre() + ": " + t.getResumen();
      case Imagen img -> "Imagen de " + img.getAutor().getNombre() + ": " + img.getResumen();
      case Encuesta e -> "Encuesta: " + e.getResumen();
      case Evento ev  -> "Evento: " + ev.getResumen() + " (cupo: " + ev.getCapacidadMaxima() + ")";
  };
  ```

- `publicar(Usuario usuario, Publicacion pub)`:  
  **Lógica proposicional:**  
  - **p:** `usuario.puedePublicar()`  
  - **q:** `usuario.isActivo()`  
  - **r:** `pub != null`  
  - Puede publicar: `p ∧ q ∧ r`  
  Lanza `IllegalStateException` si no puede publicar.

- `moderar(Usuario moderador, String idPublicacion)`:  
  - **Proposición:** solo puede eliminar si `moderador.puedeModerar()`.

- `getPublicacionesPorAutor(String autorId)` → `List<Publicacion>` (Stream).

- `getPublicacionesConMasLikes(int n)` → `List<Publicacion>` (Stream, ordenadas desc).

- `getTendencias()` → `Map<String, Long>`: cantidad de publicaciones por tipo.

- `getEventosConCupo()` → `List<Evento>`: eventos con inscritos < capacidadMaxima (Stream + pattern matching con `instanceof`).

- `getUsuarioPorId(String id)` → `Optional<Usuario>`.

- `imprimirFeed()`: imprime el feed completo usando `procesarPublicacion`.

---

## 🚫 Restricciones

- Usa `sealed class` y `permits` correctamente (Java 17+).
- El `switch` en `procesarPublicacion` debe ser exhaustivo (cubrir **todos** los tipos permitidos).
- `puedePublicar()` y `puedeModerar()` deben usar lógica proposicional con variables `boolean` explícitas.
- Usa `Optional<Usuario>` en `getUsuarioPorId`.
- Usa Streams en todos los métodos de consulta del feed.
- El `switch` con pattern matching es obligatorio en `procesarPublicacion` (no uses `if/else if`).

---

## 📥 Ejemplos de entrada

```java
// Usuarios
Estudiante est1 = new Estudiante("U001", "Valentina Rivas", "v.rivas@campus.cl", true, "Ingeniería en Software", 4);
Docente doc1    = new Docente("U002", "Dr. Hugo Reyes",  "h.reyes@campus.cl", true, "Ciencias de la Computación", true);
Docente doc2    = new Docente("U003", "Prof. Carla Muñoz","c.munoz@campus.cl", true, "Matemáticas", false);
Administrativo adm1 = new Administrativo("U004", "Roberto Pino", "r.pino@campus.cl", true, "Registro Civil");

// Publicaciones
Texto t1 = new Texto("P001", est1, "¡Acabo de aprobar Algoritmos con un 6.5! Gracias a todos los que me ayudaron 🎉");
Imagen i1 = new Imagen("P002", doc1, "Foto del laboratorio renovado", "https://campus.cl/img/lab.jpg", "Nuevo laboratorio de computación con 40 PCs");
Encuesta e1 = new Encuesta("P003", doc2, "¿Qué horario prefieren para la clase de recuperación?",
    List.of("Lunes 18:00", "Miércoles 18:00", "Sábado 10:00"));
Evento ev1 = new Evento("P004", adm1, "Feria de prácticas profesionales 2025",
    "Sala magna, edificio A", "15/04/2025 10:00", 200);

// Interacciones
t1.darLike(); t1.darLike(); t1.darLike();
i1.darLike(); i1.darLike();
e1.votar("Miércoles 18:00"); e1.votar("Miércoles 18:00"); e1.votar("Lunes 18:00");
ev1.inscribir("U001"); ev1.inscribir("U003");

FeedCampusConnect feed = new FeedCampusConnect();
feed.agregarUsuario(est1); feed.agregarUsuario(doc1);
feed.agregarUsuario(doc2); feed.agregarUsuario(adm1);

feed.publicar(est1, t1);
feed.publicar(doc1, i1);
feed.publicar(doc2, e1);
feed.publicar(adm1, ev1);

feed.imprimirFeed();
System.out.println("\n📊 Tendencias: " + feed.getTendencias());
System.out.println("🏆 Más likes: " + feed.getPublicacionesConMasLikes(2)
    .stream().map(Publicacion::getId).toList());
```

---

## 📤 Salidas esperadas

```
══════════════════════════════════════════════════
            FEED — CAMPUS CONNECT
══════════════════════════════════════════════════

📝 Texto de Valentina Rivas:
   "¡Acabo de aprobar Algoritmos con un 6.5! Gracias..."
   ❤️  3 likes · 💬 0 comentarios

📷 Imagen de Dr. Hugo Reyes:
   [Imagen] Nuevo laboratorio de computación con 40 PCs
   ❤️  2 likes · 💬 0 comentarios

📊 Encuesta de Prof. Carla Muñoz:
   ¿Qué horario prefieren para la clase de recuperación?
   🔵 Lunes 18:00      — 1 voto
   🟢 Miércoles 18:00  — 2 votos ✅ Ganando
   ⚪ Sábado 10:00     — 0 votos
   ❤️  0 likes · 💬 0 comentarios

📅 Evento de Roberto Pino:
   Feria de prácticas profesionales 2025
   📍 Sala magna, edificio A
   🕐 15/04/2025 10:00
   👥 Inscritos: 2 / 200
   ❤️  0 likes · 💬 0 comentarios

📊 Tendencias: {📝 Texto=1, 📷 Imagen=1, 📊 Encuesta=1, 📅 Evento=1}
🏆 Más likes: [P001, P002]
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Switch exhaustivo con sealed</summary>

Si `Publicacion` es `sealed` con `permits Texto, Imagen, Encuesta, Evento`, el `switch` con pattern matching **no necesita `default`** porque el compilador sabe que cubre todos los casos.
</details>

<details>
<summary>Pista 2 — puedePublicar con proposiciones</summary>

```java
// En la clase Estudiante:
@Override
public boolean puedePublicar() {
    boolean p = this.isActivo();
    boolean q = this.semestre >= 1;
    return p && q;
}
```
</details>

<details>
<summary>Pista 3 — getEventosConCupo con instanceof en Stream</summary>

```java
public List<Evento> getEventosConCupo() {
    return publicaciones.stream()
        .filter(p -> p instanceof Evento)
        .map(p -> (Evento) p)
        // o con pattern matching: .map(Evento.class::cast)
        .filter(e -> e.getInscritos().size() < e.getCapacidadMaxima())
        .collect(Collectors.toList());
}
```
</details>

---

## 🧠 Reflexión final

1. ¿Qué garantía ofrece `sealed class` que `abstract class` no ofrece?
2. ¿Por qué el `switch` con pattern matching sobre `sealed class` es exhaustivo sin `default`?
3. Diseña la tabla de verdad para la condición `puedePublicar` de un `Docente` considerando `activo` y cualquier otra condición que agregues.
4. ¿Cómo se podría extender este sistema para agregar notificaciones push cuando alguien da like sin romper el código existente (principio Open/Closed de SOLID)?

---

*[← Ejercicio anterior](./13_ecommerce_tecnologia.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./15_simulador_banco.md)*

