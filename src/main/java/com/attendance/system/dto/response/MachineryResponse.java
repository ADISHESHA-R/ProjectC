package com.attendance.system.dto.response;

import com.attendance.system.enums.MachineryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineryResponse {
    private Long id;
    private String code;
    private String name;
    private String itemDescription;
    private String jobCode;
    private String defaultUom;
    private Long siteId;
    private String siteName;
    private String imagePath;
    private String serialNumber;
    private String model;
    private MachineryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
