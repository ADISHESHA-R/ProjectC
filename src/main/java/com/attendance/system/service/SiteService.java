package com.attendance.system.service;

import com.attendance.system.dto.request.CreateSiteRequest;
import com.attendance.system.dto.request.UpdateSiteRequest;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.entity.CustomerFeedbackToken;
import com.attendance.system.entity.Site;
import com.attendance.system.entity.User;
import com.attendance.system.enums.CertificateClientStatus;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.AttendanceRepository;
import com.attendance.system.repository.CustomerFeedbackTokenRepository;
import com.attendance.system.repository.MachineryRepository;
import com.attendance.system.repository.MachineryUsageRepository;
import com.attendance.system.repository.SiteRepository;
import com.attendance.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteService {

    private final SiteRepository siteRepository;
    private final AttendanceRepository attendanceRepository;
    private final MachineryRepository machineryRepository;
    private final MachineryUsageRepository machineryUsageRepository;
    private final UserRepository userRepository;
    private final CustomerFeedbackTokenRepository customerFeedbackTokenRepository;
    private final CustomerFeedbackService customerFeedbackService;
    private final SiteJobDataService siteJobDataService;

    @Transactional
    public SiteResponse createSite(CreateSiteRequest request) {
        if (siteRepository.existsByJobCode(request.getJobCode())) {
            throw new RuntimeException("Job code already exists");
        }

        Site site = new Site();
        site.setName(request.getName());
        site.setJobCode(request.getJobCode());
        site.setAddress(request.getAddress());
        site.setIsActive(true);
        site.setCustomerName(request.getCustomerName());
        site.setEstimatedDays(request.getEstimatedDays());
        site.setSiteStartDate(request.getSiteStartDate());
        site.setSiteEndDate(request.getSiteEndDate());
        site.setTotalProjectDays(request.getTotalProjectDays());
        site.setCertificateClientStatus(CertificateClientStatus.NONE);

        if (request.getInchargeUserId() != null) {
            User incharge = userRepository.findById(request.getInchargeUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getInchargeUserId()));
            site.setInchargeUser(incharge);
        }
        if (request.getLocationSiteId() != null) {
            Site loc = siteRepository.findById(request.getLocationSiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Site", request.getLocationSiteId()));
            site.setLocationSite(loc);
        }

        site = siteRepository.save(site);
        return mapToSiteResponse(siteRepository.findByIdWithJobMeta(site.getId()).orElse(site));
    }

    @Transactional
    public SiteResponse updateSite(Long id, UpdateSiteRequest request) {
        Site site = siteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Site", id));

        if (request.getName() != null) {
            site.setName(request.getName());
        }
        if (request.getJobCode() != null && !request.getJobCode().equals(site.getJobCode())) {
            if (siteRepository.existsByJobCode(request.getJobCode())) {
                throw new RuntimeException("Job code already exists");
            }
            site.setJobCode(request.getJobCode());
        }
        if (request.getAddress() != null) {
            site.setAddress(request.getAddress());
        }
        if (request.getIsActive() != null) {
            site.setIsActive(request.getIsActive());
        }
        if (request.getCustomerName() != null) {
            site.setCustomerName(request.getCustomerName());
        }
        if (request.getEstimatedDays() != null) {
            site.setEstimatedDays(request.getEstimatedDays());
        }
        if (Boolean.TRUE.equals(request.getClearIncharge())) {
            site.setInchargeUser(null);
        } else if (request.getInchargeUserId() != null) {
            User incharge = userRepository.findById(request.getInchargeUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getInchargeUserId()));
            site.setInchargeUser(incharge);
        }
        if (Boolean.TRUE.equals(request.getClearLocationSite())) {
            site.setLocationSite(null);
        } else if (request.getLocationSiteId() != null) {
            if (request.getLocationSiteId().equals(id)) {
                throw new IllegalArgumentException("locationSiteId cannot reference the same site row");
            }
            Site loc = siteRepository.findById(request.getLocationSiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Site", request.getLocationSiteId()));
            site.setLocationSite(loc);
        }
        if (request.getSiteStartDate() != null) {
            site.setSiteStartDate(request.getSiteStartDate());
        }
        if (request.getSiteEndDate() != null) {
            site.setSiteEndDate(request.getSiteEndDate());
        }
        if (request.getTotalProjectDays() != null) {
            site.setTotalProjectDays(request.getTotalProjectDays());
        }

        site = siteRepository.save(site);
        return mapToSiteResponse(siteRepository.findByIdWithJobMeta(site.getId()).orElse(site));
    }

    @Transactional
    public void deleteSite(Long id) {
        Site site = siteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Site", id));
        if (attendanceRepository.countBySite_Id(id) > 0) {
            throw new IllegalStateException(
                "Cannot delete this site: attendance records still reference it. Remove or reassign those records first.");
        }
        if (machineryUsageRepository.countBySite_Id(id) > 0) {
            throw new IllegalStateException(
                "Cannot delete this site: machinery usage records still reference it.");
        }
        if (machineryRepository.countBySite_Id(id) > 0) {
            throw new IllegalStateException(
                "Cannot delete this site: machinery catalog entries still reference this site. Delete or move those machines first.");
        }
        customerFeedbackTokenRepository.deleteBySite_Id(id);
        siteJobDataService.deleteAllJobData(id);
        siteRepository.delete(site);
    }

    /**
     * Resolves a site key from URLs or query params: numeric DB id, exact job code, or UI slug
     * {@code {name}-{jobCode}} (job code is taken as the segment after the last {@code '-'}, case-insensitive).
     */
    public long resolveSiteIdFromClientKey(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ResourceNotFoundException("Site not found: empty key");
        }
        String key = raw.trim();
        if (key.chars().allMatch(ch -> ch >= '0' && ch <= '9')) {
            long numericId = Long.parseLong(key);
            if (siteRepository.existsById(numericId)) {
                return numericId;
            }
            return siteRepository.findByJobCodeIgnoreCase(key)
                .map(Site::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id or job code: " + key));
        }
        Optional<Site> byFullCode = siteRepository.findByJobCodeIgnoreCase(key);
        if (byFullCode.isPresent()) {
            return byFullCode.get().getId();
        }
        int lastDash = key.lastIndexOf('-');
        if (lastDash > 0 && lastDash < key.length() - 1) {
            String suffix = key.substring(lastDash + 1);
            return siteRepository.findByJobCodeIgnoreCase(suffix)
                .map(Site::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found: " + key));
        }
        throw new ResourceNotFoundException("Site not found: " + key);
    }

    public SiteResponse getSiteById(Long id) {
        Site site = siteRepository.findByIdWithJobMeta(id)
            .orElseThrow(() -> new ResourceNotFoundException("Site", id));
        return mapToSiteResponse(site);
    }

    public Site getSiteEntityById(Long id) {
        return siteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Site", id));
    }

    public SiteResponse getSiteByJobCode(String jobCode) {
        Site site = siteRepository.findByJobCode(jobCode)
            .orElseThrow(() -> new ResourceNotFoundException("Site not found with job code: " + jobCode));
        Site loaded = siteRepository.findByIdWithJobMeta(site.getId()).orElse(site);
        return mapToSiteResponse(loaded);
    }

    public List<SiteResponse> getAllSites() {
        return siteRepository.findAll().stream()
            .map(this::mapToSiteResponse)
            .collect(Collectors.toList());
    }

    public List<SiteResponse> getAllSitesSortedByName() {
        return siteRepository.findAllWithJobMetaOrderByNameAsc().stream()
            .map(this::mapToSiteResponse)
            .collect(Collectors.toList());
    }

    public Page<SiteResponse> searchSites(String search, Boolean isActive, Pageable pageable) {
        String q = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<Site> page;
        if (q == null) {
            page = siteRepository.filterSites(isActive, pageable);
        } else {
            page = siteRepository.searchSites(q, isActive, pageable);
        }
        return page.map(s -> {
            Site hydrated = siteRepository.findByIdWithJobMeta(s.getId()).orElse(s);
            return mapToSiteResponse(hydrated);
        });
    }

    public List<SiteResponse> getActiveSites() {
        return siteRepository.findAllWithJobMetaOrderByNameAsc().stream()
            .filter(Site::getIsActive)
            .map(this::mapToSiteResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public SiteResponse saveWizardData(Long siteId, String json) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        site.setWizardData(json);
        siteRepository.save(site);
        siteJobDataService.trySyncChallengeLinesFromWizardString(siteId, json);
        customerFeedbackService.tryMergeCustomerFeedbackFromWizard(siteId, json);
        return mapToSiteResponse(siteRepository.findByIdWithJobMeta(siteId).orElse(site));
    }

    public String getWizardData(Long siteId) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        return site.getWizardData() != null ? site.getWizardData() : "{}";
    }

    private SiteResponse mapToSiteResponse(Site site) {
        Long inchargeId = null;
        String inchargeName = null;
        String inchargeEmpId = null;
        if (site.getInchargeUser() != null) {
            inchargeId = site.getInchargeUser().getId();
            inchargeName = site.getInchargeUser().getName();
            inchargeEmpId = site.getInchargeUser().getEmployeeId();
        }
        Long locId = null;
        String locLabel = null;
        if (site.getLocationSite() != null) {
            locId = site.getLocationSite().getId();
            Site ref = site.getLocationSite();
            locLabel = ref.getName() + (ref.getAddress() != null && !ref.getAddress().isBlank()
                ? " — " + ref.getAddress() : "");
        }
        CertificateClientStatus cert = site.getCertificateClientStatus() != null
            ? site.getCertificateClientStatus() : CertificateClientStatus.NONE;
        String feedbackToken = null;
        LocalDateTime feedbackTokenExpires = null;
        Optional<CustomerFeedbackToken> inviteOpt = customerFeedbackTokenRepository
            .findFirstBySite_IdAndRevokedFalseAndExpiresAtAfterOrderByExpiresAtDesc(site.getId(), LocalDateTime.now());
        if (inviteOpt.isPresent()) {
            CustomerFeedbackToken t = inviteOpt.get();
            feedbackToken = t.getToken();
            feedbackTokenExpires = t.getExpiresAt();
        }
        return new SiteResponse(
            site.getId(),
            site.getName(),
            site.getJobCode(),
            site.getAddress(),
            site.getIsActive(),
            site.getCustomerName(),
            site.getEstimatedDays(),
            inchargeId,
            inchargeName,
            inchargeEmpId,
            locId,
            locLabel,
            site.getSiteStartDate(),
            site.getSiteEndDate(),
            site.getTotalProjectDays(),
            cert,
            site.getCustomerFeedbackApprovedAt(),
            feedbackToken,
            feedbackTokenExpires,
            site.getCustomerFeedbackPayload(),
            site.getCreatedAt(),
            site.getUpdatedAt()
        );
    }
}
