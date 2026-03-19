# Ejercicio 15 — Simulador de banco completo

> **Nivel:** ⭐⭐⭐⭐⭐ Avanzado  
> **Conceptos:** Integración total · Arquitectura por capas · `sealed` · `record` · `Optional` · Streams · Excepciones · Genéricos · Lambdas · Interfaces · Herencia · Lógica proposicional  
> **Tiempo estimado:** 120–180 min

---

## 🏦 Contexto

**BancoDigital SA** necesita un simulador de sistema bancario que cubra las operaciones más comunes: creación de cuentas, depósitos, retiros, transferencias y generación de cartolas. El sistema debe estar bien estructurado por capas, ser robusto ante errores y generar reportes precisos.

> ⚠️ Este es el ejercicio más completo del mini curso. Integra **todos** los conceptos vistos. Tómate el tiempo necesario para planificarlo antes de codificar.

---

## 📐 Arquitectura esperada

```
bancodigital/
├── model/          (entidades y records)
│   ├── Cliente.java
│   ├── TipoCuenta.java         (enum)
│   ├── Cuenta.java             (abstract)
│   ├── CuentaCorriente.java
│   ├── CuentaAhorro.java
│   ├── CuentaVista.java
│   ├── Movimiento.java         (record)
│   └── EstadoMovimiento.java   (enum)
│
├── exception/      (jerarquía de excepciones)
│   ├── BancoException.java
│   ├── SaldoInsuficienteException.java
│   ├── CuentaBloqueadaException.java
│   ├── LimiteDiarioExcedidoException.java
│   └── CuentaNoEncontradaException.java
│
├── service/        (lógica de negocio)
│   ├── CuentaService.java      (interface)
│   ├── TransferenciaService.java (interface)
│   └── impl/
│       ├── CuentaServiceImpl.java
│       └── TransferenciaServiceImpl.java
│
├── repository/     (persistencia simulada)
│   └── BancoRepository.java    (genérico)
│
└── BancoDigitalApp.java        (main)
```

---

## 📋 Enunciado detallado

### Capa: `model`

#### `enum TipoCuenta`
```java
public enum TipoCuenta {
    CORRIENTE("Cuenta Corriente", 5_000_000, true),
    AHORRO("Cuenta de Ahorro", 2_000_000, false),
    VISTA("Cuenta Vista / RUT", 500_000, false);

    private final String nombre;
    private final double limiteDiario;      // límite de transferencia diaria
    private final boolean permiteSobregiro;
    // constructor, getters
}
```

#### `record Movimiento`
```java
public record Movimiento(
    String id,
    String cuentaOrigen,
    String cuentaDestino,   // null si es depósito/retiro
    double monto,
    String tipo,            // "DEPOSITO", "RETIRO", "TRANSFERENCIA"
    String descripcion,
    LocalDateTime fecha,
    EstadoMovimiento estado
) {}
```

#### Clase abstracta `Cuenta`

**Atributos protegidos:**
- `numeroCuenta` (`String`)
- `titular` (`Cliente`)
- `saldo` (`double`)
- `tipo` (`TipoCuenta`)
- `bloqueada` (`boolean`)
- `historial` (`List<Movimiento>`)
- `totalRetiradoHoy` (`double`)

**Métodos abstractos:**
- `double calcularComisionTransferencia(double monto)` → cada tipo cobra distinto.
- `boolean aceptaSobregiro()` → según el tipo.

**Métodos concretos:**
- `depositar(double monto)`: valida `monto > 0`, agrega al saldo, registra `Movimiento`.
- `retirar(double monto)`: valida condiciones, descuenta saldo, registra `Movimiento`.
- `getHistorial()`: retorna lista inmutable.
- `getSaldo()`, `getNumeroCuenta()`, `getTitular()`, getters en general.

**Validaciones en `retirar` (lógica proposicional):**
- **p:** `monto > 0`
- **q:** `!bloqueada`
- **r:** `saldo + (aceptaSobregiro() ? margenSobregiro() : 0) >= monto`
- **s:** `totalRetiradoHoy + monto <= tipo.getLimiteDiario()`
- Condición para retirar: `p ∧ q ∧ r ∧ s`
- Si `¬p` → `IllegalArgumentException("Monto inválido")`
- Si `¬q` → `CuentaBloqueadaException`
- Si `¬r` → `SaldoInsuficienteException`
- Si `¬s` → `LimiteDiarioExcedidoException`

#### Subclases

**`CuentaCorriente extends Cuenta`:**
- `margenSobregiro`: $500.000 fijo.
- `calcularComisionTransferencia(monto)` → 0.5% del monto (mínimo $500).
- `aceptaSobregiro()` → `true`.

**`CuentaAhorro extends Cuenta`:**
- Sin sobregiro.
- `calcularComisionTransferencia(monto)` → $300 fijo si monto < $100.000; gratis si monto >= $100.000.
- `aceptaSobregiro()` → `false`.

**`CuentaVista extends Cuenta`:**
- Sin sobregiro. Límite diario de $500.000.
- `calcularComisionTransferencia(monto)` → gratis siempre.
- `aceptaSobregiro()` → `false`.
- Máximo **3 transferencias por día** (agrega validación adicional).

---

### Capa: `exception`

Jerarquía completa (todas `checked`, heredan de `BancoException extends Exception`):

| Excepción | Atributos extra | Mensaje |
|-----------|----------------|---------|
| `SaldoInsuficienteException` | `saldoActual`, `montoSolicitado` | `"Saldo insuficiente: disponible $X, solicitado $Y"` |
| `CuentaBloqueadaException` | `numeroCuenta` | `"La cuenta %s está bloqueada"` |
| `LimiteDiarioExcedidoException` | `limiteRestante`, `montoIntentado` | `"Límite diario excedido: restante $X, intentado $Y"` |
| `CuentaNoEncontradaException` | `numeroCuenta` | `"No existe la cuenta: %s"` |

---

### Capa: `service`

#### Interface `CuentaService`

```java
public interface CuentaService {
    Cuenta crearCuenta(Cliente cliente, TipoCuenta tipo);
    void depositar(String numeroCuenta, double monto) throws BancoException;
    void retirar(String numeroCuenta, double monto) throws BancoException;
    Optional<Cuenta> buscarCuenta(String numeroCuenta);
    List<Movimiento> obtenerHistorial(String numeroCuenta) throws BancoException;
    void bloquearCuenta(String numeroCuenta) throws BancoException;
    void imprimirCartola(String numeroCuenta) throws BancoException;
}
```

#### Interface `TransferenciaService`

```java
public interface TransferenciaService {
    void transferir(String cuentaOrigen, String cuentaDestino, double monto, String descripcion)
        throws BancoException;
    List<Movimiento> getTransferenciasEntreFechas(String numeroCuenta,
        LocalDate desde, LocalDate hasta) throws BancoException;
}
```

---

### Capa: `repository`

```java
public class BancoRepository<T extends Cuenta> {
    private final Map<String, T> cuentas = new HashMap<>();

    public void guardar(T cuenta) { cuentas.put(cuenta.getNumeroCuenta(), cuenta); }
    public Optional<T> buscar(String numero) { return Optional.ofNullable(cuentas.get(numero)); }
    public List<T> listarTodas() { return new ArrayList<>(cuentas.values()); }
    public boolean existe(String numero) { return cuentas.containsKey(numero); }
    public int total() { return cuentas.size(); }
}
```

---

## 🚫 Restricciones

- La arquitectura por capas debe respetarse: el `main` solo habla con `Service`; el `Service` habla con `Repository` y `Cuenta`; `Cuenta` no conoce `Service`.
- Todas las excepciones del dominio son **checked**.
- El `main` debe usar `try/catch` diferenciados para cada excepción.
- `Movimiento` debe ser un `record`.
- Los métodos de `Service` que generan listas deben usar Streams.
- `imprimirCartola` debe usar Stream para ordenar por fecha y formatear.
- `getTransferenciasEntreFechas` debe usar `filter` con `LocalDate`.
- El número de cuenta se autogenera con un UUID o contador estático.

---

## 📥 Ejemplo de uso (main)

```java
CuentaServiceImpl cuentaService = new CuentaServiceImpl(new BancoRepository<>());
TransferenciaService transService = new TransferenciaServiceImpl(cuentaService);

Cliente cli1 = new Cliente("11111111-1", "Ana García", "ana@mail.cl");
Cliente cli2 = new Cliente("22222222-2", "Pedro López", "pedro@mail.cl");

Cuenta cc1 = cuentaService.crearCuenta(cli1, TipoCuenta.CORRIENTE);
Cuenta ca1 = cuentaService.crearCuenta(cli1, TipoCuenta.AHORRO);
Cuenta cv2 = cuentaService.crearCuenta(cli2, TipoCuenta.VISTA);

cuentaService.depositar(cc1.getNumeroCuenta(), 1_500_000);
cuentaService.depositar(cv2.getNumeroCuenta(), 200_000);

transService.transferir(cc1.getNumeroCuenta(), cv2.getNumeroCuenta(), 300_000, "Pago arriendo");
cuentaService.retirar(cv2.getNumeroCuenta(), 50_000);

// Intentar transferir más del límite diario desde CuentaVista
try {
    transService.transferir(cv2.getNumeroCuenta(), cc1.getNumeroCuenta(), 600_000, "Prueba límite");
} catch (LimiteDiarioExcedidoException e) {
    System.out.println("❌ " + e.getMessage());
}

cuentaService.imprimirCartola(cc1.getNumeroCuenta());
cuentaService.imprimirCartola(cv2.getNumeroCuenta());
```

---

## 📤 Salidas esperadas

```
╔══════════════════════════════════════════════════════╗
║         CARTOLA — BANCO DIGITAL SA                  ║
╠══════════════════════════════════════════════════════╣
║ Titular:  Ana García                                ║
║ RUT:      11111111-1                                ║
║ Cuenta:   CC-0001 (Cuenta Corriente)                ║
║ Saldo:    $1.198.500                                ║
╠══════════════════════════════════════════════════════╣
║ MOVIMIENTOS                                         ║
╠══════════════════════════════════════════════════════╣
║ 19/03/2026 10:15  DEPÓSITO       +$1.500.000        ║
║ 19/03/2026 10:16  TRANSFERENCIA  -$300.000          ║
║                   → Pedro López (CV-0001)           ║
║                   Comisión: -$1.500                 ║
╠══════════════════════════════════════════════════════╣
║ Total depósitos:      $1.500.000                    ║
║ Total retiros/trans:  -$301.500                     ║
╚══════════════════════════════════════════════════════╝

❌ LimiteDiarioExcedidoException: Límite diario excedido: restante $150.000, intentado $600.000

╔══════════════════════════════════════════════════════╗
║         CARTOLA — BANCO DIGITAL SA                  ║
╠══════════════════════════════════════════════════════╣
║ Titular:  Pedro López                               ║
║ RUT:      22222222-2                                ║
║ Cuenta:   CV-0001 (Cuenta Vista / RUT)              ║
║ Saldo:    $450.000                                  ║
╠══════════════════════════════════════════════════════╣
║ MOVIMIENTOS                                         ║
╠══════════════════════════════════════════════════════╣
║ 19/03/2026 10:15  DEPÓSITO       +$200.000          ║
║ 19/03/2026 10:16  TRANSFERENCIA  +$300.000          ║
║                   ← Ana García (CC-0001)            ║
║ 19/03/2026 10:17  RETIRO         -$50.000           ║
╠══════════════════════════════════════════════════════╣
║ Total depósitos/rec:  $500.000                      ║
║ Total retiros/trans:  -$50.000                      ║
╚══════════════════════════════════════════════════════╝
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Validaciones en retirar con proposiciones</summary>

```java
public void retirar(double monto) throws BancoException {
    boolean p = monto > 0;
    boolean q = !this.bloqueada;
    double margen = aceptaSobregiro() ? margenSobregiro() : 0;
    boolean r = this.saldo + margen >= monto;
    boolean s = this.totalRetiradoHoy + monto <= this.tipo.getLimiteDiario();

    if (!p) throw new IllegalArgumentException("El monto debe ser positivo");
    if (!q) throw new CuentaBloqueadaException(this.numeroCuenta);
    if (!r) throw new SaldoInsuficienteException(this.saldo, monto);
    if (!s) throw new LimiteDiarioExcedidoException(
        tipo.getLimiteDiario() - totalRetiradoHoy, monto);

    this.saldo -= monto;
    this.totalRetiradoHoy += monto;
    registrarMovimiento("RETIRO", null, monto, "Retiro en efectivo");
}
```
</details>

<details>
<summary>Pista 2 — getTransferenciasEntreFechas con Stream</summary>

```java
public List<Movimiento> getTransferenciasEntreFechas(String numero,
        LocalDate desde, LocalDate hasta) throws BancoException {
    Cuenta cuenta = obtenerCuentaOFallar(numero);
    return cuenta.getHistorial().stream()
        .filter(m -> m.tipo().equals("TRANSFERENCIA"))
        .filter(m -> {
            LocalDate fechaMov = m.fecha().toLocalDate();
            return !fechaMov.isBefore(desde) && !fechaMov.isAfter(hasta);
        })
        .sorted(Comparator.comparing(Movimiento::fecha))
        .collect(Collectors.toList());
}
```
</details>

<details>
<summary>Pista 3 — Generar número de cuenta</summary>

```java
private static int contadorCC = 0;
private static int contadorCA = 0;
private static int contadorCV = 0;

private String generarNumeroCuenta(TipoCuenta tipo) {
    return switch (tipo) {
        case CORRIENTE -> "CC-%04d".formatted(++contadorCC);
        case AHORRO    -> "CA-%04d".formatted(++contadorCA);
        case VISTA     -> "CV-%04d".formatted(++contadorCV);
    };
}
```
</details>

<details>
<summary>Pista 4 — Planificación sugerida</summary>

Implementa en este orden para minimizar errores de compilación:

1. `enum TipoCuenta` y `enum EstadoMovimiento`
2. `record Movimiento` y clase `Cliente`
3. Jerarquía de excepciones
4. `Cuenta` (abstracta) → `CuentaCorriente`, `CuentaAhorro`, `CuentaVista`
5. `BancoRepository<T>`
6. Interfaces `CuentaService` y `TransferenciaService`
7. Implementaciones `CuentaServiceImpl` y `TransferenciaServiceImpl`
8. `BancoDigitalApp` (main con casos de prueba)
</details>

---

## 🧠 Reflexión final

1. ¿Por qué la capa `Cuenta` no debe conocer la capa `Service`? ¿Qué principio SOLID describe esto?
2. Construye la tabla de verdad completa de la condición de retiro `p ∧ q ∧ r ∧ s` (16 filas). ¿En cuántas combinaciones se permite el retiro?
3. ¿Cómo adaptarías `BancoRepository<T extends Cuenta>` para persistir los datos en un archivo JSON en lugar de en memoria?
4. Si añadieras notificaciones por email al realizar una transferencia, ¿en qué capa lo implementarías y por qué?
5. ¿Cómo usarías este simulador como base para un proyecto Spring Boot? Identifica qué clases se convertirían en `@Entity`, `@Repository`, `@Service` y `@RestController`.

---

## 🏆 Desafío adicional (opcional)

Una vez que el simulador funcione, intenta agregar:

- **Multi-moneda:** soporte para CLP y USD, con conversión automática en transferencias entre cuentas de distinta moneda.
- **Interés automático:** las `CuentaAhorro` generan un 0.3% mensual; implementa un método `aplicarInteresDelMes()`.
- **Límite de intentos:** bloqueo automático de cuenta tras 3 retiros fallidos consecutivos por saldo insuficiente.

---

*[← Ejercicio anterior](./14_red_social_universitaria.md) · [Volver al índice](./README.md)*

