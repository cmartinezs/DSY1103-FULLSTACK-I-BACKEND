# Lección 12 — Migraciones de Base de Datos con Flyway

## ¿De dónde venimos?

En la lección 11 configuraste múltiples bases de datos (H2, MySQL, Supabase) con perfiles de Spring Boot. Tu aplicación automáticamente crea las tablas gracias a `ddl-auto: update` de JPA/Hibernate.

Esto funciona en desarrollo, pero tiene problemas en producción:

- **Sin control de versiones:** Si cambias el esquema, ¿cómo lo sincronizas con la BD de otros desarrolladores?
- **Cambios irreversibles:** `ddl-auto: update` nunca borra columnas; si cometes un error, queda para siempre
- **Múltiples BDs:** En Supabase necesitas sincronizar cambios de esquema sin código Java
- **Auditoría:** No hay registro de quién cambió qué en la BD

Esta lección introduce **Flyway**, una herramienta profesional de migraciones que soluciona todo esto.

---

## Los dos enfoques

| Enfoque | Tool | Cuándo | Ventajas | Desventajas |
|---------|------|--------|----------|------------|
| **JPA Auto** | Hibernate + `ddl-auto` | Desarrollo local, H2 | Simple, automático | Sin versiones, sin reversión |
| **Migraciones** | Flyway | Desarrollo persistente, producción | Versionado, reversible, profesional | Requiere escribir SQL |

---

## ¿Qué vas a construir?

Al terminar esta lección podrás:

1. Entender cuándo usar JPA automático vs Flyway
2. Configurar Flyway para MySQL y Supabase
3. Escribir migraciones SQL versionadas
4. Aplicar migraciones automáticamente al arrancar
5. Revertir migraciones si cometes un error
6. Mantener múltiples BDs sincronizadas

### Lo que vas a poder explicar

- ¿Por qué Flyway es importante en producción?
- ¿Cómo funciona el versionado de Flyway?
- ¿Qué hace la carpeta `db/migration/` y cómo nombrar archivos?
- ¿Por qué H2 no necesita Flyway si usan JPA?
- ¿Cómo revertir una migración si sale mal?
- ¿Cuál es la diferencia entre Flyway y Liquibase?

---

## Estructura de la Lección

1. **[Este documento](01_objetivo_y_alcance.md)** — Objetivo y alcance
2. **[Guión Paso a Paso](02_guion_paso_a_paso.md)** — Instrucciones prácticas
3. **[Configuración por Perfil](03_configuracion_por_perfil.md)** — YAML + propiedades
4. **[Ejemplos de Migraciones](04_ejemplos_migraciones.md)** — Scripts SQL listos
5. **[Comparación: JPA vs Flyway](05_jpa_vs_flyway.md)** — Cuándo usar cada uno
6. **[Troubleshooting](06_troubleshooting.md)** — Errores y soluciones
7. **[Checklist](07_checklist_rubrica_minima.md)** — Verificación
8. **[Actividad Individual](08_actividad_individual.md)** — Tu tarea
