package com.attendance.system.repository;

import com.attendance.system.entity.SiteAttendanceRegisterCell;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SiteAttendanceRegisterCellRepository extends JpaRepository<SiteAttendanceRegisterCell, Long> {

    List<SiteAttendanceRegisterCell> findBySite_IdAndCalendarDayBetween(Long siteId, LocalDate start, LocalDate end);

    Optional<SiteAttendanceRegisterCell> findBySite_IdAndEmployee_IdAndCalendarDay(Long siteId, Long employeeId, LocalDate day);

    void deleteBySite_Id(Long siteId);

    void deleteBySite_IdAndCalendarDayIn(Long siteId, Collection<LocalDate> days);

    void deleteBySite_IdAndEmployee_IdAndCalendarDay(Long siteId, Long employeeId, LocalDate day);
}
