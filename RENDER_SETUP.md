# Render Service Configuration Guide

## Database Setup

### Step 1: Create PostgreSQL Database

1. Go to Render Dashboard → "New +" → "PostgreSQL"
2. Configure:
   - **Name**: `attendance-db`
   - **Plan**: Free tier
   - **Database**: `attendance_db`
3. After creation, copy connection details:
   - Internal Database URL
   - Host
   - Port
   - Database name
   - Username
   - Password

**Important**: All services (dev, qa, prod) will use this same database.

## Service Mapping

| Branch | Service Name | Environment | Profile | Auto-Deploy | Purpose |
|--------|-------------|-------------|---------|-------------|---------|
| `dev` | `attendance-dev` | Development | `render` | ON | Backend development |
| `qa` | `attendance-qa` | QA Testing | `render` | OFF | Frontend integration |
| `main` | `attendance-prod` | Production | `render` | OFF | Production/Client demos |

## Render Setup Steps

### Service 1: Dev (Create Now)

1. Go to Render Dashboard → "New +" → "Web Service"
2. Connect GitHub repository: `Attendance-SystemC/attendance-backend`
3. Configure:
   - **Name**: `attendance-dev`
   - **Language**: **Docker** (important!)
   - **Branch**: `dev`
   - **Build Command**: (Leave blank - Docker builds automatically)
   - **Start Command**: (Leave blank - Docker runs automatically)
   - **Dockerfile Path**: `Dockerfile` (or leave blank for default)
   - **Auto-Deploy**: ON ✅
4. Add Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE=render
   PORT=8080
   DB_HOST=your-db-host.onrender.com
   DB_PORT=5432
   DB_NAME=attendance_db
   DB_USERNAME=your-username
   DB_PASSWORD=your-password
   JWT_SECRET=your-dev-secret-key
   ```
5. Click "Create Web Service"

### Service 2: QA (Create When Frontend Starts)

1. Go to Render Dashboard → "New +" → "Web Service"
2. Connect GitHub repository: `Attendance-SystemC/attendance-backend`
3. Configure:
   - **Name**: `attendance-qa`
   - **Language**: **Docker** (important!)
   - **Branch**: `qa`
   - **Build Command**: (Leave blank - Docker builds automatically)
   - **Start Command**: (Leave blank - Docker runs automatically)
   - **Dockerfile Path**: `Dockerfile` (or leave blank for default)
   - **Auto-Deploy**: OFF ❌ (Manual only - keep stable)
4. Add Environment Variables (SAME database as dev):
   ```
   SPRING_PROFILES_ACTIVE=render
   PORT=8080
   DB_HOST=your-db-host.onrender.com  # SAME
   DB_PORT=5432
   DB_NAME=attendance_db               # SAME
   DB_USERNAME=your-username           # SAME
   DB_PASSWORD=your-password           # SAME
   JWT_SECRET=your-qa-secret-key
   ```
5. Click "Create Web Service"

### Service 3: Prod (Create Later)

1. Go to Render Dashboard → "New +" → "Web Service"
2. Connect GitHub repository: `Attendance-SystemC/attendance-backend`
3. Configure:
   - **Name**: `attendance-prod`
   - **Language**: **Docker** (important!)
   - **Branch**: `main`
   - **Build Command**: (Leave blank - Docker builds automatically)
   - **Start Command**: (Leave blank - Docker runs automatically)
   - **Dockerfile Path**: `Dockerfile` (or leave blank for default)
   - **Auto-Deploy**: OFF ❌ (Manual only)
4. Add Environment Variables (SAME database):
   ```
   SPRING_PROFILES_ACTIVE=render
   PORT=8080
   DB_HOST=your-db-host.onrender.com  # SAME
   DB_PORT=5432
   DB_NAME=attendance_db               # SAME
   DB_USERNAME=your-username           # SAME
   DB_PASSWORD=your-password           # SAME
   JWT_SECRET=your-prod-secret-key
   ```
5. Click "Create Web Service"

## URLs

After deployment, your services will be available at:
- Dev: https://attendance-dev.onrender.com
- QA: https://attendance-qa.onrender.com
- Prod: https://attendance-prod.onrender.com

## Cost

All services are free on Render free tier:
- PostgreSQL: Free tier (shared by all services)
- Web Services: 750 hours/month per service
- Total: 2,250 hours/month (750 × 3)
- Services sleep after 15 minutes of inactivity
- Wake time: ~30-50 seconds
