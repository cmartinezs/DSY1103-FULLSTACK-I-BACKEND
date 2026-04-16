# Lección 17 - Logging: Auditoría e Investigación

## El problema

Sin logs, cuando algo falla en producción no tienes pista de qué pasó.

```
Cliente: "Mi ticket desapareció"
Tú: "¿Quién lo eliminó? ¿Cuándo? ¿Accidentalmente?"
Sin logs: "No sé." 😕
```

Con logs:

```
[2026-04-16 10:34:22] INFO  TicketService - Ticket #5 creado por admin
[2026-04-16 10:45:15] INFO  TicketService - Ticket #5 asignado a maria
[2026-04-16 11:02:47] ERROR TicketService - Error al eliminar #5: acceso denegado (user)
```

---

## Quick Start

### Concepto

Logging = registrar eventos con:
- **Timestamp:** cuándo
- **Level:** importancia (DEBUG, INFO, WARN, ERROR)
- **Mensaje:** qué pasó

### Niveles (del menos al más grave)

```
DEBUG   → Detalles técnicos (valores de variables)
INFO    → Eventos importantes (create, update, delete)
WARN    → Advertencias (recurso no encontrado, retry)
ERROR   → Errores (excepción lanzada)
```

---

## Lo que construirás

1. Agregar SLF4J + Logback (ya incluidos en Spring Boot)
2. Loguear en `create()`, `updateById()`, `deleteById()`
3. Diferenciar niveles por perfil (DEBUG en dev, INFO en prod)
4. Ver logs en consola + guardar en archivo

---

## Lecturas recomendadas

- Lección 11: Perfiles (DEBUG vs INFO por ambiente)
- Lección 16: Spring Security (loguear quién accede)
