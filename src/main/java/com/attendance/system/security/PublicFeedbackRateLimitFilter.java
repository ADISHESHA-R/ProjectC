package com.attendance.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple per-IP rate limit for anonymous public feedback endpoints (deploy safety).
 * Tune via {@code app.public-feedback-rate-limit-per-minute} (default 120).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicFeedbackRateLimitFilter extends OncePerRequestFilter {

    private static final ConcurrentHashMap<String, AtomicInteger> MINUTE_BUCKETS = new ConcurrentHashMap<>();

    @org.springframework.beans.factory.annotation.Value("${app.public-feedback-rate-limit-per-minute:120}")
    private int maxPerMinute;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/public/feedback")) {
            filterChain.doFilter(request, response);
            return;
        }

        long minute = System.currentTimeMillis() / 60_000L;
        String ip = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
        String key = ip + ":" + minute;
        AtomicInteger cnt = MINUTE_BUCKETS.computeIfAbsent(key, k -> new AtomicInteger(0));
        if (cnt.incrementAndGet() > maxPerMinute) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Try again shortly.\"}");
            return;
        }
        if (MINUTE_BUCKETS.size() > 50_000) {
            MINUTE_BUCKETS.clear();
        }
        filterChain.doFilter(request, response);
    }
}
