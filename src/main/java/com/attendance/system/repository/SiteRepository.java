package com.attendance.system.repository;

import com.attendance.system.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    Optional<Site> findByJobCode(String jobCode);
    boolean existsByJobCode(String jobCode);
    Optional<Site> findByIdAndIsActiveTrue(Long id);
}
