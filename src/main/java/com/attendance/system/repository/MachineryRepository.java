package com.attendance.system.repository;

import com.attendance.system.entity.Machinery;
import com.attendance.system.enums.MachineryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MachineryRepository extends JpaRepository<Machinery, Long> {

    @Query("SELECT COUNT(m) > 0 FROM Machinery m WHERE LOWER(m.code) = LOWER(:code)")
    boolean existsByCodeIgnoreCase(@Param("code") String code);

    Optional<Machinery> findByCodeIgnoreCase(String code);

    List<Machinery> findBySite_IdOrderByCodeAsc(Long siteId);

    List<Machinery> findBySite_IdAndStatusOrderByCodeAsc(Long siteId, MachineryStatus status);

    long countBySite_Id(Long siteId);
}
