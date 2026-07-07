package com.farmerai.companion.auth.infrastructure.persistence.adapter;

import com.farmerai.companion.auth.application.port.output.RoleRepositoryPort;
import com.farmerai.companion.auth.domain.entity.Role;
import com.farmerai.companion.auth.infrastructure.persistence.mapper.AuthPersistenceMapper;
import com.farmerai.companion.auth.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the application-layer {@link RoleRepositoryPort}
 * using Spring Data JPA.
 */
@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository jpaRepository;
    private final AuthPersistenceMapper mapper;

    public RoleRepositoryAdapter(RoleJpaRepository jpaRepository,
                                  AuthPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
