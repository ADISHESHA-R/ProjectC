package com.attendance.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "site_equipment_availability_cells",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_equipment_item_day",
        columnNames = {"item_id", "calendar_day"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteEquipmentAvailabilityCell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private SiteEquipmentItem item;

    @Column(name = "calendar_day", nullable = false)
    private LocalDate calendarDay;

    @Column(nullable = false)
    private boolean present = true;
}
