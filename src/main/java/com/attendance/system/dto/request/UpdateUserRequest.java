package com.attendance.system.dto.request;

import com.attendance.system.enums.BloodGroup;
import com.attendance.system.enums.EmployeeStatus;
import com.attendance.system.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private String employeeId;
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private UserStatus status;
    
    // New Employee Fields
    private String address;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    
    private BloodGroup bloodGroup;
    
    private EmployeeStatus employeeStatus;
}
