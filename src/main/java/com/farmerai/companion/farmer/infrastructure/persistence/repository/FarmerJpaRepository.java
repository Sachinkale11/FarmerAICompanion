package com.farmerai.companion.farmer.infrastructure.persistence.repository;

import com.farmerai.companion.farmer.infrastructure.persistence.entity.FarmerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmerJpaRepository extends JpaRepository<FarmerJpaEntity, UUID> {
    Optional<FarmerJpaEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    boolean existsByPhoneNumber(String phoneNumber);
}
