# Admin list APIs — search & filter

All responses use the existing `ApiResponse` wrapper. **Users** were already paginated; **sites** and **admin notices** now return a **Spring `Page`** (same shape as users: `content`, `totalElements`, `totalPages`, `size`, `number`, …).

## `GET /api/admin/users`

| Query param | Optional | Description |
|-------------|----------|-------------|
| `search` | yes | Matches **name**, **email**, or **employeeId** (case-insensitive, substring). |
| `role` | yes | `ADMIN` or `EMPLOYEE`. |
| `status` | yes | `ACTIVE` or `INACTIVE`. |
| `page` | no (default `0`) | Page index. |
| `size` | no (default `10`) | Page size. |

Default sort: **name** ascending.

## `GET /api/admin/sites`

| Query param | Optional | Description |
|-------------|----------|-------------|
| `search` | yes | Matches **name**, **jobCode**, or **address** (case-insensitive, substring). |
| `isActive` | yes | `true` = active only, `false` = inactive only; omit = all. |
| `page` | no (default `0`) | Page index. |
| `size` | no (default `20`) | Page size. |

Default sort: **name** ascending.

**Breaking change:** Response `data` is a **page object**, not a raw array. Use `data.content` for the list.

## `GET /api/admin/notices`

| Query param | Optional | Description |
|-------------|----------|-------------|
| `search` | yes | Matches **message** (case-insensitive, substring). |
| `page` | no (default `0`) | Page index. |
| `size` | no (default `20`) | Page size. |

Default sort: **updatedAt** descending.

**Breaking change:** Response `data` is a **page object**, not a raw array.

## Pending approvals

Use existing **`GET /api/admin/attendance`** with `status=PENDING` (and optional `date`, `siteId`, `page`, `size`).

Public **`GET /api/notices`** (employees) is unchanged — still returns a full list for the dashboard.
