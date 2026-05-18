# Rough UI screens — aligned with paper forms & `ProjectC` backend

Wireframes + field rules for the frontend. **API details:** see `FRONTEND_API_HANDOFF.md`.

**Reference scans (for visual parity):** assets under  
`ProjectCm/assets/` (workspace image exports), e.g. project introduction, engineering procedure, checklists, movement register, advance, tools report, behaviour, attendance, challenges, feedback, certificate.

---

## Navigation (required flow)

| Step | Screen | Opens when… |
|------|--------|-------------|
| **0** | Job list | After admin login |
| **1** | Project introduction | User clicks **Job code** hyperlink on row `siteId` |
| **2** | Engineering procedure | **`Next`** from step 1 |
| **3** | Daily checklist(s) | **`Next`** from step 2 |
| **4** | Site team movement register | **`Next`** from step 3 |
| **5** | Site advance & technician funds | **`Next`** from step 4 |
| **6** | Tools missing / damage / repair | **`Next`** from step 5 |
| **7** | Site behaviour report | **`Next`** from step 6 |
| **8** | Site team attendance register | **`Next`** from step 7 |
| **9** | Challenges at site | **`Next`** from step 8 |

- **`Prev`:** goes one step back (8 → 7 → … → 1). From step 1, `Prev` can return to job list (step 0).
- **`Save`:** persist `wizardData` via `PUT /api/admin/sites/{siteId}/wizard` (merge full JSON client-side). Optionally also `PUT /api/admin/sites/{siteId}` when header fields (customer, dates, etc.) are edited on step 4 or 8.
- **Step indicator:** show `[1●][2][3]…[9]` (or labels) so the user always knows position.

**Shared top bar (steps 1–9):**  
`← Jobs` · **Job code:** `{read-only from Site}` · **Est. days:** `{editable → Site.estimatedDays}` · **step chips** · **`[Save]`** **`[Prev]`** **`[Next]`**

---

## Screen 0 — Job list

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ Jobs                                                    [Admin] [Logout]    │
├──────────────┬────────────┬─────────────────────────┬───────────────────────┤
│ Job code     │ Est. days  │ Site location           │ Incharge              │
│ (hyperlink)  │ [number]   │ [▼ active sites]        │ [▼ employees]        │
├──────────────┼────────────┼─────────────────────────┼───────────────────────┤
│ J0261        │ 20         │ …                       │ …                     │
└──────────────┴────────────┴─────────────────────────┴───────────────────────┘
```

- **Job code** is a **link** → `navigate(/wizard/{siteId}?step=1)` (or your route). Loads `GET /api/admin/sites/{id}` for header defaults.
- **APIs:** `GET/PUT /api/admin/sites`, `GET /api/admin/site-options`, `GET /api/admin/users/employees`.

---

## Screen 1 — PROJECT INTRODUCTION

Matches first paper: metadata row, proposed equipment list, job description, dimensional table, bottom split (mobilization + site team).

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ PROJECT INTRODUCTION                                    Job code: J0261     │
├──────────────────┬──────────────────┬──────────────┬─────────────────────┤
│ Name of Client   │ Site location    │ Job code     │ Scheduled days       │
│ [______________] │ [______________] │ J0261 (ro)   │ [____] ← sync Site   │
└──────────────────┴──────────────────┴──────────────┴─────────────────────┘
│ Proposed equipment  [+ add line]                                            │
│  1 [________________]  2 [________________]  …                              │
│ Description of the job  [ multiline ]                                       │
│ Dimensional details & machining scope                                       │
│ ┌────┬──────────────┬──────────────┬────────────────────────────────────┐  │
│ │ Sl │ Activity     │ Dimensions   │ Description                        │  │
│ ├────┼──────────────┼──────────────┼────────────────────────────────────┤  │
│ │ 1  │ [          ] │ [          ] │ [                                ] │  │
│ └────┴──────────────┴──────────────┴────────────────────────────────────┘  │
│ ┌─ Mobilization schedule ─────────────┐ ┌─ Site team members ────────────┐ │
│ │Sl│ Activity              │ Date     │ │Sl│ Name [▼ employee] │ (notes) │ │
│ │1 │ Eqmt Despatched on    │ [date]   │ │1 │ [▼]               │         │ │
│ │… │ (fixed labels 2–8)    │ [date]   │ │… │ …                 │         │ │
│ └─────────────────────────────────────┘ └──────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Bind** “Name of Client” / “Scheduled days” / location text to **`Site`** (`customerName`, `estimatedDays`, `name`/`address` or `locationSiteId`) and **save** with `PUT /api/admin/sites/{id}` when user edits.
- **Equipment / dimensional / mobilization / extra team columns:** store in **`wizardData.page1`** (structure is frontend-defined JSON).

---

## Screen 2 — ENGINEERING PROCEDURE

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ ENGINEERING PROCEDURE     ┌──────────────┐  ┌──────────────┐                │
│                           │Target sched. │  │ JOB CODE     │                │
│                           │  20 DAYS     │  │  J0261       │                │
│                           └──────────────┘  └──────────────┘                │
├────┬─────────────────────────────────┬────┬────────────┬──────────┬─────────┤
│ Sl │ Activity                        │ Day│Target time │Actual    │Reason…  │
├────┼─────────────────────────────────┼────┼────────────┼──────────┼─────────┤
│ 1  │ Pilot machining on…             │[ ] │ [        ] │ [      ] │ [     ] │
└────┴─────────────────────────────────┴────┴────────────┴──────────┴─────────┘
```

- **Target days / job code:** from `Site.estimatedDays`, `Site.jobCode` (display; optional sync if user edits target on this screen → `PUT` site).
- **Table body:** `wizardData.page2`.

---

## Screen 3 — Daily checklist (**category-based**, matches paper UI)

The paper form is **not** one flat table. It is **grouped by category** (lettered sections). Each category has its **own Sl.No. sequence starting at 1** and the same column pattern: **Item | UOM | Qty | (optional Date) | merged “Make tick…” | 01–15**.

Use **accordion**, **tabs by letter**, or **stacked blocks** with a **bold category band** between blocks (mobile: single scroll).

### Block 1 — first printed sheet (equipment on site)

| Category code | Title (as on form) |
|---------------|-------------------|
| **A** | MEASURING INSTRUMENTS |
| **B** | DRILLING MACHINE TOOLS |
| **C** | HAND TOOLS |

Example layout per category:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ Daily Check List … Tools, Measuring Instruments & Safety    Job: J0261   │
├─────────────────────────────────────────────────────────────────────────────┤
│ ▼ A. MEASURING INSTRUMENTS                                                  │
│ ┌────┬──────────────────────────────┬─────┬────┬────┬── 01 … 15 (ticks) ──┐  │
│ │ Sl │ Item description             │ UOM │Qty │Date│ ☐ ☐ …              │  │
│ ├────┼──────────────────────────────┼─────┼────┼────┼────────────────────┤  │
│ │ 1  │ Dial Gauge with Mag… 0-10mm  │ Nos │ 2  │    │ ☐ …                │  │
│ │ …  │ (fixed template rows)        │     │    │    │                    │  │
│ └────┴──────────────────────────────┴─────┴────┴────┴────────────────────┘  │
│ ▼ B. DRILLING MACHINE TOOLS                                                │
│ ┌────┬──────────────────────────────┬─────┬────┬────┬── 01 … 15 ──────────┐  │
│ │ Sl │ …                            │     │    │    │                    │  │
│ └────┴──────────────────────────────┴─────┴────┴────┴────────────────────┘  │
│ ▼ C. HAND TOOLS                                                            │
│ ┌────┬──────────────────────────────┬─────┬────┬────┬── 01 … 15 ──────────┐  │
│ │ Sl │ …                            │     │    │    │                    │  │
│ └────┴──────────────────────────────┴─────┴────┴────┴────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Block 2 — second printed sheet (lifting / safety / spares)

| Category code | Title (as on form) |
|---------------|-------------------|
| **I** | LIFTING TOOLS |
| **J** | SAFETY ITEMS |
| **K** | SPARES |

Same table columns under each **I / J / K** header (Sl restarts at 1 per section on the paper — keep that behaviour in UI for parity).

**Persist (suggested JSON shape):** `wizardData.page3` as an array of categories, e.g.

```json
{
  "categories": [
    { "code": "A", "title": "MEASURING INSTRUMENTS", "rows": [ { "sl": 1, "item": "…", "uom": "Nos", "qty": 2, "date": null, "days": [false, true, …] } ] },
    { "code": "B", "title": "DRILLING MACHINE TOOLS", "rows": [ ] },
    { "code": "C", "title": "HAND TOOLS", "rows": [ ] },
    { "code": "I", "title": "LIFTING TOOLS", "rows": [ ] },
    { "code": "J", "title": "SAFETY ITEMS", "rows": [ ] },
    { "code": "K", "title": "SPARES", "rows": [ ] }
  ]
}
```

Row text for **A/B/C** and **I/J/K** can be **seeded from a static JSON file** in the frontend (copied from the paper list) so the first load matches the template; user edits and ticks persist in `wizardData.page3`.

---

## Screen 4 — SITE TEAM MEMBERS MOVEMENT REGISTER

**Header (all mandatory on this screen — bind to `Site` + show read-only where noted):**

| Field | Source |
|--------|--------|
| **Name of the customer** | `Site.customerName` — editable → `PUT /api/admin/sites/{id}` |
| **Job code** | `Site.jobCode` — **read-only** |
| **Site start date** | `Site.siteStartDate` — date picker → `PUT` |
| **Site close date** | `Site.siteEndDate` — date picker → `PUT` |
| **Total project days** | `Site.totalProjectDays` — number → `PUT` (can default from `estimatedDays` if you want) |

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ SITE TEAM MEMBERS MOVEMENT REGISTER          ┌──────────────┐              │
│ Name of customer [________________]          │ Job code     │              │
│ Site start [📅]  Site close [📅]  Tot.days │  J0261       │              │
│ [____]                                       └──────────────┘              │
├────┬──────────────────┬────────────────┬────────────┬────────────┬─────────┤
│ Sl │ Name of member   │ Designation    │ Present at site          │ Reasons│
│    │                  │                │ From [📅]  │ To [📅]    │         │
├────┼──────────────────┼────────────────┼────────────┼────────────┼─────────┤
│ 01 │ [▼ Employee API] │ [▼ Designations│ [date]     │ [date]     │[notes]  │
│ …  │ …employees…      │  API]          │            │            │         │
│ 10 │                  │                │            │            │         │
└────┴──────────────────┴────────────────┴────────────┴────────────┴─────────┘
```

- **Name ▼** → `GET /api/admin/users/employees` (use `id` + display `name` / `employeeId`).
- **Designation ▼** → `GET /api/meta/designations` (use `code` or `id`; **not** JWT role). Lets you add more job titles later without tying to `ROLE_ADMIN` / `ROLE_EMPLOYEE`.
- **Row-level dates / reasons:** `wizardData.page4.rows[]` (store `employeeUserId`, `designationCode`, `from`, `to`, `reasons`).

---

## Screen 5 — Site advance & technician-wise funds

**Paper layout:** two tables — (1) **advance & expense dispersion**, (2) **technician-wise payments** with **15 day** columns + total.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ DETAILS OF SITE ADVANCE RECEIVED & PAID TO MANPOWER AT SITE    JOB: J0261  │
├─────────────────────────────────────────────────────────────────────────────┤
│ ▼ Table 1 — Details of dispersion of the expenses                          │
│ ┌──┬──────────────┬─────────┬──────┬──────┬──────┬── … expense cols … ──┬───┐│
│ │Sl│Date advance  │Opening  │Amount│Food  │Conv. │Med.│Add.MP│Weld│Site│Bal││
│ │  │received      │Bal.     │      │Allow │      │    │      │    │Expn│   ││
│ ├──┼──────────────┼─────────┼──────┼──────┼──────┼────┼──────┼────┼────┼───┤│
│ │01│ [date]       │ [0.00]  │[    ]│[   ] │[   ] │[  ]│[    ]│[  ]│[  ]│[ ]││
│ │… │ … up to 6 rows …       │      │      │      │    │      │    │    │   ││
│ └──┴──────────────┴─────────┴──────┴──────┴──────┴────┴──────┴────┴────┴───┘│
│     Details of dispersion (wide note) [ multiline spanning row ]           │
├─────────────────────────────────────────────────────────────────────────────┤
│ ▼ Table 2 — Technician-wise dispersion of funds                            │
│ ┌──┬──────────────────┬── Date of payment to technician (01–15) ──┬──────┐ │
│ │Sl│ Technician [▼emp]│ 01 │ 02 │ 03 │ … │ 15 │ Total payment / tech │ │
│ ├──┼──────────────────┼────┼────┼────┼───┼────┼────────────────────────┤ │
│ │01│ [▼]              │[  ]│[  ]│[  ]│ … │[  ]│ [auto-sum or manual]   │ │
│ │… │ … rows …         │    │    │    │   │    │                        │ │
│ └──┴──────────────────┴────┴────┴────┴───┴────┴────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Technician name ▼** → `GET /api/admin/users/employees`.
- **Persist:** `wizardData.page5` (entire structure is frontend-defined; **no** separate advance/payment tables in backend).

**Backend:** **Not implemented** as domain logic — only **blob storage** inside `PUT /api/admin/sites/{id}/wizard` (same for all wizard-only steps).

---

## Screen 6 — Tools missing / damage / repair

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ TOOLS MISSING / DAMAGE / REPAIR REPORT                   JOB CODE: J0261    │
├────┬──────┬──────────────┬──────── Missing ─┬────────┬────────┬───────────┤
│ Sl │ Pkg  │ Item desc.   │ Damage │ Repair   │ Handled│ Issue desc.        │
│    │ list │              │ date   │ date     │ [▼emp] │                    │
└────┴──────┴──────────────┴────────┴──────────┴────────┴────────────────────┘
│ Additional information if any  [ multiline ]                                │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Handled by ▼** → employees API.
- **Persist:** `wizardData.page6`.

**Backend:** **Not implemented** as domain APIs — **wizard JSON only** (`PUT/GET …/wizard`).

---

## Screen 7 — Site behaviour report

**Paper layout:** vertical title band + **Job code** box; main grid: **Sl** blocks; each block has sub-rows **Name of member** / **Issues + Date** / **issue type** (e.g. Late to Site); **one column per team member** (header = name); **checkbox** at intersection; **Remarks / Detail** wide column.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ SITE BEHAVIOUR REPORT (vertical label)              ┌──────────┐           │
│                                                     │ Job code │           │
│ ┌───┬────────────────────┬──────────┬──────────┬──────────┬────────┬───────┐ │
│ │Sl │ Description        │ SIVA     │ NAGENDRA │ CHANDRU  │ [+col]│Remark │ │
│ │   │ sub-rows           │ KUMAR    │          │          │       │       │ │
│ ├───┼────────────────────┼────┬─────┼────┬─────┼────┬─────┼───┬───┼───────┤ │
│ │01 │ Name of member     │ ☐ │     │ ☐ │     │ ☐ │     │ ☐ │   │       │ │
│ │   │ Issues / Date      │ ☐ │     │ ☐ │     │ ☐ │     │ ☐ │   │       │ │
│ │   │ Late to Site       │ ☐ │     │ ☐ │     │ ☐ │     │ ☐ │   │[notes]│ │
│ ├───┼────────────────────┼────┴─────┼────┴─────┼────┴─────┼───┴───┼───────┤ │
│ │02 │ … next issue block │          │          │          │       │       │ │
│ └───┴────────────────────┴──────────┴──────────┴──────────┴───────┴───────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Member columns:** from selected employees (same list as movement register) or `wizardData.page7.memberIds[]`.
- **Persist:** `wizardData.page7`.

**Backend:** **Not implemented** as tables — **wizard JSON only** (`GET/PUT …/wizard`).

---

## Screen 8 — Site team members attendance (match paper UI)

**Header (same fields as movement register):** Customer name, Job code, Site start, Site close, Total project days — from **`GET /api/admin/sites/{id}`**.

**Legend (show exactly as on form):**

| Code | Meaning |
|------|---------|
| P | Present |
| A | Absent |
| S | Sick |
| HQ | Back to HQ (paper sometimes shows “BH” — pick one label consistently) |
| LS | Left site |
| IN | Injured |

**Grid:**

- Row **DAY:** `01` … `15`
- Row **DATE:** computed from `siteStartDate` + column index + `blockIndex` (second block = days 16–30).
- Columns: **Sl**, **Name** (from employees list or from register API rows), then **15** status cells.

**Data binding:**

- Call **`GET /api/admin/sites/{id}/attendance-register?periodStart=&blockIndex=&daysPerBlock=&employeeIds=`** for each date window (`daysPerBlock` defaults to **15**, max **366**).
- **Today’s backend mapping:** `APPROVED` → `P`, `REJECTED` → `A`, `PENDING` / missing → empty. That matches **part** of the legend only.
- **To match the full paper legend (S, HQ, LS, IN):** either extend the attendance model/API later **or** store/register overrides in **`wizardData.page8`** and **merge** in the UI (server grid + client overrides per cell). Document this merge in your FE spec.

**Editing:** marking attendance still uses your **existing** employee mark + admin approve flows; this screen is primarily **display** unless you add write APIs for register codes.

**Backend:** **Implemented** for the **grid data** path described above (`attendance-register` + `Site`); legend codes beyond P/A need wizard merge or future schema change.

---

## Screen 9 — CHALLENGES AT SITE

**Preset head labels** (optional quick-picks — the paper form listed 22 examples; the app may add **any number** of rows):

1. Transport … 22. Local Manpower Issue *(same list as `GET /api/meta/challenge-line-heads` for dropdown suggestions)*

**Columns:** Sl | Heads (free text or preset) | Date of incident | **Involved person [▼ employees]** | Challenges faced (text) | **Status [▼]** — **Resolved** | **Pending** | **Action taken**

- **Persist (recommended):** `GET/PUT /api/admin/sites/{siteId}/job-data/challenge-lines` — each row stores **`headLabel`** in the database (plus `incidentDate`, `involvedUserId`, `challengesFaced`, `status`, optional `lineOrder`). You may also send **`challengeCatalogIndex`** (1-based index into the meta catalog) with **`headLabel` omitted** to store the catalog string automatically.
- **Legacy / optional:** `wizardData.page9` JSON if you still mirror state in the wizard blob.

**Backend:** **Implemented** — `site_challenge_lines` table; unbounded row count.

---

## Screen 10 — Customer feedback (public, no login)

**Note:** In this doc, **logged-in** wizard steps are **1–9**. **Customer** flows are **10 (feedback)** and **11 (certificate)** — separate route, token in URL.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ CUSTOMER FEED BACK                              ┌──────────┐ Feedback det.│
│                                                 │ JOB CODE │              │
│ Name [__________]  Email [__________]          │  J0261   │              │
│ Phone [__________]  Company [________________] └──────────┘              │
├─────────────────────────────────────────────────────────────────────────────┤
│ Product Quality │ Customer Svc │ Machining │ Pricing │ Ship │ Other spec. │
│ [ short text / stars per col ]                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│ Specific feedback   [ large textarea — 0–10 scale hint in label ]           │
│ Suggestions         [ large textarea ]                                      │
│ Likelihood recommend[ number 0–10 or radio row ]                             │
│ Additional comments [ large textarea ]                                      │
│                         [ Submit feedback ]  → POST public API             │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Load:** `GET /api/public/feedback/{token}` → job code, hints, `certificateClientStatus`.
- **Submit:** `POST /api/public/feedback/{token}` with JSON body (see `FRONTEND_API_HANDOFF.md`).

**Backend:** **Implemented** — `CustomerFeedbackService`, `PublicFeedbackController`, stores JSON on `Site`, sets `FEEDBACK_SUBMITTED`.

### Screen 11 — Certificate + approve (same public session)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ WORK COMPLETION CERTIFICATE                      Job: J0261 (read-only)      │
│ (Preview: project line, duration, boilerplate text — from job + feedback)  │
│                                                                              │
│              [ Approve & download PDF ]  → POST …/approve → PDF blob        │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Backend:** **Implemented** — `POST /api/public/feedback/{token}/approve` returns PDF, sets `APPROVED_BY_CLIENT`, `CertificatePdfService`.

---

## Backend implementation: screens **5–10** (what `ProjectC` actually does)

| Screen | Dedicated backend logic? | APIs / tables |
|--------|--------------------------|---------------|
| **5** | **Yes** | `GET/PUT /api/admin/sites/{id}/job-data/advance-expense-lines`, `GET/PUT …/technician-payments` → `site_advance_expense_lines`, `site_technician_daily_payments` |
| **6** | **Yes** | `GET/PUT …/job-data/tool-issues` → `site_tool_issues` |
| **7** | **Yes** | `GET/PUT …/job-data/behaviour-report` → `site_behaviour_reports.payload_json` |
| **8** | **Yes** | `GET …/attendance-register` merges **`Attendance`** + **`site_attendance_register_cells`**; `PUT …/job-data/attendance-register-cells` for **P,A,S,HQ,LS,IN** overrides |
| **9** | **Yes** | `GET/PUT …/job-data/challenge-lines` → `site_challenge_lines` (unbounded rows, `headLabel` stored); optional presets `GET /api/meta/challenge-line-heads` |
| **10** | **Yes** | Public feedback APIs + token table (unchanged) |

**Screen 11:** PDF approve flow unchanged.

**Wizard blob** (`PUT …/wizard`) remains for screens **1–4, 3** (and any legacy JSON) until the frontend switches fully to structured APIs.

---

**No, if used additively:**

| Area | Risk | Mitigation |
|------|------|------------|
| Existing attendance APIs | Low | Register **GET** + cell **PUT** are additive; marking flow unchanged. |
| `SiteResponse` new fields | Low | JSON adds fields. |
| DB | Low | New tables via Hibernate `ddl-auto: update` (see `docs/DEPLOYMENT.md`). |
| Public feedback | Medium | Tokens + expiry + **per-IP rate limit** filter (`app.public-feedback-rate-limit-per-minute`). |

---

## Quick API ↔ screen index

| Screen | Main APIs |
|--------|-----------|
| 0 | Sites CRUD, employees, site-options |
| 1–4, 3 | `GET/PUT …/wizard` (and `Site` for shared headers) |
| 5–7, 9 | `GET/PUT …/sites/{id}/job-data/…` |
| 4 header | `PUT …/sites/{id}` |
| 8 | `GET …/attendance-register`, `PUT …/job-data/attendance-register-cells`, `GET …/sites/{id}` |
| 10–11 | `/api/public/feedback/…`, feedback-invites, customer-feedback |

---

*Backend lives in `ProjectC`; these screens are specification only for the frontend team.*
