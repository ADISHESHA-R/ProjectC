package com.attendance.system.dto.response;

import com.attendance.system.enums.AttendanceStatus;
import com.attendance.system.enums.Shift;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private UserResponse employee;
    private SiteResponse site;
    private LocalDate date;
    private LocalTime time;
    private String photoPath;
    private AttendanceStatus status;
    private String rejectionReason;
    private Shift shift;
    private LocalDateTime createdAt;
}
