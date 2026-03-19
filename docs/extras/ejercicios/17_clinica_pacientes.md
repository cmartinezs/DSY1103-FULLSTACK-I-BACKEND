# Ejercicio 17 — Registro de pacientes en clínica

> **Nivel:** ⭐⭐ Básico-Medio
> **Conceptos:** Clases · Constructores · Encapsulamiento · Getters y setters · Validaciones · Relación entre objetos
> **Tiempo estimado:** 40–55 min

---

## 🏥 Contexto

La **Clínica Santa Clara** está digitalizando su proceso de admisión. Hoy todo se registra en papel y eso provoca errores, datos incompletos y pérdida de información. Tu tarea es construir el sistema que permita registrar pacientes y sus consultas médicas, con todas las validaciones que el proceso real exige.

---

## 📋 Enunciado

El sistema maneja dos entidades que se relacionan: el **paciente** y la **consulta médica**. Un paciente puede tener varias consultas registradas a lo largo del tiempo.

---

### El paciente

De cada paciente se registra: RUN con dígito verificador, nombre completo, fecha de nacimiento (día, mes y año por separado), grupo sanguíneo, alergias conocidas y si tiene seguro de salud complementario.

Al registrar un paciente solo son obligatorios el RUN, el nombre y la fecha de nacimiento. El grupo sanguíneo parte como *«desconocido»*, las alergias como *«ninguna»* y el seguro como inactivo. Estos tres datos pueden actualizarse después.

**Validaciones al momento de registrar:**
- El nombre no puede estar vacío.
- El año de nacimiento debe estar entre 1900 y el año actual. Si no cumple, el sistema debe rechazar el registro con un mensaje claro.
- Si se intenta actualizar el grupo sanguíneo con un valor vacío, también debe rechazarse.

El sistema calcula automáticamente la edad del paciente a partir de su año de nacimiento (considera el año actual como 2026). A partir de eso, también sabe si es mayor de edad (18 años o más) y si es adulto mayor (65 años o más).

---

### La consulta médica

De cada consulta se registra: el paciente al que pertenece, el médico tratante, el motivo de consulta, el diagnóstico (que puede quedar pendiente si aún no se registra) y el costo.

Cada consulta recibe un número correlativo único que se asigna automáticamente desde el primer registro; no lo ingresa el usuario.

El diagnóstico y el costo se pueden registrar después de crear la consulta. El costo no puede ser negativo.

**Cálculo del cobro:** si el paciente tiene seguro complementario, paga el 20% del costo; si no tiene, paga el 100%. Este cálculo usa directamente los datos del paciente asociado.

---

### Qué debe mostrar el programa

Para cada paciente se imprime un bloque con su información completa y luego la lista de sus consultas, indicando para cada una: número de consulta, médico, motivo, diagnóstico, costo total y monto que efectivamente paga el paciente. Al final, un resumen del total de pacientes y consultas del día.

---

## 🚫 Restricciones

- Ningún dato de paciente o consulta puede accederse directamente desde fuera; todo pasa por los métodos correspondientes.
- El número correlativo de consulta se genera solo, sin intervención del código que crea la consulta.
- El paciente no sabe que existen las consultas; quien gestiona esa relación es el programa principal.
- Las validaciones están separadas de la lógica de registro; no se mezclan en el mismo bloque.
- No uses colecciones dentro de las entidades del modelo. Si necesitas agrupar consultas de un paciente, hazlo desde el programa principal con un arreglo simple.
- El cálculo de lo que paga el paciente obtiene la información del seguro directamente desde el paciente, sin recibirla como parámetro.

---

## 📥 Casos de prueba

**Andrea Morales** — RUN 12.345.678-K, nacida el 15 de marzo de 1988, grupo A+, sin alergias, con seguro complementario.
- Consulta 1: doctora Paula Ramírez, control de presión arterial, diagnóstico hipertensión leve, costo $45.000.
- Consulta 2: doctor Héctor Lara, dolor de cabeza recurrente, diagnóstico migraña tensional, costo $38.000.

**Rodrigo Cárdenas** — RUN 9.876.543-2, nacido el 22 de julio de 2002, grupo O−, alergia a la penicilina, sin seguro.
- Consulta 1: doctor Héctor Lara, fiebre alta, diagnóstico cuadro viral, costo $32.000.
- Consulta 2: doctora Ana Soto, seguimiento, diagnóstico aún pendiente, costo aún no registrado.

**Carmen Fuentes** — RUN 6.122.313-5, nacida el 3 de enero de 1958, grupo B+, sin alergias, sin seguro.
- Consulta 1: doctora Paula Ramírez, dolor articular, diagnóstico artrosis leve, costo $55.000.

---

## 📤 Resultados esperados

**Andrea Morales** tiene 38 años, es mayor de edad, no es adulto mayor. Sus dos consultas muestran que, al tener seguro, solo paga el 20%: $9.000 de la primera y $7.600 de la segunda.

**Rodrigo Cárdenas** tiene 23 años, es mayor de edad, no es adulto mayor. Sin seguro paga el 100%: $32.000 en su primera consulta. La segunda aparece con diagnóstico y costo pendientes, por lo que el monto a cobrar también figura como pendiente.

**Carmen Fuentes** tiene 68 años, es mayor de edad y también adulto mayor. Sin seguro paga el total de su consulta: $55.000.

Al cierre: 3 pacientes registrados, 5 consultas en el día.

---

## 🧠 Reflexión final

¿Por qué el número de consulta se genera automáticamente en lugar de pedírselo a quien crea la consulta? ¿Qué problema concreto evita esa decisión? Si el sistema se reiniciara, ¿qué pasaría con ese contador y cómo se resuelve ese problema en un sistema real conectado a una base de datos? ¿Qué concepto fundamental de la POO estás aplicando cuando nadie puede modificar directamente los datos de un paciente?

---

*[← Ejercicio anterior](./16_puntos_fidelidad.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./18_turnos_hospital.md)*
