# 📚 Índice Completo del Curso — 22 Lecciones

## 🎓 Estructura del Curso

### ⚫ Pre-requisitos (Lección 0)
Herramientas y versionado

| # | Título | Estado |
|---|--------|--------|
| 00 | Git & GitHub | ✅ Completada |

---

### 🟢 Fundamentos (Lecciones 1-4)
Conceptos básicos de web, HTTP y REST

| # | Título | Estado |
|---|--------|--------|
| 01 | Web y HTTP | ✅ Completada |
| 02 | APIs y REST | ✅ Completada |
| 03 | Tu Primera API | ✅ Completada |
| 04 | Responsabilidades | ✅ Completada |

---

### 🟡 Desarrollo Backend (Lecciones 5-10)
Construcción de aplicación completa

| # | Título | Status |
|---|--------|--------|
| 05 | POST y Validación | ✅ Completada |
| 06 | CRUD Completo | ✅ Completada |
| 07 | Manejo de Errores | ✅ Completada |
| 08 | DTOs y Mapeo | ✅ Completada |
| 09 | Repositorio Customizado | ✅ Completada |
| 10 | Introducción a JPA | ✅ Completada |

---

### 🔵 Bases de Datos y Relaciones (Lecciones 11-13)
Persistencia avanzada

| # | Título | Status | Destacado |
|---|--------|--------|-----------|
| 11 | Configuración de BD | ✅ Completada | **Perfiles Spring Boot + Variables de Entorno** |
| 12 | Relaciones JPA | ✅ Completada | **One-to-Many, Many-to-Many** |
| 13 | Historial y Auditoría | ✅ Completada | **Tracking de cambios** |

---

### 🟣 Producción I (Lecciones 14-15)
Migraciones y comunicación entre servicios

| # | Título | Status | Destacado |
|---|--------|--------|-----------|
| 14 | Migraciones Flyway | ✅ Completada | **SQL Versionado (H2=JPA, MySQL/Supabase=Flyway)** |
| 15 | Microservicios | ✅ Completada | **RestTemplate y FeignClient** |

---

### 🔴 Producción II (Lecciones 16-18)
Seguridad, auditoría y manejo de errores

| # | Título | Status | Destacado |
|---|--------|--------|-----------|
| 16 | Spring Security | ✅ Completada | **Autenticación + Autorización, Roles ADMIN/USER** |
| 17 | Logging | ✅ Completada | **SLF4J + Logback, Niveles DEBUG/INFO/WARN/ERROR** |
| 18 | Exception Handling Global | ✅ Completada | **@ControllerAdvice, Respuestas Uniformes** |

---

### 🟤 Producción III (Lecciones 19-21)
Documentación, navegabilidad y testing

| # | Título | Status | Destacado |
|---|--------|--------|-----------|
| 19 | OpenAPI Specification (OAS) | ✅ Completada | **Swagger UI, Contratos de API, Ejemplos** |
| 20 | HATEOAS en APIs | ✅ Completada | **Links, _links, Respuestas Navegables** |
| 21 | Pruebas Unitarias en Microservicios | ✅ Completada | **JUnit 5, Mockito, Given When Then** |

---

## 📖 Navegación Recomendada

```
Pre-requisitos
├─ Lección 0 (Git)

Principiante
├─ Lecciones 1-4  (Conceptos básicos)
└─ Lecciones 5-6  (Primera API funcional)

Intermedio
├─ Lecciones 7-10 (Backend completo)
├─ Lección 11     (Múltiples BD)
├─ Lección 12     (Relaciones JPA)
└─ Lección 13     (Auditoría)

Avanzado
├─ Lección 14     (Migraciones profesionales)
├─ Lección 15     (Microservicios)
├─ Lección 16     (Seguridad)
├─ Lección 17     (Logging)
├─ Lección 18     (Manejo global de errores)
├─ Lección 19     (Documentación OpenAPI)
├─ Lección 20     (HATEOAS)
└─ Lección 21     (Pruebas unitarias)
```

---

## 🎯 Lección 11 — Configuración de Bases de Datos

**Tema:** Perfiles de Spring Boot + Variables de Entorno

- ✅ 3 perfiles: H2, MySQL, Supabase
- ✅ Configuración segura con `.env`
- ✅ Carga de variables en IntelliJ IDEA
- ✅ No hardcodear credenciales

**Documentos:** 11 archivos (README, índice, guía paso a paso, ejemplos, cheat sheet, etc)

**Habilidades:**
- [ ] Usar perfiles de Spring Boot
- [ ] Gestionar variables de entorno
- [ ] Cambiar BD sin modificar código Java
- [ ] Proteger credenciales

---

## 🎯 Lección 12 — Relaciones JPA

**Tema:** Modelar relaciones entre entidades

**Habilidades:**
- [ ] One-to-Many y Many-to-One
- [ ] Many-to-Many
- [ ] Lazy vs Eager loading
- [ ] Cascade operations

---

## 🎯 Lección 13 — Historial y Auditoría

**Tema:** Tracking de cambios en la BD

**Habilidades:**
- [ ] Registrar cambios automáticamente
- [ ] Auditoría de datos
- [ ] Versionado de entidades

---

## 🎯 Lección 14 — Migraciones con Flyway

**Tema:** Versionado profesional de cambios de BD

- ✅ Flyway para MySQL y Supabase
- ✅ JPA automático para H2
- ✅ SQL versionado (V1, V2, V3...)
- ✅ Tabla de control `flyway_schema_history`

**Documentos:** 9 archivos (guión, configuración, ejemplos SQL, JPA vs Flyway, troubleshooting)

**Habilidades:**
- [ ] Crear migraciones SQL versionadas
- [ ] Entender diferencia JPA vs Flyway
- [ ] Aplicar migraciones automáticamente
- [ ] Manejar errores de migración

---

## 🎯 Lección 15 — Comunicación entre Microservicios

**Tema:** HTTP entre servicios independientes

- ✅ RestTemplate (simple y flexible)
- ✅ FeignClient (automático y elegante)
- ✅ Manejo de errores y timeouts
- ✅ Fallbacks y resilencia

**Documentos:** 9 archivos (guión, comparación, ejemplos, manejo de errores, debugging)

**Habilidades:**
- [ ] Implementar comunicación HTTP entre servicios
- [ ] Usar RestTemplate y FeignClient
- [ ] Configurar timeouts y reintentos
- [ ] Implementar fallbacks
- [ ] Debuggear problemas de comunicación

---

## 🎯 Lección 19 — Microservicios con OpenAPI Specification (OAS)

**Tema:** Documentación estándar de APIs REST

- ✅ OpenAPI Specification como contrato
- ✅ Swagger UI para leer y probar endpoints
- ✅ Documentación de DTOs, errores y códigos HTTP
- ✅ Aplicación a Tickets y microservicios de apoyo

**Documentos:** 6 archivos (objetivo, conceptos, guión, ejemplos, checklist, actividad)

**Habilidades:**
- [ ] Explicar qué es OAS
- [ ] Diferenciar OpenAPI y Swagger
- [ ] Publicar Swagger UI
- [ ] Documentar endpoints y DTOs
- [ ] Usar documentación como contrato entre microservicios

---

## 🎯 Lección 20 — Implementación de HATEOAS en la Documentación de APIs

**Tema:** Respuestas REST con enlaces navegables

- ✅ Spring HATEOAS
- ✅ `EntityModel` y `CollectionModel`
- ✅ Links `self`, `all`, `update`, `audit-history`
- ✅ Documentación del formato `_links`

**Documentos:** 6 archivos (objetivo, conceptos, guión, ejemplos, checklist, actividad)

**Habilidades:**
- [ ] Explicar qué es HATEOAS
- [ ] Agregar enlaces a respuestas REST
- [ ] Crear assemblers de links
- [ ] Documentar respuestas enriquecidas
- [ ] Evaluar cuándo HATEOAS aporta valor

---

## 🎯 Lección 21 — Pruebas Unitarias en Proyectos de Microservicios

**Tema:** Testing unitario con JUnit 5 y Mockito

- ✅ JUnit 5
- ✅ Mockito
- ✅ Modelo Given When Then
- ✅ Plan breve de pruebas unitarias
- ✅ Pruebas de servicios y fallbacks sin levantar microservicios reales

**Documentos:** 7 archivos (objetivo, conceptos, guión, plan, ejemplos GWT, checklist, actividad)

**Habilidades:**
- [ ] Explicar qué es testing
- [ ] Escribir pruebas unitarias de services
- [ ] Mockear repositories y clientes HTTP
- [ ] Probar errores y fallbacks
- [ ] Crear un plan mínimo de pruebas unitarias

---

## 🛠️ Stack Tecnológico (Completo)

```
Spring Boot 4.0.5
├─ Spring Web MVC (HTTP, REST)
├─ Spring Data JPA (ORM)
├─ Hibernate (JPA implementation)
├─ Flyway (Migraciones)
├─ Lombok (Boilerplate reduction)
├─ Jakarta Validation (@Valid, @NotBlank)
├─ Spring Cloud OpenFeign (Microservicios)
├─ Spring HATEOAS (_links)
├─ springdoc-openapi (OpenAPI / Swagger UI)
├─ JUnit 5 (Testing)
├─ Mockito (Mocks)
└─ Spring Boot DevTools (Hot reload)

Databases
├─ H2 (En memoria, tests)
├─ MySQL 8.0 (Local vía XAMPP)
└─ PostgreSQL (Supabase en la nube)

Tools
├─ Maven (Builds)
├─ Git (Versionado)
├─ IntelliJ IDEA (IDE)
└─ Docker (Opcional)
```

---

## 📊 Progresión de Complejidad

```
Lección 1-4:    Fundamentos         ░░░░░░░░░░ (7%)
Lección 5-6:    API simple          ░░░░░░░░░░░░░░░ (20%)
Lección 7-10:   Backend práctico    ░░░░░░░░░░░░░░░░░░░░░░░░░░░ (47%)
Lección 11:     BD múltiples        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ (60%)
Lección 12-13:  Relaciones/Auditría ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ (73%)
Lección 14:     Migraciones Flyway  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ (87%)
Lección 15:     Microservicios      ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ (71%)
Lección 16-18:  Producción II       ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ (86%)
Lección 19-21:  Docs y Testing      ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ (100%)
```

---

## ✅ Requisitos Mínimos por Lección

### Lección 11
- ✅ 4 archivos YAML configurados (application + h2/mysql/supabase)
- ✅ `.env.example` creado
- ✅ Variables de entorno funcionales
- ✅ App arranca con todos los perfiles

### Lección 12-13
- ✅ (Relaciones y auditoría - según lecciones originales)

### Lección 14
- ✅ Flyway en `pom.xml`
- ✅ Migraciones V1, V2, V3 creadas (mysql y supabase)
- ✅ Tabla `flyway_schema_history` con 3+ registros
- ✅ Logs muestran "Successfully applied N migrations"

### Lección 15
- ✅ Cliente HTTP implementado (RestTemplate o FeignClient)
- ✅ Comunicación entre 2 servicios funcional
- ✅ Manejo de errores y fallback
- ✅ Timeouts configurados
- ✅ Tests con mocks

### Lección 19
- ✅ Swagger UI disponible
- ✅ OpenAPI JSON disponible
- ✅ Endpoints principales documentados
- ✅ DTOs y errores documentados

### Lección 20
- ✅ Respuestas con `_links`
- ✅ `self` en recurso individual
- ✅ Links útiles de negocio
- ✅ OpenAPI actualizado con formato HATEOAS

### Lección 21
- ✅ Pruebas unitarias con JUnit 5
- ✅ Mockito para repositories y clientes externos
- ✅ Casos felices, errores y fallbacks
- ✅ Plan breve de pruebas unitarias

---

## 🚀 Próximos Pasos (Opcional)

Después de las 13 lecciones, puedes explorar:

- 🔐 **Autenticación:** JWT, OAuth, Spring Security
- 📊 **Logging:** SLF4J, Logback
- 🔍 **Monitoreo:** Prometheus, Grafana
- 🐳 **Containerización:** Docker, Docker Compose
- ☸️ **Orquestación:** Kubernetes
- 📈 **CI/CD:** GitHub Actions, GitLab CI
- 🔄 **Message Queues:** RabbitMQ, Kafka
- 📄 **Contract Testing:** Pact, validación OpenAPI en pipeline

---

## 📞 Soporte

- 📖 Documentación oficial: https://spring.io/projects/spring-boot
- 🔧 Maven Central: https://mvnrepository.com
- 💬 Stack Overflow: Tag `spring-boot`
- 🤝 Comunidad: r/SpringBoot, Spring Community

---

**Última actualización:** Abril 2026  
**Versión:** 2.0 (15 lecciones completas)  
**Estado:** ✅ Listo para producción

---

*[← Volver a Lecciones](../)*
