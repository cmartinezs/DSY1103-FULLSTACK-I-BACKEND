# 11 — Anti-patrones a evitar

← [Volver al índice](./README.md)

---

## El catálogo de errores más comunes

Los anti-patrones en microservicios son especialmente peligrosos porque muchas veces **parecen una buena solución** cuando se implementan. Solo se nota el daño semanas o meses después.

---

## Anti-patrón 1: El Monolito Distribuido

### ¿Qué es?

Se separan los servicios físicamente (procesos, deployments separados) pero siguen estando **fuertemente acoplados**: comparten BD, se llaman síncronamente en cadenas largas, o los cambios en uno requieren cambios coordinados en otros.

### Cómo se ve en FabriTech

```java
// ❌ order-service accede DIRECTAMENTE a la BD de inventory-service
// (mismo servidor MySQL, diferente schema, pero misma conexión)
@Service
public class OrderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean checkAndReserveStock(String sku, int quantity) {
        // Acceso directo a la BD de otro servicio ← MONOLITO DISTRIBUIDO
        Integer stock = jdbcTemplate.queryForObject(
            "SELECT quantity - reserved_quantity FROM inventory_db.stock_entries WHERE product_sku = ?",
            Integer.class, sku
        );
        if (stock >= quantity) {
            jdbcTemplate.update(
                "UPDATE inventory_db.stock_entries SET reserved_quantity = reserved_quantity + ? WHERE product_sku = ?",
                quantity, sku
            );
            return true;
        }
        return false;
    }
}
```

### Síntomas de monolito distribuido

| Síntoma | Descripción |
|---------|-------------|
| **Deploy coordinado** | Para desplegar order-service siempre hay que desplegar inventory-service también |
| **Esquema compartido** | Los servicios leen tablas de la BD de otros servicios |
| **FKs entre servicios** | `order_items.product_id` tiene FK real a `catalog_db.products.id` |
| **Lógica de negocio en el gateway** | El API Gateway hace transformaciones de negocio, no solo routing |

### Cómo corregirlo

```java
// ✅ order-service llama a la API de inventory-service
@Service
public class OrderService {

    @Autowired
    private InventoryClient inventoryClient;   // FeignClient → HTTP a inventory-service

    public boolean checkAndReserveStock(String sku, int quantity) {
        ReservationResult result = inventoryClient.reserve(
            new ReservationRequest(sku, quantity)
        );
        return result.isSuccess();
    }
}
```

---

## Anti-patrón 2: Servicios Charlatanes (Chatty Services)

### ¿Qué es?

Un flujo de usuario requiere decenas de llamadas síncronas encadenadas entre servicios. Cada llamada agrega latencia, y si cualquiera falla, todo el flujo falla.

### Cómo se ve en FabriTech

```
El cliente hace GET /api/v1/orders/{id}/summary

order-service recibe la petición y hace:
  → customer-service:   GET /customers/456          (20ms)
  → loyalty-service:    GET /loyalty/456/balance     (18ms)
  → catalog-service:    GET /products/FT-ASP-001     (15ms)
  → catalog-service:    GET /products/FT-CHG-003     (15ms)
  → inventory-service:  GET /stock/1/FT-ASP-001      (22ms)
  → shipping-service:   GET /shipments/order-7821    (30ms)
  → payment-service:    GET /payments/order-7821     (19ms)

Total: 7 llamadas síncronas en secuencia = ~140ms mínimo
Si cualquiera de las 7 falla → error al usuario
```

### Cómo corregirlo

**Opción 1: Paralelizar las llamadas independientes**

```java
// Ejecutar en paralelo todo lo que no depende de otra llamada
public OrderSummary getOrderSummary(Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow();

    // Lanzar todas las llamadas en paralelo
    CompletableFuture<CustomerDTO> customerFuture =
        CompletableFuture.supplyAsync(() -> customerClient.getCustomer(order.getCustomerId()));

    CompletableFuture<LoyaltyBalance> loyaltyFuture =
        CompletableFuture.supplyAsync(() -> loyaltyClient.getBalance(order.getCustomerId()));

    CompletableFuture<ShipmentDTO> shipmentFuture =
        CompletableFuture.supplyAsync(() -> shippingClient.getByOrder(orderId));

    CompletableFuture<PaymentDTO> paymentFuture =
        CompletableFuture.supplyAsync(() -> paymentClient.getByOrder(orderId));

    // Esperar todas juntas → latencia = la llamada más lenta, no la suma
    CompletableFuture.allOf(customerFuture, loyaltyFuture, shipmentFuture, paymentFuture).join();

    // Resultado: ~30ms (la más lenta) en lugar de ~140ms (suma secuencial)
    return buildSummary(order, customerFuture.join(), loyaltyFuture.join(),
                        shipmentFuture.join(), paymentFuture.join());
}
```

**Opción 2: BFF (Backend for Frontend)**

Crear un servicio específico para la vista del cliente que consolide los datos:

```
mobile-bff-service → consume eventos y mantiene una vista denormalizada de las órdenes
                    → un solo endpoint: GET /mobile/orders/{id}/summary
                    → responde con todos los datos en una sola llamada
```

**Opción 3: Datos locales (denormalización controlada)**

`order-service` guarda el nombre del cliente y su tier al momento de crear el pedido (snapshot). No necesita llamar a `customer-service` ni `loyalty-service` para mostrar el resumen.

---

## Anti-patrón 3: BD Compartida

### ¿Qué es?

Múltiples servicios escriben y leen de la misma base de datos. Es el anti-patrón más dañino a largo plazo.

### Consecuencias

| Consecuencia | Descripción |
|-------------|-------------|
| **Acoplamiento de esquema** | Cambiar una columna en `orders` puede romper `report-service` y `payment-service` simultáneamente |
| **Contención de recursos** | Una consulta lenta de `report-service` bloquea el pool de conexiones de `order-service` |
| **Sin frontera de dominio** | Cualquier servicio puede escribir en cualquier tabla, sin control |
| **Migración imposible** | No puedes cambiar de PostgreSQL a MongoDB en un servicio si la BD es compartida |

### Cómo detectarlo

```sql
-- En el monolito migrado: buscar conexiones de múltiples servicios a la misma BD
SELECT
    client_addr,
    usename,
    datname,
    COUNT(*) as connections
FROM pg_stat_activity
GROUP BY client_addr, usename, datname
ORDER BY connections DESC;

-- Si ves IPs de order-service Y inventory-service conectadas a "orders_db" → problema
```

---

## Anti-patrón 4: Big Bang Migration

### ¿Qué es?

Intentar reescribir y desplegar todos los microservicios a la vez en una sola release.

### Por qué falla siempre

```
Mes 1-4:  Diseño de la arquitectura completa ✓
Mes 5-14: Desarrollo de los 15 servicios
Mes 15:   El gran deploy

REALIDAD:
  - El monolito original siguió evolucionando durante 14 meses
  - La reescritura tiene 4 meses de desfase con el monolito
  - Hay 200+ nuevas features en el monolito que no están en los microservicios
  - El equipo está agotado
  - El deploy del gran día falla 3 veces
  - Se aplaza 2 meses más
  - ...
```

### Cómo evitarlo: Strangler Fig + releases incrementales

Ver [09 — Strangler Fig](./09_strangler-fig.md) para la estrategia correcta.

---

## Anti-patrón 5: Nano-servicios

### ¿Qué es?

Dividir demasiado: un servicio por tabla, un servicio por función, un servicio por endpoint.

### Cómo se ve

```
// ❌ Demasiada granularidad
customer-name-service      (solo gestiona el nombre del cliente)
customer-email-service     (solo gestiona el email del cliente)
customer-phone-service     (solo gestiona el teléfono del cliente)
customer-address-service   (solo gestiona las direcciones)
```

### Consecuencias

| Consecuencia | Descripción |
|-------------|-------------|
| **Overhead operacional brutal** | 50 servicios con 50 BDs, 50 pipelines de CI/CD, 50 dashboards de monitoreo |
| **Latencia extrema** | Para mostrar el perfil de un cliente: 4 llamadas a 4 servicios |
| **Transacciones imposibles** | Actualizar nombre + teléfono requiere una Saga de 2 pasos |
| **Sin valor real** | Los nano-servicios no escalan de forma independiente (siempre se usan juntos) |

### El tamaño correcto

> Un servicio debe encapsular **una capacidad de negocio completa** — no una tabla, no una función técnica, sino algo que el negocio reconoce como una unidad.

`customer-service` es un servicio correcto: gestiona todo sobre un cliente (datos, direcciones, estado).

---

## Anti-patrón 6: Sincronismo Excesivo

### ¿Qué es?

Usar REST síncrono para todas las comunicaciones, incluyendo aquellas donde no se necesita respuesta inmediata.

### Ejemplo concreto

```java
// ❌ order-service espera a que el email se envíe antes de responder al cliente
@PostMapping("/api/v1/orders")
public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest req) {
    Order order = orderService.createOrder(req);       // 100ms

    // El cliente espera que se envíe el email antes de recibir su respuesta
    emailService.sendConfirmation(order);              // 800ms (latencia de SendGrid)
    notificationService.sendPush(order.getCustomerId()); // 300ms (latencia de FCM)
    loyaltyService.awardPoints(order);                 // 150ms

    // El cliente esperó 1.350ms cuando solo necesitaba 100ms
    return ResponseEntity.status(201).body(OrderResponse.from(order));
}
```

### La corrección

```java
// ✅ Responder inmediatamente, disparar el resto de forma asíncrona
@PostMapping("/api/v1/orders")
public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest req) {
    Order order = orderService.createOrder(req);       // 100ms
    eventPublisher.publish(new OrderCreatedEvent(order)); // <1ms (Kafka)

    // Responde en ~101ms. El email, push y puntos se procesan después en sus propios servicios.
    return ResponseEntity.status(201).body(OrderResponse.from(order));
}
```

---

## Anti-patrón 7: God Service

### ¿Qué es?

Un servicio que concentra demasiadas responsabilidades — el equivalente del monolito dentro de los microservicios.

### Señales de alerta

- El servicio tiene > 50 endpoints
- El servicio tiene > 20 tablas en su BD propia
- El servicio necesita > 10 otros servicios para funcionar
- El tiempo de build del servicio es > 10 minutos

### En FabriTech: el riesgo del order-service

```
❌ Si order-service incluye:
  - Gestión de pedidos (correcto)
  - Cálculo de precios y descuentos  ← debería ser pricing-service o catalog
  - Gestión de clientes              ← debería ser customer-service
  - Generación de facturas           ← debería ser payment-service
  - Cálculo de puntos                ← debería ser loyalty-service
  - Gestión de envíos                ← debería ser shipping-service
```

**Regla:** si un servicio tiene > 2 equipos trabajando en él simultáneamente, probablemente debe dividirse.

---

## Anti-patrón 8: Sin Gestión de Fallos

### ¿Qué es?

Implementar microservicios sin Circuit Breaker, sin timeout, sin retry, asumiendo que la red es confiable.

### El resultado

```
order-service llama a inventory-service sin timeout:
  - inventory-service tiene un problema de BD → responde en 30 segundos
  - El thread de order-service espera los 30 segundos
  - 50 usuarios concurrentes → 50 threads bloqueados esperando
  - El pool de threads de order-service se agota
  - order-service deja de responder
  - El API Gateway marca order-service como DOWN
  - Todos los pedidos dejan de funcionar

Todo esto por un problema en inventory-service que solo afectaba a las reservas,
no a los pedidos en sí.
```

### La solución mínima

```yaml
# Siempre configurar timeouts en todos los clientes HTTP
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            connectTimeout: 2000   # 2 seg para conectar
            readTimeout: 5000      # 5 seg para leer

resilience4j:
  circuitbreaker:
    instances:
      default:
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
```

---

## Checklist de anti-patrones

Antes de dar por terminada la migración de un servicio, verificar que NO incurre en ninguno:

| Anti-patrón | Pregunta de verificación |
|-------------|--------------------------|
| Monolito distribuido | ¿Este servicio conecta a la BD de otro servicio? |
| Chatty services | ¿Hay flujos que hacen > 5 llamadas síncronas encadenadas? |
| BD compartida | ¿Más de un servicio puede escribir en esta BD? |
| Big bang | ¿Se está planeando desplegar > 3 servicios nuevos al mismo tiempo? |
| Nano-servicios | ¿El servicio hace solo una cosa que no escala de forma independiente? |
| Sincronismo excesivo | ¿Hay llamadas síncronas donde no se necesita respuesta inmediata? |
| God service | ¿El servicio tiene > 30 endpoints o > 15 tablas? |
| Sin gestión de fallos | ¿Todos los clientes HTTP tienen timeout y circuit breaker? |

---

*← [10 — Buenas Prácticas](./10_buenas-practicas.md) | Siguiente: [12 — Checklist →](./12_checklist.md)*
