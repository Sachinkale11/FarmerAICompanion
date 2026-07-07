package com.farmerai.companion.auth.domain.exception;

/**
 * Thrown when a token has expired.
 * Maps to HTTP 401 UNAUTHORIZED.
 */
public class TokenExpiredException extends AuthenticationModuleException {

    public TokenExpiredException() {
        super("Token has expired");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
