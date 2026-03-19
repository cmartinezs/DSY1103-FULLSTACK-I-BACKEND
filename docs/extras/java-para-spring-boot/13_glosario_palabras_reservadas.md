# Módulo 13 — Glosario completo de palabras reservadas de Java

> **Objetivo:** conocer el significado, categoría, versión de introducción y forma de uso de **todas** las palabras que Java reserva para sí mismo y que **no pueden usarse como nombres de variables, métodos ni clases**.
>
> **Versión de referencia:** Java 21 (LTS).

---

## ¿Qué es una palabra reservada?

Una **palabra reservada** (*reserved word* o *keyword*) es un identificador que el compilador de Java ya usa internamente. Si intentas usarla como nombre de una variable, clase o método, el compilador lanzará un error de compilación.

```java
int class = 5;      // ❌ ERROR: 'class' es palabra reservada
int return = 10;    // ❌ ERROR: 'return' es palabra reservada
int miVariable = 7; // ✅ OK
```

Java tiene **tres grupos** de palabras reservadas:

| Grupo | Qué son | Ejemplos |
|-------|---------|---------|
| **Keywords** | Palabras clave del lenguaje | `if`, `while`, `class`, `static` |
| **Literales reservados** | Valores constantes del lenguaje | `true`, `false`, `null` |
| **Palabras contextuales** | Solo tienen significado especial en ciertos contextos (Java moderno) | `var`, `record`, `sealed` |

---

## Índice por categoría

1. [Tipos de datos primitivos](#1-tipos-de-datos-primitivos)
2. [Modificadores de acceso](#2-modificadores-de-acceso)
3. [Modificadores de comportamiento](#3-modificadores-de-comportamiento)
4. [Estructura del programa](#4-estructura-del-programa)
5. [Control de flujo — Condicionales](#5-control-de-flujo--condicionales)
6. [Control de flujo — Bucles](#6-control-de-flujo--bucles)
7. [Control de flujo — Retorno y salto](#7-control-de-flujo--retorno-y-salto)
8. [Orientación a objetos](#8-orientación-a-objetos)
9. [Manejo de excepciones](#9-manejo-de-excepciones)
10. [Concurrencia](#10-concurrencia)
11. [Otros modificadores técnicos](#11-otros-modificadores-técnicos)
12. [Literales reservados](#12-literales-reservados)
13. [Palabras contextuales — Java moderno](#13-palabras-contextuales--java-moderno)
14. [Palabras reservadas no implementadas](#14-palabras-reservadas-no-implementadas)
15. [Tabla resumen completa](#15-tabla-resumen-completa)

---

## 1. Tipos de datos primitivos

Son las palabras que definen los **8 tipos primitivos** de Java. Representan datos simples que viven en la pila (stack), no son objetos y no tienen métodos propios.

---

### `boolean`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 1 bit lógico (JVM puede usar más internamente) |
| **Desde** | Java 1.0 |
| **Valores posibles** | Solo `true` o `false` |

**Descripción:** declara una variable que solo puede tomar los valores `true` o `false`. Es el tipo fundamental para condiciones y lógica booleana.

```java
boolean esMayorDeEdad = true;
boolean tieneCuenta   = false;

// Uso en condiciones
if (esMayorDeEdad) {
    System.out.println("Puede ingresar");
}

// Resultado de comparaciones
boolean esMayor = (18 > 5);   // true
boolean esIgual = (10 == 20); // false
```

> ⚠️ No confundas el primitivo `boolean` con la clase envolvente `Boolean` (con mayúscula). El primitivo no puede ser `null`; la clase envolvente sí.

---

### `byte`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 8 bits (1 byte) |
| **Desde** | Java 1.0 |
| **Rango** | −128 a 127 |

**Descripción:** entero de rango muy pequeño. Se usa cuando se trabaja con datos binarios, flujos de bytes (archivos, redes) o cuando el ahorro de memoria es crítico.

```java
byte temperatura = 36;
byte nivel       = -10;

// Lectura de archivos: los streams de I/O trabajan con bytes
byte[] buffer = new byte[1024];
```

> 💡 Si el valor asignado supera 127 o es menor que -128, el compilador lanzará un error. Usa `int` o `short` si necesitas valores mayores.

---

### `short`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 16 bits (2 bytes) |
| **Desde** | Java 1.0 |
| **Rango** | −32 768 a 32 767 |

**Descripción:** entero de rango moderado. Más raro de ver en el día a día; se usa en protocolos de comunicación o hardware donde cada byte importa.

```java
short año  = 2024;
short port = 8080;
```

---

### `int`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 32 bits (4 bytes) |
| **Desde** | Java 1.0 |
| **Rango** | −2 147 483 648 a 2 147 483 647 |

**Descripción:** el tipo entero por defecto de Java. Es el que usarás en el 90 % de los casos para contar, indexar o representar números enteros.

```java
int edad       = 25;
int cantItems  = 1_000_000;  // guion bajo mejora legibilidad (Java 7+)
int resultado  = 10 + 5 * 2; // 20 (respeta precedencia de operadores)

// Literales en distintas bases
int decimal     = 255;
int hexadecimal = 0xFF;   // 255
int binario     = 0b1111_1111; // 255
int octal       = 0377;   // 255
```

---

### `long`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 64 bits (8 bytes) |
| **Desde** | Java 1.0 |
| **Rango** | −9.2 × 10¹⁸ a 9.2 × 10¹⁸ |
| **Sufijo literal** | `L` o `l` (se prefiere `L` mayúscula) |

**Descripción:** entero de 64 bits para valores que desbordan el rango de `int`. Muy común en timestamps, IDs de base de datos, tamaños de archivo y cálculos financieros.

```java
long poblacionMundial = 8_100_000_000L; // el L es OBLIGATORIO si supera int
long timestampMs      = System.currentTimeMillis();
long idUsuario        = 9_876_543_210L;
```

> ⚠️ Sin la `L` al final, el compilador intenta interpretar el número como `int` y puede fallar o truncarlo.

---

### `float`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 32 bits (4 bytes) |
| **Desde** | Java 1.0 |
| **Precisión** | ~7 dígitos decimales significativos |
| **Sufijo literal** | `f` o `F` |

**Descripción:** número de punto flotante de simple precisión (IEEE 754). Usa `double` en su lugar salvo que necesites reducir memoria (como arrays muy grandes de decimales).

```java
float pi     = 3.14f;      // la f es OBLIGATORIA
float precio = 9.99F;

// ⚠️ Errores de precisión — normal en punto flotante
float a = 0.1f + 0.2f;   // resultado: 0.3 (¡pero puede ser 0.30000001!)
```

---

### `double`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 64 bits (8 bytes) |
| **Desde** | Java 1.0 |
| **Precisión** | ~15 dígitos decimales significativos |

**Descripción:** número de punto flotante de doble precisión. Es el tipo decimal por defecto de Java; cualquier literal con punto decimal es `double` automáticamente.

```java
double pi      = 3.14159265358979;
double sueldo  = 850_000.50;  // sin sufijo, ya es double
double notacion = 1.5e10;     // notación científica: 15 000 000 000

// Para dinero, prefiere BigDecimal (evita errores de punto flotante)
// double no es adecuado para operaciones financieras críticas
```

---

### `char`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de dato primitivo |
| **Tamaño** | 16 bits (2 bytes) |
| **Desde** | Java 1.0 |
| **Rango** | Carácter Unicode `\u0000` a `\uFFFF` |

**Descripción:** almacena un único carácter Unicode entre comillas simples. Internamente es un entero sin signo, por lo que puede usarse en operaciones aritméticas.

```java
char letra    = 'A';
char numero   = '5';
char unicode  = '\u03A9';  // Ω (letra griega Omega)
char salto    = '\n';      // secuencia de escape: salto de línea

// char es un entero internamente
char siguiente = (char) ('A' + 1); // 'B'
int codigo     = 'Z';  // 90 (código ASCII/Unicode de 'Z')
```

---

## 2. Modificadores de acceso

Controlan la **visibilidad** de clases, atributos, métodos y constructores desde otras partes del código.

---

### `public`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de acceso |
| **Desde** | Java 1.0 |
| **Aplica a** | Clases, métodos, atributos, constructores |

**Descripción:** el elemento es visible desde **cualquier** parte del programa, incluyendo otros paquetes. Es el nivel de acceso más permisivo.

```java
public class Persona {           // visible desde cualquier paquete
    public String nombre;        // cualquiera puede leer/escribir directamente
    
    public void saludar() {      // cualquiera puede llamar este método
        System.out.println("Hola, soy " + nombre);
    }
}
```

---

### `private`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de acceso |
| **Desde** | Java 1.0 |
| **Aplica a** | Métodos, atributos, constructores (no clases externas) |

**Descripción:** el elemento es visible **solo dentro de la misma clase**. Es la base del encapsulamiento (pilar de la POO). Úsalo por defecto en todos los atributos.

```java
public class CuentaBancaria {
    private double saldo;        // solo esta clase puede tocarlo directamente
    private String numeroCuenta;

    public double getSaldo() {   // getter público expone el valor de forma controlada
        return saldo;
    }

    private void registrarLog(String msg) {  // método de uso interno
        System.out.println("[LOG] " + msg);
    }
}
```

---

### `protected`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de acceso |
| **Desde** | Java 1.0 |
| **Aplica a** | Métodos, atributos, constructores |

**Descripción:** visible dentro del **mismo paquete** y en **subclases** (incluso en otros paquetes). Es el nivel intermedio entre `private` y `public`, fundamental en herencia.

```java
public class Animal {
    protected String nombre;       // las subclases pueden acceder directamente
    
    protected void respirar() {    // las subclases pueden usar o sobreescribir esto
        System.out.println("Respirando...");
    }
}

public class Perro extends Animal {
    public void ladrar() {
        // puede acceder a 'nombre' porque es protected en la superclase
        System.out.println(nombre + " dice: ¡Guau!");
    }
}
```

---

### (sin modificador) — Package-private

> Esta no es una palabra reservada, pero es importante entenderla. Cuando **no escribes** ningún modificador de acceso, el elemento es visible solo dentro del **mismo paquete**.

```java
class Utilitario {          // sin public → solo visible en el mismo paquete
    void ayudar() { ... }   // ídem
}
```

---

## 3. Modificadores de comportamiento

Alteran el comportamiento de clases, métodos y variables.

---

### `static`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento |
| **Desde** | Java 1.0 |
| **Aplica a** | Atributos, métodos, bloques, clases anidadas |

**Descripción:** indica que el elemento **pertenece a la clase**, no a una instancia. Existe aunque nunca se haya creado un objeto de esa clase. Se accede directamente con el nombre de la clase.

```java
public class Matematica {
    // atributo estático: compartido por TODAS las instancias
    public static final double PI = 3.14159265358979;
    
    // método estático: se llama sin crear objeto
    public static int sumar(int a, int b) {
        return a + b;
    }
    
    // bloque estático: se ejecuta una sola vez cuando la clase carga
    static {
        System.out.println("Clase Matematica cargada");
    }
}

// Uso: no se necesita new Matematica()
int resultado = Matematica.sumar(3, 5);  // 8
double area   = Matematica.PI * radio * radio;
```

> ⚠️ Un método `static` NO puede acceder a atributos de instancia (no-static), porque no tiene referencia a ningún objeto.

---

### `final`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento |
| **Desde** | Java 1.0 |
| **Aplica a** | Variables, parámetros, métodos, clases |

**Descripción:** el significado varía según dónde se aplique:
- **Variable/parámetro `final`:** su valor no puede reasignarse después de la primera asignación (constante).
- **Método `final`:** no puede ser sobreescrito (*overridden*) por subclases.
- **Clase `final`:** no puede ser extendida (heredada). Ej: `String` es `final`.

```java
// Variable final (constante)
final int MAX_INTENTOS = 3;
MAX_INTENTOS = 5; // ❌ ERROR: no se puede reasignar

// Constante de clase: convención UPPER_SNAKE_CASE
public static final double IVA = 0.19;

// Método final: ninguna subclase puede sobreescribirlo
public final void calcularImpuesto() { ... }

// Clase final: no puede tener subclases
public final class Utilidades { ... }
```

> 💡 `final` en un objeto no hace inmutable el objeto; evita que la variable apunte a otro objeto distinto, pero el estado interno del objeto sí puede cambiar.

---

### `abstract`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento / OOP |
| **Desde** | Java 1.0 |
| **Aplica a** | Clases, métodos |

**Descripción:**
- **Clase `abstract`:** no puede instanciarse directamente. Sirve como plantilla base para subclases. Puede tener métodos implementados y métodos abstractos.
- **Método `abstract`:** declara la firma del método sin implementación. La subclase concreta **debe** implementarlo.

```java
// Clase abstracta: no puedes hacer new Figura()
public abstract class Figura {
    protected String color;
    
    // Método abstracto: sin cuerpo, las subclases DEBEN implementarlo
    public abstract double calcularArea();
    
    // Método concreto: implementación compartida para todas las subclases
    public void mostrarColor() {
        System.out.println("Color: " + color);
    }
}

public class Circulo extends Figura {
    private double radio;
    
    @Override
    public double calcularArea() {   // implementación obligatoria
        return Math.PI * radio * radio;
    }
}
```

---

### `synchronized`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento / Concurrencia |
| **Desde** | Java 1.0 |
| **Aplica a** | Métodos, bloques de código |

**Descripción:** garantiza que solo **un hilo (thread) a la vez** pueda ejecutar el bloque o método marcado. Evita condiciones de carrera (*race conditions*) en programación concurrente. Ver también [sección 10](#10-concurrencia).

```java
public class Contador {
    private int valor = 0;
    
    // Solo un thread a la vez puede ejecutar este método
    public synchronized void incrementar() {
        valor++;
    }
    
    // Bloque sincronizado (más granular que el método completo)
    public void incrementarParcial() {
        // código no sincronizado...
        synchronized (this) {
            valor++;
        }
        // más código no sincronizado...
    }
}
```

---

### `volatile`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento / Concurrencia |
| **Desde** | Java 1.0 |
| **Aplica a** | Atributos de instancia o estáticos |

**Descripción:** garantiza que los cambios en una variable sean **visibles inmediatamente** para todos los hilos. Sin `volatile`, cada hilo puede tener una copia local del valor en su caché, lo que provoca lecturas desactualizadas. Ver también [sección 10](#10-concurrencia).

```java
public class Servicio {
    // Sin volatile, otro hilo podría ver 'activo = true' aunque se haya cambiado a false
    private volatile boolean activo = true;
    
    public void detener() { activo = false; }
    public boolean estaActivo() { return activo; }
}
```

---

### `transient`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento / Serialización |
| **Desde** | Java 1.0 |
| **Aplica a** | Atributos de instancia |

**Descripción:** indica que un atributo **no debe incluirse** al serializar el objeto (guardar su estado en bytes para almacenamiento o transmisión). Útil para campos calculados, contraseñas o conexiones que no pueden o no deben persistirse.

```java
public class Usuario implements Serializable {
    private String nombre;
    private String email;
    
    // Esta contraseña NO se guarda cuando se serializa el objeto
    private transient String password;
    
    // Esta caché temporal tampoco se serializa
    private transient List<String> cachePermisos;
}
```

---

### `native`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento / JNI |
| **Desde** | Java 1.0 |
| **Aplica a** | Métodos |

**Descripción:** indica que el método está **implementado en código nativo** (C, C++) a través de la interfaz JNI (*Java Native Interface*). El cuerpo del método no se escribe en Java; la JVM delega la llamada a una biblioteca nativa (.dll, .so).

```java
public class SistemaOperativo {
    // Implementado en C/C++ en una biblioteca nativa
    public native long obtenerMemoriaLibre();
    
    static {
        System.loadLibrary("miLibreriaNativa"); // carga el archivo .so/.dll
    }
}
```

> 💡 En el día a día con Spring Boot raramente escribirás métodos `native`. Sin embargo, la JVM misma los usa internamente (ej: `Object.hashCode()` en ciertas JVMs).

---

### `strictfp`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Modificador de comportamiento |
| **Desde** | Java 1.2 |
| **Deprecado / cambiado** | Java 17 (ahora el comportamiento es siempre estricto) |
| **Aplica a** | Clases, interfaces, métodos |

**Descripción:** forzaba que los cálculos de punto flotante siguieran estrictamente el estándar IEEE 754, sin importar el hardware. A partir de **Java 17**, este comportamiento es el predeterminado para todos los cálculos, por lo que `strictfp` ya no tiene efecto práctico (aunque sigue siendo una palabra reservada válida).

```java
// Pre-Java 17: garantizaba resultados idénticos en cualquier plataforma
@Deprecated
public strictfp class CalculoCientifico {
    public strictfp double calcular(double a, double b) {
        return a * b + Math.sqrt(a);
    }
}
```

---

## 4. Estructura del programa

Palabras que definen la organización y el esqueleto del código Java.

---

### `package`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Estructura del programa |
| **Desde** | Java 1.0 |
| **Posición** | Primera línea del archivo (antes de imports) |

**Descripción:** declara a qué **paquete** pertenece el archivo. Los paquetes organizan las clases en namespaces jerárquicos separados por puntos, evitando colisiones de nombres y estructurando el proyecto.

```java
// Debe ser la primera línea del archivo (excepto comentarios)
package com.empresa.proyecto.servicio;

// Convención: dominio invertido + proyecto + módulo
// com.google.gson
// org.springframework.boot
// cl.duoc.dsy1103.model
```

> 💡 La estructura de paquetes debe coincidir exactamente con la estructura de carpetas del proyecto.

---

### `import`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Estructura del programa |
| **Desde** | Java 1.0 |
| **Variante** | `import static` (Java 5+) |

**Descripción:** permite usar clases de otros paquetes sin escribir el nombre completo (*fully qualified name*). El asterisco (`*`) importa todas las clases de un paquete, pero es mala práctica en código profesional porque hace ambiguo el origen de cada clase.

```java
package cl.duoc.app;

import java.util.ArrayList;        // importa solo ArrayList
import java.util.List;             // importa solo la interfaz List
import java.util.*;                // ⚠️ importa TODO (evitar en producción)

// Import estático: permite usar miembros estáticos sin el nombre de la clase
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class App {
    public static void main(String[] args) {
        List<String> lista = new ArrayList<>();  // sin import necesitaría java.util.ArrayList
        double hipotenusa = sqrt(3*3 + 4*4);     // sin import sería Math.sqrt(...)
        System.out.println("π = " + PI);         // sin import sería Math.PI
    }
}
```

---

### `void`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Tipo de retorno de método |
| **Desde** | Java 1.0 |
| **Aplica a** | Métodos |

**Descripción:** indica que un método **no retorna ningún valor**. Se usa como tipo de retorno en la firma del método. No se puede asignar ni almacenar.

```java
// void: el método hace algo pero no devuelve nada
public void imprimirMensaje(String msg) {
    System.out.println(msg);
    // no hay return (o puede haber un return; vacío para salir antes)
}

// Comparación con método que sí retorna
public int sumar(int a, int b) {
    return a + b;  // devuelve un int
}

// Punto de entrada siempre es void
public static void main(String[] args) { ... }
```

---

## 5. Control de flujo — Condicionales

---

### `if`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Condicional |
| **Desde** | Java 1.0 |

**Descripción:** ejecuta un bloque de código **solo si** la condición booleana entre paréntesis es `true`.

```java
int edad = 20;

if (edad >= 18) {
    System.out.println("Mayor de edad");
}
```

---

### `else`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Condicional |
| **Desde** | Java 1.0 |

**Descripción:** define el bloque que se ejecuta cuando la condición de `if` (o el último `else if`) es `false`. Siempre va después de un `if` o `else if`.

```java
int nota = 45;

if (nota >= 60) {
    System.out.println("Aprobado");
} else if (nota >= 40) {
    System.out.println("Examen de recuperación");
} else {
    System.out.println("Reprobado");
}
```

---

### `switch`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Condicional |
| **Desde** | Java 1.0 (clásico), Java 14 (switch expression estable) |
| **Aplica a** | `byte`, `short`, `int`, `char`, `String`, `enum` y sus wrappers |

**Descripción:** compara una expresión contra múltiples valores posibles. Existe en dos formas: el **switch clásico** (con `case`, `break` y posible *fall-through*) y el **switch expression** moderno (con `->`, más seguro y compacto).

```java
String dia = "LUNES";

// ── Switch CLÁSICO ────────────────────────────────────────────────
switch (dia) {
    case "LUNES":
    case "MARTES":
        System.out.println("Inicio de semana");
        break;            // sin break, "cae" al siguiente case
    case "VIERNES":
        System.out.println("Fin de semana laboral");
        break;
    default:
        System.out.println("Otro día");
}

// ── Switch EXPRESSION (Java 14+) ─────────────────────────────────
String tipo = switch (dia) {
    case "LUNES", "MARTES", "MIÉRCOLES" -> "Laborable";
    case "SÁBADO", "DOMINGO"           -> "Fin de semana";
    default                            -> "Otro";
};
```

---

### `case`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Condicional |
| **Desde** | Java 1.0 |

**Descripción:** define un valor o patrón a comparar dentro de un `switch`. Si la expresión del `switch` coincide con el valor del `case`, se ejecuta ese bloque.

```java
int opcion = 2;
switch (opcion) {
    case 1:
        System.out.println("Opción uno");
        break;
    case 2:
        System.out.println("Opción dos");  // se ejecuta este
        break;
    case 3:
        System.out.println("Opción tres");
        break;
}
```

---

### `default`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Condicional; también Modificador de interfaz |
| **Desde** | Java 1.0 (switch); Java 8 (métodos de interfaz) |

**Descripción (en switch):** el bloque `default` se ejecuta cuando ningún `case` coincide con el valor. Es opcional pero muy recomendable para manejar casos inesperados.

**Descripción (en interfaces):** permite definir una implementación concreta dentro de una interfaz (desde Java 8). Las clases que implementen la interfaz heredan esa implementación pero pueden sobreescribirla.

```java
// default en switch
switch (codigo) {
    case 200: System.out.println("OK"); break;
    case 404: System.out.println("No encontrado"); break;
    default:  System.out.println("Código desconocido");  // ← aquí
}

// default en interfaz (Java 8+)
public interface Saludo {
    default void saludar() {
        System.out.println("¡Hola desde la interfaz!");
    }
}
```

---

### `instanceof`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Operador OOP |
| **Desde** | Java 1.0 (operador clásico), Java 16 (pattern matching estable) |

**Descripción:** verifica si un objeto es instancia de un tipo dado. Retorna `boolean`. Desde Java 16, soporta **pattern matching**: si la condición es `true`, el objeto queda automáticamente casteado a la variable declarada en la misma línea.

```java
Object obj = "Hola Java";

// ── Forma clásica (Java 1.0) ──────────────────────────────────────
if (obj instanceof String) {
    String texto = (String) obj;   // cast manual
    System.out.println(texto.toUpperCase());
}

// ── Pattern matching (Java 16+) ──────────────────────────────────
if (obj instanceof String texto) { // 'texto' ya está casteado y en scope
    System.out.println(texto.toUpperCase()); // sin cast adicional
}

// Pattern matching en switch (Java 21)
String resultado = switch (obj) {
    case Integer i  -> "Entero: " + i;
    case String  s  -> "Texto: "  + s.toUpperCase();
    case null       -> "Es null";
    default         -> "Otro tipo";
};
```

---

## 6. Control de flujo — Bucles

---

### `for`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Bucle |
| **Desde** | Java 1.0 (for clásico), Java 5 (for-each) |

**Descripción:** repite un bloque de código un número determinado de veces. Existe en dos variantes: el **for clásico** (con inicialización, condición e incremento) y el **for-each** (para iterar colecciones o arrays).

```java
// ── For CLÁSICO ───────────────────────────────────────────────────
for (int i = 0; i < 5; i++) {
    System.out.println("Iteración: " + i);
}

// Múltiples variables
for (int i = 0, j = 10; i < j; i++, j--) {
    System.out.println(i + " - " + j);
}

// ── For-EACH (enhanced for) ───────────────────────────────────────
String[] colores = {"rojo", "verde", "azul"};
for (String color : colores) {      // "para cada color en colores"
    System.out.println(color);
}

List<Integer> numeros = List.of(1, 2, 3, 4, 5);
for (int num : numeros) {
    System.out.println(num * 2);
}
```

---

### `while`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Bucle |
| **Desde** | Java 1.0 |

**Descripción:** repite un bloque **mientras** la condición sea `true`. La condición se evalúa **antes** de cada iteración; si es `false` desde el inicio, el bloque nunca se ejecuta.

```java
int intentos = 0;
int max = 3;

while (intentos < max) {
    System.out.println("Intento " + (intentos + 1));
    intentos++;
}

// Bucle infinito controlado por break
while (true) {
    String entrada = leerEntrada();
    if (entrada.equals("salir")) break;
    procesarEntrada(entrada);
}
```

---

### `do`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Bucle |
| **Desde** | Java 1.0 |
| **Siempre acompañado de** | `while` |

**Descripción:** el bucle `do-while` ejecuta el bloque **al menos una vez** y luego verifica la condición. A diferencia de `while`, la condición se evalúa **al final** de cada iteración.

```java
int numero;

do {
    System.out.print("Ingresa un número entre 1 y 10: ");
    numero = leerNumero();
} while (numero < 1 || numero > 10);  // repite si el número no es válido

System.out.println("Número válido: " + numero);
```

> 💡 Usa `do-while` cuando el cuerpo del bucle **debe ejecutarse al menos una vez** (validaciones de entrada, menús interactivos).

---

### `break`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Salto |
| **Desde** | Java 1.0 |
| **Aplica a** | Bucles (`for`, `while`, `do-while`) y `switch` |

**Descripción:** termina inmediatamente la ejecución del bucle o `switch` más cercano y transfiere el control a la línea siguiente al bloque.

```java
// Salir del bucle al encontrar el elemento
for (int i = 0; i < 100; i++) {
    if (i == 5) {
        break;  // sale del for cuando i llega a 5
    }
    System.out.println(i);
}

// break con etiqueta (labeled break): sale de un bucle externo
externo:
for (int i = 0; i < 5; i++) {
    for (int j = 0; j < 5; j++) {
        if (i == 2 && j == 3) {
            break externo;  // sale del bucle 'externo' completo
        }
    }
}
```

---

### `continue`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Salto |
| **Desde** | Java 1.0 |
| **Aplica a** | Bucles (`for`, `while`, `do-while`) |

**Descripción:** salta el **resto de la iteración actual** y pasa directamente a la siguiente. No sale del bucle (a diferencia de `break`).

```java
// Imprimir solo números pares
for (int i = 0; i < 10; i++) {
    if (i % 2 != 0) {
        continue;  // salta los impares, va directo al próximo i++
    }
    System.out.println(i);  // solo se ejecuta para 0, 2, 4, 6, 8
}

// continue con etiqueta (labeled continue)
externo:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) continue externo; // salta al próximo i
        System.out.println(i + "," + j);
    }
}
```

---

## 7. Control de flujo — Retorno y salto

---

### `return`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Control de flujo / Retorno |
| **Desde** | Java 1.0 |
| **Aplica a** | Métodos |

**Descripción:** termina la ejecución del método actual y (opcionalmente) devuelve un valor al llamador. En métodos `void`, se puede usar `return;` solo para salir antes del final.

```java
// Método que retorna un valor
public int maximo(int a, int b) {
    if (a > b) return a;  // return temprano
    return b;
}

// Método void: return sin valor para salir antes
public void validar(String nombre) {
    if (nombre == null || nombre.isBlank()) {
        System.out.println("Nombre inválido");
        return;  // sale del método sin ejecutar más código
    }
    System.out.println("Nombre válido: " + nombre);
}
```

---

## 8. Orientación a objetos

---

### `class`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Declaración |
| **Desde** | Java 1.0 |

**Descripción:** declara una **clase**, que es la plantilla para crear objetos. Una clase define atributos (estado) y métodos (comportamiento).

```java
public class Producto {
    // Atributos (estado del objeto)
    private String nombre;
    private double precio;
    private int stock;
    
    // Constructor
    public Producto(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock  = 0;
    }
    
    // Métodos (comportamiento)
    public void agregarStock(int cantidad) {
        this.stock += cantidad;
    }
    
    public double getPrecioConIva() {
        return precio * 1.19;
    }
}
```

---

### `interface`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Declaración |
| **Desde** | Java 1.0 |

**Descripción:** declara un **contrato** que las clases pueden implementar. Define qué métodos deben existir sin especificar cómo. Desde Java 8, puede tener métodos `default` y `static`; desde Java 9, métodos `private`.

```java
// Interfaz: define el QUÉ, no el CÓMO
public interface Pagable {
    void procesarPago(double monto);        // método abstracto (implícitamente public abstract)
    boolean verificarSaldo(double monto);   // abstracto
    
    // Método default (Java 8+): implementación opcional para las clases
    default String obtenerDivisa() {
        return "CLP";
    }
    
    // Método estático en interfaz (Java 8+)
    static Pagable crear(String tipo) {
        return tipo.equals("debito") ? new TarjetaDebito() : new TarjetaCredito();
    }
}

// La clase debe implementar todos los métodos abstractos
public class TarjetaCredito implements Pagable {
    @Override
    public void procesarPago(double monto) { ... }
    @Override
    public boolean verificarSaldo(double monto) { ... }
}
```

---

### `extends`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Herencia |
| **Desde** | Java 1.0 |
| **Aplica a** | Clases (herencia), interfaces (extensión de interfaz) |

**Descripción:** establece una relación de **herencia** entre clases ("es-un") o permite que una interfaz extienda otra interfaz. Java solo permite herencia simple entre clases (una clase solo puede extender a una sola superclase).

```java
// Herencia de clase
public class Animal {
    protected String nombre;
    public void comer() { System.out.println("Comiendo..."); }
}

public class Perro extends Animal {        // Perro "es-un" Animal
    public void ladrar() {
        System.out.println(nombre + " dice: ¡Guau!");
    }
}

// Una interfaz puede extender múltiples interfaces
public interface InterfazAvanzada extends InterfazA, InterfazB { ... }
```

---

### `implements`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Contratos |
| **Desde** | Java 1.0 |
| **Aplica a** | Clases |

**Descripción:** indica que una clase **implementa uno o más interfaces**, comprometiéndose a implementar todos sus métodos abstractos. Una clase puede implementar múltiples interfaces (esto resuelve la limitación de herencia simple).

```java
public class Empleado extends Persona implements Pagable, Serializable, Comparable<Empleado> {
    // Debe implementar los métodos abstractos de Pagable y Comparable
    @Override
    public void procesarPago(double monto) { ... }
    
    @Override
    public boolean verificarSaldo(double monto) { ... }
    
    @Override
    public int compareTo(Empleado otro) {
        return this.nombre.compareTo(otro.nombre);
    }
}
```

---

### `new`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Creación de objetos |
| **Desde** | Java 1.0 |

**Descripción:** crea una **nueva instancia** de una clase o un array. Reserva memoria en el heap, inicializa el objeto y llama al constructor.

```java
// Crear instancia de una clase
Producto p = new Producto("Laptop", 850_000.0);

// Crear arrays
int[] numeros    = new int[10];          // array de 10 enteros inicializados en 0
String[] nombres = new String[5];        // array de 5 Strings (null por defecto)

// Crear instancias anónimas (sin asignar a variable)
System.out.println(new Producto("Mouse", 15_000.0).getPrecioConIva());

// Clases anónimas (implementación sobre la marcha)
Comparator<String> comp = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.length() - b.length();
    }
};
```

---

### `this`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Referencia |
| **Desde** | Java 1.0 |
| **Aplica a** | Instancias de clase (no en contexto `static`) |

**Descripción:** referencia a la **instancia actual** del objeto. Se usa para distinguir atributos de la clase de parámetros con el mismo nombre, para llamar a otro constructor de la misma clase (`this(...)`) o para pasar el objeto actual como argumento.

```java
public class Persona {
    private String nombre;
    private int edad;
    
    // this.nombre = atributo; nombre = parámetro del constructor
    public Persona(String nombre, int edad) {
        this.nombre = nombre;  // resuelve la ambigüedad
        this.edad   = edad;
    }
    
    // Llamada a otro constructor (debe ser la primera línea)
    public Persona(String nombre) {
        this(nombre, 0);  // llama al constructor de arriba
    }
    
    public Persona obtenerReferencia() {
        return this;  // retorna el objeto actual
    }
}
```

---

### `super`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Herencia |
| **Desde** | Java 1.0 |
| **Aplica a** | Clases que extienden otra clase |

**Descripción:** referencia a la **superclase** (clase padre). Se usa para llamar al constructor del padre (`super(...)`), acceder a sus métodos o atributos cuando la subclase los sobreescribe.

```java
public class Animal {
    protected String nombre;
    public Animal(String nombre) {
        this.nombre = nombre;
    }
    public String describir() {
        return "Animal: " + nombre;
    }
}

public class Perro extends Animal {
    private String raza;
    
    public Perro(String nombre, String raza) {
        super(nombre);  // DEBE ser la primera línea; llama al constructor de Animal
        this.raza = raza;
    }
    
    @Override
    public String describir() {
        return super.describir() + ", Raza: " + raza;  // reutiliza la versión del padre
    }
}
```

---

### `enum`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | OOP / Tipos enumerados |
| **Desde** | Java 5 |

**Descripción:** define un tipo especial cuyas instancias son un **conjunto fijo y conocido de constantes**. Es una forma segura de representar valores categóricos (estados, días, roles, etc.). Un `enum` es implícitamente `public static final` y extiende `java.lang.Enum`.

```java
// Enum simple
public enum Estado {
    ACTIVO, INACTIVO, SUSPENDIDO
}

// Enum con atributos y métodos
public enum DiaSemana {
    LUNES("Monday", false),
    MARTES("Tuesday", false),
    SABADO("Saturday", true),
    DOMINGO("Sunday", true);
    
    private final String nombreIngles;
    private final boolean esFinDeSemana;
    
    DiaSemana(String nombreIngles, boolean esFinDeSemana) {
        this.nombreIngles   = nombreIngles;
        this.esFinDeSemana  = esFinDeSemana;
    }
    
    public boolean esFinDeSemana() { return esFinDeSemana; }
    public String getNombreIngles() { return nombreIngles; }
}

// Uso
Estado estado = Estado.ACTIVO;
if (estado == Estado.ACTIVO) { ... }

DiaSemana hoy = DiaSemana.SABADO;
System.out.println(hoy.esFinDeSemana()); // true
```

---

## 9. Manejo de excepciones

---

### `try`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Manejo de excepciones |
| **Desde** | Java 1.0 (básico), Java 7 (try-with-resources) |

**Descripción:** delimita el bloque de código que podría lanzar una excepción. Siempre va acompañado de `catch`, `finally` o ambos. En Java 7+ puede declarar recursos en el paréntesis (`try-with-resources`) que se cierran automáticamente.

```java
// try básico
try {
    int resultado = 10 / 0;  // lanza ArithmeticException
    System.out.println(resultado);
} catch (ArithmeticException e) {
    System.out.println("Error: " + e.getMessage());
}

// try-with-resources (Java 7+): cierra el recurso automáticamente
try (FileReader fr = new FileReader("datos.txt");
     BufferedReader br = new BufferedReader(fr)) {
    String linea = br.readLine();
    System.out.println(linea);
} catch (IOException e) {
    System.out.println("Error de I/O: " + e.getMessage());
}
// br y fr se cierran automáticamente al salir del bloque (incluso si hay excepción)
```

---

### `catch`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Manejo de excepciones |
| **Desde** | Java 1.0 (básico), Java 7 (multi-catch) |

**Descripción:** captura y maneja una excepción específica lanzada en el bloque `try`. Puede haber múltiples `catch` para distintos tipos de excepción. El orden importa: coloca las más específicas primero.

```java
try {
    String texto = null;
    System.out.println(texto.length()); // NullPointerException

} catch (NullPointerException e) {
    // captura específica primero
    System.out.println("Referencia nula: " + e.getMessage());

} catch (IllegalArgumentException | IllegalStateException e) {
    // multi-catch (Java 7+): un bloque para varios tipos relacionados
    System.out.println("Argumento o estado inválido");

} catch (Exception e) {
    // captura genérica al final (atrapa todo lo que no capturaron los anteriores)
    System.out.println("Error inesperado: " + e.getClass().getSimpleName());
}
```

---

### `finally`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Manejo de excepciones |
| **Desde** | Java 1.0 |

**Descripción:** el bloque `finally` se ejecuta **siempre**, sin importar si hubo excepción o no, e incluso si hay un `return` en el bloque `try` o `catch`. Se usaba para liberar recursos (hoy reemplazado por `try-with-resources`).

```java
Connection conn = null;
try {
    conn = obtenerConexion();
    // operaciones con la BD...
    return conn.query("SELECT ...");

} catch (SQLException e) {
    System.out.println("Error SQL: " + e.getMessage());
    return null;

} finally {
    // Se ejecuta SIEMPRE, incluso después del return
    if (conn != null) {
        conn.close();  // libera la conexión pase lo que pase
    }
    System.out.println("Bloque finally ejecutado");
}
```

---

### `throw`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Manejo de excepciones |
| **Desde** | Java 1.0 |

**Descripción:** **lanza** una excepción manualmente. Interrumpe el flujo normal del método y propaga la excepción hacia arriba en la pila de llamadas hasta que sea capturada por un `catch`.

```java
public void depositar(double monto) {
    if (monto <= 0) {
        // lanzamos explícitamente una excepción
        throw new IllegalArgumentException("El monto debe ser mayor a cero. Recibido: " + monto);
    }
    if (monto > 1_000_000) {
        throw new IllegalArgumentException("El monto excede el límite permitido");
    }
    this.saldo += monto;
}

// Relanzar una excepción capturada
try {
    operacionRiesgosa();
} catch (IOException e) {
    System.out.println("Registrando en log...");
    throw e;  // relanza la misma excepción
}
```

---

### `throws`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Manejo de excepciones |
| **Desde** | Java 1.0 |
| **Aplica a** | Firma de métodos y constructores |

**Descripción:** declara en la firma del método que **puede lanzar** una o más excepciones **comprobadas** (*checked exceptions*). Le avisa al compilador y a quienes llamen al método que deben manejar o propagar esa excepción.

```java
// El método puede lanzar IOException y SQLException
public List<Usuario> cargarDesdeArchivo(String ruta)
        throws IOException, SQLException {

    // si algo sale mal, la excepción sube al llamador
    List<Usuario> usuarios = new ArrayList<>();
    // ... lectura de archivo
    return usuarios;
}

// Quien llame al método DEBE manejarlo:
try {
    List<Usuario> lista = cargarDesdeArchivo("datos.csv");
} catch (IOException | SQLException e) {
    System.out.println("Error al cargar: " + e.getMessage());
}
```

> 💡 `throw` **lanza** la excepción; `throws` **declara** que el método podría lanzarla.

---

### `assert`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Depuración / Verificación |
| **Desde** | Java 1.4 |

**Descripción:** evalúa una condición en tiempo de desarrollo. Si la condición es `false`, lanza `AssertionError`. Las assertions están **desactivadas por defecto** en producción; se habilitan con la JVM flag `-ea` (enable assertions). No deben usarse para validación de datos en producción; para eso usa `if + throw`.

```java
public double calcularRaiz(double numero) {
    assert numero >= 0 : "El número no puede ser negativo: " + numero;
    // Si numero < 0 y assertions están habilitadas (-ea), lanza AssertionError
    return Math.sqrt(numero);
}

// Activar assertions al ejecutar:
// java -ea MiPrograma
```

---

## 10. Concurrencia

---

### `synchronized` *(ver también sección 3)*

Repetimos la clave de concurrencia: garantiza que un bloque o método sea ejecutado por **un solo hilo a la vez**, usando el monitor del objeto como cerrojo.

---

### `volatile` *(ver también sección 3)*

Garantiza la **visibilidad entre hilos** de una variable, forzando lecturas y escrituras directamente en la memoria principal (no en la caché del hilo).

```java
// Patrón clásico: flag de parada compartida entre hilos
public class Tarea implements Runnable {
    private volatile boolean detener = false;
    
    public void detener() { this.detener = true; }
    
    @Override
    public void run() {
        while (!detener) {   // cada hilo lee el valor actualizado gracias a volatile
            realizarTrabajo();
        }
    }
}
```

> ⚠️ `volatile` no garantiza **atomicidad** de operaciones compuestas (como `i++`). Para eso usa `AtomicInteger` o `synchronized`.

---

## 11. Otros modificadores técnicos

*(ver también `native`, `transient`, `strictfp` en sección 3)*

---

## 12. Literales reservados

Estas tres palabras son **literales** del lenguaje: no son palabras clave técnicas, pero están completamente reservadas y no pueden usarse como identificadores.

---

### `true`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Literal booleano |
| **Desde** | Java 1.0 |
| **Tipo** | `boolean` |

**Descripción:** representa el valor verdadero del tipo `boolean`. No es una constante ni una variable; es parte de la sintaxis del lenguaje.

```java
boolean activo = true;
boolean condicion = (5 > 3);  // también es true, calculado en tiempo de ejecución

if (true) {  // siempre entra (el compilador puede optimizarlo o advertirte)
    System.out.println("Siempre se ejecuta");
}
```

---

### `false`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Literal booleano |
| **Desde** | Java 1.0 |
| **Tipo** | `boolean` |

**Descripción:** representa el valor falso del tipo `boolean`.

```java
boolean conectado = false;
boolean esMenor   = (10 > 20);  // false

while (false) {  // nunca entra (código muerto; el compilador puede advertirte)
    System.out.println("Nunca se ejecuta");
}
```

---

### `null`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Literal de referencia nula |
| **Desde** | Java 1.0 |

**Descripción:** representa la **ausencia de referencia** a un objeto. Cualquier variable de tipo referencia (objetos, arrays, interfaces) puede ser `null`. Acceder a un método o atributo de una variable `null` lanza `NullPointerException` (NPE).

```java
String nombre = null;       // variable de referencia sin objeto
Object obj    = null;

// Verificación defensiva
if (nombre != null) {
    System.out.println(nombre.length());
}

// Alternativa moderna con Optional (Java 8+) para evitar NPE
Optional<String> nombreOpt = Optional.ofNullable(nombre);
nombreOpt.ifPresent(n -> System.out.println(n.length()));

// null en switch (Java 21+)
switch (nombre) {
    case null   -> System.out.println("Es null");
    case String s -> System.out.println("Longitud: " + s.length());
}
```

> ⚠️ `null` es la fuente del error más común en Java. En código moderno, prefiere `Optional<T>` para representar la posible ausencia de un valor.

---

## 13. Palabras contextuales — Java moderno

Las **palabras contextuales** (*context-sensitive keywords* o *restricted identifiers*) son especiales: **solo tienen significado reservado en ciertos contextos**. En el resto, pueden usarse como identificadores normales (aunque no es recomendable hacerlo).

---

### `var`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Palabra contextual / Inferencia de tipo |
| **Desde** | Java 10 |
| **Aplica a** | Variables locales (no atributos de clase, parámetros ni retornos) |

**Descripción:** permite que el compilador **infiera automáticamente el tipo** de una variable local en base al valor asignado. No es tipado dinámico; el tipo se determina en tiempo de compilación y no cambia.

```java
// El compilador infiere el tipo de la derecha
var mensaje   = "Hola, Java";       // String
var contador  = 0;                   // int
var lista     = new ArrayList<String>(); // ArrayList<String>
var precios   = Map.of("A", 1.5, "B", 2.0); // Map<String, Double>

// Especialmente útil con tipos genéricos verbosos
var usuarios = new HashMap<String, List<Usuario>>(); // mucho más limpio

// var en for-each
for (var producto : listaProductos) {
    System.out.println(producto.getNombre());
}

// ❌ No se puede usar en:
// var campo = "valor";      // atributo de clase: ERROR
// public var metodo() {}    // tipo de retorno: ERROR
// void metodo(var x) {}    // parámetro: ERROR
// var x;                    // sin inicialización: ERROR
```

---

### `record`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Palabra contextual / Declaración de tipo |
| **Desde** | Java 16 (estable) |
| **Genera automáticamente** | Constructor canónico, getters, `equals()`, `hashCode()`, `toString()` |

**Descripción:** declara una clase de datos inmutable y concisa. Ideal para DTOs, value objects y cualquier estructura cuyo propósito es solo transportar datos. El compilador genera automáticamente todo el código repetitivo (*boilerplate*).

```java
// Declaración: Java genera constructor, getters, equals, hashCode, toString
public record Punto(double x, double y) {}

// Uso
Punto p = new Punto(3.0, 4.0);
System.out.println(p.x());   // getter: p.x() (no getX())
System.out.println(p.y());
System.out.println(p);       // toString: Punto[x=3.0, y=4.0]

// Record con validación en el constructor compacto
public record Temperatura(double celsius) {
    // Constructor compacto: se ejecuta antes del constructor canónico
    public Temperatura {
        if (celsius < -273.15) {
            throw new IllegalArgumentException("Por debajo del cero absoluto");
        }
    }
    
    // Métodos calculados
    public double fahrenheit() {
        return celsius * 9.0 / 5.0 + 32;
    }
}

// Record como DTO en Spring Boot
public record UsuarioDTO(Long id, String nombre, String email) {}
```

---

### `sealed`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Palabra contextual / Modificador de herencia |
| **Desde** | Java 17 (estable) |
| **Aplica a** | Clases e interfaces |
| **Siempre acompañado de** | `permits` |

**Descripción:** restringe qué clases pueden extender o implementar un tipo. Las subclases deben ser `final`, `sealed` o `non-sealed`. Permite modelar jerarquías cerradas y seguras, muy útil con pattern matching.

```java
// Solo las clases indicadas en 'permits' pueden extender Forma
public sealed class Forma permits Circulo, Rectangulo, Triangulo {}

public final class Circulo    extends Forma { double radio; }
public final class Rectangulo extends Forma { double ancho, alto; }
public sealed class Triangulo extends Forma permits TrianguloIsosceles {}

// Uso con switch pattern matching (Java 21)
double area = switch (forma) {
    case Circulo c      -> Math.PI * c.radio * c.radio;
    case Rectangulo r   -> r.ancho * r.alto;
    case Triangulo t    -> calcularAreaTriangulo(t);
};
// El compilador verifica que todos los casos estén cubiertos (exhaustividad)
```

---

### `permits`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Palabra contextual / Modificador de herencia |
| **Desde** | Java 17 (estable) |
| **Aplica a** | Clases e interfaces `sealed` |

**Descripción:** lista las clases o interfaces que tienen **permiso para extender** una clase `sealed`. Solo tiene significado junto a `sealed`.

```java
public sealed interface Resultado permits Exito, Fallo, Pendiente {}

public record Exito(Object dato) implements Resultado {}
public record Fallo(String error) implements Resultado {}
public final class Pendiente implements Resultado {}
```

---

### `yield`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Palabra contextual / Control de flujo |
| **Desde** | Java 14 (estable) |
| **Aplica a** | Bloques de `switch` expression |

**Descripción:** en un **switch expression** con bloques `{}`, `yield` retorna el valor del bloque hacia la expresión switch. Es el equivalente del `return` dentro de un bloque switch expression.

```java
// Con flecha (->) no se necesita yield
int resultado = switch (dia) {
    case "LUNES"    -> 1;
    case "MARTES"   -> 2;
    default         -> 0;
};

// Con bloque {} SÍ se necesita yield
int resultado2 = switch (dia) {
    case "LUNES" -> {
        System.out.println("Es lunes");
        yield 1;        // ← yield retorna el valor del bloque
    }
    case "MARTES" -> {
        System.out.println("Es martes");
        yield 2;
    }
    default -> {
        System.out.println("Otro día");
        yield 0;
    }
};
```

---

### `non-sealed`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Palabra contextual / Modificador de herencia |
| **Desde** | Java 17 (estable) |
| **Aplica a** | Subclases directas de una clase `sealed` |

**Descripción:** marca una subclase de una clase `sealed` como **abierta para extensión** sin restricciones. Rompe intencionalmente el sellado en esa rama de la jerarquía.

```java
public sealed class Vehiculo permits Auto, Camion, VehiculoCustom {}

public final  class Auto           extends Vehiculo {}  // no puede extenderse
public sealed class Camion         extends Vehiculo permits CamionPesado {} // sigue sellado
public non-sealed class VehiculoCustom extends Vehiculo {} // cualquiera puede extender esto
```

---

### `when` *(Java 21)*

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Palabra contextual / Guardas en pattern matching |
| **Desde** | Java 21 (estable) |
| **Aplica a** | Casos de `switch` con pattern matching |

**Descripción:** añade una **condición adicional** (*guard*) a un `case` en un switch con pattern matching. Si el patrón coincide Y la condición `when` es `true`, se ejecuta ese caso.

```java
Object obj = 42;

String descripcion = switch (obj) {
    case Integer i when i < 0   -> "Entero negativo";
    case Integer i when i == 0  -> "Cero";
    case Integer i when i > 100 -> "Entero grande";
    case Integer i              -> "Entero positivo: " + i;
    case String  s              -> "Texto: " + s;
    default                     -> "Otro tipo";
};
```

---

## 14. Palabras reservadas no implementadas

Estas palabras están reservadas por la especificación de Java pero **nunca han tenido funcionalidad**. El compilador las rechaza si intentas usarlas como identificador.

---

### `goto`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Reservada / No implementada |
| **Desde** | Java 1.0 |
| **Funcional** | ❌ No |

**Descripción:** reservada para compatibilidad y por razones históricas (existe en C/C++). Java nunca la implementó porque `goto` genera código difícil de entender y mantener (*spaghetti code*). Java ofrece alternativas estructuradas: `break` con etiqueta, `continue` con etiqueta, `return`.

```java
// goto miEtiqueta;  // ❌ ERROR DE COMPILACIÓN siempre
```

---

### `const`

| Atributo | Detalle |
|----------|---------|
| **Categoría** | Reservada / No implementada |
| **Desde** | Java 1.0 |
| **Funcional** | ❌ No |

**Descripción:** reservada para evitar confusión con C/C++, donde `const` declara constantes. En Java, las constantes se declaran con `static final`. Nunca fue implementada.

```java
// const int MAXIMO = 100;  // ❌ ERROR DE COMPILACIÓN siempre

// La forma correcta en Java:
public static final int MAXIMO = 100;  // ✅
```

---

## 15. Tabla resumen completa

| Palabra | Categoría | Desde | ¿Aún útil? |
|---------|-----------|-------|------------|
| `abstract` | OOP / Modificador | 1.0 | ✅ |
| `assert` | Depuración | 1.4 | ⚠️ Solo en desarrollo |
| `boolean` | Tipo primitivo | 1.0 | ✅ |
| `break` | Control de flujo | 1.0 | ✅ |
| `byte` | Tipo primitivo | 1.0 | ✅ |
| `case` | Control de flujo | 1.0 | ✅ |
| `catch` | Excepciones | 1.0 | ✅ |
| `char` | Tipo primitivo | 1.0 | ✅ |
| `class` | OOP | 1.0 | ✅ |
| `const` | Reservada sin uso | 1.0 | ❌ |
| `continue` | Control de flujo | 1.0 | ✅ |
| `default` | Control de flujo / Interfaz | 1.0 / 8 | ✅ |
| `do` | Bucle | 1.0 | ✅ |
| `double` | Tipo primitivo | 1.0 | ✅ |
| `else` | Control de flujo | 1.0 | ✅ |
| `enum` | OOP / Tipos enumerados | 5 | ✅ |
| `extends` | OOP / Herencia | 1.0 | ✅ |
| `false` | Literal booleano | 1.0 | ✅ |
| `final` | Modificador | 1.0 | ✅ |
| `finally` | Excepciones | 1.0 | ✅ |
| `float` | Tipo primitivo | 1.0 | ✅ |
| `for` | Bucle | 1.0 | ✅ |
| `goto` | Reservada sin uso | 1.0 | ❌ |
| `if` | Control de flujo | 1.0 | ✅ |
| `implements` | OOP / Contratos | 1.0 | ✅ |
| `import` | Estructura | 1.0 | ✅ |
| `instanceof` | OOP / Operador | 1.0 | ✅ |
| `int` | Tipo primitivo | 1.0 | ✅ |
| `interface` | OOP | 1.0 | ✅ |
| `long` | Tipo primitivo | 1.0 | ✅ |
| `native` | Modificador / JNI | 1.0 | ⚠️ Casos muy específicos |
| `new` | OOP / Creación | 1.0 | ✅ |
| `non-sealed` | OOP / Herencia sellada | 17 | ✅ |
| `null` | Literal de referencia nula | 1.0 | ✅ |
| `package` | Estructura | 1.0 | ✅ |
| `permits` | OOP / Herencia sellada | 17 | ✅ |
| `private` | Modificador de acceso | 1.0 | ✅ |
| `protected` | Modificador de acceso | 1.0 | ✅ |
| `public` | Modificador de acceso | 1.0 | ✅ |
| `record` | OOP / Datos inmutables | 16 | ✅ |
| `return` | Control de flujo | 1.0 | ✅ |
| `sealed` | OOP / Herencia sellada | 17 | ✅ |
| `short` | Tipo primitivo | 1.0 | ✅ |
| `static` | Modificador | 1.0 | ✅ |
| `strictfp` | Modificador (obsoleto) | 1.2 | ⚠️ Sin efecto desde Java 17 |
| `super` | OOP / Herencia | 1.0 | ✅ |
| `switch` | Control de flujo | 1.0 | ✅ |
| `synchronized` | Concurrencia | 1.0 | ✅ |
| `this` | OOP / Referencia | 1.0 | ✅ |
| `throw` | Excepciones | 1.0 | ✅ |
| `throws` | Excepciones | 1.0 | ✅ |
| `transient` | Serialización | 1.0 | ✅ |
| `true` | Literal booleano | 1.0 | ✅ |
| `try` | Excepciones | 1.0 | ✅ |
| `var` | Inferencia de tipo | 10 | ✅ |
| `void` | Tipo de retorno | 1.0 | ✅ |
| `volatile` | Concurrencia | 1.0 | ✅ |
| `when` | Pattern matching / Guardas | 21 | ✅ |
| `while` | Bucle | 1.0 | ✅ |
| `yield` | Switch expression | 14 | ✅ |

---

## Reglas de oro para recordar

```
┌─────────────────────────────────────────────────────────────────┐
│  1. Ninguna palabra reservada puede ser nombre de variable,     │
│     método, clase o paquete.                                    │
│                                                                 │
│  2. Java es case-sensitive: 'int' es reservada, 'Int' no lo es │
│     (aunque usar 'Int' como nombre es mala práctica).           │
│                                                                 │
│  3. Las palabras contextuales (var, record, sealed, etc.)       │
│     técnicamente SÍ pueden usarse como identificadores, pero    │
│     hacerlo confunde a los lectores y a los IDEs. Evítalo.      │
│                                                                 │
│  4. goto y const son reservadas pero no hacen nada.             │
│     Si ves que alguien las "usa", está usando un identificador  │
│     con ese nombre en otro lenguaje, no en Java.                │
│                                                                 │
│  5. true, false y null son literales, no palabras clave         │
│     técnicas, pero están igual de reservadas.                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Referencias

- [Java Language Specification (JLS) — Java 21 — Capítulo 3.9: Keywords](https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.9)
- [OpenJDK Feature List](https://openjdk.org/projects/jdk/)
- [JEP 395 — Records](https://openjdk.org/jeps/395)
- [JEP 409 — Sealed Classes](https://openjdk.org/jeps/409)
- [JEP 441 — Pattern Matching for switch](https://openjdk.org/jeps/441)

