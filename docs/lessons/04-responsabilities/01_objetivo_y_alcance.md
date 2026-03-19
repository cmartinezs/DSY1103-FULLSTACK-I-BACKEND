# Lección 04 - Separación de responsabilidades: ¿qué vas a aprender?

## ¿De dónde venimos?

En la lección anterior construiste tu primer endpoint con Spring Boot: algo como `GET /greetings` que devolvía un texto plano. Funcionaba, pero todo el código estaba en un solo lugar: el controlador hacía absolutamente todo.

Eso está bien para empezar, pero en la práctica real ese enfoque genera problemas muy rápido. Imagina que mañana tu jefe te pide cambiar cómo se buscan los datos, o agregar una regla de negocio, o hacer pruebas automáticas. Con todo en un solo archivo, cualquier cambio pequeño puede romper todo lo demás.

Esta lección existe para resolver ese problema desde el principio.

---

## ¿Qué vas a construir?

Al terminar esta lección tendrás un microservicio real y ejecutable que:

- Expone el endpoint `GET /tickets` y devuelve una lista de tickets en formato JSON
- Está organizado en **cuatro capas separadas**, cada una con una responsabilidad clara
- Usa datos en memoria (sin base de datos aún) para que podamos concentrarnos en la arquitectura

### Lo que vas a ser capaz de explicar

Más importante que el código es que entiendas el **por qué** detrás de cada decisión. Al terminar deberías poder responder:

- ¿Qué hace el `Controller` y qué NO debería hacer?
- ¿Por qué existe el `Service` como capa separada?
- ¿Por qué el `Repository` no debería tener lógica de negocio?
- ¿Qué ventaja tiene retornar `ResponseEntity` en lugar de un objeto directo?
- ¿Por qué las URLs REST usan sustantivos en plural en lugar de verbos?

---

## ¿Qué NO cubre esta lección? (y por qué)

Es importante que sepas lo que intencionalmente dejamos para más adelante, para que no te preocupes si no lo ves aquí:

| Tema | ¿Por qué lo dejamos después? |
|---|---|
| CRUD completo (crear, editar, eliminar) | Primero necesitas dominar la estructura antes de multiplicar endpoints |
| Validaciones (`@Valid`, `@NotNull`) | Agregamos complejidad solo cuando la base esté sólida |
| Manejo de errores global (`@ControllerAdvice`) | Es el paso siguiente natural después de tener un endpoint funcionando |
| Base de datos real (JPA, PostgreSQL) | Usamos memoria para que el foco sea la arquitectura, no la infraestructura |

El objetivo de esta lección no es hacer mucho: es hacer **una cosa bien hecha y con forma profesional**.

---

## Configuración del proyecto

El proyecto ya tiene una configuración mínima en `src/main/resources/application.properties`:

```properties
spring.application.name=Tickets
```

Esto le dice a Spring Boot cómo se llama tu aplicación. Es el único parámetro configurado hasta ahora.

En el futuro vas a aprender a personalizar más cosas desde ahí, como:

- **Cambiar el puerto** donde corre la aplicación (`server.port`)
- **Agregar un prefijo global** a todas tus rutas (`server.servlet.context-path`)
- **Personalizar el banner** que aparece en consola al iniciar (`banner.txt`)

Por ahora esos temas están pendientes. Lo importante es que entiendas que esa personalización **vive en el archivo de configuración**, no dentro del código Java de tus capas.

---

## La idea central de esta lección

> "No buscamos cantidad de endpoints. Buscamos escribir un endpoint pequeño, pero con forma profesional desde el inicio."

Un código bien estructurado hoy te ahorra horas de depuración mañana. La separación de responsabilidades no es un trámite burocrático: es la diferencia entre un proyecto que escala y uno que se convierte en un problema.
