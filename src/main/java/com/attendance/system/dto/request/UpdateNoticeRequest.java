package com.attendance.system.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateNoticeRequest {
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;
}
