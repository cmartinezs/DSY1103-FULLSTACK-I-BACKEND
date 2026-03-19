# 🐧 Uso de PC para Programadores — Linux (Ubuntu / Mint)

> **Sistema:** Linux con escritorio — Ubuntu, Linux Mint, Fedora, Pop!_OS u otras distros similares  
> **Herramienta principal:** Administrador de archivos (Nautilus en Ubuntu, Nemo en Mint, Dolphin en KDE)  
> **Carpeta raíz:** `/`

---

## 1. ¿Qué es el sistema de archivos?

El **sistema de archivos** en Linux se organiza como un **árbol único** que parte desde la raíz `/`. No hay letras de disco (`C:\`, `D:\`) como en Windows — todo está bajo un solo árbol.

```
/                           ← Raíz del sistema (root)
├── home/                   ← Carpetas de usuarios
│   └── tunombre/           ← TU carpeta de usuario (home)
│       ├── Escritorio/     ← Escritorio (Desktop)
│       ├── Documentos/     ← Documents
│       ├── Descargas/      ← Downloads
│       ├── Imágenes/       ← Pictures
│       └── IdeaProjects/   ← ← ← Proyectos de IntelliJ (por defecto)
├── usr/                    ← Programas del sistema
├── etc/                    ← Archivos de configuración del sistema
├── var/                    ← Logs y datos variables
└── tmp/                    ← Archivos temporales
```

> 💡 En Linux la barra que separa carpetas es `/` (barra normal). La raíz del sistema es `/`, y tu carpeta personal está en `/home/tunombre/`.

---

## 2. Carpetas, archivos y extensiones

| Concepto | Descripción | Ejemplo |
|----------|-------------|---------|
| **Carpeta (directorio)** | Contenedor que agrupa archivos o más carpetas | `mi-proyecto/` |
| **Archivo** | Unidad de información con nombre y extensión | `Main.java` |
| **Extensión** | Sufijo del nombre que indica el tipo de archivo | `.java` `.xml` `.yml` `.md` |
| **Archivo oculto** | Nombre que empieza con `.` — no se muestra por defecto | `.gitignore` `.env` `.git/` |

> 🐧 En Linux, a diferencia de Windows, las extensiones **no son obligatorias** para el sistema operativo — un archivo puede ejecutarse sin extensión. Pero en programación siempre se usan.

### Extensiones frecuentes en programación

| Extensión | Tipo | Para qué sirve |
|-----------|------|----------------|
| `.java` | Código fuente | Clases Java |
| `.xml` | Configuración | `pom.xml` (Maven) |
| `.yml` / `.yaml` | Configuración | `application.yml` (Spring Boot) |
| `.properties` | Configuración | `application.properties` |
| `.json` | Datos | Respuestas de API, configuraciones |
| `.md` | Documentación | `README.md` |
| `.zip` | Comprimido | Proyecto descargado de start.spring.io |
| `.jar` | Ejecutable Java | Resultado del build de Maven |
| `.sh` | Script | Script de shell ejecutable |

### Mostrar archivos ocultos y extensiones

```
En el Administrador de archivos (Nautilus / Nemo / Dolphin):

Mostrar archivos ocultos:
  Atajo: Ctrl + H
  O menú: Ver → Mostrar archivos ocultos

Mostrar extensiones:
  En Linux las extensiones SIEMPRE son visibles — no hay que activarlas.
```

---

## 3. Rutas absolutas y relativas

### Ruta absoluta

Parte desde `/` (la raíz del sistema). Siempre lleva al mismo lugar sin importar dónde estés.

```
/home/tunombre/IdeaProjects/mi-proyecto/src/main/java/Main.java
```

### Ruta relativa

Parte desde tu ubicación actual.

```bash
# Si estás en /home/tunombre/IdeaProjects/mi-proyecto/
src/main/java/Main.java

# Subir un nivel
../otro-proyecto/

# Carpeta actual
./src/
```

### Símbolos especiales de rutas en Linux

| Símbolo | Significado | Ejemplo |
|---------|-------------|---------|
| `/` | Raíz del sistema | `/home/tunombre/` |
| `~` | Tu carpeta home | `~/IdeaProjects/` = `/home/tunombre/IdeaProjects/` |
| `.` | Carpeta actual | `./src/` |
| `..` | Carpeta padre (un nivel arriba) | `../` |

> 💡 `~` es un atajo muy usado. `~/IdeaProjects/` significa exactamente `/home/tunombre/IdeaProjects/` — es la misma ruta.

---

## 4. Tu carpeta de usuario (home)

En Linux tu carpeta de usuario (equivalente al `C:\Users\TuNombre\` de Windows) es:

```
/home/tunombre/
```

Se puede acceder de varias formas:

```
Opción 1: Escribir ~ en la terminal: cd ~
Opción 2: En el Administrador de archivos → clic en "Inicio" o "Home" en el panel izquierdo
Opción 3: Navegar manualmente: / → home → tunombre
Opción 4: En terminal: echo $HOME  (muestra la ruta completa)
```

---

## 5. ¿Dónde se guardan los proyectos de IntelliJ IDEA?

### Ubicación por defecto

Cuando creas un proyecto en IntelliJ IDEA, **por defecto se guarda en**:

```
/home/tunombre/IdeaProjects/nombre-del-proyecto/
```

O en notación corta:

```
~/IdeaProjects/nombre-del-proyecto/
```

> ⚠️ Esta carpeta la crea IntelliJ automáticamente la primera vez. Si no la ves, es porque todavía no has creado ningún proyecto.

### Cómo ver la ubicación desde IntelliJ

**Opción 1 — Barra de título:**
```
La ruta aparece en la barra de título de IntelliJ:
mi-proyecto – [/home/tunombre/IdeaProjects/mi-proyecto]
```

**Opción 2 — Clic derecho sobre el proyecto:**
```
Panel izquierdo (Project) → clic derecho sobre el nombre del proyecto
→ "Open In" → "Files"
→ Se abre la carpeta en el Administrador de archivos
```

**Opción 3 — Terminal dentro de IntelliJ:**
```
View → Tool Windows → Terminal
Escribir: pwd
Aparece la ruta completa: /home/tunombre/IdeaProjects/mi-proyecto
```

### Qué hay dentro de una carpeta de proyecto Spring Boot

```
mi-proyecto/
├── src/
│   ├── main/
│   │   ├── java/com/ejemplo/
│   │   │   └── MiProyectoApplication.java   ← clase principal
│   │   └── resources/
│   │       └── application.yml              ← configuración
│   └── test/
│       └── java/com/ejemplo/
│           └── MiProyectoApplicationTests.java
├── target/                                  ← generado por Maven (NO editar)
├── .mvn/                                    ← Maven Wrapper
├── mvnw                                     ← Maven Wrapper (Linux/Mac) ← ejecutable
├── mvnw.cmd                                 ← Maven Wrapper (Windows)
├── pom.xml                                  ← dependencias del proyecto
└── README.md
```

> 🗑️ La carpeta `target/` la genera Maven automáticamente al compilar. **No se sube a GitHub** y puede borrarse sin perder código.

---

## 6. Cómo encontrar un proyecto desde el Administrador de archivos

### Si sabes el nombre del proyecto

```
1. Abrir el Administrador de archivos
   Ubuntu: tecla Super (Windows) → buscar "Archivos" → Enter
   Mint:   clic en el ícono de carpeta en la barra de tareas

2. Ir a Inicio (Home) en el panel izquierdo

3. Buscar y abrir la carpeta IdeaProjects/

4. Doble clic en el proyecto
```

### Si no sabes dónde está

**Buscar desde el Administrador de archivos:**
```
Nautilus (Ubuntu):    Ctrl + F → escribir el nombre
Nemo (Mint):          Ctrl + F → escribir el nombre
Dolphin (KDE):        F3 para panel de búsqueda → escribir nombre
```

**Buscar desde la terminal:**
```bash
# Buscar carpeta por nombre desde tu home
find ~ -type d -name "nombre-del-proyecto"

# Buscar archivo pom.xml (todos los proyectos Maven)
find ~ -name "pom.xml" -not -path "*/target/*"

# Buscar archivo por nombre
find ~ -name "Main.java"
```

### Abrir el proyecto en IntelliJ desde el Administrador de archivos

```
Opción 1: Arrastrar la carpeta del proyecto a IntelliJ
Opción 2: File → Open → navegar hasta la carpeta del proyecto → OK
Opción 3: Clic derecho sobre la carpeta
          → "Abrir con IntelliJ IDEA"
          (aparece si IntelliJ está instalado y registrado)
```

> ⚠️ Siempre abre la **carpeta raíz del proyecto** (donde está el `pom.xml`), no un archivo `.java` suelto.

---

## 7. Crear, renombrar, mover y eliminar

### Crear una carpeta nueva

```
Opción 1: Clic derecho en espacio vacío del Administrador → "Nueva carpeta"
Opción 2: Atajo (Nautilus / Nemo): Ctrl + Shift + N
Opción 3: Desde la terminal: mkdir nombre-carpeta
```

### Crear un archivo nuevo

```
Clic derecho en espacio vacío → "Documento vacío" (si aparece la opción)
O desde la terminal: touch nombre-archivo.txt
```

> 💡 Para crear un archivo `.java`, `.yml`, etc., créalo desde IntelliJ — no desde el Administrador de archivos.

### Renombrar

```
Opción 1: Clic derecho sobre el archivo/carpeta → "Renombrar"
Opción 2: Seleccionar y presionar F2
Opción 3: Desde la terminal: mv nombre-viejo nombre-nuevo
```

### Mover

```
Opción 1: Arrastrar con el mouse a la nueva carpeta
Opción 2: Cortar (Ctrl + X) → ir al destino → pegar (Ctrl + V)
Opción 3: Desde la terminal: mv /ruta/origen /ruta/destino
```

### Copiar

```
Opción 1: Ctrl + C → ir al destino → Ctrl + V
Opción 2: Clic derecho → Copiar → ir al destino → Clic derecho → Pegar
Opción 3: Desde la terminal: cp -r /ruta/origen /ruta/destino
```

### Eliminar

```
Opción 1: Seleccionar → tecla Supr / Delete → va a la Papelera
Opción 2: Clic derecho → "Mover a la papelera"
Opción 3: Shift + Delete → elimina permanentemente (sin papelera)
Opción 4: Desde la terminal: rm -rf nombre-carpeta  ← ¡cuidado! no hay vuelta atrás
```

> ⚠️ `rm -rf` es permanente e instantáneo. Úsalo con mucho cuidado. Eliminar la carpeta `target/` es seguro. **Nunca elimines** `src/`, `pom.xml` ni `.git/`.

---

## 8. Mostrar archivos ocultos

En Linux, los archivos y carpetas cuyo nombre comienza con `.` son **ocultos**. Los más importantes en un proyecto son `.git/`, `.gitignore`, `.env`, `.mvn/` y `.idea/`.

```
En el Administrador de archivos:
  Atajo:  Ctrl + H        ← activa/desactiva archivos ocultos
  Menú:   Ver → Mostrar archivos ocultos

En la terminal:
  ls -la                  ← lista todos los archivos, incluyendo ocultos
```

### Archivos ocultos importantes en un proyecto

| Archivo / Carpeta | Para qué sirve |
|-------------------|----------------|
| `.git/` | Repositorio Git — no borrar nunca |
| `.mvn/` | Configuración de Maven Wrapper |
| `.gitignore` | Lista de archivos que Git ignora |
| `.env` | Variables de entorno locales |
| `.idea/` | Configuración interna de IntelliJ |

---

## 9. Buscar archivos y carpetas

### Búsqueda desde el Administrador de archivos

```
Nautilus (Ubuntu):  Ctrl + F → escribir nombre
Nemo (Mint):        Ctrl + F → escribir nombre
```

### Búsqueda desde la terminal

```bash
# Buscar carpeta por nombre
find ~ -type d -name "tickets"

# Buscar archivo por nombre
find ~ -type f -name "Main.java"

# Buscar todos los pom.xml (proyectos Maven), excluyendo target/
find ~ -name "pom.xml" -not -path "*/target/*"

# Buscar archivos con cierta extensión
find ~ -name "*.java"

# Buscar texto dentro de archivos
grep -r "public class" ~/IdeaProjects/
```

### Búsqueda desde el lanzador de apps

```
Ubuntu:  tecla Super → escribir el nombre del archivo → Enter
Mint:    menú Inicio → buscar en el campo de búsqueda
```

---

## 10. Organización recomendada para proyectos

### Estructura sugerida

```
/home/tunombre/
└── IdeaProjects/          ← carpeta por defecto de IntelliJ (o crear Proyectos/)
    ├── DSY1103/           ← carpeta del curso
    │   ├── tickets/       ← proyecto del curso
    │   └── ejercicios/    ← ejercicios prácticos
    └── personales/        ← proyectos propios
```

O bien, usar directamente la carpeta por defecto de IntelliJ:

```
~/IdeaProjects/
├── tickets/
├── ejercicio-01/
└── ejercicio-02/
```

### Reglas de nombres de carpetas y archivos

> 🐧 En Linux los nombres son **sensibles a mayúsculas**: `Proyecto` y `proyecto` son dos carpetas distintas.

| ✅ Correcto | ❌ Incorrecto | Problema |
|------------|--------------|---------|
| `mi-proyecto` | `mi proyecto` | Los espacios causan errores en la terminal |
| `tickets-api` | `Tickets API` | Espacios y mayúsculas inconsistentes |
| `ejercicio-01` | `Ejercicio (1)` | Paréntesis problemáticos en rutas |
| `dsy1103` | `DSY 1103 FINAL` | Espacios en ruta |

### Dónde NO guardar proyectos

| ❌ Ubicación | ⚠️ Por qué evitarla |
|-------------|---------------------|
| `~/Escritorio/` | Se mezcla con íconos, difícil de encontrar |
| `~/Descargas/` | Se limpia frecuentemente, riesgo de perder el proyecto |
| Carpetas sincronizadas en tiempo real | La sincronización puede corromper archivos de Maven o Git |

---

## 11. Descomprimir un proyecto de start.spring.io

Cuando descargas un proyecto de [start.spring.io](https://start.spring.io/) obtienes un archivo `.zip`.

### Pasos correctos (interfaz gráfica)

```
1. El archivo descargado está en: ~/Descargas/demo.zip

2. Mover el ZIP a tu carpeta de proyectos:
   Cortar (Ctrl+X) → pegar en ~/IdeaProjects/ (Ctrl+V)

3. Extraer el ZIP:
   Clic derecho sobre el ZIP → "Extraer aquí"
   Resultado: ~/IdeaProjects/demo/ (carpeta del proyecto)

4. Abrir en IntelliJ:
   File → Open → navegar hasta "demo" → OK
   IntelliJ detecta el pom.xml y configura el proyecto automáticamente
```

### Pasos correctos (terminal)

```bash
# Mover el ZIP a la carpeta de proyectos
mv ~/Descargas/demo.zip ~/IdeaProjects/

# Ir a la carpeta de proyectos
cd ~/IdeaProjects/

# Descomprimir
unzip demo.zip

# Verificar que se creó la carpeta
ls -la

# El proyecto está en:
ls demo/
```

### Dar permisos de ejecución al Maven Wrapper

Después de descomprimir, el archivo `mvnw` puede no tener permisos de ejecución:

```bash
cd ~/IdeaProjects/demo/
chmod +x mvnw

# Verificar permisos (debe aparecer -rwxr-xr-x)
ls -la mvnw
```

### Error más común al descomprimir

```
❌ Error: descomprimir directamente desde ~/Descargas/ sin mover el ZIP
   Resultado: el proyecto queda en Descargas — difícil de encontrar luego

❌ Error: abrir el ZIP con doble clic y copiar archivos sueltos desde adentro
   Resultado: falta estructura, Maven no funciona

✅ Correcto: mover el ZIP → extraer → abrir la carpeta resultante en IntelliJ
```

---

## 12. Atajos de teclado esenciales en Linux

### En el Administrador de archivos

| Atajo | Acción |
|-------|--------|
| `Ctrl + H` | Mostrar / ocultar archivos ocultos |
| `Ctrl + F` | Buscar archivos |
| `Ctrl + C` | Copiar |
| `Ctrl + X` | Cortar |
| `Ctrl + V` | Pegar |
| `Ctrl + Z` | Deshacer |
| `Ctrl + A` | Seleccionar todo |
| `F2` | Renombrar |
| `Delete` | Enviar a la Papelera |
| `Shift + Delete` | Eliminar permanentemente |
| `Ctrl + Shift + N` | Nueva carpeta (Nautilus/Nemo) |
| `Alt + ←` | Ir a la carpeta anterior |
| `Alt + ↑` | Subir un nivel de carpeta |
| `Ctrl + L` | Editar la barra de ruta (escribir ruta manualmente) |

### En el sistema (escritorio)

| Atajo | Acción |
|-------|--------|
| `Super` (tecla Windows) | Abrir lanzador de apps (Ubuntu) / Menú Inicio (Mint) |
| `Ctrl + Alt + T` | Abrir una terminal |
| `Super + E` | Abrir el Administrador de archivos (algunas distros) |

---

## 13. Cheat Sheet — Referencia rápida Linux

```
📁 SISTEMA DE ARCHIVOS
   Raíz del sistema   → /
   Tu carpeta home    → /home/tunombre/  o  ~
   Proyectos IntelliJ → ~/IdeaProjects/
   Ver ruta actual    → pwd  (en terminal)
   Ver ruta de home   → echo $HOME

📂 OPERACIONES BÁSICAS (Administrador de archivos)
   Nueva carpeta      → Ctrl + Shift + N  o  clic derecho → Nueva carpeta
   Renombrar          → F2
   Copiar             → Ctrl + C  →  ir al destino  →  Ctrl + V
   Mover              → Ctrl + X  →  ir al destino  →  Ctrl + V
   Eliminar (papelera)→ Delete
   Eliminar (perm.)   → Shift + Delete

📂 OPERACIONES BÁSICAS (Terminal)
   Nueva carpeta      → mkdir nombre
   Renombrar / Mover  → mv origen destino
   Copiar carpeta     → cp -r origen destino
   Eliminar carpeta   → rm -rf nombre  ← ¡cuidado!
   Ver contenido      → ls -la
   Ruta actual        → pwd

👁️  VER ARCHIVOS OCULTOS
   Administrador      → Ctrl + H
   Terminal           → ls -la

🔍 BUSCAR
   Administrador      → Ctrl + F → escribir nombre
   Terminal           → find ~ -name "nombre"
   Buscar pom.xml     → find ~ -name "pom.xml" -not -path "*/target/*"

📦 ABRIR PROYECTO EN INTELLIJ
   ✅ Siempre abrir la CARPETA del proyecto (donde está pom.xml)
   ❌ Nunca abrir un archivo .java suelto
   Atajo: clic derecho sobre carpeta → Open In → Files

🗂️ NOMBRES DE CARPETAS
   ✅ mi-proyecto    ✅ tickets-api    ✅ ejercicio-01
   ❌ mi proyecto    ❌ Tickets API    ❌ Ejercicio (1)
   ⚠️  Linux distingue MAYÚSCULAS: "Proyecto" ≠ "proyecto"

⚠️  NO GUARDAR PROYECTOS EN:
   ❌ Escritorio · ❌ Descargas · ❌ Carpetas sincronizadas en tiempo real
   ✅ ~/IdeaProjects/  o  ~/Proyectos/

🔑 PERMISOS (mvnw)
   chmod +x mvnw      → dar permiso de ejecución al Maven Wrapper
```

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

