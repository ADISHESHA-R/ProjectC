package com.attendance.system.repository;

import com.attendance.system.entity.CustomerFeedbackToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerFeedbackTokenRepository extends JpaRepository<CustomerFeedbackToken, Long> {

    Optional<CustomerFeedbackToken> findByTokenAndRevokedFalse(String token);

    void deleteBySite_Id(Long siteId);
}
