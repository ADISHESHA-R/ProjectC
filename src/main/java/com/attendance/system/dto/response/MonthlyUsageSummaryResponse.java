package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyUsageSummaryResponse {
    private Long siteId;
    private int year;
    private int month;
    private List<MonthlyDaySummaryResponse> days;
}
