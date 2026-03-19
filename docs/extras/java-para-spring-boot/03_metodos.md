# Módulo 03 — Métodos: diseño y sobrecarga

> **Objetivo:** saber diseñar métodos bien nombrados, comprender firma, retorno, parámetros, sobrecarga y los conceptos de paso por valor/referencia.

---

## 3.1 Anatomía de un método

```
[modificador de acceso] [static] [tipo de retorno] nombreMétodo([parámetros]) {
    // cuerpo
    return valor; // obligatorio si el tipo de retorno no es void
}
```

```java
public class Calculadora {

    // Método público que retorna int
    public int sumar(int a, int b) {
        return a + b;
    }

    // Método sin retorno (void)
    public void imprimirResultado(int resultado) {
        System.out.println("Resultado: " + resultado);
    }

    // Método estático: se llama sin instanciar la clase
    public static double calcularIva(double precio) {
        return precio * 0.19;
    }
}

// Uso:
Calculadora calc = new Calculadora();
int suma = calc.sumar(5, 3);     // 8
calc.imprimirResultado(suma);    // "Resultado: 8"
double iva = Calculadora.calcularIva(1000); // método estático
```

---

## 3.2 Modificadores de acceso

| Modificador | Visible desde |
|------------|---------------|
| `public` | Cualquier clase |
| `protected` | Misma clase, mismo paquete, subclases |
| *(sin modificador)* | Solo mismo paquete (package-private) |
| `private` | Solo la misma clase |

```java
public class CuentaBancaria {
    private double saldo;          // solo esta clase puede leer/modificar
    double tasaInteres;            // package-private
    protected String titular;     // subclases también acceden
    public String numeroCuenta;   // todos acceden

    private void calcularInteres() { /* ... */ }  // interno
    public double getSaldo() { return saldo; }     // API pública
}
```

> 🔒 Regla de oro: **lo más restrictivo posible**. Empieza con `private` y abre solo lo necesario.

---

## 3.3 Sobrecarga de métodos (Overloading)

Múltiples métodos con el **mismo nombre** pero **diferente firma** (tipo o número de parámetros).

```java
public class Formateador {

    // Versión básica
    public String formatear(String valor) {
        return "[" + valor + "]";
    }

    // Misma acción, diferente tipo
    public String formatear(int valor) {
        return "[" + valor + "]";
    }

    // Misma acción, parámetro adicional
    public String formatear(String valor, String etiqueta) {
        return etiqueta + ": [" + valor + "]";
    }

    // Versión con varargs (número variable de argumentos)
    public String formatearTodos(String... valores) {
        return String.join(" | ", valores);
    }
}

// El compilador decide cuál llamar según los argumentos:
Formateador f = new Formateador();
f.formatear("hola");             // → "[hola]"
f.formatear(42);                 // → "[42]"
f.formatear("hola", "Saludo");  // → "Saludo: [hola]"
f.formatearTodos("a", "b", "c"); // → "a | b | c"
```

---

## 3.4 Paso por valor vs. paso por referencia

En Java **todo se pasa por valor**, pero el "valor" de un objeto es su **referencia** (dirección de memoria).

```java
// Tipos primitivos: se pasa una copia. El original NO cambia.
public static void duplicar(int n) {
    n = n * 2;  // solo modifica la copia local
}

int x = 5;
duplicar(x);
System.out.println(x); // 5 — no cambió

// Objetos: se pasa la referencia por valor.
// Puedes modificar el ESTADO del objeto, pero no reasignar la variable externa.
public static void cambiarNombre(StringBuilder sb) {
    sb.append(" Boot");  // modifica el objeto — SÍ se refleja afuera
}

public static void reasignar(StringBuilder sb) {
    sb = new StringBuilder("Otro"); // reasigna la variable local — NO se refleja afuera
}

var sb = new StringBuilder("Spring");
cambiarNombre(sb);
System.out.println(sb); // "Spring Boot"

reasignar(sb);
System.out.println(sb); // "Spring Boot" — la reasignación no tuvo efecto externo
```

---

## 3.5 Métodos con `Optional` como retorno (Java 8+)

Cuando un método **puede no encontrar** un resultado, retornar `Optional<T>` en lugar de `null` es mucho más seguro y expresivo.

```java
import java.util.Optional;

public class RepositorioUsuarios {

    private List<String> usuarios = List.of("ana@duoc.cl", "luis@duoc.cl");

    // ❌ Estilo antiguo: retornar null es peligroso
    public String buscarPorEmail(String email) {
        for (String u : usuarios) {
            if (u.equals(email)) return u;
        }
        return null; // quien llame debe recordar verificar null
    }

    // ✅ Estilo moderno: retornar Optional comunica la posibilidad de ausencia
    public Optional<String> buscarPorEmailSeguro(String email) {
        return usuarios.stream()
                       .filter(u -> u.equals(email))
                       .findFirst(); // retorna Optional<String>
    }
}

// Uso:
var repo = new RepositorioUsuarios();

Optional<String> resultado = repo.buscarPorEmailSeguro("ana@duoc.cl");

// Opción 1: ifPresent — solo actúa si hay valor
resultado.ifPresent(u -> System.out.println("Encontrado: " + u));

// Opción 2: orElse — valor por defecto
String usuario = resultado.orElse("desconocido");

// Opción 3: orElseThrow — lanza excepción si no hay valor
String usuarioOrFalla = resultado.orElseThrow(() ->
    new RuntimeException("Usuario no encontrado")
);

// Opción 4: map — transforma si hay valor
Optional<String> emailMayusculas = resultado.map(String::toUpperCase);
```

> 💡 En Spring Boot, los repositorios JPA retornan `Optional<T>` automáticamente: `Optional<Ticket> findById(Long id)`.

---

## 3.6 Métodos de fábrica estáticos (Static Factory Methods)

Patrón muy común en Spring Boot y en la API estándar de Java:

```java
public class Respuesta<T> {
    private final T datos;
    private final String mensaje;
    private final int status;

    private Respuesta(T datos, String mensaje, int status) {
        this.datos = datos;
        this.mensaje = mensaje;
        this.status = status;
    }

    // Métodos de fábrica: nombres descriptivos en lugar de constructores
    public static <T> Respuesta<T> ok(T datos) {
        return new Respuesta<>(datos, "Operación exitosa", 200);
    }

    public static <T> Respuesta<T> creado(T datos) {
        return new Respuesta<>(datos, "Recurso creado", 201);
    }

    public static <T> Respuesta<T> noEncontrado(String mensaje) {
        return new Respuesta<>(null, mensaje, 404);
    }
}

// Uso — mucho más legible que new Respuesta<>(ticket, "OK", 200):
Respuesta<Ticket> r1 = Respuesta.ok(ticket);
Respuesta<Ticket> r2 = Respuesta.creado(nuevoTicket);
Respuesta<Ticket> r3 = Respuesta.noEncontrado("Ticket no existe");
```

---

## 🏋️ Ejercicios de práctica

### Ejercicio 3.1 — Sobrecarga
Diseña una clase `Calculadora` con métodos sobrecargados `calcularArea` para:
- Círculo: recibe `double radio`
- Rectángulo: recibe `double ancho, double alto`
- Triángulo: recibe `double base, double altura`

<details>
<summary>🔍 Ver solución</summary>

```java
public class Calculadora {

    public double calcularArea(double radio) {
        return Math.PI * radio * radio;
    }

    public double calcularArea(double ancho, double alto) {
        return ancho * alto;
    }

    // ⚠️ Problema: misma firma que el rectángulo. 
    // Solución: usar nombre diferente o enumerado de figura.
    // Para el triángulo, usamos un tercer parámetro para distinguir:
    public double calcularAreaTriangulo(double base, double altura) {
        return (base * altura) / 2;
    }
}
// Nota: la sobrecarga no puede diferenciarse SOLO por nombre de parámetro,
// sino por tipo y número. Por eso triángulo y rectángulo necesitan
// nombres distintos o un parámetro adicional.
```
</details>

---

### Ejercicio 3.2 — Optional
Escribe un método `Optional<Integer> dividir(int a, int b)` que retorne `Optional.empty()` si `b == 0`, o el resultado envuelto en `Optional` si `b != 0`. Luego usa el resultado imprimiendo el valor o el mensaje "División por cero no permitida".

<details>
<summary>🔍 Ver solución</summary>

```java
public static Optional<Integer> dividir(int a, int b) {
    if (b == 0) return Optional.empty();
    return Optional.of(a / b);
}

// Uso:
dividir(10, 2)
    .ifPresentOrElse(
        r -> System.out.println("Resultado: " + r),
        () -> System.out.println("División por cero no permitida")
    );

dividir(10, 0)
    .ifPresentOrElse(
        r -> System.out.println("Resultado: " + r),
        () -> System.out.println("División por cero no permitida")
    );
```
</details>

---

### Ejercicio 3.3 — Paso por valor
Sin ejecutar el código, predice la salida:

```java
public static void modificar(int[] arr, int primitivo) {
    arr[0] = 99;
    primitivo = 999;
}

int[] arreglo = {1, 2, 3};
int numero = 5;
modificar(arreglo, numero);
System.out.println(arreglo[0]);  // ¿?
System.out.println(numero);      // ¿?
```

<details>
<summary>🔍 Ver solución</summary>

```
99  // El array es un objeto. Se pasa la referencia por valor, 
    // pero puedes modificar su CONTENIDO. arr[0] = 99 modifica el array original.
5   // El int es primitivo. Se pasa una copia. primitivo = 999 no afecta 'numero'.
```
</details>

---

*[← Módulo 02](./02_control_de_flujo.md) | [Índice](./README.md) | [Módulo 04 → Clases y objetos](./04_clases_y_objetos.md)*

