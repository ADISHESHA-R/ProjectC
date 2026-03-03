package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponse {
    private UserResponse user;
    private String password; // Plain password - only returned once during creation
    private String email; // Email for easy reference
}
