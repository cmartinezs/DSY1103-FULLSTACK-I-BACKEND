# Lección 20 — Checklist y Rúbrica Mínima

## Checklist antes de entregar

### Instalación

- [ ] `docker --version` funciona
- [ ] `docker compose version` funciona
- [ ] `docker run hello-world` funciona
- [ ] Docker Desktop está corriendo si usas Windows/macOS

### Compose

- [ ] Existe `docker-compose.yml`
- [ ] MySQL levanta correctamente
- [ ] PostgreSQL levanta correctamente
- [ ] Los puertos están documentados
- [ ] Los datos usan volúmenes
- [ ] Existe `.env.example` si se usan variables

### Spring Boot

- [ ] El proyecto puede conectarse a MySQL en Docker
- [ ] El proyecto puede conectarse a PostgreSQL en Docker
- [ ] Se entiende cuándo usar `localhost` y cuándo usar nombre de servicio
- [ ] Existe Dockerfile si se containeriza la API

### Documentación

- [ ] README incluye comandos de uso
- [ ] README indica diferencias Windows/Linux/macOS
- [ ] README explica cómo apagar y limpiar volúmenes

---

## Rúbrica

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Docker instalado y validado | 15 | `hello-world` ejecutado |
| Compose para bases de datos | 25 | MySQL y PostgreSQL levantan |
| Conexión Spring Boot | 20 | API conecta a base en Docker |
| Dockerfile funcional | 15 | Imagen Spring Boot construida |
| Configuración multiplataforma | 15 | Windows/Linux/macOS documentados |
| README claro | 10 | Comandos y troubleshooting |

**Total: 100 puntos**

---

## Red Flags

- Docker no ejecuta
- Se suben credenciales reales al repositorio
- La app usa `localhost` dentro de un contenedor para conectarse a otro contenedor
- No hay volúmenes para base de datos
- No se documenta cómo limpiar el ambiente

