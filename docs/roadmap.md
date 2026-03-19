# 🗺️ Roadmap de Estudio — Material de Apoyo

> **Curso:** DSY1103 Fullstack I Backend — Spring Boot 4 · Java 21
> **Actualizado:** Marzo 2026

Este roadmap te ayuda a organizar el estudio del [material de apoyo](./extras/README.md) según el tiempo que tienes disponible. No es obligatorio leerlo todo de una vez — la idea es ir leyendo **lo correcto, en el momento correcto**.

---

## Mapa de dependencias

Este árbol muestra qué extras dependen de otros y en qué orden tiene más sentido estudiarlos. Los niveles más altos impactan directamente tu capacidad de programar; los más bajos complementan tu perfil profesional.

```
Nivel 1 — Programación (mayor impacto inmediato)
├── ☕  Java para Spring Boot   → lenguaje base de todo el curso
│       └── 🧠  Lógica Proposicional  → condiciones, validaciones y reglas de negocio
├── ➕  Matemáticas para Programar → operaciones, contadores, descuentos, redondeo
├── 💡  Tips de Programación   → razonamiento y resolución de situaciones reales
├── 🛠️  Lombok                 → menos código repetitivo desde el primer día
├── 🔷  JSON                   → formato de todas las respuestas REST
├── 📄  YAML                   → configuración de Spring Boot
├── 🔐  Variables de Entorno   → configuración segura sin hardcodear valores
└── 📦  Maven                  → entender cómo se construye y ejecuta el proyecto

Nivel 2 — Control de versiones
└── 🌿  Git y GitHub           → historial, colaboración, entrega de actividades

Nivel 3 — Entorno de trabajo
├── 🖥️  Terminal (Bash / Windows)
└── 📝  Markdown

Nivel 4 — Complementario
├── 🧱  SOLID
├── 📊  Modelo de Madurez de Richardson
├── 🌊  GitFlow
└── 🏋️  Ejercicios Prácticos
```

> 💡 No necesitas terminar un nivel para avanzar al siguiente. El árbol muestra **qué ayuda entender mejor qué**, no una secuencia estricta.

---

## Tabla resumen de extras

| Extra | Nivel | Tiempo estimado |
|-------|-------|-----------------|
| [☕ Java para Spring Boot](./extras/java-para-spring-boot/README.md) | 🔴 1 — Programación | 8 – 12 h |
| [🧠 Lógica Proposicional](./extras/logica-proposicional/README.md) | 🔴 1 — Programación | 2 – 3 h |
| [➕ Matemáticas para Programar](./extras/matematicas-para-programar/README.md) | 🔴 1 — Programación | 2 – 3 h |
| [💡 Tips de Programación](./extras/tips-de-programacion/README.md) | 🔴 1 — Programación | 3 – 4 h |
| [🛠️ Lombok](./extras/lombok/README.md) | 🔴 1 — Programación | 1 – 1.5 h |
| [🔷 JSON](./extras/json/README.md) | 🔴 1 — Programación | 1 – 1.5 h |
| [📄 YAML](./extras/yaml/README.md) | 🔴 1 — Programación | 1 – 2 h |
| [🔐 Variables de Entorno](./extras/env-variables/README.md) | 🔴 1 — Programación | 1 – 2 h |
| [📦 Maven](./extras/maven/README.md) | 🔴 1 — Programación | 1 – 2 h |
| [🌿 Git y GitHub](./extras/git-github/README.md) | 🟠 2 — Control de versiones | 2 – 3 h |
| [🖥️ Terminal](./extras/terminal/README.md) | 🟡 3 — Entorno | 2 – 3 h |
| [📝 Markdown](./extras/markdown/README.md) | 🟡 3 — Entorno | 30 – 45 min |
| [🧱 SOLID](./extras/solid/README.md) | 🟢 4 — Complementario | 2 – 3 h |
| [📊 Richardson](./extras/richardson-maturity-model/README.md) | 🟢 4 — Complementario | 30 – 45 min |
| [🌊 GitFlow](./extras/gitflow/README.md) | 🟢 4 — Complementario | 1 – 1.5 h |
| [🏋️ Ejercicios Prácticos](./extras/ejercicios/README.md) | 🟢 4 — Complementario | 5 – 10 h |

**Total estimado:** 30 – 52 horas según profundidad

---

## Rutas según tiempo disponible

---

### ⚡ Ruta Express
> **Para quién:** tienes muy poco tiempo libre y necesitas lo más útil ahora mismo.
> **Tiempo total:** 4 – 5 horas
> **Objetivo:** impactar directamente tu forma de escribir código desde esta semana.

```
1. ☕  Java para Spring Boot (módulos 00 – 03)  → 2 h   sintaxis, tipos, control de flujo
2. 🔷  JSON                                     → 1 h   leer y entender respuestas REST
3. 📄  YAML                                     → 1 h   configurar application.yml sin errores
4. 🛠️  Lombok                                   → 30 min eliminar boilerplate en tus clases
```

> ⏭️ Con esto puedes seguir el código de clase sin quedarte atrás. El resto se va sumando semana a semana.

---

### 📅 Ruta Semanal
> **Para quién:** tienes entre 1 y 2 horas libres durante la semana, distribuidas en días de semana.
> **Tiempo total:** distribuido en 8 semanas
> **Objetivo:** avanzar de forma sostenida sin saturarte.

| Semana | Extra(s) | Tiempo | Enfoque |
|--------|----------|--------|---------|
| **1** | ☕ Java para Spring Boot (módulos 00 – 03) | 2 h | Sintaxis base y control de flujo |
| **2** | ☕ Java para Spring Boot (módulos 04 – 07) | 2 h | Métodos, clases, POO |
| **3** | ☕ Java para Spring Boot (módulos 08 – 12) | 2 h | Colecciones, lambdas, generics |
| **4** | 🧠 Lógica Proposicional + 🛠️ Lombok | 2 h | Condiciones limpias, menos boilerplate |
| **5** | 🔷 JSON + 📄 YAML + 📦 Maven | 2 h | Datos, configuración, build |
| **6** | 🔐 Variables de Entorno + 🌿 Git y GitHub | 2 h | Configuración segura y control de versiones |
| **7** | 🖥️ Terminal + 📝 Markdown | 2 h | Entorno de trabajo profesional |
| **8** | 🧱 SOLID + 📊 Richardson | 2 h | Calidad y diseño de APIs |

> 🏋️ Los Ejercicios Prácticos y GitFlow quedan como actividad libre cuando termines cada bloque o cuando el ritmo del curso lo permita.

---

### 🔥 Ruta de Fin de Semana
> **Para quién:** puedes dedicar 2 – 3 horas un sábado o domingo, sin que interfiera con el descanso.
> **Tiempo por sesión:** máximo 3 horas
> **Objetivo:** avanzar un extra completo por fin de semana, sin prisa.

Cada fin de semana es una sesión independiente. Elige según en qué parte del roadmap estás:

| Fin de semana | Extra | Duración | Qué vas a lograr |
|---------------|-------|----------|------------------|
| **1** | ☕ Java para Spring Boot (módulos 00 – 05) | 2.5 h | Tener el lenguaje base claro |
| **2** | ☕ Java para Spring Boot (módulos 06 – 12) | 2.5 h | POO, colecciones y lambdas |
| **3** | 🔷 JSON + 📄 YAML + 📦 Maven | 2.5 h | Datos y configuración del proyecto |
| **4** | 🔐 Variables de Entorno + 🛠️ Lombok | 2 h | Configuración segura y menos código |
| **5** | 🌿 Git y GitHub | 2.5 h | Control de versiones completo |
| **6** | 🧠 Lógica Proposicional | 2 h | Condiciones y validaciones más claras |
| **7** | 🖥️ Terminal + 📝 Markdown | 2 h | Entorno de trabajo |
| **8** | 🧱 SOLID + 📊 Richardson | 2 h | Calidad de código y APIs |

> ☕ El café del domingo cuenta. Dos horas con foco valen más que ocho horas distracted.

---

### 🏆 Ruta Completa
> **Para quién:** quieres dominar todo el material de apoyo sin dejar cabos sueltos.
> **Tiempo total:** 25 – 45 horas
> **Objetivo:** construir una base profesional sólida en paralelo al curso.

Sigue el orden del mapa de dependencias, dedicando al menos una sesión a cada extra antes de avanzar al siguiente nivel:

**Nivel 1 — Programación** `~22 h`
1. [Java para Spring Boot](./extras/java-para-spring-boot/README.md) — todos los módulos (00 – 12)
2. [Lógica Proposicional](./extras/logica-proposicional/README.md)
3. [Matemáticas para Programar](./extras/matematicas-para-programar/README.md)
4. [Tips de Programación](./extras/tips-de-programacion/README.md)
5. [Lombok](./extras/lombok/README.md)
6. [JSON](./extras/json/README.md)
7. [YAML](./extras/yaml/README.md)
8. [Variables de Entorno](./extras/env-variables/README.md)
9. [Maven](./extras/maven/README.md)

**Nivel 2 — Control de versiones** `~3 h`

10. [Git y GitHub](./extras/git-github/README.md)

**Nivel 3 — Entorno de trabajo** `~3 h`

11. [Terminal](./extras/terminal/README.md) — Bash y/o CMD + PowerShell
12. [Markdown](./extras/markdown/README.md)

**Nivel 4 — Complementario** `~12 h`

13. [SOLID](./extras/solid/README.md)
14. [Modelo de Madurez de Richardson](./extras/richardson-maturity-model/README.md)
15. [GitFlow](./extras/gitflow/README.md)
16. [Ejercicios Prácticos](./extras/ejercicios/README.md) — Serie I + Serie II

---

## ¿Y si tengo aún menos tiempo?

Si debes elegir **un solo extra por semana**, este es el orden de mayor a menor impacto real en tu código:

| Semana | Extra único | Por qué primero |
|--------|-------------|-----------------|
| 1 | ☕ Java para Spring Boot | El código de clase no tiene sentido sin el lenguaje |
| 2 | 🔷 JSON + 📄 YAML | Rápidos y necesarios para el proyecto actual |
| 3 | 🔐 Variables de Entorno | Evita malos hábitos desde el inicio |
| 4 | 🛠️ Lombok | Menos código que escribir, más tiempo para entender |
| 5 | 🌿 Git y GitHub | Necesario para entregar actividades correctamente |
| 6 | 🧠 Lógica Proposicional | Mejora la calidad de tus condiciones y validaciones |
| 7 | 📦 Maven | Entiende qué hace el proyecto por debajo |
| 8+ | 🖥️ Terminal, 🧱 SOLID, 🏋️ Ejercicios | Según el tiempo y el interés |

---

## Leyenda de niveles

| Nivel | Descripción |
|-------|-------------|
| 🔴 **1 — Programación** | Impacta directamente cómo escribes código hoy |
| 🟠 **2 — Control de versiones** | Necesario para trabajar y entregar actividades |
| 🟡 **3 — Entorno** | Mejora tu flujo de trabajo como desarrollador |
| 🟢 **4 — Complementario** | Enriquece tu perfil profesional sin urgencia |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
