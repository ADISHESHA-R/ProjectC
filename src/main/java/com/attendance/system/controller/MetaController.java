package com.attendance.system.controller;

import com.attendance.system.dto.jobsite.ChallengeHeadResponse;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.DesignationResponse;
import com.attendance.system.service.DesignationService;
import com.attendance.system.service.SiteJobDataService;
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
@RequestMapping("/api/meta")
@RequiredArgsConstructor
@Tag(name = "Meta", description = "Shared lookup data for authenticated users")
@SecurityRequirement(name = "Bearer Authentication")
public class MetaController {

    private final DesignationService designationService;
    private final SiteJobDataService siteJobDataService;

    @GetMapping("/designations")
    @Operation(summary = "List active designations", description = "Job-title / designation dropdown options (separate from security roles).")
    public ResponseEntity<ApiResponse<List<DesignationResponse>>> designations() {
        return ResponseEntity.ok(ApiResponse.success(designationService.listActive()));
    }

    @GetMapping("/challenge-line-heads")
    @Operation(summary = "Preset challenge head labels", description = "Optional dropdown suggestions for Screen 9; you may add any number of rows with custom headLabel text.")
    public ResponseEntity<ApiResponse<List<ChallengeHeadResponse>>> challengeLineHeads() {
        return ResponseEntity.ok(ApiResponse.success(siteJobDataService.getChallengeHeadCatalog()));
    }
}
