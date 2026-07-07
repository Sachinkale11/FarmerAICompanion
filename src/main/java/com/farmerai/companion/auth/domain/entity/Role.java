package com.farmerai.companion.auth.domain.entity;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user role.
 *
 * <p>Roles define the level of access a user has in the system.
 * Seeded values: ADMIN, FARMER, AGRONOMIST.</p>
 *
 * <p>This is a pure domain entity — no framework annotations.</p>
 */
public class Role {

    private UUID id;
    private String name;
    private String description;

    // ======================== Factory Method ========================

    /**
     * Creates a new Role with a generated UUID.
     */
    public static Role of(String name, String description) {
        Role role = new Role();
        role.id = UUID.randomUUID();
        role.name = Objects.requireNonNull(name, "Role name must not be null");
        role.description = description;
        return role;
    }

    /**
     * Reconstitutes a Role from persistence.
     */
    public static Role reconstitute(UUID id, String name, String description) {
        Role role = new Role();
        role.id = Objects.requireNonNull(id, "Role id must not be null");
        role.name = Objects.requireNonNull(name, "Role name must not be null");
        role.description = description;
        return role;
    }

    // ======================== Getters ========================

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // ======================== Equality ========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Role{id=" + id + ", name='" + name + "'}";
    }
}
