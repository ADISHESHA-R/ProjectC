package com.attendance.system.service;

import com.attendance.system.dto.request.CreateSiteRequest;
import com.attendance.system.dto.request.UpdateSiteRequest;
import com.attendance.system.dto.response.SiteResponse;
import com.attendance.system.entity.Site;
import com.attendance.system.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteService {
    
    private final SiteRepository siteRepository;
    
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
            .orElseThrow(() -> new RuntimeException("Site not found"));
        
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
            .orElseThrow(() -> new RuntimeException("Site not found"));
        siteRepository.delete(site);
    }
    
    public SiteResponse getSiteById(Long id) {
        Site site = siteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Site not found"));
        return mapToSiteResponse(site);
    }
    
    public SiteResponse getSiteByJobCode(String jobCode) {
        Site site = siteRepository.findByJobCode(jobCode)
            .orElseThrow(() -> new RuntimeException("Site not found with job code: " + jobCode));
        return mapToSiteResponse(site);
    }
    
    public List<SiteResponse> getAllSites() {
        return siteRepository.findAll().stream()
            .map(this::mapToSiteResponse)
            .collect(Collectors.toList());
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
