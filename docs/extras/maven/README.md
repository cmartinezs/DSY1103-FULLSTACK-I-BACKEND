# 🏗️ Maven — Gestión de Dependencias y Ciclo de Vida

## ¿Qué es Maven?

**Apache Maven** es una herramienta de **gestión de proyectos y automatización de builds** para Java. Se encarga de:

- Descargar y gestionar las **dependencias** del proyecto (librerías externas)
- Compilar, testear, empaquetar y desplegar el código de forma estandarizada
- Definir la **estructura del proyecto** de forma convencional

> 📌 Maven sigue el principio de **"convención sobre configuración"**: si sigues la estructura estándar, no necesitas configurar casi nada.

---

## `pom.xml` — El corazón del proyecto

El archivo **`pom.xml`** (Project Object Model) es el archivo de configuración central de Maven. Define qué es el proyecto, qué dependencias necesita y cómo construirlo.

### Estructura básica

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- ① Herencia: configuración base de Spring Boot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.3</version>
    </parent>

    <!-- ② Identidad del proyecto -->
    <groupId>cl.duoc.fullstack</groupId>
    <artifactId>Tickets</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>Tickets</name>
    <description>API REST de gestión de tickets</description>

    <!-- ③ Propiedades globales -->
    <properties>
        <java.version>21</java.version>
    </properties>

    <!-- ④ Dependencias -->
    <dependencies>
        <!-- ... -->
    </dependencies>

    <!-- ⑤ Plugins de build -->
    <build>
        <plugins>
            <!-- ... -->
        </plugins>
    </build>

</project>
```

---

## Coordenadas Maven (GAV)

Cada dependencia se identifica con tres coordenadas: **G**roupId · **A**rtifactId · **V**ersion (GAV).

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>   <!-- organización/empresa -->
    <artifactId>spring-boot-starter-web</artifactId> <!-- nombre del módulo -->
    <version>4.0.3</version>                       <!-- versión (omitible con parent) -->
</dependency>
```

> 💡 Cuando se usa `<parent>` de Spring Boot, la versión de muchas dependencias se gestiona automáticamente (no hace falta escribirla).

---

## Dependencias en el proyecto Tickets

```xml
<dependencies>

    <!-- Spring Web MVC: para construir APIs REST -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>

    <!-- DevTools: recarga automática en desarrollo -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Lombok: elimina código boilerplate (getters, setters, etc.) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Tests: JUnit + Spring Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc-test</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

---

## Scopes de dependencias

El **scope** define en qué etapa del ciclo de vida está disponible una dependencia:

| Scope | Disponible en | Incluida en el JAR final | Ejemplo |
|-------|--------------|--------------------------|---------|
| `compile` (defecto) | Compilación, tests, ejecución | ✅ Sí | Spring Web |
| `runtime` | Ejecución y tests, no compilación | ✅ Sí | DevTools, drivers JDBC |
| `test` | Solo tests | ❌ No | JUnit, Mockito |
| `provided` | Compilación, no empaquetado | ❌ No | Servlet API (la provee el servidor) |
| `optional` | Solo para el proyecto actual | ❌ No (no se hereda) | Lombok |

---

## Ciclo de vida de Maven

Maven define tres **ciclos de vida** principales. El más usado es `default`:

```
validate → compile → test → package → verify → install → deploy
```

| Fase | Qué hace |
|------|----------|
| `validate` | Verifica que el proyecto es válido y tiene la info necesaria |
| `compile` | Compila el código fuente en `.class` |
| `test` | Ejecuta los tests unitarios |
| `package` | Empaqueta el código compilado en un JAR o WAR |
| `verify` | Verifica la integridad del paquete |
| `install` | Instala el paquete en el repositorio local (`~/.m2`) |
| `deploy` | Sube el paquete al repositorio remoto |

> 📌 Cada fase **incluye todas las anteriores**: al ejecutar `package`, Maven también ejecuta `validate`, `compile` y `test`.

---

## Comandos Maven más usados

En el proyecto Tickets se usa el **Maven Wrapper** (`mvnw`), que garantiza que todos usen la misma versión de Maven sin necesidad de instalarlo globalmente.

```bash
# Compilar el proyecto
./mvnw compile

# Ejecutar los tests
./mvnw test

# Compilar, testear y empaquetar en un JAR
./mvnw package

# Empaquetar sin ejecutar tests (más rápido)
./mvnw package -DskipTests

# Limpiar los archivos compilados (carpeta target/)
./mvnw clean

# Limpiar y reempaquetar de cero
./mvnw clean package

# Ejecutar la aplicación Spring Boot
./mvnw spring-boot:run

# Instalar en el repositorio local (~/.m2)
./mvnw install
```

---

## Estructura de directorios estándar de Maven

Maven impone una estructura de carpetas convencional que Spring Boot respeta:

```
mi-proyecto/
├── pom.xml                          # Configuración del proyecto
├── mvnw                             # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                         # Maven Wrapper (Windows)
└── src/
    ├── main/
    │   ├── java/                    # Código fuente principal
    │   │   └── cl/duoc/fullstack/
    │   │       └── tickets/
    │   └── resources/               # Configuración y recursos
    │       ├── application.properties
    │       ├── static/              # Archivos estáticos (CSS, JS)
    │       └── templates/           # Plantillas HTML (Thymeleaf)
    └── test/
        └── java/                    # Tests
            └── cl/duoc/fullstack/
                └── tickets/
└── target/                          # Generado por Maven (compilados, JARs)
```

> ⚠️ La carpeta `target/` es generada automáticamente por Maven. **No debe subirse al repositorio** — asegúrate de tenerla en `.gitignore`.

---

## Repositorio de Maven

Maven descarga las dependencias desde el **repositorio central** de Maven (Maven Central) y las guarda en el **repositorio local** (`~/.m2/repository`) para no volver a descargarlas.

```
Internet → Maven Central (mvnrepository.com)
                │
                ▼
~/.m2/repository/         ← repositorio local (caché)
    org/springframework/
    com/fasterxml/
    org/projectlombok/
```

> 💡 Puedes buscar dependencias en [mvnrepository.com](https://mvnrepository.com/) o [search.maven.org](https://search.maven.org/)

---

## Herencia y módulos

### `<parent>` — Herencia de configuración

Spring Boot provee un `parent` POM que predefine versiones compatibles de todas sus dependencias, plugins y configuración del compilador:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.3</version>
</parent>
```

Esto significa que **no necesitas especificar la versión** de dependencias como Spring Web, Jackson, Lombok, etc. — Spring Boot gestiona la compatibilidad entre ellas.

---

## Plugins relevantes

```xml
<build>
    <plugins>

        <!-- Plugin de Spring Boot: permite ./mvnw spring-boot:run y genera el JAR ejecutable -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>

        <!-- Compilador Java: configura la versión y procesadores de anotaciones -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

    </plugins>
</build>
```

---

## Buenas prácticas

| ✅ Hacer | ❌ Evitar |
|---------|---------|
| Agregar `target/` al `.gitignore` | Subir `target/` al repositorio |
| Usar el Maven Wrapper (`mvnw`) | Depender de una instalación global de Maven |
| Dejar que Spring Boot gestione las versiones via `parent` | Especificar versiones manualmente sin necesidad |
| Usar `./mvnw clean package` antes de desplegar | Desplegar sin limpiar compilados anteriores |
| Definir la versión de Java en `<properties>` | Asumir la versión del JDK instalado |

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| Maven en 5 minutos (oficial) | 📖 Guía | [maven.apache.org/guides/getting-started/maven-in-five-minutes.html](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) |
| Maven Repository | 🔍 Buscador de dependencias | [mvnrepository.com](https://mvnrepository.com/) |
| Baeldung — Maven | 📄 Artículos | [baeldung.com/maven](https://www.baeldung.com/maven) |
| Spring Initializr | 🛠️ Generador de proyectos | [start.spring.io](https://start.spring.io/) |

---

*[← Volver a Extras](../README.md)*

