# Ejercicio 19 — Análisis de ventas de concesionaria

> **Nivel:** ⭐⭐⭐⭐ Medio-Avanzado
> **Conceptos:** Streams · Lambdas · `filter` · `map` · `sorted` · `collect` · `Optional` · `groupingBy` · `reduce` · Predicados
> **Tiempo estimado:** 75–95 min

---

## 🚗 Contexto

**AutoChile** tiene sucursales en varias ciudades y cierra decenas de ventas cada mes. El gerente general necesita reportes rápidos y confiables: quién vendió más, qué vehículos no se movieron, cómo se distribuye el financiamiento y qué tan bien negocian los vendedores. Tu tarea es construir el motor de análisis que genere esos reportes procesando la información de ventas registrada.

---

## 📋 Enunciado

El sistema trabaja con dos tipos de información: los **vehículos** del inventario y las **ventas** del mes.

De cada **vehículo** se conoce: un código único, la marca, el modelo, el año de fabricación, la categoría (sedán, SUV, pickup, furgón o eléctrico), el precio de lista en pesos, si es nuevo o usado, y el consumo en litros por cada 100 km (los eléctricos tienen consumo cero).

De cada **venta** se conoce: un identificador, el vehículo vendido, el nombre del vendedor, la ciudad de la sucursal, el mes de la venta (número del 1 al 12), el precio final negociado en pesos y si el cliente pagó al contado o financió con crédito.

Ambos tipos de información son inmutables: una vez registrados, sus datos no cambian.

---

### Qué debe poder hacer el sistema

El motor de análisis recibe la lista de ventas del mes y expone los siguientes reportes. **Todos deben calcularse procesando la colección de forma funcional, sin recorrerla manualmente con bucles.**

**Ventas agrupadas por ciudad** — retorna un mapa donde cada ciudad tiene su lista de ventas.

**Ingresos por ciudad** — retorna un mapa donde cada ciudad tiene la suma de los precios finales de sus ventas.

**Vehículo más vendido** — retorna el vehículo que aparece más veces en las ventas. Si no hay ventas, lo indica de forma segura sin explotar.

**Top N vendedores** — recibe un número N y retorna los N vendedores con mayor monto acumulado, ordenados de mayor a menor. Muestra nombre y total formateado.

**Vehículos que no se vendieron** — recibe la lista completa del inventario y retorna los que no aparecen en ninguna venta del mes, ordenados alfabéticamente por marca.

**Distribución de financiamiento** — separa las ventas en dos grupos: las financiadas con crédito y las pagadas al contado.

**Descuento promedio aplicado** — calcula cuánto bajó el precio en promedio, expresado como porcentaje sobre el precio de lista. Se calcula venta por venta y luego se promedia.

**Ventas de eléctricos** — filtra las ventas de vehículos eléctricos y las ordena por precio final de mayor a menor.

**Ventas rentables** — una venta es rentable cuando se cumplen las tres condiciones al mismo tiempo: el precio final supera el 90% del precio de lista, el vehículo es SUV o eléctrico, y la venta ocurrió entre octubre y diciembre. Cada condición debe definirse por separado con un nombre claro antes de combinarlas.

**Reporte ejecutivo** — imprime en pantalla un resumen con los datos más relevantes obtenidos de los reportes anteriores.

---

## 🚫 Restricciones

- Los reportes del 1 al 9 no pueden recorrer la colección manualmente; deben procesarla de forma funcional.
- Las tres condiciones del reporte de ventas rentables se declaran con nombres descriptivos antes de combinarse, no directamente en el filtro.
- El reporte de vehículo más vendido retorna un resultado que puede o no tener valor; se consume de forma segura sin verificación manual de nulos.
- El reporte de vehículos no vendidos construye internamente el conjunto de códigos ya vendidos de forma funcional, sin recorrerlo en un bucle.
- El descuento de cada venta se calcula dentro del mismo procesamiento, sin variables auxiliares externas.
- Los reportes que retornan listas entregan siempre listas nuevas, no referencias a estructuras internas.

---

## 📥 Casos de prueba

### Inventario (12 vehículos)

| Código | Marca | Modelo | Año | Categoría | Precio de lista | Estado |
|--------|-------|--------|-----|-----------|----------------|--------|
| V-01 | Chevrolet | Tracker | 2023 | SUV | $22.000.000 | Nuevo |
| V-02 | Toyota | Hilux | 2024 | Pickup | $31.000.000 | Nuevo |
| V-03 | Hyundai | Ioniq 5 | 2025 | Eléctrico | $39.000.000 | Nuevo |
| V-04 | Suzuki | Swift | 2019 | Sedán | $7.800.000 | Usado |
| V-05 | Ford | Ranger | 2022 | Pickup | $27.000.000 | Nuevo |
| V-06 | Kia | Sportage | 2023 | SUV | $24.000.000 | Nuevo |
| V-07 | BYD | Atto 3 | 2025 | Eléctrico | $36.000.000 | Nuevo |
| V-08 | Nissan | Frontier | 2021 | Pickup | $19.000.000 | Usado |
| V-09 | Renault | Duster | 2022 | SUV | $16.500.000 | Nuevo |
| V-10 | Mercedes-Benz | GLC | 2024 | SUV | $72.000.000 | Nuevo |
| V-11 | Mitsubishi | L200 | 2020 | Pickup | $14.000.000 | Usado |
| V-12 | Volkswagen | Amarok | 2023 | Pickup | $33.000.000 | Nuevo |

### Ventas de marzo 2026

| Vehículo | Vendedor | Ciudad | Precio final | Pago |
|----------|----------|--------|-------------|------|
| Chevrolet Tracker (V-01) | Ana Pérez | Santiago | $20.800.000 | Contado |
| Toyota Hilux (V-02) | Carlos Mora | Concepción | $30.200.000 | Crédito |
| Hyundai Ioniq 5 (V-03) | Ana Pérez | Santiago | $38.000.000 | Contado |
| Suzuki Swift (V-04) | Diego Salinas | Valparaíso | $7.300.000 | Crédito |
| Ford Ranger (V-05) | Carlos Mora | Concepción | $26.500.000 | Crédito |
| Kia Sportage (V-06) | Mónica Ruiz | La Serena | $23.000.000 | Contado |
| BYD Atto 3 (V-07) | Mónica Ruiz | La Serena | $35.500.000 | Contado |
| Renault Duster (V-09) | Diego Salinas | Valparaíso | $15.800.000 | Crédito |

---

## 📤 Resultados esperados

| Reporte | Resultado |
|---------|-----------|
| Ingresos por ciudad | Santiago $58.800.000 · Concepción $56.700.000 · La Serena $58.500.000 · Valparaíso $23.100.000 |
| Top 3 vendedores | Ana Pérez $58.800.000 · Mónica Ruiz $58.500.000 · Carlos Mora $56.700.000 |
| Vehículos no vendidos | Mercedes-Benz GLC · Mitsubishi L200 · Nissan Frontier · Volkswagen Amarok |
| Financiamiento | 5 ventas con crédito · 3 al contado |
| Descuento promedio | ≈ 2,4% sobre el precio de lista |
| Ventas de eléctricos | Hyundai Ioniq 5 ($38.000.000) → BYD Atto 3 ($35.500.000) |
| Ventas rentables (oct–dic) | Ninguna — marzo no es temporada alta |

---

## 🧠 Reflexión final

¿Por qué conviene darle un nombre claro a cada condición del reporte de ventas rentables en lugar de escribirlas directamente juntas en el filtro? Si el próximo año la temporada alta pasa a incluir también enero y febrero, ¿qué cambia exactamente y dónde? Finalmente, explica con tus propias palabras en qué situación usarías `flatMap` en lugar de `map`, y cómo lo aplicarías si un vehículo pudiera pertenecer a más de una categoría al mismo tiempo.

---

*[← Ejercicio anterior](./18_turnos_hospital.md) · [Volver al índice](./README.md) · [Siguiente ejercicio →](./20_sistema_delivery.md)*
