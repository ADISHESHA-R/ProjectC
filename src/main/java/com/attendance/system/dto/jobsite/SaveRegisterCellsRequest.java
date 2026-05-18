package com.attendance.system.dto.jobsite;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaveRegisterCellsRequest {
    @NotNull
    private List<AttendanceRegisterCellWriteDto> cells;
}
