# Ejercicio 01 — Validador de acceso al sistema

> **Nivel:** ⭐ Básico  
> **Conceptos:** Lógica proposicional · Operadores `&&`, `||`, `!` · `if/else` · Variables booleanas  
> **Tiempo estimado:** 20–30 min

---

## 🏢 Contexto

Trabajas como desarrollador junior en una empresa de software. El equipo de seguridad te pide implementar la **lógica de validación de acceso** al sistema interno. El sistema debe decidir si un usuario puede ingresar o no, basándose en tres condiciones simples.

---

## 📋 Enunciado

Crea un programa Java que valide si un usuario puede acceder al sistema según las siguientes reglas de negocio:

**Proposiciones:**
- **p:** El usuario tiene cuenta activa (`cuentaActiva = true/false`)
- **q:** La contraseña ingresada es correcta (`passwordCorrecto = true/false`)
- **r:** El usuario no tiene sesión activa en otro dispositivo (`sinSesionActiva = true/false`)

**Regla de acceso:**
> Un usuario puede acceder **si y solo si** tiene cuenta activa **Y** la contraseña es correcta **Y** no tiene sesión activa en otro dispositivo.
>
> Fórmula lógica: `acceso = p ∧ q ∧ r`

**Regla de bloqueo:**
> El sistema bloquea la cuenta **si** el usuario no tiene cuenta activa **O** ha intentado ingresar más de 3 veces con contraseña incorrecta.
>
> Fórmula lógica: `bloqueada = ¬p ∨ (intentos > 3)`

El programa debe imprimir un **mensaje descriptivo** indicando por qué se permite o deniega el acceso.

---

## 🚫 Restricciones

- No uses librerías externas; solo Java puro.
- Declara cada proposición como variable `boolean` con nombre descriptivo.
- Usa al menos un operador `!` (negación), uno `&&` (conjunción) y uno `||` (disyunción).
- El mensaje de salida debe ser **exactamente** como se muestra en los ejemplos.
- No uses `Scanner`; define los valores directamente en el código (como si vinieran de una base de datos).
- El método `main` debe delegar la lógica a **al menos un método separado** llamado `verificarAcceso`.

---

## 📥 Ejemplos de entrada

> Los valores se definen directamente como variables en el código.

### Caso 1 — Acceso permitido
```
cuentaActiva    = true
passwordCorrecto = true
sinSesionActiva  = true
intentos        = 1
```

### Caso 2 — Contraseña incorrecta
```
cuentaActiva    = true
passwordCorrecto = false
sinSesionActiva  = true
intentos        = 2
```

### Caso 3 — Sesión activa en otro dispositivo
```
cuentaActiva    = true
passwordCorrecto = true
sinSesionActiva  = false
intentos        = 0
```

### Caso 4 — Cuenta bloqueada por intentos
```
cuentaActiva    = true
passwordCorrecto = false
sinSesionActiva  = true
intentos        = 4
```

### Caso 5 — Cuenta desactivada
```
cuentaActiva    = false
passwordCorrecto = true
sinSesionActiva  = true
intentos        = 0
```

---

## 📤 Salidas esperadas

### Caso 1
```
✅ Acceso permitido. Bienvenido al sistema.
```

### Caso 2
```
❌ Acceso denegado: contraseña incorrecta.
```

### Caso 3
```
❌ Acceso denegado: ya tienes una sesión activa en otro dispositivo.
```

### Caso 4
```
🔒 Cuenta bloqueada: demasiados intentos fallidos. Contacta al administrador.
```

### Caso 5
```
🔒 Cuenta bloqueada: tu cuenta está desactivada. Contacta al administrador.
```

---

## 💡 Pistas

<details>
<summary>Pista 1 — Estructura del método</summary>

```java
static String verificarAcceso(boolean cuentaActiva, boolean passwordCorrecto,
                               boolean sinSesionActiva, int intentos) {
    // primero verifica si la cuenta está bloqueada
    // luego evalúa p ∧ q ∧ r
    // finalmente determina el motivo de rechazo
}
```
</details>

<details>
<summary>Pista 2 — Orden de evaluación</summary>

Evalúa primero las condiciones de bloqueo (tienen prioridad sobre el intento de acceso), luego las condiciones de acceso, y finalmente devuelve el motivo específico de rechazo.
</details>

---

## 🧠 Reflexión final

Una vez que tu programa funcione, responde:

1. ¿Qué operador lógico representa `¬` en Java?
2. ¿Por qué es importante evaluar primero el bloqueo antes que el acceso?
3. Si quisieras que el acceso fuera válido **incluso** sin contraseña cuando la cuenta tiene un token especial, ¿cómo cambiaría la fórmula lógica?

---

*[← Volver al índice](./README.md) · [Siguiente ejercicio →](./02_calculadora_descuentos.md)*

