package com.attendance.system.dto.jobsite;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/**
 * Optional sections for {@code PUT/POST /api/admin/sites/{id}/job-data/workflow-batch}.
 * Each non-null field is applied in a fixed order (see controller JavaDoc); {@code null} skips that section.
 */
@Data
public class SiteJobWorkflowBatchSaveRequest {

    /** Full wizard JSON object (stored as string; may sync challenge lines if wizard embeds them). */
    private JsonNode wizard;

    private List<SiteAdvanceExpenseLineDto> advanceExpenseLines;
    private List<SiteTechnicianDailyPaymentDto> technicianPayments;
    private List<SiteToolIssueDto> toolIssues;

    /** Same shape as {@code PUT /job-data/equipment-portal}. */
    private SiteEquipmentPortalSaveRequest equipmentPortal;

    /** Raw behaviour matrix JSON object. */
    private JsonNode behaviourReport;

    /** Array or wrapper object accepted by {@link com.attendance.system.service.SiteJobDataService#replaceChallengeLinesFromPayload}. */
    private JsonNode challengeLines;

    private SaveRegisterCellsRequest attendanceRegisterCells;
}
