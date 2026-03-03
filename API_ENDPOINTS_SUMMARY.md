# API Endpoints Summary

## ✅ Security Configuration Fixed

**CSRF Protection Disabled For:**
- `/api/auth/**` - Authentication endpoints
- `/api/users/**` - User profile endpoints
- `/api/attendance/**` - Attendance endpoints
- `/api/admin/**` - Admin endpoints

## 📋 All API Endpoints

### 🔐 Authentication Endpoints (No Auth Required)

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| POST | `/api/auth/login` | Login (Admin or Employee) | `{ accessToken, refreshToken, tokenType, expiresIn }` |
| POST | `/api/auth/refresh` | Refresh access token | `{ accessToken, refreshToken, tokenType, expiresIn }` |
| POST | `/api/auth/logout` | Logout | `{ success: true, message: "Logout successful" }` |

---

### 👤 Employee - Profile Management (Authenticated)

| Method | Endpoint | Auth | Description | Response |
|--------|----------|------|-------------|----------|
| GET | `/api/users/me` | ✅ Any | Get my profile (all 14 fields) | `UserResponse` with all fields |
| PUT | `/api/users/me` | ✅ Any | Update my profile | `UserResponse` |
| POST | `/api/users/me/photo` | ✅ Any | Upload my photo | `UserResponse` |
| POST | `/api/users/me/signature` | ✅ Any | Upload my signature | `UserResponse` |

**Note:** Employees cannot update: `status`, `employeeStatus`, `employeeId` (admin-only)

---

### 📅 Employee - Attendance (Authenticated)

| Method | Endpoint | Auth | Role | Description | Response |
|--------|----------|------|------|-------------|----------|
| POST | `/api/attendance/mark` | ✅ | EMPLOYEE | Mark attendance with photo | `AttendanceResponse` |
| GET | `/api/attendance/my-attendance` | ✅ | Any | Get my attendance history | `Page<AttendanceResponse>` |
| GET | `/api/attendance/my-attendance/date?date=YYYY-MM-DD` | ✅ | Any | Get attendance by date | `List<AttendanceResponse>` |
| GET | `/api/attendance/my-attendance/range?startDate=...&endDate=...` | ✅ | Any | Get attendance by date range | `Page<AttendanceResponse>` |
| GET | `/api/attendance/my-attendance/calendar` | ✅ | Any | Get attendance calendar | `AttendanceCalendarResponse` |
| GET | `/api/attendance/my-attendance/summary?year=...&month=...` | ✅ | Any | Get attendance summary | `Map<String, Object>` |
| GET | `/api/attendance/{id}` | ✅ | Any | Get attendance by ID (own only) | `AttendanceResponse` |

**Note:** Employees can only view their own attendance. Admins can view any attendance.

---

### 🏢 Sites - Public (Authenticated)

| Method | Endpoint | Auth | Description | Response |
|--------|----------|------|-------------|----------|
| GET | `/api/sites` | ✅ Any | Get all sites | `List<SiteResponse>` |
| GET | `/api/sites/active` | ✅ Any | Get active sites only | `List<SiteResponse>` |
| GET | `/api/sites/{id}` | ✅ Any | Get site by ID | `SiteResponse` |
| GET | `/api/sites/job-code/{jobCode}` | ✅ Any | Get site by job code | `SiteResponse` |

---

### 👨‍💼 Admin - Dashboard (Admin Only)

| Method | Endpoint | Auth | Role | Description | Response |
|--------|----------|------|------|-------------|----------|
| GET | `/api/admin/dashboard` | ✅ | ADMIN | Get admin dashboard stats | `DashboardResponse` |

---

### 👥 Admin - User Management (Admin Only)

| Method | Endpoint | Auth | Role | Description | Response |
|--------|----------|------|------|-------------|----------|
| POST | `/api/admin/users` | ✅ | ADMIN | Create employee (with all 14 fields) | `UserResponse` |
| GET | `/api/admin/users` | ✅ | ADMIN | Get all users (paginated) | `Page<UserResponse>` |
| GET | `/api/admin/users/employees` | ✅ | ADMIN | Get all employees | `List<UserResponse>` |
| GET | `/api/admin/users/{id}` | ✅ | ADMIN | Get user by ID | `UserResponse` |
| PUT | `/api/admin/users/{id}` | ✅ | ADMIN | Update user (full control) | `UserResponse` |
| DELETE | `/api/admin/users/{id}` | ✅ | ADMIN | Delete user | `{ success: true }` |
| PUT | `/api/admin/users/{id}/activate` | ✅ | ADMIN | Activate user | `UserResponse` |
| PUT | `/api/admin/users/{id}/deactivate` | ✅ | ADMIN | Deactivate user | `UserResponse` |
| POST | `/api/admin/users/{id}/reset-password` | ✅ | ADMIN | Reset password | `{ success: true }` |
| POST | `/api/admin/users/{id}/photo` | ✅ | ADMIN | Upload employee photo | `UserResponse` |
| POST | `/api/admin/users/{id}/signature` | ✅ | ADMIN | Upload employee signature | `UserResponse` |
| POST | `/api/admin/users/{id}/document` | ✅ | ADMIN | Upload employee document | `UserResponse` |

---

### 📊 Admin - Attendance Management (Admin Only)

| Method | Endpoint | Auth | Role | Description | Response |
|--------|----------|------|------|-------------|----------|
| GET | `/api/admin/attendance` | ✅ | ADMIN | Get all attendance (with filters) | `Page<AttendanceResponse>` |
| GET | `/api/admin/attendance/{id}` | ✅ | ADMIN | Get attendance by ID | `AttendanceResponse` |
| GET | `/api/admin/attendance/employee/{employeeId}` | ✅ | ADMIN | Get employee attendance | `Page<AttendanceResponse>` |
| GET | `/api/admin/attendance/site/{siteId}` | ✅ | ADMIN | Get site attendance | `Page<AttendanceResponse>` |
| PUT | `/api/admin/attendance/{id}/approve` | ✅ | ADMIN | Approve/Reject attendance | `AttendanceResponse` |
| DELETE | `/api/admin/attendance/{id}` | ✅ | ADMIN | Delete attendance | `{ success: true }` |

**Filters for GET `/api/admin/attendance`:**
- `date` - Filter by date (YYYY-MM-DD)
- `employeeId` - Filter by employee ID
- `siteId` - Filter by site ID
- `jobCode` - Filter by job code
- `status` - Filter by status (PENDING, APPROVED, REJECTED)
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)

---

### 🏗️ Admin - Site Management (Admin Only)

| Method | Endpoint | Auth | Role | Description | Response |
|--------|----------|------|------|-------------|----------|
| POST | `/api/admin/sites` | ✅ | ADMIN | Create site | `SiteResponse` |
| GET | `/api/admin/sites` | ✅ | ADMIN | Get all sites | `List<SiteResponse>` |
| GET | `/api/admin/sites/{id}` | ✅ | ADMIN | Get site by ID | `SiteResponse` |
| PUT | `/api/admin/sites/{id}` | ✅ | ADMIN | Update site | `SiteResponse` |
| DELETE | `/api/admin/sites/{id}` | ✅ | ADMIN | Delete site | `{ success: true }` |
| PUT | `/api/admin/sites/{id}/activate` | ✅ | ADMIN | Activate site | `SiteResponse` |
| PUT | `/api/admin/sites/{id}/deactivate` | ✅ | ADMIN | Deactivate site | `SiteResponse` |

---

## 🔑 Authentication

All protected endpoints require:
```
Authorization: Bearer <accessToken>
```

**Token Types:**
- `adminAccessToken` - For admin endpoints
- `employeeAccessToken` - For employee endpoints
- `accessToken` - Generic (use appropriate one)

---

## ✅ Expected Response Format

All endpoints return:
```json
{
  "success": true,
  "message": "Success message",
  "data": { ... }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

---

## 🎯 Employee Profile Fields (14 Fields)

1. **SL.No** - `id` (Long)
2. **Name** - `name` (String)
3. **EMP Code** - `employeeId` (String)
4. **Blood Group** - `bloodGroup` (BloodGroup enum)
5. **Father Name** - `fatherName` (String)
6. **Date of Birth** - `dateOfBirth` (LocalDate)
7. **Date of Joining** - `dateOfJoining` (LocalDate)
8. **Office Contact Number** - `officeContactNumber` (String)
9. **Home Contact Number** - `homeContactNumber` (String)
10. **Other Contact Number** - `otherContactNumber` (String)
11. **Identification Mark** - `identificationMark` (String)
12. **Residential Address** - `address` (String)
13. **Specimen Signature** - `specimenSignaturePath` (String - file path)
14. **Photo** - `photoPath` (String - file path)

---

## 🚀 Testing Flow

1. **Admin Login:**
   ```
   POST /api/auth/login
   { "email": "admin@attendance.com", "password": "admin123" }
   → Store adminAccessToken
   ```

2. **Admin Creates Employee:**
   ```
   POST /api/admin/users
   Authorization: Bearer <adminAccessToken>
   { ... all employee fields including email and password ... }
   → Employee created with credentials
   ```

3. **Employee Login:**
   ```
   POST /api/auth/login
   { "email": "employee@example.com", "password": "password123" }
   → Store employeeAccessToken
   ```

4. **Employee Gets Profile:**
   ```
   GET /api/users/me
   Authorization: Bearer <employeeAccessToken>
   → Returns all 14 profile fields
   ```

5. **Employee Marks Attendance:**
   ```
   POST /api/attendance/mark
   Authorization: Bearer <employeeAccessToken>
   FormData: { photo: File, siteId: 1 }
   → Status: PENDING
   ```

6. **Admin Approves Attendance:**
   ```
   PUT /api/admin/attendance/{id}/approve
   Authorization: Bearer <adminAccessToken>
   { "status": "APPROVED", "rejectionReason": "" }
   → Status updated to APPROVED
   ```

7. **Employee Views Updated Status:**
   ```
   GET /api/attendance/my-attendance
   Authorization: Bearer <employeeAccessToken>
   → Shows status: APPROVED (automatically updated)
   ```

---

## ✅ All 403 Errors Fixed

- ✅ CSRF disabled for `/api/users/**`
- ✅ CSRF disabled for `/api/attendance/**`
- ✅ CSRF disabled for `/api/admin/**`
- ✅ All endpoints properly authenticated
- ✅ Role-based access control working

**Note:** Render profile already has CSRF completely disabled, so no issues there.
