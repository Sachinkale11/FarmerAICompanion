package com.farmerai.companion.auth.domain.entity;

import com.farmerai.companion.auth.domain.valueobject.Email;
import com.farmerai.companion.auth.domain.valueobject.HashedPassword;
import com.farmerai.companion.auth.domain.valueobject.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity")
class UserTest {

    private static final Email TEST_EMAIL = new Email("test@example.com");
    private static final HashedPassword TEST_PASSWORD =
            new HashedPassword("$2a$12$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ01234");
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.of("FARMER", "Test farmer role");
    }

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("should create user with ACTIVE status")
        void shouldCreateWithActiveStatus() {
            User user = User.create(TEST_EMAIL, TEST_PASSWORD, testRole);

            assertNotNull(user.getId());
            assertEquals(TEST_EMAIL, user.getEmail());
            assertEquals(TEST_PASSWORD, user.getPassword());
            assertEquals(UserStatus.ACTIVE, user.getStatus());
            assertEquals(testRole, user.getRole());
            assertTrue(user.isActive());
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
            assertEquals(0L, user.getVersion());
        }

        @Test
        @DisplayName("should reject null email")
        void shouldRejectNullEmail() {
            assertThrows(NullPointerException.class,
                    () -> User.create(null, TEST_PASSWORD, testRole));
        }

        @Test
        @DisplayName("should reject null password")
        void shouldRejectNullPassword() {
            assertThrows(NullPointerException.class,
                    () -> User.create(TEST_EMAIL, null, testRole));
        }

        @Test
        @DisplayName("should reject null role")
        void shouldRejectNullRole() {
            assertThrows(NullPointerException.class,
                    () -> User.create(TEST_EMAIL, TEST_PASSWORD, null));
        }
    }

    @Nested
    @DisplayName("Status transitions")
    class StatusTransitions {

        @Test
        @DisplayName("should deactivate user")
        void shouldDeactivateUser() {
            User user = User.create(TEST_EMAIL, TEST_PASSWORD, testRole);
            user.deactivate();

            assertEquals(UserStatus.INACTIVE, user.getStatus());
            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("should suspend user")
        void shouldSuspendUser() {
            User user = User.create(TEST_EMAIL, TEST_PASSWORD, testRole);
            user.suspend();

            assertEquals(UserStatus.SUSPENDED, user.getStatus());
            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("should reactivate user")
        void shouldReactivateUser() {
            User user = User.create(TEST_EMAIL, TEST_PASSWORD, testRole);
            user.deactivate();
            user.activate();

            assertEquals(UserStatus.ACTIVE, user.getStatus());
            assertTrue(user.isActive());
        }
    }

    @Nested
    @DisplayName("Role changes")
    class RoleChanges {

        @Test
        @DisplayName("should change role")
        void shouldChangeRole() {
            User user = User.create(TEST_EMAIL, TEST_PASSWORD, testRole);
            Role newRole = Role.of("AGRONOMIST", "Agronomist role");

            user.changeRole(newRole);

            assertEquals(newRole, user.getRole());
        }

        @Test
        @DisplayName("should reject null role change")
        void shouldRejectNullRoleChange() {
            User user = User.create(TEST_EMAIL, TEST_PASSWORD, testRole);
            assertThrows(NullPointerException.class, () -> user.changeRole(null));
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal by id")
        void shouldBeEqualById() {
            User user1 = User.create(TEST_EMAIL, TEST_PASSWORD, testRole);
            User user2 = User.reconstitute(
                    user1.getId(), TEST_EMAIL, TEST_PASSWORD,
                    UserStatus.ACTIVE, testRole,
                    user1.getCreatedAt(), user1.getUpdatedAt(),
                    null, null, 0L
            );
            assertEquals(user1, user2);
        }
    }
}
