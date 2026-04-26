# Lección 15 — Checklist y Rúbrica Mínima

## ✅ Checklist de Completitud

### Configuración

- [ ] `pom.xml` contiene dependencia Flyway (versión ≥ 9.0)
- [ ] Carpeta `src/main/resources/db/migration/mysql/` existe
- [ ] Carpeta `src/main/resources/db/migration/supabase/` existe
- [ ] `application-mysql.yml` tiene `flyway.enabled: true`
- [ ] `application-supabase.yml` tiene `flyway.enabled: true`
- [ ] `application-h2.yml` tiene `flyway.enabled: false`
- [ ] Todos los YAML tienen `ddl-auto: validate` (MySQL/Supabase) o `create-drop` (H2)

### Migraciones

- [ ] `V1__create_tickets_table.sql` existe en mysql/ y supabase/
- [ ] `V2__create_users_table.sql` existe en mysql/ y supabase/
- [ ] `V3__add_tickets_users_relation.sql` existe en mysql/ y supabase/
- [ ] Todos los nombres siguen patrón `V[número]__[descripción].sql`
- [ ] SQL es syntácticamente correcto (probado en BD)
- [ ] Diferencias MySQL vs PostgreSQL son correctas

### Ejecución

- [ ] App arranca sin errores con `SPRING_PROFILES_ACTIVE=mysql`
- [ ] App arranca sin errores con `SPRING_PROFILES_ACTIVE=supabase`
- [ ] App arranca sin errores con `SPRING_PROFILES_ACTIVE=h2` (JPA automático)
- [ ] Logs muestran: "Successfully applied 3 migrations"
- [ ] Tabla `flyway_schema_history` tiene 3 filas
- [ ] API `/ticket-app/tickets` responde correctamente

### Documentación

- [ ] Leí lección 15 completa
- [ ] Entiendo diferencia JPA vs Flyway
- [ ] Puedo explicar por qué H2 no usa Flyway
- [ ] Documenté en comentarios SQL qué hace cada migración

### Git

- [ ] Migraciones están en repositorio
- [ ] `.env` NO está en repositorio (está en `.gitignore`)
- [ ] Commit message es descriptivo: `feat: agregar Flyway migrations`

---

## 🎓 Rúbrica de Evaluación

### 1. Configuración Flyway (25%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| Dependencia en pom.xml | ❌ Falta | ✅ Presente | ✅ + versión correcta |
| Configuración YAML | ❌ Incompleta | ✅ Correcta | ✅ + comentarios explicativos |
| Carpetas de migraciones | ❌ Estructura mal | ✅ Correcta | ✅ + bien organizadas |
| `ddl-auto` cambiado | ❌ Sigue `update` | ✅ Cambió a `validate` | ✅ + diferenciado por perfil |

### 2. Migraciones SQL (40%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| Cantidad | ❌ < 2 migraciones | ✅ 3 migraciones | ✅ 4+ migraciones |
| Nombres | ❌ Incorrecto | ✅ `V[num]__[desc].sql` | ✅ + descriptivos |
| Sintaxis MySQL | ❌ Con errores | ✅ Correcta | ✅ + optimizada |
| Sintaxis PostgreSQL | ❌ No diferenciada | ✅ Correcta | ✅ + con índices |
| Relaciones | ❌ Sin FK | ✅ Con FK simples | ✅ + con constraints |

### 3. Ejecución (20%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| App arranca (MySQL) | ❌ Errores | ✅ Arranca | ✅ Sin warnings |
| App arranca (Supabase) | ❌ Errores | ✅ Arranca | ✅ Sin warnings |
| Logs Flyway | ❌ "Failed" | ✅ "Successfully applied" | ✅ + detallados |
| Tabla de historial | ❌ No existe | ✅ Existe | ✅ + 3+ filas |

### 4. Conocimiento (15%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| Explica JPA vs Flyway | ❌ No sabe | ✅ Explica diferencias | ✅ + casos de uso |
| Entiende versionado | ❌ Confundido | ✅ Claro | ✅ + profundo |
| Conoce convenciones | ❌ Viola | ✅ Sigue | ✅ + explica por qué |
| Puede troubleshoot | ❌ Pierde | ✅ Resuelve básicos | ✅ + errores complejos |

---

## 📊 Cálculo de Nota

```
Configuración:  25% × (puntos / 4)
Migraciones:    40% × (puntos / 5)
Ejecución:      20% × (puntos / 4)
Conocimiento:   15% × (puntos / 4)
─────────────────────────────────
TOTAL:          100%
```

**Ejemplos:**
- 3/4 configuración + 5/5 migraciones + 3/4 ejecución + 4/4 conocimiento = 93%
- 2/4 configuración + 3/5 migraciones + 2/4 ejecución + 2/4 conocimiento = 58%

---

## 🚩 Red Flags (Falla Automática)

- ❌ Migraciones editadas después de ser ejecutadas
- ❌ `.env` con credenciales en repositorio
- ❌ `ddl-auto: update` en MySQL/Supabase
- ❌ Nombres de migración incorrectos (V01, v1, V1_, etc)
- ❌ App no arranca o logs muestran "Failed"
- ❌ SQL con errores de sintaxis

---

*[← Volver a Lección 15](01_objetivo_y_alcance.md)*
