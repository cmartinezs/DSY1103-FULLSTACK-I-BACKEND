# Lección 20 — Checklist y Rúbrica Mínima

## Checklist antes de entregar

### Configuración

- [ ] `pom.xml` incluye `spring-boot-starter-hateoas`
- [ ] El proyecto compila
- [ ] El context path `/ticket-app` se mantiene
- [ ] Los endpoints siguen respondiendo con códigos HTTP correctos

### Implementación

- [ ] Existe un assembler o helper para construir links
- [ ] `GET /tickets/by-id/{id}` retorna `_links`
- [ ] `GET /tickets` retorna colección con links
- [ ] Cada ticket incluye link `self`
- [ ] La colección incluye link `self`
- [ ] Se agrega al menos un link adicional (`all`, `update`, `delete`, `audit-history`)
- [ ] Links condicionales respetan estado o permisos cuando aplica

### Documentación

- [ ] OpenAPI indica que las respuestas incluyen `_links`
- [ ] Swagger UI sigue funcionando
- [ ] README o documentación explica qué links se devuelven

---

## Rúbrica

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Dependencia y compilación | 15 | Proyecto arranca correctamente |
| Respuesta individual con links | 25 | `GET /tickets/by-id/{id}` incluye `_links.self` |
| Colección con links | 20 | `GET /tickets` usa `CollectionModel` |
| Links útiles de negocio | 15 | `update`, `delete`, `audit-history` o similar |
| Documentación OpenAPI actualizada | 15 | Swagger explica HATEOAS |
| Código ordenado | 10 | Lógica de links fuera del controller cuando sea posible |

**Total: 100 puntos**

---

## Red Flags

- Se eliminan endpoints existentes
- La API deja de respetar `/ticket-app`
- Los links apuntan a rutas incorrectas
- Se duplica lógica compleja en cada endpoint
- La aplicación no compila

