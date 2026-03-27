package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyMonthSummaryResponse {
    private int month;
    private String monthName;
    private int daysWithUsage;
    private long totalMachineDays;
    private List<String> topMachineCodes;
}
