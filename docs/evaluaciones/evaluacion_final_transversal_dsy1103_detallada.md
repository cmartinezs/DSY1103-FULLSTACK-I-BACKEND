# Evaluación Final Transversal DSY1103

**Curso:** Desarrollo Fullstack I Backend  
**Tipo de evaluación:** Encargo documental grupal e individual  
**Ponderación sugerida:** 40% entrega grupal + 60% evidencia individual  
**Tecnologías base:** Java 21, Spring Boot 4.0.5, Maven, JPA, Docker, GitHub  
**Proyecto evaluado:** versión final, completa y corregida del proyecto semestral desarrollado por cada equipo

---

## Índice

1. [Propósito](#1-propósito)
2. [Contexto del encargo](#2-contexto-del-encargo)
   - [Continuidad del proyecto semestral](#21-continuidad-del-proyecto-semestral)
   - [Alcance según el proyecto de cada equipo](#22-alcance-según-el-proyecto-de-cada-equipo)
   - [Levantamiento y actualización de requerimientos](#23-levantamiento-y-actualización-de-requerimientos)
3. [Modalidad](#3-modalidad)
4. [Entregables obligatorios](#4-entregables-obligatorios)
   - [Entrega técnica](#41-entrega-técnica)
   - [Documentación técnica y funcional](#42-documentación-técnica-y-funcional)
     - [Levantamiento de requerimientos actualizado](#426-levantamiento-de-requerimientos-actualizado)
   - [Presentación de defensa técnica grupal](#43-presentación-de-defensa-técnica-grupal)
   - [Documento de defensa técnica individual](#44-documento-de-defensa-técnica-individual)
   - [Entrega en AVA](#45-entrega-en-ava)
5. [Alcance técnico obligatorio](#5-alcance-técnico-obligatorio)
6. [Cobertura por lecciones](#6-cobertura-por-lecciones)
   - [Temario teórico obligatorio para la evidencia individual](#61-temario-teórico-obligatorio-para-la-evidencia-individual)
7. [Requisitos funcionales mínimos según el dominio del equipo](#7-requisitos-funcionales-mínimos-según-el-dominio-del-equipo)
8. [Reglas de calidad](#8-reglas-de-calidad)
9. [Formato de entrega documental](#9-formato-de-entrega-documental)
   - [Presentación de defensa técnica grupal](#91-presentación-de-defensa-técnica-grupal)
   - [Documento de defensa técnica individual](#92-documento-de-defensa-técnica-individual)
   - [Resumen de enlaces para AVA](#93-resumen-de-enlaces-para-ava)
10. [Rúbrica](#10-rúbrica)
11. [Condiciones de aprobación técnica](#11-condiciones-de-aprobación-técnica)
12. [Checklist final para estudiantes](#12-checklist-final-para-estudiantes)
13. [Preguntas guía para preparar la evidencia individual](#13-preguntas-guía-para-preparar-la-evidencia-individual)
14. [Criterio central](#14-criterio-central)

---

## 1. Propósito

Esta evaluación verifica que cada equipo sea capaz de entregar la versión final y profesional del proyecto trabajado durante el semestre, aplicando de forma integrada los contenidos teóricos y técnicos vistos en todas las unidades de DSY1103.

El proyecto no debe ser una maqueta ni una colección de endpoints aislados. Debe comportarse como un sistema backend real, con reglas de negocio claras, persistencia, validaciones, seguridad, documentación, pruebas, comunicación entre servicios, despliegue reproducible y evidencia de trabajo colaborativo.

Además, cada equipo debe cumplir los requerimientos que declaró para su propio proyecto durante el semestre. Si un equipo indicó que su sistema tendría determinadas entidades, flujos, reglas, roles, servicios, pantallas de prueba, integraciones o restricciones, esos elementos pasan a ser parte obligatoria de esta evaluación.

---

## 2. Contexto del encargo

Cada equipo deberá entregar la versión completa de su trabajo semestral. No se solicita comenzar un proyecto nuevo. Se evalúa la continuidad, mejora y cierre técnico del proyecto que cada equipo ha construido durante el curso. Esto significa:

- El sistema implementa el dominio elegido por el equipo de forma coherente.
- Los requerimientos funcionales declarados por el equipo están implementados.
- Los elementos incompletos de evaluaciones anteriores están terminados.
- Las observaciones y correcciones indicadas en el feedback de la última evaluación están incorporadas.
- Las reglas de negocio no están solo escritas en el README: funcionan en código.
- Las respuestas REST son consistentes, documentadas y verificables.
- La base de datos representa correctamente el dominio.
- Los microservicios se comunican cuando el flujo de negocio lo requiere.
- La aplicación se puede ejecutar, probar y respaldar con evidencia técnica en el repositorio.

### 2.1 Continuidad del proyecto semestral

Cada equipo debe tomar como punto de partida el mismo proyecto presentado en las evaluaciones previas. La entrega final debe evidenciar evolución real respecto de la última entrega.

El equipo debe completar:

- funcionalidades comprometidas que quedaron pendientes;
- endpoints incompletos o no implementados;
- reglas de negocio que estaban descritas pero no funcionaban;
- validaciones faltantes;
- errores detectados en pruebas REST;
- problemas de persistencia o relaciones JPA;
- inconsistencias entre README, Swagger y código;
- problemas de seguridad, roles o accesos;
- pruebas unitarias ausentes o superficiales;
- problemas de ejecución local, Docker, perfiles o variables de entorno;
- observaciones específicas entregadas por el docente en el feedback de la última evaluación.

Si el equipo decide cambiar, eliminar o reemplazar un requerimiento previamente declarado, debe justificarlo por escrito en la matriz de requerimientos. No basta con omitirlo del proyecto final.

### 2.2 Alcance según el proyecto de cada equipo

El dominio de negocio evaluado corresponde al proyecto desarrollado por cada equipo durante el semestre. Los ejemplos usados en clases sirven como referencia técnica, pero no definen el negocio de la evaluación final.

Cada equipo debe aplicar los mismos aprendizajes técnicos a su propio contexto, incluyendo:

- gestión completa de las entidades principales del dominio;
- usuarios, autenticación, autorización y roles;
- catálogos, clasificaciones o entidades de apoyo;
- relaciones entre entidades;
- historial, trazabilidad o auditoría de cambios;
- notificaciones o eventos relevantes;
- búsqueda o consultas filtradas;
- indicadores, fechas límite, estados o reglas equivalentes al dominio;
- seguridad;
- documentación;
- pruebas;
- Docker o ejecución reproducible;
- API Gateway y registro de servicios si fue trabajado en la sección final.

### 2.3 Levantamiento y actualización de requerimientos

Cada equipo debe actualizar el levantamiento de requerimientos entregado al inicio del curso, tomando como base el archivo [`docs/Levantamiento_Microservicios_DSY1103.pdf`](/Levantamiento_Microservicios_DSY1103.pdf).

Ese documento debe reflejar el estado final del proyecto y contrastarse con lo efectivamente codificado. Debe indicar qué requerimientos se mantuvieron, cuáles cambiaron, cuáles se reemplazaron, cuáles se eliminaron y cuáles se agregaron durante el semestre, siempre con justificación técnica o funcional.

El levantamiento actualizado debe incluir, como mínimo:

- requerimientos originales del equipo;
- cambios aplicados durante el semestre;
- mejoras incorporadas;
- requerimientos eliminados y su justificación;
- requerimientos reemplazados y su equivalencia o nuevo alcance;
- evidencia de trazabilidad hacia código, pruebas, endpoints o documentación;
- estado final de cada requerimiento.

No basta con reproducir el documento original. Esta actualización forma parte de la evidencia que se revisará junto con el código y el resto de la documentación del repositorio.
Debe entregarse como archivo Markdown dentro del repositorio, igual que el resto de la documentación.

---

## 3. Modalidad

### 3.1 Trabajo grupal

El desarrollo del sistema se realiza en equipos. La entrega grupal evalúa la calidad del repositorio, la arquitectura, el cumplimiento técnico y la evidencia objetiva del sistema funcionando.

### 3.2 Evidencia individual

La evidencia individual se entrega mediante un documento por estudiante dentro del repositorio. Cada documento debe dejar trazabilidad clara del aporte personal, las tareas realizadas, los commits asociados, los archivos modificados y las evidencias técnicas que respaldan su participación.

---

## 4. Entregables obligatorios

La entrega se separa en cuatro partes. Todo debe quedar dentro del repositorio del equipo.

### 4.1 Entrega técnica

Corresponde al código fuente y a todo lo necesario para ejecutar, probar y verificar el sistema.

Debe incluir:

- servicio principal del dominio;
- microservicios de apoyo;
- API Gateway, si corresponde al alcance trabajado;
- Eureka Server o mecanismo de discovery, si corresponde;
- archivos `pom.xml`;
- archivos `application.yml`;
- scripts SQL o migraciones Flyway;
- pruebas unitarias en `src/test/java`;
- `docker-compose.yml` cuando aplique;
- archivo `.env.example` sin credenciales reales;
- colección Postman, archivo `.http` o equivalente;
- configuración necesaria para despliegue remoto en Render cuando aplique.

La entrega técnica debe permitir que el docente pueda:

- clonar el repositorio;
- instalar dependencias;
- configurar variables de entorno;
- levantar base de datos y servicios desde terminal;
- ejecutar la aplicación sin abrir un IDE;
- ejecutar servicios con `./mvnw spring-boot:run`, `mvnw.cmd spring-boot:run` o comandos equivalentes documentados;
- ejecutar infraestructura con `docker compose up` cuando corresponda;
- ejecutar pruebas unitarias con `./mvnw test`, `mvnw.cmd test` o comando equivalente documentado;
- probar endpoints;
- verificar Swagger/OpenAPI;
- revisar logs;
- validar despliegue remoto en Render cuando corresponda.

### 4.2 Documentación técnica y funcional

Corresponde a los documentos que explican qué hace el sistema, cómo está construido, cómo se ejecuta y cómo se demuestra que cumple los requerimientos.

Debe incluir como mínimo los siguientes archivos.

#### 4.2.1 README principal

Debe existir un `README.md` en la raíz del repositorio con:

- nombre del proyecto;
- integrantes del equipo;
- descripción del problema;
- descripción de la solución;
- arquitectura general;
- estructura del repositorio;
- listado de microservicios;
- puertos usados;
- base de datos usada por cada servicio;
- variables de entorno necesarias;
- instrucciones para ejecutar localmente desde una copia limpia del repositorio;
- comandos exactos para ejecutar sin IDE;
- comandos `mvnw` o `mvnw.cmd` por servicio;
- comandos Docker Compose cuando aplique;
- orden de arranque de servicios;
- enlaces a Swagger UI;
- rutas principales del Gateway;
- usuarios de prueba y roles;
- comandos para correr pruebas;
- URL pública de cada servicio desplegado en Render cuando aplique;
- explicación de despliegue en Render por microservicio;
- enlace a tablero Trello, GitHub Projects u otra herramienta de gestión.

#### 4.2.2 Matriz de requerimientos

Debe incluirse un archivo `docs/matriz-requerimientos.md` con esta estructura:

| ID | Requerimiento declarado por el equipo | Tipo | Estado | Endpoint o evidencia | Prueba asociada |
|----|---------------------------------------|------|--------|----------------------|-----------------|
| RF-01 | Crear recurso principal con datos obligatorios del dominio | Funcional | Implementado | `POST /api/recursos` | `RecursoServiceTest.crearRecurso_ok` |
| RNF-01 | No exponer credenciales en GitHub | No funcional | Implementado | `.env.example` + variables | Revisión repositorio |

Todo requerimiento que aparezca como "implementado" debe estar respaldado por evidencia verificable en el repositorio.

#### 4.2.3 Plan de cierre y feedback

Debe incluirse un archivo `docs/plan-cierre-feedback.md` donde el equipo demuestre qué corrigió desde la última evaluación.

Estructura mínima:

| ID | Observación o feedback recibido | Acción realizada | Archivo(s) modificados | Evidencia de verificación | Estado |
|----|--------------------------------|------------------|------------------------|---------------------------|--------|
| FB-01 | Faltaba validar email único al crear usuario | Se agregó validación en service y prueba unitaria | `UserService.java`, `UserServiceTest.java` | Test `crearUsuario_emailDuplicado_lanzaExcepcion` | Corregido |
| FB-02 | Swagger no coincidía con respuesta real de creación | Se actualizó código y documentación para retornar `201 Created` | `RecursoController.java` | Postman `POST /api/recursos` retorna 201 | Corregido |

Este documento debe incluir:

- todo feedback recibido en la última evaluación;
- tareas pendientes que el propio equipo reconoció;
- correcciones aplicadas;
- evidencia concreta de que la corrección funciona;
- observaciones que no se corrigieron, con justificación técnica.

#### 4.2.4 Documentación funcional

Debe incluirse un archivo `docs/documentacion-funcional.md` que explique el sistema desde el punto de vista del usuario o negocio:

- problema que resuelve;
- actores o perfiles;
- requerimientos funcionales;
- flujos principales;
- reglas de negocio;
- estados relevantes;
- restricciones del dominio;
- ejemplos de uso;
- datos de prueba sugeridos.

#### 4.2.5 Documentación técnica

Debe incluirse un archivo `docs/documentacion-tecnica.md` que explique el sistema desde el punto de vista de desarrollo:

- arquitectura general;
- diagrama o descripción de microservicios;
- responsabilidades por servicio;
- modelo de datos;
- relaciones principales;
- perfiles de configuración;
- variables de entorno;
- seguridad;
- comunicación entre servicios;
- manejo de errores;
- logs;
- pruebas;
- despliegue local y remoto.

La documentación técnica debe incluir una sección llamada `Estructura del repositorio`. No es obligatorio que el repositorio tenga exactamente los mismos nombres, pero sí debe tener una organización equivalente y reconocible según el alcance del proyecto.

Ejemplo referencial:

```text
nombre-proyecto/
├── README.md
├── .gitignore
├── .env.example
├── docker-compose.yml
├── docs/
│   ├── documentacion-funcional.md
│   ├── documentacion-tecnica.md
│   ├── matriz-requerimientos.md
│   ├── plan-cierre-feedback.md
│   ├── presentacion-defensa-grupal.pdf
│   ├── pruebas-rest/
│   │   ├── coleccion-postman.json
│   │   └── casos-prueba.http
│   └── defensa-individual/
│       ├── apellido-nombre-1.md
│       ├── apellido-nombre-2.md
│       └── apellido-nombre-3.md
├── services/
│   ├── servicio-principal/
│   │   ├── pom.xml
│   │   ├── mvnw
│   │   ├── mvnw.cmd
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/
│   │       │   │   └── cl/.../
│   │       │   │       ├── controller/
│   │       │   │       ├── service/
│   │       │   │       ├── respository/
│   │       │   │       ├── model/
│   │       │   │       ├── dto/
│   │       │   │       ├── config/
│   │       │   │       ├── client/
│   │       │   │       └── exception/
│   │       │   └── resources/
│   │       │       ├── application.yml
│   │       │       └── db/migration/
│   │       │           ├── V1__create_schema.sql
│   │       │           └── V2__seed_data.sql
│   │       └── test/
│   │           └── java/
│   ├── servicio-notificaciones/
│   │   ├── pom.xml
│   │   ├── mvnw
│   │   ├── mvnw.cmd
│   │   └── src/
│   ├── servicio-auditoria/
│   │   ├── pom.xml
│   │   ├── mvnw
│   │   ├── mvnw.cmd
│   │   └── src/
│   └── servicio-busqueda-o-indicadores/
│       ├── pom.xml
│       ├── mvnw
│       ├── mvnw.cmd
│       └── src/
├── gateway/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
└── discovery-server/
    ├── pom.xml
    ├── mvnw
    ├── mvnw.cmd
    └── src/
```

Si el equipo usa otra estructura, debe explicar claramente dónde están:

- código fuente de cada servicio;
- documentación técnica y funcional;
- documentos individuales;
- pruebas REST;
- migraciones;
- configuración de perfiles;
- archivos Docker Compose;
- scripts o comandos de ejecución;
- Gateway y discovery, si aplican.

La documentación técnica debe incluir una sección obligatoria llamada `Ejecución desde cero`, pensada para que otra persona pueda clonar el repositorio y ejecutar el sistema sin usar IntelliJ IDEA, VS Code ni configuraciones guardadas en el computador del equipo.

Esa sección debe indicar:

- requisitos previos instalados;
- versión de Java requerida;
- cómo crear o configurar el archivo `.env`;
- cómo levantar bases de datos con Docker Compose;
- cómo ejecutar migraciones;
- orden exacto de arranque de servicios;
- comandos Linux/macOS con `./mvnw`;
- comandos Windows con `mvnw.cmd`;
- comandos `docker compose up`, `docker compose down` y `docker compose logs` cuando aplique;
- comandos para correr pruebas;
- URL de Swagger/OpenAPI por servicio;
- comandos o pasos para verificar que el sistema quedó funcionando.

#### 4.2.6 Levantamiento de requerimientos actualizado

Debe incluirse un archivo `docs/levantamiento-requerimientos-actualizado.md` que actualice el levantamiento entregado al inicio del curso. El archivo debe estar en formato Markdown y quedar versionado dentro del repositorio, al igual que los demás documentos de la entrega.

Este documento debe servir para contrastar lo declarado por el equipo con lo que efectivamente quedó implementado al cierre del semestre. Debe contener una tabla o estructura equivalente con:

| ID | Requerimiento original | Cambio realizado | Justificación | Estado final | Evidencia en repositorio |
|----|------------------------|------------------|---------------|--------------|--------------------------|
| RF-01 | Crear recurso principal | Se amplió el flujo de aprobación | Se detectó una necesidad nueva durante el semestre | Modificado | `RecursoController.java`, `RecursoServiceTest.java` |
| RF-02 | Notificación por email | Se eliminó | El alcance final ya no contempla correo externo | Eliminado | Registro en matriz y plan de cierre |

Este archivo debe mostrar con claridad:

- qué pidió el equipo al inicio del curso;
- qué cambió durante el semestre;
- qué se mantuvo;
- qué se eliminó;
- qué se agregó;
- por qué cada decisión fue tomada;
- cómo se verifica cada cambio en el código o en la documentación.

#### 4.2.7 Pruebas REST

Debe entregarse una colección Postman, archivo `.http` o documento equivalente con pruebas para:

- endpoints principales de cada microservicio;
- casos exitosos;
- datos inválidos;
- recursos inexistentes;
- permisos insuficientes;
- comunicación entre servicios;
- endpoints del Gateway.

#### 4.2.8 Evidencia de trabajo colaborativo

El repositorio debe evidenciar participación real mediante:

- commits técnicos y descriptivos;
- commits distribuidos entre integrantes;
- ramas o pull requests cuando corresponda;
- tablero de tareas actualizado;
- relación clara entre tareas, commits y funcionalidades.

No se consideran válidos commits como `cambios`, `arreglo`, `final`, `subida final`, `asdf` o similares.

### 4.3 Presentación de defensa técnica grupal

Corresponde al material grupal que explica el proyecto de forma ordenada.

Debe entregarse en `docs/presentacion-defensa-grupal.pdf`, `docs/presentacion-defensa-grupal.pptx` o `docs/presentacion-defensa-grupal.md`.

La presentación debe incluir:

- nombre del proyecto e integrantes;
- problema abordado;
- solución propuesta;
- alcance final del sistema;
- principales requerimientos cumplidos;
- feedback de la última evaluación y correcciones aplicadas;
- arquitectura general;
- microservicios implementados y responsabilidad de cada uno;
- modelo de datos resumido;
- flujo funcional principal;
- flujo técnico principal entre capas y servicios;
- seguridad y roles;
- pruebas realizadas;
- Swagger/OpenAPI;
- despliegue local y remoto, incluyendo URL de cada servicio en Render cuando aplique;
- principales dificultades técnicas y cómo se resolvieron;
- distribución de trabajo del equipo.

### 4.4 Documento de defensa técnica individual

Cada estudiante debe entregar un documento propio. No es grupal.

Debe ubicarse en:

```text
docs/defensa-individual/
  apellido-nombre.md
```

Cada documento individual debe incluir:

- nombre del estudiante;
- rol dentro del equipo;
- funcionalidades o módulos en los que participó;
- commits propios más relevantes, con enlace o hash;
- tareas del tablero asociadas a su trabajo;
- feedback o pendiente que corrigió personalmente;
- archivos principales que modificó;
- endpoints o flujos asociados a su aporte;
- pruebas unitarias o REST asociadas a su aporte;
- explicación breve de una regla de negocio que domina;
- explicación breve de una relación de base de datos que domina;
- explicación breve de una comunicación entre servicios que domina, si aplica;
- dificultad técnica personal y cómo la resolvió;
- checklist personal de evidencia entregada.

### 4.5 Entrega en AVA

En AVA solo se deben dejar los enlaces correspondientes. Los archivos y evidencias deben estar dentro del repositorio.

Cada equipo debe registrar en AVA:

- link del repositorio del proyecto;
- link de la presentación de defensa técnica grupal dentro del repositorio;
- link del documento de defensa técnica individual de cada integrante dentro del repositorio.

---

## 5. Alcance técnico obligatorio

### 5.1 Arquitectura por capas

Cada servicio de negocio debe respetar la separación:

```text
controller -> service -> respository -> model / dto
```

En este curso, el paquete `respository` se mantiene con ese nombre cuando el proyecto base lo use así. No debe corregirse a `repository` si eso rompe la convención del proyecto trabajado.

La capa controller solo recibe solicitudes y retorna respuestas. No debe contener reglas de negocio ni acceso directo a datos.

La capa service contiene la lógica de negocio, validaciones de flujo, coordinación entre repositorios y llamadas a otros servicios.

La capa respository abstrae el acceso a datos mediante `JpaRepository` o repositorios equivalentes.

La capa model representa entidades persistentes. La capa dto representa datos de entrada y salida.

### 5.2 APIs REST

Todos los endpoints deben aplicar principios REST:

- rutas semánticas;
- nombres en plural para recursos principales;
- uso correcto de `GET`, `POST`, `PUT`, `PATCH` y `DELETE`;
- `ResponseEntity` para controlar código y cuerpo de respuesta;
- JSON como formato de entrada y salida;
- códigos HTTP coherentes;
- mensajes de error claros.

Ejemplos mínimos esperados:

| Caso | Código esperado |
|------|-----------------|
| Crear recurso correctamente | `201 Created` |
| Consultar recurso existente | `200 OK` |
| Actualizar recurso existente | `200 OK` o `204 No Content` |
| Eliminar o desactivar recurso | `204 No Content` o `200 OK` |
| Validación fallida | `400 Bad Request` |
| Credenciales ausentes o inválidas | `401 Unauthorized` |
| Rol sin permiso | `403 Forbidden` |
| Recurso inexistente | `404 Not Found` |
| Conflicto de negocio | `409 Conflict` |
| Error no esperado | `500 Internal Server Error` |

Si el proyecto define un context path o prefijo base, todas las pruebas, documentación y colecciones REST deben respetarlo. No se evaluará como correcto probar una ruta distinta a la configurada en el servicio.

### 5.3 DTOs y mapeo

El proyecto debe separar entidades JPA de los datos expuestos por la API.

Debe existir, según corresponda:

- DTO de creación;
- DTO de actualización;
- DTO de respuesta;
- DTO para comunicación entre microservicios;
- mapper manual o componente dedicado de conversión.

No se deben exponer contraseñas, hashes, atributos internos o datos sensibles en DTOs de respuesta.

### 5.4 Validaciones

El proyecto debe usar Bean Validation en DTOs de entrada:

- `@NotNull`;
- `@NotBlank`;
- `@Size`;
- `@Email`;
- `@Min`;
- `@Max`;
- `@Positive`;
- validaciones personalizadas cuando el dominio lo requiera.

Las validaciones deben tener efecto real en la API. No basta con poner anotaciones si el controller no usa `@Valid`.

También deben existir validaciones de negocio en la capa service. Por ejemplo:

- un usuario inactivo no puede crear solicitudes;
- un actor responsable no puede asignarse a sí mismo un caso si la regla lo prohíbe;
- un recurso en estado final no puede volver a estado inicial sin autorización;
- no se puede eliminar una categoría usada por registros activos;
- no se puede crear un recurso duplicado cuando el dominio exige unicidad.

### 5.5 Manejo global de errores

Debe existir manejo centralizado de excepciones con `@ControllerAdvice`.

Las respuestas de error deben ser uniformes. Ejemplo esperado:

```json
{
  "timestamp": "2026-07-06T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Recurso no encontrado con id 15",
  "path": "/api/recursos/15"
}
```

Para errores de validación, la respuesta debe indicar el campo afectado:

```json
{
  "timestamp": "2026-07-06T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "La solicitud contiene datos inválidos",
  "fields": {
    "title": "El título es obligatorio",
    "email": "El correo no tiene formato válido"
  }
}
```

### 5.6 Persistencia y base de datos

El proyecto debe usar persistencia real con JPA e Hibernate.

Debe incluir:

- entidades anotadas con `@Entity`;
- claves primarias con `@Id` y `@GeneratedValue`;
- repositorios con `JpaRepository`;
- relaciones JPA cuando correspondan;
- configuración de datasource;
- perfiles de ejecución;
- migraciones SQL o Flyway;
- datos iniciales de prueba cuando sea necesario.

Relaciones mínimas esperadas en un proyecto de complejidad semestral:

- `@ManyToOne`;
- `@OneToMany`;
- `@OneToOne` cuando el dominio lo justifique;
- entidades intermedias cuando exista una relación asociativa que requiera datos propios;
- claves foráneas reales en la base de datos;
- integridad referencial.

El equipo debe ser capaz de explicar:

- por qué una relación es `OneToMany`, `ManyToOne` o `OneToOne`;
- cuándo una relación asociativa debe modelarse con una entidad intermedia;
- cuándo usa carga lazy o eager;
- qué operaciones se propagan con cascade;
- qué ocurre si se elimina un registro relacionado;
- qué tablas crea cada migración.

### 5.7 Configuración por perfiles y variables de entorno

El proyecto debe separar configuración de código.

Se espera:

- `application.yml`;
- perfiles como `dev`, `test`, `prod` o equivalentes;
- variables de entorno para credenciales;
- archivo `.env.example` sin secretos reales;
- `.gitignore` protegiendo `.env`, claves y archivos sensibles.

No se aceptan credenciales reales hardcodeadas en el repositorio.

### 5.8 Migraciones

Cuando el servicio use base de datos relacional persistente, debe incluir migraciones versionadas.

Ejemplo:

```text
src/main/resources/db/migration/
  V1__create_initial_schema.sql
  V2__insert_seed_data.sql
  V3__add_domain_history.sql
```

Las migraciones deben poder ejecutarse desde cero en una base limpia.

El estudiante debe poder explicar:

- diferencia entre `ddl-auto` y Flyway;
- por qué en producción no se recomienda depender de `ddl-auto=create`;
- qué tabla usa Flyway para controlar migraciones;
- cómo se corrige una migración fallida.

### 5.9 Microservicios y comunicación distribuida

El proyecto debe tener separación real de responsabilidades entre servicios.

El mínimo esperado debe ajustarse al dominio de cada equipo. Una arquitectura válida puede incluir servicios como:

| Tipo de servicio | Responsabilidad esperada |
|------------------|--------------------------|
| Servicio principal del dominio | Gestionar las entidades centrales, reglas de negocio y flujos principales |
| Servicio de usuarios o identidad | Gestionar usuarios, credenciales, roles o perfiles |
| Servicio de notificaciones | Registrar o simular avisos relevantes del sistema |
| Servicio de auditoría | Registrar eventos importantes y trazabilidad |
| Servicio de búsqueda | Exponer búsquedas, filtros o indexación de datos |
| Servicio de indicadores, plazos o reglas complementarias | Calcular métricas, vencimientos, estados derivados o reglas de apoyo |
| Eureka Server o discovery equivalente | Registrar servicios, si se incluye discovery |
| API Gateway | Centralizar rutas y entrada al ecosistema, si se incluye Gateway |
| Base de datos o servicios de infraestructura | Soportar persistencia y ejecución reproducible |

Si un equipo declaró más servicios en su propio proyecto, debe implementarlos o justificar formalmente el cambio de alcance en la matriz de requerimientos.

La comunicación entre microservicios debe usar:

- `RestClient`, Feign Client, `WebClient` o `RestTemplate`;
- DTOs específicos para datos remotos;
- manejo de errores remotos;
- timeouts cuando corresponda;
- logs que permitan seguir el flujo.

No se considera comunicación entre microservicios si el equipo solo copia datos manualmente o simula respuestas sin llamada HTTP real.

### 5.10 API Gateway y Service Discovery

Si la evaluación incluye la última etapa de infraestructura, el sistema debe tener:

- Eureka Server levantando correctamente;
- microservicios registrados con nombre lógico;
- Gateway con rutas configuradas;
- rutas usando `lb://NOMBRE-SERVICIO` cuando aplique discovery;
- filtros básicos de trazabilidad, por ejemplo `X-Request-Id`;
- pruebas de endpoints a través del Gateway.

El estudiante debe poder explicar:

- qué problema resuelve un Gateway;
- por qué el cliente no debería conocer todos los puertos internos;
- qué problema resuelve Eureka;
- qué diferencia hay entre llamar por host/puerto directo y por nombre lógico;
- cómo se depura una ruta que no llega al servicio destino.

### 5.11 Seguridad

El sistema debe implementar autenticación y autorización.

Como mínimo:

- usuarios persistidos o sembrados en base de datos;
- contraseñas protegidas con encoder, por ejemplo BCrypt;
- roles del dominio;
- reglas por endpoint;
- respuestas correctas para `401` y `403`;
- datos sensibles ocultos en respuestas.

Los roles deben tener sentido dentro del dominio del equipo. Como mínimo, debe existir más de un perfil de acceso cuando el sistema declara acciones diferenciadas.

Ejemplos de reglas esperadas:

- solo un rol administrador puede crear o desactivar usuarios;
- un usuario autenticado puede crear registros propios del dominio;
- un rol responsable puede ver y modificar registros asignados;
- un rol supervisor puede consultar auditoría o reportes;
- un usuario no autenticado no puede acceder a endpoints protegidos.

### 5.12 Logging

Cada servicio debe usar SLF4J con Logback o configuración equivalente.

Debe haber logs para:

- inicio del servicio;
- creación de recursos importantes;
- actualización de estado;
- errores controlados;
- validaciones de negocio fallidas;
- llamadas remotas;
- respuestas fallidas desde otros servicios.

No se aceptan `System.out.println` como mecanismo principal de trazabilidad.

### 5.13 OpenAPI y Swagger

Cada servicio principal debe exponer documentación Swagger/OpenAPI.

La documentación debe incluir:

- descripción general del servicio;
- endpoints principales;
- parámetros;
- request bodies;
- response bodies;
- códigos HTTP;
- ejemplos JSON;
- errores esperados;
- DTOs relevantes.

La documentación debe coincidir con el comportamiento real. Si Swagger indica que un endpoint retorna `200` pero la API retorna `201`, se considera inconsistencia.

### 5.14 HATEOAS

Cuando el proyecto incluya HATEOAS, las respuestas principales deben entregar enlaces útiles.

Ejemplo:

```json
{
  "id": 10,
  "name": "Registro principal de ejemplo",
  "status": "CREATED",
  "_links": {
    "self": { "href": "http://localhost:8080/api/recursos/10" },
    "history": { "href": "http://localhost:8080/api/recursos/10/history" },
    "actions": { "href": "http://localhost:8080/api/recursos/10/actions" }
  }
}
```

El estudiante debe explicar qué gana el cliente al recibir enlaces navegables.

### 5.15 Pruebas unitarias

El proyecto debe incluir pruebas unitarias con JUnit 5 y Mockito.

Debe evaluarse principalmente la capa service:

- reglas de negocio;
- validaciones;
- excepciones esperadas;
- llamadas a repositorios;
- llamadas a clientes remotos mockeadas;
- transformación de DTOs;
- casos borde.

Las pruebas deben seguir una estructura clara, por ejemplo Given, When, Then.

Cobertura mínima esperada: 80% de la lógica de negocio relevante. Si no se usa herramienta de cobertura, el equipo debe respaldar la cobertura mediante una tabla de casos de prueba.

### 5.16 Docker y ejecución reproducible

El proyecto debe poder ejecutarse localmente desde una copia limpia del repositorio. La ejecución no debe depender del IDE, de configuraciones personales, de variables guardadas solo en el computador de un integrante ni de pasos no documentados.

Se espera uno de los siguientes mecanismos:

- ejecución documentada con Maven por servicio;
- `docker-compose.yml` para bases de datos;
- `docker-compose.yml` para servicios y bases de datos;
- despliegue remoto en Render si fue parte del alcance.

Cuando exista despliegue en Render, la documentación debe explicar la estrategia usada. Cada microservicio debe desplegarse como un servicio independiente de Render. No corresponde indicar que se sube un único `docker-compose.yml` con todos los microservicios dentro de un solo servicio Render.

Para Render se debe documentar, como mínimo:

- nombre de cada servicio creado en Render;
- repositorio, rama y directorio raíz usado por cada servicio;
- comando de build de cada microservicio;
- comando de start de cada microservicio;
- variables de entorno configuradas por servicio;
- URL pública de cada microservicio;
- base de datos remota usada, si corresponde;
- relación entre Gateway, discovery y microservicios desplegados;
- limitaciones conocidas del despliegue remoto.

El README y la documentación técnica deben indicar:

- requisitos previos;
- variables necesarias;
- orden de arranque;
- puertos;
- comandos de ejecución con `./mvnw` para Linux/macOS;
- comandos de ejecución con `mvnw.cmd` para Windows;
- comandos de prueba con `./mvnw test` y/o `mvnw.cmd test`;
- comandos Docker Compose cuando aplique;
- cómo verificar que todo está arriba.
- URL pública de cada servicio desplegado en Render cuando exista despliegue remoto.

Ejemplo de nivel de detalle esperado:

```bash
# 1. Clonar repositorio
git clone <url-del-repositorio>
cd <carpeta-del-proyecto>

# 2. Crear archivo de entorno
cp .env.example .env

# 3. Levantar base de datos o infraestructura
docker compose up -d

# 4. Ejecutar un servicio en Linux/macOS
cd services/servicio-principal
./mvnw spring-boot:run

# 5. Ejecutar pruebas
./mvnw test
```

```bat
REM Windows
cd services\servicio-principal
mvnw.cmd spring-boot:run
mvnw.cmd test
```

---

## 6. Cobertura por lecciones

La evaluación es transversal. El proyecto debe evidenciar aprendizajes de todo el curso.

| Lección | Concepto evaluado | Evidencia esperada |
|---------|-------------------|--------------------|
| 00 | Git y GitHub | Repositorio ordenado, commits técnicos, README, trazabilidad |
| 01 | Web y HTTP | Comprensión de request, response, headers, status codes |
| 02 | APIs y REST | Rutas semánticas, recursos, verbos HTTP, JSON |
| 03 | Primera API | Controllers funcionales, endpoints ejecutables |
| 04 | Responsabilidades backend | Separación de capas y responsabilidades |
| 05 | POST y validación | Creación de recursos con DTOs y Bean Validation |
| 06 | CRUD completo | Listar, obtener, crear, actualizar, eliminar/desactivar |
| 07 | Manejo de errores | Errores controlados, respuestas correctas |
| 08 | DTO y mapeo | No exponer entidades directamente cuando no corresponde |
| 09 | Repositorio customizado | Búsquedas, filtros y operaciones de repositorio |
| 10 | JPA | Entidades, repositorios, persistencia real |
| 11 | Configuración de BD | Perfiles, variables de entorno, datasource |
| 12 | Relaciones JPA | Relaciones coherentes y explicables |
| 13 | Historial y auditoría | Registro de cambios y trazabilidad |
| 14 | Microservicios | Servicios separados y comunicación HTTP |
| 15 | Flyway | Migraciones versionadas y reproducibles |
| 16 | Spring Security | Autenticación, autorización y roles |
| 17 | Logging | Logs útiles por capa y por evento |
| 18 | Exception Handling Global | `@ControllerAdvice`, errores uniformes |
| 19 | OpenAPI/OAS | Swagger consistente con la API real |
| 20 | Docker/Compose | Ejecución reproducible de BD y servicios |
| 21 | HATEOAS | Links navegables cuando aplique |
| 22 | Testing | JUnit, Mockito, asserts, casos de negocio |
| 23 | Gateway y Eureka | Punto de entrada único y service discovery, si fue trabajado |

---

### 6.1 Temario teórico obligatorio para la evidencia individual

Cada estudiante debe manejar los conceptos técnicos detrás del código. No basta con decir "lo hizo Spring" o "así estaba en el ejemplo".

| Área | Conceptos que debe poder explicar |
|------|----------------------------------|
| Web y HTTP | cliente, servidor, request, response, headers, body, query params, path variables, códigos 2xx/3xx/4xx/5xx |
| REST | recurso, representación JSON, rutas semánticas, verbos HTTP, idempotencia, diferencia entre `PUT` y `PATCH` |
| Arquitectura backend | responsabilidad de controller, service, respository, model y dto; por qué no mezclar capas |
| Java y Spring Boot | inyección de dependencias, beans, anotaciones, Maven, ciclo de arranque de una aplicación Spring |
| DTOs | diferencia entre entidad y DTO, DTO de entrada, DTO de salida, DTO remoto, protección de datos sensibles |
| Validación | Bean Validation, `@Valid`, validación de formato versus validación de negocio |
| JPA y ORM | entidad, tabla, repositorio, `JpaRepository`, clave primaria, clave foránea, lazy/eager, cascade |
| Base de datos | normalización básica, relaciones, integridad referencial, datos semilla, perfiles de conexión |
| Migraciones | diferencia entre `ddl-auto` y Flyway, versionado SQL, tabla `flyway_schema_history`, migraciones fallidas |
| Microservicios | independencia de despliegue, responsabilidad única, comunicación síncrona HTTP, tolerancia a fallas |
| Comunicación remota | `RestClient`, Feign Client, DTOs remotos, timeouts, fallback, errores 4xx/5xx entre servicios |
| Seguridad | autenticación versus autorización, roles, BCrypt, endpoint público versus protegido, `401` versus `403` |
| Logs | niveles `DEBUG`, `INFO`, `WARN`, `ERROR`, trazabilidad, uso de logs para depurar |
| Excepciones | excepción controlada, excepción no esperada, `@ControllerAdvice`, formato uniforme de error |
| OpenAPI | contrato de API, Swagger UI, documentación de request, response, parámetros y errores |
| Docker | imagen, contenedor, puerto, volumen, variable de entorno, `docker-compose.yml` |
| HATEOAS | enlaces navegables, `_links`, relación entre estado del recurso y acciones disponibles |
| Testing | prueba unitaria, mock, assert, Given-When-Then, caso exitoso, caso de error, cobertura lógica |
| Gateway y Eureka | punto de entrada único, service discovery, nombre lógico, `lb://`, filtros y trazabilidad |

---

## 7. Requisitos funcionales mínimos según el dominio del equipo

Cada equipo debe cumplir los requerimientos funcionales que definió para su propio proyecto. Los siguientes puntos no imponen un negocio específico; describen capacidades mínimas que deben existir adaptadas al contexto desarrollado.

### 7.1 Entidad principal del dominio

- Crear registros principales con los datos obligatorios definidos por el equipo.
- Asignar estado inicial cuando el flujo lo requiera.
- Listar registros.
- Filtrar por estado, tipo, categoría, usuario, responsable, fecha u otro criterio relevante.
- Obtener un registro por identificador.
- Actualizar datos permitidos según reglas del dominio.
- Cambiar estado siguiendo transiciones válidas.
- Cerrar, finalizar, cancelar, aprobar, rechazar o completar el registro según el flujo definido.
- Eliminar, desactivar o archivar registros según la regla definida.

### 7.2 Usuarios, roles y perfiles

- Crear usuarios o perfiles de acceso cuando el sistema lo requiera.
- Listar usuarios.
- Buscar usuario por ID u otro identificador.
- Actualizar datos básicos.
- Desactivar usuarios sin perder trazabilidad histórica.
- Asignar roles coherentes con el dominio.
- Validar unicidad de email, username u otro identificador.
- Proteger contraseñas o credenciales.

### 7.3 Catálogos, clasificaciones y entidades de apoyo

- Crear entidades de apoyo necesarias para el negocio.
- Listar entidades de apoyo.
- Asociarlas correctamente con la entidad principal.
- Evitar duplicados cuando corresponda.
- Validar que no se eliminen registros usados por información activa, salvo que el dominio lo permita.

### 7.4 Asignaciones, responsables o flujos de trabajo

- Asignar registros a usuarios, responsables, áreas, categorías o estados según corresponda.
- Validar que el responsable exista, esté activo y tenga rol correcto.
- Validar restricciones del dominio, como no autoasignación, cupos, disponibilidad, propiedad o permisos.
- Consultar registros asignados o relacionados con un usuario.
- Registrar historial y auditoría al realizar cambios importantes.
- Enviar notificaciones o eventos cuando el flujo lo requiera.

### 7.5 Historial y trazabilidad

- Registrar cambios de estado o cambios críticos.
- Registrar fecha de cambio.
- Registrar usuario o proceso que realiza el cambio si el sistema lo soporta.
- Consultar historial de un registro.
- No permitir manipulación manual del historial desde endpoints públicos.

### 7.6 Notificaciones o eventos

- Crear notificación o evento al crear un registro importante.
- Crear notificación o evento al asignar, modificar estado, aprobar, rechazar, completar o cancelar.
- Registrar destinatario, mensaje, tipo y fecha cuando corresponda.
- Manejar fallas del servicio de notificaciones sin romper todo el flujo si la regla del sistema así lo define.

### 7.7 Auditoría

- Registrar evento al crear, actualizar, asignar, cambiar estado, finalizar o eliminar/desactivar.
- Consultar auditoría por entidad o identificador.
- Incluir acción, entidad, identificador, fecha y descripción.

### 7.8 Búsqueda y consultas

- Buscar registros por texto o criterios relevantes del dominio.
- Indexar o consultar campos significativos.
- Permitir búsqueda por estado, categoría, usuario, fecha, tipo u otro filtro definido.
- Manejar resultados vacíos de forma correcta.

### 7.9 Indicadores, plazos o reglas derivadas

- Calcular fechas, vencimientos, totales, estados derivados, prioridades, stock, disponibilidad, puntajes u otro indicador propio del dominio.
- Determinar si un registro cumple o incumple una condición relevante.
- Exponer endpoint o flujo que permita consultar ese cálculo.
- Registrar o informar casos vencidos, críticos, completados, rechazados, agotados o equivalentes según el negocio.

---

## 8. Reglas de calidad

El proyecto será penalizado si presenta:

- código que no compila;
- servicios que no levantan;
- endpoints documentados pero inexistentes;
- rutas que funcionan solo en el computador de un integrante;
- credenciales reales en GitHub;
- entidades JPA expuestas con datos sensibles;
- lógica de negocio dentro del controller;
- repositorios llamados directamente desde controller;
- errores sin manejo;
- respuestas HTML de error por defecto;
- `System.out.println` como logging principal;
- pruebas unitarias que no prueban reglas reales;
- commits sin significado técnico;
- README incompleto;
- copia de código sin evidencia de autoría o participación.

---

## 9. Formato de entrega documental

La evidencia documental tiene dos niveles: una presentación grupal del sistema y un documento individual por estudiante. Todo debe quedar disponible dentro del repositorio.

### 9.1 Presentación de defensa técnica grupal

El equipo debe entregar una presentación grupal que explique de forma ordenada:

- problema y contexto del proyecto;
- alcance final;
- requerimientos principales;
- correcciones realizadas a partir del feedback;
- arquitectura;
- microservicios;
- modelo de datos;
- flujo funcional principal;
- seguridad;
- pruebas;
- documentación Swagger/OpenAPI;
- despliegue local y remoto;
- distribución de responsabilidades.

### 9.2 Documento de defensa técnica individual

Cada estudiante debe entregar su documento individual en `docs/defensa-individual/`.

El documento individual debe evidenciar:

- qué aportó el estudiante;
- qué archivos trabajó;
- qué requerimientos o feedback corrigió;
- qué commits respaldan su participación;
- qué partes del sistema desarrolló o corrigió;
- qué pruebas unitarias o REST respaldan su trabajo;
- qué evidencia del repositorio demuestra su participación.

El documento debe coincidir con el repositorio, los commits, el tablero de trabajo y los archivos efectivamente modificados.

### 9.3 Resumen de enlaces para AVA

En AVA se deben registrar solamente enlaces:

- enlace al repositorio;
- enlace a la presentación de defensa técnica grupal;
- enlace al documento de defensa técnica individual de cada integrante.

---

## 10. Rúbrica

### 10.0 Escala de indicadores de desempeño

Cada criterio tiene un puntaje máximo. El docente debe asignar a cada criterio un indicador de desempeño y calcular el puntaje obtenido según el porcentaje correspondiente.

| Indicador de desempeño | Valor del indicador | Cálculo sobre el puntaje del criterio |
|------------------------|---------------------|----------------------------------------|
| Logrado completamente | 100% | Puntaje máximo x 1.00 |
| Logrado con observaciones menores | 80% | Puntaje máximo x 0.80 |
| Logrado parcialmente | 60% | Puntaje máximo x 0.60 |
| Logrado de forma insuficiente | 30% | Puntaje máximo x 0.30 |
| No logrado o sin evidencia | 0% | Puntaje máximo x 0.00 |

Ejemplo: si un criterio vale 4 puntos y el desempeño observado corresponde a 80%, el puntaje asignado para ese criterio es `4 x 0.80 = 3.2 puntos`.

### 10.1 Entrega grupal: 40 puntos

| Criterio | Puntos | 100% | 80% | 60% | 30% | 0% |
|----------|--------|------|-----|-----|-----|----|
| Cumplimiento de requerimientos declarados | 4 | Todos los requerimientos propios están implementados y trazados con evidencia | Requerimientos principales implementados, con una omisión menor o evidencia incompleta | Cumple parcialmente el alcance, pero faltan requerimientos relevantes | Implementación mínima, desordenada o con trazabilidad débil | No cumple el alcance declarado o no hay evidencia |
| Corrección de pendientes y feedback previo | 3 | Corrige observaciones de la última evaluación, completa pendientes y entrega evidencia verificable | Corrige la mayoría de observaciones, con detalles menores pendientes | Corrige parte del feedback, pero deja observaciones importantes sin resolver | Corrige poco o no demuestra claramente los cambios | Ignora el feedback o no completa pendientes |
| Arquitectura y separación de capas | 4 | CSR aplicado en todos los servicios, responsabilidades claras | Arquitectura correcta con inconsistencias menores | Capas reconocibles, pero con mezcla ocasional de responsabilidades | Mezcla frecuente de lógica entre controller, service y repository | No aplica arquitectura por capas |
| REST, DTOs y respuestas HTTP | 4 | Endpoints coherentes, DTOs correctos, códigos HTTP precisos | API funcional con errores menores de consistencia | API parcialmente consistente, con rutas, cuerpos o códigos mejorables | Rutas, cuerpos o códigos presentan errores frecuentes | API incompleta o inconsistente |
| Persistencia, JPA y relaciones | 4 | Modelo relacional correcto, CRUD estable, relaciones justificables | Persistencia funcional con detalles menores en relaciones o consultas | Persistencia parcial o relaciones poco justificadas | Relaciones incorrectas, migraciones incompletas o CRUD inestable | No hay persistencia real funcional |
| Validaciones y reglas de negocio | 3 | Validaciones de entrada y reglas de service completas | Reglas principales cubiertas con omisiones menores | Reglas parcialmente implementadas o con casos borde sin cubrir | Validaciones incompletas o reglas débiles | No valida o no aplica reglas |
| Manejo global de errores y logs | 3 | Errores uniformes, códigos correctos y logs útiles | Manejo funcional con detalles menores | Manejo parcial, con respuestas inconsistentes en algunos casos | Errores inconsistentes o logs pobres | Errores sin control |
| Microservicios y comunicación | 4 | Servicios separados, comunicación real, DTOs remotos y manejo de fallas | Comunicación funcional con detalles menores | Comunicación parcial entre servicios o con errores no controlados | Comunicación frágil, simulada o poco trazable | No existe comunicación real |
| Seguridad | 3 | Autenticación, roles y protección por endpoint correctas | Seguridad funcional con omisiones menores | Seguridad parcial o con permisos poco precisos | Seguridad incompleta o fácil de evadir | No hay seguridad funcional |
| Documentación OpenAPI, README y ejecución | 3 | Swagger y README completos, con comandos claros para ejecutar desde cero sin IDE | Documentación útil, con detalles menores incompletos | Documentación parcial o con pasos poco claros | Documentación incompleta, desactualizada o dependiente del IDE | No documenta |
| Pruebas unitarias | 3 | Pruebas relevantes sobre reglas, mocks y casos borde | Pruebas principales cubiertas con omisiones menores | Pruebas parciales, enfocadas en pocos casos | Pruebas superficiales, frágiles o sin relación clara | No hay pruebas funcionales |
| Docker, perfiles y configuración | 1 | Ejecución reproducible con `mvnw` y/o Docker Compose, perfiles y variables claras | Ejecuta con ajustes menores documentados | Ejecuta parcialmente o requiere correcciones simples | Configuración confusa o comandos incompletos | No se puede ejecutar |
| GitHub y trabajo colaborativo | 1 | Commits claros, distribuidos y trazables | Evidencia suficiente con detalles menores | Participación visible pero poco ordenada | Evidencia débil, concentrada o desordenada | Sin evidencia real |

### 10.2 Evidencia individual: 60 puntos

| Criterio | Puntos | 100% | 80% | 60% | 30% | 0% |
|----------|--------|------|-----|-----|-----|----|
| Dominio general del proyecto | 6 | Documenta problema, arquitectura, servicios y flujos con claridad y evidencia | Documenta el proyecto con vacíos menores | Explica parcialmente el sistema, pero omite componentes importantes | Documentación superficial o dependiente del trabajo de otros | No evidencia comprensión del proyecto |
| Requerimientos, pendientes y feedback | 5 | Relaciona requerimientos declarados, pendientes corregidos y feedback con código, endpoints, commits y pruebas | Relaciona la mayoría con vacíos menores | Relaciona parcialmente requerimientos y evidencia | No conecta bien requerimientos, feedback y evidencia | No evidencia requerimientos ni correcciones realizadas |
| Modelado de datos y JPA | 6 | Justifica entidades, relaciones, claves y migraciones con evidencia del código | Documenta el modelo con dudas menores | Explica parcialmente el modelo y sus relaciones | Confunde relaciones, persistencia o migraciones | No evidencia comprensión de la base de datos |
| REST, DTOs y errores | 5 | Documenta rutas, DTOs, códigos y errores uniformes con ejemplos verificables | Documenta la API con detalles menores | Documenta parcialmente rutas, DTOs o errores | Confunde códigos, responsabilidades o contratos | No evidencia comprensión de la API |
| Reglas de negocio y validaciones | 7 | Documenta reglas y validaciones implementadas, indicando archivos, pruebas y commits | Documenta reglas principales con evidencia menor faltante | Documenta reglas parcialmente o sin toda la evidencia | Evidencia incompleta o poco clara | No evidencia reglas implementadas |
| Pruebas REST | 5 | Incluye endpoints, respuestas esperadas, casos exitosos y casos de error verificables | Incluye casos principales con omisiones menores | Incluye evidencia REST parcial | Evidencia REST incompleta o difícil de verificar | No incluye evidencia REST útil |
| Comunicación entre microservicios | 5 | Documenta flujo remoto, DTOs, errores y logs con archivos y pruebas asociadas | Documenta el flujo general con detalles menores | Documenta parcialmente servicios y llamadas remotas | Confunde servicios, rutas o responsabilidades | No evidencia comunicación entre servicios |
| Seguridad | 4 | Documenta autenticación, roles y protección de endpoints con evidencia verificable | Documenta seguridad principal con omisiones menores | Documenta seguridad parcialmente | Tiene inconsistencias importantes o evidencia débil | No evidencia seguridad funcional |
| Testing | 8 | Incluye pruebas, mocks, asserts y cobertura lógica asociada a su trabajo | Incluye pruebas principales con omisiones menores | Incluye pruebas parciales o poco profundas | Pruebas superficiales o sin relación clara con su aporte | No evidencia pruebas funcionales |
| Logs, errores y debugging | 4 | Incluye evidencia de logs, errores controlados y respuestas uniformes | Evidencia principal con detalles menores | Evidencia parcial de errores y logs | Evidencia débil o incompleta | No evidencia manejo de errores |
| Swagger, Docker, perfiles o Gateway | 5 | Documenta configuración, ejecución por terminal, comandos `mvnw`/Docker Compose y enlaces verificables según el alcance | Evidencia funcional con detalles menores | Evidencia parcial de infraestructura o ejecución | Evidencia incompleta o dependiente del IDE | No evidencia configuración ni ejecución |

---

## 11. Condiciones de aprobación técnica

Para que el proyecto sea considerado evaluable, debe cumplir estas condiciones mínimas:

- el repositorio existe y es accesible;
- el código compila;
- al menos el servicio principal levanta;
- existe persistencia real;
- existen endpoints REST funcionales;
- existe README con instrucciones de ejecución;
- existe evidencia de requerimientos;
- existe documentación técnica y funcional;
- existe presentación de defensa técnica grupal;
- existe documento de defensa técnica individual por estudiante;
- existen enlaces de entrega en AVA.

> **IMPORTANTE:**  
> **Si el proyecto no compila o no puede ejecutarse de ninguna forma durante la evaluación, la entrega grupal no podrá superar el nivel insuficiente, aunque existan archivos en el repositorio.**

---

## 12. Checklist final para estudiantes

Antes de entregar, cada equipo debe verificar:

- [ ] El repositorio está público o compartido con el docente.
- [ ] El último commit fue realizado antes de la fecha límite.
- [ ] La entrega técnica está completa.
- [ ] La documentación técnica y funcional está completa.
- [ ] La presentación de defensa técnica grupal está lista.
- [ ] Cada integrante tiene su documento de defensa técnica individual.
- [ ] En AVA está registrado el link del repositorio.
- [ ] En AVA está registrado el link de la presentación de defensa técnica grupal.
- [ ] En AVA está registrado el link del documento de defensa técnica individual de cada integrante.
- [ ] El proyecto compila desde una copia limpia.
- [ ] El README permite ejecutar el sistema sin preguntar al equipo.
- [ ] La documentación incluye comandos para ejecutar sin IDE.
- [ ] La documentación incluye comandos `./mvnw` para Linux/macOS o `mvnw.cmd` para Windows.
- [ ] La documentación incluye comandos Docker Compose si el proyecto usa contenedores.
- [ ] Las variables sensibles no están en GitHub.
- [ ] Existe `.env.example`.
- [ ] Los servicios levantan en los puertos documentados.
- [ ] La base de datos se crea con migraciones o instrucciones claras.
- [ ] Swagger abre correctamente.
- [ ] Los endpoints principales responden.
- [ ] Los errores retornan JSON uniforme.
- [ ] Las validaciones rechazan datos inválidos.
- [ ] La seguridad bloquea accesos no permitidos.
- [ ] Las pruebas unitarias pasan.
- [ ] La colección Postman o archivo `.http` está actualizado.
- [ ] El levantamiento de requerimientos actualizado refleja cambios, mejoras y eliminaciones justificadas.
- [ ] La matriz de requerimientos está completa.
- [ ] El plan de cierre y feedback está completo.
- [ ] Las observaciones de la última evaluación fueron corregidas o justificadas.
- [ ] Cada integrante documentó sus commits relevantes.
- [ ] Cada integrante documentó evidencia técnica de su aporte.
- [ ] Cada integrante vinculó pruebas o endpoints asociados a su trabajo.
- [ ] Cada integrante documentó un flujo entre capas asociado a su aporte.
- [ ] Cada integrante documentó un flujo entre microservicios si corresponde.

---

## 13. Preguntas guía para preparar la evidencia individual

Cada estudiante debe dejar evidencia documental para responder:

1. ¿Qué problema resuelve el sistema?
2. ¿Cuáles eran los requerimientos funcionales definidos por el equipo?
3. ¿Qué cambios, mejoras o eliminaciones se hicieron sobre el levantamiento original?
4. ¿Dónde está implementado cada requerimiento?
5. ¿Qué microservicios existen y por qué están separados?
6. ¿Qué endpoint evidencia el flujo principal?
7. ¿Qué DTO recibe ese endpoint?
8. ¿Qué entidad se persiste?
9. ¿Qué repositorio participa?
10. ¿Qué regla de negocio se valida en el service?
11. ¿Qué ocurre si el cliente envía datos inválidos?
12. ¿Qué ocurre si el recurso no existe?
13. ¿Qué código HTTP retorna cada caso?
14. ¿Qué relación JPA es la más importante del sistema?
14. ¿Qué migración crea esa tabla?
15. ¿Cómo se protege un endpoint por rol?
16. ¿Cómo se prueba una regla de negocio con Mockito?
17. ¿Qué hace el Gateway?
18. ¿Qué hace Eureka?
19. ¿Cómo se detecta una falla revisando logs?
20. ¿Qué commit propio demuestra mejor su aporte?

---

## 14. Criterio central

La evaluación no busca que el proyecto tenga muchas clases o muchos endpoints sin propósito. Busca que el equipo demuestre dominio real de backend con Spring Boot.

Un proyecto correcto debe poder responder tres preguntas:

- ¿El sistema cumple lo que el equipo prometió construir?
- ¿La solución aplica correctamente los conceptos técnicos del curso?
- ¿Cada estudiante dejó evidencia clara y verificable de su aporte al sistema?

Si la respuesta a cualquiera de esas preguntas es negativa, el proyecto todavía no está listo para una evaluación final transversal.
