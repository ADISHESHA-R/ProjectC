package com.attendance.system.dto.request;

import com.attendance.system.enums.Shift;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAttendanceShiftRequest {
    @NotNull(message = "Shift is required")
    private Shift shift;
}
