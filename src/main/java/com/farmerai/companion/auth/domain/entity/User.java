package com.farmerai.companion.auth.domain.entity;

import com.farmerai.companion.auth.domain.valueobject.Email;

import com.farmerai.companion.auth.domain.valueobject.UserStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user — the Aggregate Root for the User aggregate.
 *
 * <p>Encapsulates all user-related business logic: status transitions,
 * role changes, and invariant enforcement.</p>
 *
 * <p>This is a pure domain entity — no framework annotations.</p>
 */
public class User {

    private UUID id;
    private Email email;
    private String password;
    private UserStatus status;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    // ======================== Factory Methods ========================

    /**
     * Creates a new User with ACTIVE status.
     *
     * @param email          validated email
     * @param hashedPassword BCrypt-hashed password
     * @param role           assigned role
     * @return new User instance
     */
    public static User create(Email email, String hashedPassword, Role role) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.email = Objects.requireNonNull(email, "Email must not be null");
        user.password = Objects.requireNonNull(hashedPassword, "Password must not be null");
        if (hashedPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        user.status = UserStatus.ACTIVE;
        user.role = Objects.requireNonNull(role, "Role must not be null");
        user.createdAt = Instant.now();
        user.updatedAt = Instant.now();
        user.createdBy = email.getValue();
        user.updatedBy = email.getValue();
        user.version = 0L;
        return user;
    }

    /**
     * Reconstitutes a User from persistence.
     */
    public static User reconstitute(UUID id, Email email, String password,
                                     UserStatus status, Role role,
                                     Instant createdAt, Instant updatedAt,
                                     String createdBy, String updatedBy, Long version) {
        User user = new User();
        user.id = id;
        user.email = email;
        user.password = password;
        user.status = status;
        user.role = role;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.createdBy = createdBy;
        user.updatedBy = updatedBy;
        user.version = version;
        return user;
    }

    // ======================== Business Methods ========================

    /**
     * Activates the user account.
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    /**
     * Suspends the user account.
     */
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    /**
     * Changes the user's role.
     *
     * @param newRole the new role to assign
     */
    public void changeRole(Role newRole) {
        this.role = Objects.requireNonNull(newRole, "New role must not be null");
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if the user account is active.
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // ======================== Getters ========================

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Long getVersion() {
        return version;
    }

    // ======================== Equality ========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email=" + email + ", status=" + status + "}";
    }
}
