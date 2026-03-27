package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDaySummaryResponse {
    private LocalDate date;
    private int machineCount;
    private List<String> machineCodes;
}
