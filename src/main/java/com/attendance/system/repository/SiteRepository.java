package com.attendance.system.repository;

import com.attendance.system.entity.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    @Query("SELECT s FROM Site s WHERE " +
           "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.jobCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(COALESCE(s.address, '')) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<Site> searchSites(
        @Param("search") String search,
        @Param("isActive") Boolean isActive,
        Pageable pageable);
    Optional<Site> findByJobCode(String jobCode);
    boolean existsByJobCode(String jobCode);
    Optional<Site> findByIdAndIsActiveTrue(Long id);
}
