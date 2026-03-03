package com.attendance.system.entity;

import com.attendance.system.enums.BloodGroup;
import com.attendance.system.enums.EmployeeStatus;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, unique = true, length = 50)
    private String employeeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // New Employee Fields
    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", length = 20)
    private BloodGroup bloodGroup;

    @Column(name = "valid_document_path")
    private String validDocumentPath; // Path to uploaded document file

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_status")
    private EmployeeStatus employeeStatus = EmployeeStatus.ACTIVE;

    // New Required Employee Profile Fields
    @Column(name = "father_name", length = 255)
    private String fatherName;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "office_contact_number", length = 20)
    private String officeContactNumber;

    @Column(name = "home_contact_number", length = 20)
    private String homeContactNumber;

    @Column(name = "other_contact_number", length = 20)
    private String otherContactNumber;

    @Column(name = "identification_mark", length = 500)
    private String identificationMark;

    @Column(name = "specimen_signature_path")
    private String specimenSignaturePath; // Path to signature file

    @Column(name = "photo_path")
    private String photoPath; // Path to photo file

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
