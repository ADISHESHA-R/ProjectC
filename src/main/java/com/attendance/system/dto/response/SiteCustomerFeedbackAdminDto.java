package com.attendance.system.dto.response;

import com.attendance.system.enums.CertificateClientStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Admin completion / feedback step: certificate meta plus stored customer answers.
 * {@link #feedbackJson} is the raw blob; other fields are parsed copies for UIs that read flat {@code data.*}.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteCustomerFeedbackAdminDto {

    private CertificateClientStatus certificateClientStatus;
    private LocalDateTime customerFeedbackApprovedAt;
    /** Opaque token for public link: {@code /customer-feedback/{siteId}?token=...} */
    private String customerFeedbackInviteToken;
    private LocalDateTime customerFeedbackInviteExpiresAt;

    /** Raw JSON string stored on the site (same as public POST payload shape). */
    private String feedbackJson;

    // --- Parsed from feedbackJson for convenience (nulls when not submitted / missing keys) ---

    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String productQuality;
    private String customerService;
    private String machiningQuality;
    private String pricing;
    private String shippingDelivery;
    private String otherCategoryNote;
    private String specificFeedback;
    private String suggestions;
    private Integer likelihoodRecommend;
    private String additionalComments;
    private JsonNode extra;
}
