package com.attendance.system.service;

import com.attendance.system.dto.response.DashboardResponse;
import com.attendance.system.entity.Site;
import com.attendance.system.enums.AttendanceStatus;
import com.attendance.system.enums.Role;
import com.attendance.system.repository.AttendanceRepository;
import com.attendance.system.repository.SiteRepository;
import com.attendance.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final SiteRepository siteRepository;
    
    public DashboardResponse getAdminDashboard() {
        LocalDate today = LocalDate.now();
        
        Long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);
        Long todayAttendanceCount = attendanceRepository.countByDate(today);
        Long pendingApprovals = attendanceRepository.countByStatus(AttendanceStatus.PENDING);
        Long approvedCount = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.APPROVED);
        Long rejectedCount = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.REJECTED);
        
        List<Site> allSites = siteRepository.findAll();
        List<DashboardResponse.SiteDashboardStats> siteStats = allSites.stream()
            .map(site -> {
                Long siteTodayCount = attendanceRepository.countBySiteAndDate(site, today);
                Long sitePending = attendanceRepository.countBySiteAndStatus(site, AttendanceStatus.PENDING);
                Long siteApproved = attendanceRepository.countBySiteAndDateAndStatus(site, today, AttendanceStatus.APPROVED);
                Long siteRejected = attendanceRepository.countBySiteAndDateAndStatus(site, today, AttendanceStatus.REJECTED);
                
                return new DashboardResponse.SiteDashboardStats(
                    site.getId(),
                    site.getName(),
                    site.getJobCode(),
                    siteTodayCount,
                    sitePending,
                    siteApproved,
                    siteRejected
                );
            })
            .collect(Collectors.toList());
        
        DashboardResponse response = new DashboardResponse();
        response.setTotalEmployees(totalEmployees);
        response.setTodayAttendanceCount(todayAttendanceCount);
        response.setPendingApprovals(pendingApprovals);
        response.setApprovedCount(approvedCount);
        response.setRejectedCount(rejectedCount);
        response.setSiteStats(siteStats);
        
        return response;
    }
    
    public DashboardResponse getEmployeeDashboard(Long employeeId) {
        LocalDate today = LocalDate.now();
        
        DashboardResponse response = new DashboardResponse();
        response.setTotalEmployees(null);
        response.setTodayAttendanceCount(attendanceRepository.countByDate(today));
        response.setPendingApprovals(attendanceRepository.countByStatus(AttendanceStatus.PENDING));
        response.setApprovedCount(attendanceRepository.countByDateAndStatus(today, AttendanceStatus.APPROVED));
        response.setRejectedCount(attendanceRepository.countByDateAndStatus(today, AttendanceStatus.REJECTED));
        response.setSiteStats(null);
        
        return response;
    }
}
