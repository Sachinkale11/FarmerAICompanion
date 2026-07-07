package com.farmerai.companion.auth.application.port.output;

import com.farmerai.companion.auth.domain.entity.User;
import com.farmerai.companion.auth.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for user persistence operations.
 *
 * <p>Mirrors the domain {@code UserRepository} — implemented by
 * infrastructure adapters.</p>
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(Email email);

    Optional<User> findById(UUID id);

    boolean existsByEmail(Email email);
}
