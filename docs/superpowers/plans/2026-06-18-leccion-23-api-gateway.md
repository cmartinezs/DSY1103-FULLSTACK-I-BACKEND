# Lección 23: API Gateway con Spring Cloud Gateway MVC y Eureka — Plan de Implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Crear la lección 23 completa: 8 archivos de documentación educativa en español + proyecto EurekaServer + proyecto Gateway + registro de todos los microservicios existentes en Eureka.

**Architecture:** Dos nuevos proyectos Spring Boot: `EurekaServer` (registro de servicios, `:8761`) y `Gateway` (punto de entrada único, `:8090`). Los 5 servicios existentes (Tickets-22, NotificationService, AuditService, SearchService, SLAService) reciben la dependencia `eureka-client` para registrarse automáticamente. La documentación sigue el patrón del curso: README quick-start + archivos numerados en español.

**Tech Stack:** Spring Boot 4.1.0, Spring Cloud 5.0.1 BOM, `spring-cloud-starter-netflix-eureka-server`, `spring-cloud-starter-gateway-mvc` (servlet/MVC sin WebFlux), `spring-cloud-starter-netflix-eureka-client`, Java 21.

## Global Constraints

- Spring Boot nuevos proyectos: `4.1.0`; servicios existentes se quedan en `4.0.5`
- Spring Cloud BOM: `spring-cloud-dependencies:5.0.1` — verificar versión exacta compatible con Spring Boot 4.1.0 en https://spring.io/projects/spring-cloud antes de usar (los servicios existentes usan `spring-cloud-starter-openfeign:5.0.1`, usar misma familia)
- Gateway: `spring-cloud-starter-gateway-mvc` — NUNCA `spring-cloud-starter-gateway` (ese es el reactivo/WebFlux)
- Sin programación reactiva: cero `Mono`, `Flux`, `WebFilter`, `ReactiveAuthenticationManager`
- GroupId todos los proyectos: `cl.duoc.fullstack`
- Paquetes nuevos proyectos: `cl.duoc.fullstack.eurekaserver` y `cl.duoc.fullstack.gateway`
- Idioma documentación: español
- Convención nombres archivos doc: `XX_nombre_descriptivo.md` (igual que lecciones 21-22)
- Sin `@EnableEurekaClient` en clientes — la dependencia en classpath activa auto-configuración
- En YAML de Gateway, usar `$\{segment}` (con backslash) en `RewritePath` para evitar que Spring resuelva como property placeholder
- Ecosystem de servicios: Tickets-22 (`:8080`, context-path `/ticket-app`), NotificationService (`:8081`), AuditService (`:8082`), SearchService (`:8084`), SLAService (`:8085`)

---

## Mapa de archivos

**Crear — EurekaServer:**
- `projects/EurekaServer/pom.xml`
- `projects/EurekaServer/src/main/java/cl/duoc/fullstack/eurekaserver/EurekaServerApplication.java`
- `projects/EurekaServer/src/main/resources/application.yml`
- `projects/EurekaServer/src/test/java/cl/duoc/fullstack/eurekaserver/EurekaServerApplicationTests.java`

**Crear — Gateway:**
- `projects/Gateway/pom.xml`
- `projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/GatewayApplication.java`
- `projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/filter/RequestIdFilter.java`
- `projects/Gateway/src/main/resources/application.yml`
- `projects/Gateway/src/test/java/cl/duoc/fullstack/gateway/GatewayApplicationTests.java`

**Modificar — Servicios existentes (solo agregar eureka-client):**
- `projects/Tickets-22/pom.xml` — agregar dependencia eureka-client
- `projects/Tickets-22/src/main/resources/application.yml` — agregar eureka config
- `projects/NotificationService/pom.xml` — agregar dependencia eureka-client
- `projects/NotificationService/src/main/resources/application.yml` — agregar eureka config
- `projects/AuditService/pom.xml` — agregar dependencia eureka-client
- `projects/AuditService/src/main/resources/application.yml` — agregar eureka config
- `projects/SearchService/pom.xml` — agregar dependencia eureka-client
- `projects/SearchService/src/main/resources/application.yml` — agregar eureka config
- `projects/SLAService/pom.xml` — agregar dependencia eureka-client
- `projects/SLAService/src/main/resources/application.yml` — agregar eureka config

**Crear — Documentación:**
- `docs/lessons/23-api-gateway/README.md`
- `docs/lessons/23-api-gateway/01_objetivo_y_alcance.md`
- `docs/lessons/23-api-gateway/02_api_gateway_concepto.md`
- `docs/lessons/23-api-gateway/03_eureka_service_discovery.md`
- `docs/lessons/23-api-gateway/04_guion_paso_a_paso.md`
- `docs/lessons/23-api-gateway/05_niveles_de_seguridad.md`
- `docs/lessons/23-api-gateway/06_ejemplos_practicos.md`
- `docs/lessons/23-api-gateway/07_checklist_rubrica_minima.md`
- `docs/lessons/23-api-gateway/08_actividad_individual.md`

**Modificar:**
- `docs/lessons/INDICE_COMPLETO.md`

---

## Task 1: Crear proyecto EurekaServer

**Files:**
- Create: `projects/EurekaServer/pom.xml`
- Create: `projects/EurekaServer/src/main/java/cl/duoc/fullstack/eurekaserver/EurekaServerApplication.java`
- Create: `projects/EurekaServer/src/main/resources/application.yml`
- Create: `projects/EurekaServer/src/test/java/cl/duoc/fullstack/eurekaserver/EurekaServerApplicationTests.java`

**Interfaces:**
- Produce: servidor Eureka en `http://localhost:8761`, dashboard en `http://localhost:8761/`
- Produce: endpoint de registro `http://localhost:8761/eureka/` que los clientes usarán

- [ ] **Paso 1: Crear estructura de directorios**

```bash
mkdir -p projects/EurekaServer/src/main/java/cl/duoc/fullstack/eurekaserver
mkdir -p projects/EurekaServer/src/main/resources
mkdir -p projects/EurekaServer/src/test/java/cl/duoc/fullstack/eurekaserver
```

- [ ] **Paso 2: Crear `projects/EurekaServer/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>

    <groupId>cl.duoc.fullstack</groupId>
    <artifactId>eureka-server</artifactId>
    <version>1.0.0</version>
    <name>EurekaServer</name>
    <description>Servidor de registro de servicios para DSY1103</description>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>5.0.1</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Paso 3: Crear `EurekaServerApplication.java`**

```java
package cl.duoc.fullstack.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

- [ ] **Paso 4: Crear `application.yml`**

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    wait-time-in-ms-when-sync-empty: 0
```

`register-with-eureka: false` y `fetch-registry: false` le dicen a Eureka que no intente registrarse a sí mismo (es el servidor, no un cliente).

- [ ] **Paso 5: Crear `EurekaServerApplicationTests.java`**

```java
package cl.duoc.fullstack.eurekaserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Paso 6: Compilar y verificar**

```bash
cd projects/EurekaServer
./mvnw package -DskipTests
```

Resultado esperado:
```
BUILD SUCCESS
```

Si falla con error de versión de Spring Cloud, verificar en https://spring.io/projects/spring-cloud la versión exacta compatible con Spring Boot 4.1.0 y actualizar `<spring-cloud.version>` en pom.xml.

- [ ] **Paso 7: Arrancar y verificar dashboard**

```bash
./mvnw spring-boot:run
```

Abrir `http://localhost:8761` — debe mostrar el dashboard de Eureka con la sección "Instances currently registered with Eureka" vacía (aún sin servicios registrados).

- [ ] **Paso 8: Commit**

```bash
git add projects/EurekaServer/
git commit -m "feat: add EurekaServer project for lesson 23"
```

---

## Task 2: Crear proyecto Gateway

**Files:**
- Create: `projects/Gateway/pom.xml`
- Create: `projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/GatewayApplication.java`
- Create: `projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/filter/RequestIdFilter.java`
- Create: `projects/Gateway/src/main/resources/application.yml`
- Create: `projects/Gateway/src/test/java/cl/duoc/fullstack/gateway/GatewayApplicationTests.java`

**Interfaces:**
- Consume: `http://localhost:8761/eureka/` (EurekaServer del Task 1)
- Produce: punto de entrada único en `http://localhost:8090`
- Produce: header `X-Request-Id` en cada respuesta (RequestIdFilter)

- [ ] **Paso 1: Crear estructura de directorios**

```bash
mkdir -p projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/filter
mkdir -p projects/Gateway/src/main/resources
mkdir -p projects/Gateway/src/test/java/cl/duoc/fullstack/gateway
```

- [ ] **Paso 2: Crear `projects/Gateway/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>

    <groupId>cl.duoc.fullstack</groupId>
    <artifactId>gateway</artifactId>
    <version>1.0.0</version>
    <name>Gateway</name>
    <description>API Gateway para DSY1103</description>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>5.0.1</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- Gateway MVC — servlet/Spring MVC, sin WebFlux -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway-mvc</artifactId>
        </dependency>
        <!-- Eureka client para service discovery con lb:// -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Paso 3: Crear `GatewayApplication.java`**

```java
package cl.duoc.fullstack.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

- [ ] **Paso 4: Crear `filter/RequestIdFilter.java`**

```java
package cl.duoc.fullstack.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        response.setHeader("X-Request-Id", requestId);
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        log.info("[Gateway] {} {} → {}ms | requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                System.currentTimeMillis() - start,
                requestId);
    }
}
```

- [ ] **Paso 5: Crear `application.yml`**

```yaml
server:
  port: 8090

spring:
  application:
    name: gateway
  cloud:
    gateway:
      mvc:
        routes:
          - id: tickets
            uri: lb://Tickets
            predicates:
              - Path=/tickets/**
            filters:
              # Reescribe /tickets/... → /ticket-app/tickets/...
              # Usar $\{segment} para evitar que Spring trate ${segment} como property
              - RewritePath=/tickets(?<segment>/?.*), /ticket-app/tickets$\{segment}

          - id: notification-service
            uri: lb://notification-service
            predicates:
              - Path=/notifications/**
            filters:
              - RewritePath=/notifications(?<segment>/?.*), /api/notifications$\{segment}

          - id: audit-service
            uri: lb://audit-service
            predicates:
              - Path=/audit/**
            filters:
              - RewritePath=/audit(?<segment>/?.*), /api/audit$\{segment}

          - id: search-service
            uri: lb://search-service
            predicates:
              - Path=/search/**
            filters:
              - RewritePath=/search(?<segment>/?.*), /api/search$\{segment}

          - id: sla-service
            uri: lb://sla-service
            predicates:
              - Path=/sla/**
            filters:
              - RewritePath=/sla(?<segment>/?.*), /api/sla$\{segment}

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    cl.duoc.fullstack.gateway: DEBUG
```

Nota sobre las rutas de NotificationService, AuditService, SearchService y SLAService: el prefijo `/api/` es el convencional para esos servicios. Verificar los `@RequestMapping` en cada controlador y ajustar el `RewritePath` si difieren.

- [ ] **Paso 6: Crear `GatewayApplicationTests.java`**

```java
package cl.duoc.fullstack.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Paso 7: Compilar**

```bash
cd projects/Gateway
./mvnw package -DskipTests
```

Resultado esperado:
```
BUILD SUCCESS
```

- [ ] **Paso 8: Verificar rutas con EurekaServer activo**

Con EurekaServer corriendo (Task 1), arrancar Gateway:

```bash
./mvnw spring-boot:run
```

En los logs debe aparecer:
```
Fetching registry from eureka
```

- [ ] **Paso 9: Commit**

```bash
git add projects/Gateway/
git commit -m "feat: add Gateway project with MVC routing and RequestIdFilter for lesson 23"
```

---

## Task 3: Registrar Tickets-22 en Eureka

**Files:**
- Modify: `projects/Tickets-22/pom.xml` — agregar `spring-cloud-starter-netflix-eureka-client:5.0.1`
- Modify: `projects/Tickets-22/src/main/resources/application.yml` — agregar eureka config

**Interfaces:**
- Consume: `http://localhost:8761/eureka/` (EurekaServer del Task 1)
- Produce: servicio `TICKETS` visible en dashboard Eureka y accesible vía `lb://Tickets` desde Gateway

- [ ] **Paso 1: Agregar dependencia eureka-client en `projects/Tickets-22/pom.xml`**

Añadir dentro de `<dependencies>`, antes del cierre `</dependencies>`:

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>5.0.1</version>
        </dependency>
```

Nota: se usa versión explícita `5.0.1` para ser consistente con `spring-cloud-starter-openfeign:5.0.1` que ya existe en este pom (no hay BOM declarado).

- [ ] **Paso 2: Agregar configuración Eureka en `projects/Tickets-22/src/main/resources/application.yml`**

Añadir al final del archivo (mantener todo lo que ya existe):

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

El campo `spring.application.name: Tickets` ya existe en el archivo y es lo que Eureka usa como nombre de registro. No modificar.

- [ ] **Paso 3: Compilar**

```bash
cd projects/Tickets-22
./mvnw package -DskipTests
```

Resultado esperado:
```
BUILD SUCCESS
```

- [ ] **Paso 4: Verificar registro en Eureka**

Con EurekaServer corriendo:

```bash
./mvnw spring-boot:run
```

Abrir `http://localhost:8761` — debe aparecer `TICKETS` en "Instances currently registered with Eureka".

- [ ] **Paso 5: Verificar enrutamiento desde Gateway**

Con EurekaServer, Tickets-22 y Gateway corriendo:

```bash
curl http://localhost:8090/tickets/tickets
```

Debe devolver el mismo JSON que:

```bash
curl http://localhost:8080/ticket-app/tickets
```

- [ ] **Paso 6: Commit**

```bash
git add projects/Tickets-22/pom.xml projects/Tickets-22/src/main/resources/application.yml
git commit -m "feat: register Tickets-22 with Eureka for lesson 23"
```

---

## Task 4: Registrar NotificationService, AuditService, SearchService y SLAService en Eureka

**Files:**
- Modify: `projects/NotificationService/pom.xml`
- Modify: `projects/NotificationService/src/main/resources/application.yml`
- Modify: `projects/AuditService/pom.xml`
- Modify: `projects/AuditService/src/main/resources/application.yml`
- Modify: `projects/SearchService/pom.xml`
- Modify: `projects/SearchService/src/main/resources/application.yml`
- Modify: `projects/SLAService/pom.xml`
- Modify: `projects/SLAService/src/main/resources/application.yml`

**Interfaces:**
- Produce: `NOTIFICATION-SERVICE`, `AUDIT-SERVICE`, `SEARCH-SERVICE`, `SLA-SERVICE` visibles en dashboard Eureka

El patrón es idéntico para los cuatro servicios. Se repite para cada uno.

- [ ] **Paso 1: Agregar eureka-client a NotificationService**

En `projects/NotificationService/pom.xml`, dentro de `<dependencies>`:

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>5.0.1</version>
        </dependency>
```

En `projects/NotificationService/src/main/resources/application.yml`, añadir al final:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

El archivo ya tiene `spring.application.name: notification-service` — no modificar.

- [ ] **Paso 2: Agregar eureka-client a AuditService**

En `projects/AuditService/pom.xml`, dentro de `<dependencies>`:

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>5.0.1</version>
        </dependency>
```

En `projects/AuditService/src/main/resources/application.yml`, añadir al final:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

El archivo ya tiene `spring.application.name: audit-service` — no modificar.

- [ ] **Paso 3: Agregar eureka-client a SearchService**

En `projects/SearchService/pom.xml`, dentro de `<dependencies>`:

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>5.0.1</version>
        </dependency>
```

En `projects/SearchService/src/main/resources/application.yml`, añadir al final:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

El archivo ya tiene `spring.application.name: search-service` — no modificar.

- [ ] **Paso 4: Agregar eureka-client a SLAService**

En `projects/SLAService/pom.xml`, dentro de `<dependencies>`:

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>5.0.1</version>
        </dependency>
```

En `projects/SLAService/src/main/resources/application.yml`, añadir al final:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

El archivo ya tiene `spring.application.name: sla-service` — no modificar.

- [ ] **Paso 5: Compilar los cuatro servicios**

```bash
cd projects/NotificationService && ./mvnw package -DskipTests
cd ../AuditService && ./mvnw package -DskipTests
cd ../SearchService && ./mvnw package -DskipTests
cd ../SLAService && ./mvnw package -DskipTests
```

Cada uno debe terminar con `BUILD SUCCESS`.

- [ ] **Paso 6: Verificar dashboard con todos los servicios**

Arrancar los cuatro servicios (en terminales separadas) con EurekaServer corriendo:

```bash
# Terminal 1 — ya corriendo: EurekaServer
# Terminal 2 — ya corriendo: Tickets-22
# Terminal 3
cd projects/NotificationService && ./mvnw spring-boot:run
# Terminal 4
cd projects/AuditService && ./mvnw spring-boot:run
# Terminal 5
cd projects/SearchService && ./mvnw spring-boot:run
# Terminal 6
cd projects/SLAService && ./mvnw spring-boot:run
```

Abrir `http://localhost:8761` — deben aparecer los 5 servicios: `TICKETS`, `NOTIFICATION-SERVICE`, `AUDIT-SERVICE`, `SEARCH-SERVICE`, `SLA-SERVICE`.

- [ ] **Paso 7: Commit**

```bash
git add projects/NotificationService/ projects/AuditService/ projects/SearchService/ projects/SLAService/
git commit -m "feat: register all microservices with Eureka for lesson 23"
```

---

## Task 5: Crear README.md de la lección

**Files:**
- Create: `docs/lessons/23-api-gateway/README.md`

- [ ] **Paso 1: Crear directorio**

```bash
mkdir -p docs/lessons/23-api-gateway
```

- [ ] **Paso 2: Crear `docs/lessons/23-api-gateway/README.md`**

```markdown
# Lección 23 — API Gateway con Spring Cloud Gateway MVC y Eureka

**Un solo punto de entrada para todos tus microservicios. Descubrimiento automático de servicios sin hardcodear puertos ni IPs.**

---

## Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. API Gateway — Concepto** | 15 min | Qué es y por qué usarlo |
| **03. Eureka — Service Discovery** | 10 min | Registro y descubrimiento automático |
| **04. Guión Paso a Paso** | 40 min | Implementación completa |
| **05. Niveles de Seguridad** | 20 min | Routing, filtros y auth JWT |
| **06. Ejemplos Prácticos** | 15 min | Rutas para todos los servicios del curso |
| **07. Checklist** | 5 min | Verificación |
| **08. Actividad Individual** | — | Tu tarea |

---

## El problema

Sin gateway, el cliente conoce y llama directamente a cada microservicio:

```
Cliente → http://localhost:8080/ticket-app/tickets     (Tickets)
Cliente → http://localhost:8081/api/notifications      (Notification)
Cliente → http://localhost:8082/api/audit              (Audit)
Cliente → http://localhost:8084/api/search             (Search)
Cliente → http://localhost:8085/api/sla                (SLA)
```

Si un servicio cambia de puerto, o se mueve a otro servidor, debes actualizar cada cliente.

Con API Gateway, hay un único punto de entrada:

```
Cliente → http://localhost:8090/tickets/...        → Tickets Service
Cliente → http://localhost:8090/notifications/...  → Notification Service
Cliente → http://localhost:8090/audit/...          → Audit Service
Cliente → http://localhost:8090/search/...         → Search Service
Cliente → http://localhost:8090/sla/...            → SLA Service
```

El gateway descubre dónde está cada servicio gracias a Eureka.

---

## Quick Start (~15 minutos)

### 1. Levantar EurekaServer

```bash
cd projects/EurekaServer
./mvnw spring-boot:run
```

Visita `http://localhost:8761` — verás el dashboard de Eureka vacío.

### 2. Levantar Tickets-22

```bash
cd projects/Tickets-22
./mvnw spring-boot:run
```

En el dashboard de Eureka aparecerá `TICKETS`.

### 3. Levantar Gateway

```bash
cd projects/Gateway
./mvnw spring-boot:run
```

### 4. Probar el enrutamiento

```bash
# Directo al servicio (antes)
curl http://localhost:8080/ticket-app/tickets

# A través del gateway (después)
curl -i http://localhost:8090/tickets/tickets
```

La respuesta del gateway incluirá el header `X-Request-Id` generado automáticamente.

---

## Lo que construirás

1. Servidor Eureka (`:8761`) como registro central de servicios
2. API Gateway (`:8090`) como único punto de entrada para todos los microservicios
3. Registro automático de los 5 servicios del curso en Eureka
4. Enrutamiento con `lb://` — el gateway pregunta a Eureka dónde está cada servicio
5. Filtro global `X-Request-Id` con `OncePerRequestFilter` (sin código reactivo)

---

## Lecturas recomendadas

- Lección 15: Comunicación entre Microservicios (RestTemplate y FeignClient)
- Lección 16: Spring Security (base para el Nivel 3 de seguridad)
- Lección 20: Docker y Docker Compose (para correr todo el stack en contenedores)

---

*Lección 23 — [← Volver al Índice](../INDICE_COMPLETO.md)*
```

- [ ] **Paso 3: Commit**

```bash
git add docs/lessons/23-api-gateway/README.md
git commit -m "docs: add lesson 23 README with quick start"
```

---

## Task 6: Crear archivos 01, 02 y 03 (conceptos)

**Files:**
- Create: `docs/lessons/23-api-gateway/01_objetivo_y_alcance.md`
- Create: `docs/lessons/23-api-gateway/02_api_gateway_concepto.md`
- Create: `docs/lessons/23-api-gateway/03_eureka_service_discovery.md`

- [ ] **Paso 1: Crear `01_objetivo_y_alcance.md`**

```markdown
# Lección 23 — Objetivo y Alcance

## ¿De dónde venimos?

Después de 22 lecciones tienes un ecosistema completo:

- **Tickets Service** (`:8080/ticket-app`) — servicio principal con CRUD, seguridad, HATEOAS y documentación
- **NotificationService** (`:8081`) — notificaciones por tipo y destinatario
- **AuditService** (`:8082`) — registro de cambios y trazabilidad
- **SearchService** (`:8084`) — búsqueda de tickets
- **SLAService** (`:8085`) — control de tiempos de resolución

Cada servicio expone su propio puerto. Cualquier frontend, aplicación mobile o script que consuma estas APIs debe conocer todos los puertos y rutas base. Si algo cambia, todo falla.

Esta lección resuelve eso con un API Gateway.

---

## Objetivos de aprendizaje

Al terminar esta lección podrás:

1. Explicar qué es un API Gateway y qué problema resuelve en un ecosistema de microservicios
2. Distinguir entre reverse proxy, BFF (Backend for Frontend) y edge service
3. Crear un Eureka Server con Spring Boot y `@EnableEurekaServer`
4. Crear un API Gateway con `spring-cloud-starter-gateway-mvc` (sin código reactivo)
5. Registrar microservicios en Eureka usando `spring-cloud-starter-netflix-eureka-client`
6. Configurar rutas con service discovery usando `lb://nombre-servicio`
7. Agregar un filtro global al gateway con `OncePerRequestFilter`
8. Explicar los tres niveles de seguridad que puede implementar un gateway

---

## Qué NO cubre esta lección

- Programación reactiva (Mono, Flux, WebFlux) — el gateway MVC es suficiente para este curso
- Kubernetes Ingress — capa de enrutamiento en orquestación de contenedores
- Service mesh (Istio, Linkerd) — control de tráfico a nivel de infraestructura
- API Gateway como producto SaaS (Kong, AWS API Gateway, Azure APIM)
- Circuit breaker y Resilience4j — patrón de resiliencia para el siguiente nivel

---

## Resultado esperado

Al terminar:

1. `http://localhost:8761` muestra el Eureka Dashboard con los 5 servicios del curso registrados
2. `http://localhost:8090/tickets/tickets` devuelve la lista de tickets (misma respuesta que `localhost:8080/ticket-app/tickets`)
3. Cada respuesta del gateway incluye el header `X-Request-Id` con un UUID único

---

## Nuevos proyectos en este curso

| Proyecto | Puerto | Rol |
|----------|--------|-----|
| `EurekaServer` | `:8761` | Registro y descubrimiento de servicios |
| `Gateway` | `:8090` | Punto de entrada único, routing, filtros |
```

- [ ] **Paso 2: Crear `02_api_gateway_concepto.md`**

```markdown
# API Gateway — Concepto

## ¿Qué es un API Gateway?

Un API Gateway es un servidor que actúa como único punto de entrada para un conjunto de microservicios. Los clientes externos no hablan directamente con cada servicio — le hablan al gateway, y el gateway decide a qué servicio reenviar cada petición.

---

## ¿Qué problemas resuelve?

### Sin gateway

| Problema | Consecuencia |
|---|---|
| El cliente conoce todos los puertos y rutas base | Cambiar un puerto rompe todos los clientes |
| Cada servicio implementa su propia seguridad | Inconsistencia y código duplicado |
| No hay un lugar central para logging, headers o rate limiting | Se repite en cada servicio |
| Todos los servicios están expuestos directamente | Mayor superficie de ataque |
| Balanceo de carga manual | El cliente elige la instancia |

### Con gateway

| Solución | Beneficio |
|---|---|
| Un solo punto de entrada (`:8090`) | Los clientes solo necesitan conocer el gateway |
| Autenticación centralizada (opcional) | Un solo punto de validación |
| Filtros globales | Se aplican a todas las rutas sin repetir código |
| Solo el gateway está expuesto | Los servicios internos son privados |
| `lb://` con Eureka | El gateway balancea automáticamente entre instancias |

---

## Tres patrones comunes

### 1. Reverse Proxy (el más simple)

El gateway reenvía peticiones sin modificarlas. Actúa como un intermediario transparente.

```
Cliente  →  Gateway (:8090)  →  Tickets (:8080)
                             →  Notification (:8081)
                             →  Audit (:8082)
```

### 2. BFF — Backend for Frontend

Un gateway dedicado por tipo de cliente. Cada gateway adapta las respuestas al formato que necesita ese cliente.

```
App Web    →  Gateway-Web    →  Microservicios (respuesta completa)
App Mobile →  Gateway-Mobile →  Microservicios (respuesta reducida, optimizada)
```

### 3. Edge Service

El gateway agrega funcionalidades transversales: autenticación, caché, rate limiting, transformación de respuestas, compresión.

```
Cliente  →  Gateway (valida JWT, agrega headers, comprime)  →  Microservicios
```

---

## ¿Cuándo usar un API Gateway?

**Úsalo cuando tienes:**
- 3 o más microservicios que el cliente consume directamente
- Necesidad de un único punto de autenticación
- Múltiples tipos de clientes (web, mobile, terceros)
- Necesidad de logging y monitoreo centralizado

**No lo uses cuando:**
- Solo tienes un microservicio (overhead innecesario)
- El proyecto es un monolito
- Estás en las primeras fases de un proyecto pequeño

---

## Spring Cloud Gateway MVC vs alternativas

| Opción | Tipo | Ideal para |
|---|---|---|
| **Spring Cloud Gateway MVC** | Open source, Spring MVC, sin reactive | Proyectos Spring Boot sin WebFlux ✅ Este curso |
| Spring Cloud Gateway (WebFlux) | Open source, reactivo, alto throughput | Sistemas con miles de req/s concurrentes |
| Kong | Open source / Enterprise, lenguaje-agnóstico | Infraestructura independiente del lenguaje |
| AWS API Gateway | SaaS, sin mantenimiento | Proyectos en AWS |
| Nginx | Proxy inverso genérico | Routing simple sin lógica de negocio |

**¿Por qué Gateway MVC en este curso?**

Gateway MVC usa Spring MVC y Servlet API — exactamente lo mismo que has usado en las 22 lecciones anteriores. Los filtros son `OncePerRequestFilter`, no `WebFilter` reactivo. No necesitas aprender Reactor ni `Mono`/`Flux` para usarlo.
```

- [ ] **Paso 3: Crear `03_eureka_service_discovery.md`**

```markdown
# Eureka — Service Discovery

## El problema de las IPs y puertos hardcodeados

En la lección 15 viste cómo un microservicio llama a otro:

```java
// NotificationClient.java — Lección 15
restClient.post()
    .uri("http://localhost:8081/api/notifications")  // ← puerto hardcodeado
    ...
```

Esto funciona en desarrollo local, pero tiene tres problemas:

1. **Puerto cambia** → el código deja de funcionar hasta nuevo deploy
2. **Múltiples instancias** → ¿cuál de las dos instancias de NotificationService usas?
3. **Producción** → los servicios en contenedores o en la nube tienen IPs dinámicas

**Eureka resuelve esto con un registro central de servicios.**

---

## Cómo funciona Eureka

```
  ┌──────────────────────────────────────────┐
  │             EurekaServer (:8761)         │
  │                                          │
  │   TICKETS           → localhost:8080     │
  │   NOTIFICATION-SERVICE → localhost:8081  │
  │   AUDIT-SERVICE     → localhost:8082     │
  │   SEARCH-SERVICE    → localhost:8084     │
  │   SLA-SERVICE       → localhost:8085     │
  └──────────────────────────────────────────┘
         ↑ registro              ↓ consulta
    [Microservicios]           [Gateway]
```

### 1. Registro al arrancar

Cuando un servicio arranca con `spring-cloud-starter-netflix-eureka-client` en el classpath, se registra automáticamente en Eureka enviando:
- Nombre del servicio (`spring.application.name`)
- IP y puerto actuales
- Estado de salud (`UP` / `DOWN` / `OUT_OF_SERVICE`)

No necesitas ninguna anotación. Solo la dependencia y la URL del servidor Eureka en `application.yml`.

### 2. Heartbeat cada 30 segundos

Cada servicio envía un "latido" para confirmar que sigue activo. Si Eureka no recibe heartbeat en 90 segundos, elimina el registro automáticamente.

### 3. Consulta cuando el Gateway necesita enrutar

El Gateway consulta a Eureka: "¿dónde está `notification-service`?" y obtiene la IP y puerto actuales. Si hay dos instancias, el Gateway balancea las peticiones entre ellas (round-robin por defecto).

---

## `lb://` — la abstracción sobre IPs reales

En el `application.yml` del Gateway:

```yaml
# ❌ Sin service discovery — hardcodeado
uri: http://localhost:8081

# ✅ Con service discovery
uri: lb://notification-service
```

`lb://` significa "load balanced". En lugar de una IP fija, el Gateway pregunta a Eureka cuál es la IP actual del servicio `notification-service` en cada petición.

El nombre después de `lb://` debe coincidir exactamente con el `spring.application.name` del servicio (case-insensitive en Eureka, pero por convención se usa el mismo caso).

---

## ¿Qué pasa si Eureka cae?

- Los servicios guardan una copia local del registro (caché de 30 segundos)
- El Gateway puede seguir enrutando peticiones hasta que la caché expire
- Los nuevos arranques de servicios no pueden registrarse hasta que Eureka vuelva

Para producción real se usan múltiples instancias de Eureka con replicación peer-to-peer. En este curso usamos una sola instancia — suficiente para aprender el patrón.

---

## Dashboard de Eureka

`http://localhost:8761` muestra:

- Todos los servicios registrados con nombre, IP, puerto y estado
- Tiempo desde el último heartbeat
- Modo de protección (`EMERGENCY` si muchos servicios caen al mismo tiempo)

El modo de protección evita que Eureka elimine registros masivamente durante una caída de red parcial. En desarrollo local puede activarse si detienes varios servicios rápido — es normal y esperado.
```

- [ ] **Paso 4: Commit**

```bash
git add docs/lessons/23-api-gateway/01_objetivo_y_alcance.md \
        docs/lessons/23-api-gateway/02_api_gateway_concepto.md \
        docs/lessons/23-api-gateway/03_eureka_service_discovery.md
git commit -m "docs: add lesson 23 conceptual files 01-03"
```

---

## Task 7: Crear `04_guion_paso_a_paso.md`

**Files:**
- Create: `docs/lessons/23-api-gateway/04_guion_paso_a_paso.md`

- [ ] **Paso 1: Crear `04_guion_paso_a_paso.md`**

```markdown
# Guión Paso a Paso

## Paso 1: Crear el proyecto EurekaServer

### 1.1 Estructura de archivos

```
projects/EurekaServer/
├── pom.xml
└── src/main/
    ├── java/cl/duoc/fullstack/eurekaserver/
    │   └── EurekaServerApplication.java
    └── resources/
        └── application.yml
```

### 1.2 `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
    </parent>

    <groupId>cl.duoc.fullstack</groupId>
    <artifactId>eureka-server</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>5.0.1</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 1.3 Clase principal

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

`@EnableEurekaServer` activa el servidor de registro. Solo va en la clase principal, en ningún otro lugar.

### 1.4 `application.yml`

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 1.5 Verificar

```bash
cd projects/EurekaServer
./mvnw spring-boot:run
```

Abrir `http://localhost:8761` → debe mostrar el dashboard con "No instances available".

---

## Paso 2: Crear el proyecto Gateway

### 2.1 `pom.xml` — dependencias clave

```xml
<dependencies>
    <!-- Gateway servlet/MVC — SIN WebFlux -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway-mvc</artifactId>
    </dependency>
    <!-- Eureka client para que lb:// funcione -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

> ⚠️ No uses `spring-cloud-starter-gateway` (sin el `-mvc`). Ese es el reactivo y requiere WebFlux.

### 2.2 `application.yml` — rutas

```yaml
server:
  port: 8090

spring:
  application:
    name: gateway
  cloud:
    gateway:
      mvc:
        routes:
          - id: tickets
            uri: lb://Tickets
            predicates:
              - Path=/tickets/**
            filters:
              - RewritePath=/tickets(?<segment>/?.*), /ticket-app/tickets$\{segment}

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

Cómo funciona el `RewritePath`:
- Petición al gateway: `GET /tickets/by-id/1`
- Captura `/by-id/1` como `segment`
- Reescribe a: `/ticket-app/tickets/by-id/1`
- Envía a Tickets Service en `:8080`

### 2.3 Verificar

Con EurekaServer corriendo:

```bash
cd projects/Gateway
./mvnw spring-boot:run
```

En los logs verás que el Gateway consulta a Eureka por los servicios registrados.

---

## Paso 3: Agregar eureka-client a Tickets-22

En `projects/Tickets-22/pom.xml`, dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    <version>5.0.1</version>
</dependency>
```

En `projects/Tickets-22/src/main/resources/application.yml`, al final:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

No necesitas agregar `@EnableEurekaClient` — la dependencia en el classpath activa la auto-configuración.

---

## Paso 4: Verificar en Eureka Dashboard

Con EurekaServer y Tickets-22 corriendo, abrir `http://localhost:8761`.

En "Instances currently registered with Eureka" debe aparecer:

```
TICKETS    n/a (1)    UP (1) - DESKTOP-XXX:Tickets:8080
```

---

## Paso 5: Probar enrutamiento a través del Gateway

Con los tres servicios corriendo (EurekaServer, Tickets-22, Gateway):

```bash
# Antes (directo al servicio)
curl http://localhost:8080/ticket-app/tickets

# Después (a través del gateway)
curl -i http://localhost:8090/tickets/tickets
```

La respuesta del gateway incluirá:

```
HTTP/1.1 200 OK
X-Request-Id: a3f8b2c1-...
Content-Type: application/json
```

El `X-Request-Id` es generado por `RequestIdFilter` en el Gateway.

---

## Paso 6: Registrar los demás servicios

Repetir el Paso 3 para cada uno de los servicios restantes:

| Servicio | pom.xml a modificar | application.yml a modificar |
|---|---|---|
| NotificationService | `projects/NotificationService/pom.xml` | `projects/NotificationService/src/main/resources/application.yml` |
| AuditService | `projects/AuditService/pom.xml` | `projects/AuditService/src/main/resources/application.yml` |
| SearchService | `projects/SearchService/pom.xml` | `projects/SearchService/src/main/resources/application.yml` |
| SLAService | `projects/SLAService/pom.xml` | `projects/SLAService/src/main/resources/application.yml` |

En cada `application.yml` agregar al final:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## Paso 7: Verificar el ecosistema completo

Con todos los servicios corriendo, `http://localhost:8761` debe mostrar:

```
TICKETS               UP (1)
NOTIFICATION-SERVICE  UP (1)
AUDIT-SERVICE         UP (1)
SEARCH-SERVICE        UP (1)
SLA-SERVICE           UP (1)
```

Probar una ruta por cada servicio:

```bash
curl http://localhost:8090/tickets/tickets
curl http://localhost:8090/notifications/...  # ajustar según endpoint real
curl http://localhost:8090/audit/...          # ajustar según endpoint real
```
```

- [ ] **Paso 2: Commit**

```bash
git add docs/lessons/23-api-gateway/04_guion_paso_a_paso.md
git commit -m "docs: add lesson 23 step-by-step guide"
```

---

## Task 8: Crear `05_niveles_de_seguridad.md` y `06_ejemplos_practicos.md`

**Files:**
- Create: `docs/lessons/23-api-gateway/05_niveles_de_seguridad.md`
- Create: `docs/lessons/23-api-gateway/06_ejemplos_practicos.md`

- [ ] **Paso 1: Crear `05_niveles_de_seguridad.md`**

```markdown
# Niveles de Seguridad en el Gateway

Un gateway puede ser tan simple como un proxy o tan completo como la capa de seguridad de todo el sistema. Aquí se presentan tres niveles acumulativos — cada uno agrega sobre el anterior.

---

## Nivel 1 — Routing + Service Discovery (base)

El nivel más simple: el gateway solo enruta peticiones. No agrega seguridad ni lógica.

```yaml
spring:
  cloud:
    gateway:
      mvc:
        routes:
          - id: tickets
            uri: lb://Tickets
            predicates:
              - Path=/tickets/**
            filters:
              - RewritePath=/tickets(?<segment>/?.*), /ticket-app/tickets$\{segment}
```

**Qué protege:** Nada — cualquier cliente puede llamar a cualquier ruta.

**Cuándo es suficiente:**
- Entorno interno (servicios detrás de VPN o firewall)
- Cada microservicio tiene su propia seguridad (como en la lección 16)
- El gateway solo centraliza routing, no seguridad

---

## Nivel 2 — Filtros Globales (intermedio)

Se agrega lógica transversal sin tocar los microservicios: identificadores de request, logging de latencia, headers de correlación.

### Filtro `X-Request-Id` con logging de latencia

```java
// projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/filter/RequestIdFilter.java

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        response.setHeader("X-Request-Id", requestId);

        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long latency = System.currentTimeMillis() - start;

        log.info("[Gateway] {} {} → {}ms | requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                latency,
                requestId);
    }
}
```

`OncePerRequestFilter` garantiza que el filtro se ejecuta una vez por request, incluso si el gateway hace forward interno. Al ser un `@Component`, Spring lo registra automáticamente.

### Headers adicionales por ruta en `application.yml`

También puedes agregar headers a peticiones o respuestas directamente en la configuración:

```yaml
spring:
  cloud:
    gateway:
      mvc:
        routes:
          - id: tickets
            uri: lb://Tickets
            predicates:
              - Path=/tickets/**
            filters:
              - RewritePath=/tickets(?<segment>/?.*), /ticket-app/tickets$\{segment}
              - AddRequestHeader=X-Gateway-Source, dsy1103-gateway
              - AddResponseHeader=X-Powered-By, Spring-Cloud-Gateway-MVC
```

**Qué protege:** No protege acceso, pero da visibilidad completa sobre el tráfico.

**Cuándo usarlo:**
- En todos los proyectos — el costo es mínimo y el beneficio en debugging es alto
- Cuando necesitas correlacionar logs entre servicios con el mismo `requestId`

---

## Nivel 3 — Autenticación JWT Centralizada (avanzado)

El gateway valida el token JWT antes de enrutar. Si el token es inválido o falta, el gateway rechaza la petición sin que llegue a ningún microservicio.

> **Nota:** Este nivel requiere decidir si cada microservicio sigue validando tokens (doble validación, más seguro) o si delega completamente al gateway (más simple, menos redundante). Para producción real, la doble validación es más segura.

### Dependencia adicional en Gateway

```xml
<!-- projects/Gateway/pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Filtro de autenticación JWT

```java
// projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/filter/JwtAuthFilter.java

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String SECRET = "mi-clave-secreta-debe-ser-larga-para-ser-segura";
    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator/health",
            "/tickets/public"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Token requerido\"}");
            return;
        }

        String token = header.substring(7);
        if (!isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Token inválido o expirado\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        try {
            // Validación básica de firma — en producción usar librería JWT como jjwt
            // Esta implementación es un placeholder educativo
            return token != null && token.length() > 10;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_PATHS.stream().anyMatch(request.getRequestURI()::startsWith);
    }
}
```

> Para producción real, usar `io.jsonwebtoken:jjwt` para verificar firma y expiración. El ejemplo anterior es solo para ilustrar el patrón del filtro.

### `SecurityFilterChain` en Gateway

```java
// projects/Gateway/src/main/java/cl/duoc/fullstack/gateway/config/SecurityConfig.java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                    JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**Cuándo usar este nivel:**
- El gateway es el único punto de entrada expuesto a Internet
- Los microservicios internos están en una red privada y confían en el gateway
- El equipo quiere centralizar la lógica de autenticación

**Cuidado:** Si coexiste con Spring Security en lección 16 (que también valida tokens en cada servicio), el cliente debe enviar el token y ambas capas lo validarán. Esto es redundante pero más seguro.
```

- [ ] **Paso 2: Crear `06_ejemplos_practicos.md`**

```markdown
# Ejemplos Prácticos

## `application.yml` completo del Gateway

```yaml
server:
  port: 8090

spring:
  application:
    name: gateway
  cloud:
    gateway:
      mvc:
        routes:
          # ── Tickets Service (:8080, context-path /ticket-app) ──────────
          - id: tickets
            uri: lb://Tickets
            predicates:
              - Path=/tickets/**
            filters:
              # /tickets/by-id/1 → /ticket-app/tickets/by-id/1
              - RewritePath=/tickets(?<segment>/?.*), /ticket-app/tickets$\{segment}

          # ── Notification Service (:8081) ────────────────────────────────
          - id: notification-service
            uri: lb://notification-service
            predicates:
              - Path=/notifications/**
            filters:
              # /notifications/... → /api/notifications/...
              - RewritePath=/notifications(?<segment>/?.*), /api/notifications$\{segment}

          # ── Audit Service (:8082) ───────────────────────────────────────
          - id: audit-service
            uri: lb://audit-service
            predicates:
              - Path=/audit/**
            filters:
              - RewritePath=/audit(?<segment>/?.*), /api/audit$\{segment}

          # ── Search Service (:8084) ──────────────────────────────────────
          - id: search-service
            uri: lb://search-service
            predicates:
              - Path=/search/**
            filters:
              - RewritePath=/search(?<segment>/?.*), /api/search$\{segment}

          # ── SLA Service (:8085) ─────────────────────────────────────────
          - id: sla-service
            uri: lb://sla-service
            predicates:
              - Path=/sla/**
            filters:
              - RewritePath=/sla(?<segment>/?.*), /api/sla$\{segment}

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    cl.duoc.fullstack.gateway: DEBUG
```

---

## Comparativa: antes y después del gateway

### Tickets — Listar todos

| | Antes (directo) | Después (gateway) |
|---|---|---|
| URL | `GET http://localhost:8080/ticket-app/tickets` | `GET http://localhost:8090/tickets/tickets` |
| Header en respuesta | — | `X-Request-Id: uuid` |

### Tickets — Obtener por ID

| | Antes | Después |
|---|---|---|
| URL | `GET http://localhost:8080/ticket-app/tickets/by-id/1` | `GET http://localhost:8090/tickets/by-id/1` |

### Tickets — Crear

| | Antes | Después |
|---|---|---|
| URL | `POST http://localhost:8080/ticket-app/tickets` | `POST http://localhost:8090/tickets/tickets` |
| Body | `{"title": "Error login", "description": "..."}` | Mismo body |

---

## Cómo funciona el `RewritePath`

El filtro `RewritePath` usa expresiones regulares con grupos de captura nombrados:

```yaml
- RewritePath=/tickets(?<segment>/?.*), /ticket-app/tickets$\{segment}
```

| Petición al gateway | Regex captura | Enviado al servicio |
|---|---|---|
| `GET /tickets/tickets` | `segment = /tickets` | `GET /ticket-app/tickets/tickets` |
| `GET /tickets/by-id/1` | `segment = /by-id/1` | `GET /ticket-app/tickets/by-id/1` |
| `POST /tickets/tickets` | `segment = /tickets` | `POST /ticket-app/tickets/tickets` |
| `DELETE /tickets/by-id/3` | `segment = /by-id/3` | `DELETE /ticket-app/tickets/by-id/3` |

> El `$\{segment}` usa `$\` (no solo `$`) para que YAML no intente resolver `${segment}` como una property de Spring.

---

## Verificar que los headers llegan

```bash
curl -i http://localhost:8090/tickets/tickets
```

Respuesta esperada (encabezados relevantes):

```
HTTP/1.1 200 OK
X-Request-Id: 3f8a21b4-4c92-4e07-9d61-f27e8a1b3c5f
X-Powered-By: Spring-Cloud-Gateway-MVC
Content-Type: application/json
Transfer-Encoding: chunked
```

---

## Balanceo de carga con dos instancias de Tickets

Arrancar dos instancias de Tickets en puertos distintos:

```bash
# Terminal 1 — instancia en puerto 8080 (por defecto)
cd projects/Tickets-22
./mvnw spring-boot:run

# Terminal 2 — instancia en puerto 8086
cd projects/Tickets-22
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8086"
```

En `http://localhost:8761` aparecerán dos instancias de `TICKETS`. El gateway distribuirá las peticiones entre ellas automáticamente (round-robin).
```

- [ ] **Paso 3: Commit**

```bash
git add docs/lessons/23-api-gateway/05_niveles_de_seguridad.md \
        docs/lessons/23-api-gateway/06_ejemplos_practicos.md
git commit -m "docs: add lesson 23 security levels and practical examples"
```

---

## Task 9: Crear `07_checklist_rubrica_minima.md` y `08_actividad_individual.md`

**Files:**
- Create: `docs/lessons/23-api-gateway/07_checklist_rubrica_minima.md`
- Create: `docs/lessons/23-api-gateway/08_actividad_individual.md`

- [ ] **Paso 1: Crear `07_checklist_rubrica_minima.md`**

```markdown
# Checklist — Rúbrica Mínima

Marca cada ítem antes de considerar la lección como completa.

## EurekaServer

- [ ] `EurekaServerApplication.java` tiene `@EnableEurekaServer`
- [ ] `application.yml` tiene `register-with-eureka: false` y `fetch-registry: false`
- [ ] EurekaServer arranca en `:8761` sin errores
- [ ] Dashboard disponible en `http://localhost:8761`

## Gateway

- [ ] `pom.xml` usa `spring-cloud-starter-gateway-mvc` (NO `spring-cloud-starter-gateway`)
- [ ] `pom.xml` incluye `spring-cloud-starter-netflix-eureka-client`
- [ ] `application.yml` tiene rutas bajo `spring.cloud.gateway.mvc.routes`
- [ ] Al menos una ruta usa `lb://` (no URL hardcodeada)
- [ ] `RequestIdFilter` extiende `OncePerRequestFilter` y está anotado con `@Component`
- [ ] Gateway arranca en `:8090` sin errores

## Servicios registrados

- [ ] `Tickets-22` registrado — aparece `TICKETS` en Eureka Dashboard
- [ ] Al menos 2 servicios adicionales registrados (NotificationService, AuditService, SearchService o SLAService)
- [ ] Todos los servicios muestran estado `UP` en el dashboard

## Enrutamiento

- [ ] `GET http://localhost:8090/tickets/tickets` devuelve la misma respuesta que `GET http://localhost:8080/ticket-app/tickets`
- [ ] La respuesta incluye el header `X-Request-Id`

## Documentación

- [ ] Puedes explicar la diferencia entre `spring-cloud-starter-gateway-mvc` y `spring-cloud-starter-gateway`
- [ ] Puedes explicar qué hace `lb://` y por qué no se usa una URL hardcodeada
- [ ] Puedes explicar los tres niveles de seguridad de un gateway
```

- [ ] **Paso 2: Crear `08_actividad_individual.md`**

```markdown
# Actividad Individual — Lección 23

## Objetivo

Demostrar que el ecosistema completo de microservicios del curso está registrado en Eureka y es accesible a través del Gateway.

---

## Lo que debes hacer

### 1. Levantar todos los servicios

Abre **6 terminales** y levanta cada servicio en este orden:

```bash
# Terminal 1 — Registro
cd projects/EurekaServer && ./mvnw spring-boot:run

# Terminal 2 — Tickets
cd projects/Tickets-22 && ./mvnw spring-boot:run

# Terminal 3 — Notifications
cd projects/NotificationService && ./mvnw spring-boot:run

# Terminal 4 — Audit
cd projects/AuditService && ./mvnw spring-boot:run

# Terminal 5 — Search
cd projects/SearchService && ./mvnw spring-boot:run

# Terminal 6 — SLA
cd projects/SLAService && ./mvnw spring-boot:run

# Terminal 7 — Gateway (última, después de que los demás estén UP)
cd projects/Gateway && ./mvnw spring-boot:run
```

### 2. Verificar el Eureka Dashboard

Abrir `http://localhost:8761` y esperar a que aparezcan los 5 servicios con estado `UP`:

```
TICKETS               UP (1)
NOTIFICATION-SERVICE  UP (1)
AUDIT-SERVICE         UP (1)
SEARCH-SERVICE        UP (1)
SLA-SERVICE           UP (1)
```

### 3. Probar el gateway

```bash
curl -i http://localhost:8090/tickets/tickets
```

Verificar que la respuesta incluya `X-Request-Id` en los headers.

---

## Entregable

Una **captura de pantalla** del dashboard de Eureka (`http://localhost:8761`) mostrando los 5 servicios registrados con estado `UP`.

La captura debe incluir la barra de dirección del navegador con la URL `localhost:8761`.

---

## Bonus (opcional, sin calificación)

Implementa el Nivel 2 de seguridad: agrega un header `X-Gateway-Timestamp` con la fecha y hora actual al `RequestIdFilter`.

```java
response.setHeader("X-Gateway-Timestamp",
    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
```

Verifica que el header aparece en la respuesta con:

```bash
curl -i http://localhost:8090/tickets/tickets | grep X-Gateway-Timestamp
```
```

- [ ] **Paso 3: Commit**

```bash
git add docs/lessons/23-api-gateway/07_checklist_rubrica_minima.md \
        docs/lessons/23-api-gateway/08_actividad_individual.md
git commit -m "docs: add lesson 23 checklist and individual activity"
```

---

## Task 10: Actualizar `INDICE_COMPLETO.md`

**Files:**
- Modify: `docs/lessons/INDICE_COMPLETO.md`

- [ ] **Paso 1: Actualizar el título del archivo**

Cambiar la primera línea:

```markdown
# 📚 Índice Completo del Curso — 23 Lecciones
```

(El título ya dice 23 lecciones — verificar que sea correcto y no decir 24.)

- [ ] **Paso 2: Agregar lección 23 en la sección "Producción III"**

Localizar la tabla de "Producción III" y agregar la fila de lección 23:

```markdown
### 🟤 Producción III (Lecciones 19-23)
Documentación, contenedores, navegabilidad, testing y gateway

| # | Título | Status | Destacado |
|---|--------|--------|-----------|
| 19 | OpenAPI Specification (OAS) | ✅ Completada | **Swagger UI, Contratos de API, Ejemplos** |
| 20 | Docker, Docker Compose y Docker Desktop | ✅ Completada | **Contenedores, Compose, Windows/Linux/macOS** |
| 21 | HATEOAS en APIs | ✅ Completada | **Links, _links, Respuestas Navegables** |
| 22 | Pruebas Unitarias en Microservicios | ✅ Completada | **JUnit 5, Mockito, Given When Then** |
| 23 | API Gateway con Spring Cloud Gateway MVC y Eureka | ✅ Completada | **Gateway MVC, Eureka, lb://, Filtros Globales** |
```

- [ ] **Paso 3: Agregar lección 23 en la navegación recomendada**

En el bloque de texto `Avanzado`, agregar la nueva lección:

```
Avanzado
├─ ...
├─ Lección 22     (Pruebas unitarias)
└─ Lección 23     (API Gateway + Eureka)
```

- [ ] **Paso 4: Agregar sección de detalle de lección 23**

Al final de las secciones de detalle existentes, agregar:

```markdown
## 🎯 Lección 23 — API Gateway con Spring Cloud Gateway MVC y Eureka

**Tema:** Punto de entrada único para todos los microservicios

- ✅ EurekaServer como registro central de servicios
- ✅ Spring Cloud Gateway MVC (servlet, sin WebFlux)
- ✅ Routing con service discovery (`lb://`)
- ✅ Filtros globales con `OncePerRequestFilter`
- ✅ Tres niveles de seguridad: routing, filtros, JWT centralizado

**Proyectos:** `EurekaServer` + `Gateway` (nuevos) + eureka-client en los 5 servicios existentes

**Habilidades:**
- [ ] Crear y configurar un Eureka Server
- [ ] Crear un API Gateway con Spring Cloud Gateway MVC
- [ ] Registrar microservicios en Eureka sin anotaciones
- [ ] Enrutar con `lb://` para service discovery automático
- [ ] Agregar filtros globales con `OncePerRequestFilter`
- [ ] Explicar los niveles de seguridad de un gateway
```

- [ ] **Paso 5: Actualizar el stack tecnológico**

En la sección "Stack Tecnológico (Completo)", agregar al bloque de Spring Cloud:

```
├─ Spring Cloud Gateway MVC (API Gateway, servlet)
├─ Spring Cloud Netflix Eureka Server (registro de servicios)
└─ Spring Cloud Netflix Eureka Client (registro automático)
```

- [ ] **Paso 6: Agregar requisitos mínimos lección 23**

En la sección "Requisitos Mínimos por Lección", agregar:

```markdown
### Lección 23
- ✅ EurekaServer arranca en `:8761` y muestra dashboard
- ✅ Al menos 3 servicios registrados en Eureka con estado UP
- ✅ Gateway arranca en `:8090` con al menos una ruta funcional
- ✅ `GET http://localhost:8090/tickets/tickets` devuelve la misma respuesta que directo al servicio
- ✅ Respuesta del gateway incluye header `X-Request-Id`
```

- [ ] **Paso 7: Commit**

```bash
git add docs/lessons/INDICE_COMPLETO.md
git commit -m "docs: update INDICE_COMPLETO with lesson 23 API Gateway"
```

---

## Self-Review

### Cobertura del spec

| Requisito del spec | Tarea que lo implementa |
|---|---|
| EurekaServer proyecto independiente | Task 1 |
| Gateway con gateway-mvc + eureka-client | Task 2 |
| Todos los servicios del curso registrados | Tasks 3 y 4 |
| Tres niveles de seguridad con ejemplos | Task 8 (05_niveles_de_seguridad.md) |
| Sin programación reactiva | Global Constraint, Task 2 pom.xml |
| README quick-start 15 min | Task 5 |
| Guion paso a paso | Task 7 |
| Actividad: captura Eureka Dashboard | Task 9 |
| Actualizar INDICE_COMPLETO | Task 10 |
| path rewriting /tickets → /ticket-app/tickets | Task 2 application.yml y Task 7 guion |

### Consistency check

- `spring.application.name: Tickets` en Tickets-22 → `lb://Tickets` en Gateway ✅
- `spring.application.name: notification-service` → `lb://notification-service` ✅
- Misma versión Spring Cloud (`5.0.1`) en todos los pom.xml ✅
- `$\{segment}` (con backslash) en todos los `RewritePath` ✅
- `OncePerRequestFilter` en el filtro, no `WebFilter` reactivo ✅
- `spring.cloud.gateway.mvc.routes` (con `mvc`), no `spring.cloud.gateway.routes` ✅
