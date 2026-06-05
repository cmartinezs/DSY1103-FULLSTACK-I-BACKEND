import { promises as fs } from 'node:fs';
import path from 'node:path';

const pageDir = process.cwd();
const repoRoot = path.resolve(pageDir, '..');
const projectsRoot = (await pathExists(path.join(repoRoot, 'projects')))
  ? path.join(repoRoot, 'projects')
  : path.join(repoRoot, 'proyects');
const outDir = path.join(pageDir, 'src', 'generated');
const outFile = path.join(outDir, 'content.json');
const publicDir = path.join(pageDir, 'public');

const projectNames = [
  'Tickets',
  'Tickets-10',
  'Tickets-11',
  'Tickets-12',
  'Tickets-13',
  'Tickets-14',
  'Tickets-15',
  'Tickets-16',
  'Tickets-17',
  'Tickets-18',
  'Tickets-19',
  'NotificationService',
  'AuditService',
  'SearchService',
  'SLAService',
];

const textExtensions = new Set([
  '.java',
  '.xml',
  '.yml',
  '.yaml',
  '.properties',
  '.sql',
  '.md',
  '.txt',
  '.json',
  '.env',
  '.example',
  '.cmd',
]);

const skipDirs = new Set([
  '.git',
  '.idea',
  '.claude',
  '.codex',
  'target',
  'node_modules',
  'dist',
  'build',
  '.mvn',
]);

async function pathExists(filePath) {
  try {
    await fs.access(filePath);
    return true;
  } catch {
    return false;
  }
}

async function readText(filePath) {
  return fs.readFile(filePath, 'utf8');
}

async function listFiles(root, options = {}) {
  const files = [];
  const maxFiles = options.maxFiles ?? 600;

  async function walk(current) {
    if (files.length >= maxFiles) return;
    const entries = await fs.readdir(current, { withFileTypes: true });
    entries.sort((a, b) => a.name.localeCompare(b.name));

    for (const entry of entries) {
      if (files.length >= maxFiles) return;
      const absolute = path.join(current, entry.name);
      const relative = path.relative(root, absolute).replaceAll(path.sep, '/');

      if (entry.isDirectory()) {
        if (!skipDirs.has(entry.name)) {
          await walk(absolute);
        }
        continue;
      }

      if (!entry.isFile()) continue;
      if (options.filter && !options.filter(relative, absolute)) continue;
      files.push({ absolute, relative });
    }
  }

  await walk(root);
  return files;
}

function lessonTitle(folderName, readmeContent) {
  const firstHeading = readmeContent.match(/^#\s+(.+)$/m);
  if (firstHeading) return firstHeading[1].trim();
  return folderName.replace(/^\d+-/, '').replaceAll('-', ' ');
}

async function buildLessons() {
  const lessonsRoot = path.join(repoRoot, 'docs', 'lessons');
  const entries = await fs.readdir(lessonsRoot, { withFileTypes: true });
  const dirs = entries
    .filter((entry) => entry.isDirectory())
    .map((entry) => entry.name)
    .sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));

  const lessons = [];

  for (const dir of dirs) {
    const absolute = path.join(lessonsRoot, dir);
    const files = await listFiles(absolute, {
      maxFiles: 80,
      filter: (relative) => relative.endsWith('.md'),
    });

    const docs = [];
    for (const file of files) {
      const content = await readText(file.absolute);
      docs.push({
        id: `${dir}/${file.relative}`,
        title: titleFromMarkdown(file.relative, content),
        path: `docs/lessons/${dir}/${file.relative}`,
        content,
      });
    }

    const readme = docs.find((doc) => doc.path.endsWith('/README.md')) ?? docs[0];
    lessons.push({
      id: dir,
      title: lessonTitle(dir, readme?.content ?? ''),
      path: `docs/lessons/${dir}`,
      docs,
    });
  }

  const rootDocs = [];
  const rootFiles = await listFiles(lessonsRoot, {
    maxFiles: 40,
    filter: (relative) => !relative.includes('/') && relative.endsWith('.md'),
  });

  for (const file of rootFiles) {
    const content = await readText(file.absolute);
    rootDocs.push({
      id: file.relative,
      title: titleFromMarkdown(file.relative, content),
      path: `docs/lessons/${file.relative}`,
      content,
    });
  }

  return { lessons, rootDocs };
}

async function buildAllDocs() {
  const docsRoot = path.join(repoRoot, 'docs');
  const files = await listFiles(docsRoot, {
    maxFiles: 900,
    filter: (relative) => relative.endsWith('.md'),
  });

  const docs = [];
  for (const file of files) {
    const content = await readText(file.absolute);
    docs.push({
      id: `docs/${file.relative}`,
      title: titleFromMarkdown(file.relative, content),
      path: `docs/${file.relative}`,
      content,
    });
  }

  return docs;
}

async function buildHtmlPages() {
  const challengesRoot = path.join(repoRoot, 'docs', 'challenges');
  if (!(await pathExists(challengesRoot))) return [];

  const files = await listFiles(challengesRoot, {
    maxFiles: 120,
    filter: (relative) => relative.endsWith('.html'),
  });

  const publicChallengesRoot = path.join(publicDir, 'docs', 'challenges');
  await fs.rm(publicChallengesRoot, { recursive: true, force: true });
  await fs.mkdir(publicChallengesRoot, { recursive: true });

  const pages = [];
  for (const file of files) {
    const content = await readText(file.absolute);
    const outPath = path.join(publicChallengesRoot, file.relative);
    await fs.mkdir(path.dirname(outPath), { recursive: true });
    await fs.copyFile(file.absolute, outPath);

    pages.push({
      id: `docs/challenges/${file.relative}`,
      title: titleFromHtml(file.relative, content),
      path: `docs/challenges/${file.relative}`,
      publicPath: `docs/challenges/${file.relative}`,
      summary: htmlSummary(content),
      content,
    });
  }

  return pages;
}

function titleFromMarkdown(relative, content) {
  const firstHeading = content.match(/^#\s+(.+)$/m);
  if (firstHeading) return firstHeading[1].trim();
  return relative.replace(/\.md$/, '').replaceAll('_', ' ').replaceAll('-', ' ');
}

function titleFromHtml(relative, content) {
  const title = content.match(/<title>([\s\S]*?)<\/title>/i)?.[1];
  if (title) return decodeHtml(title).replace(/\s+/g, ' ').trim();
  const heading = content.match(/<h1[^>]*>([\s\S]*?)<\/h1>/i)?.[1];
  if (heading) return stripHtml(heading).trim();
  return relative.replace(/\.html$/, '').replaceAll('_', ' ').replaceAll('-', ' ');
}

function htmlSummary(content) {
  const text = stripHtml(content).replace(/\s+/g, ' ').trim();
  return text.length > 180 ? `${text.slice(0, 180)}...` : text;
}

function stripHtml(value) {
  return decodeHtml(value.replace(/<script[\s\S]*?<\/script>/gi, '').replace(/<style[\s\S]*?<\/style>/gi, '').replace(/<[^>]+>/g, ' '));
}

function decodeHtml(value) {
  return value
    .replaceAll('&amp;', '&')
    .replaceAll('&lt;', '<')
    .replaceAll('&gt;', '>')
    .replaceAll('&quot;', '"')
    .replaceAll('&#39;', "'");
}

function projectDescription(name, pomContent, readmeContent) {
  if (readmeContent) {
    const paragraph = readmeContent
      .split('\n')
      .map((line) => line.trim())
      .find((line) => line && !line.startsWith('#') && !line.startsWith('```'));
    if (paragraph) return paragraph;
  }

  const artifact = pomContent?.match(/<artifactId>([^<]+)<\/artifactId>/)?.[1];
  return artifact ? `Proyecto Maven ${artifact}` : 'Proyecto Java Spring Boot Maven';
}

function isRelevantProjectFile(relative) {
  const normalized = relative.replaceAll(path.sep, '/');
  if (normalized === 'pom.xml') return true;
  if (normalized === 'README.md') return true;
  if (normalized === '.env.example') return true;
  if (normalized.startsWith('src/main/')) return textExtensions.has(path.extname(normalized));
  if (normalized.startsWith('src/test/')) return textExtensions.has(path.extname(normalized));
  if (normalized.startsWith('src/main/resources/db/migration/')) return true;
  return false;
}

async function buildProjects() {
  const projects = [];

  for (const name of projectNames) {
    const absolute = path.join(projectsRoot, name);
    if (!(await pathExists(absolute))) continue;

    const files = await listFiles(absolute, {
      maxFiles: 450,
      filter: isRelevantProjectFile,
    });

    const fileItems = [];
    for (const file of files) {
      const stat = await fs.stat(file.absolute);
      if (stat.size > 120_000) continue;
      const ext = path.extname(file.relative);
      if (!textExtensions.has(ext) && file.relative !== 'pom.xml' && file.relative !== '.env.example') {
        continue;
      }
      fileItems.push({
        id: `${name}/${file.relative}`,
        path: file.relative,
        language: languageFor(file.relative),
        size: stat.size,
        content: await readText(file.absolute),
      });
    }

    const pom = fileItems.find((file) => file.path === 'pom.xml')?.content ?? '';
    const readme = fileItems.find((file) => file.path === 'README.md')?.content ?? '';

    projects.push({
      id: name,
      name,
      kind: name.startsWith('Tickets') ? 'Leccion Tickets' : 'Microservicio',
      description: projectDescription(name, pom, readme),
      fileCount: fileItems.length,
      files: fileItems,
    });
  }

  return projects;
}

function languageFor(relative) {
  const ext = path.extname(relative);
  if (ext === '.java') return 'java';
  if (ext === '.xml') return 'xml';
  if (ext === '.yml' || ext === '.yaml') return 'yaml';
  if (ext === '.properties') return 'properties';
  if (ext === '.sql') return 'sql';
  if (ext === '.md') return 'markdown';
  if (ext === '.json') return 'json';
  return 'text';
}

const lessons = await buildLessons();
const allDocs = await buildAllDocs();
const htmlPages = await buildHtmlPages();
const projects = await buildProjects();
const generatedAt = new Date().toISOString();

await fs.mkdir(outDir, { recursive: true });
await fs.writeFile(
  outFile,
  JSON.stringify(
    {
      generatedAt,
      allDocs,
      htmlPages,
      lessons: lessons.lessons,
      rootDocs: lessons.rootDocs,
      projects,
    },
    null,
    2,
  ),
);

console.log(
  `Generated ${allDocs.length} docs, ${htmlPages.length} html pages, ${lessons.lessons.length} lessons and ${projects.length} projects.`,
);
