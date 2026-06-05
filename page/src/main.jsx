import React, { useEffect, useId, useMemo, useRef, useState } from 'react';
import { createRoot } from 'react-dom/client';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import mermaid from 'mermaid';
import {
  BookOpen,
  ChevronDown,
  ChevronRight,
  Code2,
  FileCode2,
  FileText,
  Folder,
  FolderTree,
  GraduationCap,
  Layers3,
  LocateFixed,
  Package,
  PanelLeftClose,
  Search,
  Server,
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
const firstProjectFile =
  firstProject?.files.find((file) => file.path === 'README.md') ??
  firstProject?.files.find((file) => file.path === 'pom.xml') ??
  firstProject?.files[0];

function App() {
  const [mode, setMode] = useState('lessons');
  const [query, setQuery] = useState('');
  const [selectedDocId, setSelectedDocId] = useState(docFromHash()?.id ?? firstDoc?.id ?? '');
  const [selectedProjectId, setSelectedProjectId] = useState(firstProject?.id ?? '');
  const [selectedFileId, setSelectedFileId] = useState(firstProjectFile?.id ?? '');
  const [sidebarOpen, setSidebarOpen] = useState(true);

  useEffect(() => {
    if (!window.location.hash) {
      replaceRoute(firstDoc?.path ?? 'docs/README.md');
      return undefined;
    }

    function syncFromHash() {
      const doc = docFromHash();
      if (doc) {
        setMode('lessons');
        setSelectedDocId(doc.id);
        const hash = anchorFromHash();
        scrollToHash(hash);
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

  return (
    <div className="min-h-screen bg-zinc-50 text-zinc-950">
      <header className="border-b border-zinc-200 bg-white">
        <div className="mx-auto flex max-w-[1600px] flex-col gap-4 px-4 py-4 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-md bg-emerald-600 text-white">
              <GraduationCap size={24} aria-hidden="true" />
            </div>
            <div>
              <h1 className="text-xl font-semibold leading-tight text-zinc-950">DSY1103 Fullstack I</h1>
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
                placeholder="Buscar en documentos y codigo"
              />
            </div>
            <div className="grid grid-cols-2 rounded-md border border-zinc-300 bg-zinc-100 p-1">
              <ModeButton active={mode === 'lessons'} icon={BookOpen} onClick={() => setMode('lessons')}>
                Lecciones
              </ModeButton>
              <ModeButton active={mode === 'projects'} icon={Code2} onClick={() => setMode('projects')}>
                Proyectos
              </ModeButton>
            </div>
          </div>
        </div>
      </header>

      <main className="mx-auto grid max-w-[1600px] gap-4 px-4 py-4 lg:grid-cols-[minmax(280px,380px)_1fr]">
        {sidebarOpen ? (
          <aside className="min-h-[calc(100vh-130px)] overflow-hidden rounded-md border border-zinc-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-zinc-200 px-4 py-3">
              <div className="flex items-center gap-2 text-sm font-semibold">
                {mode === 'lessons' ? <Layers3 size={18} /> : <FolderTree size={18} />}
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
        ) : (
          <button
            className="flex h-12 items-center justify-center gap-2 rounded-md border border-zinc-200 bg-white text-sm font-medium shadow-sm lg:col-span-1"
            onClick={() => setSidebarOpen(true)}
          >
            <FolderTree size={18} />
            Mostrar navegador
          </button>
        )}

        <section className={sidebarOpen ? 'min-w-0' : 'min-w-0 lg:col-span-2'}>
          {mode === 'lessons' ? (
            <MarkdownViewer doc={selectedDoc} onNavigate={navigateToDoc} />
          ) : (
            <ProjectViewer project={selectedProject} file={selectedFile} onNavigateDoc={navigateToDoc} setMode={setMode} />
          )}
        </section>
      </main>
    </div>
  );
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

function docFromHash() {
  const { path } = routeFromHash();
  const candidates = candidateDocPaths(path);
  return allDocs.find((doc) => candidates.includes(doc.path)) ?? null;
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
