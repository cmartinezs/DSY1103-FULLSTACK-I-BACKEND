# Ejercicio 18 — Sistema de turnos de hospital

> **Nivel:** ⭐⭐⭐ Medio
> **Conceptos:** Herencia · Clases abstractas · Interfaces · Polimorfismo · Colecciones · Lógica proposicional
> **Tiempo estimado:** 60–80 min

---

## 🏨 Contexto

El **Hospital Regional del Sur** atiende tres tipos de pacientes: los que llegan por urgencia, los que tienen consulta ambulatoria con un médico y los que van a un procedimiento programado. Cada tipo sigue reglas distintas para calcular el tiempo de espera y decidir si el paciente pasa directo o hace cola. El sistema actual es manual y colapsa en las horas punta. Tu tarea es construir el gestor de turnos que unifique los tres tipos bajo el mismo mecanismo de administración.

---

## 📋 Enunciado

Cada turno, sin importar su tipo, tiene información en común: un número identificador único, el nombre del paciente, su RUN y la hora de llegada. El número se genera automáticamente con un prefijo distinto según el tipo: los de urgencia comienzan con **U-**, los ambulatorios con **A-** y los de procedimiento con **P-**, seguidos de un correlativo propio de cada tipo (U-001, U-002, A-001…).

Cualquier turno debe poder responder tres preguntas: ¿cuál es tu número?, ¿en qué consiste este turno?, ¿cuánto tiempo estimado de espera tienes? Y una cuarta: ¿puedes pasar a atención inmediata sin cola?

Esas cuatro preguntas las responde cada tipo de turno a su manera, según sus propias reglas.

---

### Turno de urgencia

Agrega al turno común: el nivel de urgencia (del 1 al 5, donde 1 es el más crítico), una descripción del motivo y si el paciente llegó en ambulancia.

**Tiempo de espera:** el nivel de urgencia multiplicado por 8 minutos. Los niveles 1 y 2 tienen espera cero porque se atienden al llegar.

**Atención inmediata:** el paciente pasa directo si el nivel es 1 o 2, **o** si llegó en ambulancia. Basta con que se cumpla una de las dos.

---

### Turno ambulatorio

Agrega: la especialidad médica, el nombre del médico asignado, si el paciente tiene hora previamente agendada y en qué posición está en la cola de ese médico (0 significa que es el próximo).

**Tiempo de espera:** la posición en la cola multiplicada por 15 minutos. Si el paciente tiene hora agendada, ese tiempo se reduce a la mitad.

**Atención inmediata:** solo si tiene hora agendada **y** además es el primero en la cola (posición 0). Las dos condiciones deben darse juntas.

---

### Turno de procedimiento

Agrega: el nombre del procedimiento, el número de sala, la duración estimada, si requiere preparación previa (por ejemplo ayuno) y si el equipamiento está disponible.

**Tiempo de espera:** 20 minutos si el equipamiento está listo; 90 minutos si falta algo.

**Atención inmediata:** nunca. Un procedimiento siempre requiere preparación previa del personal.

Si el equipamiento no está disponible, la descripción del turno debe mostrar una advertencia visible, porque eso cambia el tiempo de espera del paciente.

---

### El gestor de turnos

El gestor mantiene la lista de todos los turnos registrados, sin distinguir de qué tipo son. Desde ahí ofrece cuatro operaciones:

1. Mostrar todos los turnos con su descripción completa.
2. Mostrar solo los que tienen atención inmediata.
3. Calcular el tiempo total de espera acumulado entre los turnos que sí hacen cola (los de atención inmediata no cuentan).
4. Mostrar un conteo de cuántos turnos hay de cada tipo.

---

## 🚫 Restricciones

- El comportamiento compartido (número, nombre, RUN, hora) y el comportamiento específico de cada tipo deben estar claramente separados en la jerarquía de clases.
- El gestor no debe conocer qué tipo específico de turno tiene en su lista; simplemente les hace las mismas preguntas a todos y cada uno responde según sus propias reglas.
- Las condiciones de atención inmediata deben estar escritas como variables con nombre antes de combinarse, no directamente en la expresión booleana.
- El contador de numeración es independiente por tipo de turno; cada uno lleva su propio.
- La lista del gestor trabaja con el tipo más general posible, no con los tipos concretos.

---

## 📥 Casos de prueba

Los siguientes turnos se registran en este orden:

| # | Paciente | Tipo | Datos adicionales |
|---|----------|------|-------------------|
| 1 | Valentina Castro — 20.000.001-0 — llegó 08:30 | Urgencia | Nivel 2 · motivo: dolor torácico intenso · llegó en ambulancia |
| 2 | Diego Muñoz — 15.000.002-1 — llegó 09:10 | Ambulatorio | Traumatología · Dr. Sánchez · sin hora agendada · posición 3 en cola |
| 3 | Rosa Jiménez — 16.000.003-2 — llegó 07:45 | Procedimiento | Ecografía abdominal · sala 4 · 40 min · sin preparación previa · equipamiento disponible |
| 4 | Tomás Herrera — 17.000.004-3 — llegó 09:50 | Urgencia | Nivel 4 · motivo: dolor de muelas · sin ambulancia |
| 5 | Lucía Vega — 18.000.005-4 — llegó 08:00 | Ambulatorio | Cardiología · Dra. Ramírez · con hora agendada · posición 0 en cola |
| 6 | Carlos Romero — 19.000.006-5 — llegó 10:15 | Procedimiento | Biopsia de piel · sala 2 · 60 min · requiere preparación · **equipamiento no disponible** |

---

## 📤 Resultados esperados

Al listar todos los turnos en orden de ingreso:

- **U-001 · Valentina Castro** — urgencia nivel 2, dolor torácico, en ambulancia. Espera: 0 min. ✅ Atención inmediata.
- **A-001 · Diego Muñoz** — ambulatorio, traumatología, Dr. Sánchez, sin hora, posición 3. Espera: 45 min.
- **P-001 · Rosa Jiménez** — ecografía abdominal, sala 4, 40 min, sin preparación, equipamiento listo. Espera: 20 min.
- **U-002 · Tomás Herrera** — urgencia nivel 4, dolor de muelas, sin ambulancia. Espera: 32 min.
- **A-002 · Lucía Vega** — ambulatorio, cardiología, Dra. Ramírez, con hora, posición 0. Espera: 0 min. ✅ Atención inmediata.
- **P-002 · Carlos Romero** — biopsia de piel, sala 2, 60 min, requiere preparación. ⚠️ Equipamiento no disponible. Espera: 90 min.

Solo pasan a atención inmediata: **Valentina Castro** y **Lucía Vega**.

Tiempo de espera acumulado en cola (sin los inmediatos): 45 + 20 + 32 + 90 = **187 minutos**.

Resumen por tipo: 2 urgencias · 2 ambulatorios · 2 procedimientos.

---

## 🧠 Reflexión final

¿Qué ventaja concreta tiene que el gestor trabaje con el tipo más general y no con los tipos específicos? Si mañana se agrega un cuarto tipo —turnos de telemedicina—, ¿qué partes del sistema hay que modificar y cuáles quedan intactas? ¿Por qué en este diseño se necesitan tanto una estructura común base como un contrato de comportamiento separado? Explícalo con tus propias palabras, sin usar jerga técnica.

---

*[← Ejercicio anterior](./17_clinica_pacientes.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./19_concesionaria_ventas.md)*
