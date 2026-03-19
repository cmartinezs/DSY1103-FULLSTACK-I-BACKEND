# 💡 Tips de Programación

> **Nivel de entrada:** cualquiera — empieza por el módulo que corresponda a tu nivel actual.  
> **Objetivo:** desarrollar el razonamiento lógico para leer una situación real, entender qué necesita el código y escribir la solución paso a paso.  
> **Lenguaje:** Java 21 — desde consola hasta REST API con Spring Boot.

---

## ¿De qué trata este extra?

Saber la sintaxis de Java no es suficiente para programar. El verdadero desafío es **leer una situación, entender qué necesita el algoritmo y traducirlo a código correcto**.

Este extra presenta **45 situaciones reales** — 15 por nivel — organizadas de menor a mayor complejidad. Cada una sigue la misma estructura:

1. 📋 **El escenario** — qué está pasando en lenguaje natural
2. ❌ **El error común** — qué hace la mayoría de estudiantes al principio
3. 🧠 **¿Cómo pienso esto?** — el razonamiento paso a paso antes de escribir código
4. ✅ **La solución** — código Java limpio y explicado
5. 🌐 **Variante en API** — cómo se vería en un endpoint REST (cuando aplica)

> 📌 No saltes directo al código. Lee primero el razonamiento — ahí está el aprendizaje real.

---

## Índice de módulos

| # | Módulo | Nivel | Tips |
|---|--------|-------|------|
| 00 | [Cómo pensar un problema antes de codear](./00_como_pensar_un_problema.md) | 🟢 Todos | Metodología general |
| 01 | [Situaciones básicas](./01_situaciones_basicas.md) | 🟡 Básico | Tips 01 – 15 |
| 02 | [Situaciones intermedias](./02_situaciones_intermedias.md) | 🟠 Intermedio | Tips 16 – 30 |
| 03 | [Situaciones en REST API](./03_situaciones_api.md) | 🔴 Avanzado | Tips 31 – 45 |
| 04 | [Cheat Sheet — los 45 tips de un vistazo](./04_situaciones_adicionales.md) | 🟢 Todos | Referencia rápida |

---

## Mapa de situaciones

```
💡 Tips de Programación
│
├── 00 Metodología ──────────────────────────────────┐
│   ¿Qué tengo? · ¿Qué quiero? · ¿Cómo llego?       │  Base de todo
│   Pseudocódigo · dividir el problema               │
│                                                    ┘
├── 01 Básico — Consola ──────────────────────────────┐
│   Tip 01 — Decidir según un valor                  │
│   Tip 02 — Repetir hasta que ocurra algo           │
│   Tip 03 — Recorrer, buscar o filtrar una lista    │
│   Tip 04 — Validar antes de procesar               │
│   Tip 05 — String con número → operación incorrecta│  Java puro
│   Tip 06 — Combinar varias condiciones en un if    │  en consola
│   Tip 07 — Verificar si un valor está en un rango  │
│   Tip 08 — Contar elementos que cumplen condición  │
│   Tip 09 — Sumar valores de una lista              │
│   Tip 10 — División entera sin decimales           │
│   Tip 11 — Ejecutar un bloque exactamente N veces  │
│   Tip 12 — Valores que no deben cambiar            │
│   Tip 13 — Par, múltiplo y propiedades numéricas   │
│   Tip 14 — Código copiado en varios lugares        │
│   Tip 15 — Guardar varios valores del mismo tipo   │
│                                                    ┘
├── 02 Intermedio — Objetos y Streams ────────────────┐
│   Tip 16 — Comparar objetos correctamente          │
│   Tip 17 — El programa explota con NPE             │
│   Tip 18 — Filtrar, transformar u ordenar lista    │
│   Tip 19 — Trabajar con fechas                     │
│   Tip 20 — Lógica duplicada en varios métodos      │  Java con
│   Tip 21 — Magic strings con typos silenciosos     │  clases y
│   Tip 22 — Máximo, mínimo o primero que cumple     │  Streams
│   Tip 23 — Estadísticas de una lista               │
│   Tip 24 — Excepciones específicas de negocio      │
│   Tip 25 — Devolver más de un valor               │
│   Tip 26 — Campos opcionales en objetos            │
│   Tip 27 — Diferenciar el tipo de error            │
│   Tip 28 — Unir propiedades en un String           │
│   Tip 29 — Agrupar elementos en un Map             │
│   Tip 30 — Reducir una lista a un solo valor       │
│                                                    ┘
└── 03 Avanzado — REST API Spring Boot ──────────────┐
    Tip 31 — Migrar lógica de consola a API          │
    Tip 32 — Leer y validar datos de la petición     │
    Tip 33 — Status HTTP correcto y errores claros   │
    Tip 34 — Filtrar y paginar resultados            │
    Tip 35 — Actualizar solo algunos campos          │
    Tip 36 — Configuración fuera del código          │  Spring Boot
    Tip 37 — Registrar lo que ocurre en la API       │  REST API
    Tip 38 — Fechas en JSON con formato incorrecto   │
    Tip 39 — Consumir datos de otra API externa      │
    Tip 40 — Path base repetido en cada endpoint     │
    Tip 41 — Monitorear el estado de la API          │
    Tip 42 — Consultas lentas que devuelven lo mismo │
    Tip 43 — Documentar endpoints automáticamente    │
    Tip 44 — Código antes de llegar al Controller    │
    Tip 45 — Tareas sin bloquear la respuesta        │
                                                     ┘
```

---

## ¿Cómo usar este extra?

1. **Empieza siempre por el [módulo 00](./00_como_pensar_un_problema.md)** — aunque sea breve, cambia la forma de enfrentarte a cualquier problema.
2. **Lee cada situación completa**, no saltes al código directamente.
3. **Antes de ver la solución**, intenta escribir el pseudocódigo tú mismo.
4. **Si ya dominas un nivel**, avanza al siguiente — las situaciones son acumulativas.
5. Los tips del [módulo 03](./03_situaciones_api.md) asumen que ya manejas los dos anteriores.
6. Usa el [módulo 04](./04_situaciones_adicionales.md) como referencia rápida cuando necesites recordar la regla de un tip.

---

## Tabla global de tips

| # | Nivel | Situación | Módulo |
|---|-------|-----------|--------|
| 01 | 🟡 | Decidir según un valor | [→ 01](./01_situaciones_basicas.md#tip-01) |
| 02 | 🟡 | Repetir hasta que ocurra algo | [→ 01](./01_situaciones_basicas.md#tip-02) |
| 03 | 🟡 | Recorrer, buscar o filtrar una lista | [→ 01](./01_situaciones_basicas.md#tip-03) |
| 04 | 🟡 | Validar antes de procesar | [→ 01](./01_situaciones_basicas.md#tip-04) |
| 05 | 🟡 | String con número → operación incorrecta | [→ 01](./01_situaciones_basicas.md#tip-05) |
| 06 | 🟡 | Combinar varias condiciones en un if | [→ 01](./01_situaciones_basicas.md#tip-06) |
| 07 | 🟡 | Verificar si un valor está en un rango | [→ 01](./01_situaciones_basicas.md#tip-07) |
| 08 | 🟡 | Contar cuántos elementos cumplen una condición | [→ 01](./01_situaciones_basicas.md#tip-08) |
| 09 | 🟡 | Sumar todos los valores de una lista | [→ 01](./01_situaciones_basicas.md#tip-09) |
| 10 | 🟡 | División entre enteros sin decimales | [→ 01](./01_situaciones_basicas.md#tip-10) |
| 11 | 🟡 | Ejecutar un bloque exactamente N veces | [→ 01](./01_situaciones_basicas.md#tip-11) |
| 12 | 🟡 | Valor que no debería cambiar nunca | [→ 01](./01_situaciones_basicas.md#tip-12) |
| 13 | 🟡 | Saber si un número es par, múltiplo, etc. | [→ 01](./01_situaciones_basicas.md#tip-13) |
| 14 | 🟡 | El mismo bloque copiado en varios lugares | [→ 01](./01_situaciones_basicas.md#tip-14) |
| 15 | 🟡 | Guardar varios valores del mismo tipo | [→ 01](./01_situaciones_basicas.md#tip-15) |
| 16 | 🟠 | Comparar objetos con los mismos datos | [→ 02](./02_situaciones_intermedias.md#tip-16) |
| 17 | 🟠 | NullPointerException al usar un resultado | [→ 02](./02_situaciones_intermedias.md#tip-17) |
| 18 | 🟠 | Filtrar, transformar u ordenar una lista de objetos | [→ 02](./02_situaciones_intermedias.md#tip-18) |
| 19 | 🟠 | Trabajar con fechas | [→ 02](./02_situaciones_intermedias.md#tip-19) |
| 20 | 🟠 | Lógica duplicada en varios métodos | [→ 02](./02_situaciones_intermedias.md#tip-20) |
| 21 | 🟠 | Magic strings con typos silenciosos | [→ 02](./02_situaciones_intermedias.md#tip-21) |
| 22 | 🟠 | Obtener el máximo, mínimo o primero que cumple | [→ 02](./02_situaciones_intermedias.md#tip-22) |
| 23 | 🟠 | Estadísticas de una lista (total, promedio, máx.) | [→ 02](./02_situaciones_intermedias.md#tip-23) |
| 24 | 🟠 | Excepción específica para cada error de negocio | [→ 02](./02_situaciones_intermedias.md#tip-24) |
| 25 | 🟠 | Devolver más de un valor desde un método | [→ 02](./02_situaciones_intermedias.md#tip-25) |
| 26 | 🟠 | Campos opcionales en objetos o peticiones | [→ 02](./02_situaciones_intermedias.md#tip-26) |
| 27 | 🟠 | Diferenciar el tipo de error para reaccionar diferente | [→ 02](./02_situaciones_intermedias.md#tip-27) |
| 28 | 🟠 | Unir propiedades de una lista en un String | [→ 02](./02_situaciones_intermedias.md#tip-28) |
| 29 | 🟠 | Agrupar elementos de una lista en un Map | [→ 02](./02_situaciones_intermedias.md#tip-29) |
| 30 | 🟠 | Reducir toda una lista a un solo valor | [→ 02](./02_situaciones_intermedias.md#tip-30) |
| 31 | 🔴 | Migrar lógica de consola a API bien estructurada | [→ 03](./03_situaciones_api.md#tip-31) |
| 32 | 🔴 | Leer y validar datos de la petición | [→ 03](./03_situaciones_api.md#tip-32) |
| 33 | 🔴 | Status HTTP correcto y errores claros | [→ 03](./03_situaciones_api.md#tip-33) |
| 34 | 🔴 | Filtrar y paginar resultados | [→ 03](./03_situaciones_api.md#tip-34) |
| 35 | 🔴 | Actualizar solo algunos campos (PATCH vs PUT) | [→ 03](./03_situaciones_api.md#tip-35) |
| 36 | 🔴 | URLs, puertos y claves en el código fuente | [→ 03](./03_situaciones_api.md#tip-36) |
| 37 | 🔴 | Saber qué ocurre en la API cuando algo falla | [→ 03](./03_situaciones_api.md#tip-37) |
| 38 | 🔴 | JSON con fechas en formato incorrecto | [→ 03](./03_situaciones_api.md#tip-38) |
| 39 | 🔴 | Consumir datos de otra API externa | [→ 03](./03_situaciones_api.md#tip-39) |
| 40 | 🔴 | Path base repetido en cada endpoint | [→ 03](./03_situaciones_api.md#tip-40) |
| 41 | 🔴 | Monitorear el estado de la API sin endpoints propios | [→ 03](./03_situaciones_api.md#tip-41) |
| 42 | 🔴 | Consultas lentas que siempre devuelven lo mismo | [→ 03](./03_situaciones_api.md#tip-42) |
| 43 | 🔴 | Documentar endpoints automáticamente | [→ 03](./03_situaciones_api.md#tip-43) |
| 44 | 🔴 | Ejecutar código antes de que llegue la petición | [→ 03](./03_situaciones_api.md#tip-44) |
| 45 | 🔴 | Tareas en segundo plano sin bloquear la respuesta | [→ 03](./03_situaciones_api.md#tip-45) |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
