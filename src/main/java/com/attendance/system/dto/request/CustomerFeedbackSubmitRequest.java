package com.attendance.system.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class CustomerFeedbackSubmitRequest {

    /**
     * Invite token from the public link. Required for
     * {@code POST /api/public/sites/{siteId}/customer-feedback}; ignored when the token is only in the URL path
     * ({@code POST /api/public/feedback/{token}}).
     */
    private String token;

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

    /** Raw extension payload merged server-side into stored JSON. */
    private JsonNode extra;
}
