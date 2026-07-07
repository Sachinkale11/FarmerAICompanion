package com.farmerai.companion.auth.infrastructure.persistence.adapter;

import com.farmerai.companion.auth.application.port.output.UserRepositoryPort;
import com.farmerai.companion.auth.domain.entity.User;
import com.farmerai.companion.auth.domain.valueobject.Email;
import com.farmerai.companion.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.farmerai.companion.auth.infrastructure.persistence.mapper.AuthPersistenceMapper;
import com.farmerai.companion.auth.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the application-layer {@link UserRepositoryPort}
 * using Spring Data JPA.
 *
 * <p>Translates between domain entities and JPA entities via
 * {@link AuthPersistenceMapper}.</p>
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final AuthPersistenceMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository,
                                  AuthPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = mapper.toJpa(user);
        UserJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }
}
