package com.attendance.system.dto.jobsite;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SiteToolIssueDto {
    private Integer lineOrder;
    private String pkgListSl;
    private String itemDescription;
    private LocalDate dateMissing;
    private LocalDate dateDamage;
    private LocalDate dateRepair;
    @JsonAlias({"handledByEmployeeUserId"})
    private Long handledByUserId;
    private String issueDescription;
}
