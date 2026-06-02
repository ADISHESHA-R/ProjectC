package com.attendance.system.service;

import com.attendance.system.dto.jobsite.SiteEquipmentLayoutSaveRequest;
import com.attendance.system.dto.jobsite.SiteEquipmentPortalResponse;
import com.attendance.system.dto.jobsite.SiteEquipmentPortalSaveRequest;
import com.attendance.system.entity.Site;
import com.attendance.system.entity.SiteEquipmentAvailabilityCell;
import com.attendance.system.entity.SiteEquipmentCategory;
import com.attendance.system.entity.SiteEquipmentItem;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.SiteEquipmentAvailabilityCellRepository;
import com.attendance.system.repository.SiteEquipmentCategoryRepository;
import com.attendance.system.repository.SiteEquipmentItemRepository;
import com.attendance.system.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteEquipmentService {

    private final SiteRepository siteRepository;
    private final SiteEquipmentCategoryRepository categoryRepository;
    private final SiteEquipmentItemRepository itemRepository;
    private final SiteEquipmentAvailabilityCellRepository cellRepository;

    private Site requireSite(Long siteId) {
        return siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
    }

    @Transactional(readOnly = true)
    public SiteEquipmentPortalResponse getPortal(Long siteId, Integer year, Integer month) {
        requireSite(siteId);
        if ((year == null) != (month == null)) {
            throw new IllegalArgumentException("year and month must both be provided or both omitted");
        }
        YearMonth ym = null;
        if (year != null) {
            try {
                ym = YearMonth.of(year, month);
            } catch (DateTimeException e) {
                throw new IllegalArgumentException("Invalid year/month: " + year + "/" + month);
            }
        }

        SiteEquipmentPortalResponse out = new SiteEquipmentPortalResponse();
        out.setYear(year);
        out.setMonth(month);

        List<SiteEquipmentCategory> categories = categoryRepository.findBySite_IdOrderBySortOrderAscIdAsc(siteId);
        for (SiteEquipmentCategory cat : categories) {
            SiteEquipmentPortalResponse.SiteEquipmentCategoryResponseDto cd =
                new SiteEquipmentPortalResponse.SiteEquipmentCategoryResponseDto();
            cd.setId(cat.getId());
            cd.setTitle(cat.getTitle());
            cd.setSortOrder(cat.getSortOrder());

            List<SiteEquipmentItem> items = itemRepository.findByCategory_IdOrderByLineOrderAscIdAsc(cat.getId());
            for (SiteEquipmentItem it : items) {
                SiteEquipmentPortalResponse.SiteEquipmentItemResponseDto idto =
                    new SiteEquipmentPortalResponse.SiteEquipmentItemResponseDto();
                idto.setId(it.getId());
                idto.setLineOrder(it.getLineOrder());
                idto.setItemDescription(it.getItemDescription());
                idto.setUom(it.getUom());
                idto.setQty(it.getQty());
                idto.setDateNote(it.getDateNote());

                if (ym != null) {
                    LocalDate start = ym.atDay(1);
                    LocalDate end = ym.atEndOfMonth();
                    List<SiteEquipmentAvailabilityCell> cells =
                        cellRepository.findByItem_IdAndCalendarDayBetweenOrderByCalendarDayAsc(it.getId(), start, end);
                    Map<Integer, Boolean> dayPresent = new LinkedHashMap<>();
                    for (SiteEquipmentAvailabilityCell cell : cells) {
                        if (cell.isPresent()) {
                            dayPresent.put(cell.getCalendarDay().getDayOfMonth(), true);
                        }
                    }
                    idto.setDayPresent(dayPresent);
                }
                cd.getItems().add(idto);
            }
            out.getCategories().add(cd);
        }
        return out;
    }

    @Transactional
    public SiteEquipmentPortalResponse savePortal(Long siteId, SiteEquipmentPortalSaveRequest body) {
        Site site = requireSite(siteId);
        if (body == null) {
            throw new IllegalArgumentException("Body required");
        }
        if (body.getCategories() == null) {
            throw new IllegalArgumentException("categories required");
        }

        if ((body.getAvailabilityYear() == null) != (body.getAvailabilityMonth() == null)) {
            throw new IllegalArgumentException("availabilityYear and availabilityMonth must both be set or both null");
        }

        if (body.getCategories().isEmpty()) {
            deleteAllForSite(siteId);
            return getPortal(siteId, null, null);
        }

        validateNoDuplicateItemIds(body);
        validateNoDuplicateCategoryIds(body);

        Set<Long> payloadItemIds = new HashSet<>();
        for (SiteEquipmentPortalSaveRequest.SiteEquipmentCategorySaveDto catDto : body.getCategories()) {
            if (catDto.getItems() != null) {
                for (SiteEquipmentPortalSaveRequest.SiteEquipmentItemSaveDto itemDto : catDto.getItems()) {
                    if (itemDto.getId() != null) {
                        payloadItemIds.add(itemDto.getId());
                    }
                }
            }
        }

        List<Long> existingItemIds = itemRepository.findIdsBySiteId(siteId);
        List<Long> toDeleteItems = existingItemIds.stream()
            .filter(id -> !payloadItemIds.contains(id))
            .collect(Collectors.toList());
        if (!toDeleteItems.isEmpty()) {
            cellRepository.deleteByItem_IdIn(toDeleteItems);
            itemRepository.deleteAllByIdInBatch(toDeleteItems);
        }

        YearMonth ymAvail = null;
        if (body.getAvailabilityYear() != null) {
            ymAvail = YearMonth.of(body.getAvailabilityYear(), body.getAvailabilityMonth());
        }

        Set<Long> survivingCategoryIds = new HashSet<>();
        for (SiteEquipmentPortalSaveRequest.SiteEquipmentCategorySaveDto catDto : body.getCategories()) {
            SiteEquipmentCategory cat = upsertCategory(site, catDto);
            survivingCategoryIds.add(cat.getId());
            List<SiteEquipmentPortalSaveRequest.SiteEquipmentItemSaveDto> itemDtos =
                catDto.getItems() != null ? catDto.getItems() : List.of();
            int order = 0;
            for (SiteEquipmentPortalSaveRequest.SiteEquipmentItemSaveDto itemDto : itemDtos) {
                SiteEquipmentItem item = upsertItem(siteId, cat, itemDto, order++);
                if (ymAvail != null && itemDto.getDayPresent() != null) {
                    applyMonthAvailability(item.getId(), ymAvail, itemDto.getDayPresent());
                }
            }
        }

        List<Long> existingCategoryIdsAfter = categoryRepository.findBySite_IdOrderBySortOrderAscIdAsc(siteId).stream()
            .map(SiteEquipmentCategory::getId)
            .collect(Collectors.toList());
        List<Long> toDeleteCats = existingCategoryIdsAfter.stream()
            .filter(id -> !survivingCategoryIds.contains(id))
            .collect(Collectors.toList());
        for (Long cid : toDeleteCats) {
            categoryRepository.deleteById(cid);
        }

        return getPortal(siteId, body.getAvailabilityYear(), body.getAvailabilityMonth());
    }

    private void validateNoDuplicateItemIds(SiteEquipmentPortalSaveRequest body) {
        Set<Long> seen = new HashSet<>();
        for (SiteEquipmentPortalSaveRequest.SiteEquipmentCategorySaveDto catDto : body.getCategories()) {
            if (catDto.getItems() == null) {
                continue;
            }
            for (SiteEquipmentPortalSaveRequest.SiteEquipmentItemSaveDto itemDto : catDto.getItems()) {
                if (itemDto.getId() == null) {
                    continue;
                }
                if (!seen.add(itemDto.getId())) {
                    throw new IllegalArgumentException("Duplicate item id in payload: " + itemDto.getId());
                }
            }
        }
    }

    private void validateNoDuplicateCategoryIds(SiteEquipmentPortalSaveRequest body) {
        Set<Long> seen = new HashSet<>();
        for (SiteEquipmentPortalSaveRequest.SiteEquipmentCategorySaveDto catDto : body.getCategories()) {
            if (catDto.getId() == null) {
                continue;
            }
            if (!seen.add(catDto.getId())) {
                throw new IllegalArgumentException("Duplicate category id in payload: " + catDto.getId());
            }
        }
    }

    private SiteEquipmentCategory upsertCategory(Site site, SiteEquipmentPortalSaveRequest.SiteEquipmentCategorySaveDto dto) {
        String title = dto.getTitle() != null ? dto.getTitle().trim() : "";
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Category title is required");
        }
        int sort = dto.getSortOrder() != null ? dto.getSortOrder() : 0;
        if (dto.getId() == null) {
            SiteEquipmentCategory c = new SiteEquipmentCategory();
            c.setSite(site);
            c.setTitle(title.length() > 512 ? title.substring(0, 512) : title);
            c.setSortOrder(sort);
            return categoryRepository.save(c);
        }
        SiteEquipmentCategory c = categoryRepository.findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("SiteEquipmentCategory", dto.getId()));
        if (!c.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException("Category " + dto.getId() + " does not belong to this site");
        }
        c.setTitle(title.length() > 512 ? title.substring(0, 512) : title);
        c.setSortOrder(sort);
        return categoryRepository.save(c);
    }

    private SiteEquipmentItem upsertItem(
        Long siteId,
        SiteEquipmentCategory category,
        SiteEquipmentPortalSaveRequest.SiteEquipmentItemSaveDto dto,
        int lineOrder
    ) {
        int lo = dto.getLineOrder() != null ? dto.getLineOrder() : lineOrder;
        if (dto.getId() == null) {
            SiteEquipmentItem it = new SiteEquipmentItem();
            it.setCategory(category);
            it.setLineOrder(lo);
            it.setItemDescription(trimToNull(dto.getItemDescription()));
            it.setUom(trimToNull(dto.getUom()));
            it.setQty(trimToNull(dto.getQty()));
            it.setDateNote(trimToNull(dto.getDateNote()));
            return itemRepository.save(it);
        }
        SiteEquipmentItem it = itemRepository.findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("SiteEquipmentItem", dto.getId()));
        if (!it.getCategory().getSite().getId().equals(siteId)) {
            throw new IllegalArgumentException("Item " + dto.getId() + " does not belong to this site");
        }
        it.setCategory(category);
        it.setLineOrder(lo);
        it.setItemDescription(trimToNull(dto.getItemDescription()));
        it.setUom(trimToNull(dto.getUom()));
        it.setQty(trimToNull(dto.getQty()));
        it.setDateNote(trimToNull(dto.getDateNote()));
        return itemRepository.save(it);
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void applyMonthAvailability(Long itemId, YearMonth ym, Map<Integer, Boolean> dayPresent) {
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        cellRepository.deleteForItemInMonth(itemId, start, end);
        if (dayPresent.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, Boolean> e : dayPresent.entrySet()) {
            Integer dom = e.getKey();
            if (dom == null || dom < 1 || dom > ym.lengthOfMonth()) {
                continue;
            }
            if (!Boolean.TRUE.equals(e.getValue())) {
                continue;
            }
            LocalDate day = ym.atDay(dom);
            SiteEquipmentAvailabilityCell cell = new SiteEquipmentAvailabilityCell();
            cell.setItem(itemRepository.getReferenceById(itemId));
            cell.setCalendarDay(day);
            cell.setPresent(true);
            cellRepository.save(cell);
        }
    }

    @Transactional
    public void saveLayout(Long siteId, SiteEquipmentLayoutSaveRequest body) {
        requireSite(siteId);
        if (body == null || body.getCategories() == null || body.getCategories().isEmpty()) {
            throw new IllegalArgumentException("categories with itemIds required");
        }
        List<Long> allSiteItemIds = itemRepository.findIdsBySiteId(siteId);
        Set<Long> seen = new HashSet<>();
        for (SiteEquipmentLayoutSaveRequest.CategoryItemOrder block : body.getCategories()) {
            if (block.getCategoryId() == null) {
                throw new IllegalArgumentException("categoryId required for each block");
            }
            SiteEquipmentCategory cat = categoryRepository.findById(block.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("SiteEquipmentCategory", block.getCategoryId()));
            if (!cat.getSite().getId().equals(siteId)) {
                throw new IllegalArgumentException("Category " + block.getCategoryId() + " does not belong to this site");
            }
            List<Long> itemIds = block.getItemIds() != null ? block.getItemIds() : new ArrayList<>();
            int order = 0;
            for (Long itemId : itemIds) {
                if (itemId == null) {
                    continue;
                }
                if (!seen.add(itemId)) {
                    throw new IllegalArgumentException("Duplicate item id in layout: " + itemId);
                }
                SiteEquipmentItem it = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("SiteEquipmentItem", itemId));
                if (!it.getCategory().getSite().getId().equals(siteId)) {
                    throw new IllegalArgumentException("Item " + itemId + " does not belong to this site");
                }
                it.setCategory(cat);
                it.setLineOrder(order++);
                itemRepository.save(it);
            }
        }
        if (seen.size() != allSiteItemIds.size()) {
            throw new IllegalArgumentException(
                "Layout must mention every item on the site exactly once; expected " + allSiteItemIds.size() + " items, got " + seen.size());
        }
    }

    /** Deletes all equipment portal rows for a site (used when deleting the site). */
    @Transactional
    public void deleteAllForSite(Long siteId) {
        cellRepository.deleteAllForSite(siteId);
        itemRepository.deleteAllForSite(siteId);
        categoryRepository.deleteBySite_Id(siteId);
    }
}
