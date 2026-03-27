package com.attendance.system.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SaveUsageSelectionRequest {

    @NotNull
    private Long siteId;

    @NotNull
    private LocalDate date;

    @Valid
    @NotNull
    private List<UsageLineRequest> lines;
}
