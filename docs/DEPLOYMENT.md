# Deployment notes — `ProjectC`

## Build

```bash
mvn clean package -DskipTests
java -jar target/attendance-backend-0.0.1-SNAPSHOT.jar
```

## Required environment (production / Render)

| Variable | Purpose |
|----------|---------|
| `SPRING_PROFILES_ACTIVE` | `render` for PostgreSQL profile |
| `DATABASE_URL` or `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USERNAME` / `DB_PASSWORD` | PostgreSQL — if using `DATABASE_URL`, use `jdbc:postgresql://HOST:PORT/DB?...&user=...&password=...` or host-only JDBC + separate credentials; **do not** use `jdbc:postgresql://USER:PASSWORD@HOST/...` (JDBC driver misparses the host). |
| `JWT_SECRET` | ≥32 chars |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins (optional) |
| `JWT_EXPIRATION` | Optional |
| `PUBLIC_FEEDBACK_TOKEN_VALID_DAYS` | Optional (default 30) |
| `PUBLIC_FEEDBACK_RATE_LIMIT_PER_MINUTE` | Optional (default 120 requests/IP/minute on `/api/public/feedback/**`) |

### Render: `UnknownHostException: dpg-…-a`

The short **internal** hostname from the Postgres **Info** page only resolves on Render’s **private** network. If your Web Service still cannot resolve it (wrong region, networking, or manual env), set **`DB_HOST`** to the **full hostname** from the **External Database URL** (the part between `@` and `:5432`, ending in something like `…-postgres.render.com`). Keep `sslmode=require` (already in the JDBC URL). Prefer linking the database to the service in the Render dashboard so connection details stay correct.

## Schema

- Hibernate **`ddl-auto: update`** (local + current Render profile) applies JPA entity changes, including new **job-site** tables (`site_*`).
- For strict production control, plan a follow-up: **Flyway** + `ddl-auto: validate`.

## New admin APIs (job-site data)

Base: `/api/admin/sites/{siteId}/…` (admin JWT).

| Method | Path |
|--------|------|
| `PUT` | `/job-data/attendance-register-cells` — body `{ "cells": [ { "employeeUserId", "date", "code": "P"\|"A"\|"S"\|"HQ"\|"LS"\|"IN" \| null } ] }` |
| `GET`/`PUT` | `/job-data/advance-expense-lines` |
| `GET`/`PUT` | `/job-data/technician-payments` |
| `GET`/`PUT` | `/job-data/tool-issues` |
| `GET`/`PUT` | `/job-data/behaviour-report` — JSON body |
| `GET`/`PUT` | `/job-data/challenge-lines` |
| `GET` | `/api/meta/challenge-line-heads` — optional preset labels for challenge rows |

`GET …/attendance-register` merges **Attendance** (P/A) with **register cell** overrides.

## Public endpoints

- `/api/public/feedback/**` — rate-limited filter, no JWT.
- Prefer HTTPS and sensible token expiry.
