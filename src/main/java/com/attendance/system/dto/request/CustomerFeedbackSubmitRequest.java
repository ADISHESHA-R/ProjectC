package com.attendance.system.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class CustomerFeedbackSubmitRequest {

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
