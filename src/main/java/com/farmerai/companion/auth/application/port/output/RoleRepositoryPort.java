package com.farmerai.companion.auth.application.port.output;

import com.farmerai.companion.auth.domain.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Output port for role persistence operations.
 */
public interface RoleRepositoryPort {

    Optional<Role> findByName(String name);

    List<Role> findAll();
}
