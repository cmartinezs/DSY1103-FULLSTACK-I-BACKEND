# Lección 01 - Lista de verificación: ¿llegué al mínimo requerido?

Usa esta lista para revisar tu comprensión antes de avanzar a la lección siguiente. Esta lección es teórica: el criterio de evaluación no es código que funcione, sino conceptos que puedas explicar con tus propias palabras.

---

## ¿Qué significa "entender" en esta lección?

Memorizar definiciones no es suficiente. Entender un concepto significa que puedes:

1. **Explicarlo** sin leer este documento
2. **Aplicarlo** a un ejemplo nuevo (no el mismo del tutorial)
3. **Relacionarlo** con otros conceptos de la lección

Cada ítem de la lista tiene una pregunta de verificación. Si no puedes responderla sin mirar, vuelve a leer la sección correspondiente.

---

## IE 1.0.1 - Distinguir Internet de la Web

Checklist:

- [ ] Puedo explicar qué es Internet en una oración
- [ ] Puedo explicar qué es la Web en una oración
- [ ] Puedo dar un ejemplo de un servicio de Internet que NO sea la Web

**Pregunta de verificación:** Un amigo te dice "la web y el internet son lo mismo". ¿Qué le respondes y qué ejemplo le darías para ilustrar la diferencia?

---

## IE 1.0.2 - Modelo cliente-servidor

Checklist:

- [ ] Puedo explicar qué es un cliente y qué es un servidor
- [ ] Puedo describir el flujo petición-respuesta
- [ ] Entiendo que el cliente siempre inicia la comunicación
- [ ] Entiendo que un mismo programa puede actuar como cliente y como servidor

**Pregunta de verificación:** Cuando usas la aplicación de Instagram en tu teléfono, ¿quién es el cliente y quién es el servidor? ¿Qué pasa si ese servidor necesita pedirle datos a otro servidor?

---

## IE 1.0.3 - DNS y URL

Checklist:

- [ ] Puedo explicar para qué sirve el DNS con una analogía
- [ ] Puedo identificar las partes de una URL: protocolo, host, puerto, ruta, query string
- [ ] Sé qué significa `localhost` y por qué lo usamos en desarrollo
- [ ] Sé por qué el puerto por defecto para HTTP es 80 y para el desarrollo con Spring Boot es 8080

**Pregunta de verificación:** Analiza esta URL: `http://localhost:8080/api/productos?categoria=electronica&pagina=2`. Identifica cada parte y explica qué representa.

---

## IE 1.0.4 - Protocolo HTTP y sus características

Checklist:

- [ ] Puedo explicar qué es HTTP y para qué existe
- [ ] Entiendo qué significa que HTTP es "sin estado" (stateless)
- [ ] Puedo explicar la implicación práctica del stateless en el desarrollo de APIs
- [ ] Conozco las diferencias principales entre HTTP/1.1 y HTTP/2

**Pregunta de verificación:** ¿Por qué si cierras el navegador y lo vuelves a abrir, la mayoría de los sitios te piden que inicies sesión de nuevo? ¿Qué característica de HTTP explica este comportamiento?

---

## IE 1.0.5 - Anatomía del Request y Response

Checklist:

- [ ] Puedo nombrar las tres partes de una petición HTTP: línea de inicio, cabeceras, cuerpo
- [ ] Puedo nombrar las tres partes de una respuesta HTTP: línea de estado, cabeceras, cuerpo
- [ ] Sé qué hace la cabecera `Content-Type` y por qué es importante
- [ ] Entiendo cuándo una petición tiene cuerpo y cuándo no

**Pregunta de verificación:** Escribe (de memoria, sin copiar) el esqueleto de una petición HTTP que crea un usuario. ¿Qué método usarías? ¿Qué cabeceras incluirías? ¿Qué iría en el cuerpo?

---

## IE 1.0.6 - Métodos HTTP

Checklist:

- [ ] Puedo asociar cada método HTTP (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) con su operación
- [ ] Entiendo qué significa que un método es "seguro" y qué implica
- [ ] Entiendo qué significa que un método es "idempotente" y qué implica
- [ ] Puedo decir cuál método usar dado un requerimiento específico

**Pregunta de verificación:** Un sistema de inventario necesita las siguientes operaciones. ¿Qué método HTTP usarías para cada una?
- Consultar todos los productos disponibles
- Registrar un producto nuevo
- Actualizar el precio de un producto existente
- Marcar un producto como descontinuado (cambiar solo el campo `estado`)
- Eliminar un producto del catálogo

---

## IE 1.0.7 - Códigos de estado HTTP

Checklist:

- [ ] Puedo explicar qué significa cada categoría (2xx, 4xx, 5xx)
- [ ] Puedo distinguir cuándo usar `200`, `201` y `204`
- [ ] Puedo distinguir la diferencia entre `401` y `403`
- [ ] Sé por qué un `500` nunca debería ver el usuario en producción
- [ ] Puedo interpretar un código de estado que no conocía previamente, solo por su primer dígito

**Pregunta de verificación:** Recibes estas respuestas al probar una API. ¿Qué problema indica cada una y dónde buscarías la causa?
- `404 Not Found`
- `401 Unauthorized`
- `500 Internal Server Error`
- `405 Method Not Allowed`

