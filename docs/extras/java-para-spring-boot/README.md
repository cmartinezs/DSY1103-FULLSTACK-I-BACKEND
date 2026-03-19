# ☕ Mini Curso: Java para Spring Boot

> **Nivel de entrada:** cualquiera — el módulo 00 es un repaso rápido para quienes ya recuerdan Java; los módulos 01-11 parten desde la sintaxis básica.  
> **Objetivo:** llegar al nivel mínimo necesario para entender y escribir código Spring Boot con soltura.  
> **Versión objetivo:** Java 21 (LTS).

---

## ¿Cómo usar este mini curso?

1. **¿Ya recuerdas Java y POO?** Lee el [módulo 00](./00_repaso_rapido.md) como cheat sheet de referencia rápida y luego dirígete al módulo que necesites.  
2. **¿No recuerdas bien Java o llevas tiempo sin practicarlo?** Empieza por el [módulo 01](./01_conceptos_base.md) y sigue el orden hasta el 12.  
3. **Lee el código, no solo lo escanees.** Pregunta "¿por qué?" en cada línea.  
4. Realiza los ejercicios al final de cada módulo **antes** de ver la solución.  
5. El módulo 12 cierra con un puente directo a Spring Boot.

---

## Índice de módulos

| # | Tema | Archivo |
|---|------|---------|
| 00 | Repaso rápido / Cheat Sheet (ya recuerdas Java) | [`00_repaso_rapido.md`](./00_repaso_rapido.md) |
| 01 | Conceptos base: variables, operadores, bucles, parámetros | [`01_conceptos_base.md`](./01_conceptos_base.md) |
| 02 | Sintaxis esencial y tipos de datos | [`02_sintaxis_y_tipos.md`](./02_sintaxis_y_tipos.md) |
| 03 | Control de flujo moderno + operadores lógicos y buenas prácticas en condiciones | [`03_control_de_flujo.md`](./03_control_de_flujo.md) |
| 04 | Métodos: diseño y sobrecarga | [`04_metodos.md`](./04_metodos.md) |
| 05 | Clases, objetos y constructores | [`05_clases_y_objetos.md`](./05_clases_y_objetos.md) |
| 06 | Los 4 pilares de la POO aplicados | [`06_poo_pilares.md`](./06_poo_pilares.md) |
| 07 | Interfaces y clases abstractas | [`07_interfaces_y_abstraccion.md`](./07_interfaces_y_abstraccion.md) |
| 08 | Colecciones y Genéricos | [`08_colecciones_y_genericos.md`](./08_colecciones_y_genericos.md) |
| 09 | Manejo de excepciones | [`09_excepciones.md`](./09_excepciones.md) |
| 10 | Lambdas y Streams | [`10_lambdas_y_streams.md`](./10_lambdas_y_streams.md) |
| 11 | Novedades de Java 21 | [`11_java21.md`](./11_java21.md) |
| 12 | El camino a Spring Boot | [`12_camino_a_spring_boot.md`](./12_camino_a_spring_boot.md) |
| 13 | 📖 Glosario completo de palabras reservadas | [`13_glosario_palabras_reservadas.md`](./13_glosario_palabras_reservadas.md) |

---

## Hoja de ruta visual

```
Java 21
│
├── 00 Repaso rápido / Cheat Sheet ──────────────┐  Para quien ya
│   (4 pilares, Optional, Streams, Records...)    │  recuerda Java
│                                                 ┘
├── 01 Conceptos base ────────────────────────────┐
│   (variables, asignación, operadores,            │
│    sentencias, bloques, bucles, parámetros)      │  Fundamentos
│                                                 ┘
├── 02 Sintaxis básica ──────────────────────────┐
├── 03 Control de flujo (switch expr., sealed)    │
│   + operadores lógicos y buenas prácticas       │  Base del lenguaje
├── 04 Métodos y sobrecarga                       │
├── 05 Clases, objetos, records                  │
│                                                 ┘
├── 06 POO: Encapsulamiento, Herencia,
│        Polimorfismo, Abstracción ───────────────┐
├── 07 Interfaces + clases abstractas             │  POO aplicada
│                                                 ┘
├── 08 Colecciones (List, Map, Optional) ─────────┐
├── 09 Excepciones (checked, unchecked)            │  Herramientas
├── 10 Lambdas y Streams                          │  del día a día
│                                                 ┘
├── 11 Java 21 (text blocks, pattern matching,
│        virtual threads, sequenced collections)
│
└── 12 🚀 Spring Boot ─────────────────────────── @RestController
                                                   @Service
                                                   @Repository
```

---

## ¿Qué necesitas tener instalado?

| Herramienta | Versión mínima | Verificar con |
|-------------|---------------|---------------|
| JDK | 21 | `java -version` |
| IntelliJ IDEA | Community o Ultimate | — |
| Maven | 3.9+ (incluido en IntelliJ) | `mvn -version` |

---

*[← Volver a Extras](../README.md)*

