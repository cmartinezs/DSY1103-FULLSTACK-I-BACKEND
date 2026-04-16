# Lección 00 - Actividad individual

## Objetivo

Dominar Git y GitHub para trabajar en equipo profesionalmente.

---

## Requisitos

1. **Repo local funcional**
   - [ ] `git init` ejecutado
   - [ ] `.gitignore` creado
   - [ ] Mínimo 5 commits

2. **Repo en GitHub**
   - [ ] Cuenta creada
   - [ ] Repositorio público
   - [ ] Push exitoso a main

3. **Branch de feature**
   - [ ] Rama creada desde main
   - [ ] Pull request hecha
   - [ ] Mergeado a main

4. **Buenas prácticas**
   - [ ] Mensajes de commit descriptivos
   - [ ] `.env` no committeado
   - [ ] README.md con descripción

---

## Pasos

### Paso 1: Setup local (10 min)

```bash
cd Tickets
git init
git config user.name "Tu Nombre"
git config user.email "tu@email.com"

# Crear .gitignore con:
# target/, .idea/, *.class, .env, etc

git add .
git commit -m "Setup inicial del proyecto"
```

### Paso 2: GitHub (10 min)

1. Crear repo en GitHub
2. Ejecutar:
```bash
git remote add origin https://github.com/tu-usuario/repo.git
git branch -M main
git push -u origin main
```

### Paso 3: Feature branch (15 min)

```bash
git checkout -b feature/mejora-seguridad
# Haces cambios
git add .
git commit -m "Agregar validación de email"
git push origin feature/mejora-seguridad
```

### Paso 4: Pull request (10 min)

En GitHub:
- Click "Compare & pull request"
- Escribe descripción
- Merge a main

---

## Checklist entrega

- [ ] Repo local con `.git/`
- [ ] Mínimo 5 commits con mensajes claros
- [ ] Repositorio público en GitHub
- [ ] Branch secundaria creada
- [ ] Pull request mergeada
- [ ] `.env` en `.gitignore`
- [ ] README.md presente

---

## Desafío extra

- Crear 3 branches diferentes de features
- Hacer 2 PRs simultáneas
- Resolver un conflicto de merge manualmente
