# Attendance System API — each endpoint

Generated from `Attendance-System.postman_collection.json` (run `node scripts/build-postman-collection.js` then `node scripts/build-api-each-markdown.js`).

Replace `{{accessToken}}`, `{{publicFeedbackToken}}`, etc. where shown if your values differ from collection defaults.

---

## API: Admin Login

**Endpoint:**

```
POST /api/auth/admin/login
```

**Full URL:**

```
http://localhost:8080/api/auth/admin/login
```

**Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "email": "admin@example.com",
  "password": "your-password"
}
```

**Success Response (200 OK):**

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

---

## API: Employee Login

**Endpoint:**

```
POST /api/auth/employee/login
```

**Full URL:**

```
http://localhost:8080/api/auth/employee/login
```

**Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "email": "employee@example.com",
  "password": "your-password"
}
```

**Success Response (200 OK):**

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

---

## API: Refresh Token

**Endpoint:**

```
POST /api/auth/refresh
```

**Full URL:**

```
http://localhost:8080/api/auth/refresh
```

**Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "refreshToken": "{{refreshToken}}"
}
```

**Success Response (200 OK):**

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

---

## API: Logout

**Endpoint:**

```
POST /api/auth/logout
```

**Full URL:**

```
http://localhost:8080/api/auth/logout
```

**Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "refreshToken": "{{refreshToken}}"
}
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Logout successful",
  "data": null
}
```

---

## API: Auth Home / Warmup

**Endpoint:**

```
GET /api/auth/home
```

**Full URL:**

```
http://localhost:8080/api/auth/home
```

**Headers:**

```
_None required for this request._
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Restart...",
  "data": "Avoid cold start by returning a simple message"
}
```

---

## API: Public Feedback Context

**Endpoint:**

```
GET /api/public/feedback/{{publicFeedbackToken}}
```

**Full URL:**

```
http://localhost:8080/api/public/feedback/{{publicFeedbackToken}}
```

**Headers:**

```
_None required for this request._
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "jobCode": "JOB-001",
    "customerName": "Acme Ltd",
    "companyNameHint": "Hint",
    "certificateClientStatus": "NONE",
    "expired": false,
    "revoked": false
  }
}
```

---

## API: Submit Customer Feedback

**Endpoint:**

```
POST /api/public/feedback/{{publicFeedbackToken}}
```

**Full URL:**

```
http://localhost:8080/api/public/feedback/{{publicFeedbackToken}}
```

**Headers:**

```
Content-Type: application/json
```

**Request Body:**

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

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Feedback saved",
  "data": null
}
```

---

## API: Approve Certificate (Returns PDF Binary)

**Endpoint:**

```
POST /api/public/feedback/{{publicFeedbackToken}}/approve
```

**Full URL:**

```
http://localhost:8080/api/public/feedback/{{publicFeedbackToken}}/approve
```

**Headers:**

```
_None required for this request._
```

**Request Body:**

_No body._

**Success Response (200 OK):**

_No JSON example in collection (often **binary**: PDF or file bytes)._

---

## API: Designations

**Endpoint:**

```
GET /api/meta/designations
```

**Full URL:**

```
http://localhost:8080/api/meta/designations
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Challenge Line Heads

**Endpoint:**

```
GET /api/meta/challenge-line-heads
```

**Full URL:**

```
http://localhost:8080/api/meta/challenge-line-heads
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: All Sites

**Endpoint:**

```
GET /api/sites
```

**Full URL:**

```
http://localhost:8080/api/sites
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Active Sites

**Endpoint:**

```
GET /api/sites/active
```

**Full URL:**

```
http://localhost:8080/api/sites/active
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Site By Id

**Endpoint:**

```
GET /api/sites/{{siteId}}
```

**Full URL:**

```
http://localhost:8080/api/sites/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Site By Job Code

**Endpoint:**

```
GET /api/sites/job-code/{{jobCode}}
```

**Full URL:**

```
http://localhost:8080/api/sites/job-code/JOB-001
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: All Notices (Employees)

**Endpoint:**

```
GET /api/notices
```

**Full URL:**

```
http://localhost:8080/api/notices
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "message": "Holiday notice",
      "createdAt": "2026-01-01T08:00:00",
      "updatedAt": "2026-01-01T08:00:00"
    }
  ]
}
```

---

## API: Download File

**Endpoint:**

```
GET /api/files?path={{filePath}}
```

**Full URL:**

```
http://localhost:8080/api/files?path=uploads/photos/1.jpg
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

_No JSON example in collection (often **binary**: PDF or file bytes)._

---

## API: Me

**Endpoint:**

```
GET /api/users/me
```

**Full URL:**

```
http://localhost:8080/api/users/me
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Me

**Endpoint:**

```
PUT /api/users/me
```

**Full URL:**

```
http://localhost:8080/api/users/me
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "name": "Updated Name",
  "email": "me@example.com"
}
```

**Success Response (200 OK):**

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

---

## API: Me Photo (Multipart)

**Endpoint:**

```
POST /api/users/me/photo
```

**Full URL:**

```
http://localhost:8080/api/users/me/photo
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **file** (file): ``

**Success Response (200 OK):**

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

---

## API: Me Signature (Multipart)

**Endpoint:**

```
POST /api/users/me/signature
```

**Full URL:**

```
http://localhost:8080/api/users/me/signature
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **file** (file): ``

**Success Response (200 OK):**

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

---

## API: Users Paged

**Endpoint:**

```
GET /api/users?page=0&size=10
```

**Full URL:**

```
http://localhost:8080/api/users?page=0&size=10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: User By Id

**Endpoint:**

```
GET /api/users/{{userId}}
```

**Full URL:**

```
http://localhost:8080/api/users/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: User By Id

**Endpoint:**

```
PUT /api/users/{{userId}}
```

**Full URL:**

```
http://localhost:8080/api/users/1
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "name": "Admin update"
}
```

**Success Response (200 OK):**

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

---

## API: Mark Attendance (Multipart, EMPLOYEE)

**Endpoint:**

```
POST /api/attendance/mark
```

**Full URL:**

```
http://localhost:8080/api/attendance/mark
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **photo** (file): ``
- **siteId** (text): "{{siteId}}"
- **shift** (text): "FULL_DAY"

**Success Response (200 OK):**

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

---

## API: My Attendance (Paged)

**Endpoint:**

```
GET /api/attendance/my-attendance?page=0&size=10
```

**Full URL:**

```
http://localhost:8080/api/attendance/my-attendance?page=0&size=10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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
    "number": 0
  }
}
```

---

## API: Attendance By Id

**Endpoint:**

```
GET /api/attendance/{{attendanceId}}
```

**Full URL:**

```
http://localhost:8080/api/attendance/10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: My Attendance By Date

**Endpoint:**

```
GET /api/attendance/my-attendance/date?date=2026-05-20
```

**Full URL:**

```
http://localhost:8080/api/attendance/my-attendance/date?date=2026-05-20
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: My Attendance By Range

**Endpoint:**

```
GET /api/attendance/my-attendance/range?startDate=2026-05-01&endDate=2026-05-31&page=0&size=10
```

**Full URL:**

```
http://localhost:8080/api/attendance/my-attendance/range?startDate=2026-05-01&endDate=2026-05-31&page=0&size=10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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
    "totalElements": 1
  }
}
```

---

## API: My Attendance Calendar

**Endpoint:**

```
GET /api/attendance/my-attendance/calendar
```

**Full URL:**

```
http://localhost:8080/api/attendance/my-attendance/calendar
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: My Attendance Summary (Month)

**Endpoint:**

```
GET /api/attendance/my-attendance/summary?year=2026&month=5
```

**Full URL:**

```
http://localhost:8080/api/attendance/my-attendance/summary?year=2026&month=5
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Admin Dashboard

**Endpoint:**

```
GET /api/admin/dashboard
```

**Full URL:**

```
http://localhost:8080/api/admin/dashboard
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Create User

**Endpoint:**

```
POST /api/admin/users
```

**Full URL:**

```
http://localhost:8080/api/admin/users
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "employeeId": "EMP099",
  "name": "New User",
  "email": "newuser@example.com",
  "password": "atLeast8Chars",
  "role": "EMPLOYEE",
  "employeeStatus": "ACTIVE"
}
```

**Success Response (200 OK):**

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
    "email": "newuser@example.com"
  }
}
```

---

## API: Users Search (Paged)

**Endpoint:**

```
GET /api/admin/users?search=&page=0&size=10
```

**Full URL:**

```
http://localhost:8080/api/admin/users?search=&page=0&size=10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: User By Employee Id

**Endpoint:**

```
GET /api/admin/users/by-employee-id/{{employeeId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/users/by-employee-id/EMP001
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: User By Id

**Endpoint:**

```
GET /api/admin/users/{{userId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Update User

**Endpoint:**

```
PUT /api/admin/users/{{userId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "name": "Updated"
}
```

**Success Response (200 OK):**

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

---

## API: User

**Endpoint:**

```
DELETE /api/admin/users/{{userId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null
}
```

---

## API: Reset Password (Raw JSON String Body)

**Endpoint:**

```
POST /api/admin/users/{{userId}}/reset-password
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1/reset-password
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
"NewPassword123!"
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": null
}
```

---

## API: All Employees

**Endpoint:**

```
GET /api/admin/users/employees
```

**Full URL:**

```
http://localhost:8080/api/admin/users/employees
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Activate User

**Endpoint:**

```
PUT /api/admin/users/{{userId}}/activate
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1/activate
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Deactivate User

**Endpoint:**

```
PUT /api/admin/users/{{userId}}/deactivate
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1/deactivate
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Upload User Document (Multipart)

**Endpoint:**

```
POST /api/admin/users/{{userId}}/document
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1/document
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **file** (file): ``

**Success Response (200 OK):**

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

---

## API: Upload User Photo (Multipart)

**Endpoint:**

```
POST /api/admin/users/{{userId}}/photo
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1/photo
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **file** (file): ``

**Success Response (200 OK):**

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

---

## API: Upload User Signature (Multipart)

**Endpoint:**

```
POST /api/admin/users/{{userId}}/signature
```

**Full URL:**

```
http://localhost:8080/api/admin/users/1/signature
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **file** (file): ``

**Success Response (200 OK):**

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

---

## API: Create Site

**Endpoint:**

```
POST /api/admin/sites
```

**Full URL:**

```
http://localhost:8080/api/admin/sites
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "name": "New Site",
  "jobCode": "JOB-NEW",
  "address": "Addr",
  "customerName": "Client",
  "estimatedDays": 20,
  "siteStartDate": "2026-06-01",
  "siteEndDate": "2026-06-30",
  "totalProjectDays": 30
}
```

**Success Response (200 OK):**

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

---

## API: All Sites (Array)

**Endpoint:**

```
GET /api/admin/sites
```

**Full URL:**

```
http://localhost:8080/api/admin/sites
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Sites Paged

**Endpoint:**

```
GET /api/admin/sites/paged?search=&page=0&size=20
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/paged?search=&page=0&size=20
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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
    "totalElements": 1
  }
}
```

---

## API: Site Options (Active)

**Endpoint:**

```
GET /api/admin/site-options
```

**Full URL:**

```
http://localhost:8080/api/admin/site-options
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Site By Id

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Update Site

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "name": "Renamed Site",
  "isActive": true
}
```

**Success Response (200 OK):**

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

---

## API: Site

**Endpoint:**

```
DELETE /api/admin/sites/{{siteId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Site deleted successfully",
  "data": null
}
```

---

## API: Activate Site

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/activate
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/activate
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Deactivate Site

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/deactivate
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/deactivate
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: All Attendance (Filters)

**Endpoint:**

```
GET /api/admin/attendance?date=2026-05-20&page=0&size=10
```

**Full URL:**

```
http://localhost:8080/api/admin/attendance?date=2026-05-20&page=0&size=10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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
    "totalElements": 1
  }
}
```

---

## API: Attendance By Id

**Endpoint:**

```
GET /api/admin/attendance/{{attendanceId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/attendance/10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Employee Attendance

**Endpoint:**

```
GET /api/admin/attendance/employee/{{userId}}?page=0&size=10
```

**Full URL:**

```
http://localhost:8080/api/admin/attendance/employee/1?page=0&size=10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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
    ]
  }
}
```

---

## API: Site Attendance

**Endpoint:**

```
GET /api/admin/attendance/site/{{siteId}}?page=0&size=10
```

**Full URL:**

```
http://localhost:8080/api/admin/attendance/site/1?page=0&size=10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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
    ]
  }
}
```

---

## API: Approve/Reject Attendance

**Endpoint:**

```
PUT /api/admin/attendance/{{attendanceId}}/approve
```

**Full URL:**

```
http://localhost:8080/api/admin/attendance/10/approve
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "status": "APPROVED",
  "rejectionReason": null
}
```

**Success Response (200 OK):**

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

---

## API: Update Attendance Shift

**Endpoint:**

```
PUT /api/admin/attendance/{{attendanceId}}/shift
```

**Full URL:**

```
http://localhost:8080/api/admin/attendance/10/shift
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "shift": "FIRST_HALF"
}
```

**Success Response (200 OK):**

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

---

## API: Attendance

**Endpoint:**

```
DELETE /api/admin/attendance/{{attendanceId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/attendance/10
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Attendance deleted successfully",
  "data": null
}
```

---

## API: Create Notice

**Endpoint:**

```
POST /api/admin/notices
```

**Full URL:**

```
http://localhost:8080/api/admin/notices
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "message": "Board message"
}
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Notice created successfully",
  "data": {
    "id": 1,
    "message": "Board message",
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

---

## API: Notices Paged

**Endpoint:**

```
GET /api/admin/notices?search=&page=0&size=20
```

**Full URL:**

```
http://localhost:8080/api/admin/notices?search=&page=0&size=20
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [],
    "totalElements": 0
  }
}
```

---

## API: Notice By Id

**Endpoint:**

```
GET /api/admin/notices/1
```

**Full URL:**

```
http://localhost:8080/api/admin/notices/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "message": "Board message",
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-01T08:00:00"
  }
}
```

---

## API: Update Notice

**Endpoint:**

```
PUT /api/admin/notices/1
```

**Full URL:**

```
http://localhost:8080/api/admin/notices/1
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "message": "Updated"
}
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Notice updated successfully",
  "data": {
    "id": 1,
    "message": "Updated",
    "createdAt": "2026-01-01T08:00:00",
    "updatedAt": "2026-01-02T08:00:00"
  }
}
```

---

## API: Notice

**Endpoint:**

```
DELETE /api/admin/notices/1
```

**Full URL:**

```
http://localhost:8080/api/admin/notices/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Notice deleted successfully",
  "data": null
}
```

---

## API: Wizard JSON

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/wizard
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/wizard
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": "{\"step\":1}"
}
```

---

## API: Wizard JSON

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/wizard
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/wizard
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "step": 1,
  "data": {}
}
```

**Success Response (200 OK):**

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

---

## API: Attendance Register

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/attendance-register?blockIndex=0&daysPerBlock=15
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/attendance-register?blockIndex=0&daysPerBlock=15
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Attendance Register Cells

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/job-data/attendance-register-cells
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/attendance-register-cells
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

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

**Success Response (200 OK):**

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

---

## API: Advance Expense Lines

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/job-data/advance-expense-lines
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/advance-expense-lines
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": []
}
```

---

## API: Advance Expense Lines

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/job-data/advance-expense-lines
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/advance-expense-lines
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
[]
```

**Success Response (200 OK):**

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

---

## API: Technician Payments

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/job-data/technician-payments
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/technician-payments
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": []
}
```

---

## API: Technician Payments

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/job-data/technician-payments
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/technician-payments
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
[]
```

**Success Response (200 OK):**

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

---

## API: Tool Issues

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/job-data/tool-issues
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/tool-issues
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": []
}
```

---

## API: Tool Issues

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/job-data/tool-issues
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/tool-issues
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
[]
```

**Success Response (200 OK):**

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

---

## API: Behaviour Report JSON

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/job-data/behaviour-report
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/behaviour-report
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": "{}"
}
```

---

## API: Behaviour Report JSON

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/job-data/behaviour-report
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/behaviour-report
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
{
  "matrix": []
}
```

**Success Response (200 OK):**

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

---

## API: Challenge Lines

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/job-data/challenge-lines
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/challenge-lines
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": []
}
```

---

## API: Challenge Lines

**Endpoint:**

```
PUT /api/admin/sites/{{siteId}}/job-data/challenge-lines
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/job-data/challenge-lines
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

```json
[]
```

**Success Response (200 OK):**

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

---

## API: Feedback Invite

**Endpoint:**

```
POST /api/admin/sites/{{siteId}}/feedback-invites
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/feedback-invites
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Customer Feedback (Admin)

**Endpoint:**

```
GET /api/admin/sites/{{siteId}}/customer-feedback
```

**Full URL:**

```
http://localhost:8080/api/admin/sites/1/customer-feedback
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: List Machinery For Site

**Endpoint:**

```
GET /api/admin/machinery?siteId={{siteId}}
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery?siteId=1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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
      "serialNumber": null,
      "model": null,
      "status": "ACTIVE",
      "createdAt": "2026-01-01T10:00:00",
      "updatedAt": "2026-01-01T10:00:00"
    }
  ]
}
```

---

## API: Create Machinery (Multipart: Data + Optional Image)

**Endpoint:**

```
POST /api/admin/machinery
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **data** (text): "{\"code\":\"EX-02\",\"name\":\"Crane\",\"siteId\":1,\"defaultUom\":\"HR\",\"status\":\"ACTIVE\"}"
- **image** (file): ``

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Machinery created",
  "data": {
    "id": 2,
    "code": "EX-02"
  }
}
```

---

## API: Update Machinery (Multipart)

**Endpoint:**

```
PUT /api/admin/machinery/1
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_multipart/form-data — fields:_

- **data** (text): "{\"name\":\"Crane XL\"}"
- **image** (file): ``

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Machinery updated",
  "data": {
    "id": 1,
    "name": "Crane XL"
  }
}
```

---

## API: Machinery

**Endpoint:**

```
DELETE /api/admin/machinery/1
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery/1
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Success",
  "data": null
}
```

---

## API: Usage Selection (Daily)

**Endpoint:**

```
GET /api/admin/machinery/usage/selection?siteId={{siteId}}&date=2026-05-20
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery/usage/selection?siteId=1&date=2026-05-20
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Usage Selection

**Endpoint:**

```
PUT /api/admin/machinery/usage/selection
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery/usage/selection
```

**Headers:**

```
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

**Request Body:**

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

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Usage saved",
  "data": null
}
```

---

## API: Monthly Usage Summary

**Endpoint:**

```
GET /api/admin/machinery/usage/summary/month?siteId={{siteId}}&year=2026&month=5
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery/usage/summary/month?siteId=1&year=2026&month=5
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---

## API: Yearly Usage Summary

**Endpoint:**

```
GET /api/admin/machinery/usage/summary/year?siteId={{siteId}}&year=2026
```

**Full URL:**

```
http://localhost:8080/api/admin/machinery/usage/summary/year?siteId=1&year=2026
```

**Headers:**

```
Authorization: Bearer {{accessToken}}
```

**Request Body:**

_No body._

**Success Response (200 OK):**

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

---
