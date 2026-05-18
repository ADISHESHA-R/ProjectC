package com.attendance.system.repository;

import com.attendance.system.entity.SiteAdvanceExpenseLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteAdvanceExpenseLineRepository extends JpaRepository<SiteAdvanceExpenseLine, Long> {

    List<SiteAdvanceExpenseLine> findBySite_IdOrderByLineOrderAsc(Long siteId);

    void deleteBySite_Id(Long siteId);
}
