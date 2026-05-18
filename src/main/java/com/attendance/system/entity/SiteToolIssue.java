package com.attendance.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "site_tool_issues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteToolIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "line_order", nullable = false)
    private Integer lineOrder;

    @Column(name = "pkg_list_sl", length = 64)
    private String pkgListSl;

    @Column(name = "item_description", length = 2000)
    private String itemDescription;

    @Column(name = "date_missing")
    private LocalDate dateMissing;

    @Column(name = "date_damage")
    private LocalDate dateDamage;

    @Column(name = "date_repair")
    private LocalDate dateRepair;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_id")
    private User handledBy;

    @Column(name = "issue_description", length = 4000)
    private String issueDescription;
}
