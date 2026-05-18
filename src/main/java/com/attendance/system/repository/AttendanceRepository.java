package com.attendance.system.repository;

import com.attendance.system.entity.Attendance;
import com.attendance.system.entity.Site;
import com.attendance.system.entity.User;
import com.attendance.system.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    long countBySite_Id(Long siteId);

    Optional<Attendance> findByEmployeeAndDateAndSite(User employee, LocalDate date, Site site);
    
    Page<Attendance> findByEmployee(User employee, Pageable pageable);
    
    Page<Attendance> findBySite(Site site, Pageable pageable);
    
    Page<Attendance> findByDate(LocalDate date, Pageable pageable);
    
    Page<Attendance> findByStatus(AttendanceStatus status, Pageable pageable);

    long countByDate(LocalDate date);
    
    long countByStatus(AttendanceStatus status);
    
    long countByDateAndStatus(LocalDate date, AttendanceStatus status);
    
    long countBySiteAndDate(Site site, LocalDate date);
    
    long countBySiteAndStatus(Site site, AttendanceStatus status);
    
    long countBySiteAndDateAndStatus(Site site, LocalDate date, AttendanceStatus status);
    
    // Get attendance by employee and date range
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId " +
           "AND a.date BETWEEN :startDate AND :endDate " +
           "ORDER BY a.date DESC, a.time DESC")
    Page<Attendance> findByEmployeeAndDateBetween(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable);
    
    // Get attendance by employee and specific date
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId " +
           "AND a.date = :date ORDER BY a.time DESC")
    List<Attendance> findByEmployeeAndDate(
        @Param("employeeId") Long employeeId,
        @Param("date") LocalDate date);
    
    // Get all dates when employee has attendance
    @Query("SELECT DISTINCT a.date FROM Attendance a WHERE a.employee.id = :employeeId " +
           "ORDER BY a.date DESC")
    List<LocalDate> findDistinctDatesByEmployee(@Param("employeeId") Long employeeId);
    
    /** Use when {@code siteId} is required; avoids nullable-parameter issues on PostgreSQL. */
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId " +
           "AND a.date BETWEEN :startDate AND :endDate " +
           "AND a.site.id = :siteId " +
           "ORDER BY a.date DESC, a.time DESC")
    Page<Attendance> findByEmployeeAndDateBetweenAndSiteId(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("siteId") Long siteId,
        Pageable pageable);
    
    // Get attendance count by month
    @Query("SELECT COUNT(DISTINCT a.date) FROM Attendance a " +
           "WHERE a.employee.id = :employeeId " +
           "AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    Long countAttendanceDaysByMonth(
        @Param("employeeId") Long employeeId,
        @Param("year") int year,
        @Param("month") int month);

    @Query("SELECT a FROM Attendance a WHERE a.site.id = :siteId AND a.date BETWEEN :start AND :end ORDER BY a.date ASC, a.time DESC")
    List<Attendance> findBySiteIdAndDateBetweenOrderByDateAscTimeDesc(
        @Param("siteId") Long siteId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end);
}
