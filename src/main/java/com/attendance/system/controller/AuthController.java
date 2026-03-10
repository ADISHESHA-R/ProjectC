package com.attendance.system.controller;

import com.attendance.system.dto.request.LoginRequest;
import com.attendance.system.dto.request.RefreshTokenRequest;
import com.attendance.system.dto.response.ApiResponse;
import com.attendance.system.dto.response.AuthResponse;
import com.attendance.system.enums.Role;
import com.attendance.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {
    
    private final AuthService authService;
    
    // Single login (any role) - commented out; use /admin/login or /employee/login instead
    // @PostMapping("/login")
    // @Operation(summary = "User login", description = "Login with email and password (any role)")
    // public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
    //     AuthResponse response = authService.login(request);
    //     return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    // }
    
    @PostMapping("/admin/login")
    @Operation(summary = "Admin login", description = "Login for admin only. Returns 400 if user is not ADMIN.")
    public ResponseEntity<ApiResponse<AuthResponse>> adminLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginAsRole(request, Role.ADMIN);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @PostMapping("/employee/login")
    @Operation(summary = "Employee login", description = "Login for employee only. Returns 400 if user is not EMPLOYEE.")
    public ResponseEntity<ApiResponse<AuthResponse>> employeeLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginAsRole(request, Role.EMPLOYEE);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout and revoke refresh token. Accepts refresh token in request body or Authorization header.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody(required = false) RefreshTokenRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String refreshToken = null;
        
        // Try to get refresh token from request body first
        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isEmpty()) {
            refreshToken = request.getRefreshToken();
        } 
        // Fallback to Authorization header if body is empty
        else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring(7);
        }
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is required in request body or Authorization header");
        }
        
        authService.logout(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    } 
    
    @GetMapping("/home")
    @Operation(summary = "Home / status", description = "Avoid cold start by returning a simple message")
    public ResponseEntity<ApiResponse<String>> home() {
        return ResponseEntity.ok(ApiResponse.success("Restart...", "Avoid cold start by returning a simple message"));
    }
}
