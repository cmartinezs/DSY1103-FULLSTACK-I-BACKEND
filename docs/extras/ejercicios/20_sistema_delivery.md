# Ejercicio 20 — Sistema de logística y entregas

> **Nivel:** ⭐⭐⭐⭐⭐ Avanzado
> **Conceptos:** Sealed classes · Records · Pattern matching · Streams · Excepciones · Interfaces · Herencia · Genéricos · Optional · Lógica proposicional · Arquitectura por capas
> **Tiempo estimado:** 120–160 min

---

## 🚚 Contexto

**LogiExpress** opera en todo el país con tres tipos de envío: paquetes estándar, envíos refrigerados y documentos legales. Cada tipo tiene sus propias reglas de cobro, condiciones de despacho y restricciones de transporte. La empresa cuenta con repartidores especializados por zona y tipo de vehículo. Tu tarea es construir el sistema completo que gestione desde el registro de un envío hasta la confirmación de su entrega.

> ⚠️ Antes de escribir cualquier código, dibuja en papel el esquema de entidades y cómo se relacionan entre sí. Esa inversión de tiempo al principio se recupera al codificar.

---

## 📋 Enunciado

---

### Los envíos

Todo envío —sin importar su tipo— tiene un código de seguimiento generado automáticamente, los datos del remitente y el destinatario (nombre, teléfono y dirección), el peso en kilogramos, la zona de destino, el estado actual y la fecha y hora de creación.

Los códigos siguen el formato `PKG-0001` para paquetes, `REF-0001` para refrigerados y `DOC-0001` para documentos, con un contador independiente por tipo.

Cualquier envío debe poder responder: ¿cuánto cuesta?, ¿en qué consiste?, y ¿está listo para ser despachado? Esas tres preguntas las responde cada tipo según sus propias reglas.

El sistema solo permite tres tipos de envío y ningún otro. Eso debe quedar garantizado en el diseño.

---

#### Paquete estándar

Agrega: dimensiones (largo, ancho, alto en cm), si contiene artículos frágiles y si requiere firma al recibir.

**Costo:** los primeros 5 kg cuestan $3.000 por kilo; desde el kilo 6 en adelante, $2.000 por kilo. Si el contenido es frágil se suma un recargo fijo de $2.500.

**Listo para despachar** cuando se cumplen las tres condiciones juntas: el peso es mayor que cero, las tres dimensiones son positivas y el estado es *pendiente*.

---

#### Envío refrigerado

Agrega: temperatura de conservación requerida en °C, tipo de producto (alimentos, medicamentos o muestras médicas), si el contenedor está sellado y el tiempo máximo de tránsito en horas.

**Costo:** el costo base de un paquete del mismo peso, más $4.000 por cada hora de tránsito máximo.

**Listo para despachar** cuando: el contenedor está sellado, la temperatura requerida está entre −20°C y +8°C (es decir, cumple simultáneamente que es ≥ −20 **y** ≤ 8), y el estado es *pendiente*. Si el contenedor no está sellado, el sistema debe rechazar el despacho con un error descriptivo.

---

#### Documento legal

Agrega: tipo de documento (notarial, judicial, comercial o personal), si requiere acuse de recibo, número de páginas y si está autenticado notarialmente.

**Costo fijo por tipo:**

| Tipo | Costo base |
|------|-----------|
| Notarial | $8.000 |
| Judicial | $12.000 |
| Comercial | $5.000 |
| Personal | $2.500 |

Si requiere acuse de recibo se agrega $1.500.

**Listo para despachar** cuando el número de páginas es mayor que cero y el estado es *pendiente*. Si el documento es judicial y no está autenticado, el sistema muestra una advertencia pero permite el despacho igual.

---

### Los estados de un envío

Un envío pasa por cinco estados posibles: **Pendiente**, **En preparación**, **En tránsito**, **Entregado** y **Fallido**. Cada estado tiene un nombre legible y una descripción de lo que significa.

---

### Los repartidores

Todo repartidor tiene un identificador, un nombre, la zona que cubre, la cantidad de envíos que lleva actualmente y si está disponible. Hay tres tipos:

- **Repartidor en moto** — capacidad máxima de 5 envíos simultáneos. Acepta cualquier tipo de envío.
- **Repartidor en furgón** — capacidad máxima de 15 envíos. No puede transportar envíos refrigerados.
- **Repartidor refrigerado** — capacidad máxima de 8 envíos. Solo acepta refrigerados y documentos; nunca paquetes estándar de más de 10 kg.

---

### Las alertas de error

El sistema maneja tres tipos de error propios del negocio, todos de tipo comprobado (el código que los provoca debe declararlos o capturarlos explícitamente):

- **Envío no válido** — el envío no cumple las condiciones de despacho. Incluye el código del envío y el motivo del rechazo.
- **Repartidor no disponible** — ningún repartidor puede tomar ese envío en esa zona. Incluye el tipo de envío que se intentó despachar.
- **Entrega fallida** — se intentó confirmar la entrega de un envío que no estaba en tránsito. Incluye el estado actual del envío.

---

### Cómo funciona el despacho

Cuando se despacha un envío, el sistema busca el primer repartidor disponible que cumpla todas las condiciones:

- **Para un paquete estándar:** debe tener capacidad, ser de la misma zona y no ser refrigerado (los refrigerados no toman paquetes estándar de más de 10 kg).
- **Para un envío refrigerado:** debe ser de tipo refrigerado, tener capacidad y ser de la misma zona. Las tres condiciones son obligatorias.
- **Para un documento:** puede ser cualquier tipo de repartidor con capacidad disponible en la misma zona.

Si ningún repartidor cumple todas las condiciones, se lanza el error de repartidor no disponible.

La decisión de qué repartidor puede tomar qué tipo de envío debe tomarse inspeccionando el tipo real del envío, no con una cadena de comparaciones manuales.

---

### La organización del sistema

El sistema se organiza en capas que no se saltan:

- El **repositorio** almacena y recupera envíos sin conocer las reglas del negocio. Es genérico y sirve para cualquier tipo de entidad.
- El **servicio de envíos** expone: registrar un envío, buscarlo por código (devolviendo un resultado que puede estar vacío), actualizar su estado, despacharlo asignando un repartidor y listar los envíos de una zona.
- El **servicio de reportes** expone: ingreso total de envíos entregados, distribución por estado, repartidor con más entregas completadas, envíos ordenados por costo y resumen ejecutivo. Todos los cálculos usan procesamiento funcional de colecciones.
- El **programa principal** solo habla con los servicios; nunca accede directamente al repositorio ni a los envíos.

---

## 🚫 Restricciones

- El diseño debe garantizar que solo existan los tres tipos de envío definidos; no debe ser posible crear un cuarto tipo sin modificar esa declaración.
- La lógica que decide qué repartidor toma qué envío usa inspección del tipo del envío, no comparaciones manuales de texto.
- El servicio de reportes no puede recorrer colecciones con bucles; usa procesamiento funcional.
- Los tres errores del negocio son comprobados; quien llama a los métodos que los lanzan debe manejarlo explícitamente.
- El repositorio no contiene lógica de negocio; es una caja de almacenamiento genérica.
- Las condiciones de despacho de cada tipo de envío se escriben como variables con nombre antes de combinarse.

---

## 📥 Casos de prueba

### Repartidores disponibles

| Nombre | Tipo | Zona |
|--------|------|------|
| Felipe Araya | Moto | Norte |
| Sandra Mora | Furgón | Sur |
| Ignacio Pino | Refrigerado | Norte |

### Envíos a registrar (en orden)

| Código | Tipo | Remitente → Destinatario | Zona | Detalles |
|--------|------|--------------------------|------|----------|
| PKG-0001 | Paquete | María López → Pedro González | Norte | 3 kg · 30×20×15 cm · sin frágil · sin firma |
| REF-0001 | Refrigerado | Lab. BioMed → Clínica Las Condes | Sur | 1,5 kg · 4°C · medicamentos · sellado · 6h tránsito |
| DOC-0001 | Documento | Notaría Central → Constructora Andes | Norte | Notarial · 20 páginas · sin acuse · autenticado |
| PKG-0002 | Paquete | Importadora Tech → Carlos Fuentes | Sur | 12 kg · 50×40×30 cm · frágil · con firma |
| REF-0002 | Refrigerado | Frigorífico del Sur → Supermercado Metro | Norte | 8 kg · −2°C · alimentos · sellado · 4h tránsito |

Después de registrar los cinco:
1. Se intenta despachar todos.
2. PKG-0001 se actualiza a *Entregado*.
3. Se intenta registrar y despachar un sexto envío refrigerado con contenedor **no sellado** → debe generar error.
4. Se intenta confirmar la entrega de PKG-0002 que aún está *En tránsito* → debe generar error.
5. Se imprime el reporte ejecutivo.

---

## 📤 Resultados esperados

**Despacho de los cinco envíos:**

- **PKG-0001** → asignado a Felipe Araya (moto, zona norte). ✅
- **REF-0001** → zona sur, solo Sandra Mora disponible, pero no transporta refrigerados; Ignacio Pino es refrigerado pero está en zona norte. ❌ Error: repartidor no disponible.
- **DOC-0001** → Felipe Araya tiene capacidad en zona norte. ✅
- **PKG-0002** → zona sur, peso 12 kg; Sandra Mora (furgón) puede tomarlo. ✅
- **REF-0002** → zona norte, Ignacio Pino tiene capacidad. ✅

**Estado tras el despacho:**

| Envío | Estado |
|-------|--------|
| PKG-0001 | En tránsito → luego Entregado |
| REF-0001 | Pendiente (no se pudo despachar) |
| DOC-0001 | En tránsito |
| PKG-0002 | En tránsito |
| REF-0002 | En tránsito |

**Reporte ejecutivo:**
- Ingresos totales (envíos entregados): $9.000 (PKG-0001: 3 kg × $3.000).
- Distribución: 1 entregado · 1 pendiente · 3 en tránsito.
- Repartidor con más entregas: Felipe Araya (1 entrega confirmada).
- Error de envío no válido: «El contenedor del envío REF-0006 no está sellado correctamente».
- Error de entrega fallida: «PKG-0002 no está en estado Entregado; estado actual: En tránsito».

---

## 🧠 Reflexión final

¿Qué garantía concreta aporta el hecho de limitar los tipos de envío posibles, y dónde se ve esa garantía en la práctica al escribir el código? Describe el camino completo que recorre una solicitud de despacho desde que se llama hasta que el envío queda asignado a un repartidor: ¿qué capas atraviesa y qué hace cada una? ¿Por qué las reglas de negocio viven en el servicio y no directamente en las entidades? Si LogiExpress agrega un cuarto tipo de envío para objetos de valor (joyas, obras de arte), ¿qué partes del sistema hay que tocar y cuáles quedan sin cambios?

---

## 🏆 Desafíos opcionales

**Desafío A — historial de estados:** cada envío registra un historial de los cambios de estado con el estado anterior, el nuevo y la hora del cambio. El reporte ejecutivo puede mostrar la traza completa de cualquier envío por su código.

**Desafío B — múltiples zonas por repartidor:** un repartidor puede cubrir más de una zona. La asignación busca repartidores cuya lista de zonas incluya la del envío. ¿Qué tipo de colección usarías para guardar las zonas y por qué?

**Desafío C — tarifa dinámica:** el costo de cualquier envío se multiplica por un factor adicional si fue creado entre las 18:00 y las 21:00 (horario punta), y por otro factor si la zona de destino figura en una lista de zonas remotas configurables.

---

*[← Ejercicio anterior](./19_concesionaria_ventas.md) · [Volver al índice](./README.md)*
