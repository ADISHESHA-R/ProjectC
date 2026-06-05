package com.attendance.system.controller;

import com.attendance.system.dto.request.CustomerFeedbackSubmitRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.PublicFeedbackContextResponse;
import com.attendance.system.service.CustomerFeedbackService;
import com.attendance.system.web.AdminSitePathId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/sites")
@RequiredArgsConstructor
@Tag(name = "Public customer feedback (by site)", description = "No authentication. Optional invite token in JSON when using POST; GET context and tokenless POST use active site id (or job code / slug) in the path.")
public class PublicSiteCustomerFeedbackController {

    private final CustomerFeedbackService customerFeedbackService;

    @GetMapping("/{siteId}/customer-feedback")
    @Operation(
        summary = "Public feedback page context (no token)",
        description = "Job code, customer name, site name, and certificate status for the public form at /customer-feedback/{siteId}. Active sites only."
    )
    public ResponseEntity<ApiResponse<PublicFeedbackContextResponse>> contextBySite(
        @AdminSitePathId Long siteId) {
        return ResponseEntity.ok(ApiResponse.success(customerFeedbackService.getPublicContextForSite(siteId)));
    }

    @PostMapping("/{siteId}/customer-feedback")
    @Operation(
        summary = "Submit customer feedback (SPA path)",
        description = "If body.token is set, it must match a valid invite for siteId. If token is omitted, saves feedback for the active site (same DB fields as token flow) so admin completion/feedback views update."
    )
    public ResponseEntity<ApiResponse<Void>> submit(
        @AdminSitePathId Long siteId,
        @Valid @RequestBody CustomerFeedbackSubmitRequest body) {
        customerFeedbackService.submitFeedbackForSite(siteId, body);
        return ResponseEntity.ok(ApiResponse.success("Feedback saved", null));
    }
}
