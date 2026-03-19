# Módulo 07 — Principios de buen código

> **Objetivo:** conocer los principios universales que separan el código que *funciona* del código que *se puede mantener*. No son reglas de un lenguaje — son principios de artesanía de software que aplican en cualquier stack.

---

## ¿Por qué importan estos principios?

Hacer que un programa funcione es la primera etapa. Pero en el mundo real, el código:

- Lo leerán otras personas (o tú mismo en 6 meses).
- Se modificará decenas de veces a lo largo del tiempo.
- Tendrá que crecer sin romperse.

> 📌 "Cualquier tonto puede escribir código que una computadora entiende. Los buenos programadores escriben código que los humanos entienden." — Martin Fowler

---

## Índice

1. [DRY — Don't Repeat Yourself](#1-dry--dont-repeat-yourself)
2. [KISS — Keep It Simple, Stupid](#2-kiss--keep-it-simple-stupid)
3. [YAGNI — You Aren't Gonna Need It](#3-yagni--you-arent-gonna-need-it)
4. [Fail Fast](#4-fail-fast)
5. [Principio de responsabilidad única (SRP)](#5-principio-de-responsabilidad-única-srp)
6. [Nombres que explican la intención](#6-nombres-que-explican-la-intención)
7. [Funciones pequeñas y con propósito único](#7-funciones-pequeñas-y-con-propósito-único)
8. [Comentarios: cuándo sí, cuándo no](#8-comentarios-cuándo-sí-cuándo-no)
9. [Números mágicos](#9-números-mágicos)
10. [Tabla resumen](#10-tabla-resumen)
11. [📚 Literatura recomendada](#-literatura-recomendada)
12. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. DRY — Don't Repeat Yourself

### 📖 Definición

**"No te repitas"**: cada pieza de conocimiento o lógica debe tener **una única representación** en el sistema. Si copias y pegas código, estás violando DRY.

### ☕ En Java

```java
// ❌ DRY violado: la misma lógica de cálculo repetida en dos lugares
public double calcularTotalConIva(double precio) {
    return precio + (precio * 0.19);
}

public double calcularPrecioFinal(double precio) {
    return precio + (precio * 0.19); // ← copia exacta de arriba
}

// ✅ DRY aplicado: un solo lugar con la lógica
private static final double IVA = 0.19;

private double aplicarIva(double precio) {
    return precio * (1 + IVA);
}

public double calcularTotalConIva(double precio) {
    return aplicarIva(precio);
}

public double calcularPrecioFinal(double precio) {
    return aplicarIva(precio);
}
```

### ⚠️ Consecuencia de violar DRY

Si el IVA cambia del 19% al 21%, en el código ❌ debes buscarlo y cambiarlo en **cada lugar** (y es fácil olvidar uno). En el código ✅ lo cambias **en un solo lugar**.

> ⚠️ **Trampa**: DRY no significa "nunca escribas dos líneas similares". Si dos cosas parecen iguales pero representan conceptos distintos, forzar la reutilización puede crear un acoplamiento incorrecto. DRY aplica al **conocimiento**, no mecánicamente a la sintaxis.

---

## 2. KISS — Keep It Simple, Stupid

### 📖 Definición

**"Mantenlo simple"**: la solución más simple que funciona correctamente es siempre preferible a una solución compleja. La complejidad innecesaria es deuda técnica.

### ☕ En Java

```java
// ❌ Sobre-ingeniería innecesaria para determinar si un número es par
public boolean esPar(int numero) {
    return numero % 2 == 0
        ? Boolean.TRUE.equals(Boolean.valueOf(true))
        : Boolean.FALSE.equals(Boolean.valueOf(true));
}

// ✅ Simple y directo
public boolean esPar(int numero) {
    return numero % 2 == 0;
}
```

```java
// ❌ Demasiada abstracción para un caso sencillo
interface CalculadoraDeDescuento {
    double calcular(double precio);
}
class DescuentoDelDiezPorCiento implements CalculadoraDeDescuento { ... }
class FabricaDeCalculadoras { ... }
// ... 5 clases para aplicar un 10%

// ✅ Simple para el problema actual
public double aplicarDescuento(double precio, double porcentaje) {
    return precio * (1 - porcentaje / 100);
}
```

### ⚠️ KISS no es "código malo"

Simple ≠ descuidado. Significa eliminar complejidad **accidental** (la que el programador introduce innecesariamente), no la complejidad **esencial** (la que el problema requiere).

---

## 3. YAGNI — You Aren't Gonna Need It

### 📖 Definición

**"No lo vas a necesitar"**: no implementes funcionalidad que no existe en los requerimientos actuales, anticipando un futuro hipotético. El código que nunca se usa es deuda sin beneficio.

### ☕ En Java

```java
// ❌ YAGNI violado: implementar soporte para múltiples monedas
//    cuando el sistema SOLO necesita pesos chilenos ahora
public class Precio {
    private double valor;
    private String moneda;       // "nadie me pidió esto todavía"
    private double tasaDeCambio; // "por si algún día lo necesito"

    public double convertirA(String otraMoneda) { ... } // nunca se usará
}

// ✅ YAGNI aplicado: implementa lo que se necesita hoy
public class Precio {
    private double valorEnPesos;

    public double getValor() { return valorEnPesos; }
}
// Si en el futuro se necesita multi-moneda, se agrega CUANDO SEA necesario,
// con los requerimientos reales sobre la mesa.
```

### ⚠️ YAGNI no significa "código inflexible"

Significa no añadir complejidad por requerimientos imaginados. Diseña para ser **extensible**, no para anticipar cada posible extensión desde el día uno.

---

## 4. Fail Fast

### 📖 Definición

**"Falla pronto"**: detecta y reporta errores **lo antes posible**, en lugar de dejar que el error se propague y falle en un lugar lejano, haciendo el debugging muy difícil.

### ☕ En Java

```java
// ❌ Fail Slow: el error ocurre lejos de su origen
public void procesarPedido(Pedido pedido) {
    // ... 30 líneas de lógica ...
    String cliente = pedido.getCliente().getNombre(); // NPE aquí, lejos del origen
}

// ✅ Fail Fast: valida al inicio, falla con un mensaje claro
public void procesarPedido(Pedido pedido) {
    if (pedido == null) throw new IllegalArgumentException("El pedido no puede ser null");
    if (pedido.getCliente() == null) throw new IllegalArgumentException("El pedido debe tener cliente");
    if (pedido.getItems().isEmpty()) throw new IllegalArgumentException("El pedido debe tener al menos un ítem");

    // A partir de aquí, las precondiciones están garantizadas
    String cliente = pedido.getCliente().getNombre(); // seguro
    // ...
}
```

```java
// En Java se puede usar Objects.requireNonNull() para mayor expresividad
import java.util.Objects;

public void procesarPedido(Pedido pedido) {
    Objects.requireNonNull(pedido, "El pedido no puede ser null");
    Objects.requireNonNull(pedido.getCliente(), "El pedido debe tener cliente");
    // ...
}
```

### ✅ Beneficios

- El stack trace apunta al **origen real** del problema.
- El mensaje de error describe **exactamente qué salió mal**.
- El código del método principal queda **limpio de validaciones** mezcladas con lógica.

---

## 5. Principio de responsabilidad única (SRP)

### 📖 Definición

Cada módulo, clase o función debe tener **una sola razón para cambiar** — es decir, una sola responsabilidad. Si una clase hace demasiadas cosas, un cambio en cualquiera de ellas puede romper las otras.

> Este es el primero de los principios SOLID. Aquí lo vemos desde una perspectiva general; el extra de [SOLID](../solid/README.md) lo profundiza.

### ☕ En Java

```java
// ❌ Una clase con demasiadas responsabilidades
public class Ticket {
    private String descripcion;
    private String estado;

    public void guardarEnBaseDeDatos() { /* SQL aquí */ }    // responsabilidad: persistencia
    public void enviarEmailAlCliente() { /* SMTP aquí */ }   // responsabilidad: notificación
    public double calcularCosto() { /* lógica aquí */ }      // responsabilidad: negocio
    public String formatearParaReporte() { /* HTML aquí */ } // responsabilidad: presentación
}

// ✅ Cada clase con una responsabilidad
public class Ticket {                        // responsabilidad: el dominio del ticket
    private String descripcion;
    private String estado;
    public double calcularCosto() { ... }
}

public class TicketRepository {             // responsabilidad: persistencia
    public void guardar(Ticket ticket) { ... }
}

public class TicketNotificationService {    // responsabilidad: notificaciones
    public void notificarCliente(Ticket ticket) { ... }
}

public class TicketReportFormatter {        // responsabilidad: formato para reportes
    public String formatear(Ticket ticket) { ... }
}
```

---

## 6. Nombres que explican la intención

### 📖 Definición

Los nombres de variables, métodos y clases deben **revelar la intención** sin necesidad de comentarios adicionales. El código debe leerse casi como prosa.

### ☕ En Java

```java
// ❌ Nombres crípticos
int d;
List<int[]> lst;
void proc(List<int[]> l) {
    for (int[] x : l) {
        if (x[1] > 30) lst.add(x);
    }
}

// ✅ Nombres que comunican
int diasDesdeUltimaModificacion;
List<int[]> ticketsMayoresDeTreintaDias;
void filtrarTicketsVencidos(List<int[]> tickets) {
    for (int[] ticket : tickets) {
        if (ticket[EDAD_INDEX] > DIAS_LIMITE_VENCIMIENTO) {
            ticketsMayoresDeTreintaDias.add(ticket);
        }
    }
}
```

### Guía de nombres por tipo

| Qué nombrar | Convención | Criterio |
|-------------|-----------|---------|
| **Variable booleana** | `esActivo`, `tieneDescuento`, `puedeEditar` | Suena a pregunta de sí/no |
| **Método que retorna valor** | `calcularTotal()`, `obtenerNombre()`, `buscarPorId()` | Verbo + sustantivo |
| **Método void (acción)** | `procesarPago()`, `enviarEmail()`, `validarDatos()` | Verbo de acción |
| **Clase** | `TicketService`, `PedidoRepository`, `UsuarioDTO` | Sustantivo, describe la entidad |
| **Constante** | `MAX_INTENTOS`, `TASA_IVA`, `URL_BASE` | UPPER_SNAKE_CASE, sustantivo |

---

## 7. Funciones pequeñas y con propósito único

### 📖 Definición

Una función debe **hacer una sola cosa y hacerla bien**. Si necesitas usar "y" para describir qué hace un método, probablemente está haciendo demasiado.

### ☕ En Java

```java
// ❌ Método que hace demasiado
public void procesarYGuardarYNotificarTicket(Ticket ticket) {
    // validar
    if (ticket.getDescripcion() == null) throw new RuntimeException("...");
    // calcular costo
    double costo = ticket.getHoras() * TARIFA_HORA;
    ticket.setCosto(costo);
    // guardar en DB
    ticketRepository.save(ticket);
    // enviar email
    emailService.send(ticket.getCliente().getEmail(), "Tu ticket fue procesado");
    // registrar en log
    logger.info("Ticket {} procesado", ticket.getId());
}

// ✅ Métodos pequeños, cada uno con una responsabilidad
public void procesarTicket(Ticket ticket) {
    validar(ticket);
    asignarCosto(ticket);
    guardar(ticket);
    notificarCliente(ticket);
}

private void validar(Ticket ticket) {
    Objects.requireNonNull(ticket.getDescripcion(), "La descripción es requerida");
}

private void asignarCosto(Ticket ticket) {
    ticket.setCosto(ticket.getHoras() * TARIFA_HORA);
}

private void guardar(Ticket ticket) {
    ticketRepository.save(ticket);
}

private void notificarCliente(Ticket ticket) {
    emailService.send(ticket.getCliente().getEmail(), "Tu ticket fue procesado");
}
```

> 💡 El método `procesarTicket` ahora lee como una tabla de contenidos. No necesitas comentarios para entender qué hace.

---

## 8. Comentarios: cuándo sí, cuándo no

### 📖 Definición

Los comentarios no son "siempre buenos". Un buen comentario explica el **por qué**, no el **qué** (el código ya dice el qué).

### ☕ En Java

```java
// ❌ Comentarios que repiten el código (ruido inútil)
// Incrementa el contador en 1
contador++;

// Verifica si el usuario es mayor de edad
if (edad >= 18) { ... }

// ✅ Comentarios que aportan contexto del POR QUÉ
// El RFC 2822 exige que el email tenga al menos un '@' y un '.'
if (!email.contains("@") || !email.contains(".")) { ... }

// Los reintentos son exponenciales para no saturar el servicio externo
// en caso de falla masiva (circuit breaker pattern)
long espera = (long) Math.pow(2, intento) * 1000;

// ⚠️ TODO: migrar esta validación a un Validator dedicado cuando refactoricemos el módulo
if (precio <= 0) throw new IllegalArgumentException("...");
```

### Regla práctica

```
¿El código dice QUÉ hace? → El nombre del método/variable ya lo dice → NO comentes
¿El código no dice POR QUÉ lo hace así? → Comenta el razonamiento
¿Hay una excepción, bug conocido o decisión no obvia? → Comenta
```

---

## 9. Números mágicos

### 📖 Definición

Un **número mágico** (*magic number*) es un literal numérico (o de texto) que aparece en el código sin explicación. Son una violación directa de DRY y hacen el código ilegible.

### ☕ En Java

```java
// ❌ Magic numbers: ¿qué significan 0.19, 3, 86400?
double total = precio * 1.19;
if (intentos > 3) { bloquear(); }
long expiracion = System.currentTimeMillis() + 86400000;

// ✅ Constantes con nombre que explican el significado
private static final double TASA_IVA           = 0.19;
private static final int    MAX_INTENTOS_LOGIN  = 3;
private static final long   UN_DIA_EN_MS        = 24 * 60 * 60 * 1000L;

double total     = precio * (1 + TASA_IVA);
if (intentos > MAX_INTENTOS_LOGIN) { bloquear(); }
long expiracion  = System.currentTimeMillis() + UN_DIA_EN_MS;
```

---

## 10. Tabla resumen

| Principio | En una línea | Señal de que se viola |
|-----------|-------------|----------------------|
| **DRY** | No repitas lógica | Copias y pegas código |
| **KISS** | Elige la solución más simple | Sobre-ingeniería sin justificación |
| **YAGNI** | Implementa lo que necesitas ahora | Código que "algún día quizás se use" |
| **Fail Fast** | Detecta errores lo antes posible | NPE lejos de su origen, mensajes genéricos |
| **SRP** | Una clase/función, una responsabilidad | Métodos con "y" en su nombre, clases enormes |
| **Nombres claros** | El nombre revela la intención | Variables de una letra, abreviaturas crípticas |
| **Funciones pequeñas** | Una función hace una sola cosa | Métodos de 100+ líneas |
| **Sin magic numbers** | Usa constantes con nombre | Literales numéricos sin contexto |

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Clean Code: A Handbook of Agile Software Craftsmanship** | Robert C. Martin | Intermedio | El libro de referencia del tema. Cubre naming, funciones, comentarios, formateo y refactoring con ejemplos Java reales. Es la fuente de la mayoría de los principios de este módulo |
| **The Pragmatic Programmer: Your Journey to Mastery** | Hunt & Thomas | Principiante / Intermedio | Introduce DRY, YAGNI y el "principio de menor sorpresa". Lectura imprescindible al inicio de la carrera |
| **A Philosophy of Software Design** | John Ousterhout | Intermedio / Avanzado | Contraargumenta algunos puntos de Clean Code con una perspectiva más orientada a la profundidad del módulo. Pensamiento crítico esencial |
| **Refactoring: Improving the Design of Existing Code** | Martin Fowler | Intermedio | Cómo aplicar estos principios a código existente paso a paso, con catálogo de refactorizaciones concretas |
| **Code Complete** | Steve McConnell | Intermedio | El más enciclopédico. Cubre naming, funciones, comentarios y métricas de calidad con investigación empírica |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Refactoring.Guru — Code Smells** | https://refactoring.guru/es/refactoring/smells | Catálogo de "malos olores" en el código: cómo identificar violaciones de DRY, SRP y los demás principios |
| **SOLID Principles — Baeldung** | https://www.baeldung.com/solid-principles | Los principios SOLID explicados en Java — extensión natural de lo visto en este módulo |
| **Google Java Style Guide** | https://google.github.io/styleguide/javaguide.html | La guía de estilo de Google para Java — naming, formateo y buenas prácticas en producción |
| **SonarQube Rules** | https://rules.sonarsource.com/java | Las reglas de calidad de código más usadas en la industria Java — son esencialmente estos principios automatizados |
| **The Boy Scout Rule — artículo** | https://www.oreilly.com/library/view/97-things-every/9780596809515/ch08.html | "Deja el campamento más limpio de lo que lo encontraste" — el principio de mejora continua del código |

