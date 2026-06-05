package com.attendance.system.dto.response;

import com.attendance.system.enums.CertificateClientStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Admin completion / feedback step: certificate meta plus stored customer answers.
 * {@link #feedbackJson} is the raw blob; other fields are parsed copies for UIs that read flat {@code data.*}.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteCustomerFeedbackAdminDto {

    /** Confirms which site row this payload belongs to (same as path id / job). */
    @JsonAlias({"site_id"})
    private Long siteId;
    @JsonAlias({"job_code"})
    private String jobCode;

    @JsonAlias({"certificate_client_status"})
    private CertificateClientStatus certificateClientStatus;
    @JsonAlias({"customer_feedback_approved_at"})
    private LocalDateTime customerFeedbackApprovedAt;
    /** Opaque token for public link: {@code /customer-feedback/{siteId}?token=...} */
    @JsonAlias({"customer_feedback_invite_token"})
    private String customerFeedbackInviteToken;
    @JsonAlias({"customer_feedback_invite_expires_at"})
    private LocalDateTime customerFeedbackInviteExpiresAt;

    /**
     * Raw JSON string stored on the site (same as public POST payload shape).
     * Also emitted as {@code feedback_json} for clients that only read snake_case keys.
     */
    @JsonAlias({"feedback_json"})
    private String feedbackJson;

    // --- Parsed from feedbackJson for convenience (nulls when not submitted / missing keys) ---

    @JsonAlias({"customer_name"})
    private String name;
    @JsonAlias({"customer_email", "e_mail"})
    private String email;
    @JsonAlias({"phone_number"})
    private String phone;
    @JsonAlias({"company_name"})
    private String companyName;
    @JsonAlias({"product_quality"})
    private String productQuality;
    @JsonAlias({"customer_service"})
    private String customerService;
    @JsonAlias({"machining_quality"})
    private String machiningQuality;
    private String pricing;
    @JsonAlias({"shipping_delivery"})
    private String shippingDelivery;
    @JsonAlias({"other_category_note"})
    private String otherCategoryNote;
    @JsonAlias({"specific_feedback", "feedback_text"})
    private String specificFeedback;
    private String suggestions;
    @JsonAlias({"likelihood_recommend", "likelihood_to_recommend", "nps"})
    private Integer likelihoodRecommend;
    @JsonAlias({"additional_comments"})
    private String additionalComments;
    private JsonNode extra;

    @JsonAnyGetter
    public Map<String, String> feedbackJsonSnakeCaseAlias() {
        if (feedbackJson == null || feedbackJson.isBlank()) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap("feedback_json", feedbackJson);
    }
}
