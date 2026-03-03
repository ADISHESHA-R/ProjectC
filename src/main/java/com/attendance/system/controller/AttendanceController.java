package com.attendance.system.controller;

import com.attendance.system.dto.request.MarkAttendanceRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.AttendanceCalendarResponse;
import com.attendance.system.dto.response.AttendanceResponse;
import com.attendance.system.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @PostMapping("/mark")
    @Operation(summary = "Mark attendance", description = "Mark attendance with photo (Employee only)")
    public ResponseEntity<ApiResponse<AttendanceResponse>> markAttendance(
            @Valid @ModelAttribute MarkAttendanceRequest request,
            Authentication authentication) {
        Long employeeId = (Long) authentication.getPrincipal();
        AttendanceResponse attendance = attendanceService.markAttendance(employeeId, request);
        return ResponseEntity.ok(ApiResponse.success("Attendance marked successfully", attendance));
    }
    
    @GetMapping("/my-attendance")
    @Operation(summary = "Get my attendance", description = "Get logged-in employee's attendance history")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getMyAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long employeeId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> attendance = attendanceService.getEmployeeAttendance(employeeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get attendance by ID", description = "Get attendance details by ID (employees can only view their own)")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(
            @PathVariable Long id,
            Authentication authentication) {
        AttendanceResponse attendance = attendanceService.getAttendanceById(id);
        
        // Check if user is viewing their own attendance or is admin
        Long currentUserId = (Long) authentication.getPrincipal();
        String currentUserRole = authentication.getAuthorities().stream()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .findFirst()
            .orElse("");
        
        boolean isAdmin = "ADMIN".equals(currentUserRole);
        boolean isOwnAttendance = attendance.getEmployee().getId().equals(currentUserId);
        
        if (!isOwnAttendance && !isAdmin) {
            throw new RuntimeException("You can only view your own attendance records");
        }
        
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/my-attendance/date")
    @Operation(summary = "Get my attendance by date", 
              description = "Get logged-in employee's attendance for a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getMyAttendanceByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        Long employeeId = (Long) authentication.getPrincipal();
        List<AttendanceResponse> attendance = attendanceService.getEmployeeAttendanceByDate(employeeId, date);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/my-attendance/range")
    @Operation(summary = "Get my attendance by date range", 
              description = "Get logged-in employee's attendance between start and end date")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getMyAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long siteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long employeeId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> attendance = attendanceService.getEmployeeAttendanceByDateRange(
            employeeId, startDate, endDate, siteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/my-attendance/calendar")
    @Operation(summary = "Get my attendance calendar", 
              description = "Get calendar view showing all dates when employee attended")
    public ResponseEntity<ApiResponse<AttendanceCalendarResponse>> getMyAttendanceCalendar(
            Authentication authentication) {
        Long employeeId = (Long) authentication.getPrincipal();
        AttendanceCalendarResponse calendar = attendanceService.getEmployeeAttendanceCalendar(employeeId);
        return ResponseEntity.ok(ApiResponse.success(calendar));
    }
    
    @GetMapping("/my-attendance/summary")
    @Operation(summary = "Get my attendance summary by month", 
              description = "Get attendance summary for a specific month")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyAttendanceSummary(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        Long employeeId = (Long) authentication.getPrincipal();
        Map<String, Object> summary = attendanceService.getEmployeeAttendanceSummary(employeeId, year, month);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
