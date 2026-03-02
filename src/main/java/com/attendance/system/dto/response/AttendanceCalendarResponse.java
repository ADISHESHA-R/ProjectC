package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCalendarResponse {
    private List<AttendanceDateEntry> attendanceDates;
    private int totalDays;
    private int attendedDays;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceDateEntry {
        private LocalDate date;
        private boolean hasAttendance;
        private Long attendanceId;
        private String siteName;
        private String jobCode;
        private String status; // PENDING, APPROVED, REJECTED
    }
}
