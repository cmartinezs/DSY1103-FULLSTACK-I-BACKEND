# Spec: Hybrid HTML Viewer para Challenges y Guías

**Fecha:** 2026-06-19  
**Alcance:** `page/src/main.jsx`

---

## Problema

Al abrir un challenge o guía desde el portal, `openHtmlPageInNewTab` crea un `blob:https://...` URL en lugar de usar la URL real estática. Además, el viewer embebido actual usa `srcDoc` (contenido HTML inyectado como string) para la vista de pantalla completa, lo que impide que recursos externos (fuentes, CDN) carguen correctamente en algunos contextos.

Los archivos HTML ya se copian a `page/public/docs/challenges/` y `page/public/docs/guides/` durante el build, por lo que ya son accesibles como URLs reales.

---

## Diseño

### UX Flow

```
Galería (default, sin selectedPage)
  └── Card con miniatura (srcDoc preview escalado — sin cambios)
       ├── [Abrir en portal]  → setSelectedHtmlPageId / setSelectedGuideId
       │     └── HtmlViewer (iframe con src= URL real)
       │          ├── Header: "← Challenges" | título | secciones | "Nueva pestaña"
       │          └── [← Challenges] → clearSelectedHtmlPage()
       └── [Nueva pestaña]   → window.open(staticAssetPath(publicPath))
```

Para challenges multi-página (tic-tac-toe), el header del viewer muestra botones de sección que cambian el `src` del iframe. Para challenges de una página (api-gateway), no hay nav de secciones.

### Componentes afectados

| Componente | Cambio |
|---|---|
| `openHtmlPageInNewTab` | Reemplazar blob con `window.open(staticAssetPath(page.publicPath))` |
| `ChallengeGallery` | Bifurcar: si `selectedPage !== null` → `HtmlViewer`; si no → galería de cards |
| `ChallengeCard` | Eliminar prop `selectedPage`; botones "Abrir en portal" y "Nueva pestaña" van directo a sus handlers |
| `GuidesGallery` | Mismo patrón de bifurcación con `selectedGuide` |
| `GuideCard` | Recibe `onOpen` (viewer) y sigue teniendo "Nueva pestaña" |
| `HtmlViewer` (nuevo) | Componente compartido para challenges y guías |
| `HtmlPageViewer` | Eliminar — era código muerto, nunca se renderizaba en `App` |
| `htmlStandaloneDocument` | Eliminar — solo la usaba `HtmlPageViewer` |

### Componente `HtmlViewer` (nuevo)

Props: `{ page, site, onBack, onSelectPage, backLabel }`

- `page`: el `htmlPage` o guide a mostrar
- `site`: el challenge site (para obtener `site.pages` y renderizar la nav de secciones); `null` para guías
- `onBack`: callback para volver a la galería
- `onSelectPage(pageId)`: callback para cambiar la sección activa (solo challenges multi-página)
- `backLabel`: texto del botón de regreso ("Challenges" o "Guías")

Renderiza:
1. Header sticky: botón `← {backLabel}`, título del page, botones de sección (si `site?.pages.length > 0`), botón "Nueva pestaña"
2. `<iframe src={staticAssetPath(page.publicPath)} />` — full-height, sin `srcDoc`

### Cambios de estado en `App`

| Estado | Antes | Después |
|---|---|---|
| `selectedHtmlPageId` init | `htmlPageFromHash()?.id ?? firstHtmlPage?.id ?? ''` | `htmlPageFromHash()?.id ?? ''` |
| `selectedGuideId` init | `guidePages[0]?.id ?? ''` | `''` |
| `selectedHtmlPage` derivado | Con fallbacks a `filteredChallengeSites[0]?.entry` | `selectedHtmlPageId ? htmlPages.find(...) : null` |
| `selectedGuide` (nuevo) | — | `selectedGuideId ? guidePages.find(...) : null` |

Funciones actualizadas:
- `openChallenges()` → `setSelectedHtmlPageId('')` + limpiar ruta
- `openGuides()` → `setSelectedGuideId('')`
- `clearSelectedHtmlPage()` (nueva) → `setSelectedHtmlPageId('')` + `history.replaceState`

`GuidesGallery` recibe: `guides`, `selectedGuide`, `onSelectGuide`  
`ChallengeGallery` recibe: `sites`, `selectedPage`, `onSelectPage`, `onBack`

### Lo que NO cambia

- `htmlPreviewDocument` + `srcDoc` para las miniaturas de las cards (thumbnails)
- `injectHtmlHead` con `<base>` tag para que los recursos resuelvan en el srcDoc de preview
- `buildChallengeSites` y toda la lógica de agrupamiento
- `firstHtmlPage` se elimina completamente (ya no hay página por defecto)

---

## Archivos modificados

- `page/src/main.jsx` — único archivo afectado

## Sin scope

- No se cambia `generate-content.mjs`
- No se cambia CSS ni estilos existentes más allá de lo necesario para `HtmlViewer`
- No se agrega routing real (hash routing existente se mantiene para challenges)
