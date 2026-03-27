package com.attendance.system.repository;

import com.attendance.system.entity.MachineryUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MachineryUsageRepository extends JpaRepository<MachineryUsage, Long> {

    List<MachineryUsage> findBySite_IdAndUsageDateOrderByMachinery_CodeAsc(Long siteId, LocalDate usageDate);

    void deleteBySite_IdAndUsageDate(Long siteId, LocalDate usageDate);

    List<MachineryUsage> findBySite_IdAndUsageDateBetween(Long siteId, LocalDate start, LocalDate end);

    boolean existsByMachinery_Id(Long machineryId);

    @Query("SELECT u FROM MachineryUsage u JOIN FETCH u.machinery m WHERE u.site.id = :siteId AND u.usageDate BETWEEN :start AND :end")
    List<MachineryUsage> findBySiteAndDateRangeWithMachinery(
        @Param("siteId") Long siteId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
