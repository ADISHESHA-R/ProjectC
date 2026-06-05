package com.attendance.system.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class CustomerFeedbackSubmitRequest {

    /**
     * Invite token from the public link. Optional for
     * {@code POST /api/public/sites/{siteId}/customer-feedback} when the path identifies the site;
     * required when using that endpoint with a token invite. For {@code POST /api/public/feedback/{token}}
     * the path carries the token instead.
     */
    private String token;

    @JsonAlias({"customer_name"})
    private String name;
    @JsonAlias({"customer_email"})
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

    /** Raw extension payload merged server-side into stored JSON. */
    private JsonNode extra;

    /**
     * Snake_case or extra UI keys Jackson does not map to the fields above (still persisted on the site JSON).
     */
    @JsonIgnore
    private final Map<String, JsonNode> rawExtraJsonFields = new LinkedHashMap<>();

    @JsonAnySetter
    public void captureAdditionalJson(String key, JsonNode value) {
        if (value == null || value.isNull() || "token".equals(key)) {
            return;
        }
        rawExtraJsonFields.put(key, value);
    }
}
