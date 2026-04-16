# Lección 12 — JPA vs Flyway: Cuándo Usar Cada Uno

## Tabla Comparativa

| Aspecto | JPA (`ddl-auto`) | Flyway |
|---------|-----------------|--------|
| **Control de versión** | ❌ No | ✅ Sí (V1, V2, V3...) |
| **Reversión** | ❌ Complicada | ✅ Fácil (crear nueva migración) |
| **Ambiente** | Desarrollo | Producción |
| **Auditoría** | ❌ No hay registro | ✅ Tabla `flyway_schema_history` |
| **Equipo sincronización** | ❌ Problemático | ✅ Migraciones en Git |
| **SQL manual** | ❌ No | ✅ Sí |
| **Learning curve** | ✅ Fácil | ⚠️ Intermedio |
| **Seguridad** | ❌ Cambios automáticos | ✅ Cambios controlados |

---

## Decisión Rápida

```
¿Dónde está tu aplicación?
│
├─ Desarrollo local, solo tú
│  └─ Usa: JPA (ddl-auto: update)
│     Ventaja: Sin configuración extra
│
├─ Desarrollo local, múltiples desarrolladores
│  └─ Usa: Flyway
│     Ventaja: Sincronización en Git
│
├─ Staging / Producción
│  └─ Usa: Flyway
│     Ventaja: Auditoría y control
│
└─ Tests / H2
   └─ Usa: JPA (ddl-auto: create-drop)
      Ventaja: Limpieza automática
```

---

## Escenario 1: Solo Desarrollo Local (H2 o MySQL)

**Configuración:**
```yaml
jpa:
  hibernate:
    ddl-auto: update
```

**Flujo:**
1. Modificas tu entidad Java (`@Entity`)
2. Arrancar app
3. JPA **automáticamente** crea/modifica tablas
4. Listo, sin hacer nada más

✅ **Ventajas:**
- Desarrollo rápido
- No escribir SQL

❌ **Desventajas:**
- Sin versiones
- Cambios no reversibles
- Si hay error, queda en la BD

---

## Escenario 2: Múltiples Desarrolladores

**Problema sin Flyway:**
```
Dev A:          Modifica Ticket.java   (agrega columna)
Dev B:          Modifica Ticket.java   (agrega columna diferente)
                └─ CONFLICTO: ¿Qué cambios aplica JPA?
```

**Solución con Flyway:**
```
Dev A:          V1__Agregar_prioridad.sql          (commitea)
                ├─ Los demás: git pull (reciben la migración)
Dev B:          V2__Agregar_estado_resolucion.sql (commitea)
                ├─ Los demás: git pull (reciben V1 y V2)
                └─ Cuando arrancan: Flyway aplica ambas en orden
```

✅ **Ventajas:**
- Control de versiones (Git)
- Cada cambio es traceable
- Sincronización automática

❌ **Desventajas:**
- Deben escribir SQL

---

## Escenario 3: Producción

**SIN Flyway (PELIGROSO):**
```
Producción:  ddl-auto: update
             └─ ¿Quién cambió la BD? ¿Cuándo?
             └─ ¿Es reversible si falla?
             └─ RIESGO: Cambios no auditados
```

**CON Flyway (SEGURO):**
```
Producción:  V1, V2, V3 en DB
             └─ Tabla flyway_schema_history registra TODO
             └─ Cada cambio está en Git
             └─ Si falla: creas V4 para revertir
             └─ SEGURO: Auditado y reversible
```

---

## Comparación Técnica

### JPA automático

```java
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;           // JPA crea automáticamente
    private String status;          // JPA modifica automáticamente
}

// Cambias el código, arrancar app → JPA actualiza BD
```

### Flyway

```sql
-- V1__Crear_tabla_tickets.sql
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    status VARCHAR(50)
);

-- V2__Agregar_columna_prioridad.sql
ALTER TABLE tickets ADD COLUMN priority VARCHAR(20);
```

**Con Flyway, controlas exactamente qué SQL se ejecuta.**

---

## Recomendación por Etapa

| Etapa | Perfil | Herramienta | Configuración |
|-------|--------|-------------|---------------|
| **Aprendizaje (Semana 1-2)** | h2 | JPA | `ddl-auto: create-drop` |
| **Desarrollo Inicial** | h2 / mysql | JPA | `ddl-auto: update` |
| **Trabajo en Equipo** | mysql | Flyway | `locations: db/migration/mysql` |
| **Staging** | supabase | Flyway | `locations: db/migration/supabase` |
| **Producción** | supabase | Flyway | `ddl-auto: validate` |

---

## Migración: De JPA a Flyway

Si ya usabas JPA (`update`) y quieres pasar a Flyway:

### Paso 1: Exportar esquema actual

```bash
# MySQL
mysqldump -u root tickets_db --no-data > V1__Esquema_actual.sql

# PostgreSQL (Supabase)
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME --schema-only > V1__Esquema_actual.sql
```

### Paso 2: Crear V1 en `db/migration/`

Copia el esquema exportado a:
- `src/main/resources/db/migration/mysql/V1__Esquema_actual.sql`
- `src/main/resources/db/migration/supabase/V1__Esquema_actual.sql`

### Paso 3: Cambiar configuración

```yaml
flyway:
  enabled: true
  locations: classpath:db/migration/mysql
  baseline-on-migrate: true

jpa:
  hibernate:
    ddl-auto: validate  # ← Cambiar a validate
```

### Paso 4: Ejecutar

```bash
./mvnw spring-boot:run
```

Flyway crea la tabla de historial y registra V1 como aplicada (sin re-ejecutar).

---

## ✅ Checklist de Decisión

Antes de elegir, pregúntate:

- ¿Trabajo en equipo? → Flyway
- ¿Múltiples BDs (dev, staging, prod)? → Flyway
- ¿Necesito auditoría de cambios? → Flyway
- ¿Solo desarrollo local de prueba? → JPA (update)
- ¿H2 para tests? → JPA (create-drop)
- ¿Es producción? → Flyway (SIEMPRE)

---

*[← Volver a Lección 12](01_objetivo_y_alcance.md)*
