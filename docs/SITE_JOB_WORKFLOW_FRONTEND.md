# Site job workflow — backend APIs and frontend integration

Base path (admin, JWT required): `/api/admin/sites/{siteId}`  

**`{siteId}` in the path** is resolved by the server as any of:

- Numeric primary key (e.g. `1`)
- Exact **job code** (case-insensitive), e.g. `BLR001`
- UI slug **`{name}-{jobCode}`** using the segment after the last hyphen as job code, e.g. `bangalore-blr001` → `blr001` → same site as `BLR001`

So the SPA may call the same URLs it uses in the browser (slug) without a separate resolve step. Machinery `?siteId=` query params accept the same formats.

## One-call autosave (recommended)

**`PUT` or `POST`** `/api/admin/sites/{siteId}/job-data/workflow-batch`  
Content-Type: `application/json`

Body: object; **omit** a property or set it to `null` to skip that section. Non-null sections are applied in this order:

1. `wizard` — JSON object (stored as wizard blob; may sync embedded challenge arrays if present).
2. `advanceExpenseLines` — array (same shape as dedicated endpoint).
3. `technicianPayments` — array.
4. `toolIssues` — array.
5. `equipmentPortal` — object (same shape as `PUT .../job-data/equipment-portal`, including `availabilityYear` / `availabilityMonth` when saving the monthly grid).
6. `behaviourReport` — JSON object (stored as string).
7. `challengeLines` — JSON array **or** wrapper with `rows` / `challengeLines` / etc. (same rules as `PUT .../job-data/challenge-lines`).
8. `attendanceRegisterCells` — `{ "cells": [ ... ] }` (same as register-cells endpoint).

If you send both `wizard` and `challengeLines`, **challengeLines wins** for normalized challenge rows.

## Per-step dedicated endpoints (existing)

| Workflow area | GET | PUT / POST |
|----------------|-----|------------|
| Wizard blob (steps 1–2 JSON, etc.) | `GET .../wizard` | `PUT .../wizard` |
| Step 3 — equipment checklist | `GET .../job-data/equipment-portal?year=2026&month=6` | `PUT .../job-data/equipment-portal` |
| Step 4 — advance + technician tables | `GET .../job-data/advance-expense-lines`, `GET .../job-data/technician-payments` | `PUT` each |
| Step 5 — movement register | `GET .../attendance-register` | `PUT .../job-data/attendance-register-cells` |
| Step 6 — tool issues | `GET .../job-data/tool-issues` | `PUT .../job-data/tool-issues` |
| Step 7–8 — behaviour | `GET .../job-data/behaviour-report` | `PUT .../job-data/behaviour-report` |
| Step 7 / 9 — challenges | `GET .../job-data/challenge-lines` | `PUT` or `POST` `.../job-data/challenge-lines` |
| Customer feedback invite | — | `POST .../feedback-invites` |
| Admin feedback view | `GET .../customer-feedback` | — |

Meta: `GET /api/meta/challenge-line-heads` for challenge head presets.

## Customer feedback public URL

- **Create invite:** `POST /api/admin/sites/{siteId}/feedback-invites` → returns opaque `token`.
- **Site details** `GET /api/admin/sites/{siteId}` now include when a valid invite exists:
  - `customerFeedbackInviteToken`
  - `customerFeedbackInviteExpiresAt`
- **Admin feedback payload:** `GET /api/admin/sites/{siteId}/customer-feedback` includes the same token fields.

**Frontend must build the customer link as:**

`{origin}/customer-feedback/{siteId}?token={customerFeedbackInviteToken}`

Submit feedback with `POST /api/public/sites/{siteId}/customer-feedback` and JSON body that includes `token` (see `CustomerFeedbackSubmitRequest`).

Do **not** use only `/customer-feedback/{siteId}` without `token`.

## Frontend checklist

1. **`siteId` in paths** can be numeric id, job code, or slug (see above); optional resolve step is not required if you reuse the route segment.
2. **On load per step:** call the matching `GET` endpoints and hydrate tables (not only the wizard blob).
3. **On autosave / Save:** call either **`workflow-batch`** with only the sections that changed, or the individual `PUT` endpoints. Wait for `200` and `success: true` before clearing "Saving…".
4. **Equipment month grid:** include `availabilityYear` and `availabilityMonth` on equipment portal saves when persisting day checkboxes.
5. **Challenges:** send `challengeLines` (array of rows with `headLabel` or catalog index / row order) or rely on wizard sync if the wizard JSON embeds a recognized `challengeLines` / `step7` array.
6. **Feedback step:** call `POST .../feedback-invites` if no token yet; copy link using `customerFeedbackInviteToken` from site or customer-feedback GET.
