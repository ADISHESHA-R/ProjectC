package com.attendance.system.controller;

import com.attendance.system.dto.jobsite.SaveRegisterCellsRequest;
import com.attendance.system.dto.jobsite.SiteAdvanceExpenseLineDto;
import com.attendance.system.dto.jobsite.SiteChallengeLineDto;
import com.attendance.system.dto.jobsite.SiteEquipmentLayoutSaveRequest;
import com.attendance.system.dto.jobsite.SiteEquipmentPortalResponse;
import com.attendance.system.dto.jobsite.SiteEquipmentPortalSaveRequest;
import com.attendance.system.dto.jobsite.SiteTechnicianDailyPaymentDto;
import com.attendance.system.dto.jobsite.SiteToolIssueDto;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.AttendanceRegisterResponse;
import com.attendance.system.dto.response.FeedbackInviteResponse;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.enums.CertificateClientStatus;
import com.attendance.system.service.AttendanceService;
import com.attendance.system.service.CustomerFeedbackService;
import com.attendance.system.service.SiteEquipmentService;
import com.attendance.system.service.SiteJobDataService;
import com.attendance.system.service.SiteService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/sites")
@RequiredArgsConstructor
@Tag(name = "Site job extensions", description = "Wizard JSON, job-site tables, attendance register, public feedback invites (Admin)")
@SecurityRequirement(name = "Bearer Authentication")
public class SiteJobExtensionController {

    private final SiteService siteService;
    private final SiteJobDataService siteJobDataService;
    private final SiteEquipmentService siteEquipmentService;
    private final AttendanceService attendanceService;
    private final CustomerFeedbackService customerFeedbackService;

    @GetMapping("/{id}/wizard")
    @Operation(summary = "Get wizard JSON blob", description = "Returns JSON string for steps 1–9 UI state (pages still using wizard).")
    public ResponseEntity<ApiResponse<String>> getWizard(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteService.getWizardData(id)));
    }

    @PutMapping(value = "/{id}/wizard", consumes = "application/json")
    @Operation(summary = "Save wizard JSON blob", description = "Replace entire wizard payload (object stored as JSON string).")
    public ResponseEntity<ApiResponse<SiteResponse>> putWizard(
        @PathVariable Long id,
        @RequestBody JsonNode payload) {
        SiteResponse updated = siteService.saveWizardData(id, payload.toString());
        return ResponseEntity.ok(ApiResponse.success("Wizard saved", updated));
    }

    @GetMapping("/{id}/attendance-register")
    @Operation(summary = "Attendance register (N-day block)", description = "Merges Attendance (P/A) with persisted register-cell overrides (P,A,S,HQ,LS,IN). Use daysPerBlock to match UI column count (default 15, max 366).")
    public ResponseEntity<ApiResponse<AttendanceRegisterResponse>> attendanceRegister(
        @PathVariable Long id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
        @RequestParam(defaultValue = "0") int blockIndex,
        @RequestParam(defaultValue = "15") int daysPerBlock,
        @RequestParam(required = false) List<Long> employeeIds) {
        return ResponseEntity.ok(ApiResponse.success(
            attendanceService.getAttendanceRegister(id, periodStart, blockIndex, employeeIds, daysPerBlock)));
    }

    @PutMapping("/{id}/job-data/attendance-register-cells")
    @Operation(summary = "Upsert attendance register cell overrides", description = "Per employee per calendar day. Null code removes override.")
    public ResponseEntity<ApiResponse<SiteResponse>> putAttendanceRegisterCells(
        @PathVariable Long id,
        @Valid @RequestBody SaveRegisterCellsRequest body) {
        siteJobDataService.replaceRegisterCells(id, body.getCells());
        return ResponseEntity.ok(ApiResponse.success("Register cells saved", siteService.getSiteById(id)));
    }

    @GetMapping("/{id}/job-data/advance-expense-lines")
    @Operation(summary = "Screen 5 — advance & expense lines")
    public ResponseEntity<ApiResponse<List<SiteAdvanceExpenseLineDto>>> getAdvanceLines(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteJobDataService.getAdvanceExpenseLines(id)));
    }

    @PutMapping("/{id}/job-data/advance-expense-lines")
    @Operation(summary = "Replace advance & expense lines", description = "Replaces entire table for the site.")
    public ResponseEntity<ApiResponse<SiteResponse>> putAdvanceLines(
        @PathVariable Long id,
        @RequestBody List<SiteAdvanceExpenseLineDto> rows) {
        siteJobDataService.replaceAdvanceExpenseLines(id, rows);
        return ResponseEntity.ok(ApiResponse.success("Advance lines saved", siteService.getSiteById(id)));
    }

    @GetMapping("/{id}/job-data/technician-payments")
    @Operation(summary = "Screen 5 — technician daily payments")
    public ResponseEntity<ApiResponse<List<SiteTechnicianDailyPaymentDto>>> getTechnicianPayments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteJobDataService.getTechnicianPayments(id)));
    }

    @PutMapping("/{id}/job-data/technician-payments")
    @Operation(summary = "Replace technician payments", description = "Flat list of rows (technicianUserId + paymentDate + amount). Any number of rows; multiple lines per technician per day are allowed (use lineOrder for stable ordering).")
    public ResponseEntity<ApiResponse<SiteResponse>> putTechnicianPayments(
        @PathVariable Long id,
        @RequestBody List<SiteTechnicianDailyPaymentDto> rows) {
        siteJobDataService.replaceTechnicianPayments(id, rows);
        return ResponseEntity.ok(ApiResponse.success("Technician payments saved", siteService.getSiteById(id)));
    }

    @GetMapping("/{id}/job-data/tool-issues")
    @Operation(summary = "Screen 6 — tools missing / damage / repair")
    public ResponseEntity<ApiResponse<List<SiteToolIssueDto>>> getToolIssues(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteJobDataService.getToolIssues(id)));
    }

    @PutMapping("/{id}/job-data/tool-issues")
    @Operation(summary = "Replace tool issue rows")
    public ResponseEntity<ApiResponse<SiteResponse>> putToolIssues(
        @PathVariable Long id,
        @RequestBody List<SiteToolIssueDto> rows) {
        siteJobDataService.replaceToolIssues(id, rows);
        return ResponseEntity.ok(ApiResponse.success("Tool issues saved", siteService.getSiteById(id)));
    }

    @GetMapping("/{id}/job-data/equipment-portal")
    @Operation(
        summary = "Equipment portal — categories, items, optional monthly availability",
        description = "Returns site-scoped equipment rows grouped by category. When year and month are both set, each item includes dayPresent (day-of-month → true) for that calendar month."
    )
    public ResponseEntity<ApiResponse<SiteEquipmentPortalResponse>> getEquipmentPortal(
        @PathVariable Long id,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(ApiResponse.success(siteEquipmentService.getPortal(id, year, month)));
    }

    @PutMapping("/{id}/job-data/equipment-portal")
    @Operation(
        summary = "Save equipment portal (categories + items + optional month availability)",
        description = "Authoritative save: items not included are deleted. When availabilityYear and availabilityMonth are set, any item with non-null dayPresent updates that month (true = cell stored; false or omitted day = no cell)."
    )
    public ResponseEntity<ApiResponse<SiteEquipmentPortalResponse>> putEquipmentPortal(
        @PathVariable Long id,
        @Valid @RequestBody SiteEquipmentPortalSaveRequest body) {
        SiteEquipmentPortalResponse data = siteEquipmentService.savePortal(id, body);
        return ResponseEntity.ok(ApiResponse.success("Equipment portal saved", data));
    }

    @PutMapping("/{id}/job-data/equipment-portal/layout")
    @Operation(
        summary = "Reorder / cross-category move equipment items",
        description = "Each block is a categoryId and ordered itemIds for that category after drag-and-drop. Every item on the site must appear exactly once across all blocks."
    )
    public ResponseEntity<ApiResponse<SiteEquipmentPortalResponse>> putEquipmentPortalLayout(
        @PathVariable Long id,
        @Valid @RequestBody SiteEquipmentLayoutSaveRequest body) {
        siteEquipmentService.saveLayout(id, body);
        return ResponseEntity.ok(ApiResponse.success("Equipment layout saved", siteEquipmentService.getPortal(id, null, null)));
    }

    @GetMapping("/{id}/job-data/behaviour-report")
    @Operation(summary = "Screen 7 — behaviour matrix JSON")
    public ResponseEntity<ApiResponse<String>> getBehaviour(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteJobDataService.getBehaviourPayload(id)));
    }

    @PutMapping(value = "/{id}/job-data/behaviour-report", consumes = "application/json")
    @Operation(summary = "Replace behaviour matrix JSON")
    public ResponseEntity<ApiResponse<SiteResponse>> putBehaviour(
        @PathVariable Long id,
        @RequestBody JsonNode payload) {
        siteJobDataService.replaceBehaviourPayload(id, payload.toString());
        return ResponseEntity.ok(ApiResponse.success("Behaviour report saved", siteService.getSiteById(id)));
    }

    @GetMapping("/{id}/job-data/challenge-lines")
    @Operation(summary = "Screen 9 — challenges (unbounded rows)", description = "Each row persists headLabel + details; order by lineOrder.")
    public ResponseEntity<ApiResponse<List<SiteChallengeLineDto>>> getChallenges(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteJobDataService.getChallengeLines(id)));
    }

    @PutMapping("/{id}/job-data/challenge-lines")
    @Operation(summary = "Replace challenge lines", description = "Each row: headLabel (or challengeCatalogIndex 1..n with catalog from GET /api/meta/challenge-line-heads) plus optional incidentDate, involvedUserId, challengesFaced, status. Any number of rows.")
    public ResponseEntity<ApiResponse<SiteResponse>> putChallenges(
        @PathVariable Long id,
        @RequestBody List<SiteChallengeLineDto> rows) {
        siteJobDataService.replaceChallengeLines(id, rows);
        return ResponseEntity.ok(ApiResponse.success("Challenges saved", siteService.getSiteById(id)));
    }

    @PostMapping("/{id}/feedback-invites")
    @Operation(summary = "Create public feedback link", description = "Opaque token for customer feedback + certificate approval.")
    public ResponseEntity<ApiResponse<FeedbackInviteResponse>> createFeedbackInvite(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(customerFeedbackService.createInvite(id)));
    }

    @GetMapping("/{id}/customer-feedback")
    @Operation(summary = "Admin view of customer feedback", description = "Certificate status + raw feedback JSON.")
    public ResponseEntity<ApiResponse<SiteCustomerFeedbackAdminDto>> customerFeedback(@PathVariable Long id) {
        SiteResponse site = siteService.getSiteById(id);
        SiteCustomerFeedbackAdminDto dto = new SiteCustomerFeedbackAdminDto();
        dto.setCertificateClientStatus(site.getCertificateClientStatus());
        dto.setCustomerFeedbackApprovedAt(site.getCustomerFeedbackApprovedAt());
        dto.setFeedbackJson(customerFeedbackService.getFeedbackPayloadForAdmin(id));
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Data
    public static class SiteCustomerFeedbackAdminDto {
        private CertificateClientStatus certificateClientStatus;
        private LocalDateTime customerFeedbackApprovedAt;
        private String feedbackJson;
    }
}
