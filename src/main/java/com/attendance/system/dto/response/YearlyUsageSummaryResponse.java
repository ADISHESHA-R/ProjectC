package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyUsageSummaryResponse {
    private Long siteId;
    private int year;
    private List<YearlyMonthSummaryResponse> months;
}
