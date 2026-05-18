package com.attendance.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "site_advance_expense_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteAdvanceExpenseLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "line_order", nullable = false)
    private Integer lineOrder;

    @Column(name = "advance_received_date")
    private LocalDate advanceReceivedDate;

    @Column(name = "opening_bal", precision = 14, scale = 2)
    private BigDecimal openingBal;

    @Column(name = "amount", precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "food_allow", precision = 14, scale = 2)
    private BigDecimal foodAllow;

    @Column(name = "conveyance", precision = 14, scale = 2)
    private BigDecimal conveyance;

    @Column(name = "medical", precision = 14, scale = 2)
    private BigDecimal medical;

    @Column(name = "additional_manpower", precision = 14, scale = 2)
    private BigDecimal additionalManpower;

    @Column(name = "welding", precision = 14, scale = 2)
    private BigDecimal welding;

    @Column(name = "site_expn", precision = 14, scale = 2)
    private BigDecimal siteExpn;

    @Column(name = "bal_in_hand", precision = 14, scale = 2)
    private BigDecimal balInHand;

    @Column(name = "dispersion_notes", length = 4000)
    private String dispersionNotes;
}
