package com.attendance.system.repository;

import com.attendance.system.entity.SiteToolIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteToolIssueRepository extends JpaRepository<SiteToolIssue, Long> {

    List<SiteToolIssue> findBySite_IdOrderByLineOrderAsc(Long siteId);

    void deleteBySite_Id(Long siteId);
}
