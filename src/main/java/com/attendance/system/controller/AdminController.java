package com.attendance.system.controller;

import com.attendance.system.dto.request.ApproveAttendanceRequest;
import com.attendance.system.dto.request.UpdateAttendanceShiftRequest;
import com.attendance.system.dto.request.CreateNoticeRequest;
import com.attendance.system.dto.request.CreateSiteRequest;
import com.attendance.system.dto.request.CreateUserRequest;
import com.attendance.system.dto.request.UpdateNoticeRequest;
import com.attendance.system.dto.request.UpdateSiteRequest;
import com.attendance.system.dto.request.UpdateUserRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.AttendanceResponse;
import com.attendance.system.dto.response.CreateUserResponse;
import com.attendance.system.dto.response.DashboardResponse;
import com.attendance.system.dto.response.NoticeResponse;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.dto.response.UserResponse;
import com.attendance.system.enums.AttendanceStatus;
import com.attendance.system.enums.EmployeeStatus;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.UserStatus;
import com.attendance.system.entity.User;
import com.attendance.system.repository.UserRepository;
import com.attendance.system.service.AttendanceService;
import com.attendance.system.service.DashboardService;
import com.attendance.system.service.FileStorageService;
import com.attendance.system.service.NoticeService;
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
import org.springframework.data.domain.Sort;
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
    private final NoticeService noticeService;
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
    @Operation(summary = "Create user", description = "Create new user (employee or admin) (Admin only). Password is returned in response for admin to share with employee.")
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("Employee created successfully. Please share these credentials with the employee:", response));
    }
    
    @GetMapping("/users")
    @Operation(summary = "Get users (search & filter)", description = "Paginated users. Optional: search (name, email, employeeId), role, status (Admin only)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<UserResponse> users = userService.searchUsers(search, role, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/users/by-employee-id/{employeeId}")
    @Operation(summary = "Get user by employee ID", description = "Get user details by employee ID e.g. ADMIN001, EMP001 (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmployeeId(@PathVariable String employeeId) {
        UserResponse user = userService.getUserByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details by numeric ID (Admin only)")
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
    
    @PostMapping("/users/{id}/photo")
    @Operation(summary = "Upload employee photo", description = "Upload profile photo for employee (JPG, PNG - Max 3MB) (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.getUserEntityById(id);
        
        // Delete old photo if exists
        if (user.getPhotoPath() != null) {
            try {
                fileStorageService.deleteFile(user.getPhotoPath());
            } catch (Exception e) {
                // Log but don't fail if old file doesn't exist
            }
        }
        
        String photoPath = fileStorageService.storePhoto(file, id);
        user.setPhotoPath(photoPath);
        User updatedUser = userRepository.save(user);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Photo uploaded successfully", 
            userService.getUserById(updatedUser.getId())
        ));
    }
    
    @PostMapping("/users/{id}/signature")
    @Operation(summary = "Upload employee signature", description = "Upload specimen signature for employee (JPG, PNG - Max 2MB) (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> uploadSignature(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.getUserEntityById(id);
        
        // Delete old signature if exists
        if (user.getSpecimenSignaturePath() != null) {
            try {
                fileStorageService.deleteFile(user.getSpecimenSignaturePath());
            } catch (Exception e) {
                // Log but don't fail if old file doesn't exist
            }
        }
        
        String signaturePath = fileStorageService.storeSignature(file, id);
        user.setSpecimenSignaturePath(signaturePath);
        User updatedUser = userRepository.save(user);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Signature uploaded successfully", 
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
    @Operation(summary = "Get sites (search & filter)", description = "Sites as a JSON array in data (up to 10k). Optional: search, isActive (Admin only).")
    public ResponseEntity<ApiResponse<List<SiteResponse>>> getAllSites(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive) {
        List<SiteResponse> sites = siteService.listSitesForAdmin(search, isActive);
        return ResponseEntity.ok(ApiResponse.success(sites));
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
    @Operation(summary = "Get all attendance", description = "Get all attendance with filters (date, employee, site, job code, status) (Admin only)")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) String jobCode,
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> attendance = attendanceService.getAllAttendance(date, employeeId, siteId, jobCode, status, pageable);
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
    
    @PutMapping("/attendance/{id}/shift")
    @Operation(summary = "Update attendance shift", description = "Update shift (FIRST_HALF, SECOND_HALF, FULL_DAY) for an attendance entry (Admin only)")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendanceShift(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAttendanceShiftRequest request) {
        AttendanceResponse attendance = attendanceService.updateAttendanceShift(id, request);
        return ResponseEntity.ok(ApiResponse.success("Shift updated successfully", attendance));
    }
    
    @DeleteMapping("/attendance/{id}")
    @Operation(summary = "Delete attendance", description = "Delete attendance record by ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance deleted successfully", null));
    }

    // ==================== NOTICE MANAGEMENT (ADMIN CRUD) ====================
    @PostMapping("/notices")
    @Operation(summary = "Create notice", description = "Create new notice message for employees (Admin only)")
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(@Valid @RequestBody CreateNoticeRequest request) {
        NoticeResponse notice = noticeService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Notice created successfully", notice));
    }

    @GetMapping("/notices")
    @Operation(summary = "Get notices (search)", description = "Notices as a JSON array in data (up to 10k), newest first. Optional: search (Admin only).")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getAllNotices(
            @RequestParam(required = false) String search) {
        List<NoticeResponse> notices = noticeService.listNoticesForAdmin(search);
        return ResponseEntity.ok(ApiResponse.success(notices));
    }

    @GetMapping("/notices/{id}")
    @Operation(summary = "Get notice by ID", description = "Get notice details by ID (Admin only)")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNoticeById(@PathVariable Long id) {
        NoticeResponse notice = noticeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(notice));
    }

    @PutMapping("/notices/{id}")
    @Operation(summary = "Update notice", description = "Update notice message (Admin only)")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoticeRequest request) {
        NoticeResponse notice = noticeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Notice updated successfully", notice));
    }

    @DeleteMapping("/notices/{id}")
    @Operation(summary = "Delete notice", description = "Delete notice by ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Notice deleted successfully", null));
    }
}
