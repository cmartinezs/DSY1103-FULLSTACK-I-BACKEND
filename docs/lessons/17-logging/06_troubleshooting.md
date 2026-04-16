# Lección 17 - Troubleshooting

## Problema 1: Logs no aparecen en consola

**Causa:** Nivel de log muy alto.

**Solución:**
```yaml
logging:
  level:
    cl.duoc.fullstack.tickets: DEBUG  # Baja el nivel
```

## Problema 2: Demasiados logs (ruido)

**Causa:** Nivel DEBUG para librerías externas.

**Solución:**
```yaml
logging:
  level:
    root: WARN
    cl.duoc.fullstack.tickets: DEBUG
    org.springframework: WARN
    org.hibernate: WARN
```

## Problema 3: Archivo de log no se crea

**Causa:** Carpeta logs/ no existe o sin permisos.

**Solución:**
```bash
mkdir -p logs/
chmod 755 logs/
```

## Problema 4: Stack trace incompleto

**Causa:** Loguear sin pasar la excepción.

```java
// ❌ INCORRECTO
log.error("Error: " + e.getMessage());

// ✅ CORRECTO
log.error("Error", e);  // ← Incluye stack trace completo
```

## Problema 5: Performance degradada

**Causa:** Logueo síncrono + archivos lentos.

**Solución (avanzada):**
```xml
<!-- logback.xml -->
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <encoder>
        <pattern>...</pattern>
    </encoder>
    <rollingPolicy class="...">
        <fileNamePattern>logs/tickets-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
    </rollingPolicy>
</appender>
```

## Problema 6: Logs de Security ausentes

**Causa:** Nivel de Spring Security muy alto.

**Solución:**
```yaml
logging:
  level:
    org.springframework.security: DEBUG
```
