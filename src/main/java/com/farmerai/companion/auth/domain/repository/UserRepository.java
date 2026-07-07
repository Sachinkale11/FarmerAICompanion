package com.farmerai.companion.auth.domain.repository;

import com.farmerai.companion.auth.domain.entity.User;
import com.farmerai.companion.auth.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for User persistence.
 *
 * <p>Defined in the domain layer — implemented by infrastructure adapters.
 * This keeps the domain layer independent of any persistence framework.</p>
 */
public interface UserRepository {

    /**
     * Persists a user (insert or update).
     */
    User save(User user);

    /**
     * Finds a user by their email address.
     */
    Optional<User> findByEmail(Email email);

    /**
     * Finds a user by their unique identifier.
     */
    Optional<User> findById(UUID id);

    /**
     * Checks whether a user with the given email already exists.
     */
    boolean existsByEmail(Email email);
}
