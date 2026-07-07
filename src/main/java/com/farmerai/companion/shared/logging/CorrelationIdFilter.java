package com.farmerai.companion.shared.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that establishes correlation and request IDs in MDC.
 *
 * <p>
 * Runs first in the filter chain ({@link Order#HIGHEST_PRECEDENCE})
 * to ensure all downstream logging includes these IDs.
 * </p>
 *
 * <p>
 * Correlation ID is extracted from the {@code X-Correlation-ID} header
 * (for cross-service tracing) or generated if not present.
 * </p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extract or generate correlation ID
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (!StringUtils.hasText(correlationId)) {
                correlationId = UUID.randomUUID().toString();
            }

            // Always generate a unique request ID
            String requestId = UUID.randomUUID().toString();

            // Set in MDC
            MDC.put(MdcConstants.CORRELATION_ID, correlationId);
            MDC.put(MdcConstants.REQUEST_ID, requestId);

            // Echo correlation ID back in response header
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC to prevent thread-local leaks
            MDC.remove(MdcConstants.CORRELATION_ID);
            MDC.remove(MdcConstants.REQUEST_ID);
            MDC.remove(MdcConstants.USER_ID);
        }
    }
}
