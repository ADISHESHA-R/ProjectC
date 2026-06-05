# MySQL deployment

The backend supports **PostgreSQL** (profile `render`, e.g. Render) and **MySQL** (profile `mysql`, e.g. GoDaddy or any MySQL 8+ host). **H2** (`local`) is unchanged for development.

**VPS / systemd checklist:** [DEPLOY_GODADDY_VPS.md](DEPLOY_GODADDY_VPS.md). **Env template:** [`deploy/attendance.env.example`](../deploy/attendance.env.example).

## Activate MySQL

Set the active profile and JDBC settings (environment variables or hosting panel):

```text
SPRING_PROFILES_ACTIVE=mysql
```

Either a full URL:

```text
MYSQL_JDBC_URL=jdbc:mysql://HOST:3306/DATABASE?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8
```

Or separate variables (see `src/main/resources/application-mysql.yml`):

- `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`
- Optional: `MYSQL_USE_SSL` (default `false`), `MYSQL_DDL_AUTO` (default `update`)

## What changed in code

| Area | Change |
|------|--------|
| `pom.xml` | `mysql-connector-j` added **in addition to** PostgreSQL driver |
| `application-mysql.yml` | Datasource + `MySQLDialect` + Hikari |
| `SiteRepository` / `UserRepository` / `NoticeRepository` | Native search SQL uses **`COALESCE` + `LOWER`** (works on PostgreSQL and MySQL) |
| `DatabaseMigration` | Detects **H2** → skips JDBC migrations; **MySQL** vs **PostgreSQL** for `ALTER` / `DROP INDEX` / `MODIFY` / `DROP CHECK` |
| `DatabaseConfig` | `@Profile("mysql")` bean for logging |

## Do not mix profiles blindly

- **Render + PostgreSQL:** keep `SPRING_PROFILES_ACTIVE=render` (or whatever you use today).  
- **Client MySQL:** use `mysql` and a MySQL JDBC URL.  
- Do **not** point `mysql` profile at a PostgreSQL server (wrong driver/protocol).

## Schema

First boot typically uses `spring.jpa.hibernate.ddl-auto=update` (override with `MYSQL_DDL_AUTO`). For strict production control, add Flyway/Liquibase later.

## Version

Target **MySQL 8.0+** (uses `RENAME COLUMN`, `DROP CHECK`, etc.). MySQL 5.7 may need small DDL tweaks.
