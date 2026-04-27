# 07 — Servicios auxiliares

← [Volver al índice](./README.md)

---

## ¿Qué hace a un servicio "auxiliar"?

Los servicios auxiliares son **capacidades técnicas transversales** que no pertenecen a ningún dominio de negocio específico, sino que apoyan a múltiples servicios. Sus características:

| Característica | Descripción |
|----------------|-------------|
| **Sin lógica de negocio propia** | No saben si el pedido es urgente o si el cliente es Gold |
| **Consumidores de eventos o API calls** | Reciben instrucciones claras: "envía este email", "genera este PDF" |
| **Encapsulamiento de dependencias externas** | Ocultan la complejidad de SendGrid, Firebase, iText, etc. |
| **Reusables** | Los 10 servicios de dominio pueden usarlos sin duplicar código |
| **Sin estado de negocio** | No guardan entidades de negocio — solo metadata técnica (logs, plantillas) |

---

## auth-service (Puerto 8011)

### Responsabilidades

Gestionar la **identidad y autenticación** de todos los usuarios del sistema:
- Empleados internos (cajeros, bodegueros, gerentes, admins de TI)
- Clientes externos (compradores del e-commerce)

> `auth-service` gestiona **credenciales**. `customer-service` gestiona **perfiles**. Son responsabilidades distintas: un cliente puede existir en `customer-service` sin haberse registrado en la app (fue atendido en tienda física).

### JWT: estructura del token

```json
{
  "header": { "alg": "RS256", "typ": "JWT" },
  "payload": {
    "sub": "456",                             // userId
    "email": "ana@ejemplo.cl",
    "roles": ["CUSTOMER"],                    // o ["EMPLOYEE", "CASHIER", "BRANCH_MANAGER"]
    "branchId": null,                         // para empleados de sucursal
    "iat": 1714168800,                        // issued at
    "exp": 1714172400                         // expires (1 hora)
  }
}
```

### Flujo de autenticación

```
1. Cliente → POST /api/v1/auth/login { email, password }
2. auth-service verifica password con bcrypt
3. auth-service genera:
   - access_token  (JWT, expira en 1 hora)
   - refresh_token (UUID opaco, expira en 30 días, guardado en BD)
4. Responde: { access_token, refresh_token, expiresIn: 3600 }

5. Para renovar sin pedir contraseña de nuevo:
   POST /api/v1/auth/refresh { refresh_token }
   → nuevo access_token
```

### RBAC (Role-Based Access Control)

```java
// Roles del sistema
public enum Role {
    // Clientes externos
    CUSTOMER,

    // Empleados internos
    EMPLOYEE,
    CASHIER,           // vendedor en sucursal
    WAREHOUSE_STAFF,   // bodeguero
    BRANCH_MANAGER,    // jefe de sucursal
    PRODUCTION_STAFF,  // operario de fabricación
    PROCUREMENT_STAFF, // abastecimiento

    // Administración
    MANAGER,           // gerente
    ADMIN              // TI - acceso total
}
```

```java
// En cada microservicio: extrae y valida el rol del JWT
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String token = extractToken(request);
        if (token != null && jwtVerifier.isValid(token)) {
            Claims claims = jwtVerifier.parse(token);
            String userId = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, toAuthorities(roles));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
```

```java
// Uso en controller
@PostMapping("/api/v1/products")
@PreAuthorize("hasRole('ADMIN')")   // solo admins pueden crear productos
public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest req) { ... }

@GetMapping("/api/v1/products")
@PreAuthorize("permitAll()")        // público
public ResponseEntity<Page<Product>> listProducts(...) { ... }
```

---

## notification-service (Puerto 8012)

### Canales de notificación

| Canal | Tecnología | Casos de uso |
|-------|-----------|--------------|
| **Push Android** | Firebase Cloud Messaging (FCM) | Pedido listo, envío en camino, oferta |
| **Push iOS** | Apple Push Notification Service (APNs) | Ídem |
| **SMS** | Twilio | OTP, alerta crítica de entrega |
| **In-app** | WebSocket (STOMP) | Notificación en panel de empleados |

### Arquitectura interna

```java
// Router: decide qué adapter usar según el canal
@Service
public class NotificationRouter {

    private final Map<String, NotificationAdapter> adapters;

    public NotificationRouter(FcmAdapter fcm, ApnsAdapter apns,
                              TwilioAdapter twilio, WebSocketAdapter ws) {
        this.adapters = Map.of(
            "PUSH_ANDROID", fcm,
            "PUSH_IOS",     apns,
            "SMS",          twilio,
            "IN_APP",       ws
        );
    }

    public void send(NotificationRequest request) {
        NotificationAdapter adapter = adapters.get(request.channel());
        if (adapter == null) {
            throw new UnsupportedChannelException(request.channel());
        }
        adapter.send(request);
    }
}
```

### Preferencias del usuario

`notification-service` mantiene las preferencias de notificación de cada usuario:

```sql
CREATE TABLE notification_preferences (
    user_id          VARCHAR(50) PRIMARY KEY,
    push_enabled     BOOLEAN DEFAULT true,
    sms_enabled      BOOLEAN DEFAULT false,
    order_updates    BOOLEAN DEFAULT true,
    promotions       BOOLEAN DEFAULT true,
    device_token_android TEXT,
    device_token_ios     TEXT,
    phone_number         VARCHAR(20)
);
```

### Consumo de eventos

```java
@KafkaListener(topics = {"orders.events", "shipments.events", "loyalty.events"})
public void handleEvent(DomainEvent event) {
    NotificationRequest request = switch (event.type()) {
        case "OrderPaid"          -> buildOrderConfirmedNotification(event);
        case "ShipmentDispatched" -> buildShipmentNotification(event);
        case "ShipmentDelivered"  -> buildDeliveredNotification(event);
        case "TierUpgraded"       -> buildTierUpgradeNotification(event);
        default                   -> null;
    };

    if (request != null) {
        router.send(request);
    }
}
```

---

## email-service (Puerto 8013)

### Arquitectura de plantillas

Los emails usan **Thymeleaf** para el renderizado HTML. Las plantillas viven en el servicio:

```
email-service/src/main/resources/
└── templates/
    ├── order-confirmed.html
    ├── shipment-dispatched.html
    ├── invoice-ready.html
    ├── loyalty-tier-upgrade.html
    ├── stock-alert.html
    └── partials/
        ├── header.html     (logo + colores corporativos de FabriTech)
        └── footer.html     (datos legales, links de baja)
```

### Renderizado de un email

```java
@Service
public class EmailTemplateRenderer {

    private final TemplateEngine templateEngine;

    public String render(String templateName, Map<String, Object> variables) {
        Context ctx = new Context(Locale.forLanguageTag("es-CL"));
        variables.forEach(ctx::setVariable);
        return templateEngine.process(templateName, ctx);
    }
}
```

```html
<!-- templates/order-confirmed.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
  <div th:replace="partials/header :: header"></div>

  <h1>Hola, <span th:text="${customerName}">Cliente</span> 👋</h1>
  <p>Tu pedido <strong th:text="'#' + ${orderId}">ORD-7821</strong> fue confirmado.</p>

  <table>
    <tr th:each="item : ${items}">
      <td th:text="${item.productName}"></td>
      <td th:text="${item.quantity}"></td>
      <td th:text="${#numbers.formatCurrency(item.subtotal)}"></td>
    </tr>
  </table>

  <p><strong>Total: <span th:text="${#numbers.formatCurrency(total)}"></span></strong></p>

  <div th:replace="partials/footer :: footer"></div>
</body>
</html>
```

### Proveedores de email soportados

| Proveedor | Ventaja | Configuración |
|-----------|---------|---------------|
| **SendGrid** | Analítica avanzada, alta deliverability | API key |
| **AWS SES** | Costo muy bajo (volumen alto) | IAM credentials |
| **Mailgun** | Simple, buen soporte LATAM | API key |
| **SMTP propio** | Control total, para on-premise | host + credentials |

El proveedor se selecciona via variable de entorno — el código no cambia:

```yaml
# application.yml
email:
  provider: sendgrid    # o: ses | mailgun | smtp
  sendgrid:
    api-key: ${SENDGRID_API_KEY}
  from: noreply@fabritech.cl
  from-name: FabriTech
```

### Manejo de fallos y reintentos

```java
@KafkaListener(topics = "email.requests")
@RetryableTopic(
    attempts = "3",
    backoff = @Backoff(delay = 5000, multiplier = 2),  // 5s, 10s, 20s
    dltTopicSuffix = "-dlt"    // Dead Letter Topic para emails que no se pudieron enviar
)
public void handleEmailRequest(EmailRequest request) {
    emailSender.send(request);
}
```

Los emails que fallan 3 veces van al **Dead Letter Topic** `email.requests-dlt`, donde un proceso de monitoreo alerta al equipo de TI.

---

## pdf-service (Puerto 8014)

### Stack de generación de PDFs

```
1. Thymeleaf          → renderiza el HTML con datos dinámicos
2. Flying Saucer      → convierte el HTML/CSS a un árbol de rendering
3. iText / OpenPDF    → genera el archivo PDF binario
4. ResponseEntity<byte[]> → entrega el PDF directamente en la respuesta HTTP
```

### Plantillas disponibles

| Template | Descripción | Quién lo solicita |
|----------|-------------|-------------------|
| `INVOICE_BOLETA` | Boleta electrónica (datos simplificados) | `payment-service` |
| `INVOICE_FACTURA` | Factura con datos del emisor y receptor | `payment-service` |
| `DISPATCH_GUIDE` | Guía de despacho para el chofer | `shipping-service` |
| `PURCHASE_ORDER` | Orden de compra para el proveedor | `procurement-service` |
| `PICKING_LIST` | Lista de picking para bodeguero | `order-service` |
| `PRODUCTION_ORDER` | Orden de producción con BOM | `manufacturing-service` |
| `SALES_REPORT` | Reporte de ventas en PDF | `report-service` |

### API REST

```
POST /api/v1/pdf/generate
Content-Type: application/json

{
  "template": "INVOICE_BOLETA",
  "filename": "boleta-7821",
  "data": {
    "invoiceNumber": "B-00042156",
    "issuedAt": "2026-04-26T15:30:00",
    "seller": {
      "name": "FabriTech S.A.",
      "rut": "76.123.456-7",
      "address": "Av. Independencia 1234, Pudahuel"
    },
    "items": [
      { "description": "Aspiradora Robótica FT-ASP-001", "qty": 1, "price": 89990 }
    ],
    "total": 89990
  }
}

→ 200 OK
Content-Type: application/pdf
Content-Disposition: attachment; filename="boleta-7821-2026.pdf"

<bytes del PDF>
```

### Entrega del PDF como respuesta HTTP

```java
@RestController
@RequestMapping("/api/v1/pdf")
public class PdfController {

    private final PdfGenerationService pdfService;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generate(@RequestBody PdfRequest request) {
        byte[] pdfBytes = pdfService.generate(request.template(), request.data());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + request.filename() + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(pdfBytes.length)
            .body(pdfBytes);
    }
}
```

> 📖 **En producción:** cuando el PDF debe persistir o compartirse por URL, se sube a un sistema de almacenamiento de objetos (S3, MinIO, Google Cloud Storage) y se retorna una URL pre-firmada con tiempo de expiración. En el contexto del curso, la entrega directa en la respuesta es el enfoque adecuado.

### Almacenamiento en S3 (referencia para producción)

```java
// ⚠️ Requiere AWS SDK o MinIO Client — fuera del scope del curso
@Service
public class PdfStorageService {

    private final S3Client s3;

    public String uploadAndGetUrl(String filename, byte[] pdfBytes) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket("fabritech-documents")
            .key("invoices/" + filename + ".pdf")
            .contentType("application/pdf")
            .build();

        s3.putObject(request, RequestBody.fromBytes(pdfBytes));

        // URL pre-firmada válida por 24 horas
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(24))
            .getObjectRequest(b -> b.bucket("fabritech-documents")
                                    .key("invoices/" + filename + ".pdf"))
            .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
```

---

## report-service (Puerto 8015)

### El problema de los reportes en un monolito

En el monolito, los reportes hacen queries pesadas sobre la BD de producción, compitiendo con las transacciones del negocio. En una temporada alta, un reporte de ventas podía demorar 45 segundos y bloquear otras consultas.

### La solución: CQRS

**Command Query Responsibility Segregation (CQRS)**: separar las operaciones de escritura (commands) de las de lectura (queries).

```
                    WRITE SIDE (transaccional)
order-service   → BD PostgreSQL orders_db (normalizada, rápida para escritura)
                            │
                            │ eventos via Kafka
                            ▼
                    READ SIDE (analítica)
report-service  → BD Elasticsearch / PostgreSQL reports_db
                  (denormalizada, pre-calculada, rápida para lectura)
```

### Modelo de datos denormalizado (para reportes)

```sql
-- Una sola tabla plana con todo lo que necesitan los reportes
-- Se carga desde los eventos de dominio
CREATE TABLE sales_facts (
    id               BIGSERIAL PRIMARY KEY,
    order_id         VARCHAR(20),
    order_date       DATE NOT NULL,
    order_month      VARCHAR(7) NOT NULL,     -- ej: "2026-04"
    branch_id        BIGINT,
    branch_name      VARCHAR(100),
    customer_id      BIGINT,
    customer_tier    VARCHAR(20),
    product_sku      VARCHAR(20),
    product_name     VARCHAR(200),
    category_name    VARCHAR(100),
    quantity         INT,
    unit_price       DECIMAL(10,2),
    subtotal         DECIMAL(10,2),
    discount         DECIMAL(10,2),
    carrier_name     VARCHAR(50),
    payment_method   VARCHAR(20)
);

-- Index para los reportes más frecuentes
CREATE INDEX idx_sales_month       ON sales_facts(order_month);
CREATE INDEX idx_sales_branch      ON sales_facts(branch_id, order_month);
CREATE INDEX idx_sales_product     ON sales_facts(product_sku, order_month);
CREATE INDEX idx_sales_customer    ON sales_facts(customer_id);
```

### Consumer de eventos

```java
@KafkaListener(topics = {"orders.events", "shipments.events"})
public void handleEvent(DomainEvent event) {
    if ("OrderPaid".equals(event.type())) {
        // Construye y persiste los "hechos" de la venta
        List<SalesFact> facts = event.items().stream()
            .map(item -> SalesFact.builder()
                .orderId(event.orderId())
                .orderDate(event.paidAt().toLocalDate())
                .orderMonth(YearMonth.from(event.paidAt()).toString())
                .branchId(event.branchId())
                .branchName(event.branchName())   // denormalizado
                .productSku(item.sku())
                .productName(item.name())          // denormalizado
                .quantity(item.quantity())
                .unitPrice(item.unitPrice())
                .subtotal(item.subtotal())
                .build())
            .toList();

        salesFactRepository.saveAll(facts);
    }
}
```

### APIs de reportes

```
GET /api/v1/reports/sales
    ?from=2026-01-01&to=2026-04-30
    &groupBy=branch                    → ventas por sucursal
    &groupBy=product                   → ventas por producto
    &groupBy=category                  → ventas por categoría
    &format=json|csv|pdf               → formato de salida

GET /api/v1/reports/inventory/current  → snapshot actual de stock
GET /api/v1/reports/loyalty/top-customers?limit=50 → mejores clientes por LTV
GET /api/v1/reports/production/monthly → resumen de producción del mes
```

### Latencia de datos (eventual consistency)

Los datos del `report-service` tienen un retraso de **segundos** respecto a los datos transaccionales, ya que se alimentan de eventos Kafka. Este retraso es **aceptable para reportes** (nadie necesita un reporte en tiempo real al milisegundo), pero debe comunicarse a los usuarios del backoffice.

---

*← [06 — Descripción de Servicios](./06_descripcion-servicios.md) | Siguiente: [08 — Comunicación →](./08_comunicacion.md)*
