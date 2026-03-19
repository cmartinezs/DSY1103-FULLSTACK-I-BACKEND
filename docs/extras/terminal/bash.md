# 🐧 Terminal — Bash (Linux / macOS)

> **Shell:** `bash` · `zsh` (macOS por defecto desde Catalina)
> **Sistemas:** Ubuntu, Debian, Fedora, macOS, WSL2 en Windows

---

## 1. ¿Qué es una terminal y un shell?

La **terminal** es el programa que muestra la ventana de texto. El **shell** es el intérprete que lee tus comandos y los ejecuta. En Linux/macOS el shell más común es `bash` (Bourne Again Shell) o `zsh`.

```bash
# El prompt típico de bash
usuario@hostname:~/proyectos$
# └─ usuario   → tu nombre de usuario
#    hostname  → nombre del equipo
#    ~/proyectos → directorio actual (~ = carpeta home)
#    $          → indica que eres usuario normal (# = root/admin)
```

---

## 2. Navegación: rutas absolutas y relativas

Antes de ejecutar cualquier comando, necesitas saber **dónde estás** en el sistema de archivos. Cada vez que abres una terminal empiezas en tu carpeta home (`~`). Navegar con rutas **absolutas** (desde la raíz `/`) siempre funciona sin importar desde dónde estés; las rutas **relativas** dependen de tu posición actual y son más cortas de escribir.

> 🎯 **Cuándo lo usarás:** entrar a la carpeta `Tickets/` para ejecutar `./mvnw`, moverse entre `src/main/java` y `src/test/java`, navegar a la carpeta de logs en un servidor.

### Conceptos clave

| Símbolo | Significado |
|---------|-------------|
| `/` | Raíz del sistema de archivos |
| `~` | Carpeta home del usuario (`/home/usuario`) |
| `.` | Directorio actual |
| `..` | Directorio padre (un nivel arriba) |

### Comandos

```bash
# ¿Dónde estoy?
pwd
# Salida: /home/usuario/proyectos

# Ir a una ruta absoluta
cd /home/usuario/proyectos

# Ir a una ruta relativa (desde donde estoy)
cd Tickets
cd ../docs

# Ir a la carpeta home directamente
cd ~
cd          # también funciona sin argumento

# Volver al directorio anterior
cd -
```

---

## 3. Listar contenido de un directorio

Antes de crear, mover o eliminar algo, **verifica qué hay en el directorio**. `ls` es el comando que más usarás en el día a día. La opción `-la` es la más completa: muestra permisos, propietario, tamaño y fecha de cada archivo, incluyendo los ocultos (los que empiezan con `.`, como `.gitignore` o `.env`).

> 🎯 **Cuándo lo usarás:** confirmar que `pom.xml` existe antes de compilar, verificar si el JAR fue generado en `target/`, comprobar que `.env` está presente sin que Git lo vea, revisar la estructura de un proyecto recién clonado.

```bash
# Listar archivos y carpetas
ls

# Listar con detalles (permisos, tamaño, fecha)
ls -l

# Listar incluyendo archivos ocultos (los que empiezan con .)
ls -a

# Combinado: detalles + ocultos
ls -la

# Listar otra carpeta sin entrar en ella
ls -la /home/usuario/proyectos

# Listar con tamaños legibles (KB, MB)
ls -lh
```

```
# Salida de ls -la
drwxr-xr-x  5 usuario grupo 4096 mar 19 10:30 .
drwxr-xr-x 12 usuario grupo 4096 mar 18 09:00 ..
-rw-r--r--  1 usuario grupo 1234 mar 19 10:25 pom.xml
drwxr-xr-x  3 usuario grupo 4096 mar 17 08:00 src/
```

---

## 4. Crear, mover, copiar y eliminar archivos y carpetas

Estas operaciones son la base del trabajo con el sistema de archivos. En un servidor **no hay interfaz gráfica** para arrastrar y soltar: todo se hace desde la terminal. Es fundamental dominar estos comandos para organizar proyectos, mover artefactos compilados o limpiar directorios temporales.

> ⚠️ **Importante:** en Linux no hay papelera de reciclaje. `rm` elimina de forma permanente e irreversible. Siempre verifica dos veces antes de ejecutar `rm -rf`.

> 🎯 **Cuándo lo usarás:** crear la estructura de un nuevo módulo, renombrar un archivo de configuración, copiar un JAR al directorio de deploy, limpiar la carpeta `target/` antes de compilar.

### Crear

```bash
# Crear una carpeta
mkdir mi-carpeta

# Crear carpetas anidadas de una vez
mkdir -p padre/hijo/nieto

# Crear un archivo vacío
touch archivo.txt

# Crear un archivo con contenido
echo "hola mundo" > saludo.txt
```

### Mover y renombrar

En Bash `mv` sirve para **mover** y también para **renombrar**: si el destino es un nombre de archivo (no una carpeta existente), renombra. Si es una carpeta existente, mueve el archivo dentro de ella.

> 🎯 **Cuándo lo usarás:** renombrar `application.properties` a `application-dev.properties`, mover un script de deploy a la carpeta correcta.

```bash
# Mover archivo a otra carpeta
mv archivo.txt /home/usuario/docs/

# Renombrar un archivo (es el mismo comando)
mv archivo.txt nuevo-nombre.txt

# Mover y renombrar en un solo paso
mv archivo.txt /docs/nuevo-nombre.txt
```

### Copiar

`cp` copia sin eliminar el original. Para carpetas es obligatorio usar `-r` (recursivo); sin esa bandera, `cp` falla con un error.

> 🎯 **Cuándo lo usarás:** hacer un backup de `application.properties` antes de modificarlo, duplicar un módulo como punto de partida para otro.

```bash
# Copiar un archivo
cp archivo.txt copia.txt

# Copiar a otra carpeta
cp archivo.txt /docs/

# Copiar una carpeta completa (recursivo)
cp -r mi-carpeta/ /backup/mi-carpeta/
```

### Eliminar

`rm` es irreversible. La combinación `-rf` (recursivo + forzado) eliminará **todo** lo que haya dentro de la carpeta sin pedir confirmación. Úsala solo cuando estés completamente seguro.

> 🎯 **Cuándo lo usarás:** limpiar `target/` para forzar una compilación limpia (`./mvnw clean`), eliminar logs antiguos en un servidor, borrar una rama de carpetas de prueba.

```bash
# Eliminar un archivo
rm archivo.txt

# Eliminar sin confirmación
rm -f archivo.txt

# Eliminar una carpeta y todo su contenido
rm -rf mi-carpeta/

# ⚠️ CUIDADO: rm -rf no tiene papelera. Es irreversible.
```

---

## 5. Leer archivos desde la terminal

Leer archivos sin abrir un editor es una habilidad crítica en servidores remotos donde no hay GUI. `cat` vuelca el contenido completo; `less` permite navegar por archivos largos; `tail -f` es el comando más usado en producción para **monitorear logs en tiempo real** mientras la aplicación corre.

> 🎯 **Cuándo lo usarás:** revisar `application.properties` en producción, leer el `pom.xml` para verificar dependencias, seguir los logs de Spring Boot con `tail -f` mientras pruebas un endpoint, ver los últimos errores de un archivo de log.

```bash
# Ver todo el contenido de un archivo
cat archivo.txt

# Ver con números de línea
cat -n archivo.txt

# Ver página por página (navegar con espacio / q para salir)
less archivo.txt

# Ver solo las primeras N líneas (por defecto 10)
head archivo.txt
head -n 20 archivo.txt

# Ver solo las últimas N líneas
tail archivo.txt
tail -n 50 archivo.txt

# Seguir un archivo en tiempo real (útil para logs)
tail -f app.log
```

---

## 6. Variables de entorno

Las variables de entorno permiten **configurar la aplicación de forma diferente según el entorno** (desarrollo, staging, producción) sin modificar el código fuente. Spring Boot las lee directamente a través de `${MI_VARIABLE}` en `application.properties` o con `@Value`. Nunca hardcodees contraseñas, tokens o URLs en el código: usa variables de entorno.

> 🎯 **Cuándo lo usarás:** inyectar la URL de base de datos o contraseñas al arrancar Spring Boot, activar un perfil con `SPRING_PROFILES_ACTIVE=prod`, verificar que `JAVA_HOME` apunta al JDK correcto, pasar una API key externa sin subirla al repositorio.

```bash
# Ver TODAS las variables de entorno
env
printenv

# Ver una variable específica
printenv HOME
echo $HOME
echo $PATH

# Definir una variable en la sesión actual (temporal)
export MI_VAR="hola"
echo $MI_VAR

# Usar la variable al ejecutar un comando
MI_VAR="produccion" ./mvnw spring-boot:run

# Variables comunes del sistema
echo $HOME     # /home/usuario
echo $USER     # nombre de usuario
echo $PATH     # rutas donde se buscan ejecutables
echo $PWD      # directorio actual (igual que pwd)
```

> 📌 Las variables definidas con `export` solo existen mientras dure la sesión de terminal. Para hacerlas permanentes, se agregan al archivo `~/.bashrc` o `~/.zshrc`.

---

## 7. Redirección y pipes

La **redirección** envía la salida de un comando a un archivo en lugar de a la pantalla. Los **pipes** (`|`) encadenan comandos: la salida del primero se convierte en la entrada del segundo. Combinados, permiten construir flujos de procesamiento poderosos sin escribir código.

> 🎯 **Cuándo lo usarás:** guardar la salida de `./mvnw test` en un archivo para revisarla después, filtrar logs en tiempo real para ver solo los errores (`tail -f app.log | grep ERROR`), contar cuántos endpoints tiene el proyecto (`grep -r "@GetMapping" src/ | wc -l`), encadenar búsquedas sobre archivos grandes.

### Redirección

```bash
# Redirigir la salida de un comando a un archivo (sobreescribe)
echo "texto" > archivo.txt

# Agregar al final del archivo (sin sobreescribir)
echo "más texto" >> archivo.txt

# Redirigir errores a un archivo
./mvnw test 2> errores.log

# Redirigir salida normal Y errores al mismo archivo
./mvnw test > todo.log 2>&1
```

### Pipes `|`

El pipe (`|`) conecta la salida de un comando con la entrada del siguiente.

```bash
# Filtrar la salida de ls
ls -la | grep ".xml"

# Contar cuántos archivos hay
ls | wc -l

# Ver los primeros 5 resultados de un listado largo
ls -la | head -n 5

# Ver logs filtrando solo errores
cat app.log | grep "ERROR"
```

---

## 8. Búsqueda de texto en archivos

`grep` es una de las herramientas más poderosas de la terminal. Permite encontrar **cualquier texto dentro de cualquier archivo** sin necesidad de abrir un editor. Es especialmente útil para buscar en proyectos grandes o en archivos de log extensos donde el scroll manual sería impracticable.

> 🎯 **Cuándo lo usarás:** encontrar todos los archivos donde se usa `TicketService`, buscar un `NullPointerException` específico en cientos de líneas de log, localizar qué clase define una anotación `@RestController`, filtrar únicamente las líneas de `WARN` y `ERROR` de un log de producción.

```bash
# Buscar texto en un archivo
grep "ERROR" app.log

# Buscar sin distinguir mayúsculas/minúsculas
grep -i "error" app.log

# Mostrar número de línea donde aparece
grep -n "NullPointerException" app.log

# Buscar recursivamente en todos los archivos de una carpeta
grep -r "TicketService" src/

# Buscar e invertir: mostrar líneas que NO contienen el texto
grep -v "DEBUG" app.log

# Buscar múltiples patrones
grep -E "ERROR|WARN" app.log
```

---

## 9. Permisos de archivos (ejecución de scripts)

Linux controla con precisión **quién puede leer, escribir o ejecutar** cada archivo. Cuando clonas un repositorio, los scripts como `mvnw` pueden llegar sin permiso de ejecución y fallar con `Permission denied`. El comando `chmod` ajusta esos permisos.

> 🎯 **Cuándo lo usarás:** dar permisos a `mvnw` justo después de clonar el repositorio (`chmod +x mvnw`), asegurar que un script de deploy pueda ejecutarse en el servidor, restringir la lectura de un archivo `.env` que contiene credenciales (`chmod 600 .env`).

```bash
# Ver permisos de un archivo
ls -l mvnw
# Salida: -rw-r--r-- 1 usuario grupo 10284 mar 19 mvnw
#          └─ sin permiso de ejecución (x)

# Dar permiso de ejecución al propietario
chmod +x mvnw

# Ahora sí se puede ejecutar
./mvnw spring-boot:run

# Permisos en formato octal (el más común en servidores)
chmod 755 script.sh   # rwxr-xr-x
chmod 644 config.txt  # rw-r--r--
```

### Tabla de permisos

| Símbolo | Significado | Octal |
|---------|-------------|-------|
| `r` | Leer | 4 |
| `w` | Escribir | 2 |
| `x` | Ejecutar | 1 |
| `-` | Sin permiso | 0 |

> 📌 Los permisos se agrupan de a 3: **propietario** · **grupo** · **otros**. Por eso `755` = `rwx` + `r-x` + `r-x`.

---

## 10. Comandos de red: `ping` y `curl`

`ping` verifica que hay **conectividad básica** con un host. `curl` es mucho más potente: permite hacer peticiones HTTP completas (GET, POST, PUT, DELETE) directamente desde la terminal, con cabeceras, cuerpo JSON y visualización del código de respuesta. Es el equivalente a Postman, pero sin interfaz gráfica.

> 🎯 **Cuándo lo usarás:** verificar que Spring Boot levantó correctamente en el puerto 8080, probar un endpoint REST desde el servidor sin instalar nada extra, depurar si la API devuelve el código HTTP correcto, hacer peticiones de prueba en un servidor remoto al que solo tienes acceso por terminal.

### ping

```bash
# Verificar conectividad con un host
ping google.com

# Limitar a 4 paquetes
ping -c 4 google.com

# Hacer ping a localhost
ping localhost
ping 127.0.0.1
```

### curl

```bash
# GET básico
curl http://localhost:8080/api/v1/tickets

# GET con respuesta formateada (requiere jq instalado)
curl http://localhost:8080/api/v1/tickets | jq

# GET con cabeceras visibles
curl -v http://localhost:8080/api/v1/tickets

# POST con cuerpo JSON
curl -X POST http://localhost:8080/api/v1/tickets \
  -H "Content-Type: application/json" \
  -d '{"title": "Bug en login", "status": "OPEN"}'

# PUT
curl -X PUT http://localhost:8080/api/v1/tickets/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Bug corregido", "status": "CLOSED"}'

# DELETE
curl -X DELETE http://localhost:8080/api/v1/tickets/1

# Ver solo el código de respuesta HTTP
curl -o /dev/null -s -w "%{http_code}" http://localhost:8080/api/v1/tickets
```

---

## 11. Atajos y productividad

Conocer los atajos de teclado marca la diferencia entre una experiencia frustrante y una fluida. **`Ctrl + R`** es especialmente valioso: busca en el historial completo de comandos a medida que escribes, ahorrando el tiempo de reescribir comandos largos. **`Tab`** autocompleta rutas y comandos, evitando errores tipográficos.

> 🎯 **Cuándo lo usarás:** recuperar rápidamente el comando `./mvnw spring-boot:run` del historial, cancelar un proceso de Maven colgado con `Ctrl + C`, autocompletar rutas largas de `src/main/java/com/empresa/...` con un solo `Tab`.

| Atajo | Acción |
|-------|--------|
| `Tab` | Autocompletar nombre de archivo o comando |
| `Tab Tab` | Mostrar todas las opciones de autocompletado |
| `↑` / `↓` | Navegar por el historial de comandos |
| `Ctrl + C` | Cancelar el comando en ejecución |
| `Ctrl + Z` | Suspender el proceso actual |
| `Ctrl + L` | Limpiar la pantalla (equivale a `clear`) |
| `Ctrl + A` | Ir al inicio de la línea |
| `Ctrl + E` | Ir al final de la línea |
| `Ctrl + R` | Buscar en el historial de comandos |
| `!!` | Repetir el último comando |
| `history` | Ver historial completo de comandos |

---

## 12. Referencia rápida (cheat sheet)

```bash
# ─── NAVEGACIÓN ───────────────────────────────────────
pwd                        # directorio actual
cd /ruta/absoluta          # ir a ruta absoluta
cd carpeta                 # ir a subcarpeta (relativo)
cd ..                      # subir un nivel
cd ~                       # ir al home
cd -                       # volver al directorio anterior

# ─── LISTADO ──────────────────────────────────────────
ls                         # listar
ls -la                     # listar con detalles y ocultos
ls -lh                     # listar con tamaños legibles

# ─── ARCHIVOS Y CARPETAS ──────────────────────────────
mkdir carpeta              # crear carpeta
mkdir -p a/b/c             # crear carpetas anidadas
touch archivo.txt          # crear archivo vacío
cp origen destino          # copiar archivo
cp -r origen/ destino/     # copiar carpeta
mv origen destino          # mover o renombrar
rm archivo.txt             # eliminar archivo
rm -rf carpeta/            # eliminar carpeta (¡cuidado!)

# ─── LECTURA ──────────────────────────────────────────
cat archivo.txt            # ver contenido completo
less archivo.txt           # ver página a página
head -n 20 archivo.txt     # primeras 20 líneas
tail -n 20 archivo.txt     # últimas 20 líneas
tail -f app.log            # seguir en tiempo real

# ─── BÚSQUEDA ─────────────────────────────────────────
grep "texto" archivo.txt   # buscar texto
grep -r "texto" src/       # buscar recursivo
grep -n "texto" archivo    # con número de línea

# ─── VARIABLES ────────────────────────────────────────
export VAR="valor"         # definir variable
echo $VAR                  # leer variable
printenv                   # ver todas las variables

# ─── RED ──────────────────────────────────────────────
ping -c 4 host             # verificar conectividad
curl http://localhost:8080  # petición GET
curl -X POST ... -d '{}'   # petición POST con JSON

# ─── PERMISOS ─────────────────────────────────────────
chmod +x archivo           # dar permiso de ejecución
chmod 755 archivo          # rwxr-xr-x
ls -l                      # ver permisos

# ─── PIPES Y REDIRECCIÓN ──────────────────────────────
cmd1 | cmd2                # pipe: salida de cmd1 → entrada de cmd2
echo "texto" > archivo     # sobreescribir archivo
echo "texto" >> archivo    # agregar al final
cmd 2> errores.log         # redirigir errores
```

---

> 🔗 Ver la guía equivalente para Windows: [CMD y PowerShell](./windows.md)

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
