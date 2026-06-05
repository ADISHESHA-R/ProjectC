package com.attendance.system.dto.response;

import com.attendance.system.enums.CertificateClientStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteResponse {
    private Long id;
    private String name;
    private String jobCode;
    private String address;
    private Boolean isActive;

    private String customerName;
    private Integer estimatedDays;
    private Long inchargeUserId;
    private String inchargeName;
    private String inchargeEmployeeId;
    private Long locationSiteId;
    private String locationSiteLabel;
    @JsonAlias({"projectStartDate"})
    private LocalDate siteStartDate;
    @JsonAlias({"projectEndDate"})
    private LocalDate siteEndDate;
    private Integer totalProjectDays;

    private CertificateClientStatus certificateClientStatus;
    private LocalDateTime customerFeedbackApprovedAt;

    /** Latest valid invite token for public customer feedback (use with {@code ?token=} on the feedback URL). */
    private String customerFeedbackInviteToken;
    private LocalDateTime customerFeedbackInviteExpiresAt;

    /**
     * Raw customer feedback JSON stored on this site row (same as {@code GET .../customer-feedback} {@code feedbackJson}).
     * Omitted when null so site list responses stay smaller.
     * Also emitted as {@code customer_feedback_json} / {@code customer_feedback_payload} for SPA merge helpers.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAlias({"customer_feedback_json", "customerFeedbackPayload", "customer_feedback_payload"})
    private String customerFeedbackJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonAnyGetter
    public Map<String, String> customerFeedbackBlobSnakeCaseAliases() {
        if (customerFeedbackJson == null || customerFeedbackJson.isBlank()) {
            return Collections.emptyMap();
        }
        Map<String, String> m = new LinkedHashMap<>(2);
        m.put("customer_feedback_json", customerFeedbackJson);
        m.put("customer_feedback_payload", customerFeedbackJson);
        return m;
    }
}
