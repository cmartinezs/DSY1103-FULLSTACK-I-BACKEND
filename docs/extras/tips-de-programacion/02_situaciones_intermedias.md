# Módulo 02 — Situaciones intermedias

> **Nivel:** 🟠 Intermedio — Java con clases propias, objetos y Streams.  
> **Prerequisito:** haber leído el [módulo 01](./01_situaciones_basicas.md).

---

## Tip 16 — "Comparo dos objetos y me dice que son distintos aunque tienen los mismos datos"

### 📋 El escenario
Tienes dos objetos del mismo tipo con exactamente los mismos valores, pero al compararlos el programa dice que son diferentes. Sucede porque usaste `==` en lugar de `equals()`.

*Ejemplo:* dos tickets con el mismo ID creados por separado que no son reconocidos como iguales.

### ❌ El error común
```java
Ticket t1 = new Ticket(1, "Error en login", "ABIERTO");
Ticket t2 = new Ticket(1, "Error en login", "ABIERTO");

if (t1 == t2) {   // ❌ compara direcciones de memoria, no contenido
    System.out.println("Son iguales");
} else {
    System.out.println("Son diferentes");  // siempre llega aquí
}
```

### 🧠 ¿Cómo pienso esto?
```
== compara REFERENCIAS (direcciones de memoria en el heap)
   → solo es true si las dos variables apuntan al MISMO objeto

equals() compara CONTENIDO
   → es lo que necesitamos para comparar objetos por valor

¿Quién implementa equals()?
  String, Integer, LocalDate… → ya lo tienen implementado
  Mis clases propias           → debo sobreescribir equals() y hashCode()
  record                       → Java lo genera automáticamente ✅
```

### ✅ La solución

```java
// ── Opción A: record — equals() y hashCode() incluidos sin código extra ────────
record Ticket(int id, String titulo, String status) {}

Ticket t1 = new Ticket(1, "Error en login", "ABIERTO");
Ticket t2 = new Ticket(1, "Error en login", "ABIERTO");

System.out.println(t1.equals(t2));   // ✅ true
System.out.println(t1 == t2);        // false  (distintas referencias)

// ── Opción B: clase normal con equals() implementado ─────────────────────────
public class Ticket {
    private int id;
    // ...otros campos...

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket other)) return false;
        return this.id == other.id;   // el ID es el identificador de negocio
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

// ── Casos especiales ──────────────────────────────────────────────────────────
// String: equals() sí compara contenido
"hola".equals("hola")   // ✅ true
"hola" == "hola"        // ⚠️ puede ser true en el pool, pero NO te fíes

// null-safe: equalsIgnoreCase también es null-unsafe
String s = null;
// s.equals("hola")        // ❌ NullPointerException
"hola".equals(s)           // ✅ false  — el literal a la izquierda
Objects.equals(s, "hola")  // ✅ false  — null-safe
```

> 💡 **Regla:** para objetos, usa siempre `equals()`. El operador `==` solo es correcto para primitivos (`int`, `double`, `boolean`) y para comprobar si una referencia es `null`. Cuando crees clases propias de dominio, prefiere `record` — Java implementa `equals` y `hashCode` automáticamente.

---

## Tip 17 — "El programa explota con NullPointerException"

### 📋 El escenario
El programa falla en tiempo de ejecución con `NullPointerException` (NPE) al intentar llamar un método o acceder a un campo de una variable que vale `null`.

*Ejemplo:* buscar un ticket por ID y llamar un método sobre el resultado sin verificar si se encontró.

### ❌ El error común
```java
Ticket ticket = buscarPorId(lista, 99);     // puede devolver null
System.out.println(ticket.getTitulo());     // ❌ NPE si ticket es null
```

### 🧠 ¿Cómo pienso esto?
```
NPE ocurre cuando llamas un método sobre null:  null.metodo()

¿Cuándo puede ser null?
  - Resultado de una búsqueda que no encontró nada
  - Parámetro de entrada no validado
  - Campo de un objeto que no fue inicializado

Estrategias:
  1. Verificar null explícitamente antes de usarla
  2. Usar Optional para expresar "puede no existir"
  3. Inicializar siempre con un valor por defecto
```

### ✅ La solución

```java
// ── Opción A: verificación null explícita ─────────────────────────────────────
Ticket ticket = buscarPorId(lista, 99);

if (ticket != null) {
    System.out.println("Ticket: " + ticket.getTitulo());
} else {
    System.out.println("No encontrado");
}

// ── Opción B: Optional — la forma moderna y expresiva ─────────────────────────
Optional<Ticket> resultado = lista.stream()
        .filter(t -> t.getId() == 99)
        .findFirst();

// Ejecutar solo si existe
resultado.ifPresentOrElse(
    t  -> System.out.println("Ticket: " + t.getTitulo()),
    () -> System.out.println("No encontrado")
);

// Extraer con valor por defecto
String titulo = resultado
        .map(Ticket::getTitulo)
        .orElse("Sin título");

// ── En métodos que pueden no encontrar nada: devolver Optional, no null ────────
public Optional<Ticket> buscarPorId(List<Ticket> lista, int id) {
    return lista.stream()
            .filter(t -> t.getId() == id)
            .findFirst();                   // devuelve Optional<Ticket>
}

// ── Null-safe con Objects.requireNonNull ──────────────────────────────────────
public Ticket actualizar(Ticket ticket) {
    Objects.requireNonNull(ticket, "El ticket no puede ser null");
    // a partir de aquí ticket es seguro de usar
    return ticketRepository.save(ticket);
}
```

> 💡 **Regla:** nunca llames métodos sobre una referencia sin saber si puede ser `null`. Usa `Optional` cuando un método "puede no encontrar nada" — comunica la posibilidad de ausencia de forma explícita en el tipo de retorno. Nunca devuelvas `null` desde un método — devuelve `Optional.empty()`.

---

## Tip 18 — "Quiero filtrar, transformar u ordenar una lista de objetos"

### 📋 El escenario
Tienes una lista de objetos y necesitas aplicar una o varias operaciones sobre ella: quedarte con un subconjunto, convertir cada elemento a otro tipo, ordenarlos o encadenar varias operaciones.

*Ejemplo:* de todos los tickets, obtener los títulos en mayúsculas de los que están abiertos, ordenados alfabéticamente.

### ❌ El error común
```java
// ❌ Múltiples recorridos y listas intermedias para cada operación
List<Ticket> abiertos = new ArrayList<>();
for (Ticket t : tickets) {
    if (t.getStatus().equals("ABIERTO")) abiertos.add(t);
}
Collections.sort(abiertos, (a, b) -> a.getTitulo().compareTo(b.getTitulo()));
List<String> titulos = new ArrayList<>();
for (Ticket t : abiertos) {
    titulos.add(t.getTitulo().toUpperCase());
}
```

### 🧠 ¿Cómo pienso esto?
```
Piensa en el Stream como una tubería (pipeline):
  1. filter  → reduce la cantidad de elementos
  2. sorted  → ordena
  3. map     → transforma cada elemento en otra cosa
  4. toList  → materializa el resultado

Orden importa: filter primero hace que las operaciones siguientes trabajen
con menos elementos → más eficiente.
```

### ✅ La solución

```java
List<Ticket> tickets = obtenerTodos();

// ── filter: quedarse con un subconjunto ───────────────────────────────────────
List<Ticket> abiertos = tickets.stream()
        .filter(t -> t.getStatus().equals("ABIERTO"))
        .toList();

// ── map: transformar cada elemento en otra cosa ───────────────────────────────
List<String> titulos = tickets.stream()
        .map(Ticket::getTitulo)           // Ticket → String
        .toList();

// ── sorted: ordenar por un criterio ──────────────────────────────────────────
List<Ticket> porPrioridad = tickets.stream()
        .sorted(Comparator.comparingInt(Ticket::getPrioridad).reversed())
        .toList();

// ── Pipeline completo: filtrar + ordenar + transformar ───────────────────────
List<String> resultado = tickets.stream()
        .filter(t -> t.getStatus().equals("ABIERTO"))    // 1. solo abiertos
        .sorted(Comparator.comparing(Ticket::getTitulo)) // 2. orden A → Z
        .map(t -> t.getTitulo().toUpperCase())           // 3. a mayúsculas
        .toList();                                       // 4. materializar

// ── Agrupar por categoría ─────────────────────────────────────────────────────
Map<String, List<Ticket>> porStatus = tickets.stream()
        .collect(Collectors.groupingBy(Ticket::getStatus));

porStatus.forEach((status, lista) ->
    System.out.println(status + ": " + lista.size()));
// ABIERTO: 5   CERRADO: 12   EN_PROGRESO: 3

// ── Buscar el primero que cumple ─────────────────────────────────────────────
Optional<Ticket> masUrgente = tickets.stream()
        .filter(t -> t.getStatus().equals("ABIERTO"))
        .max(Comparator.comparingInt(Ticket::getPrioridad));
```

> 💡 **Regla:** `filter` reduce, `map` transforma, `sorted` ordena, `groupingBy` agrupa. Encadénalos en un pipeline limpio. El orden `filter → sorted → map` es el más eficiente: primero reduce la cantidad de elementos, luego los transforma.

---

## Tip 19 — "Necesito trabajar con fechas en mi sistema y no sé qué tipo usar"

### 📋 El escenario
El sistema necesita registrar cuándo se creó un ticket, calcular cuántos días lleva abierto, verificar si un vencimiento ya pasó o mostrar fechas formateadas. Java tiene múltiples tipos de fecha y es fácil elegir el incorrecto.

### ❌ El error común
```java
// ❌ Usar Date o Calendar (API antigua, mutable, thread-unsafe)
Date fechaCreacion = new Date();
Calendar cal = Calendar.getInstance();
cal.add(Calendar.DAY_OF_MONTH, 7);   // verboso y propenso a errores

// ❌ Guardar fechas como String
String fechaCierre = "2026-03-19";   // ¿cómo calculas días entre fechas?
```

### 🧠 ¿Cómo pienso esto?
```
¿Qué necesito representar?

  Solo fecha (día/mes/año)         → LocalDate
  Solo hora                        → LocalTime
  Fecha + hora                     → LocalDateTime
  Diferencia en días/meses/años    → Period
  Diferencia en horas/minutos/seg  → Duration

Regla: nunca Date, nunca Calendar, nunca String para fechas.
```

### ✅ La solución

```java
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

// ── Crear fechas ──────────────────────────────────────────────────────────────
LocalDate hoy          = LocalDate.now();                  // 2026-03-19
LocalDate vencimiento  = LocalDate.of(2026, 4, 30);
LocalDate desdeCadena  = LocalDate.parse("2026-03-19");    // ISO-8601

LocalDateTime ahora    = LocalDateTime.now();              // 2026-03-19T14:30:00
LocalDateTime creacion = LocalDateTime.of(2026, 3, 1, 9, 0);

// ── Aritmética ────────────────────────────────────────────────────────────────
LocalDate enUnaSemana  = hoy.plusDays(7);
LocalDate elMesPasado  = hoy.minusMonths(1);
LocalDateTime maniana  = ahora.plusDays(1);

// ── Comparar ──────────────────────────────────────────────────────────────────
boolean vencido = LocalDate.now().isAfter(vencimiento);
boolean esHoy   = hoy.isEqual(LocalDate.now());

// ── Calcular diferencia ───────────────────────────────────────────────────────
long diasAbierto = ChronoUnit.DAYS.between(creacion.toLocalDate(), LocalDate.now());
System.out.println("Días abierto: " + diasAbierto);

Period periodo = Period.between(creacion.toLocalDate(), LocalDate.now());
System.out.printf("Abierto hace: %d meses y %d días%n",
    periodo.getMonths(), periodo.getDays());

// ── Formatear para mostrar ────────────────────────────────────────────────────
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
System.out.println("Creado: " + creacion.format(fmt));   // "01/03/2026 09:00"
```

### 🌐 Variante en API

```java
// application.yml → para que Jackson serialice como ISO-8601 y no como timestamp
// spring:
//   jackson:
//     serialization:
//       write-dates-as-timestamps: false

record TicketResponse(
    int           id,
    String        titulo,
    LocalDateTime creadoEn,    // JSON: "2026-03-01T09:00:00"
    LocalDate     vencimiento  // JSON: "2026-04-30"
) {}
```

> 💡 **Regla:** `LocalDate` para fechas sin hora, `LocalDateTime` para timestamps de eventos. Nunca `Date`, `Calendar` ni `String` para representar fechas. En Spring Boot, configura `write-dates-as-timestamps: false` en el YAML para que las fechas se serialicen en formato legible.

---

## Tip 20 — "Tengo la misma lógica copiada en varios métodos y no sé cómo evitarlo"

### 📋 El escenario
El mismo bloque de código aparece en múltiples métodos. Cuando hay un bug o cambia la regla, debes modificarlo en todos los lugares — y es fácil olvidar uno.

*Ejemplo:* la validación de prioridad (entre 1 y 5) está copiada en `crear`, `actualizar` y `reasignar`.

### ❌ El error común
```java
// ❌ Misma lógica copiada 3 veces — si cambia el rango, hay que cambiarla en 3 lugares
public Ticket crear(TicketRequest req) {
    if (req.prioridad() < 1 || req.prioridad() > 5)
        throw new IllegalArgumentException("Prioridad inválida");
    // ...
}
public Ticket actualizar(int id, TicketRequest req) {
    if (req.prioridad() < 1 || req.prioridad() > 5)   // copia exacta
        throw new IllegalArgumentException("Prioridad inválida");
    // ...
}
public Ticket reasignar(int id, TicketRequest req) {
    if (req.prioridad() < 1 || req.prioridad() > 5)   // tercera copia
        throw new IllegalArgumentException("Prioridad inválida");
    // ...
}
```

### 🧠 ¿Cómo pienso esto?
```
Principio DRY: Don't Repeat Yourself (No te repitas)

Si copias y pegas un bloque de código, es la señal de que ese bloque
necesita su propio método o clase.

PASOS:
  1. Identificar el bloque que se repite
  2. Extraerlo a un método privado con nombre descriptivo
  3. Reemplazar cada copia por una llamada al método
  4. Si se necesita en varias clases → clase utilitaria estática o componente
```

### ✅ La solución

```java
@Service
public class TicketService {

    private static final int PRIORIDAD_MIN = 1;
    private static final int PRIORIDAD_MAX = 5;

    // ✅ Un solo lugar donde vive la regla
    private void validarPrioridad(int prioridad) {
        if (prioridad < PRIORIDAD_MIN || prioridad > PRIORIDAD_MAX) {
            throw new IllegalArgumentException(
                "La prioridad debe estar entre " + PRIORIDAD_MIN +
                " y " + PRIORIDAD_MAX + ". Recibido: " + prioridad
            );
        }
    }

    public Ticket crear(TicketRequest req) {
        validarPrioridad(req.prioridad());   // ← una línea en lugar de 3
        // ...
    }
    public Ticket actualizar(int id, TicketRequest req) {
        validarPrioridad(req.prioridad());   // ← misma llamada
        // ...
    }
    public Ticket reasignar(int id, TicketRequest req) {
        validarPrioridad(req.prioridad());   // ← misma llamada
        // ...
    }
}

// Alternativa: clase utilitaria para validaciones reutilizables entre servicios
public final class TicketValidator {

    private TicketValidator() {}   // no instanciable

    public static void validarPrioridad(int prioridad) {
        if (prioridad < 1 || prioridad > 5)
            throw new IllegalArgumentException("Prioridad inválida: " + prioridad);
    }

    public static void validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank())
            throw new IllegalArgumentException("El título no puede estar vacío");
        if (titulo.length() > 100)
            throw new IllegalArgumentException("El título no puede superar 100 caracteres");
    }
}
```

> 💡 **Regla DRY:** si copias y pegas un bloque, ese bloque merece su propio método. Un cambio en un único lugar debe ser suficiente para que el comportamiento cambie en todos los sitios que lo usan. Si la validación es compleja o se comparte entre servicios, extráela a una clase utilitaria.

---

## Tip 21 — "Uso Strings como estados en todo el código y cometo errores de tipeo"

### 📋 El escenario
El código compara `"ABIERTO"`, `"CERRADO"`, `"EN_PROGRESO"` en decenas de lugares. Un espacio extra o una mayúscula equivocada genera un bug invisible que solo se detecta en producción.

### ❌ El error común
```java
// ❌ Magic strings — Java no avisa si el valor no es válido
ticket.setStatus("ABEIRTO");   // typo silencioso
if (ticket.getStatus().equals("ABIERTO")) { ... }  // no coincide → bug
```

### 🧠 ¿Cómo pienso esto?
```
Si un campo tiene un conjunto FIJO y CONOCIDO de valores posibles,
ese campo no debería ser un String → debería ser un enum.

Ventajas del enum:
  - El compilador avisa si el valor no existe
  - El IDE autocompleta los valores válidos
  - == en lugar de equals() (los enum son singletons)
  - Puede tener métodos de negocio propios
```

### ✅ La solución

```java
// ── Definir el enum ───────────────────────────────────────────────────────────
public enum TicketStatus {
    ABIERTO,
    EN_PROGRESO,
    CERRADO,
    CANCELADO;

    // Método de negocio en el propio enum
    public boolean esTerminal() {
        return this == CERRADO || this == CANCELADO;
    }
}

// ── Usar el enum en la clase ──────────────────────────────────────────────────
public class Ticket {
    private int          id;
    private String       titulo;
    private TicketStatus status;   // ← tipo enum, no String
}

// ── Comparar con == (enum son singletons) ─────────────────────────────────────
Ticket t = new Ticket(1, "Error", TicketStatus.ABIERTO);

if (t.getStatus() == TicketStatus.ABIERTO) {   // ✅ el compilador verifica
    System.out.println("Pendiente de atención");
}
if (t.getStatus().esTerminal()) {
    System.out.println("No se puede modificar");
}

// ── Switch exhaustivo sobre enum ──────────────────────────────────────────────
String msg = switch (t.getStatus()) {
    case ABIERTO      -> "Pendiente";
    case EN_PROGRESO  -> "En curso";
    case CERRADO      -> "Resuelto";
    case CANCELADO    -> "Cancelado";
    // si falta un caso → el compilador avisa ✅
};
```

### 🌐 Variante en API
```java
// Spring convierte automáticamente String → enum en @RequestParam y @RequestBody
// Si el valor no existe en el enum → 400 Bad Request automático
@GetMapping
public ResponseEntity<List<Ticket>> listar(
        @RequestParam(required = false) TicketStatus status) { ... }
```

> 💡 **Regla:** si un campo tiene un conjunto cerrado de valores válidos, usa `enum` en lugar de `String`. El compilador garantiza que solo se usen valores del enum — sin typos, sin magic strings.

---

## Tip 22 — "Quiero obtener el máximo, mínimo o primer elemento que cumple algo"

### 📋 El escenario
De una lista de objetos necesitas encontrar el de mayor prioridad, el más reciente, el primero que cumple una condición, o saber si existe al menos uno que la cumple.

### ❌ El error común
```java
// ❌ Recorrer toda la lista aunque ya encontraste lo que buscabas
Ticket masUrgente = null;
for (Ticket t : tickets) {
    if (t.getStatus().equals("ABIERTO")) {
        masUrgente = t;   // ❌ sigue iterando — siempre queda el último, no el mayor
    }
}
```

### 🧠 ¿Cómo pienso esto?
```
¿Necesito...
  ...el PRIMERO que cumple una condición?  → filter + findFirst()
  ...el de MAYOR valor?                    → filter + max(Comparator)
  ...el de MENOR valor?                    → filter + min(Comparator)
  ...saber si EXISTE alguno?               → anyMatch()
  ...que TODOS cumplan?                    → allMatch()
  ...que NINGUNO cumpla?                   → noneMatch()

Todos devuelven Optional (puede que no haya ninguno).
```

### ✅ La solución

```java
List<Ticket> tickets = obtenerTodos();

// ── Primero que cumple ────────────────────────────────────────────────────────
Optional<Ticket> primero = tickets.stream()
        .filter(t -> t.getStatus() == TicketStatus.ABIERTO)
        .findFirst();

// ── El de mayor prioridad ─────────────────────────────────────────────────────
Optional<Ticket> masUrgente = tickets.stream()
        .filter(t -> t.getStatus() == TicketStatus.ABIERTO)
        .max(Comparator.comparingInt(Ticket::getPrioridad));

masUrgente.ifPresentOrElse(
    t  -> System.out.println("Más urgente: " + t.getTitulo()),
    () -> System.out.println("Sin tickets abiertos")
);

// ── El más reciente ───────────────────────────────────────────────────────────
Optional<Ticket> reciente = tickets.stream()
        .max(Comparator.comparing(Ticket::getCreadoEn));

// ── ¿Existe alguno con prioridad 5? ──────────────────────────────────────────
boolean hayUrgentes = tickets.stream()
        .anyMatch(t -> t.getPrioridad() == 5);

// ── ¿Todos están cerrados? ────────────────────────────────────────────────────
boolean todosCerrados = tickets.stream()
        .allMatch(t -> t.getStatus() == TicketStatus.CERRADO);

// ── ¿Ninguno está cancelado? ──────────────────────────────────────────────────
boolean sinCancelados = tickets.stream()
        .noneMatch(t -> t.getStatus() == TicketStatus.CANCELADO);
```

> 💡 **Regla:** para buscar uno solo → `findFirst()` o `max/min`. Para verificar existencia → `anyMatch/allMatch/noneMatch`. Todos devuelven `Optional` (para `findFirst/max/min`) o `boolean` — úsalos en lugar de recorrer manualmente.

---

## Tip 23 — "Necesito obtener estadísticas de una lista: total, promedio, máximo"

### 📋 El escenario
Tienes una lista de objetos y necesitas varios indicadores a la vez: suma, promedio, valor máximo y mínimo. Hacerlos en recorridos separados es ineficiente.

### ❌ El error común
```java
// ❌ Tres recorridos para tres estadísticas
double suma   = tickets.stream().mapToInt(Ticket::getPrioridad).sum();
double max    = tickets.stream().mapToInt(Ticket::getPrioridad).max().orElse(0);
double min    = tickets.stream().mapToInt(Ticket::getPrioridad).min().orElse(0);
double prom   = tickets.stream().mapToInt(Ticket::getPrioridad).average().orElse(0);
```

### 🧠 ¿Cómo pienso esto?
```
IntSummaryStatistics y DoubleSummaryStatistics calculan
todas las estadísticas en UN SOLO recorrido.

Para campos de tipo int  → mapToInt().summaryStatistics()
Para campos de tipo double → mapToDouble().summaryStatistics()
```

### ✅ La solución

```java
List<Ticket> tickets = obtenerTodos();

// ── Un solo recorrido para todas las estadísticas ─────────────────────────────
IntSummaryStatistics stats = tickets.stream()
        .mapToInt(Ticket::getPrioridad)
        .summaryStatistics();

System.out.println("Total tickets: " + stats.getCount());
System.out.println("Suma prioridades: " + stats.getSum());
System.out.printf("Prioridad prom:   %.2f%n", stats.getAverage());
System.out.println("Prioridad máx:    " + stats.getMax());
System.out.println("Prioridad mín:    " + stats.getMin());

// ── Estadísticas solo de los abiertos ─────────────────────────────────────────
IntSummaryStatistics statsAbiertos = tickets.stream()
        .filter(t -> t.getStatus() == TicketStatus.ABIERTO)
        .mapToInt(Ticket::getPrioridad)
        .summaryStatistics();

// ── Conteo por grupo con Collectors.counting() ────────────────────────────────
Map<TicketStatus, Long> conteo = tickets.stream()
        .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));
conteo.forEach((s, c) -> System.out.println(s + ": " + c));
```

> 💡 **Regla:** usa `summaryStatistics()` cuando necesites varias métricas del mismo campo — calcula todas en un solo recorrido. `Collectors.groupingBy(..., Collectors.counting())` para contar por categoría.

---

## Tip 24 — "Necesito crear una excepción con un mensaje específico para cada error de negocio"

### 📋 El escenario
Quieres comunicar errores de negocio de forma precisa: "el ticket ya está cerrado", "el usuario no tiene permiso para esto", "el producto no tiene stock". Con `IllegalArgumentException` genérica todo se mezcla.

### ❌ El error común
```java
// ❌ Misma excepción para todo — el que llama no sabe qué tipo de error manejar
throw new RuntimeException("Error");
throw new Exception("Algo salió mal");
```

### 🧠 ¿Cómo pienso esto?
```
Una excepción por cada tipo de error de negocio:
  TicketNotFoundException  → cuando no se encuentra
  TicketCerradoException   → cuando está cerrado y no permite cambios
  StockInsuficienteException → cuando no hay stock

Heredar de RuntimeException → no obliga al llamador a capturarla
Nombre descriptivo + mensaje con los datos del contexto
```

### ✅ La solución

```java
// ── Excepciones personalizadas ────────────────────────────────────────────────
public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(int id) {
        super("No se encontró el ticket con ID: " + id);
    }
}

public class TicketCerradoException extends RuntimeException {
    public TicketCerradoException(int id) {
        super("El ticket #" + id + " está cerrado y no puede modificarse");
    }
}

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String producto, int disponible, int solicitado) {
        super("Stock insuficiente para '" + producto
            + "': disponible=" + disponible + ", solicitado=" + solicitado);
    }
}

// ── Uso en el Service ─────────────────────────────────────────────────────────
public Ticket cerrar(int id) {
    Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException(id));  // 404

    if (ticket.getStatus() == TicketStatus.CERRADO) {
        throw new TicketCerradoException(id);   // 409
    }

    ticket.setStatus(TicketStatus.CERRADO);
    return ticketRepository.save(ticket);
}

// ── En consola: capturar por tipo específico ──────────────────────────────────
try {
    servicio.cerrar(99);
} catch (TicketNotFoundException e) {
    System.out.println("No encontrado: " + e.getMessage());
} catch (TicketCerradoException e) {
    System.out.println("Ya cerrado: " + e.getMessage());
}
```

> 💡 **Regla:** una excepción por tipo de error de negocio. Hereda de `RuntimeException` para no forzar `try-catch` en todos los llamadores. El mensaje debe incluir los datos del contexto (el ID, el valor que causó el error) para facilitar la depuración.

---

## Tip 25 — "Quiero devolver más de un valor desde un método"

### 📋 El escenario
Un método necesita devolver dos o tres valores relacionados: el resultado y un mensaje de estado, el precio con y sin descuento, el ticket creado y si ya existía antes.

### ❌ El error común
```java
// ❌ Modificar un parámetro de entrada para "devolver" el segundo valor
public double calcularDescuento(double precio, double[] resultado) {
    resultado[0] = precio * 0.1;  // hack: usar array como "puntero"
    return precio - resultado[0];
}
```

### 🧠 ¿Cómo pienso esto?
```
Java solo permite un valor de retorno.

Para devolver múltiples valores relacionados:
  → Crear un record que los agrupe
  → El record es inmutable, conciso y tiene equals/hashCode

No uses arrays ni colecciones para esto — pierde semántica.
```

### ✅ La solución

```java
// ── record como tipo de retorno compuesto ─────────────────────────────────────
record ResultadoDescuento(
    double precioOriginal,
    double montoDescuento,
    double precioFinal,
    String categoria     // "BÁSICO", "PREFERENTE", "VIP"
) {}

public ResultadoDescuento calcularDescuento(double precio, int puntosFidelidad) {
    double porcentaje = puntosFidelidad > 1000 ? 0.20
                      : puntosFidelidad > 500  ? 0.10
                      : 0.05;

    String categoria  = puntosFidelidad > 1000 ? "VIP"
                      : puntosFidelidad > 500  ? "PREFERENTE"
                      : "BÁSICO";

    double monto = precio * porcentaje;
    return new ResultadoDescuento(precio, monto, precio - monto, categoria);
}

// ── Uso — el record documenta qué es cada valor ───────────────────────────────
ResultadoDescuento r = calcularDescuento(50_000, 750);
System.out.println("Cliente:     " + r.categoria());
System.out.printf("Descuento:   $%,.2f%n", r.montoDescuento());
System.out.printf("Total final: $%,.2f%n", r.precioFinal());

// ── Otro ejemplo: resultado de una operación de creación ──────────────────────
record CreacionTicket(Ticket ticket, boolean eraExistente) {}

public CreacionTicket crearOActualizar(TicketRequest req) {
    Optional<Ticket> existente = ticketRepository.findByTitulo(req.titulo());
    if (existente.isPresent()) {
        return new CreacionTicket(existente.get(), true);
    }
    return new CreacionTicket(ticketRepository.save(mapear(req)), false);
}
```

> 💡 **Regla:** cuando un método necesita devolver varios valores relacionados, crea un `record` que los agrupe. El `record` tiene nombre, tipos explícitos y se lee como documentación. Nunca uses arrays ni listas para esto.

---

## Tip 26 — "El cliente envía datos que pueden o no incluir ciertos campos opcionales"

### 📋 El escenario
Algunos campos de una petición o de un objeto son opcionales: pueden llegar o no. Si usas `String` o `int`, no hay forma nativa de distinguir "no enviado" de "enviado con valor vacío o cero".

### ❌ El error común
```java
// ❌ Usar null como señal "no enviado" — poco expresivo y propenso a NPE
public void actualizar(Ticket ticket, String nuevoTitulo) {
    if (nuevoTitulo != null) {   // ¿null = no cambiar? ¿o es un bug?
        ticket.setTitulo(nuevoTitulo);
    }
}
```

### 🧠 ¿Cómo pienso esto?
```
Optional<T> expresa EXPLÍCITAMENTE "este valor puede no estar presente".

  Optional.empty()       → no está presente
  Optional.of(valor)     → está presente con ese valor
  Optional.ofNullable(v) → presente si v != null, vacío si v == null

Métodos útiles:
  .isPresent()        → ¿tiene valor?
  .get()              → obtener (lanza si vacío, usar con cuidado)
  .orElse(defecto)    → valor o defecto
  .ifPresent(accion)  → ejecutar solo si presente
```

### ✅ La solución

```java
// ── Optional como tipo de campo en DTO de actualización parcial ───────────────
record TicketPatchDto(
    Optional<String>       titulo,
    Optional<String>       descripcion,
    Optional<TicketStatus> status,
    Optional<Integer>      prioridad
) {}

// ── Service: aplicar solo los campos presentes ────────────────────────────────
public Ticket actualizarParcial(int id, TicketPatchDto patch) {
    Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException(id));

    patch.titulo()      .ifPresent(ticket::setTitulo);
    patch.descripcion() .ifPresent(ticket::setDescripcion);
    patch.status()      .ifPresent(ticket::setStatus);
    patch.prioridad()   .ifPresent(ticket::setPrioridad);

    return ticketRepository.save(ticket);
}

// ── Optional en métodos de consulta ──────────────────────────────────────────
public Optional<Ticket> buscarPorTitulo(String titulo) {
    return ticketRepository.findAll().stream()
            .filter(t -> t.getTitulo().equalsIgnoreCase(titulo))
            .findFirst();   // Optional<Ticket> — puede estar vacío
}

// ── Encadenar transformaciones ────────────────────────────────────────────────
String tituloBuscado = "Error en login";
String resultado = buscarPorTitulo(tituloBuscado)
        .map(Ticket::getTitulo)
        .map(String::toUpperCase)
        .orElse("No encontrado");
```

> 💡 **Regla:** usa `Optional<T>` cuando un valor puede legítimamente no estar presente. Evita `null` para comunicar ausencia — es invisible y propenso a NPE. En DTOs de actualización parcial, `Optional<T>` en cada campo permite distinguir "no enviado" de "enviado con valor".

---

## Tip 27 — "Manejo excepciones pero no sé cómo diferenciar el tipo de error"

### 📋 El escenario
Un bloque de código puede lanzar distintos tipos de excepciones y necesitas reaccionar diferente según cuál sea: si es un error de formato, tratar diferente que si es un error de conexión o de negocio.

### ❌ El error común
```java
// ❌ Capturar todo con Exception — se pierde información del tipo de error
try {
    procesarTicket(id);
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());  // ¿qué tipo fue?
}
```

### 🧠 ¿Cómo pienso esto?
```
Java permite múltiples bloques catch para distintos tipos:
  try {
    ...
  } catch (TipoEspecifico1 e) {  // primero los más específicos
    ...
  } catch (TipoEspecifico2 e) {
    ...
  } catch (Exception e) {        // último: el más general
    ...
  }

finally: se ejecuta SIEMPRE (con o sin excepción) — para liberar recursos.
try-with-resources: para Closeable (archivos, conexiones) → cierra automáticamente.
```

### ✅ La solución

```java
// ── Múltiples catch ───────────────────────────────────────────────────────────
try {
    int id = Integer.parseInt(input);        // puede lanzar NumberFormatException
    Ticket t = ticketService.buscar(id);     // puede lanzar TicketNotFoundException
    procesarTicket(t);

} catch (NumberFormatException e) {
    System.out.println("ID inválido — debe ser un número: " + input);

} catch (TicketNotFoundException e) {
    System.out.println("No encontrado: " + e.getMessage());

} catch (IllegalStateException e) {
    System.out.println("Estado incorrecto: " + e.getMessage());

} catch (Exception e) {
    System.out.println("Error inesperado: " + e.getMessage());
    e.printStackTrace();   // para depurar errores no previstos
}

// ── finally: código que SIEMPRE se ejecuta ───────────────────────────────────
Connection conn = null;
try {
    conn = obtenerConexion();
    // usar conn...
} catch (SQLException e) {
    System.out.println("Error BD: " + e.getMessage());
} finally {
    if (conn != null) conn.close();   // siempre se ejecuta
}

// ── try-with-resources: cierra automáticamente ────────────────────────────────
try (BufferedReader reader = new BufferedReader(new FileReader("datos.txt"))) {
    String linea;
    while ((linea = reader.readLine()) != null) {
        System.out.println(linea);
    }
}   // reader.close() se llama automáticamente
```

> 💡 **Regla:** captura siempre los tipos más específicos primero. Nunca hagas `catch (Exception e)` como única opción — pierdes información valiosa. Usa `try-with-resources` para cualquier recurso que implemente `Closeable` (archivos, conexiones, streams).

---

## Tip 28 — "Necesito transformar cada elemento de una lista y unirlos en un solo String"

### 📋 El escenario
Tienes una lista de objetos y necesitas producir un texto concatenando alguna propiedad de cada uno, con un separador entre elementos.

*Ejemplo:* construir `"Error en login, Botón roto, Página lenta"` a partir de una lista de tickets.

### ❌ El error común
```java
// ❌ Concatenar manualmente con StringBuilder → verboso
StringBuilder sb = new StringBuilder();
for (Ticket t : tickets) {
    sb.append(t.getTitulo());
    sb.append(", ");
}
String resultado = sb.toString();
// ❌ resultado tiene una coma extra al final: "Error en login, Botón roto, "
```

### 🧠 ¿Cómo pienso esto?
```
TENGO:   List<Objeto>
QUIERO:  String con propiedades unidas por separador

PASOS con Stream:
  1. map()     → extraer la propiedad String de cada objeto
  2. collect(Collectors.joining(separador)) → unir con separador

Collectors.joining tiene tres variantes:
  joining()               → sin separador
  joining(", ")           → separador entre elementos
  joining(", ", "[", "]") → separador + prefijo + sufijo
```

### ✅ La solución

```java
List<Ticket> tickets = obtenerTodos();

// ── joining: la forma idiomática ─────────────────────────────────────────────
String titulos = tickets.stream()
        .map(Ticket::getTitulo)
        .collect(Collectors.joining(", "));
System.out.println(titulos);   // "Error en login, Botón roto, Página lenta"

// ── Con prefijo y sufijo ──────────────────────────────────────────────────────
String formato = tickets.stream()
        .map(t -> "[" + t.getPrioridad() + "] " + t.getTitulo())
        .collect(Collectors.joining("\n"));
System.out.println(formato);
// [5] Error en login
// [3] Botón roto
// [2] Página lenta

// ── String.join para listas de Strings simples ────────────────────────────────
List<String> roles = List.of("ADMIN", "EDITOR", "LECTOR");
System.out.println(String.join(" | ", roles));   // "ADMIN | EDITOR | LECTOR"

// ── Con filtro previo ─────────────────────────────────────────────────────────
String soloAbiertos = tickets.stream()
        .filter(t -> t.getStatus() == TicketStatus.ABIERTO)
        .map(Ticket::getTitulo)
        .collect(Collectors.joining(", ", "Abiertos: [", "]"));
System.out.println(soloAbiertos);   // "Abiertos: [Error en login, Botón roto]"
```

> 💡 **Regla:** para unir elementos en un String, usa `Collectors.joining()` — maneja el separador sin comas extras al final. Para listas simples de String, `String.join()` es suficiente.

---

## Tip 29 — "Quiero agrupar elementos de una lista en un Map sin recorrerla manualmente"

### 📋 El escenario
Tienes una lista plana de objetos y necesitas organizarlos en grupos según alguna propiedad: todos los ABIERTO juntos, todos los CERRADO juntos; o agrupar productos por categoría, ventas por vendedor.

### ❌ El error común
```java
// ❌ Construir el Map manualmente — propenso a errores con null keys
Map<String, List<Ticket>> grupos = new HashMap<>();
for (Ticket t : tickets) {
    String key = t.getStatus().name();
    if (!grupos.containsKey(key)) {
        grupos.put(key, new ArrayList<>());
    }
    grupos.get(key).add(t);
}
```

### 🧠 ¿Cómo pienso esto?
```
Collectors.groupingBy(función de agrupación)
  → devuelve Map<K, List<V>>

Collectors.groupingBy(f1, Collectors.counting())
  → devuelve Map<K, Long> — cuenta en lugar de acumular

Collectors.groupingBy(f1, Collectors.summarizingInt(f2))
  → devuelve estadísticas por grupo
```

### ✅ La solución

```java
List<Ticket> tickets = obtenerTodos();

// ── Agrupar por status ────────────────────────────────────────────────────────
Map<TicketStatus, List<Ticket>> porStatus = tickets.stream()
        .collect(Collectors.groupingBy(Ticket::getStatus));

porStatus.forEach((status, lista) ->
    System.out.println(status + ": " + lista.size() + " tickets"));
// ABIERTO: 5   CERRADO: 12   EN_PROGRESO: 3

// ── Contar por grupo ──────────────────────────────────────────────────────────
Map<TicketStatus, Long> conteo = tickets.stream()
        .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));

// ── Suma de prioridades por grupo ─────────────────────────────────────────────
Map<TicketStatus, Integer> sumaPrioridades = tickets.stream()
        .collect(Collectors.groupingBy(
            Ticket::getStatus,
            Collectors.summingInt(Ticket::getPrioridad)
        ));

// ── Extraer solo los títulos por grupo ────────────────────────────────────────
Map<TicketStatus, List<String>> titulosPorStatus = tickets.stream()
        .collect(Collectors.groupingBy(
            Ticket::getStatus,
            Collectors.mapping(Ticket::getTitulo, Collectors.toList())
        ));

// ── toMap: cuando el resultado es un Map<K, V> (no Map<K, List>) ──────────────
Map<Integer, String> idATitulo = tickets.stream()
        .collect(Collectors.toMap(
            Ticket::getId,
            Ticket::getTitulo
        ));
System.out.println(idATitulo.get(1));   // "Error en login"
```

> 💡 **Regla:** `groupingBy` produce `Map<K, List<V>>` — para agrupar. `toMap` produce `Map<K, V>` — para indexar uno a uno (cuida que no haya claves duplicadas, o usa el tercer parámetro merge function).

---

## Tip 30 — "Necesito procesar una lista y producir un solo valor resultado"

### 📋 El escenario
Quieres reducir toda una lista a un único resultado que no es solo suma o conteo: el producto de todos los números, la concatenación de todos los elementos, el mayor de varios acumulados, etc.

### ❌ El error común
```java
// ❌ Acumulador manual que funciona pero es difícil de generalizar
double producto = 1;
for (double n : numeros) {
    producto = producto * n;
}
```

### 🧠 ¿Cómo pienso esto?
```
Stream.reduce(identidad, operación)
  identidad  → el valor neutro de la operación (0 para suma, 1 para producto)
  operación  → cómo combinar el acumulado con el elemento actual

Si no hay identidad → devuelve Optional (puede que la lista esté vacía)
```

### ✅ La solución

```java
List<Integer> numeros = List.of(1, 2, 3, 4, 5);

// ── Suma con reduce ───────────────────────────────────────────────────────────
int suma = numeros.stream()
        .reduce(0, Integer::sum);   // identidad = 0, operación = suma
System.out.println("Suma: " + suma);   // 15

// ── Producto ──────────────────────────────────────────────────────────────────
int producto = numeros.stream()
        .reduce(1, (acc, n) -> acc * n);   // identidad = 1, operación = producto
System.out.println("Producto: " + producto);   // 120

// ── Máximo manual ─────────────────────────────────────────────────────────────
Optional<Integer> maximo = numeros.stream()
        .reduce(Integer::max);   // sin identidad → Optional
maximo.ifPresent(m -> System.out.println("Máximo: " + m));  // 5

// ── Acumular objetos ──────────────────────────────────────────────────────────
List<Ticket> tickets = obtenerTodos();

// Resumen acumulado: concatenar títulos de tickets urgentes
String resumen = tickets.stream()
        .filter(t -> t.getPrioridad() >= 4)
        .map(Ticket::getTitulo)
        .reduce("Urgentes: ", (acc, titulo) -> acc + titulo + " | ");
System.out.println(resumen);

// ── En la práctica, para suma y producto hay formas más directas ──────────────
int sumaDirecta = numeros.stream().mapToInt(Integer::intValue).sum();
OptionalInt maxDirecto = numeros.stream().mapToInt(Integer::intValue).max();
```

> 💡 **Regla:** `reduce` es la operación más general de los Streams — puede expresar suma, producto, concatenación y cualquier operación de "muchos → uno". Para las operaciones más comunes (suma, max, min), usa los atajos `mapToInt().sum()`, `.max()`, `.min()` que son más legibles.

---

## Resumen del módulo 02

| Tip | Situación | Herramienta clave |
|-----|-----------|------------------|
| 16 | Comparar objetos con los mismos datos | `equals()` / `record` |
| 17 | NullPointerException al usar un resultado | `null check` / `Optional` |
| 18 | Filtrar, transformar u ordenar una lista | `filter`, `map`, `sorted`, `groupingBy` |
| 19 | Trabajar con fechas | `LocalDate`, `LocalDateTime`, `ChronoUnit` |
| 20 | Lógica duplicada en varios métodos | Extraer método privado — principio DRY |
| 21 | Magic strings con typos silenciosos | `enum` + `==` + `switch` exhaustivo |
| 22 | Encontrar el máximo, mínimo o primero | `max`, `min`, `findFirst`, `anyMatch` |
| 23 | Estadísticas de una lista | `summaryStatistics()`, `groupingBy + counting()` |
| 24 | Excepción específica para cada error de negocio | `RuntimeException` personalizada |
| 25 | Devolver más de un valor desde un método | `record` como tipo de retorno compuesto |
| 26 | Campos opcionales en objetos o peticiones | `Optional<T>` en campos del DTO |
| 27 | Diferenciar el tipo de error para reaccionar diferente | Múltiples `catch`, `finally`, `try-with-resources` |
| 28 | Unir propiedades de una lista en un String | `Collectors.joining()`, `String.join()` |
| 29 | Agrupar elementos en un Map | `groupingBy`, `toMap`, `counting()` |
| 30 | Reducir toda una lista a un solo valor | `Stream.reduce()`, `mapToInt().sum()` |

→ [Siguiente: Situaciones en REST API](./03_situaciones_api.md)
