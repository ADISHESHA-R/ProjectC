package com.attendance.system.controller;

import com.attendance.system.dto.request.CustomerFeedbackSubmitRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.service.CustomerFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/sites")
@RequiredArgsConstructor
@Tag(name = "Public customer feedback (by site)", description = "No authentication — invite token in JSON body must match the site id in the path.")
public class PublicSiteCustomerFeedbackController {

    private final CustomerFeedbackService customerFeedbackService;

    @PostMapping("/{siteId}/customer-feedback")
    @Operation(
        summary = "Submit customer feedback (SPA path)",
        description = "Same persistence as POST /api/public/feedback/{token}. Body must include \"token\" (opaque invite); it must belong to siteId."
    )
    public ResponseEntity<ApiResponse<Void>> submit(
        @PathVariable Long siteId,
        @Valid @RequestBody CustomerFeedbackSubmitRequest body) {
        customerFeedbackService.submitFeedbackForSite(siteId, body);
        return ResponseEntity.ok(ApiResponse.success("Feedback saved", null));
    }
}
