# Lección 14 — Ejecución Local de Múltiples Servicios

Para la segunda evaluación debes levantar ~10 microservicios simultáneamente. Una JVM de Spring Boot en configuración por defecto consume entre **200 y 350 MB de RAM**. En el peor caso: `10 × 350 MB = 3.5 GB` solo para los servicios.

Esta sección cubre las estrategias para reducir ese consumo y poder correrlos todos en una sola PC.

---

## Solución 1: Limitar la RAM de cada JVM

Agrega estos flags al arrancar cada servicio. La forma más simple es en `application.yml` o como variable de entorno:

```bash
# Límite de heap: 128 MB por servicio (suficiente para servicios simples en memoria)
mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xms64m -Xmx128m"
```

O permanentemente en el `pom.xml` de cada servicio:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>-Xms64m -Xmx128m</jvmArguments>
    </configuration>
</plugin>
```

---

## Solución 2: Reducir el overhead de Spring Boot

Agrega esto a cada `application.yml`:

```yaml
spring:
  main:
    lazy-initialization: true   # beans creados solo cuando se necesitan
  jmx:
    enabled: false              # deshabilita JMX (no lo usamos)

server:
  tomcat:
    threads:
      max: 10                   # menos hilos = menos memoria (dev only)
```

Con estas tres propiedades, un servicio simple puede arrancar usando **80-120 MB** en lugar de 300 MB.

---

## Solución 3: Docker Compose (Extra — no requerido por la asignatura)

> ⚠️ **Docker y Docker Compose no son parte del currículo oficial de DSY1103.** Esta solución se menciona como referencia para quienes ya lo conozcan o quieran explorarlo por su cuenta. Para más detalle, ver [`docs/extras/docker`](../../../docs/extras/docker/README.md).

Docker Compose levanta todos los servicios con un solo comando. Para que funcione, **cada proyecto Spring Boot necesita un `Dockerfile`** en su raíz. Sin ese archivo, `docker compose up --build` fallará.

**Sobre el nombre del archivo de configuración:** el estándar actual es `compose.yaml` (sin el prefijo `docker-`). Los nombres `docker-compose.yml` y `docker-compose.yaml` siguen siendo reconocidos por retro-compatibilidad, pero son el estilo antiguo (Compose V1).

**Sobre el comando:** usa siempre `docker compose` (con espacio), que es el plugin integrado en Docker Desktop moderno. El comando `docker-compose` (con guión) era la herramienta V1 independiente, deprecada desde 2023.

Estructura necesaria:
```
monorepo/
├── compose.yaml              ← nombre moderno (V2)
├── Tickets/
│   └── Dockerfile            ← requerido por cada servicio
├── NotificationService/
│   └── Dockerfile            ← requerido
└── ...
```

Ejemplo de `compose.yaml`:

```yaml
# compose.yaml — Compose V2 (no necesita campo "version:")
services:
  tickets:
    build: ./Tickets          # ← usa el Dockerfile de ./Tickets/
    ports: ["8080:8080"]
    environment:
      JAVA_TOOL_OPTIONS: "-Xmx128m"

  notification:
    build: ./NotificationService
    ports: ["8081:8081"]
    environment:
      JAVA_TOOL_OPTIONS: "-Xmx64m"

  audit:
    build: ./AuditService
    ports: ["8082:8082"]
    environment:
      JAVA_TOOL_OPTIONS: "-Xmx64m"
```

```bash
docker compose up --build    # construye imágenes y levanta todo
docker compose down          # detiene todo
docker compose logs -f       # ver logs en tiempo real
```

Ver [`docs/extras/docker`](../../../docs/extras/docker/README.md) para el `Dockerfile` mínimo requerido, la diferencia completa V1/V2 y más ejemplos.

---

## Solución 4: Compilación nativa con GraalVM (Extra)

> ⚠️ Esta opción es avanzada y no es requerida por la asignatura. Ver [`docs/extras/native-compilation`](../../../docs/extras/native-compilation/README.md) para el detalle completo.

En lugar de ejecutar un JAR sobre una JVM (que incluye el runtime completo), GraalVM compila la aplicación a un **ejecutable nativo** — código máquina que arranca directamente sin JVM.

| | JVM estándar | Ejecutable nativo |
|---|---|---|
| Startup | ~4 segundos | ~80 ms |
| RAM en reposo | ~250 MB | ~50 MB |
| Tiempo de build | ~10 segundos | 3–10 minutos |

Para 10 servicios: `10 × 50 MB ≈ 500 MB` de RAM total — una mejora enorme. El costo es el tiempo de compilación: cada cambio en el código requiere esperar varios minutos para volver a compilar. No es práctico para desarrollo activo, pero sí para el despliegue final.

```bash
# Requiere GraalVM instalado y JAVA_HOME apuntando a él
mvnw.cmd -Pnative native:compile -DskipTests
./target/mi-servicio     # ejecutable nativo, sin JVM
```

---

## Consejo rápido para empezar

Usa esta combinación como punto de partida — funciona con solo Java y Maven instalados:

```mermaid
flowchart LR
    A["Monorepo\n(un solo git clone)"] --> B["Cada servicio con\n-Xmx128m + lazy-init"]
    B --> C["Script de arranque\nbatch / shell"]
    C --> D["Todos los servicios\ncorriendo con ~1 GB RAM total"]
```

Crea un script `start-all.cmd` (Windows) en la raíz:

```bat
@echo off
echo Iniciando todos los microservicios...

start "NotificationService" cmd /k "cd NotificationService && mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Xmx64m"
start "AuditService"        cmd /k "cd AuditService        && mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Xmx64m"
start "SearchService"       cmd /k "cd SearchService       && mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Xmx64m"
start "SLAService"          cmd /k "cd SLAService          && mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Xmx64m"
start "Tickets"             cmd /k "cd Tickets             && mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Xmx128m"

echo Servicios iniciados. Revisa cada ventana.
```

Cada servicio abre en su propia ventana de terminal, por lo que puedes ver sus logs de forma independiente.

---

*[← Organización de repositorios](02_organizacion_repositorios.md) · [Siguiente: Despliegue distribuido y nube →](04_despliegue_externo.md)*
