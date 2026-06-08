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

    /**
     * Idempotent write for one day — avoids duplicate-key failures when concurrent autosaves
     * overlap (unique {@code uk_equipment_item_day}). PostgreSQL-specific (Render default).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            INSERT INTO site_equipment_availability_cells (item_id, calendar_day, present)
            VALUES (:itemId, :calendarDay, :present)
            ON CONFLICT (item_id, calendar_day) DO UPDATE SET present = EXCLUDED.present
            """,
        nativeQuery = true
    )
    int upsertPresent(
        @Param("itemId") Long itemId,
        @Param("calendarDay") LocalDate calendarDay,
        @Param("present") boolean present
    );
}
