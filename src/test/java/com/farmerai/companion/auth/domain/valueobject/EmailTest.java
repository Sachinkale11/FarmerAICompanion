package com.farmerai.companion.auth.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email Value Object")
class EmailTest {

    @Nested
    @DisplayName("Valid emails")
    class ValidEmails {

        @ParameterizedTest
        @ValueSource(strings = {
                "user@example.com",
                "john.doe@company.org",
                "test+tag@domain.co",
                "user123@sub.domain.com"
        })
        @DisplayName("should accept valid email formats")
        void shouldAcceptValidEmails(String email) {
            Email result = new Email(email);
            assertNotNull(result);
            assertEquals(email.toLowerCase(), result.getValue());
        }

        @Test
        @DisplayName("should normalize to lowercase")
        void shouldNormalizeToLowercase() {
            Email email = new Email("User@EXAMPLE.com");
            assertEquals("user@example.com", email.getValue());
        }
    }

    @Nested
    @DisplayName("Invalid emails")
    class InvalidEmails {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should reject null, empty, and blank emails")
        void shouldRejectNullAndBlank(String email) {
            assertThrows(IllegalArgumentException.class, () -> new Email(email));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "notanemail",
                "@domain.com",
                "user@",
                "user@.com",
                "user space@domain.com"
        })
        @DisplayName("should reject invalid email formats")
        void shouldRejectInvalidFormats(String email) {
            assertThrows(IllegalArgumentException.class, () -> new Email(email));
        }

        @Test
        @DisplayName("should reject emails exceeding 255 characters")
        void shouldRejectLongEmails() {
            String longEmail = "a".repeat(250) + "@b.com";
            assertThrows(IllegalArgumentException.class, () -> new Email(longEmail));
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal for same email (case insensitive)")
        void shouldBeEqualForSameEmail() {
            Email email1 = new Email("user@example.com");
            Email email2 = new Email("USER@EXAMPLE.COM");
            assertEquals(email1, email2);
            assertEquals(email1.hashCode(), email2.hashCode());
        }

        @Test
        @DisplayName("should not be equal for different emails")
        void shouldNotBeEqualForDifferentEmails() {
            Email email1 = new Email("user1@example.com");
            Email email2 = new Email("user2@example.com");
            assertNotEquals(email1, email2);
        }
    }
}
