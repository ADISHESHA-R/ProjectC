package com.attendance.system.service;

import com.attendance.system.dto.jobsite.AttendanceRegisterCellWriteDto;
import com.attendance.system.dto.jobsite.ChallengeHeadResponse;
import com.attendance.system.dto.jobsite.SiteAdvanceExpenseLineDto;
import com.attendance.system.dto.jobsite.SiteChallengeLineDto;
import com.attendance.system.dto.jobsite.SiteTechnicianDailyPaymentDto;
import com.attendance.system.dto.jobsite.SiteToolIssueDto;
import com.attendance.system.entity.Site;
import com.attendance.system.entity.SiteAdvanceExpenseLine;
import com.attendance.system.entity.SiteAttendanceRegisterCell;
import com.attendance.system.entity.SiteBehaviourReport;
import com.attendance.system.entity.SiteChallengeLine;
import com.attendance.system.entity.SiteTechnicianDailyPayment;
import com.attendance.system.entity.SiteToolIssue;
import com.attendance.system.entity.User;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.SiteAdvanceExpenseLineRepository;
import com.attendance.system.repository.SiteAttendanceRegisterCellRepository;
import com.attendance.system.repository.SiteBehaviourReportRepository;
import com.attendance.system.repository.SiteChallengeLineRepository;
import com.attendance.system.repository.SiteRepository;
import com.attendance.system.repository.SiteTechnicianDailyPaymentRepository;
import com.attendance.system.repository.SiteToolIssueRepository;
import com.attendance.system.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SiteJobDataService {

    private static final String[] CHALLENGE_LINE_ARRAY_KEYS = {
        "challengeLines", "challenges", "siteChallengeLines", "challengeRows", "rows", "lines", "items", "data"
    };

    private static final String[] WIZARD_STEP_KEYS = {"step7", "step9", "step_7", "step_9"};

    private static final String[] CHALLENGE_HEAD_LABELS = {
        "Transport",
        "Un-Loading",
        "Crane",
        "Entry Passes",
        "Safety Training",
        "Eqmt Set up",
        "Work Front Delay",
        "Job Inspection",
        "Job Clearance",
        "Power",
        "Welding",
        "Coren Manpower",
        "Eqmt Failure",
        "Tool Damage",
        "Non-Avlbty - Tools",
        "Customer Clearance",
        "Job related Issues",
        "WCR",
        "Eqmt Despatch",
        "Work site Closed",
        "Work Timing Restriction",
        "Local Manpower Issue"
    };

    public static final List<String> CHALLENGE_HEADS = Collections.unmodifiableList(Arrays.asList(CHALLENGE_HEAD_LABELS));

    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final SiteAdvanceExpenseLineRepository advanceExpenseLineRepository;
    private final SiteTechnicianDailyPaymentRepository technicianDailyPaymentRepository;
    private final SiteToolIssueRepository toolIssueRepository;
    private final SiteBehaviourReportRepository behaviourReportRepository;
    private final SiteChallengeLineRepository challengeLineRepository;
    private final SiteAttendanceRegisterCellRepository attendanceRegisterCellRepository;
    private final SiteEquipmentService siteEquipmentService;
    private final ObjectMapper objectMapper;

    public SiteJobDataService(
        SiteRepository siteRepository,
        UserRepository userRepository,
        SiteAdvanceExpenseLineRepository advanceExpenseLineRepository,
        SiteTechnicianDailyPaymentRepository technicianDailyPaymentRepository,
        SiteToolIssueRepository toolIssueRepository,
        SiteBehaviourReportRepository behaviourReportRepository,
        SiteChallengeLineRepository challengeLineRepository,
        SiteAttendanceRegisterCellRepository attendanceRegisterCellRepository,
        SiteEquipmentService siteEquipmentService,
        ObjectMapper objectMapper) {
        this.siteRepository = siteRepository;
        this.userRepository = userRepository;
        this.advanceExpenseLineRepository = advanceExpenseLineRepository;
        this.technicianDailyPaymentRepository = technicianDailyPaymentRepository;
        this.toolIssueRepository = toolIssueRepository;
        this.behaviourReportRepository = behaviourReportRepository;
        this.challengeLineRepository = challengeLineRepository;
        this.attendanceRegisterCellRepository = attendanceRegisterCellRepository;
        this.siteEquipmentService = siteEquipmentService;
        this.objectMapper = objectMapper;
    }

    public List<ChallengeHeadResponse> getChallengeHeadCatalog() {
        List<ChallengeHeadResponse> out = new ArrayList<>();
        for (int i = 0; i < CHALLENGE_HEADS.size(); i++) {
            out.add(new ChallengeHeadResponse(i + 1, CHALLENGE_HEADS.get(i)));
        }
        return out;
    }

    private Site requireSite(Long siteId) {
        return siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
    }

    @Transactional(readOnly = true)
    public List<SiteAdvanceExpenseLineDto> getAdvanceExpenseLines(Long siteId) {
        requireSite(siteId);
        return advanceExpenseLineRepository.findBySite_IdOrderByLineOrderAsc(siteId).stream()
            .map(this::toAdvanceDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void replaceAdvanceExpenseLines(Long siteId, List<SiteAdvanceExpenseLineDto> lines) {
        Site site = requireSite(siteId);
        advanceExpenseLineRepository.deleteBySite_Id(siteId);
        if (lines == null || lines.isEmpty()) {
            return;
        }
        int i = 0;
        for (SiteAdvanceExpenseLineDto d : lines) {
            SiteAdvanceExpenseLine e = new SiteAdvanceExpenseLine();
            e.setSite(site);
            e.setLineOrder(d.getLineOrder() != null ? d.getLineOrder() : ++i);
            e.setAdvanceReceivedDate(d.getAdvanceReceivedDate());
            e.setOpeningBal(d.getOpeningBal());
            e.setAmount(d.getAmount());
            e.setFoodAllow(d.getFoodAllow());
            e.setConveyance(d.getConveyance());
            e.setMedical(d.getMedical());
            e.setAdditionalManpower(d.getAdditionalManpower());
            e.setWelding(d.getWelding());
            e.setSiteExpn(d.getSiteExpn());
            e.setBalInHand(d.getBalInHand());
            e.setDispersionNotes(d.getDispersionNotes());
            advanceExpenseLineRepository.save(e);
        }
    }

    private SiteAdvanceExpenseLineDto toAdvanceDto(SiteAdvanceExpenseLine e) {
        SiteAdvanceExpenseLineDto d = new SiteAdvanceExpenseLineDto();
        d.setLineOrder(e.getLineOrder());
        d.setAdvanceReceivedDate(e.getAdvanceReceivedDate());
        d.setOpeningBal(e.getOpeningBal());
        d.setAmount(e.getAmount());
        d.setFoodAllow(e.getFoodAllow());
        d.setConveyance(e.getConveyance());
        d.setMedical(e.getMedical());
        d.setAdditionalManpower(e.getAdditionalManpower());
        d.setWelding(e.getWelding());
        d.setSiteExpn(e.getSiteExpn());
        d.setBalInHand(e.getBalInHand());
        d.setDispersionNotes(e.getDispersionNotes());
        return d;
    }

    @Transactional(readOnly = true)
    public List<SiteTechnicianDailyPaymentDto> getTechnicianPayments(Long siteId) {
        requireSite(siteId);
        return technicianDailyPaymentRepository.findBySite_IdOrderByPaymentDateAscTechnician_IdAsc(siteId).stream()
            .sorted(Comparator.comparing(SiteTechnicianDailyPayment::getPaymentDate)
                .thenComparing(p -> p.getTechnician().getId())
                .thenComparing(SiteTechnicianDailyPayment::getLineOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SiteTechnicianDailyPayment::getId))
            .map(p -> {
                SiteTechnicianDailyPaymentDto d = new SiteTechnicianDailyPaymentDto();
                d.setLineOrder(p.getLineOrder());
                d.setTechnicianUserId(p.getTechnician().getId());
                d.setPaymentDate(p.getPaymentDate());
                d.setAmount(p.getAmount());
                return d;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void replaceTechnicianPayments(Long siteId, List<SiteTechnicianDailyPaymentDto> rows) {
        Site site = requireSite(siteId);
        technicianDailyPaymentRepository.deleteBySite_Id(siteId);
        if (rows == null) {
            return;
        }
        int autoOrder = 0;
        for (SiteTechnicianDailyPaymentDto d : rows) {
            if (d.getTechnicianUserId() == null || d.getPaymentDate() == null || d.getAmount() == null) {
                continue;
            }
            User tech = userRepository.findById(d.getTechnicianUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", d.getTechnicianUserId()));
            SiteTechnicianDailyPayment p = new SiteTechnicianDailyPayment();
            p.setSite(site);
            p.setTechnician(tech);
            p.setPaymentDate(d.getPaymentDate());
            p.setAmount(d.getAmount());
            p.setLineOrder(d.getLineOrder() != null ? d.getLineOrder() : autoOrder++);
            technicianDailyPaymentRepository.save(p);
        }
    }

    @Transactional(readOnly = true)
    public List<SiteToolIssueDto> getToolIssues(Long siteId) {
        requireSite(siteId);
        return toolIssueRepository.findBySite_IdOrderByLineOrderAsc(siteId).stream()
            .map(this::toToolDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void replaceToolIssues(Long siteId, List<SiteToolIssueDto> rows) {
        Site site = requireSite(siteId);
        toolIssueRepository.deleteBySite_Id(siteId);
        if (rows == null) {
            return;
        }
        int order = 0;
        for (SiteToolIssueDto d : rows) {
            SiteToolIssue e = new SiteToolIssue();
            e.setSite(site);
            e.setLineOrder(d.getLineOrder() != null ? d.getLineOrder() : ++order);
            e.setPkgListSl(d.getPkgListSl());
            e.setItemDescription(d.getItemDescription());
            e.setDateMissing(d.getDateMissing());
            e.setDateDamage(d.getDateDamage());
            e.setDateRepair(d.getDateRepair());
            if (d.getHandledByUserId() != null) {
                e.setHandledBy(userRepository.findById(d.getHandledByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", d.getHandledByUserId())));
            }
            e.setIssueDescription(d.getIssueDescription());
            toolIssueRepository.save(e);
        }
    }

    private SiteToolIssueDto toToolDto(SiteToolIssue e) {
        SiteToolIssueDto d = new SiteToolIssueDto();
        d.setLineOrder(e.getLineOrder());
        d.setPkgListSl(e.getPkgListSl());
        d.setItemDescription(e.getItemDescription());
        d.setDateMissing(e.getDateMissing());
        d.setDateDamage(e.getDateDamage());
        d.setDateRepair(e.getDateRepair());
        d.setHandledByUserId(e.getHandledBy() != null ? e.getHandledBy().getId() : null);
        d.setIssueDescription(e.getIssueDescription());
        return d;
    }

    @Transactional(readOnly = true)
    public String getBehaviourPayload(Long siteId) {
        requireSite(siteId);
        return behaviourReportRepository.findBySite_Id(siteId)
            .map(SiteBehaviourReport::getPayloadJson)
            .orElse("{}");
    }

    @Transactional
    public void replaceBehaviourPayload(Long siteId, String json) {
        Site site = requireSite(siteId);
        SiteBehaviourReport r = behaviourReportRepository.findBySite_Id(siteId).orElse(null);
        if (r == null) {
            r = new SiteBehaviourReport();
            r.setSite(site);
        }
        r.setPayloadJson(json != null ? json : "{}");
        behaviourReportRepository.save(r);
    }

    @Transactional(readOnly = true)
    public List<SiteChallengeLineDto> getChallengeLines(Long siteId) {
        requireSite(siteId);
        return challengeLineRepository.findBySite_IdOrderByLineOrderAscIdAsc(siteId).stream()
            .map(this::toChallengeDto)
            .collect(Collectors.toList());
    }

    private SiteChallengeLineDto toChallengeDto(SiteChallengeLine line) {
        SiteChallengeLineDto d = new SiteChallengeLineDto();
        d.setLineOrder(line.getLineOrder());
        d.setHeadLabel(line.getHeadLabel());
        d.setIncidentDate(line.getIncidentDate());
        d.setInvolvedUserId(line.getInvolvedUser() != null ? line.getInvolvedUser().getId() : null);
        d.setChallengesFaced(line.getChallengesFaced());
        d.setStatus(line.getStatus());
        return d;
    }

    private String resolveChallengeHeadLabel(SiteChallengeLineDto d) {
        if (d.getHeadLabel() != null && !d.getHeadLabel().isBlank()) {
            return d.getHeadLabel().trim();
        }
        if (d.getChallengeCatalogIndex() != null) {
            int idx = d.getChallengeCatalogIndex();
            if (idx >= 1 && idx <= CHALLENGE_HEADS.size()) {
                return CHALLENGE_HEADS.get(idx - 1);
            }
        }
        return null;
    }

    private boolean rowHasPersistableContent(SiteChallengeLineDto d) {
        return d.getIncidentDate() != null
            || d.getInvolvedUserId() != null
            || (d.getChallengesFaced() != null && !d.getChallengesFaced().isBlank())
            || d.getStatus() != null;
    }

    /**
     * When the UI sends rows aligned to the fixed catalog but omits {@code headLabel},
     * infer the head from row order (first row maps to first catalog label, etc.).
     */
    private String resolveHeadWithCatalogFallback(SiteChallengeLineDto d, int zeroBasedRowIndex) {
        String head = resolveChallengeHeadLabel(d);
        if (head != null && !head.isEmpty()) {
            return head;
        }
        if (rowHasPersistableContent(d)
            && zeroBasedRowIndex >= 0
            && zeroBasedRowIndex < CHALLENGE_HEADS.size()) {
            return CHALLENGE_HEADS.get(zeroBasedRowIndex);
        }
        return null;
    }

    private JsonNode findFirstArrayChild(JsonNode object, String[] keys) {
        if (object == null || !object.isObject()) {
            return null;
        }
        for (String k : keys) {
            if (object.has(k)) {
                JsonNode v = object.get(k);
                if (v.isArray()) {
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * Looks for a challenge row array inside the wizard JSON (several common shapes).
     */
    private JsonNode locateChallengeArrayInWizard(JsonNode root) {
        if (root == null || !root.isObject()) {
            return null;
        }
        JsonNode direct = findFirstArrayChild(root, CHALLENGE_LINE_ARRAY_KEYS);
        if (direct != null) {
            return direct;
        }
        for (String sk : WIZARD_STEP_KEYS) {
            if (!root.has(sk) || !root.get(sk).isObject()) {
                continue;
            }
            JsonNode step = root.get(sk);
            JsonNode nested = findFirstArrayChild(step, CHALLENGE_LINE_ARRAY_KEYS);
            if (nested != null) {
                return nested;
            }
        }
        if (root.has("steps") && root.get("steps").isObject()) {
            JsonNode steps = root.get("steps");
            for (String idx : List.of("7", "9")) {
                if (!steps.has(idx)) {
                    continue;
                }
                JsonNode step = steps.get(idx);
                if (step.isArray()) {
                    return step;
                }
                if (step.isObject()) {
                    JsonNode nested = findFirstArrayChild(step, CHALLENGE_LINE_ARRAY_KEYS);
                    if (nested != null) {
                        return nested;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Parses PUT/POST body: raw JSON array, or an object with {@code rows}/{@code lines}/{@code challengeLines}/etc.
     */
    @Transactional
    public void replaceChallengeLinesFromPayload(Long siteId, JsonNode body) {
        if (body == null || body.isNull()) {
            doReplaceChallengeLines(siteId, List.of());
            return;
        }
        JsonNode array;
        if (body.isArray()) {
            array = body;
        } else if (body.isObject()) {
            array = findFirstArrayChild(body, CHALLENGE_LINE_ARRAY_KEYS);
            if (array == null) {
                throw new IllegalArgumentException(
                    "Challenge lines body must be a JSON array, or an object containing one of: "
                        + String.join(", ", CHALLENGE_LINE_ARRAY_KEYS));
            }
        } else {
            throw new IllegalArgumentException("Challenge lines body must be a JSON array or object");
        }
        List<SiteChallengeLineDto> rows = objectMapper.convertValue(array, new TypeReference<>() {});
        doReplaceChallengeLines(siteId, rows);
    }

    /**
     * When the UI only persists the wizard blob, also upsert {@code site_challenge_lines}
     * if the JSON contains a recognizable challenge array (does nothing otherwise).
     */
    @Transactional
    public void trySyncChallengeLinesFromWizardString(Long siteId, String wizardJson) {
        if (wizardJson == null || wizardJson.isBlank()) {
            return;
        }
        try {
            JsonNode root = objectMapper.readTree(wizardJson);
            JsonNode arr = locateChallengeArrayInWizard(root);
            if (arr == null || !arr.isArray()) {
                return;
            }
            List<SiteChallengeLineDto> rows = objectMapper.convertValue(arr, new TypeReference<>() {});
            doReplaceChallengeLines(siteId, rows);
        } catch (Exception e) {
            log.warn("Challenge lines sync from wizard skipped for site {}: {}", siteId, e.getMessage());
        }
    }

    @Transactional
    public void replaceChallengeLines(Long siteId, List<SiteChallengeLineDto> rows) {
        doReplaceChallengeLines(siteId, rows);
    }

    private void doReplaceChallengeLines(Long siteId, List<SiteChallengeLineDto> rows) {
        Site site = requireSite(siteId);
        challengeLineRepository.deleteBySite_Id(siteId);
        if (rows == null) {
            return;
        }
        int autoOrder = 0;
        int rowIndex = 0;
        for (SiteChallengeLineDto d : rows) {
            String head = resolveHeadWithCatalogFallback(d, rowIndex);
            rowIndex++;
            if (head == null || head.isEmpty()) {
                continue;
            }
            SiteChallengeLine e = new SiteChallengeLine();
            e.setSite(site);
            e.setHeadLabel(head.length() > 512 ? head.substring(0, 512) : head);
            e.setLineOrder(d.getLineOrder() != null ? d.getLineOrder() : autoOrder++);
            e.setIncidentDate(d.getIncidentDate());
            if (d.getInvolvedUserId() != null) {
                e.setInvolvedUser(userRepository.findById(d.getInvolvedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", d.getInvolvedUserId())));
            }
            e.setChallengesFaced(d.getChallengesFaced());
            e.setStatus(d.getStatus());
            challengeLineRepository.save(e);
        }
    }

    @Transactional
    public void replaceRegisterCells(Long siteId, List<AttendanceRegisterCellWriteDto> cells) {
        Site site = requireSite(siteId);
        if (cells == null || cells.isEmpty()) {
            return;
        }
        for (AttendanceRegisterCellWriteDto d : cells) {
            if (d.getEmployeeUserId() == null || d.getDate() == null) {
                continue;
            }
            User emp = userRepository.findById(d.getEmployeeUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", d.getEmployeeUserId()));
            attendanceRegisterCellRepository
                .deleteBySite_IdAndEmployee_IdAndCalendarDay(siteId, emp.getId(), d.getDate());
            if (d.getCode() != null) {
                SiteAttendanceRegisterCell c = new SiteAttendanceRegisterCell();
                c.setSite(site);
                c.setEmployee(emp);
                c.setCalendarDay(d.getDate());
                c.setCode(d.getCode());
                attendanceRegisterCellRepository.save(c);
            }
        }
    }

    /** Delete all normalized job-site rows for a site (before deleting the site). */
    @Transactional
    public void deleteAllJobData(Long siteId) {
        siteEquipmentService.deleteAllForSite(siteId);
        attendanceRegisterCellRepository.deleteBySite_Id(siteId);
        advanceExpenseLineRepository.deleteBySite_Id(siteId);
        technicianDailyPaymentRepository.deleteBySite_Id(siteId);
        toolIssueRepository.deleteBySite_Id(siteId);
        behaviourReportRepository.deleteBySite_Id(siteId);
        challengeLineRepository.deleteBySite_Id(siteId);
    }
}
