package com.farmerai.companion.shared.exception;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response format for all API errors.
 *
 * <p>
 * Consistent structure across the entire application:
 * </p>
 * <ul>
 * <li>Validation errors include {@code fieldErrors}</li>
 * <li>All responses include a {@code correlationId} for tracing</li>
 * </ul>
 */
public record ErrorResponse(
                Instant timestamp,
                int status,
                String error,
                String message,
                String path,
                String correlationId,
                List<FieldError> fieldErrors) {

        /**
         * Represents a single field validation error.
         */
        public record FieldError(
                        String field,
                        String message,
                        Object rejectedValue) {
        }
}
