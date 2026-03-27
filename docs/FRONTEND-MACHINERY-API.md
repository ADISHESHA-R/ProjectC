# Machinery API — Frontend integration (new)

**Base URL:** `{API_BASE}` (e.g. `http://localhost:8080` or your deployed host)  
**Prefix:** `/api/admin/machinery`  
**Auth:** `Authorization: Bearer <accessToken>` — **Admin JWT only** (same as other `/api/admin/**` routes).

All JSON responses use the existing envelope:

```json
{
  "success": true,
  "message": "Success | …",
  "data": { }
}
```

Errors: `success: false`, `message` describes the issue; `data` is often `null`.

---

## Images

- Catalog responses include `imagePath` (e.g. `machinery/uuid.jpg` or `null`).
- Load in the browser with the existing file API (authenticated):

`GET {API_BASE}/api/files?path={encodeURIComponent(imagePath)}`

---

## Endpoints summary

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/admin/machinery?siteId=` | List all machinery for a site (catalog). |
| POST | `/api/admin/machinery` | Create catalog item (multipart: JSON part + optional image). |
| PUT | `/api/admin/machinery/{id}` | Update catalog item (multipart). |
| DELETE | `/api/admin/machinery/{id}` | Delete catalog item (fails if any usage history exists). |
| GET | `/api/admin/machinery/usage/selection?siteId=&date=` | **Daily screen:** full catalog for site + qty/uom per row for that date. |
| PUT | `/api/admin/machinery/usage/selection` | **Save day:** replace all usage for `siteId` + `date` with `lines`. |
| GET | `/api/admin/machinery/usage/summary/month?siteId=&year=&month=` | Monthly overview (days that have usage). |
| GET | `/api/admin/machinery/usage/summary/year?siteId=&year=` | Yearly overview (per calendar month). |

**Query `date`:** ISO date only: `yyyy-MM-dd` (e.g. `2025-03-16`).  
**Month:** `month` is `1`–`12`.

---

## 1. List catalog

`GET /api/admin/machinery?siteId={number}`

**Response `data`:** `MachineryResponse[]`

```ts
type MachineryStatus = "ACTIVE" | "MAINTENANCE" | "RETIRED";

interface MachineryResponse {
  id: number;
  code: string;
  name: string;
  itemDescription: string | null;
  jobCode: string | null;
  defaultUom: string;       // e.g. "HOUR"
  siteId: number;
  siteName: string;
  imagePath: string | null;
  serialNumber: string | null;
  model: string | null;
  status: MachineryStatus;
  createdAt: string;          // ISO-8601
  updatedAt: string;
}
```

---

## 2. Create machinery (multipart)

`POST /api/admin/machinery`  
`Content-Type: multipart/form-data`

| Part | Type | Required |
|------|------|----------|
| `data` | JSON string, **Content-Type: `application/json`** | Yes |
| `image` | file (jpg/png) | No |

**`data` body (CreateMachineryRequest):**

```ts
interface CreateMachineryRequest {
  code: string;               // required, unique (case-insensitive)
  name: string;               // required
  itemDescription?: string | null;
  jobCode?: string | null;
  defaultUom?: string | null; // defaults to "HOUR" on server if omitted/blank
  siteId: number;             // required
  serialNumber?: string | null;
  model?: string | null;
  status?: MachineryStatus;   // default ACTIVE
  markUsedOnDate?: string | null; // ISO date yyyy-MM-dd — if set, creates usage row for that day with qty 1
}
```

**Response `data`:** `MachineryResponse`

---

## 3. Update machinery (multipart)

`PUT /api/admin/machinery/{id}`  
Same multipart rules as create. **`data`:** only include fields to change (all optional):

```ts
interface UpdateMachineryRequest {
  name?: string;
  itemDescription?: string | null;
  jobCode?: string | null;
  defaultUom?: string | null;
  serialNumber?: string | null;
  model?: string | null;
  status?: MachineryStatus;
}
```

**Response `data`:** `MachineryResponse`

---

## 4. Delete machinery

`DELETE /api/admin/machinery/{id}`

- **409 / business error** if usage rows exist (retire via `status` instead).

---

## 5. Daily usage (load / checkbox + qty screen)

`GET /api/admin/machinery/usage/selection?siteId={id}&date={yyyy-MM-dd}`

Returns **one row per machine** in the site catalog. If there is no usage for that date, `qty` is `0` and `uom` matches catalog `defaultUom` behavior from server (see implementation).

**Response `data`:**

```ts
interface DailyUsageSelectionResponse {
  siteId: number;
  date: string;               // yyyy-MM-dd
  lines: MachineryUsageLineResponse[];
}

interface MachineryUsageLineResponse {
  machineryId: number;
  code: string;
  name: string;
  itemDescription: string | null;
  catalogStatus: MachineryStatus;
  imagePath: string | null;
  qty: string | number;       // BigDecimal in JSON (e.g. "8.5" or 0)
  uom: string;
  jobCode: string | null;     // usage-line override for that day
  notes: string | null;
}
```

**UI:** Treat “used today” as `qty > 0`. QTY +/- controls only change the number you send in **Save**.

---

## 6. Save daily usage (replace set for that day)

`PUT /api/admin/machinery/usage/selection`  
`Content-Type: application/json`

```ts
interface SaveUsageSelectionRequest {
  siteId: number;
  date: string;               // yyyy-MM-dd
  lines: UsageLineRequest[];
}

interface UsageLineRequest {
  machineryId: number;
  qty: number;                // must be > 0 to be stored; 0 or omitted lines = not used
  uom: string;
  jobCode?: string | null;
  notes?: string | null;
}
```

**Behavior:**

- Server **deletes** all usage rows for that `siteId` + `date`, then inserts one row per submitted line with **`qty > 0`**.
- Send **`lines: []`** to clear the entire day.
- Each machine should appear **at most once** in `lines`.

**Response:** `data` is `null`; `message` e.g. `"Usage saved"`.

---

## 7. Monthly summary

`GET /api/admin/machinery/usage/summary/month?siteId=&year=&month=`

**Response `data`:**

```ts
interface MonthlyUsageSummaryResponse {
  siteId: number;
  year: number;
  month: number;
  days: MonthlyDaySummaryResponse[];  // only days with at least one machine
}

interface MonthlyDaySummaryResponse {
  date: string;               // yyyy-MM-dd
  machineCount: number;
  machineCodes: string[];
}
```

---

## 8. Yearly summary

`GET /api/admin/machinery/usage/summary/year?siteId=&year=`

**Response `data`:**

```ts
interface YearlyUsageSummaryResponse {
  siteId: number;
  year: number;
  months: YearlyMonthSummaryResponse[];  // 12 entries (Jan–Dec)
}

interface YearlyMonthSummaryResponse {
  month: number;              // 1–12
  monthName: string;          // e.g. "January"
  daysWithUsage: number;
  totalMachineDays: number;   // sum over days of (machines used that day)
  topMachineCodes: string[];  // up to 3 codes
}
```

---

## Quick integration checklist

1. Reuse existing **admin login** and attach **Bearer** token to all calls above.
2. **Create/Update machinery:** `FormData`: append `data` as a `Blob` with type `application/json`, optional `image` file.
3. **Daily:** GET `usage/selection` → render rows; PUT `usage/selection` with only rows with `qty > 0` (or `[]` to clear).
4. Show images via `/api/files?path=…` with the same auth token if your app loads images through XHR/fetch with credentials.

---

*Generated from backend: `MachineryController` and related DTOs.*
