# Frontend API handoff — Job / wizard / attendance register / public feedback

Base URL example: `http://localhost:8080` (or your deployed host).

All JSON responses (except PDF download) use the same wrapper:

```json
{
  "success": true,
  "message": "Success",
  "data": { }
}
```

Errors: `success: false`, `message` describes the problem; HTTP status reflects type (400, 401, 403, 404, 409, 410, etc.).

---

## Authentication

| Audience | Header | Role |
|----------|--------|------|
| Admin app (job list, wizard, invites) | `Authorization: Bearer <access_token>` | Admin login → `ROLE_ADMIN` |
| Meta / designations | Same | Any authenticated user (Admin or Employee JWT) |
| Public customer pages | **No** `Authorization` | Token in URL path only |

- **Admin** endpoints: `/api/admin/**` → require admin JWT.
- **Public** endpoints: `/api/public/**` → no JWT (CSRF disabled for this path in local profile; disabled entirely on `render` profile).

---

## 1) Job list (Page 0) — site = job row

**Concept:** Each **site** row is one **job**: `jobCode`, location, incharge, dates, customer, etc.

### List / search sites (admin)

| Method | Path | Notes |
|--------|------|--------|
| `GET` | `/api/admin/sites` | Flat array, name order |
| `GET` | `/api/admin/sites/paged?search=&isActive=&page=0&size=20` | Spring `Page` in `data` |
| `GET` | `/api/admin/site-options` | Active sites only (dropdowns) |
| `GET` | `/api/admin/sites/{id}` | Single job/site |

### Create site (admin)

`POST /api/admin/sites`  
`Content-Type: application/json`

**Request body (`CreateSiteRequest`):**

```json
{
  "name": "PAKAL DAL JAMMU & KASHMIR",
  "jobCode": "J0261",
  "address": "Full address text",
  "customerName": "VOITH HYDRO PVT LTD",
  "estimatedDays": 20,
  "inchargeUserId": 2,
  "locationSiteId": 5,
  "siteStartDate": "2024-09-26",
  "siteEndDate": "2024-10-25",
  "totalProjectDays": 30
}
```

Only `name` and `jobCode` are required; the rest is optional.

### Update site / job fields (admin)

`PUT /api/admin/sites/{id}`  
Same fields as create, all optional, plus flags to clear FKs:

**Request body (`UpdateSiteRequest`):**

```json
{
  "name": "Updated site name",
  "jobCode": "J0261",
  "address": "…",
  "isActive": true,
  "customerName": "VOITH HYDRO PVT LTD",
  "estimatedDays": 20,
  "inchargeUserId": 2,
  "clearIncharge": false,
  "locationSiteId": 5,
  "clearLocationSite": false,
  "siteStartDate": "2024-09-26",
  "siteEndDate": "2024-10-25",
  "totalProjectDays": 30
}
```

- Set `clearIncharge: true` to remove incharge (ignore `inchargeUserId` for that request).
- Set `clearLocationSite: true` to remove linked location site.
- `locationSiteId` must not equal the same site’s `{id}` (self-reference not allowed).

### `SiteResponse` (`data` for one site)

```json
{
  "id": 1,
  "name": "PAKAL DAL JAMMU & KASHMIR",
  "jobCode": "J0261",
  "address": "…",
  "isActive": true,
  "customerName": "VOITH HYDRO PVT LTD",
  "estimatedDays": 20,
  "inchargeUserId": 2,
  "inchargeName": "Test Employee",
  "inchargeEmployeeId": "EMP001",
  "locationSiteId": 5,
  "locationSiteLabel": "Other Site — Address snippet",
  "siteStartDate": "2024-09-26",
  "siteEndDate": "2024-10-25",
  "totalProjectDays": 30,
  "certificateClientStatus": "NONE",
  "customerFeedbackApprovedAt": null,
  "createdAt": "2026-05-18T10:00:00",
  "updatedAt": "2026-05-18T10:00:00"
}
```

**`certificateClientStatus` enum values:**

| Value | Meaning |
|-------|---------|
| `NONE` | No customer feedback submitted yet |
| `FEEDBACK_SUBMITTED` | Customer submitted feedback via public link |
| `APPROVED_BY_CLIENT` | Customer approved; certificate PDF generated |

---

## 2) Employee & site dropdowns (admin)

| Method | Path | `data` |
|--------|------|--------|
| `GET` | `/api/admin/users/employees` | `UserResponse[]` — use for **Name** / **Incharge** / **Handled by** dropdowns |

Employees also have non-admin site list (if you reuse elsewhere):

| Method | Path |
|--------|------|
| `GET` | `/api/sites/active` | Active sites (authenticated user) |

---

## 3) Designations — Page 4 “designation” dropdown

**Not** the same as Spring Security `ROLE_ADMIN` / `ROLE_EMPLOYEE`. This is a **configurable list** (DB-seeded; you can add rows without redeploying the enum).

| Method | Path | Auth |
|--------|------|------|
| `GET` | `/api/meta/designations` | Any authenticated JWT |

**Response `data`:** array of:

```json
{
  "id": 1,
  "code": "ADMIN",
  "label": "Administrator",
  "sortOrder": 0
}
```

Seeded codes (initial): `ADMIN`, `EMPLOYEE`, `SITE_SUPERVISOR`, `SITE_ENGINEER`, `MACHINIST`.

---

## 4) Wizard steps 1–9 — single JSON blob per job

Store **entire** multi-step form state as one JSON object (structure is **frontend-defined**; backend only persists a string).

| Method | Path | Auth |
|--------|------|------|
| `GET` | `/api/admin/sites/{id}/wizard` | Admin |
| `PUT` | `/api/admin/sites/{id}/wizard` | Admin |

- **GET** `data`: JSON **string** (parse on client). If empty server-side, returns `"{}"`.
- **PUT** body: any JSON **object** (or array); server stores `payload.toString()`.
- **PUT** response `data`: full `SiteResponse` (see above).

**Suggested keys (example only — not enforced by API):**

- `page1` … `page9` — intro, engineering procedure, **daily checklist (category blocks A/B/C + I/J/K)**, movement register, advance, tools, behaviour, attendance overrides, challenges.
- Page 4 rows: e.g. `{ "movementRows": [ { "employeeUserId", "designationCode", "presentFrom", "presentTo", "reasons" } ] }`
- Page 9: e.g. `{ "challenges": [ { "headCode", "incidentDate", "involvedEmployeeId", "notes", "status" } ] }` with `status` in `Resolved` / `Pending` / `Action taken` as **string** in JSON.

---

## 5) Attendance register (Page 8) — paper-style N-day grid

Uses **existing** `Attendance` rows for the site; maps approval status to short codes. Column count is **configurable** (default matches a 15-day paper block).

| Method | Path | Auth |
|--------|------|------|
| `GET` | `/api/admin/sites/{id}/attendance-register` | Admin |

**Query parameters:**

| Param | Required | Description |
|-------|------------|-------------|
| `periodStart` | No | First column date (`YYYY-MM-DD`). Default: site’s `siteStartDate`, or today if null. |
| `blockIndex` | No (default `0`) | `0` → first `daysPerBlock`-day window from effective start; `1` → next window; etc. |
| `daysPerBlock` | No (default `15`, max **366**) | Number of consecutive calendar columns in `dayDates` / each row’s `dayCodes`. |
| `employeeIds` | No | Repeat param: `employeeIds=1&employeeIds=2`. If omitted, employees are inferred from attendance rows in the window. |

**Response `data` (`AttendanceRegisterResponse`):**

```json
{
  "siteId": 1,
  "jobCode": "J0261",
  "customerName": "VOITH HYDRO PVT LTD",
  "siteStartDate": "2024-09-26",
  "siteEndDate": "2024-10-25",
  "totalProjectDays": 30,
  "estimatedDays": 20,
  "periodStart": "2024-09-26",
  "periodEnd": "2024-10-10",
  "blockIndex": 0,
  "dayDates": [
    "2024-09-26",
    "2024-09-27",
    "… N dates …"
  ],
  "rows": [
    {
      "slNo": 1,
      "employeeId": 3,
      "employeeName": "SIVA KUMAR",
      "dayCodes": ["P", "P", "A", "", "P"]
    }
  ]
}
```

**`dayCodes` mapping (current backend):**

| `AttendanceStatus` | Code in grid |
|--------------------|--------------|
| `APPROVED` | `P` |
| `REJECTED` | `A` |
| `PENDING` or no record | `""` (empty string) |

Legend codes **S / HQ / LS / IN** are stored via **`PUT …/job-data/attendance-register-cells`** and merged into this grid (they override attendance-derived P/A for that cell).

## 6) Public customer feedback + certificate (Pages 10–11)

### 6.1 Admin: create invite link

| Method | Path | Auth |
|--------|------|------|
| `POST` | `/api/admin/sites/{id}/feedback-invites` | Admin |

**Response `data`:**

```json
{
  "token": "64-char-hex-string",
  "expiresAt": "2026-06-17T12:00:00",
  "relativePath": "/api/public/feedback/<token>"
}
```

Frontend builds the customer URL, e.g.  
`https://your-portal.com/c/feedback?token=<token>` **if** your SPA proxies to the same API host, or directly:

`https://api.yourcompany.com/api/public/feedback/<token>`  
(document the chosen pattern for the customer.)

Token validity (default 30 days): `app.public-feedback-token-valid-days` in `application.yml` / env `PUBLIC_FEEDBACK_TOKEN_VALID_DAYS`.

### 6.2 Public: load context (no auth)

`GET /api/public/feedback/{token}`

**Response `data` (`PublicFeedbackContextResponse`):**

```json
{
  "jobCode": "J0261",
  "customerName": "VOITH HYDRO PVT LTD",
  "companyNameHint": "PAKAL DAL JAMMU & KASHMIR",
  "certificateClientStatus": "NONE",
  "expired": false,
  "revoked": false
}
```

- `companyNameHint` is currently the site **`name`** (job/location title), not a separate DB field — use for prefill label if needed.
- If token unknown → **404**. Expired link: `expired: true` on GET, but **submit/approve** return **410 Gone** when using the token.

### 6.3 Public: submit feedback (no auth)

`POST /api/public/feedback/{token}`  
`Content-Type: application/json`

**Body (`CustomerFeedbackSubmitRequest`) — all optional except business rule: approve requires prior submit (see below):**

```json
{
  "name": "Client contact",
  "email": "client@example.com",
  "phone": "+91…",
  "companyName": "VOITH HYDRO PVT LTD",
  "productQuality": "Good",
  "customerService": "Good",
  "machiningQuality": "Excellent",
  "pricing": "Fair",
  "shippingDelivery": "On time",
  "otherCategoryNote": "…",
  "specificFeedback": "Machining of stay ring surfaces …",
  "suggestions": "…",
  "likelihoodRecommend": 9,
  "additionalComments": "…",
  "extra": { "any": "additional structured fields" }
}
```

**Effect:** Merges into stored JSON on the site, sets `certificateClientStatus` → `FEEDBACK_SUBMITTED`.  
If already `APPROVED_BY_CLIENT` → **409 Conflict**.

### 6.4 Public: approve & download PDF (no auth)

`POST /api/public/feedback/{token}/approve`

- **Requires** current status **`FEEDBACK_SUBMITTED`** (submit feedback first). Otherwise **400** with message to submit first.
- **Response:** raw **PDF** bytes, not `ApiResponse` wrapper.
- Headers: `Content-Type: application/pdf`, `Content-Disposition: attachment; filename="work-completion-certificate.pdf"`.
- **Effect:** Sets `certificateClientStatus` → `APPROVED_BY_CLIENT`, sets `customerFeedbackApprovedAt`. Idempotent: if already approved, returns PDF again.

### 6.5 Admin: read feedback + status for dashboard

| Method | Path | Auth |
|--------|------|------|
| `GET` | `/api/admin/sites/{id}/customer-feedback` | Admin |

**Response `data`:**

```json
{
  "certificateClientStatus": "APPROVED_BY_CLIENT",
  "customerFeedbackApprovedAt": "2026-05-18T14:32:00",
  "feedbackJson": "{\"name\":\"…\",\"submittedAt\":\"…\", ... }"
}
```

`feedbackJson` is the stored string from the public submit step (parse as JSON on client).

---

## 7) Swagger

If enabled: `/swagger-ui.html` — lists all controllers including **Site job extensions**, **Public feedback**, **Meta**.

---

## 8) Quick screen → API matrix

| UI area | APIs |
|---------|------|
| Job list / edit row | `GET/PUT /api/admin/sites…`, `GET /api/admin/site-options`, `GET /api/admin/users/employees` |
| Wizard 1–4, 3, etc. | `GET/PUT /api/admin/sites/{id}/wizard` (pages not migrated to job-data tables) |
| Screens 5–7, 9 | `GET/PUT /api/admin/sites/{id}/job-data/…` (see §10) |
| Designation + challenge heads | `GET /api/meta/designations`, `GET /api/meta/challenge-line-heads` |
| Attendance register | `GET …/attendance-register`, `PUT …/job-data/attendance-register-cells` |
| Generate customer link | `POST /api/admin/sites/{id}/feedback-invites` |
| Customer public pages | `GET/POST /api/public/feedback/{token}`, `POST …/approve` |
| Internal certificate + feedback | `GET /api/admin/sites/{id}`, `GET /api/admin/sites/{id}/customer-feedback` |

---

## 9) UI navigation & attendance legend (frontend)

- **Wizard flow:** Job list **job code link** opens **step 1 (Project introduction)** for that `siteId`. **`Next`** / **`Prev`** move through steps **1 → 9** (see `FRONTEND_ROUGH_UI_SCREENS.md`).
- **Page 4 header** (customer, job code, site start/close, total days) maps to **`Site`** fields — use `GET/PUT /api/admin/sites/{id}` so they stay consistent with attendance (page 8) header.
- **Attendance register API** (`…/attendance-register`) merges **`Attendance`** (APPROVED→`P`, REJECTED→`A`, pending→empty) with **`PUT …/job-data/attendance-register-cells`** persisted codes (**P, A, S, HQ, LS, IN**). Cell override wins when present.

---

## 10) Normalized job-site APIs (screens 5–7, 9, register cells)

All require **admin** JWT. Replace `PUT` bodies replace the full list for that resource (empty array clears). **Row counts are not capped** — advance lines, tool issues, technician payments, and challenge lines can be as long as the UI needs; each row is persisted in its table.

| Screen | GET | PUT |
|--------|-----|-----|
| 5a Advance / expenses | `/api/admin/sites/{id}/job-data/advance-expense-lines` | same |
| 5b Technician payments | `/api/admin/sites/{id}/job-data/technician-payments` | same — multiple lines per technician per day allowed (`lineOrder`) |
| 6 Tool issues | `/api/admin/sites/{id}/job-data/tool-issues` | same |
| 7 Behaviour matrix | `/api/admin/sites/{id}/job-data/behaviour-report` | JSON body |
| 8 Register overrides | (merged into `…/attendance-register` GET) | `/api/admin/sites/{id}/job-data/attendance-register-cells` body `{ "cells": [ … ] }` |
| 9 Challenges | `/api/admin/sites/{id}/job-data/challenge-lines` | same — each row: `headLabel` (or `challengeCatalogIndex` + blank `headLabel` to copy a preset label), optional `lineOrder`, `incidentDate`, `involvedUserId`, `challengesFaced`, `status` |
| 9 Preset head labels | `/api/meta/challenge-line-heads` | — (suggestions only; custom `headLabel` strings are stored as-is) |

DTO shapes match `com.attendance.system.dto.jobsite.*`.

---

*Generated for handoff to frontend; backend module: `ProjectC`.*
