package com.attendance.system.repository;

import com.attendance.system.entity.SiteEquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteEquipmentCategoryRepository extends JpaRepository<SiteEquipmentCategory, Long> {

    List<SiteEquipmentCategory> findBySite_IdOrderBySortOrderAscIdAsc(Long siteId);

    void deleteBySite_Id(Long siteId);
}
