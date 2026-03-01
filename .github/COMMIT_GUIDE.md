# Commit Message Guide

## Deployment Strategy

### ✅ Build Only (No Auto-Deploy)
- All pushes trigger build automatically
- No deployment happens automatically (except dev auto-deploys)
- Manual deployment required via GitHub Actions or Render

### Environment Promotion

1. **Dev → QA** (When ready for frontend testing)
   ```bash
   git checkout qa
   git merge dev
   git push origin qa
   # Build runs automatically
   # Manually deploy to attendance-qa
   ```

2. **QA → Prod** (When ready for production)
   ```bash
   git checkout main
   git merge qa
   git push origin main
   # Build runs automatically
   # Manually deploy to attendance-prod
   ```

## Commit Message Format

Use conventional commits:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `refactor:` - Code refactoring
- `test:` - Adding tests
- `chore:` - Maintenance tasks

## Examples

```bash
feat: implement JWT authentication
fix: resolve null pointer exception
chore: update dependencies
refactor: optimize database queries
```

## API Endpoints Reference

- **Dev**: https://attendance-dev.onrender.com (Backend only)
- **QA**: https://attendance-qa.onrender.com (Frontend integration)
- **Prod**: https://attendance-prod.onrender.com (Production)
