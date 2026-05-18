package com.attendance.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSiteRequest {
    @NotBlank(message = "Site name is required")
    private String name;

    @NotBlank(message = "Job code is required")
    private String jobCode;

    private String address;

    private String customerName;
    private Integer estimatedDays;
    private Long inchargeUserId;
    private Long locationSiteId;
    private LocalDate siteStartDate;
    private LocalDate siteEndDate;
    private Integer totalProjectDays;
}
