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

    @Query("SELECT n FROM Notice n WHERE " +
           "(:search IS NULL OR LOWER(n.message) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Notice> searchNotices(@Param("search") String search, Pageable pageable);
}
