package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalEmployees;
    private Long todayAttendanceCount;
    private Long pendingApprovals;
    private Long approvedCount;
    private Long rejectedCount;
    
    // Site-wise statistics
    private List<SiteDashboardStats> siteStats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SiteDashboardStats {
        private Long siteId;
        private String siteName;
        private String jobCode;
        private Long todayAttendanceCount;
        private Long pendingApprovals;
        private Long approvedCount;
        private Long rejectedCount;
    }
}
