# Módulo 04 — Cheat Sheet: los 45 tips de un vistazo

> Referencia rápida con la regla clave de cada tip.  
> Para el desarrollo completo de cada situación consulta los módulos [01](./01_situaciones_basicas.md), [02](./02_situaciones_intermedias.md) y [03](./03_situaciones_api.md).

---

## 🟡 Básico — Consola (Tips 01–15)

| Tip | Situación | Regla de bolsillo |
|-----|-----------|-------------------|
| **01** | Decidir según un valor | `String` → `equals()` nunca `==` · 3+ casos → `switch expression` · muchos pares → `Map` |
| **02** | Repetir hasta que ocurra algo | La variable de control del `while` **siempre** cambia dentro del bucle · usa `break` + `boolean` |
| **03** | Recorrer, buscar o filtrar una lista | `for` siempre `< size` nunca `<=` · no modifiques la lista mientras la recorres · filtra con `stream().filter()` |
| **04** | Validar antes de procesar | Guard clauses al inicio: verificar cada condición inválida y salir con `return`/`throw` · caso feliz al final |
| **05** | String con número → operación incorrecta | `parseInt` / `parseDouble` antes de operar · `.trim()` · `try-catch NumberFormatException` · `+` siempre concatena con String |
| **06** | Combinar varias condiciones en un if | `&&` para "todo debe cumplirse" · `\|\|` para "al menos uno" · `!` para invertir · usa paréntesis al mezclarlos |
| **07** | Verificar si un valor está en un rango | "dentro" → `>= min && <= max` · "fuera" → `< min \|\| > max` · `&&` y `||` no son intercambiables |
| **08** | Contar elementos que cumplen una condición | Contador en `0` **fuera** del bucle · nunca `return`/`break` dentro del contador · `stream().filter().count()` |
| **09** | Sumar valores de una lista | Acumulador en `0.0` **antes** del bucle · declararlo dentro lo reinicia en cada vuelta · `mapToDouble().sum()` |
| **10** | División entera sin decimales | `int / int = int` → castea con `(double)` **antes** de dividir · multiplicar por `100` para porcentaje va **después** del cast |
| **11** | Ejecutar un bloque exactamente N veces | `for (int i = 0; i < N; i++)` → exactamente N veces · si el número visible importa usa `i + 1` · `<` nunca `<=` |
| **12** | Valor que no debe cambiar nunca | `static final` + `NOMBRE_EN_MAYUSCULAS` · un cambio se propaga a todos los sitios automáticamente |
| **13** | Saber si un número es par, múltiplo, etc. | `%` devuelve el **resto** · `n % 2 == 0` es par · `n % N == 0` es múltiplo de N |
| **14** | El mismo bloque copiado en varios lugares | Extraer método: lo que cambia = parámetros · lo que es igual = cuerpo · un cambio, efecto en todos |
| **15** | Guardar varios valores del mismo tipo | `List<T>` en lugar de variables individuales · `new ArrayList<>()` si crece · `List.of()` si es fija |

---

## 🟠 Intermedio — Objetos y Streams (Tips 16–30)

| Tip | Situación | Regla de bolsillo |
|-----|-----------|-------------------|
| **16** | Objetos iguales pero `==` dice distintos | `==` compara referencias · `equals()` compara contenido · `record` lo implementa automáticamente |
| **17** | NullPointerException al usar un resultado | Nunca uses una referencia sin saber si es `null` · usa `Optional` cuando un método puede no encontrar nada |
| **18** | Filtrar, transformar u ordenar una lista de objetos | `filter` reduce · `map` transforma · `sorted` ordena · `groupingBy` agrupa · orden `filter → sorted → map` |
| **19** | Trabajar con fechas | `LocalDate` (solo fecha) · `LocalDateTime` (fecha + hora) · `ChronoUnit.DAYS.between` · nunca `Date` ni `String` |
| **20** | Lógica duplicada en varios métodos | DRY: extrae método privado · si es compartida entre clases → clase utilitaria estática |
| **21** | Magic strings con typos silenciosos | `enum` en lugar de `String` · el compilador garantiza valores válidos · `switch` exhaustivo avisa si falta un caso |
| **22** | Obtener el máximo, mínimo o primero que cumple | `findFirst()` · `max/min(Comparator)` · `anyMatch/allMatch/noneMatch` · todos devuelven `Optional` o `boolean` |
| **23** | Estadísticas de una lista | `summaryStatistics()` calcula todo en un solo recorrido · `groupingBy + counting()` para contar por categoría |
| **24** | Excepción específica para cada error de negocio | Una excepción por tipo de error · hereda de `RuntimeException` · mensaje con los datos del contexto |
| **25** | Devolver más de un valor desde un método | Crea un `record` que agrupe los valores · nunca uses arrays ni listas para esto |
| **26** | Campos opcionales en objetos o peticiones | `Optional<T>` para distinguir "no enviado" de "enviado vacío" · `ifPresent` para aplicar solo si existe |
| **27** | Diferenciar el tipo de error | Captura los tipos más específicos primero · nunca solo `catch (Exception e)` · `try-with-resources` para `Closeable` |
| **28** | Unir propiedades de una lista en un String | `Collectors.joining(sep)` maneja separadores sin comas extras · `String.join()` para listas simples |
| **29** | Agrupar elementos en un Map | `groupingBy` → `Map<K, List<V>>` · `toMap` → `Map<K, V>` · `counting()` para contar por grupo |
| **30** | Reducir toda una lista a un solo valor | `reduce(identidad, operación)` · para suma/max/min usa `mapToInt().sum()/max()/min()` que son más legibles |

---

## 🔴 Avanzado — REST API Spring Boot (Tips 31–45)

| Tip | Situación | Regla de bolsillo |
|-----|-----------|-------------------|
| **31** | Migrar lógica a API bien estructurada | Controller recibe y delega · Service calcula · si el Controller tiene lógica, está mal |
| **32** | Leer y validar datos de la petición | `@PathVariable` para IDs · `@RequestParam` para filtros · `@RequestBody` para objetos · `@Valid` + Bean Validation |
| **33** | Status HTTP correcto y errores claros | `200` OK · `201` Created · `204` No Content · `400` Bad Request · `404` Not Found · `409` Conflict · nunca `try/catch` en Controller |
| **34** | Filtrar y paginar resultados | Filtros siempre `required = false` · `Pageable` para paginar · nunca `findAll()` sin límite en producción |
| **35** | Actualizar solo algunos campos | `PUT` = reemplaza todo · `PATCH` = campos opcionales · `Integer` (no `int`) en DTO de patch para poder ser `null` |
| **36** | URLs, puertos y claves en el código fuente | Todo valor que cambia por entorno va en `application.yml` · secretos en variables de entorno, nunca en el código |
| **37** | Saber qué ocurre en la API cuando algo falla | SLF4J `Logger` · `INFO` para eventos de negocio · `DEBUG` para flujo interno · `ERROR` cuando algo falló · `{}` en lugar de `+` |
| **38** | JSON con fechas en formato incorrecto | `write-dates-as-timestamps: false` en YAML → formato ISO-8601 · `@JsonFormat` solo para formatos personalizados |
| **39** | Consumir datos de otra API externa | `RestClient` (Spring 6.1+) o `RestTemplate` · centraliza en un `@Service` dedicado · maneja siempre la respuesta nula |
| **40** | Path base repetido en cada endpoint | `@RequestMapping` en la clase del Controller · los métodos solo añaden el sufijo · un cambio en un solo lugar |
| **41** | Monitorear el estado de la API | Spring Actuator · `/actuator/health` · expón solo `health` e `info` públicamente en producción |
| **42** | Consultas lentas que siempre devuelven lo mismo | `@Cacheable` guarda el resultado · `@CacheEvict` invalida cuando cambian los datos · `@EnableCaching` para activar |
| **43** | Documentar endpoints automáticamente | SpringDoc OpenAPI · Swagger UI en `/swagger-ui.html` · `@Operation` y `@ApiResponse` para comportamientos no obvios |
| **44** | Ejecutar código antes de llegar al Controller | `HandlerInterceptor` + `WebMvcConfigurer` · `preHandle` devuelve `true` para continuar o `false` para abortar |
| **45** | Tareas en segundo plano sin bloquear la respuesta | `@Async` + `@EnableAsync` · el método `@Async` debe estar en una clase diferente al que lo llama |

---

## Mapa de dependencias entre tips

```
Tip 01 (decidir)
  └── Tip 21 (enum en lugar de magic strings)
        └── Tip 33 (switch en status HTTP correcto)

Tip 02 (repetir)
  └── Tip 17 (NPE al buscar en bucle)
        └── Tip 22 (findFirst / max con Optional)

Tip 03 (listas)
  └── Tip 18 (Streams: filter/map/sorted)
        ├── Tip 28 (joining: unir en String)
        ├── Tip 29 (groupingBy: agrupar en Map)
        ├── Tip 30 (reduce: un solo valor)
        └── Tip 34 (filtrar desde URL + paginar)

Tip 04 (validar)
  └── Tip 20 (DRY: extraer validación)
        └── Tip 32 (Bean Validation en DTO)

Tip 05 (String ↔ número)
  └── Tip 32 (@RequestParam → Spring convierte automáticamente)

Tip 06 (condiciones combinadas)
  └── Tip 07 (rangos: && vs ||)

Tip 08 (contar)
  └── Tip 23 (summaryStatistics: estadísticas completas)

Tip 09 (sumar)
  └── Tip 23 (summaryStatistics incluye suma y promedio)

Tip 12 (constantes)
  └── Tip 36 (valores de configuración en application.yml)

Tip 14 (no repetir código)
  └── Tip 20 (DRY con método privado o clase utilitaria)

Tip 16 (equals vs ==)
  └── Tip 33 (comparar status del ticket al lanzar excepción)

Tip 17 (NPE → Optional)
  └── Tip 26 (Optional en campos opcionales del DTO)

Tip 19 (fechas)
  └── Tip 38 (fechas en JSON con @JsonFormat)

Tip 24 (excepciones de negocio)
  └── Tip 33 (@RestControllerAdvice → status HTTP por tipo)

Tip 31 (estructura de capas)
  └── Tip 37 (logging por capa: info en Service, error en Advice)
        └── Tip 44 (interceptor para logging transversal)

Tip 36 (configuración externa)
  └── Tip 42 (caché configurable por entorno)
```

---

## Hoja de referencia rápida — Anotaciones Spring Boot

| Anotación | Dónde va | Para qué |
|-----------|----------|----------|
| `@RestController` | Clase | Marca como Controller REST (retorna JSON) |
| `@RequestMapping` | Clase | Path base compartido por todos los métodos |
| `@GetMapping` | Método | Responde a HTTP GET |
| `@PostMapping` | Método | Responde a HTTP POST |
| `@PutMapping` | Método | Responde a HTTP PUT |
| `@PatchMapping` | Método | Responde a HTTP PATCH |
| `@DeleteMapping` | Método | Responde a HTTP DELETE |
| `@PathVariable` | Parámetro | Extrae un segmento de la URL `/tickets/{id}` |
| `@RequestParam` | Parámetro | Extrae parámetro de query string `?status=ABIERTO` |
| `@RequestBody` | Parámetro | Deserializa el JSON del cuerpo de la petición |
| `@Valid` | Parámetro | Activa la validación Bean Validation en el objeto |
| `@Service` | Clase | Marca como componente de lógica de negocio |
| `@RestControllerAdvice` | Clase | Manejador global de excepciones |
| `@ExceptionHandler` | Método | Asocia un tipo de excepción con un handler |
| `@Value` | Campo | Inyecta un valor de `application.yml` |
| `@ConfigurationProperties` | Clase/Record | Agrupa propiedades de `application.yml` |
| `@Cacheable` | Método | Cachea el resultado según los parámetros |
| `@CacheEvict` | Método | Invalida el caché al modificar datos |
| `@EnableCaching` | Clase principal | Activa el sistema de caché |
| `@Async` | Método | Ejecuta en hilo separado (no bloquea) |
| `@EnableAsync` | Clase principal | Activa la ejecución asíncrona |

---

*Última actualización: Marzo 2026 — DSY1103 Fullstack I Backend*
