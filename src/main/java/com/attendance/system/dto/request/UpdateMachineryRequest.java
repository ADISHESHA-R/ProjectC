package com.attendance.system.dto.request;

import com.attendance.system.enums.MachineryStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMachineryRequest {

    @Size(max = 255)
    private String name;

    private String itemDescription;

    @Size(max = 128)
    private String jobCode;

    @Size(max = 32)
    private String defaultUom;

    @Size(max = 128)
    private String serialNumber;

    @Size(max = 128)
    private String model;

    private MachineryStatus status;
}
