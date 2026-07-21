# Student Management System — Backend (Spring Boot + PostgreSQL)

## 1) Dependencies (Spring Initializr)

If you ever rebuild from start.spring.io, pick these:
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Validation
- Spring Security
- Lombok

This project also needs **JWT** (`io.jsonwebtoken:jjwt-api/impl/jackson`) which is
already declared in `pom.xml` — Maven will download it automatically the first
time you build, no manual step needed.

Requirements on your machine:
- **Java 17+** (`java -version`)
- **Maven** (or use the included `mvnw` wrapper if you add one — IntelliJ/VS Code's
  Java extension bundles Maven too)
- **Docker Desktop** (for PostgreSQL) — or a local Postgres install if you prefer

---

## 2) Project structure

```
sms-backend/
├── pom.xml
├── docker-compose.yml
└── src/main/
    ├── resources/
    │   └── application.properties      ← DB connection, JWT secret, CORS origin
    └── java/com/sms/backend/
        ├── SmsBackendApplication.java  ← main() entry point
        ├── entity/                     ← @Entity JPA models (9 tables)
        ├── repository/                 ← JpaRepository interfaces
        ├── dto/                        ← request/response shapes sent to React
        ├── service/                    ← business logic (validation, role scoping,
        │                                  computed fields like grade/status)
        ├── controller/                 ← @RestController REST endpoints
        ├── security/                   ← JwtUtil, JwtAuthFilter, CurrentUser, AuthUtil
        ├── config/                     ← SecurityConfig (CORS + JWT filter chain)
        └── exception/                  ← GlobalExceptionHandler (plain-text errors)
```

**How a request flows (start → end):**
```
React (axios) → Controller (@RestController) → Service (business logic,
role filtering, validation) → Repository (JpaRepository) → PostgreSQL
```
The `JwtAuthFilter` runs before every controller call, reads the `Authorization:
Bearer <token>` header, and attaches the logged-in user's id/role to the request
so services can filter data (e.g. a Student only ever sees their own fees).

---

## 3) How to run it — step by step

**Step 1 — Start PostgreSQL (Docker):**
```bash
cd sms-backend
docker compose up -d
```
This starts Postgres on `localhost:5432` (db: `sms_db`, user/pass: `postgres`/`postgres`)
and pgAdmin on `http://localhost:5050` (login: `admin@sms.com` / `admin`) if you
want a GUI to inspect tables.

**Step 2 — Check `application.properties`:**
Open `src/main/resources/application.properties` and confirm the DB
username/password match what's in `docker-compose.yml` (they do, by default).

**Step 3 — Run the Spring Boot app:**
- In VS Code: open the folder, let the Java extension index it, then run
  `SmsBackendApplication.java` (▶ button above `main`)
- Or from terminal:
```bash
mvn spring-boot:run
```
First run downloads all dependencies — this can take a few minutes.

**Step 4 — Confirm it's up:**
Hibernate will auto-create all 9 tables on startup (`ddl-auto=update`). You
should see `Tomcat started on port 8080` in the logs, and:
```
GET http://localhost:8080/api/auth/login
```
should respond (405/empty body is fine — it proves the server is alive).

**Step 5 — Create your first Admin account:**
Since there's no data yet, use the React `/signup` page (Admin-only sign-up,
exactly as your frontend already expects) — it calls
`POST /api/auth/register-admin` and creates the first user for you. From there,
log in as Admin and use the **Users** page to create Teacher/Student login
accounts, then link them to Student/Teacher profiles.

---

## 4) Connecting your React frontend

In your React project root, create (or edit) `.env`:
```
VITE_API_BASE_URL=http://localhost:8080/api
```
Your `api/client.js` already reads this exact variable — nothing else to change.
Restart `npm run dev` after editing `.env` (Vite only reads it at startup).

If your React dev server runs on a different port than `5173`, update
`app.cors.allowed-origins` in `application.properties` to match (comma-separate
multiple origins if needed).

---

## 5) About re-doing the frontend (your question #6)

**You don't need to touch your existing frontend code.** It already reads the
API base URL from `VITE_API_BASE_URL` in `.env` — and `.env` is git-ignored by
default in Vite projects, so switching it to point at this new backend locally
**will not affect your already-deployed host**. To point your *deployed* site
at this backend later, you'd change the environment variable in your hosting
provider's dashboard (Vercel/Netlify/etc.) — never in the committed code — so
nothing gets pushed to GitHub by accident.

---

## 6) Quick endpoint reference

| Method | Endpoint | Who |
|---|---|---|
| POST | /api/auth/login | Public |
| POST | /api/auth/register-admin | Public |
| GET/POST/PUT/DELETE | /api/users | Admin |
| GET | /api/students | Admin, Teacher |
| POST/PUT/DELETE | /api/students | Admin |
| GET | /api/teachers | Admin (all), Teacher (own only) |
| POST/PUT/DELETE | /api/teachers | Admin |
| GET | /api/classes | Admin, Teacher, Student |
| POST/PUT/DELETE | /api/classes | Admin |
| GET | /api/subjects | Admin (all), Teacher (own only) |
| POST/PUT/DELETE | /api/subjects | Admin |
| GET | /api/attendance | Admin, Teacher (all); Student (own only) |
| POST/PUT/DELETE | /api/attendance | Admin, Teacher |
| GET | /api/fees | Admin (all); Student (own only) |
| POST/PUT/DELETE | /api/fees | Admin |
| GET | /api/results | Admin (all); Student (own only) |
| POST/PUT/DELETE | /api/results | Admin |
| GET | /api/dashboard/summary | Admin |

All error responses are plain text (e.g. `"This student is already marked for
this date."`) so `err.response?.data` in your React code displays them directly.
