# Lección 00 - Flujo completo: Local → GitHub

## Scenario: Trabajar en equipo

```
Tú: trabajas en "feature-security"
Compañero: trabaja en "feature-logging"
Main: siempre estable

    main (v1.0)
     |
     +--- feature-security  (tú)
     |     └─ commit: agregar @PreAuthorize
     |     └─ commit: BCrypt password
     |     └─ push origin feature-security
     |
     +--- feature-logging   (compañero)
           └─ commit: agregar @Slf4j
           └─ push origin feature-logging

    Cuando ambos terminen:
    feature-security → PR → Review → Merge a main
    feature-logging  → PR → Review → Merge a main
    
    main tiene ambas features
```

## Comandos esenciales

```bash
# Ver estado
git status

# Ver historial
git log --oneline

# Ver cambios no commiteados
git diff

# Deshacer cambio (cuidado!)
git checkout -- archivo.java

# Cambiar entre ramas
git checkout main
git checkout feature-x

# Traer cambios del servidor
git pull

# Revertir último commit (sin borrar cambios)
git reset --soft HEAD~1
```

## Buenas prácticas

✅ **DO:**
- Commits pequeños y con propósito
- Mensajes claros: "Agregar validación de email"
- Push al menos 1x por día
- PR antes de merge a main
- Revisar código de otros

❌ **DON'T:**
- Commits gigantes (10 cambios distintos)
- Mensaje vacío: "."
- Trabajar solo en main
- Merge sin revisar
- Commitear .class, .jar, .env

## Conflictos (cuando pasa)

```
Tú cambias línea 10 de UserService.java
Compañero también cambia línea 10

Result: CONFLICT
Solution:
1. Abre archivo
2. Ve <<< CONFLICT >>> y ===
3. Elige cuál quieres (o ambos)
4. git add .
5. git commit -m "Resolver conflicto"
6. git push
```
