package com.attendance.system.dto.jobsite;

import com.attendance.system.enums.SiteChallengeStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SiteChallengeLineDto {
    /** Row order within the site (optional on PUT — server fills gaps). */
    private Integer lineOrder;
    /** Persisted challenge head / category (required for each saved row). */
    private String headLabel;
    /**
     * Optional: when picking from {@code GET /api/meta/challenge-line-heads}, send 1-based index
     * and leave {@code headLabel} blank to store the catalog label automatically.
     */
    private Integer challengeCatalogIndex;
    private LocalDate incidentDate;
    private Long involvedUserId;
    private String challengesFaced;
    private SiteChallengeStatus status;
}
