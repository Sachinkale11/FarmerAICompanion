package com.farmerai.companion.shared.logging;

/**
 * Constants for MDC (Mapped Diagnostic Context) keys.
 *
 * <p>
 * Used consistently across filters, interceptors, and application code
 * for structured logging.
 * </p>
 */
public final class MdcConstants {

    /**
     * Traces a request across services (from X-Correlation-ID header or generated).
     */
    public static final String CORRELATION_ID = "correlationId";

    /** Uniquely identifies each HTTP request. */
    public static final String REQUEST_ID = "requestId";

    /** Associates log entries with a user (set from JWT claims). */
    public static final String USER_ID = "userId";

    private MdcConstants() {
        // Utility class — no instantiation
    }
}
