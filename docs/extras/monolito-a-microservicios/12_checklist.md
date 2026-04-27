# 12 — Checklist de migración

← [Volver al índice](./README.md)

---

## Cómo usar este checklist

Este documento es una **guía de verificación práctica** para cada fase de la migración de FabriTech. Cada sección corresponde a un momento del proceso. Se recomienda completarla en equipo, no individualmente.

> 🟢 `PASS` — cumple el criterio  
> 🔴 `FAIL` — no cumple, bloquea el avance  
> 🟡 `PARTIAL` — cumple parcialmente, requiere plan de acción  
> ⚪ `N/A` — no aplica para este servicio

---

## Checklist 1: ¿Estamos listos para empezar la migración?

Completar ANTES de iniciar cualquier extracción de servicios.

### Infraestructura

| # | Criterio | Estado |
|---|----------|--------|
| 1.1 | El API Gateway está desplegado y enrutando 100% del tráfico al monolito | ⬜ |
| 1.2 | El Service Registry (Eureka) está operativo y el monolito se registra en él | ⬜ |
| 1.3 | El Config Server está desplegado | ⬜ |
| 1.4 | El stack de observabilidad está operativo: Prometheus + Grafana + Zipkin | ⬜ |
| 1.5 | El Message Broker (Kafka) está desplegado con al menos 3 brokers (HA) | ⬜ |
| 1.6 | El pipeline de CI/CD base está configurado (build → test → push → deploy) | ⬜ |
| 1.7 | El ambiente de staging refleja fielmente producción | ⬜ |
| 1.8 | Existe un proceso documentado de rollback para el API Gateway | ⬜ |

### Equipo

| # | Criterio | Estado |
|---|----------|--------|
| 1.9 | Todos los squads conocen la arquitectura objetivo | ⬜ |
| 1.10 | Al menos un miembro por squad sabe operar Docker y Kubernetes | ⬜ |
| 1.11 | Los SLOs de cada servicio están definidos y documentados | ⬜ |
| 1.12 | El proceso de guardia (on-call) está definido por servicio | ⬜ |

### Código del monolito

| # | Criterio | Estado |
|---|----------|--------|
| 1.13 | El análisis de acoplamiento (ArchUnit) está ejecutado y documentado | ⬜ |
| 1.14 | Existe un mapa de dependencias entre módulos del monolito | ⬜ |
| 1.15 | El Event Storming fue realizado con representantes del negocio | ⬜ |
| 1.16 | Los bounded contexts están documentados y acordados | ⬜ |

---

## Checklist 2: Diseño de un nuevo microservicio

Completar ANTES de comenzar a codear un nuevo servicio.

### Definición de responsabilidades

| # | Criterio | Estado |
|---|----------|--------|
| 2.1 | La responsabilidad del servicio está documentada en una sola oración | ⬜ |
| 2.2 | Está documentado lo que el servicio NO hace (fronteras) | ⬜ |
| 2.3 | El servicio tiene un squad dueño definido | ⬜ |
| 2.4 | Los eventos que el servicio publica están documentados (nombre + payload) | ⬜ |
| 2.5 | Los eventos que el servicio consume están documentados | ⬜ |
| 2.6 | Las APIs síncronas que el servicio expone están documentadas (OpenAPI) | ⬜ |
| 2.7 | Las APIs síncronas que el servicio llama están documentadas | ⬜ |

### Base de datos

| # | Criterio | Estado |
|---|----------|--------|
| 2.8 | El servicio tiene su propia BD (no comparte con ningún otro) | ⬜ |
| 2.9 | La BD elegida es apropiada para el patrón de acceso (OLTP / caché / full-text) | ⬜ |
| 2.10 | Las migraciones de BD están gestionadas con Flyway o Liquibase | ⬜ |
| 2.11 | No existen FKs hacia tablas de otros servicios | ⬜ |
| 2.12 | Los datos que se "duplican" (snapshots) están justificados | ⬜ |

### Resilience

| # | Criterio | Estado |
|---|----------|--------|
| 2.13 | Todos los clientes HTTP tienen timeout configurado | ⬜ |
| 2.14 | Todos los clientes HTTP tienen Circuit Breaker configurado | ⬜ |
| 2.15 | Todos los Circuit Breakers tienen un fallback definido | ⬜ |
| 2.16 | Los consumers de Kafka son idempotentes | ⬜ |
| 2.17 | Se implementa el Outbox Pattern para garantizar entrega de eventos | ⬜ |

---

## Checklist 3: Antes del deploy a producción

Completar DESPUÉS de desarrollar el servicio y ANTES de exponerlo con tráfico real.

### Testing

| # | Criterio | Estado |
|---|----------|--------|
| 3.1 | Cobertura de tests unitarios > 60% | ⬜ |
| 3.2 | Tests de integración con la BD propia | ⬜ |
| 3.3 | Tests de integración con Kafka (Testcontainers) | ⬜ |
| 3.4 | Contract tests (Pact) con todos los consumers del servicio | ⬜ |
| 3.5 | Tests de carga (k6 / Gatling) para el escenario de peak | ⬜ |
| 3.6 | Tests de caos: ¿qué pasa si los servicios dependientes caen? | ⬜ |

### Configuración

| # | Criterio | Estado |
|---|----------|--------|
| 3.7 | NO hay secretos (passwords, API keys) en el código ni en el repositorio | ⬜ |
| 3.8 | La configuración de prod viene de variables de entorno o Vault | ⬜ |
| 3.9 | El health check `/actuator/health` responde correctamente | ⬜ |
| 3.10 | El endpoint `/actuator/health/readiness` verifica la BD y Kafka | ⬜ |
| 3.11 | Los logs están en formato JSON estructurado con traceId | ⬜ |
| 3.12 | Las métricas están expuestas en `/actuator/prometheus` | ⬜ |

### Observabilidad

| # | Criterio | Estado |
|---|----------|--------|
| 3.13 | Existe un dashboard de Grafana para el servicio | ⬜ |
| 3.14 | Las alertas críticas están configuradas (error rate > 5%, latencia p95 > SLO) | ⬜ |
| 3.15 | Las trazas aparecen en Zipkin para peticiones de prueba | ⬜ |

### Documentación

| # | Criterio | Estado |
|---|----------|--------|
| 3.16 | La API está documentada en Swagger UI y accesible | ⬜ |
| 3.17 | Existe un runbook: cómo reiniciar, cómo hacer rollback, cómo diagnosticar | ⬜ |
| 3.18 | El README del repositorio describe cómo correr el servicio localmente | ⬜ |

---

## Checklist 4: Migración de la BD del monolito

Para cuando se mueve una tabla del monolito a la BD del nuevo servicio.

### Preparación

| # | Criterio | Estado |
|---|----------|--------|
| 4.1 | Están documentados TODOS los lugares del monolito que acceden a las tablas a migrar | ⬜ |
| 4.2 | Existe un script de migración de datos (SQL) probado en staging | ⬜ |
| 4.3 | El script de migración valida la integridad de los datos migrados | ⬜ |
| 4.4 | Se estimó el tiempo de migración para el volumen de producción | ⬜ |
| 4.5 | Existe un plan de rollback si la migración falla a mitad | ⬜ |

### Estrategia Expand-Contract

| # | Criterio | Estado |
|---|----------|--------|
| 4.6 | Las tablas nuevas existen en la BD del microservicio (expand) | ⬜ |
| 4.7 | El código escribe simultáneamente en ambas BDs (dual write) | ⬜ |
| 4.8 | Se verificó que los datos de ambas BDs son consistentes (por al menos 48h) | ⬜ |
| 4.9 | Se eliminaron las escrituras del monolito a las tablas migradas (contract) | ⬜ |
| 4.10 | Las FKs desde el monolito a las tablas migradas fueron eliminadas | ⬜ |
| 4.11 | El monolito lee los datos mediante la API del microservicio (no de la BD) | ⬜ |

---

## Checklist 5: Canary Release y validación en producción

| # | Criterio | Estado |
|---|----------|--------|
| 5.1 | El Gateway está configurado para Canary (ej: 10% al nuevo servicio) | ⬜ |
| 5.2 | La tasa de error del canary es ≤ la del monolito durante 24h | ⬜ |
| 5.3 | La latencia del canary cumple el SLO durante 24h | ⬜ |
| 5.4 | No hay diferencias en los datos de negocio entre canary y monolito | ⬜ |
| 5.5 | El equipo estuvo en guardia activa durante las primeras 24h | ⬜ |

### Escalar a 100%

| # | Criterio | Estado |
|---|----------|--------|
| 5.6 | El 50% del tráfico fue validado por al menos 24h | ⬜ |
| 5.7 | El 100% del tráfico fue redirigido al nuevo servicio | ⬜ |
| 5.8 | El monolito sigue disponible (apagado pero desplegado) por al menos 72h post-migración | ⬜ |

---

## Checklist 6: Cierre — Retirar el módulo del monolito

| # | Criterio | Estado |
|---|----------|--------|
| 6.1 | 0% del tráfico va al módulo correspondiente del monolito | ⬜ |
| 6.2 | 30 días sin incidentes relacionados con la migración | ⬜ |
| 6.3 | El código del módulo fue eliminado del monolito | ⬜ |
| 6.4 | Las tablas migradas fueron eliminadas de `monolito_db` (backup previo) | ⬜ |
| 6.5 | El diagrama de arquitectura fue actualizado | ⬜ |
| 6.6 | Los runbooks del servicio nuevo están verificados | ⬜ |

---

## Resumen de fases y estado

Usar esta tabla para seguimiento del proyecto completo:

| Fase | Servicio | Estado | Inicio real | Fin real | Notas |
|------|----------|--------|-------------|----------|-------|
| 0 | Infraestructura base | ⬜ Pendiente | | | |
| 1 | auth-service | ⬜ Pendiente | | | |
| 2 | catalog-service | ⬜ Pendiente | | | |
| 3 | branch-service | ⬜ Pendiente | | | |
| 4 | customer-service | ⬜ Pendiente | | | |
| 5 | loyalty-service | ⬜ Pendiente | | | |
| 6 | pdf-service | ⬜ Pendiente | | | |
| 7 | email-service | ⬜ Pendiente | | | |
| 8 | notification-service | ⬜ Pendiente | | | |
| 9 | inventory-service | ⬜ Pendiente | | | |
| 10 | order-service | ⬜ Pendiente | | | |
| 11 | payment-service | ⬜ Pendiente | | | |
| 12 | shipping-service | ⬜ Pendiente | | | |
| 13 | procurement-service | ⬜ Pendiente | | | |
| 14 | manufacturing-service | ⬜ Pendiente | | | |
| 15 | report-service | ⬜ Pendiente | | | |
| 16 | **Retirar monolito** | ⬜ Pendiente | | | 🎉 |

---

*← [11 — Anti-patrones](./11_antipatrones.md) | [Volver al índice →](./README.md)*
