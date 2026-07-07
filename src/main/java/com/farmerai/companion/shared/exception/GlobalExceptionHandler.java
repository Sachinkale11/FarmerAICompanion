package com.farmerai.companion.shared.exception;

import com.farmerai.companion.auth.domain.exception.InvalidCredentialsException;
import com.farmerai.companion.auth.domain.exception.InvalidTokenException;
import com.farmerai.companion.auth.domain.exception.TokenExpiredException;
import com.farmerai.companion.auth.domain.exception.UnauthorizedException;
import com.farmerai.companion.auth.domain.exception.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.List;
import com.farmerai.companion.shared.response.ApiResponse;

/**
 * Global exception handler for the entire application.
 *
 * <p>
 * Catches domain exceptions and maps them to appropriate HTTP responses
 * with consistent {@link ErrorResponse} format.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        // ======================== Auth Domain Exceptions ========================

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleUserAlreadyExists(
                        UserAlreadyExistsException ex, WebRequest request) {
                log.warn("User already exists: {}", ex.getMessage());
                return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidCredentials(
                        InvalidCredentialsException ex, WebRequest request) {
                log.warn("Invalid credentials attempt");
                // Deliberately vague message to prevent user enumeration
                return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", request);
        }

        @ExceptionHandler(InvalidTokenException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidToken(
                        InvalidTokenException ex, WebRequest request) {
                log.warn("Invalid token: {}", ex.getMessage());
                return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
        }

        @ExceptionHandler(TokenExpiredException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleTokenExpired(
                        TokenExpiredException ex, WebRequest request) {
                log.warn("Token expired: {}", ex.getMessage());
                return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorized(
                        UnauthorizedException ex, WebRequest request) {
                log.warn("Unauthorized: {}", ex.getMessage());
                return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
        }

        // ======================== Validation Exceptions ========================

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationErrors(
                        MethodArgumentNotValidException ex, WebRequest request) {
                log.warn("Validation failed: {}", ex.getMessage());

                List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                                .getFieldErrors().stream()
                                .map(fe -> new ErrorResponse.FieldError(
                                                fe.getField(),
                                                fe.getDefaultMessage(),
                                                fe.getRejectedValue()))
                                .toList();

                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "Validation failed",
                                extractPath(request),
                                MDC.get("correlationId"),
                                fieldErrors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(errorResponse, "Validation failed"));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleConstraintViolation(
                        ConstraintViolationException ex, WebRequest request) {
                log.warn("Constraint violation: {}", ex.getMessage());

                List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                                .map(cv -> new ErrorResponse.FieldError(
                                                cv.getPropertyPath().toString(),
                                                cv.getMessage(),
                                                cv.getInvalidValue()))
                                .toList();

                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "Validation failed",
                                extractPath(request),
                                MDC.get("correlationId"),
                                fieldErrors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(errorResponse, "Validation failed"));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgument(
                        IllegalArgumentException ex, WebRequest request) {
                log.warn("Illegal argument: {}", ex.getMessage());
                return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        }

        // ======================== Catch-All ========================

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<ErrorResponse>> handleGenericException(
                        Exception ex, WebRequest request) {
                log.error("Unexpected error: {}", ex.getMessage(), ex);
                // Never expose internal error details
                return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred", request);
        }

        // ======================== Helper ========================

        private ResponseEntity<ApiResponse<ErrorResponse>> buildResponse(HttpStatus status,
                        String message,
                        WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                extractPath(request),
                                MDC.get("correlationId"),
                                null);
                return ResponseEntity.status(status).body(ApiResponse.error(errorResponse, message));
        }

        private String extractPath(WebRequest request) {
                return request.getDescription(false).replace("uri=", "");
        }
}
