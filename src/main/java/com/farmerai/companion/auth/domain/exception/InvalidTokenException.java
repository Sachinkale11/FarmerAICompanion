package com.farmerai.companion.auth.domain.exception;

/**
 * Thrown when a provided token (refresh token) is invalid, revoked, or not found.
 * Maps to HTTP 401 UNAUTHORIZED.
 */
public class InvalidTokenException extends AuthenticationModuleException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException() {
        super("Invalid or revoked token");
    }
}
