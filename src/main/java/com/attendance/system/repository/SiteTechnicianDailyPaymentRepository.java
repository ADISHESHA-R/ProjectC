package com.attendance.system.repository;

import com.attendance.system.entity.SiteTechnicianDailyPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SiteTechnicianDailyPaymentRepository extends JpaRepository<SiteTechnicianDailyPayment, Long> {

    List<SiteTechnicianDailyPayment> findBySite_IdAndPaymentDateBetweenOrderByPaymentDateAscTechnician_IdAsc(
        Long siteId, LocalDate start, LocalDate end);

    List<SiteTechnicianDailyPayment> findBySite_IdOrderByPaymentDateAscTechnician_IdAsc(Long siteId);

    void deleteBySite_Id(Long siteId);
}
