package com.attendance.system.controller;

import com.attendance.system.dto.request.UpdateUserRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.UserResponse;
import com.attendance.system.service.FileStorageService;
import com.attendance.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    private final FileStorageService fileStorageService;
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get logged-in user details")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Update logged-in user's own profile details")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        
        // Remove admin-only fields from request for regular users
        UpdateUserRequest userRequest = sanitizeUserUpdateRequest(request, false);
        
        UserResponse user = userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
    }
    
    @PostMapping("/me/photo")
    @Operation(summary = "Upload profile photo", description = "Upload profile photo for current user")
    public ResponseEntity<ApiResponse<UserResponse>> uploadPhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) throws IOException {
        Long userId = (Long) authentication.getPrincipal();
        
        // Delete old photo if exists
        com.attendance.system.entity.User user = userService.getUserEntityById(userId);
        if (user.getPhotoPath() != null) {
            try {
                fileStorageService.deleteFile(user.getPhotoPath());
            } catch (Exception e) {
                // Log but don't fail if old file doesn't exist
            }
        }
        
        String photoPath = fileStorageService.storePhoto(file, userId);
        
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhotoPath(photoPath);
        UserResponse updatedUser = userService.updateUser(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", updatedUser));
    }
    
    @PostMapping("/me/signature")
    @Operation(summary = "Upload signature", description = "Upload specimen signature for current user")
    public ResponseEntity<ApiResponse<UserResponse>> uploadSignature(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) throws IOException {
        Long userId = (Long) authentication.getPrincipal();
        
        // Delete old signature if exists
        com.attendance.system.entity.User user = userService.getUserEntityById(userId);
        if (user.getSpecimenSignaturePath() != null) {
            try {
                fileStorageService.deleteFile(user.getSpecimenSignaturePath());
            } catch (Exception e) {
                // Log but don't fail if old file doesn't exist
            }
        }
        
        String signaturePath = fileStorageService.storeSignature(file, userId);
        
        UpdateUserRequest request = new UpdateUserRequest();
        request.setSpecimenSignaturePath(signaturePath);
        UserResponse updatedUser = userService.updateUser(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Signature uploaded successfully", updatedUser));
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Get paginated list of all users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user details (users can only update their own profile, admin has full control)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        String currentUserRole = authentication.getAuthorities().stream()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .findFirst()
            .orElse("");
        
        boolean isAdmin = "ADMIN".equals(currentUserRole);
        boolean isOwnProfile = currentUserId.equals(id);
        
        // Check if user is updating their own profile or is admin
        if (!isOwnProfile && !isAdmin) {
            throw new RuntimeException("You can only update your own profile");
        }
        
        // If not admin, remove admin-only fields
        if (!isAdmin) {
            request = sanitizeUserUpdateRequest(request, false);
        }
        // Admin has full control - all fields are allowed
        
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }
    
    private UpdateUserRequest sanitizeUserUpdateRequest(UpdateUserRequest request, boolean isAdmin) {
        if (isAdmin) {
            // Admin has full control - return as is
            return request;
        }
        
        // Regular users cannot change these admin-only fields
        UpdateUserRequest sanitized = new UpdateUserRequest();
        sanitized.setName(request.getName());
        sanitized.setEmail(request.getEmail());
        sanitized.setAddress(request.getAddress());
        sanitized.setDateOfBirth(request.getDateOfBirth());
        sanitized.setBloodGroup(request.getBloodGroup());
        sanitized.setFatherName(request.getFatherName());
        sanitized.setDateOfJoining(request.getDateOfJoining());
        sanitized.setOfficeContactNumber(request.getOfficeContactNumber());
        sanitized.setHomeContactNumber(request.getHomeContactNumber());
        sanitized.setOtherContactNumber(request.getOtherContactNumber());
        sanitized.setIdentificationMark(request.getIdentificationMark());
        sanitized.setPhotoPath(request.getPhotoPath()); // Users can update photo
        sanitized.setSpecimenSignaturePath(request.getSpecimenSignaturePath()); // Users can update signature
        // Excluded: status, employeeStatus, employeeId (admin-only)
        return sanitized;
    }
}
