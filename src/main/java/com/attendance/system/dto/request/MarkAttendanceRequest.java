package com.attendance.system.dto.request;

import com.attendance.system.enums.Shift;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MarkAttendanceRequest {
    @NotNull(message = "Photo is required")
    private MultipartFile photo;

    @NotNull(message = "Site ID is required")
    private Long siteId;

    @NotNull(message = "Shift is required")
    private Shift shift;
}
