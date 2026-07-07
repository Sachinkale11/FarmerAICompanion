package com.farmerai.companion.auth.domain.exception;

/**
 * Abstract base exception for all authentication module domain exceptions.
 *
 * <p>Provides a common type for the global exception handler to catch
 * and convert to appropriate HTTP responses.</p>
 */
public abstract class AuthenticationModuleException extends RuntimeException {

    protected AuthenticationModuleException(String message) {
        super(message);
    }

    protected AuthenticationModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
