package com.attendance.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "site_equipment_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteEquipmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private SiteEquipmentCategory category;

    @Column(name = "line_order", nullable = false)
    private Integer lineOrder = 0;

    @Column(name = "item_description", length = 2000)
    private String itemDescription;

    @Column(length = 64)
    private String uom;

    @Column(length = 64)
    private String qty;

    @Column(name = "date_note", length = 256)
    private String dateNote;
}
