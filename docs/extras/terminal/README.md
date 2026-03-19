# 🖥️ Terminal — Línea de Comandos

## ¿Por qué importa la terminal?

Todo servidor de producción corre **Linux sin interfaz gráfica**. Saber moverse en una terminal no es opcional para un desarrollador backend: es la diferencia entre poder deployar una aplicación o depender de que alguien más lo haga.

Además, herramientas como **Git**, **Maven**, **Docker** y el propio **Spring Boot** se usan principalmente desde la terminal.

---

## Dos mundos, los mismos conceptos

Este material cubre exactamente los mismos conceptos en dos guías paralelas, una para cada sistema operativo:

| Guía | Sistema | Shell | Archivo |
|------|---------|-------|---------|
| 🐧 [Bash](./bash.md) | Linux / macOS | `bash` / `zsh` | `bash.md` |
| 🪟 [CMD y PowerShell](./windows.md) | Windows | `cmd.exe` / `pwsh` | `windows.md` |

> 💡 **Recomendación:** aunque uses Windows en el día a día, estudia también la guía de Bash. Los servidores donde vivirán tus APIs son Linux.

---

## Conceptos cubiertos (en ambas guías)

| # | Concepto |
|---|----------|
| 1 | ¿Qué es una terminal y un shell? |
| 2 | Navegación: rutas absolutas y relativas |
| 3 | Listar contenido de un directorio |
| 4 | Crear, mover, copiar y eliminar archivos y carpetas |
| 5 | Leer archivos desde la terminal |
| 6 | Variables de entorno |
| 7 | Redirección y pipes |
| 8 | Búsqueda de texto en archivos |
| 9 | Permisos de archivos (ejecución de scripts) |
| 10 | Comandos de red: `ping`, `curl` |
| 11 | Atajos y productividad |
| 12 | Referencia rápida (cheat sheet) |

---

## Conexión con el curso

| Situación en el curso | Comando que necesitas |
|-----------------------|-----------------------|
| Ejecutar el proyecto | `./mvnw spring-boot:run` (Bash) · `.\mvnw spring-boot:run` (PowerShell) |
| Correr los tests | `./mvnw test` |
| Ver variables de entorno | `printenv` (Bash) · `$env:VAR` (PowerShell) |
| Descargar con curl | `curl http://localhost:8080/api/v1/tickets` |
| Buscar texto en logs | `grep "ERROR" app.log` (Bash) · `Select-String` (PowerShell) |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

