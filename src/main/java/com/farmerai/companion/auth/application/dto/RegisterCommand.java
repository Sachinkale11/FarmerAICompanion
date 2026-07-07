package com.farmerai.companion.auth.application.dto;

/**
 * Command object for user registration use case.
 *
 * <p>Decouples presentation-layer DTOs from the application layer.
 * Enables future reuse (e.g., admin-initiated registration).</p>
 */
public record RegisterCommand(
        String email,
        String password,
        String roleName,
        String deviceId,
        String deviceName,
        String platform
) {
}
