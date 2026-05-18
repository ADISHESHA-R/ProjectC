package com.attendance.system.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateSiteRequest {
    private String name;
    private String jobCode;
    private String address;
    private Boolean isActive;

    private String customerName;
    private Integer estimatedDays;
    /** Set to null with {@link #clearIncharge} to detach incharge. */
    private Long inchargeUserId;
    private Boolean clearIncharge;
    private Long locationSiteId;
    private Boolean clearLocationSite;
    private LocalDate siteStartDate;
    private LocalDate siteEndDate;
    private Integer totalProjectDays;
}
