# Spec — Lección 23: API Gateway con Spring Cloud Gateway MVC y Eureka

**Fecha:** 2026-06-18
**Estado:** Aprobado

---

## Resumen

Crear la lección 23 del curso DSY1103 sobre API Gateway como concepto y como implementación funcional completa usando Spring Cloud Gateway MVC + Eureka para Spring Boot 4.1.0. El estudiante construye dos proyectos nuevos (EurekaServer y Gateway) y registra todos los microservicios del curso en Eureka.

---

## Contexto y motivación

El curso llega a lección 22 con un ecosistema de microservicios que se llaman entre sí directamente por puerto. El cliente externo conoce y apunta a cada servicio individualmente. La lección 23 cierra el ciclo introduciendo un punto de entrada único (API Gateway) y registro automático de servicios (Eureka), que son los dos patrones de infraestructura faltantes para tener un ecosistema de microservicios production-ready.

---

## Decisiones de diseño

### Spring Cloud Gateway MVC (no WebFlux)

Se usa `spring-cloud-starter-gateway-mvc` en lugar de `spring-cloud-starter-gateway`. El curso completo usa Spring MVC; introducir programación reactiva (Mono/Flux) en la última lección sería desproporcionado. Gateway MVC ofrece el mismo resultado (routing, filtros, Eureka) con la API de servlet familiar. Disponible desde Spring Cloud 2023.0+, incluido en 2025.0.x.

### Enfoque Quick Start primero

README con código mínimo funcional en ~15 minutos, igual al patrón de lecciones 14 y 22. Los tres niveles de complejidad (routing, filtros, auth JWT) se explican en archivo dedicado, no bloqueando el flujo principal.

### Tres niveles acumulativos de seguridad

Cada nivel agrega sobre el anterior, con ejemplos independientes. El nivel 3 (JWT centralizado) incluye nota explícita sobre cómo coexiste con la seguridad de lección 16.

---

## Estructura de archivos

```
docs/lessons/23-api-gateway/
├── README.md                       # Quick Start: gateway + Eureka en ~15 min
├── 01_objetivo_y_alcance.md        # ¿De dónde venimos? Problema, objetivos de aprendizaje
├── 02_api_gateway_concepto.md      # Qué es, patrones (BFF, reverse proxy, edge), cuándo usarlo
├── 03_eureka_service_discovery.md  # Service discovery: problema, cómo funciona Eureka
├── 04_guion_paso_a_paso.md         # EurekaServer + Gateway: creación y configuración completa
├── 05_niveles_de_seguridad.md      # Nivel 1: routing / Nivel 2: filtros / Nivel 3: JWT
├── 06_ejemplos_practicos.md        # Rutas para todos los servicios del curso
├── 07_checklist_rubrica_minima.md  # Verificación mínima para aprobar
└── 08_actividad_individual.md      # Captura Eureka Dashboard con todos los servicios registrados
```

---

## Proyectos nuevos

### `proyects/EurekaServer/`

- Spring Boot 4.1.0
- Dependencia: `spring-cloud-starter-netflix-eureka-server`
- BOM: `spring-cloud-dependencies:2025.0.0`
- Anotación: `@EnableEurekaServer` en clase principal
- Puerto: `:8761`
- Dashboard: `http://localhost:8761`

### `proyects/Gateway/`

- Spring Boot 4.1.0
- Dependencias: `spring-cloud-starter-gateway-mvc` + `spring-cloud-starter-netflix-eureka-client`
- BOM: `spring-cloud-dependencies:2025.0.0`
- Puerto: `:8090`
- Routing definido en `application.yml` con `lb://` para service discovery

---

## Puertos del ecosistema completo

| Servicio         | Puerto |
|------------------|--------|
| EurekaServer     | :8761  |
| Gateway          | :8090  |
| Tickets          | :8080  |
| NotificationService | :8081 |
| AuditService     | :8082  |
| ReportsService   | :8083 (mencionado en curso, puede no estar implementado — se incluye en rutas del gateway como ejemplo, con nota al estudiante) |

Cada microservicio existente recibe la dependencia `eureka-client` y configuración `eureka.client.service-url.defaultZone`.

---

## Contenido por archivo

### README.md
- Sección "El problema": diagrama texto antes/después del gateway
- Quick Start: 4 pasos para levantar Eureka + Gateway + Tickets registrado
- Tabla de contenidos con duración estimada
- "Lo que construirás": lista de 5 items
- Lecturas recomendadas: lecciones 14, 15, 16, 20

### 01_objetivo_y_alcance.md
- ¿De dónde venimos? (resumen del ecosistema de lecciones anteriores)
- Lista de objetivos de aprendizaje (8 items)
- Qué NO cubre esta lección (Kubernetes Ingress, Istio, API Gateway como producto SaaS)
- Resultado esperado: Eureka Dashboard con N servicios registrados

### 02_api_gateway_concepto.md
- Definición y propósito
- Tres patrones: reverse proxy, BFF (Backend for Frontend), edge service
- Cuándo usarlo y cuándo NO usarlo
- Comparativa: sin gateway vs con gateway
- Spring Cloud Gateway MVC vs alternativas (Kong, AWS API Gateway, Nginx)

### 03_eureka_service_discovery.md
- El problema: IPs y puertos hardcodeados entre servicios
- Cómo funciona Eureka: registro, heartbeat, descubrimiento
- Diagrama de flujo: servicio → registra en Eureka → Gateway consulta Eureka → enruta
- `lb://nombre-servicio` como abstracción sobre IPs reales

### 04_guion_paso_a_paso.md
1. Crear EurekaServer (Spring Initializr, dependencias, `@EnableEurekaServer`, `application.yml`)
2. Crear Gateway (Spring Initializr, dependencias, `application.yml` con rutas)
3. Agregar `eureka-client` a Tickets y configurar `application.yml`
4. Levantar EurekaServer → Tickets → Gateway y verificar dashboard
5. Probar enrutamiento: `GET http://localhost:8090/tickets/tickets`
6. Registrar servicios adicionales (Notification, Audit, Reports)
7. Verificar balanceo con dos instancias de Tickets

### 05_niveles_de_seguridad.md

**Nivel 1 — Routing + Eureka (base)**
- Rutas estáticas en `application.yml`
- Rutas dinámicas con `lb://`
- Sin código Java adicional

**Nivel 2 — Filtros globales (intermedio)**
- `OncePerRequestFilter` para `X-Request-Id`
- Logging de latencia por ruta
- `AddRequestHeader` en `application.yml`
- Ejemplos completos sin nada reactivo

**Nivel 3 — Auth JWT centralizada (avanzado)**
- `OncePerRequestFilter` que valida token antes de enrutar
- `SecurityFilterChain` en el Gateway
- Rutas públicas vs protegidas en configuración
- Nota: coexistencia con Spring Security de lección 16
- Cuándo centralizar auth en gateway vs mantenerla en cada servicio

### 06_ejemplos_practicos.md
- Configuración completa de rutas para los 4 servicios del curso
- Ejemplos de llamadas antes (directo al servicio) y después (a través del gateway)
- Path rewriting: `/tickets/**` → `/ticket-app/tickets/**`
- Configuración de timeouts por ruta
- `application.yml` completo del Gateway

### 07_checklist_rubrica_minima.md
- EurekaServer levanta en `:8761`
- Dashboard Eureka accesible
- Al menos 2 servicios registrados en Eureka
- Gateway levanta en `:8090`
- Al menos una ruta funcional a través del gateway
- `lb://` usado en al menos una ruta

### 08_actividad_individual.md
- Instrucciones: levantar EurekaServer + Gateway + todos los servicios disponibles
- Verificar: `http://localhost:8761` muestra todos los servicios registrados
- Entregable: captura de pantalla del Eureka Dashboard con los servicios visibles
- Bonus (opcional): agregar un filtro `X-Request-Id` propio

---

## Stack completo de la lección

```
Spring Boot 4.1.0
Spring Cloud 2025.0.x (BOM — verificar versión exacta compatible en https://spring.io/projects/spring-cloud)
├── spring-cloud-starter-gateway-mvc       (Gateway, servlet/MVC, sin WebFlux)
├── spring-cloud-starter-netflix-eureka-server   (EurekaServer)
└── spring-cloud-starter-netflix-eureka-client   (todos los microservicios existentes + Gateway)
```

---

## Lo que NO cubre esta lección

- Programación reactiva / WebFlux / Mono / Flux
- Kubernetes Ingress
- Service mesh (Istio, Linkerd)
- API Gateway como producto SaaS (Kong, AWS API Gateway)
- Circuit breaker / Resilience4j (mencionado como próximo paso opcional)

---

## Actualización del índice

`INDICE_COMPLETO.md` requiere:
1. Agregar lección 23 en la tabla de "Producción III" (o nueva sección)
2. Actualizar el stack tecnológico con Spring Cloud Gateway MVC y Eureka
3. Agregar requisitos mínimos de la lección 23
