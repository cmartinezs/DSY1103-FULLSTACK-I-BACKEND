# Docker, Compose y Desktop

## ¿Qué es Docker?

Docker es una plataforma para ejecutar aplicaciones en **contenedores**.

Un contenedor empaqueta:

- Aplicación
- Dependencias
- Variables de entorno
- Configuración de red
- Sistema de archivos aislado

No reemplaza Java ni Spring Boot. Ayuda a ejecutar el ambiente de forma repetible.

---

## ¿Qué es una imagen?

Una imagen es una plantilla.

Ejemplo:

```text
mysql:8.4
postgres:16
eclipse-temurin:21-jre
```

Desde una imagen se crean contenedores.

---

## ¿Qué es un contenedor?

Un contenedor es una instancia en ejecución de una imagen.

```bash
docker run --name mysql-demo -p 3306:3306 mysql:8.4
```

---

## ¿Qué es Docker Compose?

Docker Compose permite definir varios servicios en un archivo `docker-compose.yml`.

Sin Compose:

```bash
docker run ...
docker network create ...
docker volume create ...
docker run ...
```

Con Compose:

```bash
docker compose up -d
```

---

## ¿Qué es Docker Desktop?

Docker Desktop es una aplicación gráfica para Windows y macOS que incluye:

- Docker Engine
- Docker Compose
- Interfaz visual
- Integración con WSL2 en Windows
- Configuración de recursos

En Linux normalmente se instala Docker Engine directamente; Docker Desktop es opcional.

---

## Conceptos clave

| Concepto | Explicación |
|----------|-------------|
| **Image** | Plantilla del contenedor |
| **Container** | Proceso aislado corriendo |
| **Volume** | Persistencia de datos |
| **Network** | Comunicación entre contenedores |
| **Port mapping** | Exponer puerto del contenedor al host |
| **Environment variable** | Configuración inyectada al contenedor |

---

## Buenas prácticas

- No guardar credenciales reales en Git
- Usar `.env.example` como plantilla
- Usar volúmenes para datos persistentes
- Usar nombres de servicio como host dentro de Compose
- Mantener Dockerfile simple para proyectos educativos
- No copiar `target/` al repositorio

