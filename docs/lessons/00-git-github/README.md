# Lección 00 - Git & GitHub: Control de Versiones

## ¿Qué es Git?

Sistema de control de versiones que registra cambios en tu código. Sin Git:

```
proyecto.zip
proyecto_v2.zip
proyecto_v2_final.zip
proyecto_v3_REAL_FINAL.zip  ← ¿Cuál es la verdadera?
```

Con Git:

```
commit 1: Setup inicial
commit 2: Agregar funcionalidad X
commit 3: Bugfix en X
commit 4: Revertir cambios de X

Todo rastreable, reversible, colaborativo.
```

---

## Quick Start

### Instalación

```bash
# Windows
choco install git
# o descargar: https://git-scm.com

# Linux/macOS
brew install git
```

### Configuración inicial

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

---

## Conceptos clave

- **Repository:** Carpeta con historial de cambios
- **Commit:** Foto del código en un momento específico
- **Branch:** Línea paralela de desarrollo
- **Push:** Enviar cambios a servidor (GitHub)
- **Pull:** Traer cambios del servidor

---

## Flujo básico

```
1. git init          ← Inicializar repo local
2. git add .         ← Preparar cambios
3. git commit -m "msg"  ← Grabar cambios
4. git push          ← Enviar a GitHub
```

---

## Próxima: Lección 1

**L01 - Web y HTTP:** Conceptos fundamentales de la web
