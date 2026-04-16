# Lección 17 - Logback vs Serilog

## Comparativa

| Aspecto | Logback (Java) | Serilog (.NET) |
|---------|--------|--------|
| **Lenguaje** | Java | C# |
| **Formato default** | Texto plano | Structured (JSON) |
| **Integración Spring** | Nativa | No aplicable |
| **Almacenamiento** | Archivo + consola | Sink-based (múltiples destinos) |
| **Performance** | Bueno | Excelente |

## Logback (aquí usamos)

**Ventajas:**
- Integrado en Spring Boot
- Configuración XML/YAML simple
- Rotación de archivos automática

**Desventajas:**
- Logs en texto plano (difícil de parsear en prod)
- No es structured logging

## Serilog (alternativa para .NET)

**Ventajas:**
- Structured logging (JSON)
- Enrichment poderoso
- Sinks múltiples

**Desventajas:**
- Solo .NET
- Más complejo que Logback

## Cuándo cada uno

- **Logback:** Desarrollo, testing, aplicaciones Spring pequeñas
- **Serilog:** Aplicaciones .NET, especialmente con ELK Stack
- **JSON + ELK:** Producción con múltiples servicios

## Pattern en Logback

```yaml
logging:
  pattern:
    console: "%d{HH:mm:ss} [%-5level] %logger{36} - %msg%n"
    #       timestamp      level      class name      message
```

**Ejemplo de salida:**
```
14:32:10 [INFO ] TicketService - Ticket creado: #5
14:32:11 [DEBUG] TicketRepository - Guardando: Ticket{id=5}
```
