# 🐙 Git y GitHub

## ¿Qué es Git?
Git es un **sistema de control de versiones distribuido** que permite registrar el historial de cambios de un proyecto, colaborar con otros desarrolladores y mantener múltiples versiones del código de forma organizada.

## ¿Qué es GitHub?
GitHub es una **plataforma en la nube** que aloja repositorios Git y ofrece herramientas para la colaboración: pull requests, issues, wikis, GitHub Actions (CI/CD), entre otros.

---

## Conceptos clave

| Concepto | Descripción |
|---|---|
| `repository` | Proyecto versionado con Git |
| `commit` | Instantánea de cambios registrada en el historial |
| `branch` | Rama de desarrollo independiente |
| `merge` | Integración de cambios entre ramas |
| `pull request` | Solicitud de revisión e integración de cambios (GitHub) |
| `clone` | Copia local de un repositorio remoto |
| `push` | Subir cambios locales al repositorio remoto |
| `pull` | Obtener y fusionar cambios del repositorio remoto |
| `.gitignore` | Archivo que indica a Git qué archivos ignorar |
| `HEAD` | Puntero al commit actual en la rama activa |
| `staging area` | Zona intermedia antes de confirmar un commit |
| `remote` | Referencia al repositorio remoto (ej. `origin`) |
| `tag` | Etiqueta fija a un commit, usada para versiones |

---

## Flujo de trabajo básico

```bash
# 1. Inicializar un repositorio
git init

# 2. Clonar un repositorio existente
git clone https://github.com/usuario/repositorio.git

# 3. Ver el estado de los archivos
git status

# 4. Agregar cambios al área de staging
git add .
git add src/archivo.java   # agregar un archivo específico

# 5. Crear un commit
git commit -m "feat: agrega endpoint de creación de tickets"

# 6. Subir cambios al repositorio remoto
git push origin main

# 7. Crear y cambiar a una nueva rama
git checkout -b feature/nueva-funcionalidad

# 8. Obtener cambios del repositorio remoto
git pull origin main

# 9. Ver el historial de commits
git log --oneline --graph
```

---

## Trabajo con ramas (branching)

Las ramas permiten desarrollar funcionalidades de forma aislada sin afectar la rama principal.

```bash
# Crear una nueva rama
git branch feature/login

# Cambiar a una rama existente
git checkout feature/login

# Crear y cambiar en un solo paso (forma moderna)
git switch -c feature/login

# Listar ramas
git branch -a

# Fusionar una rama en la rama actual
git merge feature/login

# Eliminar una rama local
git branch -d feature/login

# Eliminar una rama remota
git push origin --delete feature/login
```

### Estrategia de ramas recomendada (Git Flow simplificado)

```
main          → código estable y desplegado
develop       → integración de funcionalidades
feature/*     → nuevas funcionalidades
fix/*         → corrección de errores
```

---

## Resolución de conflictos

Un **conflicto** ocurre cuando dos ramas modifican las mismas líneas de un archivo.

```bash
# Git marcará los conflictos en el archivo:
<<<<<<< HEAD
    código de tu rama actual
=======
    código de la otra rama
>>>>>>> feature/login

# Pasos para resolver:
# 1. Editar el archivo manualmente y elegir qué código conservar
# 2. Marcar como resuelto
git add archivo-con-conflicto.java
# 3. Completar el merge
git commit
```

---

## Comandos útiles adicionales

```bash
# Ver diferencias entre archivos
git diff

# Deshacer cambios en un archivo (antes del staging)
git checkout -- archivo.java

# Quitar un archivo del staging
git reset HEAD archivo.java

# Modificar el último commit (solo si no fue pusheado)
git commit --amend -m "nuevo mensaje"

# Guardar cambios temporalmente sin commitear
git stash
git stash pop     # recuperar los cambios guardados

# Ver qué cambió en un commit específico
git show <hash-del-commit>

# Revertir un commit (sin borrar el historial)
git revert <hash-del-commit>
```

---

## Convención de commits (Conventional Commits)

Se recomienda usar el estándar [Conventional Commits](https://www.conventionalcommits.org/) para mantener un historial legible y compatible con herramientas de automatización (changelogs, versionado semántico, etc.).

### Formato

```
<tipo>(alcance opcional): descripción corta

[cuerpo opcional]

[footer opcional]
```

### Tipos comunes

| Tipo | Cuándo usarlo |
|------|--------------|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de un error |
| `docs` | Cambios en documentación |
| `refactor` | Refactorización sin cambio funcional |
| `test` | Agrega o modifica pruebas |
| `chore` | Tareas de mantenimiento, dependencias |
| `style` | Cambios de formato (espacios, punto y coma) |
| `perf` | Mejoras de rendimiento |
| `ci` | Cambios en pipelines de CI/CD |

### Ejemplos

```bash
git commit -m "feat(tickets): agrega endpoint para cerrar tickets"
git commit -m "fix(auth): corrige validación de token expirado"
git commit -m "docs: actualiza README con instrucciones de instalación"
git commit -m "refactor(service): extrae lógica de negocio al servicio"
```

---

## GitHub: funcionalidades clave

### Pull Request (PR)
Un PR es una solicitud para fusionar cambios de una rama a otra. Permite revisión de código antes de integrar.

**Buenas prácticas:**
- Títulos claros usando Conventional Commits
- Descripción del cambio y por qué se hace
- Al menos un revisor asignado
- PRs pequeños y enfocados en un solo tema

### Issues
Los issues permiten registrar tareas, bugs y mejoras. Se pueden vincular a PRs con palabras clave:

```
Closes #42
Fixes #15
Resolves #8
```

### GitHub Actions (CI/CD básico)
Permite automatizar tareas como compilar, testear y desplegar al hacer push o abrir un PR.

```yaml
# .github/workflows/build.yml
name: Build Java
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
      - run: ./mvnw test
```

---

## Archivo `.gitignore`

El `.gitignore` evita que archivos innecesarios o sensibles sean rastreados por Git.

```gitignore
# Compilados Java
target/
*.class

# IDE
.idea/
*.iml
.vscode/

# Variables de entorno
.env
*.env

# Logs
*.log
logs/
```

> 💡 Puedes generar `.gitignore` personalizados en [gitignore.io](https://www.toptal.com/developers/gitignore)

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| Pro Git Book | 📖 Libro (gratis) | [git-scm.com/book/es](https://git-scm.com/book/es/v2) |
| Learn Git Branching | 🎮 Interactivo | [learngitbranching.js.org](https://learngitbranching.js.org/?locale=es_ES) |
| GitHub Docs | 📄 Documentación | [docs.github.com/es](https://docs.github.com/es) |
| Conventional Commits | 📄 Estándar | [conventionalcommits.org](https://www.conventionalcommits.org/es/v1.0.0/) |
| gitignore.io | 🛠️ Herramienta | [toptal.com/developers/gitignore](https://www.toptal.com/developers/gitignore) |

---

*[← Volver a Extras](../README.md)*

