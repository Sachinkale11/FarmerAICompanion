package com.farmerai.companion.auth.domain.valueobject;

/**
 * Value Object enum representing the status of a user account.
 */
public enum UserStatus {

    ACTIVE("User account is active and can authenticate"),
    INACTIVE("User account is inactive and cannot authenticate"),
    SUSPENDED("User account is suspended by an administrator");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
