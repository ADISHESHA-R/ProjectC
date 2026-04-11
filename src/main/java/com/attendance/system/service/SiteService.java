package com.attendance.system.service;

import com.attendance.system.dto.request.CreateSiteRequest;
import com.attendance.system.dto.request.UpdateSiteRequest;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.entity.Site;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.AttendanceRepository;
import com.attendance.system.repository.MachineryRepository;
import com.attendance.system.repository.MachineryUsageRepository;
import com.attendance.system.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final AttendanceRepository attendanceRepository;
    private final MachineryRepository machineryRepository;
    private final MachineryUsageRepository machineryUsageRepository;
    
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
        
        site = siteRepository.save(site);
        return mapToSiteResponse(site);
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
        
        site = siteRepository.save(site);
        return mapToSiteResponse(site);
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
                "Cannot delete this site: machinery catalog entries still reference it. Delete or move those machines first.");
        }
        siteRepository.delete(site);
    }
    
    public SiteResponse getSiteById(Long id) {
        Site site = siteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Site", id));
        return mapToSiteResponse(site);
    }
    
    public SiteResponse getSiteByJobCode(String jobCode) {
        Site site = siteRepository.findByJobCode(jobCode)
            .orElseThrow(() -> new ResourceNotFoundException("Site not found with job code: " + jobCode));
        return mapToSiteResponse(site);
    }
    
    public List<SiteResponse> getAllSites() {
        return siteRepository.findAll().stream()
            .map(this::mapToSiteResponse)
            .collect(Collectors.toList());
    }

    public Page<SiteResponse> searchSites(String search, Boolean isActive, Pageable pageable) {
        String q = (search != null && !search.isBlank()) ? search.trim() : null;
        if (q == null) {
            return siteRepository.filterSites(isActive, pageable).map(this::mapToSiteResponse);
        }
        return siteRepository.searchSites(q, isActive, pageable).map(this::mapToSiteResponse);
    }

    public List<SiteResponse> getActiveSites() {
        return siteRepository.findAll().stream()
            .filter(Site::getIsActive)
            .map(this::mapToSiteResponse)
            .collect(Collectors.toList());
    }
    
    private SiteResponse mapToSiteResponse(Site site) {
        return new SiteResponse(
            site.getId(),
            site.getName(),
            site.getJobCode(),
            site.getAddress(),
            site.getIsActive(),
            site.getCreatedAt(),
            site.getUpdatedAt()
        );
    }
}
