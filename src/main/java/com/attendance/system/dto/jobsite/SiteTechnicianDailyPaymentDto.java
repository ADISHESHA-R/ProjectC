package com.attendance.system.dto.jobsite;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SiteTechnicianDailyPaymentDto {
    /** Optional; used when several lines share the same technician and date. */
    private Integer lineOrder;
    private Long technicianUserId;
    private LocalDate paymentDate;
    private BigDecimal amount;
}
