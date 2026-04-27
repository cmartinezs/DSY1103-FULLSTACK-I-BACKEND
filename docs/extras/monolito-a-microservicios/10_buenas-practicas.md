# 10 — Buenas prácticas

← [Volver al índice](./README.md)

---

## 1. Base de datos por servicio

### La regla

Cada microservicio **posee exclusivamente** su base de datos. Ningún otro servicio puede conectarse a ella directamente.

```
✅ CORRECTO:
  order-service → orders_db (PostgreSQL 5432 interno, no expuesto)
  inventory-service → inventory_db (PostgreSQL 5433 interno, no expuesto)

❌ INCORRECTO:
  order-service → orders_db
  inventory-service → orders_db    ← comparte BD con order-service
```

### ¿Cómo compartir datos sin compartir BD?

| Necesidad | Solución |
|-----------|---------|
| Leer datos de otro servicio | Llamada a su API REST |
| Reaccionar a cambios | Suscribirse a sus eventos (Kafka) |
| Reportes que combinan datos de varios servicios | `report-service` con BD de lectura propia (alimentada por eventos) |
| Datos de referencia que no cambian (ej: lista de países) | Duplicar en cada servicio que los necesita |

### Elección de BD por servicio en FabriTech

| Servicio | BD | Justificación |
|----------|-----|---------------|
| `order-service` | PostgreSQL | Transacciones ACID, consistencia |
| `inventory-service` | PostgreSQL | Transacciones críticas (stock reserva/confirm) |
| `loyalty-service` | PostgreSQL + Redis | PostgreSQL para historial; Redis para el saldo de puntos (acceso rápido) |
| `catalog-service` | PostgreSQL + Redis | Redis para caché de productos (lectura intensiva) |
| `report-service` | Elasticsearch | Full-text search, analítica rápida |
| `auth-service` | PostgreSQL + Redis | Redis para blacklist de tokens revocados |
| `notification-service` | PostgreSQL | Historial de preferencias y notificaciones enviadas |

---

## 2. Versionado de APIs

### ¿Por qué versionar?

```
catalog-service tiene 3 consumers:
  - web (Angular)
  - app Android
  - app iOS
  - order-service (otro microservicio)

Si catalog-service cambia el contrato de /api/v1/products sin versionar,
todos los consumers se rompen al mismo tiempo.
```

### Estrategia de versioning en FabriTech

**Versionado en la URL** (el más claro y visible):

```
/api/v1/products         ← versión actual (soportada indefinidamente)
/api/v2/products         ← nueva versión con campos adicionales
```

### Ciclo de vida de una versión

```
v1 en ACTIVE → anunciar v2 → v1 pasa a DEPRECATED → período de transición (3 meses)
→ v1 pasa a SUNSET (retorna 410 Gone) → v1 se elimina
```

```java
// En el Controller: manejar múltiples versiones
@RestController
@RequestMapping("/api")
public class ProductController {

    // v1: respuesta básica
    @GetMapping("/v1/products/{sku}")
    public ResponseEntity<ProductV1Response> getProductV1(@PathVariable String sku) {
        Product product = productService.getBySku(sku);
        return ResponseEntity.ok(ProductV1Response.from(product));
    }

    // v2: respuesta con imágenes + especificaciones técnicas
    @GetMapping("/v2/products/{sku}")
    public ResponseEntity<ProductV2Response> getProductV2(@PathVariable String sku) {
        Product product = productService.getBySku(sku);
        return ResponseEntity.ok(ProductV2Response.from(product));
    }

    // v1 deprecated: agregar header de advertencia
    @GetMapping("/v1/products")
    @Deprecated(since = "v2.0")
    public ResponseEntity<List<ProductV1Response>> listProductsV1(...) {
        return ResponseEntity.ok()
            .header("Deprecation", "true")
            .header("Sunset", "2026-12-31")
            .header("Link", "/api/v2/products; rel=\"successor-version\"")
            .body(products);
    }
}
```

---

## 3. Observabilidad: los tres pilares

### Pilar 1: Logging estructurado

Los logs deben ser **estructurados** (JSON), no texto plano. Esto permite filtrar y buscar en Elasticsearch/Kibana.

```java
// Configuración de Logback (logback-spring.xml)
// Usar logstash-logback-encoder para JSON

// En el código: siempre usar SLF4J, nunca System.out.println
@Slf4j
@Service
public class OrderCreationService {

    public Order createOrder(CreateOrderRequest request) {
        log.info("Iniciando creación de pedido",
            StructuredArguments.keyValue("customerId", request.customerId()),
            StructuredArguments.keyValue("itemCount", request.items().size()),
            StructuredArguments.keyValue("totalAmount", request.totalAmount())
        );
        // Resultado en JSON:
        // {"message":"Iniciando creación de pedido","customerId":456,"itemCount":3,"totalAmount":89990}
    }
}
```

**Correlation ID:** toda petición recibe un ID único que se propaga entre servicios:

```java
// En el API Gateway: inyectar traceId en cada petición
@Component
public class TraceIdFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = Optional
            .ofNullable(exchange.getRequest().getHeaders().getFirst("X-Trace-Id"))
            .orElse(UUID.randomUUID().toString());

        return chain.filter(
            exchange.mutate()
                .request(r -> r.header("X-Trace-Id", traceId))
                .build()
        ).contextWrite(Context.of("traceId", traceId));
    }
}
```

```java
// En cada servicio: incluir traceId en todos los logs
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String traceId = request.getHeader("X-Trace-Id");
        MDC.put("traceId", traceId);         // aparece en todos los logs del request
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

### Pilar 2: Métricas con Prometheus + Grafana

> 📖 **Contenido complementario — fuera del scope de DSY1103**
>
> Prometheus y Grafana son el estándar industrial para métricas en microservicios. No se implementan en el curso, pero es importante que sepas que existen y cómo se integran con Spring Boot Actuator cuando trabajes en producción.

Spring Boot Actuator expone métricas automáticamente:

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: ${ENVIRONMENT:dev}
```

**Métricas disponibles automáticamente:**
- `http_server_requests_seconds` — latencia por endpoint
- `jvm_memory_used_bytes` — uso de memoria
- `hikaricp_connections_active` — conexiones de BD activas
- `kafka_consumer_lag` — lag del consumer Kafka

**Métricas de negocio personalizadas:**

```java
@Service
public class OrderCreationService {

    private final MeterRegistry meterRegistry;

    public Order createOrder(CreateOrderRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Order order = doCreateOrder(request);
            meterRegistry.counter("orders.created",
                "type", order.getType().name()).increment();
            sample.stop(meterRegistry.timer("orders.creation.duration",
                "status", "success"));
            return order;
        } catch (InsufficientStockException e) {
            meterRegistry.counter("orders.rejected", "reason", "insufficient_stock").increment();
            sample.stop(meterRegistry.timer("orders.creation.duration",
                "status", "rejected_stock"));
            throw e;
        }
    }
}
```

**Dashboard de Grafana para FabriTech:**

```
Panel 1: Pedidos por minuto (rate de orders.created)
Panel 2: Latencia p95 de /api/v1/orders (http_server_requests_seconds)
Panel 3: Tasa de error de inventory-service (circuit breaker state)
Panel 4: Lag de Kafka consumers
Panel 5: Uso de memoria por servicio
Panel 6: Alertas activas
```

### Pilar 3: Trazas distribuidas con Zipkin

> 📖 **Contenido complementario — fuera del scope de DSY1103**
>
> El tracing distribuido permite seguir una petición a través de múltiples microservicios. Zipkin (o Jaeger) es la herramienta estándar para esto. Se menciona aquí como referencia para cuando trabajes con arquitecturas reales en producción.

```yaml
# application.yml (todos los servicios)
management:
  tracing:
    sampling:
      probability: 0.1    # muestrea el 10% de las peticiones en producción
                          # (en dev: 1.0 para samplear todo)
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans
```

Cuando un cliente hace `POST /api/v1/orders`, Zipkin muestra:

```
Trace ID: abc-123-def-456
├─ API Gateway             5ms
├─── order-service         187ms
│    ├─ customer-service   23ms
│    ├─ catalog-service    15ms
│    └─ inventory-service  142ms  ← el cuello de botella está aquí
│         └─ (BD query)    140ms  ← falta un índice
```

---

## 4. Health Checks con Spring Actuator

```yaml
management:
  health:
    db:
      enabled: true
    kafka:
      enabled: true
    diskspace:
      enabled: true
      threshold: 10GB
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true      # activa /health/liveness y /health/readiness
```

**Diferencia importante:**

| Endpoint | Propósito | Falla cuando |
|----------|-----------|-------------|
| `/health/liveness` | ¿El proceso está vivo? | La app se trabó, hay deadlock |
| `/health/readiness` | ¿El servicio puede atender peticiones? | BD no disponible, Kafka caído |

Kubernetes usa `liveness` para decidir si reiniciar el pod, y `readiness` para decidir si enrutar tráfico.

---

## 5. Contratos de API con OpenAPI

```java
// En cada microservicio: documentar con SpringDoc
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
            .info(new Info()
                .title("Order Service API")
                .description("Gestión del ciclo de vida de pedidos en FabriTech")
                .version("v2.1.0")
                .contact(new Contact()
                    .name("Squad Comercial")
                    .email("squad-comercial@fabritech.cl")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

```java
// Anotar los endpoints para documentación automática
@Operation(summary = "Crear nuevo pedido",
           description = "Reserva stock y crea el pedido. Retorna 409 si no hay stock suficiente.")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
    @ApiResponse(responseCode = "400", description = "Datos del pedido inválidos"),
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
    @ApiResponse(responseCode = "409", description = "Stock insuficiente")
})
@PostMapping("/api/v1/orders")
public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) { ... }
```

### Contract Testing con Pact

Para verificar que `order-service` y `inventory-service` son compatibles **sin desplegar ambos**:

```java
// En order-service (consumer): define el contrato esperado
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "inventory-service")
class InventoryClientContractTest {

    @Pact(consumer = "order-service")
    public RequestResponsePact reserveStockPact(PactDslWithProvider builder) {
        return builder
            .given("producto FT-ASP-001 tiene 10 unidades disponibles")
            .uponReceiving("reserva de 1 unidad de FT-ASP-001")
                .method("POST")
                .path("/api/v1/stock/reserve")
                .body(new PactDslJsonBody()
                    .stringValue("sku", "FT-ASP-001")
                    .integerType("quantity", 1))
            .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                    .booleanType("success", true)
                    .stringType("reservationId"))
            .toPact();
    }
}
```

---

## 6. Seguridad entre servicios

### mTLS (mutual TLS) en producción

En el monolito, las clases se llaman entre sí — no hay red de por medio. En microservicios, las llamadas entre servicios viajan por la red interna. Sin mTLS, cualquier proceso dentro del cluster podría llamar a cualquier servicio.

Con mTLS, tanto el cliente como el servidor verifican sus identidades via certificados:

```yaml
# Con Istio service mesh (gestiona mTLS automáticamente en Kubernetes)
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: fabritech
spec:
  mtls:
    mode: STRICT    # rechaza conexiones sin mTLS
```

### Gestión de secretos con Vault

```yaml
# Nunca en application.yml:
spring.datasource.password: ${DB_PASSWORD}    # ← viene de variable de entorno

# En producción con HashiCorp Vault:
spring:
  cloud:
    vault:
      token: ${VAULT_TOKEN}
      uri: https://vault.fabritech.internal
      kv:
        enabled: true
        backend: secret
        application-name: order-service
```

---

## 7. Conway's Law y estructura de equipos

> *"Si tienes 4 squads, tu arquitectura tendrá 4 grupos de servicios."*

Para que los microservicios funcionen, la **estructura del equipo debe alinearse con la arquitectura**:

| ❌ Organización por capa técnica | ✅ Organización por dominio |
|--------------------------------|---------------------------|
| Equipo Frontend | Squad Comercial (fullstack: web + API de catálogo y pedidos) |
| Equipo Backend | Squad Operaciones (fullstack: API de inventario y producción) |
| Equipo BD | Squad Clientes (fullstack: API de clientes y fidelización) |
| Equipo Ops | Squad Logística (fullstack: API de envíos) |
| — | Squad Plataforma (infraestructura, APIs transversales) |

Cada squad **posee, despliega y opera** sus servicios. No hay "equipo de backend" que sea el cuello de botella de todos los features.

---

## 8. SLA y SLO por servicio

Cada servicio debe tener objetivos de nivel de servicio definidos:

| Servicio | Disponibilidad (SLO) | Latencia p95 (SLO) | Periodo de retención de datos |
|----------|---------------------|---------------------|-------------------------------|
| `order-service` | 99.9% | < 500ms | 5 años |
| `inventory-service` | 99.95% | < 200ms | 3 años |
| `catalog-service` | 99.5% | < 100ms | Permanente |
| `notification-service` | 99.0% | < 2s | 90 días |
| `report-service` | 98.0% | < 5s | 7 años |

Cuando un servicio viola su SLO, se dispara una alerta en el dashboard de Grafana y se abre automáticamente un incidente en el sistema de guardia (PagerDuty).

---

*← [09 — Strangler Fig](./09_strangler-fig.md) | Siguiente: [11 — Anti-patrones →](./11_antipatrones.md)*
