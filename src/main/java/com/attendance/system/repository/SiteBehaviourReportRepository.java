package com.attendance.system.repository;

import com.attendance.system.entity.SiteBehaviourReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteBehaviourReportRepository extends JpaRepository<SiteBehaviourReport, Long> {

    Optional<SiteBehaviourReport> findBySite_Id(Long siteId);

    void deleteBySite_Id(Long siteId);
}
