# Módulo 04 — Clases, objetos y constructores

> **Objetivo:** modelar entidades del mundo real en clases Java bien diseñadas, entender el ciclo de vida de los objetos y conocer la alternativa moderna: los `record`.

---

## 4.1 ¿Qué es una clase?

Una **clase** es un molde o plantilla. Un **objeto** es una instancia de esa clase. La clase define qué datos (campos) y comportamientos (métodos) tienen los objetos.

```java
// Clase: el molde
public class Ticket {
    // Campos (estado del objeto)
    private Long   id;
    private String titulo;
    private String descripcion;
    private String estado;

    // Métodos (comportamiento del objeto)
    public boolean estaAbierto() {
        return "ABIERTO".equals(this.estado);
    }
}

// Objeto: instancia concreta del molde
Ticket t1 = new Ticket();  // crea un objeto en el heap
Ticket t2 = new Ticket();  // crea OTRO objeto independiente
```

---

## 4.2 Constructores

Un constructor inicializa el objeto en el momento de su creación. Si no defines ninguno, Java genera uno vacío por defecto.

```java
public class Usuario {
    private final String email;  // final: no puede cambiar después de asignarse
    private String nombre;
    private String rol;

    // Constructor vacío (no siempre conviene)
    public Usuario() {
    }

    // Constructor con parámetros obligatorios
    public Usuario(String email, String nombre) {
        this.email  = email;
        this.nombre = nombre;
        this.rol    = "LECTOR";  // valor por defecto
    }

    // Constructor completo
    public Usuario(String email, String nombre, String rol) {
        this(email, nombre);    // reutiliza el constructor anterior con this()
        this.rol = rol;
    }
}

// Uso:
Usuario u1 = new Usuario("ana@duoc.cl", "Ana");
Usuario u2 = new Usuario("luis@duoc.cl", "Luis", "ADMIN");
```

---

## 4.3 Getters, Setters y encapsulamiento

Los campos `private` se exponen controladamente a través de getters y setters:

```java
public class Ticket {
    private Long   id;
    private String titulo;
    private String estado;

    // Getter: acceso de solo lectura
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getEstado() { return estado; }

    // Setter con validación (encapsulamiento real)
    public void setEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        if (!List.of("ABIERTO", "EN_PROGRESO", "CERRADO").contains(estado)) {
            throw new IllegalArgumentException("Estado inválido: " + estado);
        }
        this.estado = estado;
    }

    // No siempre necesitas setter para todos los campos
    // Si 'id' solo lo asigna la base de datos, no expongas setId()
}
```

> 💡 **Lombok** genera getters, setters, constructores y más automáticamente con anotaciones. Se usa mucho en Spring Boot. Lo verás en el extra de [Lombok](../lombok/README.md).

---

## 4.4 `toString`, `equals` y `hashCode`

Estos tres métodos heredados de `Object` son fundamentales:

```java
public class Ticket {
    private Long   id;
    private String titulo;
    private String estado;

    // Constructor, getters...

    // toString: representación legible del objeto
    @Override
    public String toString() {
        return "Ticket{id=%d, titulo='%s', estado='%s'}".formatted(id, titulo, estado);
    }

    // equals: dos tickets son iguales si tienen el mismo id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                    // misma referencia
        if (!(o instanceof Ticket otro)) return false; // distinto tipo (usa pattern matching)
        return Objects.equals(id, otro.id);
    }

    // hashCode: DEBE ser consistente con equals
    // Si equals compara por id, hashCode también debe basarse en id
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// Comportamiento sin sobreescribir:
Ticket t1 = new Ticket(1L, "Bug");
Ticket t2 = new Ticket(1L, "Bug");
System.out.println(t1 == t2);      // false — diferente referencia
System.out.println(t1.equals(t2)); // false (Object.equals usa ==)

// Comportamiento CON sobreescribir:
System.out.println(t1.equals(t2)); // true — mismo id
```

> ⚠️ Si sobreescribes `equals`, **siempre** sobreescribe `hashCode`. No hacerlo causa bugs silenciosos en colecciones como `HashMap` y `HashSet`.

---

## 4.5 Clases inmutables

Una clase inmutable es aquella cuyo estado **no puede cambiar** después de crearse. Son thread-safe por naturaleza y más fáciles de razonar.

```java
// Clase inmutable: todos los campos son final, no hay setters
public final class Coordenada {
    private final double latitud;
    private final double longitud;

    public Coordenada(double latitud, double longitud) {
        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("Latitud inválida: " + latitud);
        }
        this.latitud  = latitud;
        this.longitud = longitud;
    }

    public double getLatitud()  { return latitud; }
    public double getLongitud() { return longitud; }

    // En lugar de setter, retorna un NUEVO objeto con el cambio
    public Coordenada conLatitud(double nuevaLatitud) {
        return new Coordenada(nuevaLatitud, this.longitud);
    }

    @Override
    public String toString() {
        return "(%.4f, %.4f)".formatted(latitud, longitud);
    }
}
```

---

## 4.6 `record` — clases de datos modernas (Java 16+) ⭐

Un `record` es una forma concisa de declarar una clase **inmutable de datos**. Java genera automáticamente:
- Constructor canónico (con todos los campos)
- Getters (sin prefijo `get`)
- `toString`, `equals`, `hashCode`

```java
// 1 línea reemplaza ~50 líneas de clase clásica
public record TicketDTO(Long id, String titulo, String estado) {}

// Uso:
var dto = new TicketDTO(1L, "Error en login", "ABIERTO");

System.out.println(dto.id());      // 1
System.out.println(dto.titulo());  // "Error en login"
System.out.println(dto.estado());  // "ABIERTO"
System.out.println(dto);           // TicketDTO[id=1, titulo=Error en login, estado=ABIERTO]

// Los records pueden tener:
// - Validación en el constructor compacto
public record Email(String valor) {
    // Constructor compacto: se ejecuta antes de asignar los campos
    Email {
        if (valor == null || !valor.contains("@")) {
            throw new IllegalArgumentException("Email inválido: " + valor);
        }
    }
}

// - Métodos adicionales
public record Punto(double x, double y) {
    public double distanciaAlOrigen() {
        return Math.sqrt(x * x + y * y);
    }

    public Punto mover(double dx, double dy) {
        return new Punto(x + dx, y + dy); // retorna nuevo record
    }
}
```

> ✅ En Spring Boot, los `record` son ideales para **DTOs** (objetos de transferencia de datos): el cuerpo de un request o response JSON.

### Record vs. Clase tradicional vs. Lombok

| | Record | Clase con Lombok `@Data` | Clase manual |
|---|---|---|---|
| Líneas de código | 1 | ~5 | ~50 |
| Mutable | ❌ No | ✅ Sí | ✅ Sí |
| Ideal para | DTOs de entrada/salida | Entidades JPA | Lógica compleja |
| Getters | `campo()` | `getCampo()` | Manual |

---

## 4.7 `this` y `static`

```java
public class Contador {
    private int valor;
    private static int totalInstancias = 0; // compartido por TODAS las instancias

    public Contador() {
        totalInstancias++;          // cada vez que se crea un Contador
        this.valor = 0;             // this: referencia al objeto actual
    }

    public void incrementar() {
        this.valor++;               // this es opcional aquí (sin ambigüedad)
    }

    public void incrementar(int n) {
        this.valor += n;
    }

    // Método de instancia: necesita un objeto
    public int getValor() { return valor; }

    // Método estático: pertenece a la CLASE, no a un objeto
    public static int getTotalInstancias() { return totalInstancias; }
}

Contador c1 = new Contador();
Contador c2 = new Contador();
c1.incrementar(5);

System.out.println(c1.getValor());              // 5
System.out.println(Contador.getTotalInstancias()); // 2
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 4.1 — Diseño de clase
Diseña una clase `Producto` con los campos: `id` (Long), `nombre` (String), `precio` (double), `stock` (int). Agrega:
- Constructor que recibe nombre y precio (id y stock tienen valores por defecto)
- Método `aplicarDescuento(double porcentaje)` que valide que el porcentaje esté entre 0 y 100
- Método `estaDisponible()` que retorne `true` si stock > 0
- `toString` legible

<details>
<summary>🔍 Ver solución</summary>

```java
public class Producto {
    private static long contadorId = 1;

    private final Long   id;
    private       String nombre;
    private       double precio;
    private       int    stock;

    public Producto(String nombre, double precio) {
        this.id     = contadorId++;
        this.nombre = nombre;
        this.precio = precio;
        this.stock  = 0;
    }

    public void aplicarDescuento(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Porcentaje debe estar entre 0 y 100");
        }
        this.precio = this.precio * (1 - porcentaje / 100);
    }

    public boolean estaDisponible() {
        return this.stock > 0;
    }

    // Getters
    public Long getId()       { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock()     { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "Producto{id=%d, nombre='%s', precio=%.2f, stock=%d}"
                .formatted(id, nombre, precio, stock);
    }
}
```
</details>

---

### Ejercicio 4.2 — Record con validación
Crea un `record` `Coordenada(double lat, double lon)` que valide en el constructor compacto que `lat` esté entre −90 y 90, y `lon` entre −180 y 180. Agrega un método `distanciaA(Coordenada otra)` usando la fórmula euclidiana simple.

<details>
<summary>🔍 Ver solución</summary>

```java
public record Coordenada(double lat, double lon) {
    Coordenada {
        if (lat < -90  || lat > 90)  throw new IllegalArgumentException("Latitud inválida: " + lat);
        if (lon < -180 || lon > 180) throw new IllegalArgumentException("Longitud inválida: " + lon);
    }

    public double distanciaA(Coordenada otra) {
        double dlat = this.lat - otra.lat;
        double dlon = this.lon - otra.lon;
        return Math.sqrt(dlat * dlat + dlon * dlon);
    }
}

// Prueba:
var santiago  = new Coordenada(-33.45, -70.67);
var valparaiso = new Coordenada(-33.05, -71.62);
System.out.println(santiago.distanciaA(valparaiso)); // ~1.02 (en grados)
```
</details>

---

*[← Módulo 03](./03_metodos.md) | [Índice](./README.md) | [Módulo 05 → POO Pilares](./05_poo_pilares.md)*

