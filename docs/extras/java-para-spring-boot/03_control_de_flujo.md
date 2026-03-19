# Módulo 03 — Control de flujo moderno

> **Objetivo:** dominar todas las estructuras de control de Java: condicionales (`if/else`, `switch` clásico y expression), bucles (`for`, `for-each`, `while`, `do-while`, `break`, `continue`), pattern matching moderno con `instanceof` y `switch` (Java 16-21), y buenas prácticas al escribir condiciones compuestas con operadores lógicos.

---

## 3.1 `if / else if / else`

La sentencia `if` le dice a Java: *"ejecuta este bloque solo si esta condición es verdadera"*. La condición entre paréntesis debe resultar en un `boolean`; si es `true`, entra al bloque `{}`; si es `false`, lo salta.

Cuando necesitas evaluar más de una posibilidad exclusiva, encadenas `else if`. Java evalúa las condiciones **de arriba hacia abajo** y ejecuta el **primer** bloque cuya condición sea `true`, ignorando el resto. El `else` final actúa como "en cualquier otro caso".

```java
int codigo = 404;

if (codigo == 200) {
    System.out.println("OK");
} else if (codigo == 404) {
    System.out.println("No encontrado");
} else if (codigo >= 500) {
    System.out.println("Error del servidor");
} else {
    System.out.println("Código desconocido");
}
```

> 💡 Ordena los `else if` de la condición más específica a la más general. Si inviertes el orden puedes capturar casos de forma incorrecta.

### Operador ternario

Para asignaciones condicionales simples de una línea, el operador ternario `? :` es más expresivo que un bloque `if/else` completo. Funciona así: `condición ? valorSiTrue : valorSiFalse`. Solo úsalo cuando ambas ramas sean cortas y legibles; si necesitas lógica compleja, prefiere el `if/else` clásico.

```java
// if/else verboso
String mensaje;
if (activo) {
    mensaje = "Usuario activo";
} else {
    mensaje = "Usuario inactivo";
}

// Equivalente con ternario — una sola línea
String mensaje = activo ? "Usuario activo" : "Usuario inactivo";

// Ternario anidado (evitar más de un nivel de anidamiento)
String rol = esAdmin ? "ADMIN" : esEditor ? "EDITOR" : "LECTOR";
```

---

## 3.2 `switch` — la versión clásica

El `switch` clásico sirve para comparar **una sola variable** contra múltiples valores posibles. Es equivalente a una cadena de `if/else if`, pero más legible cuando hay muchos casos discretos (como un estado, un día de la semana o un código de operación).

Cada `case` define el valor que se compara; el compilador salta directamente al `case` que coincida. El `default` se ejecuta si ningún `case` coincide, y es opcional (aunque muy recomendable para manejar valores inesperados).

El detalle más importante del switch clásico es el **`break`**: sin él, la ejecución "cae" al siguiente case y lo ejecuta también, aunque su valor no coincida. Esto se llama *fall-through* y casi siempre es un bug.

```java
String dia = "LUNES";

switch (dia) {
    case "LUNES":
    case "MARTES":
    case "MIERCOLES":
    case "JUEVES":
    case "VIERNES":
        System.out.println("Día laboral");
        break;          // SIN break, cae al siguiente case (fall-through)
    case "SABADO":
    case "DOMINGO":
        System.out.println("Fin de semana");
        break;
    default:
        System.out.println("Día inválido");
}
```

> ⚠️ El `break` olvidado es uno de los bugs más frecuentes en Java clásico. La nueva sintaxis lo elimina.

---

## 3.3 `switch` expression (Java 14+) ⭐

La versión moderna del `switch` es una **expresión** (retorna un valor) y usa `->` en lugar de `:` + `break`.

```java
String dia = "LUNES";

// Switch como expresión: asigna directamente
String tipo = switch (dia) {
    case "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" -> "Laboral";
    case "SABADO", "DOMINGO"                                   -> "Fin de semana";
    default -> throw new IllegalArgumentException("Día desconocido: " + dia);
};

System.out.println(tipo); // "Laboral"

// Con yield: cuando necesitas múltiples líneas en un branch
int numeroDia = switch (dia) {
    case "LUNES"    -> 1;
    case "MARTES"   -> 2;
    case "MIERCOLES"-> 3;
    default         -> {
        System.out.println("Procesando día: " + dia);
        yield -1;  // yield devuelve el valor en bloques multilínea
    }
};
```

> ✅ Usa la switch expression moderna siempre que puedas. Elimina bugs de `break` y es más expresiva.

---

## 3.4 Pattern Matching en `instanceof` (Java 16+)

Antes de Java 16 tenías que castear manualmente después de `instanceof`. Ahora el casting es parte del patrón:

```java
// Estilo antiguo (Java 15 y anterior)
Object objeto = "Hola";
if (objeto instanceof String) {
    String s = (String) objeto;  // cast manual
    System.out.println(s.toUpperCase());
}

// Estilo moderno con pattern matching — Java 16+
if (objeto instanceof String s) {
    // 's' ya está disponible como String, sin cast manual
    System.out.println(s.toUpperCase());
}

// Ejemplo más real: procesar distintos tipos de respuesta
public String describir(Object respuesta) {
    if (respuesta instanceof String s) {
        return "Texto: " + s.toUpperCase();
    } else if (respuesta instanceof Integer n) {
        return "Número: " + (n * 2);
    } else if (respuesta instanceof List<?> lista) {
        return "Lista con " + lista.size() + " elementos";
    } else {
        return "Tipo desconocido";
    }
}
```

---

## 3.5 Pattern Matching en `switch` (Java 21) ⭐

Java 21 lleva el pattern matching al switch, combinando el poder de ambos:

```java
// Procesar distintos tipos con switch + pattern matching
public String procesarRespuestaHttp(Object cuerpo) {
    return switch (cuerpo) {
        case String s   -> "Texto plano (%d caracteres)".formatted(s.length());
        case Integer n  -> "Código numérico: " + n;
        case List<?> l  -> "Lista de %d elementos".formatted(l.size());
        case null       -> "Cuerpo vacío";
        default         -> "Formato desconocido: " + cuerpo.getClass().getSimpleName();
    };
}

// Guarded patterns: condición adicional en el case
public String clasificarPrecio(Object valor) {
    return switch (valor) {
        case Integer n when n < 0       -> "Precio inválido";
        case Integer n when n == 0      -> "Gratis";
        case Integer n when n < 10_000  -> "Económico";
        case Integer n                  -> "Premium ($" + n + ")";
        case Double d when d < 0        -> "Precio inválido";
        case Double d                   -> "Precio: $%.2f".formatted(d);
        default                         -> "Tipo no soportado";
    };
}
```

---

## 3.6 Bucles

Los bucles permiten **repetir un bloque de código** mientras se cumpla una condición. Java ofrece cuatro tipos, cada uno con un propósito diferente. Elegir el adecuado hace el código más claro y menos propenso a errores.

### `for` clásico

Úsalo cuando sabes **exactamente cuántas veces** quieres iterar, o cuando necesitas el índice de la posición actual. Tiene tres partes en su cabecera separadas por `;`: la inicialización (se ejecuta una sola vez al inicio), la condición (se evalúa antes de cada vuelta) y el paso (se ejecuta al final de cada vuelta).

```java
for (int i = 0; i < 5; i++) {
    System.out.println("Iteración: " + i);
}

// Hacia atrás
for (int i = 10; i >= 0; i--) {
    System.out.println(i);
}

// Con paso diferente
for (int i = 0; i <= 100; i += 10) {
    System.out.println(i);  // 0, 10, 20, ... 100
}
```

### `for-each` (enhanced for) — el más usado en Java

Cuando no necesitas el índice y solo quieres recorrer todos los elementos de una colección o array, el `for-each` es la opción más limpia. Es más conciso, más legible y elimina el riesgo de cometer errores de índice. Java lo llama internamente usando el `Iterator` de la colección.

```java
List<String> nombres = List.of("Ana", "Luis", "María");

// Forma más limpia cuando no necesitas el índice
for (String nombre : nombres) {
    System.out.println("Hola, " + nombre);
}

// Con arrays también funciona
int[] numeros = {1, 2, 3, 4, 5};
for (int n : numeros) {
    System.out.println(n);
}
```

### `while` y `do-while`

Usa `while` cuando **no sabes de antemano cuántas iteraciones** necesitarás — la condición determina si el bucle sigue. La diferencia clave entre ambos es el momento en que se evalúa la condición: `while` la evalúa **antes** de cada vuelta (puede no ejecutarse nunca si la condición inicia en `false`), mientras que `do-while` la evalúa **después** de cada vuelta, garantizando que el cuerpo se ejecuta **al menos una vez**.

```java
// while: verifica ANTES de ejecutar (puede no ejecutarse nunca)
int intentos = 0;
while (intentos < 3) {
    System.out.println("Intento " + (intentos + 1));
    intentos++;
}

// do-while: verifica DESPUÉS de ejecutar (se ejecuta AL MENOS una vez)
String respuesta;
do {
    respuesta = leerEntradaUsuario();
} while (!respuesta.equals("salir"));
```

### `break` y `continue`

Dentro de un bucle puedes alterar su flujo con dos palabras clave: `break` **termina** el bucle inmediatamente y continúa con el código que viene después, mientras que `continue` **salta** el resto del cuerpo de la iteración actual y pasa a la siguiente evaluación de la condición.

```java
// break: sale del bucle completo
for (int i = 0; i < 10; i++) {
    if (i == 5) break;       // para en 5, no imprime 5
    System.out.println(i);   // imprime 0, 1, 2, 3, 4
}

// continue: salta a la siguiente iteración
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue;  // salta los pares
    System.out.println(i);      // imprime 1, 3, 5, 7, 9
}

// Etiquetas (labels): para break/continue en bucles anidados
externo:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) break externo;  // sale del bucle externo
        System.out.println(i + "," + j);
    }
}
```

---

## 3.7 Operadores útiles

Los operadores son los símbolos que realizan cálculos o comparaciones entre valores. Java los agrupa según su función: aritméticos para matemática, compuestos para abreviar asignaciones, lógicos para combinar condiciones booleanas.

Un detalle importante es el **cortocircuito** en los operadores lógicos: con `&&` (AND), si el primer operando es `false`, Java **no evalúa el segundo** (porque el resultado ya no puede ser `true`). Con `||` (OR), si el primero es `true`, el segundo tampoco se evalúa. Esto es muy útil para evitar `NullPointerException` al encadenar condiciones.

```java
// Aritméticos
int suma  = 10 + 3;   // 13
int resta = 10 - 3;   // 7
int mult  = 10 * 3;   // 30
int div   = 10 / 3;   // 3  ← división entera: descarta los decimales
int mod   = 10 % 3;   // 1  ← módulo: el resto de la división entera

// Compuestos: forma abreviada de operar y reasignar en la misma variable
int n = 5;
n += 3;  // equivale a n = n + 3 → n = 8
n -= 2;  // equivale a n = n - 2 → n = 6
n *= 4;  // equivale a n = n * 4 → n = 24
n /= 6;  // equivale a n = n / 6 → n = 4
n++;     // post-incremento: n = 5 (usa el valor actual y luego incrementa)
++n;     // pre-incremento:  n = 6 (incrementa primero y luego usa el valor)

// Lógicos: combinan expresiones booleanas
boolean a = true, b = false;
System.out.println(a && b);  // false — AND: verdadero solo si los dos son true
System.out.println(a || b);  // true  — OR:  verdadero si al menos uno es true
System.out.println(!a);      // false — NOT: invierte el valor booleano

// Cortocircuito en acción: evita NullPointerException sin necesidad de try-catch
String s = null;
if (s != null && s.length() > 0) { // s.length() solo se llama si s != null
    System.out.println("No vacío");
}
// Si s fuera null y no hubiera cortocircuito, s.length() lanzaría NullPointerException
```

> 💡 La **sección 3.8** profundiza en operadores lógicos, tabla de verdad, leyes de De Morgan y seis buenas prácticas concretas para escribir condiciones compuestas correctas y mantenibles.

---

## 3.8 Operadores lógicos y buenas prácticas en condiciones compuestas

Saber usar `if` es fácil; saber escribir condiciones **claras, seguras y mantenibles** es lo que separa el código amateur del profesional. Esta sección profundiza en los operadores lógicos y en las reglas que hacen tus condiciones legibles y sin bugs.

### Tabla de operadores lógicos

| Operador | Nombre | Descripción | Cortocircuito |
|----------|--------|-------------|:---:|
| `&&` | AND lógico | `true` solo si **ambos** son `true` | ✅ Sí |
| `\|\|` | OR lógico | `true` si **al menos uno** es `true` | ✅ Sí |
| `!` | NOT lógico | Invierte el valor booleano | — |
| `&` | AND sin cortocircuito | `true` si ambos son `true`, evalúa **siempre** ambos | ❌ No |
| `\|` | OR sin cortocircuito | `true` si al menos uno es `true`, evalúa **siempre** ambos | ❌ No |
| `^` | XOR exclusivo | `true` si **exactamente uno** es `true` (no ambos) | — |

**Tabla de verdad:**

| A | B | `A && B` | `A \|\| B` | `!A` | `A ^ B` |
|:---:|:---:|:---:|:---:|:---:|:---:|
| `true`  | `true`  | `true`  | `true`  | `false` | `false` |
| `true`  | `false` | `false` | `true`  | `false` | `true`  |
| `false` | `true`  | `false` | `true`  | `true`  | `true`  |
| `false` | `false` | `false` | `false` | `true`  | `false` |

```java
boolean activo = true;
boolean admin  = false;

System.out.println(activo && admin);   // false — AND: ambos deben ser true
System.out.println(activo || admin);   // true  — OR: al menos uno es true
System.out.println(!activo);           // false — NOT: invierte
System.out.println(activo ^ admin);    // true  — XOR: exactamente uno es true
```

---

### Cortocircuito: tu primera línea de defensa

Con `&&`, si el primer operando es `false`, Java **no evalúa el segundo**; el resultado ya no puede ser `true`. Con `||`, si el primero es `true`, el segundo tampoco se evalúa. Esta propiedad no es solo una optimización: es la herramienta principal para evitar `NullPointerException` sin try-catch.

```java
// ✅ Null check antes de usar el objeto — si nombre es null, length() nunca se llama
String nombre = null;
if (nombre != null && nombre.length() > 0) {
    System.out.println("Nombre válido: " + nombre);
}

// ❌ Con & (sin cortocircuito) — evalúa SIEMPRE los dos lados: NullPointerException
if (nombre != null & nombre.length() > 0) {  // 💥 lanza NPE si nombre es null
    System.out.println("Nombre válido");
}

// ✅ Con || — si la caché es válida, ni siquiera llama a la base de datos
boolean cacheValida = true;
if (cacheValida || cargarDesdeBD(id) != null) {  // cargarDesdeBD() NO se llama
    // usa la caché
}
```

> 💡 **Regla de oro del cortocircuito:** pon siempre la condición más barata (o el null check) a la **izquierda** del `&&` o `||`.

---

### Leyes de De Morgan — simplificar negaciones

Las leyes de De Morgan te permiten transformar negaciones compuestas en formas más legibles:

| Original | Equivalente |
|----------|-------------|
| `!(A && B)` | `!A \|\| !B` |
| `!(A \|\| B)` | `!A && !B` |

```java
// ❌ Negación de una expresión compuesta — difícil de razonar de un vistazo
if (!(estado.equals("CERRADO") || estado.equals("CANCELADO"))) {
    procesarTicket(ticket);
}

// ✅ Aplicando De Morgan: !(A || B) → !A && !B — más directo
if (!estado.equals("CERRADO") && !estado.equals("CANCELADO")) {
    procesarTicket(ticket);
}

// ✅ Mejor aún: extraer la intención a un método con nombre positivo
if (estaActivo(estado)) {
    procesarTicket(ticket);
}

private boolean estaActivo(String estado) {
    return !estado.equals("CERRADO") && !estado.equals("CANCELADO");
}
```

---

### Buenas prácticas al escribir condiciones compuestas

#### ✅ BP-1: Nombra las condiciones complejas con variables booleanas

Cuando una condición tiene más de dos partes, extráela a variables con nombres que expliquen **qué significa** la condición, no cómo se calcula. El código se lee como prosa.

```java
// ❌ Una sola línea con cinco condiciones: hay que analizar todo antes de entender
if (usuario != null && usuario.isActivo() && usuario.getRol().equals("ADMIN") && !usuario.isBloqueado()) {
    accederPanel();
}

// ✅ Variables con nombres descriptivos: se lee de un vistazo
boolean usuarioValido = usuario != null && usuario.isActivo();
boolean tienePermiso  = usuario.getRol().equals("ADMIN");
boolean noBloqueado   = !usuario.isBloqueado();

if (usuarioValido && tienePermiso && noBloqueado) {
    accederPanel();
}
```

---

#### ✅ BP-2: Limita las condiciones por `if` — extrae a métodos

Más de 3 condiciones en un mismo `if` es una señal de que la lógica debe encapsularse en un método propio. Esto también facilita el testing unitario de esa validación de forma aislada.

```java
// ❌ Seis condiciones: imposible de leer de un vistazo
if (ticket != null && ticket.getTitulo() != null && !ticket.getTitulo().isBlank()
        && ticket.getEstado().equals("ABIERTO") && ticket.getPrioridad() >= 1
        && ticket.getPrioridad() <= 5) {
    procesarTicket(ticket);
}

// ✅ Método con nombre descriptivo: legible y fácil de probar de forma aislada
if (esTicketProcesable(ticket)) {
    procesarTicket(ticket);
}

private boolean esTicketProcesable(Ticket ticket) {
    if (ticket == null) return false;
    boolean tituloValido    = ticket.getTitulo() != null && !ticket.getTitulo().isBlank();
    boolean estadoAbierto   = ticket.getEstado().equals("ABIERTO");
    boolean prioridadValida = ticket.getPrioridad() >= 1 && ticket.getPrioridad() <= 5;
    return tituloValido && estadoAbierto && prioridadValida;
}
```

---

#### ✅ BP-3: Guard clauses — retorna temprano en vez de anidar

En lugar de envolver toda la lógica en bloques `if` anidados, comprueba las precondiciones al inicio del método y retorna (o lanza excepción) si no se cumplen. El "camino feliz" queda limpio y sin indentación profunda.

```java
// ❌ Anidamiento profundo: el camino principal está sepultado
public String procesarTicket(Ticket ticket) {
    if (ticket != null) {
        if (ticket.isActivo()) {
            if (!ticket.isBloqueado()) {
                return "Procesado: " + ticket.getTitulo();
            } else {
                return "Ticket bloqueado";
            }
        } else {
            return "Ticket inactivo";
        }
    } else {
        return "Ticket nulo";
    }
}

// ✅ Guard clauses: sale temprano en cada caso de error
public String procesarTicket(Ticket ticket) {
    if (ticket == null)       return "Ticket nulo";
    if (!ticket.isActivo())   return "Ticket inactivo";
    if (ticket.isBloqueado()) return "Ticket bloqueado";

    // El camino feliz es obvio y está sin anidamiento
    return "Procesado: " + ticket.getTitulo();
}
```

> 💡 Las guard clauses también se usan en Spring Boot en los métodos de servicio: validan parámetros de entrada antes de operar con la base de datos.

---

#### ✅ BP-4: Ordena las condiciones estratégicamente

El orden de las condiciones afecta tanto a la seguridad (evitar NPE) como al rendimiento (evitar operaciones costosas innecesarias).

```java
// ✅ Null check siempre a la IZQUIERDA del && — si falla, lo demás no se evalúa
if (usuario != null && usuario.getRol().equals("ADMIN")) { ... }

// ✅ Condición barata antes que operación costosa (llamada a BD, red, etc.)
if (intentos < MAX_REINTENTOS && validarTokenEnBD(token)) { ... }
//   ↑ comparación simple           ↑ operación costosa — solo si la simple pasa

// ✅ En &&: pon la más probable que sea false a la izquierda (cortocircuita antes)
if (esCacheValida && cargarDesdeBD(id) != null) { ... }

// ✅ En ||: pon la más probable que sea true a la izquierda (cortocircuita antes)
if (estaEnCache(id) || cargarDesdeBD(id) != null) { ... }
```

---

#### ✅ BP-5: Evita las cadenas de negaciones

Múltiples `!` seguidos o la negación de expresiones compuestas hacen el código muy difícil de razonar. Usa De Morgan o extrae un método con nombre positivo.

```java
// ❌ Doble negación — hay que hacer el cálculo mental de invertir dos veces
if (!usuario.isInactivo() && !usuario.isBloqueado()) { ... }

// ✅ Nombra el concepto con un método positivo
if (usuario.isActivo() && usuario.estaDisponible()) { ... }

// ❌ Negación de una expresión compuesta — muy difícil de leer
if (!(!activo || !admin)) { ... }

// ✅ Simplifica con De Morgan y extrae el nombre
boolean puedeOperar = activo && admin;
if (puedeOperar) { ... }
```

---

#### ✅ BP-6: Compara objetos con `equals()`, no con `==`

El operador `==` en objetos compara **referencias de memoria**, no el contenido. Para Strings y cualquier objeto usa `.equals()`. Cuando el objeto puede ser `null`, usa `Objects.equals()` o pon el literal a la izquierda.

```java
String estado = obtenerEstado();

// ❌ Compara referencias — puede dar false aunque el texto sea igual
if (estado == "ABIERTO") { ... }

// ✅ Compara contenido — correcto
if (estado.equals("ABIERTO")) { ... }

// ✅ Seguro si estado puede ser null — Objects.equals maneja null sin NPE
if (Objects.equals(estado, "ABIERTO")) { ... }

// ✅ "Yoda condition" — el literal a la izquierda nunca lanza NPE
if ("ABIERTO".equals(estado)) { ... }
```

---

### Antipatrones frecuentes

```java
// ❌ Comparar boolean con true/false — siempre es redundante
if (activo == true)  { ... }   // equivale a if (activo)
if (activo == false) { ... }   // equivale a if (!activo)

// ✅ Directo y sin ruido
if (activo)  { ... }
if (!activo) { ... }

// ❌ Retornar true/false desde un if — totalmente innecesario
public boolean esAdmin(Usuario u) {
    if (u.getRol().equals("ADMIN")) {
        return true;
    } else {
        return false;
    }
}

// ✅ Retorna la expresión booleana directamente
public boolean esAdmin(Usuario u) {
    return u.getRol().equals("ADMIN");
}

// ❌ Asignación accidental dentro del if — compila, pero es un bug
if (estado = "ABIERTO") { ... }     // asigna "ABIERTO" a estado, NO compara

// ✅ Comparación correcta
if (estado.equals("ABIERTO")) { ... }
```

> ⚠️ IntelliJ IDEA detecta la mayoría de estos antipatrones y los señala con advertencias amarillas. Actívalas: **Analyze → Inspect Code**.

---

## 🏋️ Ejercicios de práctica

### Ejercicio 3.1 — Switch expression
Reescribe el siguiente switch clásico como switch expression moderna:

```java
int mes = 3;
String estacion;
switch (mes) {
    case 12: case 1: case 2:
        estacion = "Verano";
        break;
    case 3: case 4: case 5:
        estacion = "Otoño";
        break;
    case 6: case 7: case 8:
        estacion = "Invierno";
        break;
    case 9: case 10: case 11:
        estacion = "Primavera";
        break;
    default:
        estacion = "Mes inválido";
}
```

<details>
<summary>🔍 Ver solución</summary>

```java
int mes = 3;
String estacion = switch (mes) {
    case 12, 1, 2  -> "Verano";
    case 3, 4, 5   -> "Otoño";
    case 6, 7, 8   -> "Invierno";
    case 9, 10, 11 -> "Primavera";
    default        -> throw new IllegalArgumentException("Mes inválido: " + mes);
};
```
</details>

---

### Ejercicio 3.2 — FizzBuzz con control de flujo
Imprime los números del 1 al 30. Si el número es divisible por 3 imprime "Fizz", si es divisible por 5 imprime "Buzz", si es divisible por ambos imprime "FizzBuzz".

<details>
<summary>🔍 Ver solución</summary>

```java
for (int i = 1; i <= 30; i++) {
    String resultado = switch (0) {
        default -> {
            if      (i % 15 == 0) yield "FizzBuzz";
            else if (i % 3  == 0) yield "Fizz";
            else if (i % 5  == 0) yield "Buzz";
            else                  yield String.valueOf(i);
        }
    };
    System.out.println(resultado);
}

// Alternativa más simple con ternario:
for (int i = 1; i <= 30; i++) {
    if      (i % 15 == 0) System.out.println("FizzBuzz");
    else if (i % 3  == 0) System.out.println("Fizz");
    else if (i % 5  == 0) System.out.println("Buzz");
    else                  System.out.println(i);
}
```
</details>

---

### Ejercicio 3.3 — Pattern matching
Escribe un método `describir(Object obj)` que use pattern matching con switch (Java 21) para retornar:
- Si es `String`: `"Texto de N letras: [valor en mayúsculas]"`
- Si es `Integer` mayor o igual a 0: `"Entero positivo: N"`
- Si es `Integer` negativo: `"Entero negativo: N"`
- Si es `Double`: `"Decimal: N.NN"`
- Si es `null`: `"Nulo"`
- Cualquier otro: `"Desconocido"`

<details>
<summary>🔍 Ver solución</summary>

```java
public static String describir(Object obj) {
    return switch (obj) {
        case null                   -> "Nulo";
        case String s               -> "Texto de %d letras: %s".formatted(s.length(), s.toUpperCase());
        case Integer n when n >= 0  -> "Entero positivo: " + n;
        case Integer n              -> "Entero negativo: " + n;
        case Double d               -> "Decimal: %.2f".formatted(d);
        default                     -> "Desconocido";
    };
}
```
</details>

---

### Ejercicio 3.4 — Condiciones compuestas y buenas prácticas

El siguiente método tiene **cinco problemas** relacionados con operadores lógicos y buenas prácticas. Identifícalos y reescribe el método aplicando todo lo aprendido en la sección 3.8.

```java
public String validarAcceso(Usuario usuario, String accion) {
    if (usuario != null) {
        if (usuario.isActivo() == true) {
            if (!(!usuario.isBloqueado())) {
                return "Bloqueado";
            }
            if (usuario.getRol() == "ADMIN" || usuario.getRol() == "EDITOR"
                    || usuario.getRol() == "MODERADOR" || usuario.getRol() == "SUPERVISOR"
                    || accion != null && !accion.isBlank()) {
                return "Acceso concedido para: " + accion;
            } else {
                return "Sin permiso";
            }
        } else {
            return "Inactivo";
        }
    } else {
        return "Usuario nulo";
    }
}
```

<details>
<summary>🔍 Ver solución</summary>

**Problemas identificados:**
1. `usuario.isActivo() == true` — comparación redundante de boolean con `true`.
2. `!(!usuario.isBloqueado())` — doble negación, equivale a `usuario.isBloqueado()`.
3. Anidamiento profundo con cuatro niveles de `if/else` — se corrige con guard clauses.
4. Comparación de `String` con `==` en lugar de `.equals()`.
5. Cinco condiciones en un solo `if` — deben extraerse a un método con nombre descriptivo.

```java
public String validarAcceso(Usuario usuario, String accion) {
    // Guard clauses — condiciones de error al inicio
    if (usuario == null)          return "Usuario nulo";
    if (!usuario.isActivo())      return "Inactivo";
    if (usuario.isBloqueado())    return "Bloqueado";

    // Condición compleja extraída con nombre descriptivo y equals() correcto
    boolean tieneRolPermitido = usuario.getRol().equals("ADMIN")
            || usuario.getRol().equals("EDITOR")
            || usuario.getRol().equals("MODERADOR")
            || usuario.getRol().equals("SUPERVISOR");
    boolean accionValida = accion != null && !accion.isBlank();

    if (tieneRolPermitido && accionValida) {
        return "Acceso concedido para: " + accion;
    }
    return "Sin permiso";
}
```

> 💡 Bonus: `tieneRolPermitido` podría moverse a un método `tieneRolPermitido(usuario)` o validarse contra un `Set<String>` de roles permitidos para mayor mantenibilidad.

</details>

---

*[← Módulo 02 — Sintaxis y tipos](./02_sintaxis_y_tipos.md) | [Índice](./README.md) | [Módulo 04 — Métodos →](./04_metodos.md)*

