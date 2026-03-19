# 🔐 Variables de Entorno — Configuración Segura de Aplicaciones

## ¿Qué son las variables de entorno?

Las **variables de entorno** son valores de configuración que se definen **fuera del código fuente**, en el sistema operativo o en un archivo externo. Permiten que la aplicación se comporte de forma diferente según el entorno donde se ejecuta (desarrollo, testing, producción) sin necesidad de cambiar el código.

> 📌 **Regla de oro:** ningún valor sensible (contraseñas, tokens, claves de API, URLs de bases de datos) debe estar escrito directamente en el código fuente ni subirse al repositorio.

---

## ¿Por qué usarlas?

| Problema sin variables de entorno | Solución con variables de entorno |
|----------------------------------|----------------------------------|
| Contraseñas hardcodeadas en el código | Valores sensibles fuera del repositorio |
| Cambiar URL de BD requiere modificar código | Cambiar solo el entorno, no el código |
| El mismo código no funciona en prod y local | Una app, múltiples configuraciones |
| Secretos expuestos en el historial de Git | Archivos `.env` nunca subidos al repo |

---

## Variables de entorno en Spring Boot

Spring Boot lee la configuración desde múltiples fuentes, en este orden de prioridad (de mayor a menor):

```
1. Variables de entorno del sistema operativo
2. Propiedades de sistema Java (-D flags)
3. application.properties / application.yml
4. Valores por defecto en el código (@Value con fallback)
```

### `application.properties` con variables de entorno

La sintaxis `${NOMBRE_VARIABLE}` permite referenciar una variable de entorno en `application.properties`:

```properties
# application.properties

spring.application.name=Tickets

# Puerto del servidor (por defecto 8080 si no se define PORT)
server.port=${PORT:8080}

# Base de datos (valores tomados del entorno)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# JWT Secret
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=${JWT_EXPIRATION:3600}
```

> 💡 La sintaxis `${VAR:valor_por_defecto}` define un **valor por defecto** si la variable no está definida en el entorno. Muy útil en desarrollo.

---

## Inyectar valores con `@Value`

La anotación `@Value` permite leer propiedades del `application.properties` (o del entorno) directamente en clases de Spring:

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Value("${app.tickets.max-per-user:10}")
    private int maxTicketsPerUser;

    @Value("${spring.application.name}")
    private String appName;

    public void validarLimite(Long userId) {
        if (countByUser(userId) >= maxTicketsPerUser) {
            throw new RuntimeException("Límite de tickets alcanzado para: " + appName);
        }
    }
}
```

---

## Inyectar grupos de propiedades con `@ConfigurationProperties`

Para múltiples propiedades relacionadas, es más elegante usar `@ConfigurationProperties`:

```properties
# application.properties
app.tickets.max-per-user=10
app.tickets.default-status=ABIERTO
app.tickets.notify-on-create=true
```

```java
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.tickets")
public class TicketsProperties {
    private int maxPerUser;
    private String defaultStatus;
    private boolean notifyOnCreate;
}

// Uso en un servicio
@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketsProperties ticketsProperties;

    public Ticket create(Ticket ticket) {
        ticket.setStatus(ticketsProperties.getDefaultStatus());
        return repository.save(ticket);
    }
}
```

---

## Archivo `.env` — Variables locales de desarrollo

El archivo `.env` es un archivo de texto plano que define variables de entorno para el entorno **local de desarrollo**. Es la forma más común de manejar configuración sensible sin hardcodearla.

### Formato del archivo `.env`

```dotenv
# .env — Variables de entorno locales (¡NUNCA subir al repositorio!)

# Servidor
PORT=8080

# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/tickets_db
DB_USER=postgres
DB_PASSWORD=mi_password_local

# Seguridad
JWT_SECRET=mi_clave_secreta_local_muy_larga_y_segura
JWT_EXPIRATION=3600

# Configuración de la app
APP_ENV=development
```

### Archivo `.env.example` — Plantilla pública

Se debe crear un archivo `.env.example` (sin valores reales) que **sí se sube al repositorio**, para que otros desarrolladores sepan qué variables necesitan configurar:

```dotenv
# .env.example — Copia este archivo como .env y completa los valores

PORT=8080

DB_URL=jdbc:postgresql://localhost:5432/nombre_bd
DB_USER=
DB_PASSWORD=

JWT_SECRET=
JWT_EXPIRATION=3600

APP_ENV=development
```

---

## Configurar `.env` en IntelliJ IDEA

IntelliJ IDEA no carga archivos `.env` de forma nativa para las configuraciones de ejecución. Hay dos formas de configurarlo:

### Opción 1 — Plugin `.env files support` (recomendada)

1. Abre IntelliJ IDEA → **File → Settings → Plugins**
2. Busca `"EnvFile"` o `".env files support"` e instala el plugin
3. Ve a **Run → Edit Configurations...**
4. Selecciona tu configuración de Spring Boot (`TicketsApplication`)
5. En la pestaña **EnvFile**, activa **"Enable EnvFile"**
6. Haz clic en `+` y selecciona tu archivo `.env`
7. Aplica y guarda

### Opción 2 — Definir variables manualmente en la configuración de ejecución

1. Ve a **Run → Edit Configurations...**
2. Selecciona tu configuración de Spring Boot
3. En el campo **Environment variables**, haz clic en el ícono `📋` o escribe directamente:

```
DB_URL=jdbc:postgresql://localhost:5432/tickets_db;DB_USER=postgres;DB_PASSWORD=secret
```

> ⚠️ Las variables definidas en la configuración de ejecución son **solo para ese equipo local** y no se guardan en el repositorio.

### Opción 3 — Spring Boot con `spring-dotenv` (librería)

Agrega la dependencia en `pom.xml`:

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

Con esta librería, Spring Boot carga automáticamente el archivo `.env` al arrancar, sin necesidad de configurar el IDE.

```properties
# application.properties — las variables del .env ya están disponibles
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

---

## Perfiles de Spring Boot

Spring Boot permite definir diferentes configuraciones por **perfil** de entorno:

```
src/main/resources/
├── application.properties          # configuración base (común a todos)
├── application-dev.properties      # solo para desarrollo local
├── application-staging.properties  # solo para staging/QA
└── application-prod.properties     # solo para producción
```

**`application-dev.properties`:**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
logging.level.root=DEBUG
```

**`application-prod.properties`:**
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
logging.level.root=WARN
```

**Activar un perfil:**
```bash
# Por variable de entorno (recomendado en producción)
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run

# Por propiedad en application.properties (desarrollo)
spring.profiles.active=dev

# Por argumento al ejecutar el JAR
java -jar tickets.jar --spring.profiles.active=prod
```

---

## `.gitignore` — Proteger las variables

Es **crítico** que el archivo `.env` esté en `.gitignore`. Si accidentalmente se sube al repositorio, las credenciales quedan expuestas en el historial de Git para siempre.

```gitignore
# .gitignore

# Variables de entorno locales
.env
.env.local
*.env

# Configuración de ejecución local de IntelliJ (puede contener env vars)
.idea/runConfigurations/

# Archivos compilados
target/
```

> 🚨 **Si accidentalmente subiste un `.env` con credenciales reales**: cambia inmediatamente todas las contraseñas y tokens expuestos. El historial de Git conserva el archivo aunque luego lo elimines.

---

## Flujo completo en el proyecto Tickets

```
.env (local, ignorado por Git)
  │
  ▼
IntelliJ IDEA (carga el .env via plugin o configuración)
  │
  ▼
application.properties
  spring.datasource.url=${DB_URL}        ← Spring lee la variable
  spring.datasource.password=${DB_PASSWORD}
  │
  ▼
Spring Boot (inyecta los valores en los beans)
  │
  ▼
@Value("${spring.datasource.url}")       ← disponible en cualquier clase Spring
```

---

## Resumen rápido

| Qué | Cómo | ¿Va al repositorio? |
|-----|------|:-------------------:|
| Valores sensibles | Archivo `.env` | ❌ No |
| Plantilla de variables | `.env.example` | ✅ Sí |
| Referencia en Spring Boot | `${VAR_NAME}` en `.properties` | ✅ Sí |
| Inyección en código | `@Value("${propiedad}")` | ✅ Sí |
| Exclusión de Git | `.gitignore` con `.env` | ✅ Sí |

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| Spring Boot Externalized Configuration | 📖 Oficial | [docs.spring.io/spring-boot/reference/features/external-config.html](https://docs.spring.io/spring-boot/reference/features/external-config.html) |
| Plugin EnvFile para IntelliJ | 🔧 Plugin | [plugins.jetbrains.com/plugin/7861-envfile](https://plugins.jetbrains.com/plugin/7861-envfile) |
| The Twelve-Factor App — Config | 📖 Metodología | [12factor.net/es/config](https://12factor.net/es/config) |
| spring-dotenv | 📦 Librería | [github.com/paulschwarz/spring-dotenv](https://github.com/paulschwarz/spring-dotenv) |

---

*[← Volver a Extras](../README.md)*

