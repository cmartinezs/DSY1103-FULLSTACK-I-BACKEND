# El proyecto: Sistema de Soporte Técnico — Tickets

> Este documento es la referencia del proyecto que construirás a lo largo de las lecciones 04 a 09.
> Léelo antes de comenzar cualquiera de ellas. Cada lección indicará qué requerimientos de esta lista implementa.

---

## El escenario

Un equipo de soporte técnico recibe solicitudes de ayuda todos los días: equipos que no funcionan, accesos bloqueados, sistemas caídos. Hoy gestionan todo por correo y por teléfono.

El problema es evidente: no hay registro, no hay seguimiento, no hay forma de saber qué está pendiente y qué ya fue resuelto. Cuando alguien pregunta *"¿en qué quedó mi problema?"*, nadie tiene una respuesta clara.

Necesitan un sistema que les permita **registrar, consultar, actualizar y cerrar** solicitudes de soporte. A esas solicitudes las llamamos **tickets**.

---

## Los actores

| Actor | Rol |
|-------|-----|
| **El equipo de soporte** | Crea tickets cuando recibe una solicitud, los actualiza a medida que trabaja en ellos y los cierra cuando están resueltos |
| **La API REST** | El sistema que tú vas a construir; es el intermediario entre los datos y cualquier cliente que quiera consultarlos o modificarlos |

---

## Requerimientos de alto nivel

Estos son los requerimientos del sistema. Están numerados para que puedas rastrear cuáles implementas en cada lección.

| ID | Requerimiento |
|----|---------------|
| **REQ-01** | El sistema debe permitir consultar todos los tickets registrados |
| **REQ-02** | El sistema debe permitir registrar un nuevo ticket con título y descripción |
| **REQ-03** | Cada ticket debe tener un estado que comienza en `NEW` automáticamente al crearse |
| **REQ-04** | No se pueden registrar dos tickets con el mismo título |
| **REQ-05** | El sistema debe registrar automáticamente la fecha y hora exacta de creación |
| **REQ-06** | El sistema debe calcular automáticamente una fecha estimada de resolución (5 días desde la creación) |
| **REQ-07** | El sistema debe permitir consultar un ticket específico por su identificador |
| **REQ-08** | El sistema debe permitir actualizar el título o la descripción de un ticket existente |
| **REQ-09** | El sistema debe permitir eliminar un ticket |
| **REQ-10** | Cuando se solicita un ticket que no existe, el sistema debe responder con un error claro (`404 Not Found`) |
| **REQ-11** | Cuando ocurre un error, el sistema debe responder con un JSON que incluya un campo `message` con una descripción legible del problema |
| **REQ-12** | **El creador y el asignado de un ticket NO pueden ser el mismo usuario** |
| **REQ-13** | El título del ticket no puede estar vacío ni en blanco; intentarlo debe devolver `400 Bad Request` |
| **REQ-14** | La API debe aceptar un DTO de entrada (`TicketRequest`) separado del modelo de dominio (`Ticket`); el cliente solo envía lo que le corresponde |
| **REQ-15** | `GET /tickets` debe admitir un parámetro opcional `?status=` para filtrar tickets por estado; sin el parámetro devuelve todos, ordenados por fecha de creación |
| **REQ-16** | Los tickets deben persistirse en base de datos real: los datos sobreviven reinicios de la aplicación |
| **REQ-17** | Cada ticket debe registrar qué usuario lo creó (usuario creador) |
| **REQ-18** | Cada ticket puede ser asignado a un usuario técnico; la asignación puede cambiar durante la vida del ticket |
| **REQ-19** | El sistema debe registrar automáticamente un historial de cambios de estado de cada ticket, con el estado anterior, el nuevo estado y la fecha y hora del cambio |

---

## Mapa de requerimientos por lección

Esta tabla muestra de un vistazo cuándo se implementa cada requerimiento:

| Requerimiento | L04 | L05 | L06 | L07 | L08 | L09 | L10 | L11 | L12 | L13 |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| REQ-01 — Consultar todos los tickets | ✅ | | | | | | | | | |
| REQ-02 — Registrar un nuevo ticket | | ✅ | | | | | | | | |
| REQ-03 — Estado inicial `NEW` | | ✅ | | | | | | | | |
| REQ-04 — Sin títulos duplicados | | ✅ | | | | | | | | |
| REQ-05 — Fecha de creación automática | | ✅ | | | | | | | | |
| REQ-06 — Fecha estimada de resolución | | ✅ | | | | | | | | |
| REQ-07 — Consultar ticket por ID | | | ✅ | | | | | | | |
| REQ-08 — Actualizar ticket | | | ✅ | | | | | | | |
| REQ-09 — Eliminar ticket | | | ✅ | | | | | | | |
| REQ-10 — Error cuando no existe | | | ✅ | | | | | | | |
| REQ-11 — Error con cuerpo JSON `{"message":"..."}` | | | | ✅ | | | | | | |
| REQ-12 — Creador ≠ Asignado | | | | ✅ | | | | | | |
| REQ-13 — Título no puede estar vacío | | | | | ✅ | | | | | |
| REQ-14 — DTO separado del modelo | | | | | ✅ | | | | | |
| REQ-15 — Filtro por estado `?status=` | | | | | | ✅ | | | | |
| REQ-16 — Persistencia en base de datos real | | | | | | | ✅ | ✅ | | |
| REQ-17 — Usuario creador del ticket | | | | | | | | | ✅ | |
| REQ-18 — Usuario asignado al ticket | | | | | | | | | ✅ | |
| REQ-19 — Historial de cambios de estado | | | | | | | | | | ✅ |

---

## Lo que el sistema NO cubre (por ahora)

Estas funcionalidades están fuera del alcance de las lecciones 04-13. No es que no existan — es que requieren conocimientos adicionales:

| Funcionalidad fuera de alcance | Razón |
|-------------------------------|-------|
| Autenticación y control de acceso | Requiere Spring Security |
| Notificaciones por correo al cambiar estado | Fuera del alcance de una API REST básica |
| Manejo global de errores (`@ControllerAdvice`) | Se introduce en lección 07; implementación formal en etapa posterior |
| Paginación (`Pageable`) | Requiere criterios de consulta avanzados con JPA |
| Migraciones de esquema (Flyway / Liquibase) | Herramientas de nivel producción fuera del alcance del curso |
| Quién realizó cada cambio de estado (auditoría completa) | Requiere autenticación (Spring Security) para saber quién es el usuario actual |

---

## La forma del dato

Un ticket tiene la siguiente estructura en el sistema:

| Campo | Tipo | ¿Quién lo asigna? | Descripción |
|-------|------|-------------------|-------------|
| `id` | Número entero | El servidor (automático) | Identificador único |
| `title` | Texto | El cliente (en el request) | Título corto de la solicitud |
| `description` | Texto | El cliente (en el request) | Descripción del problema |
| `status` | Texto | El servidor (automático) | Estado actual; comienza en `NEW` |
| `createdAt` | Fecha y hora | El servidor (automático) | Momento exacto de creación |
| `estimatedResolutionDate` | Fecha | El servidor (automático) | 5 días después de la creación |
| `effectiveResolutionDate` | Fecha y hora | El servidor (cuando se cierre) | Momento real de resolución |

> **Nota importante:** el cliente (quien llama a la API) solo necesita enviar `title` y `description`.
> Todo lo demás lo calcula o asigna el servidor. Eso no es un detalle técnico: es una regla de negocio.
> A partir de la lección 08, esta separación se formaliza con el DTO `TicketRequest`.
