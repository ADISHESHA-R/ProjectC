package com.attendance.system.service;

import com.attendance.system.dto.jobsite.SiteJobWorkflowBatchSaveRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists multiple site job workflow payloads in one request (single transaction).
 */
@Service
public class SiteWorkflowBatchService {

    private final SiteService siteService;
    private final SiteJobDataService siteJobDataService;
    private final SiteEquipmentService siteEquipmentService;

    /**
     * {@code SiteService} is lazy so this bean can be created even if {@code SiteService} participates
     * in a dependency chain with services used during site workflow saves.
     */
    public SiteWorkflowBatchService(
        @Lazy SiteService siteService,
        SiteJobDataService siteJobDataService,
        SiteEquipmentService siteEquipmentService) {
        this.siteService = siteService;
        this.siteJobDataService = siteJobDataService;
        this.siteEquipmentService = siteEquipmentService;
    }

    /**
     * Apply each non-null section in order. Wizard is saved first (may sync embedded challenges);
     * explicit {@code challengeLines} runs after and overrides normalized challenge rows when present.
     */
    @Transactional
    public void saveBatch(Long siteId, SiteJobWorkflowBatchSaveRequest body) {
        if (body == null) {
            return;
        }
        if (body.getWizard() != null && !body.getWizard().isNull()) {
            siteService.saveWizardData(siteId, body.getWizard().toString());
        }
        if (body.getAdvanceExpenseLines() != null) {
            siteJobDataService.replaceAdvanceExpenseLines(siteId, body.getAdvanceExpenseLines());
        }
        if (body.getTechnicianPayments() != null) {
            siteJobDataService.replaceTechnicianPayments(siteId, body.getTechnicianPayments());
        }
        if (body.getToolIssues() != null) {
            siteJobDataService.replaceToolIssues(siteId, body.getToolIssues());
        }
        if (body.getEquipmentPortal() != null) {
            siteEquipmentService.savePortal(siteId, body.getEquipmentPortal());
        }
        if (body.getBehaviourReport() != null && !body.getBehaviourReport().isNull()) {
            siteJobDataService.replaceBehaviourPayload(siteId, body.getBehaviourReport().toString());
        }
        if (body.getChallengeLines() != null && !body.getChallengeLines().isNull()) {
            siteJobDataService.replaceChallengeLinesFromPayload(siteId, body.getChallengeLines());
        }
        if (body.getAttendanceRegisterCells() != null && body.getAttendanceRegisterCells().getCells() != null) {
            siteJobDataService.replaceRegisterCells(siteId, body.getAttendanceRegisterCells().getCells());
        }
    }
}
