# Módulo 13 — Patrones de diseño: Comportamiento

> **Objetivo:** conocer los patrones de comportamiento del catálogo GoF, entender qué problema de comunicación o distribución de responsabilidades resuelve cada uno y cómo se implementan en Java.

---

## ¿Qué son los patrones de comportamiento?

Los patrones de comportamiento se ocupan de **cómo los objetos interactúan y se comunican entre sí**, y de **cómo se distribuyen las responsabilidades**. No se preguntan cómo se crean ni cómo se ensamblan los objetos — se preguntan cómo *actúan* y *colaboran*.

> Si los estructurales son sobre arquitectura, los de comportamiento son sobre conversaciones: quién habla con quién, cuándo y qué dice.

---

## Índice

1. [Observer (Observador)](#1-observer-observador)
2. [Strategy (Estrategia)](#2-strategy-estrategia)
3. [Command (Comando)](#3-command-comando)
4. [Template Method (Método Plantilla)](#4-template-method-método-plantilla)
5. [Chain of Responsibility (Cadena de Responsabilidad)](#5-chain-of-responsibility-cadena-de-responsabilidad)
6. [State (Estado)](#6-state-estado)
7. [Tabla comparativa](#7-tabla-comparativa)
8. [Literatura recomendada](#8-literatura-recomendada)
9. [Enlaces de interés](#9-enlaces-de-interés)

---

## 1. Observer (Observador)

### 📖 Definición

Define una dependencia **uno-a-muchos** entre objetos: cuando un objeto cambia de estado, **todos sus dependientes son notificados automáticamente**. El objeto que genera eventos se llama **sujeto** (*subject* o *publisher*); los que reaccionan se llaman **observadores** (*observers* o *subscribers*).

> **Analogía:** una suscripción a un canal de YouTube. Cuando el canal sube un video (evento), todos los suscriptores reciben una notificación. El canal no sabe cuántos suscriptores tiene ni qué hacen con la notificación.

### 🌍 Casos de uso reales

- **Notificaciones al crear un ticket de soporte:** cuando se crea un ticket, se notifica al agente asignado por email, se crea una entrada en el log de auditoría y se actualiza el contador del dashboard — tres observadores del mismo evento.
- **Sistema de eventos de UI (click, hover, submit):** el botón "Enviar" es el sujeto. El formulario, el validador y el tracker de analytics son observadores que reaccionan al click.
- **Mercado financiero en tiempo real:** cuando el precio de una acción cambia, todos los traders, alertas y gráficos suscritos a esa acción se actualizan.
- **Spring Application Events:** `ApplicationEventPublisher.publishEvent(evento)` notifica a todos los `@EventListener` registrados en el contexto.
- **Reactive Streams (RxJava, Spring WebFlux):** la programación reactiva es Observer en su esencia — un stream de datos que notifica a sus suscriptores.
- **WebSockets:** el servidor es el sujeto; todos los clientes conectados son observadores que reciben actualizaciones en tiempo real.

### ☕ Implementación en Java

```java
// ── Interfaz del observador ──
public interface ObservadorTicket {
    void onTicketCreado(Ticket ticket);
}

// ── Sujeto: gestiona la lista de observadores y lanza eventos ──
public class SistemaTickets {

    private final List<ObservadorTicket> observadores = new ArrayList<>();

    public void suscribir(ObservadorTicket obs)   { observadores.add(obs); }
    public void desuscribir(ObservadorTicket obs) { observadores.remove(obs); }

    public Ticket crearTicket(String titulo, String clienteId) {
        Ticket ticket = new Ticket(titulo, clienteId);
        // ... guardar en BD ...
        notificar(ticket); // avisar a todos los observadores
        return ticket;
    }

    private void notificar(Ticket ticket) {
        for (ObservadorTicket obs : observadores) {
            obs.onTicketCreado(ticket); // cada uno reacciona a su manera
        }
    }
}

// ── Observadores concretos ──
public class NotificadorEmail implements ObservadorTicket {
    @Override
    public void onTicketCreado(Ticket ticket) {
        System.out.println("Email enviado al agente sobre ticket: " + ticket.getTitulo());
    }
}

public class AuditoriaLogger implements ObservadorTicket {
    @Override
    public void onTicketCreado(Ticket ticket) {
        System.out.println("Auditoría: ticket " + ticket.getId() + " creado a las " + LocalDateTime.now());
    }
}

public class DashboardActualizador implements ObservadorTicket {
    @Override
    public void onTicketCreado(Ticket ticket) {
        System.out.println("Dashboard: contador de tickets abiertos +1");
    }
}

// ── Uso ──
SistemaTickets sistema = new SistemaTickets();
sistema.suscribir(new NotificadorEmail());
sistema.suscribir(new AuditoriaLogger());
sistema.suscribir(new DashboardActualizador());

sistema.crearTicket("Error en checkout", "cliente-42");
// → Email enviado...
// → Auditoría: ticket creado a las...
// → Dashboard: contador +1
```

> 💡 En Java estándar existen `java.util.Observer`/`Observable` (deprecated desde Java 9) y el modelo de eventos de Swing. Spring usa `ApplicationEvent` + `@EventListener` como su implementación.

---

## 2. Strategy (Estrategia)

### 📖 Definición

Define una familia de algoritmos, encapsula cada uno y los hace **intercambiables**. Permite que el algoritmo varíe independientemente de los clientes que lo usan.

> **Analogía:** GPS con diferentes modos de ruta. Tienes el mismo destino, pero puedes elegir la estrategia: "ruta más rápida", "evitar peajes", "evitar autopistas", "ruta turística". El GPS no cambia — solo cambia la estrategia de cálculo.

### 🌍 Casos de uso reales

- **Cálculo de descuentos según tipo de cliente:** cliente normal → sin descuento; cliente VIP → 15% off; cliente corporativo → precio negociado fijo. El proceso de pago es el mismo; la estrategia de precio varía.
- **Ordenamiento de resultados de búsqueda:** "ordenar por precio", "ordenar por relevancia", "ordenar por popularidad" — el mismo conjunto de productos, distintos algoritmos de ordenamiento.
- **Métodos de pago intercambiables:** el flujo de checkout es el mismo; la estrategia de cobro cambia (tarjeta, transferencia, efectivo, crypto).
- **Validación de documentos:** validar un RUT chileno, un DNI argentino o un NIF español requieren algoritmos distintos. El formulario llama a `validar(documento)` con la estrategia correcta según el país.
- **Compresión de archivos:** ZIP, GZIP, LZ4 son estrategias distintas de compresión. El código que comprime no sabe cuál se usa.

### ☕ Implementación en Java

```java
// ── Interfaz de estrategia ──
public interface EstrategiaDescuento {
    double calcular(double precioOriginal);
    String getDescripcion();
}

// ── Estrategias concretas ──
public class SinDescuento implements EstrategiaDescuento {
    @Override public double calcular(double precio) { return precio; }
    @Override public String getDescripcion() { return "Precio normal"; }
}

public class DescuentoVIP implements EstrategiaDescuento {
    private static final double PORCENTAJE = 0.15;
    @Override
    public double calcular(double precio) { return precio * (1 - PORCENTAJE); }
    @Override
    public String getDescripcion() { return "Descuento VIP 15%"; }
}

public class DescuentoCorporativo implements EstrategiaDescuento {
    private final double precioFijo;
    public DescuentoCorporativo(double precioFijo) { this.precioFijo = precioFijo; }
    @Override
    public double calcular(double precio) { return Math.min(precio, precioFijo); }
    @Override
    public String getDescripcion() { return "Precio corporativo: $" + precioFijo; }
}

// ── Contexto: usa la estrategia sin saber cuál es ──
public class CarritoCompras {

    private final List<Producto> productos = new ArrayList<>();
    private EstrategiaDescuento  estrategia = new SinDescuento(); // por defecto

    public void setEstrategia(EstrategiaDescuento estrategia) {
        this.estrategia = estrategia;
    }

    public double calcularTotal() {
        double subtotal = productos.stream()
            .mapToDouble(Producto::getPrecio)
            .sum();
        return estrategia.calcular(subtotal);
    }

    public void mostrarResumen() {
        System.out.printf("Estrategia: %s%n", estrategia.getDescripcion());
        System.out.printf("Total: $%.2f%n", calcularTotal());
    }
}

// ── Uso: la estrategia se elige en tiempo de ejecución ──
CarritoCompras carrito = new CarritoCompras();
carrito.agregar(new Producto("Laptop", 999.99));

// Cliente normal
carrito.setEstrategia(new SinDescuento());
carrito.mostrarResumen(); // Precio normal → $999.99

// Cliente VIP
carrito.setEstrategia(new DescuentoVIP());
carrito.mostrarResumen(); // Descuento VIP 15% → $849.99

// Cliente corporativo
carrito.setEstrategia(new DescuentoCorporativo(800.0));
carrito.mostrarResumen(); // Precio corporativo → $800.00
```

> 💡 Las **lambdas** de Java 8+ son estrategias en su forma más concisa: `carrito.setEstrategia(precio -> precio * 0.90)` es una estrategia anónima.

---

## 3. Command (Comando)

### 📖 Definición

Encapsula una **solicitud como un objeto**, permitiendo parametrizar clientes con distintas solicitudes, hacer cola de solicitudes, y soportar operaciones que se puedan **deshacer** (*undo*).

> **Analogía:** una orden en un restaurante escrita en un papel. El mesero no ejecuta la orden — la entrega a cocina. La cocina puede procesarla cuando tenga capacidad. Y si hay un error, la nota escrita permite saber exactamente qué se pidió para corregirlo.

### 🌍 Casos de uso reales

- **Ctrl+Z / Ctrl+Y (deshacer/rehacer):** cada acción del usuario (escribir, borrar, mover) es un Command con su método `ejecutar()` y su método `deshacer()`. Se apilan en una Stack.
- **Cola de tareas asíncronas:** los jobs se encapsulan como Commands y se envían a una cola (Kafka, RabbitMQ). Un worker los procesa cuando tiene capacidad, sin que el emisor espere.
- **Transacciones de base de datos:** una transacción es un Command. Si falla, se llama a `rollback()` (el deshacer).
- **Macros y automatizaciones:** una macro graba una secuencia de Commands y los repite. Útil en editores, hojas de cálculo y herramientas de testing.
- **Sistema de auditoría:** al encapsular cada operación como Command, es trivial registrar quién ejecutó qué, cuándo y con qué parámetros.

### ☕ Implementación en Java

```java
// ── Interfaz del comando ──
public interface Comando {
    void ejecutar();
    void deshacer();
}

// ── Receptor: el objeto que hace el trabajo real ──
public class EditorTexto {
    private StringBuilder texto = new StringBuilder();

    public void insertar(String contenido) {
        texto.append(contenido);
        System.out.println("Texto actual: '" + texto + "'");
    }
    public void eliminarUltimos(int n) {
        if (texto.length() >= n) texto.delete(texto.length() - n, texto.length());
        System.out.println("Texto actual: '" + texto + "'");
    }
    public String getTexto() { return texto.toString(); }
}

// ── Comandos concretos ──
public class ComandoInsertar implements Comando {

    private final EditorTexto editor;
    private final String      contenido;

    public ComandoInsertar(EditorTexto editor, String contenido) {
        this.editor    = editor;
        this.contenido = contenido;
    }

    @Override public void ejecutar()  { editor.insertar(contenido); }
    @Override public void deshacer()  { editor.eliminarUltimos(contenido.length()); }
}

// ── Invocador: gestiona la historia de comandos ──
public class HistorialComandos {

    private final Deque<Comando> historial = new ArrayDeque<>();

    public void ejecutar(Comando cmd) {
        cmd.ejecutar();
        historial.push(cmd);        // apilar para poder deshacer
    }

    public void deshacer() {
        if (!historial.isEmpty()) {
            historial.pop().deshacer(); // desapilar y revertir
        }
    }
}

// ── Uso ──
EditorTexto    editor    = new EditorTexto();
HistorialComandos historial = new HistorialComandos();

historial.ejecutar(new ComandoInsertar(editor, "Hola"));
// Texto actual: 'Hola'
historial.ejecutar(new ComandoInsertar(editor, " mundo"));
// Texto actual: 'Hola mundo'
historial.deshacer();
// Texto actual: 'Hola'          ← Ctrl+Z
historial.deshacer();
// Texto actual: ''              ← Ctrl+Z de nuevo
```

---

## 4. Template Method (Método Plantilla)

### 📖 Definición

Define el **esqueleto de un algoritmo** en una clase base, dejando que las subclases redefinan ciertos pasos sin cambiar la estructura general del algoritmo.

> **Analogía:** una receta de cocina con pasos fijos y pasos personalizables. La secuencia es siempre: preparar ingredientes → cocinar → emplatar → decorar. Pero "cocinar" puede ser hornear, freír o hervir según la receta concreta.

### 🌍 Casos de uso reales

- **Procesamiento de distintos formatos de reporte:** el flujo siempre es: obtener datos → transformar → generar documento → guardar. Los pasos de "transformar" y "generar" varían entre PDF, Excel y CSV.
- **Proceso de autenticación:** validar credenciales → verificar cuenta → registrar acceso → devolver token. Los pasos de "validar credenciales" difieren entre autenticación por usuario/contraseña y OAuth.
- **Pipeline de importación de datos:** leer archivo → validar → transformar → persistir → notificar. El paso de "leer" varía entre CSV, XML y JSON.
- **JUnit y frameworks de testing:** `setUp()` → `test()` → `tearDown()` — el framework define la secuencia; el test concreto implementa cada paso.
- **Spring Data `JpaRepository`:** define los métodos base (`findAll`, `save`, `delete`); tú solo implementas las queries específicas de tu entidad.

### ☕ Implementación en Java

```java
// ── Clase abstracta con el algoritmo plantilla ──
public abstract class GeneradorReporte {

    // Método plantilla: define el esqueleto — NO se puede sobreescribir (final)
    public final void generar(String periodo) {
        System.out.println("=== Generando reporte de " + periodo + " ===");
        List<Object> datos     = obtenerDatos(periodo);        // paso 1: abstracto
        List<Object> procesados = procesarDatos(datos);        // paso 2: abstracto
        String       contenido  = formatear(procesados);       // paso 3: abstracto
        guardar(contenido, periodo);                           // paso 4: concreto (compartido)
        notificar(periodo);                                    // paso 5: hook (opcional)
        System.out.println("=== Reporte generado ===");
    }

    // Pasos que las subclases DEBEN implementar
    protected abstract List<Object> obtenerDatos(String periodo);
    protected abstract List<Object> procesarDatos(List<Object> datos);
    protected abstract String       formatear(List<Object> datos);

    // Paso concreto compartido por todos (no abstracto)
    private void guardar(String contenido, String periodo) {
        System.out.println("Guardando reporte en disco: reporte-" + periodo + "." + getExtension());
    }

    // Hook: las subclases pueden sobreescribir o dejar el comportamiento por defecto
    protected void notificar(String periodo) {
        System.out.println("Notificación genérica: reporte listo para " + periodo);
    }

    protected abstract String getExtension();
}

// ── Implementación concreta para PDF ──
public class ReportePDF extends GeneradorReporte {
    @Override
    protected List<Object> obtenerDatos(String periodo) {
        System.out.println("Consultando BD para período " + periodo);
        return List.of("dato1", "dato2");
    }
    @Override
    protected List<Object> procesarDatos(List<Object> datos) {
        System.out.println("Calculando totales y agrupaciones");
        return datos;
    }
    @Override
    protected String formatear(List<Object> datos) {
        System.out.println("Generando estructura PDF con iText");
        return "<pdf>contenido</pdf>";
    }
    @Override
    protected void notificar(String periodo) {
        System.out.println("Email enviado al gerente con el PDF de " + periodo); // comportamiento propio
    }
    @Override protected String getExtension() { return "pdf"; }
}

// ── Implementación concreta para Excel ──
public class ReporteExcel extends GeneradorReporte {
    @Override
    protected List<Object> obtenerDatos(String periodo) {
        System.out.println("Consultando BD para Excel " + periodo);
        return List.of("dato1", "dato2");
    }
    @Override
    protected List<Object> procesarDatos(List<Object> datos) {
        System.out.println("Pivotando datos para planilla");
        return datos;
    }
    @Override
    protected String formatear(List<Object> datos) {
        System.out.println("Generando XLSX con Apache POI");
        return "<xlsx>contenido</xlsx>";
    }
    @Override protected String getExtension() { return "xlsx"; }
    // notificar() usa la implementación por defecto
}

// ── Uso ──
GeneradorReporte pdf   = new ReportePDF();
GeneradorReporte excel = new ReporteExcel();

pdf.generar("2026-03");   // misma secuencia, pasos distintos
excel.generar("2026-03");
```

---

## 5. Chain of Responsibility (Cadena de Responsabilidad)

### 📖 Definición

Permite pasar una solicitud a lo largo de una **cadena de manejadores**. Cada manejador decide si procesa la solicitud o la pasa al siguiente en la cadena.

> **Analogía:** el proceso de aprobación de un gasto en una empresa. Un gasto de $100 lo aprueba el gerente de área. Si supera $1.000, escala al director. Si supera $10.000, escala al CEO. Cada nivel procesa lo que puede y pasa el resto al siguiente.

### 🌍 Casos de uso reales

- **Middleware / Filtros HTTP (Servlet Filters, Spring Security):** cada request pasa por una cadena de filtros: autenticación → autorización → validación de CORS → logging → rate limiting → el controller. Cada filtro puede cortar la cadena o pasarla.
- **Aprobación de préstamos o créditos:** el analista aprueba hasta cierto monto; si supera, escala al supervisor; si supera más, al comité de crédito.
- **Sistema de logging multinivel:** un mensaje de nivel DEBUG es procesado solo por el logger de desarrollo; ERROR también activa el logger de alertas y el de email; CRITICAL activa todos.
- **Validación en pipeline:** validar formato → validar reglas de negocio → validar contra BD. Cada validador puede detener el proceso con un error o pasar al siguiente.
- **Pipeline de CI/CD:** compilar → testear → analizar calidad → construir imagen Docker → desplegar. Cada etapa puede fallar y detener la cadena.

### ☕ Implementación en Java

```java
// ── Manejador abstracto ──
public abstract class AprobadorGasto {

    protected AprobadorGasto siguiente;

    public AprobadorGasto setSiguiente(AprobadorGasto siguiente) {
        this.siguiente = siguiente;
        return siguiente; // permite encadenar con fluent API
    }

    public abstract void aprobar(SolicitudGasto solicitud);

    protected void pasarAlSiguiente(SolicitudGasto solicitud) {
        if (siguiente != null) {
            siguiente.aprobar(solicitud);
        } else {
            System.out.println("RECHAZADO: monto $" + solicitud.getMonto() +
                               " supera el límite de aprobación — requiere junta directiva");
        }
    }
}

// ── Manejadores concretos ──
public class GerenteArea extends AprobadorGasto {
    private static final double LIMITE = 1_000.0;
    @Override
    public void aprobar(SolicitudGasto s) {
        if (s.getMonto() <= LIMITE) {
            System.out.printf("✅ Gerente aprobó $%.0f: %s%n", s.getMonto(), s.getConcepto());
        } else {
            System.out.printf("↗ Gerente escala $%.0f al Director%n", s.getMonto());
            pasarAlSiguiente(s);
        }
    }
}

public class Director extends AprobadorGasto {
    private static final double LIMITE = 10_000.0;
    @Override
    public void aprobar(SolicitudGasto s) {
        if (s.getMonto() <= LIMITE) {
            System.out.printf("✅ Director aprobó $%.0f: %s%n", s.getMonto(), s.getConcepto());
        } else {
            System.out.printf("↗ Director escala $%.0f al CEO%n", s.getMonto());
            pasarAlSiguiente(s);
        }
    }
}

public class CEO extends AprobadorGasto {
    private static final double LIMITE = 100_000.0;
    @Override
    public void aprobar(SolicitudGasto s) {
        if (s.getMonto() <= LIMITE) {
            System.out.printf("✅ CEO aprobó $%.0f: %s%n", s.getMonto(), s.getConcepto());
        } else {
            pasarAlSiguiente(s);
        }
    }
}

// ── Construcción de la cadena y uso ──
AprobadorGasto gerente  = new GerenteArea();
AprobadorGasto director = new Director();
AprobadorGasto ceo      = new CEO();

gerente.setSiguiente(director).setSiguiente(ceo); // encadenar

gerente.aprobar(new SolicitudGasto(500.0,    "Materiales de oficina"));
// ✅ Gerente aprobó $500: Materiales de oficina

gerente.aprobar(new SolicitudGasto(5_000.0,  "Laptop para desarrollador"));
// ↗ Gerente escala $5000 al Director
// ✅ Director aprobó $5000: Laptop para desarrollador

gerente.aprobar(new SolicitudGasto(50_000.0, "Servidor de producción"));
// ↗ Gerente escala al Director → ↗ Director escala al CEO
// ✅ CEO aprobó $50000: Servidor de producción

gerente.aprobar(new SolicitudGasto(200_000.0, "Adquisición empresa"));
// RECHAZADO: supera el límite — requiere junta directiva
```

---

## 6. State (Estado)

### 📖 Definición

Permite que un objeto **altere su comportamiento cuando su estado interno cambia**. El objeto parecerá cambiar de clase. Cada estado posible se encapsula en su propia clase.

> **Analogía:** un semáforo. El mismo semáforo se comporta diferente según su estado: en verde permite el paso, en amarillo advierte, en rojo detiene. El "objeto semáforo" es siempre el mismo, pero su comportamiento cambia con el estado.

### 🌍 Casos de uso reales

- **Ciclo de vida de un ticket de soporte:** Abierto → En Progreso → En Revisión → Resuelto → Cerrado. Cada estado permite distintas acciones: un ticket Cerrado no puede reabrirse desde Resuelto directamente; uno Abierto no puede marcarse como Resuelto sin pasar por Progreso.
- **Pedido en e-commerce:** Pendiente → Confirmado → En Preparación → Enviado → Entregado → (Devuelto). Cada estado determina qué acciones están disponibles (cancelar solo en Pendiente, registrar entrega solo en Enviado).
- **Máquina expendedora:** Sin monedas → Con monedas → Dispensando → Sin stock. Cada estado acepta o rechaza las interacciones del usuario de forma distinta.
- **Conexión de red:** Desconectado → Conectando → Conectado → Reconectando. El comportamiento de `enviarDatos()` varía completamente según el estado.
- **Documento en flujo de aprobación:** Borrador → En Revisión → Aprobado → Publicado → Archivado.

### ☕ Implementación en Java

```java
// ── Interfaz de estado ──
public interface EstadoTicket {
    void asignar(Ticket ticket);
    void resolver(Ticket ticket);
    void cerrar(Ticket ticket);
    String getNombre();
}

// ── Estados concretos ──
public class EstadoAbierto implements EstadoTicket {
    @Override
    public void asignar(Ticket ticket) {
        System.out.println("Ticket asignado a un agente");
        ticket.setEstado(new EstadoEnProgreso()); // transición
    }
    @Override
    public void resolver(Ticket ticket) {
        System.out.println("❌ No se puede resolver un ticket no asignado");
    }
    @Override
    public void cerrar(Ticket ticket) {
        System.out.println("❌ No se puede cerrar directamente — debe resolverse primero");
    }
    @Override public String getNombre() { return "ABIERTO"; }
}

public class EstadoEnProgreso implements EstadoTicket {
    @Override
    public void asignar(Ticket ticket) {
        System.out.println("❌ Ticket ya está asignado");
    }
    @Override
    public void resolver(Ticket ticket) {
        System.out.println("✅ Ticket marcado como resuelto");
        ticket.setEstado(new EstadoResuelto()); // transición
    }
    @Override
    public void cerrar(Ticket ticket) {
        System.out.println("❌ Debes resolver el ticket antes de cerrarlo");
    }
    @Override public String getNombre() { return "EN_PROGRESO"; }
}

public class EstadoResuelto implements EstadoTicket {
    @Override
    public void asignar(Ticket ticket) {
        System.out.println("❌ Ticket ya fue resuelto");
    }
    @Override
    public void resolver(Ticket ticket) {
        System.out.println("❌ Ticket ya está resuelto");
    }
    @Override
    public void cerrar(Ticket ticket) {
        System.out.println("✅ Ticket cerrado definitivamente");
        ticket.setEstado(new EstadoCerrado()); // transición
    }
    @Override public String getNombre() { return "RESUELTO"; }
}

public class EstadoCerrado implements EstadoTicket {
    @Override public void asignar(Ticket t) { System.out.println("❌ Ticket cerrado — no modificable"); }
    @Override public void resolver(Ticket t) { System.out.println("❌ Ticket cerrado — no modificable"); }
    @Override public void cerrar(Ticket t)   { System.out.println("❌ Ticket ya está cerrado"); }
    @Override public String getNombre()       { return "CERRADO"; }
}

// ── Contexto: el objeto cuyo comportamiento cambia ──
public class Ticket {
    private EstadoTicket estado = new EstadoAbierto(); // estado inicial
    private final String titulo;

    public Ticket(String titulo) { this.titulo = titulo; }

    public void setEstado(EstadoTicket estado) { this.estado = estado; }

    // Delega al estado actual — el comportamiento varía automáticamente
    public void asignar()  { estado.asignar(this);  }
    public void resolver() { estado.resolver(this); }
    public void cerrar()   { estado.cerrar(this);   }
    public String getEstadoNombre() { return estado.getNombre(); }
}

// ── Uso ──
Ticket ticket = new Ticket("Error en login");
System.out.println("Estado: " + ticket.getEstadoNombre()); // ABIERTO

ticket.resolver(); // ❌ No se puede resolver un ticket no asignado
ticket.asignar();  // ✅ Ticket asignado — estado → EN_PROGRESO
ticket.asignar();  // ❌ Ticket ya está asignado
ticket.resolver(); // ✅ Ticket resuelto — estado → RESUELTO
ticket.cerrar();   // ✅ Ticket cerrado — estado → CERRADO
ticket.asignar();  // ❌ Ticket cerrado — no modificable
```

---

## 7. Tabla comparativa

| Patrón | Pregunta que responde | Clave | Ejemplo en Java/Spring |
|--------|----------------------|-------|------------------------|
| **Observer** | ¿Cómo notifico a múltiples objetos cuando algo cambia? | Suscripción automática a eventos | Spring `@EventListener`, Reactive Streams |
| **Strategy** | ¿Cómo hago intercambiable un algoritmo? | Encapsula variantes del mismo proceso | Cálculo de descuentos, métodos de pago |
| **Command** | ¿Cómo encapsulo una acción para ejecutarla/deshacerla? | La acción como objeto | Ctrl+Z, colas de tareas asíncronas |
| **Template Method** | ¿Cómo fijo el esqueleto de un proceso con pasos variables? | Algoritmo con huecos para subclases | Generación de reportes, pipelines |
| **Chain of Responsibility** | ¿Cómo proceso una solicitud por múltiples manejadores en secuencia? | Cadena de filtros | Spring Security, aprobaciones, middleware |
| **State** | ¿Cómo cambio el comportamiento según el estado del objeto? | El estado encapsula el comportamiento | Ciclo de vida de tickets, pedidos |

---

## 8. Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Design Patterns: Elements of Reusable Object-Oriented Software** | Gamma, Helm, Johnson, Vlissides (GoF) | Avanzado | Definición original de los 23 patrones — capítulos sobre Observer y Strategy son fundamentales |
| **Head First Design Patterns** | Freeman & Robson | Principiante / Intermedio | El mejor punto de entrada — especialmente el capítulo de Strategy (capítulo 1) |
| **Refactoring to Patterns** | Joshua Kerievsky | Intermedio / Avanzado | Muestra cuándo y cómo introducir Strategy, Command y Template Method mediante refactoring |
| **Growing Object-Oriented Software, Guided by Tests** | Freeman & Pryce | Avanzado | Cómo emergen los patrones de comportamiento guiados por TDD |
| **Patterns of Enterprise Application Architecture** | Martin Fowler | Avanzado | Observer (como Domain Events), Command (Service Layer), Chain (Pipeline) en contextos enterprise |

---

## 9. Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Refactoring.Guru — Patrones de comportamiento** | https://refactoring.guru/es/design-patterns/behavioral-patterns | Diagramas interactivos y código Java/Python para cada patrón |
| **Baeldung — Behavioral Patterns** | https://www.baeldung.com/design-patterns-series | Artículos prácticos: Observer, Strategy, Command con Spring |
| **Java Design Patterns — Behavioral** | https://java-design-patterns.com/categories/behavioral/ | Repositorio con código, tests y casos de uso reales |
| **Spring Events (Observer en Spring)** | https://www.baeldung.com/spring-events | Cómo implementar Observer con `ApplicationEvent` y `@EventListener` |
| **Spring Security Filter Chain** | https://docs.spring.io/spring-security/reference/servlet/architecture.html | Chain of Responsibility en acción en el framework más usado de Java |
| **SourceMaking — Behavioral** | https://sourcemaking.com/design_patterns/behavioral_patterns | Explicaciones clásicas con analogías del mundo real |

