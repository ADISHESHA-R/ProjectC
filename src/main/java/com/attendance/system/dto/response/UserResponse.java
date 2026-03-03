package com.attendance.system.dto.response;

import com.attendance.system.enums.BloodGroup;
import com.attendance.system.enums.EmployeeStatus;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
    
    // Employee Profile Fields
    private String address; // Residential Address
    private LocalDate dateOfBirth;
    private BloodGroup bloodGroup;
    private String validDocumentPath;
    private EmployeeStatus employeeStatus;
    
    // New Required Employee Profile Fields
    private String fatherName;
    private LocalDate dateOfJoining;
    private String officeContactNumber;
    private String homeContactNumber;
    private String otherContactNumber;
    private String identificationMark;
    private String specimenSignaturePath;
    private String photoPath;
    
    private LocalDateTime createdAt;
}
