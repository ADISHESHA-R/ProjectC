package com.attendance.system.entity;

import com.attendance.system.enums.CertificateClientStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "job_code", nullable = false, unique = true)
    private String jobCode;

    @Column(length = 500)
    private String address;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /** Customer / client display name for job wizard & registers. */
    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incharge_user_id")
    private User inchargeUser;

    /**
     * Optional: pick a canonical site row for “site location” display (additive FK).
     * When null, use this row’s {@link #name} / {@link #address}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_site_id")
    private Site locationSite;

    @Column(name = "site_start_date")
    private LocalDate siteStartDate;

    @Column(name = "site_end_date")
    private LocalDate siteEndDate;

    @Column(name = "total_project_days")
    private Integer totalProjectDays;

    @Lob
    @Column(name = "wizard_data")
    private String wizardData;

    @Lob
    @Column(name = "customer_feedback_payload")
    private String customerFeedbackPayload;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_client_status", length = 40)
    private CertificateClientStatus certificateClientStatus = CertificateClientStatus.NONE;

    @Column(name = "customer_feedback_approved_at")
    private LocalDateTime customerFeedbackApprovedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
