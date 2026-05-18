package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Paper-style site team attendance grid backed by existing {@code Attendance} rows.
 * Column count is {@code dayDates.size()} (see {@code daysPerBlock} on the register API).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRegisterResponse {

    private Long siteId;
    private String jobCode;
    private String customerName;
    private LocalDate siteStartDate;
    private LocalDate siteEndDate;
    private Integer totalProjectDays;
    private Integer estimatedDays;

    /** Inclusive start of the date window shown in the grid columns. */
    private LocalDate periodStart;
    private LocalDate periodEnd;
    /** 0 = first block from periodStart using daysPerBlock, 1 = next block, etc. */
    private int blockIndex;

    private List<LocalDate> dayDates;
    private List<AttendanceRegisterRow> rows;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceRegisterRow {
        private int slNo;
        private Long employeeId;
        private String employeeName;
        /** One letter / code per day column (e.g. P, A, empty). */
        private List<String> dayCodes;
    }
}
