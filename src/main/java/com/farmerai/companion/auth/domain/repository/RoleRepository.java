package com.farmerai.companion.auth.domain.repository;

import com.farmerai.companion.auth.domain.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Role persistence.
 */
public interface RoleRepository {

    /**
     * Finds a role by its name (e.g., "ADMIN", "FARMER", "AGRONOMIST").
     */
    Optional<Role> findByName(String name);

    /**
     * Returns all available roles.
     */
    List<Role> findAll();
}
