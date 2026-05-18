package com.attendance.system.repository;

import com.attendance.system.entity.SiteChallengeLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteChallengeLineRepository extends JpaRepository<SiteChallengeLine, Long> {

    List<SiteChallengeLine> findBySite_IdOrderByLineOrderAscIdAsc(Long siteId);

    void deleteBySite_Id(Long siteId);
}
