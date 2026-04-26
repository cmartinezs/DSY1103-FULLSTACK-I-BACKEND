# Lección 15 — Migraciones de Base de Datos con Flyway

**Aprende a versionear cambios de base de datos como código con Flyway. Implementa migraciones profesionales que funcionan en MySQL, Supabase y H2.**

---

## 📚 Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. Guión Paso a Paso** ⭐ | 20 min | Instrucciones prácticas |
| **03. Configuración por Perfil** | 10 min | YAML + properties |
| **04. Ejemplos de Migraciones** | 15 min | Scripts SQL listos |
| **05. JPA vs Flyway** | 10 min | Cuándo usar cada uno |
| **06. Troubleshooting** | 10 min | Errores y soluciones |
| **07. Checklist** | 5 min | Verificación |
| **08. Actividad Individual** | - | Tu tarea |

---

## 🎯 Quick Start (5 min)

### 1. Agregar Flyway a `pom.xml`
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.3</version>
</dependency>
```

### 2. Configurar YAML (MySQL)
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration/mysql
  
  jpa:
    hibernate:
      ddl-auto: validate  # CAMBIAR a validate
```

### 3. Crear Migración
```sql
-- src/main/resources/db/migration/mysql/V1__create_tickets_table.sql
CREATE TABLE tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'NEW'
);
```

### 4. Ejecutar
```bash
export SPRING_PROFILES_ACTIVE=mysql
./mvnw spring-boot:run
```

✅ Flyway aplica automáticamente V1 y crea tabla `flyway_schema_history`

---

## 🔑 Conceptos Clave

### ¿Qué es Flyway?

Herramienta que **versionea cambios de BD como Git**:
- V1__create_table.sql
- V2__add_column.sql
- V3__create_index.sql

Cada migración se ejecuta **una sola vez**, en orden. Flyway registra el historial en `flyway_schema_history`.

### Diferencia: JPA vs Flyway

| Aspecto | JPA | Flyway |
|---------|-----|--------|
| Automático | ✅ | ❌ |
| Versionado | ❌ | ✅ |
| Reversible | ❌ | ✅ |
| Producción | ❌ | ✅ |
| H2 | ✅ | ❌ |

---

## 📂 Estructura

```
src/main/resources/
├── application.yml
├── application-h2.yml (Flyway disabled)
├── application-mysql.yml (Flyway enabled)
├── application-supabase.yml (Flyway enabled)
└── db/migration/
    ├── mysql/
    │   ├── V1__create_tickets_table.sql
    │   ├── V2__add_priority_column.sql
    │   └── V3__...
    └── supabase/
        ├── V1__create_tickets_table.sql
        ├── V2__add_priority_column.sql
        └── V3__...
```

---

## ✅ Checklist

- [ ] Flyway en `pom.xml`
- [ ] Carpetas `db/migration/{mysql,supabase}/` creadas
- [ ] `application-*.yml` configurado
- [ ] `ddl-auto: validate` en MySQL/Supabase
- [ ] V1, V2, V3 migraciones creadas
- [ ] App arranca sin errores
- [ ] Logs muestran "Successfully applied 3 migrations"
- [ ] Tabla `flyway_schema_history` con 3 filas

---

## 🚀 Sigue el Guión

Comienza con **[02. Guión Paso a Paso](02_guion_paso_a_paso.md)** para instrucciones detalladas.

---

*Lección 15 de 18 - [← Volver a Lecciones](../)*
