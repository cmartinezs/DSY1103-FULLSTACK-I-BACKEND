# Lección 21 — Checklist y Rúbrica Mínima

## Checklist antes de entregar

### Configuración

- [ ] `pom.xml` incluye `spring-boot-starter-test`
- [ ] Las pruebas corren con `mvnw.cmd test`
- [ ] No se requiere levantar MySQL, PostgreSQL ni microservicios externos para pruebas unitarias

### Pruebas unitarias

- [ ] Existe `TicketServiceTest`
- [ ] Usa JUnit 5
- [ ] Usa Mockito con `@ExtendWith(MockitoExtension.class)`
- [ ] Usa `@Mock` para repositorios y clientes externos
- [ ] Usa `@InjectMocks` cuando corresponde
- [ ] Incluye pruebas de caso feliz
- [ ] Incluye pruebas de error
- [ ] Incluye prueba de fallback o falla de microservicio
- [ ] Usa Given When Then
- [ ] Los nombres de pruebas son descriptivos

### Plan de pruebas

- [ ] Existe sección o archivo con plan breve de pruebas unitarias
- [ ] El plan indica alcance y fuera de alcance
- [ ] El plan enumera casos mínimos
- [ ] El plan indica comando de ejecución

---

## Rúbrica

| Criterio | Pts | Evidencia |
|----------|-----|-----------|
| Configuración de test | 10 | Dependencias y ejecución correcta |
| Pruebas de service | 25 | Caso feliz y error en `TicketService` |
| Uso correcto de Mockito | 20 | Mocks, stubs y verify |
| Prueba de microservicio/fallback | 15 | No llama servicios reales |
| Modelo Given When Then | 10 | Pruebas ordenadas y legibles |
| Plan de pruebas unitarias | 10 | README o documento breve |
| Buenas prácticas | 10 | Nombres claros, pruebas independientes |

**Total: 100 puntos**

---

## Red Flags

- Las pruebas unitarias requieren servicios en puertos 8081, 8082, 8084 o 8085
- Las pruebas requieren una base de datos real
- El proyecto no compila
- Las pruebas no tienen assertions
- Solo se prueba el caso feliz
- Se usa `Thread.sleep` para hacer pasar pruebas
- Se ignoran errores con `try/catch` vacío

