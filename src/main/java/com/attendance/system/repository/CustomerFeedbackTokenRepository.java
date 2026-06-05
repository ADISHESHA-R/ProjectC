package com.attendance.system.repository;

import com.attendance.system.entity.CustomerFeedbackToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomerFeedbackTokenRepository extends JpaRepository<CustomerFeedbackToken, Long> {

    Optional<CustomerFeedbackToken> findByTokenAndRevokedFalse(String token);

    /**
     * Latest non-revoked invite that is still valid (for building public links with {@code ?token=}).
     */
    Optional<CustomerFeedbackToken> findFirstBySite_IdAndRevokedFalseAndExpiresAtAfterOrderByExpiresAtDesc(
        Long siteId,
        LocalDateTime now);

    void deleteBySite_Id(Long siteId);
}
