# Lección 17 - Checklist y rúbrica

## Checklist

- [ ] @Slf4j agregado en TicketService
- [ ] @Slf4j agregado en TicketController
- [ ] log.info() en create(), updateById(), deleteById()
- [ ] log.error() captura excepciones con stack trace
- [ ] logging.level.root configurado en application.yml
- [ ] application-dev.yml con DEBUG
- [ ] application-prod.yml con INFO/WARN
- [ ] Logs aparecen en consola al ejecutar
- [ ] Archivo logs/tickets.log se crea

## Rúbrica (50 pts)

| Criterio | Pts |
|----------|-----|
| @Slf4j presente en Service y Controller | 10 |
| Mínimo 5 logs INFO en operaciones principales | 15 |
| log.error() captura excepciones | 10 |
| Configuración YAML por perfil | 10 |
| Archivo log se genera correctamente | 5 |

**Total: 50 puntos**

Red flags:
❌ Ningún @Slf4j presente
❌ Logs con System.out.println()
❌ Sin configuración YAML
