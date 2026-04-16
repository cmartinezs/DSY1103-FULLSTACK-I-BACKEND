# Lección 00 - Git & GitHub: ¿Qué vas a aprender?

## ¿De dónde venimos?

Eres nuevo en programación profesional. Hasta ahora trabajaste solo en tu computadora.

Problema: sin versionado, cuando colaboras con otros:
- No sabes qué cambió
- No puedes revertir errores
- Los cambios se pierden
- No hay historial

---

## ¿Qué vas a construir?

Al terminar esta lección:

1. **Crear un repositorio Git local**
2. **Hacer commits** (fotos del código)
3. **Subir a GitHub** (servidor)
4. **Trabajar con branches** (paralelo a main)
5. **Hacer pull requests** (colaboración)

### Flujo real en equipo

```
Tú creas rama "feature-x"
    ↓
Haces 3 commits con cambios
    ↓
Subes a GitHub (git push)
    ↓
Creas Pull Request
    ↓
Compañero revisa código
    ↓
Si está bien → "Merge a main"
    ↓
main se actualiza automáticamente
```

---

## Requerimientos

| ID | Requerimiento |
|----|---------------|
| **REQ-G01** | Repo local funcional con `.git/` |
| **REQ-G02** | Mínimo 5 commits con mensajes descriptivos |
| **REQ-G03** | Repo público en GitHub |
| **REQ-G04** | Branch secundaria + pull request |
| **REQ-G05** | Entender `.gitignore` |

---

## Estructura

```
Antes:
└── Código local sin historial

Después:
├── .git/                    (historial)
├── .gitignore              (qué ignorar)
├── README.md               (documentación)
└── Tu código               (rastreado)

Servidor (GitHub):
└── Tu repositorio público (código + historial)
```

---

## No cubre esta lección

- Git avanzado (rebase, cherry-pick)
- CI/CD (automatización)
- Git hooks

Foco: **flujo básico para trabajo en equipo**.
