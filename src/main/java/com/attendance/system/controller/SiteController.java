package com.attendance.system.controller;

import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.service.SiteService;
import com.attendance.system.web.AdminSitePathId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
@Tag(name = "Sites", description = "Site information APIs (Read-only)")
@SecurityRequirement(name = "Bearer Authentication")
public class SiteController {
    
    private final SiteService siteService;
    
    @GetMapping
    @Operation(summary = "Get all sites", description = "Get list of all sites (All authenticated users)")
    public ResponseEntity<ApiResponse<List<SiteResponse>>> getAllSites() {
        return ResponseEntity.ok(ApiResponse.success(siteService.getAllSites()));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active sites", description = "Get list of active sites only (All authenticated users)")
    public ResponseEntity<ApiResponse<List<SiteResponse>>> getActiveSites() {
        return ResponseEntity.ok(ApiResponse.success(siteService.getActiveSites()));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get site by ID", description = "Get site details by ID (All authenticated users)")
    public ResponseEntity<ApiResponse<SiteResponse>> getSiteById(@AdminSitePathId Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteService.getSiteById(id)));
    }
    
    @GetMapping("/job-code/{jobCode}")
    @Operation(summary = "Get site by job code", description = "Get site details by job code (All authenticated users)")
    public ResponseEntity<ApiResponse<SiteResponse>> getSiteByJobCode(@PathVariable String jobCode) {
        return ResponseEntity.ok(ApiResponse.success(siteService.getSiteByJobCode(jobCode)));
    }
}
