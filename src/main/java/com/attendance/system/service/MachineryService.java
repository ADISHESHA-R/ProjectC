package com.attendance.system.service;

import com.attendance.system.dto.request.CreateMachineryRequest;
import com.attendance.system.dto.request.SaveUsageSelectionRequest;
import com.attendance.system.dto.request.UpdateMachineryRequest;
import com.attendance.system.dto.request.UsageLineRequest;
import com.attendance.system.dto.response.*;
import com.attendance.system.entity.Machinery;
import com.attendance.system.entity.MachineryUsage;
import com.attendance.system.entity.Site;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.MachineryRepository;
import com.attendance.system.repository.MachineryUsageRepository;
import com.attendance.system.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class MachineryService {

    private final MachineryRepository machineryRepository;
    private final MachineryUsageRepository machineryUsageRepository;
    private final SiteRepository siteRepository;
    private final FileStorageService fileStorageService;

    public List<MachineryResponse> listMachineryForSite(Long siteId) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        return machineryRepository.findBySite_IdOrderByCodeAsc(siteId).stream()
            .map(m -> toMachineryResponse(m, site.getId(), site.getName()))
            .toList();
    }

    @Transactional
    public MachineryResponse createMachinery(CreateMachineryRequest request, MultipartFile imageFile) throws IOException {
        if (machineryRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
            throw new IllegalArgumentException("Machinery code already exists: " + request.getCode());
        }
        Site site = siteRepository.findById(request.getSiteId())
            .orElseThrow(() -> new ResourceNotFoundException("Site", request.getSiteId()));

        Machinery m = new Machinery();
        m.setCode(request.getCode().trim());
        m.setName(request.getName().trim());
        m.setItemDescription(request.getItemDescription());
        m.setJobCode(request.getJobCode() != null ? request.getJobCode().trim() : null);
        String uom = request.getDefaultUom();
        if (uom == null || uom.isBlank()) {
            uom = "HOUR";
        }
        m.setDefaultUom(uom.trim());
        m.setSite(site);
        m.setSerialNumber(request.getSerialNumber());
        m.setModel(request.getModel());
        if (request.getStatus() != null) {
            m.setStatus(request.getStatus());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            m.setImagePath(fileStorageService.storeMachineryImage(imageFile));
        }

        m = machineryRepository.save(m);

        if (request.getMarkUsedOnDate() != null) {
            addUsageLine(site, request.getMarkUsedOnDate(), m, BigDecimal.ONE, m.getDefaultUom(), null, null);
        }

        return toMachineryResponse(m, site.getId(), site.getName());
    }

    private void addUsageLine(Site site, LocalDate date, Machinery machinery, BigDecimal qty, String uom,
                              String jobCode, String notes) {
        MachineryUsage u = new MachineryUsage();
        u.setSite(site);
        u.setUsageDate(date);
        u.setMachinery(machinery);
        u.setQty(qty);
        u.setUom(uom);
        u.setJobCode(jobCode);
        u.setNotes(notes);
        machineryUsageRepository.save(u);
    }

    @Transactional
    public MachineryResponse updateMachinery(Long id, UpdateMachineryRequest request, MultipartFile imageFile) throws IOException {
        Machinery m = machineryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Machinery", id));
        if (request.getName() != null) {
            m.setName(request.getName().trim());
        }
        if (request.getItemDescription() != null) {
            m.setItemDescription(request.getItemDescription());
        }
        if (request.getJobCode() != null) {
            m.setJobCode(request.getJobCode().isBlank() ? null : request.getJobCode().trim());
        }
        if (request.getDefaultUom() != null && !request.getDefaultUom().isBlank()) {
            m.setDefaultUom(request.getDefaultUom().trim());
        }
        if (request.getSerialNumber() != null) {
            m.setSerialNumber(request.getSerialNumber());
        }
        if (request.getModel() != null) {
            m.setModel(request.getModel());
        }
        if (request.getStatus() != null) {
            m.setStatus(request.getStatus());
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            if (m.getImagePath() != null) {
                try {
                    fileStorageService.deleteFile(m.getImagePath());
                } catch (IOException ignored) {
                    // best-effort delete old image
                }
            }
            m.setImagePath(fileStorageService.storeMachineryImage(imageFile));
        }
        Machinery saved = machineryRepository.save(m);
        Long siteId = saved.getSite().getId();
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        return toMachineryResponse(saved, site.getId(), site.getName());
    }

    @Transactional
    public void deleteMachinery(Long id) {
        Machinery m = machineryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Machinery", id));
        if (machineryUsageRepository.existsByMachinery_Id(id)) {
            throw new IllegalStateException("Cannot delete machinery with usage history; retire it instead.");
        }
        if (m.getImagePath() != null) {
            try {
                fileStorageService.deleteFile(m.getImagePath());
            } catch (IOException ignored) {
            }
        }
        machineryRepository.delete(m);
    }

    public DailyUsageSelectionResponse getDailySelection(Long siteId, LocalDate date) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        List<Machinery> catalog = machineryRepository.findBySite_IdOrderByCodeAsc(siteId);
        Map<Long, MachineryUsage> byMachinery = machineryUsageRepository
            .findBySite_IdAndUsageDateOrderByMachinery_CodeAsc(siteId, date).stream()
            .collect(Collectors.toMap(u -> u.getMachinery().getId(), u -> u, (a, b) -> a));

        List<MachineryUsageLineResponse> lines = catalog.stream()
            .map(mach -> {
                MachineryUsage u = byMachinery.get(mach.getId());
                BigDecimal qty = u != null ? u.getQty() : BigDecimal.ZERO;
                String uom = u != null ? u.getUom() : mach.getDefaultUom();
                return new MachineryUsageLineResponse(
                    mach.getId(),
                    mach.getCode(),
                    mach.getName(),
                    mach.getItemDescription(),
                    mach.getStatus(),
                    mach.getImagePath(),
                    qty,
                    uom,
                    u != null ? u.getJobCode() : null,
                    u != null ? u.getNotes() : null
                );
            })
            .toList();

        return new DailyUsageSelectionResponse(site.getId(), date, lines);
    }

    @Transactional
    public void saveUsageSelection(SaveUsageSelectionRequest request) {
        Site site = siteRepository.findById(request.getSiteId())
            .orElseThrow(() -> new ResourceNotFoundException("Site", request.getSiteId()));
        LocalDate date = request.getDate();

        machineryUsageRepository.deleteAllForSiteAndDate(site.getId(), date);

        if (request.getLines() == null || request.getLines().isEmpty()) {
            return;
        }

        // One row per (site, date, machinery): merge duplicate machineryIds (last line wins)
        Map<Long, UsageLineRequest> uniqueByMachinery = new LinkedHashMap<>();
        for (UsageLineRequest line : request.getLines()) {
            if (line.getMachineryId() == null) {
                continue;
            }
            if (line.getQty() != null && line.getQty().compareTo(BigDecimal.ZERO) > 0) {
                uniqueByMachinery.put(line.getMachineryId(), line);
            }
        }

        for (UsageLineRequest line : uniqueByMachinery.values()) {
            Machinery mach = machineryRepository.findById(line.getMachineryId())
                .orElseThrow(() -> new ResourceNotFoundException("Machinery", line.getMachineryId()));
            if (!mach.getSite().getId().equals(site.getId())) {
                throw new IllegalArgumentException("Machinery " + mach.getCode() + " does not belong to this site");
            }
            String uom = line.getUom().trim();
            MachineryUsage u = new MachineryUsage();
            u.setSite(site);
            u.setUsageDate(date);
            u.setMachinery(mach);
            u.setQty(line.getQty());
            u.setUom(uom);
            u.setJobCode(line.getJobCode() != null && !line.getJobCode().isBlank() ? line.getJobCode().trim() : null);
            u.setNotes(line.getNotes());
            machineryUsageRepository.save(u);
        }
    }

    public MonthlyUsageSummaryResponse getMonthlySummary(Long siteId, int year, int month) {
        siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());

        List<MachineryUsage> usages = machineryUsageRepository.findBySiteAndDateRangeWithMachinery(siteId, start, end);

        Map<LocalDate, List<MachineryUsage>> byDay = usages.stream()
            .collect(Collectors.groupingBy(MachineryUsage::getUsageDate));

        List<MonthlyDaySummaryResponse> days = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            List<MachineryUsage> dayRows = byDay.getOrDefault(d, List.of());
            if (dayRows.isEmpty()) {
                continue;
            }
            List<String> codes = dayRows.stream()
                .sorted(Comparator.comparing(u -> u.getMachinery().getCode()))
                .map(u -> u.getMachinery().getCode())
                .toList();
            days.add(new MonthlyDaySummaryResponse(d, dayRows.size(), codes));
        }

        return new MonthlyUsageSummaryResponse(siteId, year, month, days);
    }

    public YearlyUsageSummaryResponse getYearlySummary(Long siteId, int year) {
        siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));

        List<YearlyMonthSummaryResponse> months = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            LocalDate start = LocalDate.of(year, m, 1);
            LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
            List<MachineryUsage> usages = machineryUsageRepository.findBySiteAndDateRangeWithMachinery(siteId, start, end);

            Map<LocalDate, List<MachineryUsage>> byDay = usages.stream()
                .collect(Collectors.groupingBy(MachineryUsage::getUsageDate));
            int daysWithUsage = byDay.size();
            long totalMachineDays = byDay.values().stream().mapToLong(List::size).sum();

            Map<String, Long> machineDayScore = new HashMap<>();
            Map<String, BigDecimal> qtySum = new HashMap<>();
            for (MachineryUsage u : usages) {
                String code = u.getMachinery().getCode();
                machineDayScore.merge(code, 1L, Long::sum);
                qtySum.merge(code, u.getQty(), BigDecimal::add);
            }
            List<String> top = machineDayScore.entrySet().stream()
                .sorted((e1, e2) -> {
                    int c = Long.compare(e2.getValue(), e1.getValue());
                    if (c != 0) {
                        return c;
                    }
                    return qtySum.getOrDefault(e2.getKey(), BigDecimal.ZERO)
                        .compareTo(qtySum.getOrDefault(e1.getKey(), BigDecimal.ZERO));
                })
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

            String monthName = Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            months.add(new YearlyMonthSummaryResponse(m, monthName, daysWithUsage, totalMachineDays, top));
        }

        return new YearlyUsageSummaryResponse(siteId, year, months);
    }

    private MachineryResponse toMachineryResponse(Machinery m, Long siteId, String siteName) {
        return new MachineryResponse(
            m.getId(),
            m.getCode(),
            m.getName(),
            m.getItemDescription(),
            m.getJobCode(),
            m.getDefaultUom(),
            siteId,
            siteName,
            m.getImagePath(),
            m.getSerialNumber(),
            m.getModel(),
            m.getStatus(),
            m.getCreatedAt(),
            m.getUpdatedAt()
        );
    }
}
