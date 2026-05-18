package com.attendance.system.dto.jobsite;

import com.attendance.system.enums.RegisterAttendanceCode;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceRegisterCellWriteDto {
    private Long employeeUserId;
    private LocalDate date;
    /** Null clears override for that day (falls back to attendance approval mapping). */
    private RegisterAttendanceCode code;
}
