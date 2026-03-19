# Módulo 01 — Situaciones básicas
> **Nivel:** 🟡 Básico — Java puro en consola, sin clases propias ni colecciones complejas.  
> **Prerequisito:** haber leído el [módulo 00](./00_como_pensar_un_problema.md).
---
## Tip 01 — "Quiero que el programa reaccione diferente según un valor"
### 📋 El escenario
Tienes un valor (un número, un texto, un estado) y el programa debe ejecutar cosas distintas según cuál sea. Puede ser un caso simple (dos caminos) o uno con muchas opciones discretas.
*Ejemplos:* mostrar un mensaje según el código HTTP recibido; calcular el precio según la categoría del producto.
### ❌ El error común
```java
// ❌ Comparar Strings con == → compara referencias de memoria, no contenido
String status = "ABIERTO";
if (status == "ABIERTO") { ... }  // puede fallar aunque visualmente parezca igual
// ❌ Cadena interminable de if/else para valores discretos conocidos
if      (categoria.equals("BASICO"))       precio = 5_000;
else if (categoria.equals("ESTANDAR"))     precio = 10_000;
else if (categoria.equals("PREMIUM"))      precio = 20_000;
else if (categoria.equals("EMPRESARIAL"))  precio = 50_000;
// ... y así con más categorías
```
### 🧠 ¿Cómo pienso esto?
```
¿Cuántas ramas tiene la decisión?
  2 caminos  → if / else  (o ternario si cabe en una línea)
  3+ caminos → switch expression  (más limpio, sin fall-through)
  Muchos pares clave→valor que pueden crecer → Map<clave, resultado>
¿Qué tipo es el valor que comparo?
  String  → equals() obligatorio, nunca ==
  int / enum → == o switch (ambos válidos)
```
### ✅ La solución
```java
// ── Caso simple: dos caminos ─────────────────────────────────────────────────
int edad = 20;
String acceso = edad >= 18 ? "Permitido" : "Denegado";   // ternario
// ── Varios caminos con String: switch expression (Java 14+) ──────────────────
String status = "EN_PROGRESO";
String mensaje = switch (status) {
    case "ABIERTO"     -> "Pendiente de atención";
    case "EN_PROGRESO" -> "Siendo trabajado";
    case "CERRADO"     -> "Resuelto";
    default            -> "Estado desconocido: " + status;
};
System.out.println(mensaje);  // "Siendo trabajado"
// ── Muchos pares clave→valor (pueden crecer o venir de configuración): Map ────
Map<String, Double> precios = Map.of(
    "BASICO",       5_000.0,
    "ESTANDAR",    10_000.0,
    "PREMIUM",     20_000.0,
    "EMPRESARIAL", 50_000.0
);
String categoria = "PREMIUM";
double precio = precios.getOrDefault(categoria, 0.0);
System.out.printf("Precio: $%,.2f%n", precio);  // $20,000.00
// ── Con int: switch también funciona ─────────────────────────────────────────
int codigo = 404;
String descripcion = switch (codigo) {
    case 200 -> "OK";
    case 201 -> "Created";
    case 400 -> "Bad Request";
    case 404 -> "Not Found";
    case 500 -> "Internal Server Error";
    default  -> "Código " + codigo;
};
```
> 💡 **Regla:** usa `equals()` para comparar `String`, nunca `==`. Con 3 o más casos discretos del mismo tipo, `switch expression` es más legible que una cadena de `if/else if`. Si los pares pueden crecer o vienen de configuración, usa un `Map`.
---
## Tip 02 — "Quiero repetir algo hasta que ocurra una condición"
### 📋 El escenario
El programa debe seguir ejecutando un bloque mientras algo sea verdadero, o detenerse cuando ocurra un evento: el usuario acierta un dato, se alcanza un límite, se encuentra un elemento.
*Ejemplo:* pedir una contraseña hasta que sea correcta, con máximo 3 intentos.
### ❌ El error común
```java
int intentos = 0;
while (intentos < 3) {
    System.out.println("Ingrese contraseña");
    // ❌ olvidó incrementar intentos → bucle infinito
}
```
### 🧠 ¿Cómo pienso esto?
```
¿Sé cuántas veces se repite?
  Sí, N veces fijas      → for (int i = 0; i < N; i++)
  No, depende de algo    → while
Para while:
  1. Inicializar la variable de control ANTES del bucle
  2. Modificarla DENTRO del bucle (si no → bucle infinito)
  3. Usar break para salir anticipadamente cuando ocurra el evento
  4. Verificar DESPUÉS del bucle qué causó la salida (éxito o límite)
```
### ✅ La solución
```java
final String CLAVE_CORRECTA = "java21";
final int    LIMITE         = 3;
int          intentos       = 0;
boolean      acceso         = false;
Scanner scanner = new Scanner(System.in);
while (intentos < LIMITE) {
    System.out.printf("Contraseña (%d/%d): ", intentos + 1, LIMITE);
    String entrada = scanner.nextLine();
    intentos++;
    if (entrada.equals(CLAVE_CORRECTA)) {
        acceso = true;
        break;          // salir inmediatamente al acertar
    }
    System.out.println("Incorrecta.");
}
if (acceso) {
    System.out.println("✅ Bienvenido");
} else {
    System.out.println("❌ Cuenta bloqueada tras " + LIMITE + " intentos");
}
```
```java
// Variante: do-while → ejecuta al menos una vez antes de verificar la condición
int numero;
do {
    System.out.print("Ingresa un número entre 1 y 10: ");
    numero = scanner.nextInt();
} while (numero < 1 || numero > 10);
System.out.println("Número válido: " + numero);
```
> 💡 **Regla:** la variable de control del `while` **siempre** debe cambiar dentro del bucle. Usa `break` para salir cuando ocurra el evento buscado. Usa un `boolean` externo para distinguir, después del bucle, si saliste por éxito o por límite.
---
## Tip 03 — "Tengo una lista y quiero recorrerla, procesarla o buscar en ella"
### 📋 El escenario
Tienes una colección de elementos y necesitas: ejecutar algo sobre cada uno, buscar si existe alguno que cumple una condición, o quedarte solo con los que te interesan.
*Ejemplos:* mostrar tickets, verificar si un usuario ya existe, listar solo los elementos activos.
### ❌ El error común
```java
List<String> tickets = List.of("A", "B", "C");
// ❌ Condición <= en lugar de < → IndexOutOfBoundsException en el último elemento
for (int i = 0; i <= tickets.size(); i++) {
    System.out.println(tickets.get(i));
}
// ❌ Eliminar elementos mientras se recorre → ConcurrentModificationException
for (String t : tickets) {
    if (t.equals("B")) tickets.remove(t);   // falla en tiempo de ejecución
}
```
### 🧠 ¿Cómo pienso esto?
```
¿Qué necesito hacer?
  Ejecutar algo sobre cada elemento     → for-each  o  forEach + lambda
  Necesito el índice también            → for clásico  (i = 0; i < size; i++)
  ¿Existe alguno que cumple X?          → contains()  o  stream().anyMatch()
  Quedarme solo con los que cumplen X   → nueva lista vacía + for-each
                                          o  stream().filter().toList()
Nunca modificar la lista original mientras la recorro con for-each.
```
### ✅ La solución
```java
List<String> tickets = List.of("Error en login", "Botón roto", "Página lenta");
// ── Recorrer sin índice: for-each ─────────────────────────────────────────────
for (String t : tickets) {
    System.out.println("- " + t);
}
// ── Recorrer con índice: for clásico (< no <=) ────────────────────────────────
for (int i = 0; i < tickets.size(); i++) {
    System.out.printf("%d. %s%n", i + 1, tickets.get(i));
}
// ── Buscar si existe un elemento ──────────────────────────────────────────────
List<String> usuarios = new ArrayList<>(List.of("ana", "carlos", "maria"));
String nuevo = "carlos";
if (usuarios.contains(nuevo)) {
    System.out.println("Ya existe");
} else {
    usuarios.add(nuevo);
    System.out.println("Registrado");
}
// Búsqueda con condición más específica (ignora mayúsculas)
boolean existe = usuarios.stream().anyMatch(u -> u.equalsIgnoreCase(nuevo));
// ── Filtrar: crear una lista nueva, no modificar la original ──────────────────
List<String> conError = tickets.stream()
        .filter(t -> t.toLowerCase().contains("error"))
        .toList();
System.out.println(conError);   // [Error en login]
```
> 💡 **Regla:** el `for` clásico siempre es `i < lista.size()`, nunca `<=`. Prefiere `for-each` cuando no necesitas el índice. Nunca elimines o agregues elementos mientras recorres con `for-each` — filtra creando una lista nueva.
---
## Tip 04 — "Quiero asegurarme de que los datos son correctos antes de procesarlos"
### 📋 El escenario
Antes de ejecutar la lógica principal, los datos de entrada pueden ser nulos, vacíos, estar fuera de rango o violar una regla de negocio. Si no se detecta a tiempo, el error aparece más tarde y es más difícil de rastrear.
*Ejemplo:* un método que calcula el promedio no debería continuar si la lista está vacía o tiene notas fuera de escala.
### ❌ El error común
```java
// ❌ Procesar sin validar → excepción oscura en tiempo de ejecución
double calcularPromedio(List<Double> notas) {
    double suma = 0;
    for (double n : notas) suma += n;
    return suma / notas.size();   // ArithmeticException si la lista está vacía
}
// ❌ Validar con anidamiento profundo (pirámide de la muerte)
if (usuario != null) {
    if (usuario.isActivo()) {
        if (usuario.getRol().equals("ADMIN")) {
            // lógica real en el nivel 3 de anidamiento
        }
    }
}
```
### 🧠 ¿Cómo pienso esto?
```
Patrón: Guard Clauses (cláusulas de guardia)
  1. Verificar cada condición inválida AL INICIO del método
  2. Si una condición falla → salir inmediatamente (return / throw)
  3. El resto del método asume que los datos son válidos → sin anidamiento extra
Ventaja: el "caso feliz" queda plano, al final, sin indentación extra.
```
### ✅ La solución
```java
// ✅ Guard clauses: salir temprano en cada caso inválido
double calcularPromedio(List<Double> notas) {
    if (notas == null)    throw new IllegalArgumentException("La lista no puede ser null");
    if (notas.isEmpty())  throw new IllegalArgumentException("La lista no puede estar vacía");
    if (notas.stream().anyMatch(n -> n < 1.0 || n > 7.0))
                          throw new IllegalArgumentException("Notas deben estar entre 1.0 y 7.0");
    // a partir de aquí los datos son válidos — sin anidamiento
    return notas.stream().mapToDouble(Double::doubleValue).sum() / notas.size();
}
// ✅ Guard clauses vs pirámide: mismo resultado, mucho más legible
void verificarPublicacion(Usuario usuario) {
    if (usuario == null)                     { System.out.println("No encontrado"); return; }
    if (!usuario.isActivo())                 { System.out.println("Inactivo");      return; }
    if (!usuario.getRol().equals("PREMIUM")) { System.out.println("Sin permiso");   return; }
    if (usuario.getPostsHoy() >= 10)         { System.out.println("Límite diario"); return; }
    System.out.println("✅ Puede publicar");   // caso feliz al final, sin anidamiento
}
// Uso del método con guard clauses
try {
    double promedio = calcularPromedio(List.of(5.5, 6.0, 4.8));
    System.out.printf("Promedio: %.2f%n", promedio);   // 5.43
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
}
```
> 💡 **Regla:** valida al inicio del método con guard clauses. Si una condición es inválida, sal inmediatamente con `return` o `throw`. El "caso feliz" queda plano y sin anidamiento al final del método.
---
## Tip 05 — "Tengo un número guardado como texto y al operarlo falla o se concatena"
### 📋 El escenario
El programa recibe un valor numérico como `String` (consola, archivo, parámetro de URL) y al intentar operar con él el resultado es incorrecto o lanza una excepción en tiempo de ejecución.
*Ejemplo:* el usuario escribe `"25"` y al sumarle 1 obtienes `"251"` en lugar de `26`.
### ❌ El error común
```java
Scanner scanner = new Scanner(System.in);
String edadTexto = scanner.nextLine();   // usuario escribe "25"
System.out.println(edadTexto + 1);           // ❌ "251"   → concatenación, no suma
int edad = Integer.parseInt("veinticinco");  // ❌ NumberFormatException
```
### 🧠 ¿Cómo pienso esto?
```
TENGO:   un String que debería representar un número
QUIERO:  un int / double para operar matemáticamente
PASOS:
  1. Convertir con parseInt() / parseDouble() ANTES de operar
  2. Limpiar espacios con .trim() antes de parsear
  3. El String puede no ser un número válido → try-catch NumberFormatException
  4. Validar el rango si el negocio lo exige (no solo que sea número)
```
### ✅ La solución
```java
// ── Tabla de conversiones ────────────────────────────────────────────────────
int    entero  = Integer.parseInt("42");         // "42"    → 42
double decimal = Double.parseDouble("3.14");     // "3.14"  → 3.14
long   grande  = Long.parseLong("9000000000");   // String  → long
String s1 = String.valueOf(42);                  // 42      → "42"
String s2 = String.format("%.2f", 3.14);         // 3.14    → "3.14"
// ── Conversión segura desde consola ──────────────────────────────────────────
Scanner scanner = new Scanner(System.in);
System.out.print("Ingresa tu edad: ");
String entrada = scanner.nextLine();
try {
    int edad = Integer.parseInt(entrada.trim());   // .trim() elimina espacios
    if (edad < 0 || edad > 150) {
        System.out.println("Edad fuera de rango");
    } else {
        System.out.println("El año que viene tendrás: " + (edad + 1));  // suma real
    }
} catch (NumberFormatException e) {
    System.out.println("'" + entrada + "' no es un número válido");
}
// ── Suma vs concatenación: el tipo del operando izquierdo decide ──────────────
System.out.println("Resultado: " + 3 + 4);     // ❌ "Resultado: 34"
System.out.println("Resultado: " + (3 + 4));   // ✅ "Resultado: 7"
```
> 💡 **Regla:** convierte el `String` a número con `parseInt` / `parseDouble` **antes** de operar. Usa `.trim()` para eliminar espacios. Protégete del `NumberFormatException` con `try-catch`. El `+` entre un `String` y cualquier otro valor siempre concatena — usa paréntesis para forzar la operación aritmética.

---

## Tip 06 — "Necesito combinar varias condiciones en un solo if"

### 📋 El escenario
La lógica exige que se cumplan varias condiciones a la vez (o al menos una). Anidar `if` dentro de `if` funciona pero es difícil de leer y mantener.

*Ejemplo:* un usuario puede acceder solo si tiene sesión activa Y su rol es `ADMIN` Y no está bloqueado.

### ❌ El error común
```java
// ❌ Tres if anidados cuando uno solo alcanza
if (usuario.isActivo()) {
    if (usuario.getRol().equals("ADMIN")) {
        if (!usuario.isBloqueado()) {
            // lógica real al tercer nivel de indentación
        }
    }
}
```

### 🧠 ¿Cómo pienso esto?
```
Operadores lógicos en Java:
  &&  → Y lógico:  ambas deben ser true
  ||  → O lógico:  al menos una debe ser true
  !   → Negación:  invierte el valor booleano

Cortocircuito:
  A && B → si A es false, B ni se evalúa
  A || B → si A es true,  B ni se evalúa
```

### ✅ La solución

```java
// ── Un solo if con todas las condiciones ──────────────────────────────────────
boolean puedeAcceder = usuario.isActivo()
        && usuario.getRol().equals("ADMIN")
        && !usuario.isBloqueado();

if (puedeAcceder) {
    System.out.println("✅ Acceso concedido");
} else {
    System.out.println("❌ Acceso denegado");
}

// ── || para condiciones alternativas ─────────────────────────────────────────
String rol = usuario.getRol();
if (rol.equals("ADMIN") || rol.equals("MODERADOR")) {
    System.out.println("Puede gestionar contenido");
}

// ── ! para invertir ───────────────────────────────────────────────────────────
if (!lista.isEmpty()) {
    procesarPrimero(lista.get(0));
}

// ── Mezcla con paréntesis para claridad ──────────────────────────────────────
// ¿Puede editar? → es admin  O  (es autor Y el contenido es suyo)
boolean puedeEditar = esAdmin || (esAutor && esContenidoPropio);
```

> 💡 **Regla:** `&&` para "todo debe cumplirse", `||` para "al menos uno debe cumplirse", `!` para invertir. Cuando mezcles los tres en la misma expresión, usa paréntesis — el código se lee más rápido y el orden de evaluación queda explícito.

---

## Tip 07 — "Quiero saber si un valor está dentro de un rango"

### 📋 El escenario
Necesitas comprobar si un número está entre un mínimo y un máximo, si una fecha cae en un periodo o si una cadena tiene la longitud correcta.

*Ejemplo:* verificar que la nota esté entre 1.0 y 7.0, o que la prioridad esté entre 1 y 5.

### ❌ El error común
```java
// ❌ Comparación incompleta — falta el límite superior
int prioridad = 6;
if (prioridad > 0) {  // acepta 6, 7, 100... sin límite
    System.out.println("Válida");
}

// ❌ Lógica invertida con &&: nunca puede ser cierto a la vez
if (prioridad < 1 && prioridad > 5) {  // imposible
    System.out.println("Inválida");
}
```

### 🧠 ¿Cómo pienso esto?
```
¿El valor está DENTRO del rango [min, max]?
  valor >= min && valor <= max

¿El valor está FUERA del rango?
  valor < min || valor > max   ← OR, no AND

Nemotécnica:
  "dentro"  →  && (debe cumplir AMBOS extremos)
  "fuera"   →  || (basta con romper UNO de los extremos)
```

### ✅ La solución

```java
// ── Dentro del rango ──────────────────────────────────────────────────────────
double nota = 5.5;
boolean notaValida = nota >= 1.0 && nota <= 7.0;

// ── Fuera del rango (para lanzar error) ──────────────────────────────────────
if (nota < 1.0 || nota > 7.0) {
    throw new IllegalArgumentException("Nota fuera de rango: " + nota);
}

// ── Clamping: forzar al rango si se pasa ─────────────────────────────────────
int valorUsuario = 12;
int valorSeguro  = Math.max(1, Math.min(valorUsuario, 10));  // resultado: 10

// ── Descuento escalonado por rangos ──────────────────────────────────────────
double compra = 75_000;
double descuento;

if      (compra >= 100_000)                          descuento = 20.0;
else if (compra >=  50_000 && compra < 100_000)      descuento = 10.0;
else if (compra >=  20_000 && compra <  50_000)      descuento =  5.0;
else                                                 descuento =  0.0;

System.out.printf("Descuento: %.0f%%%n", descuento);  // 10%
```

> 💡 **Regla:** "dentro del rango" usa `&&` (las dos condiciones deben ser true). "Fuera del rango" usa `||` (basta con que una falle). La confusión entre `&&` y `||` en rangos es la fuente de bugs lógicos muy silenciosos.

---

## Tip 08 — "Quiero contar cuántos elementos de una lista cumplen una condición"

### 📋 El escenario
Tienes una colección y necesitas saber cuántos elementos satisfacen un criterio: cuántos tickets están abiertos, cuántos estudiantes aprobaron, cuántos productos tienen stock bajo.

### ❌ El error común
```java
// ❌ return dentro del bucle → devuelve en el primero que cumple, no cuenta todos
int contador = 0;
for (String estado : estados) {
    if (estado.equals("ABIERTO")) {
        contador++;
        return contador;  // ❌ sale al encontrar el primero
    }
}
```

### 🧠 ¿Cómo pienso esto?
```
TENGO:   lista con N elementos
QUIERO:  cuántos cumplen la condición X

PASOS:
  1. Inicializar contador = 0  FUERA del bucle
  2. Recorrer TODOS los elementos (sin return ni break)
  3. Si cumple la condición → contador++
  4. Usar el resultado DESPUÉS del bucle
```

### ✅ La solución

```java
List<String> estados = List.of("ABIERTO", "CERRADO", "ABIERTO", "EN_PROGRESO", "ABIERTO");

// ── Opción A: for-each con contador ───────────────────────────────────────────
int abiertos = 0;
for (String estado : estados) {
    if (estado.equals("ABIERTO")) abiertos++;
}
System.out.println("Abiertos: " + abiertos);   // 3

// ── Opción B: stream().filter().count() ──────────────────────────────────────
long cantidad = estados.stream()
        .filter(e -> e.equals("ABIERTO"))
        .count();

// ── Múltiples contadores en un solo recorrido ─────────────────────────────────
int cntAbierto = 0, cntCerrado = 0, cntProgreso = 0;
for (String e : estados) {
    switch (e) {
        case "ABIERTO"     -> cntAbierto++;
        case "CERRADO"     -> cntCerrado++;
        case "EN_PROGRESO" -> cntProgreso++;
    }
}
System.out.printf("Abiertos: %d | Cerrados: %d | En progreso: %d%n",
    cntAbierto, cntCerrado, cntProgreso);
```

> 💡 **Regla:** el contador se inicializa en `0` **fuera** del bucle y se incrementa dentro. Nunca uses `return` ni `break` dentro de un contador — eso lo convierte en "buscar el primero", no en "contar todos".

---

## Tip 09 — "Quiero sumar todos los valores de una lista"

### 📋 El escenario
Necesitas calcular el total de un conjunto de valores: la suma de precios de un carrito, el total de horas trabajadas, la suma de calificaciones para promediar.

### ❌ El error común
```java
// ❌ El acumulador se declara DENTRO del bucle → se reinicia en cada iteración
for (double precio : precios) {
    double total = 0;   // reinicia cada vuelta
    total += precio;    // solo tiene el último valor
}
```

### 🧠 ¿Cómo pienso esto?
```
PASOS:
  1. Declarar acumulador = 0.0  FUERA y ANTES del bucle
  2. En cada iteración: acumulador += valorActual
  3. Usar el resultado DESPUÉS del bucle

Declararlo DENTRO del bucle reinicia el acumulador en cada vuelta.
```

### ✅ La solución

```java
List<Double> precios = List.of(9_990.0, 15_500.0, 3_200.0, 8_750.0);

// ── Opción A: acumulador clásico ──────────────────────────────────────────────
double total = 0.0;   // ← FUERA del bucle
for (double precio : precios) {
    total += precio;
}
System.out.printf("Total: $%,.2f%n", total);   // $37,440.00

// ── Opción B: stream ──────────────────────────────────────────────────────────
double totalStream = precios.stream()
        .mapToDouble(Double::doubleValue)
        .sum();

// ── Promedio ──────────────────────────────────────────────────────────────────
double promedio = total / precios.size();

// ── Estadísticas completas ────────────────────────────────────────────────────
DoubleSummaryStatistics stats = precios.stream()
        .mapToDouble(Double::doubleValue)
        .summaryStatistics();
System.out.println("Suma: " + stats.getSum());
System.out.println("Prom: " + stats.getAverage());
System.out.println("Máx:  " + stats.getMax());
System.out.println("Mín:  " + stats.getMin());
```

> 💡 **Regla:** el acumulador se declara e inicializa en `0` o `0.0` **antes** del bucle. Declararlo dentro del bucle es el error más frecuente con acumuladores — lo reinicia en cada iteración.

---

## Tip 10 — "La división entre dos enteros me da 0 o un número sin decimales"

### 📋 El escenario
Divides dos números enteros y el resultado es siempre entero aunque esperas decimal. O el resultado es `0` porque el dividendo es menor que el divisor.

*Ejemplo:* `7 / 2` debería dar `3.5` pero da `3`. O `3 / 10` debería dar `0.3` pero da `0`.

### ❌ El error común
```java
int aprobados = 7, total = 10;
double porcentaje = aprobados / total * 100;  // ❌ resultado: 0.0
// Porque: int/int = 0, luego 0 * 100 = 0
```

### 🧠 ¿Cómo pienso esto?
```
Java:  int / int = int  → trunca los decimales

Para obtener decimal, al menos UN operando debe ser double:
  Opción 1: cast explícito: (double) entero / otroEntero
  Opción 2: multiplicar por 1.0 antes de dividir
  Opción 3: declarar las variables como double

El cast debe aplicarse ANTES de la división, no después.
```

### ✅ La solución

```java
System.out.println(7 / 2);         // 3    ← división entera
System.out.println(7 / 2.0);       // 3.5  ← uno es double → resultado double
System.out.println(7.0 / 2);       // 3.5

int aprobados = 7, total = 10;

// ❌ Ambos int → trunca
double mal = aprobados / total;              // 0.0

// ✅ Cast explícito antes de dividir
double pct = (double) aprobados / total;     // 0.7
System.out.printf("Aprobaron: %.1f%%%n", pct * 100);  // "70.0%"

// ✅ Multiplicar por 1.0
double pct2 = aprobados * 1.0 / total;      // 0.7

// ── División entera INTENCIONAL (cuando quieres el entero) ───────────────────
int minutos = 137;
int horas   = minutos / 60;   // 2   (intentional int)
int resto   = minutos % 60;   // 17
System.out.printf("%dh %dmin%n", horas, resto);  // "2h 17min"
```

> 💡 **Regla:** `int / int` siempre devuelve `int` en Java, descartando los decimales. Castea uno de los operandos con `(double)` **antes** de la división. Recuerda que multiplicar por `100` para porcentajes debe hacerse **después** del cast.

---

## Tip 11 — "Quiero ejecutar un bloque de código exactamente N veces"

### 📋 El escenario
Necesitas repetir una acción un número fijo de veces conocido: imprimir una tabla, generar N objetos de prueba, ejecutar exactamente 10 iteraciones.

### ❌ El error común
```java
// ❌ Off-by-one: <= en lugar de < ejecuta N+1 veces
for (int i = 0; i <= 5; i++) {
    System.out.println("Vuelta " + i);  // imprime 0,1,2,3,4,5 → 6 veces
}
```

### 🧠 ¿Cómo pienso esto?
```
Forma canónica:
  for (int i = 0; i < N; i++)  →  ejecuta exactamente N veces (i: 0 a N-1)

Si necesitas i como número visible (1 a N): ajusta dentro del cuerpo con i+1
Si el rango importa (ej: del 1 al 10): usa i = 1; i <= 10
```

### ✅ La solución

```java
int N = 5;

// ── Forma canónica: 0 a N-1, exactamente N veces ─────────────────────────────
for (int i = 0; i < N; i++) {
    System.out.println("Iteración " + (i + 1) + " de " + N);
}

// ── Rango visible 1 a 10 ──────────────────────────────────────────────────────
for (int i = 1; i <= 10; i++) {
    System.out.printf("%d × 7 = %d%n", i, i * 7);  // tabla del 7
}

// ── Generar N objetos de prueba ───────────────────────────────────────────────
List<String> tickets = new ArrayList<>();
for (int i = 1; i <= 10; i++) {
    tickets.add("Ticket-" + String.format("%03d", i));  // "Ticket-001"...
}

// ── Cuenta regresiva ──────────────────────────────────────────────────────────
for (int i = N; i >= 1; i--) {
    System.out.println("Cuenta: " + i);
}
System.out.println("¡Ya!");
```

> 💡 **Regla:** `for (int i = 0; i < N; i++)` ejecuta exactamente N veces y es la forma más predecible. Si el número visible dentro del bucle importa, usa `i + 1`. El error off-by-one (`<` vs `<=`) es el bug más frecuente en bucles `for`.

---

## Tip 12 — "Tengo un valor que no debería cambiar y me lo modifican por error"

### 📋 El escenario
Usas un número o texto como si fuera configurable (el IVA, el límite de intentos, una URL base) pero está escrito directamente en el código sin protección. Alguien lo reasigna accidentalmente o el mismo valor aparece copiado en 5 lugares.

### ❌ El error común
```java
// ❌ Magic numbers — ¿qué significan 3 y 0.19?
if (intentos >= 3) bloquear();
double total = precio * 1.19;

// ❌ Variable en lugar de constante — nada impide reasignarla
double iva = 0.19;
iva = 0.21;  // accidentalmente en un refactor
```

### 🧠 ¿Cómo pienso esto?
```
Si un valor:
  - aparece en más de un lugar del código
  - no debería cambiar en tiempo de ejecución
  - tiene un significado de negocio
→ debe ser una CONSTANTE

Java: static final + NOMBRE_EN_MAYUSCULAS_CON_GUION
```

### ✅ La solución

```java
// ── Constantes de clase ───────────────────────────────────────────────────────
public class Config {
    public static final int    MAX_INTENTOS_LOGIN = 3;
    public static final double TASA_IVA           = 0.19;
    public static final String ROL_ADMIN          = "ADMIN";
    public static final int    PRIORIDAD_MIN       = 1;
    public static final int    PRIORIDAD_MAX       = 5;
}

// ── Uso — el nombre documenta el significado ──────────────────────────────────
if (intentos >= Config.MAX_INTENTOS_LOGIN) { bloquear(); }
double totalConIVA = precio * (1 + Config.TASA_IVA);

// ── final local: proteger dentro del método ───────────────────────────────────
final int limite = calcularLimite(usuario);
// limite = 10;  ← error de compilación → Java avisa inmediatamente

// ── enum como constantes de dominio ──────────────────────────────────────────
public enum TicketStatus { ABIERTO, EN_PROGRESO, CERRADO, CANCELADO }
```

> 💡 **Regla:** los valores que no cambian en tiempo de ejecución van en `static final` con nombre en `MAYUSCULAS`. Un cambio en la constante se propaga a todos los sitios automáticamente — no hay que buscar y reemplazar manualmente.

---

## Tip 13 — "Quiero saber si un número es par, múltiplo o tiene alguna propiedad numérica"

### 📋 El escenario
Necesitas verificar si un número cumple una propiedad basada en la división: si es par o impar, si es múltiplo de otro, si cae en cierta posición periódica.

### ❌ El error común
```java
// ❌ Comparar el cociente en lugar del resto
if (numero / 2 == 0) {  // nunca es true para números >= 2
    System.out.println("Es par");
}
```

### 🧠 ¿Cómo pienso esto?
```
El operador % (módulo) devuelve el RESTO de la división entera.

  numero % 2 == 0  → es par      (el resto al dividir por 2 es 0)
  numero % 2 != 0  → es impar
  numero % N == 0  → es múltiplo de N
  i % 3 == 0       → cada 3 posiciones (0, 3, 6, 9...)
```

### ✅ La solución

```java
// ── Par / Impar ───────────────────────────────────────────────────────────────
int n = 17;
System.out.println(n % 2 == 0 ? "par" : "impar");   // "impar"

// ── Múltiplo de N ─────────────────────────────────────────────────────────────
System.out.println(15 % 5 == 0);   // true → 15 es múltiplo de 5
System.out.println(15 % 7 == 0);   // false

// ── Acción periódica: aplicar algo cada N iteraciones ─────────────────────────
for (int i = 1; i <= 9; i++) {
    System.out.print(i);
    if (i % 3 == 0) System.out.print(" | ");  // separador cada 3
    else            System.out.print(", ");
}
// 1, 2, 3 | 4, 5, 6 | 7, 8, 9 |

// ── Turno circular (asignar mesa en restaurante) ──────────────────────────────
int mesas = 4;
for (int cliente = 1; cliente <= 10; cliente++) {
    int mesa = ((cliente - 1) % mesas) + 1;
    System.out.printf("Cliente %d → Mesa %d%n", cliente, mesa);
}

// ── Año bisiesto ──────────────────────────────────────────────────────────────
int anio = 2024;
boolean bisiesto = (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0);
```

> 💡 **Regla:** `%` devuelve el **resto**, no el cociente. `n % 2 == 0` es "es par". `n % N == 0` es "es múltiplo de N". Es uno de los operadores más usados en lógica de programación y algoritmos.

---

## Tip 14 — "Tengo el mismo bloque de código copiado en varios lugares"

### 📋 El escenario
El mismo bloque de 5-10 líneas aparece copiado en distintos lugares. Cuando hay un bug, hay que corregirlo en todos — y es fácil olvidar uno.

### ❌ El error común
```java
// ❌ Mismo cálculo en 3 lugares del main — si cambia la tasa de IVA, hay que cambiarla en 3 sitios
double precio1 = 10_000;
System.out.printf("Neto: $%,.2f | Total: $%,.2f%n", precio1, precio1 * 1.19);

double precio2 = 25_000;
System.out.printf("Neto: $%,.2f | Total: $%,.2f%n", precio2, precio2 * 1.19);

double precio3 = 8_500;
System.out.printf("Neto: $%,.2f | Total: $%,.2f%n", precio3, precio3 * 1.19);
```

### 🧠 ¿Cómo pienso esto?
```
Patrón: Extracción de método

Pasos:
  1. Identificar qué CAMBIA entre las copias → serán los PARÁMETROS
  2. Identificar qué es SIEMPRE IGUAL → será el CUERPO del método
  3. Extraer a un método con esos parámetros
  4. Reemplazar cada copia por una llamada al método
```

### ✅ La solución

```java
// ✅ Método extraído: la lógica vive en un solo lugar
static void imprimirConIVA(String descripcion, double precioNeto) {
    final double IVA   = 0.19;
    double montoIVA    = precioNeto * IVA;
    double total       = precioNeto + montoIVA;
    System.out.printf("%-20s Neto: $%,9.2f | IVA: $%,8.2f | Total: $%,10.2f%n",
            descripcion, precioNeto, montoIVA, total);
}

public static void main(String[] args) {
    imprimirConIVA("Teclado mecánico",  10_000);
    imprimirConIVA("Monitor 27\"",       25_000);
    imprimirConIVA("Pad ergonómico",      8_500);
    // Si la tasa cambia → se cambia en UN solo lugar
}
```

> 💡 **Regla:** si copias y pegas un bloque, ese bloque merece su propio método. Los parámetros son lo que cambia entre copias; el cuerpo es lo que siempre es igual. Un cambio en el método se propaga a todos los sitios.

---

## Tip 15 — "Necesito guardar varios valores del mismo tipo sin crear una variable por cada uno"

### 📋 El escenario
Tienes 10 calificaciones, 5 nombres de productos o 20 IDs. Crear una variable por cada valor (`nota1`, `nota2`, `nota3`...) no escala y no se puede recorrer.

### ❌ El error común
```java
// ❌ Una variable por cada valor — no se puede recorrer ni escala
double nota1 = 5.5, nota2 = 4.8, nota3 = 6.2, nota4 = 3.9, nota5 = 7.0;
// ¿Cómo calculo el promedio? ¿Y si hay 100 notas?
```

### 🧠 ¿Cómo pienso esto?
```
¿Cuántos elementos y cambian?
  Cantidad fija conocida al compilar  → array  int[], double[]
  Cantidad variable o desconocida     → List<T>  (la más flexible)

¿Necesito modificar la lista después de crearla?
  Sí → new ArrayList<>()
  No → List.of()  (inmutable, más eficiente)
```

### ✅ La solución

```java
// ── ArrayList: lista mutable (agregar, eliminar, modificar) ──────────────────
List<Double> notas = new ArrayList<>();
notas.add(5.5);
notas.add(4.8);
notas.add(6.2);
notas.add(3.9);
notas.add(7.0);

System.out.println("Total:   " + notas.size());
System.out.println("Primera: " + notas.get(0));
System.out.println("Última:  " + notas.get(notas.size() - 1));

// ── List.of: lista inmutable (solo lectura) ───────────────────────────────────
List<String> estados = List.of("ABIERTO", "EN_PROGRESO", "CERRADO");

// ── Calcular el promedio ──────────────────────────────────────────────────────
double suma = 0;
for (double nota : notas) suma += nota;
double promedio = suma / notas.size();
System.out.printf("Promedio: %.2f%n", promedio);

// ── Con Streams ───────────────────────────────────────────────────────────────
double promedioStream = notas.stream()
        .mapToDouble(Double::doubleValue).average().orElse(0.0);

// ── Array primitivo (tamaño fijo, tipo primitivo) ─────────────────────────────
double[] calificaciones = {5.5, 4.8, 6.2, 3.9, 7.0};
System.out.println("Longitud: " + calificaciones.length);
```

> 💡 **Regla:** usa `List<T>` en lugar de variables individuales cuando tienes varios valores del mismo tipo. `new ArrayList<>()` para listas que crecen; `List.of()` para listas fijas. Nunca crees `valor1`, `valor2`, `valor3` — eso es una lista disfrazada.

---

## Resumen del módulo 01

| Tip | Situación | Herramienta clave |
|-----|-----------|------------------|
| 01 | Decidir según un valor | `equals()`, `switch` expression, `Map` |
| 02 | Repetir hasta que ocurra algo | `while` + `break` + `boolean` de control |
| 03 | Recorrer, buscar o filtrar una lista | `for-each`, `contains()`, `stream().filter()` |
| 04 | Validar antes de procesar | Guard clauses — `return` / `throw` temprano |
| 05 | String con número → operación incorrecta | `parseInt`, `parseDouble`, `NumberFormatException` |
| 06 | Combinar varias condiciones en un if | `&&`, `\|\|`, `!` con paréntesis |
| 07 | Verificar si un valor está en un rango | `>= min && <= max` / `< min \|\| > max` |
| 08 | Contar cuántos elementos cumplen una condición | Contador + `if` / `stream().filter().count()` |
| 09 | Sumar o acumular valores de una lista | Acumulador fuera del bucle / `mapToDouble().sum()` |
| 10 | División entera da resultado sin decimales | Cast `(double)` antes de dividir |
| 11 | Ejecutar un bloque exactamente N veces | `for (int i = 0; i < N; i++)` |
| 12 | Un valor que no debe cambiar nunca | `static final` + `NOMBRE_EN_MAYUSCULAS` |
| 13 | Saber si un número es par, múltiplo, etc. | Operador `%` (módulo / resto) |
| 14 | El mismo bloque copiado en varios lugares | Extraer método con parámetros |
| 15 | Guardar varios valores del mismo tipo | `List<T>`, `ArrayList`, `List.of()` |

→ [Siguiente: Situaciones intermedias](./02_situaciones_intermedias.md)
