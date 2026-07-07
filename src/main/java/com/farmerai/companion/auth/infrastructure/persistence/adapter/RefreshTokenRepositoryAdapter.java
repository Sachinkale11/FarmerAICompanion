package com.farmerai.companion.auth.infrastructure.persistence.adapter;

import com.farmerai.companion.auth.application.port.output.RefreshTokenRepositoryPort;
import com.farmerai.companion.auth.domain.entity.RefreshToken;
import com.farmerai.companion.auth.domain.valueobject.TokenValue;
import com.farmerai.companion.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.farmerai.companion.auth.infrastructure.persistence.mapper.AuthPersistenceMapper;
import com.farmerai.companion.auth.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the application-layer {@link RefreshTokenRepositoryPort}
 * using Spring Data JPA.
 */
@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;
    private final AuthPersistenceMapper mapper;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository,
                                          AuthPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity jpaEntity = mapper.toJpa(refreshToken);
        RefreshTokenJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(TokenValue tokenValue) {
        return jpaRepository.findByToken(tokenValue.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId, Instant.now());
    }

    @Override
    public int deleteExpiredTokens() {
        return jpaRepository.deleteExpiredTokens(Instant.now());
    }
}
