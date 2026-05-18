package com.attendance.system.repository;

import com.attendance.system.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    List<Designation> findByActiveTrueOrderBySortOrderAscIdAsc();
}
