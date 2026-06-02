package com.attendance.system.dto.jobsite;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class SiteEquipmentPortalResponse {

    /** Echo of query params when loading availability; may be null if not requested. */
    private Integer year;
    private Integer month;

    private List<SiteEquipmentCategoryResponseDto> categories = new ArrayList<>();

    @Data
    public static class SiteEquipmentCategoryResponseDto {
        private Long id;
        private String title;
        private Integer sortOrder;
        private List<SiteEquipmentItemResponseDto> items = new ArrayList<>();
    }

    @Data
    public static class SiteEquipmentItemResponseDto {
        private Long id;
        private Integer lineOrder;
        private String itemDescription;
        private String uom;
        private String qty;
        private String dateNote;
        /**
         * Day-of-month (1–31) → present on site that calendar day (for requested year/month only).
         * Omitted days are treated as not present when year/month were requested.
         */
        private Map<Integer, Boolean> dayPresent = new LinkedHashMap<>();
    }
}
