# Attendance Management System - Backend

## 🚀 Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- H2 Database (Local) / PostgreSQL (Render)
- Maven

## 📋 Prerequisites
- JDK 17 or higher
- Maven 3.6+
- Git

## 🌿 Branch Strategy

| Branch | Environment | Database | Service | Purpose |
|--------|------------|----------|---------|---------|
| `dev` | Development | PostgreSQL (Render) | attendance-dev | Backend development |
| `qa` | QA Testing | PostgreSQL (Render) | attendance-qa | Frontend-Backend integration |
| `main` | Production | PostgreSQL (Render) | attendance-prod | Production/Client demos |

**Note**: All Render services share the same PostgreSQL database. Local development uses H2 in-memory.

## 🔗 API Endpoints

- **Dev API**: https://attendance-dev.onrender.com
  - ⚠️ Backend use only (unstable, changes frequently)
  
- **QA API**: https://attendance-qa.onrender.com
  - ✅ Frontend integration testing (stable)
  
- **Prod API**: https://attendance-prod.onrender.com
  - ✅ Production/Client demos

## 🚀 Getting Started

### Local Development

1. Clone the repository:
```bash
git clone https://github.com/Attendance-SystemC/attendance-backend.git
cd attendance-backend
```

2. Checkout dev branch:
```bash
git checkout dev
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access H2 Console (Local only):
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:attendancedb`
- Username: `sa`
- Password: (empty)

### Environment Variables

#### Local (H2 - Default)
```bash
# No environment variables needed
# Uses H2 in-memory by default
```

#### Render (PostgreSQL)
```bash
SPRING_PROFILES_ACTIVE=render
PORT=8080
DB_HOST=your-db-host.onrender.com
DB_PORT=5432
DB_NAME=attendance_db
DB_USERNAME=your-username
DB_PASSWORD=your-password
JWT_SECRET=your-secret-key
```

## 📝 API Documentation
- Health Check: http://localhost:8080/actuator/health
- H2 Console (Local): http://localhost:8080/h2-console

## 🔄 Deployment Workflow

### Backend Developer Workflow:
1. **Development**: Push to `dev` → Build runs → attendance-dev auto-deploys
2. **Ready for Frontend**: Merge `dev` → `qa` → Build runs → Manually deploy to attendance-qa
3. **Production**: Merge `qa` → `main` → Build runs → Manually deploy to attendance-prod

### Frontend Developer:
- Use **QA API** (https://attendance-qa.onrender.com) for integration testing
- Don't use Dev API (unstable)
- Don't use Prod API (production only)

## 👥 Contributing

1. Create feature branch from `dev`
2. Make changes and commit
3. Push to `dev` branch
4. When stable, merge to `qa` for frontend testing
5. After QA approval, merge to `main` for production

## 📄 License
Proprietary - All rights reserved
