# 🖥️ Uso de PC para Programadores

> **Nivel de entrada:** sin requisitos — este extra es para quienes empiezan desde cero o tienen dudas básicas sobre cómo manejar su computador como desarrollador.  
> **Objetivo:** entender el sistema de archivos, saber dónde se guardan los proyectos y manejar el PC con confianza para programar.  
> **Contexto:** Windows 10/11 y Linux (Ubuntu / Mint / cualquier distribución con escritorio).

---

## ¿Por qué importa saber usar el PC como programador?

Muchos estudiantes abren IntelliJ IDEA, crean un proyecto, escriben código… y no saben dónde está ese código en su computador.

Esto genera problemas reales:

- No encuentran el proyecto para subirlo a GitHub.
- Borran carpetas sin querer y pierden trabajo.
- Descomprimen proyectos en el lugar equivocado.
- Tienen 10 copias del mismo proyecto en distintas ubicaciones.
- Les piden la ruta del archivo y no saben ni qué es una ruta.

> 💡 Un programador que no entiende cómo funciona el sistema de archivos de su propio computador tiene una desventaja enorme desde el primer día.

---

## Dos guías, los mismos conceptos

Este extra cubre exactamente los mismos conceptos en dos guías paralelas:

| Guía | Sistema | Archivo |
|------|---------|---------|
| 🪟 [Windows 10 / 11](./windows.md) | File Explorer, rutas con `\`, disco `C:\` | `windows.md` |
| 🐧 [Linux (Ubuntu / Mint)](./linux.md) | Administrador de archivos, rutas con `/`, carpeta `home` | `linux.md` |

> 💡 Aunque uses solo un sistema, leer ambas guías ayuda a entender las diferencias y a no confundirse cuando ves rutas de un sistema distinto en tutoriales o en el trabajo.

---

## Conceptos cubiertos (en ambas guías)

| # | Concepto |
|---|----------|
| 1 | ¿Qué es el sistema de archivos? |
| 2 | Carpetas, archivos y extensiones |
| 3 | Rutas absolutas y relativas |
| 4 | ¿Dónde está mi carpeta de usuario (`home`)? |
| 5 | **¿Dónde se guardan los proyectos de IntelliJ?** |
| 6 | Cómo encontrar un proyecto desde el explorador de archivos |
| 7 | Crear, renombrar, mover y eliminar archivos y carpetas |
| 8 | Mostrar archivos ocultos y extensiones de archivo |
| 9 | Buscar archivos y carpetas |
| 10 | Organización recomendada: dónde guardar tus proyectos |
| 11 | Descomprimir un proyecto (`.zip` de Spring Initializr) |
| 12 | Atajos de teclado esenciales |
| 13 | Referencia rápida (cheat sheet) |

---

## Errores más comunes que se evitan con este extra

| ❌ Error frecuente | ✅ Lo que hay que hacer |
|-------------------|------------------------|
| Guardar proyectos en el Escritorio | Crear una carpeta `Proyectos/` en `Documentos` o en `home` |
| Guardar proyectos en `Descargas` | Mover el proyecto a una ubicación fija y organizada |
| No saber dónde está el proyecto | Aprende a leer la ruta en IntelliJ y en el explorador |
| Abrir archivos `.java` sueltos | Siempre abrir la **carpeta raíz del proyecto** en IntelliJ |
| Copiar carpetas con nombre "Copia de..." | Usar Git para versionar, no duplicar carpetas |
| Guardar en OneDrive / Google Drive | Puede causar errores con Maven — mejor fuera de carpetas sincronizadas |
| Descomprimir sobre el ZIP | Extraer siempre en una carpeta nueva y clara |

---

## Conexión con el curso

| Situación en el curso | Concepto de este extra que necesitas |
|-----------------------|-------------------------------------|
| Crear el primer proyecto Spring Boot | Saber dónde guardarlo y con qué nombre |
| Subir el proyecto a GitHub | Saber la ruta completa del proyecto |
| Abrir un proyecto clonado | Abrir la carpeta raíz, no un archivo suelto |
| Descomprimir proyecto de start.spring.io | Extraer en la carpeta correcta |
| Compartir el proyecto con el profesor | Comprimir la carpeta correcta (sin `target/`) |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

