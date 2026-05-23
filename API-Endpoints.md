# Attendance System – API Endpoints & Output

Base URL: `http://localhost:8080` (or your deployed URL)

All successful responses use the wrapper:
```json
{
  "success": true,
  "message": "Success",
  "data": { ... }
}
```
Error responses: `"success": false`, `"message": "<error text>"`, `"data": null`.

Admin endpoints require header: `Authorization: Bearer <accessToken>` (from login).

---

## 1. Authentication (`/api/auth`)

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/admin/login` | Admin login (email + password; user must be `ADMIN`) |
| POST | `/api/auth/employee/login` | Employee login (email + password; user must be `EMPLOYEE`) |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Logout and revoke refresh token |

There is **no** generic `POST /api/auth/login` — use the role-specific URL above.

### POST `/api/auth/admin/login`

**Request body:**
```json
{
  "email": "admin@attendance.com",
  "password": "admin123"
}
```

**Sample output:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  }
}
```

### POST `/api/auth/employee/login`

Same request body shape as admin login; returns `400` / error message if the account is not an employee.

**Sample output:** Same `data` shape as admin login (`accessToken`, `refreshToken`, `tokenType`, `expiresIn`).

### POST `/api/auth/refresh`

**Request body:**
```json
{
  "refreshToken": "<refreshToken from login>"
}
```

**Sample output:** Same structure as login `data` (new `accessToken`, `refreshToken`, etc.).

### POST `/api/auth/logout`

**Request body (optional):**
```json
{
  "refreshToken": "<refreshToken>"
}
```
Or send refresh token in header: `Authorization: Bearer <refreshToken>`.

**Sample output:**
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null
}
```

---

## 2. Users – current user & profile (`/api/users`)

Requires: `Authorization: Bearer <accessToken>`.

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/users/me` | Get current user |
| PUT | `/api/users/me` | Update current user profile |
| POST | `/api/users/me/photo` | Upload profile photo (multipart `file`) |
| POST | `/api/users/me/signature` | Upload signature (multipart `file`) |
| GET | `/api/users` | Get all users (paginated) |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user (own profile or admin) |

### GET `/api/users/me`

**Sample output:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "employeeId": "ADMIN001",
    "name": "Admin User",
    "email": "admin@attendance.com",
    "role": "ADMIN",
    "status": "ACTIVE",
    "address": null,
    "dateOfBirth": null,
    "bloodGroup": null,
    "validDocumentPath": null,
    "employeeStatus": null,
    "fatherName": null,
    "dateOfJoining": null,
    "officeContactNumber": null,
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": null,
    "photoPath": null,
    "createdAt": "2025-01-15T10:00:00"
  }
}
```

### GET `/api/users?page=0&size=10`

**Sample output:** `data` is a Spring `Page<UserResponse>` (content array, totalElements, totalPages, size, number, etc.).

### GET `/api/users/{id}`

**Sample output:** `data` is a single `UserResponse` object (same shape as above).

---

## 3. Sites – read-only (`/api/sites`)

Requires: `Authorization: Bearer <accessToken>`.

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/sites` | Get all sites |
| GET | `/api/sites/active` | Get active sites only |
| GET | `/api/sites/{id}` | Get site by ID |
| GET | `/api/sites/job-code/{jobCode}` | Get site by job code |

### GET `/api/sites`

**Sample output:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "Main Office",
      "jobCode": "SITE001",
      "address": "123 Street",
      "isActive": true,
      "createdAt": "2025-01-15T10:00:00",
      "updatedAt": "2025-01-15T10:00:00"
    }
  ]
}
```

### GET `/api/sites/{id}` or `/api/sites/job-code/{jobCode}`

**Sample output:** `data` is a single `SiteResponse` object (same shape as above).

---

## 4. Attendance – employee self-service (`/api/attendance`)

Requires: `Authorization: Bearer <accessToken>`. Employees mark and view their own attendance.

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/attendance/mark` | Mark attendance (employee only) |
| GET | `/api/attendance/my-attendance` | My attendance (paginated) |
| GET | `/api/attendance/{id}` | Get attendance by ID (own only) |
| GET | `/api/attendance/my-attendance/date?date=2025-03-01` | My attendance by date |
| GET | `/api/attendance/my-attendance/range?startDate=...&endDate=...&siteId=&page=0&size=10` | My attendance by date range |
| GET | `/api/attendance/my-attendance/calendar` | My attendance calendar |
| GET | `/api/attendance/my-attendance/summary?year=2025&month=3` | My attendance summary by month |

### POST `/api/attendance/mark`

**Request:** multipart form (e.g. `siteId`, `photo` file).

**Sample output:**
```json
{
  "success": true,
  "message": "Attendance marked successfully",
  "data": {
    "id": 1,
    "employee": { "id": 2, "employeeId": "EMP001", "name": "John", "email": "emp@example.com", "role": "EMPLOYEE", ... },
    "site": { "id": 1, "name": "Main Office", "jobCode": "SITE001", "address": "...", "isActive": true, ... },
    "date": "2025-03-03",
    "time": "09:15:00",
    "photoPath": "/uploads/...",
    "status": "PENDING",
    "rejectionReason": null,
    "createdAt": "2025-03-03T09:15:00"
  }
}
```

### GET `/api/attendance/my-attendance?page=0&size=10`

**Sample output:** `data` is a Spring `Page<AttendanceResponse>` (content array of attendance objects, totalElements, totalPages, etc.).

### GET `/api/attendance/my-attendance/date?date=2025-03-01`

**Sample output:** `data` is array of `AttendanceResponse` for that date.

### GET `/api/attendance/my-attendance/calendar`

**Sample output:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "attendanceDates": [
      {
        "date": "2025-03-01",
        "hasAttendance": true,
        "attendanceId": 1,
        "siteName": "Main Office",
        "jobCode": "SITE001",
        "status": "APPROVED"
      }
    ],
    "totalDays": 31,
    "attendedDays": 5
  }
}
```

### GET `/api/attendance/my-attendance/summary?year=2025&month=3`

**Sample output:** `data` is a map (e.g. totalDays, attendedDays, approvedCount, rejectedCount, pendingCount, etc.).

---

## 5. Admin – dashboard (`/api/admin`)

All below require: `Authorization: Bearer <accessToken>` and **Admin** role.

### GET `/api/admin/dashboard`

**Sample output:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "totalEmployees": 10,
    "todayAttendanceCount": 5,
    "pendingApprovals": 3,
    "approvedCount": 100,
    "rejectedCount": 2,
    "siteStats": [
      {
        "siteId": 1,
        "siteName": "Main Office",
        "jobCode": "SITE001",
        "todayAttendanceCount": 3,
        "pendingApprovals": 2,
        "approvedCount": 50,
        "rejectedCount": 1
      }
    ]
  }
}
```

---

## 6. Admin – User management

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/admin/users` | Create user |
| GET | `/api/admin/users` | Get all users (paginated) |
| GET | `/api/admin/users/by-employee-id/{employeeId}` | Get user by employee ID |
| GET | `/api/admin/users/{id}` | Get user by ID |
| PUT | `/api/admin/users/{id}` | Update user |
| DELETE | `/api/admin/users/{id}` | Delete user |
| POST | `/api/admin/users/{id}/reset-password` | Reset password (body: plain new password string) |
| GET | `/api/admin/users/employees` | Get all employees |
| PUT | `/api/admin/users/{id}/activate` | Activate user |
| PUT | `/api/admin/users/{id}/deactivate` | Deactivate user |
| POST | `/api/admin/users/{id}/document` | Upload document (multipart `file`, PDF/JPG/PNG, max 5MB) |
| POST | `/api/admin/users/{id}/photo` | Upload photo (multipart `file`, JPG/PNG, max 3MB) |
| POST | `/api/admin/users/{id}/signature` | Upload signature (multipart `file`, JPG/PNG, max 2MB) |

### POST `/api/admin/users`

**Request body:**
```json
{
  "name": "New Employee",
  "email": "new@example.com",
  "employeeId": "EMP002",
  "role": "EMPLOYEE",
  "address": "...",
  "dateOfBirth": "1990-01-01",
  "bloodGroup": "O_POSITIVE",
  "fatherName": "...",
  "dateOfJoining": "2025-01-01",
  "officeContactNumber": "...",
  "homeContactNumber": "...",
  "otherContactNumber": "...",
  "identificationMark": "..."
}
```

**Sample output:**
```json
{
  "success": true,
  "message": "Employee created successfully. Please share these credentials with the employee:",
  "data": {
    "user": { "id": 3, "employeeId": "EMP002", "name": "New Employee", "email": "new@example.com", "role": "EMPLOYEE", ... },
    "password": "generatedPlainPassword",
    "email": "new@example.com"
  }
}
```

### GET `/api/admin/users?page=0&size=10`

**Sample output:** `data` is `Page<UserResponse>`.

### GET `/api/admin/users/employees`

**Sample output:** `data` is array of `UserResponse` (employees only).

### PUT `/api/admin/users/{id}`

**Request body (example):** name, email, status, employeeStatus, address, etc.

**Sample output:** `data` is updated `UserResponse`.

### DELETE `/api/admin/users/{id}`

**Sample output:**
```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null
}
```

---

## 7. Admin – Site management

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/admin/sites` | Create site |
| GET | `/api/admin/sites` | Get all sites |
| GET | `/api/admin/sites/{id}` | Get site by ID |
| PUT | `/api/admin/sites/{id}` | Update site |
| DELETE | `/api/admin/sites/{id}` | Delete site |
| PUT | `/api/admin/sites/{id}/activate` | Activate site |
| PUT | `/api/admin/sites/{id}/deactivate` | Deactivate site |

### POST `/api/admin/sites`

**Request body:**
```json
{
  "name": "Branch Office",
  "jobCode": "SITE002",
  "address": "456 Avenue"
}
```

**Sample output:** `data` is `SiteResponse`.

### GET `/api/admin/sites`

**Sample output:** `data` is array of `SiteResponse`.

---

## 8. Admin – Attendance management

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/admin/attendance` | Get all attendance (with filters) |
| GET | `/api/admin/attendance/{id}` | Get attendance by ID |
| GET | `/api/admin/attendance/employee/{employeeId}` | Get employee attendance (paginated) |
| GET | `/api/admin/attendance/site/{siteId}` | Get site attendance (paginated) |
| PUT | `/api/admin/attendance/{id}/approve` | Approve or reject attendance |
| DELETE | `/api/admin/attendance/{id}` | Delete attendance |

### GET `/api/admin/attendance`

**Query params (all optional):** `date`, `employeeId`, `siteId`, `jobCode`, `status`, `page`, `size`.

**Sample output:** `data` is `Page<AttendanceResponse>`.

### PUT `/api/admin/attendance/{id}/approve`

**Request body:**
```json
{
  "status": "APPROVED",
  "rejectionReason": null
}
```
Or for reject: `"status": "REJECTED"`, `"rejectionReason": "Reason text"`.

**Sample output:** `data` is updated `AttendanceResponse`.

### DELETE `/api/admin/attendance/{id}`

**Sample output:**
```json
{
  "success": true,
  "message": "Attendance deleted successfully",
  "data": null
}
```

---

## Summary table (all endpoints)

| Method | URL | Auth | Description |
|--------|-----|------|-------------|
| POST | `/api/auth/admin/login` | No | Admin login |
| POST | `/api/auth/employee/login` | No | Employee login |
| POST | `/api/auth/refresh` | No | Refresh token |
| POST | `/api/auth/logout` | No | Logout |
| GET | `/api/users/me` | Yes | Current user |
| PUT | `/api/users/me` | Yes | Update my profile |
| POST | `/api/users/me/photo` | Yes | Upload my photo |
| POST | `/api/users/me/signature` | Yes | Upload my signature |
| GET | `/api/users` | Yes | All users (paginated) |
| GET | `/api/users/{id}` | Yes | User by ID |
| PUT | `/api/users/{id}` | Yes | Update user |
| GET | `/api/sites` | Yes | All sites |
| GET | `/api/sites/active` | Yes | Active sites |
| GET | `/api/sites/{id}` | Yes | Site by ID |
| GET | `/api/sites/job-code/{jobCode}` | Yes | Site by job code |
| POST | `/api/attendance/mark` | Yes | Mark attendance |
| GET | `/api/attendance/my-attendance` | Yes | My attendance |
| GET | `/api/attendance/{id}` | Yes | Attendance by ID |
| GET | `/api/attendance/my-attendance/date` | Yes | My attendance by date |
| GET | `/api/attendance/my-attendance/range` | Yes | My attendance by range |
| GET | `/api/attendance/my-attendance/calendar` | Yes | My calendar |
| GET | `/api/attendance/my-attendance/summary` | Yes | My summary by month |
| GET | `/api/admin/dashboard` | Admin | Dashboard stats |
| POST | `/api/admin/users` | Admin | Create user |
| GET | `/api/admin/users` | Admin | All users |
| GET | `/api/admin/users/by-employee-id/{employeeId}` | Admin | User by employee ID |
| GET | `/api/admin/users/{id}` | Admin | User by ID |
| PUT | `/api/admin/users/{id}` | Admin | Update user |
| DELETE | `/api/admin/users/{id}` | Admin | Delete user |
| POST | `/api/admin/users/{id}/reset-password` | Admin | Reset password |
| GET | `/api/admin/users/employees` | Admin | All employees |
| PUT | `/api/admin/users/{id}/activate` | Admin | Activate user |
| PUT | `/api/admin/users/{id}/deactivate` | Admin | Deactivate user |
| POST | `/api/admin/users/{id}/document` | Admin | Upload document |
| POST | `/api/admin/users/{id}/photo` | Admin | Upload user photo |
| POST | `/api/admin/users/{id}/signature` | Admin | Upload user signature |
| POST | `/api/admin/sites` | Admin | Create site |
| GET | `/api/admin/sites` | Admin | All sites |
| GET | `/api/admin/sites/{id}` | Admin | Site by ID |
| PUT | `/api/admin/sites/{id}` | Admin | Update site |
| DELETE | `/api/admin/sites/{id}` | Admin | Delete site |
| PUT | `/api/admin/sites/{id}/activate` | Admin | Activate site |
| PUT | `/api/admin/sites/{id}/deactivate` | Admin | Deactivate site |
| GET | `/api/admin/attendance` | Admin | All attendance (filtered) |
| GET | `/api/admin/attendance/{id}` | Admin | Attendance by ID |
| GET | `/api/admin/attendance/employee/{employeeId}` | Admin | Employee attendance |
| GET | `/api/admin/attendance/site/{siteId}` | Admin | Site attendance |
| PUT | `/api/admin/attendance/{id}/approve` | Admin | Approve/reject attendance |
| DELETE | `/api/admin/attendance/{id}` | Admin | Delete attendance |
