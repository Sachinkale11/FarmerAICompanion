package com.farmerai.companion.auth.application.dto;

public record RefreshTokenCommand(
        String refreshToken,
        String deviceId,
        String deviceName,
        String platform
) {
}
