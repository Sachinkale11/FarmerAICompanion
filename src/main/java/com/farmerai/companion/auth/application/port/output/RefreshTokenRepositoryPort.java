package com.farmerai.companion.auth.application.port.output;

import com.farmerai.companion.auth.domain.entity.RefreshToken;
import com.farmerai.companion.auth.domain.valueobject.TokenValue;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for refresh token persistence operations.
 */
public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(TokenValue tokenValue);

    void revokeAllByUserId(UUID userId);

    int deleteExpiredTokens();
}
