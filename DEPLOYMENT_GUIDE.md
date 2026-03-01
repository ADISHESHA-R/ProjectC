# Deployment Guide

## Quick Reference

### For Backend Developers

#### Daily Development (Local):
```bash
git checkout dev
# Make changes
# Test locally:
mvn spring-boot:run
# Or with Docker:
docker-compose -f docker-compose.local.yml up
# No deployment needed - test locally ✅
```

#### Ready for Frontend Testing:
```bash
git checkout qa
git merge dev
git push origin qa
# ✅ Build runs automatically
# Then manually deploy to attendance-qa
# Share URL with frontend developer: https://attendance-qa.onrender.com
```

#### Production Release:
```bash
git checkout main
git merge qa
git push origin main
# ✅ Build runs automatically
# Then manually deploy to attendance-prod
```

### For Frontend Developers

#### API Endpoints:
- **QA API**: https://attendance-qa.onrender.com
  - ✅ Use this for integration testing
  - Stable environment
  - Updated when backend is ready

- **Prod API**: https://attendance-prod.onrender.com
  - ⚠️ Don't use (production only)

## Manual Deployment Steps

### Deploy to QA:

1. **Via Render Dashboard:**
   - Go to Render Dashboard
   - Select `attendance-qa` service
   - Click "Manual Deploy"
   - Select latest commit from `qa` branch

2. **Via GitHub Actions:**
   - Go to GitHub → Actions
   - Select "Build and Manual Deploy"
   - Click "Run workflow"
   - Select branch: `qa`
   - Select environment: `qa`
   - Click "Run workflow"

### Deploy to Prod:

1. **Via Render Dashboard:**
   - Go to Render Dashboard
   - Select `attendance-prod` service
   - Click "Manual Deploy"
   - Select latest commit from `main` branch

2. **Via GitHub Actions:**
   - Go to GitHub → Actions
   - Select "Build and Manual Deploy"
   - Click "Run workflow"
   - Select branch: `main`
   - Select environment: `prod`
   - Click "Run workflow"

## Environment Promotion Flow

```
dev branch (Backend Development)
    ↓ (Merge when feature complete)
qa branch (Frontend Integration)
    ↓ (Merge when QA approved)
main branch (Production)
```

## Communication

### When Deploying to QA:
- Notify frontend developer
- Share QA API URL: https://attendance-qa.onrender.com
- Mention what features are available
- Ask for feedback

### When Deploying to Prod:
- Notify team
- Prepare for client demo
- Verify all features working
