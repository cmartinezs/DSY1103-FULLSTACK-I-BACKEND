# Módulo 12 — Patrones de diseño: Estructurales

> **Objetivo:** conocer los patrones estructurales del catálogo GoF, entender qué problema de composición resuelve cada uno y cómo se implementan en Java.

---

## ¿Qué son los patrones estructurales?

Los patrones estructurales se ocupan de **cómo se componen clases y objetos** para formar estructuras más grandes. Su propósito es facilitar el diseño especificando una forma simple de **relacionar entidades** sin crear dependencias rígidas entre ellas.

> Si los patrones creacionales hablan de *cómo crear*, los estructurales hablan de *cómo ensamblar*.

---

## Índice

1. [Adapter (Adaptador)](#1-adapter-adaptador)
2. [Decorator (Decorador)](#2-decorator-decorador)
3. [Facade (Fachada)](#3-facade-fachada)
4. [Composite (Compuesto)](#4-composite-compuesto)
5. [Proxy](#5-proxy)
6. [Tabla comparativa](#6-tabla-comparativa)
7. [Literatura recomendada](#7-literatura-recomendada)
8. [Enlaces de interés](#8-enlaces-de-interés)

---

## 1. Adapter (Adaptador)

### 📖 Definición

Convierte la interfaz de una clase en otra interfaz que el cliente espera. Permite que **clases con interfaces incompatibles trabajen juntas** sin modificar su código fuente.

> **Analogía:** un adaptador de enchufe de viaje. Tu cargador tiene un enchufe tipo A (americano) y el tomacorriente es tipo C (europeo). El adaptador no cambia ninguno de los dos — solo hace que sean compatibles.

### 🌍 Casos de uso reales

- **Integrar una librería de terceros con interfaz distinta:** tu sistema espera un `ServicioPago` con el método `cobrar(monto)`, pero la librería del proveedor tiene `procesarTransaccion(importe, moneda)`. El Adapter traduce entre las dos.
- **Migración progresiva de sistemas legacy:** el sistema nuevo trabaja con `ClienteDTO`, pero la base de datos antigua devuelve `LegacyClienteRecord`. Un Adapter convierte uno en otro sin tocar ninguno.
- **Unificar múltiples fuentes de datos:** tienes tres proveedores de clima con APIs distintas. Un Adapter por proveedor los hace verse idénticos al resto del sistema.
- **Logging:** Spring usa Adapter para que distintas librerías de logging (Log4j, Logback, JUL) se comporten como `slf4j.Logger`.
- **Lectores de archivos en distintos formatos:** tu parser espera un `LectorTexto`, pero tienes archivos CSV, JSON y XML. Un Adapter por formato hace que todos sean `LectorTexto`.

### ☕ Implementación en Java

```java
// ── Interfaz que el sistema espera ──
public interface ServicioPago {
    boolean cobrar(String clienteId, double monto);
}

// ── Clase externa incompatible (no podemos modificarla) ──
public class WebpaySDK {
    public String iniciarTransaccion(double importe, String rut, String moneda) {
        // lógica interna del SDK de Webpay
        return "TOKEN-" + System.currentTimeMillis();
    }
    public boolean confirmarTransaccion(String token) {
        return true; // simulación
    }
}

// ── Adapter: hace que WebpaySDK se vea como ServicioPago ──
public class WebpayAdapter implements ServicioPago {

    private final WebpaySDK sdk;

    public WebpayAdapter(WebpaySDK sdk) {
        this.sdk = sdk;
    }

    @Override
    public boolean cobrar(String clienteId, double monto) {
        // Traduce la llamada del sistema hacia la API del SDK
        String token = sdk.iniciarTransaccion(monto, clienteId, "CLP");
        return sdk.confirmarTransaccion(token);
    }
}

// ── Uso: el cliente solo conoce ServicioPago, no sabe que por dentro es Webpay ──
ServicioPago pago = new WebpayAdapter(new WebpaySDK());
boolean exito = pago.cobrar("rut-123456789", 59990.0);
// Si mañana cambiamos a PayPal, creamos PaypalAdapter — el cliente no cambia
```

### ⚠️ Cuándo NO usarlo

- Si puedes modificar la clase original para que cumpla la interfaz, hazlo directamente — el Adapter agrega una capa de indirección.

---

## 2. Decorator (Decorador)

### 📖 Definición

Agrega **comportamiento adicional a un objeto dinámicamente**, sin modificar su clase. Una alternativa flexible a la herencia para extender funcionalidades.

> **Analogía:** un café negro. Le agregas leche → café con leche. Le agregas azúcar → café con leche y azúcar. Le agregas canela → café con leche, azúcar y canela. Cada adición es un "decorador" que envuelve al anterior.

### 🌍 Casos de uso reales

- **Logging de llamadas a métodos:** decorar un `Repository` con un `LoggingRepository` que registra el tiempo de cada consulta, sin tocar el repositorio original.
- **Caché sobre un servicio:** decorar `UsuarioService` con `CachedUsuarioService` que guarda el resultado en memoria — si ya existe, lo retorna sin ir a la BD.
- **Compresión y cifrado de archivos:** `ArchivoBase → ArchivoComprimido(base) → ArchivoCifrado(comprimido)`. Cada capa agrega un comportamiento.
- **Autorización sobre endpoints:** decorar un handler HTTP con verificación de permisos antes de ejecutar la lógica real.
- **Streams de Java I/O:** `new BufferedReader(new InputStreamReader(new FileInputStream("archivo.txt")))` — cada wrapper decora al anterior con nueva funcionalidad.
- **Spring AOP (`@Transactional`, `@Cacheable`, `@Async`):** Spring envuelve el bean original en un proxy que agrega comportamiento transaccional, de caché o asíncrono — exactamente el patrón Decorator.

### ☕ Implementación en Java

```java
// ── Interfaz base ──
public interface TicketRepository {
    Ticket buscarPorId(Long id);
    void   guardar(Ticket ticket);
}

// ── Implementación real ──
public class TicketRepositoryJPA implements TicketRepository {
    @Override
    public Ticket buscarPorId(Long id) {
        System.out.println("Consultando BD para ticket " + id);
        return new Ticket(id, "Descripción del ticket");
    }
    @Override
    public void guardar(Ticket ticket) {
        System.out.println("Guardando ticket en BD: " + ticket.getId());
    }
}

// ── Decorator: agrega logging sin modificar el original ──
public class LoggingTicketRepository implements TicketRepository {

    private final TicketRepository delegado;

    public LoggingTicketRepository(TicketRepository delegado) {
        this.delegado = delegado;
    }

    @Override
    public Ticket buscarPorId(Long id) {
        long inicio = System.currentTimeMillis();
        Ticket resultado = delegado.buscarPorId(id);        // delega al original
        long fin = System.currentTimeMillis();
        System.out.printf("buscarPorId(%d) tardó %d ms%n", id, fin - inicio);
        return resultado;
    }

    @Override
    public void guardar(Ticket ticket) {
        System.out.println("Guardando ticket: " + ticket.getId());
        delegado.guardar(ticket);
    }
}

// ── Decorator: agrega caché encima del logging ──
public class CachedTicketRepository implements TicketRepository {

    private final TicketRepository delegado;
    private final Map<Long, Ticket> cache = new HashMap<>();

    public CachedTicketRepository(TicketRepository delegado) {
        this.delegado = delegado;
    }

    @Override
    public Ticket buscarPorId(Long id) {
        return cache.computeIfAbsent(id, delegado::buscarPorId);
    }

    @Override
    public void guardar(Ticket ticket) {
        delegado.guardar(ticket);
        cache.put(ticket.getId(), ticket); // actualizar caché
    }
}

// ── Composición de decoradores ──
TicketRepository repo =
    new CachedTicketRepository(           // capa 3: caché
        new LoggingTicketRepository(      // capa 2: logging
            new TicketRepositoryJPA()     // capa 1: implementación real
        )
    );

Ticket t = repo.buscarPorId(1L); // pasa por las 3 capas
```

---

## 3. Facade (Fachada)

### 📖 Definición

Proporciona una **interfaz simplificada** a un conjunto de interfaces en un subsistema complejo. La Fachada no elimina la complejidad — la oculta detrás de una interfaz limpia.

> **Analogía:** el panel de control de un auto moderno. No sabes nada del motor, la transmisión ni la inyección de combustible. Giras la llave (o presionas un botón) y el auto arranca. El panel es la fachada del sistema complejo del vehículo.

### 🌍 Casos de uso reales

- **Servicio de checkout en e-commerce:** el frontend llama a `CheckoutService.procesarCompra(carrito, pago)`. Por dentro, ese método coordina el servicio de inventario, el procesador de pagos, el generador de facturas, el servicio de envío y el de notificaciones. El cliente solo ve un método.
- **Capa de servicio en Spring Boot:** `TicketService` es una fachada sobre el repositorio, el servicio de notificaciones, el validador y el logger — el controller llama a `ticketService.crear(dto)` sin saber nada de los detalles.
- **SDK de terceros:** AWS SDK expone fachadas (`S3Client`, `SesClient`, `SqsClient`) sobre subsistemas HTTP extremadamente complejos.
- **Módulo de reportes:** `ReporteService.generarMensual(mes, año)` coordina internamente la consulta a múltiples tablas, el cálculo de métricas, la generación del PDF y el envío por email.

### ☕ Implementación en Java

```java
// ── Subsistemas complejos ──
public class ServicioInventario {
    public boolean verificarStock(Long productoId, int cantidad) {
        System.out.println("Verificando stock de " + productoId);
        return true;
    }
    public void reservarStock(Long productoId, int cantidad) {
        System.out.println("Reservando " + cantidad + " unidades de " + productoId);
    }
}

public class ServicioPago {
    public String procesarPago(String tarjeta, double monto) {
        System.out.println("Procesando pago de $" + monto);
        return "TRANSACCION-" + System.currentTimeMillis();
    }
}

public class ServicioFactura {
    public String generarFactura(String transaccionId, double monto) {
        System.out.println("Generando factura para transacción " + transaccionId);
        return "FACTURA-001";
    }
}

public class ServicioNotificacion {
    public void notificarCompra(String email, String facturaId) {
        System.out.println("Enviando confirmación a " + email + " con factura " + facturaId);
    }
}

// ── Fachada: interfaz simple sobre los 4 subsistemas ──
public class CheckoutFacade {

    private final ServicioInventario  inventario;
    private final ServicioPago        pago;
    private final ServicioFactura     factura;
    private final ServicioNotificacion notificacion;

    public CheckoutFacade(ServicioInventario inventario, ServicioPago pago,
                          ServicioFactura factura, ServicioNotificacion notificacion) {
        this.inventario   = inventario;
        this.pago         = pago;
        this.factura      = factura;
        this.notificacion = notificacion;
    }

    // Una sola llamada coordina todos los subsistemas
    public String procesarCompra(Long productoId, int cantidad,
                                  String tarjeta, double monto, String email) {
        if (!inventario.verificarStock(productoId, cantidad)) {
            throw new IllegalStateException("Sin stock suficiente");
        }
        inventario.reservarStock(productoId, cantidad);
        String transaccion = pago.procesarPago(tarjeta, monto);
        String facturaId   = factura.generarFactura(transaccion, monto);
        notificacion.notificarCompra(email, facturaId);
        return facturaId;
    }
}

// ── Uso: el controller solo conoce la fachada ──
CheckoutFacade checkout = new CheckoutFacade(
    new ServicioInventario(), new ServicioPago(),
    new ServicioFactura(), new ServicioNotificacion()
);

String factura = checkout.procesarCompra(42L, 2, "4111-1111-1111-1111", 59990.0, "ana@email.com");
```

---

## 4. Composite (Compuesto)

### 📖 Definición

Compone objetos en **estructuras de árbol** para representar jerarquías parte-todo. Permite tratar a los **objetos individuales y las composiciones de manera uniforme**.

> **Analogía:** el sistema de archivos. Una carpeta puede contener archivos y otras carpetas. Calcular el "tamaño total" funciona igual tanto para un archivo (su propio tamaño) como para una carpeta (suma recursiva de su contenido).

### 🌍 Casos de uso reales

- **Sistema de archivos:** tanto archivos como carpetas tienen nombre y tamaño. `getTamaño()` en un archivo retorna su tamaño; en una carpeta, suma recursivamente el tamaño de todo su contenido.
- **Menú de navegación con submenús:** un ítem de menú puede ser un enlace (hoja) o un submenú (compuesto) que contiene más ítems. Renderizar el menú funciona igual en ambos casos.
- **Árbol de categorías de una tienda:** "Electrónica" contiene "Computadores" que contiene "Laptops" que contiene productos. El precio total o el conteo de productos se calcula recursivamente.
- **Expresiones matemáticas:** `(3 + 4) * (2 - 1)`. Cada operación puede contener otras operaciones o valores simples. Evaluar la expresión funciona recursivamente.
- **DOM HTML:** cada elemento puede tener hijos (compuesto) o ser un nodo de texto (hoja). `render()` funciona igual en todos.

### ☕ Implementación en Java

```java
// ── Componente base (interfaz común para hojas y compuestos) ──
public interface ComponenteMenu {
    String getNombre();
    void   renderizar(int nivel);
}

// ── Hoja: ítem simple sin hijos ──
public class ItemMenu implements ComponenteMenu {

    private final String nombre;
    private final String url;

    public ItemMenu(String nombre, String url) {
        this.nombre = nombre;
        this.url    = url;
    }

    @Override public String getNombre() { return nombre; }

    @Override
    public void renderizar(int nivel) {
        System.out.println("  ".repeat(nivel) + "→ " + nombre + " (" + url + ")");
    }
}

// ── Compuesto: puede contener hojas y otros compuestos ──
public class Submenu implements ComponenteMenu {

    private final String                   nombre;
    private final List<ComponenteMenu>     hijos = new ArrayList<>();

    public Submenu(String nombre) { this.nombre = nombre; }

    @Override public String getNombre() { return nombre; }

    public void agregar(ComponenteMenu componente) { hijos.add(componente); }

    @Override
    public void renderizar(int nivel) {
        System.out.println("  ".repeat(nivel) + "▼ " + nombre);
        for (ComponenteMenu hijo : hijos) {
            hijo.renderizar(nivel + 1);   // recursivo — funciona igual para hojas y compuestos
        }
    }
}

// ── Uso ──
Submenu menuPrincipal = new Submenu("Menú Principal");

menuPrincipal.agregar(new ItemMenu("Inicio", "/"));

Submenu menuProductos = new Submenu("Productos");
menuProductos.agregar(new ItemMenu("Laptops",    "/productos/laptops"));
menuProductos.agregar(new ItemMenu("Celulares",  "/productos/celulares"));

Submenu menuAccesorios = new Submenu("Accesorios");
menuAccesorios.agregar(new ItemMenu("Teclados",  "/accesorios/teclados"));
menuAccesorios.agregar(new ItemMenu("Mouses",    "/accesorios/mouses"));

menuProductos.agregar(menuAccesorios); // un submenú dentro de otro

menuPrincipal.agregar(menuProductos);
menuPrincipal.agregar(new ItemMenu("Contacto", "/contacto"));

menuPrincipal.renderizar(0);
// ▼ Menú Principal
//   → Inicio (/)
//   ▼ Productos
//     → Laptops (/productos/laptops)
//     → Celulares (/productos/celulares)
//     ▼ Accesorios
//       → Teclados (/accesorios/teclados)
//       → Mouses (/accesorios/mouses)
//   → Contacto (/contacto)
```

---

## 5. Proxy

### 📖 Definición

Proporciona un **sustituto o marcador de posición** para otro objeto. El Proxy controla el acceso al objeto real, pudiendo añadir lógica antes o después de delegar la llamada.

> **Analogía:** el asistente personal de un CEO. No hablas directamente con el CEO — hablas con su asistente, quien decide si la reunión es necesaria, la agenda, y te pasa con el CEO solo si corresponde.

### 🌍 Casos de uso reales

- **Lazy loading (carga perezosa):** el objeto real es costoso de crear (abre conexión a BD, carga un archivo grande). El Proxy existe como representante y solo crea el objeto real cuando realmente se necesita.
- **Control de acceso (proxy de seguridad):** antes de llamar a `eliminarUsuario()`, el proxy verifica si el usuario actual tiene permisos de administrador. Si no, lanza una excepción.
- **Caché (proxy de caché):** el proxy guarda el resultado de la primera llamada y para las siguientes devuelve el valor en memoria sin llamar al objeto real.
- **Logging / Auditoría (proxy de logging):** registra cada llamada a métodos sensibles: quién llamó, cuándo, con qué parámetros y cuál fue el resultado.
- **Spring AOP y `@Transactional`:** cuando Spring crea un bean con `@Transactional`, en realidad te entrega un Proxy que envuelve el bean real y gestiona la transacción automáticamente.
- **JPA Lazy Loading (`@OneToMany`):** Hibernate entrega un Proxy en lugar del objeto real. Al acceder por primera vez a la colección, el Proxy dispara la consulta SQL.

### ☕ Implementación en Java

```java
// ── Interfaz ──
public interface ServicioUsuario {
    Usuario buscarPorId(Long id);
    void    eliminar(Long id);
}

// ── Servicio real ──
public class ServicioUsuarioImpl implements ServicioUsuario {
    @Override
    public Usuario buscarPorId(Long id) {
        System.out.println("Consultando BD para usuario " + id);
        return new Usuario(id, "Ana García");
    }
    @Override
    public void eliminar(Long id) {
        System.out.println("Eliminando usuario " + id + " de la BD");
    }
}

// ── Proxy de control de acceso ──
public class ProxyServicioUsuario implements ServicioUsuario {

    private final ServicioUsuario   servicio;
    private final SesionUsuario     sesion;

    public ProxyServicioUsuario(ServicioUsuario servicio, SesionUsuario sesion) {
        this.servicio = servicio;
        this.sesion   = sesion;
    }

    @Override
    public Usuario buscarPorId(Long id) {
        // buscar no requiere permisos especiales
        return servicio.buscarPorId(id);
    }

    @Override
    public void eliminar(Long id) {
        // eliminar requiere rol ADMIN
        if (!sesion.tieneRol("ADMIN")) {
            throw new SecurityException("Sin permisos para eliminar usuarios");
        }
        System.out.println("Auditoría: " + sesion.getUsuario() + " eliminó al usuario " + id);
        servicio.eliminar(id);
    }
}

// ── Uso ──
SesionUsuario sesion = new SesionUsuario("ana", List.of("ADMIN"));
ServicioUsuario servicio = new ProxyServicioUsuario(new ServicioUsuarioImpl(), sesion);

servicio.buscarPorId(1L);   // ✅ pasa sin validación
servicio.eliminar(2L);      // ✅ tiene ADMIN — ejecuta
```

---

## 6. Tabla comparativa

| Patrón | Pregunta que responde | Clave | Ejemplo en Java/Spring |
|--------|----------------------|-------|------------------------|
| **Adapter** | ¿Cómo hago compatibles dos interfaces distintas? | Traducción de interfaz | SDK de terceros, legacy integration |
| **Decorator** | ¿Cómo agrego comportamiento sin modificar la clase? | Envuelve el original, agrega lógica | `@Transactional`, `@Cacheable`, I/O streams |
| **Facade** | ¿Cómo simplifico un subsistema complejo? | Punto de entrada único | `@Service` en Spring, SDKs, checkout |
| **Composite** | ¿Cómo trato igual a hojas y contenedores? | Estructura árbol recursiva | Sistema de archivos, menús, DOM |
| **Proxy** | ¿Cómo controlo el acceso a un objeto? | Sustituto con lógica añadida | Spring AOP, JPA lazy loading, seguridad |

### ⚠️ Decorator vs Proxy — la confusión más frecuente

Se implementan igual (ambos envuelven al objeto original con la misma interfaz), pero el **propósito es distinto**:

| | Decorator | Proxy |
|--|-----------|-------|
| **Propósito** | Agregar funcionalidad | Controlar acceso |
| **Lo decide** | El código cliente (compone decoradores) | El framework/sistema (transparente al cliente) |
| **Ejemplos** | Logging de rendimiento, caché opcional | Seguridad, lazy loading, Spring AOP |

---

## 7. Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Design Patterns: Elements of Reusable Object-Oriented Software** | Gamma, Helm, Johnson, Vlissides (GoF) | Avanzado | Fuente original de los patrones estructurales |
| **Head First Design Patterns** | Freeman & Robson | Principiante / Intermedio | Capítulos sobre Decorator y Adapter especialmente claros |
| **Refactoring: Improving the Design of Existing Code** | Martin Fowler | Intermedio | Cómo evolucionar hacia patrones estructurales mediante refactoring |
| **Patterns of Enterprise Application Architecture** | Martin Fowler | Avanzado | Proxy, Repository y otros patrones en contexto enterprise |

---

## 8. Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Refactoring.Guru — Patrones estructurales** | https://refactoring.guru/es/design-patterns/structural-patterns | Diagramas UML animados y código Java para cada patrón |
| **Baeldung — Structural Patterns** | https://www.baeldung.com/design-patterns-series | Artículos individuales con ejemplos Java detallados |
| **Java Design Patterns — Structural** | https://java-design-patterns.com/categories/structural/ | Repositorio con implementaciones y tests |
| **Spring Framework — cómo usa los patrones** | https://www.baeldung.com/spring-framework-design-patterns | Proxy (AOP), Decorator, Facade en el contexto de Spring |
| **Patrones en la JDK** | https://stackoverflow.com/questions/1673841 | Lista de dónde usa la JDK estándar cada patrón GoF |

