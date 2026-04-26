# 🐳 Docker y Docker Compose

> **Nota:** Docker no es parte de la asignatura DSY1103. Este material es complementario para quienes quieran profundizar en la contenerización de aplicaciones Spring Boot.

---

## Contenido

| Archivo | Descripción |
|---|---|
| **README.md** (este archivo) | Resumen orientativo: V1 vs V2, Dockerfile mínimo, compose.yaml básico |
| [`01_conceptos_basicos.md`](./01_conceptos_basicos.md) | Imágenes, capas, contenedores, redes, volúmenes — con diagramas |
| [`02_dockerfile.md`](./02_dockerfile.md) | Todas las instrucciones Dockerfile, multi-stage, `.dockerignore`, variantes de imagen base |
| [`03_compose_avanzado.md`](./03_compose_avanzado.md) | `depends_on`+healthchecks, profiles, `.env`, redes personalizadas, watch, referencia completa de comandos |

---

## ¿Qué es Docker?

**Docker** es una plataforma de **contenerización**: empaqueta una aplicación junto con todas sus dependencias (JDK, librerías, configuración) en una imagen portable que puede ejecutarse en cualquier máquina con Docker instalado, independientemente del sistema operativo.

Un **contenedor** es una instancia en ejecución de una imagen. Es similar a una máquina virtual pero mucho más liviano, porque comparte el kernel del sistema operativo en lugar de virtualizarlo completo.

---

## Docker Compose V1 vs V2 — lo nuevo y lo viejo

Este es uno de los puntos que más confusión genera porque ambas versiones coexisten y se parecen, pero hay diferencias importantes.

### El comando: `docker compose` vs `docker-compose`

| | V1 (legacy) | V2 (actual) |
|---|---|---|
| **Comando** | `docker-compose` (guión) | `docker compose` (espacio) |
| **Tipo** | Herramienta Python independiente | Plugin integrado en el CLI de Docker |
| **Instalación** | `pip install docker-compose` o paquete separado | Incluido en Docker Desktop y Docker Engine moderno |
| **Estado** | ⚠️ Deprecado desde 2023 | ✅ Estándar actual |
| **Retro-compatibilidad** | — | Lee archivos `docker-compose.yml` del V1 |

> `docker-compose` (con guión) puede seguir funcionando si está instalado por separado en el sistema, pero no viene incluido en las versiones modernas de Docker Desktop. **Usa siempre `docker compose` (con espacio).**

### El archivo: `compose.yaml` vs `docker-compose.yml`

Docker Compose busca el archivo de configuración en este orden de prioridad:

1. `compose.yaml` ← **preferido** (estándar actual)
2. `compose.yml`
3. `docker-compose.yaml` ← retro-compatibilidad
4. `docker-compose.yml` ← retro-compatibilidad (V1 legacy)

> **Usa `compose.yaml`** en proyectos nuevos. Los archivos `docker-compose.yml` siguen funcionando, pero son el estilo antiguo.

### ¿Qué es retro-compatible y qué no?

| Feature | V1 `docker-compose` | V2 `docker compose` |
|---------|---------------------|---------------------|
| Leer `docker-compose.yml` | ✅ | ✅ |
| Leer `compose.yaml` | ❌ | ✅ |
| `depends_on` con `condition: service_healthy` | ❌ | ✅ |
| `docker compose watch` (hot-reload) | ❌ | ✅ |
| `--wait` (esperar a que estén listos) | ❌ | ✅ |
| Sintaxis básica de servicios/ports/environment | ✅ | ✅ |

---

## Requisito: Dockerfile en cada servicio

Docker Compose necesita saber **cómo construir la imagen** de cada servicio. Eso se define en un archivo `Dockerfile` en la raíz de cada proyecto Spring Boot. Sin él, `docker compose up --build` fallará.

### Dockerfile mínimo para Spring Boot (multi-stage)

```dockerfile
# Etapa 1: construir el JAR con Maven
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests --no-transfer-progress

# Etapa 2: imagen final liviana (solo JRE, sin herramientas de build)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**¿Por qué dos etapas?** La primera incluye JDK + Maven (~600 MB). La segunda solo necesita el JRE (~200 MB) para ejecutar el JAR compilado. El resultado final es una imagen más pequeña.

> Spring Boot también puede generar la imagen automáticamente con:
> ```bash
> mvnw.cmd spring-boot:build-image
> ```
> Pero requiere Docker instalado y corriendo, y tarda más la primera vez.

---

## compose.yaml básico

```yaml
# compose.yaml (en la raíz del monorepo)
#
# Diferencias con el estilo antiguo (docker-compose.yml):
#   - El campo "version:" ya NO es necesario en Compose V2
#   - El archivo se llama "compose.yaml" (no "docker-compose.yml")
#   - El comando es "docker compose" (espacio), no "docker-compose" (guión)

services:
  tickets:
    build: ./Tickets          # ← busca Dockerfile en ./Tickets/
    ports:
      - "8080:8080"           # "puerto_host:puerto_contenedor"
    environment:
      # JAVA_TOOL_OPTIONS es la variable estándar de la JVM.
      # -Xmx limita la RAM máxima del heap. -Xms es el tamaño inicial.
      # Sin esto, la JVM puede reclamar toda la RAM del host.
      JAVA_TOOL_OPTIONS: "-Xmx128m -Xms64m"

  notification:
    build: ./NotificationService
    ports:
      - "8081:8081"
    environment:
      JAVA_TOOL_OPTIONS: "-Xmx64m -Xms32m"

  audit:
    build: ./AuditService
    ports:
      - "8082:8082"
    environment:
      JAVA_TOOL_OPTIONS: "-Xmx64m -Xms32m"

  search:
    build: ./SearchService
    ports:
      - "8084:8084"
    environment:
      JAVA_TOOL_OPTIONS: "-Xmx64m -Xms32m"

  sla:
    build: ./SLAService
    ports:
      - "8085:8085"
    environment:
      JAVA_TOOL_OPTIONS: "-Xmx64m -Xms32m"
```

> **`JAVA_TOOL_OPTIONS` vs `JAVA_OPTS`**: `JAVA_TOOL_OPTIONS` es la variable estándar reconocida directamente por la JVM (definida en la especificación Java). `JAVA_OPTS` es una convención histórica de scripts de arranque (como los de Tomcat) — no es estándar y puede no funcionar dentro de un contenedor si el script de inicio no la reenvía explícitamente.

> **¿Por qué los servicios pequeños usan 64m?** Spring Boot con almacenamiento en memoria consume alrededor de 50-80 MB de heap en reposo. Sin límite, la JVM reserva por defecto 25% de la RAM del sistema, lo que en una máquina con 16 GB significaría 4 GB por servicio.

### Comandos principales (V2)

```bash
# Primera vez: construye las imágenes Y levanta los contenedores
docker compose up --build

# Veces siguientes (si el código no cambió): levanta las imágenes ya construidas
docker compose up

# En background (no bloquea la terminal)
docker compose up -d

# Detener todos los contenedores (los datos en volúmenes se conservan)
docker compose down

# Detener Y eliminar imágenes también
docker compose down --rmi all

# Levantar solo un servicio específico (y sus dependencias declaradas)
docker compose up tickets

# Ver logs en tiempo real de todos los servicios
docker compose logs -f

# Ver logs de un servicio específico (últimas 100 líneas + seguimiento)
docker compose logs -f --tail=100 tickets

# Ver estado de los contenedores
docker compose ps
```

---

## Comparativa: sin Docker vs con Docker

| | Sin Docker | Con Docker |
|---|---|---|
| **Instalación en PC** | Java + Maven | Solo Docker Desktop |
| **Arrancar todo** | Script batch + múltiples terminales | `docker compose up` |
| **Reproducibilidad** | Depende del entorno local | Igual en cualquier máquina |
| **Curva de aprendizaje** | Baja | Media |
| **Uso en producción** | No recomendado | Sí |

---

## Recursos para aprender más

- [Docker Compose — Documentación oficial](https://docs.docker.com/compose/)
- [Migración de V1 a V2](https://docs.docker.com/compose/migrate/)
- [Spring Boot con Docker — Guía oficial](https://spring.io/guides/gs/spring-boot-docker/)
- [Especificación Compose (archivo compose.yaml)](https://docs.docker.com/compose/compose-file/)
