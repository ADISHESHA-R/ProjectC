# Deploy backend + MySQL on a client VPS (e.g. GoDaddy VPS with SSH)

This app needs **Linux + SSH + Java 17** and **MySQL 8+**. **Shared PHP-only hosting** usually cannot run this Spring Boot service—use a **VPS** (or equivalent).

Related: [MYSQL.md](MYSQL.md) (profile + JDBC), [application-mysql.yml](../src/main/resources/application-mysql.yml).

---

## 1. Build the JAR (developer machine)

```bash
cd ProjectC
mvn clean package
```

Copy `target/attendance-backend-0.0.1-SNAPSHOT.jar` to the server, e.g. as `/opt/attendance/app.jar`.

---

## 2. MySQL on the server (or GoDaddy managed MySQL)

1. Create database (e.g. `attendance_prod`) with **utf8mb4**.
2. Create user + password; grant **ALL** on that database (for first boot with `ddl-auto=update`).
3. If MySQL is **remote** to the app: allow host firewall + MySQL user host (`'appuser'@'app-server-ip'`) and use TLS if offered (`MYSQL_USE_SSL=true` + JDBC params per provider).

---

## 3. Server directories

```bash
sudo mkdir -p /opt/attendance/uploads
sudo useradd -r -s /bin/false attendance   # if not exists
sudo chown -R attendance:attendance /opt/attendance
```

Place:

- `/opt/attendance/app.jar`
- `/opt/attendance/attendance.env` — copy from [deploy/attendance.env.example](../deploy/attendance.env.example), fill real values, then:

```bash
sudo chmod 600 /opt/attendance/attendance.env
```

**Choose one DB style in the env file:** either set `MYSQL_JDBC_URL` **or** leave it unset and use `MYSQL_HOST` + `MYSQL_PORT` + `MYSQL_DATABASE` + `MYSQL_USER` + `MYSQL_PASSWORD` (see example file comments).

---

## 4. systemd unit

Create `/etc/systemd/system/attendance.service`:

```ini
[Unit]
Description=Attendance API (Spring Boot)
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=attendance
Group=attendance
WorkingDirectory=/opt/attendance
EnvironmentFile=/opt/attendance/attendance.env
ExecStart=/usr/bin/java -jar /opt/attendance/app.jar
Restart=on-failure
RestartSec=15
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Install Java 17 on the server (`openjdk-17-jre-headless` or equivalent), then:

```bash
sudo systemctl daemon-reload
sudo systemctl enable attendance
sudo systemctl start attendance
sudo systemctl status attendance
journalctl -u attendance -f
```

---

## 5. Nginx reverse proxy + HTTPS (recommended)

Example `/etc/nginx/sites-available/attendance-api` (replace domain and cert paths):

```nginx
server {
    listen 443 ssl http2;
    server_name api.clientdomain.com;

    ssl_certificate     /etc/letsencrypt/live/api.clientdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.clientdomain.com/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Obtain certs with **certbot** (Let’s Encrypt) or use GoDaddy-provided SSL. Reload nginx: `sudo nginx -t && sudo systemctl reload nginx`.

Set **`CORS_ALLOWED_ORIGINS`** in `attendance.env` to the SPA’s **https** origin(s).

---

## 6. Smoke tests

- `curl -sk https://api.clientdomain.com/actuator/health` (if actuator is exposed; adjust path/security).
- Login + **site search**, **user search**, **notices** (native SQL is portable).
- One file upload if used (`UPLOAD_DIR` must be writable).

---

## 7. Checklist for the client

| Step | Done |
|------|------|
| VPS with SSH + Java 17 | ☐ |
| MySQL 8 + DB + user + privileges | ☐ |
| `app.jar` + `attendance.env` (from example) + `uploads/` | ☐ |
| `JWT_SECRET` strong and unique | ☐ |
| `SPRING_PROFILES_ACTIVE=mysql` | ☐ |
| systemd service enabled | ☐ |
| HTTPS + nginx → port 8080 | ☐ |
| `CORS_ALLOWED_ORIGINS` if SPA separate | ☐ |

---

## 8. Do not mix profiles

- **GoDaddy MySQL:** `SPRING_PROFILES_ACTIVE=mysql` + MySQL JDBC vars.  
- **Render PostgreSQL:** keep `render` + `DATABASE_URL` / Postgres settings — do not point `mysql` profile at Postgres.
