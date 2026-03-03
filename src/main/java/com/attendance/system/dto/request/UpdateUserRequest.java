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
    
    // Employee Profile Fields
    private String address;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    
    private BloodGroup bloodGroup;
    
    private EmployeeStatus employeeStatus;

    // New Required Employee Profile Fields
    private String fatherName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoining;

    private String officeContactNumber;

    private String homeContactNumber;

    private String otherContactNumber;

    private String identificationMark;

    // Photo and Signature paths (can be set directly or via upload endpoint)
    private String photoPath;
    
    private String specimenSignaturePath;
}
