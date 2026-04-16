# 🗺️ Roadmap de Estudio — Material de Apoyo

> **Curso:** DSY1103 Fullstack I Backend — Spring Boot 4 · Java 21
> **Actualizado:** Marzo 2026

Este roadmap te ayuda a organizar el estudio del [material de apoyo](./extras/README.md) según el tiempo que tienes disponible. No es obligatorio leerlo todo de una vez — la idea es ir leyendo **lo correcto, en el momento correcto**.

---

## Mapa de dependencias

Este árbol muestra qué extras dependen de otros y en qué orden tiene más sentido estudiarlos. Los niveles más altos impactan directamente tu capacidad de programar; los más bajos complementan tu perfil profesional.

```
Nivel 1 — Programación (mayor impacto inmediato)
├── 📚  Conceptos de Programación → vocabulario universal, previo o paralelo a Java
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
├── 🗂️  Uso de PC              → sistema de archivos, dónde viven tus proyectos
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
| [📚 Conceptos de Programación](./extras/conceptos-de-programacion/README.md) | 🔴 1 — Programación | 7 – 10 h |
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
| [🗂️ Uso de PC](./extras/uso-de-pc/README.md) | 🟡 3 — Entorno | 1 – 1.5 h |
| [🖥️ Terminal](./extras/terminal/README.md) | 🟡 3 — Entorno | 2 – 3 h |
| [📝 Markdown](./extras/markdown/README.md) | 🟡 3 — Entorno | 30 – 45 min |
| [🧱 SOLID](./extras/solid/README.md) | 🟢 4 — Complementario | 2 – 3 h |
| [📊 Richardson](./extras/richardson-maturity-model/README.md) | 🟢 4 — Complementario | 30 – 45 min |
| [🌊 GitFlow](./extras/gitflow/README.md) | 🟢 4 — Complementario | 1 – 1.5 h |
| [🏋️ Ejercicios Prácticos](./extras/ejercicios/README.md) | 🟢 4 — Complementario | 5 – 10 h |

**Total estimado:** 38 – 63 horas según profundidad

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

**Nivel 1 — Programación** `~30 h`
1. [Conceptos de Programación](./extras/conceptos-de-programacion/README.md) — todos los módulos (01 – 13)
2. [Java para Spring Boot](./extras/java-para-spring-boot/README.md) — todos los módulos (00 – 12)
3. [Lógica Proposicional](./extras/logica-proposicional/README.md)
4. [Matemáticas para Programar](./extras/matematicas-para-programar/README.md)
5. [Tips de Programación](./extras/tips-de-programacion/README.md)
6. [Lombok](./extras/lombok/README.md)
7. [JSON](./extras/json/README.md)
8. [YAML](./extras/yaml/README.md)
9. [Variables de Entorno](./extras/env-variables/README.md)
10. [Maven](./extras/maven/README.md)

**Nivel 2 — Control de versiones** `~3 h`

11. [Git y GitHub](./extras/git-github/README.md)

**Nivel 3 — Entorno de trabajo** `~4 h`

12. [Uso de PC](./extras/uso-de-pc/README.md) — sistema de archivos y organización
13. [Terminal](./extras/terminal/README.md) — Bash y/o CMD + PowerShell
14. [Markdown](./extras/markdown/README.md)

**Nivel 4 — Complementario** `~12 h`

15. [SOLID](./extras/solid/README.md)
16. [Modelo de Madurez de Richardson](./extras/richardson-maturity-model/README.md)
17. [GitFlow](./extras/gitflow/README.md)
18. [Ejercicios Prácticos](./extras/ejercicios/README.md) — Serie I + Serie II

---

## Rutas según tu perfil

Cada perfil parte de una base distinta. Elige el que mejor te describa para ahorrar tiempo en lo que ya dominas y enfocarte en lo que realmente necesitas.

| Perfil | Tiempo total | Para quién |
|--------|--------------|------------|
| 🎓 Estándar | 19 – 25 h | Sigues el curso al ritmo de clases |
| 🐣 Desde Cero | 51 – 66 h | Nunca has programado |
| 🔁 Refresh Java | 13 – 17 h | Estudiaste Java hace años |
| 🌐 Java → Web | 11 – 15 h | Sabes Java pero no HTTP/REST |
| 🚀 Spring Pro | 2 – 4 h | Ya construiste APIs con Spring Boot |

---

### 🎓 Ruta Estándar — Seguimiento del curso
> **Para quién:** estás cursando DSY1103 y quieres ir al ritmo de las clases sin atrasarte.
> **Tiempo total:** 19 – 25 h distribuidas durante el semestre
> **Objetivo:** llegar preparado a cada clase con los prerrequisitos resueltos.

**Antes de empezar (semana 0)**
1. 🗂️ Uso de PC — sistema de archivos y dónde guardar tus proyectos (lectura rápida si ya te manejas)
2. 📚 Conceptos de Programación — módulos 01, 03, 04 (vocabulario, estructuras de datos, errores)
3. ☕ Java para Spring Boot — módulos 00 – 03 (sintaxis y control de flujo)
4. 🔷 JSON + 📄 YAML
5. 🛠️ Lombok

**Primeras 4 semanas del curso**
6. ☕ Java para Spring Boot — módulos 04 – 12 (POO, colecciones, lambdas)
7. 📚 Conceptos de Programación — módulos 02, 07 (paradigmas, principios de buen código)
8. 📦 Maven
9. 🔐 Variables de Entorno
10. 🌿 Git y GitHub

**Cuando el ritmo del curso lo permita**
11. 🧠 Lógica Proposicional
12. 🧱 SOLID + 📊 Richardson
13. 🏋️ Ejercicios Prácticos (Serie I)

> 📌 Esta ruta asume que vas resolviendo los `Tickets-N/` en paralelo. Los extras refuerzan lo que ya estás viendo en clase.

---

### 🐣 Ruta Desde Cero — Sin conocimientos de programación
> **Para quién:** nunca has programado y este curso es tu punto de partida.
> **Tiempo total:** 51 – 66 h
> **Objetivo:** construir una base sólida antes de tocar Spring Boot.

**Fase 0 — Conocer tu computador** `~1 h`
1. 🗂️ Uso de PC — sistema de archivos, rutas, dónde viven los proyectos (obligatorio antes de abrir IntelliJ)

**Fase 1 — Pensamiento computacional y vocabulario** `~12 h`
2. 💡 Tips de Programación — cómo razonar problemas
3. 📚 Conceptos de Programación — módulos 01, 02, 03, 04 (vocabulario, paradigmas, estructuras de datos, errores)
4. 🧠 Lógica Proposicional — condiciones y validaciones
5. ➕ Matemáticas para Programar — operadores, contadores, redondeo

**Fase 2 — Lenguaje base** `~14 h`
6. ☕ Java para Spring Boot — TODOS los módulos (00 – 12), sin saltar
7. 🖥️ Terminal — operaciones básicas
8. 📝 Markdown — para documentar tu trabajo

**Fase 3 — Formatos y configuración** `~6 h`
9. 🔷 JSON
10. 📄 YAML
11. 📦 Maven
12. 🛠️ Lombok
13. 🔐 Variables de Entorno

**Fase 4 — Versionado y diseño** `~10 h`
14. 🌿 Git y GitHub
15. 📚 Conceptos de Programación — módulos 05, 06, 07 (recursividad, Big O, principios de buen código)
16. 🧱 SOLID
17. 📊 Richardson

**Fase 5 — Práctica deliberada** `~5 – 10 h`
18. 🏋️ Ejercicios Prácticos (Serie I, luego Serie II)

> 🛟 No avances de fase si no entendiste la anterior. Cada base sostiene la siguiente — saltar etapas se paga después.

---

### 🔁 Ruta Refresh Java — Tuve Java alguna vez
> **Para quién:** estudiaste Java hace años y necesitas reactivarlo sin partir de cero.
> **Tiempo total:** 13 – 17 h
> **Objetivo:** recordar lo esencial y aprender lo que cambió (records, lambdas, streams).

**Refresh rápido** `~3 h`
1. ☕ Java para Spring Boot — módulos 00 – 03 (lectura rápida)
2. ☕ Java para Spring Boot — módulos 04 – 07 (POO)

**Probablemente lo nuevo para ti** `~5 h`
3. ☕ Java para Spring Boot — módulos 08 – 12 (colecciones modernas, lambdas, streams, generics)
4. 📚 Conceptos de Programación — módulos 07, 10 (principios modernos, memoria/GC explicada bien)
5. 🛠️ Lombok — convención que no existía cuando aprendiste

**Configuración y formato** `~4 h`
6. 🔷 JSON
7. 📄 YAML
8. 📦 Maven
9. 🔐 Variables de Entorno

**Si te sobra tiempo**
10. 📚 Conceptos de Programación — módulos 11, 12, 13 (patrones de diseño GoF)
11. 🧱 SOLID — repaso de diseño
12. 🏋️ Ejercicios Prácticos (Serie I) — para confirmar que recordaste

> ⏭️ Si al revisar los módulos 00 – 07 sientes que todo es familiar, sáltalos. El verdadero refresh está en el 08 – 12.

---

### 🌐 Ruta Java → Web — Sé Java pero no sé nada de internet
> **Para quién:** dominas Java pero nunca has trabajado con HTTP, APIs, JSON o servidores web.
> **Tiempo total:** 11 – 15 h
> **Objetivo:** entender cómo Java se conecta con el mundo web.

**Puedes saltarte sin culpa**
- ☕ Java para Spring Boot — solo revisa módulos 11 – 12 si no manejas lambdas/streams
- 🧠 Lógica Proposicional · ➕ Matemáticas — ya las dominas

**Lo que sí necesitas** `~7 h`
1. 🔷 JSON — formato base de toda comunicación REST
2. 📄 YAML — configuración declarativa de Spring
3. 📦 Maven — gestión de dependencias y ciclo de build
4. 🔐 Variables de Entorno — separar configuración del código
5. 🛠️ Lombok — convención estándar del ecosistema Spring
6. 📊 Richardson — niveles de madurez REST (clave conceptual para diseñar bien)
7. 🧱 SOLID — diseño en capas Controller / Service / Repository

**Conceptos de backend** `~3 h`
8. 📚 Conceptos de Programación — módulos 08, 09 (concurrencia/hilos, I/O bloqueante vs no bloqueante)
9. 📚 Conceptos de Programación — módulos 11, 12, 13 (patrones de diseño que verás en Spring)

**Entorno profesional** `~3 h`
10. 🌿 Git y GitHub — flujo de trabajo
11. 🖥️ Terminal — operar el proyecto desde consola

> 🌐 Tu mayor brecha no es Java, es entender HTTP, REST y cómo se serializa todo. Prioriza JSON + Richardson.

---

### 🚀 Ruta Spring Pro — Ya tengo experiencia con Spring Boot
> **Para quién:** has construido APIs con Spring Boot antes y solo necesitas alinearte con este repo.
> **Tiempo total:** 2 – 4 h
> **Objetivo:** ubicarte rápido en el código y detectar las particularidades del proyecto.

**Lectura obligatoria** `~30 min`
1. `CLAUDE.md` y `AGENTS.md` — convenciones del repo (paquete `respository` con typo intencional, etc.)
2. README de cada `Tickets-N/` — qué cambia entre lecciones

**Extras que vale la pena revisar** `~2 – 3 h`
3. 📊 Richardson — para alinear vocabulario REST con el del curso
4. 🌊 GitFlow — si vas a contribuir o entregar
5. 🧱 SOLID — repaso rápido de diseño en capas
6. 🏋️ Ejercicios Prácticos (Serie II) — desafíos no triviales

> ⏭️ Omite el Nivel 1 completo salvo que detectes algo desconocido. Usa el repo como referencia, no como tutorial.

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

## Cómo usar este repositorio

Este repositorio contiene el material de apoyo del curso, organizado en dos grandes áreas:

1. **Proyectos progresivos** (`Tickets/`, `Tickets-10/`, ..., `Tickets-18/`) — cada carpeta es una lección独立性
2. **Extras** (`docs/extras/`) — material teóricos opcionales

### Según tu nivel

#### 🐣 Nuevo en programación
> No has programado antes o llevas poco tiempo.

Usa los proyectos en orden. Cada `Tickets-N/` incluye TODO lo necesario para esa lección:
- Clases completas con comentarios
- Tests para verificar
- Configuración lista para ejecutar

**Extras requeridos:**
- ☕ Java para Spring Boot (módulos 00 – 07)
- 🔷 JSON
- 📄 YAML
- 🛠️ Lombok

**Extras opcionales:**
- 🧠 Lógica Proposicional
- ➕ Matemáticas para Programar
- 🖥️ Terminal

```
Secuencia sugerida:
Tickets/   → Tickets-10/ → ... → Tickets-18/
   (base)      (JPA)            (Exceptions)
```

---

#### 🔰 Principiante
> Ya conoces lo básico de Java u otro lenguaje.

Puedes saltar directamente a la lección que necesitas:
- ¿Necesitas JPA? → `Tickets-10/`
- ¿Necesitas Security? → `Tickets-16/`
- ¿Necesitas Microservicios? → `Tickets-15/`
- ¿Necesitas Exception Handling? → `Tickets-18/`

**Extras requeridos:**
- 📄 YAML
- 📦 Maven
- 🔐 Variables de Entorno

**Extras opcionales:**
- 💡 Tips de Programación
- 🧱 SOLID
- 📊 Richardson

---

#### 🚀 Avanzado
> Ya has trabajado con Spring Boot antes.

Usa este repo como referencia rápida:
- Busca el patrón que necesitas en los proyectos
- Revisa los extras para profundizar
- Omitir lo que ya dominas

**Extras opcionales:**
- 🌊 GitFlow
- 🏋️ Ejercicios Prácticos (Serie II)
- 🧱 SOLID

---

### Estructura de cada proyecto

Cada carpeta `Tickets-N/` sigue la misma estructura:

```
Tickets-N/
├── src/main/java/           # Código fuente
│   └── cl/duoc/fullstack/
│       ├── controller/      # Endpoints REST
│       ├── service/        # Lógica de negocio
│       ├── repository/     # Acceso a datos
│       ├── model/         # Entidades
│       ├── dto/           # Objetos de transferencia
│       └── config/        # Configuración
├── src/main/resources/
│   ├── application.yml    # Configuración principal
│   ├── application-h2.yml
│   └── db/migration/    # Flyway migrations
└── README.md           # Cambios de la lección
```

---

### Comandos comunes

```bash
# Ejecutar proyecto (desde su directorio)
cd Tickets-10
mvnw.cmd spring-boot:run

# Compilar
mvnw.cmd compile

# Ejecutar tests
mvnw.cmd test
```

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
