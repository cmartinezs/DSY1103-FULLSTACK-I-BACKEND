# 🧠 Lógica Proposicional

## ¿Qué es la lógica proposicional?

La **lógica proposicional** (también llamada *lógica de enunciados*) es la rama de la lógica matemática que estudia las relaciones entre **proposiciones** y las leyes que las rigen. Es la base del razonamiento formal, de los circuitos digitales y —muy directamente— de las **condiciones y validaciones** en cualquier lenguaje de programación.

> 📌 Cada vez que escribes un `if`, un `while` o una expresión booleana en Java, estás aplicando lógica proposicional.

---

## Conceptos básicos

### ¿Qué es una proposición?

Una **proposición** es un enunciado declarativo que puede ser **verdadero (V / true)** o **falso (F / false)**, pero no ambos al mismo tiempo.

| Enunciado | ¿Es proposición? | Valor |
|-----------|-----------------|-------|
| "Java es un lenguaje de programación" | ✅ Sí | **V** |
| "2 + 2 = 5" | ✅ Sí | **F** |
| "¿Cuánto vale x?" | ❌ No (pregunta) | — |
| "Cierra la puerta" | ❌ No (orden) | — |
| "x > 10" | ⚠️ Sí, si `x` tiene valor | Depende |

Las proposiciones se representan con letras minúsculas: **p**, **q**, **r**, **s**, …

---

## Conectivos lógicos

Los **conectivos lógicos** permiten combinar proposiciones simples para formar **proposiciones compuestas**.

| Símbolo | Nombre | En Java / programación |
|---------|--------|------------------------|
| `¬` / `~` | Negación | `!` |
| `∧` | Conjunción (Y) | `&&` |
| `∨` | Disyunción (O) | `\|\|` |
| `→` | Condicional (Si…entonces) | `if (p) { q }` |
| `↔` | Bicondicional (Si y solo si) | `p == q` (booleans) |

---

## 1. Negación (`¬p`)

Invierte el valor de verdad de una proposición.

| p | ¬p |
|---|----|
| V | F  |
| F | V  |

```java
boolean esMayor = edad >= 18;
boolean esNenor = !esMayor;  // negación
```

---

## 2. Conjunción (`p ∧ q`)

Es verdadera **solo cuando ambas** proposiciones son verdaderas.

| p | q | p ∧ q |
|---|---|-------|
| V | V | **V** |
| V | F | F |
| F | V | F |
| F | F | F |

```java
// p: el usuario está activo
// q: el usuario tiene el rol ADMIN
boolean puedeAdministrar = usuario.isActivo() && usuario.getRol().equals("ADMIN");
```

---

## 3. Disyunción (`p ∨ q`)

Es falsa **solo cuando ambas** proposiciones son falsas.

| p | q | p ∨ q |
|---|---|-------|
| V | V | V |
| V | F | V |
| F | V | V |
| F | F | **F** |

```java
// p: el ticket está en estado ABIERTO
// q: el ticket está en estado EN_PROCESO
boolean estaActivo = estado.equals("ABIERTO") || estado.equals("EN_PROCESO");
```

> ⚠️ Existe también la **disyunción exclusiva (XOR)**: verdadera solo cuando *exactamente una* de las dos es verdadera.
>
> | p | q | p ⊕ q |
> |---|---|-------|
> | V | V | F |
> | V | F | **V** |
> | F | V | **V** |
> | F | F | F |
>
> En Java: `p ^ q` (con booleans, el operador `^` es XOR a nivel de bits).

---

## 4. Condicional (`p → q`)

Leído como "**si p entonces q**". Es falso **solo** cuando el antecedente es verdadero y el consecuente es falso.

| p | q | p → q |
|---|---|-------|
| V | V | V |
| V | F | **F** |
| F | V | V |
| F | F | V |

> 📌 "Si llueve, entonces el suelo está mojado" — Solo se rompe la promesa si llueve y el suelo NO está mojado.

```java
// Si el ticket existe, entonces se puede actualizar
// Equivale a: ¬p ∨ q
if (ticketExiste) {
    actualizar(ticket);
}
// Si ticketExiste es false, no importa lo que ocurra → la condición no se rompe
```

### Formas relacionadas al condicional

Dada la implicación **p → q**:

| Forma | Expresión | Equivalente lógico |
|-------|-----------|-------------------|
| Recíproca | q → p | NO equivalente |
| Contrarrecíproca | ¬q → ¬p | ✅ Equivalente a p → q |
| Inversa | ¬p → ¬q | NO equivalente |

---

## 5. Bicondicional (`p ↔ q`)

Leído como "**p si y solo si q**". Verdadero cuando ambas tienen el **mismo valor de verdad**.

| p | q | p ↔ q |
|---|---|-------|
| V | V | **V** |
| V | F | F |
| F | V | F |
| F | F | **V** |

```java
// Un usuario es premium si y solo si pagó su suscripción
boolean esPremium = haPagado == tieneBeneficiosPremium;
```

---

## Tabla de verdad general

Resumen de todos los conectivos para referencia rápida:

| p | q | ¬p | p ∧ q | p ∨ q | p ⊕ q | p → q | p ↔ q |
|---|---|----|-------|-------|-------|-------|-------|
| V | V | F  | V     | V     | F     | V     | V     |
| V | F | F  | F     | V     | V     | F     | F     |
| F | V | V  | F     | V     | V     | V     | F     |
| F | F | V  | F     | F     | F     | V     | V     |

---

## Tautologías, contradicciones y contingencias

| Tipo | Definición | Ejemplo |
|------|-----------|---------|
| **Tautología** | Siempre verdadera | `p ∨ ¬p` ("p o no p") |
| **Contradicción** | Siempre falsa | `p ∧ ¬p` ("p y no p") |
| **Contingencia** | A veces V, a veces F | `p ∧ q` |

```java
// Tautología en código (siempre true, el compilador puede optimizarla)
boolean siempre = valor > 0 || valor <= 0;  // p ∨ ¬p → siempre true

// Contradicción en código (siempre false, código muerto)
boolean nunca = valor > 10 && valor < 5;    // imposible → siempre false
```

> ⚠️ Las **contradicciones** en condiciones crean *código muerto* (código que nunca se ejecuta). Son errores lógicos difíciles de detectar.

---

## Leyes y equivalencias lógicas

Dos proposiciones son **lógicamente equivalentes** (`≡`) si tienen la misma tabla de verdad.

### Leyes de identidad
```
p ∧ V ≡ p
p ∨ F ≡ p
```

### Leyes de dominación
```
p ∨ V ≡ V
p ∧ F ≡ F
```

### Leyes de idempotencia
```
p ∨ p ≡ p
p ∧ p ≡ p
```

### Leyes de complemento
```
p ∨ ¬p ≡ V    (tautología)
p ∧ ¬p ≡ F    (contradicción)
¬(¬p)  ≡ p    (doble negación)
```

### Leyes conmutativas
```
p ∧ q ≡ q ∧ p
p ∨ q ≡ q ∨ p
```

### Leyes asociativas
```
(p ∧ q) ∧ r ≡ p ∧ (q ∧ r)
(p ∨ q) ∨ r ≡ p ∨ (q ∨ r)
```

### Leyes distributivas
```
p ∧ (q ∨ r) ≡ (p ∧ q) ∨ (p ∧ r)
p ∨ (q ∧ r) ≡ (p ∨ q) ∧ (p ∨ r)
```

### ⭐ Leyes de De Morgan

Las más utilizadas en programación:

```
¬(p ∧ q) ≡ ¬p ∨ ¬q
¬(p ∨ q) ≡ ¬p ∧ ¬q
```

En palabras:
- La negación de un **Y** es un **O** de negaciones.
- La negación de un **O** es un **Y** de negaciones.

```java
// ❌ Difícil de leer
if (!(usuario.isActivo() && usuario.tieneRol("ADMIN"))) {
    throw new AccessDeniedException("Acceso denegado");
}

// ✅ Equivalente por De Morgan — más claro
if (!usuario.isActivo() || !usuario.tieneRol("ADMIN")) {
    throw new AccessDeniedException("Acceso denegado");
}
```

### Eliminación del condicional
```
p → q ≡ ¬p ∨ q
```

```java
// "Si el ticket está cerrado, no se puede editar"
// p → q  ≡  ¬p ∨ q
// "El ticket está abierto O se puede editar"
boolean puedeEditar = !ticket.isCerrado() || tienePermisoEspecial;
```

---

## Precedencia de operadores

Al igual que en aritmética (multiplicación antes que suma), los operadores lógicos tienen orden de precedencia:

| Prioridad | Operador | Descripción |
|-----------|----------|-------------|
| 1 (mayor) | `¬` | Negación |
| 2 | `∧` | Conjunción |
| 3 | `∨` | Disyunción |
| 4 | `→` | Condicional |
| 5 (menor) | `↔` | Bicondicional |

> 📌 Usa **paréntesis** para hacer explícita la precedencia y mejorar la legibilidad, igual que en Java.

```java
// Ambiguo — ¿cuál se evalúa primero?
boolean resultado = a || b && c;

// Explícito — intención clara
boolean resultado = a || (b && c);  // && tiene mayor precedencia que ||
```

---

## Forma Normal Conjuntiva (FNC) y Forma Normal Disyuntiva (FND)

Cualquier proposición puede reescribirse en una **forma normal estándar**:

| Forma | Estructura | Ejemplo |
|-------|-----------|---------|
| **FND** | Disyunción de conjunciones (OR de ANDs) | `(p ∧ q) ∨ (¬p ∧ r)` |
| **FNC** | Conjunción de disyunciones (AND de ORs) | `(p ∨ q) ∧ (¬p ∨ r)` |

> 📌 Las bases de datos usan FNC internamente para optimizar consultas con `WHERE` compuesto.

---

## Aplicación práctica en Spring Boot

### Validaciones con múltiples condiciones

```java
@Service
public class TicketService {

    public void validarCambioEstado(Ticket ticket, String nuevoEstado, Usuario usuario) {
        // p: el ticket no está cerrado
        // q: el usuario es el creador o tiene rol ADMIN
        // r: el nuevo estado es válido

        boolean p = !ticket.getEstado().equals("CERRADO");
        boolean q = ticket.getCreadorId().equals(usuario.getId())
                    || usuario.getRol().equals("ADMIN");
        boolean r = List.of("ABIERTO", "EN_PROCESO", "CERRADO").contains(nuevoEstado);

        // Se permite el cambio si: p ∧ q ∧ r
        if (!(p && q && r)) {
            // Por De Morgan: ¬p ∨ ¬q ∨ ¬r
            if (!p) throw new IllegalStateException("No se puede cambiar el estado de un ticket cerrado");
            if (!q) throw new AccessDeniedException("No tienes permiso para cambiar este ticket");
            if (!r) throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }
    }
}
```

### Construcción de filtros dinámicos

```java
// Filtrar tickets activos y asignados al usuario actual
// p: estado es ABIERTO o EN_PROCESO → (e1 ∨ e2)
// q: asignado al usuario → a
// Condición total: (e1 ∨ e2) ∧ a

List<Ticket> filtrados = tickets.stream()
    .filter(t -> (t.getEstado().equals("ABIERTO") || t.getEstado().equals("EN_PROCESO"))
                 && t.getAsignadoId().equals(usuarioId))
    .collect(Collectors.toList());
```

### Reglas de negocio como proposiciones

```java
// Regla: un ticket puede eliminarse si y solo si
//        está cerrado Y (el usuario es admin O es el creador)
// p ↔ (q ∧ (r ∨ s))

boolean puedeEliminar =
    ticket.getEstado().equals("CERRADO")
    && (usuario.getRol().equals("ADMIN") || usuario.getId().equals(ticket.getCreadorId()));
```

---

## Inferencia lógica — Reglas básicas

| Regla | Forma | Ejemplo |
|-------|-------|---------|
| **Modus Ponens** | p, p→q ⊢ q | Si el token es válido (p) y token válido→acceso concedido (p→q), entonces acceso concedido (q) |
| **Modus Tollens** | ¬q, p→q ⊢ ¬p | Si no hay acceso (¬q), entonces el token no era válido (¬p) |
| **Silogismo hipotético** | p→q, q→r ⊢ p→r | Login→token, token→acceso ⊢ login→acceso |
| **Adición** | p ⊢ p ∨ q | Si el usuario es admin, entonces es admin o moderador |
| **Simplificación** | p ∧ q ⊢ p | Si el usuario es activo y verificado, entonces es activo |

---

## Resumen

| Concepto | Definición rápida |
|----------|-------------------|
| **Proposición** | Enunciado con valor V o F |
| **Negación ¬** | Invierte el valor de verdad |
| **Conjunción ∧** | V solo si ambas son V (`&&`) |
| **Disyunción ∨** | F solo si ambas son F (`\|\|`) |
| **Condicional →** | F solo si antecedente V y consecuente F |
| **Bicondicional ↔** | V cuando ambas tienen el mismo valor |
| **Tautología** | Siempre V para cualquier combinación |
| **Contradicción** | Siempre F para cualquier combinación |
| **De Morgan** | `¬(p∧q) ≡ ¬p∨¬q` y `¬(p∨q) ≡ ¬p∧¬q` |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

