package com.attendance.system.dto.response;

import com.attendance.system.enums.AttendanceStatus;
import com.attendance.system.enums.Shift;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Admin and employee attendance payloads. Photo fields: {@code photoPath} is the stored key;
 * {@code photoUrl} is the canonical browser URL ({@code /api/files?path=...} or absolute when configured).
 * {@code imageUrl}, {@code image}, and {@code photo} mirror {@code photoUrl} for older clients.
 */
@Data
@NoArgsConstructor
public class AttendanceResponse {
    private Long id;
    /** Same as {@code site.id} when {@code site} is present; helps flat table UIs and filters. */
    private Long siteId;
    /**
     * Copy of the site’s planned start/end for list screens that do not drill into {@code site}.
     * ISO date strings in JSON; names align with {@link SiteResponse}.
     */
    private LocalDate siteStartDate;
    private LocalDate siteEndDate;

    private UserResponse employee;
    private SiteResponse site;
    private LocalDate date;
    private LocalTime time;
    /** Stored filename or relative key passed to {@code GET /api/files}. */
    private String photoPath;
    /** Preferred URL to load the attendance photo (requires same-origin or {@code app.api-public-base-url}). */
    private String photoUrl;
    private String imageUrl;
    private String image;
    private String photo;

    private AttendanceStatus status;
    private String rejectionReason;
    private Shift shift;
    private LocalDateTime createdAt;
}
