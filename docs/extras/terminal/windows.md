# 🪟 Terminal — CMD y PowerShell (Windows)

> **Shells disponibles en Windows:**
> - **CMD** (`cmd.exe`) — el clásico, heredado de MS-DOS. Limitado pero universal.
> - **PowerShell** (`pwsh` / `powershell.exe`) — moderno, potente, orientado a objetos. **Recomendado.**
>
> En este documento cada bloque de código indica explícitamente si pertenece a **CMD** o **PowerShell**.

---

## 1. ¿Qué es una terminal y un shell?

La **terminal** (o consola) es el programa que muestra la ventana de texto. El **shell** es el intérprete que lee tus comandos y los ejecuta.

**Cómo abrirlos:**
- `Win + R` → escribir `cmd` → Enter (abre CMD)
- `Win + R` → escribir `powershell` → Enter (abre PowerShell)
- Buscar **"Windows Terminal"** en el menú inicio (abre ambos en pestañas)

**CMD:**
```cmd
:: Prompt de CMD
C:\Users\usuario\proyectos>
:: └─ C:\ → unidad y ruta actual
::    >   → indica que puedes escribir un comando
```

**PowerShell:**
```powershell
# Prompt de PowerShell
PS C:\Users\usuario\proyectos>
# └─ PS  → indica que estás en PowerShell
```

> 💡 Los comentarios en CMD usan `::` y en PowerShell usan `#`.

---

## 2. Navegación: rutas absolutas y relativas

Antes de ejecutar cualquier comando, necesitas saber **dónde estás** en el sistema de archivos. Cada vez que abres una terminal empiezas en la carpeta del usuario (`C:\Users\tu-usuario`). Navegar con rutas **absolutas** (`C:\ruta\completa`) siempre funciona sin importar desde dónde estés; las rutas **relativas** dependen de tu posición actual y son más cortas de escribir.

> 🎯 **Cuándo lo usarás:** entrar a la carpeta `Tickets\` para ejecutar `.\mvnw`, moverse entre `src\main\java` y `src\test\java`, cambiar de unidad de disco cuando el proyecto está en `D:\`.

### Conceptos clave

| Símbolo | Significado |
|---------|-------------|
| `C:\` | Raíz de la unidad C |
| `%USERPROFILE%` (CMD) · `$HOME` (PS) | Carpeta del usuario (`C:\Users\usuario`) |
| `.` | Directorio actual |
| `..` | Directorio padre (un nivel arriba) |

### Comandos

**CMD:**
```cmd
:: ¿Dónde estoy?
cd
:: Salida: C:\Users\usuario\proyectos

:: Ir a una ruta absoluta
cd C:\Users\usuario\proyectos

:: Ir a una ruta relativa
cd Tickets
cd ..\docs

:: Ir a la carpeta del usuario
cd %USERPROFILE%

::  Cambiar de unidad (CMD)
D:
```

**PowerShell:**
```powershell
# ¿Dónde estoy?
Get-Location       # o su alias:
pwd

# Ir a una ruta absoluta
Set-Location C:\Users\usuario\proyectos   # o su alias:
cd C:\Users\usuario\proyectos

# Ir a una ruta relativa
cd Tickets
cd ..\docs

# Ir a la carpeta del usuario
cd $HOME
cd ~               # también funciona en PowerShell

# Volver al directorio anterior
cd -               # solo en PowerShell
```

---

## 3. Listar contenido de un directorio

Antes de crear, mover o eliminar algo, **verifica qué hay en el directorio**. En CMD se usa `dir`; en PowerShell `Get-ChildItem` (o sus alias `ls` / `dir`). La opción `-Force` en PowerShell equivale a `dir /A` en CMD e incluye archivos ocultos como `.gitignore` o `.env`.

> 🎯 **Cuándo lo usarás:** confirmar que `pom.xml` existe antes de compilar, verificar si el JAR fue generado en `target\`, comprobar que `.env` está presente, revisar la estructura de un proyecto recién clonado.

**CMD:**
```cmd
:: Listar archivos y carpetas
dir

:: Listar con más detalle
dir /Q

:: Listar incluyendo archivos ocultos
dir /A

:: Listar otra carpeta sin entrar
dir C:\proyectos
```

**PowerShell:**
```powershell
# Listar archivos y carpetas
Get-ChildItem    # o sus alias:
ls
dir

# Listar con detalles
Get-ChildItem | Format-List

# Listar incluyendo archivos ocultos
Get-ChildItem -Force

# Listar otra carpeta sin entrar
Get-ChildItem C:\proyectos

# Listar solo archivos .md
Get-ChildItem *.md
```

---

## 4. Crear, mover, copiar y eliminar archivos y carpetas

Estas operaciones son la base del trabajo con el sistema de archivos. Dominarlas te permite organizar proyectos, mover artefactos compilados o limpiar directorios temporales **sin depender del explorador de archivos**, lo que es esencial al trabajar en servidores remotos.

> ⚠️ **Importante:** tanto `del` (CMD) como `Remove-Item` (PowerShell) eliminan archivos de forma permanente, **sin enviarlos a la papelera**. Verifica siempre el path antes de ejecutar.

> 🎯 **Cuándo lo usarás:** crear la estructura de un nuevo módulo, renombrar un archivo de configuración, copiar un JAR al directorio de deploy, limpiar la carpeta `target\` antes de compilar.

### Crear

**CMD:**
```cmd
:: Crear una carpeta
mkdir mi-carpeta

:: Crear carpetas anidadas
mkdir padre\hijo\nieto

:: Crear un archivo vacío
type nul > archivo.txt

:: Crear un archivo con contenido
echo hola mundo > saludo.txt
```

**PowerShell:**
```powershell
# Crear una carpeta
New-Item -ItemType Directory -Name mi-carpeta   # o:
mkdir mi-carpeta

# Crear carpetas anidadas
New-Item -ItemType Directory -Path padre\hijo\nieto -Force

# Crear un archivo vacío
New-Item -ItemType File -Name archivo.txt        # o:
ni archivo.txt

# Crear un archivo con contenido
Set-Content saludo.txt "hola mundo"              # o:
"hola mundo" | Out-File saludo.txt
```

### Mover y renombrar

En CMD `move` mueve y `ren` / `rename` renombra (son comandos distintos). En PowerShell `Move-Item` hace ambas cosas según si el destino es una carpeta existente o un nombre de archivo nuevo.

> 🎯 **Cuándo lo usarás:** renombrar `application.properties` a `application-dev.properties`, mover un script de deploy a la carpeta correcta, reorganizar la estructura de carpetas de un módulo.

**CMD:**
```cmd
:: Mover archivo a otra carpeta
move archivo.txt C:\docs\

:: Renombrar un archivo
rename archivo.txt nuevo-nombre.txt
ren archivo.txt nuevo-nombre.txt
```

**PowerShell:**
```powershell
# Mover archivo a otra carpeta
Move-Item archivo.txt C:\docs\             # o:
mv archivo.txt C:\docs\

# Renombrar un archivo
Rename-Item archivo.txt nuevo-nombre.txt   # o:
mv archivo.txt nuevo-nombre.txt

# Mover y renombrar en un paso
Move-Item archivo.txt C:\docs\nuevo-nombre.txt
```

### Copiar

`copy` (CMD) y `Copy-Item` (PowerShell) copian sin eliminar el original. Para carpetas completas es obligatorio usar `xcopy /E /I` en CMD o `-Recurse` en PowerShell.

> 🎯 **Cuándo lo usarás:** hacer un backup de `application.properties` antes de modificarlo, duplicar un módulo como punto de partida para otro.

**CMD:**
```cmd
:: Copiar un archivo
copy archivo.txt copia.txt

:: Copiar a otra carpeta
copy archivo.txt C:\docs\

:: Copiar una carpeta completa
xcopy mi-carpeta\ C:\backup\mi-carpeta\ /E /I
```

**PowerShell:**
```powershell
# Copiar un archivo
Copy-Item archivo.txt copia.txt            # o:
cp archivo.txt copia.txt

# Copiar a otra carpeta
Copy-Item archivo.txt C:\docs\

# Copiar una carpeta completa (recursivo)
Copy-Item mi-carpeta\ C:\backup\mi-carpeta\ -Recurse
```

### Eliminar

`del` (CMD) y `Remove-Item` (PowerShell) son **permanentes e irreversibles**: no hay papelera. La combinación `-Recurse -Force` en PowerShell eliminará todo el contenido de una carpeta sin pedir confirmación.

> 🎯 **Cuándo lo usarás:** limpiar `target\` para forzar una compilación limpia (`.\mvnw clean`), eliminar logs antiguos en un servidor, borrar carpetas temporales de prueba.

**CMD:**
```cmd
:: Eliminar un archivo
del archivo.txt

:: Eliminar sin confirmación
del /F archivo.txt

:: Eliminar una carpeta y su contenido
rmdir /S /Q mi-carpeta
```

**PowerShell:**
```powershell
# Eliminar un archivo
Remove-Item archivo.txt                    # o:
rm archivo.txt

# Eliminar sin confirmación
Remove-Item archivo.txt -Force

# Eliminar una carpeta y todo su contenido
Remove-Item mi-carpeta -Recurse -Force

# ⚠️ CUIDADO: Remove-Item no usa papelera. Es irreversible.
```

---

## 5. Leer archivos desde la terminal

Leer archivos sin abrir un editor es una habilidad crítica al conectarse a servidores remotos. En CMD `type` vuelca el contenido completo y `more` pagina; en PowerShell `Get-Content` es mucho más flexible: puede mostrar las primeras o últimas líneas, y con `-Wait` **monitorea el archivo en tiempo real**, equivalente al `tail -f` de Linux.

> 🎯 **Cuándo lo usarás:** revisar `application.properties` en producción, leer el `pom.xml` para verificar dependencias, seguir los logs de Spring Boot con `Get-Content app.log -Wait -Tail 20` mientras pruebas un endpoint, ver los últimos errores de un archivo de log.

**CMD:**
```cmd
:: Ver todo el contenido de un archivo
type archivo.txt

:: Ver página por página
more archivo.txt
:: (Espacio para avanzar, Q para salir)
```

**PowerShell:**
```powershell
# Ver todo el contenido de un archivo
Get-Content archivo.txt                    # o:
cat archivo.txt

# Ver con números de línea
Get-Content archivo.txt | Select-String "" | ForEach-Object { "$($_.LineNumber): $($_.Line)" }

# Ver página por página
Get-Content archivo.txt | more

# Ver solo las primeras N líneas
Get-Content archivo.txt -TotalCount 20    # o:
Get-Content archivo.txt | Select-Object -First 20

# Ver solo las últimas N líneas
Get-Content archivo.txt -Tail 20          # o:
Get-Content archivo.txt | Select-Object -Last 20

# Seguir un archivo en tiempo real (útil para logs)
Get-Content app.log -Wait -Tail 20
```

---

## 6. Variables de entorno

Las variables de entorno permiten **configurar la aplicación de forma diferente según el entorno** (desarrollo, staging, producción) sin modificar el código fuente. Spring Boot las lee directamente a través de `${MI_VARIABLE}` en `application.properties` o con `@Value`. Nunca hardcodees contraseñas, tokens o URLs en el código: usa variables de entorno.

> 🎯 **Cuándo lo usarás:** inyectar la URL de base de datos o contraseñas al arrancar Spring Boot, activar un perfil con `$env:SPRING_PROFILES_ACTIVE = "prod"`, verificar que `JAVA_HOME` apunta al JDK correcto, pasar una API key externa sin subirla al repositorio.

**CMD:**
```cmd
:: Ver TODAS las variables de entorno
set

:: Ver una variable específica
echo %USERPROFILE%
echo %PATH%
echo %JAVA_HOME%

:: Definir una variable en la sesión actual (temporal)
set MI_VAR=hola
echo %MI_VAR%

:: Definir de forma permanente (persiste al cerrar la terminal)
setx MI_VAR "valor permanente"
```

**PowerShell:**
```powershell
# Ver TODAS las variables de entorno
Get-ChildItem Env:                         # o:
ls Env:

# Ver una variable específica
$env:USERPROFILE
$env:PATH
$env:JAVA_HOME

# Definir una variable en la sesión actual (temporal)
$env:MI_VAR = "hola"
$env:MI_VAR

# Definir de forma permanente (nivel usuario)
[System.Environment]::SetEnvironmentVariable("MI_VAR", "valor", "User")

# Variables comunes del sistema
$env:USERPROFILE    # C:\Users\usuario
$env:USERNAME       # nombre de usuario
$env:COMPUTERNAME   # nombre del equipo
$HOME               # alias de USERPROFILE en PS
```

> 📌 Las variables definidas con `$env:VAR = "valor"` solo existen mientras dure la sesión de PowerShell. Para hacerlas permanentes, se agregan al perfil de PowerShell (`$PROFILE`) o usando `SetEnvironmentVariable`.

---

## 7. Redirección y pipes

La **redirección** envía la salida de un comando a un archivo en lugar de a la pantalla. Los **pipes** (`|`) encadenan comandos. En PowerShell el pipe es especialmente potente porque no pasa texto plano sino **objetos**, lo que permite filtrar, ordenar y transformar resultados con precisión usando `Where-Object`, `Select-Object` o `Measure-Object`.

> 🎯 **Cuándo lo usarás:** guardar la salida de `.\mvnw test` en un archivo para revisarla, filtrar logs para ver solo errores (`Get-Content app.log | Where-Object { $_ -match "ERROR" }`), contar cuántos archivos `.java` tiene el proyecto (`Get-ChildItem src\ -Recurse -Filter *.java | Measure-Object`), redirigir errores de compilación a un log.

### Redirección

**CMD:**
```cmd
:: Redirigir la salida a un archivo (sobreescribe)
echo texto > archivo.txt

:: Agregar al final del archivo
echo más texto >> archivo.txt

:: Redirigir errores a un archivo
mvnw.cmd test 2> errores.log
```

**PowerShell:**
```powershell
# Redirigir la salida a un archivo (sobreescribe)
"texto" | Out-File archivo.txt             # o:
"texto" > archivo.txt

# Agregar al final (sin sobreescribir)
"más texto" | Out-File archivo.txt -Append # o:
"más texto" >> archivo.txt

# Redirigir errores
.\mvnw test 2> errores.log

# Redirigir salida normal Y errores
.\mvnw test *> todo.log
```

### Pipes `|`

**CMD:**
```cmd
:: Filtrar la salida de dir
dir | find ".xml"

:: Ver los primeros resultados
dir | more
```

**PowerShell:**
```powershell
# El pipe en PowerShell pasa OBJETOS (no texto plano)
# Filtrar por nombre
Get-ChildItem | Where-Object { $_.Name -like "*.xml" }

# Contar elementos
Get-ChildItem | Measure-Object

# Ver los primeros 5 resultados
Get-ChildItem | Select-Object -First 5

# Filtrar logs
Get-Content app.log | Where-Object { $_ -match "ERROR" }
```

---

## 8. Búsqueda de texto en archivos

En CMD se usa `find` para búsquedas simples y `findstr` para búsquedas recursivas o con patrones. En PowerShell, `Select-String` (alias `sls`) es equivalente a `grep` de Linux: busca en archivos, muestra el número de línea y soporte regex. Es especialmente útil para analizar proyectos grandes sin abrir un IDE.

> 🎯 **Cuándo lo usarás:** encontrar todos los archivos donde se usa `TicketService`, buscar un `NullPointerException` específico en cientos de líneas de log, localizar qué clase tiene la anotación `@RestController`, filtrar solo las líneas de `WARN` y `ERROR` de un log de producción.

**CMD:**
```cmd
:: Buscar texto en un archivo
find "ERROR" app.log

:: Buscar sin distinguir mayúsculas/minúsculas
find /I "error" app.log

:: Mostrar número de línea
find /N "NullPointerException" app.log

:: Buscar recursivamente (requiere findstr)
findstr /S /I "TicketService" src\*
```

**PowerShell:**
```powershell
# Buscar texto en un archivo
Select-String "ERROR" app.log              # o su alias:
sls "ERROR" app.log

# Buscar sin distinguir mayúsculas/minúsculas (por defecto en PS)
Select-String "error" app.log

# Mostrar número de línea (siempre visible en PS)
Select-String "NullPointerException" app.log
# Salida: app.log:42:java.lang.NullPointerException

# Buscar recursivamente en todos los archivos de una carpeta
Get-ChildItem src\ -Recurse | Select-String "TicketService"

# Buscar con regex
Select-String "ERROR|WARN" app.log

# Buscar e invertir (líneas que NO contienen el texto)
Get-Content app.log | Where-Object { $_ -notmatch "DEBUG" }
```

---

## 9. Permisos de archivos (ejecución de scripts)

En Windows los permisos funcionan diferente a Linux. El principal obstáculo es la **política de ejecución** de PowerShell, que por seguridad bloquea la ejecución de scripts locales por defecto. Configurar `RemoteSigned` es el primer paso obligatorio al instalar el entorno de desarrollo.

> 🎯 **Cuándo lo usarás:** habilitar la ejecución de `.\mvnw` la primera vez que usas PowerShell en un equipo nuevo, ejecutar scripts de automatización del proyecto, desbloquear scripts de configuración descargados del repositorio.

**CMD:**
```cmd
:: En CMD no existe el equivalente a chmod.
:: Los archivos .bat y .cmd se ejecutan directamente.
.\mvnw.cmd spring-boot:run
```

**PowerShell:**
```powershell
# Ver la política de ejecución actual
Get-ExecutionPolicy

# Permitir scripts locales (recomendado para desarrollo)
# ⚠️ Ejecutar PowerShell como Administrador
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Ejecutar el Maven Wrapper
.\mvnw spring-boot:run

# Ver permisos de un archivo
Get-Acl archivo.txt

# Ver si un archivo es de solo lectura
Get-Item archivo.txt | Select-Object IsReadOnly

# Quitar atributo de solo lectura
Set-ItemProperty archivo.txt -Name IsReadOnly -Value $false
```

> 📌 `RemoteSigned` permite ejecutar scripts locales sin firmar, pero bloquea los descargados de internet que no estén firmados. Es la configuración recomendada para desarrollo.

---

## 10. Comandos de red: `ping` y `curl`

`ping` verifica que hay **conectividad básica** con un host. `curl` permite hacer peticiones HTTP completas (GET, POST, PUT, DELETE) desde la terminal. En PowerShell la alternativa nativa es `Invoke-RestMethod`, que además **parsea la respuesta JSON automáticamente** devolviéndola como objeto, lo que facilita inspeccionar campos concretos de la respuesta.

> 🎯 **Cuándo lo usarás:** verificar que Spring Boot levantó en el puerto 8080, probar un endpoint REST sin abrir Postman, depurar si la API devuelve el código HTTP correcto, hacer peticiones de prueba en un servidor remoto accesible solo por terminal.

### ping

**CMD:**
```cmd
:: Verificar conectividad
ping google.com

:: Limitar a 4 paquetes
ping -n 4 google.com

:: Hacer ping a localhost
ping localhost
ping 127.0.0.1
```

**PowerShell:**
```powershell
# Verificar conectividad
ping google.com                              # funciona igual que CMD

# Versión PowerShell nativa
Test-Connection google.com
Test-Connection google.com -Count 4

# Hacer ping a localhost
Test-Connection localhost
```

### curl

**CMD:**
```cmd
:: En CMD no hay curl nativo en versiones antiguas.
:: Desde Windows 10 (1803+) está disponible curl.exe
curl http://localhost:8080/api/v1/tickets

:: POST con JSON
:: (^ es el carácter de continuación de línea en CMD)
curl -X POST http://localhost:8080/api/v1/tickets ^
  -H "Content-Type: application/json" ^
  -d "{\"title\": \"Bug en login\", \"status\": \"OPEN\"}"

:: DELETE
curl -X DELETE http://localhost:8080/api/v1/tickets/1

:: Ver solo el código de respuesta HTTP
curl -o NUL -s -w "%{http_code}" http://localhost:8080/api/v1/tickets
```

**PowerShell:**
```powershell
# ⚠️ El alias "curl" en PS apunta a Invoke-WebRequest, NO al curl real.
# Para usar el curl real, escribe curl.exe explícitamente.

# GET con curl real
curl.exe http://localhost:8080/api/v1/tickets

# GET con el cmdlet nativo de PowerShell
Invoke-RestMethod http://localhost:8080/api/v1/tickets

# POST con cuerpo JSON (PowerShell nativo)
# (` es el carácter de continuación de línea en PowerShell)
$body = @{ title = "Bug en login"; status = "OPEN" } | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8080/api/v1/tickets `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

# PUT
$body = @{ title = "Bug corregido"; status = "CLOSED" } | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8080/api/v1/tickets/1 `
  -Method PUT `
  -ContentType "application/json" `
  -Body $body

# DELETE
Invoke-RestMethod -Uri http://localhost:8080/api/v1/tickets/1 -Method DELETE

# Ver código de respuesta HTTP
(Invoke-WebRequest http://localhost:8080/api/v1/tickets).StatusCode
```

> 💡 Para evitar confusión con el alias, en PowerShell es recomendable usar `curl.exe` (el binario real) o `Invoke-RestMethod` (el nativo de PS).

---

## 11. Atajos y productividad

Conocer los atajos de teclado marca la diferencia entre una experiencia frustrante y una fluida. En PowerShell, **`Ctrl + R`** busca en el historial completo de comandos a medida que escribes. **`Tab`** autocompleta tanto comandos como parámetros y rutas. En ambos shells, **`Ctrl + C`** es el salvavidas para cancelar cualquier proceso colgado.

> 🎯 **Cuándo lo usarás:** recuperar rápidamente el comando `.\mvnw spring-boot:run` del historial sin volver a escribirlo, cancelar un proceso de Maven colgado con `Ctrl + C`, autocompletar rutas largas como `src\main\java\com\empresa\...` con un solo `Tab`.

---

## 12. Referencia rápida (cheat sheet)

**CMD:**
```cmd
:: ─── NAVEGACIÓN ─────────────────────────────────────────────
cd                             :: directorio actual
cd C:\ruta\absoluta            :: ir a ruta absoluta
cd carpeta                     :: ir a subcarpeta (relativo)
cd ..                          :: subir un nivel
cd %USERPROFILE%               :: ir al home
D:                             :: cambiar de unidad

:: ─── LISTADO ────────────────────────────────────────────────
dir                            :: listar
dir /A                         :: listar con ocultos
dir /Q                         :: listar con propietario

:: ─── ARCHIVOS Y CARPETAS ────────────────────────────────────
mkdir carpeta                  :: crear carpeta
mkdir padre\hijo\nieto         :: crear carpetas anidadas
type nul > archivo.txt         :: crear archivo vacío
echo texto > archivo.txt       :: crear archivo con contenido
copy origen destino            :: copiar archivo
xcopy origen\ destino\ /E /I   :: copiar carpeta
move origen destino            :: mover archivo
ren archivo.txt nuevo.txt      :: renombrar archivo
del archivo.txt                :: eliminar archivo
rmdir /S /Q carpeta            :: eliminar carpeta

:: ─── LECTURA ─────────────────────────────────────────────────
type archivo.txt               :: ver contenido completo
more archivo.txt               :: ver página por página

:: ─── BÚSQUEDA ───────────────────────────────────────────────
find "texto" archivo.txt       :: buscar texto
find /I "texto" archivo.txt    :: buscar sin case-sensitive
find /N "texto" archivo.txt    :: buscar con número de línea
findstr /S /I "texto" src\*    :: buscar recursivo

:: ─── VARIABLES ───────────────────────────────────────────────
set                            :: ver todas las variables
echo %MI_VAR%                  :: leer variable
set MI_VAR=valor               :: definir variable (sesión)
setx MI_VAR "valor"            :: definir variable (permanente)

:: ─── RED ──────────────────────────────────────────────────────
ping -n 4 host                 :: verificar conectividad
curl http://localhost:8080     :: petición GET
curl -X POST ... -d "{}"       :: petición POST con JSON

:: ─── PIPES Y REDIRECCIÓN ─────────────────────────────────────
cmd1 | cmd2                    :: pipe
echo texto > archivo.txt       :: sobreescribir
echo texto >> archivo.txt      :: agregar al final
cmd 2> errores.log             :: redirigir errores
```

**PowerShell:**
```powershell
# ─── NAVEGACIÓN ───────────────────────────────────────────────
pwd / Get-Location            # directorio actual
cd C:\ruta\absoluta           # ir a ruta absoluta
cd carpeta                    # ir a subcarpeta (relativo)
cd ..                         # subir un nivel
cd ~  /  cd $HOME             # ir al home
cd -                          # volver al directorio anterior (solo PS)

# ─── LISTADO ──────────────────────────────────────────────────
ls / dir / Get-ChildItem      # listar
Get-ChildItem -Force          # listar con ocultos
Get-ChildItem *.xml           # filtrar por extensión

# ─── ARCHIVOS Y CARPETAS ──────────────────────────────────────
mkdir carpeta                          # crear carpeta
New-Item -ItemType File -Name f.txt    # crear archivo vacío
cp origen destino / Copy-Item          # copiar archivo
Copy-Item origen\ destino\ -Recurse   # copiar carpeta
mv origen destino / Move-Item          # mover o renombrar
rm archivo / Remove-Item               # eliminar archivo
Remove-Item carpeta -Recurse -Force    # eliminar carpeta

# ─── LECTURA ──────────────────────────────────────────────────
cat archivo / Get-Content archivo      # ver contenido completo
Get-Content archivo -TotalCount 20     # primeras 20 líneas
Get-Content archivo -Tail 20           # últimas 20 líneas
Get-Content app.log -Wait -Tail 20     # seguir en tiempo real

# ─── BÚSQUEDA ─────────────────────────────────────────────────
Select-String "texto" archivo          # buscar texto (alias: sls)
Get-ChildItem src\ -Recurse | sls "x"  # buscar recursivo

# ─── VARIABLES ────────────────────────────────────────────────
$env:MI_VAR = "valor"                  # definir variable (sesión)
$env:MI_VAR                            # leer variable
ls Env:                                # ver todas las variables

# ─── RED ──────────────────────────────────────────────────────
ping localhost                         # verificar conectividad
Test-Connection google.com             # ping nativo PS
curl.exe http://localhost:8080         # petición GET (curl real)
Invoke-RestMethod http://localhost:8080 -Method GET
Invoke-RestMethod http://... -Method POST -Body $json -ContentType "application/json"

# ─── PERMISOS / EJECUCIÓN ─────────────────────────────────────
Get-ExecutionPolicy                              # ver política actual
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser  # habilitar scripts
.\mvnw spring-boot:run                           # ejecutar Maven Wrapper

# ─── PIPES Y REDIRECCIÓN ──────────────────────────────────────
cmd1 | cmd2                            # pipe (pasa objetos en PS)
"texto" > archivo.txt                  # sobreescribir
"texto" >> archivo.txt                 # agregar al final
cmd *> todo.log                        # salida + errores al mismo archivo
```

---

> 🔗 Ver la guía equivalente para Linux/macOS: [Bash](./bash.md)

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
