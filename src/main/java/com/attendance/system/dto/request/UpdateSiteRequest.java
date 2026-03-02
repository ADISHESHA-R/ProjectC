package com.attendance.system.dto.request;

import lombok.Data;

@Data
public class UpdateSiteRequest {
    private String name;
    private String jobCode;
    private String address;
    private Boolean isActive;
}
