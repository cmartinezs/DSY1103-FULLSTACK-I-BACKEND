# Lección 01 - Actividad individual: investigación y reflexión

Esta actividad no tiene código. Tiene preguntas que requieren que investigues, pienses y escribas tus propias conclusiones. El objetivo es que construyas criterio propio, no que copies definiciones de Wikipedia.

> **Formato de entrega:** un documento Markdown (`.md`) con tus respuestas. Cada sección debe tener título y respuesta redactada en tus propias palabras. El largo mínimo por respuesta es el necesario para que se entienda tu razonamiento; no hay máximo.

---

## Parte 1: Actividades investigativas

Estas actividades requieren que busques información y la sintetices con tus propias palabras.

---

### 🔍 Investigación 1.1 — Inspecciona una petición HTTP real

**Objetivo:** ver HTTP en acción con tus propios ojos, no solo en ejemplos del curso.

**Instrucciones:**

1. Abre el navegador (Chrome o Firefox)
2. Ve a cualquier sitio web que uses regularmente (ej: `reddit.com`, `github.com`, `emol.com`)
3. Abre las DevTools con `F12` o clic derecho → "Inspeccionar"
4. Ve a la pestaña **"Network"** (o "Red" si está en español)
5. Recarga la página con `F5`
6. Haz clic en cualquiera de las peticiones que aparecen en la lista

**Responde en tu documento:**

a) ¿Qué URL visitaste? ¿Cuántas peticiones HTTP se generaron al cargar la página?

b) Selecciona UNA de las peticiones y copia su método HTTP, URL completa y código de respuesta. ¿Qué crees que hace esa petición específica?

c) Mira las cabeceras de respuesta (`Response Headers`). Lista al menos tres cabeceras que encuentres y explica, en tus propias palabras, qué podrían significar (aunque no las hayas visto antes, usa el nombre como pista).

d) ¿Hay peticiones con código `3xx`? ¿A qué URL redirigen? ¿Por qué crees que existen esas redirecciones?

---

### 🔍 Investigación 1.2 — HTTP vs HTTPS

**Objetivo:** entender por qué HTTPS importa y qué problema resuelve.

**Instrucciones:** investiga en fuentes confiables (documentación oficial, MDN Web Docs, artículos técnicos). No uses resúmenes de IA como fuente única; contrasta al menos dos fuentes.

**Responde en tu documento:**

a) ¿Qué diferencia hay entre HTTP y HTTPS a nivel técnico? ¿Qué agrega HTTPS que HTTP no tiene?

b) ¿Qué es TLS? ¿Cuál es su relación con HTTPS?

c) Si alguien interceptara el tráfico de red de un sitio HTTP, ¿qué podría ver? ¿Y si fuera HTTPS?

d) ¿Por qué los navegadores modernos marcan los sitios HTTP como "No seguros"? ¿Qué riesgo concreto existe al usar HTTP para enviar, por ejemplo, una contraseña en un formulario de login?

e) Para el desarrollo local (`localhost`), ¿por qué generalmente se usa HTTP sin problema? ¿Qué hace diferente a `localhost` de un dominio en internet?

---

### 🔍 Investigación 1.3 — Evolución de HTTP

**Objetivo:** entender por qué HTTP sigue evolucionando y qué problemas concretos resuelve cada versión.

**Instrucciones:** investiga las diferencias entre HTTP/1.1, HTTP/2 y HTTP/3.

**Responde en tu documento:**

a) ¿Qué problema concreto de HTTP/1.1 resolvió HTTP/2? ¿Qué es la "multiplexación" y por qué es importante?

b) HTTP/3 usa un protocolo de transporte diferente a HTTP/2. ¿Cuál es y por qué se eligió?

c) ¿Cómo puedes saber qué versión de HTTP usa una página web que visitas? (Pista: las DevTools del navegador te lo dicen.) Inspecciona un sitio grande como `google.com` o `cloudflare.com` y reporta qué versión encontraste.

d) ¿Afecta la versión de HTTP al código que escribirías en Spring Boot? ¿Por qué?

---

## Parte 2: Actividades reflexivas

Estas actividades no tienen una respuesta correcta única. Requieren que analices, compares y defiendas tu posición con argumentos.

---

### 💭 Reflexión 1.4 — Stateless: ¿ventaja o limitación?

HTTP es sin estado (stateless): el servidor no recuerda peticiones anteriores.

**Responde en tu documento:**

a) Piensa en tres aplicaciones web que usas frecuentemente. Para cada una, identifica una funcionalidad que claramente requiere "recordar" al usuario entre peticiones (ej: el carrito de compras de un e-commerce). ¿Cómo crees que esas aplicaciones logran ese "recuerdo" si HTTP es stateless?

b) Imagina que HTTP fuera stateful (con estado): cada conexión se mantendría abierta y el servidor recordaría todo sobre cada usuario. ¿Qué ventajas tendría? ¿Qué problemas crearía si el servidor tiene miles de usuarios simultáneos?

c) ¿Por qué crees que los diseñadores de HTTP eligieron el modelo stateless? ¿Fue una buena decisión?

---

### 💭 Reflexión 1.5 — Semántica de los métodos HTTP

El protocolo HTTP define la semántica de los métodos, pero no la hace cumplir técnicamente. Un servidor podría recibir un `DELETE` y crear un registro, y HTTP no lo impediría.

**Responde en tu documento:**

a) ¿Por qué es importante respetar la semántica de los métodos HTTP aunque el protocolo no lo exija? Piensa en qué pasaría si un equipo de desarrollo decide usar siempre `POST` para todo.

b) Algunos desarrolladores argumentan que `PATCH` es innecesario y que `PUT` podría reemplazarlo siempre. ¿Estás de acuerdo? ¿En qué caso concreto `PATCH` es claramente mejor que `PUT`?

c) Busca en internet algún ejemplo real de una API pública (Twitter/X, GitHub, Spotify, etc.) que use métodos HTTP de forma incorrecta o no convencional. ¿Encontraste alguno? ¿Cómo lo justifican?

---

### 💭 Reflexión 1.6 — Códigos de estado y experiencia de usuario

Los códigos de estado HTTP están pensados para la comunicación entre máquinas. Pero el usuario final también los ve a veces.

**Responde en tu documento:**

a) Cuando un usuario ve un `500 Internal Server Error` en su pantalla, ¿qué significa eso desde la perspectiva técnica? ¿Y desde la perspectiva del usuario? ¿Qué debería mostrar una aplicación bien diseñada en lugar del código crudo?

b) ¿Cuál es la diferencia entre un `404` real (el recurso genuinamente no existe) y un `404` de privacidad (el servidor sabe que el recurso existe pero no quiere revelarlo)? ¿En qué situaciones tiene sentido usar el segundo?

c) Hay un debate en la comunidad sobre si las APIs deberían devolver siempre `200 OK` con información de error en el cuerpo, o si deben usar los códigos de estado correctamente. ¿Cuál enfoque prefieres y por qué?

---

## Criterios de evaluación de la actividad

| Criterio | Descripción |
|---|---|
| **Comprensión** | Las respuestas demuestran entendimiento del concepto, no solo copia de definiciones |
| **Evidencia** | Las investigaciones citan fuentes o evidencia observada (capturas, URLs, etc.) |
| **Argumento** | Las reflexiones presentan una posición clara con al menos un argumento de respaldo |
| **Redacción** | Las respuestas están escritas con claridad y en español correcto |
| **Formato** | El documento está en Markdown, con títulos y estructura legible |

---

## Recursos sugeridos para investigar

- [MDN Web Docs - HTTP](https://developer.mozilla.org/es/docs/Web/HTTP) — La referencia más completa y confiable de HTTP en español
- [HTTP Status Codes](https://httpstatuses.com/) — Referencia rápida de todos los códigos de estado
- [How HTTPS works](https://howhttpsworks.com/) — Explicación visual e interactiva de HTTPS
- [HTTP/3 explained](https://http3-explained.haxx.se/en/) — El libro gratuito sobre HTTP/3 del creador de curl
- [DevTools Network panel](https://developer.chrome.com/docs/devtools/network/) — Guía oficial de Chrome DevTools para el panel Network

