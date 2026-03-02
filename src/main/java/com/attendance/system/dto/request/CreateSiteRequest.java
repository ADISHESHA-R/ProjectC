package com.attendance.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSiteRequest {
    @NotBlank(message = "Site name is required")
    private String name;

    @NotBlank(message = "Job code is required")
    private String jobCode;

    private String address;
}
