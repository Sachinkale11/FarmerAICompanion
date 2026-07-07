package com.farmerai.companion.auth.infrastructure.persistence.mapper;

import com.farmerai.companion.auth.domain.entity.RefreshToken;
import com.farmerai.companion.auth.domain.entity.Role;
import com.farmerai.companion.auth.domain.entity.User;
import com.farmerai.companion.auth.domain.valueobject.Email;
import com.farmerai.companion.auth.domain.valueobject.TokenValue;
import com.farmerai.companion.auth.domain.valueobject.UserStatus;
import com.farmerai.companion.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.farmerai.companion.auth.infrastructure.persistence.entity.RoleJpaEntity;
import com.farmerai.companion.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Manual mapper between domain entities and JPA entities.
 *
 * <p>Hand-written instead of MapStruct because domain entities use value objects
 * ({@link Email}, etc.) that require special handling.
 * MapStruct would need extensive custom mappings that defeat its purpose here.</p>
 */
@Component
public class AuthPersistenceMapper {

    // ======================== Role Mapping ========================

    public Role toDomain(RoleJpaEntity jpa) {
        if (jpa == null) return null;
        return Role.reconstitute(jpa.getId(), jpa.getName(), jpa.getDescription());
    }

    public RoleJpaEntity toJpa(Role domain) {
        if (domain == null) return null;
        RoleJpaEntity jpa = new RoleJpaEntity();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setDescription(domain.getDescription());
        return jpa;
    }

    // ======================== User Mapping ========================

    public User toDomain(UserJpaEntity jpa) {
        if (jpa == null) return null;
        return User.reconstitute(
                jpa.getId(),
                new Email(jpa.getEmail()),
                jpa.getPassword(),
                UserStatus.valueOf(jpa.getStatus()),
                toDomain(jpa.getRole()),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt(),
                jpa.getCreatedBy(),
                jpa.getUpdatedBy(),
                jpa.getVersion()
        );
    }

    public UserJpaEntity toJpa(User domain) {
        if (domain == null) return null;
        UserJpaEntity jpa = new UserJpaEntity();
        jpa.setId(domain.getId());
        jpa.setEmail(domain.getEmail().getValue());
        jpa.setPassword(domain.getPassword());
        jpa.setStatus(domain.getStatus().name());
        jpa.setRole(toJpa(domain.getRole()));
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        jpa.setCreatedBy(domain.getCreatedBy());
        jpa.setUpdatedBy(domain.getUpdatedBy());
        jpa.setVersion(domain.getVersion());
        return jpa;
    }

    // ======================== RefreshToken Mapping ========================

    public RefreshToken toDomain(RefreshTokenJpaEntity jpa) {
        if (jpa == null) return null;
        return RefreshToken.reconstitute(
                jpa.getId(),
                new TokenValue(jpa.getToken()),
                jpa.getUserId(),
                jpa.getDeviceId(),
                jpa.getDeviceName(),
                jpa.getPlatform(),
                jpa.getLastUsedAt(),
                jpa.getExpiryDate(),
                jpa.isRevoked(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }

    public RefreshTokenJpaEntity toJpa(RefreshToken domain) {
        if (domain == null) return null;
        RefreshTokenJpaEntity jpa = new RefreshTokenJpaEntity();
        jpa.setId(domain.getId());
        jpa.setToken(domain.getToken().getValue());
        jpa.setUserId(domain.getUserId());
        jpa.setDeviceId(domain.getDeviceId());
        jpa.setDeviceName(domain.getDeviceName());
        jpa.setPlatform(domain.getPlatform());
        jpa.setLastUsedAt(domain.getLastUsedAt());
        jpa.setExpiryDate(domain.getExpiryDate());
        jpa.setRevoked(domain.isRevoked());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }
}
