package com.attendance.system.dto.jobsite;

import com.attendance.system.enums.SiteChallengeStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SiteChallengeLineDto {
    /** Row order within the site (optional on PUT — server fills gaps). */
    @JsonAlias({"sl", "serial", "order", "rowIndex"})
    private Integer lineOrder;
    /** Persisted challenge head / category (required for each saved row). */
    @JsonAlias({"head", "heads", "headsLabel", "category", "challengeHead", "label", "headName"})
    private String headLabel;
    /**
     * Optional: when picking from {@code GET /api/meta/challenge-line-heads}, send 1-based index
     * and leave {@code headLabel} blank to store the catalog label automatically.
     */
    @JsonAlias({"catalogIndex", "headIndex", "catalogIdx"})
    private Integer challengeCatalogIndex;
    @JsonAlias({"dateOfIncident", "incident_date", "date", "incidentDate"})
    private LocalDate incidentDate;
    /** JSON may use {@code involvedUserId} or {@code involvedEmployeeUserId} (field-level alias for Jackson). */
    @JsonAlias({"involvedEmployeeUserId", "userId", "involvedPersonUserId"})
    private Long involvedUserId;
    @JsonAlias({"challenges", "notes", "description", "challengeDetails"})
    private String challengesFaced;
    private SiteChallengeStatus status;
}
