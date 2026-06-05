# Site job workflow — backend JSON contracts

This doc complements [`SITE_JOB_WORKFLOW_FRONTEND.md`](SITE_JOB_WORKFLOW_FRONTEND.md) with **response shapes** the **Attendance Portal** admin SPA expects, with notes on what **this** Spring API actually returns.

---

## Admin customer feedback (Step 10 / completion)

### Endpoint

- **`GET /api/admin/sites/{siteId}/customer-feedback`** (JWT)  
- **`{siteId}`** resolves like other admin site routes: numeric id, job code, or slug `{name}-{jobCode}` (job code = segment after last `-`, case-insensitive).

### HTTP envelope (`ApiResponse`)

This backend returns:

```json
{
  "success": true,
  "Success": true,
  "status": true,
  "message": "Success",
  "data": { }
}
```

- **`success`** is the canonical boolean (preferred).
- **`Success`** and **`status`** duplicate the same boolean for SPAs that check alternate envelope shapes.
- **`data`** is always the **`SiteCustomerFeedbackAdminDto` object** (never a bare array). It is **not** double-wrapped as `{ "data": { "data": … } }` by this service.

If **`data`** were a **string** containing JSON (some proxies), the frontend parser may **`JSON.parse`** it once; this backend does not emit that shape by default.

### Ideal DTO inside `data` (`SiteCustomerFeedbackAdminDto`)

**Canonical wire format from this API:** **camelCase** property names (Jackson default). The same values are also accepted on **inbound** JSON under common **snake_case** aliases (e.g. `customer_name` → `name`, `feedback_json` → `feedbackJson`) thanks to `@JsonAlias`.

When **`feedbackJson`** is non-null and non-blank, the JSON also includes a duplicate key **`feedback_json`** with the same string so clients that only read snake_case still see the blob.

| Field | Type / notes |
|--------|----------------|
| `siteId`, `jobCode` | Long / string — identify the site row |
| `certificateClientStatus` | String enum name, e.g. `NONE`, `FEEDBACK_SUBMITTED`, `APPROVED_BY_CLIENT` |
| `customerFeedbackApprovedAt` | ISO-8601 date-time or omitted |
| `customerFeedbackInviteToken`, `customerFeedbackInviteExpiresAt` | Latest valid invite, or omitted |
| `feedbackJson` | Raw stored JSON **string** (same as DB `customer_feedback_payload`), or omitted |
| `name`, `email`, `phone`, `companyName` | Strings, or omitted |
| `productQuality`, `customerService`, `machiningQuality`, `pricing`, `shippingDelivery` | Strings, or omitted |
| `likelihoodRecommend` | Integer when parsed from stored JSON, or omitted |
| `otherCategoryNote`, `specificFeedback`, `suggestions`, `additionalComments` | Strings, or omitted |
| `extra` | JSON object passthrough when present |

**Example** (minimal successful read; flat answers preferred when present):

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "siteId": 12,
    "jobCode": "sam123",
    "certificateClientStatus": "FEEDBACK_SUBMITTED",
    "name": "Test Test",
    "email": "user@example.com",
    "phone": "",
    "companyName": ""
  }
}
```

The server **hydrates flat fields** from stored JSON when possible (`CustomerFeedbackService.mergeStoredCustomerFeedbackIntoDto`). You can rely on **`feedbackJson`** alone, or on **flat fields**, or both.

### When answers live only in `feedbackJson`

Supported **stored** patterns inside the string (client may flatten nested string wrappers):

- **A — Single encoding:**  
  `"feedbackJson": "{\"name\":\"Test Test\",\"email\":\"user@example.com\"}"`

- **B — Double-encoded wrapper:**  
  `"feedbackJson": "{\"feedbackJson\":\"{\\\"name\\\":\\\"Test Test\\\"}\"}"`  
  The admin SPA may repeatedly parse `feedbackJson` / `feedback_json` string layers and merge inner objects until plain fields appear.

This backend **persists** a single JSON object string (see public submit + wizard merge); it does not require double-encoding. Legacy or gateway blobs may still use nested strings; the client normalizes those.

### Snake_case on the wire

This API **serializes the DTO in camelCase**. If a proxy or older layer emits **snake_case**, the frontend may map a subset (e.g. `customer_name` → `name`, `company_name` → `companyName`, `product_quality` → `productQuality`, `customer_email` / `e_mail` → `email`, `phone_number` → `phone`). **Preferred:** camelCase on `data` as produced by this service.

### Optional: Spring `Page` / `{ content: [ … ] }`

If **`data`** is wrapped like `{ "content": [ { …dto… } ] }` (e.g. accidental gateway mapping), the frontend may treat the **first** element of `content` as the feedback DTO when it looks like one. **This backend** does not wrap `GET …/customer-feedback` in `Page`; `data` is the DTO directly.

### `GET /api/admin/sites/{siteId}` (site row mirror)

For the same completion step the app may **merge** the dedicated customer-feedback response with the **site** object.

- **`SiteResponse`** includes **`customerFeedbackJson`** (camelCase) when non-null, plus duplicate keys **`customer_feedback_json`** and **`customer_feedback_payload`** with the same string for SPA merge helpers.
- The client may also look for aliases such as `customer_feedback_json`, `customerFeedbackPayload`, `customer_feedback_payload` on generic site JSON; **this API** uses **`customerFeedbackJson`** on `SiteResponse`.

**Contract summary**

1. **`GET …/customer-feedback`:** `{ "success": true, "data": <SiteCustomerFeedbackAdminDto> }` with at least **`certificateClientStatus`**, and either **flat answer fields** and/or **`feedbackJson`** (string, possibly nested string wrappers after client normalize).
2. **`GET …/sites/{id}`:** optional mirror via **`customerFeedbackJson`** so the UI can backfill if the dedicated payload is empty or partial.

### Wizard merge (same site row)

Saving the wizard (`PUT …/wizard` or `workflow-batch` with `wizard`) may **merge** embedded step-10 feedback into `customer_feedback_payload` on that site (`CustomerFeedbackService.tryMergeCustomerFeedbackFromWizard`). That keeps admin reads **site-scoped** and consistent with public `POST …/customer-feedback`.

---

## Related files (backend)

- Controller: `SiteJobExtensionController` — `GET /{id}/customer-feedback`
- DTO: `SiteCustomerFeedbackAdminDto`
- Envelope: `ApiResponse`
- Site mirror field: `SiteResponse.customerFeedbackJson`
- Merge logic: `CustomerFeedbackService.mergeStoredCustomerFeedbackIntoDto`, `tryMergeCustomerFeedbackFromWizard`

Frontend reference (separate repo / module): `extractCustomerFeedbackDtoFromAdminResponse`, `normalizeAdminCustomerFeedbackDto`, `mergeSiteAndEndpointCustomerFeedbackForAdmin` in `src/data/siteJobWorkflowForms.js` and `src/config/customerFeedbackPublic.js`.
