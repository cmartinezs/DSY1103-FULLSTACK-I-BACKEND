# Lección 11 — Configurar bases de datos reales: H2, MySQL y Supabase

## ¿De dónde venimos?

En la lección 10 migraste el proyecto a JPA con H2 (base de datos en memoria). Tu aplicación ahora persiste tickets, pero los datos se pierden al cerrar la aplicación.

Hay dos escenarios que necesitas manejar:

1. **Persistencia real:** necesitas que los datos sobrevivan entre ejecuciones (MySQL, PostgreSQL)
2. **Entornos distintos:** necesitas poder cambiar de base de datos sin modificar el código

Esta lección resuelve ambos con **perfiles de Spring Boot** y **variables de entorno**.

---

## Los tres caminos

| Opción | Dónde corre | Cuándo usarla |
|---|---|---|
| **H2 (en memoria)** | Tu computador local | Tests, desarrollo rápido, sin persistencia |
| **MySQL + XAMPP** | Tu computador local | Desarrollo diario, pruebas con datos persistentes |
| **Supabase** | Nube (PostgreSQL) | Entrega de actividades, demos, trabajo colaborativo |

Los tres usan SQL estándar y funcionan perfectamente con JPA/Hibernate. La única diferencia está en los archivos de configuración.

**Nota:** Aunque usamos la misma base de datos (Supabase) para test y prod, los entornos son diferentes (distinto proyecto, distintas credenciales).

---

## ¿Qué vas a construir?

Al terminar esta lección podrás:

1. Entender cómo usar **perfiles de Spring Boot** para manejar múltiples configuraciones
2. Configurar **entornos** con diferentes valores de variables para cada perfil
3. Configurar variables de entorno para no hardcodear credenciales
3. Conectar la aplicación a **H2** (desarrollo rápido)
4. Conectar la aplicación a **MySQL local** (XAMPP) con la configuración correcta
5. Crear un proyecto en **Supabase** y obtener la URL de conexión
6. Conectar la aplicación a **Supabase** (PostgreSQL en la nube)
7. Cambiar entre las tres bases de datos con un solo argumento (sin modificar código)

### Lo que vas a poder explicar

- ¿Cuál es la diferencia entre MySQL, PostgreSQL y H2 para este proyecto?
- ¿Qué son los **perfiles de Spring Boot** y cómo se usan?
- ¿Cómo protejo mis credenciales usando variables de entorno?
- ¿Qué información necesita Spring Boot para conectarse a una base de datos?
- ¿Qué hace `ddl-auto: create` vs `ddl-auto: update` y cuándo usar cada uno?
- ¿Por qué cambiar de base de datos no requiere cambiar el código Java?
- ¿Cómo configuro variables de entorno desde el sistema operativo y desde IntelliJ?

---

## Documentación por secciones

1. **[Guión paso a paso](02_guion_paso_a_paso.md)** — Instrucciones detalladas para configurar cada perfil
2. **[Resumen de archivos](07_resumen_archivos.md)** — Referencia rápida de qué va en cada archivo
3. **[Guía IntelliJ](06_guia_intellij_env.md)** — Cómo cargar `.env` en IntelliJ IDEA
4. **[MySQL vs PostgreSQL](03_mysql_vs_postgresql.md)** — Comparación técnica

---

## ¿Qué NO cubre esta lección?

| Tema | ¿Por qué queda afuera? |
|---|---|
| Migraciones con Flyway o Liquibase | Herramientas de nivel producción, fuera del alcance del curso |
| Conexión con SSL/TLS forzado | Configuración avanzada de red |
| Connection pooling avanzado | Configuración por defecto de Hikari es suficiente |
