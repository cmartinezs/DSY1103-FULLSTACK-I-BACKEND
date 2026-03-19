# 📄 YAML — Configuración en Spring Boot

## ¿Qué es YAML?

**YAML** (*YAML Ain't Markup Language*) es un formato de serialización de datos diseñado para ser **legible por humanos**. A diferencia de JSON (que usa llaves y corchetes) o `.properties` (que usa pares `clave=valor` planos), YAML usa **indentación** para representar jerarquía, lo que lo hace muy intuitivo para configuraciones complejas.

Spring Boot soporta tanto `application.properties` como `application.yml`. A medida que la configuración crece, **YAML escala mucho mejor** porque evita la repetición de prefijos y permite agrupar visualmente las propiedades relacionadas.

```yaml
# application.properties equivalente:
# server.port=8080
# server.servlet.context-path=/api/v1
# spring.application.name=Tickets

# En application.yml:
server:
  port: 8080
  servlet:
    context-path: /api/v1

spring:
  application:
    name: Tickets
```

---

## 1. Regla fundamental: la indentación

> ⚠️ **La indentación en YAML es obligatoria y significativa.** Un error de espaciado rompe el archivo completo.

```yaml
# ✅ Correcto — 2 espacios por nivel (convención estándar)
server:
  port: 8080
  servlet:
    context-path: /api/v1

# ❌ Incorrecto — mezcla de espacios
server:
    port: 8080   # 4 espacios
  servlet:       # 2 espacios — inconsistente, error
    context-path: /api/v1
```

**Reglas de oro:**
- Usa siempre **2 espacios** por nivel de indentación (nunca tabulaciones)
- Todos los elementos del mismo nivel deben tener **exactamente la misma indentación**
- Los dos puntos (`:`) van seguidos de un **espacio** antes del valor: `clave: valor`

---

## 2. Tipos de datos escalares

YAML infiere el tipo de dato automáticamente. Spring Boot lee estos valores y los convierte al tipo Java correspondiente.

```yaml
# Números enteros → int, long
server:
  port: 8080
  max-connections: 200

# Números decimales → float, double
app:
  tax-rate: 0.19
  timeout: 30.5

# Booleanos → boolean
# Valores true: true, yes, on
# Valores false: false, no, off
features:
  swagger-enabled: true
  maintenance-mode: false

# Nulo → null (o simplemente sin valor)
app:
  api-key: null
  description:        # también es null si no tiene valor
```

> ⚠️ **Cuidado con los booleanos:** `yes`, `no`, `on`, `off` son válidos en YAML pero pueden causar confusión. En Spring Boot se recomienda usar siempre `true` o `false`.

---

## 3. Strings (cadenas de texto)

Los strings son el tipo más común en `application.yml`. Las comillas son **opcionales** salvo en casos especiales.

```yaml
# Sin comillas — la mayoría de los casos
spring:
  application:
    name: Tickets API

# Con comillas simples — cuando el valor contiene caracteres especiales
# (las comillas simples no interpretan secuencias de escape)
app:
  mensaje: 'Bienvenido al sistema: ingresa tus credenciales'
  patron: '^\d{4}-\d{2}-\d{2}$'

# Con comillas dobles — cuando necesitas secuencias de escape (\n, \t, etc.)
app:
  separador: "linea1\nlinea2"
  ruta-windows: "C:\\Users\\usuario\\proyectos"

# Cuándo son OBLIGATORIAS las comillas:
config:
  valor-true: "true"        # sin comillas sería booleano, no string
  version: "1.0"            # sin comillas podría interpretarse como número
  vacio: ""                 # string vacío explícito
  especiales: "host:puerto" # contiene : que confundiría al parser
```

---

## 4. Strings multilínea

Útiles para mensajes largos, SQL embebido, certificados o cualquier texto que ocupe varias líneas.

```yaml
# Bloque literal con | → preserva los saltos de línea exactamente
app:
  mensaje-bienvenida: |
    Bienvenido al sistema de tickets.
    Por favor inicia sesión para continuar.
    Soporte: soporte@empresa.com

# Bloque plegado con > → une las líneas con espacios (ignora los saltos)
app:
  descripcion: >
    Esta es una descripción muy larga
    que se une en una sola línea
    cuando Spring Boot la lee.
    # Resultado: "Esta es una descripción muy larga que se une en una sola línea cuando Spring Boot la lee."
```

---

## 5. Listas

Las listas en YAML se representan con guión (`-`) seguido de un espacio. Son el equivalente a los arrays en Java.

```yaml
# Lista de strings
app:
  cors:
    allowed-origins:
      - http://localhost:4200
      - http://localhost:3000
      - https://mi-app.com

# Lista de números
server:
  allowed-ports:
    - 8080
    - 8443
    - 9090

# Lista en formato compacto (inline) — equivalente al bloque anterior
app:
  roles-admin: [ADMIN, SUPER_ADMIN, ROOT]

# Lista de objetos (cada elemento tiene múltiples campos)
app:
  datasources:
    - name: principal
      url: jdbc:postgresql://localhost:5432/tickets
      driver: org.postgresql.Driver
    - name: replica
      url: jdbc:postgresql://replica:5432/tickets
      driver: org.postgresql.Driver
```

**Lectura en Java con Spring Boot:**

```java
// Para listas simples
@Value("${app.cors.allowed-origins}")
private List<String> allowedOrigins;

// Para listas de objetos, se usa @ConfigurationProperties
@ConfigurationProperties(prefix = "app")
public record AppConfig(List<DataSourceConfig> datasources) {}
```

---

## 6. Objetos anidados (mapas)

Los objetos en YAML son pares `clave: valor` agrupados por indentación. Cada nivel de indentación representa un nivel más profundo en la jerarquía.

```yaml
# Equivalencia entre .properties y .yml

# .properties:
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.datasource.username=sa
# spring.datasource.password=
# spring.datasource.driver-class-name=org.h2.Driver

# application.yml:
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
    driver-class-name: org.h2.Driver
```

La clave de YAML es que **no repite el prefijo** en cada línea. Cuanto más profunda es la jerarquía, más claro se ve el beneficio frente a `.properties`.

---

## 7. Comentarios

Los comentarios en YAML usan `#`. Todo lo que va después del `#` en una línea es ignorado por el parser.

```yaml
# Comentario de línea completa

server:
  port: 8080  # comentario al final de una línea

# ── Sección de base de datos ──────────────────────────
spring:
  datasource:
    # url: jdbc:postgresql://localhost:5432/db  ← línea desactivada
    url: jdbc:h2:mem:testdb                     # usar H2 en desarrollo
    username: sa
    password:   # vacío intencional para H2
```

> 💡 Los comentarios son una de las grandes ventajas de YAML sobre JSON (que no los soporta). Úsalos para documentar decisiones de configuración o deshabilitar temporalmente propiedades.

---

## 8. YAML vs `.properties` — comparativa

| Aspecto | `.properties` | `.yml` |
|---------|--------------|--------|
| Jerarquía | Prefijo repetido en cada línea | Indentación visual |
| Listas | `prop[0]=a`, `prop[1]=b` | Guiones `-` |
| Comentarios | `# comentario` | `# comentario` |
| Multilínea | No soportado nativamente | `\|` y `>` |
| Legibilidad con muchas propiedades | ❌ Se vuelve repetitivo | ✅ Agrupa visualmente |
| Riesgo de error | Bajo (formato plano) | Medio (indentación estricta) |
| Preferencia en proyectos reales | Legacy / simple | ✅ Estándar actual |

```properties
# application.properties — difícil de leer con muchas propiedades
spring.datasource.url=jdbc:postgresql://localhost:5432/tickets
spring.datasource.username=admin
spring.datasource.password=secret
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
```

```yaml
# application.yml — mismo resultado, más legible
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tickets
    username: admin
    password: secret
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
```

---

## 9. Estructura base de `application.yml` en Spring Boot

Esta es la estructura típica de un proyecto Spring Boot real organizada por secciones:

```yaml
# ── Aplicación ────────────────────────────────────────────────
spring:
  application:
    name: tickets-api

# ── Servidor ──────────────────────────────────────────────────
server:
  port: 8080
  servlet:
    context-path: /api/v1

# ── Base de datos ─────────────────────────────────────────────
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tickets
    username: ${DB_USER}          # valor desde variable de entorno
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

# ── JPA / Hibernate ───────────────────────────────────────────
  jpa:
    hibernate:
      ddl-auto: update            # create | update | validate | none
    show-sql: false
    open-in-view: false

# ── Logging ───────────────────────────────────────────────────
logging:
  level:
    root: INFO
    com.empresa.tickets: DEBUG    # más detalle en nuestro paquete
  file:
    name: logs/app.log

# ── Actuator (monitoreo) ──────────────────────────────────────
management:
  endpoints:
    web:
      exposure:
        include: health, info
```

> 📌 En un `application.yml` real **no repitas la clave `spring:`** dos veces como en el ejemplo de arriba: agrupa todas las propiedades bajo un único bloque `spring:`. El ejemplo está separado por secciones solo para explicar cada parte.

---

## 10. Variables de entorno en YAML

YAML se integra directamente con las variables de entorno usando la sintaxis `${NOMBRE_VARIABLE}`. Esto es fundamental para no guardar credenciales en el repositorio.

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:h2:mem:testdb}    # valor por defecto después de :
    username: ${DB_USER:sa}
    password: ${DB_PASSWORD:}            # por defecto: string vacío

  security:
    oauth2:
      client:
        registration:
          github:
            client-id: ${GITHUB_CLIENT_ID}      # sin valor por defecto → obligatorio
            client-secret: ${GITHUB_CLIENT_SECRET}

server:
  port: ${PORT:8080}    # útil en plataformas cloud que asignan el puerto
```

**Sintaxis:**

| Expresión | Comportamiento |
|-----------|----------------|
| `${VAR}` | Lee la variable; falla si no existe |
| `${VAR:valor}` | Lee la variable; usa `valor` si no existe |
| `${VAR:}` | Lee la variable; usa string vacío si no existe |

---

## 11. Perfiles de Spring Boot

Los perfiles permiten tener **configuraciones diferentes por entorno** (dev, test, prod) en un mismo proyecto. Hay dos formas de organizarlos.

### Opción A — Archivos separados (recomendada)

```
src/main/resources/
├── application.yml           ← configuración base (siempre se carga)
├── application-dev.yml       ← se carga solo con perfil "dev"
├── application-test.yml      ← se carga solo con perfil "test"
└── application-prod.yml      ← se carga solo con perfil "prod"
```

```yaml
# application.yml — base compartida
spring:
  application:
    name: tickets-api

server:
  servlet:
    context-path: /api/v1
```

```yaml
# application-dev.yml — sobreescribe o agrega propiedades para dev
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop

logging:
  level:
    com.empresa.tickets: DEBUG
```

```yaml
# application-prod.yml — configuración de producción
server:
  port: ${PORT:8080}

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    root: WARN
    com.empresa.tickets: INFO
```

### Opción B — Todo en un solo archivo con separador `---`

```yaml
# application.yml con múltiples perfiles

# ── Configuración base ────────────────────────────────────────
spring:
  application:
    name: tickets-api
server:
  servlet:
    context-path: /api/v1

---
# ── Perfil: dev ───────────────────────────────────────────────
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
  jpa:
    show-sql: true
server:
  port: 8080

---
# ── Perfil: prod ──────────────────────────────────────────────
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
server:
  port: ${PORT:8080}
```

### Activar un perfil

```bash
# Al ejecutar con Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Como variable de entorno
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run

# En application.yml (perfil por defecto para desarrollo)
spring:
  profiles:
    active: dev   # ⚠️ no hacer esto en prod, usar variable de entorno
```

---

## 12. Propiedades personalizadas y `@ConfigurationProperties`

Puedes definir tus propias propiedades en YAML y leerlas en Java de forma tipada.

```yaml
# application.yml
app:
  nombre: Tickets API
  version: 1.0.0
  max-tickets-por-usuario: 10
  cors:
    allowed-origins:
      - http://localhost:4200
      - https://mi-frontend.com
    allowed-methods:
      - GET
      - POST
      - PUT
      - DELETE
  mail:
    host: smtp.gmail.com
    port: 587
    remitente: noreply@empresa.com
```

```java
// Java — leer con @ConfigurationProperties (forma recomendada)
@ConfigurationProperties(prefix = "app")
public record AppProperties(
    String nombre,
    String version,
    int maxTicketsPorUsuario,
    CorsProperties cors,
    MailProperties mail
) {
    public record CorsProperties(
        List<String> allowedOrigins,
        List<String> allowedMethods
    ) {}

    public record MailProperties(
        String host,
        int port,
        String remitente
    ) {}
}

// Java — leer con @Value (forma simple, para valores individuales)
@Value("${app.nombre}")
private String nombre;

@Value("${app.max-tickets-por-usuario:5}")  // con valor por defecto
private int maxTickets;
```

> 📌 **Convención de nombres:** Spring Boot acepta tanto `camelCase` (`maxTicketsPorUsuario`) como `kebab-case` (`max-tickets-por-usuario`) en el YAML. Se recomienda usar `kebab-case` en el archivo `.yml` y `camelCase` en Java — Spring los convierte automáticamente (*Relaxed Binding*).

---

## 13. Errores comunes

### ❌ Usar tabulaciones en vez de espacios

```yaml
# ❌ Error — tabulación en vez de espacios (invisible pero fatal)
server:
	port: 8080   # TAB → falla con "mapping values are not allowed here"

# ✅ Correcto — 2 espacios
server:
  port: 8080
```

### ❌ Olvidar el espacio después de `:`

```yaml
# ❌ Error
server:
  port:8080    # sin espacio → Spring no lo parsea correctamente

# ✅ Correcto
server:
  port: 8080
```

### ❌ Strings sin comillas que YAML interpreta como otro tipo

```yaml
# ❌ Problemático
app:
  activo: yes        # YAML lo lee como booleano true, no como string "yes"
  version: 1.0       # YAML lo lee como número decimal, no como string "1.0"
  codigo: 007        # YAML lo lee como número 7 (pierde el 0 inicial)
  vacio:             # YAML lo lee como null, no como string ""

# ✅ Con comillas cuando importa el tipo
app:
  activo: "yes"
  version: "1.0"
  codigo: "007"
  vacio: ""
```

### ❌ Indentación inconsistente al definir listas

```yaml
# ❌ Error — el guión debe estar al mismo nivel
spring:
  profiles:
    active:
    - dev     # mal: el - debería estar indentado bajo active

# ✅ Correcto
spring:
  profiles:
    active:
      - dev
```

### ❌ Duplicar una clave raíz

```yaml
# ❌ Error — spring: aparece dos veces en el mismo bloque
spring:
  application:
    name: Tickets

spring:           # segunda aparición: sobreescribe la primera
  datasource:
    url: jdbc:h2:mem:testdb

# ✅ Correcto — todo bajo un único bloque spring:
spring:
  application:
    name: Tickets
  datasource:
    url: jdbc:h2:mem:testdb
```

---

## 14. Referencia rápida (cheat sheet)

```yaml
# ─── TIPOS DE DATOS ───────────────────────────────────────────
entero: 8080
decimal: 0.19
booleano: true
nulo: null
texto: Hola mundo
texto-especial: "valor: con dos puntos"
texto-escape: "linea1\nlinea2"

# ─── LISTAS ───────────────────────────────────────────────────
lista-bloque:
  - elemento1
  - elemento2
  - elemento3

lista-inline: [elemento1, elemento2, elemento3]

# ─── OBJETOS ANIDADOS ─────────────────────────────────────────
padre:
  hijo:
    nieto: valor

# ─── VARIABLES DE ENTORNO ─────────────────────────────────────
propiedad: ${VARIABLE}               # obligatoria
propiedad: ${VARIABLE:por-defecto}   # con fallback
propiedad: ${VARIABLE:}              # fallback vacío

# ─── MULTILÍNEA ───────────────────────────────────────────────
literal: |
  preserva
  los saltos

plegado: >
  une todas
  las líneas

# ─── COMENTARIOS ──────────────────────────────────────────────
# línea completa comentada
clave: valor  # comentario al final

# ─── ESTRUCTURA SPRING BOOT ───────────────────────────────────
spring:
  application:
    name: mi-api
  datasource:
    url: ${DB_URL:jdbc:h2:mem:testdb}
    username: ${DB_USER:sa}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

server:
  port: ${PORT:8080}
  servlet:
    context-path: /api/v1

logging:
  level:
    root: INFO
    com.empresa: DEBUG

# ─── PERFILES ─────────────────────────────────────────────────
---
spring:
  config:
    activate:
      on-profile: dev
# propiedades específicas de dev...

---
spring:
  config:
    activate:
      on-profile: prod
# propiedades específicas de prod...
```

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*

