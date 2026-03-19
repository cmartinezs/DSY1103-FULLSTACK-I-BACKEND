# Módulo 11 — Patrones de diseño: Creacionales

> **Objetivo:** conocer los patrones creacionales del catálogo GoF, entender qué problema resuelve cada uno, cuándo aplicarlos y cómo se implementan en Java.

---

## ¿Qué son los patrones de diseño?

Un **patrón de diseño** (*design pattern*) es una **solución reutilizable y probada a un problema recurrente** en el diseño de software orientado a objetos. No es código copiable — es una plantilla conceptual que se adapta al contexto de cada proyecto.

Fueron sistematizados en 1994 por los cuatro autores conocidos como la **"Gang of Four" (GoF)**: Gamma, Helm, Johnson y Vlissides, en el libro *"Design Patterns: Elements of Reusable Object-Oriented Software"*.

### Las tres familias

| Familia | Pregunta que responde | Módulo |
|---------|----------------------|--------|
| **Creacionales** | ¿Cómo crear objetos de forma flexible y controlada? | Este módulo |
| **Estructurales** | ¿Cómo componer clases y objetos en estructuras mayores? | Módulo 12 |
| **De comportamiento** | ¿Cómo distribuir responsabilidades y comunicación entre objetos? | Módulo 13 |

---

## Patrones creacionales

Los patrones creacionales abstraen el proceso de **instanciación de objetos**. En lugar de usar `new ClaseX()` directamente en todas partes, encapsulan la lógica de creación, haciendo el sistema más flexible y desacoplado.

---

## Índice

1. [Singleton](#1-singleton)
2. [Factory Method](#2-factory-method)
3. [Abstract Factory](#3-abstract-factory)
4. [Builder](#4-builder)
5. [Prototype](#5-prototype)
6. [Tabla comparativa](#6-tabla-comparativa)
7. [Literatura recomendada](#7-literatura-recomendada)
8. [Enlaces de interés](#8-enlaces-de-interés)

---

## 1. Singleton

### 📖 Definición

Garantiza que una clase tenga **una única instancia** en toda la aplicación y proporciona un punto de acceso global a ella.

> **Analogía:** el presidente de un país. Solo puede haber uno al mismo tiempo, y todos acceden al mismo a través del mismo cargo.

### 🌍 Casos de uso reales

- **Pool de conexiones a la base de datos** (HikariCP en Spring Boot): una sola instancia gestiona todas las conexiones — crear una por request sería devastadoramente lento.
- **Logger de la aplicación**: todos los módulos escriben al mismo sistema de logs. Si hubiera múltiples instancias, los archivos de log quedarían fragmentados o con condiciones de carrera.
- **Caché en memoria**: una sola instancia de caché compartida entre todos los servicios. Varias instancias implicarían datos inconsistentes.
- **Configuración de la aplicación**: las propiedades cargadas desde `application.properties` se leen una vez y se comparten globalmente.
- **Driver de hardware**: el driver de una impresora, tarjeta de red o GPU debe existir en una sola instancia para controlar el dispositivo.

### ☕ Implementación en Java

```java
// ── Singleton con inicialización perezosa (thread-safe con double-checked locking) ──
public class ConfiguracionApp {

    // volatile garantiza visibilidad entre hilos (ver módulo 08)
    private static volatile ConfiguracionApp instancia;

    private final String urlBaseDeDatos;
    private final int    puertoServidor;

    // Constructor privado — nadie puede hacer 'new ConfiguracionApp()'
    private ConfiguracionApp() {
        this.urlBaseDeDatos = System.getenv("DB_URL");
        this.puertoServidor = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    }

    // Punto de acceso global único
    public static ConfiguracionApp getInstance() {
        if (instancia == null) {
            synchronized (ConfiguracionApp.class) {
                if (instancia == null) {          // segunda verificación dentro del lock
                    instancia = new ConfiguracionApp();
                }
            }
        }
        return instancia;
    }

    public String getUrlBaseDeDatos() { return urlBaseDeDatos; }
    public int    getPuertoServidor()  { return puertoServidor; }
}

// Uso
ConfiguracionApp config = ConfiguracionApp.getInstance();
System.out.println(config.getPuertoServidor()); // 8080
```

```java
// ── Singleton con enum (forma más segura y concisa en Java) ──
public enum DatabaseConnection {
    INSTANCE;

    private final String url = "jdbc:postgresql://localhost:5432/miapp";

    public String getUrl() { return url; }
    public void ejecutarQuery(String sql) { /* ... */ }
}

// Uso
DatabaseConnection.INSTANCE.ejecutarQuery("SELECT * FROM tickets");
```

> 💡 **En Spring Boot** no necesitas implementar Singleton manualmente. Todos los `@Service`, `@Repository` y `@Component` son Singleton por defecto — Spring gestiona la instancia única automáticamente.

### ⚠️ Cuándo NO usarlo

- Cuando el estado compartido genera problemas de concurrencia (ver Módulo 08).
- Cuando dificulta las pruebas unitarias (el estado global persiste entre tests).
- En exceso: el "anti-patrón" de hacer Singleton todo lo que no debería serlo.

---

## 2. Factory Method

### 📖 Definición

Define una **interfaz para crear un objeto**, pero deja que las **subclases decidan qué clase instanciar**. El método de fábrica delega la instanciación a las subclases.

> **Analogía:** una pizzería tiene un método `hacerPizza()`. La pizzería de Milán hace pizza italiana; la de Nueva York hace pizza americana. Ambas implementan el mismo proceso, pero crean productos distintos.

### 🌍 Casos de uso reales

- **Generación de reportes en múltiples formatos:** el sistema tiene un método `generarReporte()`, y según el formato solicitado (PDF, Excel, CSV), se instancia el generador correspondiente sin que el cliente sepa cuál es.
- **Sistema de pagos con múltiples proveedores:** `PagoFactory.crear("WEBPAY")` devuelve una instancia de `PagoWebpay`; `PagoFactory.crear("PAYPAL")` devuelve `PagoPaypal`. El código cliente no cambia al añadir un nuevo proveedor.
- **Creación de notificaciones por canal:** `NotificacionFactory.crear("EMAIL")`, `crear("SMS")`, `crear("PUSH")` — mismo método, objeto distinto según el canal.
- **Drivers de bases de datos (JDBC):** `DriverManager.getConnection(url)` devuelve una conexión distinta según si la URL apunta a PostgreSQL, MySQL u Oracle.

### ☕ Implementación en Java

```java
// ── Producto (interfaz) ──
public interface Notificacion {
    void enviar(String destinatario, String mensaje);
}

// ── Productos concretos ──
public class NotificacionEmail implements Notificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Email a " + destinatario + ": " + mensaje);
    }
}

public class NotificacionSms implements Notificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("SMS a " + destinatario + ": " + mensaje);
    }
}

public class NotificacionPush implements Notificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Push a " + destinatario + ": " + mensaje);
    }
}

// ── Fábrica ──
public class NotificacionFactory {

    public static Notificacion crear(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "EMAIL" -> new NotificacionEmail();
            case "SMS"   -> new NotificacionSms();
            case "PUSH"  -> new NotificacionPush();
            default      -> throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        };
    }
}

// ── Uso: el cliente no sabe qué clase concreta se usa ──
String canalPreferido = usuario.getCanalNotificacion(); // "EMAIL", "SMS" o "PUSH"
Notificacion notif = NotificacionFactory.crear(canalPreferido);
notif.enviar(usuario.getContacto(), "Tu ticket fue resuelto");
```

### ⚠️ Cuándo NO usarlo

- Cuando solo hay un tipo de producto y no se espera que crezca. El patrón agrega complejidad que solo se justifica con múltiples variantes.

---

## 3. Abstract Factory

### 📖 Definición

Proporciona una interfaz para crear **familias de objetos relacionados** sin especificar sus clases concretas. Es una "fábrica de fábricas".

> **Analogía:** una fábrica de muebles de estilo. Si eliges estilo "Moderno", te entrega sofá moderno, mesa moderna y silla moderna — todos coordinados. Si eliges "Clásico", te entrega la familia clásica completa.

### 🌍 Casos de uso reales

- **Soporte multibases de datos:** una `DBFactory` para PostgreSQL crea `PostgresConnection`, `PostgresQueryBuilder` y `PostgresMigrator`; la de MySQL crea sus equivalentes. Al cambiar de BD, solo se cambia la fábrica.
- **Temas visuales (UI theming):** una `UIFactory` para tema oscuro crea botones oscuros, barras de progreso oscuras y diálogos oscuros; la de tema claro, sus equivalentes. El código de la app no cambia.
- **Pasarelas de pago por región:** la fábrica para Chile usa Webpay, la de México usa OXXO Pay, la de Europa usa Stripe — cada fábrica crea el cliente de pago, el generador de recibos y el validador de tarjetas correspondiente a la región.

### ☕ Implementación en Java

```java
// ── Interfaces de la familia de productos ──
public interface Boton      { void renderizar(); }
public interface Checkbox   { void seleccionar(); }

// ── Familia "Claro" ──
public class BotonClaro implements Boton {
    @Override public void renderizar() { System.out.println("Botón blanco con borde gris"); }
}
public class CheckboxClaro implements Checkbox {
    @Override public void seleccionar() { System.out.println("Checkbox azul claro marcado"); }
}

// ── Familia "Oscuro" ──
public class BotonOscuro implements Boton {
    @Override public void renderizar() { System.out.println("Botón gris oscuro con borde blanco"); }
}
public class CheckboxOscuro implements Checkbox {
    @Override public void seleccionar() { System.out.println("Checkbox verde neón marcado"); }
}

// ── Fábrica abstracta ──
public interface UIFactory {
    Boton    crearBoton();
    Checkbox crearCheckbox();
}

// ── Fábricas concretas ──
public class UIFactoryClaro implements UIFactory {
    @Override public Boton    crearBoton()    { return new BotonClaro(); }
    @Override public Checkbox crearCheckbox() { return new CheckboxClaro(); }
}

public class UIFactoryOscuro implements UIFactory {
    @Override public Boton    crearBoton()    { return new BotonOscuro(); }
    @Override public Checkbox crearCheckbox() { return new CheckboxOscuro(); }
}

// ── Uso ──
String tema = configuracion.getTema(); // "CLARO" u "OSCURO"
UIFactory factory = tema.equals("OSCURO") ? new UIFactoryOscuro() : new UIFactoryClaro();

Boton    boton    = factory.crearBoton();    // siempre coordinado con el tema
Checkbox checkbox = factory.crearCheckbox();
boton.renderizar();
checkbox.seleccionar();
```

---

## 4. Builder

### 📖 Definición

Separa la **construcción de un objeto complejo** de su representación, permitiendo que el mismo proceso de construcción cree distintas representaciones. Permite construir objetos paso a paso, eligiendo solo los campos necesarios.

> **Analogía:** configurar una hamburguesa en una app de delivery. Eliges pan, carne, vegetales, salsas — paso a paso, solo lo que quieres. Al final pides `construir()` y obtienes tu hamburguesa personalizada.

### 🌍 Casos de uso reales

- **Construcción de queries SQL dinámicas:** `QueryBuilder.select("nombre").from("usuarios").where("activo = true").orderBy("nombre").limit(10).build()` — solo se añaden las cláusulas necesarias.
- **Configuración de clientes HTTP:** construir un `HttpClient` con timeout, headers de autenticación, proxy y política de reintentos — solo los que apliquen.
- **Creación de emails complejos:** asunto, destinatarios, CC, BCC, cuerpo HTML, adjuntos — no siempre se usan todos los campos.
- **Generación de documentos PDF:** título, autor, márgenes, fuente, páginas — se construye paso a paso.
- **DTOs de respuesta de API con campos opcionales:** `ApiResponse.builder().data(resultado).mensaje("OK").paginacion(pagina).build()`.

### ☕ Implementación en Java

```java
// ── Objeto a construir ──
public class Ticket {
    // Campos obligatorios
    private final String titulo;
    private final String descripcion;
    private final String clienteId;
    // Campos opcionales
    private final String   asignadoA;
    private final String   prioridad;
    private final String   categoria;
    private final String   etiquetas;

    private Ticket(Builder builder) {
        this.titulo      = builder.titulo;
        this.descripcion = builder.descripcion;
        this.clienteId   = builder.clienteId;
        this.asignadoA   = builder.asignadoA;
        this.prioridad   = builder.prioridad;
        this.categoria   = builder.categoria;
        this.etiquetas   = builder.etiquetas;
    }

    // ── Builder estático interno ──
    public static class Builder {
        // Obligatorios
        private final String titulo;
        private final String descripcion;
        private final String clienteId;
        // Opcionales (con valores por defecto)
        private String asignadoA = "Sin asignar";
        private String prioridad  = "NORMAL";
        private String categoria  = "GENERAL";
        private String etiquetas  = "";

        public Builder(String titulo, String descripcion, String clienteId) {
            this.titulo      = titulo;
            this.descripcion = descripcion;
            this.clienteId   = clienteId;
        }

        public Builder asignarA(String agente)    { this.asignadoA = agente;    return this; }
        public Builder prioridad(String prioridad) { this.prioridad  = prioridad; return this; }
        public Builder categoria(String categoria) { this.categoria  = categoria; return this; }
        public Builder etiquetas(String etiquetas) { this.etiquetas  = etiquetas; return this; }

        public Ticket build() {
            // Aquí se pueden añadir validaciones antes de construir
            if (titulo.isBlank()) throw new IllegalStateException("El título no puede estar vacío");
            return new Ticket(this);
        }
    }

    @Override
    public String toString() {
        return "Ticket{titulo='%s', prioridad='%s', asignadoA='%s'}".formatted(titulo, prioridad, asignadoA);
    }
}

// ── Uso: legible, sin constructores con 7 parámetros ──
Ticket ticketSimple = new Ticket.Builder("Error en login", "No puedo ingresar", "cliente-42")
    .build();

Ticket ticketCompleto = new Ticket.Builder("Falla crítica en pagos", "El checkout falla en prod", "cliente-99")
    .asignarA("desarrollador-senior")
    .prioridad("CRITICA")
    .categoria("PAGOS")
    .etiquetas("produccion,urgente,pagos")
    .build();

System.out.println(ticketCompleto);
// Ticket{titulo='Falla crítica en pagos', prioridad='CRITICA', asignadoA='desarrollador-senior'}
```

> 💡 **Lombok** genera el Builder automáticamente con `@Builder`. **Spring** usa este patrón extensivamente en `WebClient.builder()`, `RestTemplate.builder()`, etc.

---

## 5. Prototype

### 📖 Definición

Permite crear nuevos objetos **copiando (clonando) un objeto existente** en lugar de instanciar desde cero. Útil cuando la creación es costosa o cuando se necesitan muchas variaciones de un objeto base.

> **Analogía:** en lugar de rediseñar un contrato desde cero cada vez, partes de una **plantilla** (prototipo) y modificas solo lo que necesitas: nombre del cliente, fecha, montos.

### 🌍 Casos de uso reales

- **Plantillas de documentos:** clonar una plantilla de contrato y personalizar solo los campos variables (cliente, monto, fecha). Crear el objeto desde cero implicaría reconstruir toda la estructura.
- **Configuraciones base de pruebas (fixtures):** en testing, clonar un objeto de usuario base y modificar solo el campo que el test necesita verificar.
- **Objetos de juego (game objects):** clonar un "enemigo base" con sus propiedades por defecto y personalizar la dificultad, posición o apariencia para cada instancia.
- **Caché de objetos costosos de inicializar:** crear el objeto una vez con todos sus datos cargados (de DB, de archivos), y clonar cuando se necesita una copia.

### ☕ Implementación en Java

```java
// Java tiene soporte nativo con Cloneable, pero la forma moderna usa copy constructors
public class ConfiguracionEmail implements Cloneable {

    private String servidor;
    private int    puerto;
    private String usuario;
    private boolean usarSSL;
    private List<String> destinatariosCopia; // campo de referencia

    public ConfiguracionEmail(String servidor, int puerto, String usuario, boolean usarSSL) {
        this.servidor            = servidor;
        this.puerto              = puerto;
        this.usuario             = usuario;
        this.usarSSL             = usarSSL;
        this.destinatariosCopia  = new ArrayList<>();
    }

    // Copy constructor (forma recomendada en Java moderno)
    public ConfiguracionEmail(ConfiguracionEmail original) {
        this.servidor            = original.servidor;
        this.puerto              = original.puerto;
        this.usuario             = original.usuario;
        this.usarSSL             = original.usarSSL;
        this.destinatariosCopia  = new ArrayList<>(original.destinatariosCopia); // copia profunda
    }

    public ConfiguracionEmail conUsuario(String nuevoUsuario) {
        ConfiguracionEmail copia = new ConfiguracionEmail(this); // clonar
        copia.usuario = nuevoUsuario;                             // personalizar
        return copia;
    }

    // getters y setters...
}

// ── Uso ──
ConfiguracionEmail baseEmpresa = new ConfiguracionEmail("smtp.empresa.com", 587, "sistema@empresa.com", true);

// Clonar y personalizar para distintos departamentos
ConfiguracionEmail emailVentas  = baseEmpresa.conUsuario("ventas@empresa.com");
ConfiguracionEmail emailSoporte = baseEmpresa.conUsuario("soporte@empresa.com");
// baseEmpresa no fue modificado
```

---

## 6. Tabla comparativa

| Patrón | Pregunta que responde | Cuándo usarlo | Ejemplo en Java/Spring |
|--------|----------------------|---------------|------------------------|
| **Singleton** | ¿Cómo garantizar una sola instancia? | Recursos compartidos globalmente (pool, config, logger) | `@Service`, `@Component` en Spring |
| **Factory Method** | ¿Cómo crear objetos sin especificar la clase exacta? | Múltiples variantes del mismo tipo de objeto | `NotificacionFactory.crear("EMAIL")` |
| **Abstract Factory** | ¿Cómo crear familias de objetos relacionados? | Cambiar toda una familia de implementaciones a la vez | Factories de DB, themes de UI |
| **Builder** | ¿Cómo construir objetos complejos paso a paso? | Objetos con muchos campos opcionales | `@Builder` de Lombok, `WebClient.builder()` |
| **Prototype** | ¿Cómo crear copias de objetos existentes? | Objetos costosos de crear, plantillas con variaciones | Copy constructors, `BeanUtils.copyProperties()` |

---

## 7. Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Design Patterns: Elements of Reusable Object-Oriented Software** | Gamma, Helm, Johnson, Vlissides (GoF) | Avanzado | El libro original. Denso pero es la referencia definitiva del campo |
| **Head First Design Patterns** | Freeman & Robson | Principiante / Intermedio | La introducción más amigable. Visual, con humor y ejemplos prácticos |
| **Refactoring to Patterns** | Joshua Kerievsky | Intermedio / Avanzado | Cómo llegar a los patrones mediante refactoring, no desde cero |
| **Clean Code** | Robert C. Martin | Intermedio | Principios de código limpio que complementan el uso correcto de patrones |
| **Effective Java** | Joshua Bloch | Intermedio / Avanzado | Ítem 1: "Static factory methods instead of constructors" — directamente relevante |

---

## 8. Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Refactoring.Guru — Patrones creacionales** | https://refactoring.guru/es/design-patterns/creational-patterns | Las mejores explicaciones visuales disponibles, con diagramas UML y código en Java |
| **Java Design Patterns** | https://java-design-patterns.com/patterns/ | Repositorio de patrones con implementaciones Java reales y tests |
| **Baeldung — Design Patterns in Java** | https://www.baeldung.com/design-patterns-series | Artículos detallados con código Java práctico |
| **SourceMaking** | https://sourcemaking.com/design_patterns | Explicaciones clásicas con analogías del mundo real |
| **Spring Framework Patterns** | https://www.baeldung.com/spring-framework-design-patterns | Cómo Spring usa internamente los patrones GoF |

