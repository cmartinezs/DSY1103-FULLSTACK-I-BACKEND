# Lección 11 — MySQL vs PostgreSQL, y la cadena de conexión JDBC

## MySQL y PostgreSQL: lo que necesitas saber

Ambos son motores de base de datos relacionales que hablan SQL estándar. Para este curso son casi intercambiables. Estas son las diferencias que sí te afectan:

| Aspecto | MySQL (XAMPP) | PostgreSQL (Supabase) |
|---|---|---|
| `AUTO_INCREMENT` | `AUTO_INCREMENT` | `SERIAL` o `GENERATED ALWAYS AS IDENTITY` |
| Palabra reservada `user` | Problemática como nombre de tabla | También problemática |
| Tipos de texto | `VARCHAR`, `TEXT`, `LONGTEXT` | `VARCHAR`, `TEXT` (más flexible) |
| Insensibilidad a mayúsculas | Depende del cotejamiento | Requiere `ILIKE` o `LOWER()` |
| Driver JDBC | `com.mysql.cj.jdbc.Driver` | `org.postgresql.Driver` |
| Puerto por defecto | `3306` | `5432` |

**Para JPA con `GenerationType.IDENTITY`:** ambas bases de datos lo soportan. Hibernate genera el SQL correcto para cada motor automáticamente según el dialecto configurado.

---

## Anatomía de una cadena de conexión JDBC

```
jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
│    │      │         │    │           │
│    │      │         │    │           └─ parámetros adicionales
│    │      │         │    └─ nombre de la base de datos
│    │      │         └─ puerto
│    │      └─ host (servidor)
│    └─ tipo de base de datos
└─ protocolo JDBC
```

```
jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres
│    │           │                             │    │
│    │           │                             │    └─ nombre de la BD en Supabase (siempre "postgres")
│    │           │                             └─ puerto PostgreSQL estándar
│    │           └─ host de Supabase (único por proyecto)
│    └─ tipo de base de datos
└─ protocolo JDBC
```

---

## ¿Por qué "user" es una palabra reservada?

Tanto en MySQL como en PostgreSQL, `USER` es una función del sistema (devuelve el usuario conectado). Si creas una tabla llamada `user`, el motor SQL se confunde.

**Solución:** siempre usa `@Table(name = "users")` (en plural) para la entidad de usuarios. Lo aplicarás en la lección 12.

```java
@Entity
@Table(name = "users")  // ← "users", nunca "user"
public class User { ... }
```

---

## ¿Dónde guarda los datos cada opción?

**MySQL / XAMPP:**
Los datos se guardan en archivos del sistema de archivos de tu computador:
```
C:\xampp\mysql\data\tickets_db\
├── tickets.ibd       ← datos de la tabla tickets
└── db.opt            ← configuración de la base de datos
```
Si desinstalaras XAMPP sin respaldar, perderías los datos.

**Supabase:**
Los datos se guardan en servidores de AWS en la región que elegiste. Supabase ofrece backups automáticos diarios en el plan gratuito.

---

## El dialecto de Hibernate

El `dialect` le dice a Hibernate qué "sabor" de SQL debe generar:

```yaml
# MySQL:
properties:
  hibernate:
    dialect: org.hibernate.dialect.MySQLDialect

# PostgreSQL:
properties:
  hibernate:
    dialect: org.hibernate.dialect.PostgreSQLDialect
```

Con Spring Boot 4 y Hibernate 6, el dialecto se detecta automáticamente la mayoría de las veces. Igual es buena práctica explicitarlo para evitar advertencias en la consola.

---

## Buenas prácticas con contraseñas

Nunca subas credenciales reales al repositorio de Git. El `application.yml` que tienes en el repo debería tener contraseñas vacías o de ejemplo:

```yaml
# application.yml (lo que va a Git)
datasource:
  url: jdbc:mysql://localhost:3306/tickets_db
  username: root
  password:          # contraseña vacía para XAMPP local
```

Para Supabase, usa variables de entorno:

```yaml
# application.yml con variable de entorno
datasource:
  url: ${DB_URL}
  username: ${DB_USERNAME}
  password: ${DB_PASSWORD}
```

Y defines las variables en tu entorno antes de arrancar la app. La [Guía IntelliJ](06_guia_intellij_env.md) explica cómo cargarlo desde el IDE, y el [Guión paso a paso](02_guion_paso_a_paso.md) cubre las opciones por terminal y sistema operativo.
