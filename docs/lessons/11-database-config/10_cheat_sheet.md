# 🚀 Cheat Sheet — Referencia Rápida Lección 11

## Tres comandos para arrancar

```bash
# H2 (en memoria, rápido, sin BD)
./mvnw spring-boot:run

# MySQL (local con XAMPP)
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

# Supabase (nube)
export SPRING_PROFILES_ACTIVE=supabase && ./mvnw spring-boot:run
```

---

## Estructura de archivos (qué va dónde)

```yaml
# application.yml — Todas las apps
spring:
  profiles:
    active: h2          # Default
  jpa:
    hibernate:
      ddl-auto: update

# application-h2.yml — Solo para perfil h2
spring:
  datasource:
    url: jdbc:h2:mem:ticketsdb
    driver-class-name: org.h2.Driver

# application-mysql.yml — Solo para perfil mysql
spring:
  datasource:
    url: ${MYSQL_URL:...}
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:}

# application-supabase.yml — Solo para perfil supabase
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

---

## Variables de Entorno (`.env`)

```env
# Perfil
SPRING_PROFILES_ACTIVE=mysql

# Para MySQL
MYSQL_URL=jdbc:mysql://localhost:3306/tickets_db?useSSL=false&serverTimezone=America/Santiago
MYSQL_USERNAME=root
MYSQL_PASSWORD=

# Para Supabase
DB_HOST=db.xxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USER=postgres
DB_PASSWORD=your-password
```

---

## Cargar `.env` desde IntelliJ

1. **Plugin:** Instala "EnvFile"
2. **Run** → **Edit Configurations**
3. **EnvFile** tab → ✅ Enable → Selecciona `.env`
4. ▶️ Ejecuta

---

## Matriz de Decisión

| Caso | Perfil | Comando |
|------|--------|---------|
| Tests rápidos | h2 | `./mvnw spring-boot:run` |
| Desarrollo local | mysql | `-Dspring-boot.run.arguments="--spring.profiles.active=mysql"` |
| Entrega final | supabase | `export SPRING_PROFILES_ACTIVE=supabase` |

---

## Cómo sé que funcionó?

Busca en los logs:
```
The following profiles are active: [tu-perfil]
HikariPool-1 - Connection is working...
```

Luego: http://localhost:8080/ticket-app/tickets

---

## Seguridad

✅ `.env.example` → commitear  
❌ `.env` → NO commitear (en `.gitignore`)  
🔒 Credenciales reales → solo en `.env` local  

---

## Troubleshooting

**"Connection refused"**
→ Verifica que BD está corriendo y credenciales son correctas

**"Variables vacías"**
→ Instala plugin EnvFile en IntelliJ o define manualmente en Edit Configurations

**"¿Cuál perfil estoy usando?"**
→ Mira los logs al arrancar: `The following profiles are active: ...`

---

*Más detalles: [Lección 11](00_indice.md)*
