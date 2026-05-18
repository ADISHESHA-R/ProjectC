package com.attendance.system.dto.jobsite;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SiteAdvanceExpenseLineDto {
    private Integer lineOrder;
    private LocalDate advanceReceivedDate;
    private BigDecimal openingBal;
    private BigDecimal amount;
    private BigDecimal foodAllow;
    private BigDecimal conveyance;
    private BigDecimal medical;
    private BigDecimal additionalManpower;
    private BigDecimal welding;
    private BigDecimal siteExpn;
    private BigDecimal balInHand;
    private String dispersionNotes;
}
