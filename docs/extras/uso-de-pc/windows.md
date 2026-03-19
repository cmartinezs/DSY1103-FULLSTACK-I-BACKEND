# 🪟 Uso de PC para Programadores — Windows 10 / 11

> **Sistema:** Windows 10 / 11  
> **Herramienta principal:** Explorador de archivos (`Explorador de Windows`)  
> **Carpeta raíz:** `C:\`

---

## 1. ¿Qué es el sistema de archivos?

El **sistema de archivos** es la forma en que el sistema operativo organiza todo lo que hay en el disco duro: tus documentos, fotos, programas, código fuente — todo.

En Windows se organiza como un **árbol** que parte desde un disco:

```
C:\                          ← Raíz del disco C
├── Program Files\           ← Programas instalados (64 bits)
├── Program Files (x86)\     ← Programas instalados (32 bits)
├── Windows\                 ← Sistema operativo
└── Users\                   ← Carpetas de usuarios
    └── TuNombre\            ← TU carpeta de usuario (home)
        ├── Desktop\         ← Escritorio
        ├── Documents\       ← Documentos
        ├── Downloads\       ← Descargas
        ├── Pictures\        ← Imágenes
        └── IdeaProjects\    ← ← ← Proyectos de IntelliJ (por defecto)
```

> 💡 Todo en Windows empieza desde una letra de disco (`C:\`, `D:\`, etc.). La barra que separa carpetas es `\` (barra invertida).

---

## 2. Carpetas, archivos y extensiones

| Concepto | Descripción | Ejemplo |
|----------|-------------|---------|
| **Carpeta (directorio)** | Contenedor que agrupa archivos o más carpetas | `mi-proyecto\` |
| **Archivo** | Unidad de información con un nombre y extensión | `Main.java` |
| **Extensión** | Sufijo del nombre que indica el tipo de archivo | `.java` `.xml` `.yml` `.md` |

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
| `.class` | Bytecode Java | Java compilado (generado automáticamente) |

### Mostrar extensiones de archivo (¡importante!)

Por defecto Windows **oculta** las extensiones. Como programador necesitas verlas siempre:

```
1. Abrir el Explorador de archivos
2. Clic en la pestaña "Ver" (Windows 10) o menú "Ver" → "Mostrar" (Windows 11)
3. Activar: ✅ "Extensiones de nombre de archivo"
4. Activar: ✅ "Elementos ocultos"
```

**Windows 10:**
```
Explorador → Ver → (marcar) Extensiones de nombre de archivo
                 → (marcar) Elementos ocultos
```

**Windows 11:**
```
Explorador → Ver → Mostrar → (marcar) Extensiones de nombre de archivo
                            → (marcar) Elementos ocultos
```

---

## 3. Rutas absolutas y relativas

Una **ruta** es la dirección de un archivo o carpeta dentro del sistema de archivos.

### Ruta absoluta

Parte siempre desde la raíz del disco. No importa en qué carpeta estés: siempre lleva al mismo lugar.

```
C:\Users\TuNombre\IdeaProjects\mi-proyecto\src\main\java\Main.java
```

### Ruta relativa

Parte desde la ubicación actual. Depende de dónde estés parado.

```
# Si estás en C:\Users\TuNombre\IdeaProjects\mi-proyecto\
src\main\java\Main.java

# Subir un nivel
..\otro-proyecto\

# Carpeta actual
.\src\
```

### Separadores de ruta en Windows

| Símbolo | Nombre | Uso |
|---------|--------|-----|
| `\` | Barra invertida (backslash) | Separador en Windows |
| `/` | Barra normal (slash) | También funciona en consola PowerShell/Git Bash |
| `C:` | Letra de disco | Identifica el disco |

---

## 4. Tu carpeta de usuario (home)

En Windows tu carpeta de usuario es:

```
C:\Users\TuNombre\
```

Se puede acceder de varias formas:

```
Opción 1: Escribir %USERPROFILE% en la barra de direcciones del Explorador
Opción 2: Escribir ~ en PowerShell o Git Bash
Opción 3: Ir a "Este equipo" → disco C → Users → TuNombre
Opción 4: Clic en el nombre de usuario en el panel izquierdo del Explorador
```

---

## 5. ¿Dónde se guardan los proyectos de IntelliJ IDEA?

### Ubicación por defecto

Cuando creas un proyecto en IntelliJ IDEA, **por defecto se guarda en**:

```
C:\Users\TuNombre\IdeaProjects\nombre-del-proyecto\
```

> ⚠️ Esta carpeta la crea IntelliJ automáticamente la primera vez. Si no la ves, es porque todavía no has creado ningún proyecto.

### Cómo ver la ubicación desde IntelliJ

**Opción 1 — Barra de título:**
```
La ruta aparece en la barra de título de IntelliJ:
mi-proyecto – [C:\Users\TuNombre\IdeaProjects\mi-proyecto]
```

**Opción 2 — Clic derecho sobre el proyecto:**
```
Panel izquierdo (Project) → clic derecho sobre el nombre del proyecto
→ "Open In" → "Explorer"
→ Se abre la carpeta en el Explorador de Windows
```

**Opción 3 — Archivo del proyecto:**
```
File → Open in → Explorer
```

**Opción 4 — Terminal dentro de IntelliJ:**
```
View → Tool Windows → Terminal
Escribir: cd .
Aparece la ruta actual del proyecto
```

### Qué hay dentro de una carpeta de proyecto Spring Boot

```
mi-proyecto\
├── src\
│   ├── main\
│   │   ├── java\com\ejemplo\
│   │   │   └── MiProyectoApplication.java   ← clase principal
│   │   └── resources\
│   │       └── application.yml              ← configuración
│   └── test\
│       └── java\com\ejemplo\
│           └── MiProyectoApplicationTests.java
├── target\                                  ← generado por Maven (NO editar)
├── .mvn\                                    ← Maven Wrapper
├── mvnw                                     ← Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                 ← Maven Wrapper (Windows)
├── pom.xml                                  ← dependencias del proyecto
└── README.md
```

> 🗑️ La carpeta `target\` la genera Maven automáticamente al compilar. **No se sube a GitHub** y puede borrarse sin perder código.

---

## 6. Cómo encontrar un proyecto desde el Explorador

### Si sabes el nombre del proyecto

```
1. Abrir el Explorador de archivos (Win + E)
2. Ir a C:\Users\TuNombre\IdeaProjects\
3. Buscar la carpeta con el nombre del proyecto
4. Doble clic para entrar
```

### Si no sabes dónde está

**Buscar desde el Explorador:**
```
1. Abrir el Explorador (Win + E)
2. Clic en "Este equipo" en el panel izquierdo
3. En el cuadro de búsqueda (arriba a la derecha) escribir el nombre del proyecto
4. Esperar los resultados → clic derecho → "Abrir ubicación del archivo"
```

**Buscar desde el menú Inicio:**
```
Win → escribir el nombre del archivo o carpeta → Enter
```

### Abrir el proyecto en IntelliJ desde el Explorador

```
Opción 1: Arrastrar la carpeta del proyecto a IntelliJ
Opción 2: File → Open → navegar hasta la carpeta del proyecto → OK
Opción 3: Clic derecho sobre la carpeta → "Open Folder as IntelliJ IDEA Project"
          (aparece si IntelliJ está instalado)
```

> ⚠️ Siempre abre la **carpeta raíz del proyecto** (donde está el `pom.xml`), no un archivo `.java` suelto.

---

## 7. Crear, renombrar, mover y eliminar

### Crear una carpeta nueva

```
Opción 1: Clic derecho en espacio vacío del Explorador → Nuevo → Carpeta
Opción 2: Botón "Nueva carpeta" en la barra de herramientas del Explorador
Opción 3: Ctrl + Shift + N (dentro del Explorador)
```

### Crear un archivo nuevo

```
Clic derecho en espacio vacío → Nuevo → Documento de texto (u otro tipo)
```

> 💡 Para crear un archivo `.java`, `.yml`, etc., créalo desde IntelliJ — no desde el Explorador.

### Renombrar

```
Opción 1: Clic derecho sobre el archivo/carpeta → Cambiar nombre
Opción 2: Seleccionar y presionar F2
Opción 3: Clic lento (dos clics separados, no doble clic) sobre el nombre
```

### Mover (cortar y pegar)

```
Seleccionar → Ctrl + X  (cortar)
Ir a destino → Ctrl + V  (pegar)

También: arrastrar con el mouse entre carpetas
```

### Copiar

```
Seleccionar → Ctrl + C  (copiar)
Ir a destino → Ctrl + V  (pegar)
```

### Eliminar

```
Opción 1: Seleccionar → tecla Supr (Delete) → va a la Papelera
Opción 2: Seleccionar → Shift + Supr → elimina permanentemente (sin papelera)
Opción 3: Clic derecho → Eliminar
```

> ⚠️ Eliminar la carpeta `target\` es seguro — Maven la regenera. **Nunca elimines** `src\`, `pom.xml` ni `.git\`.

---

## 8. Mostrar archivos ocultos

Los archivos y carpetas que empiezan con `.` (como `.git`, `.mvn`, `.env`) son **ocultos** por defecto en Windows.

```
Windows 10:
Explorador → pestaña "Ver" → marcar "Elementos ocultos"

Windows 11:
Explorador → Ver → Mostrar → Elementos ocultos
```

### Archivos ocultos importantes en un proyecto

| Archivo / Carpeta | Para qué sirve |
|-------------------|----------------|
| `.git\` | Repositorio Git — no borrar nunca |
| `.mvn\` | Configuración de Maven Wrapper |
| `.gitignore` | Lista de archivos que Git ignora |
| `.env` | Variables de entorno locales |
| `.idea\` | Configuración interna de IntelliJ |

---

## 9. Buscar archivos y carpetas

### Búsqueda rápida desde el Explorador

```
1. Abrir el Explorador y navegar a la carpeta donde quieres buscar
2. Clic en el cuadro de búsqueda (arriba a la derecha)
3. Escribir el nombre del archivo o parte de él
4. Presionar Enter
```

### Búsqueda avanzada

```
# Buscar por extensión:
*.java        → todos los archivos Java
*.yml         → todos los archivos YAML
pom.xml       → buscar por nombre exacto

# Buscar contenido dentro de archivos (desde PowerShell):
Select-String -Path "C:\Users\TuNombre\IdeaProjects\*\*.java" -Pattern "public class"
```

### Búsqueda desde el menú Inicio

```
Win → escribir nombre → aparecen resultados inmediatos
```

---

## 10. Organización recomendada para proyectos

### Estructura sugerida

```
C:\
└── Users\
    └── TuNombre\
        └── Proyectos\          ← crear esta carpeta tú mismo
            ├── DSY1103\        ← carpeta del curso
            │   ├── tickets\    ← proyecto del curso
            │   └── ejercicios\ ← ejercicios prácticos
            └── personales\     ← proyectos propios
```

O bien, usar la carpeta que IntelliJ crea por defecto:

```
C:\Users\TuNombre\IdeaProjects\
├── tickets\
├── ejercicio-01\
└── ejercicio-02\
```

### Reglas de nombres de carpetas y archivos

| ✅ Correcto | ❌ Incorrecto | Problema |
|------------|--------------|---------|
| `mi-proyecto` | `mi proyecto` | Los espacios causan errores en la terminal |
| `tickets-api` | `Tickets API` | Espacios y mayúsculas inconsistentes |
| `ejercicio-01` | `Ejercicio (1)` | Paréntesis problemáticos en rutas |
| `dsy1103` | `DSY 1103 FINAL` | Espacios en ruta |

### Dónde NO guardar proyectos

| ❌ Ubicación | ⚠️ Por qué evitarla |
|-------------|---------------------|
| `Escritorio\` | Se mezcla con otros ícono, difícil de encontrar |
| `Descargas\` | Se limpia frecuentemente, riesgo de perder el proyecto |
| `OneDrive\` | La sincronización en tiempo real puede corromper archivos de Maven o Git |
| `Google Drive\` | Mismo problema que OneDrive |
| Raíz de `C:\` | Desordenado, puede requerir permisos de administrador |

---

## 11. Descomprimir un proyecto de start.spring.io

Cuando descargas un proyecto de [start.spring.io](https://start.spring.io/) obtienes un archivo `.zip`.

### Pasos correctos

```
1. El archivo descargado está en: C:\Users\TuNombre\Downloads\demo.zip

2. Mover el ZIP a tu carpeta de proyectos:
   Cortar (Ctrl+X) → pegar en C:\Users\TuNombre\IdeaProjects\ (Ctrl+V)

3. Extraer el ZIP:
   Clic derecho sobre el ZIP → "Extraer todo..."
   → Destino: C:\Users\TuNombre\IdeaProjects\
   → Clic en "Extraer"

4. Resultado: C:\Users\TuNombre\IdeaProjects\demo\ (carpeta del proyecto)

5. Abrir en IntelliJ:
   File → Open → navegar hasta "demo" → OK
   IntelliJ detecta el pom.xml y configura el proyecto automáticamente
```

### Error más común al descomprimir

```
❌ Error: extraer directamente desde la carpeta Descargas sin mover el ZIP antes
   Resultado: el proyecto queda en Descargas — difícil de encontrar luego

❌ Error: abrir el ZIP con doble clic y copiar archivos sueltos desde adentro
   Resultado: falta estructura, Maven no funciona

✅ Correcto: mover el ZIP → extraer → abrir la carpeta resultante en IntelliJ
```

---

## 12. Atajos de teclado esenciales en Windows

| Atajo | Acción |
|-------|--------|
| `Win + E` | Abrir el Explorador de archivos |
| `Win + D` | Mostrar/ocultar el escritorio |
| `Ctrl + C` | Copiar |
| `Ctrl + X` | Cortar |
| `Ctrl + V` | Pegar |
| `Ctrl + Z` | Deshacer |
| `Ctrl + A` | Seleccionar todo |
| `F2` | Renombrar archivo o carpeta seleccionado |
| `Delete` | Enviar a la Papelera |
| `Shift + Delete` | Eliminar permanentemente |
| `Alt + ←` | Ir a la carpeta anterior en el Explorador |
| `Alt + ↑` | Subir un nivel de carpeta en el Explorador |
| `Ctrl + Shift + N` | Nueva carpeta en el Explorador |
| `Win + R` | Abrir cuadro "Ejecutar" (útil: escribir `%USERPROFILE%`) |

---

## 13. Cheat Sheet — Referencia rápida Windows

```
📁 SISTEMA DE ARCHIVOS
   Raíz del disco     → C:\
   Tu carpeta home    → C:\Users\TuNombre\
   Proyectos IntelliJ → C:\Users\TuNombre\IdeaProjects\
   Acceso rápido home → %USERPROFILE% en la barra de direcciones

📂 OPERACIONES BÁSICAS (Explorador de archivos)
   Nueva carpeta      → Ctrl + Shift + N
   Renombrar          → F2
   Copiar             → Ctrl + C  →  ir al destino  →  Ctrl + V
   Mover              → Ctrl + X  →  ir al destino  →  Ctrl + V
   Eliminar (papelera)→ Delete
   Eliminar (perm.)   → Shift + Delete
   Deshacer           → Ctrl + Z

🔍 BUSCAR
   Desde Explorador   → clic en cuadro de búsqueda → escribir nombre
   Desde Inicio       → Win → escribir nombre

👁️ VER ARCHIVOS OCULTOS Y EXTENSIONES
   Windows 10         → Explorador → Ver → ✅ Extensiones · ✅ Elementos ocultos
   Windows 11         → Explorador → Ver → Mostrar → ✅ Extensiones · ✅ Elementos ocultos

📦 ABRIR PROYECTO EN INTELLIJ
   ✅ Siempre abrir la CARPETA del proyecto (donde está pom.xml)
   ❌ Nunca abrir un archivo .java suelto
   Atajo: clic derecho sobre carpeta del proyecto → Open In → Explorer

🗂️ NOMBRES DE CARPETAS
   ✅ mi-proyecto    ✅ tickets-api    ✅ ejercicio-01
   ❌ mi proyecto    ❌ Tickets API    ❌ Ejercicio (1)

⚠️  NO GUARDAR PROYECTOS EN:
   ❌ Escritorio · ❌ Descargas · ❌ OneDrive · ❌ Google Drive
   ✅ C:\Users\TuNombre\IdeaProjects\ o C:\Users\TuNombre\Proyectos\
```

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

