# Módulo 03 — Estructuras de datos comunes

> **Objetivo:** conocer las estructuras de datos más usadas en programación, entender qué problema resuelve cada una y cuándo elegirla. Estos conceptos aparecen en todos los lenguajes, aunque la sintaxis varía.

---

## ¿Qué es una estructura de datos?

Una **estructura de datos** es una forma de **organizar, almacenar y gestionar datos en memoria** de manera que puedan ser accedidos y modificados eficientemente. La elección de la estructura correcta puede marcar la diferencia entre un programa rápido y uno lento.

> **Analogía:** organizar documentos en una oficina. Puedes meterlos todos en una caja (array), apilarlos en una pila (stack), hacer una fila para atención (queue), o guardarlos en carpetas etiquetadas por nombre (map). Cada forma tiene ventajas según cómo necesitas acceder a ellos.

---

## Índice

1. [Array / Arreglo](#1-array--arreglo)
2. [Lista](#2-lista)
3. [Pila (Stack)](#3-pila-stack)
4. [Cola (Queue)](#4-cola-queue)
5. [Mapa (Map / Diccionario)](#5-mapa-map--diccionario)
6. [Conjunto (Set)](#6-conjunto-set)
7. [Árbol (Tree)](#7-árbol-tree)
8. [Grafo (Graph)](#8-grafo-graph)
9. [¿Cuál elegir?](#9-cuál-elegir)
10. [Tabla resumen](#10-tabla-resumen)
11. [📚 Literatura recomendada](#-literatura-recomendada)
12. [🔗 Enlaces de interés](#-enlaces-de-interés)

---

## 1. Array / Arreglo

### 📖 Definición

Un **array** es la estructura de datos más básica: una **colección de tamaño fijo** de elementos del mismo tipo, almacenados de forma **contigua en memoria**, accesibles por **índice** (empezando en 0).

### 🌐 Perspectiva universal

```python
notas = [7.5, 6.0, 9.0, 8.5]
print(notas[0])  # 7.5
```
```javascript
const notas = [7.5, 6.0, 9.0, 8.5];
console.log(notas[0]);  // 7.5
```

### ☕ En Java

```java
// Tamaño FIJO — debes saber de antemano cuántos elementos tendrás
int[] numeros = new int[5];          // array de 5 enteros (todos en 0 inicialmente)
String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie"};

System.out.println(dias[0]);         // "Lun"
System.out.println(dias.length);     // 5

// Recorrer
for (int i = 0; i < dias.length; i++) {
    System.out.println(i + ": " + dias[i]);
}
```

### ✅ Cuándo usarlo

- Sabes exactamente cuántos elementos tendrás.
- Necesitas acceso muy rápido por posición (índice).
- Trabajas con datos primitivos y la performance importa.

### 🌍 Ejemplos reales

- **Píxeles de una imagen:** una foto de 1920×1080 px es, internamente, un array de 2.073.600 valores de color. El acceso por coordenada (fila, columna) es instantáneo.
- **Tabla de posiciones de una liga:** 18 equipos en posiciones fijas del 1 al 18. El tamaño no cambia durante la temporada.
- **Asientos en un avión:** fila 12, asiento C → posición exacta en un array bidimensional. Reservar o liberar un asiento es acceso directo por índice.
- **Buffer de audio/video:** los reproductores cargan fragmentos del audio en arrays de bytes de tamaño fijo para garantizar reproducción sin cortes.

### ⚠️ Limitación

El tamaño es **fijo en el momento de creación**. Si necesitas agregar o quitar elementos dinámicamente, usa una Lista.

---

## 2. Lista

### 📖 Definición

Una **lista** es como un array, pero de **tamaño dinámico**: puede crecer o decrecer según se agreguen o eliminen elementos. Es la estructura de datos más utilizada en el día a día.

### 🌐 Perspectiva universal

```python
# Python: list es dinámica por defecto
nombres = ["Ana", "Luis"]
nombres.append("María")   # agrega al final
nombres.remove("Luis")    # elimina por valor
print(len(nombres))       # 2
```
```javascript
// JavaScript: los arrays son dinámicos
const nombres = ["Ana", "Luis"];
nombres.push("María");    // agrega al final
nombres.splice(1, 1);     // elimina en posición 1
```

### ☕ En Java

```java
import java.util.ArrayList;
import java.util.List;

// ArrayList: lista basada en array dinámico (más rápida para lectura)
List<String> nombres = new ArrayList<>();

// Agregar elementos
nombres.add("Ana");
nombres.add("Luis");
nombres.add("María");
// → ["Ana", "Luis", "María"]

// Acceder por índice
System.out.println(nombres.get(0));    // "Ana"
System.out.println(nombres.size());   // 3

// Eliminar
nombres.remove("Luis");               // por valor → ["Ana", "María"]
nombres.remove(0);                    // por índice → ["María"]

// Verificar existencia
boolean existe = nombres.contains("Ana"); // false (ya fue eliminada)

// Recorrer
for (String nombre : nombres) {
    System.out.println(nombre);
}

// Lista inmutable (solo lectura)
List<String> dias = List.of("Lun", "Mar", "Mié");
dias.add("Jue"); // ❌ UnsupportedOperationException
```

### ⚠️ ArrayList vs LinkedList en Java

| | `ArrayList` | `LinkedList` |
|--|-------------|-------------|
| Acceso por índice | ✅ Rápido O(1) | ❌ Lento O(n) |
| Insertar/eliminar al inicio | ❌ Lento O(n) | ✅ Rápido O(1) |
| Uso recomendado | Mayoría de casos | Inserción/eliminación frecuente al inicio |

> 💡 En la práctica, usa `ArrayList` por defecto a menos que tengas una razón específica para `LinkedList`.

### 🌍 Ejemplos reales

- **Carrito de compras en un e-commerce:** los productos se agregan y eliminan durante la sesión. El tamaño varía constantemente — perfecta para una lista dinámica.
- **Playlist de Spotify:** el usuario puede añadir canciones al final, insertar en cualquier posición o eliminar. El orden importa y cambia.
- **Historial de pedidos de un usuario:** la aplicación carga los últimos N pedidos. La lista puede paginarse y filtrase.
- **Resultados de una búsqueda:** Google, una tienda o una API devuelven una lista de ítems que puede ser vacía, de un elemento, o de miles.
- **Línea de tiempo de una red social:** los posts del feed se van agregando al principio conforme llegan nuevos.

---

## 3. Pila (Stack)

### 📖 Definición

Una **pila** (*stack*) es una estructura de datos que funciona bajo el principio **LIFO** (*Last In, First Out*): el **último elemento en entrar es el primero en salir**. Solo se puede acceder al elemento que está en la "cima" de la pila.

> **Analogía:** una pila de platos. Solo puedes tomar el plato de arriba (el último que pusiste), y solo puedes poner platos encima del que está en la cima.

### Operaciones fundamentales

| Operación | Qué hace | Nombre común |
|-----------|----------|-------------|
| Agregar al tope | Pone un elemento en la cima | `push` |
| Sacar del tope | Extrae y elimina el elemento de la cima | `pop` |
| Ver el tope | Mira sin eliminar el elemento de la cima | `peek` |

### 🌐 Perspectiva universal

```python
# Python: usa list como stack
pila = []
pila.append("A")   # push
pila.append("B")
pila.append("C")
print(pila.pop())  # pop → "C" (el último en entrar)
print(pila.pop())  # pop → "B"
```

### ☕ En Java

```java
import java.util.ArrayDeque;
import java.util.Deque;

// La forma recomendada en Java moderno para Stack
Deque<String> pila = new ArrayDeque<>();

pila.push("A");   // agrega al tope
pila.push("B");
pila.push("C");
// Estado interno: [C, B, A] — C es el tope

System.out.println(pila.peek());  // "C" — solo mira, no extrae
System.out.println(pila.pop());   // "C" — extrae y elimina
System.out.println(pila.pop());   // "B"
System.out.println(pila.pop());   // "A"
```

### ✅ Cuándo usarla

- **Deshacer/Rehacer** (`Ctrl+Z`): cada acción se apila; deshacer hace `pop`.
- **Navegación hacia atrás** en un browser: cada página visitada se apila.
- **Llamadas a funciones**: el propio lenguaje usa una pila interna (call stack).
- **Validar paréntesis balanceados**: `(`, `[`, `{` se apilan, `)`, `]`, `}` verifican el tope.

### 🌍 Ejemplos reales

- **Ctrl+Z en Word, VS Code, Photoshop:** cada cambio que haces se apila. Al deshacer, se saca del tope — el último cambio es el primero en revertirse.
- **Botón "atrás" del navegador:** cada página que visitas se apila. Al hacer clic en "atrás", se saca la última del tope y vuelves a la anterior.
- **Pila de llamadas al debuggear:** cuando el programa falla, el stack trace muestra los frames apilados — el tope es donde ocurrió el error, la base es el punto de entrada.
- **Función "deshacer" en un editor de texto colaborativo (Google Docs):** cada operación (escribir, borrar, formato) se apila por usuario para poder revertirlas en orden inverso.
- **Compilador verificando llaves y paréntesis:** al leer `{`, lo apila. Al leer `}`, lo compara con el tope. Si no coincide o la pila queda vacía antes de tiempo → error de sintaxis.

---

## 4. Cola (Queue)

### 📖 Definición

Una **cola** (*queue*) funciona bajo el principio **FIFO** (*First In, First Out*): el **primer elemento en entrar es el primero en salir**. Como una fila de supermercado.

> **Analogía:** una fila de personas en caja. La primera persona en llegar es la primera en ser atendida.

### Operaciones fundamentales

| Operación | Qué hace | Nombre común |
|-----------|----------|-------------|
| Agregar al final | Pone un elemento al final de la fila | `offer` / `enqueue` |
| Sacar del frente | Extrae el primero de la fila | `poll` / `dequeue` |
| Ver el frente | Mira sin eliminar el primero | `peek` |

### 🌐 Perspectiva universal

```python
# Python: collections.deque como queue
from collections import deque
cola = deque()
cola.append("ticket-1")    # enqueue
cola.append("ticket-2")
cola.append("ticket-3")
print(cola.popleft())      # dequeue → "ticket-1"
print(cola.popleft())      # → "ticket-2"
```

### ☕ En Java

```java
import java.util.ArrayDeque;
import java.util.Queue;

Queue<String> cola = new ArrayDeque<>();

cola.offer("ticket-1");   // agrega al final
cola.offer("ticket-2");
cola.offer("ticket-3");
// Estado: [ticket-1, ticket-2, ticket-3] — ticket-1 es el frente

System.out.println(cola.peek());   // "ticket-1" — solo mira
System.out.println(cola.poll());   // "ticket-1" — extrae
System.out.println(cola.poll());   // "ticket-2"
System.out.println(cola.size());   // 1
```

### ✅ Cuándo usarla

- **Procesamiento de tareas en orden**: impresoras, colas de mensajes (Kafka, RabbitMQ).
- **Atención de tickets**: el primero en crear el ticket, primero en ser atendido.
- **BFS** (Búsqueda en Anchura) en grafos y árboles.
- **Buffer de datos**: cuando el productor genera más rápido de lo que el consumidor procesa.

### 🌍 Ejemplos reales

- **Cola de impresión compartida en una oficina:** los documentos se imprimen en el orden en que se enviaron — el primero en llegar, primero en salir.
- **Sistema de turnos en un hospital o banco:** el paciente/cliente que llegó primero es atendido primero. El número de turno es su posición en la cola.
- **Colas de mensajería en microservicios (Kafka, RabbitMQ, SQS):** un servicio produce eventos (ej: "nuevo pedido") y los encola; otro servicio los consume en orden para procesarlos.
- **Notificaciones push pendientes en un celular sin internet:** se encolan y se envían en orden cuando vuelve la conexión.
- **Descarga de canciones offline en Spotify:** las canciones se agregan a una cola y se descargan una a una en el orden en que se añadieron.

### ⚠️ Stack vs Queue

| | Stack (LIFO) | Queue (FIFO) |
|--|-------------|-------------|
| Principio | Último en entrar, primero en salir | Primero en entrar, primero en salir |
| Analogía | Pila de platos | Fila del banco |
| Agrega en | El tope | El final |
| Extrae de | El tope | El frente |

---

## 5. Mapa (Map / Diccionario)

### 📖 Definición

Un **mapa** (*map*) es una estructura que almacena pares **clave → valor**. Cada clave es única; el valor se obtiene buscando por su clave. A diferencia de las listas, no se accede por posición numérica, sino por una clave de cualquier tipo.

> **Analogía:** un diccionario físico. Buscas la palabra (clave) y encuentras su definición (valor). No recorres todas las páginas — vas directamente a la clave.

### 🌐 Perspectiva universal

```python
# Python: dict
persona = {"nombre": "Ana", "edad": 25, "activo": True}
print(persona["nombre"])       # "Ana"
persona["ciudad"] = "Santiago" # agregar nueva clave
```
```javascript
// JavaScript: objeto o Map
const persona = { nombre: "Ana", edad: 25 };
console.log(persona.nombre);   // "Ana"
persona.ciudad = "Santiago";
```

### ☕ En Java

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> edades = new HashMap<>();

// Agregar pares clave→valor
edades.put("Ana", 25);
edades.put("Luis", 30);
edades.put("María", 28);

// Acceder por clave
System.out.println(edades.get("Ana"));           // 25
System.out.println(edades.get("Carlos"));        // null (clave no existe)
System.out.println(edades.getOrDefault("Carlos", 0));  // 0 (valor por defecto)

// Verificar
System.out.println(edades.containsKey("Luis"));  // true
System.out.println(edades.containsValue(30));    // true

// Eliminar
edades.remove("Luis");

// Recorrer
for (Map.Entry<String, Integer> entrada : edades.entrySet()) {
    System.out.println(entrada.getKey() + " tiene " + entrada.getValue() + " años");
}

// Mapa inmutable
Map<String, String> config = Map.of(
    "host", "localhost",
    "puerto", "8080"
);
```

### ⚠️ Tipos de Map en Java

| Tipo | Ordenado | Thread-safe | Cuándo usar |
|------|---------|------------|-------------|
| `HashMap` | ❌ | ❌ | Caso general — más rápido |
| `LinkedHashMap` | ✅ Orden de inserción | ❌ | Necesitas iterar en orden de inserción |
| `TreeMap` | ✅ Orden natural de claves | ❌ | Necesitas las claves ordenadas (alfabético) |
| `ConcurrentHashMap` | ❌ | ✅ | Acceso concurrente (multi-hilo) |

### 🌍 Ejemplos reales

- **Agenda de contactos del celular:** el nombre (clave) apunta a los datos de contacto (valor). Buscas por nombre, no por posición.
- **Configuración de una aplicación:** `"host" → "localhost"`, `"puerto" → "8080"`, `"timeout" → "30"`. Cada clave es un parámetro, cada valor es su configuración.
- **Caché de una API:** `URL → respuesta`. Antes de hacer la petición real, se consulta si la URL ya está en el mapa. Si está, se devuelve el resultado guardado sin volver a llamar al servidor externo.
- **Inventario de una tienda:** `código de producto → stock disponible`. Buscar el stock de "REF-1234" es inmediato, sin recorrer toda la lista de productos.
- **Tabla de precios de un restaurante:** `nombre del plato → precio`. El sistema de caja busca el precio por nombre del plato.
- **Headers de una petición HTTP:** `"Content-Type" → "application/json"`, `"Authorization" → "Bearer token..."`. Cada header es un par clave-valor.

---

## 6. Conjunto (Set)

### 📖 Definición

Un **conjunto** (*set*) es una colección de elementos **sin duplicados y sin orden garantizado** (en la mayoría de implementaciones). Está inspirado en la teoría de conjuntos matemáticos.

> **Analogía:** una lista de invitados a un evento. No importa el orden en que lleguen, y una misma persona no puede aparecer dos veces.

### 🌐 Perspectiva universal

```python
# Python: set
numeros = {1, 2, 3, 2, 1}
print(numeros)  # {1, 2, 3} — sin duplicados

# Operaciones de conjuntos
a = {1, 2, 3}
b = {2, 3, 4}
print(a & b)    # {2, 3}     — intersección
print(a | b)    # {1, 2, 3, 4} — unión
print(a - b)    # {1}          — diferencia
```

### ☕ En Java

```java
import java.util.HashSet;
import java.util.Set;

Set<String> etiquetas = new HashSet<>();

etiquetas.add("java");
etiquetas.add("backend");
etiquetas.add("java");      // duplicado: NO se agrega
etiquetas.add("spring");

System.out.println(etiquetas.size());           // 3 (no 4)
System.out.println(etiquetas.contains("java")); // true

// Eliminar duplicados de una lista usando Set
List<Integer> conDuplicados = List.of(1, 2, 3, 2, 1, 4, 3);
Set<Integer> sinDuplicados = new HashSet<>(conDuplicados);
// sinDuplicados: {1, 2, 3, 4}

// Set inmutable
Set<String> colores = Set.of("rojo", "verde", "azul");
```

### ✅ Cuándo usarlo

- Cuando necesitas **eliminar duplicados** de una colección.
- Cuando necesitas verificar **pertenencia muy rápido** (`contains` en O(1)).
- Cuando el orden no importa.
- Operaciones de conjuntos: intersección, unión, diferencia.

### 🌍 Ejemplos reales

- **Lista de emails únicos suscritos a un newsletter:** si el mismo correo intenta registrarse dos veces, el Set lo ignora automáticamente.
- **Etiquetas (tags) de un artículo de blog:** "java", "backend", "spring". No tiene sentido repetir una etiqueta; el orden tampoco importa.
- **Permisos de un usuario en un sistema:** `{"LEER", "ESCRIBIR", "ADMIN"}`. Verificar si un usuario tiene permiso es inmediato — no importa el orden, no puede haber duplicados.
- **IPs únicas que visitaron una página en el día:** un servidor recibe miles de requests. Para contar visitantes únicos, cada IP se agrega al Set — las repetidas se descartan automáticamente.
- **Palabras ya vistas al procesar un texto:** útil para detectar vocabulario único o evitar procesar la misma palabra dos veces.

### ⚠️ Tipos de Set en Java

| Tipo | Orden | Cuándo usar |
|------|-------|------------|
| `HashSet` | Sin orden | Caso general — más rápido |
| `LinkedHashSet` | Orden de inserción | Necesitas iterar en el orden en que se agregaron |
| `TreeSet` | Orden natural | Necesitas los elementos ordenados |

---

## 7. Árbol (Tree)

### 📖 Definición

Un **árbol** es una estructura de datos **jerárquica** formada por **nodos** conectados por **aristas** (enlaces). Tiene un nodo raíz (*root*) del que parten ramas, y cada nodo puede tener cero o más nodos hijos. Los nodos sin hijos se llaman **hojas** (*leaves*).

> **Analogía:** un árbol genealógico o el organigrama de una empresa. Hay un ancestro común (la raíz) y cada nodo tiene exactamente un padre — excepto la raíz, que no tiene ninguno.

```
          [CEO]               ← raíz
         /     \
      [CTO]   [CFO]          ← nodos internos
      /   \       \
  [Dev1] [Dev2]  [Cont]      ← hojas
```

### Vocabulario clave

| Término | Significado |
|---------|------------|
| **Raíz** (*root*) | El nodo sin padre — el punto de entrada al árbol |
| **Hijo** (*child*) | Nodo que depende directamente de otro |
| **Padre** (*parent*) | Nodo del que desciende otro |
| **Hoja** (*leaf*) | Nodo sin hijos |
| **Profundidad** (*depth*) | Distancia de un nodo a la raíz |
| **Altura** (*height*) | Distancia máxima desde la raíz hasta una hoja |
| **Subárbol** | Cualquier nodo y todos sus descendientes |

### Tipos más comunes

| Tipo | Característica | Uso típico |
|------|---------------|-----------|
| **Árbol binario** | Cada nodo tiene máximo 2 hijos | Base de muchos algoritmos de búsqueda |
| **Árbol binario de búsqueda (BST)** | Hijo izq. < padre < hijo der. | Búsqueda eficiente O(log n) |
| **Árbol equilibrado (AVL, Rojo-Negro)** | Se autobalancea para mantener O(log n) | `TreeMap`, `TreeSet` en Java |
| **Árbol N-ario** | Cada nodo puede tener N hijos | Sistema de archivos, DOM HTML |
| **Trie** | Árbol de prefijos de texto | Autocompletado de búsquedas |

### 🌍 Ejemplos reales

- **Sistema de archivos (Windows Explorer, Finder):** `C:/` es la raíz. Dentro hay carpetas (nodos internos) y archivos (hojas). Cada carpeta puede contener otras carpetas o archivos, formando un árbol N-ario.
- **DOM del navegador (HTML):** `<html>` es la raíz. Dentro está `<head>` y `<body>`, que a su vez contienen otros elementos. Cuando JavaScript hace `document.getElementById(...)`, recorre este árbol.
- **Organigrama de una empresa:** el CEO es la raíz. Cada director tiene gerentes bajo su cargo, y cada gerente tiene empleados. Es un árbol exacto.
- **Categorías de un e-commerce:** "Electrónica" → "Computadores" → "Laptops" → "Gaming". La jerarquía de categorías es un árbol.
- **Menú de navegación con submenús:** un menú principal tiene ítems, algunos de los cuales despliegan submenús, que a su vez pueden tener sub-submenús.
- **Árbol de decisión médico o de soporte técnico:** "¿Tiene fiebre? → Sí → ¿Hace más de 3 días? → Sí → Ir al médico". Cada pregunta es un nodo, cada respuesta es una rama.
- **Git (control de versiones):** los commits forman un árbol (o grafo). El historial de un branch es una rama del árbol de commits.
- **`TreeMap` y `TreeSet` en Java:** internamente usan un árbol rojo-negro para mantener las claves ordenadas con búsqueda, inserción y eliminación en O(log n).

### ✅ Cuándo usarlo

- Los datos tienen una **jerarquía natural** (padre → hijos).
- Necesitas **búsqueda eficiente** en datos ordenados (BST).
- Trabajas con **estructuras anidadas** de profundidad variable (directorios, JSON anidado, menús).
- Necesitas una colección **siempre ordenada** (`TreeMap`, `TreeSet`).

---

## 8. Grafo (Graph)

### 📖 Definición

Un **grafo** es la estructura de datos más general: un conjunto de **nodos** (también llamados *vértices*) conectados por **aristas** (*edges*). A diferencia del árbol, **no hay raíz**, los nodos pueden conectarse libremente entre sí y pueden existir **ciclos** (caminos que vuelven al mismo nodo).

> **Analogía:** un mapa de carreteras. Las ciudades son los nodos y las carreteras son las aristas. Puedes ir de ciudad A a ciudad B por distintos caminos, y puede haber rutas circulares.

```
Grafo no dirigido (las conexiones van en ambos sentidos):

  [Santiago] ——— [Valparaíso]
      |  \            |
      |   \           |
   [Rancagua] ——— [San Antonio]


Grafo dirigido (las conexiones tienen dirección, como un seguidor en Twitter):

  [Ana] ——→ [Luis]
    ↑           |
    |           ↓
  [María] ←—— [Pedro]
```

### Conceptos clave

| Término | Significado |
|---------|------------|
| **Nodo / Vértice** | Entidad en el grafo (ciudad, persona, página web) |
| **Arista / Arco** | Conexión entre dos nodos |
| **Dirigido** | Las aristas tienen dirección (→). Un seguidor de Twitter es dirigido |
| **No dirigido** | Las aristas no tienen dirección. Una amistad en Facebook es no dirigida |
| **Peso** | Valor asociado a una arista (ej.: distancia, costo, tiempo) |
| **Camino** | Secuencia de nodos conectados de A a B |
| **Ciclo** | Camino que empieza y termina en el mismo nodo |
| **Grado** | Número de aristas que llegan/salen de un nodo |

### Tipos comunes

| Tipo | Característica | Ejemplo |
|------|---------------|---------|
| **No dirigido** | Conexión en ambos sentidos | Amistad en Facebook |
| **Dirigido (dígrafo)** | Conexión con dirección | Seguidor en Twitter/X, dependencias entre módulos |
| **Ponderado** | Aristas con peso/costo | Mapa con distancias, red con latencia |
| **Acíclico dirigido (DAG)** | Sin ciclos, con dirección | Dependencias de tareas, flujos de trabajo |
| **Árbol** | Caso especial de grafo: acíclico, conectado, con raíz | (ver sección anterior) |

### 🌍 Ejemplos reales

- **Google Maps / Waze:** las ciudades (o intersecciones) son nodos; las carreteras son aristas con peso (distancia o tiempo). El algoritmo busca el camino más corto en este grafo ponderado.
- **Redes sociales (LinkedIn, Facebook, Twitter):** cada persona es un nodo. Una amistad es una arista no dirigida; un "seguir" es una arista dirigida. "Personas que quizás conozcas" se calcula encontrando nodos cercanos en el grafo.
- **Internet:** los servidores y routers son nodos; los cables y conexiones inalámbricas son aristas. Cuando un paquete de datos viaja de Chile a Japón, sigue un camino en este inmenso grafo.
- **Dependencias de paquetes (npm, Maven, pip):** cada librería es un nodo; "A depende de B" es una arista dirigida. Instalar una librería instala automáticamente todo su subgrafo de dependencias.
- **Recomendaciones de Netflix/Spotify:** "a los usuarios que vieron X también les gustó Y" construye un grafo de relaciones entre contenidos y usuarios.
- **Flujos de trabajo (GitHub Actions, Jira):** las tareas de un sprint o los pasos de un pipeline CI/CD forman un grafo acíclico dirigido (DAG). La tarea "desplegar" solo puede ejecutarse después de "testear", que depende de "compilar".
- **Mapeo de la web (Google Crawler):** cada página es un nodo; cada hipervínculo es una arista dirigida. Google recorre este enorme grafo para indexar el contenido.
- **Detección de fraude bancario:** los nodos son cuentas y personas; las transacciones son aristas. Patrones sospechosos en el grafo (ciclos cortos, nodos con demasiadas conexiones) pueden indicar fraude.

### ✅ Cuándo usarlo

- Los datos representan **relaciones arbitrarias** entre entidades (no solo padre-hijo).
- Necesitas encontrar **caminos** entre nodos (rutas, conexiones, dependencias).
- Los datos tienen **conexiones circulares** o múltiples caminos entre los mismos puntos.
- Trabajas con **redes**: sociales, de transporte, de comunicación, de dependencias.

### ⚠️ Árbol vs Grafo

| | Árbol | Grafo |
|--|-------|-------|
| ¿Tiene raíz? | ✅ Siempre | ❌ No necesariamente |
| ¿Puede haber ciclos? | ❌ No | ✅ Sí |
| ¿Un nodo puede tener varios padres? | ❌ Solo un padre | ✅ Sí |
| Relación | Jerárquica | Arbitraria |
| ¿Un árbol es un grafo? | ✅ Sí — caso especial | — |

---

## 9. ¿Cuál elegir?

```
¿Necesito acceso por posición/índice?
  ✅ Sí, tamaño fijo → Array
  ✅ Sí, tamaño dinámico → List (ArrayList)

¿Necesito acceso por clave (no por posición)?
  ✅ Sí → Map

¿Necesito eliminar duplicados o verificar pertenencia rápido?
  ✅ Sí → Set

¿Necesito procesar en orden de llegada (FIFO)?
  ✅ Sí → Queue

¿Necesito procesar el último primero (LIFO) o ir hacia atrás?
  ✅ Sí → Stack (Deque)

¿Los datos tienen una jerarquía natural (padre → hijos)?
  ✅ Sí, con búsqueda eficiente → Tree (TreeMap / TreeSet)
  ✅ Sí, con estructura anidada → Tree N-ario

¿Los datos tienen relaciones arbitrarias entre sí (redes, rutas, dependencias)?
  ✅ Sí → Graph
```

---

## 10. Tabla resumen

| Estructura | Duplicados | Orden | Acceso | Caso de uso típico |
|------------|-----------|-------|--------|-------------------|
| **Array** | ✅ Sí | ✅ Por índice | Por índice | Píxeles de imagen, asientos de avión, buffer de audio |
| **List** | ✅ Sí | ✅ Por inserción | Por índice | Carrito de compras, playlist, resultados de búsqueda |
| **Stack** | ✅ Sí | LIFO | Solo el tope | Ctrl+Z, botón "atrás" del navegador, call stack |
| **Queue** | ✅ Sí | FIFO | Solo el frente | Cola de impresión, turnos en hospital, colas Kafka |
| **Map** | ❌ Claves únicas | Por clave | Por clave | Agenda de contactos, caché, headers HTTP, configuración |
| **Set** | ❌ Sin duplicados | Sin orden | Sin índice | Emails únicos, etiquetas de blog, permisos de usuario |
| **Tree** | ✅ Sí | Jerárquico | Desde la raíz | Sistema de archivos, DOM HTML, categorías de tienda |
| **Graph** | ✅ Sí | Relacional | Desde cualquier nodo | Google Maps, redes sociales, dependencias npm |

---

## 📚 Literatura recomendada

| Libro | Autores | Nivel | Por qué leerlo |
|-------|---------|-------|----------------|
| **Grokking Algorithms** | Aditya Bhargava | Principiante | El mejor libro de introducción a algoritmos y estructuras de datos. Ilustrado, con ejemplos en Python. Cubre arrays, listas, pilas, colas, grafos y árboles de forma visual |
| **Introduction to Algorithms (CLRS)** | Cormen, Leiserson, Rivest, Stein | Avanzado | La biblia de los algoritmos y estructuras de datos. Denso pero exhaustivo. Referencia para toda la carrera |
| **Data Structures and Algorithm Analysis in Java** | Mark Allen Weiss | Intermedio | Directamente en Java. Cubre todas las estructuras con análisis de complejidad |
| **Algorithms** | Robert Sedgewick & Kevin Wayne | Intermedio / Avanzado | Excelente cobertura de grafos y árboles con implementaciones Java. Tiene curso gratuito en Coursera |
| **The Algorithm Design Manual** | Steven Skiena | Intermedio / Avanzado | Mitad teoría, mitad "guerra de trincheras" — casos reales de cuándo usar cada estructura |

---

## 🔗 Enlaces de interés

| Recurso | URL | Por qué vale la pena |
|---------|-----|----------------------|
| **VisuAlgo** | https://visualgo.net/es | Visualizaciones animadas de estructuras de datos y algoritmos — ver el Stack, Queue, árbol y grafo en acción |
| **CS Visualized — Linked List, Tree, Graph** | https://dev.to/lydiahallie/cs-visualized-useful-git-commands-37p1 | Artículos visuales de estructuras de datos con animaciones |
| **Big-O Cheat Sheet** | https://www.bigocheatsheet.com | Tabla comparativa de complejidad de las operaciones de cada estructura |
| **Java Collections Framework** | https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html | Documentación oficial de todas las colecciones Java (List, Map, Set, Queue) |
| **LeetCode — Explore** | https://leetcode.com/explore/ | Práctica de estructuras de datos con ejercicios graduados por dificultad |
| **Graph Theory — Khan Academy** | https://www.khanacademy.org/computing/computer-science/algorithms | Introducción a grafos, BFS y DFS de forma accesible |

