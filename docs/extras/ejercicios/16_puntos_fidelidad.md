# Ejercicio 16 — Sistema de puntos de fidelidad del supermercado

> **Nivel:** ⭐ Básico
> **Conceptos:** Variables y tipos de datos · Operadores aritméticos · Lógica proposicional · Condicionales · Métodos
> **Tiempo estimado:** 20–35 min

---

## 🛒 Contexto

La cadena de supermercados **FreshMart** lanzó un programa de fidelidad para premiar a sus clientes frecuentes. Con cada compra acumulan puntos que luego pueden canjear como descuento. El equipo comercial necesita una herramienta que, dado el perfil del cliente y el detalle de la compra, calcule automáticamente cuántos puntos gana, si puede canjear algo ese día y cuánto termina pagando.

---

## 📋 Enunciado

Para cada compra se conoce lo siguiente: el nombre del cliente, el monto pagado en pesos, los puntos que tenía acumulados antes de esta visita, si pertenece al programa de socios, y si la compra ocurre en fin de semana.

---

### Cómo se acumulan los puntos

Por cada $1.000 completos de compra el cliente gana un punto. Los clientes socios ganan el doble. Si además la compra es en fin de semana, reciben un punto extra por cada 10 puntos acumulados en esa compra. Las fracciones de punto se descartan siempre.

---

### Cuándo se puede canjear

El canje solo es posible cuando se cumplen las tres condiciones al mismo tiempo:

- El cliente es socio del programa.
- Tiene al menos 100 puntos acumulados antes de la compra.
- El monto de la compra es de $5.000 o más.

Si alguna de las tres no se cumple, no hay canje. Cada punto vale $5 de descuento, pero el máximo que se puede canjear equivale al 20% del monto de la compra, aunque el cliente tenga más puntos disponibles.

El monto final es el monto original menos el descuento obtenido por canje. Si no hubo canje, el monto final es el mismo que el original.

---

### Qué debe mostrar el programa

Al procesar una compra, el programa imprime un resumen con:

- Nombre del cliente y si es socio o no.
- Si la compra fue en fin de semana.
- Monto original de la compra.
- Puntos previos al inicio de la compra.
- Puntos ganados en esta compra y cómo se calcularon.
- Si hubo canje: cuántos puntos se canjearon y a cuánto equivalen en pesos.
- Monto final a pagar.
- Nuevo saldo de puntos (previos + ganados − canjeados).

---

## 🚫 Restricciones

- Los puntos son siempre números enteros; nunca uses decimales para representarlos.
- Las tres condiciones del canje deben estar escritas como tres variables con nombres que expliquen qué verifican, antes de combinarlas en la decisión final.
- No mezcles el cálculo del canje dentro de la condición misma; primero determina si hay canje, luego calcula cuántos puntos aplican.
- Organiza la lógica en métodos con nombres descriptivos; el punto de entrada solo coordina las llamadas.
- Muestra los montos en pesos con separador de miles, sin centavos.

---

## 📥 Casos de prueba

**Caso 1 — Sofía Herrera:** socia del programa, compra en fin de semana por $85.000, tenía 120 puntos acumulados.

**Caso 2 — Marcos Díaz:** no es socio, compra un día de semana por $4.300, tenía 50 puntos acumulados.

**Caso 3 — Laura Vega:** socia del programa, compra un día de semana por $30.000, tenía 60 puntos acumulados.

**Caso 4 — Pedro Soto:** socio del programa, compra en fin de semana por $3.000, tenía 200 puntos acumulados.

---

## 📤 Resultados esperados

**Caso 1 — Sofía Herrera:**
Como es socia, los puntos base son 170 (85 × 2). Como es fin de semana, gana 17 puntos adicionales (170 ÷ 10, descartando fracción). Total ganado: 187 puntos. Las tres condiciones de canje se cumplen: es socia ✔, tiene más de 100 puntos previos ✔, compra mayor a $5.000 ✔. El 20% de $85.000 son $17.000, que equivaldrían a 3.400 puntos; como solo tiene 120, canjea los 120 completos → descuento de $600. Monto final: $84.400. Nuevo saldo: 120 + 187 − 120 = **187 puntos**.

**Caso 2 — Marcos Díaz:**
No es socio, gana solo puntos base: 4 puntos ($4.300 ÷ $1.000, descartando fracción). No puede canjear porque no es socio. Monto final: $4.300. Nuevo saldo: 50 + 4 = **54 puntos**.

**Caso 3 — Laura Vega:**
Es socia pero no es fin de semana, gana el doble de los puntos base: 60 puntos. No puede canjear porque sus 60 puntos previos son menos de 100. Monto final: $30.000. Nuevo saldo: 60 + 60 = **120 puntos**.

**Caso 4 — Pedro Soto:**
Es socio y es fin de semana. Puntos base: 6 (3 × 2). Bono de fin de semana: 0 (6 ÷ 10 = 0). Total ganado: 6 puntos. No puede canjear porque la compra es menor a $5.000. Monto final: $3.000. Nuevo saldo: 200 + 6 = **206 puntos**.

---

## 🧠 Reflexión final

¿Cuál de las tres condiciones del canje crees que se incumple más frecuentemente en el mundo real, y por qué? Si mañana el área comercial agrega una cuarta condición —que el cliente no tenga deudas pendientes con la tienda—, ¿cómo cambia la regla? ¿Seguiría siendo una conjunción o cambiaría el conectivo lógico?

---

*[← Ejercicio anterior](./15_simulador_banco.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./17_clinica_pacientes.md)*
