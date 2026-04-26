# Lección 14 — Checklist y Rúbrica Mínima

## ✅ Checklist de Completitud

### Dependencias

- [ ] Spring Cloud OpenFeign agregado a `pom.xml` (si usas Feign)
- [ ] Resilience4j agregado (opcional, para circuit breaker)
- [ ] RestClient disponible (Spring 6.1+)

### Código

- [ ] `@EnableFeignClients` en aplicación principal (si usas Feign)
- [ ] RestClient.Builder inyectado (si usas RestClient)
- [ ] Cliente HTTP creado (RestClient, Feign interface o RestTemplate)
- [ ] Fallback implementado (para Feign)
- [ ] Manejo de errores implementado

### Configuración

- [ ] Timeouts configurados en `application.yml`
- [ ] Logging configurado para ver requests/responses
- [ ] URLs correctas (host y puerto)
- [ ] Perfil seleccionado correctamente

### Testing

- [ ] Probaste comunicación entre servicios
- [ ] Tests con mocks de clientes HTTP
- [ ] Probaste fallback cuando servicio cae

### Documentación

- [ ] Entiendo cuándo usar RestClient vs FeignClient vs RestTemplate
- [ ] Puedo explicar qué son microservicios
- [ ] Sé cómo manejar timeouts y errores
- [ ] Entiendo circuit breaker

---

## 🎓 Rúbrica de Evaluación

### 1. Implementación HTTP (40%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| Uso correcto | ❌ No funciona | ✅ Funciona | ✅ + optimizado |
| RestClient | ❌ No usa | ✅ Usa correctamente | ✅ + configurado |
| FeignClient | ❌ No comprende | ✅ Usa | ✅ + fallback |
| RestTemplate | ❌ Usa deprecated | ✅ Solo si legacy | ✅ N/A |
| Elección | ❌ Equivocada | ✅ Apropiada | ✅ + justificada |

### 2. Manejo de Errores (30%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| Timeouts | ❌ No configurado | ✅ Configurado | ✅ + por cliente |
| Excepciones | ❌ No manejadas | ✅ Try/catch | ✅ + custom handlers |
| Fallback | ❌ No existe | ✅ Básico | ✅ + resiliente |
| Logging | ❌ No hay | ✅ Básico | ✅ + detallado |

### 3. Comunicación (20%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| Entre servicios | ❌ No comunica | ✅ Comunica | ✅ + bidireccional |
| Modelos DTO | ❌ Mal formatos | ✅ Correcto | ✅ + documentados |
| Respuestas | ❌ Incompletas | ✅ Correctas | ✅ + validadas |

### 4. Conocimiento (10%)

| Criterio | Insuficiente | Satisfactorio | Excelente |
|----------|-------------|--------------|-----------|
| Microservicios | ❌ Confuso | ✅ Entiende | ✅ + ventajas/desventajas |
| RestClient vs Feign | ❌ No diferencia | ✅ Conoce diferencias | ✅ + cuándo usar cada uno |
| Debugging | ❌ No sabe | ✅ Puede debuggear | ✅ + logs avanzados |

---

*[← Volver a Lección 14](01_objetivo_y_alcance.md)*
