# Lección 00 - Troubleshooting

## Problema 1: "fatal: not a git repository"

**Causa:** Ejecutaste `git status` en carpeta sin `.git/`

**Solución:**
```bash
cd Tickets/
git init
```

## Problema 2: "error: src refspec main does not match any"

**Causa:** No hay commits aún

**Solución:**
```bash
git add .
git commit -m "Setup inicial"
git push -u origin main
```

## Problema 3: Cambios perdidos

**Causa:** Editaste archivo pero no commiteaste, cambio de rama

**Solución:**
```bash
git reflog                    # Ver historial
git checkout <commit-hash>    # Recuperar
```

## Problema 4: Quiero deshacer último commit

**Opción 1:** Sin borrar cambios
```bash
git reset --soft HEAD~1
```

**Opción 2:** Borrando cambios
```bash
git reset --hard HEAD~1
```

## Problema 5: Conflicto en merge

**Síntoma:** Archivos con `<<<<<<< HEAD`

**Solución:**
1. Abre el archivo
2. Resuelve manualmente (elige cuál código mantener)
3. `git add .`
4. `git commit -m "Resolver conflicto"`

## Problema 6: Acidental commit de .env

**Prevención:**
```bash
echo ".env" >> .gitignore
```

**Arreglo (si ya lo commitiste):**
```bash
git rm --cached .env
git commit -m "Remove .env from tracking"
```
