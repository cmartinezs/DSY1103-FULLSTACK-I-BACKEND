# ☕ Mini Curso: Java para Spring Boot

> **Nivel de entrada:** cualquiera — el módulo 00 es un repaso rápido para quienes ya recuerdan Java; los módulos 01-11 parten desde la sintaxis básica.  
> **Objetivo:** llegar al nivel mínimo necesario para entender y escribir código Spring Boot con soltura.  
> **Versión objetivo:** Java 21 (LTS).

---

## ¿Cómo usar este mini curso?

1. **¿Ya recuerdas Java y POO?** Lee el [módulo 00](./00_repaso_rapido.md) como cheat sheet de referencia rápida y luego dirígete al módulo que necesites.  
2. **¿No recuerdas bien Java o llevas tiempo sin practicarlo?** Sigue los módulos **01 al 11 en orden**: cada uno construye sobre el anterior.  
3. **Lee el código, no solo lo escanees.** Pregunta "¿por qué?" en cada línea.  
4. Realiza los ejercicios al final de cada módulo **antes** de ver la solución.  
5. El módulo 11 cierra con un puente directo a Spring Boot.

---

## Índice de módulos

| # | Tema | Archivo |
|---|------|---------|
| 00 | Repaso rápido / Cheat Sheet (ya recuerdas Java) | [`00_repaso_rapido.md`](./00_repaso_rapido.md) |
| 01 | Sintaxis esencial y tipos de datos | [`01_sintaxis_y_tipos.md`](./01_sintaxis_y_tipos.md) |
| 02 | Control de flujo moderno | [`02_control_de_flujo.md`](./02_control_de_flujo.md) |
| 03 | Métodos: diseño y sobrecarga | [`03_metodos.md`](./03_metodos.md) |
| 04 | Clases, objetos y constructores | [`04_clases_y_objetos.md`](./04_clases_y_objetos.md) |
| 05 | Los 4 pilares de la POO aplicados | [`05_poo_pilares.md`](./05_poo_pilares.md) |
| 06 | Interfaces y clases abstractas | [`06_interfaces_y_abstraccion.md`](./06_interfaces_y_abstraccion.md) |
| 07 | Colecciones y Genéricos | [`07_colecciones_y_genericos.md`](./07_colecciones_y_genericos.md) |
| 08 | Manejo de excepciones | [`08_excepciones.md`](./08_excepciones.md) |
| 09 | Lambdas y Streams | [`09_lambdas_y_streams.md`](./09_lambdas_y_streams.md) |
| 10 | Novedades de Java 21 | [`10_java21.md`](./10_java21.md) |
| 11 | El camino a Spring Boot | [`11_camino_a_spring_boot.md`](./11_camino_a_spring_boot.md) |

---

## Hoja de ruta visual

```
Java 21
│
├── 00 Repaso rápido / Cheat Sheet ──────────────┐  Para quien ya
│   (4 pilares, Optional, Streams, Records...)    │  recuerda Java
│                                                 ┘
├── 01 Sintaxis básica ──────────────────────────┐
├── 02 Control de flujo (switch expr., sealed)    │
├── 03 Métodos y sobrecarga                       │  Base del lenguaje
├── 04 Clases, objetos, records                  │
│                                                 ┘
├── 05 POO: Encapsulamiento, Herencia,
│        Polimorfismo, Abstracción ───────────────┐
├── 06 Interfaces + clases abstractas             │  POO aplicada
│                                                 ┘
├── 07 Colecciones (List, Map, Optional) ─────────┐
├── 08 Excepciones (checked, unchecked)            │  Herramientas
├── 09 Lambdas y Streams                          │  del día a día
│                                                 ┘
├── 10 Java 21 (text blocks, pattern matching,
│        virtual threads, sequenced collections)
│
└── 11 🚀 Spring Boot ─────────────────────────── @RestController
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

