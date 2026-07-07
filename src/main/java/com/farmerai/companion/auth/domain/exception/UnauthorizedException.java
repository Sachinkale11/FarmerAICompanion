package com.farmerai.companion.auth.domain.exception;

/**
 * Thrown when a user is not authorized to perform an action.
 * Maps to HTTP 403 FORBIDDEN.
 *
 * <p>Distinct from authentication failure — the user IS authenticated
 * but does not have sufficient permissions or their account is not active.</p>
 */
public class UnauthorizedException extends AuthenticationModuleException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("User is not authorized to perform this action");
    }
}
