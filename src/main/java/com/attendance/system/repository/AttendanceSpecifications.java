package com.attendance.system.repository;

import com.attendance.system.entity.Attendance;
import com.attendance.system.enums.AttendanceStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic predicates avoid {@code (:param IS NULL OR ...)} in JPQL/SQL, which breaks PostgreSQL
 * parameter typing for optional filters.
 */
public final class AttendanceSpecifications {

    private AttendanceSpecifications() {
    }

    public static Specification<Attendance> withAdminFilters(
            LocalDate date,
            Long employeeId,
            Long siteId,
            String jobCode,
            AttendanceStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (date != null) {
                predicates.add(cb.equal(root.get("date"), date));
            }
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employee").get("id"), employeeId));
            }
            if (siteId != null) {
                predicates.add(cb.equal(root.get("site").get("id"), siteId));
            }
            if (jobCode != null && !jobCode.isBlank()) {
                predicates.add(cb.equal(root.get("site").get("jobCode"), jobCode.trim()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
