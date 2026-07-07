package com.farmerai.companion.farm.infrastructure.persistence.repository;

import com.farmerai.companion.farm.infrastructure.persistence.entity.FarmJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FarmJpaRepository extends JpaRepository<FarmJpaEntity, UUID> {
    List<FarmJpaEntity> findByFarmerId(UUID farmerId);
    boolean existsByFarmerIdAndName(UUID farmerId, String name);
}
