# Hybrid HTML Viewer (Challenges + Guías) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reemplazar blob URLs y srcDoc-en-viewer con iframes reales (`src=`) servidos desde `page/public/`, y agregar botón "Abrir en portal" que abre el HTML embebido dentro del portal.

**Architecture:** Todo el trabajo ocurre en `page/src/main.jsx`. Los archivos HTML ya se copian a `page/public/docs/challenges/` y `page/public/docs/guides/` durante el build/dev (`generate-content.mjs`), así que son accesibles como URLs reales. El viewer embebido (`HtmlViewer`) usa `<iframe src={staticAssetPath(page.publicPath)}>`. Las miniaturas de preview en las cards siguen usando `srcDoc` (comportamiento actual, no cambia).

**Tech Stack:** React 18, Vite (`base: './'`), Tailwind CSS, `lucide-react` para iconos.

## Global Constraints

- Solo se modifica `page/src/main.jsx` — no se toca `generate-content.mjs`, CSS ni otros archivos.
- `staticAssetPath(path)` ya existe en el archivo; retorna `./path` con el `BASE_URL` de Vite. Usarla para todo URL de recurso estático.
- Las miniaturas de preview (`srcDoc={htmlPreviewDocument(...)}`) NO se cambian — el usuario las aprobó como están.
- Tailwind classes: seguir el mismo patrón visual existente (emerald para acciones primarias, zinc para secundarias).
- No agregar dependencias nuevas.

---

### Task 1: Corregir blob URL y eliminar código muerto

**Files:**
- Modify: `page/src/main.jsx` — funciones `openHtmlPageInNewTab`, `htmlStandaloneDocument`, componente `HtmlPageViewer`

**Interfaces:**
- Produce: `openHtmlPageInNewTab(page)` — abre `staticAssetPath(page.publicPath)` en nueva pestaña

- [ ] **Paso 1: Reemplazar `openHtmlPageInNewTab` (línea ~1555)**

Buscar:
```js
function openHtmlPageInNewTab(page) {
  if (!page?.content) return;
  const blob = new Blob([htmlStandaloneDocument(page)], { type: 'text/html' });
  const url = URL.createObjectURL(blob);
  const opened = window.open(url, '_blank', 'noopener,noreferrer');
  if (!opened) {
    URL.revokeObjectURL(url);
    return;
  }
  window.setTimeout(() => URL.revokeObjectURL(url), 60_000);
}
```

Reemplazar con:
```js
function openHtmlPageInNewTab(page) {
  if (!page?.publicPath) return;
  window.open(staticAssetPath(page.publicPath), '_blank', 'noopener,noreferrer');
}
```

- [ ] **Paso 2: Eliminar `htmlStandaloneDocument` (línea ~1541)**

Buscar y eliminar completo:
```js
function htmlStandaloneDocument(page) {
  if (!page?.content) return '';
  return injectHtmlHead(page.content, page);
}
```

- [ ] **Paso 3: Eliminar componente `HtmlPageViewer` (línea ~1142)**

Buscar y eliminar completo:
```js
function HtmlPageViewer({ page }) {
  if (!page) return <EmptyState text="No hay pagina seleccionada." />;

  return (
    <article className="overflow-hidden rounded-md border border-zinc-200 bg-white shadow-panel">
      <ViewerHeader icon={ExternalLink} title={page.title} subtitle={page.path} />
      <div className="border-b border-zinc-200 bg-stone-50 px-5 py-3">
        <button
          type="button"
          className="inline-flex items-center gap-2 rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm font-semibold text-zinc-700 transition hover:border-emerald-500 hover:text-emerald-700"
          onClick={() => openHtmlPageInNewTab(page)}
        >
          <ExternalLink size={16} />
          Abrir en pestaña
        </button>
      </div>
      <iframe
        key={page.publicPath}
        title={page.title}
        srcDoc={htmlStandaloneDocument(page)}
        className="html-page-frame"
      />
    </article>
  );
}
```

- [ ] **Paso 4: Verificar que el servidor arranca sin errores**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND/page
npm run dev
```

Abrir `http://localhost:5173`. El portal debe cargar. Ir a Challenges, hacer clic en "Abrir en pestaña" en cualquier card — debe abrir `http://localhost:5173/docs/challenges/...` (NO `blob:http://...`).

- [ ] **Paso 5: Commit**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND
git add page/src/main.jsx
git commit -m "refactor: fix blob URL and remove dead HtmlPageViewer code"
```

---

### Task 2: Actualizar estado en `App` y funciones de navegación

**Files:**
- Modify: `page/src/main.jsx` — bloque de `useState` en `App`, funciones `openChallenges`/`openGuides`, derivación de `selectedHtmlPage`/`selectedGuide`

**Interfaces:**
- Produce: `selectedHtmlPage: htmlPage | null` — `null` cuando no hay challenge seleccionado
- Produce: `selectedGuide: guide | null` — `null` cuando no hay guía seleccionada
- Produce: `clearSelectedHtmlPage()` — limpia selección y URL hash

- [ ] **Paso 1: Eliminar `firstHtmlPage` (línea ~80)**

Buscar:
```js
const firstHtmlPage =
  htmlPages.find((page) => page.path === 'docs/challenges/tic-tac-toe/index.html') ??
  htmlPages.find((page) => page.path.endsWith('/index.html')) ??
  htmlPages[0];
```

Eliminarlo completo (dejar solo `const challengeSites = buildChallengeSites(htmlPages);` y lo que sigue).

- [ ] **Paso 2: Cambiar estado inicial de `selectedHtmlPageId` y `selectedGuideId` (línea ~93)**

Buscar:
```js
const [selectedHtmlPageId, setSelectedHtmlPageId] = useState(htmlPageFromHash()?.id ?? firstHtmlPage?.id ?? '');
const [selectedGuideId, setSelectedGuideId] = useState(guidePages[0]?.id ?? '');
```

Reemplazar con:
```js
const [selectedHtmlPageId, setSelectedHtmlPageId] = useState(htmlPageFromHash()?.id ?? '');
const [selectedGuideId, setSelectedGuideId] = useState('');
```

- [ ] **Paso 3: Reemplazar derivación de `selectedHtmlPage` y agregar `selectedGuide` (línea ~184)**

Buscar:
```js
  const selectedHtmlPage =
    htmlPages.find((page) => page.id === selectedHtmlPageId) ??
    filteredChallengeSites[0]?.entry ??
    htmlPages[0];
```

Reemplazar con:
```js
  const selectedHtmlPage = selectedHtmlPageId
    ? (htmlPages.find((page) => page.id === selectedHtmlPageId) ?? null)
    : null;

  const selectedGuide = selectedGuideId
    ? (guidePages.find((g) => g.id === selectedGuideId) ?? null)
    : null;
```

- [ ] **Paso 4: Actualizar `openChallenges`, `openGuides` y agregar `clearSelectedHtmlPage` (línea ~224)**

Buscar:
```js
  function openChallenges() {
    setMode('challenges');
    replaceRoute(selectedHtmlPage?.path ?? firstHtmlPage?.path ?? 'docs/challenges/README.md');
  }

  function openGuides() {
    setMode('guides');
  }
```

Reemplazar con:
```js
  function openChallenges() {
    setMode('challenges');
    setSelectedHtmlPageId('');
    window.history.replaceState(null, '', `${window.location.pathname}${window.location.search}`);
  }

  function openGuides() {
    setMode('guides');
    setSelectedGuideId('');
  }

  function clearSelectedHtmlPage() {
    setSelectedHtmlPageId('');
    window.history.replaceState(null, '', `${window.location.pathname}${window.location.search}`);
  }
```

- [ ] **Paso 5: Verificar en el navegador**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND/page
npm run dev
```

- Ir a tab **Challenges** → debe mostrar la galería de cards (no un viewer).
- Ir a tab **Guías** → debe mostrar la galería de guías.
- Volver a Inicio → OK.

- [ ] **Paso 6: Commit**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND
git add page/src/main.jsx
git commit -m "refactor: update App state for gallery-first challenge and guide navigation"
```

---

### Task 3: Agregar componente `HtmlViewer`

**Files:**
- Modify: `page/src/main.jsx` — insertar nuevo componente `HtmlViewer` antes de `ChallengeGallery`

**Interfaces:**
- Consumes:
  - `page: { publicPath: string, title: string, id: string }` — el challenge page o guía a mostrar
  - `site: { pages: page[] } | null` — challenge site para nav de secciones; `null` = sin secciones
  - `onBack: () => void` — volver a la galería
  - `onSelectPage: (pageId: string) => void` — cambiar sección activa
  - `backLabel: string` — texto del botón regreso ("Challenges" o "Guías")
- Produce: componente `HtmlViewer` — iframe full-height con header de navegación

- [ ] **Paso 1: Insertar `HtmlViewer` antes de la función `ChallengeGallery`**

Buscar la línea:
```js
function ChallengeGallery({ sites, selectedPage, onSelectPage }) {
```

Insertar ANTES de esa línea:
```jsx
function HtmlViewer({ page, site, onBack, onSelectPage, backLabel }) {
  return (
    <div
      className="flex flex-col overflow-hidden rounded-md border border-zinc-200 bg-white shadow-panel"
      style={{ height: 'calc(100vh - 160px)' }}
    >
      <div className="flex shrink-0 flex-wrap items-center gap-2 border-b border-zinc-200 px-4 py-3">
        <button
          type="button"
          onClick={onBack}
          className="flex items-center gap-1.5 rounded-md border border-zinc-300 bg-white px-3 py-1.5 text-sm font-medium text-zinc-700 transition hover:border-emerald-500 hover:text-emerald-700"
        >
          ← {backLabel}
        </button>
        <span className="min-w-0 flex-1 truncate text-sm font-semibold text-zinc-900">{page.title}</span>
        <div className="flex flex-wrap items-center gap-2">
          {site?.pages.map((p) => (
            <button
              key={p.id}
              type="button"
              onClick={() => onSelectPage(p.id)}
              className={`rounded-md border px-2.5 py-1 text-xs font-semibold transition ${
                p.id === page.id
                  ? 'border-emerald-500 bg-emerald-50 text-emerald-800'
                  : 'border-zinc-200 bg-white text-zinc-600 hover:border-emerald-400 hover:text-emerald-700'
              }`}
            >
              {shortHtmlPageTitle(p)}
            </button>
          ))}
          <button
            type="button"
            onClick={() => openHtmlPageInNewTab(page)}
            className="flex items-center gap-1.5 rounded-md border border-zinc-300 bg-white px-3 py-1.5 text-sm font-medium text-zinc-700 transition hover:border-emerald-500 hover:text-emerald-700"
          >
            <ExternalLink size={15} />
            Nueva pestaña
          </button>
        </div>
      </div>
      <iframe
        key={page.publicPath}
        src={staticAssetPath(page.publicPath)}
        title={page.title}
        className="min-h-0 w-full flex-1 border-0"
      />
    </div>
  );
}

```

- [ ] **Paso 2: Verificar compilación**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND/page
npm run dev
```

El portal carga sin errores en consola. El componente `HtmlViewer` no se usa todavía, pero no debe haber errores de parse.

- [ ] **Paso 3: Commit**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND
git add page/src/main.jsx
git commit -m "feat: add HtmlViewer component with real src= iframe"
```

---

### Task 4: Refactorizar `ChallengeGallery`, `ChallengeCard` y conectar en `App`

**Files:**
- Modify: `page/src/main.jsx` — `ChallengeGallery`, `ChallengeCard`, render de `App`

**Interfaces:**
- Consumes (ChallengeGallery): `{ sites, selectedPage, onSelectPage, onBack }`
- Consumes (ChallengeCard): `{ site, onSelectPage }`

- [ ] **Paso 1: Reemplazar `ChallengeGallery` completa**

Buscar y reemplazar la función `ChallengeGallery` completa (desde `function ChallengeGallery` hasta su `}` de cierre, antes de `function ChallengeCard`):

```jsx
function ChallengeGallery({ sites, selectedPage, onSelectPage, onBack }) {
  if (sites.length === 0) return <EmptyState text="No hay challenges para la busqueda actual." />;

  if (selectedPage) {
    const site = sites.find(
      (s) => s.entry.id === selectedPage.id || s.pages.some((p) => p.id === selectedPage.id),
    );
    return (
      <HtmlViewer
        page={selectedPage}
        site={site}
        onBack={onBack}
        onSelectPage={onSelectPage}
        backLabel="Challenges"
      />
    );
  }

  return (
    <div className="space-y-4">
      <section className="rounded-md border border-zinc-200 bg-white p-5 shadow-panel">
        <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-emerald-700">docs/challenges</p>
            <h2 className="mt-1 text-2xl font-semibold leading-tight text-zinc-950">Mini sitios evaluativos</h2>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-zinc-600">
              Cada challenge se publica como un sitio HTML independiente. Las tarjetas muestran una vista previa real,
              resumen y accesos directos a sus secciones internas.
            </p>
          </div>
          <span className="rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm font-medium text-zinc-600">
            {sites.length} mini sitio{sites.length === 1 ? '' : 's'}
          </span>
        </div>
      </section>

      <div className="grid gap-4 xl:grid-cols-2">
        {sites.map((site) => (
          <ChallengeCard key={site.id} site={site} onSelectPage={onSelectPage} />
        ))}
      </div>
    </div>
  );
}
```

- [ ] **Paso 2: Reemplazar `ChallengeCard` completa**

Buscar y reemplazar la función `ChallengeCard` completa (desde `function ChallengeCard` hasta su `}` de cierre):

```jsx
function ChallengeCard({ site, onSelectPage }) {
  return (
    <article className="overflow-hidden rounded-md border border-zinc-200 bg-white shadow-panel">
      <div className="aspect-[16/9] border-b border-zinc-200 bg-zinc-950">
        <iframe
          title={`Preview ${site.title}`}
          srcDoc={htmlPreviewDocument(site.entry)}
          className="challenge-preview-frame"
          tabIndex={-1}
        />
      </div>
      <div className="space-y-4 p-4">
        <div>
          <h3 className="text-lg font-semibold leading-tight text-zinc-950">{site.title}</h3>
          <p className="mt-1 break-all text-xs text-zinc-500">{site.path}</p>
          <p className="mt-3 line-clamp-3 text-sm leading-6 text-zinc-700">{site.summary}</p>
        </div>

        {site.pages.length > 0 && (
          <div>
            <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-zinc-500">Secciones</p>
            <div className="flex flex-wrap gap-2">
              {site.pages.map((page) => (
                <button
                  key={page.id}
                  type="button"
                  onClick={() => onSelectPage(page.id)}
                  className="rounded-md border border-zinc-200 bg-white px-2.5 py-1.5 text-xs font-semibold text-zinc-600 transition hover:border-emerald-400 hover:text-emerald-700"
                >
                  {shortHtmlPageTitle(page)}
                </button>
              ))}
            </div>
          </div>
        )}

        <div className="flex flex-wrap items-center gap-2 border-t border-zinc-200 pt-3">
          <button
            type="button"
            onClick={() => onSelectPage(site.entry.id)}
            className="inline-flex items-center gap-2 rounded-md bg-emerald-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            <ExternalLink size={16} />
            Abrir en portal
          </button>
          <button
            type="button"
            onClick={() => openHtmlPageInNewTab(site.entry)}
            className="inline-flex items-center gap-2 rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm font-semibold text-zinc-700 transition hover:border-emerald-500 hover:text-emerald-700"
          >
            Nueva pestaña
          </button>
        </div>
      </div>
    </article>
  );
}
```

- [ ] **Paso 3: Agregar `onBack` al render de `ChallengeGallery` en `App`**

Buscar en el JSX de `App`:
```jsx
          ) : mode === 'challenges' ? (
            <ChallengeGallery
              sites={filteredChallengeSites}
              selectedPage={selectedHtmlPage}
              onSelectPage={navigateToHtmlPage}
            />
```

Reemplazar con:
```jsx
          ) : mode === 'challenges' ? (
            <ChallengeGallery
              sites={filteredChallengeSites}
              selectedPage={selectedHtmlPage}
              onSelectPage={navigateToHtmlPage}
              onBack={clearSelectedHtmlPage}
            />
```

- [ ] **Paso 4: Verificar flujo completo de challenges**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND/page
npm run dev
```

Verificar:
1. Tab "Challenges" → galería de cards.
2. Clic en "Abrir en portal" → aparece `HtmlViewer` con iframe real (URL en barra de estado del browser = `http://localhost:5173/docs/challenges/...`).
3. Botón "← Challenges" → regresa a galería.
4. Para tic-tac-toe: aparecen botones de sección en el header del viewer.
5. Clic en sección → iframe carga esa sección.
6. Botón "Nueva pestaña" → abre `http://localhost:5173/docs/challenges/...` (sin blob).
7. Clic en sección dentro de la galería (pill en card) → entra al viewer en esa sección.

- [ ] **Paso 5: Commit**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND
git add page/src/main.jsx
git commit -m "feat: refactor ChallengeGallery with embedded HtmlViewer and real URLs"
```

---

### Task 5: Refactorizar `GuidesGallery`, `GuideCard` y conectar en `App`

**Files:**
- Modify: `page/src/main.jsx` — `GuidesGallery`, `GuideCard`, render de `App`

**Interfaces:**
- Consumes (GuidesGallery): `{ guides, selectedGuide, onSelectGuide }`
- Consumes (GuideCard): `{ guide, onOpen }`

- [ ] **Paso 1: Reemplazar `GuidesGallery` completa**

Buscar y reemplazar la función `GuidesGallery` completa (desde `function GuidesGallery` hasta su `}` de cierre, antes de `function GuideCard`):

```jsx
function GuidesGallery({ guides, selectedGuide, onSelectGuide }) {
  if (guides.length === 0) return <EmptyState text="No hay guías disponibles." />;

  if (selectedGuide) {
    return (
      <HtmlViewer
        page={selectedGuide}
        site={null}
        onBack={() => onSelectGuide('')}
        onSelectPage={() => {}}
        backLabel="Guías"
      />
    );
  }

  return (
    <div className="space-y-4">
      <section className="rounded-md border border-zinc-200 bg-white p-5 shadow-panel">
        <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-emerald-700">docs/guides</p>
            <h2 className="mt-1 text-2xl font-semibold leading-tight text-zinc-950">Guías de referencia</h2>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-zinc-600">
              Documentos HTML independientes con referencia técnica y guías de uso para el ramo.
            </p>
          </div>
          <span className="rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm font-medium text-zinc-600">
            {guides.length} guía{guides.length === 1 ? '' : 's'}
          </span>
        </div>
      </section>
      <div className="grid gap-4 xl:grid-cols-2">
        {guides.map((guide) => (
          <GuideCard key={guide.id} guide={guide} onOpen={() => onSelectGuide(guide.id)} />
        ))}
      </div>
    </div>
  );
}
```

- [ ] **Paso 2: Reemplazar `GuideCard` completa**

Buscar y reemplazar la función `GuideCard` completa (desde `function GuideCard` hasta su `}` de cierre):

```jsx
function GuideCard({ guide, onOpen }) {
  return (
    <article className="overflow-hidden rounded-md border border-zinc-200 bg-white shadow-panel">
      <div className="aspect-[16/9] border-b border-zinc-200 bg-zinc-950">
        <iframe
          title={`Preview ${guide.title}`}
          srcDoc={htmlPreviewDocument(guide)}
          className="challenge-preview-frame"
          tabIndex={-1}
        />
      </div>
      <div className="space-y-4 p-4">
        <div>
          <h3 className="text-lg font-semibold leading-tight text-zinc-950">{guide.title}</h3>
          <p className="mt-1 break-all text-xs text-zinc-500">{guide.path}</p>
          <p className="mt-3 line-clamp-3 text-sm leading-6 text-zinc-700">{guide.summary}</p>
        </div>
        <div className="flex flex-wrap items-center gap-2 border-t border-zinc-200 pt-3">
          <button
            type="button"
            onClick={onOpen}
            className="inline-flex items-center gap-2 rounded-md bg-emerald-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            <ExternalLink size={16} />
            Abrir en portal
          </button>
          <button
            type="button"
            onClick={() => openHtmlPageInNewTab(guide)}
            className="inline-flex items-center gap-2 rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm font-semibold text-zinc-700 transition hover:border-emerald-500 hover:text-emerald-700"
          >
            Nueva pestaña
          </button>
        </div>
      </div>
    </article>
  );
}
```

- [ ] **Paso 3: Agregar `selectedGuide` y `onSelectGuide` al render de `GuidesGallery` en `App`**

Buscar en el JSX de `App`:
```jsx
          ) : mode === 'guides' ? (
            <GuidesGallery guides={filteredGuides} />
```

Reemplazar con:
```jsx
          ) : mode === 'guides' ? (
            <GuidesGallery
              guides={filteredGuides}
              selectedGuide={selectedGuide}
              onSelectGuide={setSelectedGuideId}
            />
```

- [ ] **Paso 4: Verificar flujo completo de guías**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND/page
npm run dev
```

Verificar:
1. Tab "Guías" → galería de cards de guías.
2. Clic en "Abrir en portal" → aparece `HtmlViewer` con iframe real.
3. Botón "← Guías" → regresa a galería de guías.
4. Botón "Nueva pestaña" → abre URL real (sin blob).
5. Clic en tab "Guías" estando en el viewer → vuelve a galería (por `openGuides` que llama `setSelectedGuideId('')`).

- [ ] **Paso 5: Verificar también el HomePortal**

En el Inicio (`mode === 'home'`), el challenge destacado tiene botones de sección que llaman `onOpenChallenge(page.id)` → `navigateToHtmlPage(pageId)` → modo challenges + viewer. Verificar que funciona: clic en un mini-link del challenge destacado → navega a challenges en viewer mode.

- [ ] **Paso 6: Commit final**

```bash
cd /home/carlos/projects/DSY1103-FULLSTACK-I-BACKEND
git add page/src/main.jsx
git commit -m "feat: refactor GuidesGallery with embedded HtmlViewer and real URLs"
```
