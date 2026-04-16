# 🎯 Lección 11 — Índice de Contenidos

## 📚 Documentos (en orden de lectura)

### 1. **[Objetivo y Alcance](01_objetivo_y_alcance.md)** (5 min)
Qué aprenderás, por qué importa, qué no cubre.

### 2. **[Guión Paso a Paso](02_guion_paso_a_paso.md)** (30 min) ⭐ EMPIEZA AQUÍ
Instrucciones detalladas para:
- Configurar perfiles de Spring Boot
- Crear y usar archivo `.env`
- Configurar MySQL local (XAMPP)
- Configurar Supabase (PostgreSQL en la nube)

### 3. **[Resumen de Archivos](07_resumen_archivos.md)** (5 min)
Referencia rápida: qué va en cada archivo de configuración.

### 4. **[Guía IntelliJ IDEA](06_guia_intellij_env.md)** (5 min)
Cómo cargar variables de entorno desde `.env` en IntelliJ.

### 5. **[MySQL vs PostgreSQL](03_mysql_vs_postgresql.md)** (10 min)
Comparación técnica entre las dos bases de datos.

---

## 🚀 Quick Start

**Si solo quieres que funcione rápido:**

1. Ve a **[Guión Paso a Paso](02_guion_paso_a_paso.md)** → sección "Configuración con Perfiles de Spring Boot"
2. Elige un perfil (H2 para empezar)
3. Ejecuta: `./mvnw spring-boot:run`

**Si quieres usar Supabase:**

1. Ve a **[Guión Paso a Paso](02_guion_paso_a_paso.md)** → sección "Variables de Entorno y Archivo `.env`"
2. Copia `.env.example` a `.env`
3. Rellena las variables de Supabase
4. Ejecuta: `./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=supabase"`

**Si usas IntelliJ IDEA:**

1. Instala el plugin **EnvFile**
2. Ve a **[Guía IntelliJ](06_guia_intellij_env.md)**
3. Configura la Run Configuration

---

## 📋 Archivos de Configuración

```
Tickets/
├── src/main/resources/
│   ├── application.yml              ← Base común
│   ├── application-h2.yml           ← Perfil: BD en memoria
│   ├── application-mysql.yml        ← Perfil: MySQL local
│   └── application-supabase.yml     ← Perfil: Supabase PostgreSQL
├── .env.example                     ← Plantilla (commitear ✅)
├── .env                             ← Local con credenciales (NO commitear ❌)
└── .gitignore                       ← Contiene .env
```

---

## 🎓 Checklist de Aprendizaje

Al terminar, deberías poder:

- [ ] Explicar qué son los perfiles de Spring Boot
- [ ] Activar un perfil desde línea de comandos, SO e IntelliJ
- [ ] Crear y usar un archivo `.env` de forma segura
- [ ] Conectar a H2, MySQL y Supabase sin cambiar código Java
- [ ] Diferenciar cuándo usar cada base de datos
- [ ] Proteger credenciales con variables de entorno

---

*[← Volver a Lecciones](../)*
