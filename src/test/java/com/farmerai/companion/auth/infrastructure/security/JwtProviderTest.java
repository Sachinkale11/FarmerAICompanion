package com.farmerai.companion.auth.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtProvider")
class JwtProviderTest {

    private JwtProvider jwtProvider;

    // Base64-encoded 256-bit test secret
    private static final String TEST_SECRET = "dGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JBSUZ1cm1lckNvbXBhbmlvbkFwcA==";
    private static final long EXPIRATION_MS = 900000; // 15 minutes
    private static final String ISSUER = "ai-farmer-companion";

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(TEST_SECRET, EXPIRATION_MS, ISSUER);
    }

    @Nested
    @DisplayName("Token generation")
    class TokenGeneration {

        @Test
        @DisplayName("should generate a non-null JWT token")
        void shouldGenerateToken() {
            UUID userId = UUID.randomUUID();
            String token = jwtProvider.generateAccessToken(userId, "test@example.com", "FARMER");

            assertNotNull(token);
            assertFalse(token.isEmpty());
            // JWT has 3 parts separated by dots
            assertEquals(3, token.split("\\.").length);
        }
    }

    @Nested
    @DisplayName("Token validation")
    class TokenValidation {

        @Test
        @DisplayName("should validate a valid token")
        void shouldValidateValidToken() {
            UUID userId = UUID.randomUUID();
            String token = jwtProvider.generateAccessToken(userId, "test@example.com", "FARMER");

            assertTrue(jwtProvider.validateToken(token));
        }

        @Test
        @DisplayName("should reject an invalid token")
        void shouldRejectInvalidToken() {
            assertFalse(jwtProvider.validateToken("invalid.token.here"));
        }

        @Test
        @DisplayName("should reject a null token")
        void shouldRejectNullToken() {
            assertFalse(jwtProvider.validateToken(null));
        }

        @Test
        @DisplayName("should reject an empty token")
        void shouldRejectEmptyToken() {
            assertFalse(jwtProvider.validateToken(""));
        }

        @Test
        @DisplayName("should reject a tampered token")
        void shouldRejectTamperedToken() {
            UUID userId = UUID.randomUUID();
            String token = jwtProvider.generateAccessToken(userId, "test@example.com", "FARMER");

            // Tamper with the last character
            String tampered = token.substring(0, token.length() - 1) + "X";
            assertFalse(jwtProvider.validateToken(tampered));
        }

        @Test
        @DisplayName("should reject token signed with different secret")
        void shouldRejectDifferentSecret() {
            JwtProvider otherProvider = new JwtProvider(
                    "YW5vdGhlclNlY3JldEtleVRoYXRJc0RpZmZlcmVudEZyb21UaGVPcmlnaW5hbEtleQ==",
                    EXPIRATION_MS, ISSUER
            );
            String token = otherProvider.generateAccessToken(
                    UUID.randomUUID(), "test@example.com", "FARMER"
            );

            assertFalse(jwtProvider.validateToken(token));
        }
    }

    @Nested
    @DisplayName("Claim extraction")
    class ClaimExtraction {

        @Test
        @DisplayName("should extract userId from token")
        void shouldExtractUserId() {
            UUID userId = UUID.randomUUID();
            String token = jwtProvider.generateAccessToken(userId, "test@example.com", "FARMER");

            assertEquals(userId, jwtProvider.getUserIdFromToken(token));
        }

        @Test
        @DisplayName("should extract email from token")
        void shouldExtractEmail() {
            String token = jwtProvider.generateAccessToken(
                    UUID.randomUUID(), "test@example.com", "FARMER"
            );

            assertEquals("test@example.com", jwtProvider.getEmailFromToken(token));
        }

        @Test
        @DisplayName("should extract role from token")
        void shouldExtractRole() {
            String token = jwtProvider.generateAccessToken(
                    UUID.randomUUID(), "test@example.com", "ADMIN"
            );

            assertEquals("ADMIN", jwtProvider.getRoleFromToken(token));
        }
    }

    @Test
    @DisplayName("should return correct expiration in seconds")
    void shouldReturnExpirationInSeconds() {
        assertEquals(900, jwtProvider.getAccessTokenExpirationSeconds());
    }
}
