# 🌶️ Lombok — Reducción de Código Boilerplate en Java

## ¿Qué es Lombok?

**Project Lombok** es una librería Java que usa **procesamiento de anotaciones** en tiempo de compilación para generar automáticamente código repetitivo (*boilerplate*): getters, setters, constructores, `equals`, `hashCode`, `toString`, builders, y más.

> 💡 El código generado por Lombok **no aparece en tus archivos fuente**, pero sí está presente en el bytecode compilado (`.class`). El IDE lo reconoce gracias a un plugin.

---

## ¿Por qué usar Lombok?

Sin Lombok, una clase como `Ticket` requiere escribir manualmente decenas de líneas:

```java
// ❌ Sin Lombok — código tedioso y propenso a errores
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;

    public Ticket(Long id, String title, String description, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

Con Lombok, el mismo resultado se logra así:

```java
// ✅ Con Lombok — conciso y mantenible
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
}
```

> 📌 Este es exactamente el modelo `Ticket.java` que usa este proyecto.

---

## Configuración en Maven

Lombok se agrega como dependencia en el `pom.xml`. En **Spring Boot**, también se debe registrar como procesador de anotaciones:

```xml
<!-- Dependencia principal (opcional = no se empaqueta en el JAR final) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Procesador de anotaciones en el compilador Maven -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

> ⚠️ Asegúrate de tener instalado el **plugin de Lombok en tu IDE** (IntelliJ IDEA lo soporta de forma nativa desde la versión 2020.3+).

---

## Anotaciones principales

### `@Getter` y `@Setter`

Generan automáticamente los métodos `getXxx()` y `setXxx()` para todos los campos de la clase (o solo para el campo anotado).

```java
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ticket {
    private Long id;
    private String title;
    private String status;
}

// Lombok genera:
// getId(), setId(Long id)
// getTitle(), setTitle(String title)
// getStatus(), setStatus(String status)
```

**A nivel de campo** (más granular):

```java
public class Ticket {
    @Getter @Setter
    private Long id;

    @Getter           // solo lectura
    private String title;

    private String internalNote; // sin getter ni setter
}
```

---

### `@NoArgsConstructor`

Genera un constructor sin parámetros. Requerido por JPA, Jackson y otros frameworks.

```java
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Ticket {
    private Long id;
    private String title;
}

// Lombok genera:
// public Ticket() {}
```

---

### `@AllArgsConstructor`

Genera un constructor con **todos** los campos como parámetros, en el orden en que están declarados.

```java
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
}

// Lombok genera:
// public Ticket(Long id, String title, String description, String status) { ... }
```

---

### `@RequiredArgsConstructor`

Genera un constructor solo con los campos marcados como `final` o con `@NonNull`. Muy usado en Spring Boot para **inyección de dependencias por constructor**.

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository; // campo final → incluido

    // Lombok genera:
    // public TicketService(TicketRepository ticketRepository) {
    //     this.ticketRepository = ticketRepository;
    // }
}
```

> 🌟 **Este es el patrón recomendado** para inyección de dependencias en Spring Boot. Evita usar `@Autowired` en el campo.

---

### `@Data`

**Atajo todo-en-uno**: equivale a combinar `@Getter` + `@Setter` + `@RequiredArgsConstructor` + `@ToString` + `@EqualsAndHashCode`.

```java
import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String title;
    private String status;
}

// Lombok genera: getters, setters, toString(), equals(), hashCode() y constructor
```

> ⚠️ **Precaución con `@Data` en entidades JPA**: el `equals()` y `hashCode()` generados pueden causar problemas con Hibernate si no se configuran correctamente. Prefiere usar `@Getter`, `@Setter` y `@EqualsAndHashCode(of = "id")` por separado en entidades.

---

### `@Builder`

Implementa el **patrón de diseño Builder**, permitiendo construir objetos de forma legible y flexible.

```java
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
}

// Uso:
Ticket ticket = Ticket.builder()
    .id(1L)
    .title("Error en login")
    .description("El usuario no puede iniciar sesión")
    .status("ABIERTO")
    .build();
```

> 💡 Muy útil en tests y en la construcción de objetos de respuesta complejos.

---

### `@ToString`

Genera el método `toString()` incluyendo todos los campos (o solo los seleccionados).

```java
import lombok.ToString;

@ToString
public class Ticket {
    private Long id;
    private String title;
    private String status;
}

// Lombok genera:
// public String toString() {
//     return "Ticket(id=" + id + ", title=" + title + ", status=" + status + ")";
// }
```

**Excluir campos sensibles:**

```java
@ToString(exclude = "password")
public class Usuario {
    private Long id;
    private String email;
    private String password; // no aparecerá en toString()
}
```

---

### `@EqualsAndHashCode`

Genera los métodos `equals()` y `hashCode()` basados en los campos de la clase.

```java
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id") // solo compara por id
public class Ticket {
    private Long id;
    private String title;
    private String status;
}
```

> 📌 En entidades JPA, siempre especifica `of = "id"` para evitar comparaciones incorrectas entre objetos con los mismos datos pero diferente identidad de base de datos.

---

### `@Slf4j`

Inyecta automáticamente un **logger** de SLF4J (`log`) en la clase, listo para usar. Elimina la necesidad de declarar el logger manualmente.

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketService {

    public Ticket buscarPorId(Long id) {
        log.info("Buscando ticket con id: {}", id);
        // lógica...
        log.debug("Ticket encontrado: {}", ticket);
        return ticket;
    }
}

// Sin Lombok, necesitarías escribir:
// private static final Logger log = LoggerFactory.getLogger(TicketService.class);
```

---

### `@NonNull`

Genera una verificación `null` al inicio del método o constructor, lanzando `NullPointerException` con un mensaje descriptivo si el valor es nulo.

```java
import lombok.NonNull;

public class TicketService {
    public void crear(@NonNull Ticket ticket) {
        // Lombok genera al inicio: if (ticket == null) throw new NullPointerException("ticket is marked non-null but is null");
        // lógica...
    }
}
```

---

## Tabla resumen de anotaciones

| Anotación | Lo que genera | Uso típico |
|-----------|---------------|------------|
| `@Getter` | Métodos `getXxx()` | Modelos, DTOs, entidades |
| `@Setter` | Métodos `setXxx()` | Modelos, DTOs |
| `@NoArgsConstructor` | Constructor vacío | Entidades JPA, deserialización JSON |
| `@AllArgsConstructor` | Constructor con todos los campos | Modelos simples |
| `@RequiredArgsConstructor` | Constructor con campos `final`/`@NonNull` | Servicios, controllers (DI) |
| `@Data` | Getters + Setters + Constructor + equals + hashCode + toString | DTOs simples |
| `@Builder` | Patrón Builder | Construcción de objetos complejos |
| `@ToString` | Método `toString()` | Depuración y logging |
| `@EqualsAndHashCode` | Métodos `equals()` y `hashCode()` | Entidades, colecciones |
| `@Slf4j` | Logger SLF4J (`log`) | Services, controllers |
| `@NonNull` | Validación de nulos | Parámetros críticos |

---

## Lombok en el proyecto Tickets

El proyecto usa las siguientes anotaciones sobre el modelo `Ticket`:

```java
package cl.duoc.fullstack.tickets.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter           // genera getId(), getTitle(), getDescription(), getStatus()
@Setter           // genera setId(), setTitle(), setDescription(), setStatus()
@AllArgsConstructor // genera Ticket(Long id, String title, String description, String status)
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private String status;
}
```

**Evolución sugerida** a medida que el proyecto crece:

```java
// Con persistencia JPA y logging
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor          // requerido por JPA
@AllArgsConstructor
@ToString(exclude = "descripcion")
@EqualsAndHashCode(of = "id")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;
}
```

---

## Buenas prácticas

| ✅ Hacer | ❌ Evitar |
|---------|---------|
| Usar `@RequiredArgsConstructor` + `final` para inyección de dependencias | Usar `@Autowired` en campos |
| Usar `@EqualsAndHashCode(of = "id")` en entidades JPA | Usar `@Data` en entidades JPA sin configuración |
| Usar `@Slf4j` para logging | Declarar loggers manualmente |
| Usar `@Builder` para objetos con muchos campos opcionales | Usar constructores con 6+ parámetros |
| Excluir campos sensibles con `@ToString(exclude = "...")` | Loguear contraseñas o tokens accidentalmente |

---

## Recursos recomendados

| Recurso | Tipo | Enlace |
|---------|------|--------|
| Documentación oficial de Lombok | 📖 Oficial | [projectlombok.org/features](https://projectlombok.org/features/) |
| Lombok + Spring Boot (Baeldung) | 📄 Artículo | [baeldung.com/intro-to-project-lombok](https://www.baeldung.com/intro-to-project-lombok) |
| Plugin Lombok para IntelliJ IDEA | 🔧 Plugin | [plugins.jetbrains.com/plugin/6317-lombok](https://plugins.jetbrains.com/plugin/6317-lombok) |
| Lombok + JPA: precauciones | 📄 Artículo | [baeldung.com/lombok-entity](https://www.baeldung.com/lombok-entity) |

---

*[← Volver a Extras](../README.md)*

