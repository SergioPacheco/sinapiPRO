package com.sinapipro.api.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 120;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Skip actuator and health endpoints
        if (path.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        String key = resolveKey(httpRequest);
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(MAX_REQUESTS_PER_MINUTE, WINDOW_MS));

        if (bucket.tryConsume()) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/problem+json");
            httpResponse.getWriter().write("""
                    {"type":"about:blank","title":"Too Many Requests","status":429,"detail":"Rate limit exceeded. Max %d requests per minute."}
                    """.formatted(MAX_REQUESTS_PER_MINUTE));
        }
    }

    private String resolveKey(HttpServletRequest request) {
        // Prefer token subject, fallback to IP
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return "token:" + auth.substring(7, Math.min(auth.length(), 30));
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        return "ip:" + (forwarded != null ? forwarded.split(",")[0].trim() : request.getRemoteAddr());
    }

    private static class TokenBucket {
        private final int maxTokens;
        private final long windowMs;
        private final AtomicLong tokens;
        private volatile long windowStart;

        TokenBucket(int maxTokens, long windowMs) {
            this.maxTokens = maxTokens;
            this.windowMs = windowMs;
            this.tokens = new AtomicLong(maxTokens);
            this.windowStart = System.currentTimeMillis();
        }

        boolean tryConsume() {
            long now = System.currentTimeMillis();
            if (now - windowStart > windowMs) {
                tokens.set(maxTokens);
                windowStart = now;
            }
            return tokens.decrementAndGet() >= 0;
        }
    }
}
