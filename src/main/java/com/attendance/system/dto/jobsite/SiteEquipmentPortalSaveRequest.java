package com.attendance.system.dto.jobsite;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class SiteEquipmentPortalSaveRequest {

    /**
     * When set with {@link #availabilityMonth}, any item whose {@link SiteEquipmentItemSaveDto#getDayPresent()}
     * is non-null has month slice replaced: true keys create cells, missing/false keys remove cells for those days.
     */
    private Integer availabilityYear;
    private Integer availabilityMonth;

    private List<SiteEquipmentCategorySaveDto> categories = new ArrayList<>();

    @Data
    public static class SiteEquipmentCategorySaveDto {
        private Long id;
        private String title;
        private Integer sortOrder;
        private List<SiteEquipmentItemSaveDto> items = new ArrayList<>();
    }

    @Data
    public static class SiteEquipmentItemSaveDto {
        private Long id;
        private Integer lineOrder;
        private String itemDescription;
        private String uom;
        private String qty;
        private String dateNote;
        /** Non-null + parent availability year/month → replace that month for this item. */
        private Map<Integer, Boolean> dayPresent;
    }
}
