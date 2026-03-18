# 04 - Checklist de cobertura mínima (alineado a rúbrica)

Esta clase 1 no busca cubrir toda la evaluación, pero sí dejar evidencia temprana de los indicadores clave.

## IE 1.2.1 - Estructura CSR con separación real

Checklist:

- [ ] Existen paquetes `controller`, `service`, `repository`, `model`
- [ ] `Controller` no contiene acceso directo a colecciones
- [ ] `Service` orquesta lógica y llama a `Repository`
- [ ] `Repository` concentra acceso a datos en memoria

Evidencia observable:

- Flujo `Controller -> Service -> Repository`

## IE 1.1.2 - Diseño de endpoints REST

Checklist:

- [ ] Base path versionado: `/api/v1`
- [ ] Recurso en plural: `/tickets` o `/users`
- [ ] Método HTTP correcto (`GET` para listar)
- [ ] Nombres semánticos (sin verbos en URL)

Evidencia observable:

- Endpoint funcional `GET /api/v1/{recurso}`

## IE 1.1.3 - Respuestas REST y códigos HTTP

Checklist:

- [ ] Uso de `ResponseEntity` en el endpoint
- [ ] Retorno `200 OK` para consulta exitosa
- [ ] Respuesta en JSON serializable

Evidencia observable:

- Prueba en Postman/Insomnia con estado y body correctos

## IE 1.2.2 - Modelo y persistencia en memoria (avance inicial)

Checklist:

- [ ] Clase de dominio coherente (`Ticket` o `User`)
- [ ] `List` en repository para persistencia temporal
- [ ] Identificador definido (`id`)

Evidencia observable:

- Datos semilla visibles en respuesta JSON

## Configuración mínima Spring Boot (evidencia operativa base)

Checklist:

- [ ] Existe `src/main/resources/application.properties` con valores explícitos
- [ ] Se puede cambiar puerto con `server.port`
- [ ] Se puede cambiar prefijo global con `server.servlet.context-path`
- [ ] Existe `src/main/resources/banner.txt` personalizado

Evidencia observable:

- Aplicación inicia con banner propio
- Endpoint responde bajo nuevo puerto y context path (ejemplo: `GET /tickets-app/api/v1/tickets`)

## Indicadores que se preparan, pero no se exigen en esta clase

- `IE 1.2.3` CRUD completo y transformaciones
- `IE 1.3.1` validaciones de entrada con anotaciones
- `IE 1.3.2` manejo de excepciones más robusto
- `IE 1.3.3` cobertura de pruebas REST por todos los endpoints

## Criterio de logro sugerido para cierre de clase

Clase lograda si el estudiante:

- Presenta estructura CSR limpia
- Expone un endpoint REST versionado y semántico
- Retorna JSON con `ResponseEntity` y `200 OK`
- Justifica verbalmente por qué cada bloque de código vive en su capa
