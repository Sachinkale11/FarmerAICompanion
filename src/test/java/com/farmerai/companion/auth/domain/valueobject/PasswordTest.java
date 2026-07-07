package com.farmerai.companion.auth.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Value Object")
class PasswordTest {

    private static final String VALID_PASSWORD = "StrongP@ss1";

    @Nested
    @DisplayName("Valid passwords")
    class ValidPasswords {

        @ParameterizedTest
        @ValueSource(strings = {
                "StrongP@ss1",
                "MyP@ssw0rd!",
                "C0mplex!Pass",
                "Ab1!defgh"
        })
        @DisplayName("should accept valid passwords")
        void shouldAcceptValidPasswords(String password) {
            Password result = new Password(password);
            assertNotNull(result);
            assertEquals(password, result.getValue());
        }
    }

    @Nested
    @DisplayName("Invalid passwords")
    class InvalidPasswords {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should reject null, empty, and blank passwords")
        void shouldRejectNullAndBlank(String password) {
            assertThrows(IllegalArgumentException.class, () -> new Password(password));
        }

        @Test
        @DisplayName("should reject passwords shorter than 8 characters")
        void shouldRejectShortPasswords() {
            assertThrows(IllegalArgumentException.class, () -> new Password("Ab1!def"));
        }

        @Test
        @DisplayName("should reject passwords without uppercase")
        void shouldRejectWithoutUppercase() {
            assertThrows(IllegalArgumentException.class, () -> new Password("strongp@ss1"));
        }

        @Test
        @DisplayName("should reject passwords without lowercase")
        void shouldRejectWithoutLowercase() {
            assertThrows(IllegalArgumentException.class, () -> new Password("STRONGP@SS1"));
        }

        @Test
        @DisplayName("should reject passwords without digit")
        void shouldRejectWithoutDigit() {
            assertThrows(IllegalArgumentException.class, () -> new Password("StrongP@ss"));
        }

        @Test
        @DisplayName("should reject passwords without special character")
        void shouldRejectWithoutSpecialChar() {
            assertThrows(IllegalArgumentException.class, () -> new Password("StrongPass1"));
        }
    }

    @Test
    @DisplayName("toString should mask password value")
    void toStringShouldMaskPassword() {
        Password password = new Password(VALID_PASSWORD);
        assertEquals("Password{****}", password.toString());
        assertFalse(password.toString().contains(VALID_PASSWORD));
    }
}
