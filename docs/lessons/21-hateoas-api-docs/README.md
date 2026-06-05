# Lección 20 — Implementación de HATEOAS en la Documentación de APIs

**Aprende a enriquecer respuestas REST con enlaces navegables y a documentar APIs que guían al consumidor hacia las siguientes acciones disponibles.**

---

## Contenidos

| Documento | Duración | Para |
|-----------|----------|------|
| **01. Objetivo y Alcance** | 5 min | Entender qué aprenderás |
| **02. HATEOAS, Funcionamiento y Beneficios** | 15 min | Conceptos clave |
| **03. Guión Paso a Paso** | 25 min | Implementación práctica |
| **04. Ejemplos Prácticos** | 20 min | Código aplicado a Tickets |
| **05. Checklist** | 5 min | Verificación |
| **06. Actividad Individual** | - | Tu tarea |

---

## El problema

Una API REST tradicional suele devolver datos, pero no siempre indica qué se puede hacer después.

```json
{
  "id": 7,
  "title": "No puedo ingresar",
  "status": "OPEN"
}
```

Con HATEOAS, la respuesta también incluye enlaces:

```json
{
  "id": 7,
  "title": "No puedo ingresar",
  "status": "OPEN",
  "_links": {
    "self": { "href": "http://localhost:8080/ticket-app/tickets/by-id/7" },
    "all": { "href": "http://localhost:8080/ticket-app/tickets" }
  }
}
```

---

## Lo que construirás

1. Agregar Spring HATEOAS
2. Retornar recursos con enlaces
3. Documentar el formato enriquecido en Swagger/OpenAPI
4. Agregar enlaces `self`, `all`, `update` y `delete`
5. Aplicar reglas simples según estado del ticket

---

## Lecturas recomendadas

- Lección 06: CRUD completo
- Lección 18: Global Exception Handling
- Lección 19: OpenAPI Specification

---

*Lección 20 - [← Volver al Índice](../INDICE_COMPLETO.md)*
