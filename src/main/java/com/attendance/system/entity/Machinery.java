package com.attendance.system.entity;

import com.attendance.system.enums.MachineryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "machinery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Machinery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "item_description", columnDefinition = "TEXT")
    private String itemDescription;

    /** Default / reference job code for this catalog item (optional). */
    @Column(name = "job_code", length = 128)
    private String jobCode;

    @Column(name = "default_uom", nullable = false, length = 32)
    private String defaultUom = "HOUR";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "image_path", length = 512)
    private String imagePath;

    @Column(name = "serial_number", length = 128)
    private String serialNumber;

    @Column(length = 128)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MachineryStatus status = MachineryStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
