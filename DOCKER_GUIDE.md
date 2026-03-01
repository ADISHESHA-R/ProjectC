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

## Local Development with Docker

### Build Docker Image

```bash
docker build -t attendance-backend .
```

### Run with Local Profile (H2)

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  attendance-backend
```

### Run with Render Profile (PostgreSQL)

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
docker build -t attendance-backend .
```

### Run
```bash
docker run -p 8080:8080 attendance-backend
```

### View Logs
```bash
docker logs <container-id>
```

### Stop Container
```bash
docker stop <container-id>
```

### Remove Container
```bash
docker rm <container-id>
```

### Remove Image
```bash
docker rmi attendance-backend
```

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
