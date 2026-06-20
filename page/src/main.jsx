import React, { useEffect, useId, useMemo, useRef, useState } from 'react';
import { createRoot } from 'react-dom/client';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import mermaid from 'mermaid';
import {
  ArrowRight,
  BookMarked,
  BookOpen,
  Braces,
  ChevronDown,
  ChevronRight,
  Code2,
  Database,
  ExternalLink,
  FileCode2,
  FileText,
  Folder,
  FolderTree,
  Home,
  Layers3,
  LocateFixed,
  Package,
  Presentation,
  Rocket,
  PanelLeftClose,
  Search,
  Server,
  Workflow,
} from 'lucide-react';
import content from './generated/content.json';
import './styles.css';

mermaid.initialize({
  startOnLoad: false,
  securityLevel: 'strict',
  theme: 'base',
  markdownAutoWrap: true,
  flowchart: {
    htmlLabels: true,
    useMaxWidth: true,
    wrappingWidth: 220,
    nodeSpacing: 70,
    rankSpacing: 90,
    padding: 18,
  },
  sequence: {
    wrap: true,
    width: 180,
  },
  themeVariables: {
    primaryColor: '#ecfdf5',
    primaryTextColor: '#064e3b',
    primaryBorderColor: '#10b981',
    lineColor: '#52525b',
    secondaryColor: '#f5f5f4',
    tertiaryColor: '#fef3c7',
    fontFamily: 'Inter, ui-sans-serif, system-ui, sans-serif',
  },
});

const allDocs =
  content.allDocs ??
  [
    ...content.rootDocs.map((doc) => ({ ...doc, path: doc.path ?? `docs/lessons/${doc.id}` })),
    ...content.lessons.flatMap((lesson) =>
      lesson.docs.map((doc) => ({ ...doc, lessonTitle: lesson.title, lessonId: lesson.id })),
    ),
  ];

const firstDoc =
  allDocs.find((doc) => doc.path === 'docs/README.md') ??
  allDocs.find((doc) => doc.path.endsWith('/README.md')) ??
  allDocs[0];
const firstProject = content.projects[0];
const htmlPages = content.htmlPages ?? [];
const guidePages = content.guides ?? [];
const challengeSites = buildChallengeSites(htmlPages);
const firstProjectFile =
  firstProject?.files.find((file) => file.path === 'README.md') ??
  firstProject?.files.find((file) => file.path === 'pom.xml') ??
  firstProject?.files[0];

function App() {
  const [mode, setMode] = useState(window.location.hash ? 'lessons' : 'home');
  const [query, setQuery] = useState('');
  const [selectedDocId, setSelectedDocId] = useState(docFromHash()?.id ?? firstDoc?.id ?? '');
  const [selectedHtmlPageId, setSelectedHtmlPageId] = useState(htmlPageFromHash()?.id ?? '');
  const [selectedGuideId, setSelectedGuideId] = useState('');
  const [selectedProjectId, setSelectedProjectId] = useState(firstProject?.id ?? '');
  const [selectedFileId, setSelectedFileId] = useState(firstProjectFile?.id ?? '');
  const [sidebarOpen, setSidebarOpen] = useState(true);

  useEffect(() => {
    if (!window.location.hash) {
      return undefined;
    }

    function syncFromHash() {
      const doc = docFromHash();
      if (doc) {
        setMode('lessons');
        setSelectedDocId(doc.id);
        const hash = anchorFromHash();
        scrollToHash(hash);
        return;
      }

      const htmlPage = htmlPageFromHash();
      if (htmlPage) {
        setMode('challenges');
        setSelectedHtmlPageId(htmlPage.id);
      }
    }

    syncFromHash();
    window.addEventListener('hashchange', syncFromHash);
    return () => window.removeEventListener('hashchange', syncFromHash);
  }, []);

  const filteredDocs = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return allDocs;
    return allDocs.filter((doc) =>
      [doc.title, doc.path, doc.content].some((value) =>
        value?.toLowerCase().includes(needle),
      ),
    );
  }, [query]);

  const filteredProjects = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return content.projects;
    return content.projects
      .map((project) => ({
        ...project,
        files: project.files.filter((file) =>
          [project.name, project.description, file.path, file.content].some((value) =>
            value?.toLowerCase().includes(needle),
          ),
        ),
      }))
      .filter((project) => project.files.length > 0 || project.name.toLowerCase().includes(needle));
  }, [query]);

  const filteredChallengeSites = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return challengeSites;
    return challengeSites.filter((site) =>
      [site.title, site.path, site.summary, ...site.pages.map((page) => `${page.title} ${page.path}`)].some((value) =>
        value?.toLowerCase().includes(needle),
      ),
    );
  }, [query]);

  const filteredGuides = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return guidePages;
    return guidePages.filter((guide) =>
      [guide.title, guide.path, guide.summary].some((value) => value?.toLowerCase().includes(needle)),
    );
  }, [query]);

  const selectedDoc =
    allDocs.find((doc) => doc.id === selectedDocId) ?? filteredDocs[0] ?? allDocs[0];

  const selectedProject =
    content.projects.find((project) => project.id === selectedProjectId) ??
    filteredProjects[0] ??
    content.projects[0];

  const selectedFile =
    selectedProject?.files.find((file) => file.id === selectedFileId) ??
    selectedProject?.files.find((file) => file.path === 'README.md') ??
    selectedProject?.files.find((file) => file.path === 'pom.xml') ??
    selectedProject?.files[0];

  const selectedHtmlPage = selectedHtmlPageId
    ? (htmlPages.find((page) => page.id === selectedHtmlPageId) ?? null)
    : null;

  const selectedGuide = selectedGuideId
    ? (guidePages.find((g) => g.id === selectedGuideId) ?? null)
    : null;

  function selectProject(project) {
    const nextFile =
      project.files.find((file) => file.path === 'README.md') ??
      project.files.find((file) => file.path === 'pom.xml') ??
      project.files[0];
    setSelectedProjectId(project.id);
    setSelectedFileId(nextFile?.id ?? '');
  }

  function navigateToDoc(docId, hash = '') {
    const doc = allDocs.find((item) => item.id === docId);
    if (!doc) return;
    setMode('lessons');
    setSelectedDocId(doc.id);
    pushRoute(doc.path, hash);
    scrollToHash(hash);
  }

  function navigateToHtmlPage(pageId) {
    const page = htmlPages.find((item) => item.id === pageId);
    if (!page) return;
    setMode('challenges');
    setSelectedHtmlPageId(page.id);
    pushRoute(page.path);
  }

  function navigateHome() {
    setMode('home');
    window.history.replaceState(null, '', `${window.location.pathname}${window.location.search}`);
  }

  function openLessons() {
    setMode('lessons');
    replaceRoute(selectedDoc?.path ?? firstDoc?.path ?? 'docs/README.md');
  }

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

  return (
    <div className="portal-shell min-h-screen text-zinc-950">
      <header className="sticky top-0 z-50 border-b border-zinc-200 bg-white/88 backdrop-blur-xl">
        <div className="mx-auto flex max-w-[1600px] flex-col gap-4 px-4 py-4 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex items-center gap-3">
            <div className="brand-mark">
              DS
            </div>
            <div>
              <h1 className="text-xl font-semibold leading-tight text-zinc-950">DSY1103 | Desarrollo Fullstack I</h1>
              <p className="text-sm text-zinc-600">
                {content.lessons.length} lecciones, {content.projects.length} proyectos, material generado{' '}
                {formatDate(content.generatedAt)}
              </p>
            </div>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            <div className="relative min-w-0 sm:w-80">
              <Search className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-zinc-500" size={18} />
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                className="h-11 w-full rounded-md border border-zinc-300 bg-white pl-10 pr-3 text-sm outline-none transition focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100"
                placeholder="Buscar en documentos, challenges y codigo"
              />
            </div>
            <div className="grid w-full grid-cols-3 rounded-md border border-zinc-300 bg-zinc-100 p-1 sm:w-auto sm:grid-cols-5">
              <ModeButton active={mode === 'home'} icon={Home} onClick={navigateHome}>
                Inicio
              </ModeButton>
              <ModeButton active={mode === 'lessons'} icon={BookOpen} onClick={openLessons}>
                Lecciones
              </ModeButton>
              <ModeButton active={mode === 'challenges'} icon={ExternalLink} onClick={openChallenges}>
                Challenges
              </ModeButton>
              <ModeButton active={mode === 'guides'} icon={BookMarked} onClick={openGuides}>
                Guías
              </ModeButton>
              <ModeButton active={mode === 'projects'} icon={Code2} onClick={() => setMode('projects')}>
                Proyectos
              </ModeButton>
            </div>
          </div>
        </div>
      </header>

      {mode === 'home' ? (
        <HomePortal
          onOpenLessons={openLessons}
          onOpenChallenges={openChallenges}
          onOpenProjects={() => setMode('projects')}
          onOpenDoc={navigateToDoc}
          onOpenChallenge={navigateToHtmlPage}
        />
      ) : (
      <main className="mx-auto grid max-w-[1600px] gap-4 px-4 py-4 lg:grid-cols-[minmax(280px,380px)_1fr]">
        {mode !== 'challenges' && mode !== 'guides' && sidebarOpen ? (
          <aside className="min-h-[calc(100vh-130px)] overflow-hidden rounded-md border border-zinc-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-zinc-200 px-4 py-3">
              <div className="flex items-center gap-2 text-sm font-semibold">
                {mode === 'projects' ? <FolderTree size={18} /> : <Layers3 size={18} />}
                {mode === 'lessons' ? 'docs/' : 'Proyectos Java'}
              </div>
              <button
                className="icon-button"
                onClick={() => setSidebarOpen(false)}
                title="Ocultar panel"
                aria-label="Ocultar panel"
              >
                <PanelLeftClose size={18} />
              </button>
            </div>
            {mode === 'lessons' ? (
              <DocsTree docs={filteredDocs} selectedDoc={selectedDoc} onSelect={navigateToDoc} />
            ) : (
              <ProjectTree
                projects={filteredProjects}
                selectedProject={selectedProject}
                selectedFile={selectedFile}
                onProjectSelect={selectProject}
                onFileSelect={setSelectedFileId}
              />
            )}
          </aside>
        ) : mode !== 'challenges' && mode !== 'guides' ? (
          <button
            className="flex h-12 items-center justify-center gap-2 rounded-md border border-zinc-200 bg-white text-sm font-medium shadow-sm lg:col-span-1"
            onClick={() => setSidebarOpen(true)}
          >
            <FolderTree size={18} />
            Mostrar navegador
          </button>
        ) : null}

        <section className={mode === 'challenges' || mode === 'guides' || !sidebarOpen ? 'min-w-0 lg:col-span-2' : 'min-w-0'}>
          {mode === 'lessons' ? (
            <MarkdownViewer doc={selectedDoc} onNavigate={navigateToDoc} />
          ) : mode === 'challenges' ? (
            <ChallengeGallery
              sites={filteredChallengeSites}
              selectedPage={selectedHtmlPage}
              onSelectPage={navigateToHtmlPage}
            />
          ) : mode === 'guides' ? (
            <GuidesGallery guides={filteredGuides} />
          ) : (
            <ProjectViewer project={selectedProject} file={selectedFile} onNavigateDoc={navigateToDoc} setMode={setMode} />
          )}
        </section>
      </main>
      )}
    </div>
  );
}

function HomePortal({ onOpenLessons, onOpenChallenges, onOpenProjects, onOpenDoc, onOpenChallenge }) {
  const featuredLessons = [
    'docs/lessons/00-git-github/README.md',
    'docs/lessons/03-first-api/README.md',
    'docs/lessons/06-crud/README.md',
    'docs/lessons/10-jpa-intro/README.md',
    'docs/lessons/14-microservices/README.md',
    'docs/lessons/22-unit-testing-microservices/README.md',
  ]
    .map((path) => allDocs.find((doc) => doc.path === path))
    .filter(Boolean);
  const featuredChallenge = challengeSites[0];
  const featuredProjects = [
    'Tickets-22',
    'NotificationService',
    'AuditService',
    'SearchService',
    'SLAService',
    'Tickets-21',
  ]
    .map((id) => content.projects.find((project) => project.id === id))
    .filter(Boolean);

  return (
    <main>
      <section className="portal-hero">
        <div className="portal-container grid items-center gap-10 lg:grid-cols-[1.25fr_0.75fr]">
          <div>
            <p className="portal-eyebrow">Spring Boot · Java 21 · REST APIs · Microservicios</p>
            <h2 className="portal-title">DSY1103 Desarrollo Fullstack I</h2>
            <p className="portal-lead">
              Portal de clases, challenges y snapshots de proyecto para construir backend Java desde HTTP y REST
              hasta persistencia, seguridad, documentación OpenAPI y servicios distribuidos.
            </p>
            <div className="mt-8 flex flex-wrap gap-3">
              <button type="button" className="portal-btn portal-btn-main" onClick={onOpenLessons}>
                <Rocket size={18} />
                Entrar a las lecciones
              </button>
              <button type="button" className="portal-btn portal-btn-ghost" onClick={onOpenChallenges}>
                <Presentation size={18} />
                Ver challenges
              </button>
            </div>
          </div>

          <div className="learn-scene" aria-label="Escena visual de arquitectura backend">
            <div className="console-top">
              <span className="dot red" />
              <span className="dot yellow" />
              <span className="dot green" />
            </div>
            <div className="code-panel">
              <span className="code-line"><span className="kw">@RestController</span></span>
              <span className="code-line"><span className="kw">@RequestMapping</span>(<span className="str">"/ticket-app/tickets"</span>)</span>
              <span className="code-line"><span className="kw">class</span> <span className="fn">TicketController</span> {'{'}</span>
              <span className="code-line">&nbsp;&nbsp;<span className="kw">private final</span> TicketService service;</span>
              <span className="code-line">&nbsp;&nbsp;<span className="kw">@GetMapping</span></span>
              <span className="code-line">&nbsp;&nbsp;List&lt;TicketDTO&gt; <span className="fn">findAll</span>() {'{'}</span>
              <span className="code-line">&nbsp;&nbsp;&nbsp;&nbsp;<span className="kw">return</span> service.findAll();</span>
              <span className="code-line">&nbsp;&nbsp;{'}'}</span>
              <span className="code-line">{'}'}</span>
            </div>
            <div className="floating-chip chip-a"><Server size={15} /> REST</div>
            <div className="floating-chip chip-b"><Database size={15} /> JPA</div>
            <div className="floating-chip chip-c"><Workflow size={15} /> MS</div>
          </div>
        </div>
      </section>

      <section id="actividades" className="portal-section">
        <div className="portal-container">
          <div className="section-heading">
            <p className="portal-eyebrow">Inicio rápido</p>
            <h2 className="section-title">Material disponible</h2>
            <p className="section-copy">
              Accesos principales del ramo organizados como tarjetas, sin depender del arbol de archivos para empezar.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-3">
            <PortalCard
              icon={BookOpen}
              tone="blue"
              title={`${content.lessons.length} lecciones`}
              text="Teoría y práctica de HTTP, REST, capas, DTO, persistencia, seguridad, logging y documentación."
              tags={['docs/', 'Spring Boot', 'Java 21']}
              action="Abrir lecciones"
              onClick={onOpenLessons}
            />
            <PortalCard
              icon={Presentation}
              tone="teal"
              title={`${challengeSites.length} challenge${challengeSites.length === 1 ? '' : 's'}`}
              text="Mini sitios evaluativos con enunciados, arquitectura, endpoints, estándares y criterios de entrega."
              tags={['mini sitios', 'evaluación', 'HTML']}
              action="Abrir challenges"
              onClick={onOpenChallenges}
            />
            <PortalCard
              icon={Code2}
              tone="rose"
              title={`${content.projects.length} proyectos`}
              text="Snapshots Maven del proyecto Tickets y microservicios de apoyo para revisar código real por lección."
              tags={['Maven', 'Tickets', 'microservicios']}
              action="Abrir proyectos"
              onClick={onOpenProjects}
            />
          </div>
        </div>
      </section>

      <section id="material" className="portal-section">
        <div className="portal-container">
          <div className="section-heading">
            <p className="portal-eyebrow">Lecciones y apoyo</p>
            <h2 className="section-title">Ruta de aprendizaje</h2>
            <p className="section-copy">
              Un recorrido sugerido desde fundamentos web hasta servicios con persistencia, seguridad y despliegue local.
            </p>
          </div>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            {featuredLessons.map((doc, index) => (
              <button
                key={doc.id}
                type="button"
                onClick={() => onOpenDoc(doc.id)}
                className="material-card"
              >
                <span className="material-kicker">Lección {String(index + 1).padStart(2, '0')}</span>
                <span className="material-title">{cleanPortalTitle(doc.title)}</span>
                <span className="material-copy">{doc.path}</span>
              </button>
            ))}
          </div>
        </div>
      </section>

      <section id="challenges" className="portal-section">
        <div className="portal-container">
          <div className="section-heading">
            <p className="portal-eyebrow">Challenge destacado</p>
            <h2 className="section-title">{featuredChallenge?.title ?? 'Challenges'}</h2>
            <p className="section-copy">
              Los challenges se tratan como mini sitios independientes: vista previa, secciones internas y apertura en
              pestaña sin pasar por el router de documentos.
            </p>
          </div>

          {featuredChallenge ? (
            <div className="ppt-card grid overflow-hidden lg:grid-cols-[1.1fr_0.9fr]">
              <div className="min-h-[260px] border-b border-zinc-200 bg-zinc-950 lg:border-b-0 lg:border-r">
                <iframe
                  title={`Preview ${featuredChallenge.title}`}
                  srcDoc={htmlPreviewDocument(featuredChallenge.entry)}
                  className="challenge-preview-frame"
                  tabIndex={-1}
                />
              </div>
              <div className="p-5">
                <span className="material-kicker">{featuredChallenge.path}</span>
                <h3 className="mt-2 text-2xl font-extrabold leading-tight text-zinc-950">{featuredChallenge.title}</h3>
                <p className="mt-3 text-sm leading-6 text-zinc-600">{featuredChallenge.summary}</p>
                <div className="mt-5 flex flex-wrap gap-2">
                  {featuredChallenge.pages.slice(0, 7).map((page) => (
                    <button
                      key={page.id}
                      type="button"
                      className="mini-link"
                      onClick={() => onOpenChallenge(page.id)}
                    >
                      {shortHtmlPageTitle(page)}
                    </button>
                  ))}
                </div>
                <button type="button" className="portal-btn portal-btn-main mt-6" onClick={onOpenChallenges}>
                  Ver todos los challenges
                  <ArrowRight size={18} />
                </button>
              </div>
            </div>
          ) : null}
        </div>
      </section>

      <section id="proyectos" className="portal-path">
        <div className="portal-container">
          <div className="section-heading">
            <p className="portal-eyebrow">Snapshots Java</p>
            <h2 className="section-title">Proyectos del ramo</h2>
            <p className="section-copy">
              El snapshot actual incorpora pruebas de controller y service, JaCoCo con umbral de 85% y
              microservicios auxiliares organizados por capas.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {featuredProjects.map((project) => (
              <article key={project.id} className="project-card">
                <div className="flex items-start gap-3">
                  <div className="project-icon">
                    {project.kind?.toLowerCase().includes('micro') ? <Workflow size={20} /> : <Braces size={20} />}
                  </div>
                  <div>
                    <h3 className="font-extrabold text-zinc-950">{project.name}</h3>
                    <p className="mt-1 text-sm leading-6 text-zinc-600">{project.description}</p>
                  </div>
                </div>
                <div className="mt-4 flex flex-wrap gap-2">
                  <span className="tag">{project.kind}</span>
                  <span className="tag">{project.fileCount} archivos</span>
                </div>
              </article>
            ))}
          </div>

          <div className="mt-6">
            <button type="button" className="portal-btn portal-btn-ghost" onClick={onOpenProjects}>
              Explorar código de proyectos
              <ArrowRight size={18} />
            </button>
          </div>
        </div>
      </section>

      <footer className="portal-footer">
        <div className="portal-container flex flex-wrap justify-between gap-3">
          <span>DSY1103 | Desarrollo Fullstack I</span>
          <span>Backend Java, Spring Boot y arquitectura por capas</span>
        </div>
      </footer>
    </main>
  );
}

function PortalCard({ icon: Icon, tone, title, text, tags, action, onClick }) {
  return (
    <article className="activity-card">
      <div className={`activity-icon bg-${tone}`}>
        <Icon size={22} />
      </div>
      <h3>{title}</h3>
      <p>{text}</p>
      <div className="tag-row">
        {tags.map((tag) => (
          <span key={tag} className="tag">{tag}</span>
        ))}
      </div>
      <button type="button" className="open-link" onClick={onClick}>
        {action} <ArrowRight size={16} />
      </button>
    </article>
  );
}

function cleanPortalTitle(title) {
  return title
    .replace(/^LECCIÓN\s+\d+\s*[—-]\s*/i, '')
    .replace(/^Lección\s+\d+\s*[—-]\s*/i, '')
    .replace(/^#\s*/, '');
}

function ModeButton({ active, icon: Icon, children, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex h-9 items-center justify-center gap-2 rounded px-3 text-sm font-medium transition ${
        active ? 'bg-white text-emerald-700 shadow-sm' : 'text-zinc-600 hover:text-zinc-950'
      }`}
    >
      <Icon size={17} aria-hidden="true" />
      <span>{children}</span>
    </button>
  );
}

function DocsTree({ docs, selectedDoc, onSelect }) {
  const tree = useMemo(() => buildDocTree(docs), [docs]);
  const allFolderPaths = useMemo(() => collectFolderPaths(tree), [tree]);
  const [expandedPaths, setExpandedPaths] = useState(new Set());
  const docRefs = useRef(new Map());
  const searchIsActive = docs.length !== allDocs.length;

  function toggleFolder(path) {
    setExpandedPaths((current) => {
      const next = new Set(current);
      if (next.has(path)) {
        next.delete(path);
      } else {
        next.add(path);
      }
      return next;
    });
  }

  function closeAll() {
    setExpandedPaths(new Set());
  }

  function openAll() {
    setExpandedPaths(new Set(allFolderPaths));
  }

  function locateSelected() {
    if (!selectedDoc) return;
    const ancestors = folderAncestorsForDoc(selectedDoc.path);
    setExpandedPaths((current) => new Set([...current, ...ancestors]));
    window.setTimeout(() => {
      docRefs.current.get(selectedDoc.id)?.scrollIntoView({
        block: 'center',
        behavior: 'smooth',
      });
    }, 0);
  }

  function registerDocRef(id, node) {
    if (node) {
      docRefs.current.set(id, node);
    } else {
      docRefs.current.delete(id);
    }
  }

  return (
    <div className="grid max-h-[calc(100vh-190px)] grid-rows-[auto_1fr] overflow-hidden">
      <div className="border-b border-zinc-200 p-3">
        <div className="grid grid-cols-3 gap-2">
          <TreeControlButton onClick={closeAll}>Cerrar</TreeControlButton>
          <TreeControlButton onClick={openAll}>Abrir</TreeControlButton>
          <TreeControlButton onClick={locateSelected} icon={LocateFixed}>
            Ubicar
          </TreeControlButton>
        </div>
        <p className="mt-2 text-xs leading-5 text-zinc-500">
          {searchIsActive
            ? 'Busqueda activa: se abren las ramas con coincidencias.'
            : 'Carpetas cerradas por defecto. Abre solo lo que necesitas.'}
        </p>
      </div>
      <div className="overflow-y-auto p-3">
        <TreeNode
          node={tree}
          selectedDoc={selectedDoc}
          onSelect={onSelect}
          expandedPaths={expandedPaths}
          onToggle={toggleFolder}
          forceOpen={searchIsActive}
          registerDocRef={registerDocRef}
          root
        />
        {docs.length === 0 && <EmptyState text="No hay documentos para la busqueda actual." />}
      </div>
    </div>
  );
}

function ChallengeGallery({ sites, selectedPage, onSelectPage }) {
  if (sites.length === 0) return <EmptyState text="No hay challenges para la busqueda actual." />;

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
          <ChallengeCard
            key={site.id}
            site={site}
            selectedPage={selectedPage}
            onSelectPage={onSelectPage}
          />
        ))}
      </div>
    </div>
  );
}

function ChallengeCard({ site, selectedPage, onSelectPage }) {
  const activePage = site.pages.find((page) => page.id === selectedPage?.id);
  const previewPage = activePage ?? site.entry;

  return (
    <article className="overflow-hidden rounded-md border border-zinc-200 bg-white shadow-panel">
      <div className="aspect-[16/9] border-b border-zinc-200 bg-zinc-950">
        <iframe
          title={`Preview ${site.title}`}
          srcDoc={htmlPreviewDocument(previewPage)}
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

        <div>
          <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-zinc-500">Secciones</p>
          <div className="flex flex-wrap gap-2">
            {site.pages.map((page) => (
              <button
                key={page.id}
                type="button"
                onClick={() => onSelectPage(page.id)}
                className={`rounded-md border px-2.5 py-1.5 text-xs font-semibold transition ${
                  selectedPage?.id === page.id
                    ? 'border-emerald-500 bg-emerald-50 text-emerald-800'
                    : 'border-zinc-200 bg-white text-zinc-600 hover:border-emerald-400 hover:text-emerald-700'
                }`}
              >
                {shortHtmlPageTitle(page)}
              </button>
            ))}
          </div>
        </div>

        <div className="flex flex-wrap items-center justify-between gap-2 border-t border-zinc-200 pt-3">
          <button
            type="button"
            onClick={() => onSelectPage(site.entry.id)}
            className="inline-flex items-center gap-2 rounded-md bg-emerald-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            <ExternalLink size={16} />
            Previsualizar inicio
          </button>
          <button
            type="button"
            className="inline-flex items-center gap-2 rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm font-semibold text-zinc-700 transition hover:border-emerald-500 hover:text-emerald-700"
            onClick={() => openHtmlPageInNewTab(activePage ?? site.entry)}
          >
            Abrir en pestaña
          </button>
        </div>
      </div>
    </article>
  );
}

function GuidesGallery({ guides }) {
  if (guides.length === 0) return <EmptyState text="No hay guías disponibles." />;

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
          <GuideCard key={guide.id} guide={guide} />
        ))}
      </div>
    </div>
  );
}

function GuideCard({ guide }) {
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
        <div className="flex items-center gap-2 border-t border-zinc-200 pt-3">
          <button
            type="button"
            className="inline-flex items-center gap-2 rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm font-semibold text-zinc-700 transition hover:border-emerald-500 hover:text-emerald-700"
            onClick={() => openHtmlPageInNewTab(guide)}
          >
            <ExternalLink size={16} />
            Abrir en pestaña
          </button>
        </div>
      </div>
    </article>
  );
}

function TreeControlButton({ children, onClick, icon: Icon }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className="flex h-9 items-center justify-center gap-1 rounded-md border border-zinc-200 bg-white px-2 text-xs font-semibold text-zinc-700 transition hover:bg-zinc-100"
    >
      {Icon ? <Icon size={14} /> : null}
      <span>{children}</span>
    </button>
  );
}

function TreeNode({
  node,
  selectedDoc,
  onSelect,
  expandedPaths,
  onToggle,
  forceOpen = false,
  registerDocRef,
  root = false,
  depth = 0,
}) {
  const folders = [...node.children.values()].sort((a, b) =>
    a.name.localeCompare(b.name, undefined, { numeric: true }),
  );
  const files = [...node.files].sort((a, b) => a.path.localeCompare(b.path, undefined, { numeric: true }));
  const expanded = root || forceOpen || expandedPaths.has(node.path);

  return (
    <div className={root ? '' : 'ml-2 border-l border-zinc-200 pl-2'}>
      {!root && (
        <button
          type="button"
          onClick={() => onToggle(node.path)}
          className="mb-1 mt-2 flex w-full items-center gap-2 rounded px-2 py-1 text-left text-sm font-semibold text-zinc-700 transition hover:bg-zinc-100"
        >
          {expanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
          <Folder size={15} className="text-amber-600" />
          <span className="truncate">{node.name}</span>
          <span className="ml-auto rounded bg-zinc-100 px-1.5 py-0.5 text-[11px] font-medium text-zinc-500">
            {countDocs(node)}
          </span>
        </button>
      )}

      {expanded ? (
        <>
          {files.map((doc) => (
            <button
              key={doc.id}
              ref={(nodeRef) => registerDocRef(doc.id, nodeRef)}
              type="button"
              onClick={() => onSelect(doc.id)}
              className={`mb-1 flex w-full items-start gap-2 rounded-md px-2 py-2 text-left text-sm transition ${
                selectedDoc?.id === doc.id ? 'bg-emerald-50 text-emerald-900 ring-1 ring-emerald-200' : 'hover:bg-zinc-100'
              }`}
              style={{ marginLeft: root ? 0 : Math.min(depth, 3) * 2 }}
            >
              <FileText className="mt-0.5 shrink-0 text-zinc-500" size={15} />
              <span className="min-w-0">
                <span className="line-clamp-2 font-medium">{doc.title}</span>
                <span className="mt-0.5 block break-all text-xs text-zinc-500">{doc.path}</span>
              </span>
            </button>
          ))}

          {folders.map((child) => (
            <TreeNode
              key={child.path}
              node={child}
              selectedDoc={selectedDoc}
              onSelect={onSelect}
              expandedPaths={expandedPaths}
              onToggle={onToggle}
              forceOpen={forceOpen}
              registerDocRef={registerDocRef}
              depth={depth + 1}
            />
          ))}
        </>
      ) : null}
    </div>
  );
}

function ProjectTree({ projects, selectedProject, selectedFile, onProjectSelect, onFileSelect }) {
  const projectPackages = useMemo(() => buildProjectPackages(content.projects), []);
  const tree = useMemo(() => buildProjectTree(projects, projectPackages), [projects, projectPackages]);
  const allFolderPaths = useMemo(() => collectFolderPaths(tree), [tree]);
  const [expandedPaths, setExpandedPaths] = useState(new Set());
  const fileRefs = useRef(new Map());
  const searchIsActive = projects.length !== content.projects.length;

  function toggleFolder(path) {
    setExpandedPaths((current) => {
      const next = new Set(current);
      if (next.has(path)) {
        next.delete(path);
      } else {
        next.add(path);
      }
      return next;
    });
  }

  function closeAll() {
    setExpandedPaths(new Set());
  }

  function openAll() {
    setExpandedPaths(new Set(allFolderPaths));
  }

  function locateSelected() {
    if (!selectedProject || !selectedFile) return;
    const ancestors = folderAncestorsForProjectFile(selectedProject, selectedFile, projectPackages);
    setExpandedPaths((current) => new Set([...current, ...ancestors]));
    window.setTimeout(() => {
      fileRefs.current.get(selectedFile.id)?.scrollIntoView({
        block: 'center',
        behavior: 'smooth',
      });
    }, 0);
  }

  function registerFileRef(id, node) {
    if (node) {
      fileRefs.current.set(id, node);
    } else {
      fileRefs.current.delete(id);
    }
  }

  return (
    <div className="grid max-h-[calc(100vh-190px)] grid-rows-[auto_1fr] overflow-hidden">
      <div className="border-b border-zinc-200 p-3">
        <div className="grid grid-cols-3 gap-2">
          <TreeControlButton onClick={closeAll}>Cerrar</TreeControlButton>
          <TreeControlButton onClick={openAll}>Abrir</TreeControlButton>
          <TreeControlButton onClick={locateSelected} icon={LocateFixed}>
            Ubicar
          </TreeControlButton>
        </div>
        <p className="mt-2 text-xs leading-5 text-zinc-500">
          {searchIsActive
            ? 'Busqueda activa: se muestran los proyectos con coincidencias.'
            : 'Cada proyecto abre su arbol Maven con paquetes Java compactados.'}
        </p>
      </div>
      <div className="overflow-y-auto p-3">
        <ProjectTreeNode
          node={tree}
          selectedProject={selectedProject}
          selectedFile={selectedFile}
          onProjectSelect={onProjectSelect}
          onFileSelect={onFileSelect}
          expandedPaths={expandedPaths}
          onToggle={toggleFolder}
          forceOpen={searchIsActive}
          registerFileRef={registerFileRef}
          root
        />
        {projects.length === 0 && <EmptyState text="No hay proyectos para la busqueda actual." />}
      </div>
    </div>
  );
}

function ProjectTreeNode({
  node,
  selectedProject,
  selectedFile,
  onProjectSelect,
  onFileSelect,
  expandedPaths,
  onToggle,
  forceOpen = false,
  registerFileRef,
  root = false,
  depth = 0,
}) {
  const folders = [...node.children.values()].sort((a, b) =>
    a.name.localeCompare(b.name, undefined, { numeric: true }),
  );
  const files = [...node.files].sort((a, b) =>
    a.treePath.localeCompare(b.treePath, undefined, { numeric: true }),
  );
  const expanded = root || forceOpen || expandedPaths.has(node.path);
  const isProject = node.type === 'project';
  const selectedFolder = isProject && selectedProject?.id === node.project.id;

  return (
    <div className={root ? '' : 'ml-2 border-l border-zinc-200 pl-2'}>
      {!root && (
        <button
          type="button"
          onClick={() => {
            onToggle(node.path);
            if (isProject) onProjectSelect(node.project);
          }}
          className={`mb-1 mt-2 flex w-full items-center gap-2 rounded px-2 py-1 text-left text-sm font-semibold transition ${
            selectedFolder ? 'bg-emerald-50 text-emerald-900 ring-1 ring-emerald-200' : 'text-zinc-700 hover:bg-zinc-100'
          }`}
        >
          {expanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
          {node.packageFolder ? (
            <Package size={15} className="text-sky-700" />
          ) : (
            <Folder size={15} className={isProject ? 'text-emerald-700' : 'text-amber-600'} />
          )}
          <span className="truncate">{node.name}</span>
          <span className="ml-auto rounded bg-zinc-100 px-1.5 py-0.5 text-[11px] font-medium text-zinc-500">
            {countProjectFiles(node)}
          </span>
        </button>
      )}

      {expanded ? (
        <>
          {files.map((file) => (
            <button
              key={file.id}
              ref={(nodeRef) => registerFileRef(file.id, nodeRef)}
              type="button"
              onClick={() => {
                onProjectSelect(file.project);
                onFileSelect(file.id);
              }}
              className={`mb-1 flex w-full items-start gap-2 rounded-md px-2 py-2 text-left text-sm transition ${
                selectedFile?.id === file.id ? 'bg-teal-50 text-teal-950 ring-1 ring-teal-200' : 'hover:bg-zinc-100'
              }`}
              style={{ marginLeft: root ? 0 : Math.min(depth, 3) * 2 }}
            >
              <FileCode2 className="mt-0.5 shrink-0 text-zinc-500" size={16} />
              <span className="min-w-0">
                <span className="block break-all font-medium">{file.fileName}</span>
                <span className="text-xs text-zinc-500">{file.language}</span>
              </span>
            </button>
          ))}

          {folders.map((child) => (
            <ProjectTreeNode
              key={child.path}
              node={child}
              selectedProject={selectedProject}
              selectedFile={selectedFile}
              onProjectSelect={onProjectSelect}
              onFileSelect={onFileSelect}
              expandedPaths={expandedPaths}
              onToggle={onToggle}
              forceOpen={forceOpen}
              registerFileRef={registerFileRef}
              depth={depth + 1}
            />
          ))}
        </>
      ) : null}
    </div>
  );
}

function MarkdownViewer({ doc, onNavigate }) {
  if (!doc) return <EmptyState text="No hay documento seleccionado." />;

  return (
    <article className="overflow-hidden rounded-md border border-zinc-200 bg-white shadow-panel">
      <ViewerHeader icon={FileText} title={doc.title} subtitle={doc.path} />
      <MarkdownContent content={doc.content} currentPath={doc.path} onNavigate={onNavigate} />
    </article>
  );
}

function ProjectViewer({ project, file, onNavigateDoc, setMode }) {
  if (!project || !file) return <EmptyState text="No hay archivo seleccionado." />;

  function navigateFromProject(docId, hash) {
    setMode('lessons');
    onNavigateDoc(docId, hash);
  }

  return (
    <article className="overflow-hidden rounded-md border border-zinc-200 bg-white shadow-panel">
      <ViewerHeader
        icon={Server}
        title={`${project.name} / ${file.path}`}
        subtitle={`${project.kind} · ${file.language} · ${formatBytes(file.size)}`}
      />
      <div className={`grid gap-0 ${file.language === 'markdown' ? 'lg:grid-cols-[minmax(0,1fr)_320px]' : 'lg:grid-cols-[minmax(0,1fr)_320px]'}`}>
        {file.language === 'markdown' ? (
          <MarkdownContent
            content={file.content}
            currentPath={`${project.name}/${file.path}`}
            onNavigate={navigateFromProject}
            compact
          />
        ) : (
          <CodeViewer file={file} />
        )}
        <aside className="border-t border-zinc-200 bg-stone-50 p-4 lg:border-l lg:border-t-0">
          <h2 className="mb-2 flex items-center gap-2 text-sm font-semibold text-zinc-900">
            <Code2 size={17} />
            Resumen del proyecto
          </h2>
          <p className="text-sm leading-6 text-zinc-700">{project.description}</p>
          <dl className="mt-4 grid grid-cols-2 gap-2 text-sm">
            <Metric label="Archivos" value={project.fileCount} />
            <Metric label="Tipo" value={project.kind} />
          </dl>
        </aside>
      </div>
    </article>
  );
}

function CodeViewer({ file }) {
  const [formatted, setFormatted] = useState(file.content);
  const [formatStatus, setFormatStatus] = useState(formatLabelFor(file.language));

  useEffect(() => {
    let cancelled = false;

    async function formatCode() {
      const config = await prettierConfigFor(file.language);
      if (!config) {
        setFormatted(file.content);
        setFormatStatus(formatLabelFor(file.language));
        return;
      }

      try {
        const next = await config.prettier.format(file.content, config.options);
        if (!cancelled) {
          setFormatted(next.trimEnd());
          setFormatStatus(`Prettier ${file.language}`);
        }
      } catch {
        if (!cancelled) {
          setFormatted(file.content);
          setFormatStatus(`${formatLabelFor(file.language)} sin formatear`);
        }
      }
    }

    formatCode();
    return () => {
      cancelled = true;
    };
  }, [file]);

  return (
    <div className="code-pane max-h-[calc(100vh-190px)] overflow-auto bg-zinc-950">
      <div className="sticky top-0 z-10 flex items-center justify-between border-b border-zinc-800 bg-zinc-950/95 px-4 py-2 text-xs text-zinc-400 backdrop-blur">
        <span>{formatStatus}</span>
        <span>{file.path}</span>
      </div>
      <SyntaxHighlighter
        language={syntaxLanguageFor(file.language)}
        style={oneDark}
        PreTag="div"
        customStyle={{
          margin: 0,
          padding: '1.25rem',
          background: 'transparent',
          fontSize: '0.875rem',
          lineHeight: '1.6',
        }}
        codeTagProps={{
          style: {
            fontFamily: 'JetBrains Mono, ui-monospace, SFMono-Regular, monospace',
          },
        }}
      >
        {formatted}
      </SyntaxHighlighter>
    </div>
  );
}

function MarkdownContent({ content, currentPath, onNavigate, compact = false }) {
  const components = useMemo(
    () => createMarkdownComponents(currentPath, onNavigate),
    [currentPath, onNavigate],
  );

  return (
    <div
      className={`markdown-body max-h-[calc(100vh-190px)] overflow-y-auto px-5 py-5 ${
        compact ? 'bg-white' : 'lg:px-8'
      }`}
    >
      <ReactMarkdown remarkPlugins={[remarkGfm]} components={components}>
        {content}
      </ReactMarkdown>
    </div>
  );
}

function MermaidDiagram({ chart }) {
  const rawId = useId();
  const elementId = `mermaid-${rawId.replace(/[^a-zA-Z0-9_-]/g, '')}`;
  const wrappedChart = useMemo(() => wrapMermaidLabels(chart), [chart]);
  const [svg, setSvg] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function renderDiagram() {
      try {
        const result = await mermaid.render(elementId, wrappedChart);
        if (!cancelled) {
          setSvg(result.svg);
          setError('');
        }
      } catch (err) {
        if (!cancelled) {
          setSvg('');
          setError(err instanceof Error ? err.message : 'No se pudo renderizar el diagrama Mermaid.');
        }
      }
    }

    renderDiagram();

    return () => {
      cancelled = true;
    };
  }, [wrappedChart, elementId]);

  if (error) {
    return (
      <div className="md-mermaid-error">
        <p className="mb-2 font-semibold">Error al compilar Mermaid</p>
        <p className="mb-3 text-sm">{error}</p>
        <pre className="overflow-x-auto rounded bg-zinc-950 p-3 text-xs text-zinc-100">
          <code>{chart}</code>
        </pre>
      </div>
    );
  }

  return (
    <div
      className="md-mermaid"
      aria-label="Diagrama Mermaid"
      dangerouslySetInnerHTML={{ __html: svg }}
    />
  );
}

function wrapMermaidLabels(source) {
  return source
    .split('\n')
    .map((line) => {
      let next = line;
      next = next.replace(/(\[[^\]]+\])/g, (match) => wrapDelimitedMermaidLabel(match, '[', ']'));
      next = next.replace(/(\("[^"]+"\))/g, (match) => wrapDelimitedMermaidLabel(match, '("', '")'));
      next = next.replace(/(\{[^{}]+\})/g, (match) => wrapDelimitedMermaidLabel(match, '{', '}'));
      next = next.replace(/\|([^|]+)\|/g, (_match, label) => `|${wrapMermaidText(label, 28)}|`);
      return next;
    })
    .join('\n');
}

function wrapDelimitedMermaidLabel(value, open, close) {
  const inner = value.slice(open.length, value.length - close.length);
  if (inner.includes('<br')) return value;
  return `${open}${wrapMermaidText(inner, 28)}${close}`;
}

function wrapMermaidText(value, targetLength) {
  const text = value.trim();
  if (text.length <= targetLength) return text;

  const chunks = [];
  let rest = text;

  while (rest.length > targetLength) {
    const searchWindow = rest.slice(0, targetLength + 12);
    const breakAt = findNaturalBreak(searchWindow, targetLength);

    if (breakAt <= 0) {
      chunks.push(rest);
      rest = '';
      break;
    }

    chunks.push(rest.slice(0, breakAt).trim());
    rest = rest.slice(breakAt).trim();
  }

  if (rest) chunks.push(rest);
  return chunks.join('<br/>');
}

function findNaturalBreak(text, targetLength) {
  const breakChars = new Set([' ', ':', ',', ';']);
  let bestBefore = -1;
  let bestAfter = -1;

  for (let index = 0; index < text.length; index += 1) {
    if (!breakChars.has(text[index])) continue;
    if (index <= targetLength) {
      bestBefore = index + 1;
    } else if (bestAfter === -1) {
      bestAfter = index + 1;
    }
  }

  return bestBefore > 0 ? bestBefore : bestAfter;
}

function ViewerHeader({ icon: Icon, title, subtitle }) {
  return (
    <div className="border-b border-zinc-200 bg-white px-5 py-4">
      <div className="flex min-w-0 items-start gap-3">
        <div className="mt-1 flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-teal-600 text-white">
          <Icon size={19} />
        </div>
        <div className="min-w-0">
          <h2 className="break-words text-lg font-semibold leading-snug text-zinc-950">{title}</h2>
          <p className="mt-1 break-all text-sm text-zinc-500">{subtitle}</p>
        </div>
      </div>
    </div>
  );
}

function Metric({ label, value }) {
  return (
    <div className="rounded-md border border-zinc-200 bg-white p-3">
      <dt className="text-xs font-medium uppercase text-zinc-500">{label}</dt>
      <dd className="mt-1 break-words text-sm font-semibold text-zinc-900">{value}</dd>
    </div>
  );
}

function EmptyState({ text }) {
  return (
    <div className="rounded-md border border-dashed border-zinc-300 bg-white p-8 text-center text-sm text-zinc-600">
      {text}
    </div>
  );
}

function formatDate(value) {
  try {
    return new Intl.DateTimeFormat('es-CL', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
  } catch {
    return value;
  }
}

function formatBytes(bytes) {
  if (!Number.isFinite(bytes)) return '-';
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

function resolveInternalDocLink(href, currentPath) {
  if (!href || href.startsWith('#')) return null;
  if (/^[a-zA-Z][a-zA-Z\d+.-]*:/.test(href)) return null;
  if (href.startsWith('mailto:')) return null;

  const [rawTarget, hash = ''] = href.split('#');
  if (!rawTarget) return null;

  const currentDir = currentPath?.includes('/')
    ? currentPath.slice(0, currentPath.lastIndexOf('/'))
    : '';
  const base = currentDir ? `${currentDir}/` : '';
  const normalized = normalizePath(`${base}${rawTarget}`);
  const candidates = candidateDocPaths(normalized);

  const target =
    allDocs.find((doc) => candidates.includes(doc.path)) ??
    firstDocInDirectory(normalized);
  return target ? { docId: target.id, path: target.path, hash } : null;
}

function candidateDocPaths(path) {
  const normalized = normalizePath(path);
  const baseCandidates = [
    normalized,
    normalized.startsWith('docs/') ? normalized : `docs/${normalized}`,
    normalized.startsWith('docs/lessons/') ? normalized : `docs/lessons/${normalized}`,
  ];

  return baseCandidates.flatMap((candidate) => {
    if (candidate.endsWith('.md')) return [candidate];
    const clean = candidate.endsWith('/') ? candidate.slice(0, -1) : candidate;
    return [`${clean}/README.md`, `${clean}.md`];
  });
}

function firstDocInDirectory(path) {
  const normalized = normalizePath(path);
  const bases = [
    normalized,
    normalized.startsWith('docs/') ? normalized : `docs/${normalized}`,
    normalized.startsWith('docs/lessons/') ? normalized : `docs/lessons/${normalized}`,
  ].map((candidate) => (candidate.endsWith('/') ? candidate : `${candidate}/`));

  return allDocs
    .filter((doc) => bases.some((base) => doc.path.startsWith(base)))
    .sort((a, b) => a.path.localeCompare(b.path, undefined, { numeric: true }))[0] ?? null;
}

function normalizePath(input) {
  const parts = [];

  for (const part of input.split('/')) {
    if (!part || part === '.') continue;
    if (part === '..') {
      parts.pop();
      continue;
    }
    parts.push(part);
  }

  return parts.join('/');
}

function staticAssetPath(path) {
  const base = import.meta.env.BASE_URL || './';
  const normalizedBase = base.endsWith('/') ? base : `${base}/`;
  return `${normalizedBase}${path.replace(/^\/+/, '')}`;
}

function htmlPreviewDocument(page) {
  if (!page?.content) return '';
  return injectHtmlHead(page.content, page, `<style>
    html, body { width: 100%; min-height: 100%; overflow: hidden; }
    body { transform: scale(0.56); transform-origin: top left; width: 178%; }
    * { scrollbar-width: none; }
    *::-webkit-scrollbar { display: none; }
  </style><script>
    window.addEventListener('click', function (event) {
      event.preventDefault();
      event.stopPropagation();
    }, true);
  <\/script>`);
}

function injectHtmlHead(content, page, extraHead = '') {
  const basePath = page.publicPath.includes('/')
    ? page.publicPath.slice(0, page.publicPath.lastIndexOf('/') + 1)
    : '';
  const baseHref = new URL(staticAssetPath(basePath), window.location.href).href;
  const baseTag = `<base href="${escapeHtmlAttribute(baseHref)}">`;
  return content.replace(/<head([^>]*)>/i, `<head$1>${baseTag}${extraHead}`);
}

function openHtmlPageInNewTab(page) {
  if (!page?.publicPath) return;
  window.open(staticAssetPath(page.publicPath), '_blank', 'noopener,noreferrer');
}

function escapeHtmlAttribute(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('"', '&quot;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;');
}

function buildChallengeSites(pages) {
  const grouped = new Map();

  for (const page of pages) {
    const relative = page.path.replace(/^docs\/challenges\//, '');
    const parts = relative.split('/');
    const siteKey = parts.length > 1 ? parts[0] : relative.replace(/\.html$/, '');

    if (!grouped.has(siteKey)) {
      grouped.set(siteKey, []);
    }
    grouped.get(siteKey).push(page);
  }

  return [...grouped.entries()]
    .map(([siteKey, sitePages]) => {
      const entry =
        sitePages.find((page) => page.path.endsWith('/index.html')) ??
        sitePages.find((page) => page.path.endsWith(`${siteKey}.html`)) ??
        sitePages[0];
      const pagesForSite = sitePages
        .filter((page) => page.path !== `docs/challenges/${siteKey}.html`)
        .sort((a, b) => challengePageOrder(a).localeCompare(challengePageOrder(b), undefined, { numeric: true }));

      return {
        id: `docs/challenges/${siteKey}`,
        title: entry.title,
        path: `docs/challenges/${siteKey}`,
        summary: entry.summary,
        entry,
        pages: pagesForSite,
      };
    })
    .sort((a, b) => a.title.localeCompare(b.title, undefined, { numeric: true }));
}

function challengePageOrder(page) {
  const fileName = page.path.split('/').pop();
  if (fileName === 'index.html') return '00';
  const order = {
    arquitectura: '01',
    'ms-server': '02',
    'ms-cliente': '03',
    estandares: '04',
    extension: '05',
    entrega: '06',
    stack: '07',
  };
  return order[fileName.replace(/\.html$/, '')] ?? fileName;
}

function shortHtmlPageTitle(page) {
  return page.title
    .replace(/^Juego del Gato\s+—\s+/i, '')
    .replace(/^00\s+/, 'Intro');
}

function docFromHash() {
  const { path } = routeFromHash();
  const candidates = candidateDocPaths(path);
  return allDocs.find((doc) => candidates.includes(doc.path)) ?? null;
}

function htmlPageFromHash() {
  const { path } = routeFromHash();
  const normalized = normalizePath(path);
  const candidates = [
    normalized,
    normalized.startsWith('docs/') ? normalized : `docs/${normalized}`,
  ];
  return htmlPages.find((page) => candidates.includes(page.path)) ?? null;
}

function anchorFromHash() {
  return routeFromHash().anchor;
}

function routeFromHash() {
  const raw = window.location.hash.startsWith('#/')
    ? window.location.hash.slice(2)
    : window.location.hash.slice(1);
  const decoded = decodeURIComponent(raw || 'docs/README.md');
  const [path = 'docs/README.md', anchor = ''] = decoded.split('#');
  return {
    path: normalizePath(path || 'docs/README.md'),
    anchor,
  };
}

function pushRoute(path, anchor = '') {
  const next = routeFor(path, anchor);
  if (window.location.hash !== next) {
    window.location.hash = next;
  }
}

function replaceRoute(path, anchor = '') {
  const next = routeFor(path, anchor);
  window.history.replaceState(null, '', `${window.location.pathname}${window.location.search}${next}`);
}

function routeFor(path, anchor = '') {
  const normalized = normalizePath(path);
  return `#/${encodeURI(normalized)}${anchor ? `#${encodeURIComponent(anchor)}` : ''}`;
}

function scrollToHash(hash) {
  if (!hash) return;
  window.setTimeout(() => {
    document.getElementById(decodeURIComponent(hash))?.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    });
  }, 0);
}

function buildDocTree(docs) {
  const root = { name: 'docs', path: 'docs', children: new Map(), files: [] };

  for (const doc of docs) {
    const parts = doc.path.split('/');
    const fileName = parts.pop();
    let current = root;

    for (const part of parts) {
      if (part === 'docs') continue;
      if (!current.children.has(part)) {
        current.children.set(part, {
          name: part,
          path: `${current.path}/${part}`,
          children: new Map(),
          files: [],
        });
      }
      current = current.children.get(part);
    }

    current.files.push({ ...doc, fileName });
  }

  return root;
}

function collectFolderPaths(node) {
  const paths = [];
  for (const child of node.children.values()) {
    paths.push(child.path);
    paths.push(...collectFolderPaths(child));
  }
  return paths;
}

function folderAncestorsForDoc(docPath) {
  const parts = docPath.split('/');
  parts.pop();
  const ancestors = [];
  let current = 'docs';

  for (const part of parts) {
    if (part === 'docs') continue;
    current = `${current}/${part}`;
    ancestors.push(current);
  }

  return ancestors;
}

function countDocs(node) {
  let total = node.files.length;
  for (const child of node.children.values()) {
    total += countDocs(child);
  }
  return total;
}

function buildProjectPackages(projects) {
  const packages = new Map();

  for (const project of projects) {
    const javaRoots = project.files
      .filter((file) => file.path.startsWith('src/main/java/') && file.path.endsWith('.java'))
      .map((file) => file.path.replace(/^src\/main\/java\//, '').split('/').slice(0, -1))
      .filter((parts) => parts.length > 0);

    packages.set(project.id, commonPathPrefix(javaRoots));
  }

  return packages;
}

function commonPathPrefix(paths) {
  if (paths.length === 0) return [];
  const [first] = paths;
  const prefix = [];

  for (let index = 0; index < first.length; index += 1) {
    const candidate = first[index];
    if (paths.every((parts) => parts[index] === candidate)) {
      prefix.push(candidate);
    } else {
      break;
    }
  }

  return prefix;
}

function buildProjectTree(projects, projectPackages) {
  const root = { name: 'proyects', path: 'proyects', children: new Map(), files: [] };

  for (const project of projects) {
    const projectNode = {
      name: project.name,
      path: `proyects/${project.id}`,
      type: 'project',
      project,
      children: new Map(),
      files: [],
    };
    root.children.set(project.id, projectNode);

    for (const file of project.files) {
      addProjectFile(projectNode, project, file, displayProjectPath(file.path, projectPackages.get(project.id)));
    }
  }

  return root;
}

function addProjectFile(root, project, file, treePath) {
  const parts = treePath.split('/');
  const fileName = parts.pop();
  let current = root;

  for (const part of parts) {
    const nextPath = `${current.path}/${part}`;
    if (!current.children.has(part)) {
      current.children.set(part, {
        name: part,
        path: nextPath,
        packageFolder: isPackageSegment(part),
        children: new Map(),
        files: [],
      });
    }
    current = current.children.get(part);
  }

  current.files.push({
    ...file,
    project,
    treePath,
    fileName,
  });
}

function displayProjectPath(filePath, mainPackage = []) {
  const mainJavaPrefix = 'src/main/java/';
  const testJavaPrefix = 'src/test/java/';

  if (mainPackage.length > 0 && filePath.startsWith(mainJavaPrefix)) {
    return compactJavaPackagePath(filePath, mainJavaPrefix, mainPackage);
  }

  if (mainPackage.length > 0 && filePath.startsWith(testJavaPrefix)) {
    return compactJavaPackagePath(filePath, testJavaPrefix, mainPackage);
  }

  return filePath;
}

function compactJavaPackagePath(filePath, prefix, mainPackage) {
  const packagePath = mainPackage.join('/');
  const relative = filePath.slice(prefix.length);

  if (!relative.startsWith(`${packagePath}/`)) return filePath;

  const rest = relative.slice(packagePath.length + 1);
  return `${prefix}${mainPackage.join('.')}/${rest}`;
}

function isPackageSegment(value) {
  return /^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$/.test(value);
}

function folderAncestorsForProjectFile(project, file, projectPackages) {
  const treePath = displayProjectPath(file.path, projectPackages.get(project.id));
  const parts = treePath.split('/');
  parts.pop();

  const ancestors = [`proyects/${project.id}`];
  let current = `proyects/${project.id}`;

  for (const part of parts) {
    current = `${current}/${part}`;
    ancestors.push(current);
  }

  return ancestors;
}

function countProjectFiles(node) {
  let total = node.files.length;
  for (const child of node.children.values()) {
    total += countProjectFiles(child);
  }
  return total;
}

async function prettierConfigFor(language) {
  if (language !== 'java' && language !== 'yaml') return null;

  const prettier = await import('prettier/standalone');

  if (language === 'java') {
    const javaPrettierPlugin = await import('prettier-plugin-java');
    return {
      prettier,
      options: {
        parser: 'java',
        plugins: [javaPrettierPlugin.default ?? javaPrettierPlugin],
        tabWidth: 4,
        printWidth: 100,
      },
    };
  }

  if (language === 'yaml') {
    const yamlPrettierPlugin = await import('prettier/plugins/yaml');
    return {
      prettier,
      options: {
        parser: 'yaml',
        plugins: [yamlPrettierPlugin.default ?? yamlPrettierPlugin],
        tabWidth: 2,
        printWidth: 100,
      },
    };
  }
}

function syntaxLanguageFor(language) {
  if (language === 'yaml') return 'yaml';
  if (language === 'properties') return 'properties';
  return language || 'text';
}

function formatLabelFor(language) {
  if (language === 'java' || language === 'yaml') return `Prettier ${language}`;
  return `Resaltado ${language || 'text'}`;
}

function slugifyHeading(children) {
  return String(children)
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9\s-]/g, '')
    .trim()
    .replace(/\s+/g, '-');
}

function createMarkdownComponents(currentPath, onNavigate) {
  return {
  h1: ({ children }) => <h1 id={slugifyHeading(children)} className="md-h1">{children}</h1>,
  h2: ({ children }) => <h2 id={slugifyHeading(children)} className="md-h2">{children}</h2>,
  h3: ({ children }) => <h3 id={slugifyHeading(children)} className="md-h3">{children}</h3>,
  p: ({ children }) => <p className="md-p">{children}</p>,
  a: ({ children, href }) => {
    if (href?.startsWith('#')) {
      const anchor = href.slice(1);
      return (
        <button
          type="button"
          className="md-a inline text-left"
          onClick={() => {
            pushRoute(currentPath, anchor);
            scrollToHash(anchor);
          }}
        >
          {children}
        </button>
      );
    }

    const internal = resolveInternalDocLink(href, currentPath);

    if (internal) {
      return (
        <button
          type="button"
          className="md-a inline text-left"
          onClick={() => {
            onNavigate?.(internal.docId, internal.hash);
          }}
          title={internal.path}
        >
          {children}
        </button>
      );
    }

    return (
      <a className="md-a" href={href} target="_blank" rel="noreferrer">
        {children}
      </a>
    );
  },
  ul: ({ children }) => <ul className="md-ul">{children}</ul>,
  ol: ({ children }) => <ol className="md-ol">{children}</ol>,
  li: ({ children }) => <li className="md-li">{children}</li>,
  table: ({ children }) => (
    <div className="md-table-wrap">
      <table className="md-table">{children}</table>
    </div>
  ),
  th: ({ children }) => <th className="md-th">{children}</th>,
  td: ({ children }) => <td className="md-td">{children}</td>,
  blockquote: ({ children }) => <blockquote className="md-quote">{children}</blockquote>,
  code: ({ className, children }) => {
    const isBlock = className?.startsWith('language-');
    const language = className?.replace('language-', '').trim();
    const code = String(children).replace(/\n$/, '');

    if (isBlock && language === 'mermaid') {
      return <MermaidDiagram chart={code} />;
    }

    return isBlock ? (
      <SyntaxHighlighter
        language={language || 'text'}
        style={oneDark}
        PreTag="div"
        customStyle={{
          margin: 0,
          padding: 0,
          background: 'transparent',
          fontSize: '0.875rem',
          lineHeight: '1.6',
        }}
        codeTagProps={{
          style: {
            fontFamily: 'JetBrains Mono, ui-monospace, SFMono-Regular, monospace',
          },
        }}
      >
        {code}
      </SyntaxHighlighter>
    ) : (
      <code className="md-inline-code">{children}</code>
    );
  },
  pre: ({ children }) => <pre className="md-pre">{children}</pre>,
  };
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
