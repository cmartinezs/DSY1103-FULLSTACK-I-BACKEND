# Lección 00 - Tutorial paso a paso: Git & GitHub

## Paso 1: Instalar Git

**Windows:**
```bash
choco install git
# o descargar de https://git-scm.com
```

**Verificar:**
```bash
git --version
```

## Paso 2: Configuración global

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
git config --global core.autocrlf true  # Windows
```

## Paso 3: Crear repositorio local

En tu carpeta del proyecto:

```bash
cd C:\Users\tu\IdeaProjects\DSY1103-FULLSTACK-I-BACKEND\Tickets
git init
```

Resultado: aparece carpeta `.git/` (oculta).

## Paso 4: Crear .gitignore

Archivo `Tickets/.gitignore`:

```
target/
.idea/
*.class
*.jar
.env
.DS_Store
node_modules/
```

## Paso 5: Primer commit

```bash
git add .                    # Preparar todos los cambios
git commit -m "Setup inicial"   # Primer snapshot
```

## Paso 6: Crear cuenta GitHub

1. Ir a https://github.com/signup
2. Crear cuenta
3. Verificar email

## Paso 7: Crear repo en GitHub

1. Click "+New repository"
2. Nombre: `DSY1103-FULLSTACK-I-BACKEND`
3. Descripción: `Curso Spring Boot - Sistema de Tickets`
4. Public (para que vea profesor)
5. Crear

GitHub te da comandos. Ejecuta:

```bash
git remote add origin https://github.com/tu-usuario/DSY1103-FULLSTACK-I-BACKEND.git
git branch -M main
git push -u origin main
```

## Paso 8: Hacer cambios y push

```bash
# Editas algo...
git add .
git commit -m "Agregar endpoint /health"
git push
```

En GitHub verás los cambios automáticamente.

## Paso 9: Crear rama de feature

```bash
git checkout -b feature/nuevaFuncion
# Haces cambios...
git add .
git commit -m "Implementar login"
git push origin feature/nuevaFuncion
```

## Paso 10: Pull Request

En GitHub:
1. Click "Compare & pull request"
2. Escribe descripción
3. Click "Create pull request"
4. Revisor aprueba
5. Click "Merge pull request"

main ahora incluye tus cambios.
