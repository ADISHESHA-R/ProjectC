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

    /** List/filter only — no LOWER() on text columns (avoids PostgreSQL lower(bytea) when columns are bytea). */
    @Query("SELECT s FROM Site s WHERE (:isActive IS NULL OR s.isActive = :isActive)")
    Page<Site> filterSites(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query(value = "SELECT s.* FROM sites s WHERE " +
           "(LOWER(CAST(s.name AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(s.job_code AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(COALESCE(s.address, '') AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isActive IS NULL OR s.is_active = :isActive)",
           countQuery = "SELECT count(*) FROM sites s WHERE " +
           "(LOWER(CAST(s.name AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(s.job_code AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(COALESCE(s.address, '') AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isActive IS NULL OR s.is_active = :isActive)",
           nativeQuery = true)
    Page<Site> searchSites(
        @Param("search") String search,
        @Param("isActive") Boolean isActive,
        Pageable pageable);
    Optional<Site> findByJobCode(String jobCode);
    boolean existsByJobCode(String jobCode);
    Optional<Site> findByIdAndIsActiveTrue(Long id);
}
