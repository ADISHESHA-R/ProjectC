package com.attendance.system.controller;

import com.attendance.system.dto.request.CreateMachineryRequest;
import com.attendance.system.dto.request.SaveUsageSelectionRequest;
import com.attendance.system.dto.request.UpdateMachineryRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.DailyUsageSelectionResponse;
import com.attendance.system.dto.response.MachineryResponse;
import com.attendance.system.dto.response.MonthlyUsageSummaryResponse;
import com.attendance.system.dto.response.YearlyUsageSummaryResponse;
import com.attendance.system.service.MachineryService;
import com.attendance.system.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/machinery")
@RequiredArgsConstructor
@Tag(name = "Admin Machinery", description = "Machinery catalog and daily usage (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class MachineryController {

    private final MachineryService machineryService;
    private final SiteService siteService;

    @GetMapping
    @Operation(summary = "List machinery catalog for a site")
    public ResponseEntity<ApiResponse<List<MachineryResponse>>> listForSite(@RequestParam String siteId) {
        long sid = siteService.resolveSiteIdFromClientKey(siteId);
        return ResponseEntity.ok(ApiResponse.success(machineryService.listMachineryForSite(sid)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create machinery (optional image part \"image\")")
    public ResponseEntity<ApiResponse<MachineryResponse>> create(
            @Valid @RequestPart("data") CreateMachineryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        MachineryResponse created = machineryService.createMachinery(request, image);
        return ResponseEntity.ok(ApiResponse.success("Machinery created", created));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update machinery (optional image part \"image\")")
    public ResponseEntity<ApiResponse<MachineryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("data") UpdateMachineryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        MachineryResponse updated = machineryService.updateMachinery(id, request, image);
        return ResponseEntity.ok(ApiResponse.success("Machinery updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete machinery (only if no usage history)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        machineryService.deleteMachinery(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/usage/selection")
    @Operation(summary = "Daily view: catalog for site with qty/uom per machine for the date")
    public ResponseEntity<ApiResponse<DailyUsageSelectionResponse>> getUsageSelection(
            @RequestParam String siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        long sid = siteService.resolveSiteIdFromClientKey(siteId);
        return ResponseEntity.ok(ApiResponse.success(machineryService.getDailySelection(sid, date)));
    }

    @PutMapping("/usage/selection")
    @Operation(summary = "Replace usage lines for site + date (omit or zero qty to clear a machine for that day)")
    public ResponseEntity<ApiResponse<Void>> saveUsageSelection(@Valid @RequestBody SaveUsageSelectionRequest request) {
        machineryService.saveUsageSelection(request);
        return ResponseEntity.ok(ApiResponse.success("Usage saved", null));
    }

    @GetMapping("/usage/summary/month")
    @Operation(summary = "Monthly overview: days with usage counts and machine codes")
    public ResponseEntity<ApiResponse<MonthlyUsageSummaryResponse>> monthlySummary(
            @RequestParam String siteId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        long sid = siteService.resolveSiteIdFromClientKey(siteId);
        return ResponseEntity.ok(ApiResponse.success(machineryService.getMonthlySummary(sid, year, month)));
    }

    @GetMapping("/usage/summary/year")
    @Operation(summary = "Yearly overview: per-month aggregates")
    public ResponseEntity<ApiResponse<YearlyUsageSummaryResponse>> yearlySummary(
            @RequestParam String siteId,
            @RequestParam int year
    ) {
        long sid = siteService.resolveSiteIdFromClientKey(siteId);
        return ResponseEntity.ok(ApiResponse.success(machineryService.getYearlySummary(sid, year)));
    }
}
