package com.farmerai.companion.auth.application.dto;

/**
 * Command object for user login use case.
 */
public record LoginCommand(
        String email,
        String password,
        String deviceId,
        String deviceName,
        String platform
) {
}
