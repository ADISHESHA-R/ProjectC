package com.attendance.system.controller;

import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.NoticeResponse;
import com.attendance.system.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "Notices", description = "View notices (all authenticated users)")
@SecurityRequirement(name = "Bearer Authentication")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @Operation(summary = "Get all notices", description = "Get list of all notices for employees (authenticated)")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getAllNotices() {
        List<NoticeResponse> notices = noticeService.getAll();
        return ResponseEntity.ok(ApiResponse.success(notices));
    }
}
