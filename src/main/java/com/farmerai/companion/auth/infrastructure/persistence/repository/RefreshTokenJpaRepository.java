package com.farmerai.companion.auth.infrastructure.persistence.repository;

import com.farmerai.companion.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link RefreshTokenJpaEntity}.
 */
@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByToken(String token);

    /**
     * Revokes all non-revoked refresh tokens for a given user.
     */
    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity rt SET rt.revoked = true, rt.updatedAt = :now " +
           "WHERE rt.userId = :userId AND rt.revoked = false")
    int revokeAllByUserId(@Param("userId") UUID userId, @Param("now") Instant now);

    /**
     * Deletes all expired refresh tokens.
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity rt WHERE rt.expiryDate < :now")
    int deleteExpiredTokens(@Param("now") Instant now);
}
