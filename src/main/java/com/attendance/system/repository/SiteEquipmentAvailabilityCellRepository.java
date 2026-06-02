package com.attendance.system.repository;

import com.attendance.system.entity.SiteEquipmentAvailabilityCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SiteEquipmentAvailabilityCellRepository extends JpaRepository<SiteEquipmentAvailabilityCell, Long> {

    List<SiteEquipmentAvailabilityCell> findByItem_IdAndCalendarDayBetweenOrderByCalendarDayAsc(
        Long itemId,
        LocalDate startInclusive,
        LocalDate endInclusive
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM SiteEquipmentAvailabilityCell c WHERE c.item.id IN :itemIds")
    int deleteByItem_IdIn(@Param("itemIds") List<Long> itemIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM SiteEquipmentAvailabilityCell c WHERE c.item.category.site.id = :siteId")
    int deleteAllForSite(@Param("siteId") Long siteId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM SiteEquipmentAvailabilityCell c WHERE c.item.id = :itemId AND c.calendarDay >= :start AND c.calendarDay <= :end")
    int deleteForItemInMonth(
        @Param("itemId") Long itemId,
        @Param("start") LocalDate startInclusive,
        @Param("end") LocalDate endInclusive
    );
}
