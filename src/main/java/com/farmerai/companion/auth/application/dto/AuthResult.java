package com.farmerai.companion.auth.application.dto;

import java.util.UUID;

/**
 * Result object returned by authentication use cases.
 *
 * <p>Contains both tokens and basic user information needed for the response.</p>
 */
public record AuthResult(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        String role,
        String status
) {
    /**
     * Creates an AuthResult with "Bearer" token type.
     */
    public static AuthResult of(String accessToken, String refreshToken,
                                 long expiresInSeconds, UUID userId,
                                 String email, String role, String status) {
        return new AuthResult(
                accessToken, refreshToken, "Bearer",
                expiresInSeconds, userId, email, role, status
        );
    }

    /**
     * Creates a token-only AuthResult (for refresh responses without user info).
     */
    public static AuthResult tokensOnly(String accessToken, String refreshToken,
                                         long expiresInSeconds) {
        return new AuthResult(
                accessToken, refreshToken, "Bearer",
                expiresInSeconds, null, null, null, null
        );
    }
}
