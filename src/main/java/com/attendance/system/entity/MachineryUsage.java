package com.attendance.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "machinery_usage",
    uniqueConstraints = @UniqueConstraint(columnNames = {"site_id", "usage_date", "machinery_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineryUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machinery_id", nullable = false)
    private Machinery machinery;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal qty = BigDecimal.ZERO;

    @Column(nullable = false, length = 32)
    private String uom;

    /** Optional override for that day (in addition to / instead of machinery default). */
    @Column(name = "job_code", length = 128)
    private String jobCode;

    @Column(length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
