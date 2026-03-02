package com.attendance.system.dto.request;

import com.attendance.system.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveAttendanceRequest {
    @NotNull(message = "Status is required")
    private AttendanceStatus status;
    
    private String rejectionReason;
}
