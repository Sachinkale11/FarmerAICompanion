package com.farmerai.companion.auth.domain.repository;

import com.farmerai.companion.auth.domain.entity.RefreshToken;
import com.farmerai.companion.auth.domain.valueobject.TokenValue;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for RefreshToken persistence.
 */
public interface RefreshTokenRepository {

    /**
     * Persists a refresh token (insert or update).
     */
    RefreshToken save(RefreshToken refreshToken);

    /**
     * Finds a refresh token by its opaque token value.
     */
    Optional<RefreshToken> findByToken(TokenValue tokenValue);

    /**
     * Revokes all refresh tokens belonging to a user.
     * Used for security events (compromised token detected, password change, etc.).
     *
     * @param userId the user whose tokens should be revoked
     */
    void revokeAllByUserId(UUID userId);

    /**
     * Deletes all expired refresh tokens.
     * Used by a scheduled cleanup job.
     *
     * @return the number of deleted tokens
     */
    int deleteExpiredTokens();
}
