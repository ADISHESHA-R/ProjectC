# Docker Setup Guide

## Overview

This project uses Docker for consistent builds and deployments across all environments (local, dev, qa, prod).

## Docker Files

### Dockerfile
- Multi-stage build (Maven build + JRE runtime)
- Optimized for caching and smaller image size
- Includes health check
- Runs as non-root user for security

### .dockerignore
- Excludes unnecessary files from Docker build context
- Reduces build time and image size

## Docker Compose Files

### docker-compose.local.yml
- For local development with H2 database
- No external database needed
- Quick start for development

### docker-compose.render.yml
- For Render deployment (dev, qa, prod)
- Uses PostgreSQL database
- Environment variables from `.env` file

### docker-compose.yml
- All environments in one file
- Use profiles to run specific environment

## Local Development with Docker

### Option 1: Using Docker Compose (Recommended)

#### Local Environment (H2):
```bash
docker-compose -f docker-compose.local.yml up --build
```

#### Dev Environment (PostgreSQL):
```bash
# Create .env file with database credentials
docker-compose -f docker-compose.render.yml --profile dev up --build
```

#### QA Environment:
```bash
docker-compose -f docker-compose.render.yml --profile qa up --build
```

#### Prod Environment:
```bash
docker-compose -f docker-compose.render.yml --profile prod up --build
```

### Option 2: Using Docker Commands

#### Build Docker Image

```bash
docker build -t attendance-backend .
```

#### Run with Local Profile (H2)

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  attendance-backend
```

#### Run with Render Profile (PostgreSQL)

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=render \
  -e DB_HOST=your-db-host \
  -e DB_PORT=5432 \
  -e DB_NAME=attendance_db \
  -e DB_USERNAME=your-username \
  -e DB_PASSWORD=your-password \
  -e JWT_SECRET=your-secret \
  attendance-backend
```

### Test Health Endpoint

```bash
curl http://localhost:8080/actuator/health
```

## Docker Commands Reference

### Build
```bash
# Build image
docker build -t attendance-backend .

# Build with docker-compose
docker-compose build
```

### Run
```bash
# Run container
docker run -p 8080:8080 attendance-backend

# Run with docker-compose
docker-compose -f docker-compose.local.yml up
```

### View Logs
```bash
# Docker logs
docker logs <container-id>

# Docker-compose logs
docker-compose logs -f
```

### Stop Container
```bash
# Stop container
docker stop <container-id>

# Stop with docker-compose
docker-compose down
```

### Remove Container
```bash
# Remove container
docker rm <container-id>

# Remove with docker-compose (includes volumes)
docker-compose down -v
```

### Remove Image
```bash
docker rmi attendance-backend
```

## All Environments Dockerized

### ✅ Local Environment
- **File**: `docker-compose.local.yml`
- **Database**: H2 (in-memory)
- **Command**: `docker-compose -f docker-compose.local.yml up`
- **Port**: 8080

### ✅ Dev Environment
- **File**: `docker-compose.render.yml` (profile: dev)
- **Database**: PostgreSQL (shared)
- **Command**: `docker-compose -f docker-compose.render.yml --profile dev up`
- **Port**: 8081

### ✅ QA Environment
- **File**: `docker-compose.render.yml` (profile: qa)
- **Database**: PostgreSQL (shared)
- **Command**: `docker-compose -f docker-compose.render.yml --profile qa up`
- **Port**: 8082

### ✅ Prod Environment
- **File**: `docker-compose.render.yml` (profile: prod)
- **Database**: PostgreSQL (shared)
- **Command**: `docker-compose -f docker-compose.render.yml --profile prod up`
- **Port**: 8083

## Render Deployment

### Configuration

For all services (dev, qa, prod) on Render:

1. **Language**: Select **"Docker"**
2. **Build Command**: Leave blank (Docker builds automatically)
3. **Start Command**: Leave blank (Docker runs automatically)
4. **Dockerfile Path**: `Dockerfile` (or leave blank)

### How It Works

1. Render detects `Dockerfile` in your repository
2. Builds Docker image automatically
3. Runs container with your environment variables
4. Exposes port 8080

## Environment Variables

All environments use the same Docker image but with different environment variables:

### Local (H2)
```
SPRING_PROFILES_ACTIVE=local
```

### Render (PostgreSQL)
```
SPRING_PROFILES_ACTIVE=render
DB_HOST=your-db-host.onrender.com
DB_PORT=5432
DB_NAME=attendance_db
DB_USERNAME=your-username
DB_PASSWORD=your-password
JWT_SECRET=your-secret-key
```

## Benefits

✅ **Consistent**: Same image works everywhere
✅ **Fast**: Docker layer caching speeds up builds
✅ **Isolated**: No conflicts with system dependencies
✅ **Portable**: Works on any Docker-compatible platform
✅ **Secure**: Runs as non-root user

## Troubleshooting

### Build Fails
- Check Dockerfile syntax
- Verify pom.xml is correct
- Check Docker logs

### Container Won't Start
- Verify environment variables
- Check port 8080 is available
- Review application logs

### Health Check Fails
- Ensure actuator endpoint is enabled
- Check application is running
- Verify port mapping
