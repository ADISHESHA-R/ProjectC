package com.attendance.system.dto.response;

import com.attendance.system.enums.CertificateClientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteResponse {
    private Long id;
    private String name;
    private String jobCode;
    private String address;
    private Boolean isActive;

    private String customerName;
    private Integer estimatedDays;
    private Long inchargeUserId;
    private String inchargeName;
    private String inchargeEmployeeId;
    private Long locationSiteId;
    private String locationSiteLabel;
    private LocalDate siteStartDate;
    private LocalDate siteEndDate;
    private Integer totalProjectDays;

    private CertificateClientStatus certificateClientStatus;
    private LocalDateTime customerFeedbackApprovedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
