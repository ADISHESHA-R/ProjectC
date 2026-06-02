package com.attendance.system.dto.jobsite;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Reorders items within categories and supports cross-category moves.
 * Must list every equipment item on the site exactly once across all category blocks.
 */
@Data
public class SiteEquipmentLayoutSaveRequest {

    private List<CategoryItemOrder> categories = new ArrayList<>();

    @Data
    public static class CategoryItemOrder {
        private Long categoryId;
        private List<Long> itemIds = new ArrayList<>();
    }
}
