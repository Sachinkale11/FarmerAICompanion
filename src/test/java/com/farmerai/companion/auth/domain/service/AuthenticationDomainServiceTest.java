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
import com.farmerai.companion.auth.domain.valueobject.HashedPassword;
import com.farmerai.companion.auth.domain.valueobject.TokenValue;
import com.farmerai.companion.auth.domain.valueobject.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("AuthenticationDomainService")
@ExtendWith(MockitoExtension.class)
class AuthenticationDomainServiceTest {

    private AuthenticationDomainService domainService;

    @Mock
    private UserRepository userRepository;

    private static final Email TEST_EMAIL = new Email("test@example.com");
    private static final HashedPassword TEST_HASH =
            new HashedPassword("$2a$12$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ01234");
    private static final Role TEST_ROLE = Role.of("FARMER", "Farmer");

    @BeforeEach
    void setUp() {
        domainService = new AuthenticationDomainService();
    }

    @Nested
    @DisplayName("validateUserNotExists")
    class ValidateUserNotExists {

        @Test
        @DisplayName("should pass when user does not exist")
        void shouldPassWhenUserNotExists() {
            when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
            assertDoesNotThrow(() -> domainService.validateUserNotExists(TEST_EMAIL, userRepository));
        }

        @Test
        @DisplayName("should throw when user exists")
        void shouldThrowWhenUserExists() {
            when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);
            assertThrows(UserAlreadyExistsException.class,
                    () -> domainService.validateUserNotExists(TEST_EMAIL, userRepository));
        }
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("should create user with correct attributes")
        void shouldCreateUser() {
            User user = domainService.createUser(TEST_EMAIL, TEST_HASH, TEST_ROLE);

            assertNotNull(user.getId());
            assertEquals(TEST_EMAIL, user.getEmail());
            assertEquals(TEST_HASH, user.getPassword());
            assertEquals(UserStatus.ACTIVE, user.getStatus());
            assertEquals(TEST_ROLE, user.getRole());
        }
    }

    @Nested
    @DisplayName("validateCredentials")
    class ValidateCredentials {

        @Test
        @DisplayName("should pass when password matches")
        void shouldPassWhenPasswordMatches() {
            User user = User.create(TEST_EMAIL, TEST_HASH, TEST_ROLE);
            AuthenticationDomainService.PasswordMatcher matcher = (raw, encoded) -> true;

            assertDoesNotThrow(() -> domainService.validateCredentials(user, "password", matcher));
        }

        @Test
        @DisplayName("should throw when password does not match")
        void shouldThrowWhenPasswordMismatch() {
            User user = User.create(TEST_EMAIL, TEST_HASH, TEST_ROLE);
            AuthenticationDomainService.PasswordMatcher matcher = (raw, encoded) -> false;

            assertThrows(InvalidCredentialsException.class,
                    () -> domainService.validateCredentials(user, "wrong", matcher));
        }
    }

    @Nested
    @DisplayName("validateUserActive")
    class ValidateUserActive {

        @Test
        @DisplayName("should pass when user is active")
        void shouldPassWhenActive() {
            User user = User.create(TEST_EMAIL, TEST_HASH, TEST_ROLE);
            assertDoesNotThrow(() -> domainService.validateUserActive(user));
        }

        @Test
        @DisplayName("should throw when user is inactive")
        void shouldThrowWhenInactive() {
            User user = User.create(TEST_EMAIL, TEST_HASH, TEST_ROLE);
            user.deactivate();

            assertThrows(UnauthorizedException.class,
                    () -> domainService.validateUserActive(user));
        }

        @Test
        @DisplayName("should throw when user is suspended")
        void shouldThrowWhenSuspended() {
            User user = User.create(TEST_EMAIL, TEST_HASH, TEST_ROLE);
            user.suspend();

            assertThrows(UnauthorizedException.class,
                    () -> domainService.validateUserActive(user));
        }
    }

    @Nested
    @DisplayName("createRefreshToken")
    class CreateRefreshToken {

        @Test
        @DisplayName("should create refresh token with correct expiry")
        void shouldCreateRefreshToken() {
            UUID userId = UUID.randomUUID();
            RefreshToken token = domainService.createRefreshToken(userId, 7);

            assertNotNull(token.getId());
            assertNotNull(token.getToken());
            assertEquals(userId, token.getUserId());
            assertFalse(token.isRevoked());
            assertTrue(token.getExpiryDate().isAfter(Instant.now()));
            assertTrue(token.getExpiryDate().isBefore(
                    Instant.now().plus(8, ChronoUnit.DAYS)
            ));
        }
    }

    @Nested
    @DisplayName("validateRefreshToken")
    class ValidateRefreshToken {

        @Test
        @DisplayName("should pass for valid token")
        void shouldPassForValidToken() {
            RefreshToken token = RefreshToken.create(
                    UUID.randomUUID(),
                    new TokenValue(UUID.randomUUID().toString()),
                    Instant.now().plus(7, ChronoUnit.DAYS)
            );

            assertDoesNotThrow(() -> domainService.validateRefreshToken(token));
        }

        @Test
        @DisplayName("should throw for revoked token")
        void shouldThrowForRevokedToken() {
            RefreshToken token = RefreshToken.create(
                    UUID.randomUUID(),
                    new TokenValue(UUID.randomUUID().toString()),
                    Instant.now().plus(7, ChronoUnit.DAYS)
            );
            token.revoke();

            assertThrows(InvalidTokenException.class,
                    () -> domainService.validateRefreshToken(token));
        }

        @Test
        @DisplayName("should throw for expired token")
        void shouldThrowForExpiredToken() {
            RefreshToken token = RefreshToken.create(
                    UUID.randomUUID(),
                    new TokenValue(UUID.randomUUID().toString()),
                    Instant.now().minus(1, ChronoUnit.HOURS)
            );

            assertThrows(TokenExpiredException.class,
                    () -> domainService.validateRefreshToken(token));
        }
    }
}
