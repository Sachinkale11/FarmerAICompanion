package com.farmerai.companion.auth.domain.service;

import com.farmerai.companion.auth.domain.entity.RefreshToken;
import com.farmerai.companion.auth.domain.entity.Role;
import com.farmerai.companion.auth.domain.entity.User;
import com.farmerai.companion.auth.domain.exception.InvalidCredentialsException;
import com.farmerai.companion.auth.domain.exception.InvalidTokenException;
import com.farmerai.companion.auth.domain.exception.TokenExpiredException;
import com.farmerai.companion.auth.domain.exception.UnauthorizedException;
import com.farmerai.companion.auth.domain.exception.UserAlreadyExistsException;
import com.farmerai.companion.auth.domain.repository.UserRepository;
import com.farmerai.companion.auth.domain.valueobject.Email;
import com.farmerai.companion.auth.domain.valueobject.TokenValue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Domain service containing authentication-related business logic that
 * doesn't naturally belong to a single entity.
 *
 * <p>
 * This service operates on domain objects only — it has NO framework
 * dependencies and NO knowledge of infrastructure.
 * </p>
 *
 * <p>
 * The password matching is delegated via a functional interface parameter
 * to maintain the domain layer's independence from Spring Security's
 * BCryptPasswordEncoder.
 * </p>
 */
public class AuthenticationDomainService {

    /**
     * Validates that no user exists with the given email.
     *
     * @throws UserAlreadyExistsException if a user with the email already exists
     */
    public void validateUserNotExists(Email email, UserRepository userRepository) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email.getValue());
        }
    }

    /**
     * Creates a new user with ACTIVE status.
     */
    public User createUser(Email email, String hashedPassword, Role role) {
        return User.create(email, hashedPassword, role);
    }

    /**
     * Validates that the raw password matches the user's stored hash.
     *
     * @param user            the user to validate against
     * @param rawPassword     the raw password to check
     * @param passwordMatcher functional interface to delegate BCrypt matching
     * @throws InvalidCredentialsException if the password does not match
     */
    public void validateCredentials(User user, String rawPassword,
            PasswordMatcher passwordMatcher) {
        if (!passwordMatcher.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Validates that the user's account is active.
     *
     * @throws UnauthorizedException if the user account is not active
     */
    public void validateUserActive(User user) {
        if (!user.isActive()) {
            throw new UnauthorizedException(
                    "User account is " + user.getStatus().name().toLowerCase());
        }
    }

    /**
     * Creates a new refresh token for the given user.
     *
     * @param userId     the user ID
     * @param expiryDays number of days until the token expires
     * @return a new RefreshToken entity
     */
    public RefreshToken createRefreshToken(UUID userId, int expiryDays, String deviceId, String deviceName,
            String platform) {
        TokenValue tokenValue = new TokenValue(UUID.randomUUID().toString());
        Instant expiryDate = Instant.now().plus(expiryDays, ChronoUnit.DAYS);
        return RefreshToken.create(userId, tokenValue, expiryDate, deviceId, deviceName, platform);
    }

    /**
     * Validates that a refresh token is usable (not expired and not revoked).
     *
     * <p>
     * If the token is revoked, this indicates potential token theft.
     * The caller should revoke ALL tokens for the user (reuse detection).
     * </p>
     *
     * @param token the refresh token to validate
     * @throws InvalidTokenException if the token is revoked
     * @throws TokenExpiredException if the token has expired
     */
    public void validateRefreshToken(RefreshToken token) {
        if (token.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }
        if (token.isExpired()) {
            throw new TokenExpiredException("Refresh token has expired");
        }
    }

    /**
     * Functional interface for password matching — decouples domain from BCrypt.
     */
    @FunctionalInterface
    public interface PasswordMatcher {
        boolean matches(String rawPassword, String encodedPassword);
    }
}
