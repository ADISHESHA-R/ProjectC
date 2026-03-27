package com.attendance.system.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UsageLineRequest {

    @NotNull
    private Long machineryId;

    @NotNull
    @PositiveOrZero
    private BigDecimal qty;

    @NotNull
    @Size(max = 32)
    private String uom;

    @Size(max = 128)
    private String jobCode;

    @Size(max = 1000)
    private String notes;
}
