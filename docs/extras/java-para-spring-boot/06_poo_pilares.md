# Módulo 06 — Los 4 pilares de la POO aplicados

> **Objetivo:** comprender y aplicar los 4 pilares de la POO en contextos reales, con ejemplos que aparecen directamente en código Spring Boot.

---

## Los 4 pilares de un vistazo

| Pilar | Pregunta que responde | Beneficio |
|-------|----------------------|-----------|
| **Encapsulamiento** | ¿Quién puede ver/modificar el estado? | Protección de datos, cohesión |
| **Herencia** | ¿Cómo reutilizo código entre clases relacionadas? | Reutilización, jerarquía |
| **Polimorfismo** | ¿Cómo proceso distintos tipos de forma uniforme? | Extensibilidad, código genérico |
| **Abstracción** | ¿Qué necesito exponer, qué puedo ocultar? | Simplicidad, bajo acoplamiento |

---

## 5.1 🔒 Encapsulamiento

**Principio:** oculta el estado interno. Solo expón lo necesario mediante una API controlada.

```java
public class CuentaBancaria {
    private final String numeroCuenta;
    private double saldo;
    private final List<String> movimientos = new ArrayList<>();

    public CuentaBancaria(String numeroCuenta, double saldoInicial) {
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldoInicial;
    }

    // Getter: solo lectura
    public double getSaldo() { return saldo; }
    public String getNumeroCuenta() { return numeroCuenta; }

    // API controlada: toda la lógica de negocio está aquí
    public void depositar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto debe ser positivo");
        saldo += monto;
        movimientos.add("+ $" + monto);
    }

    public void retirar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto debe ser positivo");
        if (monto > saldo) throw new IllegalStateException("Saldo insuficiente");
        saldo -= monto;
        movimientos.add("- $" + monto);
    }

    // Retorna copia inmutable para que nadie pueda modificar la lista interna
    public List<String> getMovimientos() {
        return List.copyOf(movimientos);
    }
}
```

> 💡 En Spring Boot: las clases `@Entity`, `@Service` y los DTOs aplican encapsulamiento. Los controladores no acceden directo al repositorio; pasan por el servicio.

---

## 5.2 🧬 Herencia

**Principio:** una clase hija (`extends`) hereda campos y métodos de la clase padre y puede especializarlos.

```java
// Clase padre: comportamiento común a todas las entidades con ID
public class EntidadBase {
    private Long id;
    private LocalDateTime creadoEn = LocalDateTime.now();
    private LocalDateTime actualizadoEn;

    public Long getId() { return id; }
    public LocalDateTime getCreadoEn() { return creadoEn; }

    // Marca la entidad como actualizada
    public void marcarActualizado() {
        this.actualizadoEn = LocalDateTime.now();
    }
}

// Clase hija: hereda id, creadoEn, actualizadoEn
public class Ticket extends EntidadBase {
    private String titulo;
    private String descripcion;
    private String estado;

    public Ticket(String titulo, String descripcion) {
        // super(): llama al constructor del padre (implícito si es vacío)
        this.titulo      = titulo;
        this.descripcion = descripcion;
        this.estado      = "ABIERTO";
    }

    public void actualizar(String nuevoTitulo) {
        this.titulo = nuevoTitulo;
        super.marcarActualizado();  // llama método del padre con super
    }

    // Getters...
}

// Otra hija con diferente especialización
public class Usuario extends EntidadBase {
    private String email;
    private String rol;
    // ...
}
```

### Cuándo NO usar herencia

> ⚠️ Usa herencia solo para relaciones **"es un"** verdaderas. Si la relación es **"tiene un"**, usa composición.

```java
// ❌ Herencia inapropiada: un Ticket NO "es" un ArrayList
public class Ticket extends ArrayList<String> { /* ... */ }

// ✅ Composición: un Ticket "tiene" comentarios
public class Ticket extends EntidadBase {
    private List<String> comentarios = new ArrayList<>();
    // ...
}
```

### Regla de Liskov (L de SOLID)

Una subclase debe poder **reemplazar** a su superclase sin romper el comportamiento del programa:

```java
// Función que trabaja con cualquier EntidadBase
public void registrarAuditoria(EntidadBase entidad) {
    System.out.println("Entidad ID " + entidad.getId() +
                       " creada el " + entidad.getCreadoEn());
}

// Funciona con Ticket O con Usuario sin importar el tipo concreto
registrarAuditoria(new Ticket("Bug", "Descripción"));
registrarAuditoria(new Usuario("ana@duoc.cl", "ADMIN"));
```

---

## 5.3 🎭 Polimorfismo

**Principio:** un objeto puede comportarse de distintas formas según su tipo real en tiempo de ejecución.

### Polimorfismo en tiempo de ejecución (Override)

```java
public class Notificador {
    public String enviar(String destinatario, String mensaje) {
        return "Notificando a " + destinatario;
    }
}

public class EmailNotificador extends Notificador {
    @Override
    public String enviar(String destinatario, String mensaje) {
        return "📧 Email a " + destinatario + ": " + mensaje;
    }
}

public class SmsNotificador extends Notificador {
    @Override
    public String enviar(String destinatario, String mensaje) {
        return "📱 SMS a " + destinatario + ": " + mensaje;
    }
}

// Código polimórfico: no sabe NI le importa qué tipo concreto es
public class ServicioAlertas {
    private final Notificador notificador; // referencia al tipo BASE

    public ServicioAlertas(Notificador notificador) {
        this.notificador = notificador;
    }

    public void alertar(String usuario, String mensaje) {
        // En tiempo de ejecución Java llama al método del tipo REAL
        String resultado = notificador.enviar(usuario, mensaje);
        System.out.println(resultado);
    }
}

// Uso: mismo código, distinto comportamiento
var email = new ServicioAlertas(new EmailNotificador());
var sms   = new ServicioAlertas(new SmsNotificador());

email.alertar("ana@duoc.cl", "Tu ticket fue cerrado"); // 📧 Email a ana...
sms.alertar("+56912345678", "Tu ticket fue cerrado");  // 📱 SMS a +569...
```

> 💡 En Spring Boot esto es el corazón de la **inyección de dependencias**: el servicio no sabe qué implementación concreta de `Notificador` recibirá. Spring la inyecta en tiempo de ejecución.

---

## 5.4 🧩 Abstracción

**Principio:** expón solo lo esencial; oculta los detalles de implementación.

### Con clase abstracta

```java
// Define el "qué" hace, no el "cómo"
public abstract class ServicioBase<T, ID> {
    // Método abstracto: obliga a las subclases a implementarlo
    public abstract T buscarPorId(ID id);
    public abstract List<T> listarTodos();
    public abstract T guardar(T entidad);

    // Método concreto: comportamiento compartido
    public void validarNoNulo(T entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
    }
}

// Clase concreta: implementa el "cómo"
public class TicketService extends ServicioBase<Ticket, Long> {
    private final List<Ticket> tickets = new ArrayList<>();

    @Override
    public Ticket buscarPorId(Long id) {
        return tickets.stream()
                      .filter(t -> t.getId().equals(id))
                      .findFirst()
                      .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + id));
    }

    @Override
    public List<Ticket> listarTodos() {
        return List.copyOf(tickets);
    }

    @Override
    public Ticket guardar(Ticket ticket) {
        validarNoNulo(ticket); // usa método del padre
        tickets.add(ticket);
        return ticket;
    }
}
```

---

## 5.5 Override vs. Overload — diferencias clave

Estos dos términos suenan parecidos pero son conceptos completamente distintos que se confunden frecuentemente:

- **Override (sobreescritura)** es redefinir en una subclase un método que ya existe en la superclase, cambiando su **comportamiento** pero manteniendo la misma firma. Es el mecanismo del polimorfismo. Java lo resuelve en **tiempo de ejecución** según el tipo real del objeto. La anotación `@Override` es opcional pero muy recomendable porque le pide al compilador que verifique que sí estás sobreescribiendo algo.

- **Overload (sobrecarga)** es declarar múltiples métodos en la **misma clase** con el mismo nombre pero diferente firma (tipo o número de parámetros). No requiere herencia. Java elige cuál invocar en **tiempo de compilación** según los argumentos que le pasas.

| | **Override** (Polimorfismo) | **Overload** (Sobrecarga) |
|---|---|---|
| ¿Qué es? | Redefinir método heredado en subclase | Múltiples métodos con mismo nombre |
| Anotación | `@Override` (recomendada) | Ninguna |
| Firma | Debe ser **idéntica** | Debe ser **diferente** |
| Resuelto en | Tiempo de **ejecución** | Tiempo de **compilación** |
| Requiere herencia | Sí | No |

```java
public class Animal {
    public String sonido() { return "..."; }
}

public class Perro extends Animal {
    @Override                              // Override: MISMA firma, DISTINTO comportamiento
    public String sonido() { return "Guau"; }
    // Sin @Override compilaría igual, pero es buena práctica tenerlo:
    // el compilador te avisa si cometes un error en el nombre o los parámetros

    public String sonido(int veces) {      // Overload: DISTINTA firma (diferente parámetro)
        return "Guau".repeat(veces);       // el compilador elige este cuando llamas sonido(3)
    }
}

Animal a = new Perro();      // referencia Animal, objeto Perro
a.sonido();                  // → "Guau" (Override: usa el método del tipo REAL en ejecución)
// a.sonido(3);              // ❌ Animal no tiene sonido(int), solo Perro tiene Overload
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 5.1 — Encapsulamiento
Diseña una clase `Semaforo` con un estado interno `String color` que solo puede ser `"ROJO"`, `"AMARILLO"` o `"VERDE"`. El color inicial es `"ROJO"`. Agrega un método `siguiente()` que avance al siguiente color en ciclo (ROJO → VERDE → AMARILLO → ROJO...).

<details>
<summary>🔍 Ver solución</summary>

```java
public class Semaforo {
    private String color;
    private static final List<String> CICLO = List.of("ROJO", "VERDE", "AMARILLO");

    public Semaforo() {
        this.color = "ROJO";
    }

    public String getColor() { return color; }

    public void siguiente() {
        int indiceActual = CICLO.indexOf(color);
        int siguienteIndice = (indiceActual + 1) % CICLO.size();
        this.color = CICLO.get(siguienteIndice);
    }

    @Override
    public String toString() {
        return "Semaforo[" + color + "]";
    }
}
```
</details>

---

### Ejercicio 5.2 — Herencia y polimorfismo
Crea una jerarquía: clase base `Figura` con un método abstracto `calcularArea()` y un método concreto `describir()` que imprima `"Figura: [nombre] — Área: [area]"`. Implementa `Circulo(double radio)`, `Rectangulo(double ancho, double alto)` y `Triangulo(double base, double altura)`. Crea una lista de figuras mixta y llama `describir()` en cada una.

<details>
<summary>🔍 Ver solución</summary>

```java
public abstract class Figura {
    private final String nombre;

    public Figura(String nombre) { this.nombre = nombre; }

    public abstract double calcularArea();

    public void describir() {
        System.out.printf("Figura: %-12s — Área: %.2f%n", nombre, calcularArea());
    }
}

public class Circulo extends Figura {
    private final double radio;
    public Circulo(double radio) { super("Círculo"); this.radio = radio; }

    @Override
    public double calcularArea() { return Math.PI * radio * radio; }
}

public class Rectangulo extends Figura {
    private final double ancho, alto;
    public Rectangulo(double ancho, double alto) {
        super("Rectángulo"); this.ancho = ancho; this.alto = alto;
    }

    @Override
    public double calcularArea() { return ancho * alto; }
}

public class Triangulo extends Figura {
    private final double base, altura;
    public Triangulo(double base, double altura) {
        super("Triángulo"); this.base = base; this.altura = altura;
    }

    @Override
    public double calcularArea() { return (base * altura) / 2; }
}

// Main
List<Figura> figuras = List.of(
    new Circulo(5),
    new Rectangulo(4, 6),
    new Triangulo(3, 8)
);

figuras.forEach(Figura::describir);
// Figura: Círculo      — Área: 78.54
// Figura: Rectángulo   — Área: 24.00
// Figura: Triángulo    — Área: 12.00
```
</details>

---

*[← Módulo 04](./04_clases_y_objetos.md) | [Índice](./README.md) | [Módulo 06 → Interfaces](./06_interfaces_y_abstraccion.md)*

