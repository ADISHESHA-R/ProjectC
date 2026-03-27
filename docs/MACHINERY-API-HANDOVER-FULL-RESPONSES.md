# Machinery APIs — Handover for frontend (new)

**Base path:** `{API_BASE}/api/admin/machinery`  
**Authentication:** `Authorization: Bearer <admin_access_token>`  
**Response envelope (all endpoints):**

```json
{
  "success": true,
  "message": "string",
  "data": { }
}
```

**Error example (4xx/5xx with body):**

```json
{
  "success": false,
  "message": "Human-readable error text",
  "data": null
}
```

**Images:** `imagePath` in payloads is a relative path. Fetch with:

`GET {API_BASE}/api/files?path=<encodeURIComponent(imagePath)>`

---

## 1. List machinery catalog (by site)

**Request**

- **Method:** `GET`
- **URL:** `/api/admin/machinery?siteId={number}`
- **Query:** `siteId` (required)

**Success — HTTP 200**

`message` is typically `"Success"` (default from backend).

```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "code": "MAC-001",
      "name": "Excavator A",
      "itemDescription": "Full specification text.",
      "jobCode": "SITE-OMKAR-01",
      "defaultUom": "HOUR",
      "siteId": 3,
      "siteName": "Omkar",
      "imagePath": "machinery/machinery_a1b2c3d4.jpg",
      "serialNumber": "SN-12345",
      "model": "CAT-320",
      "status": "ACTIVE",
      "createdAt": "2025-03-16T10:15:30",
      "updatedAt": "2025-03-16T10:15:30"
    }
  ]
}
```

**Notes:** `data` is an **array**; may be `[]` if no machines for that site.  
`createdAt` / `updatedAt` are ISO-8601 local date-time strings from the server.

---

## 2. Create machinery

**Request**

- **Method:** `POST`
- **URL:** `/api/admin/machinery`
- **Headers:** `Authorization`, `Content-Type: multipart/form-data`
- **Parts:**
  - `data` — JSON string, content type `application/json`
  - `image` — optional file (JPG/PNG)

**Example `data` JSON (multipart part):**

```json
{
  "code": "MAC-001",
  "name": "Excavator A",
  "itemDescription": "Optional long description.",
  "jobCode": "SITE-OMKAR-01",
  "defaultUom": "HOUR",
  "siteId": 3,
  "serialNumber": "SN-12345",
  "model": "CAT-320",
  "status": "ACTIVE",
  "markUsedOnDate": null
}
```

`status`: `ACTIVE` | `MAINTENANCE` | `RETIRED`.  
If `markUsedOnDate` is `"2025-03-16"`, server adds a usage row for that day with qty `1`.

**Success — HTTP 200**

```json
{
  "success": true,
  "message": "Machinery created",
  "data": {
    "id": 1,
    "code": "MAC-001",
    "name": "Excavator A",
    "itemDescription": "Optional long description.",
    "jobCode": "SITE-OMKAR-01",
    "defaultUom": "HOUR",
    "siteId": 3,
    "siteName": "Omkar",
    "imagePath": "machinery/machinery_a1b2c3d4.jpg",
    "serialNumber": "SN-12345",
    "model": "CAT-320",
    "status": "ACTIVE",
    "createdAt": "2025-03-16T10:15:30",
    "updatedAt": "2025-03-16T10:15:30"
  }
}
```

**Error examples:** duplicate code (`400`), invalid site (`404`), validation errors (`400`).

---

## 3. Update machinery

**Request**

- **Method:** `PUT`
- **URL:** `/api/admin/machinery/{id}`
- **Headers:** `Authorization`, `multipart/form-data`
- **Parts:** `data` (JSON, all fields optional), optional `image`

**Example `data` JSON:**

```json
{
  "name": "Excavator A (updated)",
  "itemDescription": "Updated text.",
  "jobCode": "SITE-OMKAR-01",
  "defaultUom": "DAY",
  "serialNumber": "SN-12345",
  "model": "CAT-320",
  "status": "MAINTENANCE"
}
```

**Success — HTTP 200**

```json
{
  "success": true,
  "message": "Machinery updated",
  "data": {
    "id": 1,
    "code": "MAC-001",
    "name": "Excavator A (updated)",
    "itemDescription": "Updated text.",
    "jobCode": "SITE-OMKAR-01",
    "defaultUom": "DAY",
    "siteId": 3,
    "siteName": "Omkar",
    "imagePath": "machinery/machinery_a1b2c3d4.jpg",
    "serialNumber": "SN-12345",
    "model": "CAT-320",
    "status": "MAINTENANCE",
    "createdAt": "2025-03-16T10:15:30",
    "updatedAt": "2025-03-16T11:00:00"
  }
}
```

---

## 4. Delete machinery

**Request**

- **Method:** `DELETE`
- **URL:** `/api/admin/machinery/{id}`

**Success — HTTP 200**

```json
{
  "success": true,
  "message": "Success",
  "data": null
}
```

**Error — e.g. usage history exists — HTTP 400**

```json
{
  "success": false,
  "message": "Cannot delete machinery with usage history; retire it instead.",
  "data": null
}
```

---

## 5. Get daily usage selection (catalog + qty/uom for one date)

**Request**

- **Method:** `GET`
- **URL:** `/api/admin/machinery/usage/selection?siteId={id}&date={yyyy-MM-dd}`
- **Query:** `siteId` (required), `date` (required, ISO date e.g. `2025-03-16`)

**Success — HTTP 200**

One row **per machine** in the site catalog. `qty` is `0` when there is no usage for that date.

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 3,
    "date": "2025-03-16",
    "lines": [
      {
        "machineryId": 1,
        "code": "MAC-001",
        "name": "Excavator A",
        "itemDescription": "Full specification text.",
        "catalogStatus": "ACTIVE",
        "imagePath": "machinery/machinery_a1b2c3d4.jpg",
        "qty": 8.5,
        "uom": "HOUR",
        "jobCode": "JOB-2025-044",
        "notes": null
      },
      {
        "machineryId": 2,
        "code": "MAC-002",
        "name": "Generator B",
        "itemDescription": null,
        "catalogStatus": "ACTIVE",
        "imagePath": null,
        "qty": 0,
        "uom": "HOUR",
        "jobCode": null,
        "notes": null
      }
    ]
  }
}
```

**Notes:** `qty` may serialize as a number or as a string for decimal values depending on Jackson config; parse safely.  
`catalogStatus` mirrors the machinery record (`ACTIVE` | `MAINTENANCE` | `RETIRED`).

---

## 6. Save daily usage (replace all usage for site + date)

**Request**

- **Method:** `PUT`
- **URL:** `/api/admin/machinery/usage/selection`
- **Headers:** `Authorization`, `Content-Type: application/json`

**Body:**

```json
{
  "siteId": 3,
  "date": "2025-03-16",
  "lines": [
    {
      "machineryId": 1,
      "qty": 8.5,
      "uom": "HOUR",
      "jobCode": "JOB-2025-044",
      "notes": null
    },
    {
      "machineryId": 2,
      "qty": 1,
      "uom": "DAY",
      "jobCode": null,
      "notes": "Optional note"
    }
  ]
}
```

Server **replaces** all usage for that `siteId` + `date`. Only lines with **`qty > 0`** are stored.  
`lines: []` clears the day.

**Success — HTTP 200**

```json
{
  "success": true,
  "message": "Usage saved",
  "data": null
}
```

---

## 7. Monthly usage summary

**Request**

- **Method:** `GET`
- **URL:** `/api/admin/machinery/usage/summary/month?siteId={id}&year={yyyy}&month={1-12}`

**Success — HTTP 200**

`days` includes **only dates that have at least one usage** in that month.

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 3,
    "year": 2025,
    "month": 3,
    "days": [
      {
        "date": "2025-03-01",
        "machineCount": 2,
        "machineCodes": ["MAC-001", "MAC-002"]
      },
      {
        "date": "2025-03-16",
        "machineCount": 1,
        "machineCodes": ["MAC-001"]
      }
    ]
  }
}
```

---

## 8. Yearly usage summary

**Request**

- **Method:** `GET`
- **URL:** `/api/admin/machinery/usage/summary/year?siteId={id}&year={yyyy}`

**Success — HTTP 200**

`months` always has **12** entries (January → December).

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 3,
    "year": 2025,
    "months": [
      {
        "month": 1,
        "monthName": "January",
        "daysWithUsage": 18,
        "totalMachineDays": 42,
        "topMachineCodes": ["MAC-001", "MAC-002", "MAC-003"]
      },
      {
        "month": 2,
        "monthName": "February",
        "daysWithUsage": 0,
        "totalMachineDays": 0,
        "topMachineCodes": []
      }
    ]
  }
}
```

**Field meanings:**

- `daysWithUsage` — number of calendar days in that month with at least one machine used.
- `totalMachineDays` — sum over those days of (count of machines used that day).
- `topMachineCodes` — up to **3** machine codes ranked by usage frequency in that month (then by total qty as tie-breaker).

*(Example truncates to 2 months; real response has 12.)*

---

## Quick reference table

| # | Method | Path | `data` type |
|---|--------|------|-------------|
| 1 | GET | `/api/admin/machinery?siteId=` | `MachineryResponse[]` |
| 2 | POST | `/api/admin/machinery` | `MachineryResponse` |
| 3 | PUT | `/api/admin/machinery/{id}` | `MachineryResponse` |
| 4 | DELETE | `/api/admin/machinery/{id}` | `null` |
| 5 | GET | `/api/admin/machinery/usage/selection?siteId=&date=` | `DailyUsageSelectionResponse` |
| 6 | PUT | `/api/admin/machinery/usage/selection` | `null` |
| 7 | GET | `/api/admin/machinery/usage/summary/month?siteId=&year=&month=` | `MonthlyUsageSummaryResponse` |
| 8 | GET | `/api/admin/machinery/usage/summary/year?siteId=&year=` | `YearlyUsageSummaryResponse` |

---

*Backend: `MachineryController` under `/api/admin/machinery`. Timestamps and IDs in examples are illustrative.*
