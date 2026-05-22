package com.attendance.system.service;

import com.attendance.system.dto.request.ApproveAttendanceRequest;
import com.attendance.system.dto.request.MarkAttendanceRequest;
import com.attendance.system.dto.request.UpdateAttendanceShiftRequest;
import com.attendance.system.dto.response.AttendanceCalendarResponse;
import com.attendance.system.dto.response.AttendanceRegisterResponse;
import com.attendance.system.dto.response.AttendanceResponse;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.dto.response.UserResponse;
import com.attendance.system.entity.Attendance;
import com.attendance.system.entity.Site;
import com.attendance.system.entity.SiteAttendanceRegisterCell;
import com.attendance.system.entity.User;
import com.attendance.system.enums.AttendanceStatus;
import com.attendance.system.enums.CertificateClientStatus;
import com.attendance.system.enums.RegisterAttendanceCode;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.Shift;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.AttendanceRepository;
import com.attendance.system.repository.SiteAttendanceRegisterCellRepository;
import com.attendance.system.repository.SiteRepository;
import com.attendance.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.attendance.system.repository.AttendanceSpecifications.withAdminFilters;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final SiteAttendanceRegisterCellRepository siteAttendanceRegisterCellRepository;
    private final FileStorageService fileStorageService;
    private final FileUrlService fileUrlService;
    
    @Transactional
    public AttendanceResponse markAttendance(Long employeeId, MarkAttendanceRequest request) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        
        if (employee.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("Only employees can mark attendance");
        }
        
        Site site = siteRepository.findByIdAndIsActiveTrue(request.getSiteId())
            .orElseThrow(() -> new ResourceNotFoundException("Site not found or inactive"));
        
        LocalDate today = LocalDate.now();
        var existingOpt = attendanceRepository.findByEmployeeAndDateAndSite(employee, today, site);
        
        if (existingOpt.isPresent()) {
            Attendance existing = existingOpt.get();
            if (existing.getStatus() == AttendanceStatus.APPROVED) {
                throw new RuntimeException("Attendance already approved for this day at this site. Cannot resubmit.");
            }
            if (existing.getStatus() == AttendanceStatus.PENDING) {
                throw new RuntimeException("Attendance pending for this day at this site. Cannot resubmit.");
            }
            // REJECTED -> allow resubmit: update existing record
            try {
                try {
                    fileStorageService.deleteFile(existing.getPhotoPath());
                } catch (Exception e) {
                    // ignore if old file missing
                }
                String photoPath = fileStorageService.storeFile(request.getPhoto(), employeeId);
                existing.setPhotoPath(photoPath);
                existing.setTime(LocalTime.now());
                existing.setShift(request.getShift());
                existing.setStatus(AttendanceStatus.PENDING);
                existing.setRejectionReason(null);
                existing = attendanceRepository.save(existing);
                return mapToAttendanceResponse(existing);
            } catch (Exception e) {
                throw new RuntimeException("Failed to resubmit attendance: " + e.getMessage());
            }
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
            attendance.setShift(request.getShift());
            
            attendance = attendanceRepository.save(attendance);
            return mapToAttendanceResponse(attendance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark attendance: " + e.getMessage());
        }
    }
    
    @Transactional
    public AttendanceResponse approveAttendance(Long attendanceId, ApproveAttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", attendanceId));
        
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
    public AttendanceResponse updateAttendanceShift(Long attendanceId, UpdateAttendanceShiftRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", attendanceId));
        attendance.setShift(request.getShift());
        attendance = attendanceRepository.save(attendance);
        return mapToAttendanceResponse(attendance);
    }
    
    @Transactional
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
        
        try {
            fileStorageService.deleteFile(attendance.getPhotoPath());
        } catch (Exception e) {
            System.err.println("Failed to delete photo file: " + e.getMessage());
        }
        
        attendanceRepository.delete(attendance);
    }
    
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
        return mapToAttendanceResponse(attendance);
    }
    
    public Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, Pageable pageable) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        return attendanceRepository.findByEmployee(employee, pageable)
            .map(this::mapToAttendanceResponse);
    }
    
    public Page<AttendanceResponse> getSiteAttendance(Long siteId, Pageable pageable) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        return attendanceRepository.findBySite(site, pageable)
            .map(this::mapToAttendanceResponse);
    }
    
    public Page<AttendanceResponse> getAllAttendance(LocalDate date, Long employeeId, 
                                                      Long siteId, String jobCode, 
                                                      AttendanceStatus status, Pageable pageable) {
        return attendanceRepository.findAll(withAdminFilters(date, employeeId, siteId, jobCode, status), pageable)
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
            .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        
        if (siteId != null) {
            return attendanceRepository.findByEmployeeAndDateBetweenAndSiteId(
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
        userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        
        return attendanceRepository.findByEmployeeAndDate(employeeId, date)
            .stream()
            .map(this::mapToAttendanceResponse)
            .collect(Collectors.toList());
    }
    
    // Get calendar view (all dates with attendance)
    public AttendanceCalendarResponse getEmployeeAttendanceCalendar(Long employeeId) {
        // Verify employee exists
        userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        
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
                    latest.getStatus().toString(),
                    latest.getShift() != null ? latest.getShift().name() : Shift.FULL_DAY.name()
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
            .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        
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

    /**
     * Builds an N-column register for a site using existing attendance approval status.
     * Register codes: {@code P} = approved (present), {@code A} = rejected (absent), blank = pending / no mark.
     *
     * @param daysPerBlock number of consecutive calendar columns (default UI: 15); clamped to 1–366.
     */
    @Transactional(readOnly = true)
    public AttendanceRegisterResponse getAttendanceRegister(
        Long siteId,
        LocalDate periodStart,
        int blockIndex,
        List<Long> employeeIds,
        int daysPerBlock) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));

        int n = Math.min(Math.max(daysPerBlock, 1), 366);

        LocalDate base = periodStart != null ? periodStart
            : (site.getSiteStartDate() != null ? site.getSiteStartDate() : LocalDate.now());
        if (blockIndex < 0) {
            blockIndex = 0;
        }
        LocalDate windowStart = base.plusDays((long) blockIndex * n);
        LocalDate windowEnd = windowStart.plusDays(n - 1L);

        List<LocalDate> dayDates = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            dayDates.add(windowStart.plusDays(i));
        }

        List<Attendance> raw = attendanceRepository.findBySiteIdAndDateBetweenOrderByDateAscTimeDesc(
            siteId, windowStart, windowEnd);

        Map<String, Attendance> best = new LinkedHashMap<>();
        for (Attendance a : raw) {
            String key = a.getEmployee().getId() + ":" + a.getDate();
            best.merge(key, a, (x, y) -> x.getTime().isAfter(y.getTime()) ? x : y);
        }

        LinkedHashSet<Long> idSet = new LinkedHashSet<>();
        if (employeeIds != null) {
            for (Long eid : employeeIds) {
                if (eid != null) {
                    idSet.add(eid);
                }
            }
        }
        if (idSet.isEmpty()) {
            for (Attendance a : raw) {
                idSet.add(a.getEmployee().getId());
            }
        }

        List<SiteAttendanceRegisterCell> overlay = siteAttendanceRegisterCellRepository
            .findBySite_IdAndCalendarDayBetween(siteId, windowStart, windowEnd);
        Map<String, RegisterAttendanceCode> cellOverride = new HashMap<>();
        for (SiteAttendanceRegisterCell c : overlay) {
            cellOverride.put(c.getEmployee().getId() + ":" + c.getCalendarDay(), c.getCode());
        }

        List<User> users = userRepository.findAllById(idSet);
        users.sort(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER));

        List<AttendanceRegisterResponse.AttendanceRegisterRow> rows = new ArrayList<>();
        int sl = 1;
        for (User u : users) {
            List<String> codes = new ArrayList<>();
            for (LocalDate d : dayDates) {
                RegisterAttendanceCode override = cellOverride.get(u.getId() + ":" + d);
                if (override != null) {
                    codes.add(override.name());
                    continue;
                }
                Attendance att = best.get(u.getId() + ":" + d);
                codes.add(att == null ? "" : registerCode(att.getStatus()));
            }
            rows.add(new AttendanceRegisterResponse.AttendanceRegisterRow(
                sl++, u.getId(), u.getName(), codes));
        }

        return new AttendanceRegisterResponse(
            site.getId(),
            site.getJobCode(),
            site.getCustomerName(),
            site.getSiteStartDate(),
            site.getSiteEndDate(),
            site.getTotalProjectDays(),
            site.getEstimatedDays(),
            windowStart,
            windowEnd,
            blockIndex,
            dayDates,
            rows
        );
    }

    private static String registerCode(AttendanceStatus status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case APPROVED -> "P";
            case REJECTED -> "A";
            case PENDING -> "";
        };
    }
    
    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        User employee = attendance.getEmployee();
        Site site = attendance.getSite();

        SiteResponse siteResponse = new SiteResponse(
            site.getId(),
            site.getName(),
            site.getJobCode(),
            site.getAddress(),
            site.getIsActive(),
            site.getCustomerName(),
            site.getEstimatedDays(),
            null,
            null,
            null,
            null,
            null,
            site.getSiteStartDate(),
            site.getSiteEndDate(),
            site.getTotalProjectDays(),
            site.getCertificateClientStatus() != null
                ? site.getCertificateClientStatus() : CertificateClientStatus.NONE,
            site.getCustomerFeedbackApprovedAt(),
            site.getCreatedAt(),
            site.getUpdatedAt()
        );

        UserResponse employeeResponse = new UserResponse(
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
            employee.getFatherName(),
            employee.getDateOfJoining(),
            employee.getOfficeContactNumber(),
            employee.getHomeContactNumber(),
            employee.getOtherContactNumber(),
            employee.getIdentificationMark(),
            employee.getSpecimenSignaturePath(),
            employee.getPhotoPath(),
            employee.getCreatedAt()
        );

        String photoPath = attendance.getPhotoPath();
        String photoUrl = fileUrlService.buildFilesUrl(photoPath);

        AttendanceResponse r = new AttendanceResponse();
        r.setId(attendance.getId());
        r.setSiteId(site.getId());
        r.setSiteStartDate(site.getSiteStartDate());
        r.setSiteEndDate(site.getSiteEndDate());
        r.setEmployee(employeeResponse);
        r.setSite(siteResponse);
        r.setDate(attendance.getDate());
        r.setTime(attendance.getTime());
        r.setPhotoPath(photoPath);
        r.setPhotoUrl(photoUrl);
        r.setImageUrl(photoUrl);
        r.setImage(photoUrl);
        r.setPhoto(photoUrl);
        r.setStatus(attendance.getStatus());
        r.setRejectionReason(attendance.getRejectionReason());
        r.setShift(attendance.getShift() != null ? attendance.getShift() : Shift.FULL_DAY);
        r.setCreatedAt(attendance.getCreatedAt());
        return r;
    }
}
