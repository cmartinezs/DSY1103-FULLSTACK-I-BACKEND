# 🛠️ Herramientas para Desarrolladores

> **Nivel de entrada:** cualquiera — este extra es una guía de referencia, no un tutorial lineal.  
> **Objetivo:** conocer el ecosistema de herramientas que un desarrollador backend usa en el día a día y saber para qué sirve cada una.  
> **Contexto:** Java 21 + Spring Boot 4 + MySQL (XAMPP) + APIs REST.

---

## ¿Por qué importa conocer las herramientas?

Un programador no trabaja solo con el lenguaje. El entorno que lo rodea —el IDE, el cliente HTTP, el gestor de base de datos, las utilidades online— define en gran medida su productividad y la calidad de su trabajo.

> 💡 Conocer la herramienta correcta para cada tarea es tan importante como saber programar.

---

## Mapa de herramientas

```
🛠️ Herramientas para Desarrolladores
│
├── 🖊️  IDEs y editores          → IntelliJ IDEA · VS Code
├── 🌐  Clientes HTTP / API      → Postman · Insomnia · Hoppscotch · curl
├── 🗄️  Gestores de bases de datos → DBeaver · pgAdmin · MySQL Workbench
├── 🐳  Contenedores             → Docker Desktop
├── 🌿  Git GUI                  → GitKraken · GitHub Desktop · Sourcetree
├── 🔍  Calidad de código        → SonarLint · CheckStyle
├── 🧰  Utilidades online        → start.spring.io · jwt.io · regex101 · crontab.guru
├── 🔬  Navegador y DevTools     → Chrome / Firefox DevTools
└── 📄  Documentación de API     → Swagger UI · SpringDoc OpenAPI
```

---

## Índice

| # | Categoría | Herramientas |
|---|-----------|-------------|
| 1 | [IDEs y editores](#1-ides-y-editores) | IntelliJ IDEA, VS Code |
| 2 | [Clientes HTTP / API](#2-clientes-http--api) | Postman, Insomnia, Hoppscotch, curl |
| 3 | [Gestores de bases de datos](#3-gestores-de-bases-de-datos) | DBeaver, pgAdmin, MySQL Workbench |
| 4 | [Contenedores](#4-contenedores) | Docker Desktop |
| 5 | [Git GUI](#5-git-gui) | GitKraken, GitHub Desktop, Sourcetree |
| 6 | [Calidad de código](#6-calidad-de-código) | SonarLint, CheckStyle |
| 7 | [Utilidades online](#7-utilidades-online) | start.spring.io, jwt.io, regex101, JSONLint, crontab.guru |
| 8 | [Navegador y DevTools](#8-navegador-y-devtools) | Chrome / Firefox DevTools |
| 9 | [Documentación de API](#9-documentación-de-api) | Swagger UI, SpringDoc OpenAPI |
| 10 | [Tabla resumen](#10-tabla-resumen) | Todas |

---

## 1. IDEs y editores

Un **IDE** (Integrated Development Environment) es el entorno donde se escribe, ejecuta y depura el código. Elegir uno bueno impacta directamente en la productividad.

---

### IntelliJ IDEA ⭐ Recomendado para este curso

| Dato | Detalle |
|------|---------|
| **Fabricante** | JetBrains |
| **Ediciones** | Community (gratis, sin soporte Spring) · Ultimate (de pago, licencia educativa gratuita) |
| **Descarga** | [jetbrains.com/idea](https://www.jetbrains.com/idea/) |
| **Ideal para** | Java, Spring Boot, Kotlin, Maven/Gradle |

> ⚠️ **Este curso requiere IntelliJ IDEA Ultimate** (o la licencia educativa gratuita). La edición Community **no incluye** soporte para Spring Boot, HTTP Client ni las herramientas de base de datos integradas.
>
> 🎓 Tramita la **licencia educativa gratuita** de JetBrains en [jetbrains.com/student](https://www.jetbrains.com/student/) — solo necesitas un correo institucional o cuenta de GitHub Student.

**¿Por qué IntelliJ?**

- Autocompletado inteligente que entiende el contexto de Spring Boot (anota con `@Autowired`, sugiere beans, detecta errores de configuración).
- Refactorizaciones seguras: renombrar una clase actualiza todas las referencias automáticamente.
- Integración nativa con Maven, Git, Docker, bases de datos y HTTP Client.
- Debugger visual potente: inspección de variables, breakpoints condicionales, evaluación de expresiones.

**Características clave para el curso**

| Función | Cómo usarla |
|---------|-------------|
| Ejecutar la app | Botón ▶ o `Shift+F10` |
| Debuggear | `Shift+F9`, poner breakpoints con clic en el margen |
| Buscar en todo el proyecto | `Ctrl+Shift+F` |
| Navegar a una clase | `Ctrl+N` |
| Navegar a un símbolo | `Ctrl+Shift+Alt+N` |
| Ver todos los errores | Ventana *Problems* |
| HTTP Client integrado | Archivos `.http` — prueba endpoints sin salir del IDE |
| Database integrada | Vista *Database* — conectar a PostgreSQL, MySQL, H2 |

**Edición Community vs. Ultimate**

| Función | Community | Ultimate |
|---------|-----------|----------|
| Java y Kotlin | ✅ | ✅ |
| Spring Boot support | ❌ | ✅ |
| HTTP Client | ❌ | ✅ |
| Database tools | ❌ | ✅ |
| Frameworks web (HTML/JS) | ❌ | ✅ |


---

### Visual Studio Code

| Dato | Detalle |
|------|---------|
| **Fabricante** | Microsoft |
| **Precio** | Gratuito y open source |
| **Descarga** | [code.visualstudio.com](https://code.visualstudio.com/) |
| **Ideal para** | JavaScript, TypeScript, Python, Front End — también Java con extensiones |

**Extensiones útiles para Java con VS Code**

- `Extension Pack for Java` — soporte completo para Java
- `Spring Boot Extension Pack` — soporte para Spring Boot
- `REST Client` — archivo `.http` para probar APIs (similar al HTTP Client de IntelliJ)
- `GitLens` — historial de Git enriquecido
- `Prettier` — formateo automático de código

> 💡 VS Code es excelente para el **frontend** y para proyectos JavaScript/TypeScript. Para el backend Java con Spring Boot, IntelliJ IDEA es significativamente más potente.

---

## 2. Clientes HTTP / API

Un **cliente HTTP** permite enviar peticiones a una API REST y ver la respuesta sin necesidad de un navegador o un frontend. Es la herramienta principal para probar y depurar endpoints.

---

### Postman ⭐ Más popular

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis (plan básico) |
| **Descarga** | [postman.com](https://www.postman.com/) |
| **Plataformas** | Windows, macOS, Linux, Web |

**Conceptos clave de Postman**

| Concepto | Descripción |
|----------|-------------|
| **Request** | Una petición HTTP configurada (método, URL, headers, body) |
| **Collection** | Carpeta que agrupa requests relacionados (ej.: todas las peticiones de `tickets`) |
| **Environment** | Conjunto de variables (ej.: `{{BASE_URL}}`, `{{TOKEN}}`) que cambian según el entorno |
| **Tests** | Scripts JavaScript que validan automáticamente la respuesta |
| **Pre-request script** | Script que se ejecuta antes de enviar la petición |

**Flujo típico en el curso**

```
1. Abrir Postman
2. Crear una colección "Tickets API"
3. Agregar un Environment con variable BASE_URL = http://localhost:8080
4. Crear requests: GET /api/v1/tickets · POST /api/v1/tickets · etc.
5. Ejecutar y verificar la respuesta (status code, body JSON)
```

**Ejemplo de petición POST con body JSON**

```
Method: POST
URL: {{BASE_URL}}/api/v1/tickets
Headers:
  Content-Type: application/json
Body (raw JSON):
{
  "title": "Error en login",
  "priority": "HIGH"
}
```

---

### Insomnia

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis (plan básico) |
| **Descarga** | [insomnia.rest](https://insomnia.rest/) |
| **Plataformas** | Windows, macOS, Linux |

Interfaz más limpia y directa que Postman. Muy popular entre desarrolladores que prefieren simplicidad. Soporta REST, GraphQL y gRPC.

---

### Hoppscotch

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis y open source |
| **Acceso** | [hoppscotch.io](https://hoppscotch.io/) (sin instalación) |
| **Plataformas** | Web, PWA instalable |

Alternativa liviana 100% en el navegador. Ideal para pruebas rápidas sin instalar nada.

---

### curl (línea de comandos)

`curl` es la herramienta de línea de comandos más usada para hacer peticiones HTTP. Viene preinstalada en Linux y macOS; en Windows está disponible desde PowerShell.

**Comandos esenciales**

```bash
# GET
curl http://localhost:8080/api/v1/tickets

# GET con formato JSON bonito (requiere jq instalado)
curl http://localhost:8080/api/v1/tickets | jq

# POST con body JSON
curl -X POST http://localhost:8080/api/v1/tickets \
  -H "Content-Type: application/json" \
  -d '{"title": "Error en login", "priority": "HIGH"}'

# PUT
curl -X PUT http://localhost:8080/api/v1/tickets/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Error actualizado", "priority": "LOW"}'

# DELETE
curl -X DELETE http://localhost:8080/api/v1/tickets/1

# Ver headers de respuesta
curl -i http://localhost:8080/api/v1/tickets

# Verbose (ver todo el detalle de la petición)
curl -v http://localhost:8080/api/v1/tickets
```

> 💡 `curl` es indispensable en servidores Linux donde no hay interfaz gráfica. Aprenderlo es parte de ser un desarrollador backend completo.

---

### Comparación de clientes HTTP

| Herramienta | Instalación | GUI | Colecciones | Scripting | Colaboración | Ideal para |
|-------------|-------------|-----|-------------|-----------|--------------|-----------|
| Postman | App | ✅ | ✅ | ✅ JS | ✅ | Equipos, pruebas complejas |
| Insomnia | App | ✅ | ✅ | ✅ JS | ✅ | Simplicidad, GraphQL |
| Hoppscotch | Web | ✅ | ✅ | ❌ | ✅ | Pruebas rápidas sin instalar |
| curl | Terminal | ❌ | ❌ | ✅ Bash | ❌ | Scripts, servidores |
| HTTP Client (IntelliJ) | IDE | Archivos `.http` | ✅ | ✅ JS | ❌ | Integrado al flujo de trabajo |

---

## 3. Gestores de bases de datos

Un **gestor de bases de datos** (o cliente de BD) es una interfaz visual para conectarse a una base de datos, ejecutar consultas SQL, ver tablas y gestionar datos.

---

### DBeaver ⭐ Recomendado

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis (Community Edition) |
| **Descarga** | [dbeaver.io](https://dbeaver.io/) |
| **Plataformas** | Windows, macOS, Linux |
| **Soporta** | PostgreSQL, MySQL, MariaDB, H2, SQLite, Oracle, SQL Server y +80 más |

**¿Por qué DBeaver?**

- Conecta a **cualquier base de datos** con un único cliente.
- Editor SQL con autocompletado y resaltado de sintaxis.
- Vista de tablas, relaciones y datos como hoja de cálculo.
- Exportar/importar datos en CSV, JSON, SQL.

**Conexión típica a H2 (base de datos en memoria de Spring Boot)**

```
Driver: H2 Embedded
URL: jdbc:h2:mem:testdb   (o la que configure en application.yml)
User: sa
Password: (vacío)
```

**Conexión a PostgreSQL**

```
Host: localhost
Port: 5432
Database: nombre_db
User: postgres
Password: tu_contraseña
```

---

### pgAdmin

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis y open source |
| **Descarga** | [pgadmin.org](https://www.pgadmin.org/) |
| **Ideal para** | PostgreSQL exclusivamente |

Herramienta oficial de administración de PostgreSQL. Más completa para tareas avanzadas de administración (usuarios, roles, backups), pero más pesada que DBeaver para el uso diario.

---

### MySQL Workbench

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis |
| **Descarga** | [mysql.com/products/workbench](https://www.mysql.com/products/workbench/) |
| **Ideal para** | MySQL y MariaDB exclusivamente |

Herramienta oficial de Oracle para MySQL. Incluye modelado visual de base de datos (diagramas ER), migración y administración.

---

### XAMPP ⭐ Recomendado para este curso

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis y open source |
| **Descarga** | [apachefriends.org](https://www.apachefriends.org/) |
| **Plataformas** | Windows, macOS, Linux |
| **Incluye** | Apache · **MySQL (MariaDB)** · PHP · phpMyAdmin |

**¿Por qué XAMPP en este curso?**

XAMPP es la forma más sencilla de instalar MySQL en el equipo local sin configuración manual. Con un solo instalador tienes MySQL corriendo y disponible para Spring Boot en minutos.

> 💡 Para este curso **solo necesitas MySQL** — Apache y PHP son componentes extra que XAMPP trae pero no son obligatorios.

**Pasos para usarlo con Spring Boot**

```
1. Instalar XAMPP
2. Abrir el Panel de Control de XAMPP
3. Iniciar el módulo "MySQL" (botón Start)
4. MySQL queda disponible en localhost:3306
5. Entrar a phpMyAdmin → http://localhost/phpmyadmin
6. Crear la base de datos del proyecto (ej.: tickets_db)
7. Configurar Spring Boot para conectarse a esa BD
```

**Configuración en `application.yml` (Spring Boot 4)**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tickets_db
    username: root
    password:          # Por defecto XAMPP no tiene contraseña
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect
```

**Dependencia Maven necesaria**

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

> ⚠️ XAMPP usa **MariaDB** (fork compatible de MySQL). Funciona perfectamente con el driver de MySQL y con Spring Data JPA sin ningún cambio adicional.

---

### Comparación de gestores de BD

| Herramienta | Precio | Multi-BD | Diagramas ER | Peso | Ideal para |
|-------------|--------|----------|--------------|------|-----------|
| **XAMPP** | Gratis | ❌ MySQL/MariaDB | ❌ | Ligero | **Instalar MySQL fácil — este curso** |
| DBeaver Community | Gratis | ✅ +80 | ✅ | Medio | Explorar y consultar la BD |
| pgAdmin | Gratis | ❌ Solo PG | ❌ | Medio | Administración PostgreSQL |
| MySQL Workbench | Gratis | ❌ Solo MySQL | ✅ | Pesado | Administración MySQL avanzada |
| DataGrip (JetBrains) | De pago | ✅ | ✅ | Pesado | Profesional avanzado |

---

## 4. Contenedores

### Docker Desktop

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis para uso personal y educativo |
| **Descarga** | [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/) |
| **Plataformas** | Windows, macOS, Linux |

**¿Qué es Docker?**

Docker permite **empaquetar una aplicación y todas sus dependencias en un contenedor** que corre igual en cualquier máquina. Es la solución al problema clásico: *"en mi máquina funciona"*.

**Conceptos clave**

| Concepto | Descripción | Analogía |
|----------|-------------|---------|
| **Imagen** | Plantilla inmutable (el "molde") | Clase en Java |
| **Contenedor** | Instancia de una imagen en ejecución | Objeto en Java |
| **Dockerfile** | Instrucciones para construir una imagen | `pom.xml` pero para el entorno |
| **docker-compose.yml** | Orquesta múltiples contenedores | Un "proyecto" de contenedores |
| **Registry** | Repositorio de imágenes | Maven Central, pero para contenedores |
| **Docker Hub** | Registry público oficial | [hub.docker.com](https://hub.docker.com/) |

**Comandos esenciales**

```bash
# Descargar una imagen
docker pull postgres:16

# Ejecutar PostgreSQL en un contenedor
docker run --name mi-postgres \
  -e POSTGRES_PASSWORD=secreto \
  -e POSTGRES_DB=tickets_db \
  -p 5432:5432 \
  -d postgres:16

# Ver contenedores en ejecución
docker ps

# Ver logs de un contenedor
docker logs mi-postgres

# Detener un contenedor
docker stop mi-postgres

# Eliminar un contenedor
docker rm mi-postgres
```

**`docker-compose.yml` típico para el curso**

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    container_name: tickets-db
    environment:
      POSTGRES_DB: tickets_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secreto
    ports:
      - "5432:5432"
    volumes:
      - tickets_data:/var/lib/postgresql/data

volumes:
  tickets_data:
```

```bash
# Levantar todos los servicios del compose
docker compose up -d

# Bajar todos los servicios
docker compose down
```

> 💡 Docker es la forma más práctica de tener una base de datos PostgreSQL local sin instalarla directamente en el sistema operativo. Se levanta y se baja con un comando.

---

## 5. Git GUI

Los clientes gráficos de Git facilitan visualizar el historial, las ramas y los conflictos de merge sin usar la terminal. No reemplazan a Git, pero complementan su uso.

---

### GitKraken

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis (plan básico) · Pro de pago |
| **Descarga** | [gitkraken.com](https://www.gitkraken.com/) |
| **Ideal para** | Visualización de historial complejo, GitFlow |

Interfaz visual más completa del mercado. Excelente para entender el grafo de commits y ramas. Tiene soporte integrado para **GitFlow** (crea y cierra ramas con botones).

---

### GitHub Desktop

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis y open source |
| **Descarga** | [desktop.github.com](https://desktop.github.com/) |
| **Ideal para** | Principiantes, flujo simple con GitHub |

Interfaz minimalista oficial de GitHub. Ideal para quien empieza con Git y quiere lo esencial: clone, commit, push, pull, branch, merge.

---

### Sourcetree

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis |
| **Descarga** | [sourcetreeapp.com](https://www.sourcetreeapp.com/) |
| **Ideal para** | GitFlow visual, Atlassian/Bitbucket |

Cliente gratuito de Atlassian (creadores de Jira y Confluence). Tiene soporte para GitFlow y se integra bien con Bitbucket.

---

### Comparación de clientes Git GUI

| Herramienta | Precio | Plataformas | GitFlow | Integración | Ideal para |
|-------------|--------|-------------|---------|-------------|-----------|
| GitKraken | Freemium | Win/Mac/Linux | ✅ Visual | GitHub, GitLab, Bitbucket | Uso avanzado |
| GitHub Desktop | Gratis | Win/Mac | ❌ | GitHub | Principiantes |
| Sourcetree | Gratis | Win/Mac | ✅ Visual | GitHub, Bitbucket | Flujo Atlassian |
| IntelliJ Git | Incluido | Win/Mac/Linux | ❌ | GitHub, GitLab | Uso integrado |

---

## 6. Calidad de código

### SonarLint ⭐ Recomendado

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis |
| **Instalación** | Plugin para IntelliJ IDEA / VS Code |
| **Sitio** | [sonarlint.org](https://www.sonarlint.org/) |

**¿Qué hace SonarLint?**

Analiza el código en tiempo real mientras escribes y detecta:

- **Bugs**: lógica incorrecta que puede causar errores en producción.
- **Code Smells**: código que funciona pero es difícil de mantener.
- **Vulnerabilidades de seguridad**: uso de APIs inseguras, exposición de datos sensibles.
- **Duplicación de código**: bloques copiados que deberían estar en un método.

**Instalación en IntelliJ IDEA**

```
File → Settings → Plugins → Marketplace → buscar "SonarLint" → Install
```

> 💡 SonarLint es como tener un code reviewer automático que revisa cada línea mientras la escribes. Instálalo desde el primer día.

---

### CheckStyle

| Dato | Detalle |
|------|---------|
| **Precio** | Gratis y open source |
| **Instalación** | Plugin para IntelliJ IDEA · integración con Maven |
| **Sitio** | [checkstyle.org](https://checkstyle.org/) |

Verifica que el código cumple una **guía de estilo** (convenciones de nombrado, longitud de líneas, orden de imports, etc.). Se puede configurar con estilos predefinidos como Google Java Style o Sun Coding Standards.

---

## 7. Utilidades online

Herramientas web que se usan puntualmente durante el desarrollo, sin necesidad de instalación.

---

### start.spring.io — Spring Initializr ⭐

| Dato | Detalle |
|------|---------|
| **URL** | [start.spring.io](https://start.spring.io/) |
| **Uso** | Generador de proyectos Spring Boot |

Genera la estructura inicial de un proyecto Spring Boot con las dependencias seleccionadas. Produce un ZIP listo para abrir en IntelliJ.

**Opciones clave al crear un proyecto**

| Campo | Recomendado para el curso |
|-------|--------------------------|
| Project | Maven |
| Language | Java |
| Spring Boot | **4.x** (última versión estable, sin SNAPSHOT) |
| Java | **21** |
| Dependencias | Spring Web · Spring Data JPA · Lombok · **MySQL Driver** |

---

### jwt.io — Debugger de JWT

| Dato | Detalle |
|------|---------|
| **URL** | [jwt.io](https://jwt.io/) |
| **Uso** | Decodificar y verificar JSON Web Tokens |

Permite pegar un token JWT y ver su contenido (header, payload, signature) de forma visual. Útil cuando se trabaja con autenticación.

**Anatomía de un JWT**

```
eyJhbGciOiJIUzI1NiJ9      ← Header  (algoritmo)
.eyJ1c2VyIjoiYWRtaW4ifQ  ← Payload (datos)
.SflKxwRJSMeKKF2QT4fwpM  ← Signature (verificación)
```

---

### regex101 — Probador de expresiones regulares

| Dato | Detalle |
|------|---------|
| **URL** | [regex101.com](https://regex101.com/) |
| **Uso** | Escribir, probar y entender expresiones regulares |

Permite probar una regex contra texto de ejemplo y explica paso a paso qué hace cada parte. Soporta el flavour de Java (`JAVA 8`).

**Ejemplo de regex útil en el curso**

```regex
^\+?[0-9]{8,15}$    ← Validar número de teléfono
^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$  ← Validar email
^[A-Z][a-z]+([ ][A-Z][a-z]+)*$  ← Nombre propio
```

---

### JSONLint / JSON Formatter

| Herramienta | URL | Uso |
|-------------|-----|-----|
| JSONLint | [jsonlint.com](https://jsonlint.com/) | Validar que un JSON es sintácticamente correcto |
| JSON Formatter | [jsonformatter.curiousconcept.com](https://jsonformatter.curiousconcept.com/) | Formatear JSON comprimido en modo legible |

Útiles para verificar el body de una petición o la respuesta de una API cuando el JSON viene en una sola línea.

---

### crontab.guru — Expresiones Cron

| Dato | Detalle |
|------|---------|
| **URL** | [crontab.guru](https://crontab.guru/) |
| **Uso** | Construir y entender expresiones cron para tareas programadas |

Spring Boot incluye `@Scheduled` para tareas programadas, que usa expresiones cron. Esta herramienta las explica en lenguaje natural.

```java
@Scheduled(cron = "0 0 9 * * MON-FRI")
// "A las 9:00 AM de lunes a viernes"
public void enviarReporte() { ... }
```

---

### Otros recursos online útiles

| Herramienta | URL | Para qué |
|-------------|-----|---------|
| HTTP Status Codes | [httpstatuses.io](https://httpstatuses.io/) | Referencia rápida de todos los status codes |
| Mockaroo | [mockaroo.com](https://www.mockaroo.com/) | Generar datos de prueba realistas (CSV, JSON, SQL) |
| Carbon | [carbon.now.sh](https://carbon.now.sh/) | Crear imágenes bonitas de fragmentos de código |
| Excalidraw | [excalidraw.com](https://excalidraw.com/) | Diagramas y bocetos de arquitectura |
| draw.io | [app.diagrams.net](https://app.diagrams.net/) | Diagramas UML, ER, arquitectura |

---

## 8. Navegador y DevTools

Las **DevTools** (herramientas de desarrollo del navegador) son esenciales para depurar el frontend y las peticiones HTTP que se hacen a la API.

---

### Chrome DevTools / Firefox DevTools

**Cómo abrir:** `F12` o clic derecho → *Inspeccionar*

**Pestañas relevantes para el curso**

| Pestaña | Uso principal |
|---------|--------------|
| **Network** | Ver todas las peticiones HTTP, sus headers, body y respuestas |
| **Console** | Ver errores de JavaScript y ejecutar código en el contexto de la página |
| **Application** | Ver cookies, localStorage, sessionStorage |
| **Elements** | Inspeccionar y modificar el HTML/CSS en vivo |

**Pestaña Network — Lo más útil**

```
1. Abrir DevTools → pestaña Network
2. Realizar la acción en el frontend (clic en botón, enviar formulario)
3. Ver la petición generada: URL, método, status code, body enviado, respuesta recibida
4. Copiar como cURL: clic derecho → Copy → Copy as cURL
```

> 💡 La pestaña **Network** permite ver exactamente qué petición envía el navegador a la API. Si algo falla, ese es el primer lugar donde buscar.

---

## 9. Documentación de API

### Swagger UI / SpringDoc OpenAPI ⭐

| Dato | Detalle |
|------|---------|
| **Dependencia** | `springdoc-openapi-starter-webmvc-ui` |
| **URL local** | `http://localhost:8080/swagger-ui.html` |
| **Estándar** | OpenAPI 3.0 |

**¿Qué es Swagger UI?**

Una interfaz web interactiva que documenta automáticamente todos los endpoints de una API REST a partir de las anotaciones de Spring Boot. Permite probar endpoints directamente desde el navegador.

**Agregar SpringDoc al proyecto**

En `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

**Anotaciones opcionales para mejorar la documentación**

```java
@Tag(name = "Tickets", description = "Gestión de tickets de soporte")
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @Operation(summary = "Obtener todos los tickets")
    @ApiResponse(responseCode = "200", description = "Lista de tickets")
    @GetMapping
    public List<TicketResponse> getAll() { ... }

    @Operation(summary = "Crear un nuevo ticket")
    @ApiResponse(responseCode = "201", description = "Ticket creado exitosamente")
    @PostMapping
    public ResponseEntity<TicketResponse> create(@RequestBody TicketRequest request) { ... }
}
```

**URLs importantes**

| URL | Contenido |
|-----|-----------|
| `/swagger-ui.html` | Interfaz visual interactiva |
| `/v3/api-docs` | Especificación OpenAPI en JSON |
| `/v3/api-docs.yaml` | Especificación OpenAPI en YAML |

---

## 10. Tabla resumen

| Categoría | Herramienta | Precio | Imprescindible |
|-----------|-------------|--------|----------------|
| **Lenguaje** | Java 21 JDK | Gratis | ⭐⭐⭐ |
| **Framework** | Spring Boot 4 | Gratis | ⭐⭐⭐ |
| **Build tool** | Maven (vía `mvnw`) | Gratis | ⭐⭐⭐ |
| **IDE** | IntelliJ IDEA Ultimate | Licencia educativa gratuita | ⭐⭐⭐ |
| **Cliente HTTP** | Postman | Gratis | ⭐⭐⭐ |
| **Cliente HTTP** | curl | Preinstalado | ⭐⭐⭐ |
| **Cliente HTTP** | Insomnia | Gratis | ⭐⭐ |
| **Cliente HTTP** | Hoppscotch | Web gratis | ⭐⭐ |
| **Base de datos** | XAMPP (MySQL) | Gratis | ⭐⭐⭐ |
| **Base de datos** | DBeaver Community | Gratis | ⭐⭐ |
| **Contenedores** | Docker Desktop | Gratis | ⭐ |
| **Control de versiones** | Git | Gratis | ⭐⭐⭐ |
| **Git GUI** | GitHub Desktop | Gratis | ⭐⭐ (Opcional) |
| **Git GUI** | GitKraken | Freemium | ⭐ |
| **Calidad** | SonarLint (plugin) | Gratis | ⭐⭐⭐ |
| **Online** | start.spring.io | Gratis | ⭐⭐⭐ |
| **Online** | jwt.io | Gratis | ⭐⭐ |
| **Online** | regex101 | Gratis | ⭐⭐ |
| **Online** | JSONLint | Gratis | ⭐⭐ |
| **Navegador** | Chrome / Firefox DevTools | Incluido | ⭐⭐⭐ |
| **Documentación API** | SpringDoc OpenAPI | Gratis | ⭐⭐⭐ |

**Leyenda:** ⭐⭐⭐ Obligatorio desde el primer día · ⭐⭐ Útil en etapas posteriores · ⭐ Opcional / avanzado

---

## Kit mínimo para arrancar el curso

Instala estas herramientas **en orden** antes de la primera clase:

| # | Herramienta | Por qué |
|---|-------------|---------|
| 1 | **Java 21 JDK** | El lenguaje del curso — sin él nada compila |
| 2 | **IntelliJ IDEA Ultimate** (licencia educativa) | IDE del curso con soporte completo para Spring Boot |
| 3 | **Maven** | Gestor de dependencias y build — viene integrado como `mvnw` en cada proyecto |
| 4 | **Spring Boot 4** | El framework — se descarga automáticamente vía `start.spring.io` + Maven |
| 5 | **XAMPP** | La forma más fácil de instalar MySQL localmente; inicia el módulo MySQL con un clic |
| 6 | **Postman** | Probar y explorar los endpoints de la API |
| 7 | **Git** | Control de versiones — imprescindible para cualquier proyecto |
| 8 | **GitHub Desktop** *(Opcional)* | Interfaz gráfica para Git si prefieres no usar la terminal |

**Pasos de instalación sugeridos**

```
1. Instalar Java 21 JDK
   → adoptium.net (Temurin 21 LTS) o jetbrains.com/jdk

2. Instalar IntelliJ IDEA Ultimate
   → jetbrains.com/idea
   → Activar con licencia educativa: jetbrains.com/student

3. Instalar XAMPP
   → apachefriends.org
   → Abrir el Panel de Control → iniciar módulo MySQL

4. Instalar Postman
   → postman.com

5. Instalar Git
   → git-scm.com

6. (Opcional) Instalar GitHub Desktop
   → desktop.github.com

7. Crear el primer proyecto en start.spring.io
   → Project: Maven · Java 21 · Spring Boot 4.x
   → Dependencias: Spring Web · Spring Data JPA · Lombok · MySQL Driver
   → Abrir en IntelliJ IDEA
```

> ✅ Maven **no requiere instalación separada** — cada proyecto Spring Boot incluye el Maven Wrapper (`mvnw` / `mvnw.cmd`) que se auto-descarga la versión correcta.

Con este kit puedes desarrollar, probar y gestionar cualquier API REST del curso desde el primer día.

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

