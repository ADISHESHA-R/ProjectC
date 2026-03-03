package com.attendance.system.controller;

import com.attendance.system.dto.request.ApproveAttendanceRequest;
import com.attendance.system.dto.request.CreateSiteRequest;
import com.attendance.system.dto.request.CreateUserRequest;
import com.attendance.system.dto.request.UpdateSiteRequest;
import com.attendance.system.dto.request.UpdateUserRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.AttendanceResponse;
import com.attendance.system.dto.response.DashboardResponse;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.dto.response.UserResponse;
import com.attendance.system.enums.AttendanceStatus;
import com.attendance.system.enums.EmployeeStatus;
import com.attendance.system.entity.User;
import com.attendance.system.repository.UserRepository;
import com.attendance.system.service.AttendanceService;
import com.attendance.system.service.DashboardService;
import com.attendance.system.service.FileStorageService;
import com.attendance.system.service.SiteService;
import com.attendance.system.service.UserService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs - Full Access (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    
    private final UserService userService;
    private final AttendanceService attendanceService;
    private final DashboardService dashboardService;
    private final SiteService siteService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    
    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard", description = "Get dashboard statistics with site-wise data (Admin only)")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
    
    // ==================== USER MANAGEMENT (FULL CRUD) ====================
    @PostMapping("/users")
    @Operation(summary = "Create user", description = "Create new user (employee or admin) (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", user));
    }
    
    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Get paginated list of all users (Admin only)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update user details (name, email, status) (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }
    
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Delete user by ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
    @PostMapping("/users/{id}/reset-password")
    @Operation(summary = "Reset password", description = "Reset user password (Admin only)")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @RequestBody String newPassword) {
        userService.resetPassword(id, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }
    
    @GetMapping("/users/employees")
    @Operation(summary = "Get all employees", description = "Get list of all employees (Admin only)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllEmployees()));
    }
    
    @PutMapping("/users/{id}/activate")
    @Operation(summary = "Activate user", description = "Activate user account (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Long id) {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setStatus(com.attendance.system.enums.UserStatus.ACTIVE);
        request.setEmployeeStatus(EmployeeStatus.ACTIVE);
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", user));
    }
    
    @PutMapping("/users/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate user account (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Long id) {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setStatus(com.attendance.system.enums.UserStatus.INACTIVE);
        request.setEmployeeStatus(EmployeeStatus.SUSPENDED);
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", user));
    }
    
    @PostMapping("/users/{id}/document")
    @Operation(summary = "Upload employee document", description = "Upload valid document for employee (PDF, JPG, PNG - Max 5MB) (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.getUserEntityById(id);
        String documentPath = fileStorageService.storeDocument(file, id);
        
        user.setValidDocumentPath(documentPath);
        User updatedUser = userRepository.save(user);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Document uploaded successfully", 
            userService.getUserById(updatedUser.getId())
        ));
    }
    
    // ==================== SITE MANAGEMENT (FULL CRUD) ====================
    @PostMapping("/sites")
    @Operation(summary = "Create site", description = "Create new site with unique job code (Admin only)")
    public ResponseEntity<ApiResponse<SiteResponse>> createSite(@Valid @RequestBody CreateSiteRequest request) {
        SiteResponse site = siteService.createSite(request);
        return ResponseEntity.ok(ApiResponse.success("Site created successfully", site));
    }
    
    @GetMapping("/sites")
    @Operation(summary = "Get all sites", description = "Get list of all sites including inactive (Admin only)")
    public ResponseEntity<ApiResponse<List<SiteResponse>>> getAllSites() {
        return ResponseEntity.ok(ApiResponse.success(siteService.getAllSites()));
    }
    
    @GetMapping("/sites/{id}")
    @Operation(summary = "Get site by ID", description = "Get site details by ID (Admin only)")
    public ResponseEntity<ApiResponse<SiteResponse>> getSiteById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(siteService.getSiteById(id)));
    }
    
    @PutMapping("/sites/{id}")
    @Operation(summary = "Update site", description = "Update site details (name, job code, address, status) (Admin only)")
    public ResponseEntity<ApiResponse<SiteResponse>> updateSite(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSiteRequest request) {
        SiteResponse site = siteService.updateSite(id, request);
        return ResponseEntity.ok(ApiResponse.success("Site updated successfully", site));
    }
    
    @DeleteMapping("/sites/{id}")
    @Operation(summary = "Delete site", description = "Delete site by ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteSite(@PathVariable Long id) {
        siteService.deleteSite(id);
        return ResponseEntity.ok(ApiResponse.success("Site deleted successfully", null));
    }
    
    @PutMapping("/sites/{id}/activate")
    @Operation(summary = "Activate site", description = "Activate site (Admin only)")
    public ResponseEntity<ApiResponse<SiteResponse>> activateSite(@PathVariable Long id) {
        UpdateSiteRequest request = new UpdateSiteRequest();
        request.setIsActive(true);
        SiteResponse site = siteService.updateSite(id, request);
        return ResponseEntity.ok(ApiResponse.success("Site activated successfully", site));
    }
    
    @PutMapping("/sites/{id}/deactivate")
    @Operation(summary = "Deactivate site", description = "Deactivate site (Admin only)")
    public ResponseEntity<ApiResponse<SiteResponse>> deactivateSite(@PathVariable Long id) {
        UpdateSiteRequest request = new UpdateSiteRequest();
        request.setIsActive(false);
        SiteResponse site = siteService.updateSite(id, request);
        return ResponseEntity.ok(ApiResponse.success("Site deactivated successfully", site));
    }
    
    // ==================== ATTENDANCE MANAGEMENT (FULL CRUD) ====================
    @GetMapping("/attendance")
    @Operation(summary = "Get all attendance", description = "Get all attendance with filters (date, employee, site, status) (Admin only)")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> attendance = attendanceService.getAllAttendance(date, employeeId, siteId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/attendance/{id}")
    @Operation(summary = "Get attendance by ID", description = "Get attendance details by ID (Admin only)")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(@PathVariable Long id) {
        AttendanceResponse attendance = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/attendance/employee/{employeeId}")
    @Operation(summary = "Get employee attendance", description = "Get all attendance records for a specific employee (Admin only)")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getEmployeeAttendance(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> attendance = attendanceService.getEmployeeAttendance(employeeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/attendance/site/{siteId}")
    @Operation(summary = "Get site attendance", description = "Get all attendance records for a specific site (Admin only)")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getSiteAttendance(
            @PathVariable Long siteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> attendance = attendanceService.getSiteAttendance(siteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @PutMapping("/attendance/{id}/approve")
    @Operation(summary = "Approve/Reject attendance", description = "Approve or reject attendance with optional reason (Admin only)")
    public ResponseEntity<ApiResponse<AttendanceResponse>> approveAttendance(
            @PathVariable Long id,
            @Valid @RequestBody ApproveAttendanceRequest request) {
        AttendanceResponse attendance = attendanceService.approveAttendance(id, request);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", attendance));
    }
    
    @DeleteMapping("/attendance/{id}")
    @Operation(summary = "Delete attendance", description = "Delete attendance record by ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance deleted successfully", null));
    }
}
