package com.attendance.system.repository;

import com.attendance.system.entity.SiteEquipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiteEquipmentItemRepository extends JpaRepository<SiteEquipmentItem, Long> {

    List<SiteEquipmentItem> findByCategory_IdOrderByLineOrderAscIdAsc(Long categoryId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM SiteEquipmentItem i WHERE i.category.site.id = :siteId")
    int deleteAllForSite(@Param("siteId") Long siteId);

    @Query("SELECT i.id FROM SiteEquipmentItem i WHERE i.category.site.id = :siteId")
    List<Long> findIdsBySiteId(@Param("siteId") Long siteId);
}
