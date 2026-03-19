# Lección 02 - Lista de verificación: ¿llegué al mínimo requerido?

Usa esta lista para revisar tu comprensión antes de avanzar a la lección 03, donde vas a construir tu primera API. Si no puedes responder las preguntas de verificación sin releer el material, vuelve a la sección correspondiente.

---

## IE 2.0.1 - Frontend vs Backend

Checklist:

- [ ] Puedo explicar qué es el frontend con un ejemplo concreto
- [ ] Puedo explicar qué es el backend con un ejemplo concreto
- [ ] Entiendo por qué el frontend no puede guardar datos sensibles de forma segura
- [ ] Puedo describir cómo el frontend y el backend se comunican

**Pregunta de verificación:** Un amigo te dice que quiere construir una aplicación de notas con login de usuario. ¿Qué partes serían frontend y qué partes serían backend? ¿Por qué el login debe procesarse en el backend y no en el frontend?

---

## IE 2.0.2 - Arquitectura monolítica

Checklist:

- [ ] Puedo definir qué es una arquitectura monolítica
- [ ] Puedo mencionar al menos 3 ventajas del monolito
- [ ] Puedo mencionar al menos 3 desventajas del monolito
- [ ] Entiendo por qué es el punto de partida correcto para proyectos nuevos

**Pregunta de verificación:** Una startup con 3 desarrolladores está construyendo su primer producto. ¿Le recomendarías una arquitectura monolítica o de microservicios? Justifica tu respuesta con al menos dos argumentos.

---

## IE 2.0.3 - Microservicios

Checklist:

- [ ] Puedo definir qué es una arquitectura de microservicios
- [ ] Puedo comparar microservicios con monolito en al menos 3 dimensiones
- [ ] Entiendo qué problemas resuelven los microservicios y cuáles crean
- [ ] Entiendo en qué contexto tiene sentido migrar de monolito a microservicios

**Pregunta de verificación:** Una empresa grande tiene un monolito que procesa pagos, gestiona inventario y envía emails de notificación. El módulo de pagos necesita escalarse 10 veces más que los otros. ¿Por qué en este caso los microservicios podrían justificarse? ¿Qué nuevo problema aparecería al separarlos?

---

## IE 2.0.4 - Qué es una API

Checklist:

- [ ] Puedo definir qué es una API en términos generales
- [ ] Puedo explicar la analogía del menú de restaurante y qué representa cada parte
- [ ] Entiendo por qué la implementación interna puede cambiar sin afectar al cliente
- [ ] Puedo diferenciar entre una API web/HTTP y otros tipos de API

**Pregunta de verificación:** Cuando usas Google Maps en tu aplicación de delivery, ¿qué rol cumple la API de Google Maps? ¿Qué pasa si Google cambia internamente cómo calcula las rutas? ¿Afecta a tu aplicación?

---

## IE 2.0.5 - Qué es REST y sus principios

Checklist:

- [ ] Puedo explicar qué es REST con mis propias palabras (sin copiar la definición)
- [ ] Puedo nombrar al menos 4 de los 6 principios de REST
- [ ] Entiendo qué significa "interfaz uniforme" y por qué es el principio más importante
- [ ] Entiendo la relación entre REST y HTTP (REST usa HTTP pero no toda API HTTP es REST)

**Pregunta de verificación:** Un colega dice "nuestra API es REST porque usa HTTP y devuelve JSON". ¿Estás de acuerdo? ¿Qué criterios adicionales debería cumplir para ser genuinamente REST?

---

## IE 2.0.6 - Buenas prácticas de diseño de APIs REST

Checklist:

- [ ] Entiendo por qué las URLs deben tener sustantivos, no verbos
- [ ] Puedo diseñar las URLs para un CRUD completo dado un recurso
- [ ] Puedo elegir el método HTTP y código de estado correcto para cada operación
- [ ] Entiendo qué es el versionado de API y por qué existe
- [ ] Entiendo cómo implementar filtrado y paginación con query parameters

**Pregunta de verificación:** Diseña las URLs y métodos HTTP para una API de gestión de libros de una biblioteca. Debe permitir: listar todos los libros, ver un libro específico, agregar un libro nuevo, actualizar la cantidad de ejemplares disponibles, y eliminar un libro del catálogo. ¿Qué código de estado devuelves en cada caso?

---

## IE 2.0.7 - El Modelo de Madurez de Richardson

Checklist:

- [ ] Puedo describir los 4 niveles del Modelo de Madurez de Richardson
- [ ] Puedo identificar en qué nivel está una API dado un ejemplo
- [ ] Entiendo en qué nivel se trabaja en la industria habitualmente
- [ ] Entiendo qué es HATEOAS aunque no lo implementes en este curso

**Pregunta de verificación:** Una API tiene un único endpoint `POST /api` que recibe en el cuerpo un campo `accion` con valores como `"listar_usuarios"`, `"crear_usuario"`, `"eliminar_usuario"`. ¿En qué nivel de Richardson está? ¿Cómo la mejorarías para llevarla al Nivel 2?

