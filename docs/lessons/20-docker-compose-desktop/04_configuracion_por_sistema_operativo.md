# Configuración por Sistema Operativo

## Windows

### Recomendado

Usar **Docker Desktop + WSL2**.

Pasos:

1. Activar virtualización en BIOS/UEFI si está deshabilitada
2. Instalar WSL2
3. Instalar una distribución Linux, por ejemplo Ubuntu
4. Instalar Docker Desktop
5. En Docker Desktop, activar integración con WSL2

Verificación en PowerShell:

```powershell
docker --version
docker compose version
docker run hello-world
```

Verificación en Ubuntu WSL:

```bash
docker --version
docker compose version
```

### Recomendaciones para Windows

- Trabaja el repositorio dentro del filesystem de WSL, por ejemplo `~/projects/...`
- Evita ejecutar proyectos pesados desde `C:\` montado en WSL si notas lentitud
- Usa `mvnw.cmd` en PowerShell
- Usa `./mvnw` dentro de WSL
- Si un puerto queda ocupado, revisa Docker Desktop y servicios locales

---

## Linux

### Recomendado

Instalar Docker Engine desde el repositorio oficial de tu distribución.

Verificación:

```bash
docker --version
docker compose version
docker run hello-world
```

Si Docker requiere `sudo`:

```bash
sudo usermod -aG docker $USER
```

Luego cierra sesión y vuelve a entrar.

### Recomendaciones para Linux

- Usa `./mvnw` para Maven Wrapper
- Revisa permisos de archivos si copiaste el repo desde Windows
- Usa `docker compose`, no `docker-compose`, salvo instalaciones antiguas
- Revisa logs con `docker compose logs -f`

---

## macOS

### Recomendado

Usar **Docker Desktop for Mac**.

Pasos:

1. Instalar Docker Desktop
2. Abrir Docker Desktop
3. Esperar a que el engine quede corriendo
4. Validar desde Terminal

```bash
docker --version
docker compose version
docker run hello-world
```

### Apple Silicon

En equipos M1/M2/M3:

- Prefiere imágenes multiarquitectura oficiales
- `mysql:8.4`, `postgres:16` y `eclipse-temurin:21` funcionan bien
- Si una imagen antigua no soporta ARM64, busca una versión más reciente

### Recomendaciones para macOS

- Ajusta recursos de Docker Desktop si los contenedores quedan lentos
- Usa `./mvnw`
- Si un puerto está ocupado, revisa servicios locales con `lsof -i :3306`

---

## Puertos comunes

| Servicio | Puerto |
|----------|--------|
| Tickets API | 8080 |
| NotificationService | 8081 |
| AuditService | 8082 |
| SearchService | 8084 |
| SLAService | 8085 |
| MySQL | 3306 |
| PostgreSQL | 5432 |

