package com.attendance.system.service;

import com.attendance.system.dto.request.ApproveAttendanceRequest;
import com.attendance.system.dto.request.MarkAttendanceRequest;
import com.attendance.system.dto.response.AttendanceCalendarResponse;
import com.attendance.system.dto.response.AttendanceResponse;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.dto.response.UserResponse;
import com.attendance.system.entity.Attendance;
import com.attendance.system.entity.Site;
import com.attendance.system.entity.User;
import com.attendance.system.enums.AttendanceStatus;
import com.attendance.system.repository.AttendanceRepository;
import com.attendance.system.repository.SiteRepository;
import com.attendance.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final FileStorageService fileStorageService;
    
    @Transactional
    public AttendanceResponse markAttendance(Long employeeId, MarkAttendanceRequest request) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        Site site = siteRepository.findByIdAndIsActiveTrue(request.getSiteId())
            .orElseThrow(() -> new RuntimeException("Site not found or inactive"));
        
        LocalDate today = LocalDate.now();
        
        if (attendanceRepository.findByEmployeeAndDateAndSite(employee, today, site).isPresent()) {
            throw new RuntimeException("Attendance already marked for today at this site");
        }
        
        try {
            String photoPath = fileStorageService.storeFile(request.getPhoto(), employeeId);
            
            Attendance attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setSite(site);
            attendance.setDate(today);
            attendance.setTime(LocalTime.now());
            attendance.setPhotoPath(photoPath);
            attendance.setStatus(AttendanceStatus.PENDING);
            
            attendance = attendanceRepository.save(attendance);
            return mapToAttendanceResponse(attendance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark attendance: " + e.getMessage());
        }
    }
    
    @Transactional
    public AttendanceResponse approveAttendance(Long attendanceId, ApproveAttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        attendance.setStatus(request.getStatus());
        if (request.getStatus() == AttendanceStatus.REJECTED) {
            attendance.setRejectionReason(request.getRejectionReason());
        } else {
            attendance.setRejectionReason(null);
        }
        
        attendance = attendanceRepository.save(attendance);
        return mapToAttendanceResponse(attendance);
    }
    
    @Transactional
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        try {
            fileStorageService.deleteFile(attendance.getPhotoPath());
        } catch (Exception e) {
            System.err.println("Failed to delete photo file: " + e.getMessage());
        }
        
        attendanceRepository.delete(attendance);
    }
    
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        return mapToAttendanceResponse(attendance);
    }
    
    public Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, Pageable pageable) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        return attendanceRepository.findByEmployee(employee, pageable)
            .map(this::mapToAttendanceResponse);
    }
    
    public Page<AttendanceResponse> getSiteAttendance(Long siteId, Pageable pageable) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new RuntimeException("Site not found"));
        return attendanceRepository.findBySite(site, pageable)
            .map(this::mapToAttendanceResponse);
    }
    
    public Page<AttendanceResponse> getAllAttendance(LocalDate date, Long employeeId, 
                                                      Long siteId, AttendanceStatus status, Pageable pageable) {
        return attendanceRepository.findByFilters(date, employeeId, siteId, status, pageable)
            .map(this::mapToAttendanceResponse);
    }
    
    // Get attendance by date range
    public Page<AttendanceResponse> getEmployeeAttendanceByDateRange(
            Long employeeId, 
            LocalDate startDate, 
            LocalDate endDate, 
            Long siteId,
            Pageable pageable) {
        // Verify employee exists
        userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (siteId != null) {
            return attendanceRepository.findByEmployeeAndDateRangeAndSite(
                employeeId, startDate, endDate, siteId, pageable)
                .map(this::mapToAttendanceResponse);
        } else {
            return attendanceRepository.findByEmployeeAndDateBetween(
                employeeId, startDate, endDate, pageable)
                .map(this::mapToAttendanceResponse);
        }
    }
    
    // Get attendance by specific date
    public List<AttendanceResponse> getEmployeeAttendanceByDate(
            Long employeeId, 
            LocalDate date) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        return attendanceRepository.findByEmployeeAndDate(employeeId, date)
            .stream()
            .map(this::mapToAttendanceResponse)
            .collect(Collectors.toList());
    }
    
    // Get calendar view (all dates with attendance)
    public AttendanceCalendarResponse getEmployeeAttendanceCalendar(Long employeeId) {
        // Verify employee exists
        userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        List<LocalDate> attendanceDates = attendanceRepository.findDistinctDatesByEmployee(employeeId);
        
        List<AttendanceCalendarResponse.AttendanceDateEntry> entries = new ArrayList<>();
        
        for (LocalDate date : attendanceDates) {
            List<Attendance> attendances = attendanceRepository.findByEmployeeAndDate(employeeId, date);
            if (!attendances.isEmpty()) {
                Attendance latest = attendances.get(0); // Get first (most recent time)
                entries.add(new AttendanceCalendarResponse.AttendanceDateEntry(
                    date,
                    true,
                    latest.getId(),
                    latest.getSite().getName(),
                    latest.getSite().getJobCode(),
                    latest.getStatus().toString()
                ));
            }
        }
        
        return new AttendanceCalendarResponse(
            entries,
            attendanceDates.size(),
            entries.size()
        );
    }
    
    // Get attendance summary by month
    public Map<String, Object> getEmployeeAttendanceSummary(Long employeeId, int year, int month) {
        // Verify employee exists
        userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        Page<AttendanceResponse> attendance = getEmployeeAttendanceByDateRange(
            employeeId, startDate, endDate, null, Pageable.unpaged());
        
        long totalDays = startDate.lengthOfMonth();
        Long attendedDays = attendanceRepository.countAttendanceDaysByMonth(employeeId, year, month);
        if (attendedDays == null) {
            attendedDays = 0L;
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalDays", totalDays);
        summary.put("attendedDays", attendedDays);
        summary.put("attendanceList", attendance.getContent());
        summary.put("attendanceRate", totalDays > 0 ? (attendedDays * 100.0 / totalDays) : 0);
        
        return summary;
    }
    
    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        User employee = attendance.getEmployee();
        Site site = attendance.getSite();
        
        return new AttendanceResponse(
            attendance.getId(),
            new UserResponse(
                employee.getId(),
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getRole(),
                employee.getStatus(),
                employee.getAddress(),
                employee.getDateOfBirth(),
                employee.getBloodGroup(),
                employee.getValidDocumentPath(),
                employee.getEmployeeStatus(),
                employee.getCreatedAt()
            ),
            new SiteResponse(
                site.getId(),
                site.getName(),
                site.getJobCode(),
                site.getAddress(),
                site.getIsActive(),
                site.getCreatedAt(),
                site.getUpdatedAt()
            ),
            attendance.getDate(),
            attendance.getTime(),
            attendance.getPhotoPath(),
            attendance.getStatus(),
            attendance.getRejectionReason(),
            attendance.getCreatedAt()
        );
    }
}
