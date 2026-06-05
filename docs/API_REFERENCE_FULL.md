# Attendance System — Full API reference

Base URL: `http://localhost:8080` (override with `PORT` / deployment).

## Conventions

### JSON envelope (`ApiResponse<T>`)

Most JSON endpoints return:

```json
{
  "success": true,
  "message": "Success",
  "data": {}
}
```

- **`success`**: `true` on OK, `false` on error.
- **`message`**: human-readable text.
- **`data`**: payload type varies by endpoint (`null` when void).

**Validation error** (`400`): `success: false`, `message: "Validation failed"`, `data` = object map of field name → error string.

**401 Unauthorized** (missing/invalid JWT): JSON `{ "success": false, "message": "Unauthorized: invalid or missing token" }` (no `data` key).

**Authenticated requests:** header `Authorization: Bearer <accessToken>`.

## 1. Authentication (`/api/auth`)

No `Authorization` header required for this section.

---

## **POST** `/api/auth/admin/login`

*Admin login — rejects non-ADMIN users.*

**Auth**

- None
- **Access:** Public

### Request

*Request body (`application/json`)*

```json
{
  "email": "admin@example.com",
  "password": "your-password"
}
```

**Notes**

- `email` (string, required) — admin user email.
- `password` (string, required).

### Response

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "<jwt>",
    "refreshToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Notes**

- `data` — `AuthResponse`: `accessToken`, `refreshToken`, `tokenType` (default `Bearer`), `expiresIn` (seconds).


---

## **POST** `/api/auth/employee/login`

*Employee login — rejects non-EMPLOYEE users.*

**Auth**

- None
- **Access:** Public

### Request

*Request body (`application/json`)*

```json
{
  "email": "employee@example.com",
  "password": "your-password"
}
```

**Notes**

- `email` (string, required).
- `password` (string, required).

### Response

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "<jwt>",
    "refreshToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Notes**

- Same shape as admin login.


---

## **POST** `/api/auth/refresh`

*New access token from refresh token.*

**Auth**

- None
- **Access:** Public

### Request

*Request body (`application/json`)*

```json
{
  "refreshToken": "<refresh-jwt>"
}
```

**Notes**

- `refreshToken` (string, required).

### Response

```json
{
  "success": true,
  "message": "Token refreshed",
  "data": {
    "accessToken": "<jwt>",
    "refreshToken": "<jwt>",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Notes**

- `data` — same as login `AuthResponse`.


---

## **POST** `/api/auth/logout`

*Revoke refresh token.*

**Auth**

- None
- **Access:** Public

### Request

*Request body (`application/json`, optional if using header)*

```json
{
  "refreshToken": "<refresh-jwt>"
}
```

**Notes**

- `refreshToken` in body **or** `Authorization: Bearer <refreshToken>`.

### Response

```json
{
  "success": true,
  "message": "Logout successful",
  "data": null
}
```

**Notes**

- `data` is `null` on success.


---

## **GET** `/api/auth/home`

*Lightweight health / cold-start ping.*

**Auth**

- None
- **Access:** Public

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Restart...",
  "data": "Avoid cold start by returning a simple message"
}
```

**Notes**

- `data` is a plain string message.

## 2. Public customer feedback (`/api/public/feedback`)

No JWT. Rate limiting may apply.

---

## **GET** `/api/public/feedback/{token}`

*Context for invite token.*

**Auth**

- None
- **Access:** Public

### Request

No request body. Path parameter: `token` (opaque string).

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "jobCode": "JOB-001",
    "customerName": "Acme Ltd",
    "companyNameHint": "…",
    "certificateClientStatus": "NONE",
    "expired": false,
    "revoked": false
  }
}
```

**Notes**

- `data` — `PublicFeedbackContextResponse`: `jobCode`, `customerName`, `companyNameHint`, `certificateClientStatus` (`NONE` | `FEEDBACK_SUBMITTED` | `APPROVED_BY_CLIENT`), `expired`, `revoked` (booleans).


---

## **POST** `/api/public/feedback/{token}`

*Submit customer feedback JSON.*

**Auth**

- None
- **Access:** Public

### Request

*Request body (`application/json`)*

```json
{
  "name": "Jane",
  "email": "jane@client.com",
  "phone": "+1",
  "companyName": "Acme",
  "productQuality": "Good",
  "customerService": "Good",
  "machiningQuality": "Good",
  "pricing": "Fair",
  "shippingDelivery": "On time",
  "otherCategoryNote": "",
  "specificFeedback": "Great work",
  "suggestions": "",
  "likelihoodRecommend": 9,
  "additionalComments": "",
  "extra": {
    "customField": "value"
  }
}
```

**Notes**

- `name`, `email`, `phone`, `companyName` — optional strings.
- Rating / text fields: `productQuality`, `customerService`, `machiningQuality`, `pricing`, `shippingDelivery`, `otherCategoryNote`, `specificFeedback`, `suggestions`, `additionalComments`.
- `likelihoodRecommend` — integer (e.g. 0–10), optional.
- `extra` — arbitrary JSON object merged into stored payload.

### Response

```json
{
  "success": true,
  "message": "Feedback saved",
  "data": null
}
```

**Notes**

- `data` is `null`.


---

## **POST** `/api/public/feedback/{token}/approve`

*Client approves certificate; returns PDF file.*

**Auth**

- None
- **Access:** Public

### Request

No request body.

### Response

**Binary:** `application/pdf`, `Content-Disposition: attachment; filename="work-completion-certificate.pdf"`. Not JSON.

## 3. Meta (`/api/meta`)


---

## **GET** `/api/meta/designations`

*Active designations / job titles.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "code": "TECH",
      "label": "Technician",
      "sortOrder": 10
    }
  ]
}
```

**Notes**

- `data` — array of `{ id, code, label, sortOrder }`.


---

## **GET** `/api/meta/challenge-line-heads`

*Preset labels for site challenge lines (Screen 9).*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "index": 1,
      "label": "Safety"
    }
  ]
}
```

**Notes**

- `data` — array of `{ index, label }`.

## 4. Sites — read (`/api/sites`)


---

## **GET** `/api/sites`

*List all sites.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    }
  ]
}
```

**Notes**

- `data` — `SiteResponse[]`.


---

## **GET** `/api/sites/active`

*Active sites only.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    }
  ]
}
```

**Notes**

- `data` — `SiteResponse[]`.


---

## **GET** `/api/sites/{id}`

*Site by numeric id.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

Path: `id` (long).

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/sites/job-code/{jobCode}`

*Site by job code.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

Path: `jobCode` (string).

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.

## 5. Notices — employee (`/api/notices`)


---

## **GET** `/api/notices`

*All notices for authenticated users.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "message": "Holiday on Friday",
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    }
  ]
}
```

**Notes**

- `data` — `NoticeResponse[]`: `id`, `message`, `createdAt`, `updatedAt`.

## 6. Files (`/api/files`)


---

## **GET** `/api/files`

*Download stored file (photo, signature, document).*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `path` | Yes | Stored relative path (e.g. from `photoPath` on user). |

### Request

No JSON body.

### Response

**Binary** stream: `Content-Disposition: inline`. Content-Type from file extension (jpeg, png, pdf, or octet-stream).

## 7. Users (`/api/users`)


---

## **GET** `/api/users/me`

*Current user profile.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse` (see appendix).


---

## **PUT** `/api/users/me`

*Update own profile (non-admin fields only).*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

*Request body (`application/json`)*

```json
{
  "name": "Ravi K",
  "email": "ravi@example.com"
}
```

**Notes**

- Optional fields: `name`, `email`, `address`, `dateOfBirth`, `bloodGroup`, `fatherName`, `dateOfJoining`, contact numbers, `identificationMark`, `photoPath`, `specimenSignaturePath`.
- Non-admins cannot set `status`, `employeeStatus`, `employeeId`.

### Response

```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — updated `UserResponse`.


---

## **POST** `/api/users/me/photo`

*Upload profile photo.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

*`multipart/form-data`*

| Part | Type | Description |
|------|------|-------------|
| `file` | file | JPG/PNG; max size per server config (e.g. 3MB). |

### Response

```json
{
  "success": true,
  "message": "Photo uploaded successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse` with new `photoPath`.


---

## **POST** `/api/users/me/signature`

*Upload specimen signature.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

*`multipart/form-data`*

| Part | Type | Description |
|------|------|-------------|
| `file` | file | JPG/PNG; max ~2MB per service rules. |

### Response

```json
{
  "success": true,
  "message": "Signature uploaded successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **GET** `/api/users`

*Paginated users.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `page` | No | 0-based page (default `0`). |
| `size` | No | page size (default `10`). |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "employeeId": "EMP001",
        "name": "Ravi Kumar",
        "email": "ravi@example.com",
        "role": "EMPLOYEE",
        "status": "ACTIVE",
        "address": "123 Street",
        "dateOfBirth": "1990-05-01",
        "bloodGroup": "A_POSITIVE",
        "validDocumentPath": null,
        "employeeStatus": "ACTIVE",
        "fatherName": "Father Name",
        "dateOfJoining": "2024-01-15",
        "officeContactNumber": "+911234567890",
        "homeContactNumber": null,
        "otherContactNumber": null,
        "identificationMark": null,
        "specimenSignaturePath": "uploads/signatures/1_sig.png",
        "photoPath": "uploads/photos/1.jpg",
        "createdAt": "2026-01-01T10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — Spring `Page<UserResponse>`.


---

## **GET** `/api/users/{id}`

*User by id.*

**Auth**

- Bearer JWT
- **Access:** Any authenticated user

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **PUT** `/api/users/{id}`

*Update user; self or admin.*

**Auth**

- Bearer JWT
- **Access:** Owner or ADMIN

### Request

*Request body (`application/json`)*

```json
{
  "name": "Updated name"
}
```

**Notes**

- Same optional fields as `UpdateUserRequest`; admin may set `status`, `employeeStatus`, `employeeId`.

### Response

```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.

## 8. Attendance (`/api/attendance`)


---

## **POST** `/api/attendance/mark`

*Mark attendance with photo.*

**Auth**

- Bearer JWT
- **Access:** **EMPLOYEE** role only

### Request

*`multipart/form-data`*

| Part | Type | Description |
|------|------|-------------|
| `photo` | file | Required image. |
| `siteId` | text | Long — site id. |
| `shift` | text | `FIRST_HALF` | `SECOND_HALF` | `FULL_DAY`. |

### Response

```json
{
  "success": true,
  "message": "Attendance marked successfully",
  "data": {
    "id": 10,
    "employee": {
      "id": 1,
      "employeeId": "EMP001",
      "name": "Ravi Kumar",
      "email": "ravi@example.com",
      "role": "EMPLOYEE",
      "status": "ACTIVE",
      "address": "123 Street",
      "dateOfBirth": "1990-05-01",
      "bloodGroup": "A_POSITIVE",
      "validDocumentPath": null,
      "employeeStatus": "ACTIVE",
      "fatherName": "Father Name",
      "dateOfJoining": "2024-01-15",
      "officeContactNumber": "+911234567890",
      "homeContactNumber": null,
      "otherContactNumber": null,
      "identificationMark": null,
      "specimenSignaturePath": "uploads/signatures/1_sig.png",
      "photoPath": "uploads/photos/1.jpg",
      "createdAt": "2026-01-01T10:00:00"
    },
    "site": {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    },
    "date": "2026-05-20",
    "time": "09:15:00",
    "photoPath": "uploads/attendance/10.jpg",
    "status": "PENDING",
    "rejectionReason": null,
    "shift": "FULL_DAY",
    "createdAt": "2026-05-20T09:15:01"
  }
}
```

**Notes**

- `data` — `AttendanceResponse`: `id`, nested `employee`/`site`, `date`, `time`, `photoPath`, `status` (`PENDING`|`APPROVED`|`REJECTED`), `rejectionReason`, `shift`, `createdAt`.


---

## **GET** `/api/attendance/my-attendance`

*Own attendance history (paged).*

**Auth**

- Bearer JWT
- **Access:** Authenticated (employee principal)

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `page` | No | default `0` |
| `size` | No | default `10` |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 10,
        "employee": {
          "id": 1,
          "employeeId": "EMP001",
          "name": "Ravi Kumar",
          "email": "ravi@example.com",
          "role": "EMPLOYEE",
          "status": "ACTIVE",
          "address": "123 Street",
          "dateOfBirth": "1990-05-01",
          "bloodGroup": "A_POSITIVE",
          "validDocumentPath": null,
          "employeeStatus": "ACTIVE",
          "fatherName": "Father Name",
          "dateOfJoining": "2024-01-15",
          "officeContactNumber": "+911234567890",
          "homeContactNumber": null,
          "otherContactNumber": null,
          "identificationMark": null,
          "specimenSignaturePath": "uploads/signatures/1_sig.png",
          "photoPath": "uploads/photos/1.jpg",
          "createdAt": "2026-01-01T10:00:00"
        },
        "site": {
          "id": 1,
          "name": "Site Alpha",
          "jobCode": "JOB-001",
          "address": "Industrial Area",
          "isActive": true,
          "customerName": "Acme Ltd",
          "estimatedDays": 30,
          "inchargeUserId": 2,
          "inchargeName": "Lead User",
          "inchargeEmployeeId": "EMP002",
          "locationSiteId": null,
          "locationSiteLabel": null,
          "siteStartDate": "2026-01-01",
          "siteEndDate": "2026-01-31",
          "totalProjectDays": 31,
          "certificateClientStatus": "NONE",
          "customerFeedbackApprovedAt": null,
          "createdAt": "2026-01-01T08:00:00",
          "updatedAt": "2026-01-01T08:00:00"
        },
        "date": "2026-05-20",
        "time": "09:15:00",
        "photoPath": "uploads/attendance/10.jpg",
        "status": "PENDING",
        "rejectionReason": null,
        "shift": "FULL_DAY",
        "createdAt": "2026-05-20T09:15:01"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — `Page<AttendanceResponse>`.


---

## **GET** `/api/attendance/{id}`

*Single attendance by id (own row or admin).*

**Auth**

- Bearer JWT
- **Access:** Owner or ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 10,
    "employee": {
      "id": 1,
      "employeeId": "EMP001",
      "name": "Ravi Kumar",
      "email": "ravi@example.com",
      "role": "EMPLOYEE",
      "status": "ACTIVE",
      "address": "123 Street",
      "dateOfBirth": "1990-05-01",
      "bloodGroup": "A_POSITIVE",
      "validDocumentPath": null,
      "employeeStatus": "ACTIVE",
      "fatherName": "Father Name",
      "dateOfJoining": "2024-01-15",
      "officeContactNumber": "+911234567890",
      "homeContactNumber": null,
      "otherContactNumber": null,
      "identificationMark": null,
      "specimenSignaturePath": "uploads/signatures/1_sig.png",
      "photoPath": "uploads/photos/1.jpg",
      "createdAt": "2026-01-01T10:00:00"
    },
    "site": {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    },
    "date": "2026-05-20",
    "time": "09:15:00",
    "photoPath": "uploads/attendance/10.jpg",
    "status": "PENDING",
    "rejectionReason": null,
    "shift": "FULL_DAY",
    "createdAt": "2026-05-20T09:15:01"
  }
}
```

**Notes**

- `data` — `AttendanceResponse`.


---

## **GET** `/api/attendance/my-attendance/date`

*Own marks for one calendar day.*

**Auth**

- Bearer JWT
- **Access:** Employee

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `date` | Yes | ISO date `YYYY-MM-DD`. |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 10,
      "employee": {
        "id": 1,
        "employeeId": "EMP001",
        "name": "Ravi Kumar",
        "email": "ravi@example.com",
        "role": "EMPLOYEE",
        "status": "ACTIVE",
        "address": "123 Street",
        "dateOfBirth": "1990-05-01",
        "bloodGroup": "A_POSITIVE",
        "validDocumentPath": null,
        "employeeStatus": "ACTIVE",
        "fatherName": "Father Name",
        "dateOfJoining": "2024-01-15",
        "officeContactNumber": "+911234567890",
        "homeContactNumber": null,
        "otherContactNumber": null,
        "identificationMark": null,
        "specimenSignaturePath": "uploads/signatures/1_sig.png",
        "photoPath": "uploads/photos/1.jpg",
        "createdAt": "2026-01-01T10:00:00"
      },
      "site": {
        "id": 1,
        "name": "Site Alpha",
        "jobCode": "JOB-001",
        "address": "Industrial Area",
        "isActive": true,
        "customerName": "Acme Ltd",
        "estimatedDays": 30,
        "inchargeUserId": 2,
        "inchargeName": "Lead User",
        "inchargeEmployeeId": "EMP002",
        "locationSiteId": null,
        "locationSiteLabel": null,
        "siteStartDate": "2026-01-01",
        "siteEndDate": "2026-01-31",
        "totalProjectDays": 31,
        "certificateClientStatus": "NONE",
        "customerFeedbackApprovedAt": null,
        "createdAt": "2026-01-01T08:00:00",
        "updatedAt": "2026-01-01T08:00:00"
      },
      "date": "2026-05-20",
      "time": "09:15:00",
      "photoPath": "uploads/attendance/10.jpg",
      "status": "PENDING",
      "rejectionReason": null,
      "shift": "FULL_DAY",
      "createdAt": "2026-05-20T09:15:01"
    }
  ]
}
```

**Notes**

- `data` — `AttendanceResponse[]` (may be empty).


---

## **GET** `/api/attendance/my-attendance/range`

*Own marks between dates (paged).*

**Auth**

- Bearer JWT
- **Access:** Employee

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `startDate` | Yes | ISO date |
| `endDate` | Yes | ISO date |
| `siteId` | No | filter by site |
| `page` | No | undefined |
| `size` | No | undefined |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 10,
        "employee": {
          "id": 1,
          "employeeId": "EMP001",
          "name": "Ravi Kumar",
          "email": "ravi@example.com",
          "role": "EMPLOYEE",
          "status": "ACTIVE",
          "address": "123 Street",
          "dateOfBirth": "1990-05-01",
          "bloodGroup": "A_POSITIVE",
          "validDocumentPath": null,
          "employeeStatus": "ACTIVE",
          "fatherName": "Father Name",
          "dateOfJoining": "2024-01-15",
          "officeContactNumber": "+911234567890",
          "homeContactNumber": null,
          "otherContactNumber": null,
          "identificationMark": null,
          "specimenSignaturePath": "uploads/signatures/1_sig.png",
          "photoPath": "uploads/photos/1.jpg",
          "createdAt": "2026-01-01T10:00:00"
        },
        "site": {
          "id": 1,
          "name": "Site Alpha",
          "jobCode": "JOB-001",
          "address": "Industrial Area",
          "isActive": true,
          "customerName": "Acme Ltd",
          "estimatedDays": 30,
          "inchargeUserId": 2,
          "inchargeName": "Lead User",
          "inchargeEmployeeId": "EMP002",
          "locationSiteId": null,
          "locationSiteLabel": null,
          "siteStartDate": "2026-01-01",
          "siteEndDate": "2026-01-31",
          "totalProjectDays": 31,
          "certificateClientStatus": "NONE",
          "customerFeedbackApprovedAt": null,
          "createdAt": "2026-01-01T08:00:00",
          "updatedAt": "2026-01-01T08:00:00"
        },
        "date": "2026-05-20",
        "time": "09:15:00",
        "photoPath": "uploads/attendance/10.jpg",
        "status": "PENDING",
        "rejectionReason": null,
        "shift": "FULL_DAY",
        "createdAt": "2026-05-20T09:15:01"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — `Page<AttendanceResponse>`.


---

## **GET** `/api/attendance/my-attendance/calendar`

*Calendar summary for logged-in employee.*

**Auth**

- Bearer JWT
- **Access:** Employee

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "attendanceDates": [
      {
        "date": "2026-05-01",
        "hasAttendance": true,
        "attendanceId": 10,
        "siteName": "Site Alpha",
        "jobCode": "JOB-001",
        "status": "APPROVED",
        "shift": "FULL_DAY"
      }
    ],
    "totalDays": 1,
    "attendedDays": 1
  }
}
```

**Notes**

- `data` — `attendanceDates[]` with `date`, `hasAttendance`, `attendanceId`, `siteName`, `jobCode`, `status`, `shift`; plus `totalDays`, `attendedDays`.


---

## **GET** `/api/attendance/my-attendance/summary`

*Monthly summary for logged-in employee.*

**Auth**

- Bearer JWT
- **Access:** Employee

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `year` | Yes | integer |
| `month` | Yes | 1–12 |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "year": 2026,
    "month": 5,
    "totalDays": 31,
    "attendedDays": 18,
    "attendanceList": [
      {
        "id": 10,
        "employee": {
          "id": 1,
          "employeeId": "EMP001",
          "name": "Ravi Kumar",
          "email": "ravi@example.com",
          "role": "EMPLOYEE",
          "status": "ACTIVE",
          "address": "123 Street",
          "dateOfBirth": "1990-05-01",
          "bloodGroup": "A_POSITIVE",
          "validDocumentPath": null,
          "employeeStatus": "ACTIVE",
          "fatherName": "Father Name",
          "dateOfJoining": "2024-01-15",
          "officeContactNumber": "+911234567890",
          "homeContactNumber": null,
          "otherContactNumber": null,
          "identificationMark": null,
          "specimenSignaturePath": "uploads/signatures/1_sig.png",
          "photoPath": "uploads/photos/1.jpg",
          "createdAt": "2026-01-01T10:00:00"
        },
        "site": {
          "id": 1,
          "name": "Site Alpha",
          "jobCode": "JOB-001",
          "address": "Industrial Area",
          "isActive": true,
          "customerName": "Acme Ltd",
          "estimatedDays": 30,
          "inchargeUserId": 2,
          "inchargeName": "Lead User",
          "inchargeEmployeeId": "EMP002",
          "locationSiteId": null,
          "locationSiteLabel": null,
          "siteStartDate": "2026-01-01",
          "siteEndDate": "2026-01-31",
          "totalProjectDays": 31,
          "certificateClientStatus": "NONE",
          "customerFeedbackApprovedAt": null,
          "createdAt": "2026-01-01T08:00:00",
          "updatedAt": "2026-01-01T08:00:00"
        },
        "date": "2026-05-20",
        "time": "09:15:00",
        "photoPath": "uploads/attendance/10.jpg",
        "status": "PENDING",
        "rejectionReason": null,
        "shift": "FULL_DAY",
        "createdAt": "2026-05-20T09:15:01"
      }
    ],
    "attendanceRate": 58.06
  }
}
```

**Notes**

- `data` map: `year`, `month`, `totalDays`, `attendedDays`, `attendanceList` (array of `AttendanceResponse`), `attendanceRate` (number).

## 9. Admin — dashboard & directory (`/api/admin`)

**All endpoints in this section require role `ADMIN`.**


---

## **GET** `/api/admin/dashboard`

*Aggregated stats + per-site counts.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "totalEmployees": 50,
    "todayAttendanceCount": 40,
    "pendingApprovals": 5,
    "approvedCount": 30,
    "rejectedCount": 2,
    "siteStats": [
      {
        "siteId": 1,
        "siteName": "Site Alpha",
        "jobCode": "JOB-001",
        "todayAttendanceCount": 10,
        "pendingApprovals": 1,
        "approvedCount": 8,
        "rejectedCount": 0
      }
    ]
  }
}
```

**Notes**

- `data` — `totalEmployees`, `todayAttendanceCount`, `pendingApprovals`, `approvedCount`, `rejectedCount`, `siteStats[]` (`siteId`, `siteName`, `jobCode`, counts).


---

## **POST** `/api/admin/users`

*Create employee or admin.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body (`application/json`) — `CreateUserRequest`*

```json
{
  "employeeId": "EMP099",
  "name": "New Hire",
  "email": "hire@example.com",
  "password": "atLeast8Chars",
  "role": "EMPLOYEE",
  "address": "Addr",
  "dateOfBirth": "1995-01-01",
  "bloodGroup": "O_POSITIVE",
  "employeeStatus": "ACTIVE",
  "fatherName": "F",
  "dateOfJoining": "2026-05-01",
  "officeContactNumber": "111",
  "homeContactNumber": null,
  "otherContactNumber": null,
  "identificationMark": null
}
```

**Notes**

- `employeeId` — optional string.
- `name`, `email`, `password` (min 8), `role` (`ADMIN`|`EMPLOYEE`) — required.
- Profile fields optional — see DTO in codebase.

### Response

```json
{
  "success": true,
  "message": "Employee created successfully. Please share these credentials with the employee:",
  "data": {
    "user": {
      "id": 1,
      "employeeId": "EMP001",
      "name": "Ravi Kumar",
      "email": "ravi@example.com",
      "role": "EMPLOYEE",
      "status": "ACTIVE",
      "address": "123 Street",
      "dateOfBirth": "1990-05-01",
      "bloodGroup": "A_POSITIVE",
      "validDocumentPath": null,
      "employeeStatus": "ACTIVE",
      "fatherName": "Father Name",
      "dateOfJoining": "2024-01-15",
      "officeContactNumber": "+911234567890",
      "homeContactNumber": null,
      "otherContactNumber": null,
      "identificationMark": null,
      "specimenSignaturePath": "uploads/signatures/1_sig.png",
      "photoPath": "uploads/photos/1.jpg",
      "createdAt": "2026-01-01T10:00:00"
    },
    "password": "atLeast8Chars",
    "email": "hire@example.com"
  }
}
```

**Notes**

- `data` — `CreateUserResponse`: `user`, `password` (plain, once), `email`.


---

## **GET** `/api/admin/users`

*Search/filter users (paged).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `search` | No | name / email / employeeId |
| `role` | No | `ADMIN` | `EMPLOYEE` |
| `status` | No | `ACTIVE` | `INACTIVE` |
| `page` | No | undefined |
| `size` | No | undefined |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "employeeId": "EMP001",
        "name": "Ravi Kumar",
        "email": "ravi@example.com",
        "role": "EMPLOYEE",
        "status": "ACTIVE",
        "address": "123 Street",
        "dateOfBirth": "1990-05-01",
        "bloodGroup": "A_POSITIVE",
        "validDocumentPath": null,
        "employeeStatus": "ACTIVE",
        "fatherName": "Father Name",
        "dateOfJoining": "2024-01-15",
        "officeContactNumber": "+911234567890",
        "homeContactNumber": null,
        "otherContactNumber": null,
        "identificationMark": null,
        "specimenSignaturePath": "uploads/signatures/1_sig.png",
        "photoPath": "uploads/photos/1.jpg",
        "createdAt": "2026-01-01T10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — `Page<UserResponse>`.


---

## **GET** `/api/admin/users/by-employee-id/{employeeId}`

*Lookup by employee id string.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `employeeId` e.g. `EMP001`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **GET** `/api/admin/users/{id}`

*User by numeric id.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **PUT** `/api/admin/users/{id}`

*Update user (admin — all fields allowed).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body (`application/json`) — `UpdateUserRequest`*

```json
{
  "name": "Updated",
  "status": "ACTIVE"
}
```

**Notes**

- All fields optional: `employeeId`, `name`, `email`, `status`, profile fields, `photoPath`, `specimenSignaturePath`.

### Response

```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **DELETE** `/api/admin/users/{id}`

*Delete user.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null
}
```

**Notes**

- `data` — `null`.


---

## **POST** `/api/admin/users/{id}/reset-password`

*Set new password (raw JSON string body).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

The body must be a **JSON-encoded string** (quotes included), not an object:

```json
"NewSecurePassword123!"
```

### Response

```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": null
}
```

**Notes**

- `data` — `null`.


---

## **GET** `/api/admin/users/employees`

*List all employees.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "employeeId": "EMP001",
      "name": "Ravi Kumar",
      "email": "ravi@example.com",
      "role": "EMPLOYEE",
      "status": "ACTIVE",
      "address": "123 Street",
      "dateOfBirth": "1990-05-01",
      "bloodGroup": "A_POSITIVE",
      "validDocumentPath": null,
      "employeeStatus": "ACTIVE",
      "fatherName": "Father Name",
      "dateOfJoining": "2024-01-15",
      "officeContactNumber": "+911234567890",
      "homeContactNumber": null,
      "otherContactNumber": null,
      "identificationMark": null,
      "specimenSignaturePath": "uploads/signatures/1_sig.png",
      "photoPath": "uploads/photos/1.jpg",
      "createdAt": "2026-01-01T10:00:00"
    }
  ]
}
```

**Notes**

- `data` — `UserResponse[]`.


---

## **PUT** `/api/admin/users/{id}/activate`

*Activate account.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "User activated successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **PUT** `/api/admin/users/{id}/deactivate`

*Deactivate / suspend.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "User deactivated successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **POST** `/api/admin/users/{id}/document`

*Upload valid document (PDF/JPG/PNG).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*`multipart/form-data`*

| Part | Type | Description |
|------|------|-------------|
| `file` | file | max 5MB typical |

### Response

```json
{
  "success": true,
  "message": "Document uploaded successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **POST** `/api/admin/users/{id}/photo`

*Upload employee photo.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*`multipart/form-data`*

| Part | Type | Description |
|------|------|-------------|
| `file` | file | JPG/PNG |

### Response

```json
{
  "success": true,
  "message": "Photo uploaded successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.


---

## **POST** `/api/admin/users/{id}/signature`

*Upload specimen signature.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*`multipart/form-data`*

| Part | Type | Description |
|------|------|-------------|
| `file` | file | JPG/PNG |

### Response

```json
{
  "success": true,
  "message": "Signature uploaded successfully",
  "data": {
    "id": 1,
    "employeeId": "EMP001",
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "address": "123 Street",
    "dateOfBirth": "1990-05-01",
    "bloodGroup": "A_POSITIVE",
    "validDocumentPath": null,
    "employeeStatus": "ACTIVE",
    "fatherName": "Father Name",
    "dateOfJoining": "2024-01-15",
    "officeContactNumber": "+911234567890",
    "homeContactNumber": null,
    "otherContactNumber": null,
    "identificationMark": null,
    "specimenSignaturePath": "uploads/signatures/1_sig.png",
    "photoPath": "uploads/photos/1.jpg",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `UserResponse`.

## 10. Admin — sites (`/api/admin/sites` …)


---

## **POST** `/api/admin/sites`

*Create site.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body — `CreateSiteRequest`*

```json
{
  "name": "New Site",
  "jobCode": "JOB-NEW",
  "address": "Addr",
  "customerName": "Client",
  "estimatedDays": 25,
  "siteStartDate": "2026-06-01",
  "siteEndDate": "2026-06-30",
  "totalProjectDays": 30
}
```

**Notes**

- `name`, `jobCode` — required strings.
- `address`, `customerName`, `estimatedDays`, `inchargeUserId`, `locationSiteId`, `siteStartDate`, `siteEndDate`, `totalProjectDays` — optional.

### Response

```json
{
  "success": true,
  "message": "Site created successfully",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/admin/sites`

*All sites as flat array (name order).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    }
  ]
}
```

**Notes**

- `data` — `SiteResponse[]`.


---

## **GET** `/api/admin/sites/paged`

*Paged search.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `search` | No | name/jobCode |
| `isActive` | No | boolean |
| `page` | No | undefined |
| `size` | No | default 20 |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Site Alpha",
        "jobCode": "JOB-001",
        "address": "Industrial Area",
        "isActive": true,
        "customerName": "Acme Ltd",
        "estimatedDays": 30,
        "inchargeUserId": 2,
        "inchargeName": "Lead User",
        "inchargeEmployeeId": "EMP002",
        "locationSiteId": null,
        "locationSiteLabel": null,
        "siteStartDate": "2026-01-01",
        "siteEndDate": "2026-01-31",
        "totalProjectDays": 31,
        "certificateClientStatus": "NONE",
        "customerFeedbackApprovedAt": null,
        "createdAt": "2026-01-01T08:00:00",
        "updatedAt": "2026-01-01T08:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — `Page<SiteResponse>`.


---

## **GET** `/api/admin/site-options`

*Active sites for dropdowns.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    }
  ]
}
```

**Notes**

- `data` — `SiteResponse[]`.


---

## **GET** `/api/admin/sites/{id}`

*Get site.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **PUT** `/api/admin/sites/{id}`

*Update site — `UpdateSiteRequest`.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body*

```json
{
  "name": "Renamed",
  "isActive": true
}
```

**Notes**

- Optional: `name`, `jobCode`, `address`, `isActive`, `customerName`, `estimatedDays`, `inchargeUserId`, `clearIncharge`, `locationSiteId`, `clearLocationSite`, dates, `totalProjectDays`.

### Response

```json
{
  "success": true,
  "message": "Site updated successfully",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **DELETE** `/api/admin/sites/{id}`

*Delete site.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Site deleted successfully",
  "data": null
}
```

**Notes**

- `data` — `null`.


---

## **PUT** `/api/admin/sites/{id}/activate`

*Set active.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Site activated successfully",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **PUT** `/api/admin/sites/{id}/deactivate`

*Set inactive.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Site deactivated successfully",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.

## 11. Admin — attendance (`/api/admin/attendance`)


---

## **GET** `/api/admin/attendance`

*Filter all attendance (paged).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `date` | No | ISO date |
| `employeeId` | No | user id |
| `siteId` | No | undefined |
| `jobCode` | No | undefined |
| `status` | No | `PENDING`|`APPROVED`|`REJECTED` |
| `page` | No | undefined |
| `size` | No | undefined |

### Request

No request body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 10,
        "employee": {
          "id": 1,
          "employeeId": "EMP001",
          "name": "Ravi Kumar",
          "email": "ravi@example.com",
          "role": "EMPLOYEE",
          "status": "ACTIVE",
          "address": "123 Street",
          "dateOfBirth": "1990-05-01",
          "bloodGroup": "A_POSITIVE",
          "validDocumentPath": null,
          "employeeStatus": "ACTIVE",
          "fatherName": "Father Name",
          "dateOfJoining": "2024-01-15",
          "officeContactNumber": "+911234567890",
          "homeContactNumber": null,
          "otherContactNumber": null,
          "identificationMark": null,
          "specimenSignaturePath": "uploads/signatures/1_sig.png",
          "photoPath": "uploads/photos/1.jpg",
          "createdAt": "2026-01-01T10:00:00"
        },
        "site": {
          "id": 1,
          "name": "Site Alpha",
          "jobCode": "JOB-001",
          "address": "Industrial Area",
          "isActive": true,
          "customerName": "Acme Ltd",
          "estimatedDays": 30,
          "inchargeUserId": 2,
          "inchargeName": "Lead User",
          "inchargeEmployeeId": "EMP002",
          "locationSiteId": null,
          "locationSiteLabel": null,
          "siteStartDate": "2026-01-01",
          "siteEndDate": "2026-01-31",
          "totalProjectDays": 31,
          "certificateClientStatus": "NONE",
          "customerFeedbackApprovedAt": null,
          "createdAt": "2026-01-01T08:00:00",
          "updatedAt": "2026-01-01T08:00:00"
        },
        "date": "2026-05-20",
        "time": "09:15:00",
        "photoPath": "uploads/attendance/10.jpg",
        "status": "PENDING",
        "rejectionReason": null,
        "shift": "FULL_DAY",
        "createdAt": "2026-05-20T09:15:01"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — `Page<AttendanceResponse>`.


---

## **GET** `/api/admin/attendance/{id}`

*One record.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 10,
    "employee": {
      "id": 1,
      "employeeId": "EMP001",
      "name": "Ravi Kumar",
      "email": "ravi@example.com",
      "role": "EMPLOYEE",
      "status": "ACTIVE",
      "address": "123 Street",
      "dateOfBirth": "1990-05-01",
      "bloodGroup": "A_POSITIVE",
      "validDocumentPath": null,
      "employeeStatus": "ACTIVE",
      "fatherName": "Father Name",
      "dateOfJoining": "2024-01-15",
      "officeContactNumber": "+911234567890",
      "homeContactNumber": null,
      "otherContactNumber": null,
      "identificationMark": null,
      "specimenSignaturePath": "uploads/signatures/1_sig.png",
      "photoPath": "uploads/photos/1.jpg",
      "createdAt": "2026-01-01T10:00:00"
    },
    "site": {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    },
    "date": "2026-05-20",
    "time": "09:15:00",
    "photoPath": "uploads/attendance/10.jpg",
    "status": "PENDING",
    "rejectionReason": null,
    "shift": "FULL_DAY",
    "createdAt": "2026-05-20T09:15:01"
  }
}
```

**Notes**

- `data` — `AttendanceResponse`.


---

## **GET** `/api/admin/attendance/employee/{employeeId}`

*By employee user id.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `page` | No | undefined |
| `size` | No | undefined |

### Request

Path: `employeeId` (Long).

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 10,
        "employee": {
          "id": 1,
          "employeeId": "EMP001",
          "name": "Ravi Kumar",
          "email": "ravi@example.com",
          "role": "EMPLOYEE",
          "status": "ACTIVE",
          "address": "123 Street",
          "dateOfBirth": "1990-05-01",
          "bloodGroup": "A_POSITIVE",
          "validDocumentPath": null,
          "employeeStatus": "ACTIVE",
          "fatherName": "Father Name",
          "dateOfJoining": "2024-01-15",
          "officeContactNumber": "+911234567890",
          "homeContactNumber": null,
          "otherContactNumber": null,
          "identificationMark": null,
          "specimenSignaturePath": "uploads/signatures/1_sig.png",
          "photoPath": "uploads/photos/1.jpg",
          "createdAt": "2026-01-01T10:00:00"
        },
        "site": {
          "id": 1,
          "name": "Site Alpha",
          "jobCode": "JOB-001",
          "address": "Industrial Area",
          "isActive": true,
          "customerName": "Acme Ltd",
          "estimatedDays": 30,
          "inchargeUserId": 2,
          "inchargeName": "Lead User",
          "inchargeEmployeeId": "EMP002",
          "locationSiteId": null,
          "locationSiteLabel": null,
          "siteStartDate": "2026-01-01",
          "siteEndDate": "2026-01-31",
          "totalProjectDays": 31,
          "certificateClientStatus": "NONE",
          "customerFeedbackApprovedAt": null,
          "createdAt": "2026-01-01T08:00:00",
          "updatedAt": "2026-01-01T08:00:00"
        },
        "date": "2026-05-20",
        "time": "09:15:00",
        "photoPath": "uploads/attendance/10.jpg",
        "status": "PENDING",
        "rejectionReason": null,
        "shift": "FULL_DAY",
        "createdAt": "2026-05-20T09:15:01"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — `Page<AttendanceResponse>`.


---

## **GET** `/api/admin/attendance/site/{siteId}`

*By site.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `page` | No | undefined |
| `size` | No | undefined |

### Request

Path: `siteId`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 10,
        "employee": {
          "id": 1,
          "employeeId": "EMP001",
          "name": "Ravi Kumar",
          "email": "ravi@example.com",
          "role": "EMPLOYEE",
          "status": "ACTIVE",
          "address": "123 Street",
          "dateOfBirth": "1990-05-01",
          "bloodGroup": "A_POSITIVE",
          "validDocumentPath": null,
          "employeeStatus": "ACTIVE",
          "fatherName": "Father Name",
          "dateOfJoining": "2024-01-15",
          "officeContactNumber": "+911234567890",
          "homeContactNumber": null,
          "otherContactNumber": null,
          "identificationMark": null,
          "specimenSignaturePath": "uploads/signatures/1_sig.png",
          "photoPath": "uploads/photos/1.jpg",
          "createdAt": "2026-01-01T10:00:00"
        },
        "site": {
          "id": 1,
          "name": "Site Alpha",
          "jobCode": "JOB-001",
          "address": "Industrial Area",
          "isActive": true,
          "customerName": "Acme Ltd",
          "estimatedDays": 30,
          "inchargeUserId": 2,
          "inchargeName": "Lead User",
          "inchargeEmployeeId": "EMP002",
          "locationSiteId": null,
          "locationSiteLabel": null,
          "siteStartDate": "2026-01-01",
          "siteEndDate": "2026-01-31",
          "totalProjectDays": 31,
          "certificateClientStatus": "NONE",
          "customerFeedbackApprovedAt": null,
          "createdAt": "2026-01-01T08:00:00",
          "updatedAt": "2026-01-01T08:00:00"
        },
        "date": "2026-05-20",
        "time": "09:15:00",
        "photoPath": "uploads/attendance/10.jpg",
        "status": "PENDING",
        "rejectionReason": null,
        "shift": "FULL_DAY",
        "createdAt": "2026-05-20T09:15:01"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

**Notes**

- `data` — `Page<AttendanceResponse>`.


---

## **PUT** `/api/admin/attendance/{id}/approve`

*Approve or reject.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body — `ApproveAttendanceRequest`*

```json
{
  "status": "APPROVED",
  "rejectionReason": null
}
```

**Notes**

- `status` — required: `APPROVED` or `REJECTED`.
- `rejectionReason` — optional string when rejecting.

### Response

```json
{
  "success": true,
  "message": "Attendance updated successfully",
  "data": {
    "id": 10,
    "employee": {
      "id": 1,
      "employeeId": "EMP001",
      "name": "Ravi Kumar",
      "email": "ravi@example.com",
      "role": "EMPLOYEE",
      "status": "ACTIVE",
      "address": "123 Street",
      "dateOfBirth": "1990-05-01",
      "bloodGroup": "A_POSITIVE",
      "validDocumentPath": null,
      "employeeStatus": "ACTIVE",
      "fatherName": "Father Name",
      "dateOfJoining": "2024-01-15",
      "officeContactNumber": "+911234567890",
      "homeContactNumber": null,
      "otherContactNumber": null,
      "identificationMark": null,
      "specimenSignaturePath": "uploads/signatures/1_sig.png",
      "photoPath": "uploads/photos/1.jpg",
      "createdAt": "2026-01-01T10:00:00"
    },
    "site": {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    },
    "date": "2026-05-20",
    "time": "09:15:00",
    "photoPath": "uploads/attendance/10.jpg",
    "status": "PENDING",
    "rejectionReason": null,
    "shift": "FULL_DAY",
    "createdAt": "2026-05-20T09:15:01"
  }
}
```

**Notes**

- `data` — `AttendanceResponse`.


---

## **PUT** `/api/admin/attendance/{id}/shift`

*Change shift.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body — `UpdateAttendanceShiftRequest`*

```json
{
  "shift": "FIRST_HALF"
}
```

**Notes**

- `shift` — `FIRST_HALF` | `SECOND_HALF` | `FULL_DAY`.

### Response

```json
{
  "success": true,
  "message": "Shift updated successfully",
  "data": {
    "id": 10,
    "employee": {
      "id": 1,
      "employeeId": "EMP001",
      "name": "Ravi Kumar",
      "email": "ravi@example.com",
      "role": "EMPLOYEE",
      "status": "ACTIVE",
      "address": "123 Street",
      "dateOfBirth": "1990-05-01",
      "bloodGroup": "A_POSITIVE",
      "validDocumentPath": null,
      "employeeStatus": "ACTIVE",
      "fatherName": "Father Name",
      "dateOfJoining": "2024-01-15",
      "officeContactNumber": "+911234567890",
      "homeContactNumber": null,
      "otherContactNumber": null,
      "identificationMark": null,
      "specimenSignaturePath": "uploads/signatures/1_sig.png",
      "photoPath": "uploads/photos/1.jpg",
      "createdAt": "2026-01-01T10:00:00"
    },
    "site": {
      "id": 1,
      "name": "Site Alpha",
      "jobCode": "JOB-001",
      "address": "Industrial Area",
      "isActive": true,
      "customerName": "Acme Ltd",
      "estimatedDays": 30,
      "inchargeUserId": 2,
      "inchargeName": "Lead User",
      "inchargeEmployeeId": "EMP002",
      "locationSiteId": null,
      "locationSiteLabel": null,
      "siteStartDate": "2026-01-01",
      "siteEndDate": "2026-01-31",
      "totalProjectDays": 31,
      "certificateClientStatus": "NONE",
      "customerFeedbackApprovedAt": null,
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    },
    "date": "2026-05-20",
    "time": "09:15:00",
    "photoPath": "uploads/attendance/10.jpg",
    "status": "PENDING",
    "rejectionReason": null,
    "shift": "FULL_DAY",
    "createdAt": "2026-05-20T09:15:01"
  }
}
```

**Notes**

- `data` — `AttendanceResponse`.


---

## **DELETE** `/api/admin/attendance/{id}`

*Delete record.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Attendance deleted successfully",
  "data": null
}
```

**Notes**

- `data` — `null`.

## 12. Admin — notices (`/api/admin/notices`)


---

## **POST** `/api/admin/notices`

*Create notice.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body — `CreateNoticeRequest`*

```json
{
  "message": "Board update for all staff."
}
```

**Notes**

- `message` — required, max 2000 chars.

### Response

```json
{
  "success": true,
  "message": "Notice created successfully",
  "data": {
    "id": 1,
    "message": "Board update for all staff.",
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `NoticeResponse`.


---

## **GET** `/api/admin/notices`

*Paged + search.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `search` | No | undefined |
| `page` | No | undefined |
| `size` | No | default 20 |

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [],
    "totalElements": 0,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": true
  }
}
```

**Notes**

- `data` — `Page<NoticeResponse>`.


---

## **GET** `/api/admin/notices/{id}`

*Get one.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "message": "Board update",
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `NoticeResponse`.


---

## **PUT** `/api/admin/notices/{id}`

*Update message.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body — `UpdateNoticeRequest`*

```json
{
  "message": "Updated text"
}
```

**Notes**

- `message` — optional, max 2000.

### Response

```json
{
  "success": true,
  "message": "Notice updated successfully",
  "data": {
    "id": 1,
    "message": "Updated text",
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-02T09:00:00"
  }
}
```

**Notes**

- `data` — `NoticeResponse`.


---

## **DELETE** `/api/admin/notices/{id}`

*Delete.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: `id`.

### Response

```json
{
  "success": true,
  "message": "Notice deleted successfully",
  "data": null
}
```

**Notes**

- `data` — `null`.

## 13. Admin — site job data (`/api/admin/sites/{id}/…`)

All require **ADMIN**. Path `{id}` = site id.


---

## **GET** `/api/admin/sites/{id}/wizard`

*Get wizard JSON blob (string).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": "{\"step\":1,\"payload\":{}}"
}
```

**Notes**

- `data` — string containing JSON text for UI wizard state.


---

## **PUT** `/api/admin/sites/{id}/wizard`

*Replace wizard blob.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body (`application/json`)*

```json
{
  "step": 2,
  "pages": []
}
```

**Notes**

- Any JSON object; stored as string internally.

### Response

```json
{
  "success": true,
  "message": "Wizard saved",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/admin/sites/{id}/attendance-register`

*Paper-style register grid.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `periodStart` | No | ISO date; default from site |
| `blockIndex` | No | default 0 |
| `daysPerBlock` | No | default 15, max 366 |
| `employeeIds` | No | repeat query param for each Long |

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 1,
    "jobCode": "JOB-001",
    "customerName": "Acme Ltd",
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "estimatedDays": 30,
    "periodStart": "2026-01-01",
    "periodEnd": "2026-01-15",
    "blockIndex": 0,
    "dayDates": [
      "2026-01-01",
      "2026-01-02"
    ],
    "rows": [
      {
        "slNo": 1,
        "employeeId": 1,
        "employeeName": "Ravi",
        "dayCodes": [
          "P",
          ""
        ]
      }
    ]
  }
}
```

**Notes**

- `data` — `AttendanceRegisterResponse`: site meta, `periodStart`/`periodEnd`, `blockIndex`, `dayDates[]`, `rows[]` with `slNo`, `employeeId`, `employeeName`, `dayCodes[]`.


---

## **PUT** `/api/admin/sites/{id}/job-data/attendance-register-cells`

*Upsert register cell overrides.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body — `SaveRegisterCellsRequest`*

```json
{
  "cells": [
    {
      "employeeUserId": 1,
      "date": "2026-01-10",
      "code": "HQ"
    }
  ]
}
```

**Notes**

- `cells` — array of `{ employeeUserId, date (ISO), code }`.
- `code`: `P`|`A`|`S`|`HQ`|`LS`|`IN` or `null` to clear override.

### Response

```json
{
  "success": true,
  "message": "Register cells saved",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/admin/sites/{id}/job-data/advance-expense-lines`

*Screen 5 advance/expense rows.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "lineOrder": 1,
      "advanceReceivedDate": "2026-01-05",
      "openingBal": 1000,
      "amount": 500,
      "foodAllow": 0,
      "conveyance": 0,
      "medical": 0,
      "additionalManpower": 0,
      "welding": 0,
      "siteExpn": 0,
      "balInHand": 1500,
      "dispersionNotes": ""
    }
  ]
}
```

**Notes**

- `data` — `SiteAdvanceExpenseLineDto[]`.


---

## **PUT** `/api/admin/sites/{id}/job-data/advance-expense-lines`

*Replace all rows.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body (`application/json`)*

```json
[
  {
    "lineOrder": 1,
    "advanceReceivedDate": "2026-01-05",
    "openingBal": 1000,
    "amount": 500,
    "foodAllow": 0,
    "conveyance": 0,
    "medical": 0,
    "additionalManpower": 0,
    "welding": 0,
    "siteExpn": 0,
    "balInHand": 1500,
    "dispersionNotes": ""
  }
]
```

**Notes**

- Array of `SiteAdvanceExpenseLineDto` (see GET example fields).

### Response

```json
{
  "success": true,
  "message": "Advance lines saved",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/admin/sites/{id}/job-data/technician-payments`

*Technician daily payments.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "lineOrder": 1,
      "technicianUserId": 5,
      "paymentDate": "2026-01-10",
      "amount": 1200
    }
  ]
}
```

**Notes**

- `data` — `SiteTechnicianDailyPaymentDto[]`.


---

## **PUT** `/api/admin/sites/{id}/job-data/technician-payments`

*Replace all payment rows.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body*

```json
[
  {
    "lineOrder": 1,
    "technicianUserId": 5,
    "paymentDate": "2026-01-10",
    "amount": 1200
  }
]
```

**Notes**

- Array of `{ lineOrder?, technicianUserId, paymentDate, amount }`.

### Response

```json
{
  "success": true,
  "message": "Technician payments saved",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/admin/sites/{id}/job-data/tool-issues`

*Tools missing/damage/repair.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "lineOrder": 1,
      "pkgListSl": "PL-1",
      "itemDescription": "Hammer",
      "dateMissing": null,
      "dateDamage": "2026-01-08",
      "dateRepair": null,
      "handledByUserId": 2,
      "issueDescription": "Handle crack"
    }
  ]
}
```

**Notes**

- `data` — `SiteToolIssueDto[]`.


---

## **PUT** `/api/admin/sites/{id}/job-data/tool-issues`

*Replace tool issue rows.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body*

```json
[
  {
    "lineOrder": 1,
    "pkgListSl": "PL-1",
    "itemDescription": "Hammer",
    "dateMissing": null,
    "dateDamage": "2026-01-08",
    "dateRepair": null,
    "handledByUserId": 2,
    "issueDescription": "Handle crack"
  }
]
```

**Notes**

- Array of `SiteToolIssueDto`.

### Response

```json
{
  "success": true,
  "message": "Tool issues saved",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/admin/sites/{id}/job-data/behaviour-report`

*Behaviour matrix JSON string.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": "{}"
}
```

**Notes**

- `data` — string (JSON text).


---

## **PUT** `/api/admin/sites/{id}/job-data/behaviour-report`

*Replace behaviour JSON.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body*

```json
{
  "rows": []
}
```

**Notes**

- Any JSON object.

### Response

```json
{
  "success": true,
  "message": "Behaviour report saved",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **GET** `/api/admin/sites/{id}/job-data/challenge-lines`

*Challenge rows.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "lineOrder": 1,
      "headLabel": "Safety",
      "challengeCatalogIndex": null,
      "incidentDate": "2026-01-12",
      "involvedUserId": 3,
      "challengesFaced": "Access issue",
      "status": "PENDING"
    }
  ]
}
```

**Notes**

- `data` — `SiteChallengeLineDto[]`.


---

## **PUT** `/api/admin/sites/{id}/job-data/challenge-lines`

*Replace challenges.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body*

```json
[
  {
    "lineOrder": 1,
    "headLabel": "Safety",
    "challengeCatalogIndex": null,
    "incidentDate": "2026-01-12",
    "involvedUserId": 3,
    "challengesFaced": "Access issue",
    "status": "PENDING"
  }
]
```

**Notes**

- Array of rows: `lineOrder`, `headLabel` (or `challengeCatalogIndex` 1-based from meta), `incidentDate`, `involvedUserId`, `challengesFaced`, `status` (`RESOLVED`|`PENDING`|`ACTION_TAKEN`).

### Response

```json
{
  "success": true,
  "message": "Challenges saved",
  "data": {
    "id": 1,
    "name": "Site Alpha",
    "jobCode": "JOB-001",
    "address": "Industrial Area",
    "isActive": true,
    "customerName": "Acme Ltd",
    "estimatedDays": 30,
    "inchargeUserId": 2,
    "inchargeName": "Lead User",
    "inchargeEmployeeId": "EMP002",
    "locationSiteId": null,
    "locationSiteLabel": null,
    "siteStartDate": "2026-01-01",
    "siteEndDate": "2026-01-31",
    "totalProjectDays": 31,
    "certificateClientStatus": "NONE",
    "customerFeedbackApprovedAt": null,
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

**Notes**

- `data` — `SiteResponse`.


---

## **POST** `/api/admin/sites/{id}/feedback-invites`

*Create public feedback token.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "token": "opaque-token",
    "expiresAt": "2026-06-01T12:00:00",
    "relativePath": "/api/public/feedback/opaque-token"
  }
}
```

**Notes**

- `data` — `FeedbackInviteResponse`: `token`, `expiresAt`, `relativePath`.


---

## **GET** `/api/admin/sites/{id}/customer-feedback`

*Admin view of feedback + certificate state.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "certificateClientStatus": "FEEDBACK_SUBMITTED",
    "customerFeedbackApprovedAt": null,
    "feedbackJson": "{}"
  }
}
```

**Notes**

- `data` — `certificateClientStatus`, `customerFeedbackApprovedAt`, `feedbackJson` (string).

## 14. Admin — machinery (`/api/admin/machinery`)


---

## **GET** `/api/admin/machinery`

*Catalog for one site.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `siteId` | Yes | Long |

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "code": "EX-01",
      "name": "Excavator",
      "itemDescription": null,
      "jobCode": "JOB-001",
      "defaultUom": "HR",
      "siteId": 1,
      "siteName": "Site Alpha",
      "imagePath": null,
      "serialNumber": "SN-1",
      "model": "X200",
      "status": "ACTIVE",
      "createdAt": "2026-01-01T10:00:00",
      "updatedAt": "2026-01-01T10:00:00"
    }
  ]
}
```

**Notes**

- `data` — `MachineryResponse[]`.


---

## **POST** `/api/admin/machinery`

*Create machinery (optional image).*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*`multipart/form-data`*

| Part | Type | Description |
|------|------|-------------|
| `data` | text (`application/json`) | `CreateMachineryRequest` JSON |
| `image` | file | optional |

```json
{
  "code": "EX-02",
  "name": "Crane",
  "itemDescription": null,
  "jobCode": "JOB-001",
  "defaultUom": "HR",
  "siteId": 1,
  "serialNumber": null,
  "model": null,
  "status": "ACTIVE",
  "markUsedOnDate": null
}
```

### Response

```json
{
  "success": true,
  "message": "Machinery created",
  "data": {
    "id": 1,
    "code": "EX-01",
    "name": "Excavator",
    "itemDescription": null,
    "jobCode": "JOB-001",
    "defaultUom": "HR",
    "siteId": 1,
    "siteName": "Site Alpha",
    "imagePath": null,
    "serialNumber": "SN-1",
    "model": "X200",
    "status": "ACTIVE",
    "createdAt": "2026-01-01T10:00:00",
    "updatedAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `MachineryResponse`.


---

## **PUT** `/api/admin/machinery/{id}`

*Update machinery.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*`multipart/form-data`*

- Part `data`: JSON `UpdateMachineryRequest` (all fields optional).
- Part `image`: optional file.

```json
{
  "name": "Crane XL",
  "status": "ACTIVE"
}
```

### Response

```json
{
  "success": true,
  "message": "Machinery updated",
  "data": {
    "id": 1,
    "code": "EX-01",
    "name": "Excavator",
    "itemDescription": null,
    "jobCode": "JOB-001",
    "defaultUom": "HR",
    "siteId": 1,
    "siteName": "Site Alpha",
    "imagePath": null,
    "serialNumber": "SN-1",
    "model": "X200",
    "status": "ACTIVE",
    "createdAt": "2026-01-01T10:00:00",
    "updatedAt": "2026-01-01T10:00:00"
  }
}
```

**Notes**

- `data` — `MachineryResponse`.


---

## **DELETE** `/api/admin/machinery/{id}`

*Delete if no usage history.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

Path: machinery `id`.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": null
}
```

**Notes**

- `data` — `null` (message may be generic `Success`).


---

## **GET** `/api/admin/machinery/usage/selection`

*Daily usage grid for site+date.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `siteId` | Yes | undefined |
| `date` | Yes | ISO |

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 1,
    "date": "2026-05-20",
    "lines": [
      {
        "machineryId": 1,
        "code": "EX-01",
        "name": "Excavator",
        "itemDescription": null,
        "catalogStatus": "ACTIVE",
        "imagePath": null,
        "qty": 8.5,
        "uom": "HR",
        "jobCode": "JOB-001",
        "notes": null
      }
    ]
  }
}
```

**Notes**

- `data` — `siteId`, `date`, `lines[]` (`MachineryUsageLineResponse`).


---

## **PUT** `/api/admin/machinery/usage/selection`

*Replace usage lines for site+date.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Request

*Request body (`application/json`) — `SaveUsageSelectionRequest`*

```json
{
  "siteId": 1,
  "date": "2026-05-20",
  "lines": [
    {
      "machineryId": 1,
      "qty": 8,
      "uom": "HR",
      "jobCode": "JOB-001",
      "notes": ""
    }
  ]
}
```

**Notes**

- `siteId` (long, required).
- `date` (ISO date, required).
- `lines` (array, required): each `{ machineryId, qty (≥0), uom, jobCode?, notes? }`.

### Response

```json
{
  "success": true,
  "message": "Usage saved",
  "data": null
}
```

**Notes**

- `data` — `null`.


---

## **GET** `/api/admin/machinery/usage/summary/month`

*Monthly machinery usage overview.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `siteId` | Yes | undefined |
| `year` | Yes | undefined |
| `month` | Yes | 1–12 |

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 1,
    "year": 2026,
    "month": 5,
    "days": [
      {
        "date": "2026-05-20",
        "machineCount": 2,
        "machineCodes": [
          "EX-01",
          "EX-02"
        ]
      }
    ]
  }
}
```

**Notes**

- `data` — `siteId`, `year`, `month`, `days[]` with `date`, `machineCount`, `machineCodes[]`.


---

## **GET** `/api/admin/machinery/usage/summary/year`

*Yearly overview.*

**Auth**

- Bearer JWT
- **Access:** ADMIN

### Query parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `siteId` | Yes | undefined |
| `year` | Yes | undefined |

### Request

No body.

### Response

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 1,
    "year": 2026,
    "months": [
      {
        "month": 5,
        "monthName": "MAY",
        "daysWithUsage": 10,
        "totalMachineDays": 25,
        "topMachineCodes": [
          "EX-01"
        ]
      }
    ]
  }
}
```

**Notes**

- `data` — `siteId`, `year`, `months[]` with `month`, `monthName`, `daysWithUsage`, `totalMachineDays`, `topMachineCodes[]`.

## Appendix — enum values (JSON)

| Enum | Values |
|------|--------|
| `Role` | `ADMIN`, `EMPLOYEE` |
| `UserStatus` | `ACTIVE`, `INACTIVE` |
| `EmployeeStatus` | `ACTIVE`, `ON_LEAVE`, `TERMINATED`, `SUSPENDED` |
| `AttendanceStatus` | `PENDING`, `APPROVED`, `REJECTED` |
| `Shift` | `FIRST_HALF`, `SECOND_HALF`, `FULL_DAY` |
| `BloodGroup` | `A_POSITIVE`, `A_NEGATIVE`, … `O_NEGATIVE` |
| `CertificateClientStatus` | `NONE`, `FEEDBACK_SUBMITTED`, `APPROVED_BY_CLIENT` |
| `MachineryStatus` | `ACTIVE`, `MAINTENANCE`, `RETIRED` |
| `SiteChallengeStatus` | `RESOLVED`, `PENDING`, `ACTION_TAKEN` |
| `RegisterAttendanceCode` | `P`, `A`, `S`, `HQ`, `LS`, `IN` |

## Appendix — `UserResponse` shape

```json
{
  "id": 1,
  "employeeId": "EMP001",
  "name": "Ravi Kumar",
  "email": "ravi@example.com",
  "role": "EMPLOYEE",
  "status": "ACTIVE",
  "address": "123 Street",
  "dateOfBirth": "1990-05-01",
  "bloodGroup": "A_POSITIVE",
  "validDocumentPath": null,
  "employeeStatus": "ACTIVE",
  "fatherName": "Father Name",
  "dateOfJoining": "2024-01-15",
  "officeContactNumber": "+911234567890",
  "homeContactNumber": null,
  "otherContactNumber": null,
  "identificationMark": null,
  "specimenSignaturePath": "uploads/signatures/1_sig.png",
  "photoPath": "uploads/photos/1.jpg",
  "createdAt": "2026-01-01T10:00:00"
}
```

## Appendix — `SiteResponse` shape

```json
{
  "id": 1,
  "name": "Site Alpha",
  "jobCode": "JOB-001",
  "address": "Industrial Area",
  "isActive": true,
  "customerName": "Acme Ltd",
  "estimatedDays": 30,
  "inchargeUserId": 2,
  "inchargeName": "Lead User",
  "inchargeEmployeeId": "EMP002",
  "locationSiteId": null,
  "locationSiteLabel": null,
  "siteStartDate": "2026-01-01",
  "siteEndDate": "2026-01-31",
  "totalProjectDays": 31,
  "certificateClientStatus": "NONE",
  "customerFeedbackApprovedAt": null,
  "createdAt": "2026-01-01T08:00:00",
  "updatedAt": "2026-01-01T08:00:00"
}
```

---

*Generated by `scripts/build-api-reference-md.js`. Regenerate after API changes.*