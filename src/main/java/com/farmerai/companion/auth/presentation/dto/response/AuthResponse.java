package com.farmerai.companion.auth.presentation.dto.response;

/**
 * Response DTO for authentication operations (register, login, refresh).
 *
 * <p>
 * Contains the token pair and basic user information.
 * </p>
 */
public record AuthResponse(
                String accessToken,
                String refreshToken,
                String tokenType,
                long expiresIn,
                UserResponse user) {
}
