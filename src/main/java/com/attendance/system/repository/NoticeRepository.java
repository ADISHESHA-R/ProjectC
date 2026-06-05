package com.attendance.system.repository;

import com.attendance.system.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByCreatedAtDesc();

    @Query("SELECT n FROM Notice n")
    Page<Notice> filterNotices(Pageable pageable);

    @Query(value = "SELECT n.* FROM notices n WHERE " +
           "LOWER(COALESCE(n.message, '')) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY n.updated_at DESC",
           countQuery = "SELECT count(*) FROM notices n WHERE " +
           "LOWER(COALESCE(n.message, '')) LIKE LOWER(CONCAT('%', :search, '%'))",
           nativeQuery = true)
    Page<Notice> searchNotices(@Param("search") String search, Pageable pageable);
}
