package com.attendance.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Builds URLs for {@code GET /api/files?path=...} so clients can open stored paths with Bearer auth.
 */
@Service
public class FileUrlService {

    /**
     * Optional absolute API origin (e.g. {@code https://api.example.com}).
     * When empty, returns a root-relative URL so same-origin SPAs resolve it against the browser host.
     */
    @Value("${app.api-public-base-url:}")
    private String apiPublicBaseUrl;

    /**
     * @param storedPath value persisted in DB (e.g. {@code uuid.jpg} or {@code photos/photo_1_....jpg})
     * @return URL including encoded {@code path} query param, or null when path is blank
     */
    public String buildFilesUrl(String storedPath) {
        if (!StringUtils.hasText(storedPath)) {
            return null;
        }
        String encoded = URLEncoder.encode(storedPath.trim(), StandardCharsets.UTF_8);
        String relative = "/api/files?path=" + encoded;
        String base = apiPublicBaseUrl == null ? "" : apiPublicBaseUrl.trim();
        if (base.isEmpty()) {
            return relative;
        }
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + relative;
    }
}
