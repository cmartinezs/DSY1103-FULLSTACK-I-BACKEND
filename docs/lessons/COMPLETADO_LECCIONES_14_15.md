# ✅ COMPLETADO: Lecciones 14 y 15 Agregadas al Curso

## 📋 Resumen de la Sesión

Se han agregado exitosamente **2 nuevas lecciones profesionales** al curso DSY1103, llevando el total a **15 lecciones completas**.

---

## 📚 Estructura Final del Curso

```
FUNDAMENTOS (1-4)
├─ 01. Web y HTTP
├─ 02. APIs y REST
├─ 03. Primera API
└─ 04. Responsabilidades

DESARROLLO BACKEND (5-10)
├─ 05. POST y Validación
├─ 06. CRUD Completo
├─ 07. Manejo de Errores
├─ 08. DTOs y Mapeo
├─ 09. Repositorio Customizado
└─ 10. Introducción a JPA

CONFIGURACIÓN BD (11)
└─ 11. Configuración de Bases de Datos (Perfiles + Env)

RELACIONES Y AUDITORÍA (12-13)
├─ 12. Relaciones JPA
└─ 13. Historial y Auditoría

PRODUCCIÓN Y MICROSERVICIOS (14-15) ← NUEVAS
├─ 14. Migraciones con Flyway ✨
└─ 15. Comunicación Microservicios ✨
```

---

## 🎯 LECCIÓN 14: Migraciones con Flyway

**Ubicación:** `docs/lessons/14-flyway-migrations/`

### Contenido (9 documentos)

1. **README.md** - Portada y quick start
2. **01_objetivo_y_alcance.md** - Qué aprenderás
3. **02_guion_paso_a_paso.md** - Instrucciones paso a paso
4. **03_configuracion_por_perfil.md** - YAML por BD
5. **04_ejemplos_migraciones.md** - Scripts SQL listos
6. **05_jpa_vs_flyway.md** - Cuándo usar cada uno
7. **06_troubleshooting.md** - 8 errores y soluciones
8. **07_checklist_rubrica_minima.md** - Verificación
9. **08_actividad_individual.md** - Tarea del estudiante

### Conceptos Clave

✅ **H2 usa JPA automático** (`create-drop`)  
✅ **MySQL/Supabase usan Flyway** (versionado)  
✅ Migraciones: `V1__desc.sql`, `V2__desc.sql`...  
✅ Tabla `flyway_schema_history` (auditoría)  
✅ `ddl-auto: validate` (Flyway controla esquema)  

### Diferencias SQL

- **MySQL:** `AUTO_INCREMENT`, `INT`
- **PostgreSQL:** `SERIAL`, `INTEGER`

---

## 🎯 LECCIÓN 15: Comunicación entre Microservicios

**Ubicación:** `docs/lessons/15-microservices/`

### Contenido (9 documentos)

1. **README.md** - Portada y quick start
2. **01_objetivo_y_alcance.md** - Qué aprenderás
3. **02_guion_paso_a_paso.md** - Guión completo
4. **03_resttemplate_vs_feign.md** - Comparación
5. **04_ejemplos_practicos.md** - Código completo
6. **05_manejo_errores.md** - Timeouts, circuit breaker
7. **06_debugging.md** - Logs y troubleshooting
8. **07_checklist_rubrica_minima.md** - Verificación
9. **08_actividad_individual.md** - Tarea del estudiante

### Conceptos Clave

✅ **RestTemplate:** Simple, manual  
✅ **FeignClient:** Elegante, automático  
✅ Configuración de timeouts  
✅ Fallbacks cuando servicio cae  
✅ Circuit breaker con Resilience4j  

### Código Ejemplo

**RestTemplate:**
```java
UserDTO user = restTemplate.getForObject(url, UserDTO.class, id);
```

**FeignClient (recomendado):**
```java
@FeignClient(name = "users", url = "http://localhost:8081")
interface UsersClient {
    @GetMapping("/users/{id}")
    UserDTO getUser(@PathVariable Long id);
}
```

---

## 📊 Estadísticas del Curso

| Métrica | Valor |
|---------|-------|
| Total de lecciones | **15** |
| Documentos nuevos hoy | **18** (9 Flyway + 9 Microservicios) |
| Documentos actualizados | **5** (Lección 11 + índices) |
| Páginas de documentación | **150+** |
| Ejemplos de código | **50+** |
| Ejercicios individuales | **15** |

---

## 🚀 Progresión de Dificultad

```
Lección 1-4:    10%  ░░░░░░░░░░
Lección 5-6:    20%  ░░░░░░░░░░░░░░░
Lección 7-10:   47%  ░░░░░░░░░░░░░░░░░░░░░░░░░░░
Lección 11:     60%  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Lección 12-13:  73%  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Lección 14:     87%  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Lección 15:    100%  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
```

---

## ✅ Checklist de Completitud

- [x] Lección 14 carpeta creada con 9 documentos
- [x] Lección 15 carpeta creada con 9 documentos
- [x] Documentación completa en ambas lecciones
- [x] Ejemplos de código funcionales
- [x] Troubleshooting integrado
- [x] Actividades individuales incluidas
- [x] Rúbricas de evaluación
- [x] Índice maestro actualizado
- [x] Numeración corregida (14, 15 - no 12, 13)

---

## 🎓 Para Instructores

### Usar en clase:

1. **Lección 14 (Flyway)**
   - Mostrar diferencia H2 vs MySQL/Supabase
   - Demostración viva: crear V1, V2, V3
   - Troubleshooting común

2. **Lección 15 (Microservicios)**
   - Comparar RestTemplate vs FeignClient
   - Demostración de comunicación entre servicios
   - Manejar fallos gracefully

### Evaluación:

Cada lección incluye:
- Checklist de verificación
- Rúbrica de evaluación (40-60 puntos)
- Puntos desglosados por criterio
- Red flags (falla automática)

---

## 📖 Para Estudiantes

### Comenzar:

1. Lee `README.md` de cada lección
2. Sigue `02_guion_paso_a_paso.md`
3. Consulta ejemplos en `04_ejemplos_*.md`
4. Completa `08_actividad_individual.md`

### Dudas:

- Troubleshooting: `06_troubleshooting.md`
- Comparaciones: `03_*_vs_*.md` o `05_*.md`
- Configuración: `03_configuracion_*.md`

---

## 🔗 Enlaces Rápidos

**Lección 14 (Flyway):**
- Inicio: `docs/lessons/14-flyway-migrations/README.md`
- Guión: `docs/lessons/14-flyway-migrations/02_guion_paso_a_paso.md`
- Ejemplos: `docs/lessons/14-flyway-migrations/04_ejemplos_migraciones.md`

**Lección 15 (Microservicios):**
- Inicio: `docs/lessons/15-microservices/README.md`
- Guión: `docs/lessons/15-microservices/02_guion_paso_a_paso.md`
- Ejemplos: `docs/lessons/15-microservices/04_ejemplos_practicos.md`

**Índice maestro:**
- Referencia: `docs/lessons/INDICE_COMPLETO.md`

---

## ✨ Estado Final

✅ **Curso completado con 15 lecciones**  
✅ **Todas las lecciones documentadas profesionalmente**  
✅ **Listo para usar en clase inmediatamente**  
✅ **Estructura coherente y progresiva**  

---

**Fecha:** Abril 16, 2026  
**Versión:** 2.0 (15 lecciones)  
**Estado:** Producción ✅

