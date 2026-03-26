package com.charter.rewardsapplication.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Filter to log HTTP request and response details.
 * Logs method, URI, request body, response body, status, and execution time.
 */
@Component
@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request, 1024 * 1024);

        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse, System.currentTimeMillis() - startTime);
            wrappedResponse.copyBodyToResponse(); // VERY IMPORTANT
        }
    }

    /**
     * Logs incoming HTTP request details.
     *
     * @param request wrapped request
     */
    private void logRequest(ContentCachingRequestWrapper request) {

        String requestBody = new String(
                request.getContentAsByteArray(),
                StandardCharsets.UTF_8
        );

        log.info(
                "Incoming Request -> method={}, uri={}, query={}, contentType={}, body={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getContentType(),
                isEmpty(requestBody)
        );
    }

    /**
     * Logs outgoing HTTP response details.
     *
     * @param response wrapped response
     * @param durationMs execution time
     */
    private void logResponse(ContentCachingResponseWrapper response, long durationMs) {

        String responseBody = new String(
                response.getContentAsByteArray(),
                StandardCharsets.UTF_8
        );

        log.info(
                "Outgoing Response -> status={}, contentType={}, durationMs={}, body={}",
                response.getStatus(),
                response.getContentType(),
                durationMs,
                isEmpty(responseBody)
        );
    }

    /**
     * Utility method to handle empty/null bodies.
     */
    private String isEmpty(String body) {
        return (body == null || body.isBlank()) ? "N/A" : body;
    }

    /**
     * Skip logging for swagger and H2 console endpoints.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/h2-console");
    }
}