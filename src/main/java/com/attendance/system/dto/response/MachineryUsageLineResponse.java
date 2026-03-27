package com.attendance.system.dto.response;

import com.attendance.system.enums.MachineryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineryUsageLineResponse {
    private Long machineryId;
    private String code;
    private String name;
    private String itemDescription;
    private MachineryStatus catalogStatus;
    private String imagePath;
    private BigDecimal qty;
    private String uom;
    private String jobCode;
    private String notes;
}
