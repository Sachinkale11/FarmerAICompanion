package com.farmerai.companion.auth.domain.entity;

import com.farmerai.companion.auth.domain.valueobject.TokenValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RefreshToken Entity")
class RefreshTokenTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final TokenValue TOKEN = new TokenValue(UUID.randomUUID().toString());

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("should create non-revoked token with future expiry")
        void shouldCreateValidToken() {
            Instant expiry = Instant.now().plus(7, ChronoUnit.DAYS);
            RefreshToken token = RefreshToken.create(USER_ID, TOKEN, expiry);

            assertNotNull(token.getId());
            assertEquals(TOKEN, token.getToken());
            assertEquals(USER_ID, token.getUserId());
            assertEquals(expiry, token.getExpiryDate());
            assertFalse(token.isRevoked());
            assertTrue(token.isUsable());
        }
    }

    @Nested
    @DisplayName("Revocation")
    class Revocation {

        @Test
        @DisplayName("should mark token as revoked")
        void shouldRevoke() {
            Instant expiry = Instant.now().plus(7, ChronoUnit.DAYS);
            RefreshToken token = RefreshToken.create(USER_ID, TOKEN, expiry);

            token.revoke();

            assertTrue(token.isRevoked());
            assertFalse(token.isUsable());
        }
    }

    @Nested
    @DisplayName("Expiration")
    class Expiration {

        @Test
        @DisplayName("should detect expired token")
        void shouldDetectExpired() {
            Instant pastExpiry = Instant.now().minus(1, ChronoUnit.HOURS);
            RefreshToken token = RefreshToken.create(USER_ID, TOKEN, pastExpiry);

            assertTrue(token.isExpired());
            assertFalse(token.isUsable());
        }

        @Test
        @DisplayName("should detect non-expired token")
        void shouldDetectNonExpired() {
            Instant futureExpiry = Instant.now().plus(7, ChronoUnit.DAYS);
            RefreshToken token = RefreshToken.create(USER_ID, TOKEN, futureExpiry);

            assertFalse(token.isExpired());
            assertTrue(token.isUsable());
        }
    }

    @Nested
    @DisplayName("Usability")
    class Usability {

        @Test
        @DisplayName("should be unusable when revoked even if not expired")
        void shouldBeUnusableWhenRevoked() {
            Instant futureExpiry = Instant.now().plus(7, ChronoUnit.DAYS);
            RefreshToken token = RefreshToken.create(USER_ID, TOKEN, futureExpiry);
            token.revoke();

            assertFalse(token.isUsable());
        }

        @Test
        @DisplayName("should be unusable when expired even if not revoked")
        void shouldBeUnusableWhenExpired() {
            Instant pastExpiry = Instant.now().minus(1, ChronoUnit.HOURS);
            RefreshToken token = RefreshToken.create(USER_ID, TOKEN, pastExpiry);

            assertFalse(token.isUsable());
        }
    }
}
