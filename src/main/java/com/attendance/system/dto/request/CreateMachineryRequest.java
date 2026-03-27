package com.attendance.system.dto.request;

import com.attendance.system.enums.MachineryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMachineryRequest {

    @NotBlank
    @Size(max = 64)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String itemDescription;

    @Size(max = 128)
    private String jobCode;

    @Size(max = 32)
    private String defaultUom;

    @NotNull
    private Long siteId;

    @Size(max = 128)
    private String serialNumber;

    @Size(max = 128)
    private String model;

    private MachineryStatus status = MachineryStatus.ACTIVE;

    /** If set, creates a usage line for this site + date after save (qty defaults to 1). */
    private LocalDate markUsedOnDate;
}
