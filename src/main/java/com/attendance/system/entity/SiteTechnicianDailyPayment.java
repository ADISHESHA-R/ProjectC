package com.attendance.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "site_technician_daily_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteTechnicianDailyPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    /** Order when multiple payment lines exist for the same technician and date. */
    @Column(name = "line_order", nullable = false)
    private Integer lineOrder = 0;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
}
