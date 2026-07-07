package com.farmerai.companion.auth.domain.exception;

/**
 * Thrown when attempting to register a user with an email that already exists.
 * Maps to HTTP 409 CONFLICT.
 */
public class UserAlreadyExistsException extends AuthenticationModuleException {

    public UserAlreadyExistsException(String email) {
        super("User with email '" + email + "' already exists");
    }
}
