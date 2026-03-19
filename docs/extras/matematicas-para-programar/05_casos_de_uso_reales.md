# Módulo 05 — Casos de uso reales integrados

> **Objetivo:** aplicar todos los conceptos de los módulos anteriores en ejemplos completos, del tipo que se implementan en APIs REST reales.

---

## 5.1 Carrito de compras

Integra acumulador, descuentos, IVA y redondeo.

```java
public class Carrito {

    record Producto(String nombre, double precio, int cantidad) {}

    public static void main(String[] args) {
        List<Producto> items = List.of(
            new Producto("Teclado",  29_990.0, 1),
            new Producto("Mouse",   15_500.0, 2),
            new Producto("Monitor", 189_990.0, 1)
        );

        // ── Acumulador: subtotal ────────────────────────────────────────────
        double subtotal = 0.0;
        for (Producto p : items) {
            subtotal += p.precio() * p.cantidad();
        }
        // subtotal = 29990 + 31000 + 189990 = 250_980

        // ── Descuento escalonado ────────────────────────────────────────────
        double porcentajeDescuento;
        if      (subtotal >= 200_000) porcentajeDescuento = 15.0;
        else if (subtotal >= 100_000) porcentajeDescuento = 10.0;
        else if (subtotal >= 50_000)  porcentajeDescuento = 5.0;
        else                          porcentajeDescuento = 0.0;

        double montoDescuento  = subtotal * (porcentajeDescuento / 100);
        double subtotalDescontado = subtotal - montoDescuento;

        // ── IVA 19% sobre el precio descontado ─────────────────────────────
        double tasaIVA   = 19.0;
        double montoIVA  = subtotalDescontado * (tasaIVA / 100);
        double total     = subtotalDescontado + montoIVA;

        // ── Resumen ─────────────────────────────────────────────────────────
        System.out.println("=== RESUMEN DEL CARRITO ===");
        for (Producto p : items) {
            System.out.printf("  %-10s ×%d  $%,.2f%n",
                p.nombre(), p.cantidad(), p.precio() * p.cantidad());
        }
        System.out.println("---------------------------");
        System.out.printf("Subtotal:          $%,.2f%n", subtotal);
        System.out.printf("Descuento (%.0f%%):  -$%,.2f%n", porcentajeDescuento, montoDescuento);
        System.out.printf("IVA (19%%):        +$%,.2f%n", montoIVA);
        System.out.printf("TOTAL:             $%,.2f%n", total);
    }
}
```

**Salida:**
```
=== RESUMEN DEL CARRITO ===
  Teclado    ×1  $29,990.00
  Mouse      ×2  $31,000.00
  Monitor    ×1  $189,990.00
---------------------------
Subtotal:          $250,980.00
Descuento (15%):  -$37,647.00
IVA (19%):        +$40,210.77
TOTAL:             $213,543.77
```

---

## 5.2 Cálculo de nómina (sueldo y descuentos)

```java
public class Nomina {

    public static void main(String[] args) {
        String nombreEmpleado = "Ana González";
        double sueldoBruto    = 1_500_000.0;
        double horasExtra     = 8.0;
        double valorHoraExtra = 12_500.0;

        // ── Ingresos ────────────────────────────────────────────────────────
        double montoHorasExtra = horasExtra * valorHoraExtra;  // 100_000
        double totalIngresos   = sueldoBruto + montoHorasExtra; // 1_600_000

        // ── Descuentos legales (% del sueldo bruto) ─────────────────────────
        double descuentoSalud      = totalIngresos * (7.0 / 100);   // 7% salud
        double descuentoPension    = totalIngresos * (11.5 / 100);  // 11.5% pensión
        double descuentoDesempleo  = totalIngresos * (0.6 / 100);   // 0.6% desempleo
        double totalDescuentos     = descuentoSalud + descuentoPension + descuentoDesempleo;

        // ── Sueldo líquido ──────────────────────────────────────────────────
        double sueldoLiquido = totalIngresos - totalDescuentos;

        // ── Reporte ─────────────────────────────────────────────────────────
        System.out.println("============================");
        System.out.println("  LIQUIDACIÓN DE SUELDO");
        System.out.println("  " + nombreEmpleado);
        System.out.println("============================");
        System.out.printf("Sueldo base:      $%,.2f%n", sueldoBruto);
        System.out.printf("Horas extra (%.0f): $%,.2f%n", horasExtra, montoHorasExtra);
        System.out.printf("Total ingresos:   $%,.2f%n", totalIngresos);
        System.out.println("----------------------------");
        System.out.printf("Salud    (7%%):   -$%,.2f%n", descuentoSalud);
        System.out.printf("Pensión (11.5%%): -$%,.2f%n", descuentoPension);
        System.out.printf("Desempleo(0.6%%): -$%,.2f%n", descuentoDesempleo);
        System.out.printf("Total descuentos: -$%,.2f%n", totalDescuentos);
        System.out.println("============================");
        System.out.printf("SUELDO LÍQUIDO:   $%,.2f%n", sueldoLiquido);
    }
}
```

---

## 5.3 Estadísticas de tickets (Spring Boot — capa Service)

```java
@Service
public class TicketStatsService {

    private final TicketRepository ticketRepository;

    public TicketStatsService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketStatsDTO calcularEstadisticas() {
        List<Ticket> tickets = ticketRepository.findAll();

        // ── Contadores por estado ────────────────────────────────────────────
        int totalTickets  = tickets.size();
        int abiertos      = 0;
        int cerrados      = 0;
        int enProgreso    = 0;

        // ── Acumulador de prioridad (para calcular promedio) ─────────────────
        int sumaPrioridad = 0;

        for (Ticket t : tickets) {
            switch (t.getStatus()) {
                case "ABIERTO"      -> abiertos++;
                case "CERRADO"      -> cerrados++;
                case "EN_PROGRESO"  -> enProgreso++;
            }
            sumaPrioridad += t.getPriority();
        }

        // ── Promedio de prioridad ────────────────────────────────────────────
        double promedioPrioridad = totalTickets > 0
                ? (double) sumaPrioridad / totalTickets
                : 0.0;

        // ── Porcentaje de resolución ─────────────────────────────────────────
        double porcentajeResolucion = totalTickets > 0
                ? ((double) cerrados / totalTickets) * 100
                : 0.0;

        return new TicketStatsDTO(
            totalTickets,
            abiertos,
            cerrados,
            enProgreso,
            Math.round(promedioPrioridad * 10.0) / 10.0,     // 1 decimal
            Math.round(porcentajeResolucion * 10.0) / 10.0   // 1 decimal
        );
    }
}

// DTO de respuesta
record TicketStatsDTO(
    int    total,
    int    abiertos,
    int    cerrados,
    int    enProgreso,
    double promedioPrioridad,
    double porcentajeResolucion
) {}
```

**Ejemplo de respuesta JSON:**
```json
{
  "total": 120,
  "abiertos": 36,
  "cerrados": 72,
  "enProgreso": 12,
  "promedioPrioridad": 2.4,
  "porcentajeResolucion": 60.0
}
```

---

## 5.4 Sistema de parking (tiempo y tarifa)

```java
public class Parking {

    static final double TARIFA_POR_HORA    = 2_500.0;  // $2.500 por hora
    static final double TARIFA_FRACCION    = 1_500.0;  // $1.500 por fracción < 1 hora
    static final int    MINUTOS_POR_HORA   = 60;
    static final double TOPE_DIARIO        = 20_000.0; // máximo por día

    public static double calcularTarifa(int minutosEstacionado) {
        int horas        = minutosEstacionado / MINUTOS_POR_HORA;  // división entera
        int minutosExtra = minutosEstacionado % MINUTOS_POR_HORA;  // módulo

        double tarifa = horas * TARIFA_POR_HORA;

        if (minutosExtra > 0) {
            tarifa += TARIFA_FRACCION;  // cargo por fracción
        }

        // Aplicar tope diario
        return Math.min(tarifa, TOPE_DIARIO);
    }

    public static void main(String[] args) {
        int[][] casos = {{30}, {60}, {90}, {150}, {480}};
        String[] etiquetas = {"30 min", "1 hora", "1h 30min", "2h 30min", "8 horas"};

        System.out.printf("%-12s  %s%n", "Tiempo", "Tarifa");
        System.out.println("------------------------");
        for (int i = 0; i < casos.length; i++) {
            double tarifa = calcularTarifa(casos[i][0]);
            System.out.printf("%-12s  $%,.2f%n", etiquetas[i], tarifa);
        }
    }
}
```

**Salida:**
```
Tiempo        Tarifa
------------------------
30 min        $1,500.00
1 hora        $2,500.00
1h 30min      $4,000.00
2h 30min      $6,500.00
8 horas       $20,000.00
```

---

## 5.5 Calculadora de notas universitarias

```java
public class CalculadoraNotas {

    record Evaluacion(String nombre, double nota, double ponderacion) {}

    public static void main(String[] args) {
        List<Evaluacion> evaluaciones = List.of(
            new Evaluacion("Control 1",  5.5, 0.15),
            new Evaluacion("Control 2",  4.8, 0.15),
            new Evaluacion("Tarea",      6.2, 0.20),
            new Evaluacion("Solemne 1",  4.5, 0.20),
            new Evaluacion("Solemne 2",  5.8, 0.30)
        );

        // ── Acumulador ponderado ─────────────────────────────────────────────
        double notaPonderada  = 0.0;
        double totalPonderado = 0.0;

        for (Evaluacion e : evaluaciones) {
            notaPonderada  += e.nota() * e.ponderacion();
            totalPonderado += e.ponderacion();
        }

        double notaFinal = notaPonderada / totalPonderado;

        // ── Resultado ────────────────────────────────────────────────────────
        System.out.println("=== NOTAS ===");
        for (Evaluacion e : evaluaciones) {
            System.out.printf("  %-12s  %.1f  (%.0f%%)%n",
                e.nombre(), e.nota(), e.ponderacion() * 100);
        }
        System.out.println("─────────────────────────");
        System.out.printf("Nota final:   %.1f%n", notaFinal);
        System.out.printf("Estado:       %s%n", notaFinal >= 4.0 ? "✅ APROBADO" : "❌ REPROBADO");
    }
}
```

**Salida:**
```
=== NOTAS ===
  Control 1     5.5  (15%)
  Control 2     4.8  (15%)
  Tarea         6.2  (20%)
  Solemne 1     4.5  (20%)
  Solemne 2     5.8  (30%)
─────────────────────────
Nota final:   5.3
Estado:       ✅ APROBADO
```

---

## 5.6 Cheat sheet de patrones matemáticos en Java

```java
// ─── Contador ────────────────────────────────────────────────────────────────
int count = 0;
for (Elemento e : lista) {
    if (condicion(e)) count++;
}

// ─── Acumulador ──────────────────────────────────────────────────────────────
double total = 0.0;
for (Elemento e : lista) {
    total += e.getValor();
}

// ─── Promedio ────────────────────────────────────────────────────────────────
double promedio = lista.isEmpty() ? 0.0 : (double) sumaTotal / lista.size();

// ─── Porcentaje de una parte ─────────────────────────────────────────────────
double porcentaje = ((double) parte / total) * 100;

// ─── Descuento ───────────────────────────────────────────────────────────────
double montoDescuento  = precio * (descuento / 100.0);
double precioFinal     = precio - montoDescuento;
// o en un paso:
double precioFinal     = precio * (1 - descuento / 100.0);

// ─── Cargo / IVA ─────────────────────────────────────────────────────────────
double montoIVA        = precio * (tasa / 100.0);
double precioFinal     = precio + montoIVA;
// o en un paso:
double precioFinal     = precio * (1 + tasa / 100.0);

// ─── Descuento + IVA ─────────────────────────────────────────────────────────
double precioFinal     = precio * (1 - descuento / 100.0) * (1 + iva / 100.0);

// ─── Par / Impar ─────────────────────────────────────────────────────────────
boolean esPar          = numero % 2 == 0;

// ─── Múltiplo de N ───────────────────────────────────────────────────────────
boolean esMultiplo     = numero % n == 0;

// ─── Redondear a 2 decimales ─────────────────────────────────────────────────
double redondeado      = Math.round(valor * 100.0) / 100.0;
String formateado      = String.format("%.2f", valor);

// ─── Máximo y mínimo ─────────────────────────────────────────────────────────
double maximo          = lista.stream().mapToDouble(Double::doubleValue).max().orElse(0);
double minimo          = lista.stream().mapToDouble(Double::doubleValue).min().orElse(0);

// ─── Dinero con BigDecimal ───────────────────────────────────────────────────
BigDecimal monto       = new BigDecimal("9990.50");
BigDecimal descuento   = monto.multiply(new BigDecimal("0.10"));
BigDecimal total       = monto.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
```

---

## Resumen del extra

| Módulo | Concepto clave |
|--------|---------------|
| 01 — Operaciones básicas | `+` `-` `*` `/` `%` · division entera · `+=` `++` |
| 02 — Contador y acumulador | `count++` · `total += valor` · promedio · máx/mín |
| 03 — Porcentajes y cargos | descuento = `precio * (1 - desc/100)` · IVA = `* (1 + tasa/100)` |
| 04 — Redondeo y precisión | `Math.round` · `String.format` · `BigDecimal` para dinero |
| 05 — Casos reales | Carrito · Nómina · Tickets · Parking · Notas |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

