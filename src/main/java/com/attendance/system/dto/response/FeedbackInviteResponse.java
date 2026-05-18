package com.attendance.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackInviteResponse {
    private String token;
    private LocalDateTime expiresAt;
    /** Example path only; front-end should build full URL from configured public base. */
    private String relativePath;
}
