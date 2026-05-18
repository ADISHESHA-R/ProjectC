package com.attendance.system.controller;

import com.attendance.system.dto.request.CustomerFeedbackSubmitRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.PublicFeedbackContextResponse;
import com.attendance.system.service.CustomerFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/feedback")
@RequiredArgsConstructor
@Tag(name = "Public customer feedback", description = "No authentication — token identifies the job invite.")
public class PublicFeedbackController {

    private final CustomerFeedbackService customerFeedbackService;

    @GetMapping("/{token}")
    @Operation(summary = "Public context", description = "Job code and certificate state for the invite token.")
    public ResponseEntity<ApiResponse<PublicFeedbackContextResponse>> context(@PathVariable String token) {
        return ResponseEntity.ok(ApiResponse.success(customerFeedbackService.getPublicContext(token)));
    }

    @PostMapping("/{token}")
    @Operation(summary = "Submit customer feedback", description = "Stores JSON payload on the site record.")
    public ResponseEntity<ApiResponse<Void>> submit(
        @PathVariable String token,
        @Valid @RequestBody CustomerFeedbackSubmitRequest body) {
        customerFeedbackService.submitFeedback(token, body);
        return ResponseEntity.ok(ApiResponse.success("Feedback saved", null));
    }

    @PostMapping("/{token}/approve")
    @Operation(summary = "Approve certificate", description = "Marks APPROVED_BY_CLIENT and returns PDF bytes.")
    public ResponseEntity<byte[]> approve(@PathVariable String token) {
        byte[] pdf = customerFeedbackService.approveAndBuildPdf(token);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"work-completion-certificate.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
