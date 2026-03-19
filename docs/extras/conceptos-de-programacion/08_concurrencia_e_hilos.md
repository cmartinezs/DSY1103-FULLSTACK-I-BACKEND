# Módulo 08 — Concurrencia e hilos

> **Objetivo:** entender qué es la concurrencia, por qué existe y cuáles son los problemas clásicos que genera. Es un tema que aparece en cualquier sistema real y que muchos problemas "raros" en producción tienen su origen aquí.

---

## ¿Qué es la concurrencia?

La **concurrencia** es la capacidad de un sistema de **ejecutar múltiples tareas al mismo tiempo** (o aparentemente al mismo tiempo). En programación, esto se logra mediante **hilos** (*threads*).

> **Analogía:** un restaurante con un solo mozo (programa de un hilo) atiende a una mesa, espera que pidan, lleva el pedido a cocina, espera, vuelve, cobra... mientras las demás mesas esperan. Con varios mozos (multihilo), se atienden varias mesas en paralelo.

---

## Índice

1. [Proceso vs Hilo (Thread)](#1-proceso-vs-hilo-thread)
2. [Concurrencia vs Paralelismo](#2-concurrencia-vs-paralelismo)
3. [Por qué usar concurrencia](#3-por-qué-usar-concurrencia)
4. [Problemas clásicos de concurrencia](#4-problemas-clásicos-de-concurrencia)
5. [Sincronización](#5-sincronización)
6. [Concurrencia en Java](#6-concurrencia-en-java)
7. [Concurrencia en la práctica backend](#7-concurrencia-en-la-práctica-backend)
8. [Tabla resumen](#8-tabla-resumen)
9. [📚 Literatura recomendada](#-literatura-recomendada)
10. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Proceso vs Hilo (Thread)

| | Proceso | Hilo (Thread) |
|--|---------|--------------|
| **Qué es** | Programa en ejecución, con su propia memoria | Unidad de ejecución dentro de un proceso |
| **Memoria** | Propia y aislada | Compartida con otros hilos del mismo proceso |
| **Comunicación** | Compleja (IPC) | Directa (variables compartidas) |
| **Costo de creación** | Alto | Bajo |
| **Ejemplo** | Abrir Chrome, IntelliJ, Spotify a la vez | Varias pestañas dentro de Chrome |

```
Proceso JVM
├── Hilo principal (main)
├── Hilo de Garbage Collector
├── Hilo del servidor HTTP (Spring Boot)
│   ├── Hilo para request del usuario A
│   ├── Hilo para request del usuario B
│   └── Hilo para request del usuario C
└── Hilos del pool de base de datos
```

---

## 2. Concurrencia vs Paralelismo

Son conceptos distintos que se confunden con frecuencia:

| | Concurrencia | Paralelismo |
|--|-------------|------------|
| **Definición** | Múltiples tareas en progreso, alternando en el tiempo | Múltiples tareas ejecutándose literalmente al mismo tiempo |
| **Requiere** | Un solo núcleo puede lograrlo (time-slicing) | Múltiples núcleos de CPU |
| **Analogía** | Un chef preparando varios platos alternando entre ellos | Varios chefs, cada uno preparando un plato a la vez |

> 📌 La concurrencia es sobre la **estructura** del programa. El paralelismo es sobre la **ejecución** simultánea en hardware. Un programa concurrente puede correr en paralelo si el hardware lo permite.

```
CONCURRENCIA (1 núcleo, alternando):
Hilo A: ████░░░░████░░░░████
Hilo B: ░░░░████░░░░████░░░░

PARALELISMO (2 núcleos, simultáneo):
Hilo A (núcleo 1): ████████████████████
Hilo B (núcleo 2): ████████████████████
```

---

## 3. Por qué usar concurrencia

### ✅ Casos de uso legítimos

| Caso | Por qué ayuda la concurrencia |
|------|------------------------------|
| **Servidor web** | Cada request de usuario se atiende en su propio hilo — sin uno, los usuarios esperarían en fila |
| **Operaciones I/O lentas** | Mientras un hilo espera respuesta de la DB o red, otro puede ejecutarse |
| **Procesamiento de lotes** | Dividir un trabajo grande en partes y procesarlas en paralelo |
| **UI responsiva** | La interfaz gráfica tiene su propio hilo para no congelarse mientras hay cómputo pesado |
| **Tareas en background** | Envío de emails, generación de reportes, mientras el usuario sigue usando la app |

---

## 4. Problemas clásicos de concurrencia

La memoria compartida entre hilos es una fuente de bugs difíciles de reproducir. Son bugs que *a veces* ocurren, en ciertos momentos, bajo ciertas condiciones — los peores bugs de todos.

### 4.1 Condición de carrera (Race Condition)

Dos o más hilos acceden y modifican datos compartidos sin coordinación, y el resultado depende del **orden de ejecución** (que es no determinístico).

```java
// ❌ Condición de carrera: dos hilos incrementan el mismo contador
public class ContadorInseguro {
    private int contador = 0;

    public void incrementar() {
        contador++; // NO es atómica: es leer + sumar + escribir (3 pasos)
    }
}

// Escenario problemático con dos hilos, contador = 0:
// Hilo A lee contador = 0
// Hilo B lee contador = 0
// Hilo A calcula 0 + 1 = 1, escribe 1
// Hilo B calcula 0 + 1 = 1, escribe 1  ← ¡perdió un incremento!
// Resultado final: 1  (esperado: 2)
```

### 4.2 Deadlock (Bloqueo mutuo)

Dos hilos esperan el uno al otro para liberar un recurso, indefinidamente. El programa se cuelga para siempre.

```
Hilo A tiene el lock de Recurso 1, espera el lock de Recurso 2
Hilo B tiene el lock de Recurso 2, espera el lock de Recurso 1
→ Ninguno puede avanzar → DEADLOCK
```

```
Analogía: dos coches en un callejón de un carril. Ninguno retrocede.
```

### 4.3 Starvation (Inanición)

Un hilo nunca consigue acceso al recurso porque otros hilos con mayor prioridad lo acaparan constantemente.

### 4.4 Visibilidad de memoria

Sin las garantías correctas, un hilo puede leer un valor "obsoleto" de una variable que otro hilo ya modificó, debido a las cachés de CPU.

```java
// ❌ Problema de visibilidad: Hilo B puede no ver el cambio de Hilo A
private boolean corriendo = true;

// Hilo A
public void detener() { corriendo = false; }

// Hilo B (puede no ver el cambio nunca, en ciertos sistemas)
while (corriendo) { hacerAlgo(); }
```

---

## 5. Sincronización

La sincronización es el mecanismo para **coordinar el acceso** a recursos compartidos entre hilos.

### `synchronized` en Java

```java
// ✅ synchronized garantiza que solo un hilo ejecuta este método a la vez
public class ContadorSeguro {
    private int contador = 0;

    public synchronized void incrementar() {
        contador++; // ahora es seguro
    }

    public synchronized int obtener() {
        return contador;
    }
}
```

### `volatile` en Java

```java
// ✅ volatile garantiza visibilidad: todos los hilos ven el valor más reciente
private volatile boolean corriendo = true;

// Hilo A
public void detener() { corriendo = false; }

// Hilo B — ahora sí ve el cambio
while (corriendo) { hacerAlgo(); }
```

### Tipos atómicos

```java
import java.util.concurrent.atomic.AtomicInteger;

// ✅ AtomicInteger: operaciones atómicas sin necesidad de synchronized
private AtomicInteger contador = new AtomicInteger(0);

public void incrementar() {
    contador.incrementAndGet(); // atómico: leer + sumar + escribir en un solo paso
}
```

---

## 6. Concurrencia en Java

### Crear hilos

```java
// Forma 1: extender Thread
class MiHilo extends Thread {
    @Override
    public void run() {
        System.out.println("Ejecutando en hilo: " + Thread.currentThread().getName());
    }
}
new MiHilo().start();

// Forma 2: implementar Runnable (más flexible)
Runnable tarea = () -> System.out.println("Hilo: " + Thread.currentThread().getName());
new Thread(tarea).start();
```

### ExecutorService — la forma correcta

Crear hilos manualmente es costoso y difícil de controlar. `ExecutorService` gestiona un **pool de hilos** reutilizables:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Pool de 4 hilos
ExecutorService executor = Executors.newFixedThreadPool(4);

// Enviar tareas al pool
for (int i = 0; i < 10; i++) {
    int tareaId = i;
    executor.submit(() -> {
        System.out.println("Tarea " + tareaId + " en hilo " + Thread.currentThread().getName());
    });
}

// Siempre cerrar el executor al terminar
executor.shutdown();
```

### CompletableFuture — operaciones asíncronas encadenadas

```java
import java.util.concurrent.CompletableFuture;

// Ejecutar en background y continuar cuando termine
CompletableFuture.supplyAsync(() -> buscarUsuarioEnDB(1L))      // en otro hilo
    .thenApply(usuario -> calcularDescuento(usuario))            // procesa el resultado
    .thenAccept(descuento -> System.out.println("Descuento: " + descuento)) // usa el resultado
    .exceptionally(ex -> { System.out.println("Error: " + ex.getMessage()); return null; });
```

---

## 7. Concurrencia en la práctica backend

En el desarrollo backend con Spring Boot, la concurrencia está presente aunque no la veas directamente:

```
Cada request HTTP → Spring crea (o reusar) un hilo para atenderlo
                 → Tu código se ejecuta en ese hilo
                 → Todos los requests comparten los beans de Spring
                 → Por eso los beans deben ser thread-safe (sin estado mutable)
```

```java
// ✅ Correcto: bean sin estado (stateless) — thread-safe por naturaleza
@Service
public class TicketService {
    private final TicketRepository repository; // inmutable, inyectado una vez

    public List<Ticket> listar() {
        return repository.findAll(); // no modifica ningún campo del servicio
    }
}

// ❌ Peligroso: bean con estado mutable compartido entre requests
@Service
public class TicketServiceInseguro {
    private List<Ticket> cache = new ArrayList<>(); // ← COMPARTIDA entre todos los hilos

    public void agregarAlCache(Ticket t) {
        cache.add(t); // ← race condition si dos requests llegan al mismo tiempo
    }
}
```

### Buenas prácticas en Spring Boot

| Práctica | Por qué |
|----------|---------|
| Beans sin estado (`@Service`, `@Repository`) | Thread-safe automáticamente |
| No guardar estado en campos de los controllers/services | Evita race conditions |
| Usar `ConcurrentHashMap` en lugar de `HashMap` si es compartido | `HashMap` no es thread-safe |
| Operaciones de DB en transacciones (`@Transactional`) | Garantiza consistencia aunque haya concurrencia |
| Usar `@Async` para tareas en background | Spring maneja el pool de hilos |

---

## 8. Tabla resumen

| Concepto | Definición | Ejemplo |
|----------|-----------|---------|
| **Hilo (Thread)** | Unidad de ejecución dentro de un proceso | Cada request HTTP en Spring tiene su hilo |
| **Concurrencia** | Múltiples tareas en progreso, alternando | Chef preparando varios platos en orden |
| **Paralelismo** | Múltiples tareas ejecutándose al mismo tiempo | Varios chefs, cada uno con un plato |
| **Race condition** | El resultado depende del orden de ejecución no determinístico | Dos hilos incrementando el mismo contador |
| **Deadlock** | Dos hilos esperándose mutuamente para siempre | A espera a B, B espera a A |
| **synchronized** | Solo un hilo a la vez ejecuta ese bloque | `public synchronized void incrementar()` |
| **volatile** | Garantiza visibilidad del valor entre hilos | `private volatile boolean corriendo` |
| **AtomicInteger** | Operación de lectura-modificación-escritura atómica | `contador.incrementAndGet()` |
| **ExecutorService** | Pool de hilos reutilizables | `Executors.newFixedThreadPool(4)` |
| **CompletableFuture** | Tarea asíncrona encadenable | Llamada a API externa sin bloquear |

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Java Concurrency in Practice** | Goetz, Peierls, Bloch, Bowbeer, Holmes, Lea | Intermedio / Avanzado | El libro definitivo de concurrencia en Java. Cubre race conditions, deadlocks, synchronized, volatile, executors y concurrent collections. Lectura obligada antes de escribir código multihilo en producción |
| **The Art of Multiprocessor Programming** | Herlihy & Shavit | Avanzado | Tratamiento académico riguroso de algoritmos concurrentes y estructuras de datos thread-safe |
| **Effective Java** | Joshua Bloch | Intermedio | Capítulo 11: "Concurrency" — ítems prácticos como preferir executors sobre threads directos y usar concurrent collections |
| **Modern Java in Action** | Urma, Fusco, Mycroft | Intermedio | Capítulos sobre CompletableFuture y programación reactiva — la concurrencia en Java moderno |
| **Seven Concurrency Models in Seven Weeks** | Paul Butcher | Intermedio / Avanzado | Explica distintos modelos de concurrencia (threads, actores, STM, CSP) para entender cuándo usar cada uno |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **Oracle — Java Concurrency Tutorial** | https://docs.oracle.com/javase/tutorial/essential/concurrency/ | Tutorial oficial de Oracle sobre threads, sincronización y concurrent utilities |
| **Baeldung — Java Concurrency** | https://www.baeldung.com/java-concurrency | Serie completa de artículos: threads, ExecutorService, CompletableFuture, locks, concurrent collections |
| **Java Memory Model — Artículo** | https://www.cs.umd.edu/~pugh/java/memoryModel/ | El Java Memory Model explicado por sus creadores — fundamental para entender volatile y synchronized |
| **Spring Async — Baeldung** | https://www.baeldung.com/spring-async | Cómo usar `@Async` en Spring Boot para ejecutar métodos en hilos separados |
| **Visualización de Race Conditions** | https://deadlockempire.github.io | Juego interactivo que muestra visualmente cómo ocurren deadlocks y race conditions — muy efectivo para entenderlos |

