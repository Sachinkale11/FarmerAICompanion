package com.farmerai.companion.auth.domain.exception;

/**
 * Thrown when login credentials (email or password) are invalid.
 * Maps to HTTP 401 UNAUTHORIZED.
 *
 * <p>The message is deliberately vague to prevent user enumeration attacks.</p>
 */
public class InvalidCredentialsException extends AuthenticationModuleException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
