package com.attendance.system.service;

import com.attendance.system.dto.request.CustomerFeedbackSubmitRequest;
import com.attendance.system.dto.response.FeedbackInviteResponse;
import com.attendance.system.dto.response.PublicFeedbackContextResponse;
import com.attendance.system.entity.CustomerFeedbackToken;
import com.attendance.system.entity.Site;
import com.attendance.system.enums.CertificateClientStatus;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.CustomerFeedbackTokenRepository;
import com.attendance.system.repository.SiteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerFeedbackService {

    private final CustomerFeedbackTokenRepository tokenRepository;
    private final SiteRepository siteRepository;
    private final CertificatePdfService certificatePdfService;
    private final ObjectMapper objectMapper;

    @Value("${app.public-feedback-token-valid-days:30}")
    private int tokenValidDays;

    @Transactional
    public FeedbackInviteResponse createInvite(Long siteId) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        String token = UUID.randomUUID().toString().replace("-", "")
            + UUID.randomUUID().toString().replace("-", "");

        CustomerFeedbackToken row = new CustomerFeedbackToken();
        row.setToken(token);
        row.setSite(site);
        row.setExpiresAt(LocalDateTime.now().plusDays(tokenValidDays));
        row.setRevoked(false);
        tokenRepository.save(row);

        return new FeedbackInviteResponse(token, row.getExpiresAt(), "/api/public/feedback/" + token);
    }

    @Transactional(readOnly = true)
    public PublicFeedbackContextResponse getPublicContext(String token) {
        CustomerFeedbackToken row = tokenRepository.findByTokenAndRevokedFalse(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid or expired link"));
        Site site = row.getSite();
        boolean expired = row.getExpiresAt().isBefore(LocalDateTime.now());
        return new PublicFeedbackContextResponse(
            site.getJobCode(),
            site.getCustomerName(),
            site.getName(),
            site.getCertificateClientStatus() != null ? site.getCertificateClientStatus() : CertificateClientStatus.NONE,
            expired,
            Boolean.TRUE.equals(row.getRevoked())
        );
    }

    @Transactional
    public void submitFeedback(String token, CustomerFeedbackSubmitRequest body) {
        CustomerFeedbackToken row = requireValidToken(token);
        Site site = row.getSite();
        if (site.getCertificateClientStatus() == CertificateClientStatus.APPROVED_BY_CLIENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback already approved");
        }
        try {
            ObjectNode root = objectMapper.createObjectNode();
            if (body.getName() != null) root.put("name", body.getName());
            if (body.getEmail() != null) root.put("email", body.getEmail());
            if (body.getPhone() != null) root.put("phone", body.getPhone());
            if (body.getCompanyName() != null) root.put("companyName", body.getCompanyName());
            if (body.getProductQuality() != null) root.put("productQuality", body.getProductQuality());
            if (body.getCustomerService() != null) root.put("customerService", body.getCustomerService());
            if (body.getMachiningQuality() != null) root.put("machiningQuality", body.getMachiningQuality());
            if (body.getPricing() != null) root.put("pricing", body.getPricing());
            if (body.getShippingDelivery() != null) root.put("shippingDelivery", body.getShippingDelivery());
            if (body.getOtherCategoryNote() != null) root.put("otherCategoryNote", body.getOtherCategoryNote());
            if (body.getSpecificFeedback() != null) root.put("specificFeedback", body.getSpecificFeedback());
            if (body.getSuggestions() != null) root.put("suggestions", body.getSuggestions());
            if (body.getLikelihoodRecommend() != null) root.put("likelihoodRecommend", body.getLikelihoodRecommend());
            if (body.getAdditionalComments() != null) root.put("additionalComments", body.getAdditionalComments());
            if (body.getExtra() != null && !body.getExtra().isNull()) {
                root.set("extra", body.getExtra());
            }
            root.put("submittedAt", LocalDateTime.now().toString());
            site.setCustomerFeedbackPayload(objectMapper.writeValueAsString(root));
            site.setCertificateClientStatus(CertificateClientStatus.FEEDBACK_SUBMITTED);
            siteRepository.save(site);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not store feedback: " + e.getMessage());
        }
    }

    @Transactional
    public byte[] approveAndBuildPdf(String token) {
        CustomerFeedbackToken row = requireValidToken(token);
        Site site = row.getSite();
        if (site.getCertificateClientStatus() == CertificateClientStatus.APPROVED_BY_CLIENT) {
            return regeneratePdf(site);
        }
        if (site.getCertificateClientStatus() != CertificateClientStatus.FEEDBACK_SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submit feedback before approving the certificate");
        }
        String projectLine = site.getName();
        try {
            if (site.getCustomerFeedbackPayload() != null) {
                JsonNode n = objectMapper.readTree(site.getCustomerFeedbackPayload());
                if (n.has("specificFeedback") && !n.get("specificFeedback").asText().isBlank()) {
                    projectLine = n.get("specificFeedback").asText();
                }
            }
        } catch (Exception ignored) {
            // keep default
        }
        site.setCertificateClientStatus(CertificateClientStatus.APPROVED_BY_CLIENT);
        site.setCustomerFeedbackApprovedAt(LocalDateTime.now());
        siteRepository.save(site);
        return regeneratePdfInternal(site, projectLine);
    }

    private byte[] regeneratePdf(Site site) {
        String projectLine = site.getName();
        try {
            if (site.getCustomerFeedbackPayload() != null) {
                JsonNode n = objectMapper.readTree(site.getCustomerFeedbackPayload());
                if (n.has("specificFeedback")) {
                    projectLine = n.get("specificFeedback").asText(projectLine);
                }
            }
        } catch (Exception ignored) {
        }
        return regeneratePdfInternal(site, projectLine);
    }

    private byte[] regeneratePdfInternal(Site site, String projectLine) {
        try {
            return certificatePdfService.buildWorkCompletionCertificate(site, projectLine);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PDF generation failed: " + e.getMessage());
        }
    }

    private CustomerFeedbackToken requireValidToken(String token) {
        CustomerFeedbackToken row = tokenRepository.findByTokenAndRevokedFalse(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid or expired link"));
        if (row.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Link expired");
        }
        return row;
    }

    @Transactional(readOnly = true)
    public String getFeedbackPayloadForAdmin(Long siteId) {
        Site site = siteRepository.findById(siteId)
            .orElseThrow(() -> new ResourceNotFoundException("Site", siteId));
        return site.getCustomerFeedbackPayload();
    }
}
